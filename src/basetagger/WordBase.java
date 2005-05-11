/** 
 * @author Frieder Witschel, Chris Biemann
 * @version 08.05.2005
 *
 * 
 * Data Structure for Words
 */



package basetagger;

import java.util.Vector;


public class WordBase implements Comparable{

	protected String wordStr;
	protected String baseform;
	protected int freq;
	protected String pos;
	protected Vector parts;
	protected double sig;

	/**
	* Constructor that needs a word and its frequency only
	* @param w the String that makes up the word
	* @param f the frequency of this word (in a certain text)
	*/
	public WordBase(String w, int f){
		wordStr = w;
		freq = f;
	}

	/**
	* Constructor that initialises word string and pos
	*/
	public WordBase(String w, String p){
		wordStr = w;
		pos = p;
	}
	/**
	* Constructor that initialises word string, pos and baseform
	*/
	public WordBase(String w, String p, String b){
		wordStr = w;
		pos = p;
		baseform= b;
	}
	
	

	/**
	* Returns a String representation of a word, containing the word string, frequency and significance
	* value
	*/
	public String toString(){
		String ret = wordStr + ", " + freq + ", " + sig + ", " + pos;
		return ret;
	}

	/**
	* method that checks for equality of two words: they are equal if they have the same
	* word string
	*/
	public boolean equals(Object o){
	
		if ( this == o ) return true;
		if ( !(o instanceof WordBase) ) return false;

		WordBase that = (WordBase)o;
		return this.wordStr.equals(that.getWordStr());
	}

	/**
	* compareTo method: yields -1 if sig of other Word is less than this word's
	*/
	public int compareTo(Object o){
		final int LOWER = 1;
		final int EQUAL = 0;
		final int GREATER = -1;

		if (this == o) return EQUAL;
		final WordBase that = (WordBase)o;
		if(this.sig < that.getSig())return LOWER;
		if(this.sig > that.getSig())return GREATER;
		if(this.sig == that.getSig())return EQUAL;

		return EQUAL;
	}
	
	public int hashCode(){
		return this.wordStr.hashCode();
	}

	// ---------------------- getter methods -----------------------------
	public String getWordStr(){
		return wordStr;
	}

	public int getFreq(){;
		return freq;
	}

	public Vector getParts(){
		return parts;
	}

	public double getSig(){
		return sig;
	}

	public String getPos(){
		return pos;
	}
	
        public String getBase(){
		return baseform;
	}
	

	public void setSig(double s){
		sig = s;
	}
	
	public void setFreq(int f){
		freq = f;
	}
	
	public void setPOS(String p){
		pos = p;
	}
	
	public void setParts(Vector p){
		parts = p;
	}
	
	public void setBase(String b){
		baseform = b;
	}
	
	

}
