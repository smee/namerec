package com.bordag.ksim;

import java.util.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class HypExperimente
{
  protected KollokSimilarity ksim = null;

  public HypExperimente(KollokSimilarity ksim)
  {
    this.ksim = ksim;
    measureMinWordNr(new Integer(30061), new Integer(3803));
  }

  public void measureMinWordNr(Integer inputNr, Integer targetNr)
  {
    int stepSize = 60;
    System.out.println("minWordNr\tstepSize:"+stepSize);
    System.out.println("l1size\tl2size\tcos\tanz\tminWordNr");
    for ( int i = 0 ; i < 10000 ; i+=stepSize )
    {
      // wie sich zum Elefant das Tier benimmt, bei aenderung von Optionen
      KSimOptions.getInstance().setMinWordNr(30+i);
      measurePair(inputNr, targetNr);
      System.out.println("\t"+KSimOptions.getInstance().getMinWordNr());
    }
  }


  public void measureMaxColls1(Integer inputNr, Integer targetNr)
  {
    int stepSize = 30;
    System.out.println("maxColls1\t"+stepSize);
    System.out.println("l1size\tl2size\tcos\tanz\tmaxColls1");
    for ( int i = 0 ; i < 1000 ; i+=stepSize )
    {
      // wie sich zum Elefant das Tier benimmt, bei aenderung von Optionen
      KSimOptions.getInstance().setMaxKollokationen1(30+i);
      measurePair(inputNr, targetNr);
      System.out.println("\t"+KSimOptions.getInstance().getMaxKollokationen1());
    }
  }

  public void measureMaxColls12(Integer inputNr, Integer targetNr)
  {
    int stepSize = 30;
    System.out.println("maxColls12\t"+stepSize);
    System.out.println("l1size\tl2size\tcos\tanz\tmaxColls12");
    for ( int i = 0 ; i < 1000 ; i+=stepSize )
    {
      // wie sich zum Elefant das Tier benimmt, bei aenderung von Optionen
      KSimOptions.getInstance().setMaxKollokationen12(30+i);
      measurePair(inputNr, targetNr);
      System.out.println("\t"+KSimOptions.getInstance().getMaxKollokationen12());
    }
  }

  public void measureMaxColls2(Integer inputNr, Integer targetNr)
  {
    int stepSize = 30;
    System.out.println("maxColls2\t"+stepSize);
    System.out.println("l1size\tl2size\tcos\tanz\tmaxColls2");
    for ( int i = 0 ; i < 1000 ; i+=stepSize )
    {
      // wie sich zum Elefant das Tier benimmt, bei aenderung von Optionen
      KSimOptions.getInstance().setMaxKollokationen2(30+i);
      measurePair(inputNr, targetNr);
      System.out.println("\t"+KSimOptions.getInstance().getMaxKollokationen2());
    }
  }

  public void measurePair(Integer inputNr, Integer targetNr)
  {
    RankedResultList results = ksim.calcSim(inputNr);
    System.out.print(results.list.size()+"\t"+results.list.size()+"\t");
    System.out.print(getPosition(results.list,targetNr)+"\t"+getPosition(results.list2,targetNr));

//    results.list.
  }

  protected int getPosition(TreeSet set,Integer wordNr)
  {
    int i = -1;
    for ( Iterator it = set.iterator() ; it.hasNext() ; i++ )
    {
      RankedResultList.ResultElement curNr = (RankedResultList.ResultElement) it.next();
      if ( curNr.wordNr2.intValue() == wordNr.intValue() )
      {
        return i;
      }
    }
    return i;
  }

  /**
   *
   * @param args
   */
  public static void main(String[] args)
  {
    try
    {
      DBUtil util = new DBUtil(null);
      KollokSimilarity ksim = new KollokSimilarity(util);
      HypExperimente experimente = new HypExperimente(ksim);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }
  }


}