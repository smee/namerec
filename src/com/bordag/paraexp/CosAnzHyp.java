package com.bordag.paraexp;

import java.util.*;
import com.bordag.colloc.*;

/**
 * select k.wort_nr1, k.wort_nr2, k.cos into outfile '/var/roedel/ksim/kollok_sim_cos.dump' from kollok_sim_fullcos k where k.wort_nr1 != k.wort_nr2 order by k.wort_nr1 asc, k.cos desc;
 *
 *
 * select k.wort_nr1, k.wort_nr2, k.anzahl into outfile '/var/roedel/ksim/kollok_sim_anz.dump' from kollok_sim_fullcos k where k.wort_nr1 != k.wort_nr2 order by k.wort_nr1 asc, k.anzahl desc;
 *
 * @author Stefan Bordag
 */
public class CosAnzHyp
{
  protected Collocations cos = null;
  protected Collocations anz = null;
  protected FileWortliste wort_bin = null;

  protected int MINSIG = 4;
  protected int MAXCOLLS = 200;
  protected int MINWORDNR = 500;

  public CosAnzHyp()
  {
    this.cos = new FileColloc( "data/ksim/kollok_sim_cos.dump" );
    this.anz = new FileColloc( "data/ksim/kollok_sim_anz.dump" );
    this.wort_bin = new FileWortliste( "data/ksim/wortliste.dump" );
/*    for ( Iterator itAnz = this.cos.getCollocsAndSigs(new Integer(30061), this.MINSIG, this.MAXCOLLS, this.MINWORDNR).iterator() ; itAnz.hasNext() ; )
    {
      Integer[] curAnz = ( Integer[] )itAnz.next();
      Integer curAnzNr = curAnz[0];
      Integer curAnzSig = curAnz[1];
      System.out.println(""+this.wort_bin.getWord(curAnzNr));
    }
    System.exit(0);*/
  }

  public void calculateWord(Integer wordNr)
  {
    // This map stores wordNr(Integer) -> value(Double)
    Map map = new HashMap();

    for ( Iterator itCos = normalizeSigs(this.cos.getCollocsAndSigs(wordNr, this.MINSIG, this.MAXCOLLS, this.MINWORDNR)).iterator() ; itCos.hasNext() ; )
    {
      Object[] curCos = (Object[])itCos.next();
      Integer curCosNr = (Integer)curCos[0];
      Double curCosSig = (Double)curCos[1];
      for ( Iterator itAnz = normalizeSigs(this.anz.getCollocsAndSigs(curCosNr, this.MINSIG, this.MAXCOLLS, this.MINWORDNR)).iterator() ; itAnz.hasNext() ; )
      {
        Object[] curAnz = (Object[])itAnz.next();
        Integer curAnzNr = (Integer)curAnz[0];
        Double curAnzSig = (Double)curAnz[1];
        Double value = null;
        if ( map.containsKey(curAnzNr) )
        {
          value = (Double)map.get(curAnzNr);
        }
        else
        {
          value = new Double(0.0);
        }
        value = new Double( value.doubleValue() + curCosSig.doubleValue() * curAnzSig.doubleValue() );
        //System.out.println("For word ["+this.wort_bin.getWord(curCosNr)+"] voting word ["+this.wort_bin.getWord(curAnzNr)+"]: "+value);
        map.put(curAnzNr, value);

      }
      System.out.println("size: "+map.size());
    }

    // now copy all elements from the map into the set in order to sort them
    Set results = new TreeSet(new ResultComparator());
    for ( Iterator it = map.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curKey = (Integer)it.next();
      Double curVal = (Double)map.get(curKey);
      ResultElement el = new ResultElement(curKey, curVal.doubleValue());
      results.add(el);
    }

    // for testing only, print results here:
    System.out.println("Calculated word "+this.wort_bin.getWord(wordNr));
    int count = 500;
    for ( Iterator it = results.iterator() ; it.hasNext() && count > 0 ; count-- )
    {
      ResultElement curEl = (ResultElement)it.next();
      System.out.println(this.wort_bin.getWord(curEl.wordNr)+"\t"+curEl.value);
    }
  }

  /**
   * Gets Integer[2] where first is wordnr and second the values to be
   * normalized to something between 0 and 1
   * @param collsAndSigs
   * @return
   */
  protected List normalizeSigs(List collsAndSigs)
  {
    // determine min and max
//    System.out.println("before: "+collsAndSigs);
    Integer minVal = new Integer(Integer.MAX_VALUE);
    Integer maxVal = new Integer(Integer.MIN_VALUE);
    for ( Iterator it = collsAndSigs.iterator() ; it.hasNext() ; )
    {
      Integer[] curVal = (Integer[])it.next();
      if ( minVal.intValue() > curVal[1].intValue() )
      {
        minVal = curVal[1];
      }
      if ( maxVal.intValue() < curVal[1].intValue() )
      {
        maxVal = curVal[1];
      }
    }

    List retList = new Vector();
    for ( Iterator it = collsAndSigs.iterator() ; it.hasNext() ; )
    {
      Integer[] curVal = (Integer[])it.next();
      Object[] curNewVal = new Object[2];
      curNewVal[0] = curVal[0];
      curNewVal[1] = new Double( (curVal[1].doubleValue() - minVal.doubleValue())/(maxVal.doubleValue()) );
      retList.add(curNewVal);
    }
//    System.out.println("after: "+collsAndSigs);
    return retList;
  }

  public static void main(String[] args)
  {
    CosAnzHyp c = new CosAnzHyp();
    c.calculateWord(new Integer(30061));
  }

  /**
   * This class is the means to sort the results
   */
  class ResultComparator implements Comparator
  {
    public int compare(Object obj1, Object obj2)
    {
      int DIR1 = -1;
      int DIR2 = 1;
      if ( obj1 instanceof ResultElement && obj2 instanceof ResultElement )
      {
        ResultElement res = (ResultElement)obj1;
        ResultElement res2 = (ResultElement)obj2;
        if ( res.value > res2.value )
        {
          return DIR1;
        }
        else if ( res.value < res2.value )
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
   * This subclass contains the result elements which are stored in the resultset
   */
  class ResultElement
  {
    public Integer wordNr = null;
    public double value = 0.0;

    public ResultElement(Integer nr, double val)
    {
      this.wordNr = nr;
      this.value = val;
    }

    public int hashCode()
    {
      return this.wordNr.intValue();
    }
  }
}