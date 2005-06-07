package com.bordag.colloc;

/**
 * This class converts bytes to Integers and vice versa, the same for chars.
 */
public class Converter
{
  public static final int MASK = 0xff;

  protected Converter()
  {
  }

  public static char[] IntegerToChars(Integer a)
  {
    char[] c = new char[4];
    c[0] = (char) ((a.intValue() >> 24) & MASK);
    c[1] = (char) ((a.intValue() >> 16) & MASK);
    c[2] = (char) ((a.intValue() >> 8)  & MASK);
    c[3] = (char) ( a.intValue()        & MASK);
    return c;
  }

  public static byte[] IntegerToBytes(Integer a)
  {
    byte[] c = new byte[4];
    c[0] = (byte) ((a.intValue() >> 24) & MASK);
    c[1] = (byte) ((a.intValue() >> 16) & MASK);
    c[2] = (byte) ((a.intValue() >> 8)  & MASK);
    c[3] = (byte) ( a.intValue()        & MASK);
    return c;
  }


  /**
   * @todo: implement directly, without creating an extra charfield.
   * @param b
   * @param offset
   * @return
   */
  public static Integer CharsToInteger(char[] b, int offset)
  {
    char[] x = new char[4];
    x[0] = b[offset];
    x[1] = b[offset+1];
    x[2] = b[offset+2];
    x[3] = b[offset+3];
    return CharsToInteger(x);
  }

  /**
   * @todo: implement directly, without creating an extra charfield.
   * @todo: implement variable size
   * @param b
   * @param offset
   * @return
   */
  public static Integer bytesToInteger(byte[] b, int offset)
  {
    int mask = MASK;

    int val = 0;
    int shift = 0;
    int j = offset+4;
    for(int i = offset+4 ; i > offset ; i--)
    {
      val |= (byte) b[i - 1] << shift & mask;
      shift += 8;
      mask <<= 8;
    }
    return new Integer(val);
  }


  public static Integer CharsToInteger(char[] b)
  {
    int mask = MASK;

    if(b.length < 5)
    {
        int val = 0;
        int shift = 0;
        int j = b.length;
        for(int i = b.length; i > 0; i--)
        {
            val |= (char) b[i - 1] << shift & mask;
            shift += 8;
            mask <<= 8;
        }
        return new Integer(val);
    }
    throw new IllegalArgumentException("CharsToInteger requires <5 byte arrays");
  }

  public static Integer bytesToInteger(byte[] b)
  {
    int mask = MASK;

    if(b.length < 5)
    {
        int val = 0;
        int shift = 0;
        int j = b.length;
        for(int i = b.length; i > 0; i--)
        {
            val |= (byte) b[i - 1] << shift & mask;
            shift += 8;
            mask <<= 8;
        }
        return new Integer(val);
    }
    throw new IllegalArgumentException("bytesToInteger requires <5 byte arrays");
  }


}