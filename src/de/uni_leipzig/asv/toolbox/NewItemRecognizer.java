/*
 * Created on 17.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_leipzig.asv.toolbox;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.uni_leipzig.asv.toolbox.util.BlockingQueue;


/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewItemRecognizer{

    private double schwelle;
    private Vector rules;
    private BlockingQueue toTest;
    private Map sentenceMap;
    
    Set threads=new HashSet();
    ThreadGroup workers;
    
    private Recognizer rec;
    
    public NewItemRecognizer(Recognizer rec, Vector rules,  double schwelle, int threadnum, DBaccess db) {
        this.schwelle=schwelle;
        this.rules=rules;
        this.rec=rec;
        toTest=new BlockingQueue(100000);
        sentenceMap=Collections.synchronizedMap(new HashMap());
        
        if(threadnum <=0)
            threadnum=1;
        if(threadnum > 200)
            threadnum=200;
        workers = new ThreadGroup("verifiergroup");
        for(int i=0; i < threadnum;i++) {
            Thread testthread=new Verifier((DBaccess) db.clone(), workers,"worker_"+i);
            testthread.setName("Checker"+i);
            testthread.setDaemon(true);
            testthread.start();
            threads.add(testthread);
        }
    }
    private class Verifier extends Thread{
        private DBaccess db;
        private RulesNE rules_ne;
        public Verifier(DBaccess db, ThreadGroup workers, String name) {
            super(workers,name);
            rules_ne=rec.createRulesNE(db);
            this.db=db;
        }
    public void run() {
        while(true) {
            Object o = toTest.dequeue();
            if(o instanceof NameTable) {
                NameTable kandidaten=(NameTable) o;
                if(kandidaten.size() > 0){
                    rec.addWissen(rec.checkCandidates(kandidaten,schwelle,rules,db),(String)sentenceMap.remove(kandidaten));
                }
            }else {//List von String tokens, also NE-erkennung
                String text =(String)o;
                rec.findNEs(text, rules_ne);
            }
        }
    }
    }
    public void addTask(NameTable kandidaten, String sentence) {
        sentenceMap.put(kandidaten,sentence);
        toTest.enqueue(kandidaten);
        
    }

    public void waitTillJobsDone(int samples, String prefix) {
        toTest.waitTillEmpty(samples,prefix);
    }

    /**
     * @param list
     */
    public void addTask(String text) {
        toTest.enqueue(text);
    }
}
