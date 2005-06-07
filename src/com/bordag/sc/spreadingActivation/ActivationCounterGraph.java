package com.bordag.sc.spreadingActivation;

import java.util.*;

/**
 * This is a simplified 'network' as it doesn't store the given network
 * structure, it just counts how many times a certain node has been seen.
 * Structure of HashMap: NodeNr -> count (double)
 *
 * @author Stefan Bordag
 * @date   16.08.2003
 */

public class ActivationCounterGraph
{
  private HashMap nodes = null;
  private int wordCounter = 0;

  public ActivationCounterGraph()
  {
    this.nodes = new HashMap();
  }

  public HashMap getActivations()
  {
    return this.nodes;
  }

  public int getInputWords()
  {
    return this.wordCounter;
  }

  /**
   *
   * @param node The node which is to be added to this graph
   * @param neighbours A set of Integer instances representing the neighbours
   * of the given node
   */
  public void addNode(Integer node, Set neighbours)
  {
    this.wordCounter++;
    neighbours.add(node);
    for ( Iterator it = neighbours.iterator() ; it.hasNext() ; )
    {
      Integer curNodeNr = (Integer)it.next();
      if ( !this.nodes.containsKey(curNodeNr) )
      {
//        System.out.println("Adding with 1");
//        System.out.println("before("+curNodeNr+")="+this.nodes.get(curNodeNr));
        this.nodes.put(curNodeNr, new Double(1.0));
//        System.out.println("after("+curNodeNr+")="+this.nodes.get(curNodeNr));
      }
      else
      {
//        System.out.println("Adding with +1");
//        System.out.println("before("+curNodeNr+")="+this.nodes.get(curNodeNr));
        this.nodes.put(curNodeNr, new Double(((Double)this.nodes.get(curNodeNr)).doubleValue()+1.0));
//        System.out.println("after("+curNodeNr+")="+this.nodes.get(curNodeNr));
      }
    }
  }

  /**
   * <p>Removes the given node from the graph, both from the list and from
   * the values of the HashMap</p>
   * @param nodeNr
   */
  public void removeNode(Integer nodeNr)
  {
    if ( this.nodes.containsKey(nodeNr) )
    {
      this.nodes.remove(nodeNr);
    }
  }

  /**
   * Activates a node according to documented formula and pases a timestep for
   * all other nodes.
   * @param nodeNr
   */
  public void activate(Integer nodeNr, double energy, int step, boolean timeStep){}

  /**
   * Removes little-activated nodes entirely from graph to keep it slim
   */
  public void pruneGraph(){}

  public String toString()
  {
    return "ActivationGraph: "+this.nodes+" ";
  }

  public static void main(String[] args)
  {
    ActivationCounterGraph graph = new ActivationCounterGraph();
    HashSet set1 = new HashSet();
    set1.add(new Integer(2));
    set1.add(new Integer(3));
    set1.add(new Integer(4));
    set1.add(new Integer(5));
    graph.addNode(new Integer(1), set1);
    HashSet set2 = new HashSet();
    set2.add(new Integer(1));
    set2.add(new Integer(3));
    set2.add(new Integer(4));
    set2.add(new Integer(5));
    graph.addNode(new Integer(2), set2);
    System.out.println(graph.toString());
  }
}
