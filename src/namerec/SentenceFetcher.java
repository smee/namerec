/**
 * 
 */
package namerec;


class SentenceFetcher extends Thread implements SatzDatasource{
    BlockingQueue sentences;
    
    private final DBaccess baccess;
    private int pos;

    private int endNr;
    private final int NUM;
    
    public SentenceFetcher(DBaccess baccess, int startnr, int endNr, int num) {
        this.baccess = baccess;
        this.pos=startnr;
        this.endNr=endNr;
        this.NUM=num;
        sentences=new BlockingQueue(10000);
        setDaemon(true);
        start();
    }
    
    public void run() {
        while(true) {
            if(baccess.retrieveNextSentences(this, pos, Math.min(pos+NUM,endNr)) == false) {
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
}