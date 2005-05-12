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
  
    
    private BaseTaggerTagger tagger;

    

    // constructor
    public BaseWorder( ) {
    }


    public BaseWorder( String language, String directory ) {
    
    
        if (d) System.err.print( "BaseTagger: starting BaseTaggerIterator... " );
        if (language.equals("de")) {
          tagger=new BaseTaggerTagger(directory+"de-vv-tag.tree",directory+"de-vh-tag.tree");
        } else if (language.equals("en")) {
          tagger=new BaseTaggerTagger(directory+"en-vv-tag.tree",directory+"en-vh-tag.tree");
        } else { System.err.println("Language "+language+" not recognized. Possible: de, en");}
        
    }



    public String getTag(String toTag) {
        WordBase[] base=new WordBase[] {new WordBase(toTag,0)};
        tagger.tag(base);
        return base[0].getPos();
    }

} // end class BaseWorder
