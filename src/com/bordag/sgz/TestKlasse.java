package com.bordag.sgz;

// app specific imports
import com.bordag.sgz.util.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class TestKlasse
{
  public TestKlasse()
  {
    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    DBConnection connection = null;
    try
    {
      connection = new CachedDBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      Output.println("Could not establish connection, exiting.");
      System.exit(0);
    }

    for ( int i = 1 ; i < 100000 ; i=i+10 )
    {
      String query = "select count(*) from kollok_sig where wort_nr2 < 100000 and wort_nr1="+i+";";
      ComparableStringBuffer[][] buf = connection.executeQuery(query);
      System.out.println(i+"\t"+buf[0][0]);
    }

  }

  /**
   * Main method, just creates a new instance of this class, ignores as yet
   * all arguments.
   */
  public static void main(String argv[])
  {
    TestKlasse db = new TestKlasse();
  }

}