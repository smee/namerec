package com.bordag.sc;

import java.util.*;
import java.io.*;

import com.bordag.sc.util.*;
import com.bordag.sgz.util.*;

/**
 * Takes a given file,
 * parses it for sentences,
 * puts all sentences as String arrays into a vector
 * Runs various evaluations:
 *   takes the last contentword and makes error of LD=1
 *     counts how often it will be corrected
 *   LD=2
 *   LD=3
 *  ...
 *   5
 * Takes only last content word because implementation of spreading activation
 * is only prototypical and 'context' is simplified to the sentence up to the
 * word.
 *
 * Sentences shorter or equal then 3 content words should be ignored.
 */
public class SCEvaluator
{
  protected WordServer server = null;

  protected SpellChecker checker = null;

  protected DBConnection connection = null;
  protected de.wortschatz.util.DBConnection connection2 = null;
  // This vector contains arrays of String which represent the sentences
  protected Vector sentences = null;
  protected double LDInfluence = 2.0;

  protected Random r = null;


  public SCEvaluator(DBConnection connection, de.wortschatz.util.DBConnection connection2, String fileName, int distance, int maxKollok, double LDInfluence)
  {
    this.LDInfluence = LDInfluence;
    this.r = new Random(1);
    this.connection = connection;

    System.out.println("Creating Sentence parser");
    SentenceParser parser = new SentenceParser(fileName);
    System.out.println("Reading sentences");
    this.sentences = parser.getSentences();

    System.out.println("Importing WordServer");
    if ( !importWordList() )
    {
      System.out.println("WordServer was deleted or is out of date, Rebuilding WordServer");
      this.server = new WordServer(this.connection);
      loadWords(this.sentences);
      exportWordList();
    }
    System.out.println("Creating Spell Checker ");
    this.connection2 = connection2;
    this.checker = new SpellChecker(this.connection2, this.server, maxKollok, this.LDInfluence );

    checkSentences(this.sentences);
    //printSentences(this.sentences);
    evaluate(distance);
    //
  }

  /**
   * Makes one LD=n mistake onto each sentences last word, lets the corrector
   * correct it and counts how many times it words.
   * if n is larger then the length of the word it is still possible to add
   * useless letters...
   */
  protected void evaluate(int levenDistance)
  {
    Vector brokenSentences = damageSentences(this.sentences,levenDistance);
//    printSentences(brokenSentences);
    int allSentences = brokenSentences.size();
    int correctedSentences = 0;
    int noDecisions = 0;
    int i = 0;
    for ( Iterator it = brokenSentences.iterator() ; it.hasNext() ; i++ )
    {
      String[] curSentence = (String[])it.next();
      String[] correctedSentence = checker.getCorrectedSentence(curSentence);
      if ( compareSentences((String[])this.sentences.get(i), correctedSentence) == 0 )
      {
        correctedSentences++;
      }
      noDecisions += sentenceNoDecision(correctedSentence);
    }
//    System.out.println("From "+allSentences+" sentences "+correctedSentences+" were corrected right, which makes "+(((double)correctedSentences/(double)allSentences)*100.0)+"%");
    double percCorr = (((double)correctedSentences/(double)allSentences)*100.0);
    System.out.println("+ "+correctedSentences+"\t"+percCorr);
    int wrong = allSentences - correctedSentences - noDecisions ;
    double percWrong = (((double)wrong/(double)allSentences)*100.0);
    System.out.println("- "+wrong+"\t"+percWrong);
    double percNodes = (((double)noDecisions/(double)allSentences)*100.0);
    System.out.println(". "+noDecisions+"\t"+percNodes);
  }

  protected int sentenceNoDecision(String[] sentence)
  {
    int count = 0;
    for ( int i = 0 ; i < sentence.length ; i++ )
    {
      if ( sentence[i].indexOf("noDecision") >= 0 )
      {
        count++;
      }
    }
    return count;
  }

  protected int compareSentences(String[] s1, String[] s2)
  {
    int retVal = Math.abs(s1.length - s2.length);
    for ( int i = 0 ; i < Math.min(s1.length, s2.length) ; i++ )
    {
      if ( !s1[i].equals(s2[i]) )
      {
        retVal++;
      }
    }
    return retVal;
  }

  protected Vector damageSentences(Vector sentences, int levenDistance)
  {
    Vector retVec = new Vector();
    for ( Iterator it = sentences.iterator() ; it.hasNext() ; )
    {
      String[] curSen = (String[]) it.next();
      String[] damSen = copySentence(curSen);
      String lastWord = damSen[damSen.length-1];
      char[] last = lastWord.toCharArray();
      for ( int i = 0 ; i < Math.min(levenDistance,last.length) ; i++ )
      {
        //lastWord = damageWord(lastWord);
        last[i]='_';
      }
      lastWord = new String(last);
      damSen[damSen.length-1]=lastWord;
      retVec.add(damSen);
    }
    return retVec;
  }

  /**
   * @todo: replace only not already replaced previously characters
   * @todo: somehow stop replacing/deleting operation such that only 2 '_'es
   * remain
   * @param word
   * @return
   */
  protected String damageWord(String word)
  {
    if ( word.length() == 0 )
    {
      word = "_";
      return word;
    }
    char letter = '_';

    // replace or insert or delete
    double decide = this.r.nextDouble();
    //System.out.print(" decide ("+decide+"): ");
    if ( decide <= 0.33 ) // replace
    {
      int position = (int)(((double)word.length()-1.0) * this.r.nextDouble());
      word = word.substring(0, position) + letter + "" + word.substring( position+1 , word.length());
    }
    else if ( decide <= 0.66 ) // insert
    {
      word = word + "" + letter;
    }
    else // delete
    {
      if ( word.length() > 0 )
      {
        word = word.substring(0, word.length()-1);
      }
    }
    return word;
  }

  protected void loadWords(Vector sentences)
  {
    for ( Iterator it = sentences.iterator() ; it.hasNext() ; )
    {
      String[] curSentence = (String[])it.next();
      this.server.addStrs( curSentence );
    }
    System.out.println("Syncing wordList");
    this.server.sync();
  }

  /**
   * Makes sure that every word which appears in 'correct' sentences is really
   * known by throwing away unknown words.
   * @param sentences
   */
  protected void checkSentences(Vector sentences)
  {
    Vector newSentences = new Vector();
    for ( Iterator it = sentences.iterator() ; it.hasNext() ; )
    {
      String[] curSentence = (String[])it.next();
      Vector knownWords = new Vector();
      for ( int i = 0 ; i < curSentence.length ; i++ )
      {
        if ( this.server.getNrForWord(curSentence[i]) != null && curSentence[i].length() > 0 )
        {
          knownWords.add(curSentence[i]);
        }
      }
      String[] newSentence = new String[knownWords.size()];
      int i = 0;
      for ( Iterator it2 = knownWords.iterator() ; it2.hasNext() ; i++ )
      {
        String curWord = (String) it2.next();
        newSentence[i] = curWord;
      }
      newSentences.add(newSentence);
    }
    this.sentences = newSentences;
  }

  /**
   * Imports the current state of WordServer into a file so that not each test
   * run has to fetch everything from the DB again
   */
  protected void exportWordList()
  {
    String file = "C:\\develop\\WortschatzTool\\data\\sc\\wortliste.bin";
    try
    {
      FileOutputStream fo = new FileOutputStream(file);
      ObjectOutputStream wc = new ObjectOutputStream(fo);
      wc.writeObject(this.server);
      wc.flush();
      wc.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return;
    }
  }

  /**
   * Imports a previously exported instance of WordServer to not have to fetch
   * it all through db again.
   * @return
   */
  protected boolean importWordList()
  {
    String file = "C:\\develop\\WortschatzTool\\data\\sc\\wortliste.bin";
    try
    {
      FileInputStream fi = new FileInputStream(file);
      ObjectInputStream si = new ObjectInputStream(fi);
      this.server = (WordServer) si.readObject();
      this.server.setConnection(this.connection);
      si.close();
      return true;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }

  protected String[] copySentence(String[] old)
  {
    String[] newSentence = new String[old.length];
    for ( int i = 0 ; i < old.length ; i++ )
    {
      newSentence[i] = new String(old[i]);
    }
    return newSentence;
  }

  protected void printSentence(String[] sentence)
  {
    System.out.print("sentence:");
    for ( int i = 0 ; i < sentence.length ; i++ )
    {
      System.out.print(" ["+sentence[i]+"]");
    }
    System.out.println();
  }

  /**
   * Prints out the given Vector of sentences
   * @param sentences
   */
  protected void printSentences(Vector sentences)
  {
    if ( sentences == null || sentences.size() < 1 )
    {
      System.out.println("Error: No sentences found.");
      System.exit(0);
    }
    for ( Iterator it = sentences.iterator() ; it.hasNext() ; )
    {
      String[] curSentence = (String[])it.next();
      System.out.print("s: ");
      for ( int i = 0 ; i < curSentence.length ; i++ )
      {
        System.out.print("["+curSentence[i]+"] ");
      }
      System.out.println();
    }
  }

  /**
   * Creates connection, checks arguments and starts up the program
   * @param args
   */
  public static void main(String[] args)
  {
    if ( args.length < 1 )
    {
      System.out.println("usage: java SCEvaluator fileToBeEvaluated.txt [LD]");
      System.exit(0);
    }

    String url = "jdbc:mysql://woclu2.informatik.uni-leipzig.de/en";
    String user = "sbordag";
    String passwd = "ansiBla";

    System.out.println("Connecting to DB");
    DBConnection connection = null;
    de.wortschatz.util.DBConnection connection2 = null;
    try
    {
      connection = new CachedDBConnection(url, user, passwd);
      connection2 = new de.wortschatz.util.DBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      System.out.println("Could not establish connection, exiting.");
      System.exit(0);
    }

    System.out.println("Launching Evaluator");
    int distance = 2;
    if ( args.length > 1 )
    {
      distance = (new Integer(args[1])).intValue();
    }
    int maxKollok=500;
    if ( args.length > 2 )
    {
      maxKollok = (new Integer(args[2])).intValue();
    }
    double LDInfluence = 2.0;
    if ( args.length > 3 )
    {
      LDInfluence = (new Double(args[3])).doubleValue();
    }
    SCEvaluator s = new SCEvaluator(connection,connection2, args[0], distance, maxKollok, LDInfluence);
  }

}