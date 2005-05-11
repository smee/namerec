/** 
 * @author Christian Biemann 
 * @version 08.05.2005
 *
 * 
 * Interface for BaseWorder, Base form reduction for words
 */


package basetagger;


public class BaseWorderInterface {

    private static boolean d=false; //Debugging
    
    private static BaseWorder reducer;

//    private LineSegmenter segmenter;
    /**
     * default constructor: übergeben der parameter, zum einstellen der
     * Sprachwerte
     *
     * @param s --
     *            input-data (text)
     */
    public BaseWorderInterface(String language, String directory) {
       this.init(language, directory);
        
    }

    public BaseWorderInterface() {
   
        
    }


    private static void init(String language, String directory) {

        reducer = new BaseWorder(language, directory); 

    }


    /**
     * the iterator
     * 
     * @return Returns the iterator (which returns WordBases)
     */
    public BaseWorder getReducer() {
        BaseWorder ret = this.reducer;
        this.reducer = null;
        return ret;
    }

    

    // ======================================

    public static void main( String[] args ) {


        if ( args.length != 2  ) {
            System.err.println( "Parameters (2): language:en/de datadir)" );
            System.exit( 1 );
        }

        init(args[0], args[1]);

        
        System.out.println(reducer.baseform("Dies ist ein Test für einen Satz, der grundformreduziert werden solle!"));
        System.out.println(reducer.baseform("Boote"));
        System.out.println(reducer.baseform("Kommunalabgaben"));
        System.out.println(reducer.baseform("verdiente"));
        System.out.println(reducer.baseform("verdicteten"));
        
     


        System.err.println( "\n_done_" );

    }
}
