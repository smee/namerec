package namerec;
import java.util.*;
import java.io.*;
import gnu.regexp.*;
import java.sql.*;

public class TextProcessor {

    static boolean d=false;  //debugging
    static gnu.regexp.RE exp;

    public Vector tokenize(String input) {

	Vector retvec=new Vector();

	// VORARBEIT:
	// Satzzeichen mit trennenden Leerzeichen versehen
	//  \n und \t in Leerzeichen umwandeln

	String newstr="";
	int posInt;

	for(int pos=0;pos<input.length();pos++) {
	    posInt=(int)input.charAt(pos);
	    
	    // Erlaubte Zeichen
	    if ((posInt==32)||  // Leerzeichen
		((posInt>64)&&(posInt<91))||    //Grossbuchstaben
		((posInt>96)&&(posInt<123))||    //Kleinbuchstaben
		((posInt==228)||(posInt==196 ))|| // ��
		((posInt==246)||(posInt==214 ))|| // ��
		((posInt==252)||(posInt==220 ))|| // ��
		((posInt==223)||(posInt==45 ))|| // "-�"
		((posInt>=48)&&(posInt<=57 ))|| // 0-9
		((posInt>=192)&&(posInt<=255 )) // akzentzeug
		) {
		newstr+=(char)posInt;
	    } //fi posInt
	    
	    
	    // Zeichen, die in Leerzeichen konvertiert werden 
  	    if ((posInt==10)||(posInt==9) )  // newline
		{
		    newstr+=" ";
		} // fi posInt
	    
	    
	    // Sonderzeichen und Satzzeichen
 	    if ((posInt==46)||(posInt==47)||((posInt>=33)&&(posInt<=44))||(posInt>=58)&&(posInt<=64)||(posInt>=91)&&(posInt<=96))  
		// komma, punkt, slash, !, ?, (,),[,], ", &, %, =, ', + usw. 
		
		{
		    newstr+=" "+(char)posInt+" ";
		} // fi posInt	    
	} // rof pos


	StringTokenizer tokens=new StringTokenizer(newstr);

	while(tokens.hasMoreTokens()) {
	    retvec.addElement(tokens.nextToken());
	} //elihw

	return retvec;

    } // end public vector tokenize


    public NameTable getCandidatesOfText(String text, NameTable regexp, NameTable classif,NameTable klassKeys, Rules rules) throws gnu.regexp.REException, IOException, FileNotFoundException {

	NameTable retvec=new NameTable();
	NameTable newCands=new NameTable();
	String actWord=new String();
	String actExp=new String();
	String classification=new String();
	int actClass=0;
	int addClass=0;
	boolean match=false;
	Vector input=tokenize(text);

	for (Enumeration en=input.elements();en.hasMoreElements();) { // fuer alle woerter
	    actWord=en.nextElement().toString();
	    actClass=0;

	    if (d) System.out.println("Behandle Wort '"+actWord+"'");

	    // matche mit Regexps
	    for(Enumeration reg=regexp.keys();reg.hasMoreElements();) {
		actExp=reg.nextElement().toString();


		try {
		    exp = new gnu.regexp.RE(actExp);
		    match=exp.isMatch(actWord);
		} catch (REException e) {System.out.println(e.getMessage());}
		
		if (match) {
		    if (d) System.out.println("Match mit '"+actExp+"'");
		    classification=regexp.get(actExp).toString();
		    if (d) System.out.println(" Klassifiziert als: '"+classification+"'");
		    addClass= new Integer(klassKeys.get(classification).toString()).intValue();
		    actClass = actClass | addClass;
		} // fi match

	    } // rof Enumeration reg

	    // matche mit Grundwissen
	    if (classif.containsKey(actWord)) {
		classification=classif.get(actWord).toString();
		if (d) System.out.println(" Klassifiziert als: '"+classification+"'");
		addClass= new Integer(klassKeys.get(classification).toString()).intValue();
		actClass = actClass | addClass;
	    } // fi classif.contains

	    // einf�gen in Return-vector

	    try {
	        newCands=rules.candidates(actClass, actWord,klassKeys);
	    } catch (Exception e) {System.out.println("Something wrong!"+e.getMessage());}
	    retvec.insert(newCands);

	} // rof Enumeration e
	System.out.println("Im Satz:"+retvec.toString());
	return retvec;

    } // end public Vector getCandidatesOF

    public void getNEsOfText(String text, NameTable regexp, NameTable classif,NameTable klassKeys, RulesNE rules,Connection Verbindung) throws gnu.regexp.REException, SQLException, IOException, FileNotFoundException {

	NameTable retvec=new NameTable();
	NameTable newCands=new NameTable();
	String actWord=new String();
	String actExp=new String();
	String classification=new String();
	int actClass=0;
	int addClass=0;
	boolean match=false;
	Vector input=tokenize(text);

	for (Enumeration en=input.elements();en.hasMoreElements();) { // fuer alle woerter
	    actWord=en.nextElement().toString();
	    actClass=0;

	    if (d) System.out.println("Behandle Wort '"+actWord+"'");

	    // matche mit Regexps
	    for(Enumeration reg=regexp.keys();reg.hasMoreElements();) {
		actExp=reg.nextElement().toString();


		try {
		    exp = new gnu.regexp.RE(actExp);
		    match=exp.isMatch(actWord);
		} catch (REException e) {System.out.println(e.getMessage());}
		
		if (match) {
		    if (d) System.out.println("Match mit '"+actExp+"'");
		    classification=regexp.get(actExp).toString();
		    if (d) System.out.println(" Klassifiziert als: '"+classification+"'");
		    addClass= new Integer(klassKeys.get(classification).toString()).intValue();
		    actClass = actClass | addClass;
		} // fi match

	    } // rof Enumeration reg

	    // matche mit Grundwissen
	    if (classif.containsKey(actWord)) {
		classification=classif.get(actWord).toString();
		if (d) System.out.println(" Klassifiziert als: '"+classification+"'");
		addClass= new Integer(klassKeys.get(classification).toString()).intValue();
		actClass = actClass | addClass;
	    } // fi classif.contains

	    // einf�gen in Return-vector

	    try {
	        newCands=rules.match(actClass, actWord,klassKeys,Verbindung);
	    } catch (Exception e) {System.out.println("Something wrong!"+e.getMessage());}
	    retvec.insert(newCands);

	} // rof Enumeration e
	System.out.println("Im Satz:"+retvec.toString());
	

    } // end public Vector getNEsOF



} // end class Textprocessor
