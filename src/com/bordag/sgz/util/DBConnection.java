package com.bordag.sgz.util;

// standard imports
import java.util.*;
import java.sql.*;
import java.io.*;

/**
 * This class represents a connection to a DB and can receive and process
 * queries.
 *
 * @author    Stefan Bordag
 * @date      28.12.2001
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
//    try
//    {
      this.driver = Class.forName(Options.getInstance().getConDriver()).newInstance();
//      this.driver = Class.forName("org.gjt.mm.mysql.Driver").newInstance();
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
//    }
//    catch (Exception ex)
//    {
//      Output.println("Couldn't establish connection to DB ");
//      ex.printStackTrace();
//      //System.exit(-1);
//    }
  }

  /**
   * Loads the mysql driver or exits
   */
  private void loadDriver() throws ClassNotFoundException, IllegalAccessException, InstantiationException
  {
//    try
//    {
      this.driver = Class.forName(Options.getInstance().getConDriver()).newInstance();
      System.out.println(driver.toString());
//    }
//    catch (Exception e)
//    {
//      Debugger.getInstance().println("JDBC-Treiber konnte nicht geladen werden.\n",1);
//      e.printStackTrace();
//      System.exit(-1);
//    }

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
  public ComparableStringBuffer[][] getResultsOf(String query, String[] queryArguments) throws IllegalArgumentException
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
    return getResultsOf(query);
  }


  /**
   * Checks whether we are connected, if not we simulate data and let a warning out
   */
  public ComparableStringBuffer[][] getResultsOf(String query) throws IllegalArgumentException
  {
    if ( this.connection == null )
    {
      Output.println("DBConnection: Warning, not connected to DB, falling back to simulation.");
      return executeQuerySimulated(query);
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
   */
  private ComparableStringBuffer[][] executeQuerySimulated(String query) throws IllegalArgumentException
  {
    if ( query == null || query.length() < 1 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String) query is invalid.");
    }
    if ( query.indexOf(this.ARGUMENT_PLACE) >= 0 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String) query contains replaceable Arguments, you should call DBConnection.getResultsOf(String, String[]) instead!");
    }
    ComparableStringBuffer[][] retVal = new ComparableStringBuffer[2][3];
    if ( query.indexOf("Seenplatte") > 0 )
    {
      retVal[0][0] = new ComparableStringBuffer("1");
      retVal[1][0] = new ComparableStringBuffer("s1");
      retVal[0][1] = new ComparableStringBuffer("2");
      retVal[1][1] = new ComparableStringBuffer("s2");
      retVal[0][2] = new ComparableStringBuffer("3");
      retVal[1][2] = new ComparableStringBuffer("s3");
    }
    return retVal;
  }

  /**
   * Returns the results from executing the given batch of valid SQL statements
   * or throws an Exception
   */
  private ComparableStringBuffer[][] executeBatch(String[] batch)
  {
    if ( batch == null || batch.length < 1 )
    {
      throw new IllegalArgumentException("In DBConnection.executeBatch(String) query batch is empty.");
    }
    ComparableStringBuffer[][] retVal = null;
    try
    {
      Statement statement = this.connection.createStatement();
      for ( int i = 0 ; i < batch.length ; i++ )
      {
        if ( batch[i] == null || batch.length < 1 )
        {
          throw new IllegalArgumentException("In DBConnection.executeBatch(String) batch query "+i+" is empty.");
        }
        statement.addBatch(batch[i]);
      }

      int[] updateCount = statement.executeBatch();
      for ( int i = 0 ; i < updateCount.length ; i++ )
      {
        ResultSet resultSet = statement.getResultSet();
        statement.getMoreResults();
        System.out.println("Got for "+batch[i]+" "+updateCount[i]+" results.");
       // resultSet.
      }

/*      int dataColumns = resultSet.getMetaData().getColumnCount();

      int dataRows = 0;
      if ( resultSet.wasNull() ) { return null; }
      try
      {
        while ( resultSet.next() ) { dataRows++; }
      }
      catch ( SQLException ex ) { return null; }
      resultSet.beforeFirst();

      retVal = new ComparableStringBuffer[dataRows][dataColumns];
      int rows = 0;
      int columns = 1;

      while (resultSet.next())
      {
        for ( columns = 1 ; columns < dataColumns+1 ; columns++ )
        {
          retVal[rows][columns-1] = new ComparableStringBuffer(resultSet.getString(columns));
        }
        rows++;
      }*/
    }
    catch ( Exception ex )
    {
      Debugger.getInstance().println("Error processing batch.",1);
      ex.printStackTrace();
    }
    return retVal;
  }

  /**
   * Returns a String array containing the resulting table from the query, null
   * if there was some problem with the query
   * The array is stored in a way so that telling array[0] gives you all the
   * words, array[1] significance or whatever and so on
   */
  public ComparableStringBuffer[][] executeQuery(String query)
  {
    if ( query == null || query.length() < 1 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String) query is invalid.");
    }
    if ( query.indexOf(this.ARGUMENT_PLACE) >= 0 )
    {
      throw new IllegalArgumentException("In DBConnection.getResultsOf(String) query contains replaceable Arguments, you should call DBConnection.getResultsOf(String, String[]) instead!");
    }
    ComparableStringBuffer[][] retVal = null;
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

      retVal = new ComparableStringBuffer[dataRows][dataColumns];
      int rows = 0;
      int columns = 1;

      while (resultSet.next())
      {
        for ( columns = 1 ; columns < dataColumns+1 ; columns++ )
        {
          retVal[rows][columns-1] = new ComparableStringBuffer(resultSet.getString(columns));
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


  /**
   * Returns the word from the database which is associated with the given number
   */
  public ComparableStringBuffer getWordForNumber(ComparableStringBuffer number)
  {
    String query = Options.getInstance().getGenQueryNumber2Word();
    String[] args = new String[1];
    args[0] = number.toString();
    ComparableStringBuffer[][] temp = this.getResultsOf(query,args);
    if ( temp != null )
    {
      return temp[0][0];
    }
    return null;
  }

  /**
   * Returns the word from the database which is associated with the given number
   */
  public ComparableStringBuffer getNumberForWord(ComparableStringBuffer word)
  {
    String query = Options.getInstance().getGenQueryWord2Number();
    String[] args = new String[1];
    args[0] = word.toString();
    ComparableStringBuffer[][] temp = this.getResultsOf(query, args);
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      return temp[0][0];
    }
    return null;
  }

  /**
   * Returns the word from the database which is associated with the given number
   * @todo : move this method to where it belongs. where?
   */
  public ComparableStringBuffer getNumberForSachgebiet(ComparableStringBuffer word)
  {
    String query = Options.getInstance().getSachQuerySachNr();
    String[] args = new String[1];
    args[0] = word.toString();
    ComparableStringBuffer[][] temp = this.getResultsOf(query,args);
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      return temp[0][0];
    }
    return null;
  }

  /**
   * Gets an array of words and returns the same array, just with
   * wordnumbers instead
   **/
  public ComparableStringBuffer[] getNumbersForWords(ComparableStringBuffer[] words)
  {
    if ( words == null || words.length < 1 )
    {
      return words;
    }
    for ( int i = 0 ; i < words.length ; i++ )
    {
      words[i] = getNumberForWord(words[i]);
    }
    return words;
  }

  /**
   * Returns the strings of words (because the one in DBConnection orders them,
   * thus loosing the connection to the wordnumber
   * @param numbers
   * @return
   */
  public ComparableStringBuffer[] getWordsForNumbersSameOrder(Object[] numbers)
  {
    Arrays.sort(numbers, new ComparableStringBuffer(""));
    if ( numbers == null || numbers.length < 1 )
    {
      return null;
    }
    String queryBegin = "select w.wort_bin from wortliste w where ";
    String queryMid1   = "w.wort_nr=";
    String queryMid2   = " or ";
    String queryEnd    = " order by w.wort_nr asc;";
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
    ComparableStringBuffer[][] temp = this.getResultsOf(query);
    ComparableStringBuffer[] temp2 = new ComparableStringBuffer[temp.length];
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
   * Returns the strings of words (because the one in DBConnection orders them,
   * thus loosing the connection to the wordnumber
   * @param numbers
   * @return
   */
  public ComparableStringBuffer[] getNumbersForWordsSameOrder(Object[] words)
  {
    Arrays.sort(words, new ComparableStringBuffer(""));
    if ( words == null || words.length < 1 )
    {
      return null;
    }
    String queryBegin = "select w.wort_nr from wortliste w where ";
    String queryMid1   = "w.wort_bin=\"";
    String queryMid2   = "\" or ";
    String queryEnd    = "\" group by wort_bin order by w.wort_bin asc;";
    String query = queryBegin;
    for ( int i = 0 ; i < words.length ; i++ )
    {
      query = query + "" + queryMid1 + words[i].toString();
      if ( i < words.length - 1 )
      {
        query = query + queryMid2;
      }
    }
    query = query + "" + queryEnd;
    ComparableStringBuffer[][] temp = this.getResultsOf(query);
    ComparableStringBuffer[] temp2 = new ComparableStringBuffer[temp.length];
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
   * Gets an array of words and returns the same array, just with
   * wordnumbers instead
   * select w.wort_bin from wortliste w where
   *    w.wort_nr=1
   * or w.wort_nr=2
   * or w.wort_nr=3
   * order by w.wort_bin asc;
   **/
  public ComparableStringBuffer[] getWordsForNumbers(Object[] numbers)
  {
    if ( numbers == null || numbers.length < 1 )
    {
      return null;
    }
    String queryBegin = "select w.wort_bin from wortliste w where ";
    String queryMid1   = "w.wort_nr=";
    String queryMid2   = " or ";
    String queryEnd    = " order by w.wort_bin asc;";
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
    //System.out.println("Query: ["+query+"]");
    ComparableStringBuffer[][] temp = this.getResultsOf(query);
    ComparableStringBuffer[] temp2 = new ComparableStringBuffer[temp.length];
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
   * Gets an array of words and returns the same array, just with
   * wordnumbers instead
   **/
  public Set getNumbersForWords(Set words)
  {
    if ( words == null || words.size() < 1 )
    {
      return words;
    }
    HashSet set = new HashSet();
    // add conversion to- and from Array...0
    for ( Iterator it = words.iterator() ; it.hasNext() ; )
    {
      set.add(getNumberForWord((ComparableStringBuffer)it.next()));
    }
    return set;
  }

  /**
   * Gets an array of words and returns the same array, just with
   * wordnumbers instead
   **/
  public Set getWordsForNumbers(Set numbers)
  {
    if ( numbers == null || numbers.size() < 1 )
    {
      return numbers;
    }
    Object[] arr = new Object[numbers.size()];
    int i = 0;
    for ( Iterator it = numbers.iterator() ; it.hasNext() ; )
    {
      arr[i] = it.next();
      i++;
    }
    Object[] arr2 = this.getWordsForNumbers(arr);
    Set words = new HashSet();
    for ( int j = 0 ; j < arr2.length ; j++ )
    {
      words.add(arr2[j]);
    }
    TreeSet setSorted = new TreeSet(words);
    return setSorted;
  }

  /**
   * Returns how many time a given word as a number in the DB has been seen.
   */
  public int getFrequencyOfWordNr(ComparableStringBuffer wordNr)
  {
    if ( wordNr == null )
    {
      return -1;
    }
    String query = Options.getInstance().getGenQueryFrequency();
    String[] args = new String[1];
    args[0] = wordNr.toString();
    ComparableStringBuffer[][] temp = this.getResultsOf(query, args);
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      return new Integer(temp[0][0].toString()).intValue();
    }
    return -2;
  }

  /**
   * Retuns all those elements which are associated with the given one along
   * with extra info, if any.
   */
  public ComparableStringBuffer[][] getTriAssociatedOf(ComparableStringBuffer element)
  {
    if ( this.connection != null )
    {
      String[] s = new String[4];
      s[0] = Options.getInstance().getTriMinSignifikanz();
      s[1] = Options.getInstance().getTriMinWordNr();
      s[2] = element.toString();
      s[3] = Options.getInstance().getTriMaxKollokationen();
      return getResultsOf(Options.getInstance().getTriQueryKollokationen(), s);
    }
    return null;
  }

  /**
   * Resolves a sachgebiet into the nr of the sachgebiet
   */
  protected String getSachNrForString(ComparableStringBuffer string)
  {
    String query = Options.getInstance().getSachQuerySachNr();
    String[] args = new String[1];
    args[0] = string.toString();
    ComparableStringBuffer[][] temp = getResultsOf(query,args);
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      return temp[0][0]+"";
    }
    return null;
  }

  /**
   * Returns a set containing the wordnumbers which belong to the given
   * sachgebiet
   */
  public Set getSachgebietsWordNumbers(ComparableStringBuffer sachgebiet)
  {
    HashSet retSet = new HashSet();
    String[] s = new String[3];
    s[0] = sachgebiet.toString();
    s[1] = Options.getInstance().getMinWordFreq();
    s[2] = Options.getInstance().getSachMaxDefiningWords();
    ComparableStringBuffer[][] buffer = getResultsOf(Options.getInstance().getSachQuerySachgebiete(),s);
    if ( buffer == null || buffer.length < 1 )
    {
      return retSet;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        retSet.add(buffer[i][0]);
      }
    }
    return retSet;
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
