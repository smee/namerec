package com.bordag.parasyn;

import java.util.*;
import com.bordag.parasyn.util.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ParaSynMap implements java.io.Serializable
{

  /**
   * It's a set of PSWord instances whatsoever
   */
  protected Set collocations = null;

  protected PSWord word = null;

  public ParaSynMap(Set colls)
  {
    this.collocations = colls;
  }

  public void setWord(PSWord word)
  {
    this.word = word;

/*    double[] hyp = getIdealHyperonymPoint();
    PSWord hypW = new PSWord(new CHString("HYPERONYM"), new Integer(1), hyp[1], 1 );
    hypW.xValue = hyp[0];
    this.collocations.add(hypW);

    double[] cohyp = getIdealCohyponymPoint();
    PSWord cohypW = new PSWord(new CHString("COHYPONYM"), new Integer(2), cohyp[1], 1 );
    cohypW.xValue = cohyp[0];
    this.collocations.add(cohypW);

    double[] lingcoll = getIdealLingCollocPoint();
    PSWord lingcollW = new PSWord(new CHString("LING COLL"), new Integer(3), lingcoll[1], 1 );
    lingcollW.xValue = lingcoll[0];
    this.collocations.add(lingcollW);

    double[] mean = calcMean();
    PSWord meanW = new PSWord(new CHString("MEAN"), new Integer(4), mean[1], 1 );
    meanW.xValue = mean[0];
    this.collocations.add(meanW);
*/

  }

  public Set getElements()
  {
    return this.collocations;
  }

  /**
   * Returns a set of words which are close enough to the given point
   * @param point
   * @param threshold
   * @return
   */
  protected Set getCloseWordsTo(PSWord point, double threshold, double xFactor, double yFactor)
  {
    Set retSet = new HashSet();
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curWord = (PSWord)it.next();
      if ( getDistance(point, curWord, xFactor, yFactor) < threshold )
      {
        retSet.add(curWord);
      }
    }
    return retSet;
  }

  /**
   * Returns the distance between two instances of PSWords, modified by the
   * according parameters
   * @param sourceWord
   * @param targetWord
   * @param xFactor
   * @param yFactor
   * @return
   */
  protected double getDistance(PSWord sourceWord, PSWord targetWord, double xFactor, double yFactor)
  {
    // sqrt( (x2-x1)^2+(y2-y1)^2 )
    double x1 = sourceWord.getX()*xFactor;
    double x2 = targetWord.getX()*xFactor;
    double y1 = sourceWord.getY()*yFactor;
    double y2 = targetWord.getY()*yFactor;
    double distance = Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
    return distance;
  }

  /**
   * Returns the ParaSynInfo objects which are in the region. The region is
   * understood to be quadratic and 1 long and 1 height. That means that
   * 0.5;0.5 1;0.5 1;1 0.5;1 gives the upper right quarter of the region.
   *
   * @param region The region from which to return wordNrs
   * @return returns a possibly empty Hashset of PSWord (never returns
   * null)
   */
  public HashSet getParaSynInfos(Polygon region)
  {
    HashSet retSet = new HashSet();
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curP = (PSWord)it.next();
      if ( region.inside(curP.getX(), curP.getY()) )
      {
        retSet.add(curP);
      }
    }
    return retSet;
  }

  /**
   * Gives a first glance on what possibly linguistic collocations might be in
   * this map
   * @return
   */
  public HashSet getLinguisticCollocations()
  {
    PSWord point = new PSWord(new CHString("linguisticCollocations"), new Integer(-1), 0.0, 0);
    point.xValue = 0.03;
    point.significance = 1.0;
    return (HashSet)getCloseWordsTo(point, 0.7, 1.0, 3.0);
  }

  public HashSet getCohyponyms()
  {
    PSWord point = new PSWord(new CHString("linguisticCollocations"), new Integer(-1), 0.0, 0);
    point.xValue = 0.8;
    point.significance = 0.8;
    return (HashSet)getCloseWordsTo(point, 0.5, 1.0, 1.0);
  }

  public HashSet getHyperonyms()
  {
    PSWord point = new PSWord(new CHString("linguisticCollocations"), new Integer(-1), 0.0, 0);
    point.xValue = 0.6;
    point.significance = 0.0;
    return (HashSet)getCloseWordsTo(point, 0.6, 1.0, 1.0);
  }


//------new experiment with radians

  protected double[] calcMean()
  {
    double mx = 0.0;
    double my = 0.0;
    double count = 0.0;
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curP = (PSWord)it.next();
      mx += curP.getX();
      my += curP.getY();
      count += 1.0;
    }
    double[] retVal = new double[2];
    retVal[0] = mx / count;
    retVal[1] = my / count;
    return retVal;
  }

  protected double calcMeanVariance(double[] mean)
  {
    double sumDistances = 0.0;
    double count = 0.0;
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curP = (PSWord)it.next();
      double[] target = new double[2];
      target[0] = curP.getX();
      target[1] = curP.getY();
      sumDistances += calcDistance(mean, target);
      count += 1.0;
    }
    return sumDistances / count;
  }

  protected double[] getIdealHyperonymPoint()
  {
    double[] mean = calcMean();
    double variance = calcMeanVariance(mean);
    double[] idealHyperonymPoint = new double[2];
    idealHyperonymPoint[0] = mean[0] + 1.0*variance;
    idealHyperonymPoint[1] = mean[1] - 1.0*variance;
    return idealHyperonymPoint;
  }

  protected double[] getIdealCohyponymPoint()
  {
    double[] mean = calcMean();
    double variance = calcMeanVariance(mean);
    double[] idealHyperonymPoint = new double[2];
    idealHyperonymPoint[0] = mean[0] + 1.0*variance;
    idealHyperonymPoint[1] = mean[1] + 1.0*variance;
    return idealHyperonymPoint;
  }

  protected double[] getIdealLingCollocPoint()
  {
    double[] mean = calcMean();
    double variance = calcMeanVariance( mean );
    double[] idealHyperonymPoint = new double[2];
    idealHyperonymPoint[0] = mean[0] - 1.0 * variance;
    idealHyperonymPoint[1] = mean[1] + 1.0 * variance;
    return idealHyperonymPoint;
  }



  /**
   * Returns the distance between two instances of PSWords, modified by the
   * according parameters
   * @param sourceX,Y
   * @param targetX,Y
   * @return
   */
  protected double calcDistance(double[] source, double[] target)
  {
    // sqrt( (x2-x1)^2+(y2-y1)^2 )
    double x1 = source[0];
    double x2 = target[0];
    double y1 = source[1];
    double y2 = target[1];
    return Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
  }

  /**
   * Goes through all elements of the map
   * counts maximum and then goes through again, normalizing them.
   * @param map
   * @return
   */
  protected PSWordHashMap normalizeRadian(PSWordHashMap map)
  {
    double maxVal = Double.MIN_VALUE;
    for ( Iterator it = map.keySet().iterator() ; it.hasNext() ; )
    {
      PSWord curWord = ( PSWord )it.next();
      maxVal = Math.max(maxVal, map.get(curWord)[0]);
    }
    for ( Iterator it = map.keySet().iterator() ; it.hasNext() ; )
    {
      PSWord curWord = ( PSWord )it.next();
      double[] curVals = map.get(curWord);
      curVals[0] = 1.0 - (curVals[0] / maxVal);
    }
    return map;
  }


  /**
   * All points which are more to the upper left of this point are have following
   * formula:
   *   0.2*distance
   * All other:
   *   distance
   *
   * @return
   */
  public PSWordHashMap getLinguisticCollocationsRadian()
  {
    double[] ideal = getIdealLingCollocPoint();
    PSWordHashMap map = new PSWordHashMap(1);
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curP = (PSWord)it.next();
      double[] curPoint = new double[2];
      curPoint[0] = curP.getX();
      curPoint[1] = curP.getY();

      double[] rating = new double[1];
      rating[0] = calcDistance(ideal, curPoint);
      if ( curPoint[0] < ideal[0] && curPoint[1] > ideal[1] )
      {
        //rating[0] *= 0.2;
      }
      map.put(curP, rating);
    }
    return normalizeRadian(map);
  }

  public PSWordHashMap getCohyponymsRadian()
  {
    double[] ideal = getIdealCohyponymPoint();
    PSWordHashMap map = new PSWordHashMap(1);
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curP = (PSWord)it.next();
      double[] curPoint = new double[2];
      curPoint[0] = curP.getX();
      curPoint[1] = curP.getY();

      double[] rating = new double[1];
      rating[0] = calcDistance(ideal, curPoint);
      if ( curPoint[0] > ideal[0] && curPoint[1] > ideal[1] )
      {
        //rating[0] *= 0.2;
      }
      map.put(curP, rating);
    }
    return normalizeRadian(map);
  }

  public PSWordHashMap getHyperonymsRadian()
  {
    double[] ideal = getIdealHyperonymPoint();
    PSWordHashMap map = new PSWordHashMap(1);
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curP = (PSWord)it.next();
      double[] curPoint = new double[2];
      curPoint[0] = curP.getX();
      curPoint[1] = curP.getY();

      double[] rating = new double[1];
      rating[0] = calcDistance(ideal, curPoint);
      if ( curPoint[0] > ideal[0] && curPoint[1] < ideal[1] )
      {
        //rating[0] *= 0.2;
      }
      map.put(curP, rating);
    }
    return normalizeRadian(map);
  }


}