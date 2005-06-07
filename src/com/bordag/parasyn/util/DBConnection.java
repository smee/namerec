package com.bordag.parasyn.util;

// standard imports
import java.util.*;
import java.sql.*;
import java.io.*;

import com.bordag.util.*;

/**
 * This class represents a connection to a DB and can receive and process
 * queries.
 *
 * @author    Stefan Bordag
 * @date      26.09.2003
 */
public class DBConnection implements java.io.Serializable
{
  /**
   * The real connection around which this class is wrapped
   */
  protected transient Connection connection = null;

  /**
   * The driver used to connect to the DB
   */
  private Object driver = null;

  /**
   * The string for which this class will look to replace with query arguments
   */
  public static final String ARGUMENT_PLACE = "#ARG#";

  /**
   * Empty constructor for offline usage, will simulate incoming data
   */
  public DBConnection()
  {
  }

  /**
   * Construct an instance of this Object which can then execute queries on
   * the DB
   */
  public DBConnection(String URL, String user, String passwd) throws ClassNotFoundException, IllegalAccessException, SQLException, InstantiationException
  {
    loadDriver();
    this.driver = Class.forName(Options.getInstance().getConDriver()).newInstance();
    Output.println("Connecting to DB.");
    Output.println("Installed drivers: ");
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

  /**
   * Loads the mysql driver or exits
   */
  private void loadDriver() throws ClassNotFoundException, IllegalAccessException, InstantiationException
  {
    this.driver = Class.forName(Options.getInstance().getConDriver()).newInstance();
    System.out.println(driver.toString());
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
  public CHString[][] getResultsOf(String query, String[] queryArguments) throws IllegalArgumentException
  {
    if ( query == null || queryArguments == null || query.length() < 1 || queryArguments.length < 1 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String, String[]) query is invalid.");
    }
    query = removeNewLines(query);
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
    return executeQuery(query);
  }

  protected String removeNewLines(String string)
  {
    string = string.replace('\n',' ');
    return string;
  }

  /**
   * Returns a String array containing the resulting table from the query, null
   * if there was some problem with the query
   * The array is stored in a way so that telling array[0] gives you all the
   * words, array[1] significance or whatever and so on
   */
  public CHString[][] executeQuery(String query)
  {
    if ( query == null || query.length() < 1 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String) query is invalid.");
    }
    if ( query.indexOf(this.ARGUMENT_PLACE) >= 0 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String) query contains replaceable Arguments, you should call DBConnection.getResultsOf(String, String[]) instead!");
    }
    CHString[][] retVal = null;
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

      retVal = new CHString[dataRows][dataColumns];
      int rows = 0;
      int columns = 1;

      while (resultSet.next())
      {
        for ( columns = 1 ; columns < dataColumns+1 ; columns++ )
        {
          retVal[rows][columns-1] = new CHString(resultSet.getString(columns));
        }
        rows++;
      }
    }
    catch ( Exception ex )
    {
      Debugger.getInstance().println("Error processing query ["+query+"]",1);
      ex.printStackTrace();
    }
    return retVal;
  }

  /**
   * Executes the given query and returns whether it was successfull
   */
  public boolean execute(String query)
  {
    if ( query == null || query.length() < 1 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String) query is invalid.");
    }
    boolean retVal = false;
    try
    {
      Statement statement = this.connection.createStatement();

      retVal = statement.execute(query);

    }
    catch ( Exception ex )
    {
      Debugger.getInstance().println("Error processing query ["+query+"]",1);
      ex.printStackTrace();
    }
    return retVal;
  }

  public Connection getConnection()
  {
    return this.connection;
  }

  /**
   * Overridden toString method to get some status information as well.
   */
  public String toString()
  {
    if ( this.connection == null )
    {
      return "Simulated DBConnection : "+super.toString();
    }
    return "Online DBConnection"+super.toString();
  }

}