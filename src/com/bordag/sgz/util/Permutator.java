package com.bordag.sgz.util;

import java.util.*;

/**
 * This class represents all possible permutations without repetition of a
 * given ordered set of items with a given length, default 3
 *
 * @author  Stefan Bordag
 * @date    13.03.2002
 */
public class Permutator
{
  private Object[] items = null;
  private int length = 3;
  private int[] countBuffer = null;
  private boolean hasMore = true;

  private Permutator()
  {
  }

  public Permutator(Object[] items, int length)
  {
    if ( items == null || items.length == 0 || items.length < length )
    {
      throw new IllegalArgumentException("In Permutator.Permutator(Object[],int) incorrect arguments given, please check it!");
    }
    this.items = items;
    this.length = length;
    this.countBuffer = new int[this.length];
    for ( int i = 0 ; i < this.length ; i++ )
    {
      this.countBuffer[i] = i;
    }
  }

  public Permutator(Vector items, int length)
  {
    if ( items == null || items.size() == 0 || items.size() < length )
    {
      throw new IllegalArgumentException("In Permutator.Permutator(HashSet,int) incorrect arguments given, please check it!");
    }
    this.items = new Object[items.size()];
    int j = 0;
    for ( Enumeration enum = items.elements() ; enum.hasMoreElements() ; j++ )
    {
      this.items[j] = enum.nextElement();
    }
    this.length = length;
    this.countBuffer = new int[this.length];
    for ( int i = 0 ; i < this.length ; i++ )
    {
      this.countBuffer[i] = i;
    }
  }

  public Permutator(Set items, int length)
  {
    if ( items == null || items.size() == 0 || items.size() < length )
    {
      throw new IllegalArgumentException("In Permutator.Permutator(HashSet,int) incorrect arguments given, please check it!");
    }
    this.items = new Object[items.size()];
    int j = 0;
    for ( Iterator it = items.iterator() ; it.hasNext() ; j++ )
    {
      this.items[j] = it.next();
    }
    this.length = length;
    this.countBuffer = new int[this.length];
    for ( int i = 0 ; i < this.length ; i++ )
    {
      this.countBuffer[i] = i;
    }
  }

  /**
   * Prints the whole countBuffer register (for testing purposes only)
   */
  private void printCountBuffer(String sth)
  {
    System.out.print(sth+" countBuffer : [ ");
    for ( int i = 0 ; i < this.length ; i++ )
    {
      System.out.print(this.countBuffer[i]+" ");
    }
    System.out.println("]");
  }

  public Vector getNextAsVector()
  {
    if ( this.hasMore )
    {
      Vector retVal = new Vector();
      Object[] temp = getNext();
      for ( int i = 0 ; i < temp.length ; i++ )
      {
        retVal.add(temp[i]);
      }
      return retVal;
    }
    return null;
  }

  /**
   * Calculates the next state of the countBuffer and returns the next
   * permutation
   */
  public Object[] getNext()
  {
    if ( this.hasMore )
    {
      Object[] retVal = new Object[this.countBuffer.length];
      for ( int i = 0 ; i < retVal.length ; i++ )
      {
        retVal[i] = this.items[this.countBuffer[i]];
      }
      advanceState(this.length-1);
      return retVal;
    }
    return null;
  }

  /**
   * If the given place of the countBuffer is over limit, then return true
   */
  private boolean isBufferOverLimit(int place)
  {
    if ( this.countBuffer[place] < (this.items.length - ( ( this.length - 1 ) - place ) ) )
    {
      return false;
    }
    return true;
  }

  /**
   * All countBuffers after the given are dropped to right after this one, in
   * increasing order.
   */
  private void dropAllAfter(int place)
  {
    for ( int i = 1 ; i < (this.countBuffer.length - place) ; i++ )
    {
      this.countBuffer[place+i] = this.countBuffer[place] + i;
    }
  }

  /**
   * Increases a given countBuffer by one
   */
  private void increase(int place)
  {
    this.countBuffer[place]++;
  }

  /**
   * Advances the state of the whole countBuffer register to the next possible
   * recursively
   */
  private void advanceState(int place)
  {
    if ( place < 0 )
    {
      this.hasMore = false;
      return;
    }
    increase(place);
    if ( isBufferOverLimit(place) )
    {
      advanceState(place-1);
    }
    else
    {
      dropAllAfter(place);
    }
  }

  /**
   * Returns whether there are more elements to be retrieved via getNext or not
   */
  public boolean hasMore()
  {
    return this.hasMore;
  }

  /**
   * Main method only for testing
   */
  public static void main(String argv[])
  {

  }
}
