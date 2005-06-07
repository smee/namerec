package com.bordag.sc.util;

import java.util.*;
import java.io.*;

import com.bordag.sgz.util.*;

/**
 * Gets a valid connection to Wortschatz DB and receives wordStrings/wordNumbers
 * After calling sync method, makes sure it always has the according
 * translations cached.
 * For faster searching is realized as having information twice: (two hashtables)
 * first hashtable: wordNR -> wordStr
 * second hashtable: wordStr -> wordNr
 * For convenience, it can be set to synchronized mode, where it will always
 * synchronize the moment it gets a new word/number
 *
 * FIXME: Word->Number Currently broken as I don't know how to massively ask a database for
 * wordnumbers such that whenever a word doesn't exist, it would return a
 * placeholder
 *
 * @author Stefan Bordag
 * @date 15.08.2003
 */
public class WordServer implements Serializable
{

  protected transient DBConnection connection = null;

  /**
   * These Hashsets contain ComparableStringBuffers
   */
  protected HashSet pendingNrs = null;
  protected HashSet pendingStrs = null;

  protected Hashtable NrToStr = null;
  protected Hashtable StrToNr = null;

  protected boolean synched = false;

  public WordServer(DBConnection connection)
  {
    this.connection = connection;
    init();
  }

  public WordServer(DBConnection connection, boolean synched)
  {
    this.connection = connection;
    this.synched = synched;
    init();
  }

  public void setConnection(DBConnection connection)
  {
    this.connection = connection;
  }

  private void init()
  {
    this.NrToStr = new Hashtable();
    this.StrToNr = new Hashtable();
    this.pendingNrs = new HashSet();
    this.pendingStrs = new HashSet();
  }

  /**
  * Makes all pending items cached
  */
  public void sync()
  {
    synchronized ( this.getClass() )
    {
      if ( this.pendingNrs.size() > 0 )
      {
        ComparableStringBuffer[] bufNrs = new ComparableStringBuffer[this.pendingNrs.size()];
        int i = 0;
        for (Iterator it = this.pendingNrs.iterator(); it.hasNext(); i++ )
        {
          bufNrs[i] = new ComparableStringBuffer( ((Integer)it.next()).toString() );
        }
        ComparableStringBuffer[] bufWords = this.connection.getWordsForNumbersSameOrder(bufNrs);
        for ( int j = 0 ; j < bufWords.length ; j++ )
        {
          this.NrToStr.put(new Integer(bufNrs[j].toString()), bufWords[j].toString());
          this.StrToNr.put(bufWords[j].toString(), new Integer(bufNrs[j].toString()));
        }
      }
      this.pendingNrs.clear();
      if ( this.pendingStrs.size() > 0 )
      {
        ComparableStringBuffer[] bufStrs = new ComparableStringBuffer[this.pendingStrs.size()];
        int i = 0;
        for (Iterator it = this.pendingStrs.iterator(); it.hasNext(); i++ )
        {
          bufStrs[i] = new ComparableStringBuffer( (String)it.next() );
        }
        ComparableStringBuffer[] bufNrs = this.connection.getNumbersForWordsSameOrder(bufStrs);
        for ( int j = 0 ; j < bufNrs.length ; j++ )
        {
          this.StrToNr.put(bufStrs[j].toString(), new Integer( bufNrs[j].toString()) );
          this.NrToStr.put(new Integer( bufNrs[j].toString()), bufStrs[j].toString() );
        }
      }
      this.pendingStrs.clear();
    }
  }

  // --------- these all methods add numbers to the pending numbers list
  public void addNumber(Integer number)
  {
    this.pendingNrs.add(number);
    if ( this.synched ){sync();}
  }

  public void addNumbers(Integer[] numbers)
  {
    for ( int i = 0 ; i < numbers.length ; i++ )
    {
      this.pendingNrs.add(numbers[i]);
    }
    if ( this.synched ){sync();}
  }

  public void addNumbers(Set numbers)
  {
    this.pendingNrs.addAll(numbers);
    if ( this.synched ){sync();}
  }

  public void addNumbers(List numbers)
  {
    this.pendingNrs.addAll(numbers);
    if ( this.synched ){sync();}
  }

  // --------- these all methods add words to the pending words list
  public void addStr(String word)
  {
    this.pendingStrs.add(word);
      sync();
    if ( this.synched ){sync();}
  }

  public void addStrs(String[] words)
  {
    for ( int i = 0 ; i < words.length ; i++ )
    {
      this.pendingStrs.add(words[i]);
        sync();
    }
    if ( this.synched ){sync();}
  }

  public void addStrs(Set words)
  {
    for ( Iterator it = words.iterator() ; it.hasNext() ; )
    {
      this.pendingNrs.add(it.next());
      sync();
    }
//    this.pendingStrs.addAll(words);
    if ( this.synched ){sync();}
  }

  public void addStrs(List words)
  {
//    this.pendingStrs.addAll(words);
    for ( Iterator it = words.iterator() ; it.hasNext() ; )
    {
      this.pendingNrs.add(it.next());
      sync();
    }
    if ( this.synched ){sync();}
  }

  // --------- these methods allow access to the cache
  public void put(Integer number, String word)
  {
    this.NrToStr.put(number, word);
    this.StrToNr.put(word, number);
  }

  public Integer getNrForWord(String word)
  {
    if ( this.StrToNr.containsKey(word) )
    {
      return (Integer)this.StrToNr.get(word);
    }
    return null;
  }

  public String getWordForNr(Integer nr)
  {
    if ( this.NrToStr.containsKey(nr) )
    {
      return (String)this.NrToStr.get(nr);
    }
    return null;
  }

  /**
   * Tests this class for proper functioning
   */
  public static void main(String[] args)
  {
    String url = "jdbc:mysql://woclu2.informatik.uni-leipzig.de/de";
    String user = "sbordag";
    String passwd = "ansiBla";

    DBConnection connection = null;
    try
    {
      connection = new DBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      System.out.println("Could not establish connection, exiting.");
      System.exit(0);
    }
    WordServer server = new WordServer(connection, false);
    HashSet set = new HashSet();
    set.add(new Integer(4));set.add(new Integer(5));set.add(new Integer(6));
    server.addNumbers(set);
    server.addNumber(new Integer(1));
    server.addNumber(new Integer(2));
    server.addNumber(new Integer(3));

    HashSet set2 = new HashSet();
    set2.add("Stefan");set2.add("Programmierer");set2.add("ist");
    server.addStrs(set2);
    server.addStr("programmieren");
    server.addStr("keine");
    server.addStr("Erholung");

    server.sync();

    System.out.println("testing number -> Word:");
    System.out.println(new Integer(1)+" -> "+server.getWordForNr(new Integer(1)));
    System.out.println(new Integer(2)+" -> "+server.getWordForNr(new Integer(2)));
    System.out.println(new Integer(3)+" -> "+server.getWordForNr(new Integer(3)));
    System.out.println(new Integer(4)+" -> "+server.getWordForNr(new Integer(4)));
    System.out.println(new Integer(5)+" -> "+server.getWordForNr(new Integer(5)));
    System.out.println(new Integer(6)+" -> "+server.getWordForNr(new Integer(6)));

    System.out.println("testing Word -> number:");
    System.out.println("Stefan"+" -> "+server.getNrForWord("Stefan"));
    System.out.println("Programmierer"+" -> "+server.getNrForWord("Programmierer"));
    System.out.println("ist"+" -> "+server.getNrForWord("ist"));
    System.out.println("programmieren"+" -> "+server.getNrForWord("programmieren"));
    System.out.println("keine"+" -> "+server.getNrForWord("keine"));
    System.out.println("Erholung"+" -> "+server.getNrForWord("Erholung"));

    System.out.println("testing for stability:");
    server.sync();server.sync();server.sync();
    System.out.println(new Integer(0)+" -> "+server.getWordForNr(new Integer(0)));
    server.addNumber(new Integer(-1));
    System.out.println(new Integer(-1)+" -> "+server.getWordForNr(new Integer(-1)));
    server.addStr("diesIstGanzBestimmtKeinGueltigesWortImWortschatz");
    System.out.println("diesIstGanzBestimmtKeinGueltigesWortImWortschatz"+" -> "+server.getNrForWord("diesIstGanzBestimmtKeinGueltigesWortImWortschatz"));
    System.out.println("Seems okay, are the numbers->words okay?");
  }
}