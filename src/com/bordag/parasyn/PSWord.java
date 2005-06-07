package com.bordag.parasyn;

import java.util.*;

import com.bordag.parasyn.util.*;

/**
 * Represents a word, having a number, a string and a significance value.
 * Setting the string to some value is optional.
 */
public class PSWord implements Comparable, Comparator, java.io.Serializable
{
  public CHString word = null;
  public Integer wordNr = null;
  public double significance = 0.0;
  public double xValue = 0.0;
  public int meaning = 0;

  public PSWord(CHString word, Integer wordNr, double significance, int meaning)
  {
    this.word = word;
    this.wordNr = wordNr;
    this.significance = significance;
    this.meaning = meaning;
  }

  public PSWord(Integer wordNr, double significance, int meaning)
  {
    this.wordNr = wordNr;
    this.significance = significance;
    this.meaning = meaning;
  }

  public double getX()
  {
    return xValue;
  }

  public double getY()
  {
    return significance;
  }

  public void setX(double x)
  {
    this.xValue = x;
  }

  public void setY(double y)
  {
    this.significance = y;
  }

  public int hashCode()
  {
    return this.wordNr.intValue();
  }

  public int compareTo(Object o)
  {
    return this.word.toString().compareToIgnoreCase(o.toString());
  }

  /**
   * Implementation of the Comparator interface to allow alphabetical ordering
   * of instances of this class.
   */
  public int compare(Object o, Object o2)
  {
    if ( o instanceof PSWord && o2 instanceof PSWord )
    {
      try
      {
        Integer iOne = ((PSWord)o).wordNr;
        Integer iTwo = ((PSWord)o2).wordNr;
        return iOne.compareTo(iTwo);
      }
      catch ( Exception ex )
      {
        return o.toString().compareTo(o2.toString());
      }
    }
    else if ( o instanceof Integer && o2 instanceof Integer )
    {
      Integer iOne = (Integer)o;
      Integer iTwo = (Integer)o2;
      return iOne.compareTo(iTwo);
    }
    else
    {
      System.out.println("In com.bordag.parasyn.PSWord.compareTo comparing something which's not PSWord... exiting!");
      //throw new IllegalArgumentException("HERE");
      System.exit(0);
    }
    return 0;
  }

  /**
   * Tests whether the stored string of that object is the same as what we have
   */
  public boolean equals(Object o)
  {
    if ( o == null )
    {
      return false;
    }
    return this.wordNr.equals( ((PSWord)o).wordNr );
  }

  public String toString()
  {
    if ( this.word == null && this.xValue == 0.0 && this.significance == 0.0 )
    {
      return this.wordNr.toString();
    }
    else if ( this.word != null )
    {
      String sig = "" + this.significance;
      int index = sig.indexOf(".");
      sig = sig.substring(0, index + Math.min(3, sig.length() - index ));
      String yval = "" + this.xValue;
      index = yval.indexOf(".");
      yval = yval.substring(0, index + Math.min(3, yval.length() - index ));
      return "  "+this.word + " " + this.wordNr + " " + "s[" + sig + "]x["+yval+"]m["+this.meaning+"]";
    }
    else
    {
      return this.wordNr + "[" + this.significance + "]x["+this.xValue+"]m["+this.meaning+"]";
    }
  }

}