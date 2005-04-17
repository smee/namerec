package namerec;
import java.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.sql.*;

/* Recognizer
Findet Namen in der Wortschatz-Datenbank WDTAKTUELL,
neue Namen werde in der WORTSCHATZ-DB überprüft.

Autor: Christian Biemann 04/002
 für Wortschatz Projekt


*/



public class Recognizer {

  static boolean d=!false; //debugging an
  static String dbTreiber = "org.gjt.mm.mysql.Driver";  // Treiber für Datenbankanbindung

  static Connection Verbindung_akt; // Verbindung zu WDTaktuell   
  static Connection Verbindung_ws; // Verbindung zu WORTSCHATZ

   private static String fileRegexp="regexps.txt"; // enthält Reguläre Ausdrücke mit Klassifikationen, z.B. "[A-Z][a-z]+ \t GR" für grossgeschriebene Wörter
    private static String fileGrundstock="wissenAkt.txt";// Enthält das Grundwissen  
    private static String itemFile="itemsFound.txt";
    private static String patFile="pats2.txt";
    private static String patFile_NE="patPers.txt";
    private static String fileKlass="klassNamen.txt";   
    private static String maybeFile="maybes.txt";   
    private static String fileContexts="contexts.txt";   
    private static String dbName = "lipsia/wortschatz"; // name der Datenbank
    private static String fileGarantie="NEs.txt";
    private static int startNr=0;

    //switches für Kommandozeilenparameter - zu viele, da von Pendel adaptiert.
    private static boolean switch_ic=false, switch_ik=false, switch_ir=false, switch_ig=false,switch_ii=false;
    private static boolean switch_rl=false, switch_rf=false, switch_rt=false, switch_rp=false;
    private static boolean switch_pk=false, switch_pi=false, switch_pt=false, switch_pr=false, switch_pn=false,switch_pd=false;
    private static boolean switch_ss=false; // start sentence nr
    private static boolean switch_oi=false, switch_om=false, switch_og=false, switch_or=false;



    // Default-Werte für Kommandozeilenparameter
    private static int n_cands=30;	// switch pk
    private static double acceptItem=0.1;	// switch pt

    // Vectoren fuer Regellernen und Laden

    // Nametables fuer Wissen aller Art	
    private static NameTable alleRegexp=new NameTable(); // Regexps
    private static NameTable allesWissen=new NameTable(); // Alle bekannten items
    private static NameTable klassKeys=new NameTable(); // Bitzuordnung zu Klassen
    // Patchy: Candidatecheck rules    
    private static Vector canrules= new Vector();	// Regeln

    // Andere Klassen
    private static Rules rules=new Rules();
    private static RulesNE rules_NE=new RulesNE();
    private static TextProcessor textProc=new TextProcessor();
    private static DBaccess db=new DBaccess();

    public static void processArguments(String mainargs[]) {
	// Verarbeitet Kommandozeilenparameter
	
	boolean errorflag=false;

	for(int i=0;i<mainargs.length;i++) {
	    if (mainargs[i].substring(0,1).equals("-")) { //Schalter
		if ((i+1)==mainargs.length||mainargs[i+1].substring(0,1).equals("-")) {errorflag=true;} else {
		    if (mainargs[i].equals("-ic")) {fileKlass=mainargs[i+1];switch_ic=true;} else
		    if (mainargs[i].equals("-ik")) {fileGrundstock=mainargs[i+1];switch_ik=true;} else
		    if (mainargs[i].equals("-ir")) {fileRegexp=mainargs[i+1];switch_ir=true;} else
		    if (mainargs[i].equals("-rl")) {patFile=mainargs[i+1];switch_rl=true;} else
		    if (mainargs[i].equals("-rp")) {patFile_NE=mainargs[i+1];switch_rp=true;} else
		    if (mainargs[i].equals("-pk")) {n_cands=new Integer(mainargs[i+1]).intValue();switch_pk=true;} else
		    if (mainargs[i].equals("-pt")) {acceptItem=new Double(mainargs[i+1]).doubleValue();switch_pt=true;} else
		    if (mainargs[i].equals("-ss")) {startNr=new Integer(mainargs[i+1]).intValue();switch_ss=true;} else
		    if (mainargs[i].equals("-oi")) {itemFile=mainargs[i+1];switch_oi=true;} else
		    if (mainargs[i].equals("-om")) {maybeFile=mainargs[i+1];switch_om=true;} else
		    if (mainargs[i].equals("-or")) {fileContexts=mainargs[i+1];switch_or=true;} else
		    if (mainargs[i].equals("-og")) {fileGarantie=mainargs[i+1];switch_og=true;} else
		    {errorflag=true;}
		} //esle fi
	    } // fi mainargs.substr (Schalter)
	} // rof i
	if (errorflag) {
	    System.out.println("Was falsch mit den Parametern!");
	    System.exit(1);}
	else {
	    System.out.println("Einstellungen:\n-------------");
	    if (switch_ic)  System.out.print("\n Klassen: "+fileKlass);
	    if (switch_ik)  System.out.print("\n Wissen Items: "+fileGrundstock);
	    if (switch_ir)  System.out.print("\n Wissen Regexp: "+fileRegexp);
	    if (switch_rl)  System.out.print("\n Wissen Regeln: "+patFile);
	    if (switch_rp)  System.out.print("\n Regeln für NEs "+patFile_NE);
	    if (switch_pk)  System.out.print("\n Anzahl Sätze zur Kandidatenüberprüfung "+n_cands);
	    if (switch_pt)  System.out.print("\n Threshhold Anerkennung Item "+acceptItem);
	    if (switch_ss)  System.out.print("\n Beginne bei Satz: "+startNr);
	    if (switch_oi)  System.out.print("\n Datei für neue Items: "+itemFile);
	    if (switch_om)  System.out.print("\n Datei für eventuelle Items: "+maybeFile);
	    if (switch_or)  System.out.print("\n Datei für Kontexte, wenn Regeln irgendwie zuschlagen: "+fileContexts+"\n");
	    if (switch_og)  System.out.print("\n Datei für komplett bekannte Namen: "+fileGarantie+"\n");
	    System.out.println();
	} //esle (params ok)
	
		 
	
	if (!(switch_rl||switch_rf||switch_rt)) {

	
	    //System.out.println("Fehler! [rl|rf|rt] obligatorisch!");
	    //System.exit(1);
	    
	} // fi !switches

    } // end processArguments




    private static String getFromFile(String filename) throws FileNotFoundException, IOException { 
	// liest file ein und liefert String zurück

	FileReader file=new FileReader(filename);
	try{
	   file=new FileReader(filename); 
	} catch (FileNotFoundException e)  {System.out.println("Can't find file "+filename+"\n");}


	int inInt;
	String retString= new String();
	try{
	    while ((inInt=file.read())!=-1) { //lese bis EOF
		retString+=(char)inInt;
	    } // elihw EOF

	} catch (IOException e) {System.out.println("Can't find file "+filename+"\n");}
	return retString;

    } // end getfromFile


    private static void init(String fileGrundstock, String fileRegexp) throws IOException, FileNotFoundException, ClassNotFoundException  {
	//initialisiere die Tabellen und verbinde mit DBs
	alleRegexp.loadFile(fileRegexp);
	allesWissen.loadFile(fileGrundstock);
	klassKeys.loadFile(fileKlass);
	
	
	// init DB
	try {
	    System.out.println("Treiber-init...");
	    Class.forName(dbTreiber);
	    System.out.println("Verbindung-init...");
	    Verbindung_ws=DriverManager.getConnection("jdbc:mysql://lipsia/wortschatz?user=biemann&password=kasmat");
	    Verbindung_akt=DriverManager.getConnection("jdbc:mysql://woclu4/wdtaktuell?user=cbiemann&password=italienisch");


	}
	catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}
	//System.out.println("V:"+Verbindung.toString());


    } //end init


    // Andere Klassen fuer checkCandidates
    private static Annotate anno=new Annotate();	// Annotierer
    private static MatcherNam matcher=new MatcherNam(); // Matcher
  

   private static int nlength(String item) {
	// Errechnedt Länge des Namens, Bei NAmen mit Bindestrichen: länsgter Teilname

	int l=0;
	int lmax=0;

	for(int pos=0;pos<item.length();pos++) {
		if (item.charAt(pos)=='-') {
			if (l>lmax) {lmax=l;}
			l=0;
		} else{ l++;}
	} // rof int pos
	if (l>lmax) lmax=l;
	
	return lmax;

   } // end nlength




    private static NameTable checkCandidates(NameTable toCheck,double schwelle,Vector pattern,Connection Verbindung) throws InterruptedException, IOException, SQLException, Exception {
	// Ueberprueft, ob Kandidaten  in Beispielsätzen, in denen sie vorkommen, auch als "forWhat" klassifiziert werden. Falls der Anteil hoeher als "schwelle" ist, besteht Kandidat Prüfung.
	// Es wird WORTSCHATZ und WDTAKTUELL abgefragt

	String actItem=new String();
	String actClass=new String();
	int actClassInt=0;
	String itemText;
	Vector wordVec;
	Vector classVec;
	int ergClass=0;
 
	Vector ergTest;
	NameTable checked=new NameTable();    //wird mit Kandidaten, die Prüfung bestanden haben, gefüllt.
	int anzOk, anzTot;


	//System.out.println("Tocheck: "+toCheck.toString());
	for (Enumeration checkItems=toCheck.keys();checkItems.hasMoreElements();) {
	    actItem=(String)checkItems.nextElement();
	    actClass=(String)toCheck.get(actItem);
	    actClassInt=new Integer(klassKeys.get(actClass).toString()).intValue();
	    // nehme nächsten Kandidaten
	    
	
// SPEZIAL für VN: VNs mit Länge groesser 10 sind TITEL!

	    if (actClass.equals("VN")&&nlength(actItem)>10) {actClass="TIT";}


	    System.out.println("Ueberpruefe Kandidat "+actItem+"?="+actClass);

	    // check if already known
	    if (allesWissen.containsKey(actItem)) {
		System.out.println("Wissen: "+actItem+"="+allesWissen.get(actItem));
            } // fi allesWissen
	    else { // nur, wenn noch nicht im Wissen



		    itemText=db.getNof(actItem,n_cands,Verbindung);    // finde Sätze, die Kandidaten enthalten
		    itemText+=db.getNof(actItem,n_cands,Verbindung_akt);
		    wordVec=anno.tokenize(itemText);
		    classVec=anno.annotate(wordVec,alleRegexp,allesWissen,klassKeys);
		    ergTest=matcher.getClassificationsOf(actItem,wordVec,classVec,klassKeys,pattern);     //extrahiere Liste, wie aktName klassifiziert wurde
		    anzOk=0; anzTot=0;
		    for (Enumeration e=ergTest.elements();e.hasMoreElements();) {
			ergClass=new Integer(e.nextElement().toString()).intValue();
			
			anzTot++;
			if ((ergClass & actClassInt)==actClassInt) {anzOk++;}        // Zähle Gesamtzahl und Zahl der auf NN klassifizierten
		    } // rof Enumeration ergTest
	
		    if (d) {System.out.println("Kandidat '"+actItem+"' ist "+actClass+" mit "+anzOk+"/"+anzTot);}
		    
		    if (anzTot>0) { // wenn überhaupt gefunden
	
			if (((double)anzOk/(double)anzTot)>schwelle) {
			    if (anzOk>2) {
				checked.put(actItem,actClass);           // Falls Quotient über Schwelle, akzeptiere Kandidaten
				if (d) {System.out.println("akzeptiert! Füge ein:\n"+actItem+"\t"+actClass);}
			        NameTable newitem=new NameTable();
		                newitem.put(actItem, actClass);
       	                 	newitem.appendFile(itemFile);

			    } // fi anzOK
			    else { // elsif anzOK  // dieser fall wird durch (anzOk>0) nicht erreicht!
				if (d) {System.out.println("unter Vorbehalt akzeptiert!");}
			        NameTable maybes=new NameTable();
		                checked.put(actItem, actClass);
				maybes.put(actItem, actClass);
       	                 	maybes.appendFile(maybeFile);
	
			    } // esle
			}
		    } // fi anzTot
	    	} // esle (noch nicht im Wissen)
	    
	} // rof Enumeration name

	return checked;
    } // end checkCandidates






    public static void main(String args[]) throws IOException, InterruptedException, FileNotFoundException, SQLException, ClassNotFoundException, Exception {


	String text;
	NameTable Kandidaten=new NameTable();
	NameTable Kandidaten_alt=new NameTable();

	      
	processArguments(args);
	int bspnr=startNr;
	canrules = matcher.loadPatterns(patFile);
	init(fileGrundstock,fileRegexp);

	rules.loadPatterns(patFile,fileContexts); // Regeln init
	rules.resetRules();
	System.out.println("Anzahl Regeln: "+rules.patterns.size());
	rules_NE.loadPatterns(patFile_NE,fileGarantie); // Regeln init
	rules_NE.resetRules();
	System.out.println("Regeln: rec="+rules.patterns.size()+" NE="+rules_NE.patterns.size());




	text="Müller, Huber, Seifert, Bodden, Abel, Schnoor und ich.";
	
	
	while(!(text.equals("END")||text.equals(""))) {
	  do {
  	    System.out.println(bspnr+": "+text);
	    rules.resetRules(); 
	    Kandidaten_alt=Kandidaten;
	    Kandidaten=textProc.getCandidatesOfText(text, alleRegexp, allesWissen,klassKeys,rules);

	    System.out.println(Kandidaten.toString());
	    allesWissen.insert(checkCandidates(Kandidaten,acceptItem,canrules,Verbindung_ws));
	   } while (false&&(Kandidaten_alt.toString().length()!=Kandidaten.toString().length()));

	  rules_NE.resetRules();
	  textProc.getNEsOfText(text, alleRegexp, allesWissen, klassKeys, rules_NE,Verbindung_akt);  // Extrahieren der NEs und speichern in DB
	    bspnr++;
	    text=db.getBsp(bspnr,Verbindung_akt);
	} // elihw



    } // end main

    
} // end Recognizer
