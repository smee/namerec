package namerec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import namerec.util.Config;



public class DBaccess implements Cloneable{
    public static boolean d=true; // debugging aus
    
    Connection Verbindung_ws; // Verbindung zu WORTSCHATZ
    Connection Verbindung_akt; // Verbindung zu WDTaktuell

    private String akt;

    private String ws;

    private String dbTreiber;

    private final String version;


    
    public DBaccess() {
        this("org.gjt.mm.mysql.Driver");
    }
    
    public DBaccess(String dbTreiber) {
        this(dbTreiber,
                "jdbc:mysql://localhost/de?user=toolbox&password=booltox",
                "jdbc:mysql://localhost/wdt_test?user=toolbox&password=booltox",
                "NameRec 1.1neu");
    }
    public DBaccess(Config cfg) {
        this(cfg.getString("DB.DBCLASS","org.gjt.mm.mysql.Driver"),
             cfg.getJDBCStringWS("jdbc:mysql://localhost/de?user=toolbox&password=booltox"),
             cfg.getJDBCStringAKT("jdbc:mysql://localhost/wdt_test?user=toolbox&password=booltox"),
             cfg.getString("OPTION.VERSION","NameRec 1.1neu"));
        
    }
    /**
     * @param string
     * @param string2
     * @param string3
     * @param bspnr
     */
    public DBaccess(String dbTreiber, String ws, String akt, String version) {
        // init DB
        this.ws=ws;
        this.akt=akt;
        this.dbTreiber=dbTreiber;
        this.version=version;
        initConnections();
    }

    /**
     * @param dbTreiber
     * @param ws
     * @param akt
     */
    private void initConnections() {
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
            e.printStackTrace();
            System.exit(1);
        }catch (ClassNotFoundException e) {
            System.err.println("Unknown classname for jdbcdriver!");
            e.printStackTrace();
            System.exit(1);
        }
    }




    public String getNof(String name, int anz)  throws SQLException{
    	int wortnr = getWortNr(name);
        return new String(new StringBuffer(getNof(name,anz,wortnr,Verbindung_ws)).append(getNof(name,anz,wortnr,Verbindung_akt)));
    }
    private String getNof(String name, int limit, int wortnr, Connection Verbindung) throws SQLException{
        ResultSet Ergebnis=null;
        StringBuffer ergString=new StringBuffer();

        if (d) {System.out.println("Wort '"+name+"' hat Nr.  "+wortnr);}
        
        if (wortnr>0) {  // nur, wenn wort auch in DB
            String Anfrage= "Select beispiel from saetze s, inv_liste i where i.wort_nr="+wortnr+" and i.bsp_nr=s.bsp_nr limit "+limit;   
            try{
                if (d) System.out.println("Verbindung-init...fuer "+name);
                
                Statement SQLAbfrage = Verbindung.createStatement();
                Ergebnis = SQLAbfrage.executeQuery(Anfrage);
            }
            catch (SQLException e) {
            	System.out.println("Datenbankfehler!"+e.getMessage());
            	e.printStackTrace();	
            }
            
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


    /**
	 * @param name
	 * @param Verbindung
	 * @param anzahl
	 * @return
	 */
	private int getWortNr(String name) {
		ResultSet Ergebnis=null;
		int anzahl=0;
		String Anfrage= "SELECT wort_nr FROM wortliste WHERE wort_bin='"+name+"'";
        try{	    
            Statement SQLAbfrage = Verbindung_akt.createStatement();
            Ergebnis = SQLAbfrage.executeQuery(Anfrage);
        }
        catch (SQLException e) {
            System.out.println("Datenbankfehler bei SQL-Statement: "+Anfrage);
            e.printStackTrace();
        }
                try {
            boolean correct=Ergebnis.next();
            if(correct)
                anzahl=Ergebnis.getInt(1);
            else
                anzahl=0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return anzahl;
	}

	public int nrOfLex(String lex) {
        ResultSet Ergebnis=null;
        String Anfrage="Select count(*) from person where wort_lex='"+lex+"' and quelle='"+version+"'";
        int retInt=0; 
        
        try{
            
            Statement SQLAbfrage = Verbindung_akt.createStatement();
            Ergebnis = SQLAbfrage.executeQuery(Anfrage);
            
        }
        catch (SQLException e) {
        	System.out.println("Datenbankfehler!"+e.getMessage());
        	e.printStackTrace();
        }
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

    boolean retrieveNextSentences(SentenceFetcher fetcher, int from, int to) {
        System.out.println("SentenceFetcher: fetching ["+from+","+to+")...");
        String Anfrage= "Select beispiel from saetze where bsp_nr>="+from+" and bsp_nr <"+to;   
        ResultSet Ergebnis=null;
        try{
            
            if (DBaccess.d) {System.out.println("Verbindung-init...fuer ["+from+","+to+")");}
            
            Statement SQLAbfrage = Verbindung_ws.createStatement();
            Ergebnis = SQLAbfrage.executeQuery(Anfrage);
            
        }
        catch (SQLException e) {
        	System.out.println("Datenbankfehler!");
        	e.printStackTrace();
        }
        
        // Nun Umwandlen ResultSet in String
        try {
            while (Ergebnis.next()) {
                fetcher.sentences.enqueue(Ergebnis.getString(1));
            }// elihw
        } catch (SQLException e) {
            System.out.println("Fehler beim Empfangen von Satz Nr. ["+from+","+to+")");
            fetcher.sentences.enqueue("END");
            return false;
        }
        return true;
    }

    public Object clone() {
        DBaccess clone=null;
        try {
            clone = (DBaccess) super.clone();
            clone.initConnections();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        
        return clone;
    }

    public int getMaxSentenceNo() {
        String Anfrage= "Select max(bsp_nr) from saetze";   
        ResultSet Ergebnis=null;
        try{
            Statement SQLAbfrage = Verbindung_ws.createStatement();
            Ergebnis = SQLAbfrage.executeQuery(Anfrage);
        }
        catch (SQLException e) {
            System.out.println("Datenbankfehler!");
            e.printStackTrace();
        }
        
        // Nun Umwandlen ResultSet in String
        try {
            return Ergebnis.getInt(1);
        } catch (SQLException e) {
            System.out.println("Fehler beim Empfangen der groessten Satxnummer!");
            return Integer.MAX_VALUE;
        }
    }

} // end DBaccess











