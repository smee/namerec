package com.bordag.ksim;

import java.util.*;

/**
 * This class can be used to save information like the 1000th something
 * has a property or hasn't. In this case it's used for wordnumbers which
 * either are already visited or not yet.
 *
 * Counting starts with 1 here, which means setPosition(1,true) will set
 * the very first bit to a one
 *
 * @author Stefan Bordag
 * @date   5.11.2003
 */
final public class BitField implements java.io.Serializable
{
  /**
   * The fixed size of this BitField (usage over this size results in
   * IllegalArgumentException)
   */
  private int size = 0;

  /**
   * The bitfield itself, implemented simply as an array of bytes
   */
  private byte[] field = null;

  /**
   * A BitField without size doesn't make sense, so making it private
   */
  private BitField(){}

  /**
   * Constructs this bitfield. If size is too small, it defaults to one byte
   * @param size
   */
  public BitField(int size)
  {
    this.size = size;
    int realSize = size;
    if ( realSize < 8 )
    {
      realSize = 1;
    }
    else
    {
      realSize = (int)Math.ceil ( (double)realSize / 8);
    }
    this.field = new byte[realSize];
  }

  /**
   * Returns the value of the specified index.
   * @param position
   * @return
   */
  public boolean getPosition(int position)
  {
    position -= 1;
    if ( position < 0 || position > this.size )
    {
      throw new IllegalArgumentException(" in BitField.setPosition(int, boolean) Size of BitField is "+size+" and requested position is "+position);
    }
    int realPos = (int)Math.floor( (double)position/8 );
    byte subPos = (byte)(position % 8);
    byte realSubPos = (byte)Math.pow(2,subPos);
    if ( (this.field[realPos] & realSubPos) == realSubPos )
    {
      return true;
    }
    return false;
  }

  /**
   * Gets an array of positions to check and returns an array of values.
   * @param positions
   * @return
   */
  public boolean[] getPositions(int[] positions)
  {
    if ( positions == null || positions.length == 0 )
    {
      return new boolean[0];
    }
    boolean[] retArr = new boolean[positions.length];
    for ( int i = 0 ; i < retArr.length ; i++ )
    {
      if ( positions[i] < 0 || positions[i] > this.size )
      {
        throw new IllegalArgumentException(" in BitField.getPositions(int[]) with size "+this.size+" tried to obtain value of "+positions[i]);
      }
      retArr[i] = getPosition(positions[i]);
    }
    return retArr;
  }

  /**
   * Sets the value at the specified index to the specified value
   * @param position
   * @param value
   */
  public void setPosition(int position, boolean value)
  {
    position -= 1;
    if ( position < 0 || position > this.size )
    {
      throw new IllegalArgumentException(" in BitField.setPosition(int, boolean) Size of BitField is "+size+" and requested position is "+position);
    }
    int realPos = (int)Math.floor( (double)position/8 );
    byte subPos = (byte)(position % 8);
    byte realSubPos = (byte)Math.pow(2,subPos);
    if ( value )
    {
      this.field[realPos] = (byte)(this.field[realPos]|realSubPos);
    }
    else if ( (this.field[realPos] & realSubPos) == realSubPos )
    {
      this.field[realPos] =  (byte)(this.field[realPos] - realSubPos);
    }
  }

  /**
   * Sets the values at the specified indices to the specified values
   * @param positions
   * @param values
   */
  public void setPositions(int[] positions, boolean[] values)
  {
    if ( positions == null || positions.length == 0 )
    {
      return ;
    }
    for ( int i = 0 ; i < positions.length ; i++ )
    {
      if ( positions[i] < 0 || positions[i] > this.size )
      {
        throw new IllegalArgumentException(" in BitField.setPositions(int[], boolean[]) with size "+this.size+" tried to obtain value of "+positions[i]);
      }
      setPosition(positions[i], values[i]);
    }
    return;
  }

  /**
   * Returns the number of bits this field contains
   * @return
   */
  public int size()
  {
    return this.field.length*8;
  }

/*  public void setPositionsFalse(int[] positions)
  {

  }

  public void setPositionsTrue(int[] positions)
  {

  }*/

  /**
   * Printout for debugging purposes, don't use it on large bitfields!
   * @return
   */
  public String toString()
  {
    String retString = "BitField: ";
    for ( int i = 0 ; i < this.field.length ; i++ )
    {
      byte curByte = this.field[i];
      for ( int j = 0 ; j < 8 ; j++ )
      {
        byte curPos = (byte)Math.pow(2, j);
        if ( (curByte & curPos) == curPos )
        {
          retString = retString+"1";
        }
        else
        {
          retString = retString+"0";
        }
      }
    }
    return retString;
  }

  /**
   * Use this to test this class for bugs
   * @param args
   */
  public static void main(String[] args)
  {
    BitField f = new BitField(100000000);
    for ( int i = 1 ; i <= 100; i++ )
    {
      f.setPosition(i,true);
      //System.out.println(" "+f);
    }
    for ( int i = 1 ; i <= 100; i++ )
    {
      f.setPosition(i,false);
      //System.out.println(" "+f);
    }

    f.setPosition(1000000,true);
    //System.out.println(" "+f);
    System.out.println(" 999999 : "+f.getPosition(999999));
    System.out.println("1000000 : "+f.getPosition(1000000));
    System.out.println("1000001 : "+f.getPosition(1000001));

  }

}