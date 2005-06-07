package com.bordag.wtool;

// app specific imports
import com.bordag.sgz.util.*;
import com.bordag.sgz.*;
import com.bordag.sgz.algorithms.*;
import com.bordag.parasyn.*;
import com.bordag.parasyn.util.*;

import java.util.*;
import java.beans.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import java.awt.image.*;


/**
 * This class draws the ParaSynMap in an appropriate way in order for the user
 * to be able to define regions for word groups.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 */
public class ExperimentPanel extends JPanel
{
  JLabel EingabewortLabel = new JLabel();
  JTextField EingabeWortTextField = new JTextField();
  JButton StartButton = new JButton();
  ConnectionPanel cPanel = null;

  protected ParaSynMap myMap = null;
  protected Graphics2D bufferGraphics = null;
  protected BufferedImage buffer = null;
  protected Point origin = new Point(50, 420);
  protected Point cSystemSize = new Point(650, 380);

  protected int X = 2;
  protected int Y = 1;

  public ExperimentPanel(ConnectionPanel cPanel)
  {
    this.cPanel = cPanel;

    this.buffer = new BufferedImage(800, 550, BufferedImage.TYPE_INT_RGB);
    this.bufferGraphics = buffer.createGraphics();

    Hashtable hints = new Hashtable();
    hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    hints.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
    hints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
    hints.put(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);
    hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    this.bufferGraphics.addRenderingHints(hints);
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    EingabewortLabel.setText("Eingabewort :");
    EingabewortLabel.setBounds(new Rectangle(20, 15, 130, 20));
    this.setBackground(Color.white);
    this.setFont(new java.awt.Font("Dialog", 0, 12));
    this.setBorder(BorderFactory.createLoweredBevelBorder());
    this.setLayout(null);
    EingabeWortTextField.setText(Options.getInstance().getParaLastWord());
    EingabeWortTextField.setBounds(new Rectangle(175, 15, 190, 20));
    StartButton.setText("Start");
    StartButton.setBounds(new Rectangle(400, 15, 79, 20));
    StartButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        StartButton_actionPerformed(e);
      }
    });
    cPanel.setBorder(BorderFactory.createEtchedBorder());
    this.add(EingabeWortTextField, null);
    this.add(StartButton, null);
    this.add(EingabewortLabel, null);
  }

  public void paint(Graphics g)
  {
    super.paint(g);
    bufferGraphics.setColor(Color.white);
    bufferGraphics.fillRect(0,0,800,550);

    drawCoordinateSystem(this.cSystemSize.getX(), this.cSystemSize.getY());
    if ( this.myMap != null )
    {
//      drawCoordinateSystem(ParaSynInfo.getMaxX(), ParaSynInfo.getMaxY());
      drawCollocSigSet();
    }


    Graphics2D g2 = (Graphics2D) g;
    g2.drawImage(buffer, 0, 50, this);
  }

  /**
   * Draws the collocations
   */
  protected void drawCollocSigSet()
  {
    double ox = this.origin.getX();
    double oy = this.origin.getY();

    for ( Iterator it = this.myMap.getElements().iterator() ; it.hasNext() ; )
    {
      PSWord curInfo = (PSWord)it.next();
System.out.println("printing: "+curInfo);
      int x = 0;
      int y = 0;
      x = (int)(ox + curInfo.getY()*this.cSystemSize.getX());
      y = (int)(oy - curInfo.getX()*this.cSystemSize.getY());

      String word = curInfo.word.toString();
      bufferGraphics.setColor(Color.black);

      if ( x > 0 && y > 0 )
      {
        try
        {
          bufferGraphics.drawLine(x, y, x, y);
        }
        catch ( Exception ex )
        {
          System.out.println("x = "+x+" y="+y);
          ex.printStackTrace();
          System.out.println();
        }
        bufferGraphics.setColor(Color.red);
        bufferGraphics.setFont(bufferGraphics.getFont().deriveFont((float)8.0));
        if ( word.length() > 12 )
        {
          bufferGraphics.drawString(word.substring(0,11),x,y);
        }
        else
        {
          bufferGraphics.drawString(word,x,y);
        }
      }
    }
  }

  /**
   * Draws the coordinate system (some very simple form of a coordinate system ;-)
   * @param maxX
   * @param maxY
   */
  protected void drawCoordinateSystem(double maxX, double maxY)
  {
    bufferGraphics.setColor(Color.black);
    double ox = this.origin.getX();
    double oy = this.origin.getY();

    double labelX = maxX;
    double labelY = maxY;
    if ( this.myMap != null )
    {
      //double[] maximums = this.myMap.getMaximums();
      //labelX = maximums[0];
      //labelY = maximums[1];
      labelX = 1.0;
      labelY = 1.0;
    }

    bufferGraphics.drawLine((int)ox, (int)oy, (int)(ox+maxX), (int)oy);
    bufferGraphics.drawString((labelY+"").substring(0,3),(int)ox - 35, (int)(oy-maxY+5));
      bufferGraphics.drawString((3*(labelY/4)+"").substring(0,3),(int)ox - 35, (int)(oy-(3*(maxY/4))+5));
     bufferGraphics.drawString((labelY/2+"").substring(0,3),(int)ox - 35, (int)(oy-(maxY/2)+5));
      bufferGraphics.drawString((labelY/4+"").substring(0,3),(int)ox - 35, (int)(oy-(maxY/4)+5));

    bufferGraphics.drawLine((int)ox, (int)oy, (int)ox, (int)(oy - maxY));
    bufferGraphics.drawString((labelY+"").substring(0,3),(int)(ox + maxX) -10 , (int)oy+15);

      bufferGraphics.drawString((3*(labelY/4)+"").substring(0,3),(int)(ox + 3*(maxX/4)) -10 , (int)oy+15);
     bufferGraphics.drawString((labelY/2+"").substring(0,3),(int)(ox + (maxX/2)) -10 , (int)oy+15);
      bufferGraphics.drawString((labelY/4+"").substring(0,3),(int)(ox + (maxX/4)) -10 , (int)oy+15);

    bufferGraphics.drawString(this.EingabeWortTextField.getText(), (int)ox-35, (int)oy+15);
  }

  /**
   * The user entered a word and wants it to be calculated
   * @param e
   */
  void StartButton_actionPerformed(ActionEvent e)
  {
    String word = this.EingabeWortTextField.getText();
    if ( this.cPanel.connection != null && word != null && word.length() > 0 )
    {
      try
      {
        Options.getInstance().setParaLastWord(word);
        //this.myMap = new ParaSynMap(word, (CachedDBConnection)this.cPanel.connection);

        //-------------TEST

        String url = Options.getInstance().getConUrl();
        String user = Options.getInstance().getConUser();
        String passwd = Options.getInstance().getConPasswd();

        com.bordag.parasyn.util.DBConnection connection = null;
        try
        {
          System.out.print( "Creating connection:" );
          connection = new com.bordag.parasyn.util.DBConnection( url, user, passwd );
          System.out.print( "\t\t\tdone\nCreating util:" );
          DBUtil util = new DBUtil( connection );
          System.out.print( "\t\tdone\nCreating factory:" );
          PSDataFactory factory = new PSDataFactory( util );
          CHString wordString = new CHString( word.toString() );
          com.bordag.parasyn.PSWord myWord = new PSWord( wordString, util.getNumberForWord( wordString ), 0.0, 0 );
          System.out.print( "\t\tdone\nCreating PSData element for word " + myWord + ":" );

          Vector v = factory.getPSData(myWord);
          int maxMeaning = 0;
          int maxWords = 0;
          int i = 0;
          for ( Iterator it = v.iterator() ; it.hasNext() ; i++ )
          {
            ParaSynMap curMap = (ParaSynMap)it.next();
            if ( curMap.getLinguisticCollocations().size() > maxWords )
            {
              maxWords = curMap.getLinguisticCollocations().size();
              maxMeaning = i;
            }
          }
          this.myMap = (ParaSynMap)v.get(maxMeaning);
/*          this.myMap = (ParaSynMap)factory.getPSData(myWord).iterator().next();
          for ( Iterator it = factory.getPSData( myWord ).iterator(); it.hasNext(); )
          {
            PSPipe pipe = new PSPipe( ( ParaSynMap ) it.next(), util );
          }*/
        }
        catch ( Exception ex )
        {
          ex.printStackTrace();
        }

        //-------------TEST
      }
      catch ( Exception ex )
      {
        ex.printStackTrace();
      }
      repaint();
      System.out.println("Possible Linguistic collocations: "+this.myMap.getLinguisticCollocations());
      System.out.println("Possible cohyponyms: "+this.myMap.getCohyponyms());
      System.out.println("Possible hyperonyms: "+this.myMap.getHyperonyms());
    }
  }
}