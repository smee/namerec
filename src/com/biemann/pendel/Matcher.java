package com.biemann.pendel;

import java.util.*;
import java.io.*;

public class Matcher {


    private static boolean d=false; // Debugging

    public static int width=9;
    public static int middle=4;
    public static int shortest=2;
    public static int longest=5;
    public static Vector StringPatterns=new Vector(); // für Doublettencheck
    public static Vector patterns=new Vector();

  public Vector loadPatterns(String patfile) throws IOException, FileNotFoundException {
	FileReader file=new FileReader(patfile);
	String inLine="";
	int inInt;
	Vector retvec=new Vector();


	String goalClass;
	int length;
	int goalPos;
	String dummy;


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

    StringTokenizer dummyTokens=new StringTokenizer(dummy,"=");
    dummy=dummyTokens.nextToken();
    if (dummyTokens.hasMoreTokens()) {goalClass=dummyTokens.nextToken();}
	else {goalClass="unspecified";}
    dummyTokens=new StringTokenizer(tokens.nextToken(),"=");
    dummy=dummyTokens.nextToken();
		length=new Integer(dummyTokens.nextToken()).intValue();
    dummyTokens=new StringTokenizer(tokens.nextToken(),":");
    dummy=dummyTokens.nextToken();
    dummy=dummyTokens.nextToken();
		goalPos=new Integer(dummy).intValue();
		String pats[]=new String[length];
		dummy=tokens.nextToken(); // nun sowas wie: pattern= VN KL VN GR

		StringTokenizer ptokens= new StringTokenizer(dummy," ");
		ptokens.nextToken(); // omit "pattern="
		for(int i=0;i<length;i++) {
		    pats[i]=ptokens.nextToken();
		} // rof i

		Pattern inputpat=new Pattern();
		inputpat.init(goalClass,length,goalPos,pats);
		inputpat.partialInit();
	        if (d) System.out.println("Newpat: "+inputpat.toString());
		retvec.addElement(inputpat);
	       } // fi -1
	       inLine="";
	    } // elihw EOF

	} catch (IOException e) {System.out.println("Can't find file "+patfile+"\n");}

	return retvec;
    } // end loadPatterns

  public void saveFile(String filename) throws IOException{
	FileWriter file=new FileWriter(filename,false);
	String outstr=new String();
  Pattern actPat=new Pattern();
	char outChar;

	outstr="";
  for (Enumeration e=patterns.elements();e.hasMoreElements();) {
    actPat=(Pattern)e.nextElement();
    outstr+=actPat.toString()+"\n";
  } //rof



	try {
	     for (int pos=0;pos<outstr.length();pos++) {
    		 outChar=outstr.charAt(pos);

		     file.write((int)outChar);
	     } //rof

	} catch (IOException e){System.out.println("Can.t write "+filename);}
	finally {file.close();}

    } // end saveFile



    public static void newPatterns(int start, int end, int goalpos, int size, String goalString, Vector already, Vector[] inbuff) {
	String actClass=new String();



	// Rek. Ende
	if (start>end) { // Pattern gefunden
	    Pattern p=new Pattern();
	    String[] pat=new String[size];
	    String newpat=new String();
	    int po=0;
	    for(Enumeration pa=already.elements();pa.hasMoreElements();) {
		pat[po]=(String)pa.nextElement();
		po++;
	    } // rof
	    p.init(goalString,size,size-goalpos-1,pat);
	    newpat=p.toString();
	    if (StringPatterns.contains(newpat)==false) { // nur wenn noch nicht drin
		StringPatterns.addElement(newpat);
		patterns.addElement(p);
		if (d) System.out.println("Neu: "+newpat);
	    } // fi patterns.contains

	} else { // weitermachen
	    for(Enumeration cla=inbuff[start].elements();cla.hasMoreElements();) {
		actClass=(String)cla.nextElement();
		if ((actClass.equals(goalString))&&(start==end-goalpos)) {} else { // nur, wenn Zieposition nicht Zielklasse
		    already.addElement(actClass);
		    newPatterns(start+1,end,goalpos,size,goalString,already,inbuff);
		    already.removeElement(actClass);
		} //esle
	    } // for Enumeration cla
	} // esle if start>end


    } // end newpatterns


    public static Vector findPattern(String goalClass, Vector annoVec,NameTable classKeys) {


	Vector inbuff[]=new Vector[width];
	int buffer[]=new int[width];
	int goalInt=new Integer(classKeys.get(goalClass).toString()).intValue();
	int actClassInt=0;
	String actClass=new String();
	int bpos=0;
	patterns=new Vector();



	for(Enumeration av=annoVec.elements();av.hasMoreElements();) { // für alle items
	    if (bpos<width) { // wenn buffer noch nicht voll
		buffer[bpos]=new Integer(av.nextElement().toString()).intValue();
		bpos++;
	    } else { // buffer voll

		if ((buffer[middle] & goalInt)==goalInt) {// falls in buffer[middle] Zielklasse
		    // clear inbuff
		    for(int i=0;i<width;i++) {inbuff[i]=new Vector();}

		    // Ermittle Klassifikationen für buffer[i] in inbuff[i]
		    for(int i=0;i<width;i++) {
			// inbuff[i].addElement("DC"); // dont care ist default
			for(Enumeration cla=classKeys.keys();cla.hasMoreElements();) {
			    actClass=(String)cla.nextElement();
			    actClassInt=new Integer(classKeys.get(actClass).toString()).intValue();
			    if ((buffer[i] & actClassInt)==actClassInt) { // wenn klasassifikation in buffer[i] vorhanden
				inbuff[i].addElement(actClass);
			    } // fi buffer[i]=actClass
			} // Enumeration cla
		    } // rof i

		    //Erzeuge Pattern
		    for(int size=shortest;size<=longest;size++) { // für alle Längen
			for(int gpos=0;gpos<size;gpos++) { // und alle Startpositionen
			    Vector already=new Vector();
			    newPatterns(middle-size+gpos+1,middle+gpos,gpos,size,goalClass,already,inbuff); // Rekursionsaufruf
			} // rof gpos
		    } // rof size

		} // fi buffer[middle] Zielklasse

		// Rücke buffer eins weiter
		for(int i=0;i<width-1;i++) {
		    buffer[i]=buffer[i+1];
		} // rof i
		buffer[width-1]=new Integer(av.nextElement().toString()).intValue();


	    } // esle (buffer voll)
	} // rof Enumeration av
	annoVec=new Vector();

	annoVec=null;
	classKeys=null;

	return patterns;
 } // end findPattern

    public static Vector testLiability(Vector patterns,Vector classVec, Vector wordVec, NameTable klassKeys,double threshold,double minRule) {

	Vector rpats= new Vector();
	Pattern actPat=new Pattern();
	int index=0;
	int bpos=0;
	int[] buffer = new int[width];
	boolean matchflag;



	if (d) System.out.println("Start Test Liability mit "+classVec.size()+" Txtlänge und "+patterns.size()+" zu überprüfenden Regeln");
	for(Enumeration clV=classVec.elements();clV.hasMoreElements();) {

	    if (bpos<longest) { // wenn buffer noch nicht voll
		// lasse buffer vollaufen
		buffer[bpos]=new Integer(clV.nextElement().toString()).intValue();
		bpos++;
	    } else { // sonst: buffer voll

		for(Enumeration pats=patterns.elements();pats.hasMoreElements();) {// für alle Pattern
		    actPat=(Pattern)pats.nextElement();
		    //patterns.removeElement(actPat);
		    // matche
		    matchflag=true;
		    for(int pos=0;pos<actPat.length;pos++) { // für alle Musterteile im Pattern
			if (matchflag) { // nur, wenn noch matcht
			    int actpatternpos=new Integer(klassKeys.get(actPat.pattern[pos]).toString()).intValue();
			    if ((buffer[pos] & actpatternpos)!= actpatternpos) {matchflag=false;} // wenn nicht matched, dann falsch

			    //			    System.out.println("Matche: "+buffer[pos]+"&"+actpatternpos+" = "+matchflag);

			} // fi matchflag
			
		    } // rof  pos

		    if (matchflag) { // wenn pattern tatsächlich matcht

		

			int goalClassInt=new Integer(klassKeys.get(actPat.goalClass).toString()).intValue();
			if ((buffer[actPat.goalPos] & goalClassInt)==goalClassInt) { // wenn tatsächlich Zielklasse
			    actPat.hits++;

			    if (d) { System.out.print("HIT:");}

			} else { // wenn falsch klassifiziert}
			    actPat.misses++;
			    if (d) {System.out.print("MISS:");}

			}

			if (d) { // bei debugging:
			    // Ausgabe pattern und bsp
			    System.out.print("Pattern: "+actPat.toString()+" Text: ");
			    for( int k=0;k<actPat.length;k++) {
				System.out.print(wordVec.elementAt(bpos-longest+k)+" ("+classVec.elementAt(bpos-longest+k)+") ");
			    } // rof k
			    System.out.println();
			} // fi d


		    } // fi matchflag



		    //patterns.addElement(actPat);
		} // rof Enum pats

		// eins weiter im buffer
		for(int i=0;i<longest-1;i++) {
		    buffer[i]=buffer[i+1];
		} // rof i
		buffer[longest-1]=new Integer(clV.nextElement().toString()).intValue();
		bpos++;
		if (d) System.out.println("Buffer: "+buffer[0]+" Wort: "+wordVec.elementAt(bpos-longest).toString());
		

	   
	    } // esle (buffer voll)

	} // rof Enumeration clV

	//berechne ratings, wenn positiv, dann lasse drin

	Pattern actRPat=new Pattern();


	for(Enumeration rpat=patterns.elements();rpat.hasMoreElements();) {
	    actRPat=(Pattern)rpat.nextElement();
	    actRPat.calcRating();
	    if ((actRPat.rating>=threshold) // nur wenn rating ausreichend
		&&(actRPat.hits>=minRule) // keine Eintagsfliegen
		)

		{rpats.addElement(actRPat);
		System.out.println(actRPat.toString());
		} //fi rating and hits
	} // rof Enumeration rpat


	if (d) System.out.println("Ende Test Liability");

	klassKeys=null;
	classVec=null;
	wordVec=null;
	patterns=null;
	
	return rpats;

    } // end testliability

    public NameTable getCandidates(Vector rules,Vector wordVec, Vector classVec, NameTable klassKeys, String neFile, String gaFile) throws IOException {

	//liefert candidatenliste

      
	NameTable cands= new NameTable();
	Pattern actPat=new Pattern();
	int index=0;
	int bpos=0;
	int[] buffer = new int[width];
	boolean matchflag;
	
	FileWriter gfile=new FileWriter(gaFile,true);
	FileWriter cfile=new FileWriter(neFile,true);

	if (d) System.out.println("Matche mit "+rules.size()+ " Regeln." );

	for(Enumeration clV=classVec.elements();clV.hasMoreElements();) {
	    if (bpos<longest+1) { // wenn buffer noch nicht voll
		// lasse buffer vollaufen
		buffer[bpos]=new Integer(clV.nextElement().toString()).intValue();
		bpos++;
	    } else { // sonst: buffer voll
		
 
		for(Enumeration pats=rules.elements();pats.hasMoreElements();) {// für alle Pattern
		    actPat=(Pattern)pats.nextElement();
		    // matche
		    matchflag=true;
		    for(int pos=0;pos<actPat.length;pos++) { // für alle Musterteile im Pattern
			if (matchflag) { // nur, wenn noch matcht
			    int actpatternpos=new Integer(klassKeys.get(actPat.pattern[pos]).toString()).intValue();
			    if ((buffer[pos] & actpatternpos)!= actpatternpos) {matchflag=false;} // wenn nicht matched, dann falsch

			    //			    System.out.println("Matche: "+buffer[pos]+"&"+actpatternpos+" = "+matchflag);

			} // fi matchflag
			
		    } // rof  pos
		    if (d) {System.out.println("Matche mit: "+actPat.toString());}
		    if (d) for( int k=0;k<actPat.length;k++) {
			System.out.print(wordVec.elementAt(bpos-longest+k)+" ("+classVec.elementAt(bpos-longest+k)+") ");
		    } // rof k

		    if (matchflag) { // wenn pattern tatsächlich matcht

			int goalClassInt=new Integer(klassKeys.get(actPat.goalClass).toString()).intValue();
			if ((buffer[actPat.goalPos] & goalClassInt)!=goalClassInt) { // wenn noch nicht als  "Zielklasse" klassifiziert 
			    String candString=new String();
			    candString=wordVec.elementAt(bpos-longest+actPat.goalPos).toString();

			    
			    cands.put(candString,actPat.goalClass);
			   
			    if (d) { System.out.print("HIT:");}

			    // Ausgabe pattern 
			    int pflag=0;
			    int qflag=0;
			    String namedEntity="";
			    String separator="";

			    // Zielklassen: (müssen mit klassNamen.txt übereinstimmen)
			    int VN=128,NN=256,TIT=512,PU=4096,GR=32,DET=4;
		       
			    for( int k=-1;k<actPat.length;k++) {
		             int actClassInt=new Integer(classVec.elementAt(bpos-longest+k).toString()).intValue();
			     if (pflag<5) {
				 if (pflag<3) {if ((actClassInt&(TIT+GR))>0) {qflag=1;}} else {qflag=5;}

				 if (pflag<3) {if ((actClassInt&PU)==PU) {qflag=2;}} else {qflag=5;}
				 if (pflag==0) {if ((actClassInt&PU)==PU) {qflag=0;}}
				 if ((actClassInt&DET)==DET) {qflag=0;}				 
				 if ((actClassInt&(VN+GR))>0) {qflag=3;}
				 if ((actClassInt&(NN+GR))>0) {qflag=4;}

				 //separator in abh von übergang
				 separator=" "; // hier immer space

				 if ((qflag<5)&&(qflag>0)) {namedEntity+=separator+(String)wordVec.elementAt(bpos-longest+k);}
				 pflag=qflag;
			     } // fi pflag<5
			    } // rof k
			    namedEntity+="\n";
			    

			    // write to file
			    char outChar;

			    try {

				for (int pos=0;pos<namedEntity.length();pos++) {
				    outChar=namedEntity.charAt(pos);
				    cfile.write((int)outChar);
				} //rof
			    } catch (IOException e){System.out.println("Can't write "+gaFile);}



			} // fi buffer...
			else { // Falls schon klassifiziert und matcht
			    int pflag=0;
			    int qflag=0;
			    String namedEntity="";
			    String separator="";

			    // Zielklassen: (müssen mit klassNamen.txt übereinstimmen)
			    int VN=128,NN=256,TIT=512,PU=4096,GR=32,DET=4;

			    for( int k=-1;k<actPat.length;k++) {
		             int actClassInt=new Integer(classVec.elementAt(bpos-longest+k).toString()).intValue();
			     if (pflag<5) {
				 if (pflag<3) {if ((actClassInt&(TIT+GR))>0) {qflag=1;}} else {qflag=5;}

				 if (pflag<3) {if ((actClassInt&PU)==PU) {qflag=2;}} else {qflag=5;}
				 if (pflag==0) {if ((actClassInt&PU)==PU) {qflag=0;}}
				 if ((actClassInt&DET)==DET) {qflag=0;}
				 if ((actClassInt&VN)==VN) {qflag=3;}
				 if ((actClassInt&NN)==NN) {qflag=4;}

				 //separator in abh von übergang
				 separator=" ";
				 // if ((pflag==0)&&(qflag>0)) {separator="";} // anfang
				 if ((pflag==1)&&(qflag==2)) {separator="";} // TIT PU
				 if ((pflag<3)&&(qflag>2)) {separator="\t";} // [TIT PU] [VN NN]


				 if ((qflag<5)&&(qflag>0)) {namedEntity+=separator+(String)wordVec.elementAt(bpos-longest+k);}
				 pflag=qflag;
			     } // fi pflag<5
			    } // rof k
			    namedEntity+="\n";
			    
			    // write to file
			    char outChar;

			    try {
				
				for (int pos=0;pos<namedEntity.length();pos++) {
				    outChar=namedEntity.charAt(pos);
				    gfile.write((int)outChar);                      
				} //rof
			    } catch (IOException e){System.out.println("Can.t write "+gaFile);} 			    
			} // esle fi buffer

		    } // fi matchflag



		    //patterns.addElement(actPat);
		} // rof Enum pats

		// eins weiter im buffer
		for(int i=0;i<longest-1;i++) {
		    buffer[i]=buffer[i+1];
		} // rof i
		buffer[longest-1]=new Integer(clV.nextElement().toString()).intValue();
		bpos++;
		if (d) System.out.println("Buffer: "+buffer[0]+" Wort: "+wordVec.elementAt(bpos-longest).toString());
		

	   
	    } // esle (buffer voll)

	} // rof Enumeration clV
	
	cfile.close();
	gfile.close();

	return cands;

    } // end getCandidates


    public Vector getClassificationsOf(String item, Vector wordVec, Vector classVec, NameTable klassKeys, Vector patterns) {
	// liefert (numerische) Liste, als was "item" so alles klassifiziert wurde

	// Ablauf: Regeln drüberlaufenlassen, klassifikationen dazuschreiben,
	// danach nach Item im Text suchen und klassifikationen nacheinander zurückgeben
	// schrecklich uneffektiv.


	Vector classifications= new Vector();
	Pattern actPat=new Pattern();
	int index=0;
	int bpos=0;
	int[] buffer = new int[width];
	boolean matchflag;

	for(Enumeration clV=classVec.elements();clV.hasMoreElements();) {
	    if (bpos<longest) { // wenn buffer noch nicht voll
		// lasse buffer vollaufen
		buffer[bpos]=new Integer(clV.nextElement().toString()).intValue();
		bpos++;
	    } else { // sonst: buffer voll
		
 
		for(Enumeration pats=patterns.elements();pats.hasMoreElements();) {// für alle Pattern
		    actPat=(Pattern)pats.nextElement();
		    // matche
		    matchflag=true;
		    for(int pos=0;pos<actPat.length;pos++) { // für alle Musterteile im Pattern
			if (matchflag) { // nur, wenn noch matcht
			    int actpatternpos=new Integer(klassKeys.get(actPat.pattern[pos]).toString()).intValue();
			    if ((buffer[pos] & actpatternpos)!= actpatternpos) {matchflag=false;} // wenn nicht matched, dann falsch

			    //			    System.out.println("Matche: "+buffer[pos]+"&"+actpatternpos+" = "+matchflag);

			} // fi matchflag
			
		    } // rof  pos

		    if (matchflag) { // wenn pattern tatsächlich matcht

			int goalClassInt=new Integer(klassKeys.get(actPat.goalClass).toString()).intValue();
			buffer[actPat.goalPos]=(buffer[actPat.goalPos] | goalClassInt); // im Buffer Klassifikation hinzufügen

			    if (d) { System.out.print("HIT:");}

		    
			    if (d) { // bei debugging:
				// Ausgabe pattern und bsp
				System.out.print("PN> ");
				for( int k=0;k<actPat.length;k++) {
				    System.out.print(wordVec.elementAt(bpos-longest+k)+" ("+classVec.elementAt(bpos-longest+k)+") ");
				} // rof k
				System.out.print("Pattern: "+actPat.toString()+" Text: ");
			
				System.out.println();
			    } // fi (d)
		    } // fi d match

		
		} // rof Enum pats

		// neue Klassifikation in classVec einfügen
		String intStr=new String().valueOf(buffer[0]);
		classVec.setElementAt(intStr,bpos-longest);
		
		// eins weiter im buffer
		for(int i=0;i<longest-1;i++) {
		    buffer[i]=buffer[i+1];
		} // rof i
		buffer[longest-1]=new Integer(clV.nextElement().toString()).intValue();
		bpos++;
		if (d) System.out.println("Buffer: "+buffer[0]+" Wort: "+wordVec.elementAt(bpos-longest).toString());
		

		
	    } // esle (buffer voll)
	    
	} // rof Enumeration clV

	// restbuffer in classVec einfügen
	for (int r=bpos-longest;r<bpos;r++) {
	    String intString=new String().valueOf(buffer[r-bpos+longest]);
	    		classVec.setElementAt(intString,r);
	} // rof r


	// nun Klassifizierungsliste erstellen

	String actWord=new String();
	int wpos=0;

	for (Enumeration e=wordVec.elements();e.hasMoreElements();) {
	    actWord=(String)e.nextElement();
	    if (actWord.equals(item)) {
		classifications.addElement(classVec.elementAt(wpos));
	    } // fi actWord== item


	    wpos++;
	} // rof Enum e


	classVec=null;
	wordVec=null;
	klassKeys=null;
	patterns=null;

	return classifications;




    } // end getClassificationsOF


} // end class matcher

