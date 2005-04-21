package namerec;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/* Rules verwaltet die Patterns und verschiebt den Dot. usw

Prozeduren:
- loadPatterns: Läd Pattern aus ner Datei
- initPatterns: Nimmt externe Patterns
- candidates: verarbeitet ein Wort mit klasse und gibt kandidaten zurück
- resetRules: setzt dots auf 0
*/



public class Rules {

    static boolean d=true; //debugging aus
    Vector patterns;  // Vektor der Regeln
    String matchFile="matchfile.txt"; // default, besser setzen
	protected BufferedWriter br;


    public void loadPatterns(String patfile, String fileContexts) throws IOException, FileNotFoundException {
	FileReader file=new FileReader(patfile); 
	String inLine="";
	int inInt;
	Vector retvec=new Vector();
       
	String goalClass;
	int length;
	int goalPos;
	String dummy;
	this.matchFile=fileContexts;	

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
		length=new Integer(tokens.nextToken().substring(7)).intValue();
		goalPos=new Integer(tokens.nextToken().substring(8)).intValue();
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
	
	this.patterns=retvec;
    } // end loadPatterns

    public void resetRules() {
	for(Enumeration r=patterns.elements();r.hasMoreElements();) { // Für alle pattern
	    Pattern actPat=(Pattern)r.nextElement();
	    actPat.dot=0;
	} // rof Enum r

    } // end  public static void resetRules()




    protected void output(Pattern pat) throws IOException, FileNotFoundException {
    	if(br==null){
    		br=new BufferedWriter(new FileWriter(matchFile,true));//wird geschlossen, sobald das Programm beendet wird.
    	}
    	
    	StringBuffer outstr=new StringBuffer();
    	char outChar;
    	
    	for(int i=0;i<pat.length;i++) {
    		outstr.append(pat.word[i]).append("(");
    		if (i==pat.goalPos) {
    			outstr.append("?").append(pat.goalClass).append(") ");
    		}else {
    			outstr.append(pat.pattern[i]).append(") ");
    		}
    	}
    	outstr.append("\t").append(pat.toString()).append("\n");
    	
    	System.out.print("R: "+outstr);
    	br.write(outstr.toString());                                               
    	br.flush();
    	
    } // end private void output



    public NameTable candidates(int classWord, String plainWord, NameTable klassKeys) throws IOException, FileNotFoundException {

	NameTable retItems = new NameTable();
	Pattern actPat;  // aktuelles Pattern in Schleife
	int patMatch; // nächste Klasse in Pattern

	for(Enumeration r=patterns.elements();r.hasMoreElements();) { // Für alle pattern
	    actPat=(Pattern)r.nextElement();
	    patMatch=new Integer(klassKeys.get(actPat.pattern[actPat.dot]).toString()).intValue();

	    if ((patMatch&classWord)==patMatch) { // Falls aktuelles Wort matcht
		actPat.word[actPat.dot]=plainWord; // Wort in Pattern merken
		actPat.dot++;  // Dot eins weiter
	    } else { // Falls nicht, resette dot
		actPat.dot=0;
	    } // eslefi patMatch&classWord
	    
	    if (actPat.dot==actPat.length) { // Falls Länge erreicht, also Regel komplett matcht
		retItems.put(actPat.word[actPat.goalPos],actPat.goalClass); // neuer Kandidat in Nametable
		output(actPat); // Schreibe match raus
		actPat.dot=0; // Resette dot
	    } // fi dot=length

	} // rof Enum r
	return retItems;
    } // end candidates

    
} // end Class Rules
