package com.bordag.parasyn.util;

import java.util.*;
import com.bordag.parasyn.*;

/**
 * Just overrides the standard HashMap in order to not write over and over the
 * casts
 * @author Stefan Bordag
 * @date 25.10.2003
 */

public class PSWordHashMap extends HashMap implements java.io.Serializable
{

  // this variable tells how many fields in the put method to expect from
  // the values
  protected int fieldSize = 2;

  public PSWordHashMap(int fieldSize)
  {
    super();
    this.fieldSize=fieldSize;
  }

  public void put(PSWord word, double[] values)
  {
    if ( values == null || values.length != this.fieldSize )
    {
      throw new IllegalArgumentException("Wrong usage of PSWordHashMap, values: "+values+" length: "+values.length);
    }
    super.put(word, values);
  }

  public double[] get(PSWord word)
  {
    return (double[])super.get(word);
  }

  public String toStringOrderedBy(int column)
  {
    String retString = "PSWordHashMapOrdered : ";
    PSWordHashMap myMap = new PSWordHashMap(this.fieldSize);
    for ( Iterator it = this.keySet().iterator() ; it.hasNext() ; )
    {
      Object curKey = it.next();
      double[] curVal = (double[])this.get(curKey);
      myMap.put(curKey, curVal);
    }
    while ( myMap.size() > 0 )
    {
      Object curMaxKey = getMin(myMap, column);
      retString = retString + "("+curMaxKey + " " + ((double[])this.get(curMaxKey))[column]+") \n";
      myMap.remove(curMaxKey);
    }
    return retString;
  }

  protected Object getMin(PSWordHashMap myMap,int column)
  {
    double min = Double.MAX_VALUE;
    Object minKey = null;
    for ( Iterator it = myMap.keySet().iterator() ; it.hasNext() ; )
    {
      Object curKey = it.next();
      double[] curVal = ( double[] )myMap.get( curKey );
      if ( curVal[column] < min )
      {
        min = curVal[column];
        minKey = curKey;
      }
    }
    return minKey;
  }

  public String toString()
  {
    String retString = "PSWordHashMap: ";
    for ( Iterator it = this.keySet().iterator() ; it.hasNext() ; )
    {
      Object curKey = it.next();
      double[] curVal = (double[])this.get(curKey);
      retString = retString + " " + curKey + " ";
      for ( int i = 0 ; i < curVal.length ; i++ )
      {
        retString = retString + " " + curVal[i];
      }
    }
    return retString;
  }

}