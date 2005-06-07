package com.bordag.wtool;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.awt.datatransfer.*;

import com.bordag.sgz.algorithms.*;
import com.bordag.sgz.clustering.*;
import com.bordag.sgz.util.*;

/**
 * @author    Bordag Stefan
 * @date      30.04.2003
 */
public class DisOutputDialog extends JDialog implements Runnable
{
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JButton copyClipboard = new JButton();
  JButton stopButton = new JButton();
  JTable jTable1 = null;
  JProgressBar jProgressBar1 = new JProgressBar();

  Thread myThread = null;
  NonThreadedDisambiguator dis = null;
  MyTableModel tableModel = null;
  DisambiguationPanel parent = null;

  private double progressPerRun = 0.0;
  private double curProgress = 0.0;

  public DisOutputDialog(DisambiguationPanel parent, Frame frame, String title, boolean modal, NonThreadedDisambiguator dis)
  {
    super(frame, title, modal);
    this.parent = parent;
    this.dis = dis;
    this.tableModel = new MyTableModel(dis);
    jTable1 = new JTable(this.tableModel);

    this.curProgress += 2;
    this.progressPerRun = ( 1 / ( new Double(Options.getInstance().getDisMaxRuns()).doubleValue() - 1 ) ) * 100.0;

    try
    {
      jbInit();
      pack();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }


    myThread = new Thread(this);
    myThread.start();
  }

  public void run()
  {

    int curRun = 0;
    while ( true )
    {
      int newRun = dis.getCurRun();
      if ( newRun != curRun )
      {
      System.out.println("Renewing after run : "+curRun+" with new run: "+dis.getCurRun());
        increaseProgressBar(newRun - curRun);
        curRun = newRun;
        this.tableModel.renewData();
        this.jTable1.revalidate();
      }
      try
      {
        myThread.sleep(10);
      }
      catch ( Exception ex )
      {
        ex.printStackTrace();
      }
    }
  }

  private void increaseProgressBar(int diff)
  {
    curProgress += this.progressPerRun*diff;
    this.jProgressBar1.setValue((int)curProgress);
  }

  void jbInit() throws Exception
  {
    this.getContentPane().setLayout(null);
    this.getContentPane().setBackground(Color.red);

    jPanel1.setBackground(Color.white);
    jPanel1.setBounds(new Rectangle(0, 0, 800, 500));
    jPanel1.setLayout(null);
    jTable1.setPreferredScrollableViewportSize(new Dimension(800, 500));
    jScrollPane1.setBounds(new Rectangle(0, 0, 800, 500));
    jScrollPane1.getViewport().add(jTable1, null);
    jPanel1.add(jScrollPane1, null);


    jPanel2.setBackground(Color.white);
    jPanel2.setBounds(new Rectangle(0, 500, 800, 600));
    jPanel2.setLayout(null);
    copyClipboard.setText("Copy all to clipboard");
    copyClipboard.setBounds(new Rectangle(123, 15, 145, 35));
    copyClipboard.addActionListener(new DisOutputDialog_copyClipboard_actionAdapter(this));
    stopButton.setText("STOP");
    stopButton.setBounds(new Rectangle(51, 15, 67, 35));
    stopButton.addActionListener(new DisOutputDialog_stopButton_actionAdapter(this));
    jProgressBar1.setBackground(Color.white);
    jProgressBar1.setForeground(Color.blue);
    jProgressBar1.setBounds(new Rectangle(5, 1, 785, 10));
    jPanel2.add(jProgressBar1, null);
    jPanel2.add(stopButton, null);
    jPanel2.add(copyClipboard, null);

    this.getContentPane().add(jPanel1, null);
    this.getContentPane().add(jPanel2, null);
  }

  void stopButton_actionPerformed(ActionEvent e)
  {
    this.parent.startButton_actionPerformed(null);
    this.hide();
  }

  void copyClipboard_actionPerformed(ActionEvent e)
  {
    String exp = new String("");
    for ( int i = 0 ; i < this.tableModel.getRowCount() ; i++ )
    {
      exp = exp + " CV: "+this.tableModel.getValueAt(i, 0) + " : "+this.tableModel.getValueAt(i, 1) + "\n";
    }
    StringSelection expString = new StringSelection(exp);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(expString, null);
  }

  /**
   * This model returns the values from the disambiguator clusterer
   */
  class MyTableModel extends AbstractTableModel
  {
    NonThreadedDisambiguator dis = null;
    final String[] colNames = {"Tripel", "Kontextvektoren"};
    Object[][] myData = null;

    public MyTableModel(NonThreadedDisambiguator d)
    {
      dis = d;
    }

    public void renewData()
    {
      Object[][] newData = null;
      TreeClustering treeClus = dis.getCluster().getClone();
      if ( dis != null && treeClus != null &&
           treeClus.getClusterVectorsAsArray() != null )
      {
        DBConnection connection = dis.getConnection();
        newData = new Object[treeClus.getClusterVectorsAsArray().length][2];
        ClusterVector[] array = treeClus.getClusterVectorsAsArray();
        for ( int i = 0 ; i < array.length ; i++ )
        {
          ClusterVector curVec = array[i];
          newData[i][0] = getArrayAsSet(connection.getWordsForNumbers(curVec.getKey()));
          newData[i][1] = getArrayAsSet(connection.getWordsForNumbers(curVec.getValue()));
        }
      }
      this.myData = newData;
    }

    public int getColumnCount()
    {
      return 2;
    }

    public int getRowCount()
    {
      if ( this.myData != null )
      {
        return this.myData.length;
      }
      else
      {
        return 0;
      }
    }

    public String getColumnName(int index)
    {
      return colNames[index];
    }

    public TreeSet getArrayAsSet(ComparableStringBuffer[] buf)
    {
      TreeSet set = new TreeSet();
      for ( int i = 0 ; i < buf.length ; i++ )
      {
        set.add(buf[i].toString());
      }
      return set;
    }

    public Object getValueAt(int row, int col)
    {
      if ( this.myData != null && row < myData.length && col < myData[row].length )
      {
        return this.myData[row][col];
      }
      else
      {
        return "no data";
      }
    }
  }
}

  class DisOutputDialog_stopButton_actionAdapter implements java.awt.event.ActionListener
  {
    DisOutputDialog adaptee;

    DisOutputDialog_stopButton_actionAdapter(DisOutputDialog adaptee)
    {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e)
    {
      adaptee.stopButton_actionPerformed(e);
    }
  }

  class DisOutputDialog_copyClipboard_actionAdapter implements java.awt.event.ActionListener
  {
    DisOutputDialog adaptee;

    DisOutputDialog_copyClipboard_actionAdapter(DisOutputDialog adaptee)
    {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e)
    {
      adaptee.copyClipboard_actionPerformed(e);
    }
  }
