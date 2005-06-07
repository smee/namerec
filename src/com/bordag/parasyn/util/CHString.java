package com.bordag.parasyn.util;

import java.util.Comparator;

/**
 * This is a String representation class which can be put into Hashtables as
 * keys. That means that two different instances of a String containing the
 * same string will always have the same Hashvalue. <br>
 * Internally it holds the strings as a StringBuffer.
 * @author    Stefan Bordag
 * @date      28.12.2001
 */
public class CHString implements Comparable, Comparator, java.io.Serializable
{
  /**
   * This is the string which is stored in this instance
   */
  private StringBuffer string = null;

  /**
   * General constructor, initializes with an empty string
   */
  public CHString()
  {
    this.string = new StringBuffer("");
  }

  /**
   * Default constructor, initializes with the given String
   */
  public CHString(String s)
  {
    this.string = new StringBuffer(s);
  }

  /**
   * Another constructor, initialises with the string which is made up from
   * the given integer
   */
  public CHString(int u)
  {
    this.string = new StringBuffer(u);
  }

  /**
   * Gives the possibility to alter the stored string
   */
  public void setString(String s)
  {
    this.string = new StringBuffer(s);
  }

  /**
   * Returns the stored string
   */
  public String toString()
  {
    return this.string.toString();
  }

  /**
   * Calculates the hashcode so that it always is the same for equals strings
   */
  public int hashCode()
  {
    try
    {
      return new Integer(this.string.toString()).intValue();
    }
    catch ( NumberFormatException nfe )
    {
      return this.string.hashCode();
    }
  }

  public int compareTo(Object o)
  {
    return string.toString().compareToIgnoreCase(o.toString());
  }

  public int compare(Object o, Object o2)
  {
    if ( o instanceof CHString && o2 instanceof CHString )
    {
      try
      {
        Integer iOne = new Integer ( o.toString() );
        Integer iTwo = new Integer ( o2.toString() );
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
      System.out.println("In com.bordag.parasyn.CHString.compareTo comparing something which's not CHString... exiting!");
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
    return this.string.toString().equals(o.toString());
  }

}
