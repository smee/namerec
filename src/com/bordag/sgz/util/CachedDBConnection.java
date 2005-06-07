package com.bordag.sgz.util;

// standard imports
import java.util.*;
import java.sql.*;
import java.io.*;


/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class CachedDBConnection extends DBConnection
{
  public Hashtable collocationsCache = null;

  public CachedDBConnection()
  {
    init();
  }

  /**
   * Construct an instance of this Object which can then execute queries on
   * the DB
   */
  public CachedDBConnection(String URL, String user, String passwd) throws ClassNotFoundException, IllegalAccessException, SQLException, InstantiationException
  {
    super(URL, user, passwd);
    init();
  }

  /**
   * Note: This method is also called if clearCache is called
   */
  private void init()
  {
    this.collocationsCache = new Hashtable();
  }

//------------------ public methods ----------------------------

  /**
   * Creates a signle statement which retrieves collocations for each of the
   * given words
   */
  public void cacheCollocationsOf(ComparableStringBuffer inputWordNr, List wordNrs) throws SQLException
  {
    HashSet set = new HashSet();
    for ( Iterator it = wordNrs.iterator() ; it.hasNext() ; )
    {
      set.add(it.next());
    }
    this.collocationsCache.put(inputWordNr, set);

    Statement statement = this.connection.createStatement();
    String query = "(";
    int i = 0;
    for ( Iterator it = wordNrs.iterator() ; it.hasNext() ;  )
    {
      ComparableStringBuffer curWordNr = new ComparableStringBuffer( it.next().toString() );

      String[] args = new String[4];
      args[0] = Options.getInstance().getTriMinSignifikanz();
      args[1] = Options.getInstance().getTriMinWordNr();
      args[2] = curWordNr.toString();
      args[3] = Options.getInstance().getTriMaxKollokationen();
      String tempQuery = Options.getInstance().getTriQueryKollokationen().replace(';',' ');
      tempQuery = addArguments(tempQuery, args);
      if (it.hasNext() )
      {
        query = query + tempQuery + ") UNION ( SELECT 0,"+i+" ) UNION (";
      }
      else
      {
        query = query + tempQuery + ");";
      }
      i++;
    }
    ResultSet resultSet = statement.executeQuery(query);
    putToHash(wordNrs, resultSet);
  }

  /**
   * Transforms the ResultSet into the Hash which represents the cache
   */
  protected void putToHash(List wordNrs, ResultSet resultSet) throws SQLException
  {
    int dataColumns = resultSet.getMetaData().getColumnCount();

    int dataRows = 0;
    if ( resultSet.wasNull() ) { return ; }
    try
    {
      while ( resultSet.next() ) { dataRows++; }
    }
    catch ( SQLException ex ) { return ; }
    resultSet.beforeFirst();


    int rows = 0;
    int columns = 1;

    Iterator it = wordNrs.iterator();

    Set collocations = new HashSet();
    int i = 0;
    while (resultSet.next())
    {
      ComparableStringBuffer temp = new ComparableStringBuffer(resultSet.getString(1));
      //System.out.print(" "+temp);
      if ( temp.equals(new ComparableStringBuffer(new String("0"))) )
      {
        //System.out.println("Putting "+collocations.size());
        Object key = it.next();
        if ( ! this.collocationsCache.containsKey(key) )
        {
          this.collocationsCache.put(key, collocations);
        }
        collocations = new HashSet();
      }
      else
      {
        collocations.add(temp);
      }
      i++;
    }
    //System.out.println("Cache contains now : ["+collocationsCache.size()+"] elements");
  }

  /**
   * Clears off everything from the cache.
   */
  public void clearCache()
  {
    this.collocationsCache.clear();
    init();
  }

  /**
   * Returns the collocations of 'element' for the trigram calculation
   */
  public ComparableStringBuffer[][] getTriAssociatedOf(ComparableStringBuffer element)
  {
    if (this.collocationsCache.containsKey(element))
    {
      Set set = (Set)this.collocationsCache.get(element);
      ComparableStringBuffer[][] retVal = new ComparableStringBuffer[set.size()][1];
      int i = 0;
      for ( Iterator it = set.iterator() ; it.hasNext() ; )
      {
        retVal[i][0] = new ComparableStringBuffer(it.next().toString());
        i++;
      }
      return retVal;
    }
    else
    {
      return super.getTriAssociatedOf(element);
    }
  }

  public Set getDisCollocationsOf(ComparableStringBuffer wordNr)
  {
    Output.println("CachedDBConnection.getDisCollocationsOf(wordNr): This method is not yet implemented.");
    return null;
  }

  public Set getTriCollocationsOf(ComparableStringBuffer wordNr)
  {
    Output.println("CachedDBConnection.getTriCollocationsOf(wordNr): This method is not yet implemented.");
    return null;
  }

//------------------ private methods ----------------------------

  /**
   * Replaces the #ARG# strings in the query with the given arguments
   */
  private String addArguments(String query, String[] args) throws IllegalArgumentException
  {
    if ( query == null || args == null || query.length() < 1 || args.length < 1 )
    {
      throw new IllegalArgumentException("In CachedDBConnection.getResultsOf(String, String[]) query or arguments are invalid.");
    }
    query = removeNewLines(query);
    int beginReplace = 0;
    int endReplace = 0;
    for ( int i = 0 ; i < args.length ; i++ )
    {
      beginReplace = query.indexOf(this.ARGUMENT_PLACE);
      endReplace = query.indexOf(this.ARGUMENT_PLACE) + this.ARGUMENT_PLACE.length();
      if ( args[i] == null || args[i].length() < 1 )
      {
        throw new IllegalArgumentException("In CachedDBConnection.getResultsOf(String, String[]) Argument["+i+"] is invalid!");
      }
      try
      {
        query = query.substring(0, beginReplace) + args[i] + query.substring(endReplace, query.length());
      }
      catch(Exception ex)
      {
        String temp = "";
        for ( int j = 0 ; j < args.length ; j++ )
        {
          temp = temp+" "+args[j];
        }
        throw new IllegalArgumentException("In DBConnection.getResultsOf(String, String[]) Query ["+query+"] was used with wrong number of arguments: "+temp);
      }
    }
    return query;
  }
}