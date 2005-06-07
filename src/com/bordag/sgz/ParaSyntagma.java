package com.bordag.sgz;

// standard imports
import java.util.*;

// app specific imports
import com.bordag.sgz.util.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */
public class ParaSyntagma
{

  protected DBConnection connection = null;
  protected Integer wordNr = null;
  protected List collocSet = null;
  protected List collocSigSet = null;
  protected Hashtable collocCollocSet = null;

  protected List collocWordslogSigSet = null;
  protected int X = 2;
  protected int Y = 1;

  public ParaSyntagma(DBConnection connection, String word)
  {
    this.connection = connection;
    this.collocSet = new Vector();
    this.collocSigSet = new Vector();
    this.wordNr = new Integer(this.connection.getNumberForWord(new ComparableStringBuffer(word)).toString());
    getCollocations(this.wordNr);
    try
    {
      ((CachedDBConnection)this.connection).cacheCollocationsOf(new ComparableStringBuffer(this.wordNr.toString()), collocSet);
      this.collocCollocSet = ((CachedDBConnection)this.connection).collocationsCache;
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(0);
    }
    compare();
    transform();
    //printCollocSigSet();
  }

  public void transform()
  {
    ComparableStringBuffer[] buf = new ComparableStringBuffer[this.collocSigSet.size()];
    int i = 0;
    for ( Iterator it = this.collocSigSet.iterator() ; it.hasNext() ; )
    {
      Integer[] curVal = (Integer[])it.next();
      buf[i] = new ComparableStringBuffer(curVal[0].toString());
      i++;
    }
    ComparableStringBuffer[] words = this.getWordsForNumbers(buf);
    this.collocWordslogSigSet = new Vector();
    for ( int j = 0 ; j < words.length ; j++ )
    {
      Object[] obj = new Object[3];
      obj[0] = new String(words[j].toString());
      obj[1] = new Double( Math.max(0,Math.log(((Integer[])this.collocSigSet.get(j))[1].doubleValue())) );
      obj[2] = new Double( Math.max(0,Math.log(((Integer[])this.collocSigSet.get(j))[2].doubleValue())) );
//      obj[1] = new Double( (((Integer[])this.collocSigSet.get(j))[1].doubleValue()/10) );
//      obj[2] = new Double( (((Integer[])this.collocSigSet.get(j))[2].doubleValue()/10) );
      this.collocWordslogSigSet.add(obj);
    }
  }

  public ComparableStringBuffer[] getWordsForNumbers(Object[] numbers)
  {
    if ( numbers == null || numbers.length < 1 )
    {
      return null;
    }
/*    String queryBegin = "select w.wort_bin from wortliste w where ";
    String queryMid1   = "w.wort_nr=";
    String queryMid2   = " or ";
    String queryEnd    = " ;";
*/
    String queryBegin = "(select w.wort_bin from wortliste w where ";
    String queryMid1   = "w.wort_nr=";
    String queryMid2   = ") union ( select w.wort_bin from wortliste w where ";
    String queryEnd    = " );";
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
//System.out.println("Query : ["+query+"]");
    ComparableStringBuffer[][] temp = this.connection.getResultsOf(query);
    ComparableStringBuffer[] temp2 = new ComparableStringBuffer[temp.length];
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      for ( int i = 0 ; i < temp.length ; i++ )
      {
        temp2[i]=temp[i][0];
      }
    }
    return temp2;
  }

  public ParaSyntagma(String word)
  {
    this.collocSet = new Vector();
    this.collocSigSet = new Vector();
    connect();
    this.wordNr = new Integer(this.connection.getNumberForWord(new ComparableStringBuffer(word)).toString());
    getCollocations(this.wordNr);
    try
    {
      ((CachedDBConnection)this.connection).cacheCollocationsOf(new ComparableStringBuffer(this.wordNr.toString()), collocSet);
      this.collocCollocSet = ((CachedDBConnection)this.connection).collocationsCache;
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(0);
    }
    compare();
    printCollocSigSet();
  }

  protected void compare()
  {
    List newCollocSigSet = new Vector();
    for ( Iterator it = this.collocSigSet.iterator() ; it.hasNext() ; )
    {
      Integer[] curWordNr = (Integer[])it.next();
      HashSet stringBuffers = (HashSet)this.collocCollocSet.get(new ComparableStringBuffer(curWordNr[0].toString()));
//      System.out.println("Got "+stringBuffers+"");
      int x = compare(this.collocSigSet, stringBuffers);
      curWordNr[2] = new Integer(x);
      if ( ! curWordNr[0].equals(this.wordNr) )
      {
        newCollocSigSet.add(curWordNr);
      }
    }
    this.collocSigSet = newCollocSigSet;
  }

  // List where there are Integer[3] (take the Integer[0])
  // with HashSet, where there are ComparableStringBuffers
  protected int compare(List integers, HashSet stringBuffers)
  {
    int counter = 0;
    if ( stringBuffers == null || stringBuffers.size() < 1 )
    {
      return 0;
    }
    for ( Iterator it = integers.iterator() ; it.hasNext() ; )
    {
      Integer[] curVal = (Integer[])it.next();
      if ( stringBuffers.contains(new ComparableStringBuffer(curVal[0].toString())) )
      {
        counter++;
      }
    }
    return counter;
  }

  protected void printCollocSigSet()
  {
    System.out.println("collocSigSet = ");
    for ( Iterator it = this.collocSigSet.iterator() ; it.hasNext() ; )
    {
      Integer[] val = (Integer[])it.next();
      System.out.println(this.connection.getWordForNumber(new ComparableStringBuffer(val[0].toString())).toString().replace(' ','_')+"\t"+val[1]+"\t"+val[2]);
    }
  }

  protected void connect()
  {
    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    try
    {
      this.connection = new CachedDBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      Output.println("Could not establish connection, exiting.");
      System.exit(0);
    }
  }

  /**
   * Retrieved the collocations of a given word(number)
   */
  protected void getCollocations(Integer word)
  {
    String[] s = new String[4];
    s[0] = Options.getInstance().getDisMinSignifikanz();
    s[1] = Options.getInstance().getDisMinWordNr();
    s[2] = word.toString();
    s[3] = Options.getInstance().getDisMaxKollokationen();
    String query = Options.getInstance().getDisQueryKollokationen();
    ComparableStringBuffer[][] buffer = this.connection.getResultsOf(query,s);
    if ( buffer == null || buffer.length < 1 )
    {
      return ;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        Integer[] val = new Integer[3];
        val[0] = new Integer(buffer[i][0].toString());
        val[1] = new Integer(buffer[i][1].toString());
        val[2] = new Integer(0);
        this.collocSigSet.add(val);
        this.collocSet.add(buffer[i][0]);
      }
    }
  }

  protected double getMaxX()
  {
    double maxValue = 0;
    for ( Iterator it = this.collocWordslogSigSet.iterator() ; it.hasNext() ; )
    {
      Object[] curVal = (Object[])it.next();
      maxValue = Math.max(maxValue, ((Double)curVal[this.X]).doubleValue());
    }
    return maxValue;
  }

  protected double getMaxY()
  {
    double maxValue = 0;
    for ( Iterator it = this.collocWordslogSigSet.iterator() ; it.hasNext() ; )
    {
      Object[] curVal = (Object[])it.next();
      maxValue = Math.max(maxValue, ((Double)curVal[this.Y]).doubleValue());
    }
    return maxValue;
  }


  public List getCollocSigSet()
  {
    return this.collocWordslogSigSet;
  }

  public static void main(String[] args)
  {
    if ( args.length < 1 )
    {
      Output.println("Give me a word!");
      System.exit(0);
    }
    ParaSyntagma syn = new ParaSyntagma(args[0]);
  }

}