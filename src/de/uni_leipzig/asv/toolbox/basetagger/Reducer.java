/** 
 * @title Prefixkompression - Demo fuer Training und Test von Prefixbaumklassifikatoren 
 * @author Christian Biemann 
 * @version 12.04.2003
 *
 * Aufruf: java LoadClass classify.tree testfile thresh
 * 
 *  Testfile hat Struktur WORT1 <tab> KLASSE1 <CRLF> WORT2 ...
 * neben Evaluierung mit verschiedneen thresholds aird Baum auch in "compr.tree" gespeichert

 */

package de.uni_leipzig.asv.toolbox.basetagger;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;





public class Reducer {
    
	Pretree grfTree= new Pretree();
	static String anweisungGrf=new String();
	static boolean d=!true; // debugguing
	StringTokenizer st_gezu;

	public Reducer(String tree, boolean ic) {
	   this.init(tree, ic);
	}

   	static String reverse(String torev) {
        	String ret=new String();
        	for(int i=torev.length();i>0;i--) { ret+=torev.substring(i-1,i);}
        	return ret;
    	}



       public String reduce(String wort) {
             String retwort=wort;
	     
             anweisungGrf=grfTree.classify(reverse(wort));
 	     if (d) System.out.println("Anweisung für "+wort+": "+anweisungGrf);
             if (!anweisungGrf.equals("undecided")) {
                StringTokenizer kommatok=new StringTokenizer(anweisungGrf,",");
                anweisungGrf=kommatok.nextToken(); // nehme bei mehreren nurerstes
                // parsing anweisung
                String zahlStr=new String();
                String suffix=new String();

                for(int i=0;i<anweisungGrf.length();i++) {
                        char c=anweisungGrf.charAt(i);
                        //System.out.println("Parse: "+c+" "+(int)c);
                        if ( ((int)c <58)&&( (int)c >47)) {zahlStr+=c;}
                        else {suffix+=c;}
                } // rof i


 		if(d) System.out.println(anweisungGrf+"->"+zahlStr+"-"+suffix+"'");

                int cutpos=new Integer(zahlStr).intValue();
                if (cutpos>retwort.length()) {cutpos=retwort.length();}
                retwort=retwort.substring(0,retwort.length()-cutpos)+suffix;

		// hier #ge zbd #zu-behandlung
		if (retwort.indexOf("#")>0) {
		    st_gezu=new StringTokenizer(retwort,"#");
		    String wordd=st_gezu.nextToken();
		    String toElim=st_gezu.nextToken();
		    int pos=wordd.indexOf(toElim);
		    if (pos>0) {
			wordd=wordd.substring(0,pos)+wordd.substring(pos+toElim.length(),wordd.length());
		    }	
		    retwort=wordd;
		}

              }
	      if (d) System.out.println(retwort);
              return retwort;

        }




	public void init(String gfred, boolean ic) {
		// Bäume initialisierung
		// System.out.println("Loading from "+grfFile);
	     try {

		grfTree.lade(gfred);
                if (d) System.out.println("loaded");
		grfTree.setIgnoreCase(ic);  // Trainingsmenge in lowcase :(
		grfTree.setThresh(0.46);  // weiss nicht?

	    } catch (FileNotFoundException e) {/*  */}	     
	}


} // end class Zerleger



