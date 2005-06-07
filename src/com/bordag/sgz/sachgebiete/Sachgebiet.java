package com.bordag.sgz.sachgebiete;

// app specific imports
import com.bordag.sgz.algorithms.*;
import com.bordag.sgz.util.*;

// standard imports
import java.util.*;

/**
 * Abstract class which provides a common interface how to handle a
 * sachgebietsclassifier and some tool methods
 *
 * Create an instance of this class, give it a sachgebiet and depending of the
 * current implementation it should print out what it find for this sachgebiet
 *
 * @author  Stefan Bordag
 * @date    09.05.2002
 */
public abstract class Sachgebiet
{

  /**
   * Connection to DB
   */
  protected DBConnection connection = null;

  /**
   * The sachgebietsnumber of the sachgebiet which we are processing
   */
  protected ComparableStringBuffer sachgebietNr = null;

  protected Sachgebiet()
  {
  }

  /**
   * Standard constructor for this class, as it need a conection to the DB and
   * which sachgebiet it should process
   */
  public Sachgebiet(DBConnection connection)
  {
    this.connection = connection;
    //runAlgorithm();
  }

  /**
   * Standard constructor for this class, as it need a conection to the DB and
   * which sachgebiet it should process
   */
  public Sachgebiet(DBConnection connection, ComparableStringBuffer sachgebiet)
  {
    this.connection = connection;
    this.sachgebietNr = this.connection.getNumberForSachgebiet(sachgebiet);
    //runAlgorithm();
  }

  /**
   * This should start the algorithm, whatever it does
   */
  protected abstract void runAlgorithm();


  /**
   * Prints a given array of word numbers resolving them automatically to word_bins
   */
  protected void printArray(ComparableStringBuffer[] array)
  {
    if ( array == null )
    {
      return;
    }
    for ( int i = 0 ; i < array.length ; i++ )
    {
      Output.print(this.connection.getWordForNumber(array[i])+" ");

    }
  }

  /**
   * Prints a given set of word numbers resolving them automatically to word_bins
   */
  protected void printSet(Set array)
  {
    if ( array == null )
    {
      return;
    }
    for ( Iterator it = array.iterator() ; it.hasNext() ; )
    {
      Output.print(this.connection.getWordForNumber((ComparableStringBuffer)it.next())+" ");
    }
  }

  public static DBConnection getStandardConnection()
  {
    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();
    DBConnection connection = null;
    try
    {
      connection = new DBConnection(url, user, passwd);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
    return connection;
  }

}
