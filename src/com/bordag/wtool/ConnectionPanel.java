package com.bordag.wtool;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

// app specific imports
import com.bordag.sgz.util.*;
import com.bordag.sgz.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class ConnectionPanel extends JPanel
{
  JLabel PasswordLabel = new JLabel();
  JPasswordField PasswordField = new JPasswordField();
  JTextField LoginField = new JTextField();
  JLabel LoginLabel = new JLabel();
  JLabel UrlLabel = new JLabel();
  JTextField UrlField = new JTextField();
  JTextField DriverField = new JTextField();
  JLabel DriverLabel = new JLabel();
  JButton connectButton = new JButton();
  JLabel statusLabel1 = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea OutputArea = new JTextArea();
  DBConnection connection = null;

  public ConnectionPanel()
  {
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
    this.setBackground(UIManager.getColor("info"));
    this.setToolTipText("Set up the connection to the databases by filling out the appropriate text fields and press connect then.");
    UrlField.setBounds(new Rectangle(26, 52, 403, 24));
    UrlField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        UrlField_focusLost(e);
      }
    });
    UrlField.setText(Options.getInstance().getConUrl());
    UrlLabel.setBounds(new Rectangle(25, 26, 429, 23));
    UrlLabel.setText("URL");
    LoginLabel.setBounds(new Rectangle(26, 102, 207, 19));
    LoginLabel.setText("Login");
    LoginField.setBounds(new Rectangle(27, 124, 203, 26));
    LoginField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        LoginField_focusLost(e);
      }
    });
    LoginField.setText(Options.getInstance().getConUser());
    PasswordField.setBounds(new Rectangle(31, 221, 201, 24));
    PasswordField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        PasswordField_focusLost(e);
      }
    });
    PasswordField.setText(Options.getInstance().getConPasswd());
    PasswordLabel.setBounds(new Rectangle(30, 186, 202, 26));
    PasswordLabel.setText("Password");
    this.setLayout(null);
    DriverField.setText(Options.getInstance().getConDriver());
    DriverField.setBounds(new Rectangle(38, 310, 254, 29));
    DriverField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        DriverField_focusLost(e);
      }
    });
    DriverLabel.setText("JDBC Driver");
    DriverLabel.setBounds(new Rectangle(37, 269, 205, 28));
    connectButton.setActionCommand("connectButton");
    connectButton.setText("CONNECT");
    connectButton.setBounds(new Rectangle(322, 215, 136, 38));
    connectButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        connectButton_actionPerformed(e);
      }
    });
    this.setMaximumSize(new Dimension(600, 600));
    this.setMinimumSize(new Dimension(600, 600));
    this.setPreferredSize(new Dimension(600, 600));
    statusLabel1.setForeground(Color.red);
    statusLabel1.setText("not connected");
    statusLabel1.setBounds(new Rectangle(325, 178, 120, 21));
    jScrollPane1.setBounds(new Rectangle(24, 352, 461, 93));
    OutputArea.setToolTipText("");
    OutputArea.setEditable(false);
    this.add(UrlField, null);
    this.add(UrlLabel, null);
    this.add(LoginField, null);
    this.add(LoginLabel, null);
    this.add(PasswordLabel, null);
    this.add(PasswordField, null);
    this.add(connectButton, null);
    this.add(DriverLabel, null);
    this.add(DriverField, null);
    this.add(statusLabel1, null);
    this.add(jScrollPane1, null);
    jScrollPane1.getViewport().add(OutputArea, null);
  }

  void connectButton_actionPerformed(ActionEvent e)
  {
    if ( this.connection == null )
    {
      this.OutputArea.setText("");
      this.statusLabel1.setForeground(Color.blue);
      this.statusLabel1.setText("connecting ...");
      this.statusLabel1.repaint();
      Options.getInstance().setConUrl(this.UrlField.getText());
      Options.getInstance().setConDriver(this.DriverField.getText());
      Options.getInstance().setConUser(this.LoginField.getText());
      Options.getInstance().setConPasswd(new String(this.PasswordField.getPassword()));
      String url = Options.getInstance().getConUrl();
      String user = Options.getInstance().getConUser();
      String passwd = Options.getInstance().getConPasswd();

      try
      {
        this.connection = new CachedDBConnection(url, user, passwd);
      }
      catch (ClassNotFoundException cnfe)
      {
        this.OutputArea.append(cnfe.getMessage());
        this.OutputArea.append("\nCould not load Mysql Driver");
        this.statusLabel1.setForeground(Color.red);
        this.statusLabel1.setText("not connected");
        return;
      }
      catch(Exception ex)
      {
        this.OutputArea.append(ex.getMessage());
        this.OutputArea.append("\nCould not establish connection");
        this.statusLabel1.setForeground(Color.red);
        this.statusLabel1.setText("not connected");
        return;
      }
      this.statusLabel1.setForeground(Color.green);
      this.statusLabel1.setText("connected");
      this.connectButton.setText("DISCONNECT");
    }
    else
    {
      this.connection = null;
      this.statusLabel1.setForeground(Color.red);
      this.statusLabel1.setText("not connected");
      this.connectButton.setText("CONNECT");
    }
  }

  void UrlField_focusLost(FocusEvent e)
  {
    if ( this.UrlField.getText() != null && this.UrlField.getText().length() > 0 )
    {
      Options.getInstance().setConUrl(this.UrlField.getText());
    }
  }

  void LoginField_focusLost(FocusEvent e)
  {
    if ( this.LoginField.getText() != null && this.LoginField.getText().length() > 0 )
    {
      Options.getInstance().setConUser(this.LoginField.getText());
    }
  }

  void PasswordField_focusLost(FocusEvent e)
  {
    if ( this.PasswordField.getPassword() != null && this.PasswordField.getPassword().length > 0 )
    {
      Options.getInstance().setConPasswd(new String(this.PasswordField.getPassword()));
    }
  }

  void DriverField_focusLost(FocusEvent e)
  {
    if ( this.DriverField.getText() != null && this.DriverField.getText().length() > 0 )
    {
      Options.getInstance().setConDriver(this.DriverField.getText());
    }
  }

}