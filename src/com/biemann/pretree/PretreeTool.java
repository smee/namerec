package com.biemann.pretree;
//Titel:      Wortschatz-Tool
//Version:
//Copyright:  Copyright (c) 2003
//Autor:     C. Biemann
//Organisation:    Uni Leipzig
//Beschreibung:Ihre Beschreibung
//package wtool;

import javax.swing.UIManager;

public class PretreeTool {
  boolean packFrame = false;

  //Anwendung konstruieren
  public PretreeTool() {
    PretreePanel frame = new PretreePanel(null);
    //Frames validieren, die eine voreingestellte Gr��e besitzen
    //Frames packen, die n�tzliche bevorzugte Infos �ber die Gr��e besitzen, z.B. aus ihrem Layout
    if (packFrame)
      frame.revalidate();
    else
      frame.validate();
    frame.setVisible(true);
  }

  //Main-Methode
  public static void main(String[] args) {
    try  {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
    }
    new PretreeTool();
  }
}