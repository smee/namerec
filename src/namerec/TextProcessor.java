package namerec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class TextProcessor {
    
    static final boolean d=false;  //debugging
    private NameTable klassKeys;
    private Annotate anno;
    
    public TextProcessor(Annotate anno) {
        this.anno=anno;
    }
    
    public static List tokenize(String input) {
        
        
        // VORARBEIT:
        // Satzzeichen mit trennenden Leerzeichen versehen
        //  \n und \t in Leerzeichen umwandeln
        
        StringBuffer newstr=new StringBuffer("");
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
                newstr.append((char)posInt);
            } //fi posInt
            
            
            // Zeichen, die in Leerzeichen konvertiert werden 
            if ((posInt==10)||(posInt==9) )  // newline
            {
                newstr.append(" ");
            } // fi posInt
            
            
            // Sonderzeichen und Satzzeichen
            if ((posInt==46)||(posInt==47)||((posInt>=33)&&(posInt<=44))||(posInt>=58)&&(posInt<=64)||(posInt>=91)&&(posInt<=96))  
                // komma, punkt, slash, !, ?, (,),[,], ", &, %, =, ', + usw. 
                
            {
                newstr.append(" ").append((char)posInt).append(" ");
            } // fi posInt	    
        } // rof pos
        
        
        StringTokenizer tokens=new StringTokenizer(newstr.toString());
        List retvec=new ArrayList(tokens.countTokens());
        while(tokens.hasMoreTokens()) {
            retvec.add(tokens.nextToken());
        } //elihw
        
        return retvec;
        
    } // end public vector tokenize
    
    
    
    public NameTable getCandidatesOfText(String text,Rules rules) throws SQLException{ 
        List tokens=TextProcessor.tokenize(text);
        int[] classes=anno.annotate(tokens);
        NameTable retvec=new NameTable();
        NameTable newCands=new NameTable();
        String actWord;
        int actClass=0;
        int idx=0;
        for (Iterator it=tokens.iterator();it.hasNext();idx++) { // fuer alle woerter
            actWord=it.next().toString();
            actClass=classes[idx];
            
          
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
