/**
 * 
 */
package de.uni_leipzig.asv.toolbox;

import de.uni_leipzig.asv.toolbox.util.BlockingQueue;


class SentenceFetcher extends Thread implements SatzDatasource{
    private static Thread thread;

    BlockingQueue sentences;
    
    private final DBaccess dbaccess;
    private int pos;

    private final int endNr;
    private final int NUM;
    
    public SentenceFetcher(DBaccess dbaccess, int startnr, int endNr, int num) {
        this.dbaccess = dbaccess;
        this.pos=startnr;
        if(endNr < 0)
            endNr=Integer.MAX_VALUE;
        this.endNr=endNr;
        this.NUM=num;
        sentences=new BlockingQueue(10000);
        setDaemon(true);
        start();
        
    }
    
    public void run() {
        thread=Thread.currentThread();
        while(true) {
            if(dbaccess.retrieveNextSentences(this, pos, Math.min(pos+NUM,endNr)) == false) {
                System.out.println("enqueuing END");
                sentences.enqueue("END");
                break;//sind mit der DB durch
            }
            pos+=NUM;            
            if(Math.min(pos,endNr) >= endNr) {
                System.out.println("enqueuing END");
                sentences.enqueue("END");
                break;
            }
        }
    }

    public String getNextSentence() {
        return (String) sentences.dequeue();
    }

    public static void stopThread() {
        thread.stop();        
    }

    public int getNumOfSentences() {
        return dbaccess.getNumOfSentences();
    }
}