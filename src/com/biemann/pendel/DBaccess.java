package com.biemann.pendel;

import java.*;
import java.util.*;
import java.sql.*;



public class DBaccess {
 
    public static boolean d=false; // debugging aus
    String dbTreiber = "org.gjt.mm.mysql.Driver";  // Treiber für Datenbankanbindung
    String dbName = "wortschatz"; // name der Datenbank
    Connection Verbindung; // Verbindung mit param. DBname, user, passwd
    
    Statement SQLAbfrage;
    String Anfrage;
    String ergString;
    ResultSet Ergebnis;

    public String getNof(String name, int anz, Connection Verbindung) throws SQLException{
	int anzahl;

	this.Verbindung=Verbindung;
	// Finde NR des Wortes in DB

      Anfrage= "SELECT wort_nr FROM wortliste WHERE wort_bin='"+name+"'";
	try{	    
	    SQLAbfrage = Verbindung.createStatement();
	    Ergebnis = SQLAbfrage.executeQuery(Anfrage);
	}
	catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}


     try {
    	Ergebnis.next();
        anzahl=Ergebnis.getInt(1);
      } catch (SQLException e) {anzahl=0;}
	if (d) {System.out.println("Wort '"+name+"' hat Nr.  "+anzahl);}
	
	if (anzahl>0) {  // nur, wenn wort auch in DB
	 Anfrage= "Select beispiel from saetze s, inv_liste i where i.wort_nr="+anzahl+" and i.bsp_nr=s.bsp_nr limit "+anz;   
	
	try{
	    	    
	    if (d) {System.out.println("Verbindung-init...fuer "+name);}
	    
	    SQLAbfrage = Verbindung.createStatement();
	    Ergebnis = SQLAbfrage.executeQuery(Anfrage);

	}
	catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}
	

	// Nun Umwandlen ResultSet in String
	
	ergString="Hier gehts los: ";


     try {
	while (Ergebnis.next()) {
	    ergString+= Ergebnis.getString(1)+"\n";
	
	}// elihw
      } catch (SQLException e) {ergString="für "+name+" gibt es nichts!";}
	//if (d)System.out.println("Ergebnis DB: "+ergString);
     } else {  // wort nicht in DB gefunden
	  System.out.println("Wort '"+name+"' nicht in DB!");
	  ergString="Ein Fehler ist aufgetreten, tut mir leid!";
	} //esle
	ergString+=" und jetzt Schluss!";
	return ergString;
    } // end getNof



    public String getSomeLike(String name, Connection Verbindung) throws SQLException{

	int anzahl;

	this.Verbindung=Verbindung;
	// Finde NR des Wortes in DB

      Anfrage= "SELECT anzahl FROM wortliste WHERE wort_bin='"+name+"'";
	try{	    
	    SQLAbfrage = Verbindung.createStatement();
	    Ergebnis = SQLAbfrage.executeQuery(Anfrage);
	}
	catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}


     try {
	Ergebnis.next();
        anzahl=Ergebnis.getInt(1);
      } catch (SQLException e) {anzahl=0;}
	if (d) {System.out.println("Wort '"+name+"' kommt "+anzahl+" mal vor!");}

	if (anzahl>255) {  // nur, wenn wort auch ganz oft in DB
	
	 Anfrage= "Select beispiel from saetze where beispiel LIKE '%" +name+" %' LIMIT 1000";   
	try{
	    	    
	    if (d) {System.out.println("Verbindung-init...fuer "+name);}
	    
	    SQLAbfrage = Verbindung.createStatement();
	    Ergebnis = SQLAbfrage.executeQuery(Anfrage);

	}
	catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}
	

	// Nun Umwandlen ResultSet in String
	
	ergString="Hier gehts los: ";


     try {
	while (Ergebnis.next()) {
	    ergString+= Ergebnis.getString(1)+"\n";
	
	}// elihw
      } catch (SQLException e) {ergString="für "+name+" gibt es nichts!";}
	//if (d)System.out.println("Ergebnis DB: "+ergString);
     } else {  // wortanzahl < 255 -> getNof
	  
	  ergString=this.getNof(name,anzahl, Verbindung);
	} //esle
	ergString+=" und jetzt Schluss!";
	return ergString;
    } // end getSomeLike

} // end DBaccess











