package com.bordag.sgz;

import java.util.*;

import com.bordag.sgz.util.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class KohypVerifier
{

  protected CachedDBConnection connection = null;
  protected int X = 2;
  protected int Y = 1;
  protected double thresholdX = 0.3;
  protected double thresholdY = 0.4;
  protected double secondThresholdX = 0.2;
  protected double secondThresholdY = 0.3;

  protected String word = null;

  public KohypVerifier(String word, CachedDBConnection connection)
  {
    this.connection = connection;
    this.word = word;

    ParaSyntagma para = new ParaSyntagma(this.connection, word);
    List collocSigSet = para.getCollocSigSet();
    Set candidates = calcCandidates(collocSigSet, this.thresholdX, this.thresholdY);
    verifyCandidates(candidates);
  }

  protected Set calcCandidates(List collocSigSet, double thresholdX, double thresholdY)
  {
    Set retSet = new HashSet();
    double maxX = getMaxX(collocSigSet);
    double maxY = getMaxY(collocSigSet);
    double borderX = maxX*thresholdX;
    double borderY = maxY*thresholdY;
    for ( Iterator it = collocSigSet.iterator() ; it.hasNext() ; )
    {
      Object[] curVal = (Object[])it.next();
      if ( ( ((Double)curVal[this.X]).doubleValue() >= borderX ) &&
           ( ((Double)curVal[this.Y]).doubleValue() >= borderY ) )
      {
        retSet.add(curVal[0]);
      }
    }
    return retSet;
  }

  protected void verifyCandidates(Set candidates)
  {
//    protected List[] collocSigLists = null;
    HashMap map = new HashMap();
    System.out.println("candidates for "+this.word+" : "+candidates);
    for ( Iterator it = candidates.iterator() ; it.hasNext() ; )
    {
      Object obj = it.next();
      System.out.println("  verifying "+obj);
     // System.out.println("verifying (nr) "+this.connection.getNumberForWord(new ComparableStringBuffer((String)obj)).toString());
      ParaSyntagma para = new ParaSyntagma(this.connection, obj.toString());//new Integer(this.connection.getNumberForWord(new ComparableStringBuffer((String)obj)).toString()).toString() );
      List curSigSet = para.getCollocSigSet();
      Set curCand = calcCandidates(curSigSet, this.secondThresholdX, this.secondThresholdY);
      // candidates  = Strings
      // curCand = Strings
      for ( Iterator it2 = candidates.iterator() ; it2.hasNext() ; )
      {
        Object obj2 = it2.next();
        if ( curCand.contains(obj2) )
        {
          if ( map.containsKey(obj2) )
          {
            Integer val = (Integer)map.get(obj2);
            val = new Integer(val.intValue()+1);
            map.put(obj2, val);
          }
          else
          {
            map.put(obj2, new Integer(1));
          }
          if ( map.containsKey(obj) )
          {
            Integer val = (Integer)map.get(obj);
            val = new Integer(val.intValue()+10);
            map.put(obj, val);
          }
          else
          {
            map.put(obj, new Integer(10));
          }
        }
        //System.out.println("    preliminary Map = "+map);
      }
      System.out.println("  preFinal Map = "+map);
    }
    System.out.println("Final Map = "+map);
  }

  protected double getMaxX(List sigSet)
  {
    double maxValue = 0;
    for ( Iterator it = sigSet.iterator() ; it.hasNext() ; )
    {
      Object[] curVal = (Object[])it.next();
      maxValue = Math.max(maxValue, ((Double)curVal[this.X]).doubleValue());
    }
    return maxValue;
  }

  protected double getMaxY(List sigSet)
  {
    double maxValue = 0;
    for ( Iterator it = sigSet.iterator() ; it.hasNext() ; )
    {
      Object[] curVal = (Object[])it.next();
      maxValue = Math.max(maxValue, ((Double)curVal[this.Y]).doubleValue());
    }
    return maxValue;
  }

  public static void main(String[] args)
  {
    if ( args.length < 1 )
    {
      System.out.println("Give a word");
     // System.exit(0);
    }

    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    CachedDBConnection connection = null;
    try
    {
      connection = new CachedDBConnection(url, user, passwd);
//    KohypVerifier ver = new KohypVerifier(args[0], connection);
      KohypVerifier ver = null;
      ver = new KohypVerifier("Affe", connection);
      ver = new KohypVerifier("Elefant", connection);
      ver = new KohypVerifier("Hund", connection);
      ver = new KohypVerifier("Ratte", connection);
      ver = new KohypVerifier("Giraffe", connection);
      ver = new KohypVerifier("Katze", connection);
      ver = new KohypVerifier("Kuh", connection);
      ver = new KohypVerifier("Schaf", connection);
      connection = new CachedDBConnection(url, user, passwd);

      ver = new KohypVerifier("Küche", connection);
      ver = new KohypVerifier("Lampe", connection);
      ver = new KohypVerifier("Tisch", connection);
      ver = new KohypVerifier("Stuhl", connection);
      ver = new KohypVerifier("Schlafzimmer", connection);
      ver = new KohypVerifier("Wohnzimmer", connection);
      ver = new KohypVerifier("Flur", connection);
      ver = new KohypVerifier("Korridor", connection);
      connection = new CachedDBConnection(url, user, passwd);

      ver = new KohypVerifier("Stich", connection);
      ver = new KohypVerifier("Auto", connection);
      ver = new KohypVerifier("Fahrt", connection);
      ver = new KohypVerifier("Rad", connection);
      ver = new KohypVerifier("Dreck", connection);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      System.out.println("Could not establish connection, exiting.");
      System.exit(0);
    }

  }
}