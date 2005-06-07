package com.bordag.ksim;

import java.util.*;
import java.io.*;

/**
 * @author Stefan Bordag
 * @date   15.11.2003
 */
public class KollokSimilarity
{
  protected static final String A_beginNr = "begin";
  protected static final String A_endNr = "end";
  protected static final String A_batchSize = "size";
  protected static final String A_takeColls = "takecolls";

  /**
   * This gives comfortable access to the database
   */
  protected DBUtil dbUtil = null;

  protected BitField doneWords = null;

  public KollokSimilarity(DBUtil dbUtil)
  {
    this.dbUtil = dbUtil;
    int maxNr = this.dbUtil.getMaxWordNr();
    this.doneWords = new BitField(maxNr+1);

  }

  public void runAlgorithm(int maxNr)
  {
    // create a table, containing only the max1000kolloks collocations
    String query = "CREATE TABLE IF NOT EXISTS kollok_sim (wort_nr1 mediumint(8) unsigned not null, wort_nr2 mediumint(8) unsigned not null, anzahl mediumint(8) unsigned not null,  anz_norm mediumint(8) unsigned not null, cos mediumint(8) unsigned not null, PRIMARY KEY (wort_nr1, wort_nr2));";
    if ( ! KSimOptions.getInstance().getTakeColls() )
    {
      query = "CREATE TABLE IF NOT EXISTS kollok_sim_short (wort_nr1 mediumint(8) unsigned not null, wort_nr2 mediumint(8) unsigned not null, anzahl mediumint(8) unsigned not null,  anz_norm mediumint(8) unsigned not null, cos mediumint(8) unsigned not null, PRIMARY KEY (wort_nr1, wort_nr2));";
    }
    System.out.println(query);

    // for each word ...
    int beginNr = KSimOptions.getInstance().getBeginNr();
    int endNr = this.dbUtil.getMaxWordNr();
//    int endNr = KSimOptions.getInstance().getEndNr();
    int direction = -1;
    if ( beginNr < endNr ) { direction = 1; }

    int i=0;
    for ( i = beginNr ; i != endNr ; i+=direction )
    {
        calcSim( new Integer( i ) ).printResults();
        this.doneWords.setPosition( i, true );
        this.dbUtil.clearCache();
        if ( KSimOptions.getInstance().getShowProgress() ) { System.out.println("Done word: "+i); }
    }
    if ( KSimOptions.getInstance().getShowProgress() ) { System.out.println("Done with words! strange... "+i); }
  }

  /**
   * Calculates similarity between this word and all other possible candidates
   */
  public RankedResultList calcSim(Integer wordNr)
  {
    HashMap collocMap = new HashMap();
    // get all collocations and collocations of collocations in batches as candidates
    KollokCandidates candidates = new KollokCandidates(wordNr, this.doneWords, this.dbUtil, KSimOptions.getInstance().getTakeColls());
    List collocsOfInput = this.dbUtil.getCollocations(wordNr, KSimOptions.getInstance().getMinSignifikance(),KSimOptions.getInstance().getMaxKollokationen2(),KSimOptions.getInstance().getMinWordNr());

    RankedResultList resultList = new RankedResultList(this.dbUtil);

    while ( candidates.hasNextBatch() )
    {
      List curCandidates = candidates.getNextBatch(KSimOptions.getInstance().getBatchSize());

      if ( KSimOptions.getInstance().getShowProgress() ) { System.out.print("."); }

      // now in one step get collocs for each candidate
      List[] curCandidatesColls = this.dbUtil.getCollocations(curCandidates, KSimOptions.getInstance().getMinSignifikance(), KSimOptions.getInstance().getMaxKollokationen2(), KSimOptions.getInstance().getMinWordNr());
      // the in a for-loop compare them all with input

      for ( int i = 0 ; i < curCandidatesColls.length ; i++ )
      {
        // but not to forget to remove the selfreferences...
        int anz = calcAnz(wordNr, collocsOfInput, (Integer)curCandidates.get(i), curCandidatesColls[i]);
        double anz_norm = 0.0;
        double cos = 0.0;
        if ( anz > 1.0 )
        {
          anz_norm = calcSim(wordNr, collocsOfInput, (Integer)curCandidates.get(i), curCandidatesColls[i]);
          cos = calcCos(wordNr, collocsOfInput, (Integer)curCandidates.get(i), curCandidatesColls[i]);
        }
        if ( anz > 1.0 && anz_norm > 0.001 && cos > 0.001 )
        {
          resultList.putNextResult(wordNr, (Integer)curCandidates.get( i ), anz, anz_norm, cos);
        }
      }
    }
    //resultList.printResults();
    return resultList;
  }

  /**
   * This method calculates the cos between the two given vectors.
   * Since it gets only wordnumbers, it first fetches the significances.
   * The it builds two vectors, after which it calculates the cos between them
   *
   * @param wordNr1 the wordnr, used to get the significances
   * @param collocs the collocwordNrs, used to build the two vectors
   * @param wordNr2
   * @param collocs2
   *
   * FIXME: 2 bugs:
   * 1. collocs is as large as maxColls1, should be maxColls2
   * 2. when fetching significances it fetches them all, instead of only maxColls2
   *
   * @return
   */
  public double calcCos(Integer wordNr1, List collocs, Integer wordNr2, List collocs2)
  {
    double lengthX1 = 0.0;
    Map sigs = this.dbUtil.getSignificancesAsMap(wordNr1, collocs);
    HashSet completeSet = new HashSet(collocs.size()+collocs2.size());
    int i = 0;
    for ( Iterator it = collocs.iterator() ; it.hasNext() ; i++ )
    {
      Integer curWordNr = (Integer)it.next();
      Integer sig = (Integer)sigs.get(curWordNr);
      if ( sig != null )
      {
        lengthX1 += Math.pow(sig.doubleValue(),2.0);
      }
      completeSet.add(curWordNr);
    }
    lengthX1 = Math.sqrt(lengthX1);

    double lengthX2 = 0.0;
    Map sigs2 = this.dbUtil.getSignificancesAsMap(wordNr2, collocs2);
    i=0;
    for ( Iterator it = collocs2.iterator() ; it.hasNext() ; i++ )
    {
      Integer curWordNr = (Integer)it.next();
      Integer sig = (Integer)sigs2.get(curWordNr);
      if ( sig != null )
      {
        lengthX2 += Math.pow(sig.doubleValue(),2.0);
      }
      completeSet.add(curWordNr);
    }
    lengthX2 = Math.sqrt( lengthX2 );

    ArrayList completeVec = new ArrayList(completeSet);
    double cosAlpha = 0.0;
    for ( Iterator it = completeVec.iterator() ; it.hasNext() ; )
    {
      Integer curWordNr = (Integer)it.next();
      Integer sig1 = (Integer)sigs.get(curWordNr);
      Integer sig2 = (Integer)sigs2.get(curWordNr);
      if ( sig1 != null && sig2 != null )
      {
        cosAlpha += sig1.doubleValue()*sig2.doubleValue();
      }
    }
    return cosAlpha/(lengthX1*lengthX2);
  }

  public double calcCosFreqNorm(Integer wordNr1, List collocs, Integer wordNr2, List collocs2)
  {
    double lengthX1 = 0.0;
    Map sigs = this.dbUtil.getSignificancesAsMap(wordNr1, collocs);
    HashSet completeSet = new HashSet(collocs.size()+collocs2.size());
    int i = 0;
    Integer maxSig1 = new Integer(0);
    for ( Iterator it = collocs.iterator() ; it.hasNext() ; i++ )
    {
      Integer curWordNr = (Integer)it.next();
      Integer sig = (Integer)sigs.get(curWordNr);
      if ( sig != null )
      {
        lengthX1 += Math.pow(sig.doubleValue(),2.0);
      }
      if ( maxSig1.intValue() < sig.intValue() )
      {
        maxSig1 = new Integer( sig.intValue() );
      }
      completeSet.add(curWordNr);
    }
    lengthX1 = Math.sqrt(lengthX1);

    double lengthX2 = 0.0;
    Map sigs2 = this.dbUtil.getSignificancesAsMap(wordNr2, collocs2);
    i=0;
    Integer maxSig2 = new Integer(0);
    for ( Iterator it = collocs2.iterator() ; it.hasNext() ; i++ )
    {
      Integer curWordNr = (Integer)it.next();
      Integer sig = (Integer)sigs2.get(curWordNr);
      if ( sig != null )
      {
        lengthX2 += Math.pow(sig.doubleValue(),2.0);
      }
      if ( maxSig2.intValue() < sig.intValue() )
      {
        maxSig2 = new Integer( sig.intValue() );
      }
      completeSet.add(curWordNr);
    }
    lengthX2 = Math.sqrt( lengthX2 );

    double ratio = (double)Math.min(maxSig1.intValue(), maxSig2.intValue()) /
                (double)Math.max(maxSig1.intValue(), maxSig2.intValue());

    List smaller = collocs;
    Map smallerMap = sigs;
    if ( maxSig1.intValue() > maxSig2.intValue() )
    {
      smaller = collocs2;
      smallerMap = sigs2;
    }
    for ( Iterator it = smaller.iterator() ; it.hasNext() ; i++ )
    {
      Integer curWordNr = ( Integer )it.next();
      Integer sig = ( Integer )smallerMap.get( curWordNr );
      sig = new Integer ( (int)((double)sig.intValue()*ratio) );
    }

    ArrayList completeVec = new ArrayList(completeSet);
    double cosAlpha = 0.0;
    for ( Iterator it = completeVec.iterator() ; it.hasNext() ; )
    {
      Integer curWordNr = (Integer)it.next();
      Integer sig1 = (Integer)sigs.get(curWordNr);
      Integer sig2 = (Integer)sigs2.get(curWordNr);
      if ( sig1 != null && sig2 != null )
      {
        cosAlpha += sig1.doubleValue()*sig2.doubleValue();
      }
    }
    return cosAlpha/(lengthX1*lengthX2);
  }

  public double calcSim(Integer wordNr1, List collocs, Integer wordNr2, List collocs2)
  {
    return getMatchingElements(wordNr1, collocs, wordNr2, collocs2)/Math.min((double)collocs.size(),(double)collocs2.size());
  }

  public int calcAnz(Integer wordNr1, List collocs, Integer wordNr2, List collocs2)
  {
    return (int)getMatchingElements(wordNr1, collocs, wordNr2, collocs2);
  }

  /**
   * Returns the number of elements which are in both given vectors
   * @param wordNr1
   * @param collocs
   * @param wordNr2
   * @param collocs2
   * @return
   */
  public double getMatchingElements(Integer wordNr1, List collocs, Integer wordNr2, List collocs2)
  {
    double retVal = 0.0;
    List biggerList = collocs;
    Integer numberOfBigger = wordNr1;
    List smallerList = collocs2;
    Integer numberOfSmaller = wordNr2;
    if ( biggerList.size() < smallerList.size() )
    {
      biggerList = collocs2;
      numberOfBigger = wordNr2;
      smallerList = collocs;
      numberOfSmaller = wordNr1;
    }

    HashSet tempSet = new HashSet();
    tempSet.addAll(biggerList);
    for ( Iterator it = smallerList.iterator() ; it.hasNext() ; )
    {
      Integer curNum = (Integer)it.next();
      if ( tempSet.contains(curNum) )
      {
        retVal += 1.0;
      }
    }
    return retVal;
  }

  /**
   *
   * @param args
   */
  public static void main(String[] args)
  {
    for ( int i = 0 ; i < args.length ; i++ )
    {
      if ( args[i].equalsIgnoreCase(A_batchSize) && args.length > (i+1) )
      {
        KSimOptions.getInstance().setBatchSize(new Integer(args[i+1]).intValue());
      }
      else if ( args[i].equalsIgnoreCase(A_beginNr) && args.length > (i+1) )
      {
        KSimOptions.getInstance().setBeginNr(new Integer(args[i+1]).intValue());
      }
      else if ( args[i].equalsIgnoreCase(A_endNr) && args.length > (i+1) )
      {
        KSimOptions.getInstance().setEndNr(new Integer(args[i+1]).intValue());
      }
      else if ( args[i].equalsIgnoreCase(A_takeColls) && args.length > (i+1) )
      {
        KSimOptions.getInstance().setTakeColls(new Boolean(args[i+1]).booleanValue());
      }
    }

    String url = KSimOptions.getInstance().getConUrl();
    String user = KSimOptions.getInstance().getConUser();
    String passwd = KSimOptions.getInstance().getConPasswd();

    DBConnection connection = null;
    try
    {
//      connection = new DBConnection(url, user, passwd);
      connection = null;
      DBUtil util = new DBUtil(connection);
      KollokSimilarity ksim = new KollokSimilarity(util);
      ksim.runAlgorithm(util.getMaxWordNr());
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }
  }
}
