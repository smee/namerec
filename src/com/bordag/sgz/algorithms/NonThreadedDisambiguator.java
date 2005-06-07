package com.bordag.sgz.algorithms;

// app specific imports
import com.bordag.sgz.util.*;
import com.bordag.sgz.clustering.*;

// standard imports
import java.util.*;

/**
 *  This class represents the disambiguation algorithm.
 *
 * @author  Stefan Bordag
 * @date    10.05.2002
 * @see     com.bordag.sgz.algorithms.Triangles
 */
public class NonThreadedDisambiguator
{
  /**
   * Reference to the DB connection
   */
  protected DBConnection connection = null;

  /**
   * The word to disambiguate
   */
  protected ComparableStringBuffer inputword = null;

  /**
   * The list of words to be ignored
   */
  protected ExcludeList excludeList = null;

  /**
   * Helper class to store words which couldn't be assigned to one of the
   * context vectors
   */
  public ClassifiedWordsList classifiedWordsList = null;

  /**
   * The clusterer which should cluster the vectors
   */
  protected TreeClustering cluster = null;

  /**
   * How many runs to perform at most
   **/
  protected int maxRuns = 1;

  /**
   * This boolean tells us whether a stop of the calculation ist desired.
   */
  protected boolean stop = false;

  protected int curRun = 0;

  /**
   * Standard constructor
   * gets inputword as string which it converts to a number
   */
  public NonThreadedDisambiguator(DBConnection connection, ComparableStringBuffer inputWord)
  {
    this.connection = connection;
    this.inputword = connection.getNumberForWord(inputWord);
    if ( Options.getInstance().getGenUseStopwortFile().equalsIgnoreCase(Options.TRUE) )
    {
      this.excludeList = new ExcludeList(Options.getInstance().getGenStopwortFile());
    }
    else
    {
      this.excludeList = new ExcludeList();
    }
    this.classifiedWordsList = new ClassifiedWordsList();
    this.maxRuns = new Integer(Options.getInstance().getDisMaxRuns()).intValue();
  }

  /**
   * The same constrctor which also specifies how many runs to do
   * gets inputWord as number
   */
  public NonThreadedDisambiguator(DBConnection connection, ComparableStringBuffer inputWord, int maxRuns)
  {
    this.connection = connection;
    this.inputword = inputWord;
    this.excludeList = new ExcludeList(Options.getInstance().getGenStopwortFile());
    this.classifiedWordsList = new ClassifiedWordsList();
    this.maxRuns = maxRuns;
  }

  protected boolean abortCheck()
  {
    if ( stop )
    {
      Output.println("Operation cancelled");
      return true;
    }
    return false;
  }

  public int getCurRun()
  {
    return this.curRun;
  }

  /**
   * Get the first x collocations of input word.
   * remove all words which are in exclude list
   * if nothing left, stop and print out
   *   get their permutations
   *   for each permutation calculate triangles
   *   cluster those
   *   Add all clustered words to exclude list
   *   goto 1
   */
  public void runAlgorithm()
  {
    if ( abortCheck() ) { return; }
    this.cluster = new TreeClustering();
    Vector collocations = getCollocations(this.inputword);
    if ( abortCheck() ) { return; }
    if ( collocations.size() < 4 ) { return; } // min 4 collocations needed for successful operation
    // test
    try
    {

      ((CachedDBConnection)this.connection).cacheCollocationsOf(inputword, collocations);
    }
    catch ( Exception ex )
    {
      Output.println("Exception: "+ex.getMessage());
      Output.println("Warning: You are probably connected to a mysql DB smaller than 4.0, program will continue, but will be slow.");
    }
    // test

    if ( abortCheck() ) { return; }
//--    Output.println("For word "+this.connection.getWordForNumber(this.inputword)+" got "+collocations.size()+" collocations");
    collocations.removeAll(this.excludeList.getItems());
//--    Output.println("After excludeList "+collocations.size()+" collocations left");
    int i = 1;
    while ( collocations.size() > 0 )
    {
      Hashtable trianglesResultSet = getTriangles(this.inputword, collocations, new Integer(Options.getInstance().getDisKollokationenPerStep()).intValue());
//--      Output.println("Adding to clusterer new data:");
      this.cluster.addKeyAndValueAsHash(trianglesResultSet);
      if ( abortCheck() ) { return; }
//--      Output.println("After "+i+". step following classification:");

      if ( i >= maxRuns )
      {
        break;
      }

//--      this.cluster.printClusterVectors(this.connection);
      if ( abortCheck() ) { return; }
//--      Output.println("Before applying exclude list "+collocations.size()+" items in collocations.");
      this.excludeList = addAllWordsFromClusterToExcludeList(this.excludeList, cluster.getClusterVectorsAsSet());
      collocations.removeAll(this.excludeList.getItems());
//--      Output.println("After applying exclude list containig "+this.excludeList.getItems().size()+" elements "+collocations.size()+" items left");

//--      Output.print("Unused words in this run are : ");
//--      this.classifiedWordsList.printUnclusteredWords();
//--      Output.println();

      i++;
      this.curRun = i;
      if ( abortCheck() ) { return; }
    }
  }

  /**
   * Retrieved the collocations of a given word(number)
   */
  protected Vector getCollocations(ComparableStringBuffer word)
  {
    Vector retSet = new Vector();
    String[] s = new String[4];
    s[0] = Options.getInstance().getDisMinSignifikanz();
    s[1] = Options.getInstance().getDisMinWordNr();
    s[2] = this.inputword.toString();
    s[3] = Options.getInstance().getDisMaxKollokationen();
    String query = Options.getInstance().getDisQueryKollokationen();
    ComparableStringBuffer[][] buffer = this.connection.getResultsOf(query,s);
    if ( buffer == null || buffer.length < 1 )
    {
      return retSet;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        retSet.add(buffer[i][0]);
      }
    }
    return retSet;
  }

  /**
   * Calculates triangles building combinations taking maximum given amount of
   * collocations from the set and removing them from there
   */
  protected Hashtable getTriangles(ComparableStringBuffer word1, Vector collocations, int howMany)
  {
    Hashtable retVal = new Hashtable();
    Vector currentCollocations = new Vector();
    for ( int i = 0 ; i < Math.min(howMany, collocations.size()); i++ )
    {
      currentCollocations.add(collocations.elementAt(0));
      collocations.remove(0);
    }
    collocations.removeAll(currentCollocations);
    try
    {
      if ( collocations.size() < 3 )
      {
        return retVal;
      }
      Permutator perm = new Permutator(currentCollocations, 2);
      int i = 0;
      while ( perm.hasMore() )
      {
        Vector tripel = new Vector();
        Object[] buffer = perm.getNext();
        tripel.add(word1);
        tripel.add((ComparableStringBuffer)buffer[0]);
        tripel.add((ComparableStringBuffer)buffer[1]);
        this.classifiedWordsList.addUsedWords(tripel);

        NonThreadTriangles triangle = new NonThreadTriangles(this.connection, tripel);
        HashSet value = triangle.getResults();

        i++;
        if ( value == null )
        {
//--          Output.print(".");
          continue;
        }
        value.remove(this.inputword);
        value.remove((ComparableStringBuffer)buffer[0]);
        value.remove((ComparableStringBuffer)buffer[1]);
        if ( value.size() > 0 )
        {
          this.classifiedWordsList.addClusteredWords(value);
          this.classifiedWordsList.addClusteredWords(tripel);
          retVal.put(tripel.toArray(),value.toArray());
          if ( value.size() > 4 )
          {
//--            Output.print("*");
          }
          else
          {
//--            Output.print("+");
          }
        }
        else
        {
//--          Output.print("-");
        }
      }
//--      Output.println();
      return retVal;
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      return retVal;
    }
  }

  /**
   * Retrieves all possible wordnumbers from the collocationsvector and adds
   * the to the exclude list
   */
  protected ExcludeList addAllWordsFromClusterToExcludeList(ExcludeList excludeList, HashSet clusterVectors)
  {
    for ( Iterator it = clusterVectors.iterator() ; it.hasNext() ; )
    {
      ClusterVector cv = (ClusterVector)it.next();
      excludeList.addItems(cv.getKey());
      excludeList.addItems(cv.getValue());
    }
    return excludeList;
  }

  /**
   * This list stores used words and subtracts classifieds to show which don't
   * bring any information to clustering
   */
  public class ClassifiedWordsList
  {
    HashSet wordSet = null;
    HashSet clusteredSet = null;

    public ClassifiedWordsList()
    {
      this.wordSet = new HashSet();
      this.clusteredSet = new HashSet();
    }

    public void addUsedWords(Collection words)
    {
      this.wordSet.addAll(words);
    }

    public void addClusteredWords(Collection words)
    {
      this.clusteredSet.addAll(words);
    }

    public void printUnclusteredWords()
    {
      HashSet tempSet = (HashSet)this.wordSet.clone();
      tempSet.removeAll(this.clusteredSet);
      System.out.println(connection.getWordsForNumbers(tempSet));
/*      for ( Iterator it = tempSet.iterator() ; it.hasNext() ; )
      {
        ComparableStringBuffer wordNumber = (ComparableStringBuffer)it.next();
        Output.print(connection.getWordForNumber(wordNumber)+" ");
      }*/
    }

    public HashSet getUnclusteredWords()
    {
      HashSet tempSet = (HashSet)this.wordSet.clone();
      tempSet.removeAll(this.clusteredSet);
      return tempSet;
    }
  }

  public TreeClustering getCluster()
  {
    return this.cluster;
  }

  public DBConnection getConnection()
  {
    return this.connection;
  }

  /**
   * Stops the calculation as soon as possible
   */
  public void dispose()
  {
    this.stop = true;
  }
}
