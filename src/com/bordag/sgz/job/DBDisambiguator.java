package com.bordag.sgz.job;

import java.util.*;

import com.bordag.sgz.util.*;

/**
 * Creates n connections and n disambiguating Threads where each thread gets
 * it's own connection. If a thread is done it calls threadDone(thread).
 * Then this thread is removed from the Vector which contains it (this the
 * connection x is not used anymore and thus is given to the next created thread)
 * A new thread is created, the x connection is given to it and added to the
 * Vector containing the thread at the place where the former thread has left
 * (synchronized)
 */
public class DBDisambiguator implements Runnable
{
  private Thread myThread = null;

  private Vector disThreads = null;
  private Vector DBConnections = null;

  public static final String TABLE_NAME = "disamb_sig";
  public static final String FIELD_NAME1 = "wort_nr1";
  public static final String FIELD_NAME2 = "wort_nr2";
  public static final String FIELD_NAME3 = "wort_grp";

  /**
   * The maximum number of threads which are allowed to run at any time
   */
  private final static int maxThreads = 6;

  /**
   * Creates connections for all threads to come.
   * Creates then the threads and starts them.
   */
  public DBDisambiguator()
  {
    this.disThreads = new Vector();
    this.DBConnections = new Vector();

    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    checkPresenceOfTable(url,user,passwd);

    for ( int i = 0 ; i < this.maxThreads ; i++ )
    {
      // new connection
      DBConnection connection = null;
      try{connection = new CachedDBConnection(url, user, passwd);}catch(Exception ex)
      {
        ex.printStackTrace();
        Output.println("Could not establish connection, exiting.");
        System.exit(0);
      }
      this.DBConnections.add(i, connection);
    }

    this.myThread = new Thread(this);
    this.myThread.start();

    for ( int i = 0 ; i < this.maxThreads ; i++ )
    {
      // new thread with his connection
      DisThread disThread = new DisThread((DBConnection)this.DBConnections.get(i), this);
      // put thread and connection into vectors
      this.disThreads.add(i, disThread);
    }

  }

  /**
   * Connected to the DB specified via the arguments and checks, whether the
   * table needed is available.
   * If not, creates the table.
   * NOTE: Exits program if not allowed to create table.
   */
  protected void checkPresenceOfTable(String url, String user, String passwd)
  {
    DBConnection dbConnection = null;
    try {dbConnection = new CachedDBConnection(url, user, passwd);}
    catch (Exception ex) {
      ex.printStackTrace();
      Output.println("Could not establish connection, exiting.");
      System.exit(0);
    }

    String query = "create table if not exists disamb_sig (wort_nr1 mediumint(8) unsigned not null, wort_nr2 mediumint(8) unsigned not null, wort_grp mediumint(8) unsigned not null, primary key (wort_nr1, wort_nr2, wort_grp));";
    boolean retVal = false;
    try
    {
      retVal = dbConnection.execute(query);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
    if ( retVal )
    {
      System.out.println("Table created.");
    }
    else
    {
      System.out.println("Table present.");
    }
  }

  /**
   * Is called by a thread to indicate that it finished so that another one can
   * be started
   * @param disThread which Thread has finished work
   */
  public void threadDone(DisThread disThread)
  {
    synchronized ( this.getClass() )
    {
      int threadNum = this.disThreads.indexOf(disThread);
      this.disThreads.remove(threadNum);
      ((CachedDBConnection)this.DBConnections.get(threadNum)).clearCache();
      DisThread newThread = new DisThread((DBConnection)this.DBConnections.get(threadNum), this);
      this.disThreads.add(threadNum, newThread);

    }
  }

  /**
   * Just not to let this thread die.
   */
  public void run()
  {
    while ( true )
    {
      try
      {
        this.myThread.sleep(100);
      }
      catch ( Exception ex )
      {
        ex.printStackTrace();
      }
    }
  }

  public static void main(String[] args)
  {
    DBDisambiguator dis = new DBDisambiguator();
  }
}