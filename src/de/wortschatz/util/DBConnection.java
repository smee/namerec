package de.wortschatz.util;

// standard imports
import java.util.*;
import java.sql.*;
import java.io.*;

/**
 * This class represents a connection to a DB and can receive and process
 * queries.
 *
 * @author    Stefan Bordag
 * @date      07.04.2003
 */
public class DBConnection
{
  /**
   * The real connection around which this class is wrapped
   */
  protected Connection connection = null;

  /**
   * The driver used to connect to the DB
   */
  private Object driver = null;

  /**
   * The string for which this class will look to replace with query arguments
   */
  public static final String ARGUMENT_PLACE = "#ARG#";

  /**
   * Don't use this
   */
  private DBConnection()
  {
  }

  /**
   * Construct an instance of this Object which can then execute queries on
   * the DB
   */
  public DBConnection(String URL, String user, String passwd)
  {
    String driver = "org.gjt.mm.mysql.Driver";
    loadDriver(driver);
    try
    {
      System.out.println("Connecting to DB.");
      System.out.println("Installed drivers: ");
      for ( Enumeration enum = DriverManager.getDrivers() ; enum.hasMoreElements() ; )
      {
        System.out.println("["+enum.nextElement().toString()+"]");
      }
      // fix URL
      if ( URL.lastIndexOf("?user=") < 1 )
      {
        URL = URL + "?user=" + user + "&password=" + passwd;
      }

      // go!
      this.connection = DriverManager.getConnection(URL, user, passwd);
    }
    catch (Exception ex)
    {
      System.out.println("Couldn't establish connection to DB ");
      ex.printStackTrace();
    }
  }

  /**
   * Loads the mysql driver or exits
   */
  private void loadDriver(String driver)
  {
    try
    {
      this.driver = Class.forName(driver).newInstance();
      System.out.println(driver.toString());
    }
    catch (Exception e)
    {
      System.out.println("JDBC-Treiber konnte nicht geladen werden.\n");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Returns a String array containing the resulting table from the query, null
   * if there was some problem with the query.
   * Additionally it takes an incomplete query and puts the given arguments into
   * the query at places specially outlined as following:
   * SELECT something FROM somewhere WHERE somewhere.anything = #ARG#;
   * Arguments will be placed in the order in which they come and if there are
   * less than places or more, we return with null (later an Exception)
   */
  public String[][] getResultsOf(String query, String[] queryArguments) throws IllegalArgumentException
  {
    if ( query == null || queryArguments == null || query.length() < 1 || queryArguments.length < 1 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String, String[]) query is invalid.");
    }
    int beginReplace = 0;
    int endReplace = 0;
    for ( int i = 0 ; i < queryArguments.length ; i++ )
    {
      beginReplace = query.indexOf(this.ARGUMENT_PLACE);
      endReplace = query.indexOf(this.ARGUMENT_PLACE) + this.ARGUMENT_PLACE.length();
      if ( queryArguments[i] == null || queryArguments[i].length() < 1 )
      {
        throw new IllegalArgumentException("In DBConnection.getResultsOf(String, String[]) Argument["+i+"] is invalid!");
      }
      try
      {
        query = query.substring(0, beginReplace) + queryArguments[i] + query.substring(endReplace, query.length());
      }
      catch(Exception ex)
      {
        String args = "";
        for ( int j = 0 ; j < queryArguments.length ; j++ )
        {
          args = args+" "+queryArguments[j];
        }
        throw new IllegalArgumentException("In DBConnection.getResultsOf(String, String[]) Query ["+query+"] was used with wrong number of arguments: "+args);
      }
    }
    return getResultsOf(query);
  }

  /**
   * Checks whether we are connected, if not we simulate data and let a warning out
   */
  public String[][] getResultsOf(String query) throws IllegalArgumentException
  {
    if ( this.connection == null )
    {
      System.out.println("DBConnection: Warning, not connected to DB.");
      return null;
    }
    return executeQuery(query);
  }

  /**
   * Returns a String array containing the resulting table from the query, null
   * if there was some problem with the query
   * The array is stored in a way so that telling array[0] gives you all the
   * words, array[1] significance or whatever and so on
   */
  private String[][] executeQuery(String query)
  {
    if ( query == null || query.length() < 1 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String) query is invalid.");
    }
    if ( query.indexOf(this.ARGUMENT_PLACE) >= 0 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String) query contains replaceable Arguments, you should call DBConnection.getResultsOf(String, String[]) instead!");
    }
    String[][] retVal = null;
    try
    {
      Statement statement = this.connection.createStatement();

      ResultSet resultSet = statement.executeQuery(query);

      int dataColumns = resultSet.getMetaData().getColumnCount();

      int dataRows = 0;
      if ( resultSet.wasNull() ) { return null; }
      try
      {
        while ( resultSet.next() ) { dataRows++; }
      }
      catch ( SQLException ex ) { return null; }
      resultSet.beforeFirst();

      retVal = new String[dataRows][dataColumns];
      int rows = 0;
      int columns = 1;

      while (resultSet.next())
      {
        for ( columns = 1 ; columns < dataColumns+1 ; columns++ )
        {
          retVal[rows][columns-1] = resultSet.getString(columns);
        }
        rows++;
      }
    }
    catch ( Exception ex )
    {
      System.out.println("Error processing query ["+query+"]");
      ex.printStackTrace();
    }
    return retVal;
  }

  /**
   * Returns the word from the database which is associated with the given number
   */
  public String getWordForNumber(Integer number)
  {
    String query = "select w.wort_bin from wortliste w where w.wort_nr = "+this.ARGUMENT_PLACE+";";
    String[] args = new String[1];
    args[0] = number.toString();
    String[][] temp = this.getResultsOf(query,args);
    if ( temp != null )
    {
      return temp[0][0];
    }
    return null;
  }

  /**
   * Returns the word from the database which is associated with the given number
   */
  public Integer getNumberForWord(String word)
  {
    String query = "select w.wort_nr from wortliste w where w.wort_bin = \""+this.ARGUMENT_PLACE+"\";";
    String[] args = new String[1];
    args[0] = word.toString();
    String[][] temp = this.getResultsOf(query, args);
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      return new Integer(temp[0][0]);
    }
    return null;
  }

  /**
   * Returns an array with wordnumbers.
   * select w.wort_bin from wortliste w where
   *    w.wort_nr=1
   * or w.wort_nr=2
   * or w.wort_nr=3
   * order by w.wort_bin asc;
   **/
  public String[] getWordsForNumbers(Integer[] numbers)
  {
    if ( numbers == null || numbers.length < 1 )
    {
      return null;
    }
    String queryBegin = "select w.wort_bin from wortliste w where ";
    String queryMid1   = "w.wort_nr=";
    String queryMid2   = " or ";
    String queryEnd    = " order by w.wort_nr;";
    String query = queryBegin;
    for ( int i = 0 ; i < numbers.length ; i++ )
    {
      query = query + "" + queryMid1 + numbers[i].toString();
      if ( i < numbers.length - 1 )
      {
        query = query + queryMid2;
      }
    }
    query = query + "" + queryEnd;

    String[][] temp = this.getResultsOf(query);
    String[] temp2 = new String[temp.length];
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      for ( int i = 0 ; i < temp.length ; i++ )
      {
        temp2[i]=temp[i][0];
      }
    }
    return temp2;
  }

  /**
   * Gets a set of words and returns the same array, just with
   * wordnumbers instead
   **/
  public Set getWordsForNumbers(Set numbers)
  {
    if ( numbers == null || numbers.size() < 1 )
    {
      return numbers;
    }
    Integer[] arr = new Integer[numbers.size()];
    int i = 0;
    for ( Iterator it = numbers.iterator() ; it.hasNext() ; )
    {
      arr[i] = (Integer)it.next();
      i++;
    }
    String[] arr2 = this.getWordsForNumbers(arr);
    Set words = new HashSet();
    for ( int j = 0 ; j < arr2.length ; j++ )
    {
      words.add(arr2[j]);
    }
    TreeSet setSorted = new TreeSet(words);
    return setSorted;
  }

  /**
   * Returns how many time a given word as a number in the DB has been seen
   * or null.
   */
  public Integer getAnzForWordNr(Integer wordNr)
  {
    if ( wordNr == null )
    {
      return null;
    }
    String query = "select w.anzahl from wortliste w where w.wort_nr = "+this.ARGUMENT_PLACE+"";
    String[] args = new String[1];
    args[0] = wordNr.toString();
    String[][] temp = this.getResultsOf(query, args);
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      return new Integer(temp[0][0]);
    }
    return null;
  }

  /**
   * Retuns all those elements which are associated with the given one
   */
  public Set getSatzKollokationen(Integer wortNr, int minSignifikanz, int minWortnummer, int maxKollokationen)
  {
    String query = "SELECT k.wort_nr2,k.signifikanz FROM kollok_sig k WHERE k.signifikanz > #ARG# and k.wort_nr2 > #ARG# and k.wort_nr1 = #ARG# order by k.signifikanz desc limit #ARG#;";
    return getKollokationen(query, wortNr, minSignifikanz, minWortnummer, maxKollokationen);
  }

  /**
   * Retuns all those elements which are left- associated with the given one
   */
  public Set getNachbarKollokationenLinks(Integer wortNr, int minSignifikanz, int minWortnummer, int maxKollokationen)
  {
    String query = "SELECT k.wort_nr2,k.signifikanz FROM kollok_nb k WHERE k.signifikanz > #ARG# and k.wort_nr2 > #ARG# and k.wort_nr1 = #ARG# order by k.signifikanz desc limit #ARG#;";
    return getKollokationen(query, wortNr, minSignifikanz, minWortnummer, maxKollokationen);
  }

  /**
   * Retuns all those elements which are right-associated with the given one
   */
  public Set getNachbarKollokationenRechts(Integer wortNr, int minSignifikanz, int minWortnummer, int maxKollokationen)
  {
    String query = "SELECT k.wort_nr1,k.signifikanz FROM kollok_nb k WHERE k.signifikanz > #ARG# and k.wort_nr1 > #ARG# and k.wort_nr2 = #ARG# order by k.signifikanz desc limit #ARG#;";
    return getKollokationen(query, wortNr, minSignifikanz, minWortnummer, maxKollokationen);
  }

  /**
   * Returns a set containing solely the collocations, without significances
   */
  protected Set getKollokationen(String query, Integer wortNr, int minSignifikanz, int minWortnummer, int maxKollokationen)
  {
    String[] s = new String[4];
    s[0] = "" + minSignifikanz;
    s[1] = "" + minWortnummer;
    s[2] = wortNr.toString();
    s[3] = "" + maxKollokationen;
    String[][] ret = getResultsOf(query, s);
    Set set = new HashSet();
    for ( int i = 0 ; i < ret.length ; i++ )
    {
      set.add(new Integer(ret[i][0]));
    }
    return set;
  }

  /**
   * Overridden toString method to get some status information as well.
   */
  public String toString()
  {
    return "DBConnection "+super.toString();
  }

}
