package com.bordag.ksim;

import java.util.*;
import java.sql.*;

import com.bordag.colloc.*;

/**
 * @author Stefan Bordag
 * @date   19.11.2003
 */

final public class DBUtil implements java.io.Serializable
{
  /**
   * The string for which this class will look to replace with query arguments
   */
  public static final String ARGUMENT_PLACE = "#ARG#";

  protected transient DBConnection connection = null;

  protected Collocations fastCollocs = null;

  protected FileWortliste fastWortliste = null;

  protected FileGrfNAVS fastGrfNAVS = null;

  public DBUtil(DBConnection connection)
  {
    this.connection = connection;

    try
    {
      if ( KSimOptions.getInstance().getLoadCollsToRam() )
      {
        this.fastCollocs = new RamColloc( "data/frieder/kollok_sig_bolted.dump" );
        //this.fastCollocs = new RamColloc( "data/ksim/kollok_sig.dump" );
      }
      else
      {
        this.fastCollocs = new FileColloc( "data/frieder/kollok_sig_bolted.dump" );
        //this.fastCollocs = new FileColloc( "data/ksim/kollok_sig.dump" );
      }
      this.fastWortliste = new FileWortliste( "data/frieder/wortliste_bolted.dump" );
      //this.fastWortliste = new FileWortliste( "data/ksim/wortliste.dump" );
      this.fastGrfNAVS = new FileGrfNAVS("data/ksim/grfNAVS.dump");
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  public void clearCache()
  {
  }

  public void printCache()
  {
    System.exit(0);
  }

  /**
   * Returns the word from the database which is associated with the given number
   */
  public CHString getWordForNumber(CHString number)
  {
    return new CHString( this.fastWortliste.getWord(new Integer(number.toString())) );
  }

  /**
   * Returns the word from the database which is associated with the given number
   */
  public String getWordForNumber(Integer number)
  {
    return this.fastWortliste.getWord(number) ;
  }


  /**
   * Returns the word from the database which is associated with the given number
   */
  public Integer getNumberForWord(CHString word)
  {
    String query = KSimOptions.getInstance().getQueryNumberForWord();
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
    List retVec = new ArrayList(numbers.size());
    List v = this.fastWortliste.getWordsForNumbersSameOrder(numbers);
    for ( Iterator it = v.iterator() ; it.hasNext() ; )
    {
      String curWord = (String)it.next() ;
      retVec.add(new CHString(curWord));
    }
    return retVec;
  }

  public Map getSignificancesAsMap(Integer wordNr, List numbers)
  {
    HashMap retMap = new HashMap();
    List l = this.fastCollocs.getCollocsAndSigs(wordNr, KSimOptions.getInstance().getMinSignifikance(), KSimOptions.getInstance().getMaxKollokationen2(), KSimOptions.getInstance().getMinWordNr());
    for ( Iterator it = l.iterator() ; it.hasNext() ; )
    {
      Integer[] curVals = (Integer[]) it.next();
      retMap.put(curVals[0], curVals[1]);
    }
    return retMap;
  }

  public List getSignificancesForNumbers(Integer wordNr, List numbers)
  {
    Integer[] nrs = new Integer[numbers.size()];
    for ( int i = 0 ; i < nrs.length ; i++ )
    {
      nrs[i] = (Integer)numbers.get(i);
    }
    nrs = getSignificancesForNumbers(wordNr, nrs);
    List retVec = new ArrayList(nrs.length);
    for ( int i = 0 ; i < nrs.length ; i++ )
    {
      retVec.add(nrs[i]);
    }
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
    Integer[] retArr = null;
    List colls = this.fastCollocs.getCollocsAndSigs(wordNr);
    HashMap map = new HashMap();
    for ( Iterator it = colls.iterator() ; it.hasNext() ; )
    {
      Integer[] curVals = (Integer[])it.next();
      map.put(curVals[0],curVals[1]);
    }
    retArr = new Integer[numbers.length];
    for ( int i = 0 ; i < retArr.length ; i++ )
    {
      retArr[i] = (Integer)map.get(numbers[i]);
    }
    return retArr;
  }

  /**
   * Returns the strings of words (because the one in DBConnection orders them,
   * thus loosing the connection to the wordnumber
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
   * Returns how many time a given word as a number in the DB has been seen.
   */
  public int getFrequencyOfWordNr(CHString wordNr)
  {
    return ((Integer)((Object[])this.fastWortliste.getWordAndAnzahl(new Integer(wordNr.toString())))[1]).intValue();
  }

  /**
   * Retrieves the collocations of a given word(number)
   */
  public List getCollocations(Integer word, int myMinSignificance, int myMaxCollocs, int myMinWordNr)
  {
    if ( this.fastCollocs != null )
    {
      ArrayList retVec = new ArrayList();
      for ( Iterator it = this.fastCollocs.getCollocsAndSigs(word, myMinSignificance, myMaxCollocs, myMinWordNr).iterator() ; it.hasNext() ; )
      {
        Integer[] vals = (Integer[])it.next();
        retVec.add(vals[0]);
      }
      return retVec;
    }
    return new ArrayList();
  }

  /**
   * This gives the collocations of the wordNrs
   * @param wordNrs
   * @return
   */
  public List[] getCollocations(List wordNrs, int myMinSignificance, int myMaxCollocs, int myMinWordNr)
  {
    if ( this.fastCollocs != null )
    {
      List[] retList = new ArrayList[wordNrs.size()];
      int i = 0;
      for ( Iterator it = wordNrs.iterator(); it.hasNext(); i++ )
      {
        Integer curNr = ( Integer ) it.next();
        retList[i] = getCollocations( curNr, myMinSignificance, myMaxCollocs, myMinWordNr );
      }
      return retList;
    }
    return new List[0];
  }

  /**
   * This returns all the wordNrs which occur in the resultsets of getting
   * collocations of the given wordNrs
   * @param wordNrs
   * @return
   */
  public List getCollocationsStack(List wordNrs, int myMinSignificance, int myMaxCollocs, int myMinWordNr)
  {
    HashSet retSet = new HashSet();
    List[] collsOfColls = getCollocations(wordNrs, myMinSignificance, myMaxCollocs, myMinWordNr);
    for ( int i = 0 ; i < collsOfColls.length ; i++ )
    {
      retSet.addAll(collsOfColls[i]);
    }
    return new ArrayList(retSet);
  }

  /**
   * Returns true if the given word is a noun. Currently requires connection
   * to aspra16.
   * @param wordNr
   * @return
   */
  protected String getWordForm(Integer wordNr)
  {
    String retVal = "";
    for ( Iterator it = this.fastGrfNAVS.getWortarten(wordNr).iterator() ; it.hasNext() ; )
    {
      retVal += it.next();
    }
    return retVal;
  }


  /**
   * Returns true if the given word is a noun.
   * @param wordNr
   * @return
   */
  protected boolean checkWortart(Integer wordNr, String wortart)
  {
    if ( this.fastGrfNAVS != null &&
         wordNr != null )
    {
      for ( Iterator it = this.fastGrfNAVS.getWortarten(wordNr).iterator() ; it.hasNext() ; )
      {
        if ( ((String)it.next()).equalsIgnoreCase(wortart) )
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns true if the given word is a noun.
   * @param wordNr
   * @return
   */
  protected boolean isNoun(Integer wordNr)
  {
    if ( this.checkWortart(wordNr, "N") || this.checkWortart(wordNr, "S") )
    {
      return true; // |16
    }
    return false;
  }

  /**
   * Returns true if the given word is an adjective.
   * @param wordNr
   * @return
   */
  protected boolean isAdjective(Integer wordNr)
  {
    return this.checkWortart(wordNr, "A"); // |26
  }

  /**
   * Returns true if the given word is a verb.
   * @param wordNr
   * @return
   */
  protected boolean isVerb(Integer wordNr)
  {
    return this.checkWortart(wordNr, "V"); // |36
  }

  /**
   * Returns true if the given word is a stopword.
   * @param wordNr
   * @return
   */
  protected boolean isStopword(Integer wordNr)
  {
    return this.checkWortart(wordNr, "S"); // |??
  }

  protected int getMaxWordNr()
  {
    return this.fastWortliste.getMaxWordNr();
/*    String query = "select w.wort_nr from wortliste w order by wort_nr desc limit 1;";
    CHString[][] res = this.connection.executeQuery(query);
    return new Integer(res[0][0].toString()).intValue();*/
  }

  public Connection getConnection()
  {
    return this.connection.getConnection();
  }

}
