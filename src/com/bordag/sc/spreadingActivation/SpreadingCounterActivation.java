package com.bordag.sc.spreadingActivation;

import de.wortschatz.util.*;

import java.util.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SpreadingCounterActivation
{

  private static int MIN_SIGNIFIKANZ = 1;
  private static int MIN_WORDNUM = 1;
  private int MAX_KOLLOK = 500;
  private double LD_INFLUENCE  = 2.0;
  // A HashMap which stores wordnumbers as keys and wordstrings as values
  // stores only those words which are also in the graph
  private HashMap wordNrMap = null;

  private DBConnection connection = null;

  private HashMap stopWords = null;

  private ActivationCounterGraph graph = null;

  public SpreadingCounterActivation(DBConnection connection, double LDInfluence)
  {
    this.LD_INFLUENCE = LDInfluence;
    this.wordNrMap = new HashMap();
    this.stopWords = new HashMap();
    // fill stopWordMap from file...

    this.connection = connection;
    this.graph = new ActivationCounterGraph();
  }

  public void setMaxKollok(int maxKollok)
  {
    this.MAX_KOLLOK = maxKollok;
  }

  public void nextWordNr(Integer number, String word)
  {
    //System.out.println("receiving number: "+number);
    // if word is not stopword
    if ( ! this.stopWords.containsKey(number) )
    {
      // add word to my own mapping
      this.wordNrMap.put(number, word);
      // get neighbours of word
      Set neighbours = this.connection.getSatzKollokationen(number, this.MIN_SIGNIFIKANZ, this.MIN_WORDNUM, this.MAX_KOLLOK);
      // add all neighbours to wordNrMapping
      addMapping(neighbours);
      // add node with neighbours to ActivationGraph
      this.graph.addNode(number, neighbours);
      System.out.print("<"+ (int)( ((double)count(this.graph.getActivations(),2)/(double)this.graph.getActivations().size())*100.0 ) +"| ");
    }
  }

  protected int count(HashMap table, int min)
  {
    int retVal = 0;
    for ( Iterator it = table.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curNumber = (Integer)it.next();
      Double curDouble = (Double)table.get(curNumber);
      if ( curDouble.intValue() >= min )
      {
//        System.out.println("curInt:"+curInt);
        retVal++;
      }
    }
    return retVal;
  }

  /**
   * Retrieves also the strings for the neighbours
   * @param neighbours
   */
  private void addMapping(Set neighbours)
  {
    Integer[] numbers = new Integer[neighbours.size()];
    int i = 0;
    for ( Iterator it = neighbours.iterator() ; it.hasNext() ; i++ )
    {
      numbers[i] = (Integer)it.next();
    }
    Arrays.sort(numbers);
    i = 0;
    String[] words = this.connection.getWordsForNumbers(numbers);
    for ( int j = 0; j < numbers.length ; j++ )
    {
      this.wordNrMap.put(numbers[j], words[j]);
    }
  }


  public String guess(String remove)
  {
    this.graph.getActivations().remove(this.connection.getNumberForWord(remove));
    Random r = new Random(1);
    int length = (int) (r.nextDouble()*14.0);
    char[] wordField = new char[length];
    for ( int i = 0 ; i < wordField.length ; i++ )
    {
      wordField[i] = (char)(80.0*r.nextDouble() + 65.0);
    }
    String word = new String(wordField);

    // create HashMap with value:
    // key = wordNumber
    // value = clever combination(tm) = LD*(1-activation)
    HashMap values = new HashMap();
    HashMap activations = this.graph.getActivations();
    for ( Iterator it = activations.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curNr = (Integer)it.next();
      String wordStr = (String)this.wordNrMap.get(curNr);
      double curActivation = ((Double)activations.get(curNr)).doubleValue();
      double levenshteinDistance = (double)LevenshteinDistance.LD(word, (String)this.wordNrMap.get(curNr));
      double lengthDif = (double)Math.abs(word.length() - wordStr.length());
      double combination = 2.0*levenshteinDistance +
                           ( (double)this.graph.getInputWords() - curActivation ) +
                           lengthDif*2.0;
      values.put(this.wordNrMap.get(curNr),new Double(combination));
    }

    double minVal = Double.MAX_VALUE;
    String minString = word;
    for ( Iterator it = values.keySet().iterator() ; it.hasNext() ; )
    {
      String curString = (String) it.next();
      double curVal = ((Double)values.get(curString)).doubleValue();
      if ( curVal < minVal )
      {
        minVal = curVal;
        minString = curString;
      }
    }

    return minString;

  }

  /**
   * noDecision:
   * 1. Wenn gar kein Buchstabe stimmt (vor allem bei kurzen!)
   * 2. Wenn LD >= 80% des gewaehlten Wortes
   * (3. Wenn Kombination aus tg und Wortlaenge zu gross ist)
   * @param word
   * @return
   */
  public String correct(String word)
  {
//    System.out.println("Graph looks like this: "+this.graph.getActivations());
    if ( word == null || word.length() < 1 )
    {
      return word;
    }
    // create HashMap with value:
    // key = wordNumber
    // value = clever combination(tm) = LD*(1-activation)
    HashMap values = new HashMap();
    HashMap activations = this.graph.getActivations();
    for ( Iterator it = activations.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curNr = (Integer)it.next();
      String wordStr = (String)this.wordNrMap.get(curNr);
      double curActivation = ((Double)activations.get(curNr)).doubleValue();
      double levenshteinDistance = (double)LevenshteinDistance.LD(word, (String)this.wordNrMap.get(curNr));
      double lengthDif = (double)Math.abs(word.length() - wordStr.length());
      double combination = this.LD_INFLUENCE*levenshteinDistance +
                           ( (double)this.graph.getInputWords() - curActivation ) +
                           lengthDif*2.0;
      values.put(this.wordNrMap.get(curNr),new Double(combination));
    }
/*    System.out.println("\nMap looks finally like that: ");//+values);
    for ( Iterator it = values.keySet().iterator() ; it.hasNext() ; )
    {
      String curString = (String) it.next();
      System.out.println(curString+" : "+values.get(curString));
    }//
 //
*/
    double minVal = Double.MAX_VALUE;
    String minString = word;
    Vector orderedTopList = new Vector();
    for ( Iterator it = values.keySet().iterator() ; it.hasNext() ; )
    {
      String curString = (String) it.next();
      double curVal = ((Double)values.get(curString)).doubleValue();
      if ( curVal < minVal )
      {
        minVal = curVal;
        minString = curString;
        orderedTopList.add(0,"["+minString+":"+minVal+"]");
      }
    }

    // create and print top-most activated list
    Vector orderedActivTopList = new Vector();
    Integer maxV = new Integer(Integer.MIN_VALUE);
    for ( Iterator it = this.graph.getActivations().keySet().iterator() ; it.hasNext() ; )
    {
      Integer curNr = (Integer)it.next();
      String curWord = (String)this.wordNrMap.get(curNr);
      Integer curValue = new Integer( ((Double)this.graph.getActivations().get(curNr)).intValue() );
      if ( curValue.intValue() >= maxV.intValue() )
      {
        maxV = curValue;
        orderedActivTopList.add(0, curWord+"("+curValue+")");
      }
    }

    for ( int i = 0 ; i < Math.min(10, orderedActivTopList.size()) ; i++ )
    {
      System.out.print(" "+orderedActivTopList.get(i)+" ");
    }


    // print out the 5 top most rated guesses
    for ( int i = 0 ; i < Math.min(5, orderedTopList.size()) ; i++ )
    {
      System.out.print(" "+orderedTopList.get(i)+" ");
    }

    /*
    * noDecision:
    * 1. Wenn gar kein Buchstabe stimmt (vor allem bei kurzen!)
    * 2. Wenn LD >= 80% des gewaehlten Wortes
    * (3. Wenn Kombination aus tg und Wortlaenge zu gross ist)
    **/
    int match = findMatchingLetters(minString, word);
    double rating = ( ((double)minString.length() + (double)word.length())/2.0 )*0.751;
    double minRating = ( ((double)minString.length() + (double)word.length())/2.0 )*0.20;
    String guessedWord = new String(minString);
    if ( match < 1 || match <= minRating || (double)LevenshteinDistance.LD(word, minString) >= rating || minVal > 30.0 )
    {
      minString = "noDecision"+minString;
    }

    System.out.print(" LD="+(double)LevenshteinDistance.LD(word, guessedWord));
    System.out.print(" ac="+this.graph.getActivations().get(getNrOfWord(guessedWord)));
    System.out.print(" tg="+minVal+" ");
    return minString;
  }

  private int findMatchingLetters(String s1, String s2)
  {
    int match = 0;
    char[] chars1 = s1.toCharArray();
    char[] chars2 = s2.toCharArray();//
    for ( int i = 0 ; i < chars1.length ; i++ )
    {
      if ( s2.indexOf(chars1[i]) >= 0 ) //&& s2.indexOf(chars1[i]) >= i-1 && s2.indexOf(chars1[i]) <= i+1 )
      {
        match++;
      }
    }
    return match;
  }

  private Integer getNrOfWord(String word)
  {
    for ( Iterator it = this.wordNrMap.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curKey = (Integer)it.next();
      if ( this.wordNrMap.get(curKey).equals(word) )
      {
        return curKey;
      }
    }
    return null;
  }

  public static void main(String[] args)
  {
    String url = "jdbc:mysql://woclu2.informatik.uni-leipzig.de/de";
    String user = "sbordag";
    String passwd = "ansiBla";

    System.out.println("Connecting to DB");
    DBConnection connection = null;
    de.wortschatz.util.DBConnection connection2 = null;
    try
    {
      connection2 = new de.wortschatz.util.DBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      System.out.println("Could not establish connection, exiting.");
      System.exit(0);
    }
    SpreadingCounterActivation spreading = new SpreadingCounterActivation(connection2,2.0);
    String word = "";
    word = "Auf";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "deutschen";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "Bundesstraßen";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "zu";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "schnell";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "und";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "besoffen";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "fahren";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "kann";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "gefährlich";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    word = "sein";
    spreading.nextWordNr(connection2.getNumberForWord(word), word);
    System.out.println("Auf deutschen Bundesstrassen zu schnell und besoffen fahren kann gefährlich sein");
    while ( true )
    {
      word = spreading.guess(word);
      System.out.print(" "+word);
      spreading.nextWordNr(connection2.getNumberForWord(word), word);
    }


  }
}
