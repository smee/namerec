/** 
 * @author Christian Biemann 
 * @version 27.04.2005
 *
 * Base form reduction for words with tags

 */

package basetagger;



public class BaseTaggerReducer {
    
    
    static String w=new String();
    
    static Reducer reducerN; 
    static Reducer reducerA; 
    static Reducer reducerV;               
                
    static boolean d=false;     // Debugging AUS
    static double thresh=0.51;
    static boolean rev=!true;
    static boolean disp=true;
   
    static String reverse(String torev) {
	String ret=new String();
	for(int i=torev.length();i>0;i--) { ret+=torev.substring(i-1,i);}
	return ret;
    }


    // Constructor: Initialize with names of Pretrees for N, A, V wordclasses
    public BaseTaggerReducer(String filename_adjs, String filename_nouns, String filename_verbs) {
       // initialize Pretrees
       reducerA=new Reducer(filename_adjs, true);
       reducerV=new Reducer(filename_verbs, true);
       reducerN=new Reducer(filename_nouns, false);
       if (d) System.out.println("BaseTaggerReducer initialized");
    } // end constructor   


    public WordBase[] reduce(WordBase[] thissentence) {
    
      if (d) System.out.print("REDUCER: ");
      for (int i=0;i<thissentence.length;i++) {
        String wortStr=thissentence[i].getWordStr();
        String tag=thissentence[i].getPos();      
        String baseForm= wortStr;
        
        if (tag.equals("N")) { baseForm=reducerN.reduce(wortStr);} 
        if (tag.equals("A")) { baseForm=reducerA.reduce(wortStr);} 
        if (tag.equals("V")) { baseForm=reducerV.reduce(wortStr);} 
        
        thissentence[i].setBase(baseForm);
        
        if (d) System.out.print(thissentence[i].getWordStr()+"["+thissentence[i].getPos()+"]"+thissentence[i].getBase()+" ");    
      }
      if (d) System.out.println();
      
      return thissentence;
   } // end reduce
   
   
} // end class BaseTaggerReducer



