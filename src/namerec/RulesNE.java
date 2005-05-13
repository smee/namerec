package namerec;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/* Rules verwaltet die Patterns und verschiebt den Dot. usw

Prozeduren:
- loadPatterns: Läd Pattern aus ner Datei
- initPatterns: Nimmt externe Patterns
- candidates: verarbeitet ein Wort mit klasse und gibt kandidaten zurück
- resetRules: setzt dots auf 0
*/



public class RulesNE extends Rules{
    
    static boolean d=false; //debugging aus
    private DBaccess db;
    
    
    public RulesNE(DBaccess db, String patfile, String fileContexts) throws FileNotFoundException, IOException {
        super(patfile,fileContexts);
        this.db=db;
    }
    
    private void dbInsertPerson(Pattern pat) {
        // Generiere insert-statement für person(wort_bin="VN ZN NN ,TIT",wort_lex="ZN NN VN TIT", beruf="TIT", quelle"NameRec")
        
        String vns="";
        String znnns="";
        String titpus="";
        String first_vn="";
        String first_zn="";
        String first_nn="";
        String normalform="";
        
        int mix=0;
        int tit=0;
        
        int f_vn=0;
        int f_zn=0;
        int f_nn=0;
        
        
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
            
            //firstones für normalform
            if ((f_vn==0)&&pat.pattern[i].equals("VN")) {first_vn=pat.word[i]; f_vn=1;}
            if ((f_zn==0)&&pat.pattern[i].equals("ZN")) {first_zn=pat.word[i]; f_zn=1;}
            if ((f_nn==0)&&pat.pattern[i].equals("NN")) {first_nn=pat.word[i]; f_nn=1;}
        } // for int i
        
        if (f_zn==0) {normalform=first_vn+" "+first_nn; }
        else {normalform=first_vn+" "+first_zn+" "+first_nn; }
        
        // entferne führendes " "
        if (!vns.equals("")) vns=vns.substring(1,vns.length());
        if (!znnns.equals("")) znnns=znnns.substring(1,znnns.length());
        if (!titpus.equals("")) titpus=titpus.substring(1,titpus.length());
//      if (!vns.equals("")) { // no fun without a vornamen
//      change 4.12.2002: it is fun with vornamen!
        String statement="INSERT INTO person (wort_bin,wort_lex,wort_alt,beruf,kat_nr,quelle) values ";
        if (titpus.equals("")) {  //Fall: keine titel
            statement+="('"+vns+" "+znnns+"','"+znnns+" "+vns+"','"+normalform +"','',4,'NameRec 1.1neu')";
        } else {  //Fall: wohl Titel
            statement+="('"+titpus+" "+vns+" "+znnns+"','"+znnns+" "+vns+" "+titpus+"','"+normalform+"','"+titpus+"',4,'NameRec 1.1neu')";
            
        } // esle
        if(d) System.out.println("\nInserting NE: "+statement);
        if (db.nrOfLex(znnns+" "+vns+" "+titpus)<1) { // nur, wenn noch nicht drin
            try {  
                db.SQLstatement(statement);
            } catch (SQLException e) {System.out.println("Datenbankfehler! "+e.getMessage());}
        } // fi dbNrOfLex
        
        else {System.out.println("Schon vorhanden!");}
        
//      } // fi !vns.empty
        
    } // end dbInsertPerson
    
    
    
    
    protected synchronized void output(Pattern pat)  {
        dbInsertPerson(pat);//soll waehrend des testens nicht verwendet werden!
        StringBuffer outstr=new StringBuffer();
        
        for(int i=0;i<pat.length;i++) {
            outstr.append(pat.word[i]).append(" ");
        }
        outstr.append("\t");
        for(int i=0;i<pat.length;i++) {
                outstr.append(pat.pattern[i]).append(" ");
        }


        outstr.append("\n");

        System.out.print("Z: "+outstr+"\t"+pat.toString());
        try {
            br.write(outstr.toString());
            br.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }                                               


        } // end private void output
} // end Class Rules
