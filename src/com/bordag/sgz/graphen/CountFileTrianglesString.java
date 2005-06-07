package com.bordag.sgz.graphen;

import java.util.*;
import java.io.*;

/**
 * First part: Read in the file and build HashSet
 *
 * Second part: Make two BufferedReaders from the file where reader1 is at
 * ith position and reader two is created, seeked to ith position and then
 * always reads from there on up to the point that the number in first column
 * changes -> thus returning all neighbours of ith wordnumber.
 * Thus we have our edge (ith position pair) and all possible zs which we'd like
 * to check which we then do on the HashSet
 *
 * The inputfile can be divided:
 * 1. part words_1 below 50000 and word_2 below 50000
 * 2. part words_1 above 50000 and word_2 above 50000
 * 3. part words_1 below 50000 and word_2 above 50000
 * 4. part words_1 above 50000 and word_2 below 50000
 */

public class CountFileTrianglesString
{
  protected HashSet stringList = null;

  protected String fileName = "C:\\develop\\WortschatzTool\\data\\triangles\\kollok1000.txt";

  public final static String DELIM_STRING = "\t";

  public final static int DELIM_LENGTH = 2;

  protected CountFile cFile = null;

  public CountFileTrianglesString(String fileName)
  {
    if ( fileName != null )
    {
      this.fileName = fileName;
    }
    long curTime = System.currentTimeMillis();

    //this.cFile = new CountFile(this.fileName);
    System.out.print("Creating instance of CountFileCached .... ");
    this.cFile = new CountFile(this.fileName);
    System.out.println("done");
    System.out.print("Loading HashSet as Strings .............. ");
    loadFile();
    System.out.println("done");
    System.out.println("Now counting triangles: ");
    countTriangles();

    long endTime = System.currentTimeMillis();
    System.out.println("Task took: "+((endTime-curTime)/1000)+" seconds");
  }

  protected void loadFile()
  {
    this.stringList = new HashSet(2000000);
    try
    {
      BufferedReader input = new BufferedReader(new FileReader(this.fileName));
      int i = 0 ;
      while ( input.ready() )
      {
        this.stringList.add(input.readLine());
        if (i % 20000 == 0)
        {
          System.out.println("i = " + i);
        }
        i++;
      }
      input.close();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  protected void countTriangles()
  {
    int triangles = 0;
    Integer curWord = null;
    while ( true )
    {
      Set set = cFile.getNextNeighbours();
      if ( set == null || set.size() == 0 )
      {
        break;
      }
      Iterator it = null;
      Pair curPair = null;
      while ( set.size() > 0 )
      {
        it = set.iterator();
        curPair = (Pair)it.next();
        it = null;

        // do searching here
        triangles+=searchTriangles(curPair.myPair[0], curPair.myPair[1], set);
        // remove pair from set?
        set.remove(curPair);
        this.stringList.remove(curPair.myPair[0]+DELIM_STRING+curPair.myPair[1]);
        //System.out.println("tri:"+triangles+" set:"+set);
      }
      if ( curPair.myPair[0].intValue() % 5 == 0 )
      {
        System.out.println("Word: " + curPair.myPair[0] + " found " + triangles + " so far. set had " + set.size() + " elements.");
      }

    }
    System.out.println("Found "+triangles+" triangles as compared to "+maxTriangles(5000,1917692));
  }

  /**
   * Returns a count of all triangles with edges x and y using following procedure:
   * Take HashSet of x - these are all now potential z's
   * Now look in HashSet of each potential z whether y is in there
   */
  private int searchTriangles(Integer x, Integer y, Set potentialZSet)
  {
    int retVal = 0;
    if ( potentialZSet == null ) { return 0; }
    for ( Iterator it = potentialZSet.iterator() ; it.hasNext() ; )
    {
      Pair curPair = (Pair)it.next();
      if ( curPair.myPair[1].equals(y) )//|| curPair.myPair[1].equals(y) )
      {
        //System.out.println("happened with "+x+" and "+y);
      }
      else if ( this.stringList.contains(curPair.myPair[1]+DELIM_STRING+y) )
      {
//        System.out.println("Triangle: ("+x.intValue()+","+curPair.myPair[1].intValue()+","+y.intValue()+")");
        retVal++;
      }
    }
    return retVal;
  }

  /**
   * c = trunc(sqrt( 2*V - sqrt(2*V) ) ) + 1
   */
  public static long maxTriangles(int nodes, int connections)
  {
    long V = connections - nodes + 1;
    long c = (long)Math.floor(Math.sqrt(2*V - Math.sqrt(2*V)))+1;
    long k = c+2;
    long d = V - (c*(c-1))/2;

    long t = ( (k-1)*(k-1)*(k-1) - 3*(k-1)*(k-1) + 2*(k-1) ) / 6 +
            (d*(d+1))/2;

    return t;
  }

  public static void main(String[] args)
  {
    if ( args.length >= 1 )
    {
      CountFileTrianglesString t = new CountFileTrianglesString(args[0]);
    }
    else if ( args.length < 1 )
    {
      CountFileTrianglesString t = new CountFileTrianglesString(
          "C:\\develop\\WortschatzTool\\data\\triangles\\kollok1_1000.txt");
      t = null;
      CountFileTrianglesString t2 = new CountFileTrianglesString(
          "C:\\develop\\WortschatzTool\\data\\triangles\\kollok2_1000.txt");
      t2 = null;
      CountFileTrianglesString t3 = new CountFileTrianglesString(
          "C:\\develop\\WortschatzTool\\data\\triangles\\kollok3_1000.txt");
      t3 = null;
      CountFileTrianglesString t4 = new CountFileTrianglesString(
          "C:\\develop\\WortschatzTool\\data\\triangles\\kollok4_1000.txt");
      t4 = null;
    }
  }


//------------COUNT FILE--------------
  class CountFile
  {
    private BufferedReader reader1 = null;
    private BufferedReader reader2 = null;
    private String fileName = null;
    private String[] lastPair = null;

    public CountFile(String fileName)
    {
      this.fileName = fileName;
      try
      {
        this.reader1 = new BufferedReader(new FileReader(fileName));
        this.reader2 = new BufferedReader(new FileReader(fileName));
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }

    /**
     * Returns the neighbours of the given wordNumber
     * @param wordNum
     * @return
     */
    public Set getNextNeighbours()
    {
      HashSet retSet = new HashSet();
      Integer curWordNr = null;
      try
      {
        String[] s = null;
        while (this.reader2.ready())
        {
          // keep track of the last pair because we always read one too far!
          if ( lastPair != null )
          {
            s = lastPair;
            lastPair=null;
          }
          else
          {
            s = this.reader2.readLine().split(DELIM_STRING);
          }
          // build set
          if ( curWordNr == null )
          {
            retSet.add( new Pair(new Integer(s[0]), new Integer(s[1])) );
            curWordNr = new Integer(s[0]);
          }
          else if ( new Integer(s[0]).equals(curWordNr) )
          {
            retSet.add( new Pair(new Integer(s[0]), new Integer(s[1])) );
          }
          else
          {
            lastPair = s;
            break;
          }
        }
      }
      catch ( Exception ex )
      {
        ex.printStackTrace();
      }
      return retSet;
    }

  }


  /**
   * Stores the collocationsliste as an Integer[2] array and defines the
   * traverse direction through this array.
   * <p>Title: WortschatzTool</p>
   * <p>Description: Abteilungsinternes Entwicklungstool</p>
   * <p>Copyright: Copyright (c) 2003</p>
   * <p>Company: </p>
   * @author not attributable
   * @version 1.0
   */
  class CountFileCached
  {
    private int curPos = 0;
    private Integer[][] table = null;

    public CountFileCached(String fileName)
    {
      try
      {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        int lines = 0;
        while ( reader.ready())
        {
          reader.readLine();
          lines++;
        }
        reader.close();
        this.table = new Integer[lines][2];
        reader = new BufferedReader(new FileReader(fileName));
        int i = 0;
        while ( reader.ready() )
        {
          String[] s = reader.readLine().split(DELIM_STRING);
          this.table[i][0] = new Integer(s[0]);
          this.table[i][1] = new Integer(s[1]);
          i++;
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }

    /**
     * Returns the neighbours of the given wordNumber
     * @param wordNum
     * @return
     */
    public Set getNeighboursOf(Integer wordNum)
    {
      int i = this.curPos-1;
      HashSet retSet = new HashSet();
      while ( true )
      {
        if ( i < this.table.length && this.table[i][0].equals(wordNum) )
        {
          retSet.add(this.table[i][1]);
        }
        else
        {
          break;
        }
        i++;
      }
      return retSet;
    }

    public Integer[] getNextPair()
    {
      if ( this.curPos < table.length )
      {
        Integer[] pair = table[this.curPos];
        this.curPos++;
        return pair;
      }
      return null;
    }
  }

  class Pair
  {
    public Integer[] myPair = null;

    public Pair(Integer x, Integer y)
    {
      myPair = new Integer[2];
      myPair[0] = x;
      myPair[1] = y;
    }

    public Pair(Integer[] xy)
    {
      myPair = xy;
    }

    public int hashCode()
    {
      Random r = new Random( (int)(((double)myPair[0].intValue())/2) + (int)(((double)myPair[1].intValue())/2) );
      return r.nextInt();
    }

    public boolean equals(Object other)
    {
      if ( other instanceof Pair )
      {
        if ( ((Pair)other).myPair[0].equals(myPair[0]) && ((Pair)other).myPair[1].equals(myPair[1]) )
        {
          return true;
        }
      }
      return false;
    }

    public String toString()
    {
      return "("+this.myPair[0]+";"+this.myPair[1]+")";
    }
  }


}