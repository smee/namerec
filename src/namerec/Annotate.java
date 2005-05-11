package namerec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import basetagger.BaseWorder;

public class Annotate {

    static boolean d=false;  //debugging
    private NameTable regexp;
    private NameTable classif;
    private NameTable classKeys;
    private BaseWorder basetagger; 
    /**
     * Private constructor, because here are only static helpermethods.
     */
    public  Annotate(NameTable regexp, NameTable classif,NameTable classKeys, String treedir) {
        this.regexp=regexp;
        this.classif=classif;
        this.classKeys=classKeys;
        System.out.println("Initializing basetagger...");
        this.basetagger=new BaseWorder("de",treedir);
    }
    public static List tokenize(String input) {

	List retvec=new ArrayList();

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
		newstr.append((char)posInt);
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
		    newstr.append(" ").append((char)posInt).append(" ");
		} // fi posInt	    
	} // rof pos


	StringTokenizer tokens=new StringTokenizer(new String(newstr));

	while(tokens.hasMoreTokens()) {
	    retvec.add(tokens.nextToken());
	} //elihw




	return retvec;


    } // end public vector tokenize


    public int[] annotate(List input){
        
        String actWord=new String();
        String classification=new String();
        int addClass=0;
        boolean match=false;
        int idx=0;
        int[] ret=new int[input.size()];
        
        for (Iterator it=input.iterator();it.hasNext();idx++) {
            actWord=it.next().toString();
            int actClass=0;
            
            if (d) System.out.println("Behandle Wort '"+actWord+"'");
            
            // matche mit Regexps
            for(Enumeration reg=regexp.keys();reg.hasMoreElements();) {
                Pattern actExp=(Pattern) reg.nextElement();
                
                
                match=actExp.matcher(actWord).matches();
                
                if (match) {
                    if (d) System.out.println("Match mit '"+actExp+"'");
                    classification=regexp.get(actExp).toString();
                    if (d) System.out.println(" Klassifiziert als: '"+classification+"'");
                    addClass= ((Integer)classKeys.get(classification)).intValue();
                    actClass = actClass | addClass;
                } // fi match
                
            } // rof Enumeration reg
            
            // matche mit Grundwissen
            if (classif.containsKey(actWord)) {
                classification=classif.get(actWord).toString();
                if (d) System.out.println(" Klassifiziert als: '"+classification+"'");
                addClass= ((Integer)classKeys.get(classification)).intValue();
                actClass = actClass | addClass;
            } // fi classif.contains
            //benutze basetagger
            String postag=basetagger.getTag(actWord);
            addClass= ((Integer)classKeys.get(postag)).intValue();
            actClass = actClass | addClass;
            ret[idx]=actClass;
        } // rof Enumeration e
        
        return ret;
        
    } // end public Vector annotate



} // end class Annotate
