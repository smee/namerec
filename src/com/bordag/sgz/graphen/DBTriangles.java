package com.bordag.sgz.graphen;

// standard imports
import java.util.*;

// app specific imports
import com.bordag.sgz.util.*;


/**
 * Counts how manz triangles there are in wortschatz.kollok_sig
 */
public class DBTriangles
{
  /**
   * The source of information about the graph
   **/
  private DBConnection connection = null;

  /**
   * The query with which to retrieve the neighbours of a word
   **/
//  private final static String TRIANGLES_COUNT_QUERY = "select #ARG#, #ARG#, kx.wort_nr2 from kollok_sig kx, kollok_sig ky where kx.wort_nr1 = #ARG# and ky.wort_nr2 = #ARG# and kx.wort_nr2 = ky.wort_nr1;";

  /**
   * Drops a given entry from DB
   **/
//  private final static String PAIR_DROP_QUERY = "drop from kollok_sig where wort_nr1 = #ARG# and wort_nr2 = #ARG#;";

  /**
   * Returns some entry (not defined which one)
   **/
//  private final static String NEXT_PAIR_QUERY = "select wort_nr1, wort_nr2 from kollok_sig limit 1;";

  /**
   * Returns the number of entries in kollok_sig
   **/
//  private final static String CONN_COUNT_QUERY = "select count(*) from kollok_sig;";

//------------TEST----------------
  /**
   * The query with which to retrieve the neighbours of a word
   **/
  private final static String TRIANGLES_COUNT_QUERY = "select count(kx.wort_nr2) from kollok_sig kx, kollok_sig ky where kx.wort_nr1 = #ARG# and ky.wort_nr2 = #ARG# and kx.wort_nr2 = ky.wort_nr1;";

  /**
   * Drops a given entry from DB
   **/
  private final static String PAIR_DROP_QUERY = "delete from kollok_sig where wort_nr1 = #ARG# and wort_nr2 = #ARG#;";

  /**
   * Returns some entry (not defined which one)
   **/
  private final static String NEXT_PAIR_QUERY = "select wort_nr1, wort_nr2 from kollok_sig limit 1;";

  /**
   * Returns the number of entries in kollok_sig
   **/
  private final static String CONN_COUNT_QUERY = "select count(*) from kollok_sig;";



  public DBTriangles(DBConnection connection)
  {
    this.connection = connection;
  }

  public void countTriangles()
  {
    int pairs = getPairsCount();
    int triangles = 0;
    System.out.println("wort_n1,wort_nr2\ttriangles");
    Integer[] pair = null;
    for ( int i = 0 ; i < pairs ; i++ )
    {
      try
      {
        // get next pair (just some next pair)
        pair = getNextPair();
        if ( pair == null )
        {
          System.err.println("Run "+i+" yielded no pair.");
          continue;
        }
        // set off TRIANGLES_COUNT_QUERY
        int curCount = getTrianglesCount(pair);
        triangles += curCount;
        System.out.println(pair[0]+","+pair[1]+"\t"+curCount);
        // drop pair
        dropPair(pair);
      }
      catch ( Exception ex )
      {
        System.err.println("In run "+i+" with words "+pair[0]+","+pair[1]+" following Exception: ");
        ex.printStackTrace();
      }
    }
    System.out.println("Overall : "+triangles);
  }

  protected void dropPair(Integer[] pair)
  {
    //drop both variants of pair!
    String[] args1 = new String[2];
    String[] args2 = new String[2];
    args1[0] = pair[0].toString();
    args1[1] = pair[1].toString();
    args2[0] = pair[1].toString();
    args2[1] = pair[0].toString();
    try
    {
      connection.getResultsOf(this.PAIR_DROP_QUERY, args1);
      connection.getResultsOf(this.PAIR_DROP_QUERY, args2);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  protected int getTrianglesCount(Integer[] pair)
  {
    String[] args = new String[2];
    args[0] = pair[0].toString();
    args[1] = pair[1].toString();
    ComparableStringBuffer[][] resultsOfQuery = connection.getResultsOf(this.TRIANGLES_COUNT_QUERY,args);
    if ( resultsOfQuery == null || resultsOfQuery.length == 0 )
    {
      return 0;
    }
    return new Integer( resultsOfQuery[0][0].toString() ).intValue();
  }

  /**
   * Returns the next entry
   */
  protected Integer[] getNextPair()
  {
    ComparableStringBuffer[][] resultsOfQuery = connection.getResultsOf(this.NEXT_PAIR_QUERY);
    Integer[] retVal = new Integer[2];
    retVal[0] = new Integer( resultsOfQuery[0][0].toString() );
    retVal[1] = new Integer( resultsOfQuery[0][1].toString() );
    return retVal;
  }

  /**
   * Returns the number of connections in kollok_sig
   **/
  protected int getPairsCount()
  {
    int retVal = 0;
    // DB here
    ComparableStringBuffer[][] resultsOfQuery = connection.getResultsOf(this.CONN_COUNT_QUERY);
    retVal = new Integer( resultsOfQuery[0][0].toString() ).intValue();
    return retVal/2;
  }

  public static void main(String[] argv)
  {
    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();
    DBConnection connection = null;
    try
    {
      connection = new DBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      Output.println("Could not establish connection, exiting.");
      System.exit(0);
    }

    DBTriangles triangles = new DBTriangles(connection);
    triangles.countTriangles();
  }
}