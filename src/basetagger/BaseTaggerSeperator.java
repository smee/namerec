/** 
 * @author Christian Biemann 
 * @version 8.05.2005
 *
 * 
 * Seperates a line of text into words
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * to be replaced by something more intelligent!
 */


package basetagger;





public class BaseTaggerSeperator {
   private static boolean d=false; //debugging 
    
   public WordBase[] seperate(String line) {
      if (d) System.out.println("Seperating: "+line);
      // Substituting for special characters
      line=line.replaceAll(","," ,");
      line=line.replaceAll(":"," :");
      line=line.replaceAll("!"," !");
      line=line.replaceAll("[\\?]"," ?");
      line=line.replaceAll("\\("," (");
      line=line.replaceAll("\\)"," )");
      line=line.replaceAll("/"," /");
      line=line.replaceAll("\""," \" ");     
      line=line.replaceAll("'"," '");
      line=line.replaceAll("[\\"+".]"," .");
      
      
   
      String[] temp=line.split(" ");
      WordBase[] thissentence=new WordBase[temp.length];
      for(int i=0;i<temp.length;i++) {
        thissentence[i]=new WordBase(temp[i],0);
      
      } // rof i
      
      return thissentence;
   }    
   
   

} // end class BaseTaggerSeperator



