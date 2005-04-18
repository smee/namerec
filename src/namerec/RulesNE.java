package namerec;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;

/* Rules verwaltet die Patterns und verschiebt den Dot. usw

Prozeduren:
- loadPatterns: Läd Pattern aus ner Datei
- initPatterns: Nimmt externe Patterns
- candidates: verarbeitet ein Wort mit klasse und gibt kandidaten zurück
- resetRules: setzt dots auf 0
*/



public class RulesNE extends Rules{
    
    static boolean d=true; //debugging aus
    
    
    private void dbInsertPerson(Pattern pat,DBaccess db) {
        
        // Generiere insert-statement für person(wort_bin="VN ZN NN ,TIT",wort_lex="ZN NN VN TIT", beruf="TIT", quelle"NameRec")
        
        String vns="";
        String znnns="";
        String titpus="";
        //
        
        int mix=0;
        int tit=0;
        for(int i=0;i<pat.length;i++) {
            //vns
            if (pat.pattern[i].equals("VN")) {vns+=" "+pat.word[i];mix=0;tit=0;}
            if (pat.pattern[i].equals("MIX")) {vns+=" "+pat.word[i];mix=1;tit=0;}
            if ((mix==1)&&pat.pattern[i].equals("PU")) {vns+=".";mix=0;tit=0;}
            //znnns
            
            if (pat.pattern[i].equals("ZN")) {znnns+=" "+pat.word[i];mix=0;tit=0;}
            if (pat.pattern[i].equals("NN")) {znnns+=" "+pat.word[i];mix=0;tit=0;}
            
            //titpus
            if (pat.pattern[i].equals("TIT")) {titpus+=" "+pat.word[i];mix=0;tit=1;}
            if ((tit==1)&&pat.pattern[i].equals("PU")) {titpus+=".";mix=0;tit=0;}
        } // for int i
        
        // entferne führendes " "
        if (!vns.equals("")) vns=vns.substring(1,vns.length());
        if (!znnns.equals("")) znnns=znnns.substring(1,znnns.length());
        if (!titpus.equals("")) titpus=titpus.substring(1,titpus.length());
                if (!vns.equals("")) { // no fun without a vornamen
            String statement="INSERT INTO person (wort_bin, wort_lex,beruf,quelle) values ";
            if (titpus.equals("")) {  //Fall: keine titel
                statement+="('"+vns+" "+znnns+"','"+znnns+" "+vns+"','','NameRec')";
            } else {  //Fall: wohl Titel
                statement+="('"+vns+" "+znnns+"','"+znnns+" "+vns+" "+titpus+"','"+titpus+"','NameRec')";
                
            } // esle
            
            if (db.nrOfLex(znnns+" "+vns+" "+titpus)<1) { // nur, wenn noch nicht drin
                try {  
                    db.SQLstatement(statement);
                } catch (SQLException e) {System.out.println("Datenbankfehler! "+e.getMessage());}
            } // fi dbNrOfLex
            
                    } // fi !vns.empty
        
    } // end dbInsertPerson
    
    
    
    public NameTable matchAndUpdateDB(int classWord, String plainWord, NameTable klassKeys,DBaccess db) throws IOException, FileNotFoundException, SQLException {
        
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
                //dbInsertPerson(actPat,db);//soll waehrend des testens nicht verwendet werden!
                actPat.dot=0; // Resette dot
            } // fi dot=length
            
        } // rof Enum r
        return retItems;
    } // end candidates
    
    
} // end Class Rules
