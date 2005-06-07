package com.bordag.sgz.util;

/**
 * Debugger class to be able to switch on or off debugging messages.
 *
 * @author  Stefan Bordag
 * @date    28.12.2001
 */
public class Debugger
{
  private static Debugger instance = null;
  public  static int NUL_LEVEL     = 0;// be quiet
  public  static int MIN_LEVEL     = 1;// true errors
  public  static int MED_LEVEL     = 2;// resource errors
  public  static int MAX_LEVEL     = 3;// other stuff
  private static int level         = 0;

  /**
   * Hidden away
   */
  protected Debugger()
  {
  }

  /**
   *  Makes sure there is always just one instance of Debugger
   */
  public static Debugger getInstance()
  {
    if ( instance == null )
    {
      synchronized ( Debugger.class )
      {
        instance = new Debugger();
      }
    }
    return instance;
  }

  /**
   * Prints the given message if the given level is big enough (so that we
   * can filter messages away)
   */
  public void println(String message, int level)
  {
    this.level = new Integer(Options.getInstance().getGenDebugLevel()).intValue();
    if ( level <= this.level )
    {
      System.err.println("Debugger: ["+message+"]");
    }
  }
}
