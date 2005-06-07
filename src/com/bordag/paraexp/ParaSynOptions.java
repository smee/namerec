package com.bordag.paraexp;

import com.bordag.util.*;

/**
 * @author not attributable
 */

public class ParaSynOptions
{

  private static final int PSMAP_MIN_SIG = 10;
  private static final int PSMAP_MAX_COLL = 500;
  private static final int PSMAP_MIN_WORDNR = 1000;
  private static final double PSMAP_VARIANCE_INFLUENCE = 4.0;

  public int getPsmapMinSig()
  {
    return this.PSMAP_MIN_SIG;
  }
  public int getPsmapMaxColl()
  {
    return this.PSMAP_MAX_COLL;
  }
  public int getPsmapMinWordNr()
  {
    return this.PSMAP_MIN_WORDNR;
  }
  public double getPsmapMinVarianceInfluence()
  {
    return this.PSMAP_VARIANCE_INFLUENCE;
  }

  /**
   * The sole instance of options for the whole program
   */
  private static ParaSynOptions instance = null;

  protected ParaSynOptions()
  {
  }

  public static ParaSynOptions getInstance()
  {
    if ( instance == null )
    {
      synchronized(ParaSynOptions.class)
      {
        instance = new ParaSynOptions();
      }
    }
    return instance;
  }

  public String toString()
  {
    return " MaxColl    : "+ this.getPsmapMaxColl()
          +"   Minsig     : "+ this.getPsmapMinSig()
          +"   MinWordNr  : "+ this.getPsmapMinWordNr()
          +"   MinVariance: "+ this.getPsmapMinVarianceInfluence();

  }
}
