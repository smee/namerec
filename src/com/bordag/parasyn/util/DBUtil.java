package com.bordag.parasyn.util;

import java.util.*;
import java.sql.*;

import com.bordag.util.*;
import com.bordag.colloc.*;


/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class DBUtil extends DBUtilUncached implements java.io.Serializable
{
  private boolean debug = false;

  /**
   * The string for which this class will look to replace with query arguments
   */
  public static final String ARGUMENT_PLACE = "#ARG#";

  protected Collocations fastCollocs = null;

  protected FileWortliste fastWortliste = null;

  public DBUtil(DBConnection connection)
  {
    super(connection);
    try
    {
      this.fastCollocs = new FileColloc( "data/ksim/kollok_sig.dump" );
      this.fastWortliste = new FileWortliste( "data/ksim/wortliste.dump" );
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  public void cacheHint(CHString word)
  {
  }

  protected void getCollsWStringsNumbersSigs(Integer wordNr)
  {
  }

  /**
   * Replaces the #ARG# strings in the query with the given arguments
   */
  private String addArguments(String query, String[] args) throws IllegalArgumentException
  {
    if ( query == null || args == null || query.length() < 1 || args.length < 1 )
    {
      throw new IllegalArgumentException("In DBUtil.addArguments(String, String[]) query or arguments are invalid.");
    }
    query = removeNewLines(query);
    int beginReplace = 0;
    int endReplace = 0;
    for ( int i = 0 ; i < args.length ; i++ )
    {
      beginReplace = query.indexOf(this.ARGUMENT_PLACE);
      endReplace = query.indexOf(this.ARGUMENT_PLACE) + this.ARGUMENT_PLACE.length();
      if ( args[i] == null || args[i].length() < 1 )
      {
        throw new IllegalArgumentException("In DBUtil.addArguments(String, String[]) Argument["+i+"] is invalid!");
      }
      try
      {
        query = query.substring(0, beginReplace) + args[i] + query.substring(endReplace, query.length());
      }
      catch(Exception ex)
      {
        String temp = "";
        for ( int j = 0 ; j < args.length ; j++ )
        {
          temp = temp+" "+args[j];
        }
        throw new IllegalArgumentException("In DBUtil.addArguments(String, String[]) Query ["+query+"] was used with wrong number of arguments: "+temp);
      }
    }
    return query;
  }

  private String removeNewLines(String string)
  {
    string = string.replace('\n',' ');
    return string;
  }

  /**
   * Before calculating a new word, it might make sense to clear cache.
   */
  public void clearCache()
  {
  }

//-----------------here overridden access methods

  /**
   * Returns the word from the database which is associated with the given number
   */
  public CHString getWordForNumber(CHString number)
  {
    if ( this.fastWortliste != null )
    {
      return new CHString(this.fastWortliste.getWord(new Integer(number.toString())));
    }
    if ( debug ) { System.out.println("Warning: fallback for "+number);}
    return super.getWordForNumber(number);
  }

  /**
   * Returns the word from the database which is associated with the given number
   */
  public Integer getNumberForWord(CHString word)
  {
    return super.getNumberForWord(word);
  }

  /**
   * At any point, if a missing item is detected, the procedure aborts and the
   * method from the super class is called.
   * @param numbers (Integer)
   * @return List of CHStrings containing the words
   */
  public List getWordsForNumbersSameOrder(List numbers)
  {
    if ( this.fastWortliste != null )
    {
      Vector retVec = new Vector(numbers.size());
      List v = this.fastWortliste.getWordsForNumbersSameOrder(numbers);
      for ( Iterator it = v.iterator() ; it.hasNext() ; )
      {
        String curWord = (String)it.next() ;
        retVec.add(new CHString(curWord));
      }
      return retVec;
    }
    return super.getWordsForNumbersSameOrder(numbers);
  }

  /**
   * @param numbers
   * @return
   */
  public CHString[] getWordsForNumbersSameOrder(Object[] numbers)
  {
    CHString[] retArr = new CHString[numbers.length];
    for ( int i = 0 ; i < numbers.length ; i++ )
    {
      retArr[i] = new CHString( this.fastWortliste.getWord( new Integer(numbers[i].toString())) );
    }
    return retArr;
  }

  /**
   * For the given wordNr and it's (assumed) collocations, returns their
   * collocation significances
   * @param wordNr
   * @param numbers
   * @return
   */
  public Integer[] getSignificancesForNumbers(Integer wordNr, Integer[] numbers)
  {
    List tempNums = new Vector(numbers.length);
    for ( int i = 0 ; i < numbers.length ; i++ )
    {
      tempNums.add(numbers[i]);
    }
    tempNums = this.fastCollocs.getCollocsAndSigs(wordNr);
    HashMap map = new HashMap();
    for ( Iterator it = tempNums.iterator() ; it.hasNext() ; )
    {
      Integer[] curVals = (Integer[])it.next();
      map.put(curVals[0],curVals[1]);
    }
    Integer[] retNums = new Integer[tempNums.size()];
    for ( int i = 0 ; i < numbers.length ; i++ )
    {
      retNums[i] = (Integer)map.get(numbers[i]);
    }
    return retNums;
  }

  /**
   * Calls getNumbersForWordsSameORder(Object[] words)
   * @param words
   * @return
   */
  public List getNumbersForWordsSameOrder(List words)
  {
    return super.getNumbersForWordsSameOrder(words);
  }

  /**
   * Returns the strings of words (because the one in DBConnection orders them,
   * thus loosing the connection to the wordnumber
   * @param numbers
   * @return
   */
  public CHString[] getNumbersForWordsSameOrder(Object[] words)
  {
     return super.getWordsForNumbersSameOrder(words);
  }

  /**
   * Returns how many time a given word as a number in the DB has been seen.
   */
  public int getFrequencyOfWordNr(CHString wordNr)
  {
    return ((Integer)((Object[])this.fastWortliste.getWordAndAnzahl(new Integer(wordNr.toString())))[1]).intValue();
  }

  /**
   * Retrieves the collocations of a given word(number)
   */
  public List getCollocations(Integer word, String minSig)
  {
    return this.fastCollocs.getCollocsAndSigs(word);
  }

  /**
   */
  public Vector getDisambiguation(Integer word)
  {
    System.out.println("Disambiguation called!");
    return super.getDisambiguation(word);
  }

  /**
   * Retrieves the collocations of a given word(number)
   */
  public Vector getDisambiguationFallback(Integer word, String minSig)
  {
    //System.out.println( "DisambiguationFallback called!" );
    Vector retVec = new Vector();
    List colls = this.fastCollocs.getCollocs(word);
    Integer fallback = new Integer(42);
    for ( Iterator it = colls.iterator() ; it.hasNext() ; )
    {
      Integer vals = (Integer) it.next();
      Integer[] newVals = new Integer[2];
      newVals[0] = vals;
      newVals[1] = fallback;
      retVec.add(newVals);
    }
    return retVec;
  }
}