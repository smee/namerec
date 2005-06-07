package com.bordag.sgz.job;

import java.sql.*;
import java.util.*;

import com.bordag.sgz.util.*;
import com.bordag.sgz.algorithms.*;
import com.bordag.sgz.clustering.*;

/**
 * DB table created by these threads look like this:
 * word_nr1 - word which was disambiguated
 * word_nr2 - word which is collocation of word_nr1
 * word_grp - group number of disambiguated group
 *
 * NOTE: word_grp = 0 is the group of words which he couldn't assign!
 *
 *
 * Title:        Wortschatz Tool
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author
 * @version 1.0
 */

public class DisThread implements Runnable
{
  private Thread myThread = null;
  private Integer wordNum = null;
  private DBConnection connection = null;
  private DBDisambiguator dis = null;

  public final static int MIN_WORDS = 10;

  public DisThread(DBConnection connection, DBDisambiguator dis)
  {
    this.wordNum = new Integer(Ticket.getInstance().getNewTicket());
    this.connection = connection;
    this.dis = dis;
    this.myThread = new Thread(this);
    this.myThread.start();
  }

  /**
   * disambiguate the word represented by the ticket as wordnumber.
   * put the results into DB if any.
   */
  public void run()
  {
    try
    {
      String word = this.connection.getWordForNumber(new ComparableStringBuffer(this.wordNum.toString())).toString();
//    System.out.println("calculating "+word);
      // disambiguate the word represented by the ticket as wordnumber
      NonThreadedDisambiguator disambiguator = null;
      try {
        disambiguator = new NonThreadedDisambiguator(this.connection, new ComparableStringBuffer(word));
        disambiguator.runAlgorithm();
      }
      catch (Exception ex) {
        System.out.println("Error calculating word " + word);
//      ex.printStackTrace();
      }
      // put the results into DB if any
      try {
        putResultsToDB(disambiguator);
      }
      catch (SQLException ex) {
        System.out.println("For word " + word + " couldn't put all results.");
        //ex.printStackTrace();
      }
    }
    catch ( Exception ex )
    {
      System.err.println("Error with wordNum: "+this.wordNum+" ignoring it.");
    }
    // I am done and have to return the ticket and tell the threadmanager
    Ticket.getInstance().ticketDone(this.wordNum.intValue());
    this.dis.threadDone(this);

//    System.out.println(".. "+word+" done");
    // then done call Ticket.done
  }

  protected void putResultsToDB(NonThreadedDisambiguator dis) throws SQLException
  {
    // a valid group is one which has more or equal to MIN_WORDS words in
    // value
    // ignore all groups which have less and put them into DB

    Statement statement = this.connection.getConnection().createStatement();

    HashSet unclusteredWords = dis.classifiedWordsList.getUnclusteredWords();
    for ( Iterator it = unclusteredWords.iterator() ; it.hasNext() ; )
    {
      String curQuery = "insert ignore into "+DBDisambiguator.TABLE_NAME+" ("+DBDisambiguator.FIELD_NAME1+","+DBDisambiguator.FIELD_NAME2+","+DBDisambiguator.FIELD_NAME3+") VALUES("+this.wordNum+","+it.next()+",0);";
      statement.addBatch(curQuery);
    }

    ClusterVector[] vectors = dis.getCluster().getClusterVectorsAsArray();
    Vector newVectors = new Vector();
    for ( int i = 0 ; i < vectors.length ; i++ )
    {
      Vector curVec = dis.getCluster().getClusterVectorsAsArray()[i].getValueAsVector();
      if ( curVec.size() >= this.MIN_WORDS )
      {
//        System.out.println("Adding "+i+"th vector.size = "+curVec.size());
        newVectors.add(curVec);
      }
    }

    int i=1;
//    System.out.println("newVectors.size = "+newVectors.size());
    for ( Iterator it = newVectors.iterator() ; it.hasNext() ; i++ )
    {
      Vector curVec = (Vector)it.next();
      for ( Iterator it2 = curVec.iterator() ; it2.hasNext() ; )
      {
        Object curObj = it2.next();
        String curQuery = "insert ignore into "+DBDisambiguator.TABLE_NAME+" ("+DBDisambiguator.FIELD_NAME1+","+DBDisambiguator.FIELD_NAME2+","+DBDisambiguator.FIELD_NAME3+") VALUES("+this.wordNum+","+curObj+","+i+");";
//        System.out.println(curQuery);
        statement.addBatch(curQuery);
      }
    }

//    System.out.println("Statement was: "+statement);
    try
    {
      statement.executeBatch();
      statement.close();
      statement.clearBatch();
    }
    catch ( SQLException ex )
    {
      //System.out.println("statement looked like that: "+statement);
      throw ex;
    }
    // do this using a batch statement
  }
}