package com.biemann.pendel;

import java.util.* ;
import gnu.regexp.*;

public class Annotate {

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
		((posInt==228)||(posInt==196 ))|| // äÄ
		((posInt==246)||(posInt==214 ))|| // öÖ
		((posInt==252)||(posInt==220 ))|| // üÜ
		((posInt==223)||(posInt==45 ))|| // "-ß"
		((posInt==192)||(posInt==255))|| //ascii sonderzeichen
		((posInt>=48)&&(posInt<=57 )) // 0-9
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
		// komma, stop, slash, !, ?, (,),[,], ", &, %, =, ', + usw.

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


    public Vector annotate(Vector input, NameTable regexp, NameTable classif,NameTable classKeys) throws gnu.regexp.REException {

	Vector retvec=new Vector();
	String actWord=new String();
	String actExp=new String();
	String classification=new String();
	int actClass=0;
	int addClass=0;
	boolean match=false;

	for (Enumeration en=input.elements();en.hasMoreElements();) {
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
		    addClass= new Integer(classKeys.get(classification).toString()).intValue();
		    actClass = actClass | addClass;
		} // fi match

	    } // rof Enumeration reg

	    // matche mit Grundwissen
	    if (classif.containsKey(actWord)) {
		classification=classif.get(actWord).toString();
		if (d) System.out.println(" Klassifiziert als: '"+classification+"'");
		addClass= new Integer(classKeys.get(classification).toString()).intValue();
		actClass = actClass | addClass;
	    } // fi classif.contains

	    // einfgen in Return-vector

	    String inputString=new String().valueOf(actClass);
	    retvec.addElement(inputString);

	} // rof Enumeration e
	regexp=null;
	input=null;
	classif=null;
	classKeys=null;

	return retvec;

    } // end public Vector annotate



} // end class Annotate
