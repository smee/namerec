package com.bordag.paraexp;

import java.util.*;
import com.bordag.colloc.*;

/**
 * This class respresents the coordinate system, with the two axis' which is
 * stored as a set of ParaSynWord instances which contain the two different
 * values and eventually the wordString as well for debugging.
 *
 * @author Stefan Bordag
 */
public class ParaSynMap implements java.io.Serializable
{

  /**
   * It's a set of ParaSynWord instances which contains x- and y- values
   */
  protected Set collocations = null;

  protected ParaSynWord word = null;

  protected Collocations sigValues = null;

  protected Collocations anzValues = null;

  protected FileWortliste wortliste = null;

  protected boolean resolveStrings = true;

//  protected double[] meanBeforePruning = null;
//  protected double meanVarianceBeforePruning = -1.0;

  public ParaSynMap()
  {

  }

  public ParaSynMap(Integer wordNr, Collocations sigs, Collocations anz, FileWortliste wortliste, boolean resolveStrings)
  {
    this.word = new ParaSynWord(wordNr);
    this.sigValues = sigs;
    this.anzValues = anz;
    this.wortliste = wortliste;
    this.collocations = new HashSet();
    this.resolveStrings = resolveStrings;
    init();
  }

  public int hashCode()
  {
    return this.word.wordNr.intValue();
  }

  protected void init()
  {
    Map map = new HashMap();
    // get all sigs, put into the objects
    for ( Iterator it = this.sigValues.getCollocsAndSigs(this.word.wordNr, ParaSynOptions.getInstance().getPsmapMinSig(), ParaSynOptions.getInstance().getPsmapMaxColl(), ParaSynOptions.getInstance().getPsmapMinWordNr()).iterator() ; it.hasNext() ; )
    {
      Integer[] curEl = (Integer[])it.next();
      ParaSynWord curWord = null;
      if ( this.resolveStrings )
      {
        curWord = new ParaSynWord(this.wortliste.getWord(curEl[0]),curEl[0]);
      }
      else
      {
        curWord = new ParaSynWord(curEl[0]);
      }
      curWord.setY(curEl[1].doubleValue());
      map.put(curEl[0], curWord);
    }
    // get all anz, add info into the objects
    for ( Iterator it = this.anzValues.getCollocsAndSigs(this.word.wordNr, ParaSynOptions.getInstance().getPsmapMinSig(), ParaSynOptions.getInstance().getPsmapMaxColl(), ParaSynOptions.getInstance().getPsmapMinWordNr()).iterator() ; it.hasNext() ; )
    {
      Integer[] curEl = (Integer[])it.next();

      ParaSynWord curWord = null;
      if ( map.containsKey(curEl[0]) )
      {
        curWord = (ParaSynWord)map.get(curEl[0]);
      }
      else
      {
        curWord = new ParaSynWord( this.wortliste.getWord( curEl[0] ), curEl[0] );
      }
      curWord.setX(curEl[1].doubleValue());
      map.put(curEl[0], curWord);
    }

    // now find extreme values for normalizing later on
    Double minX = new Double(Double.MAX_VALUE);
    Double minY = new Double(Double.MAX_VALUE);
    Double maxX = new Double(Double.MIN_VALUE);
    Double maxY = new Double(Double.MIN_VALUE);
    for ( Iterator it = map.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curKey = (Integer)it.next();
      ParaSynWord curWord = (ParaSynWord)map.get(curKey);
      if ( minX.doubleValue() > curWord.getX() )
      {
        minX = new Double(curWord.getX());
      }
      if ( maxX.doubleValue() < curWord.getX() )
      {
        maxX = new Double(curWord.getX());
      }
      if ( minY.doubleValue() > curWord.getY() )
      {
        minY = new Double(curWord.getY());
      }
      if ( maxY.doubleValue() < curWord.getY() )
      {
        maxY = new Double(curWord.getY());
      }
    }

    maxX = new Double( maxX.doubleValue() - minX.doubleValue() );
    maxY = new Double( maxY.doubleValue() - minY.doubleValue() );
    double constantX = 1.0;
    double constantY = 1.0;

    // finally normalizing here:
    for ( Iterator it = map.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curKey = (Integer)it.next();
      ParaSynWord curEl = (ParaSynWord)map.get(curKey);
      curEl.setX( Math.log((curEl.getX() - minX.doubleValue())+constantX)/(Math.log(maxX.doubleValue()+constantX)) );
      curEl.setY( Math.log((curEl.getY() - minY.doubleValue())+constantY)/(Math.log(maxY.doubleValue()+constantY)) );
      this.collocations.add(curEl);
    }

    //pruneMap();

  }

  /**
   * This method prunes the map off unneeded elements which just provide
   * statistic noise
   * 0. Calculates meanPoint and variance before pruning
   * 1. Determines the mean point
   * 2. removes all ParaSynWords to the left of this mean
   */
  protected void pruneMap()
  {
    Set newSet = new HashSet();
    // the following two calls store the values in the global variables
    double[] mean = calcMean();
    calcMeanVariance(mean);
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curWord = (ParaSynWord)it.next();
      if ( curWord.getX() > mean[0]/2.0 || curWord.getY() > mean[1]/2.0 )
      {
        newSet.add(curWord);
      }
    }
    this.collocations = newSet;
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
  protected Set getCloseWordsTo(ParaSynWord point, double threshold, double xFactor, double yFactor)
  {
    Set retSet = new HashSet();
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curWord = (ParaSynWord)it.next();
      if ( getDistance(point, curWord, xFactor, yFactor) < threshold )
      {
        retSet.add(curWord);
      }
    }
    return retSet;
  }

  /**
   * Returns the distance between two instances of ParaSynWords, modified by the
   * according parameters
   * @param sourceWord
   * @param targetWord
   * @param xFactor
   * @param yFactor
   * @return
   */
  protected double getDistance(ParaSynWord sourceWord, ParaSynWord targetWord, double xFactor, double yFactor)
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
   * @return returns a possibly empty Hashset of ParaSynWord (never returns
   * null)
   */
  public HashSet getParaSynInfos(Polygon region)
  {
    HashSet retSet = new HashSet();
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curP = (ParaSynWord)it.next();
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
    ParaSynWord point = new ParaSynWord(new String("linguisticCollocations"), new Integer(-1), 0.0, 0.0);
    point.xValue = 0.03;
    point.yValue = 1.0;
    return (HashSet)getCloseWordsTo(point, 0.7, 1.0, 3.0);
  }

  public HashSet getCohyponyms()
  {
    ParaSynWord point = new ParaSynWord(new String("linguisticCollocations"), new Integer(-1), 0.0, 0);
    point.xValue = 0.8;
    point.yValue = 0.8;
    return (HashSet)getCloseWordsTo(point, 0.5, 1.0, 1.0);
  }

  public HashSet getHyperonyms()
  {
    ParaSynWord point = new ParaSynWord(new String("linguisticCollocations"), new Integer(-1), 0.0, 0);
    point.xValue = 0.6;
    point.yValue = 0.0;
    return (HashSet)getCloseWordsTo(point, 0.6, 1.0, 1.0);
  }


//------new experiment with radians

  protected double[] calcMean()
  {
/*    if ( this.meanBeforePruning != null )
    {
      return this.meanBeforePruning;
    }*/
    double mx = 0.0;
    double my = 0.0;
    double count = 0.0;
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curP = (ParaSynWord)it.next();
      mx += curP.getX();
      my += curP.getY();
      count += 1.0;
    }
    double[] retVal = new double[2];
    retVal[0] = mx / count;
    retVal[1] = my / count;
//    this.meanBeforePruning = retVal;
    return retVal;
  }

  protected double calcMeanVariance(double[] mean)
  {
/*    if ( this.meanVarianceBeforePruning >= 0.0 )
    {
      return this.meanVarianceBeforePruning;
    }*/
    double sumDistances = 0.0;
    double count = 0.0;
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curP = (ParaSynWord)it.next();
      double[] target = new double[2];
      target[0] = curP.getX();
      target[1] = curP.getY();
      sumDistances += calcDistance(mean, target);
      count += 1.0;
    }
//    this.meanVarianceBeforePruning = sumDistances / count;
    return sumDistances / count;
  }

  protected double[] getIdealHyperonymPoint()
  {
    double[] mean = calcMean();
    double variance = calcMeanVariance(mean);
    double[] idealHyperonymPoint = new double[2];
    idealHyperonymPoint[0] = mean[0] + ParaSynOptions.getInstance().getPsmapMinVarianceInfluence()*variance;
    idealHyperonymPoint[1] = mean[1] - 2.0*ParaSynOptions.getInstance().getPsmapMinVarianceInfluence()*variance;
    return idealHyperonymPoint;
  }

  protected double[] getIdealCohyponymPoint()
  {
    double[] mean = calcMean();
    double variance = calcMeanVariance(mean);
    double[] idealHyperonymPoint = new double[2];
    idealHyperonymPoint[0] = mean[0] + ParaSynOptions.getInstance().getPsmapMinVarianceInfluence()*variance;
    idealHyperonymPoint[1] = mean[1] + ParaSynOptions.getInstance().getPsmapMinVarianceInfluence()*variance;
    return idealHyperonymPoint;
  }

  protected double[] getIdealLingCollocPoint()
  {
    double[] mean = calcMean();
    double variance = calcMeanVariance( mean );
    double[] idealHyperonymPoint = new double[2];
    idealHyperonymPoint[0] = mean[0] - ParaSynOptions.getInstance().getPsmapMinVarianceInfluence() * variance;
    idealHyperonymPoint[1] = mean[1] + ParaSynOptions.getInstance().getPsmapMinVarianceInfluence() * variance;
    return idealHyperonymPoint;
  }



  /**
   * Returns the distance between two instances of ParaSynWords, modified by the
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
  protected ParaSynWordHashMap normalizeRadian(ParaSynWordHashMap map)
  {
    double maxVal = Double.MIN_VALUE;
    for ( Iterator it = map.keySet().iterator() ; it.hasNext() ; )
    {
      ParaSynWord curWord = ( ParaSynWord )it.next();
      maxVal = Math.max(maxVal, map.get(curWord).doubleValue());
    }
    for ( Iterator it = map.keySet().iterator() ; it.hasNext() ; )
    {
      ParaSynWord curWord = ( ParaSynWord )it.next();
      double curVals = map.get(curWord).doubleValue();
      curVals = 1.0 - (curVals / maxVal);
      map.remove(curWord);
      map.put(curWord, new Double(curVals));
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
  public ParaSynWordHashMap getLinguisticCollocationsRadian()
  {
    double[] ideal = getIdealLingCollocPoint();
    ParaSynWordHashMap map = new ParaSynWordHashMap();
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curP = (ParaSynWord)it.next();
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

  public ParaSynWordHashMap getCohyponymsRadian()
  {
    double[] ideal = getIdealCohyponymPoint();
    ParaSynWordHashMap map = new ParaSynWordHashMap();
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curP = (ParaSynWord)it.next();
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

  public ParaSynWordHashMap getHyperonymsRadian()
  {
    double[] ideal = getIdealHyperonymPoint();
    ParaSynWordHashMap map = new ParaSynWordHashMap();
    for ( Iterator it = this.collocations.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curP = (ParaSynWord)it.next();
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
