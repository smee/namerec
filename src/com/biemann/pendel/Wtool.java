
//Titel:      Wortschatz-Tool
//Version:
//Copyright:  Copyright (c) 1999
//Autor:     C. Biemann
//Organisation:    Uni Leipzig
//Beschreibung:Ihre Beschreibung
package com.biemann.pendel;

import javax.swing.UIManager;

public class Wtool {
  boolean packFrame = false;

  //Anwendung konstruieren
  public Wtool() {
    PendelPanel frame = new PendelPanel(null);
    //Frames validieren, die eine voreingestellte Größe besitzen
    //Frames packen, die nützliche bevorzugte Infos über die Größe besitzen, z.B. aus ihrem Layout
    if (packFrame)
      frame.revalidate(); // SB geandert, da pack nicht existiert
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
    new Wtool();
  }
}