package namerec;
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

    static boolean d=true; //debugging aus
    static boolean dbinsert=true; // INSERT
    static Vector patterns;  // Vektor der Regeln
    static String matchFile="matchfile.txt"; // default, besser setzen
    static DBaccess db=new DBaccess();
    

    public void initPatterns(Vector inpats, String fileContexts) {
	this.patterns=inpats;
	this.matchFile=fileContexts;
    } // end initPatterns


    public void loadPatterns(String patfile, String fileContexts) throws IOException, FileNotFoundException {
	FileReader file=new FileReader(patfile); 
	String inLine="";
	int inInt;
	Vector retvec=new Vector();
       
	String goalClass;
	int length;
	int goalPos;
	String dummy;
	this.matchFile=fileContexts;	

	try{
	    inLine="\n";
	    while ((inInt=file.read())!=-1) { //lese bis EOF
	    
	       while(inInt!=-1&&inInt!=41) {           // Lese bis ")" 
		    inLine+=(char)inInt;
		    inInt=file.read();
	       } //elihw inInt<>LF/CR    
	       if (inInt!=-1) {
		inLine+=(char)inInt;

		if (d) System.out.print("Line: "+inLine);

		StringTokenizer tokens = new StringTokenizer(inLine,",");
		if (d) System.out.print("Tokens:\n");
		
		// extract pattern information
		
		dummy=tokens.nextToken();
		goalClass=dummy.substring(8,dummy.length());
		length=new Integer(tokens.nextToken().substring(7,9)).intValue();
		goalPos=new Integer(tokens.nextToken().substring(8,9)).intValue();
		String pats[]=new String[length];
		dummy=tokens.nextToken(); // nun sowas wie: pattern= VN KL VN GR 
	       
		StringTokenizer ptokens= new StringTokenizer(dummy," ");
		ptokens.nextToken(); // omit "pattern="
		for(int i=0;i<length;i++) {
		    pats[i]=ptokens.nextToken();
		} // rof i

		Pattern inputpat=new Pattern();
		inputpat.init(goalClass,length,goalPos,pats);
	        if (d) System.out.println("Newpat: "+inputpat.toString());
		retvec.addElement(inputpat);
	       } // fi -1
	       inLine="";
	    } // elihw EOF

	} catch (IOException e) {System.out.println("Can't find file "+patfile+"\n");}
	
	this.patterns=retvec;
    } // end loadPatterns

    public static void resetRules() {
	for(Enumeration r=patterns.elements();r.hasMoreElements();) { // Für alle pattern
	    Pattern actPat=(Pattern)r.nextElement();
	    actPat.dot=0;
	} // rof Enum r

    } // end  public static void resetRules()


    private static void dbInsertPerson(Pattern pat,Connection Verbindung) {
	
	// Generiere insert-statement für person(wort_bin="VN ZN NN ,TIT",wort_lex="ZN NN VN TIT", beruf="TIT", quelle"NameRec")

	String vns="";
	String znnns="";
	String titpus="";
	String first_vn="";
	String first_zn="";
	String first_nn="";
	String normalform="";
	String kat="4"; // Kategorie "sonstige Person"
	//

	int mix=0;
	int tit=0;
	
	int f_vn=0;
	int f_zn=0;
	int f_nn=0;


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

	    //firstones für normalform
 	    if ((f_vn==0)&&pat.pattern[i].equals("VN")) {first_vn=pat.word[i]; f_vn=1;}
 	    if ((f_zn==0)&&pat.pattern[i].equals("ZN")) {first_zn=pat.word[i]; f_zn=1;}
 	    if ((f_nn==0)&&pat.pattern[i].equals("NN")) {first_nn=pat.word[i]; f_nn=1;}
	} // for int i

	if (f_zn==0) {normalform=first_vn+" "+first_nn; }
	else {normalform=first_vn+" "+first_zn+" "+first_nn; }

	// entferne führendes " "
	if (!vns.equals("")) vns=vns.substring(1,vns.length());
	if (!znnns.equals("")) znnns=znnns.substring(1,znnns.length());
	if (!titpus.equals("")) titpus=titpus.substring(1,titpus.length());
//     if (!vns.equals("")) { // no fun without a vornamen
//	change 4.12.2002: it is fun with vornamen!
	String statement="INSERT INTO person (wort_bin,wort_lex,wort_alt,beruf,kat_nr,quelle) values ";
	if (titpus.equals("")) {  //Fall: keine titel
	    statement+="('"+vns+" "+znnns+"','"+znnns+" "+vns+"','"+normalform +"','',4,'NameRec 1.1')";
	} else {  //Fall: wohl Titel
	    statement+="('"+titpus+" "+vns+" "+znnns+"','"+znnns+" "+vns+" "+titpus+"','"+normalform+"','"+titpus+"',4,'NameRec 1.1')";

	} // esle
	
	if (db.nrOfLex(znnns+" "+vns+" "+titpus,Verbindung)<1) { // nur, wenn noch nicht drin
	    try {  
		db.SQLstatement(statement,Verbindung);
	    } catch (SQLException e) {System.out.println("Datenbankfehler! "+e.getMessage());}
	} // fi dbNrOfLex 
        else {System.out.println("Schon vorhanden!");}
	
//     } // fi !vns.empty

    } // end dbInsertPerson



    private static void output(String filename,Pattern pat) throws IOException, FileNotFoundException {
	

	FileWriter file=new FileWriter(filename,true); // true für append
	String outstr=new String();
	char outChar;
	
	for(int i=0;i<pat.length;i++) {
	    outstr+=pat.word[i]+" ";
	}
	outstr+="\t";
	for(int i=0;i<pat.length;i++) {
            outstr+=pat.pattern[i]+" ";
	}


	outstr+="\n";

	System.out.print("Z: "+outstr+"\t"+pat.toString());
	try {
	    for (int pos=0;pos<outstr.length();pos++) {
		outChar=outstr.charAt(pos);		 
		file.write((int)outChar);                                                  } //rof
	} catch (IOException e){System.out.println("Can't write "+filename);} 
	finally {file.close();}


    } // end private void output



    public static NameTable match(int classWord, String plainWord, NameTable klassKeys,Connection Verbindung) throws IOException, FileNotFoundException, SQLException {

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
		retItems.put(actPat.word[actPat.goalPos],actPat.goalClass); // neuer Kandidat in Nametable
		output(matchFile,actPat); // Schreibe match raus
		if (dbinsert) dbInsertPerson(actPat,Verbindung);
		actPat.dot=0; // Resette dot
	    } // fi dot=length

	} // rof Enum r
	return retItems;
    } // end candidates

    
} // end Class Rules
