/*
 * Created on 17.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package namerec;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

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
    
    Set threads=new HashSet();
    private DBaccess db;
    
    public NewItemRecognizer(Vector rules,  double schwelle, int threadnum, DBaccess db) {
        this.schwelle=schwelle;
        this.rules=rules;
        toTest=new BlockingQueue(Integer.MAX_VALUE);
        if(threadnum <=0)
            threadnum=1;
        if(threadnum > 200)
            threadnum=200;
        for(int i=0; i < threadnum;i++) {
            Thread testthread=new Verifier((DBaccess) db.clone());
            testthread.setName("Checker"+i);
            testthread.setDaemon(true);
            testthread.start();
            threads.add(testthread);
        }
    }
    private class Verifier extends Thread{
        private DBaccess db;
        public Verifier(DBaccess db) {
            this.db=db;
        }
    public void run() {
        while(true) {
            NameTable kandidaten=(NameTable) toTest.dequeue();
            if(kandidaten.size() > 0)
                Recognizer.addWissen(Recognizer.checkCandidates(kandidaten,schwelle,rules,db));
        }
    }
    }
    public void addTask(NameTable kandidaten) {
        toTest.enqueue(kandidaten);
    }
    public void waitTillJobsDone() {
        toTest.waitTillEmpty();
    }
}
