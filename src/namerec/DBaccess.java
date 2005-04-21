package namerec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class DBaccess implements SatzDatasource {
    private class SentenceFetcher implements Runnable{
        private int pos;
        private Connection Verbindung;

        public SentenceFetcher(Connection Verbindung, int startnr) {
            this.Verbindung=Verbindung;
            this.pos=startnr;
        }
        
        public void run() {
            while(true) {
                if(retrieveNextSentences() == false || Recognizer.stopEverything==true)
                    break;//sind mit der DB durch
            }
        }

        private boolean retrieveNextSentences() {
            String Anfrage= "Select beispiel from saetze where bsp_nr>="+pos+" and bsp_nr <"+(pos+1000);   
            ResultSet Ergebnis=null;
            try{
                
                if (d) {System.out.println("Verbindung-init...fuer ["+pos+","+(pos+1000)+")");}
                
                Statement SQLAbfrage = Verbindung.createStatement();
                Ergebnis = SQLAbfrage.executeQuery(Anfrage);
                
            }
            catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}
            
            // Nun Umwandlen ResultSet in String
            try {
                while (Ergebnis.next()) {
                    sentences.enqueue(Ergebnis.getString(1));
                    if(fetcherThread.isInterrupted() || Recognizer.stopEverything == true)
                        return false;
                    pos++;
                }// elihw
            } catch (SQLException e) {
                System.out.println("Fehler beim Empfangen von Satz Nr. "+pos);
                sentences.enqueue("END");
                return false;
            }
            return true;
        }
    }
    
    public static boolean d=false; // debugging aus
    
    Connection Verbindung_ws; // Verbindung zu WORTSCHATZ
    Connection Verbindung_akt; // Verbindung zu WDTaktuell

    BlockingQueue sentences;
    Thread fetcherThread;
    
    public DBaccess() {
        this("org.gjt.mm.mysql.Driver");
    }
    
    public DBaccess(String dbTreiber) {
        this(dbTreiber,
                "jdbc:mysql://localhost/de?user=toolbox&password=booltox",
                "jdbc:mysql://localhost/wdt_test?user=toolbox&password=booltox",
                0);
    }


    /**
     * @param string
     * @param string2
     * @param string3
     * @param bspnr
     */
    public DBaccess(String dbTreiber, String ws, String akt, int startnr) {
        // init DB
        try {
            System.out.println("Treiber-init...");
            Class.forName(dbTreiber);
            System.out.println("Verbindung-init...");
            System.out.println("using the following dbStrings: ");
            System.out.println("VerbindungWS: "+ws);
            System.out.println("Verbindung_akt: "+akt);

//     HIER DATENBANKVERBINDUNGEN EINTRAGEN!!!!

            Verbindung_ws=DriverManager.getConnection(ws);//sdienst%pw
            Verbindung_akt=DriverManager.getConnection(akt);
        }
        catch (SQLException e) {
            System.out.println("Datenbankfehler!"+e.getMessage());
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        sentences=new BlockingQueue(500);
        fetcherThread=new Thread(new SentenceFetcher(Verbindung_akt,startnr));
        fetcherThread.start();
    }

    public String getNextSentence() {
        return (String) sentences.dequeue();
    }


    public String getNof(String name, int anz)  throws SQLException{
        return new String(new StringBuffer(getNof(name,anz,Verbindung_ws)).append(getNof(name,anz,Verbindung_akt)));
    }
    private String getNof(String name, int anz, Connection Verbindung) throws SQLException{
        int anzahl;
        ResultSet Ergebnis=null;
        StringBuffer ergString=new StringBuffer();
        //this.Verbindung=Verbindung;
        // Finde NR des Wortes in DB
                //System.out.println(name+" "+anz+" ");
        String Anfrage= "SELECT wort_nr FROM wortliste WHERE wort_bin='"+name+"'";
        try{	    
            Statement SQLAbfrage = Verbindung.createStatement();
            Ergebnis = SQLAbfrage.executeQuery(Anfrage);
        }
        catch (SQLException e) {
            System.out.println("Datenbankfehler!"+e.getMessage());
        }
                try {
            Ergebnis.next();
            anzahl=Ergebnis.getInt(1);
        } catch (SQLException e) {anzahl=0;}
        if (d) {System.out.println("Wort '"+name+"' hat Nr.  "+anzahl);}
        
        if (anzahl>0) {  // nur, wenn wort auch in DB
            Anfrage= "Select beispiel from saetze s, inv_liste i where i.wort_nr="+anzahl+" and i.bsp_nr=s.bsp_nr limit "+anz;   
            try{
                if (d) System.out.println("Verbindung-init...fuer "+name);
                
                Statement SQLAbfrage = Verbindung_ws.createStatement();
                Ergebnis = SQLAbfrage.executeQuery(Anfrage);
            }
            catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}
            
            // Nun Umwandlen ResultSet in String
            
            ergString.append("Hier gehts los: ");
            try {
                while (Ergebnis.next()) {
                    ergString.append(Ergebnis.getString(1));
                    ergString.append("\n");
                    
                }// elihw
            } catch (SQLException e) {
                ergString.append("für ");
                ergString.append(name);
                ergString.append(" gibt es nichts!");
             }
            //if (d)System.out.println("Ergebnis DB: "+ergString);
        } else {  // wort nicht in DB gefunden
            System.out.println("Wort '"+name+"' nicht in DB!");
            ergString.append("Ein Fehler ist aufgetreten, tut mir leid!");
        } //esle
                ergString.append(" und jetzt Schluss!");
        return new String(ergString);
    } // end getNof


    public int nrOfLex(String lex) {
        ResultSet Ergebnis=null;
        String Anfrage="Select count(*) from person where wort_lex='"+lex+"'";
        int retInt=0; 
        
        try{
            
            Statement SQLAbfrage = Verbindung_akt.createStatement();
            Ergebnis = SQLAbfrage.executeQuery(Anfrage);
            
        }
        catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}
        if(Ergebnis==null)
            return -1;
        try {
            while (Ergebnis.next()) {
                retInt= Ergebnis.getInt(1);
                
            }// elihw
        }catch (SQLException e) {retInt=-1;}
        
        return retInt;
        
        
    } // end nrOfLex



    public void SQLstatement(String statement) throws SQLException{

	// Fuehre SQL-Befehl aus

	try{	    
	    Statement SQLAbfrage = Verbindung_akt.createStatement();
	    SQLAbfrage.executeQuery(statement);
	}
	catch (SQLException e) {System.out.println("Datenbankfehler!"+e.getMessage());}


	
    } // end SQLstatement

    public void close() {
        fetcherThread.interrupt();
    }
} // end DBaccess











