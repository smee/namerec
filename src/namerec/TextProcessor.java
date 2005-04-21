package namerec;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

public class TextProcessor {
    
    static final boolean d=false;  //debugging
    
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
    
    
    
    public NameTable getCandidatesOfText(String text, NameTable regexp, NameTable classif,NameTable klassKeys, Rules rules) throws SQLException, IOException, FileNotFoundException {
        
        NameTable retvec=new NameTable();
        NameTable newCands=new NameTable();
        String actWord=new String();
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
                java.util.regex.Pattern actExp=(java.util.regex.Pattern) reg.nextElement();
                
                
                match=actExp.matcher(actWord).matches();
                if (match) {
                    if (d) System.out.println("Match mit '"+actExp.pattern()+"'");
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
            
            // einfügen in Return-vector
            
            try {
                newCands=rules.candidates(actClass, actWord,klassKeys);
            } catch (Exception e) {
                System.out.println("Something wrong!");
                e.printStackTrace();
            }
            retvec.putAll(newCands);
            
        } // rof Enumeration e
        System.out.println("Im Satz:"+retvec.toString());
        return retvec;
        
    } // end public Vector getNEsOF
    
    
    
} // end class Textprocessor
