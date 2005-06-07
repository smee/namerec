package com.bordag.sc.spreadingActivation;

import java.util.*;

/**
 * <p>Represents an activation graph along with most important methods neded to
 * work with it.</p>
 * <p>The graph is implemented as a nodelist (hash) where for each node all nodes
 * are stored to which it connects.</p>
 * <p>This is an undirected cyclic graph</p>
 * <p>The spreading activation logic is also implemented here.</p>
 *
 * Note: In the values list of the map there are no GraphNode instances, instead
 * Integer instances just to mark the connections to the nodes which are then
 * identified in the keyset of this graph
 *
 * @todo: Beim propagieren der Energie mitschicken, welche Knoten schon besucht
 * sind, damit Energie nicht zurueckfliesst und sich damit vermehrt.
 *
 * @author Stefan Bordag
 * @date   09.06.2003
 */

public class ActivationGraph
{
  private HashMap nodes = null;
  private HashMap activations = null;

  private static double DAEMMUNGS_FAKTOR = 0.3;
  private static double ERREGUNGS_FAKTOR = 0.5;
  private static double ENERGY_LOSS      = 0.5;
  private static int    MAX_STEPS        = 2;
  private static double DROP_THRESHOLD   = 0.2;

  private static double DOESNT_MATTER = 0.0;

  public ActivationGraph()
  {
    this.nodes = new HashMap();
    this.activations = new HashMap();
  }

  public HashMap getActivations()
  {
    return this.activations;
  }

  /**
   *
   * @param node The node which is to be added to this graph
   * @param neighbours A set of Integer instances representing the neighbours
   * of the given node
   */
  public void addNode(Integer node, Set neighbours)
  {
    if ( ! this.nodes.containsKey(node) )
    {
      this.nodes.put(node, neighbours);

      // make it directed and add all nodes as new if they are not yet present
      for (Iterator it = neighbours.iterator(); it.hasNext(); )
      {
        Integer curNodeNr = (Integer) it.next();
        // add neighbours to activation list, too
        this.activations.put(curNodeNr, new Double(0.0));
        // do the rest of adding
        if ( this.nodes.get(curNodeNr) == null )
        {
          HashSet curSet = new HashSet();
          curSet.add(node);
          this.nodes.put(curNodeNr, curSet);
        }
        else // the node exists already, so we simply add ourself to its neighbors
        {
          // get its neighbours
          HashSet curNeighbours = (HashSet)this.nodes.get(curNodeNr);
          // add ourself to them
          curNeighbours.add(node);
          // write neighbours of node
          this.nodes.put(curNodeNr, curNeighbours);
        }
      }
    }
    else // remove old first, add then new
    {
      this.removeNodeWithoutActivations(node);
      this.addNodeWithoutActivations(node, neighbours);
    }
    if ( ! this.activations.containsKey(node) )
    {
      this.activations.put(node, new Double(0.0));
    }
  }

  /**
   *
   * @param node The node which is to be added to this graph
   * @param neighbours A set of Integer instances representing the neighbours
   * of the given node
   */
  public void addNodeWithoutActivations(Integer node, Set neighbours)
  {
    if ( ! this.nodes.containsKey(node) )
    {
      this.nodes.put(node, neighbours);

      // make it directed and add all nodes as new if they are not yet present
      for (Iterator it = neighbours.iterator(); it.hasNext(); )
      {
        Integer curNodeNr = (Integer) it.next();
        // add neighbours to activation list, too
        if ( ! this.activations.containsKey(curNodeNr) )
        {
          this.activations.put(curNodeNr, new Double(0.0));
        }
        // do the rest of adding
        if ( this.nodes.get(curNodeNr) == null )
        {
          HashSet curSet = new HashSet();
          curSet.add(node);
          this.nodes.put(curNodeNr, curSet);
        }
        else // the node exists already, so we simply add ourself to its neighbors
        {
          // get its neighbours
          HashSet curNeighbours = (HashSet)this.nodes.get(curNodeNr);
          // add ourself to them
          curNeighbours.add(node);
          // write neighbours of node
          this.nodes.put(curNodeNr, curNeighbours);
        }
      }
    }
    if ( ! this.activations.containsKey(node) )
    {
      this.activations.put(node, new Double(0.0));
    }

  }

  /**
   * <p>Removes the given node from the graph, both from the list and from
   * the values of the HashMap</p>
   * @param nodeNr
   */
  public void removeNode(Integer nodeNr)
  {
    // remove the node from graphlist, remove it from all lists of every other node
    if ( this.nodes.get(nodeNr) != null )
    {
      this.nodes.remove(nodeNr);
      for ( Iterator it = this.nodes.keySet().iterator() ; it.hasNext() ; )
      {
        Object curKey = it.next();
        HashSet curSet = (HashSet)this.nodes.get(curKey);
        if ( curSet.contains(nodeNr) )
        {
          curSet.remove(nodeNr);
        }
        this.nodes.put(curKey, curSet);
      }
    }
    // remove node from activation list
    if ( this.activations.containsKey(nodeNr) )
    {
      this.activations.remove(nodeNr);
    }
  }

  /**
   * <p>Removes the given node from the graph, both from the list and from
   * the values of the HashMap</p>
   * @param nodeNr
   */
  public void removeNodeWithoutActivations(Integer nodeNr)
  {
    // remove the node from graphlist, remove it from all lists of every other node
    if ( this.nodes.get(nodeNr) != null )
    {
      this.nodes.remove(nodeNr);
      for ( Iterator it = this.nodes.keySet().iterator() ; it.hasNext() ; )
      {
        Object curKey = it.next();
        HashSet curSet = (HashSet)this.nodes.get(curKey);
        if ( curSet.contains(nodeNr) )
        {
          curSet.remove(nodeNr);
        }
        this.nodes.put(curKey, curSet);
      }
    }
  }

  /**
   * Activates a node according to documented formula and pases a timestep for
   * all other nodes.
   * @param nodeNr
   */
  public void activate(Integer nodeNr, double energy, int step, boolean timeStep)
  {
    if ( step >= this.MAX_STEPS )
    {
      return;
    }

    // decrease activation for all nodes if this activation is a timestep
    // activation
    if ( timeStep )
    {
      for (Iterator it = this.nodes.keySet().iterator(); it.hasNext(); )
      {
        Integer curNode = (Integer) it.next();
        //System.out.println("Trying to fetch activation of "+curNode+" from activation table "+this.activations+" with graph="+this.nodes);

        double curActivation = ((Double)this.activations.get(curNode)).doubleValue();
        double uebrigeActivation = 1.0 - curActivation;
        double newActivation = curActivation * (1.0 - this.DAEMMUNGS_FAKTOR);
        this.activations.put(curNode, new Double(newActivation));

      }
    }

    // activate the actived node
    if ( nodeNr != null )
    {
      double curActivation = ((Double)this.activations.get(nodeNr)).doubleValue();
      double uebrigeActivation = 1.0 - curActivation;
      double newActivation = curActivation + uebrigeActivation * this.ERREGUNGS_FAKTOR * energy;
      this.activations.put(nodeNr, new Double(newActivation));

      HashSet neighbours = (HashSet)this.nodes.get(nodeNr);
      // propagate activation to neighbours
      for ( Iterator it = neighbours.iterator() ; it.hasNext() ; )
      {
        Integer curNodeNr = (Integer)it.next();
        this.activate(curNodeNr, energy*this.ENERGY_LOSS, step+1, false);
      }
      this.nodes.put(nodeNr,neighbours);
    }
  }

  /**
   * Removes little-activated nodes entirely from graph to keep it slim
   */
  public void pruneGraph()
  {
    HashSet toBeDropped = new HashSet();
    for ( Iterator it = this.nodes.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curDropNr = (Integer)it.next();
      if ( ((Double)this.activations.get(curDropNr)).doubleValue() < this.DROP_THRESHOLD )
      {
        toBeDropped.add(curDropNr);
      }
    }
    for ( Iterator it = toBeDropped.iterator() ; it.hasNext() ; )
    {
      Integer curDrop = (Integer)it.next();
      this.removeNode(curDrop);
    }
  }

  public String toString()
  {
    return "ActivationGraph: "+this.nodes+" activations: "+this.activations;
  }

  public static void main(String[] args)
  {
    ActivationGraph graph = new ActivationGraph();
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
    graph.removeNode(new Integer(2));
    graph.activate(new Integer(4), 1.0, 0, true);
    graph.pruneGraph();
    System.out.println(graph.toString());
  }
}
