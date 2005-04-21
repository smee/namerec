/*
 * Created on 17.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package namerec;

import java.util.Vector;

/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewItemRecognizer implements Runnable{

    private double schwelle;
    private Vector rules;
    private BlockingQueue toTest;
    Thread testthread;
    
    public NewItemRecognizer(Vector rules,  double schwelle) {
        this.schwelle=schwelle;
        this.rules=rules;
        toTest=new BlockingQueue(Integer.MAX_VALUE);
        testthread=new Thread(this);
        //testthread.setDaemon(true);
        testthread.setName("Checker");
        testthread.start();
    }
    public void run() {
        while(true) {
            if(Recognizer.stopEverything && toTest.empty()) {
                System.out.println("Verfier-thread ends...");
                break;
            }
            NameTable kandidaten=(NameTable) toTest.dequeue();
            if(kandidaten==null) {
                System.out.println("Verfier-thread ends...");
                return;//fertig
            }
            if(kandidaten.size() > 0)
                Recognizer.addWissen(Recognizer.checkCandidates(kandidaten,schwelle,rules));
        }
    }
    
    public void addTask(NameTable kandidaten) {
        toTest.enqueue(kandidaten);
    }
    public void stop() {
        testthread.interrupt();
    }
    
}
