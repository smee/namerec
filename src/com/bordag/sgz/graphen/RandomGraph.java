package com.bordag.sgz.graphen;

// app specific imports
import com.bordag.sgz.util.*;

// standard imports
import java.util.*;

/**
 * This class represents a random undirected and unweighted graph
 *
 * The nodes are simply Integer objects and the connections are implemented
 * as a list of connected Integer for each Integer. (implemented as a
 * HashSet for improved searching)
 *
 * @author  Stefan Bordag
 * @date    16.04.2002
 */
public class RandomGraph
{
  /**
   * Stores the nodes in a Hashtable with the wordnumbers as keys and a HashSet
   * of connected wordnumbers as value.
   */
  protected Hashtable nodes = null;

  /**
   * As connections are created this variable just counts them
   **/
  protected int connectionsCount = 0;

  /**
   * Constructor for an empty graph
   */
  public RandomGraph()
  {
    init();
  }

  /**
   * Constructs a graph out of the given arguments
   */
  public RandomGraph(int nodesTotal, int connectionsTotal)
  {
    init();
    Integer curNode = null;
    for ( int i = 0 ; i < nodesTotal ; i++ )
    {
      curNode = new Integer(i);
      this.nodes.put(curNode, new HashSet());
    }
    Random rand = new Random();
    int node1 = 0;
    int node2 = 0;
    // add nodes up to the desired amount or up to the maximum
    for ( int i = 0 ; i < Math.min ( ((nodesTotal*(nodesTotal-1)) - this.connectionsCount), connectionsTotal) ; i++ )
    {
      boolean added = false;
      while ( !added )
      {
        node1 = rand.nextInt(nodesTotal);
        node2 = rand.nextInt(nodesTotal);
        added = addConnection(new Integer(node1), new Integer(node2));
      }
    }
  }

  /**
   * Constructs a graph out of the given words arguments
   * It wil lcreate the specified amount of nodes and try to
   * create as many connections as to filfill the ratio best with
   * the given variance
   */
  public RandomGraph(int nodesTotal, double connectionsRatio, double variance)
  {
  Output.println("Constructor not yet implemented");
    init();
  }

  /**
   * Necessary default initializations
   */
  protected void init()
  {
    this.nodes = new Hashtable();
  }

  /**
   * Adds if not yet present both nodes to the hashtable and builds connections
   * between them (in the hashsets)
   */
  protected boolean addConnection(Object node1, Object node2)
  {
    boolean added = false;
    if ( node1.equals(node2) )
    {
      return false;
    }
    if ( ! this.nodes.containsKey(node1) )
    {
      this.nodes.put(node1, new HashSet());
    }
    if ( ! this.nodes.containsKey(node2) )
    {
      this.nodes.put(node2, new HashSet());
    }
    HashSet set = (HashSet)this.nodes.get(node1);
    if ( ! set.contains(node2) )
    {
      set.add(node2);
      added = true;
    }
    this.nodes.put(node1, set);

    HashSet set2 = (HashSet)this.nodes.get(node2);
    if ( ! set2.contains(node1) )
    {
      set2.add(node1);
      added = true;
    }
    this.nodes.put(node2, set2);
    if ( added )
    {
      this.connectionsCount++;
      return true;
    }
    return false;
  }

  /**
   * Counts how many triangles there are in this graph.
   * For each node,
   *   it iterates through each neighbour
   *   and checks whether for this neighbour any of it's neighbours is
   *   a neighbour of the original node as well.
   * @return The triangles count.
   */
  public int countTriangles()
  {
    int triangles = 0;
    for ( Enumeration enum = this.nodes.keys() ; enum.hasMoreElements() ; )
    {
      Object curX = enum.nextElement();
      HashSet curXEdges = (HashSet)this.nodes.get(curX);
      for ( Iterator it = curXEdges.iterator() ; it.hasNext() ; )
      {
        Object curY = it.next();
        for ( Iterator itY = ((HashSet)this.nodes.get(curY)).iterator() ; itY.hasNext() ; )
        {
          Object curZ = itY.next();
          if ( curXEdges.contains(curZ) )
          {
            triangles++;
          }
        }
      }
    }
    return triangles/6;
  }

  /**
   * Calculates by how much the
   */
  public double calculateConnectionsCoefficient()
  {
    double N = (double)this.nodes.size();
    double possibleConnectionsPerNode = N - 1.0;
    double meanValue = 0.0;
    for ( Enumeration enum = this.nodes.keys() ; enum.hasMoreElements() ; )
    {
      HashSet currentSet = (HashSet)this.nodes.get((Object)enum.nextElement());
      meanValue += (double)currentSet.size() / possibleConnectionsPerNode;
    }
    meanValue = meanValue / N;
    return meanValue;
  }

  /**
   * Binomialkoeffizient n ueber k
   *       n!
   * -------------
   * k! ( n - k )!
   */
  public static double calculateBinomialCoefficient(double n, double k)
  {
    return calculateFaculty(n) / ( calculateFaculty(k) * calculateFaculty( n - k ) );
  }

  /**
   * x * ( x - 1 ) * ... * 1
   */
  public static double calculateFaculty(double x)
  {
    if ( x <= 0 )
    {
      return 0;
    }
    if ( x == 1 )
    {
      return 1;
    }
    return (x * calculateFaculty(x-1));
  }

  /**
   * For each node a
   *   if this node knows a set of other nodes B, then for each node b there
   *   if b knows c which is not b or a then
   *   if a knows c
   **/
  public double calculateCliquishness()
  {
    double retVal = 0.0;
    double count = 0.0;
    for ( Enumeration enum = this.nodes.keys() ; enum.hasMoreElements() ; )
    {
      Integer curObjectA = (Integer)enum.nextElement();
      HashSet nodesB = (HashSet)this.nodes.get(curObjectA);
      for ( Iterator it = nodesB.iterator() ; it.hasNext() ; )
      {
        Integer curObjectB = (Integer)it.next();
        if ( curObjectB.equals(curObjectA) )
        {
          continue;
        }
        HashSet nodesC = (HashSet)this.nodes.get(curObjectB);
        for ( Iterator it2 = nodesC.iterator() ; it2.hasNext() ; )
        {
	        Integer curObjectC = (Integer)it2.next();
          if ( curObjectC.equals(curObjectA) || curObjectC.equals(curObjectB) )
          {
	          continue;
          }
          HashSet nodesD = (HashSet)this.nodes.get(curObjectB);
          count += 1.0;
          for ( Iterator it3 = nodesD.iterator() ; it3.hasNext() ; )
          {
            Integer curObject = (Integer)it3.next();
            if ( curObject.equals(curObjectA) || curObject.equals(curObjectB) )
            {
              continue;
            }
            if ( nodesB.contains(curObject) )
            {
              retVal += 1.0;
            }
          }
        }
      }
    }
    if ( count < 1.0 )
    {
      return 0.0;
    }
    double ratio = retVal/count;
    ratio = ratio / ((double) this.nodes.size() - 2.0);
    return ratio;
  }

  /**
   * Calculates the mean leangth of the path to get from any point A to
   * any other point B for given amount of tries
   **/
  public double calculateMeanPathLength(int tries)
  {
    double retVal = 0.0;
    Random rand = new Random();
    int node1 = 0;
    int node2 = 0;
    int counter = 0;
    for ( int i = 0 ; i < tries ; i++ )
    {
      node1 = rand.nextInt(this.nodes.size()-2)+1;
      node2 = rand.nextInt(this.nodes.size()-2)+1;
      double curValue = 0.0;
      try
      {
        curValue = getPathLength(node1, node2);
      }
      catch ( Exception ex )
      {
        Output.println("node1 = ["+node1+"] node2 = ["+node2+"]");
      }
      if ( curValue > 0 )
      {
        retVal += curValue;
        counter++;
      }
    }
    return retVal/(double)counter;
  }

  /**
   *
   **/
  public int getPathLength(int node1,int node2)
  {
    int retVal = 0;
    for ( int i = 0 ; i < 6 ; i++ )
    {
      if ( getSetOfOrder(node1, i).contains(new Integer(node2)) )
      {
        return i+1;
      }
    }
    return -1;
  }

  /**
   * Traversing the graph from one point gives larger and larger sets, each
   * step is a higher order
   */
  private HashSet getSetOfOrder(int node, int order)
  {
    if ( order == 0 )
    {
      return (HashSet)this.nodes.get(new Integer(node));
    }
    else
    {
      Integer myNode = new Integer(node);
      HashSet curSet = (HashSet)this.nodes.get(myNode);
      HashSet allSet = new HashSet();
      for ( Iterator it = curSet.iterator() ; it.hasNext() ; )
      {
        int curNode = ((Integer)it.next()).intValue();
        allSet.addAll(getSetOfOrder(curNode, order-1));
      }
      return allSet;
    }
  }

  /**
   * Returns how many nodes this graph has
   **/
  public int getNodeCount()
  {
    return this.nodes.size();
  }

  /**
   * Returns how many connections this graph has
   **/
  public int getConnectionsCount()
  {
    return this.connectionsCount;
  }

  /**
   * Counts how many nodes are stand-alone
   **/
  public int getUnconnectedNodes()
  {
    int retVal = 0;
    for ( Enumeration enum = this.nodes.keys() ; enum.hasMoreElements() ; )
    {
      Integer key = (Integer)enum.nextElement();
      HashSet set = (HashSet)this.nodes.get(key);
      if ( set.size() == 0 )
      {
        retVal++;
      }
    }
    return retVal;
  }

  private static void experimentEqualRatioRaisingOverall()
  {
    Output.println("EinzKn\tGesKn\tKanten\tClust\t\tlength");
    for ( int i = 25 ; i < 1500 ; i += 25 )
    {
      RandomGraph graph = new RandomGraph(i,i*2);
      //    System.out.println("Dichte = "+graph.calculateConnectionsCoefficient());
      Output.print(graph.getUnconnectedNodes()+"\t");
      Output.print(graph.getNodeCount()+"\t");
      Output.print(graph.getConnectionsCount()+"\t");
      Output.print(new Double(graph.calculateCliquishness()).toString()+"\t");
      Output.print(graph.calculateMeanPathLength(50)+"");
      Output.println();
    }
  }

  private static void experimentRaisingConnections()
  {
    Output.println("EinzKn\tGesKn\tTri\tKanten\tClust\t\tlength");
    for ( int i = 1 ; i < 6000 ; i += 1 )
    {
      RandomGraph graph = new RandomGraph(100,i*16);
      //    System.out.println("Dichte = "+graph.calculateConnectionsCoefficient());
      Output.print(graph.getUnconnectedNodes()+"\t");
      Output.print(graph.getNodeCount()+"\t");
      Output.print(graph.countTriangles()+"\t");
      Output.print(graph.getConnectionsCount()+"\t");
      //Output.print(new Double(graph.calculateCliquishness()).toString()+"\t");
      Output.print("0.000000000000000000\t");
      Output.print(graph.calculateMeanPathLength(50)+"");
      Output.println();
    }
  }

  /**
   * testing
   */
  public static void main(String argv[])
  {
    //experimentEqualRatioRaisingOverall();
    experimentRaisingConnections();
  }
}
