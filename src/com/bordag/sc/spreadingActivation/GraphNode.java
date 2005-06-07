package com.bordag.sc.spreadingActivation;

/**
 * <p>Title: GraphNode</p>
 * <p>Description: This is just an encapsulation of a node which can have a
 * certain activation.</p>
 * @author Stefan Bordag
 * @date   09.06.2003
 */
public class GraphNode
{
  // stores the wordNumber
  private Integer number = null;

  // stores the activation of this node
  private double activation = 0.0;

  public GraphNode(Integer number, double activation)
  {
    this.number = number;
    this.activation = activation;
  }

  public double getActivation()
  {
    return this.activation;
  }

  public void setActivation(double activation)
  {
    this.activation = activation;
  }

  public Integer getNumber()
  {
    return this.number;
  }

  public void setNumber(Integer number)
  {
    this.number = number;
  }

  /**
   * Overriding hashCode function to ensure it's doing what we want it to do: To
   * recognize whether a given node is already in a hash by simple calling
   * exists on it.
   * @return Integer which represents the wordNumber
   */
  public int hashCode()
  {
    return this.number.intValue();
  }

  /**
   * Overriding hashCode function to ensure it's doing what we want it to do: To
   * recognize whether a given node is already in a hash by simple calling
   * exists on it.
   * @return Boolean whether the object has the same wordNumber
   */
  public boolean equals(Object obj)
  {
    if ( obj instanceof GraphNode )
    {
      if ( ((GraphNode)obj).getNumber().intValue() == this.number.intValue() )
      {
        return true;
      }
    }
    else if ( obj instanceof Integer )
    {
      if ( ((Integer)obj).intValue() == this.number.intValue() )
      {
        return true;
      }
    }
    return false;
  }

  public String toString()
  {
    return "("+this.number.toString()+";"+this.activation+")";
  }
}
