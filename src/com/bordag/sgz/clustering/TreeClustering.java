package com.bordag.sgz.clustering;

// app specific imports
import com.bordag.sgz.util.*;
import com.bordag.sgz.graphen.*;

// standard imports
import java.util.*;

/**
 * An instance of this class can receive at any time 'any' number of new
 * ClusterVectors which it clusters whith what was received before.
 *
 * Clustering is hierarchical which looks like a tree, then. It will hold a
 * reference to any received vector as long as it has not been clustered
 * together with some other vector, after which the two are merged together
 * and only the new vector will be kept
 *
 * Also it can at any print which vectors are left after the last clustering.
 *
 * @author  Stefan Bordag
 * @date    10.04.2002
 */
public class TreeClustering implements Cloneable
{

  /**
   * This is the set (in this case a resiseable array) which contains all
   * vectors which do not cluster together
   */
  protected HashSet vectors = null;

  /**
   * The threshold which must be smaller then the similarity between two
   * compared vectors in order for them to be clustered together
   */
  protected double threshold = 30.0;

  /**
   * Just creates an instance, everything else is done via the addVector method
   * and the like
   */
  public TreeClustering()
  {
    this.vectors = new HashSet();
    this.threshold = new Double(Options.getInstance().getDisClusterThreshold()).doubleValue();
  }

  /**
   * Assumes that the hashtable contains both as hashkeys and values Object
   * arrays (ClassCastException is thrown automatically if not) and
   */
  public void addKeyAndValueAsHash(Hashtable table)
  {
    for ( Enumeration enum = table.keys() ; enum.hasMoreElements() ; )
    {
      Object[] key = (Object[])enum.nextElement();
      Object[] values = (Object[])table.get(key);
      ClusterVector v = new ClusterVector(key, values);
      addClusterVector(v);
    }
  }

  /**
   * Adds a set of new vectors to cluster
   */
  public void addClusterVectorSet(HashSet vectors)
  {
    for ( Iterator it = vectors.iterator() ; it.hasNext() ; )
    {
      addClusterVector((ClusterVector)it.next());
    }
  }

  /**
   * Adds a set of new vectors to cluster
   */
  public void addClusterVectorSet(HashSet vectors, ComparableStringBuffer wordNr)
  {
    for ( Iterator it = vectors.iterator() ; it.hasNext() ; )
    {
      addClusterVector((ClusterVector)it.next(), wordNr);
    }
  }

  /**
   * Adds an array of new vectors to cluster
   */
  public void addClusterVectorArray(ClusterVector[] vectors)
  {
    if ( vectors == null || vectors.length < 1 )
    {
      throw new IllegalArgumentException("TreeClustering.addClusterVectors(ClusterVector[]) illegal argument!");
    }
    for ( int i = 0 ; i < vectors.length ; i++ )
    {
      addClusterVector(vectors[i]);
    }
  }

  /**
   * Receives a new vector to cluster. Immediately tries to cluster it or just
   * adds it to the list of vectors if clustering fails due to too low
   * similarity to any saved item.
   */
  public void addClusterVector(ClusterVector vector)
  {
    // finds out, which of the existing vectors fit best to the new one
    ClusterVector bestFoundVector = getBestFittingVector(vector);
    // if there isn't such a fitting vector then the new one can't currently be
    // clustered, so we simply add it to the list.
    if ( bestFoundVector == null )
    {
      this.vectors.add(vector);
      //recluster();
      return;
    }
    //   (keep always reference to the last best bet)
    // merges the given and the found one
    ClusterVector newVector = bestFoundVector.mergeWithVector(vector);
    // adds the merged to the set
    this.vectors.add(newVector);
    // removes the one from the list which was used to merge
    this.vectors.remove(bestFoundVector);
    recluster();
    return;
  }

  /**
   * Receives a new vector to cluster. Immediately tries to cluster it or just
   * adds it to the list of vectors if clustering fails due to too low
   * similarity to any saved item.
   */
  public void addClusterVector(ClusterVector vector, ComparableStringBuffer wordNr)
  {
    // finds out, which of the existing vectors fit best to the new one
    ClusterVector bestFoundVector = getBestFittingVector(vector);
    // if there isn't such a fitting vector then the new one can't currently be
    // clustered, so we simply add it to the list.

    vector.addSeenWith(wordNr);

    if ( bestFoundVector == null )
    {
      this.vectors.add(vector);
      //recluster();
      return;
    }
    //   (keep always reference to the last best bet)
    // merges the given and the found one
    ClusterVector newVector = bestFoundVector.mergeWithVector(vector);
    // adds the merged to the set
    this.vectors.add(newVector);
    // removes the one from the list which was used to merge
    this.vectors.remove(bestFoundVector);
    recluster();
    return;
  }

  /**
   * Returns the array of cluster vectors which are left
   */
  public ClusterVector[] getClusterVectorsAsArray()
  {
    Vector v = new Vector();
    for ( Iterator it = this.vectors.iterator() ; it.hasNext() ; )
    {
      v.add(it.next());
    }
    Collections.sort(v, new ClusterVectorComparator());
    Object[] obj = v.toArray();
    ClusterVector[] retVal = new ClusterVector[obj.length];
    for ( int i = 0 ; i < retVal.length ; i++ )
    {
      retVal[i] = (ClusterVector)obj[i];
    }
    return retVal;
  }

  /**
   * Returns the vectors set directly
   */
  public HashSet getClusterVectorsAsSet()
  {
    return this.vectors;
  }

  /**
   * Prints what's left
   */
  public void printClusterVectors()
  {
    for ( Iterator it = this.vectors.iterator() ; it.hasNext() ; )
    {
      Output.println(it.next().toString());
    }
  }

  /**
   * Prints what's left using the connection to resolve wordnumbers
   */
  public void printClusterVectors(DBConnection connection)
  {
    Vector v = new Vector();
    for ( Iterator it = this.vectors.iterator() ; it.hasNext() ; )
    {
      v.add(it.next());
    }
    Collections.sort(v, new ClusterVectorComparator());
    for ( Enumeration enum = v.elements() ; enum.hasMoreElements() ; )
    {
      ClusterVector cv = (ClusterVector)enum.nextElement();
      //SubGraph graph = new SubGraph(connection,new Vector(Arrays.asList( cv.getValue() )));
      //Output.println("c = "+graph.calculateCliquishness());
      //Output.println("d = "+graph.calculateConnectionsCoefficient());

      Output.println(cv.toString(connection));
    }
  }


  /**
   * compares tthe two given vectors by how much they fit together and
   * returns a value
   * You can override this method and fit in your own measurement, but keep in
   * mind that the higher the value, the more similar are the vectors
   */
  protected double calcSimilarityOf(ClusterVector v1, ClusterVector v2)
  {
    // find out, which is smaller
    ClusterVector smaller = v1;
    ClusterVector bigger = v2;
    if ( v1.getValue().length > v2.getValue().length )
    {
      smaller = v2;
      bigger = v1;
    }
    // count matching elements
    Object[] o = ClusterVector.mergeArrays(v1.getValue(), v2.getValue());
    double numOfAllDifferent = (double)o.length; // gives number of all different words
    double numOfSmaller = (double)smaller.getValue().length;
    double numOfBigger = (double)bigger.getValue().length;
    double matchingElements = numOfBigger + numOfSmaller - numOfAllDifferent;
    double s = 0.0;
    s = ( matchingElements / numOfSmaller ) * 100.0;
    //      s = ( matchingElements / numOfAllDifferent ) * 100.0;
    return s;
  }

  /**
   * Returns the vector which is the most similar to the given one or null if no
   * of the similarities is high enough to pass threshold
   */
  private ClusterVector getBestFittingVector(ClusterVector vector)
  {
    ClusterVector bestFoundVector = null;
    double bestFoundValue = 0.0;
    for ( Iterator it = this.vectors.iterator() ; it.hasNext() ; )
    {
      ClusterVector current = (ClusterVector)it.next();

      double currentSimilarity = calcSimilarityOf(vector, current);
      if ( bestFoundValue < currentSimilarity && currentSimilarity >= this.threshold )
      {
        bestFoundValue = currentSimilarity;
        bestFoundVector = current;
      }
    }
    return bestFoundVector;
  }

  /**
   * creates a new instance of TreeClustering and adds all vectors ( thus
   * reclustering them recursively ) then retrieves the size of the the
   * references set, if it's same as ours, we drop that new thing,
   * else we overwrite our vectors with the new one's
   */
  private void recluster()
  {
    TreeClustering clusterer = new TreeClustering();
    clusterer.addClusterVectorSet(this.vectors);
    if ( clusterer.getClusterVectorsAsSet().size() != this.vectors.size() )
    {
      this.vectors = clusterer.getClusterVectorsAsSet();
    }
  }

  private class ClusterVectorComparator implements Comparator
  {
    /**
     * To be able to print the results ordered, this gives the ordering, by which
     * it is ordered
     */
    public int compare(Object o, Object o2)
    {
      if ( o instanceof ClusterVector && o2 instanceof ClusterVector)
      {
        return ((ClusterVector)o).getValue().length - ((ClusterVector)o2).getValue().length;
      }
      return o.hashCode() - o2.hashCode();
    }
  }

  public TreeClustering getClone()
  {
    synchronized ( this.getClass() )
    {
      try
      {
        return (TreeClustering)this.clone();
      }
      catch ( Exception ex )
      {
        ex.printStackTrace();
      }
    }
    return null;
  }

}
