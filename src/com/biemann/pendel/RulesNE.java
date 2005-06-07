package com.biemann.pendel;

import java.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import gnu.regexp.*;

/* Rules verwaltet die Patterns und verschiebt den Dot. usw

Prozeduren:
- loadPatterns: Läd Pattern aus ner Datei
- initPatterns: Nimmt externe Patterns
- candidates: verarbeitet ein Wort mit klasse und gibt kandidaten zurück
- resetRules: setzt dots auf 0
*/



public class RulesNE {

    static boolean d=!true; //debugging aus
    static Vector patterns;  // Vektor der Regeln
    static String matchFile="matchfile.txt"; // default, besser setzen
    static DBaccess db=new DBaccess();
    public static boolean writing=false; // bool für fileWrite


    public void initPatterns(Vector inpats, String fileContexts, boolean w) {
	         this.patterns=new Vector();
	         this.matchFile=fileContexts;
           this.writing=w;
           for(Enumeration e=inpats.elements();e.hasMoreElements();) {
              Pattern pat=(Pattern)e.nextElement();
              pat.partialInit();
              this.patterns.addElement(pat);
           }

    } // end initPatterns



    public static void resetRules() {
	for(Enumeration r=patterns.elements();r.hasMoreElements();) { // Für alle pattern
	    Pattern actPat=(Pattern)r.nextElement();
	    actPat.dot=0;
	} // rof Enum r

    } // end  public static void resetRules()

/*
    private static void dbInsertPerson(Pattern pat,Connection Verbindung) {

	// Generiere insert-statement für person(wort_bin="VN ZN NN ,TIT",wort_lex="ZN NN VN TIT", beruf="TIT", quelle"NameRec")

	String vns="";
	String znnns="";
	String titpus="";
	//

	int mix=0;
	int tit=0;
	for(int i=0;i<pat.length;i++) {
	    //vns
            if (pat.pattern[i].equals("VN")) {vns+=" "+pat.word[i];mix=0;tit=0;}
            if (pat.pattern[i].equals("MIX")) {vns+=" "+pat.word[i];mix=1;tit=0;}
            if ((mix==1)&&pat.pattern[i].equals("PU")) {vns+=".";mix=0;tit=0;}
	    //znnns

            if (pat.pattern[i].equals("ZN")) {znnns+=" "+pat.word[i];mix=0;tit=0;}
            if (pat.pattern[i].equals("NN")) {znnns+=" "+pat.word[i];mix=0;tit=0;}

	    //titpus
            if (pat.pattern[i].equals("TIT")) {titpus+=" "+pat.word[i];mix=0;tit=1;}
            if ((tit==1)&&pat.pattern[i].equals("PU")) {titpus+=".";mix=0;tit=0;}
	} // for int i

	// entferne führendes " "
	if (!vns.equals("")) vns=vns.substring(1,vns.length());
	if (!znnns.equals("")) znnns=znnns.substring(1,znnns.length());
	if (!titpus.equals("")) titpus=titpus.substring(1,titpus.length());
     if (!vns.equals("")) { // no fun without a vornamen
	String statement="INSERT INTO person (wort_bin, wort_lex,beruf,quelle) values ";
	if (titpus.equals("")) {  //Fall: keine titel
	    statement+="('"+vns+" "+znnns+"','"+znnns+" "+vns+"','','NameRec')";
	} else {  //Fall: wohl Titel
	    statement+="('"+vns+" "+znnns+"','"+znnns+" "+vns+" "+titpus+"','"+titpus+"','NameRec')";

	} // esle

	if (db.nrOfLex(znnns+" "+vns+" "+titpus,Verbindung)<1) { // nur, wenn noch nicht drin
	    try {
		db.SQLstatement(statement,Verbindung);
	    } catch (SQLException e) {System.out.println("Datenbankfehler! "+e.getMessage());}
	} // fi dbNrOfLex

     } // fi !vns.empty

    } // end dbInsertPerson

  */

    private static void output(String filename,Pattern pat) throws IOException, FileNotFoundException {


	FileWriter file=new FileWriter(filename,true); // true für append
	String outstr=new String();
	char outChar;

	for(int i=0;i<pat.length;i++) {
	    outstr+=pat.word[i]+" ";
	}
	outstr+="\t"+pat2String(pat)+(char)(13)+(char)(10);

	if (d) System.out.print("Z: "+outstr+"\t"+pat2String(pat)+"\n");
	if (writing) {try {
	    for (int pos=0;pos<outstr.length();pos++) {
		outChar=outstr.charAt(pos);
		file.write((int)outChar);                                                  } //rof
	} catch (IOException e){System.out.println("Can't write "+filename);}
	finally {file.close();}}


    } // end private void output

       private static String pat2String(Pattern pat) {
      String[] actPattern=pat.pattern;
      String patString="";
      for (int j=0;j<pat.length;j++) { // Zielpos markieren
        patString+=actPattern[j]+" ";
      } // rof
      patString=patString.substring(0,patString.length()-1)+"->"+pat.goalClass;

      return patString;
    }


    public static NameTable match(int classWord, String plainWord, NameTable klassKeys) throws IOException, FileNotFoundException, SQLException { //Connection Verbindung

	NameTable retItems = new NameTable();
	Pattern actPat;  // aktuelles Pattern in Schleife
	int patMatch; // nächste Klasse in Pattern

	for(Enumeration r=patterns.elements();r.hasMoreElements();) { // Für alle pattern
	    actPat=(Pattern)r.nextElement();
	    patMatch=new Integer(klassKeys.get(actPat.pattern[actPat.dot]).toString()).intValue();
 	    if ((patMatch&classWord)==patMatch) { // Falls aktuelles Wort matcht
		actPat.word[actPat.dot]=plainWord; // Wort in Pattern merken
		actPat.dot++;  // Dot eins weiter
	    } else { // Falls nicht, resette dot
		actPat.dot=0;
	    } // eslefi patMatch&classWord

	    if (actPat.dot==actPat.length) { // Falls Länge erreicht, also Regel komplett matcht
    //    System.out.println("Hit:"+actPat.word[actPat.goalPos]);
	 //	retItems.put(actPat.word[actPat.goalPos],actPat.goalClass); // neuer Kandidat in Nametable
		output(matchFile,actPat); // Schreibe match raus
		//dbInsertPerson(actPat,Verbindung);   für Datenbank-write von Pattern
		actPat.dot=0; // Resette dot
	    } // fi dot=length

	} // rof Enum r
	return retItems;
    } // end candidates


} // end Class Rules
