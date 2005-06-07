package com.bordag.paraexp;

import java.util.*;

/**
 * Represents a word, having a number, a string and a significance value.
 * Setting the string to some value is optional.
 *
 * @author Stefan Bordag
 */
public class ParaSynWord implements Comparable, Comparator, java.io.Serializable
{

  public String word = null;
  public Integer wordNr = null;
  public double xValue = 0.0; // cos or anz
  public double yValue = 0.0; // significance
//  public int meaning = 0;

  public ParaSynWord(String word, Integer wordNr, double xValue, double yValue)
  {
    if ( word == null )
    {
      throw new IllegalArgumentException("Word should not be null here!!");
    }
    this.word = word;
    this.wordNr = wordNr;
    this.xValue = xValue;
    this.yValue = yValue;
  }

  public ParaSynWord(Integer wordNr, double xValue, double yValue)
  {
    this.wordNr = wordNr;
    this.xValue = xValue;
    this.yValue = yValue;
  }

  public ParaSynWord(Integer wordNr)
  {
    this.wordNr = wordNr;
  }

  public ParaSynWord(String word, Integer wordNr)
  {
    this.word = word;
    this.wordNr = wordNr;
  }


  public double getX()
  {
    return xValue;
  }

  public double getY()
  {
    return yValue;
  }

  public void setX(double x)
  {
    this.xValue = x;
  }

  public void setY(double y)
  {
    this.yValue = y;
  }

  public int hashCode()
  {
    return this.wordNr.intValue();
  }

  public int compareTo(Object o)
  {
    return this.word.compareToIgnoreCase(o.toString());
  }

  /**
   * Implementation of the Comparator interface to allow alphabetical ordering
   * of instances of this class.
   */
  public int compare(Object o, Object o2)
  {
    if ( o instanceof ParaSynWord && o2 instanceof ParaSynWord )
    {
      try
      {
        Integer iOne = ((ParaSynWord)o).wordNr;
        Integer iTwo = ((ParaSynWord)o2).wordNr;
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
      System.out.println("In com.bordag.parasyn.ParaSynWord.compareTo comparing something which's not ParaSynWord... exiting!");
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
    return this.wordNr.equals( ((ParaSynWord)o).wordNr );
  }

  public String toString()
  {
    if ( this.word == null && this.xValue == 0.0 && this.yValue == 0.0 )
    {
      return "PSW: "+this.wordNr.toString();
    }
    else
    {
      int index = 0;

      String x = ""+this.xValue;
      index = x.indexOf(".");
      x = x.substring(0, index + Math.min(3, x.length() - index ));
      String y = ""+this.yValue;
      index = y.indexOf(".");
      y = y.substring(0, index + Math.min(3, y.length() - index ));
      return "PSW: "+""+this.word+" "+this.wordNr+" x["+x+"] y["+y+"]";





//      return ""+this.word+" "+this.wordNr+" x["+this.xValue+"] y["+this.yValue+"]";
    }
/*    else if ( this.word != null )
    {
      String sig = "" + this.yValue;
      int index = sig.indexOf(".");
      sig = sig.substring(0, index + Math.min(3, sig.length() - index ));
      String yval = "" + this.xValue;
      index = yval.indexOf(".");
      yval = yval.substring(0, index + Math.min(3, yval.length() - index ));
      return "  "+this.word + " " + this.wordNr + " " + "s[" + sig + "]x["+yval+"]";
    }
    else
    {
      return this.wordNr + "[" + this.yValue + "]x["+this.xValue+"]";
    }*/
  }

}
