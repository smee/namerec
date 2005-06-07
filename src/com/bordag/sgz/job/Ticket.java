package com.bordag.sgz.job;

import java.io.*;
import java.util.*;

/**
 *  This class gives tickets for a job on the wortschatz which processes words
 *  based on wordnumber. It keeps permanent information in a file for the case
 *  of a crash. The algorithm works as following:
 *
 *  Request of a new ticket:
 *  synchronized()
 *  {
 *    1. look up curDoneTickets and curWorkingTickets and curTicket for the
 *       highest number.
 *    2. newNumber = Add one to the highest.
 *    3. Add newNumber to curWorkingTickets.
 *    4. writeFile();
 *    5. return newNumber
 *  }
 *
 *  A ticket is given as done.
 *  synchronized()
 *  {
 *    1. Add this ticket to curDoneTickets
 *    2. check whether curDoneTickets has a ticket which is by one higher then
 *       curTicket. if so, inc(curTicket), curWorkingTickets.remove(curTicket)
 *       else writeFile(); return;
 *    3. goto 2.
 *  }
 *
 * @author      Stefan Bordag
 * @date        1.05.2003
 */
public class Ticket
{
  private static Ticket instance = null;
  private String ticketFile = null;

  private Integer curTicket = null;
  private Set curWorkingTickets = null;
  private Set curDoneTickets = null;

  private final static String defaultFile = "ticketFile.txt";
  private final static int defaultBeginTicket = 1000;

  private Ticket()
  {
    try
    {
      init(this.defaultFile);
    }
    catch ( Exception ex )
    {
      this.curTicket = new Integer(this.defaultBeginTicket);
      this.curWorkingTickets = new HashSet();
      this.curDoneTickets = new HashSet();
    }
  }

  public static Ticket getInstance()
  {
    if ( instance == null )
    {
      synchronized(Ticket.class)
      {
        instance = new Ticket();
      }
    }
    return instance;
  }

  /**
   * Initializes the reader
   */
  private void init(String fileName) throws FileNotFoundException
  {
    this.ticketFile = fileName;
    this.curWorkingTickets = new HashSet();
    this.curDoneTickets = new HashSet();

    try
    {
      BufferedReader reader = null;
      if ( fileName != null && fileName.length() > 0 )
      {
        reader = new BufferedReader(new FileReader(this.ticketFile));
        readFile(reader);
      }
    }
    catch (FileNotFoundException fnf)
    {
      System.out.println("Ticketfile '"+fileName+"' konnte nicht gefunden werden.");
      throw fnf;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

  }

  /**
   *  Reads in the two stored variables of this class
   **/
  private void readFile(BufferedReader reader) throws IOException
  {
    if ( reader == null )
    {
      return;
    }
    String line = reader.readLine();
    while ( line != null )
    {
      if ( line != null && line.length() > 0 )
      {
        if ( this.curTicket == null )
        {
          try{this.curTicket = new Integer(line);}
          catch ( Exception ex ){ex.printStackTrace();}
        }
        else
        {
          try{this.curDoneTickets.add( new Integer(line));}
          catch ( Exception ex ){ex.printStackTrace();}
        }
      }
      line = reader.readLine();
    }
  }

  /**
   * Writes out current state of this instance into the current file
   */
  private void writeFile()
  {
    try
    {
      BufferedWriter writer = null;
      if ( this.ticketFile != null)
      {
        writer = new BufferedWriter(new FileWriter(this.ticketFile));
      }
      writer.write(this.curTicket.toString());
      writer.newLine();
      for ( Iterator it = this.curDoneTickets.iterator() ; it.hasNext() ; )
      {
        writer.write(it.next().toString());
        if ( it.hasNext() ) { writer.newLine(); }
      }
      writer.close();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(0);
    }
  }

  /**
   *  Request of a new ticket.
   */
  public int getNewTicket()
  {
    synchronized( Ticket.class )
    {
      int retNumber = -1;
      int curNumber = this.curTicket.intValue();
      int highestNumber = getHighestNumber(this.curDoneTickets);
      highestNumber = Math.max(highestNumber, getHighestNumber(this.curWorkingTickets) );
      while ( curNumber <= highestNumber )
      {
        //curNumber++;
        Integer temp = new Integer(curNumber);
        if ( ! this.curDoneTickets.contains(temp) && ! this.curWorkingTickets.contains(temp) )
        {
          retNumber = curNumber;
          break;
        }
        curNumber++;
      }
      retNumber = curNumber;
      this.curWorkingTickets.add(new Integer(retNumber));
      writeFile();
      return retNumber;
    }
  }

  /**
   * Returns the highest Integer from the given set or -1
   * The set should contain only Integers, otherwise an Exception will
   * be thrown.
   */
  private int getHighestNumber(Set set)
  {
    if ( set == null || set.size() < 1 )
    {
      return -1;
    }
    int highestNumber = 0;
    for ( Iterator it = set.iterator() ; it.hasNext() ; )
    {
      Integer curInt = (Integer)it.next();
      if ( curInt.intValue() > highestNumber )
      {
        highestNumber = curInt.intValue();
      }
    }
    return highestNumber;
  }

  /**
   * Returns the lowest Integer from the given set or -1
   * The set should contain only Integers, otherwise an Exception will
   * be thrown.
   */
  private int getLowestNumber(Set set)
  {
    if ( set == null || set.size() < 1 )
    {
      return -1;
    }
    int lowestNumber = Integer.MAX_VALUE;
    for ( Iterator it = set.iterator() ; it.hasNext() ; )
    {
      Integer curInt = (Integer)it.next();
      if ( curInt.intValue() < lowestNumber )
      {
        lowestNumber = curInt.intValue();
      }
    }
    return lowestNumber;
  }

  /**
   *  A ticket is given as done.
   */
  public void ticketDone(int ticket)
  {
    synchronized( Ticket.class )
    {
      if ( ticket < this.curTicket.intValue() || !this.curWorkingTickets.contains(new Integer(ticket)))
      {
        return;
      }
      this.curDoneTickets.add(new Integer(ticket));
      this.curWorkingTickets.remove(new Integer(ticket));
      while ( true )
      {
        int lowestDoneTicket = getLowestNumber(this.curDoneTickets);
        if ( lowestDoneTicket == (this.curTicket.intValue()/*+1*/) )
        {
          this.curTicket = new Integer(this.curTicket.intValue()+1);
          this.curDoneTickets.remove(new Integer(lowestDoneTicket));
        }
        else
        {
          break;
        }
      }
      writeFile();
    }
  }

  /**
   * Gives some information about the state of this instance
   */
  public String toString()
  {
    String retVal = "";
    retVal = retVal + "Ticket: { curTicket: "+this.curTicket+"  curWorkingTickets: "+this.curWorkingTickets+"  curDoneTickets: "+this.curDoneTickets+"}";
    return retVal;
  }

  public static void main(String[] args)
  {
    Ticket t = Ticket.getInstance();
    System.out.println("Got ticket: "+t.getNewTicket());
    t.ticketDone(1000);

    /*    System.out.println("Ticket: "+t);
    HashSet set = new HashSet();
    for ( int i = 0 ; i < 20 ; i++ )
    {
      set.add(new Integer(t.getNewTicket()));
    }
    System.out.println("Ticket: "+t);
    System.out.println("hash: "+set);
    t.ticketDone(55);
    t.ticketDone(1055);
    t.ticketDone(1005);
    t.ticketDone(1008);
    //t.ticketDone(1000);
    t.ticketDone(1001);
    t.ticketDone(1002);
    t.ticketDone(1003);
    t.ticketDone(1004);
    t.ticketDone(1006);
    t.ticketDone(1007);
    t.ticketDone(1009);*/
/*    for ( Iterator it = set.iterator() ; it.hasNext() ; )
    {
      Integer temp = (Integer)it.next();
      t.ticketDone(temp.intValue());
    }*/
    System.out.println("Ticket: "+t);
  }
}