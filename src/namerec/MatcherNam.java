package namerec;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;


public class MatcherNam {
    
    private boolean d=false; // Debugging
    
    public int width=9;
    public int middle=4;
    public int shortest=2;
    public int longest=5;
    public Vector StringPatterns=new Vector(); // für Doublettencheck
    public Vector patterns=new Vector();
    
    private Annotate anno;
    
    public MatcherNam(Annotate anno) {
        this.anno=anno;
    }
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
                    goalClass=dummy.substring(8,dummy.length());
                    length=new Integer(tokens.nextToken().substring(7,8)).intValue();
                    goalPos=new Integer(tokens.nextToken().substring(8,9)).intValue();
                    String pats[]=new String[length];
                    dummy=tokens.nextToken(); // nun sowas wie: pattern= VN KL VN GR 
                    
                    StringTokenizer ptokens= new StringTokenizer(dummy," ");
                    ptokens.nextToken(); // omit "pattern="
                    for(int i=0;i<length;i++) {
                        pats[i]=ptokens.nextToken();
                    } // rof i
                    
                    Pattern inputpat=new Pattern(goalClass,length,goalPos,pats);
                    if (d) System.out.println("Newpat: "+inputpat.toString());
                    retvec.addElement(inputpat);
                } // fi -1
                inLine="";
            } // elihw EOF
            
        } catch (IOException e) {System.out.println("Can't find file "+patfile+"\n");}
        
        return retvec;
    } // end loadPatterns
    
    
    
    
    
    
    
    
    
    
    public Vector getClassificationsOf(String item, String text, NameTable klassKeys, Vector patterns) {
        // liefert (numerische) Liste, als was "item" so alles klassifiziert wurde
        
        // Ablauf: Regeln drüberlaufenlassen, klassifikationen dazuschreiben,
        // danach nach Item im Text suchen und klassifikationen nacheinander zurückgeben
        // schrecklich uneffektiv.
        
        
        Vector classifications= new Vector();
        Pattern actPat;
        int bpos=0;
        int[] buffer = new int[width];
        boolean matchflag;
        
        List wordVec=Annotate.tokenize(text);
        int[] classVec=anno.annotate(wordVec);
        
        for(int i=0;i<classVec.length;i++) {
            if (bpos<longest) { // wenn buffer noch nicht voll
                // lasse buffer vollaufen
                buffer[bpos]=classVec[i];
                bpos++;
            } else { // sonst: buffer voll
                for(Enumeration pats=patterns.elements();pats.hasMoreElements();) {// für alle Pattern
                    actPat=(Pattern)pats.nextElement();
                    // matche
                    matchflag=true;
                    for(int pos=0;pos<actPat.length;pos++) { // für alle Musterteile im Pattern
                        if (matchflag) { // nur, wenn noch matcht
                            if(klassKeys.containsKey(actPat.pattern[pos])) {
                                int actpatternpos=((Integer)klassKeys.get(actPat.pattern[pos])).intValue();
                                if ((buffer[pos] & actpatternpos)!= actpatternpos) 
                                    matchflag=false; // wenn nicht matched, dann falsch
                            }else {
                                if(!actPat.pattern[pos].equals(wordVec.get(pos))) 
                                    matchflag=false;
                            }
                        } // fi matchflag
                    } // rof  pos
                    
                    if (matchflag) { // wenn pattern tatsächlich matcht
                        
                        int goalClassInt=((Integer)klassKeys.get(actPat.goalClass)).intValue();
                        buffer[actPat.goalPos]=(buffer[actPat.goalPos] | goalClassInt); // im Buffer Klassifikation hinzufügen
                        
                        if (d) { System.out.print("HIT:");                        
                        // bei debugging:
                        // Ausgabe pattern und bsp
                        System.out.print("PN> ");
                        for( int k=0;k<actPat.length;k++) {
                            System.out.print(wordVec.get(bpos-longest+k)+" ("+classVec[bpos-longest+k]+") ");
                        } // rof k
                        System.out.print("Pattern: "+actPat.toString()+" Text: ");
                        
                        System.out.println();
                        } // fi (d)
                    } // fi d match
                    
                    
                } // rof Enum pats
                
                // neue Klassifikation in classVec einfügen
                classVec[bpos-longest]=buffer[0];
                
                // eins weiter im buffer
                for(int j=0;j<longest-1;j++) {
                    buffer[j]=buffer[j+1];
                } // rof i
                buffer[longest-1]=classVec[i];
                bpos++;
                if (d) System.out.println("Buffer: "+buffer[0]+" Wort: "+wordVec.get(bpos-longest).toString());
                
                
                
            } // esle (buffer voll)
            
        } // rof Enumeration clV
        
        // restbuffer in classVec einfügen
        for (int r=bpos-longest;r<bpos;r++) {
            classVec[r]=buffer[r-bpos+longest];
        } // rof r
        
        
        // nun Klassifizierungsliste erstellen
        
        String actWord=new String();
        int wpos=0;
        
        for (Iterator it=wordVec.iterator();it.hasNext();) {
            actWord=(String)it.next();
            if (actWord.equals(item)) {
                classifications.addElement(Integer.toString(classVec[wpos]));
            } // fi actWord== item
            
            
            wpos++;
        } // rof Enum e
        
        
        return classifications;
        
    } // end getClassificationsOF
    
    public void saveFile(String filename) throws IOException{
        FileWriter file=new FileWriter(filename,false);
        String outstr=new String();
        Pattern actPat;
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
} // end class matcher

