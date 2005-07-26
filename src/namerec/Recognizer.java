package namerec;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

import org.apache.commons.math.stat.descriptive.moment.FirstMoment;

import namerec.gui.RecognizerPanel;
import namerec.util.Config;
import namerec.util.ProcessEstimator;

/* Recognizer
 Findet Namen in der Wortschatz-Datenbank WDTAKTUELL,
 neue Namen werde in der WORTSCHATZ-DB überprüft.
 
 Autor: Christian Biemann 04/002
 für Wortschatz Projekt
 
 
 */



public class Recognizer extends Observable{
    
    static boolean d=true; //debugging an
   
    // Nametables fuer Wissen aller Art	
    private NameTable alleRegexp;
    // Regexps
    private NameTable allesWissen;
    // Alle bekannten items
    private NameTable klassKeys;
    // Bitzuordnung zu Klassen
    private Vector canrules= new Vector();
    private String itemFile;    
    private String maybeFile;
    private Rules rules;
    private TextProcessor textProc;
    private DBaccess db;
    private int n_cands;
    private int numofthreads;
    private int startNr;
    private int endNr;
    private double acceptItem;
    
    private MatcherNam matcher;
    private NewItemRecognizer itemrec;
    private Annotate anno;

    private final Config cfg;
    private ProcessEstimator est=null;
    private SatzDatasource ds;
    private final int samples;
    
    public Recognizer(Config cfg, SatzDatasource ds) throws IOException {
        this.cfg=cfg;
        n_cands=cfg.getInteger("OPTION.CANDIDATESNO",30);
        numofthreads=cfg.getInteger("OPTION.NUMOFTHREADS",10);
        startNr=cfg.getInteger("OPTION.STARTNO",0);
        endNr=cfg.getInteger("OPTION.ENDNO",Integer.MAX_VALUE);
        acceptItem=cfg.getDouble("OPTION.ACCEPTITEM",0.1);
        itemFile=cfg.getString("OUT.ITEMSFOUND","itemsFound.txt");
        maybeFile = cfg.getString("OUT.MAYBE","maybe.txt");
        samples=cfg.getInteger("OPTION.SAMPLES",100);
        db=new DBaccess(cfg);
        this.ds=ds;
        init(cfg);        
    }
    
    private void init( Config cfg ) throws IOException  {
        String fileRegexp=cfg.getString("IN.REGEXP","regexps.txt");
        NameTable temp=NameTable.loadFromFile(fileRegexp);
        alleRegexp=new NameTable();
        for (Iterator it = temp.keySet().iterator(); it.hasNext();) {
            String regexp = (String) it.next();
            alleRegexp.put(java.util.regex.Pattern.compile(regexp), temp.get(regexp));//nicht mehr Strings als keys sondern Instanzen von java.util.regex.Pattern
        }
        String fileGrundstock=cfg.getString("IN.KNOWLEDGE","wissenAkt.txt");
        allesWissen=NameTable.loadFromFile(fileGrundstock);
        
        String fileKlass=cfg.getString("IN.CLASSNAMES","klassNamen.txt");
        NameTable temptable=NameTable.loadFromFile(fileKlass);
        klassKeys=new NameTable();
        for (Iterator it = temptable.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            klassKeys.put(key, new Integer(Integer.parseInt((String) temptable.get(key),2)));//sind ja binaerwerte, brauchen aber Integers
        }
        anno=new Annotate(alleRegexp,
                allesWissen,
                klassKeys, 
                cfg.getString("IN.TAGGERDIR","taggerfiles/"),
                Boolean.valueOf(cfg.getString("OPTION.USETAGGER","false")).booleanValue());
        textProc=new TextProcessor(anno,klassKeys);
        matcher=new MatcherNam(anno);
        String patFile=cfg.getString("IN.PATFILE","pats2.txt");
        canrules = matcher.loadPatterns(patFile);
        itemrec = new NewItemRecognizer(this,canrules,acceptItem,numofthreads, db);

        
        String fileContexts=cfg.getString("OUT.CONTEXT","contexts.txt");
        rules=new Rules(patFile,fileContexts); // Regeln init
        rules.resetRules();
        System.out.println("Anzahl Regeln: "+rules.patterns.size());
        
        
    } //end init
    
    
    private static int nlength(String item) {
        // Errechnedt Länge des Namens, Bei NAmen mit Bindestrichen: länsgter Teilname

        int l=0;
        int lmax=0;

        for(int pos=0;pos<item.length();pos++) {
            if (item.charAt(pos)=='-') {
                if (l>lmax) {lmax=l;}
                l=0;
            } else{ l++;}
        } // rof int pos
        if (l>lmax) lmax=l;
        
        return lmax;

       } // end nlength
    
    public NameTable checkCandidates(NameTable toCheck,double schwelle,Vector pattern, DBaccess db){
        // Ueberprueft, ob Kandidaten  in Beispielsätzen, in denen sie vorkommen, auch als "forWhat" klassifiziert werden. Falls der Anteil hoeher als "schwelle" ist, besteht Kandidat Prüfung.
        
        // Es wird WORTSCHATZ und WDTAKTUELL abgefragt
        
        String actItem;
        String actClass;
        int actClassInt=0;
        String itemText;
        int ergClass=0;
        
        Vector ergTest;
        NameTable checked=new NameTable();    //wird mit Kandidaten, die Prüfung bestanden haben, gefüllt.
        int anzOk, anzTot;
        
        try {
        //System.out.println("Tocheck: "+toCheck.toString());
        for (Enumeration checkItems=toCheck.keys();checkItems.hasMoreElements();) {
            actItem=(String)checkItems.nextElement();
            actClass=(String)toCheck.get(actItem);
            actClassInt=((Integer)klassKeys.get(actClass)).intValue();
            // nehme nächsten Kandidaten
            
//          SPEZIAL für VN: VNs mit Länge groesser 10 sind TITEL!

            if (actClass.equals("VN") && nlength(actItem)>10) {
                actClass="TIT";
            }
            
            System.out.println("Ueberpruefe Kandidat "+actItem+"?="+actClass);
            
            // check if already known
            if (allesWissen.containsKey(actItem)) {
                System.out.println("Wissen: "+actItem+"="+allesWissen.get(actItem));
            } // fi allesWissen
            else { // nur, wenn noch nicht im Wissen
                itemText=db.getNof(actItem,n_cands);    // finde Sätze, die Kandidaten enthalten

                ergTest=matcher.getClassificationsOf(actItem,itemText,klassKeys,pattern);     //extrahiere Liste, wie aktName klassifiziert wurde
                anzOk=0; anzTot=0;
                for (Enumeration e=ergTest.elements();e.hasMoreElements();) {
                    ergClass=new Integer(e.nextElement().toString()).intValue();
                    
                    anzTot++;
                    if ((ergClass & actClassInt)==actClassInt) {anzOk++;}        // Zähle Gesamtzahl und Zahl der auf NN klassifizierten
                } // rof Enumeration ergTest
                
                
                if (d) {System.out.println("Kandidat '"+actItem+"' ist "+actClass+" mit "+anzOk+"/"+anzTot);}
                
                if (anzTot>0) { // wenn überhaupt gefunden
                    
                    
                    if (((double)anzOk/(double)anzTot)>schwelle) {
                        if (anzOk>2) {
                            checked.put(actItem,actClass);           // Falls Quotient über Schwelle, akzeptiere Kandidaten
                            if (d) {System.out.println("akzeptiert! Füge ein:\n"+actItem+"\t"+actClass);}
                            NameTable newitem=new NameTable();
                            newitem.put(actItem, actClass);
                            newitem.appendFile(itemFile);
                            
                        } // fi anzOK
                        else { // elsif anzOK
                            // dieser fall wird durch (anzOk>0) nicht erreicht!
                            if (d) {System.out.println("unter Vorbehalt akzeptiert!");}
                            NameTable maybes=new NameTable();
                            checked.put(actItem, actClass);
                            maybes.put(actItem, actClass);
                            maybes.appendFile(maybeFile);
                        } // esle
                    }
                } // fi anzTot
            } // esle (noch nicht im Wissen)
            
        } // rof Enumeration name
        if(checked.size()>0) {
            System.out.println("Returning checked:");
            System.out.println(checked);
        }
        }catch (Exception e) {
            System.err.println("Error on checking candidates: "+toCheck);
            e.printStackTrace();
            return null;
        }
        return checked;
    } // end checkCandidates
    
    
    
    
    public void doTheRecogBoogie() throws Exception {
        SatzDatasource src=getSatzDatasource();
        int bspnr=startNr;
        String text = "";
        int numOfSentences=src.getNumOfSentences();
        initTimeEstimator(numOfSentences);
        
        while(!(text.equals("END"))) {
            if(Thread.currentThread().isInterrupted()) {
                itemrec.workers.stop();
                SentenceFetcher.stopThread();
                return;
            }
            text=src.getNextSentence();
            rules.resetRules(); 
            
            NameTable Kandidaten=textProc.getCandidatesOfText(text,rules);
            if(Kandidaten.size() > 0) {
                System.out.println(bspnr+": "+text);
                System.out.println(Kandidaten.toString());
                itemrec.addTask(Kandidaten,text);
            }            
            bspnr++;
            est.unitCompleted();
            if(bspnr%samples==0)
                RecognizerPanel.getInstance().setStatus("Stage 1: Time remaining till sentences scanned: "+ProcessEstimator.getTimeString(est.projectedTimeRemaining()/1000));
        } 
        itemrec.waitTillJobsDone(samples,"Stage 2: Estimated time till verification completed: ");
        System.out.println("verification done!");
    }
    

    private void initTimeEstimator(int numOfSentences) {
        if(est!=null){
            est.stop();
        }
        est=new ProcessEstimator(numOfSentences,samples);
        est.start();
    }

    /**
     * @throws Exception
     */
    public void runNERecognition() throws Exception {
        SatzDatasource src=getSatzDatasource();
        initTimeEstimator(src.getNumOfSentences());
        String text=src.getNextSentence();
        System.out.println("reviewing sentences for NEs....");
        int i=0;
        while(!(text.equals("END"))) {
            i++;
            itemrec.addTask(text);
            text=src.getNextSentence();      
            est.unitCompleted();
            if(i%samples==0)
                RecognizerPanel.getInstance().setStatus("Stage 3: Time remaining till sentences scanned for NEs: "+ProcessEstimator.getTimeString(est.projectedTimeRemaining()/1000));
        }
        int samples=cfg.getInteger("OPTION.SAMPLES",100);
        itemrec.waitTillJobsDone(samples,"Stage 4: Estimated time till NE recognition completed: ");
        System.out.println("NE recognition done!");
    }

    public static void main(String args[]) throws Exception {
        if(args.length != 1) {
            System.out.println("Usage:\n-------\n\n");
            System.out.println("java -cp namerec2.jar:sqllib.jar namerec.Recognizer configfile");
            return;
        }
        Config cfg=new Config(args[0]);
        System.out.println("Using the following settings from "+args[0]);
        System.out.println(cfg);
        Recognizer rec=new Recognizer(cfg,null);
        rec.doTheRecogBoogie();
        if(Boolean.valueOf(cfg.getString("OPTION.NERECOG","false")).booleanValue()==true)
            rec.runNERecognition();
    } // end main




    /**
     * @return
     * @throws Exception
     */
    private SatzDatasource getSatzDatasource() throws Exception {
        if(ds==null)
            return new SentenceFetcher(db,startNr,endNr,1000);
        return ds;
    }




    /**
     * @param table
     * @param sentence 
     */
    public void addWissen(NameTable table, String sentence) {
        allesWissen.putAll(table);
        setChanged();
        notifyObservers(new Object[]{table,sentence});
    }

    /**
     * @param tokens@param rules_ne2
     */
    public void findNEs(String text, RulesNE rules_ne) {
        rules_ne.resetRules();
        try {
            textProc.getCandidatesOfText(text,  rules_ne);  // Extrahieren der NEs und speichern in DB
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param db2
     * @return
     */
    public RulesNE createRulesNE(DBaccess db) {
        try {
            return new RulesNE(db,cfg.getString("IN.PATFILENE","patPers.txt"),
                    cfg.getString("OUT.COMPLEXNAMES","NEs.txt"),cfg.getBoolean("DB.WRITEBACK",false));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
} // end Recognizer
