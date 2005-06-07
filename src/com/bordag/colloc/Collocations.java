package com.bordag.colloc;

import java.util.*;

/**
 * This interface defines the methods with which it is possible to access
 * collocations either from file or from ram or some other method.
 *
 * @author Stefan Bordag
 * @date   21.11.2003
 */

public interface Collocations
{
/*  public int maxCollocs = 1000;
  public int minSignificance = 4;
  public int minWordNr = 1000;*/

  /**
   * This returns a list of Integer[2] arrays where the first value
   * is the wordNr and the second the significance.
   *
   * This method returns all possible values.
   *
   * @param wordNr
   * @return List(Integer[2])
   */
  public List getCollocsAndSigs(Integer wordNr);

  /**
   * This returns a list of Integer[2] arrays where the first value
   * is the wordNr and the second the significance.
   *
   * This method returns only those values which satisfy the given arguments
   *
   * @param wordNr
   * @return List(Integer[2])
   */
  public List getCollocsAndSigs(Integer wordNr, int myMinSignificance, int myMaxCollocs, int myMinWordNr);

  /**
   * This returns a List of Integer objects which are the collocations of
   * the given wordNumber.
   *
   * This method returns all possible values
   *
   * @param wordNr
   * @return List(Integer)
   */
  public List getCollocs(Integer wordNr);

  /**
   * This returns a List of Integer objects which are the collocations of
   * the given wordNumber.
   *
   * This method returns only those values which satisfy the given arguments
   *
   * @param wordNr
   * @return List(Integer)
   */
  public List getCollocs(Integer wordNr, int myMinSignificance, int myMaxCollocs, int myMinWordNr);

}