package com.bordag.paraexp;

import java.awt.geom.*;

/**
 * This class represents a simple polygon with some convenient constructors and
 * which is of float precision.
 *
 */

public class Polygon implements java.io.Serializable
{

  /**
   * The Polygon is implemented as a GeneralPath
   */
  protected GeneralPath myPolygon = null;

  /**
   * The must be an even number of elements in the given array and at least
   * three
   * @param points the points which define the polygon in the right order
   */
  public Polygon(double[] points)
  {
    if ( points == null || points.length < 4 || points.length % 2 == 1 )
    {
      throw new IllegalArgumentException("In Polygon.Polygon(double[] points) points for polygon are invalid.");
    }
    this.myPolygon = new GeneralPath();
    this.myPolygon.moveTo((float)points[0],(float)points[1]);
    for ( int i = 2 ; i < points.length ; i=i+2 )
    {
      this.myPolygon.lineTo((float)points[i], (float)points[i+1]);
    }
  }

  /**
   * Returns true if the given coordinate is inside this polygon
   * @param x
   * @param y
   * @return Returns true if the given point is inside the polygon or lies on
   * the edges or points of the polygon. (inclusive inside)
   */
  public boolean inside(double x, double y)
  {
    return this.myPolygon.contains(x, y);
  }

  /**
   * Returns true if the given coordinate is outside this polygon
   * @param x
   * @param y
   * @return Returns true if the given point is outside the polygon or lies on
   * the edges or points of the polygon. (exclusive inside)
   */
  public boolean outside(double x, double y)
  {
    if ( this.myPolygon.contains(x,y) )
    {
      return false;
    }
    return true;
  }

  /**
   * Just for testing purposes a main method here.
   * @param args No arguments are being interpreted.
   */
  public static void main(String[] args)
  {
    double[] points = {0.0,0.0, 0.0,0.3, 0.3,0.3, 0.3,0.0};
    Polygon p = new Polygon(points);
    System.out.println("0.4;0.4 : "+p.inside(0.4,0.4));
    System.out.println("0.2;0.2 : "+p.inside(0.2,0.2));
    System.out.println("0.3;0.3 : "+p.inside(0.3,0.3));
  }

}
