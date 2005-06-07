package com.bordag.parasyn.util;

import java.util.*;
import java.sql.Connection;

import com.bordag.util.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class DBUtilUncached implements java.io.Serializable
{
  protected transient DBConnection connection = null;

  public DBUtilUncached(DBConnection connection)
  {
    this.connection = connection;
  }

  /**
   * Returns the word from the database which is associated with the given number
   */
  public CHString getWordForNumber(CHString number)
  {
    String query = Options.getInstance().getGenQueryNumber2Word();
    String[] args = new String[1];
    args[0] = number.toString();
    CHString[][] temp = this.connection.getResultsOf(query,args);
    if ( temp != null )
    {
      return temp[0][0];
    }
    return null;
  }

  /**
   * Returns the word from the database which is associated with the given number
   */
  public Integer getNumberForWord(CHString word)
  {
    String query = Options.getInstance().getGenQueryWord2Number();
    String[] args = new String[1];
    args[0] = word.toString();
    CHString[][] temp = this.connection.getResultsOf(query, args);
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      return new Integer(temp[0][0].toString());
    }
    return null;
  }

  /**
   * Calls getNumbersForWordsSameORder(Object[] words)
   * @param words
   * @return List of CHStrings containing the words
   */
  public List getWordsForNumbersSameOrder(List numbers)
  {
    Vector retVec = new Vector(numbers.size());
    Object[] numberObjs = new Object[numbers.size()];
    System.out.println("numbers in uncached: "+numbers.size());
/*    int i = 0;
    for ( Iterator it = numbers.iterator() ; it.hasNext() ; i++ )
    {
      numberObjs[i] = it.next();
    }
    CHString[] words = getWordsForNumbersSameOrder(numberObjs);*/
    for ( int i = 0 ; i < numbers.size() ; i++ )
    {
      retVec.add( i, this.getWordForNumber( new CHString ( numbers.get( i ).toString() ) ) );
    }
/*    for ( i = 0 ; i < words.length ; i++ )
    {
      retVec.add(i, words[i] );
    }*/
    System.out.println("returning in uncached: "+retVec.size());
    return retVec;
  }

  /**
   * For the given wordNr and it's (assumed) collocations, returns their
   * collocation significances and prints a warning, if the input number is
   * different then the output number of elements as this produces realy logical
   * errors. (assigning significances to wrong collocations).
   * @param wordNr
   * @param numbers
   * @return
   */
  public Integer[] getSignificancesForNumbers(Integer wordNr, Integer[] numbers)
  {
    Arrays.sort(numbers, new CHString(""));

    if ( numbers == null || numbers.length < 1 )
    {
      return null;
    }
    String queryBegin = "select signifikanz from kollok_sig where wort_nr1 = "+wordNr+" and (";
    String queryMid1   = "wort_nr2 = ";
    String queryMid2   = " or ";
    String queryEnd    = ") order by wort_nr2 asc;";
    String query = queryBegin;
    for ( int i = 0 ; i < numbers.length ; i++ )
    {
      query = query + "" + queryMid1 + numbers[i].toString();
      if ( i < numbers.length - 1 )
      {
        query = query + queryMid2;
      }
    }
    query = query + "" + queryEnd;
    CHString[][] temp = this.connection.executeQuery(query);
    Integer[] temp2 = new Integer[temp.length];
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      for ( int i = 0 ; i < temp.length ; i++ )
      {
        temp2[i]=new Integer(temp[i][0].toString());
      }
    }
    if ( numbers.length != temp2.length )
    {
      System.err.println("Warning, retrieving for word "+wordNr+" collocations yeilded less significances then expected.");
    }
    return temp2;
  }

  /**
   * Returns the strings of words (because the one in DBConnection orders them,
   * thus loosing the connection to the wordnumber
   * @param numbers
   * @return
   */
  public CHString[] getWordsForNumbersSameOrder(Object[] numbers)
  {
    Arrays.sort(numbers, new CHString(""));
    if ( numbers == null || numbers.length < 1 )
    {
      return null;
    }
    String queryBegin = "select w.wort_bin from wortliste w where ";
    String queryMid1   = "w.wort_nr=";
    String queryMid2   = " or ";
    String queryEnd    = " order by w.wort_nr asc;";
    String query = queryBegin;
    for ( int i = 0 ; i < numbers.length ; i++ )
    {
      query = query + "" + queryMid1 + numbers[i].toString();
      if ( i < numbers.length - 1 )
      {
        query = query + queryMid2;
      }
    }
    query = query + "" + queryEnd;
    CHString[][] temp = this.connection.executeQuery(query);
    CHString[] temp2 = new CHString[temp.length];
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      for ( int i = 0 ; i < temp.length ; i++ )
      {
        temp2[i]=temp[i][0];
      }
    }
    return temp2;
  }

  /**
   * Calls getNumbersForWordsSameORder(Object[] words)
   * @param words
   * @return
   */
  public List getNumbersForWordsSameOrder(List words)
  {
    Vector retVec = new Vector(words.size());
    Object[] wordObjs = new Object[words.size()];
    int i = 0;
    for ( Iterator it = words.iterator() ; it.hasNext() ; i++ )
    {
      wordObjs[i] = it.next();
    }
    CHString[] numbers = getNumbersForWordsSameOrder(wordObjs);
    for ( i = 0 ; i < numbers.length ; i++ )
    {
      retVec.add(i, new Integer(numbers[i].toString()) );
    }
    return retVec;
  }

  /**
   * Returns the strings of words (because the one in DBConnection orders them,
   * thus loosing the connection to the wordnumber
   * @param numbers
   * @return
   */
  public CHString[] getNumbersForWordsSameOrder(Object[] words)
  {
    Arrays.sort(words, new CHString(""));
    if ( words == null || words.length < 1 )
    {
      return null;
    }
    String queryBegin = "select w.wort_nr from wortliste w where ";
    String queryMid1   = "w.wort_bin=\"";
    String queryMid2   = "\" or ";
    String queryEnd    = "\" group by wort_bin order by w.wort_bin asc;";
    String query = queryBegin;
    for ( int i = 0 ; i < words.length ; i++ )
    {
      query = query + "" + queryMid1 + words[i].toString();
      if ( i < words.length - 1 )
      {
        query = query + queryMid2;
      }
    }
    query = query + "" + queryEnd;
    CHString[][] temp = this.connection.executeQuery(query);
    CHString[] temp2 = new CHString[temp.length];
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      for ( int i = 0 ; i < temp.length ; i++ )
      {
        temp2[i]=temp[i][0];
      }
    }
    return temp2;
  }

  /**
   * Returns how many time a given word as a number in the DB has been seen.
   */
  public int getFrequencyOfWordNr(CHString wordNr)
  {
    if ( wordNr == null )
    {
      return -1;
    }
    String query = Options.getInstance().getGenQueryFrequency();
    String[] args = new String[1];
    args[0] = wordNr.toString();
    CHString[][] temp = this.connection.getResultsOf(query, args);
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      return new Integer(temp[0][0].toString()).intValue();
    }
    return -2;
  }


  /**
   * Retrieves the collocations of a given word(number)
   * Returns Vector<Integer[2]> where first is wordNr, second significance
   */
  public Vector getCollocationSims(Integer word)
  {
    Vector retVec = new Vector();
    String[] s = new String[4];
    //s[0] = Options.getInstance().getParaMinSignifikanz();
    s[0] = "0.0";
    s[1] = Options.getInstance().getParaMinWordNr();
    s[2] = word.toString();
    s[3] = Options.getInstance().getParaMaxKollokationen();
    String query = "SELECT k.wort_nr2,k.anz_norm FROM kollok_sim_short k WHERE k.anz_norm > #ARG# and k.wort_nr2 > #ARG# and k.wort_nr1 = #ARG# order by k.signifikanz desc limit #ARG#";
    CHString[][] buffer = this.connection.getResultsOf(query,s);
    if ( buffer == null || buffer.length < 1 )
    {
      System.err.println("Wordnr ["+word+"]: had no collocations!");
      return retVec;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        Integer[] val = new Integer[2];
        val[0] = new Integer(buffer[i][0].toString());
        val[1] = new Integer(buffer[i][1].toString());    // ???? next line ??????
        if ( ! word.equals(val[0]) )
        {
          retVec.add(val);
        }
      }
    }
    return retVec;
  }

  /**
   * Retrieves the collocations of a given word(number)
   * Returns Vector<Integer[2]> where first is wordNr, second significance
   */
  public List getCollocations(Integer word, String minSig)
  {
    Vector retVec = new Vector();
    String[] s = new String[4];
    //s[0] = Options.getInstance().getParaMinSignifikanz();
    s[0] = minSig;
    s[1] = Options.getInstance().getParaMinWordNr();
    s[2] = word.toString();
    s[3] = Options.getInstance().getParaMaxKollokationen();
    String query = Options.getInstance().getParaQueryKollokationen();
    CHString[][] buffer = this.connection.getResultsOf(query,s);
    if ( buffer == null || buffer.length < 1 )
    {
      System.err.println("Wordnr ["+word+"]: had no collocations!");
      return retVec;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        Integer[] val = new Integer[2];
        val[0] = new Integer(buffer[i][0].toString());
        val[1] = new Integer(buffer[i][1].toString());    // ???? next line ??????
        val[1] = new Integer( val[1].intValue() - new Integer( Options.getInstance().getParaMinSignifikanz() ).intValue() );
        if ( ! word.equals(val[0]) )
        {
          retVec.add(val);
        }
      }
    }
    return retVec;
  }



  /**
   * Retrieves the collocations of a given word(number)
   * Each element of the vector is a two field Integer -
   * the first is the wordnumber,
   * the second is the meaning number
   */
  public Vector getDisambiguation(Integer word)
  {
    Vector retVec = new Vector();
    String[] s = new String[2];
    s[0] = Options.getInstance().getParaMinWordNr();
    s[1] = word.toString();
    String query = Options.getInstance().getParaQueryDisambig();
//    System.out.println("Query is ["+query+"]");
    CHString[][] buffer = this.connection.getResultsOf(query,s);
    if ( buffer == null || buffer.length < 1 )
    {
      System.err.println("Wordnr ["+word+"]: had no disambiguation!");
      return retVec;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        Integer[] val = new Integer[2];
        val[0] = new Integer(buffer[i][0].toString());
        val[1] = new Integer(buffer[i][1].toString());
        if ( ! word.equals(val[0]) )
        {
          retVec.add(val);
        }
      }
    }
    return retVec;
  }

  /**
   * Retrieves the collocations of a given word(number)
   * Each element of the vector is a two field Integer -
   * the first is the wordnumber,
   * the second is the meaning number
   */
  public Vector getDisambiguationFallback(Integer word, String minSig)
  {
    Vector retVec = new Vector();
    String[] s = new String[4];
    s[0] = minSig;
    s[1] = Options.getInstance().getParaMinWordNr();
    s[2] = word.toString();
    s[3] = Options.getInstance().getParaMaxKollokationen();
    String query = Options.getInstance().getParaQueryKollokationen();
//    System.out.println("Query is ["+query+"]");
    CHString[][] buffer = this.connection.getResultsOf(query,s);
    if ( buffer == null || buffer.length < 1 )
    {
      System.err.println("Wordnr ["+word+"]: had no collocations!");
      return retVec;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        Integer[] val = new Integer[2];
        val[0] = new Integer(buffer[i][0].toString());
        val[1] = new Integer(42);

        if ( ! word.equals(val[0]) )
        {
          retVec.add(val);
        }
      }
    }
    return retVec;
  }


  public Connection getConnection()
  {
    return this.connection.getConnection();
  }

}
