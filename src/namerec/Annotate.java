package namerec;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

public class Annotate {

    static boolean d=false;  //debugging
    /**
     * Private constructor, because here are only static helpermethods.
     */
    private Annotate() {}
    public static Vector tokenize(String input) {

	Vector retvec=new Vector();

	// VORARBEIT:
	// Satzzeichen mit trennenden Leerzeichen versehen
	//  \n und \t in Leerzeichen umwandeln

	StringBuffer newstr=new StringBuffer();
	int posInt;

	for(int pos=0;pos<input.length();pos++) {
	    posInt=input.charAt(pos);
	    
	    // Erlaubte Zeichen
	    if ((posInt==32)||  // Leerzeichen
		((posInt>64)&&(posInt<91))||    //Grossbuchstaben
		((posInt>96)&&(posInt<123))||    //Kleinbuchstaben
		((posInt==228)||(posInt==196 ))|| // äÄ
		((posInt==246)||(posInt==214 ))|| // öÖ
		((posInt==252)||(posInt==220 ))|| // üÜ
		((posInt==223)||(posInt==45 ))|| // "-ß"
		((posInt>=48)&&(posInt<=57 )) // 0-9
		) {
		newstr.append(posInt);
	    } //fi posInt
	    
	    
	    // Zeichen, die in Leerzeichen konvertiert werden 
  	    if ((posInt==10)||(posInt==9) )  // newline
		{
		    newstr.append(" ");
		} // fi posInt
	    
	    
	    // Sonderzeichen und Satzzeichen
 	    if ((posInt==46)||(posInt==47)||((posInt>=33)&&(posInt<=44))||(posInt>=58)&&(posInt<=64)||(posInt>=91)&&(posInt<=96))  
		// komma, stop, slash, !, ?, (,),[,], ", &, %, =, ', + usw. 
		
		{
		    newstr.append(" ").append(posInt).append(" ");
		} // fi posInt	    
	} // rof pos


	StringTokenizer tokens=new StringTokenizer(new String(newstr));

	while(tokens.hasMoreTokens()) {
	    retvec.addElement(tokens.nextToken());
	} //elihw




	return retvec;


    } // end public vector tokenize


    public static Vector annotate(Vector input, NameTable regexp, NameTable classif,NameTable classKeys){

	Vector retvec=new Vector();
	String actWord=new String();
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
		Pattern actExp=(Pattern) reg.nextElement();


		match=actExp.matcher(actWord).matches();
		
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

	    String inputString=String.valueOf(actClass);
	    retvec.addElement(inputString);

	} // rof Enumeration e
	regexp=null;
	input=null;
	classif=null;
	classKeys=null;

	return retvec;

    } // end public Vector annotate



} // end class Annotate
