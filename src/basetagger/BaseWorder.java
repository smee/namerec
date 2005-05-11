/** 
 * @author Christian Biemann 
 * @version 27.04.2005
 *
 * 
 * Bas form Reduction for words

 */


package basetagger;




public class BaseWorder  {

    private boolean d=false; // Debugging for Status prints
  
    private WordBase[] nextSentence;
    
    private BaseTaggerSeperator seperator;
    private BaseTaggerTagger tagger;
    private BaseTaggerReducer reducer;

    

    // constructor
    public BaseWorder( ) {
    }


    public BaseWorder( String language, String directory ) {
    
    
        if (d) System.err.print( "BaseTagger: starting BaseTaggerIterator... " );
        if (language.equals("de")) {
          tagger=new BaseTaggerTagger(directory+"de-vv-tag.tree",directory+"de-vh-tag.tree");
          reducer=new BaseTaggerReducer(directory+"de-adjectives.tree",directory+"de-nouns.tree",directory+"de-verbs.tree");
        } else if (language.equals("en")) {
          tagger=new BaseTaggerTagger(directory+"en-vv-tag.tree",directory+"en-vh-tag.tree");
          reducer=new BaseTaggerReducer(directory+"en-adjectives.tree",directory+"en-nouns.tree","en-verbs.tree");        
        } else { System.err.println("Language "+language+" not recognized. Possible: de, en");}
        
        seperator=new BaseTaggerSeperator();        
        

    }



    public String baseform(String temp) {
// reduce returns baseform String for fullform String
// can be line or word.
                                                                                 
                   nextSentence=seperator.seperate(temp);
                   nextSentence=tagger.tag(nextSentence);
                   nextSentence=reducer.reduce(nextSentence);
                   
                   // build outline
                   String outline=new String("");
                   outline=outline+nextSentence[0].getBase();

                   for(int i=1;i<nextSentence.length;i++) {
                      outline+=" ";
                      outline=outline+nextSentence[i].getBase();
                   } //rof
                  
       return outline;  
    }
    public String getTag(String toTag) {
        WordBase[] base=new WordBase[] {new WordBase(toTag,0)};
        nextSentence=tagger.tag(base);
        return base[0].getPos();
    }

} // end class BaseWorder
