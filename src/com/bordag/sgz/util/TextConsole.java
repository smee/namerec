package com.bordag.sgz.util;

// app specific imports
import com.bordag.sgz.algorithms.*;
import com.bordag.sgz.sachgebiete.*;
import com.bordag.sgz.graphen.*;

// standard imports
import java.util.*;
import java.io.*;

/**
 * Represents the textual console for the program, which can call the
 * different algorithms with different parameter settings
 *
 * @todo: Hasty implementation, not neccessarily as stable as I thought, there
 *        must be some better implementation somewhere to find for such things
 *
 * @author  Stefan Bordag
 * @date    02.04.2002
 */
public class TextConsole extends Thread
{

  /**
   * The reference to the connection to the DB
   */
  private DBConnection connection = null;

  /**
   * Sole constructor, requires a connection to the DB as nothing will work
   * without it
   **/
  public TextConsole(DBConnection connection)
  {
    if ( connection == null )
    {
      Output.println("Null connection in TextConsole!");
      System.exit(0);
    }
    this.connection = connection;
    init();
  }

  /**
   * Just starts the Thread
   * @todo: Add checking of the ini File
   **/
  private void init()
  {
    this.start();
  }

  /**
   * Runs and waits for userinput
   **/
  public void run()
  {
    while ( true )
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      String newCommand = null;
      try
      {
        Output.print("sgz : ");
        newCommand = in.readLine();
      }
      catch ( Exception ex )
      {
        ex.printStackTrace();
        System.exit(0);
      }
      executeCommand(newCommand);
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
    String commandName = tokenizer.nextToken();

    if ( commandName == null )
    {
      return ;
    }
    if ( commandName.equalsIgnoreCase("mysql") )
    {
      performMySqlQuery(tokenizer);
    }
    else if ( commandName.equalsIgnoreCase("Sachgebiet") ||
         commandName.equalsIgnoreCase("sach") ||
         commandName.equalsIgnoreCase("s") )
    {
      performSachgebiete(tokenizer);
    }
    else if ( commandName.equalsIgnoreCase("Disambiguate") ||
         commandName.equalsIgnoreCase("dis") ||
         commandName.equalsIgnoreCase("d") )
    {
      performDisambiguate(tokenizer);
    }
    else if ( commandName.equalsIgnoreCase("Triangle") ||
              commandName.equalsIgnoreCase("tri"))
    {
      performTri(tokenizer);
    }
    else if ( commandName.equalsIgnoreCase("reset") )
    {
      Options.getInstance().printOptions();
    }
    else if ( commandName.equalsIgnoreCase("Quit") ||
              commandName.equalsIgnoreCase("exit") ||
              commandName.equalsIgnoreCase("bye") )
    {
      Output.println("bye bye");
      System.exit(0);
    }
    else if ( commandName.equalsIgnoreCase("help") )
    {
      printHelp();
    }
    else if ( tokenizer.countTokens() >= 2 )
    {
      performInputTri(commandName,tokenizer);
    }
    else
    {
      Output.println("Unknown command.");
      printHelp();
    }
  }

  /**
   * To test some data of query, directly run some mysql command from here, this
   * is some really primitive mysql client, thus.
   */
  private void performMySqlQuery(StringTokenizer tokenizer)
  {
    String query = "";
    while ( tokenizer.hasMoreTokens() )
    {
      query = query+" "+tokenizer.nextToken();
    }
    ComparableStringBuffer[][] results = null;
    try
    {
      Output.println("query would be ["+query+"]");
      results = this.connection.getResultsOf(query);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
    if ( results == null )
    {
      Output.println("Resultset contained no data.");
      return;
    }
    for ( int i = 0 ; i < results.length ; i++ )
    {
      for ( int j = 0 ; j < results[i].length ; j++ )
      {
        Output.print(results[i][j]+"\t");
      }
      Output.println();
    }
  }

  /**
   * Performs sachgebiete on the given sachgebiet
   **/
  private void performSachgebiete(StringTokenizer tokenizer)
  {
    if ( ! tokenizer.hasMoreTokens() )
    {
      return;
    }
    if ( tokenizer.countTokens() <= 1 )
    {
      String next = tokenizer.nextToken();
      if ( next == null || next.length() < 1 )
      {
        return;
      }
      ComparableStringBuffer word = new ComparableStringBuffer(next);
      SachDisEachDefWord sach = new SachDisEachDefWord(this.connection, word);
    }
    else
    {
      HashSet set = new HashSet();
      while ( tokenizer.hasMoreTokens() )
      {
        set.add( new ComparableStringBuffer(tokenizer.nextToken()) );
      }
      SachDisEachDefWord sach = new SachDisEachDefWord(this.connection, set);
    }
  }

  /**
   * Performs the disambiguation operation
   **/
  private void performDisambiguate(StringTokenizer tokenizer)
  {
    if ( ! tokenizer.hasMoreTokens() )
    {
      return;
    }
    String next = tokenizer.nextToken();
    if ( next == null || next.length() < 1 )
    {
      return;
    }
    ComparableStringBuffer word = new ComparableStringBuffer(next);
    NonThreadedDisambiguator dis = new NonThreadedDisambiguator(this.connection, word);
    dis.runAlgorithm();
    Output.println("Following words could not be clustered:");
    dis.classifiedWordsList.printUnclusteredWords();
    Output.println("Following clusters found:");
    dis.getCluster().printClusterVectors(dis.getConnection());
    Output.println();
  }

  private void performTri(StringTokenizer tokenizer)
  {
    String fakeCommand = tokenizer.nextToken();
    performInputTri(fakeCommand, tokenizer);
  }

  private void performInputTri(String command,StringTokenizer tokenizer)
  {
    HashSet inputWords = new HashSet();

    int i = 0;
    if ( command != null )
    {
      inputWords.add(new ComparableStringBuffer(command));
      i++;
    }
    while ( tokenizer.hasMoreTokens() )
    {
      inputWords.add(new ComparableStringBuffer(tokenizer.nextToken()));
      i++;
    }
/*    Vector inWordsSorted = new Vector(inputWords);
    Collections.sort(inWordsSorted);
    Output.println("Input : "+inWordsSorted);*/
    Output.println("Input : "+inputWords);
    Set inputNumbers = this.connection.getNumbersForWords(inputWords);

    NonThreadTriangles triangles = new NonThreadTriangles(this.connection, inputNumbers);
    Set resultNumbers = triangles.getResults();
    Output.print("Results : "+this.connection.getWordsForNumbers(resultNumbers));
    SubGraph graph = new SubGraph(this.connection, resultNumbers);
    Output.println(" c = "+graph.calculateCliquishness());
  }

  /**
   * Prints the help
   * @todo: maybe read helptext from some recourcebundle or the like
   **/
  private void printHelp()
  {
    Output.println("");
    Output.println("     Trigram Calculation");
    Output.println("");
    Output.println("  word1 word2 word3 ....      : calculates triangle ");
    Output.println("  (minimum 3 words)             for given words");
    Output.println("  dis word                    : disambiguates word using parallel disambiguation");
    Output.println("  sach word                   : starts the sachgebiete algorithm on the given sachgebiet");
    Output.println("  exit                        : exits");
    Output.println("");
  }

  /**
   * For debugging purposes
   **/
  public static void main(String argv[])
  {
//    TextConsole cons = new TextConsole();
  }

}
