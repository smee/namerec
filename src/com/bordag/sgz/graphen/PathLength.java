package com.bordag.sgz.graphen;

// standard imports
import java.util.*;

// app specific imports
import com.bordag.sgz.util.*;

/**
 * Tries to find a possible path between two nodes of a graph
 **/
public class PathLength
{
  /**
   * Format of this HashMap:
   * key = wordNum
   * value = Integer[2];
   *   Integer[0] = length to wordNum from node1
   *   Integer[1] = length to wordNum from node2
   **/
  private HashMap lengthMap = null;

  /**
   * The first node
   **/
  private Integer node1 = null;
  
  /**
   * The second node
   **/
  private Integer node2 = null;

  /**
   * The source of information about the graph
   **/
  private DBConnection connection = null;

  /**
   * Look always at that many neighbours, ordered descending by frequency
   **/
  private final static String NEIGHBOURS_LIMIT = "20";

  /**
   * The query with which to retrieve the neighbours of a word
   **/
  private final static String NEIGHBOURS_QUERY = "select k.wort_nr1 from kollok_sig k, wortliste w where k.wort_nr2 = #ARG# and w.wort_nr = k.wort_nr1 order by w.anzahl desc limit #ARG#;";

  /**
   * Only initializes this object
   **/
  public PathLength(int node1, int node2, DBConnection connection)
  {
    this.lengthMap = new HashMap();
    this.node1 = new Integer(node1);
    this.node2 = new Integer(node2);
    this.connection = connection;
  }
  
  /**
   * Calculates and returns the length between the stored two 
   * points of the graph
   **/
  public int getLength(int curLength)
  {
    System.out.println("getLength("+curLength+") called with lengthMap.size() = "+this.lengthMap.size());

    Integer[] curSet = null;

    // handle first word
    curSet = getNeighbours(node1);
    System.out.println("Got neighbours");
    for ( int i = 0 ; i < curSet.length ; i++ )
    {
      if ( curSet[i].equals(node2) )
      {
        return curLength + 1;
      }
      if ( this.lengthMap.containsKey(curSet[i]) )
      {
        Integer[] values = (Integer[])this.lengthMap.get(curSet[i]);
        if ( values[1] == null ) // my one and smaller or equal value, ignore
        {
          if ( values[0].intValue() > curLength )
          {
            values[0] = new Integer(curLength);
            this.lengthMap.put(curSet[i], values);
          }
        }
        else // HIS ONE! return the stored length plus 1 plus my length
        {
          return 1 + values[0].intValue() + values[1].intValue();
        }
      }
      else // not contained at all, add it with my flag/length
      {
        Integer[] values = new Integer[2];
        values[0] = new Integer(curLength);
        values[1] = null;
        this.lengthMap.put(curSet[i], values);
      }
    }

    System.out.println("Handling second word");
    // handle second word
    curSet = getNeighbours(node2);
    System.out.println("Got his neighbours");
    for ( int i = 0 ; i < curSet.length ; i++ )
    {

      if ( curSet[i].equals(node1) )
      {
        return curLength + 1;
      }
      if ( this.lengthMap.containsKey(curSet[i]) )
      {
        Integer[] values = (Integer[])this.lengthMap.get(curSet[i]);
        if ( values[0] == null ) // my one and smaller or equal value, ignore
        {
          if ( values[1].intValue() > curLength )
          {
            values[1] = new Integer(curLength);
            this.lengthMap.put(curSet[i], values);
          }
        }
        else // HIS ONE! return the stored length plus 1 plus my length
        {
System.out.println("gN 2");
          return 1 + /*values[1].intValue()*/ + values[0].intValue();
        }
      }
      else // not contained at all, add it with my flag/length
      {
        Integer[] values = new Integer[2];
        values[0] = null;
        values[1] = new Integer(curLength);
        this.lengthMap.put(curSet[i], values);
      }
    }

// fuer jedes Element wieder dessen Nachbarn finden...

/* BLOEDSINN!
    System.out.println("Rekursively calling getLength");
    if ( curLength < 5 )
    {
      return getLength(curLength+1);
    }*/
    return 0;
  }
  
  /**
   * Returns an array with the neighbours of the given node or null
   **/
  private Integer[] getNeighbours(Integer node)
  {
    Integer[] resultSet = null;
    String[] args = new String[2];
    args[0] = node.toString();
    args[1] = this.NEIGHBOURS_LIMIT;
    ComparableStringBuffer[][] resultsOfQuery = connection.getResultsOf(this.NEIGHBOURS_QUERY, args);
    if ( resultsOfQuery == null || resultsOfQuery.length < 1 )
    {
      System.out.println("Warning, execution of query ["+this.NEIGHBOURS_QUERY+"] yielded no results!");
      return null;
    }
    resultSet = new Integer[resultsOfQuery.length];

    for ( int i = 0 ; i < resultsOfQuery.length ; i++ )
    {
      resultSet[i] = new Integer(resultsOfQuery[i][0].toString());
    }
    return resultSet;
  }
  
  public static void main(String[] args)
  {
    if ( args.length < 2 )
    {
      System.out.println("Usage: PathLength word1 word2");
      System.exit(0);
    }
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
    PathLength length = new PathLength(new Integer(args[0]).intValue(),new Integer(args[1]).intValue(), connection);
    
    System.out.println("Length : "+length.getLength(0));
    System.out.println("w1 = ["+connection.getWordForNumber(new ComparableStringBuffer(args[0]))+"] w2 = ["+connection.getWordForNumber(new ComparableStringBuffer(args[1]))+"]");
    // Testing here
/*    Integer[] neighbours = length.getNeighbours(new Integer(1000));
    System.out.println("The Neighbours of ["+connection.getWordForNumber(new ComparableStringBuffer("1000"))+"] are:");
    for ( int i = 0 ; i < neighbours.length ; i++ )
    {
      System.out.print(connection.getWordForNumber(new ComparableStringBuffer(neighbours[i].toString()))+" ");
    }*/
  }
}
