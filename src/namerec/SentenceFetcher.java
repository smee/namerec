/**
 * 
 */
package namerec;


class SentenceFetcher extends Thread implements SatzDatasource{
    BlockingQueue sentences;
    
    private final DBaccess baccess;
    private int pos;

    private int endNr;

    public SentenceFetcher(DBaccess baccess, int startnr, int endNr) {
        this.baccess = baccess;
        this.pos=startnr;
        this.endNr=endNr;
        sentences=new BlockingQueue(1500);
        setDaemon(true);
        start();
    }
    
    public void run() {
        while(true) {
            if(baccess.retrieveNextSentences(this, pos, Math.min(pos+1000,endNr)) == false) {
                System.out.println("enqueuing END");
                sentences.enqueue("END");
                break;//sind mit der DB durch
            }
            pos+=1000;            
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