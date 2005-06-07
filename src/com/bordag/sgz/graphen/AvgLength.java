package com.bordag.sgz.graphen;

// standard imports
import java.util.*;

// app specific imports
import com.bordag.sgz.util.*;

/**
 * Determines the average path length between words in the DB
 * The algorithm here does it by finding a short way to any word with
 * wordnum < 10, as it can be shown that these are all connected with each
 * other.
 * (Interval should be anything from 1 to x, support for y to x is not yet
 * included and the behaviour in such a case not defined.)
 * @author Stefan Bordag
 */
public class AvgLength
{

  protected final static int TARGET_INTERVAL_LEFT = 1;
  protected final static int TARGET_INTERVAL_RIGHT = 10;

  public AvgLength()
  {
  }

  /**
   * Calculates the average length of pathes for a given number of tries
   * prints out:
   * avgLength =
   * mittlereAbweichung =
   * p =
   */
  private void avgLengthOverall()
  {
  }

  /**
   * Claculates the average path length between the two specified intervals of
   * wordnumbers
   */
  private void avgLengthIntervals(int firstLeft, int firstRight, int secLeft, int secRight)
  {
  }

  /**
   * Returns the number of steps one has to traverse at least to get from
   * wordnumber1 to wordnumber2
   */
  private int calcLength(int wordNum1, int wordNum2)
  {
/*    TreeSet neighbours = getNeighbours(wordNum1);
    if ( neighbours.contains(wordNum2) )
    {
      return 1;
    }
    else
    {
      Iterator it = neighbours.iterator();
      if ( ! it.hasNext() )
      {
        System.out.println("Unexpected error in AvgLength.calcLength(int, int)");
      }
      return 1 + calcLengthToInterval(((Integer)it.next()).intValue(),this.TARGET_INTERVAL_LEFT,this.TARGET_INTERVAL_RIGHT);
    }*/
    return 0;
  }

  /**
   * Calculates a path from a given wordnumber to the given interval of
   * wordnumbers
   */
  private int calcLengthToInterval(int wordNum1, int intervalLeft, int intervalRight)
  {
    if ( wordNum1 >= intervalLeft && wordNum1 <= intervalRight )
    {
      return 0;
    }
    else
    {
      TreeSet neighbours = getNeighbours(wordNum1);
      Iterator it = neighbours.iterator();
      int curValue = ((Integer)it.next()).intValue();
      if ( curValue >= intervalLeft && curValue <= intervalRight )
      {
        return 1;
      }
      return 1 + calcLengthToInterval(curValue, this.TARGET_INTERVAL_LEFT, this.TARGET_INTERVAL_RIGHT);
    }
  }

  /**
   * Returns an ordered set of the neighbours (Integer) of the given word
   */
  private TreeSet getNeighbours(int wordNum1)
  {
    return null;
  }
}