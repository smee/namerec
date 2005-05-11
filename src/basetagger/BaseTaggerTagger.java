/** 
 * @author Christian Biemann 
 * @version 08.05.2005
 *
 * 
 * Tagging of words by most frequent tag  

 */


package basetagger;
import java.io.FileNotFoundException;




public class BaseTaggerTagger {
    
    
    static String w=new String();
    static Pretree baumVV; //= new Pretree();
    static Pretree baumVH; //= new Pretree();
    static boolean d=false;     // Debugging AUS
    static double thresh=0.51;
    static boolean rev=!true;
    static boolean disp=true;
   
    static String reverse(String torev) {
	String ret=new String();
	for(int i=torev.length();i>0;i--) { ret+=torev.substring(i-1,i);}
	return ret;
    }


    // Constructor: Initialize with names of Pretrees for front and back
    public BaseTaggerTagger(String filename_treeVV, String filename_treeVH) {
    try {
        baumVV=new Pretree();
        baumVV.lade(filename_treeVV);
        baumVV.setIgnoreCase(false);
        baumVV.setThresh(0.46);
        baumVH=new Pretree();
        baumVH.lade(filename_treeVH);
        baumVH.setIgnoreCase(false);
        baumVH.setThresh(0.46);
    } catch (FileNotFoundException e) {System.out.println(e.getMessage());}    
    } // end constructor   


    public WordBase[] tag(WordBase[] thissentence) {

        if (d) System.out.println("Tagger: ");
		
        for(int i=0;i<thissentence.length;i++) {
        
           String klassVV=baumVV.classify(thissentence[i].getWordStr());
           String klassVH=baumVH.classify(reverse(thissentence[i].getWordStr()));
                
           // HEURISTICS obtained by tryouts on train/test/-4.txt
           String klassGes="-";
           if (klassVV.equals(klassVH)) {klassGes=klassVV;}
           if (klassVV.equals("undecided")) {klassGes=klassVH;}
           if (klassVH.equals("undecided")) {klassGes=klassVV;}
           if (klassVV.equals("A")&&klassVH.equals("AV")) {klassGes=klassVH;}
           if (klassVV.equals("A")&&klassVH.equals("N")) {klassGes=klassVV;}
           if (klassVV.equals("A")&&klassVH.equals("NE")) {klassGes=klassVH;}
           if (klassVV.equals("A")&&klassVH.equals("S")) {klassGes=klassVH;}
           if (klassVV.equals("A")&&klassVH.equals("V")) {klassGes=klassVH;}
           if (klassVV.equals("AV")&&klassVH.equals("A")) {klassGes=klassVH;}
           if (klassVV.equals("AV")&&klassVH.equals("N")) {klassGes=klassVV;}
           if (klassVV.equals("AV")&&klassVH.equals("NE")) {klassGes=klassVV;}
           if (klassVV.equals("AV")&&klassVH.equals("S")) {klassGes=klassVV;}
           if (klassVV.equals("AV")&&klassVH.equals("V")) {klassGes=klassVV;}
           if (klassVV.equals("N")&&klassVH.equals("A")) {klassGes=klassVV;}
           if (klassVV.equals("N")&&klassVH.equals("AV")) {klassGes=klassVH;}
           if (klassVV.equals("N")&&klassVH.equals("NE")) {klassGes=klassVH;}
           if (klassVV.equals("N")&&klassVH.equals("S")) {klassGes=klassVH;}
           if (klassVV.equals("N")&&klassVH.equals("V")) {klassGes=klassVH;}
           if (klassVV.equals("NE")&&klassVH.equals("A")) {klassGes=klassVH;}
           if (klassVV.equals("NE")&&klassVH.equals("AV")) {klassGes=klassVH;}
           if (klassVV.equals("NE")&&klassVH.equals("N")) {klassGes=klassVV;}
           if (klassVV.equals("NE")&&klassVH.equals("S")) {klassGes=klassVH;}
           if (klassVV.equals("NE")&&klassVH.equals("V")) {klassGes=klassVH;}
           if (klassVV.equals("S")&&klassVH.equals("A")) {klassGes=klassVV;}
           if (klassVV.equals("S")&&klassVH.equals("AV")) {klassGes=klassVH;}
           if (klassVV.equals("S")&&klassVH.equals("N")) {klassGes=klassVV;}
           if (klassVV.equals("S")&&klassVH.equals("NE")) {klassGes=klassVV;}
           if (klassVV.equals("S")&&klassVH.equals("V")) {klassGes=klassVV;}
           if (klassVV.equals("V")&&klassVH.equals("A")) {klassGes=klassVV;}
           if (klassVV.equals("V")&&klassVH.equals("AV")) {klassGes=klassVH;}
           if (klassVV.equals("V")&&klassVH.equals("N")) {klassGes=klassVV;}
           if (klassVV.equals("V")&&klassVH.equals("NE")) {klassGes=klassVV;}
           if (klassVV.equals("V")&&klassVH.equals("S")) {klassGes=klassVH;}


           thissentence[i].setPOS(klassGes);
           
           if (d) System.out.print(thissentence[i].getWordStr()+"["+thissentence[i].getPos()+"] ");
      } // rof i for all words in sentence
      if (d) System.out.println();
      
 
      return thissentence;
   } // end tag
} // end class BaseTaggerTagger



