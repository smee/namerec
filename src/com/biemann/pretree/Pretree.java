package com.biemann.pretree;
import java.io.*;
import java.util.*;

/**
 * @title Präfixkompressionsklassifizierer
 * @author Christian Biemann
 * @version 24.05.2003
 *
 * Externe Funktionen:
 *
 * public void insert(Knoten k):  Fügt Knoten in Baum ein
 * public Knoten find(String w):  Sucht String w in Baum und returnt (end)Knoten mit w
 * public void train(String word, String class) : Einfügen von word mit Klasse class
 * public String classify(String word): Liefert class zu word (bei genug thresh)
 * public void ttyout():   Ausgabe des Baums auf Standardausgabe
   public void prune(); Pruning des Trees
* void load(String filename)  :Läd Baum
 * void save(String filename): Speichert Baum
 *
 * Parameter:
 *
 * boolean d: Debug-Meldungen
 * boolean ignorecase: wie der name sagt
 * double thresh: threshold für klassifizierung
 *
 * Interne Funktionen:
 *
 *
 * Knoten einf(Knoten k1, Knoten k2) :Knoten 2 (Blatt) wird in Knoten 1 eingefügt,
 * Knoten suche(Knoten k, String w)  :String w wird in Knoten k gesucht, returnt Knoten mit w
 * void anzeig(Knoten aktknoten,int n) : Zeigt Knotenstruktur auf Textstandardausgabe.



 * Vector vecAdd(Vector, Vector) addiert Klassenwerte von Vektoren
 */

public class Pretree implements Serializable {


    // Deklaration der Variablen/Instanzen
    static Hashtable hash; //für vecAdd
    static boolean d=false;   // für Meldungen (debugging), false=AUS
    public Knoten wurzel;   // Wurzelknoten; zunächst leerer Baum
    public double thresh=0.51;
    private boolean ignorecase=false;
    private boolean reverse=false;
    // Ende Deklaration Variablen / Instanzen



    // Konstruktior initialisiert Wurzel und Wurzelklassen ----------------------------------------------------
    public Pretree() {

	wurzel=new Knoten("");
	wurzel.classes=new Vector();
    }

    public void setThresh(double d) {
	thresh=d;
    }

    public double getThresh() {
	return thresh;
    }

    public void setIgnoreCase(boolean b) {
	this.ignorecase=b;
    }

      public void setReverse(boolean b) {
	this.reverse=b;
    }


     static String reverse(String torev) {
	String ret=new String();
	for(int i=torev.length();i>0;i--) { ret+=torev.substring(i-1,i);}
	return ret;
    }


    // Voted ermittelt in Abh. von thresh die Entscheidung ---------------------------------------------------
    public String voted(Vector classes) {
	int sum=0;
	int maxval=0, actval;
	String maxclass="undecided";
	String actclass;
	for(Enumeration e=classes.elements();e.hasMoreElements();) {
	    StringTokenizer st=new StringTokenizer((String)e.nextElement(),"=");
	    actclass=st.nextToken();
	    actval= new Integer(st.nextToken()).intValue();
	    sum+=actval;
	    if (actval>maxval) {
		maxval=actval;
		maxclass=actclass;
	    }

	}
//Zeile 100
	if (((double)maxval/(double)sum)>=thresh) {return maxclass;} else {return "undecided";}
    } // end voted

    // vecAdd addiert die Klassen von 2 Vectors -------------------------------------------------------------
    static Vector vecAdd(Vector een, Vector twee) {
	Vector terug = new Vector();
	hash= new Hashtable();
	String clas,snr;
	int nr,nr2;
	String van;
	String cont;
	for(Enumeration e=een.elements();e.hasMoreElements();) {
	    van=(String)e.nextElement();
	    StringTokenizer st= new StringTokenizer(van, "=");
	    clas=st.nextToken();
	    snr=st.nextToken();
	    //	    nr=new Integer(snr).intValue();
	    hash.put(clas, snr);
	}
	for(Enumeration f=twee.elements();f.hasMoreElements();) {
	    van=(String)f.nextElement();
	    StringTokenizer st= new StringTokenizer(van, "=");
	    clas=st.nextToken();
	    if (st.hasMoreTokens()) {snr=st.nextToken();
	    			     nr=new Integer(snr).intValue();}
	    else {nr=0;}
	    cont=(String)hash.get(clas);
	    if (cont!=null) {  // schon vorhanden
		if (d) System.out.println("Cont: "+cont);
		nr2=new Integer(cont).intValue();
		nr=nr+nr2;
	    }
	    snr=(new Integer(nr)).toString();
	    hash.put(clas,snr);
	}

	for (Enumeration g=hash.keys();g.hasMoreElements();) {
	    String c=(String)g.nextElement();
	    String instr=c+"="+hash.get(c);
	    terug.addElement(instr);
	}
	return terug;
    } // end voted


    //////////EINF//**************************************************************************************
    Knoten einf(Knoten k1, Knoten k2) {      //Knoten 2 (Blatt) wird in Knoten 1 eingefügt. 3 Fälle:
	                                     //	wk2= pr   wk1=   1.(e)  2. p    3. ps

	String w0 = new String(); //  Sem(w0)=p
	String w1 = new String(); //  Sem(w1)=s
	String w2 = new String(); //  Sem(w2)=r

	if (k1==null) {                      // FALL 1: k1 ex. noch nicht. Dann k2 hier einhängen, ende.
	   return k2;
	} //end if Fall 1

	int pos=0;             // Matching für Fälle 2 und 3
	int min;

        if (k1.inhalt.length()<k2.inhalt.length()) { min=k1.inhalt.length();}  //Mimimumsbildung für for
         else { min=k2.inhalt.length();}
        for (pos=0;pos<min;pos++) {                                            // Finden von pos für berechn. w0-w2
	  if (k1.inhalt.charAt(pos)!=k2.inhalt.charAt(pos)) break;
	} // rof pos
  	w0 = k2.inhalt.substring(0,pos);                                       // Berechnung w0,w1,w2
	w1 = k1.inhalt.substring(pos, k1.inhalt.length());
	w2 = k2.inhalt.substring(pos,k2.inhalt.length());
	if (w2.length()==0) {
	    k1.setClasses(vecAdd(k1.getClasses(),k2.getClasses()));
	    return k1;
	} // fi w2

	if (w1.length()==0) {                //Fall 2: k1 Anfangsstück von k2, |s|=0
	    k2.inhalt=w2;
	    Knoten goalpos=getChild(k1,w2);
	    if (goalpos==null) {
		k1.kinder.addElement(k2);
	    } else {
		k1.kinder.removeElement(goalpos);
		k1.kinder.addElement(einf(goalpos,k2));
	    }
	    k1.setClasses(vecAdd(k1.getClasses(),k2.getClasses()));
	    return k1;
	}  // end if Fall 2
	else  {                              //Fall 3:  k1 und k2 haben gleiches Präfix p, Suffixe s bzw. r
	    Knoten h=new Knoten(w0);
	    k2.inhalt=w2;
	    h.kinder.addElement(k2);
	    k1.inhalt=w1;
	    h.kinder.addElement(k1);
	    h.setClasses(vecAdd(k1.getClasses(),k2.getClasses()));
	    return h;
	} // end else Fall 3
    } //end Knoten einf(Knoten,Knoten)     // Returnt rekursiv veränderten Unterknoten.


    // getChild liefert kind von Knoten k zurück, in dem w gefunden werden kann
    Knoten getChild(Knoten k, String w) {
	Knoten kind;
	for(Enumeration e=k.kinder.elements();e.hasMoreElements();) {
	    kind=(Knoten)e.nextElement();
	    if (d) {
		// System.out.println("Kind: "+kind.inhalt+" vgl mit "+w);

	    }
	    if (kind.inhalt.substring(0,1).equals(w.substring(0,1))) return kind;
	}
	return null;
    }


    ////////Suche*********************************************************************************************
    Knoten suche(Knoten k, String w) {        // String w wird in Knoten k gesucht.

	Knoten rknoten;
	String w0 = new String(); //s. REM einf
	String w1 = new String(); //
	String w2 = new String(); //

	if (k==null) return null;                                  // FALL 1: p=0 (nicht gefunden)

	int min;                                                     // s. REM einf
	int pos=0;

        if (k.inhalt.length()<w.length()) { min=k.inhalt.length();}  //Mimimumsbildung für for
        else { min=w.length();}
        for (pos=0;pos<min;pos++) {                                 // Finden von pos für berechn. w0-w2
	  if (k.inhalt.charAt(pos)!=w.charAt(pos)) break;
	}

  	w0 = w.substring(0,pos);                                       // Berechnung w0,w1,w2
	w1 = k.inhalt.substring(pos, k.inhalt.length());
	w2 = w.substring(pos,w.length());

	if (w2.length()!=0) {
	    if (d) System.out.println("'"+w2+"'");
	    rknoten= suche(getChild(k,w2),w2);     // weitersuchen in unterkn.
	    if (rknoten!=null) {return rknoten;}
	    else {return k;}
	}

	if (w1.length()!=0) return k;                                          // nicht gefunden: returne letzten Knten

	return k;                                                                 //Suchergebnis durchreichen
    } // end Knoten suche(knoten,wort)                  - Return des Unterknotens;


    ////////////////////////anzeige///////////////////////////////////// für ttyout

    void anzeig(Knoten aktknoten,int n) {            // Zeigt Knotenstruktur auf Textstandardausgabe.
	int tiefe=n+1; // für "-" Anzeige
	for(Enumeration e=aktknoten.kinder.elements();e.hasMoreElements();) {
	    Knoten akk=(Knoten)e.nextElement();
	    for(int j=1;j<=tiefe;j++) System.out.print("-");
	    Vector v=akk.getClasses();
	    if (v!=null) {
	      System.out.println(akk.inhalt+" "+akk.getClasses().toString());} else
		  {System.out.println(akk.inhalt+" nix");}
	  anzeig(akk,tiefe);
	} //for-if
    } // void anzeig(Knote, tiefe)


 String anzeigStr(Knoten aktknoten,int n) {            // Zeigt Knotenstruktur auf Textstandardausgabe.
   String retStr="";
	int tiefe=n+1; // für "-" Anzeige

  // aktueller Knoten
  for(int j=1;j<=n;j++) retStr+="-";
  if (reverse) {retStr+=reverse(aktknoten.inhalt)+" "+aktknoten.getClasses().toString()+"\n";}
  else {retStr+=aktknoten.inhalt+" "+aktknoten.getClasses().toString()+"\n";}



  // kinder
	for(Enumeration e=aktknoten.kinder.elements();e.hasMoreElements();) {
	    Knoten akk=(Knoten)e.nextElement();
	    for(int j=1;j<=tiefe;j++) retStr+="-";
	    Vector v=akk.getClasses();
//	    if (v!=null) {
//	      if (reverse) {retStr+=reverse(akk.inhalt)+" "+akk.getClasses().toString()+"\n";}
 //       else {retStr+=akk.inhalt+" "+akk.getClasses().toString()+"\n";}
 //     }

	  retStr+=anzeigStr(akk,tiefe);
	} //for-if
    return retStr;
  } // String anzeigStr(Knote, tiefe)


	//*********************** Pruning *************************

    Knoten pruneKnoten(Knoten aktKnoten) {
	String vklass="";  // voted Klasse
	String aklass="";  // aktuelle Klasse
	StringTokenizer st; // für Vektoraunseinandernehmen
	Knoten akk;
	Vector temp;

	//System.out.println("Anzahl: "+aktKnoten.classes.size()+" von "+ aktKnoten.classes.toString()+" mit "+aktKnoten.kinder.size()+ " Kindern, Inhalt "+aktKnoten.inhalt);
	// Blatt: Schneide Inhalt ab
	if (aktKnoten.kinder.size()==0) {aktKnoten.inhalt=aktKnoten.inhalt.substring(0,1);}
 	// Eindeutige Klasse: Abschneiden des unterbaumes
	else if (aktKnoten.classes.size()==1) {
	    aktKnoten.inhalt=aktKnoten.inhalt.substring(0,1);
	    aktKnoten.kinder.removeAllElements();
	}
 	// innerer Knoten: rekursiv absteigen und default löschen
	else {
	    vklass=voted(aktKnoten.classes);
	    temp=new Vector();
  	    for(Enumeration e=aktKnoten.kinder.elements();e.hasMoreElements();) {
 	   	akk=(Knoten)e.nextElement();
		if (akk.classes.size()==1) { // falls Eindeutig
		  aklass=(String)akk.classes.elementAt(0);
		  st=new StringTokenizer(aklass,"=");
		  aklass=st.nextToken();
		  //System.out.println("Class "+aklass);
		  if (aklass.equals(vklass)) {} else
		  {
		    	akk=pruneKnoten(akk);
			temp.addElement(akk);
		  }
		} else {
  	    		akk=pruneKnoten(akk);
			temp.addElement(akk);
		}
  	    } //for-if
	    aktKnoten.kinder=temp;
	} // else

	return aktKnoten;
    } // end pruneKnoten


     String tree2string(Knoten aktknoten, int ebene) {   // Umwandlung der Baumstruktur in String.
	// String besteht aus Wörtern, die mit Kommas in Klammern getrennt sind und Unterklammern haben dürfen
	//  z.B..  (A,B(C,D,E(F,G)),U) sind die Wörter A,BC,BD,BEF,BEG,U.

	String returnstring=new String();
	Knoten akk;
	String classElement;

	int k;
	int j=0;
	char delimit=(char)(160-ebene);
	returnstring+="|";         // "(" Ebene tiefer
	for (Enumeration e=aktknoten.kinder.elements();e.hasMoreElements();) {
	    j++;
	    if (j!=1) returnstring+=delimit;	       // "," selbe Ebene
	    akk=(Knoten)e.nextElement();
	    returnstring+=akk.inhalt;
	    returnstring+="[";
	    k=0;
	    for (Enumeration f=akk.classes.elements();f.hasMoreElements();) {
		k++;
		if (k!=1) returnstring+=";";
		classElement=(String)f.nextElement();
		returnstring+=classElement;
	    }
	    returnstring+="]";
	    if (akk.kinder.size()>0) {
		returnstring+=tree2string(akk,ebene+1);
	    } // end if soehne >0
	} // end for

	returnstring+="";         // ")" Ebene zurück
	return returnstring;
    } // end tree2string

    Knoten string2tree(Knoten aktknoten, String s, int ebene) {

	// 1.  Zerhacke aufgrund ebeneninfo
	// 2. Wenn atom, füge ein, sonst zerhacke weiter


	String akttoken;
	char delimit=(char)(160-ebene);
	String delString="";
	delString+=delimit;
	StringTokenizer st=new StringTokenizer(s, delString);
	StringTokenizer st2;
	String token;
	String inhalt;
	String klasse;
	Vector klassv;
	String ganztoken;
	String resttoken="";

	ganztoken=s;
	if (d) System.out.println("Ebene "+ebene+":\t"+ganztoken);

	st2=new StringTokenizer(ganztoken,"|");
	token=st2.nextToken();
	if (st2.hasMoreTokens()) {resttoken=ganztoken.substring(token.length()+1,ganztoken.length()); } else resttoken="";


	if (d) System.out.println("Zerlegt in :"+token+" resttoken: "+resttoken);

	st2 = new StringTokenizer(token, "[");
	inhalt=st2.nextToken();
	klasse=st2.nextToken();
	klasse=klasse.substring(0,klasse.length()-1);
	if (d) System.out.println("Inh: "+inhalt+" Klassen: "+klasse);
	st2 = new StringTokenizer(klasse,";");
	klassv=new Vector();
	while(st2.hasMoreTokens()) {
	    String ak=st2.nextToken();
	    if (d) System.out.print("T: "+ak);
	    klassv.addElement(ak);
	}
	if (d) System.out.println(klassv.toString());

	Knoten neuknoten=new Knoten(inhalt);
	neuknoten.setClasses(klassv);


	st = new StringTokenizer(resttoken, delString);
	if (token!=ganztoken) while(st.hasMoreTokens()) {
	    akttoken=st.nextToken();
	    if (d) System.out.println("Kind :"+akttoken);
	    if (d) System.out.println("jetzt");
	    string2tree(neuknoten,akttoken,ebene+1);
	    //aktknoten.kinder.addElement(neuKnoten);
	}




	aktknoten.kinder.addElement(neuknoten);
	return aktknoten;



    } // end string2tree



     Knoten string2tree_alt(Knoten aktknoten, String s) { // Wandelt String in Baum

	s=s.substring(1,s.length()-1);
	if (d) System.out.println("Nun:"+s);
	String inhalt, klasse;
	String token=new String();
	String pretoken= new String();
	String resttoken = new String();
	int klampos=0;
	int klamopen=0;
	int lastend=0;
	Vector klassv;
	StringTokenizer st;
	boolean naatom=false;     // "not an atom", also enthält noch Klammern

	for (int pos=0;pos<s.length();pos++) { // ganzen String durchgehen...
	    char zp=s.charAt(pos);
	    if (((int)zp!=44)||(klamopen!=0)) token+=zp;
	    if ((!naatom)&&((int)zp==40))  // "(" kommt vor
		{naatom=true; klampos=pos-lastend;
		if (d) System.out.println("Erste Klammer auf bei:"+klampos+"  "+pos+"-"+lastend);
		}
	    if ((int)zp==40) klamopen++;    // Zaehlen der Ebenen, damit richtiges "," interpretiert wird;
	    if ((int)zp==41) klamopen--;


	    if ((((int)zp==44)&&(klamopen==0))||(pos==s.length()-1)) {   // 44="," ;token fertig; muss drunter eingefuegt werden.
		if (d) System.out.println("Token:"+token+" Klampos:"+klampos);
		if (!naatom) {
		    st = new StringTokenizer(token, "[");
		    inhalt=st.nextToken();
		    Knoten neuKnoten=new Knoten(inhalt);
		    klasse=st.nextToken();
		    klasse=klasse.substring(0,klasse.length()-1);
		    if (d) System.out.println("Inh: "+inhalt+" Klassen: "+klasse);
		    st = new StringTokenizer(klasse,";");
		    klassv=new Vector();
		    while(st.hasMoreTokens()) {
			String ak=st.nextToken();
			if (d) System.out.print("T: "+ak);
			klassv.addElement(ak);
		    }
		    if (d) System.out.println(klassv.toString());
		    neuKnoten.setClasses(klassv);

		    aktknoten.kinder.addElement(neuKnoten);} //war atom
		else { // kein atom

		    pretoken=token.substring(0,klampos);                   // Anfang abspalten
		    resttoken=token.substring(klampos,token.length());     // Rest -"-
		    if (d)  System.out.println("Anfang:"+pretoken+".  Ende:"+resttoken+".");

		    st = new StringTokenizer(pretoken, "[");
		    inhalt=st.nextToken();
		    Knoten neuKnoten=new Knoten(inhalt);
		    klasse=st.nextToken();
		    klasse=klasse.substring(0,klasse.length()-1);
		    st = new StringTokenizer(klasse,";");
		    klassv=new Vector();
		    while(st.hasMoreTokens()) {
			String ak=st.nextToken();
			if (d) System.out.print("T: "+ak);
			klassv.addElement(ak);
		    }
		    if (d) System.out.println(klassv.toString());
		    neuKnoten.setClasses(klassv);



                    //aktknoten.kinder.addElement(neuKnoten);

		    if (resttoken.length()!=0) {
			Knoten neukind=new Knoten();
			if (d) System.out.println("jetzt");
			string2tree_alt(neuKnoten,resttoken);
		    }
		     aktknoten.kinder.addElement(neuKnoten);
		} // end else

		naatom=false;         //
		token="";             //
		klamopen=0;           //   reset für neues Token
		klampos=0;            //
                lastend=pos+1;        //

	    } // end if zp=44
	} // end for pos

	return aktknoten;

    } // end void string2tree


    // Ansteuerung als Objekt Pretree **************************************************************************
    // ***********************************************************************************Schnittstelle*******

    public void train(String word, String cla) {
	if (ignorecase) word=word.toLowerCase();
  if (reverse) word=reverse(word);
	Knoten k=new Knoten(word+"<");
	k.classes=new Vector();
	k.classes.addElement(cla+"=1");
	insert(k);
    }


    public int getNrOfClasses() {
           Vector wurzelvec=new Vector();
           wurzelvec=wurzel.classes;
           return wurzelvec.size();
    }

    public int getNrOfNodes() {
           int retval=0;
           Vector wurzelvec=new Vector();
           wurzelvec=wurzel.classes;
           String actStr="";
           StringTokenizer st;
           String wclass="",welement="";
           int actInt=0;
           for (Enumeration f=wurzelvec.elements();f.hasMoreElements();) {
               actStr=(String)f.nextElement();
               st= new StringTokenizer(actStr,"=");
               wclass=st.nextToken();
               welement=st.nextToken();
               actInt=new Integer(welement).intValue();
               retval+=actInt;
           }
           return retval;
    }


    public String classify(String word) {
	if (ignorecase) word=word.toLowerCase();
  if (reverse) word=reverse(word);
	Knoten k=find(word+"<");
	return voted(k.classes);
    }


    public void insert(Knoten k) {
	if (d) System.out.println("Inserting:"+k.inhalt);
	wurzel.classes=vecAdd(wurzel.classes,k.classes);
	Knoten gpos=getChild(wurzel,k.inhalt);
	if (gpos==null) {
	    gpos=k;
	    wurzel.kinder.addElement(gpos);
	} else {
	    wurzel.kinder.removeElement(gpos);
	    wurzel.kinder.addElement(einf(gpos,k));
	}
    }


    public Knoten find(String w) {
	Knoten wchild=getChild(wurzel,w);
	if (wchild==null) {return wurzel; }
	else {return suche(wchild,w);}
    }


    public void ttyout() {

	System.out.println(wurzel.inhalt+" "+wurzel.classes.toString());
	anzeig(wurzel,0);

    }

    public String giveReason(String w) {
    	if (ignorecase) w=w.toLowerCase();
      if (reverse) w=reverse(w);
      w=w+"<";
      Knoten k=find(w);
//      System.out.println("Knoten: "+k.toString()+"="+k.inhalt+" "+k.classes.toString());
      return anzeigStr(k,0);
    }


    public void speichere(String filename) throws IOException {

	String outstr=new String();
	String classElement;

	 outstr+=wurzel.inhalt;
	 outstr+="[";
	 int k=0;

	 for (Enumeration f=wurzel.classes.elements();f.hasMoreElements();) {
	     k++;
	     if (k!=1) outstr+=";";
	     classElement=(String)f.nextElement();
	     outstr+=classElement;
	 }
	 outstr+="]";


	outstr+=tree2string(wurzel,0);

       try {
              ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(filename));
              oos.writeObject(outstr);
              oos.close();
       } catch (IOException ex) {}

    } // ende public void speichere







       public void lade(String filename) throws FileNotFoundException {

	int r=0;
	String instr=new String();

	try{
              ObjectInputStream ois= new ObjectInputStream(new FileInputStream(filename));
             instr=(String)ois.readObject();
              ois.close();


       } catch (IOException ex) {System.out.println(ex.getMessage());}
         catch (ClassNotFoundException cnfe) { /* readObject() can throw this */ }


	if (d) System.out.println("File read");
	StringTokenizer st=new StringTokenizer(instr,"]");
	String wclasses=st.nextToken();
	wclasses=wclasses.substring(1,wclasses.length());
	instr=instr.substring(wclasses.length()+2,instr.length());
	if (d) System.out.println(instr);
	if (d) System.out.println("Now inserting");

	st = new StringTokenizer(wclasses,";");
      	Vector klassv=new Vector();
	while(st.hasMoreTokens()) {
	    String ak=st.nextToken();
	    klassv.addElement(ak);
	}
	wurzel.setClasses(klassv);
	String char160="";
	char160+=(char)(160);

	StringTokenizer eb1=new StringTokenizer(instr, char160);
	while (eb1.hasMoreTokens()) {
	    String nextTok=eb1.nextToken();
	    string2tree(wurzel,nextTok,1);
	}
    } // end public void lade



    public void save(String filename) /*throws IOException*/ {

       try {
              ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(filename));
              oos.writeObject(this);
              oos.close();
       } catch (IOException ex) {}


    }

    public void prune() {
       	 wurzel=pruneKnoten(wurzel);
    }  // end prune()




    public Pretree load(String filename) /*throws FileNotFoundException*/
    {
	Pretree loadtree=new Pretree();
	try{
              ObjectInputStream ois= new ObjectInputStream(new FileInputStream(filename));
             loadtree=(Pretree)ois.readObject();
              ois.close();


       } catch (IOException ex) {System.out.println(ex.getMessage());}
         catch (ClassNotFoundException cnfe) { /* readObject() can throw this */ }

	return loadtree;

    }

} // end class Pretree
