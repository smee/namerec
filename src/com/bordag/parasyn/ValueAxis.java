package com.bordag.parasyn;

import java.util.*;

import com.bordag.parasyn.util.*;

/**
 * @author not attributable
 * @version 1.0
 */
public abstract class ValueAxis
{
  protected DBUtilUncached dbUtil = null;

  public ValueAxis()
  {
    throw new IllegalArgumentException("Don't call ValueAxis constructor without connection");
  }

  public ValueAxis(DBUtilUncached dbUtil)
  {
    if ( dbUtil == null )
    {
      throw new IllegalArgumentException("In ValueAxis constructor called with a null-connection.");
    }
    this.dbUtil = dbUtil;
  }

  /**
   * Returns the values for all pairs built from the given nr and the other elements in the list
   * First it gets all values stored from the nr, then matches them with the given ones,
   * defaulting to 0 if there was once something not stored in the db
   * @param nr
   * @param numbers
   * @return values List<Double>
   */
  public abstract List getValues(Integer nr, List numbers);

}