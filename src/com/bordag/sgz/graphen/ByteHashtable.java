package com.bordag.sgz.graphen;

import java.math.BigInteger;
import java.util.Random;

/**
 * Simple hashing using linear search. No resize implemented, so choose
 * initial capacity wisely ;-)
 *
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 *
 * FIXME :: Improve hashing strategy
 *
 * FIXME :: When removing, somehow really removing
 */

public class ByteHashtable
{
  protected byte[][] myValues = null;
  protected int size = 0;
  protected byte[] nullValue = null;
  protected byte[] maxValue = null;

  public ByteHashtable(int size)
  {
    this.size = size;
    this.nullValue = new byte[6];
    this.nullValue[0] = 0;
    this.nullValue[1] = 0;
    this.nullValue[2] = 0;
    this.nullValue[3] = 0;
    this.nullValue[4] = 0;
    this.nullValue[5] = 0;
    this.maxValue = new byte[6];
    this.maxValue[0] = Byte.MAX_VALUE;
    this.maxValue[1] = Byte.MAX_VALUE;
    this.maxValue[2] = Byte.MAX_VALUE;
    this.maxValue[3] = Byte.MAX_VALUE;
    this.maxValue[4] = Byte.MAX_VALUE;
    this.maxValue[5] = Byte.MAX_VALUE;
    this.myValues = new byte[size][6];
  }

  public void add(byte[] values)
  {
    int myTry = 0;
    int address = getHashCode(values,myTry);
    while ( !valueEquals(myValues[address],this.nullValue) )
    {
      if ( valueEquals(myValues[address], values) )
      {
        return; // already there
      }
      myTry++;
      address=getHashCode(values,myTry);
      System.out.println("add: One further");
    }
    myValues[address]=values;
  }

  public static void printArray(byte[] array)
  {
    System.out.print("array : [");
    for ( int i = 0 ; i < array.length ; i++ )
    {
      System.out.print(" "+array[i]);
    }
    System.out.println(" ]");
  }

  public void printMyValues()
  {
    for ( int i = 0 ; i < this.myValues.length ; i++ )
    {
      for ( int j = 0 ; j < this.myValues[i].length ; j++ )
      {
        System.out.print(this.myValues[i][j]+" ");
      }
      System.out.println();
    }
  }

  public void remove(byte[] values)
  {
    int myTry = 0;
    int address = getHashCode(values,myTry);
    if ( valueEquals(myValues[address],this.nullValue) )
    {
      return; // not there
    }

    while ( !valueEquals(myValues[address],this.nullValue) )
    {
      boolean wrapped = false;
      if ( valueEquals(myValues[address], values) )
      {
        myValues[address]=this.maxValue;// found it, removing
      }
      myTry++;
      address=getHashCode(values,myTry);
      System.out.println("remove: One further");
    }
    // not there, returning
  }

  public boolean contains(byte[] values)
  {
    int myTry = 0;
    int address = getHashCode(values,myTry);
    while ( !valueEquals(myValues[address],this.nullValue) )
    {
      if ( valueEquals(myValues[address], values) )
      {
        return true; // already there
      }
      myTry++;
      address=getHashCode(values,myTry);
    }
    return false;
  }


//------ my methods

  public int getHashCode(byte[] myValue, int myTry)
  {
    /*
    Random r = new Random(new BigInteger(myValue).intValue());
    int address = Math.abs( r.nextInt() % this.size ) ;
    for ( int i = 0 ; i < myTry ; i++ )
    {
      address = Math.abs( r.nextInt() % this.size );
    }
    return address;
    */
    int address = Math.abs(new BigInteger(myValue).intValue()) % this.size;
    return (address+myTry)%this.size;
    //return Math.abs(new BigInteger(myValue).intValue()) % this.size;
  }

  public boolean valueEquals(byte[] value1, byte[] value2)
  {
    for (int i = 0; i < value2.length; i++)
    {
      if (value1[i] != value2[i])
      {
        return false;
      }
    }
    return true;
  }

}