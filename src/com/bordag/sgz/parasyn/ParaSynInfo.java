package com.bordag.sgz.parasyn;

/**
 * <p>
 * This class represents the information about a word. It also contains the
 * maximum values for the coordinates in static variables (so it's the same
 * over all instances and hasn't to be calculated externally)
 * </p>
 *
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Stefan Bordag
 */

public class ParaSynInfo
{
  protected double x = 0.0;
  protected double y = 0.0;

  protected Integer wordNr = null;
  protected String wordString = null;

  public ParaSynInfo(Integer wordNr, String wordString, double x, double y)
  {
    this.x = x;
    this.y = y;
    this.wordNr = wordNr;
    this.wordString = wordString;
  }

  public double getX()
  {
    return this.x;
  }

  public void setX(double x)
  {
    this.x = x;
  }

  public double getY()
  {
    return this.y;
  }

  public void setY(double y)
  {
    this.y = y;
  }

  public Integer getWordNr()
  {
    return this.wordNr;
  }

  public String getWordString()
  {
    return this.wordString;
  }

  public String toString()
  {
    return this.wordString;
  }
}