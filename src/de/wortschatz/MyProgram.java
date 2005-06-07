package de.wortschatz;

import java.util.*;
import de.wortschatz.util.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class MyProgram
{

  protected DBConnection connection = null;

  public MyProgram(String u, String p)
  {
    String url = "jdbc:mysql://alf.rz.uni-leipzig.de/wortschatz";
    String user = "yyy";
    if ( u != null )
    {
      user = u;
    }
    String passwd = "yyy";
    if ( p != null )
    {
      passwd = p;
    }

    try
    {
      this.connection = new DBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      System.out.println("Could not establish connection, exiting.");
      System.exit(0);
    }
    testConnection();
  }

  public void testConnection()
  {
    System.out.print("Testing Frequency: ");
    int freq = connection.getAnzForWordNr(new Integer(1000)).intValue();
    System.out.println(freq+" okay");

    System.out.print("Testing getNumberForWord: ");
    int num = connection.getNumberForWord("der").intValue();
    System.out.println(num+" okay");

    System.out.print("Testing getWordForNumber: ");
    String word = connection.getWordForNumber(new Integer(1));
    System.out.println(word+" okay");

    System.out.print("Testing getSatzKollokationen: ");
    Set set = connection.getSatzKollokationen(new Integer(1), 5, 500, 10);
    System.out.println(set+" okay");

    System.out.print("Testing getWordsForNumbers: ");
    Set set2 = connection.getWordsForNumbers(set);
    System.out.println(set2+" okay");

    System.out.print("Testing getNachbarKollokationenLinks: ");
    Set set3 = connection.getNachbarKollokationenLinks(new Integer(1), 5, 500, 10);
    System.out.println(connection.getWordsForNumbers(set3)+" okay");

    System.out.print("Testing getNachbarKollokationenRechts: ");
    Set set4 = connection.getNachbarKollokationenRechts(new Integer(1), 5, 500, 10);
    System.out.println(connection.getWordsForNumbers(set4)+" okay");

}

  /**
   * Main method, just creates a new instance of this class, ignores as yet
   * all arguments.
   */
  public static void main(String argv[])
  {
    if ( argv.length >= 2 )
    {
      MyProgram myProgram = new MyProgram(argv[0], argv[1]);
    }
    else
    {
      MyProgram myProgram = new MyProgram(null, null);
    }
  }
}