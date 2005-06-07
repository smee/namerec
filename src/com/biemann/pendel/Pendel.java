package com.biemann.pendel;
import java.sql.*;
import java.util.*;
import java.io.*;


public class Pendel implements Runnable  {

  Thread mythread=null;
  static boolean d=true; //debugging an
  static Connection Verbindung;
  private static int n_cands=30;
  private static int n_item=255;  private static double acceptItem=0.1;  private static double acceptRule=0.5;  private static int minRule=3;  private static int maybeCount=2;
  static NameTable checkedArr;
  static String index[];
  static int vcount=0;
  static Verify[] verify;
  static String dbTreiber = "org.gjt.mm.mysql.Driver";  // Treiber für Datenbankanbindung

      // Nametables fuer Wissen aller Art
  private static NameTable alleRegexp=new NameTable(); // Regexps
  public static NameTable allesWissen=new NameTable(); // Alle bekannten items
  private static NameTable klassKeys=new NameTable(); // Bitzuordnung zu Klassen
 public static Vector rules=new Vector();  // Regeln
 private static Vector patterns=new Vector();  // Extrpats
 private static Rules findRules=new Rules(); private static RulesNE extrRules=new RulesNE();

  private static String itemFile="itemsFound.txt";
  private static String maybeFile="maybes.txt";
  private static String fileContexts="contexts.txt";
  private static String fileGarantie="granted.txt";
  private static boolean maybeWrite=false;
  private static boolean itemWrite=false;
  private static String dbString="";

	String key=new String();
	String klassAs=new String();
	int anzNeu=1;
	int schritt=0;
	String item="";
	String actItem;
	String textItem;
	String spalten=new String();
	NameTable Kandidaten=new NameTable();
	public static NameTable actItems=new NameTable();
	public static NameTable neueItems=new NameTable();


    // Andere Klassen
    private static Annotate anno=new Annotate();	// Annotierer
    private static TextProcessor textProc=new TextProcessor(); // Matcher
    private static DBaccess db=new DBaccess(); // Klasse fuer alles, was mit der Wortschatzdatenbank zu tun hat
  // Vectoren fuer Ablauf
    private static Vector wordVector= new Vector();   // Wörter
    private static Vector classVector= new Vector();  // Klassen

    public  void schlafen(int sleep) {
        try{
         this.mythread.sleep(sleep);
        } catch (InterruptedException e) {}
    }

   public Pendel(int n_search,int n_candits,int min_count,int minrulecount,
                        double thresh_item,double thresh_rule,String db_String,
                        NameTable klassKeysTable,NameTable regexpNameTable,
                        Vector classRules,Vector extrPatsTable,
                        NameTable inItemsNameTable, NameTable inItemsBackTable,
                        String fileItems,boolean boolItems,String fileContexts,boolean boolContexts,
                        String fileMaybes, boolean boolMaybes, String fileEntities, boolean boolEntities,
                        String fileLog,boolean boolLog) {


      //Parameters
  n_item=n_search; n_cands=n_candits;
  maybeCount=min_count; minRule=minrulecount;
  acceptItem=thresh_item; acceptRule=thresh_rule;
  dbString=db_String;
  klassKeys=klassKeysTable; alleRegexp=regexpNameTable;
  rules=classRules;
  patterns=extrPatsTable;
  findRules.initPatterns(classRules,fileContexts, boolContexts);
  extrRules.initPatterns(patterns,fileEntities, boolEntities);

  allesWissen=new NameTable();
  allesWissen.insert(inItemsNameTable);
  allesWissen.insert(inItemsBackTable);
  actItems=new NameTable();
  actItems.insert(inItemsNameTable);


  itemFile=fileItems; //boolItems
  fileContexts=fileContexts; //boolContexts,
  maybeFile=fileMaybes; // boolMaybes,
  maybeWrite=boolMaybes;
  itemWrite=boolItems;
  fileGarantie=fileEntities; //boolEntities,
//                        fileLog,boolLog



    mythread=new Thread(this);
    mythread.start();
  }



    private static void initDB() throws ClassNotFoundException  {
	//initialisiere die Tabellen

	// initialisiere DB

	 try {
	  	System.out.println("Treiber-init...");
	       Class.forName(dbTreiber);
	       System.out.println("Verbindung-init...");
	       Verbindung=DriverManager.getConnection(dbString);
	 }
	 catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}


    } //end init








  private  NameTable checkCandidates(NameTable toCheck,double schwelle,Vector pattern, String sourceItem) throws InterruptedException, IOException, SQLException, Exception {
	// Ueberprueft, ob Kandidaten  in Beispielsätzen, in denen sie vorkommen, auch als "forWhat" klassifiziert werden. Falls der Anteil hoeher als "schwelle" ist, besteht Kandidat Prüfung.


	String actItem=new String();
	String actClass=new String();
	int actClassInt=0;
	int ergClass=0;


	NameTable checked=new NameTable();    //wird mit Kandidaten, die Prüfung bestanden haben, gefüllt.
	int anzOk, anzTot;
  checkedArr=new NameTable();
  int totalnr= toCheck.size();
  index= new String[totalnr];
  verify=new Verify[totalnr];
  for(int i=0;i<totalnr;i++) {index[i]="on"; }  //alle auf laufend setzen;
  vcount=0;
	for (Enumeration checkItems=toCheck.keys();checkItems.hasMoreElements();) {
	    actItem=(String)checkItems.nextElement();
	    actClass=(String)toCheck.get(actItem);
      verify[vcount]=new Verify(this, vcount, checkedArr, actItem, actClass, schwelle, Verbindung, n_cands, alleRegexp, allesWissen, klassKeys, rules, maybeCount, maybeFile, maybeWrite, itemFile, itemWrite, sourceItem);

      vcount++;
      schlafen(100);
  } // rof Enumeration
  // warte, bis alle fertig
  int sum=1;
  while(sum>0) {

      sum=0;
      for(int i=0;i<totalnr;i++) {
              if (verify[i].running) {sum++;} }
      schlafen(500);
      //System.out.println("Still running: "+sum);
  }

    vcount=0;
/*  for (int i=0;i<totalnr;i++) {
      if (checkedArr[i].size()>0) {checked.insert(checkedArr[i]);}
  } // rof
*/

  return checkedArr;
  } // end checkCandidates




  public void run()  {

  // throws IOException, InterruptedException, FileNotFoundException, SQLException, ClassNotFoundException



    try{

      initDB();
      anzNeu=1;

    	while((anzNeu)>0) { //main loop: solange noch was gefunden wurde
	    for (Enumeration items = actItems.keys() ; items.hasMoreElements() ;) {    //für alle neuen Items

		actItem=(String)items.nextElement();
		System.out.println("Behandle Item '"+actItem+"'");
		textItem=db.getNof(actItem,n_item,Verbindung);         //hole Sätze mit Item aus DB
    findRules.resetRules();

		Kandidaten=textProc.getCandidatesOfText(textItem, alleRegexp, allesWissen, klassKeys,findRules);  // filtere Kandidaten für neue Items aus
/*		System.out.println("Kandidatenliste aufgrund '"+actItem+"'");
		for (Enumeration e = Kandidaten.keys(); e.hasMoreElements();) {
		    key=(String)e.nextElement();
		    klassAs=(String)Kandidaten.get(key);
		    System.out.println(" "+key+"\t"+klassAs);
		} // rof enum e
*/
		Kandidaten=checkCandidates(Kandidaten,acceptItem,rules,actItem);               // Überprüfen Kandidaten

		System.out.println("Neue Items aufgrund '"+actItem+"'");
		for (Enumeration e = Kandidaten.keys(); e.hasMoreElements();) {
		    key=(String)e.nextElement();
		    klassAs=(String)Kandidaten.get(key);
		    System.out.println(" "+key+"\t"+klassAs);
		} // rof enum e
    actItems.remove(actItem);
		neueItems.insert(Kandidaten);
		allesWissen.insert(Kandidaten);
    if (extrRules.writing) {   // nur wenn schreiben Entities angeschaltet

       extrRules.resetRules();
       textProc.getNEsOfText(textItem, alleRegexp, allesWissen, klassKeys,extrRules);
    }
   } // rof Enumeration neueItems
    anzNeu=0;
		System.out.println("Neue Items:");
		for (Enumeration e = neueItems.keys(); e.hasMoreElements();) {
		    key=(String)e.nextElement();
		    klassAs=(String)neueItems.get(key);
		    System.out.println(" "+key+"\t"+klassAs);
		    anzNeu++;
		} // rof enum e


		schritt++;
		System.out.println("\nSchritt "+schritt+"\n-------\nNeue Items: "+anzNeu);
		actItems=new NameTable();
		actItems.insert(neueItems);
    neueItems=new NameTable();
	} //elihw main loop


    } catch (Exception e) {}
  }



  public void pause() {
         mythread.suspend();
  }
  public void weiter() {
         mythread.resume();
  }

  public void stop() {
    if (vcount>0) {
	for(int i=0;i<vcount;i++) {
		if (verify[i].running) {verify[i].stop();verify[i]=null;}
       }
       vcount=0;
    }
    if (mythread!=null) mythread.stop();
    mythread=null;
  }
}