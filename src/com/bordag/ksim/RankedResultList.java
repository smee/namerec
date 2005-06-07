package com.bordag.ksim;

import java.util.*;
import java.io.*;

/**
 * Whenever KollokSimilarity calculates one more result for a word, it puts it
 * here. When done completely with a given word, it calls print and then
 * destroys this object.
 *
 * @author Stefan Bordag
 * @date   19.11.2003
 */
public class RankedResultList
{
  protected TreeSet list = null;
  protected TreeSet list2 = null;

  protected int maxSize = 500;

  protected DBUtil dbUtil = null;

  public RankedResultList(DBUtil util)
  {
    this.maxSize = KSimOptions.getInstance().getMaxResultsSizePerWord();

    this.list = new TreeSet(new ResultCos());
    this.list2 = new TreeSet(new ResultAnz());
    this.dbUtil = util;
    try
    {
      Integer maxSize = new Integer(KSimOptions.getInstance().getMaxResultsSizePerWord());
      this.maxSize = maxSize.intValue();
    }
    catch ( Exception ex )
    {
    }
  }

  /**
   * Seeks the correct place in the list, puts the element there and removes the
   * last element.
   * Words in this list are ordered by anz first, then cos, then anz_norm.
   *
   * @Todo: Do some better implementation, like binary search or sth.
   *
   * @param wordNr1
   * @param wordNr2
   * @param anz
   * @param anz_norm
   * @param cos
   */
  public void putNextResult(Integer wordNr1, Integer wordNr2, double anz, double anz_norm, double cos)
  {
    ResultElement el = new ResultElement(wordNr1, wordNr2, anz, anz_norm, cos);
    this.list.add(el);
    while ( this.list.size() > this.maxSize )
    {
      this.list.remove(this.list.last());
    }

    this.list2.add(el);
    while ( this.list2.size() > this.maxSize )
    {
      this.list2.remove(this.list2.last());
    }
  }

  public void printResults()
  {
    String fileName = KSimOptions.getInstance().getOutputMainString();
    if ( this.list.size() > 0 )
    {
      printDBResultsCos( fileName );
      printDBResultsAnz( fileName );
      printWords( fileName );
      printAnnotate( fileName );
    }
  }

  public void printDBResultsCos(String fileName)
  {
    int factor = KSimOptions.getInstance().getResultsFactor();
    try
    {
      FileWriter writer = new FileWriter( fileName + "_db_cos.txt", true );
      for ( Iterator it = this.list.iterator() ; it.hasNext() ; )
      {
        ResultElement curEl = (ResultElement)it.next();

        writer.write( curEl.wordNr1 + "\t" + curEl.wordNr2 + "\t"+(int)curEl.anz+"\t"+((int)(curEl.anz_norm*factor))+"\t"+((int)(curEl.cos*factor))+"\n" );
        writer.write( curEl.wordNr2 + "\t" + curEl.wordNr1 + "\t"+(int)curEl.anz+"\t"+((int)(curEl.anz_norm*factor))+"\t"+((int)(curEl.cos*factor))+"\n" );

        //System.out.println( "INSERT INTO kollok_sim VALUES (" + curEl.wordNr1 + "," + curEl.wordNr2 + ","+curEl.anz+","+curEl.anz_norm+","+curEl.cos+");" );
        //System.out.println( "INSERT INTO kollok_sim VALUES (" + curEl.wordNr2 + "," + curEl.wordNr1 + ","+curEl.anz+","+curEl.anz_norm+","+curEl.cos+");" );
      }
      writer.close();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  public void printDBResultsAnz(String fileName)
  {
    int factor = KSimOptions.getInstance().getResultsFactor();
    try
    {
      FileWriter writer = new FileWriter( fileName + "_db_anz.txt", true );
      for ( Iterator it = this.list2.iterator() ; it.hasNext() ; )
      {
        ResultElement curEl = (ResultElement)it.next();

        writer.write( curEl.wordNr1 + "\t" + curEl.wordNr2 + "\t"+(int)curEl.anz+"\t"+((int)(curEl.anz_norm*factor))+"\t"+((int)(curEl.cos*factor))+"\n" );
        writer.write( curEl.wordNr2 + "\t" + curEl.wordNr1 + "\t"+(int)curEl.anz+"\t"+((int)(curEl.anz_norm*factor))+"\t"+((int)(curEl.cos*factor))+"\n" );

        //System.out.println( "INSERT INTO kollok_sim VALUES (" + curEl.wordNr1 + "," + curEl.wordNr2 + ","+curEl.anz+","+curEl.anz_norm+","+curEl.cos+");" );
        //System.out.println( "INSERT INTO kollok_sim VALUES (" + curEl.wordNr2 + "," + curEl.wordNr1 + ","+curEl.anz+","+curEl.anz_norm+","+curEl.cos+");" );
      }
      writer.close();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  public void printWords(String fileName)
  {
    int factor = KSimOptions.getInstance().getResultsFactor();
    try
    {
      FileWriter writer = new FileWriter( fileName + "_words.txt", true );
      for ( Iterator it = this.list.iterator(); it.hasNext(); )
      {
        ResultElement curEl = ( ResultElement )it.next();

        writer.write( this.dbUtil.getWordForNumber( curEl.wordNr1 ) +
                            "\t" + this.dbUtil.getWordForNumber( curEl.wordNr2 ) +
                            "\t" + ( int )curEl.anz + "\t" +
                            ( ( int ) ( curEl.anz_norm * factor ) ) + "\t" +
                            ( ( int ) ( curEl.cos * factor ) ) +"\n" );
      }
      writer.close();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  public void printAnnotate(String fileName)
  {
    String fullOrHalf = "half";
    if ( KSimOptions.getInstance().getTakeColls() )
    {
      fullOrHalf = "full";
    }
    try
    {

      FileWriter writer = new FileWriter( fileName+"_"+fullOrHalf+"cos.txt", true );
      String sourceWord = null;
      String curTargetWord = null;
      int count = 0;
      for ( Iterator it = this.list.iterator() ; it.hasNext() ; count++ )
      {
        ResultElement curEl = (ResultElement)it.next();
        if ( sourceWord == null )
        {
          sourceWord = this.dbUtil.getWordForNumber( curEl.wordNr1 ).toString();
          writer.write(fullOrHalf+"cos:\t"+sourceWord+"|0");
        }
        curTargetWord = this.dbUtil.getWordForNumber( curEl.wordNr2 ).toString();
        if ( count < 1 )
	{
          continue;
	}
        if ( count <= 20 && curEl.cos > 0.25 )
        {
//          System.out.print("w1 = "+this.dbUtil.getWordForm(curEl.wordNr1)+" w2 = "+this.dbUtil.getWordForm(curEl.wordNr2));
          if ( this.dbUtil.isNoun(curEl.wordNr1) && this.dbUtil.isNoun(curEl.wordNr2) )
          { // N N Kohyponym
//            System.out.print("|16");
            writer.write( "\t" + curTargetWord + "|0|16" );
          }
          else if ( this.dbUtil.isAdjective(curEl.wordNr1) && this.dbUtil.isAdjective(curEl.wordNr2) )
          { // A A Kohyponym
//            System.out.print("|26");
            writer.write( "\t" + curTargetWord + "|0|26" );
          }
          else if ( this.dbUtil.isVerb(curEl.wordNr1) && this.dbUtil.isVerb(curEl.wordNr2) )
          { // V V Kohyponym
//            System.out.print("|36");
            writer.write( "\t" + curTargetWord + "|0|36" );
          }
          else if ( this.dbUtil.isAdjective(curEl.wordNr1) && this.dbUtil.isNoun(curEl.wordNr2) )
          { // A N hat typische Eigenschaft
//            System.out.print("|40");
            writer.write( "\t" + curTargetWord + "|0|40" );
          }
          else if ( this.dbUtil.isNoun(curEl.wordNr1) && this.dbUtil.isAdjective(curEl.wordNr2) )
          { // N A hat typische Eigenschaft
//            System.out.print("|41");
            writer.write( "\t" + curTargetWord + "|0|41" );
          }
          else if ( this.dbUtil.isVerb(curEl.wordNr1) && this.dbUtil.isNoun(curEl.wordNr2) )
          { // V N ist typische Taetigkeit
//            System.out.print("|42");
            writer.write( "\t" + curTargetWord + "|0|42" );
          }
          else if ( this.dbUtil.isNoun(curEl.wordNr1) && this.dbUtil.isVerb(curEl.wordNr2) )
          { // N V typisches Objekt/Instrument von
//            System.out.print("|74");
            writer.write( "\t" + curTargetWord + "|0|74" );
          }
          else if ( this.dbUtil.isAdjective(curEl.wordNr1) && this.dbUtil.isVerb(curEl.wordNr2) )
          { // A V ist typische Eigenschaft
//            System.out.print("|44");
            writer.write( "\t" + curTargetWord + "|0|44" );
          }
          else if ( this.dbUtil.isVerb(curEl.wordNr1) && this.dbUtil.isAdjective(curEl.wordNr2) )
          { // V A hat typische Eigenschaft
//            System.out.print("|45");
            writer.write( "\t" + curTargetWord + "|0|45" );
          }
          else
          {
            writer.write( "\t" + curTargetWord + "|0|0" );
          }
        }
        else
        {
          writer.write( "\t" + curTargetWord + "|0|0" );
        }
        if (count >= 20 )
        {
          break;
        }
      }
      writer.write("\n");
      writer.close();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }


  class ResultCos implements Comparator
  {
    public int compare(Object obj1, Object obj2)
    {
      int DIR1 = -1;
      int DIR2 = 1;
      if ( obj1 instanceof ResultElement && obj2 instanceof ResultElement )
      {
        ResultElement res = (ResultElement)obj1;
        ResultElement res2 = (ResultElement)obj2;
        if ( res.cos > res2.cos )
        {
          return DIR1;
        }
        else if ( res.cos < res2.cos )
        {
          return DIR2;
        }

        if ( res.anz > res2.anz )
        {
          return DIR1;
        }
        else if ( res.anz < res2.anz )
        {
          return DIR2;
        }

        if ( res.anz_norm > res2.anz_norm )
        {
          return DIR1;
        }
        else if ( res.anz_norm < res2.anz_norm )
        {
          return DIR2;
        }
      }
      return 0;
    }

    public boolean equals(Object obj)
    {
      return this.equals(obj);
    }
  }

  class ResultAnz implements Comparator
  {
    public int compare(Object obj1, Object obj2)
    {
      int DIR1 = -1;
      int DIR2 = 1;
      if ( obj1 instanceof ResultElement && obj2 instanceof ResultElement )
      {
        ResultElement res = (ResultElement)obj1;
        ResultElement res2 = (ResultElement)obj2;

        if ( res.anz > res2.anz )
        {
          return DIR1;
        }
        else if ( res.anz < res2.anz )
        {
          return DIR2;
        }

        if ( res.cos > res2.cos )
        {
          return DIR1;
        }
        else if ( res.cos < res2.cos )
        {
          return DIR2;
        }

        if ( res.anz_norm > res2.anz_norm )
        {
          return DIR1;
        }
        else if ( res.anz_norm < res2.anz_norm )
        {
          return DIR2;
        }
      }
      return 0;
    }

    public boolean equals(Object obj)
    {
      return this.equals(obj);
    }
  }


  /**
   * To make a convenient ordering of the result element, these are fomalized
   * into a class which implements the Comparable interface.
   *
   * @author Stefan Bordag
   * @date   19.11.2003
   */
  class ResultElement
  {
    protected Integer wordNr1 = null;
    protected Integer wordNr2 = null;
    protected double anz = 0.0;
    protected double anz_norm = 0.0;
    protected double cos = 0.0;

    public ResultElement(Integer wordNr1, Integer wordNr2, double anz, double anz_norm, double cos)
    {
      this.wordNr1 = wordNr1;
      this.wordNr2 = wordNr2;
      this.anz = anz;
      this.anz_norm = anz_norm;
      this.cos = cos;
    }

  }

}
