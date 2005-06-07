package com.bordag.sgz.graphen;

// standard imports
import java.util.*;

// app specific imports
import com.bordag.sgz.util.*;

/**
 * Determines the average clustering for words in the word cooccurrence graph
 */
public class Clustering
{

  /**
   * The source of information about the graph
   **/
  private DBConnection connection = null;

  /**
   * Look always at that many neighbours, ordered descending by frequency
   **/
  private static String NEIGHBOURS_LIMIT_1 = "200";

  /**
   * Look always at that many neighbours, ordered descending by frequency
   **/
  private static String NEIGHBOURS_LIMIT_2 = "200";

  /**
   * The query with which to retrieve the neighbours of a word
   **/
  private final static String NEIGHBOURS_QUERY = "select k.wort_nr1 from kollok_sig k where k.wort_nr2 = #ARG# limit #ARG#;";

  /**
   * Constructor
   **/
  public Clustering(DBConnection connection)
  {
    this.connection = connection;
  }

  /**
   * 1. retrieve Neighbours if input word and store in Set
   * 2. Retrieve their neighbours and store in Map (to get a
   *    count how many times the same one has been seen)
   * 3. Look, how often (plus the counts) an element from the set is in the map)
   */
  public double getClustering(Integer word)
  {
    // Step 1
    HashSet neighbours = getNeighbours(word,this.NEIGHBOURS_LIMIT_1);
    if ( neighbours == null )
    {
      return 0.0;
    }
    // Step 2
    double allCounter = 0.0;
    HashMap neighboursOfNeighbours = new HashMap();
    for ( Iterator it = neighbours.iterator() ; it.hasNext() ; )
    {
      Integer curElem = (Integer)it.next();
      HashSet curNeighbours = getNeighbours(curElem, this.NEIGHBOURS_LIMIT_2);
      if ( curNeighbours == null )
      {
        continue;
      }
      allCounter += (double)curNeighbours.size();
      for ( Iterator it2 = curNeighbours.iterator() ; it2.hasNext() ; )
      {
        Integer curElem2 = (Integer)it2.next();
        if ( neighboursOfNeighbours.containsKey(curElem2) )
        {
          Integer count = (Integer)neighboursOfNeighbours.get(curElem2);
          neighboursOfNeighbours.put(curElem2, new Integer(count.intValue()+1));
        }
        else
        {
          neighboursOfNeighbours.put(curElem2, new Integer(1));
        }
      }
    }

    // Step 3
    double counter = 0.0;
    for ( Iterator it = neighbours.iterator() ; it.hasNext() ; )
    {
      Integer curElem = (Integer)it.next();
      if ( neighboursOfNeighbours.containsKey(curElem) )
      {
        counter += (double)((Integer)neighboursOfNeighbours.get(curElem)).intValue();
      }
    }
    System.out.println(word+"\t"+(counter/allCounter)+"\t"+allCounter);
//    System.out.println("Word "+testWord+" has clustering coefficient : "+clu.getClustering(testWord));
    return counter/allCounter;
  }

  /**
   * Returns true, if at least one of the elements in set1 is also in set2
   **/
  private double checkMatching(HashSet set1, HashSet set2)
  {
    double retVal = 0.0;
    for ( Iterator it = set1.iterator() ; it.hasNext() ; )
    {
      Integer curElem = (Integer)it.next();
      if ( set2.contains(curElem) )
      {
        retVal += 1.0;
      }
    }
    return retVal;
  }

  /**
   * Returns an array with the neighbours of the given node or null
   **/
  private HashSet getNeighbours(Integer node, String limit)
  {
    HashSet resultSet = null;
    String[] args = new String[2];
    args[0] = node.toString();
    args[1] = limit;
    ComparableStringBuffer[][] resultsOfQuery = connection.getResultsOf(this.NEIGHBOURS_QUERY, args);
    if ( resultsOfQuery == null || resultsOfQuery.length < 1 )
    {
      System.out.println("Warning, execution of query ["+this.NEIGHBOURS_QUERY+"] with args : ["+args[0]+","+args[1]+"] yielded no results!");
      return null;
    }
    resultSet = new HashSet();

    for ( int i = 0 ; i < resultsOfQuery.length ; i++ )
    {
      resultSet.add( new Integer(resultsOfQuery[i][0].toString()));
    }
    return resultSet;
  }

  /**
   * This tests the functionality of this class
   **/
  public static void main(String[] args)
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
    Integer testWord = new Integer(50000);
    if ( args != null )
    {
      if ( args.length > 0 )
      {
        Clustering.NEIGHBOURS_LIMIT_1 = args[1];
      }
      if ( args.length > 1 )
      {
        Clustering.NEIGHBOURS_LIMIT_2 = args[2];
      }
      if ( args.length > 2 )
      {
        testWord = new Integer(args[0]);
        Clustering clu = new Clustering(connection);
        System.out.println("Word "+testWord+" has clustering coefficient : "+clu.getClustering(testWord));
        System.exit(0);
      }

    }
    Clustering clu = null;
    for ( int i = 1 ; i < 150000 ; i+=100 )
    {
      clu = new Clustering(connection);
      testWord = new Integer(i);
      clu.getClustering(testWord);

      clu = new Clustering(connection);
      testWord = new Integer(i+25);
      clu.getClustering(testWord);

      clu = new Clustering(connection);
      testWord = new Integer(i+50);
      clu.getClustering(testWord);

      clu = new Clustering(connection);
      testWord = new Integer(i+75);
      clu.getClustering(testWord);
    }
  }
}
