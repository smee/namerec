package com.bordag.sgz;

import java.util.*;

import java.awt.*;

import com.bordag.sgz.util.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class KohypVerifier3
{

  protected CachedDBConnection connection = null;
  protected int X = 2;
  protected int Y = 1;
  protected String word = null;

  public KohypVerifier3(String word, CachedDBConnection connection)
  {
    this.connection = connection;
    this.word = word;

    ParaSyntagma para = new ParaSyntagma(this.connection, word);
    java.util.List collocSigSet = para.getCollocSigSet();
    Set candidates = calcCandidates(collocSigSet);
    verifyCandidates(candidates);
  }

  protected Set calcCandidates(java.util.List collocSigSet)
  {
    Set retSet = new HashSet();
    double maxX = getMaxX(collocSigSet);
    double maxY = getMaxY(collocSigSet);

    Polygon poly = new Polygon();
/* Kohyponyme (und leider viel anderes Zeug)
    poly.addPoint( 2*(int)maxX, 2*(int)maxY);
    poly.addPoint( 4*(int)maxX, 2*(int)maxY);
    poly.addPoint(11*(int)maxX, 5*(int)maxY);
    poly.addPoint(11*(int)maxX,11*(int)maxY);
    poly.addPoint( 5*(int)maxX,11*(int)maxY);
    poly.addPoint( 2*(int)maxX, 4*(int)maxY);
*/
    poly.addPoint( -1*(int)maxX, 7*(int)maxY);
    poly.addPoint( 3*(int)maxX, 7*(int)maxY);
    poly.addPoint( 4*(int)maxX, 20*(int)maxY);
    poly.addPoint( -1*(int)maxX,20*(int)maxY);

    for ( Iterator it = collocSigSet.iterator() ; it.hasNext() ; )
    {
      Object[] curVal = (Object[])it.next();
      if ( poly.contains( ((Double)curVal[this.X]).doubleValue()*10.0, ((Double)curVal[this.Y]).doubleValue()*10.0 ) )
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
      ParaSyntagma para = new ParaSyntagma(this.connection, obj.toString());
      java.util.List curSigSet = para.getCollocSigSet();
      Set curCand = calcCandidates(curSigSet);
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

  protected double getMaxX(java.util.List sigSet)
  {
    double maxValue = 0;
    for ( Iterator it = sigSet.iterator() ; it.hasNext() ; )
    {
      Object[] curVal = (Object[])it.next();
      maxValue = Math.max(maxValue, ((Double)curVal[this.X]).doubleValue());
    }
    return maxValue;
  }

  protected double getMaxY(java.util.List sigSet)
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
    if ( args.length < 2 )
    {
      System.out.println("Give a range");
      System.exit(0);
    }

    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    CachedDBConnection connection = null;
    try
    {
      connection = new CachedDBConnection(url, user, passwd);

/*      for ( int i = new Integer(args[0]).intValue() ; i < new Integer(args[1]).intValue() ; i++ )
      {
        ComparableStringBuffer wordNr = new ComparableStringBuffer((new Integer(i)).toString());
        String word = connection.getWordForNumber(wordNr).toString();
        KohypVerifier3 ver = new KohypVerifier3(word, connection);
        connection.clearCache();
      }
*/
//    KohypVerifier ver = new KohypVerifier(args[0], connection);
      KohypVerifier3 ver = null;
      ver = new KohypVerifier3("Stich", connection);
      ver = new KohypVerifier3("Affe", connection);
      ver = new KohypVerifier3("Elefant", connection);
      connection.clearCache();
      ver = new KohypVerifier3("Hund", connection);
      ver = new KohypVerifier3("Ratte", connection);
      ver = new KohypVerifier3("Giraffe", connection);
      connection.clearCache();
      ver = new KohypVerifier3("Katze", connection);
      ver = new KohypVerifier3("Kuh", connection);
      ver = new KohypVerifier3("Schaf", connection);
      connection.clearCache();

      ver = new KohypVerifier3("Küche", connection);
      ver = new KohypVerifier3("Lampe", connection);
      ver = new KohypVerifier3("Tisch", connection);
      connection.clearCache();
      ver = new KohypVerifier3("Stuhl", connection);
      ver = new KohypVerifier3("Schlafzimmer", connection);
      ver = new KohypVerifier3("Wohnzimmer", connection);
      connection.clearCache();
      ver = new KohypVerifier3("Flur", connection);
      ver = new KohypVerifier3("Korridor", connection);
      ver = new KohypVerifier3("Auto", connection);
      connection.clearCache();
      ver = new KohypVerifier3("Fahrt", connection);
      ver = new KohypVerifier3("Rad", connection);
      ver = new KohypVerifier3("Dreck", connection);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      System.out.println("Could not establish connection, exiting.");
      System.exit(0);
    }

  }
}