package com.bordag.sc;

// app specific imports
//import com.bordag.sgz.util.*;
import de.wortschatz.util.*;
import com.bordag.sgz.util.Options;
import com.bordag.sc.spreadingActivation.*;

// standard imports
import java.util.*;
import java.io.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SpellCheckerCounterConsole extends Thread
{

  /**
   * The reference to the connection to the DB
   */
  private DBConnection connection = null;

  /**
   * Reference to the object which simulates spreading activation
   */
  private SpreadingCounterActivation spreading = null;

  /**
   * Constructs an instance of the console if the connection was valid.
   * @param connection
   */
  public SpellCheckerCounterConsole(DBConnection connection)
  {
    this.connection = connection;
    this.spreading = new SpreadingCounterActivation(this.connection, 4.0);
    this.start();
  }

  /**
   * Runs and waits for userinput
   * @todo: Preparse String to remove all kinds of unneccessary Strings like
   * :.;/()"" and so on, double spaces
   **/
  public void run()
  {
    while ( true )
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      String newCommand = null;
      try
      {
        System.out.print("\nsc : ");
        newCommand = in.readLine();
      }
      catch ( Exception ex )
      {
        ex.printStackTrace();
        System.exit(0);
      }
      executeCommand(newCommand);
      this.spreading = new SpreadingCounterActivation(this.connection,4.0);
    }
  }

  /**
   * Tries to recognize and execute the given command
   */
  private void executeCommand(String command)
  {
    if ( command.length() < 1 )
    {
      return;
    }
    StringTokenizer tokenizer = new StringTokenizer(command, " ");
    // go through sentence and do spellchecking.
    while ( tokenizer.hasMoreTokens() )
    {
      String curWord = (String)tokenizer.nextToken();
      Integer wordNr = this.connection.getNumberForWord(curWord);
      //System.out.println("word: "+curWord+" got number "+wordNr);
      // if word exists and is not stopword, process spreading activation on it
      if ( wordNr != null )
      {
        this.spreading.nextWordNr(wordNr, curWord);
        System.out.print("["+curWord+"]");
      }
      if ( wordNr == null )
      {
        String correctedWord = this.spreading.correct(curWord);
        System.out.print(" "+correctedWord);
      }
      // go through sentence and do spellchecking.
      // The state governors met with their respective le***latures
    }

  }

  public static void main(String[] args)
  {
    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

/*    String url = "jdbc:mysql://woclu2.informatik.uni-leipzig.de/en";
    String user = "sbordag";
    String passwd = "ansiBla";
*/
    DBConnection connection = null;
    try
    {
      connection = new DBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      System.out.println("Could not establish connection, exiting.");
      System.exit(0);
    }//The state governors met with their respective lexxxlatures
    SpellCheckerCounterConsole console = new SpellCheckerCounterConsole(connection);
  }
}
