package com.bordag.sgz.graphen;

// app specific imports
import com.bordag.sgz.util.*;

// standard imports
import java.util.*;

/**
 * This class represents a regular graph for testing purposes, so don't expect
 * a too clean implementation
 *
 * @author  Stefan Bordag
 * @date    16.04.2002
 */
public class RegularGraph extends RandomGraph
{

  private int maxStep = 0;

  public RegularGraph()
  {
  }

  /**
   * Constructs a graph out of the given arguments
   */
  public RegularGraph(int nodesTotal, int connectionsTotal)
  {
    init();
    Integer curNode = null;
    //Output.println("Creating nodes");
    for ( int i = 0 ; i < nodesTotal ; i++ )
    {
      curNode = new Integer(i);
      this.nodes.put(curNode, new HashSet());
    }
    //Output.println("Creating connections");
    int step = 1;
    int MyConnectionsCount = 0;
    while ( this.connectionsCount < connectionsTotal )
    {
      for ( int i = 0 ; i < nodesTotal-step ; i++ )
      {
        int node1 = i;
        int node2 = i+step;
        addConnection(new Integer(node1), new Integer(node2));
        if ( this.connectionsCount >= connectionsTotal )
        {
          break;
        }
      }
      if ( this.connectionsCount == MyConnectionsCount )
      {
        this.maxStep = step;
        break;
      }
      else
      {
        MyConnectionsCount = this.connectionsCount;
      }
      step++;
      this.maxStep = step;
    }
  }

  /**
   * Must rewrite the getLength to implement simple linear searching to
   * achieve the shortest possible way from A to B
   */
  /**
   *
   **/
  public int getPathLength(int node1,int node2)
  {
    int retVal = 0;
    // implement some intelligent algorithm here, maybe, which tries to jump as
    // near as possible to the target, using the numbers
    // would also allow for adventurous constructions but on random graph it would terribly fail

    // calculate for all nodes connected the distancedifference and take the
    // "nearest" node and restart algorithm using this one
    HashSet set = (HashSet)this.nodes.get(new Integer(node1));
    if ( set.contains(new Integer(node2)) )
    {
      return 1;
    }
    if ( set.size() < 2 )
    {
      return -1;
    }
    int differenceMin = this.nodes.size();
    int differenceNode = 0;
    for ( Iterator it = set.iterator() ; it.hasNext() ; )
    {
      int curNode = ((Integer)it.next()).intValue();
      int curDifference = Math.abs(node2-curNode);
      if ( curDifference < differenceMin )
      {
        differenceNode = curNode;
        differenceMin = curDifference;
      }
    }
    if ( differenceMin >= this.nodes.size() )
    {
      return -1;
    }
    else
    {
//    Output.println("dN = "+differenceNode+" node2 : "+node2+" differenceMin : "+differenceMin);
      if ( differenceMin < this.maxStep )
      {
        return 1;
      }
      return 1+getPathLength(differenceNode,node2);
    }
  }

  private static void experimentEqualRatioRaisingOverall()
  {
    Output.println("EinzKn\tGesKn\tKanten\tClust\t\tlength");
    for ( int i = 25 ; i < 1500 ; i += 25 )
    {
      RegularGraph graph = new RegularGraph(i,i*2);
      //    System.out.println("Dichte = "+graph.calculateConnectionsCoefficient());
      Output.print(graph.getUnconnectedNodes()+"\t");
      Output.print(graph.getNodeCount()+"\t");
      Output.print(graph.getConnectionsCount()+"\t");
      Output.print(graph.calculateCliquishness()+"\t");
      Output.print(graph.calculateMeanPathLength(500)+"");
      Output.println();
    }
  }

  /**
   * testing
   */
  public static void main(String argv[])
  {
    experimentEqualRatioRaisingOverall();
  }

}