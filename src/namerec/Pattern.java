package namerec;

import java.util.Arrays;

public class Pattern {

    public final String goalClass;
    public final int length;
    public final int goalPos;
    public int dot;
    public String[] pattern;
    public String[] word;
    public int hits;
    public int misses;
    public double rating;


    public Pattern(String gC,int l,int gP, String[] p) {
	this.goalClass=gC;
	this.length=l;
	this.goalPos=gP;
	this.pattern=new String[l];
	this.word=new String[l];
	for(int i=0;i<l;i++) {
	    this.pattern[i]=p[i];
	    this.word[i]="nix";
	}
	this.hits=0;
	this.misses=0;
	this.rating=0;
	this.dot=0;
    }// end init


    public boolean equals(Object other) {
        if(other instanceof Pattern) {
            Pattern o=(Pattern)other;
            return goalClass.equals(o.goalClass) &&
                   length==o.length &&
                   goalPos==o.goalPos &&
                   hits==o.hits &&
                   misses == o.misses &&
                   rating == o.rating && 
                   dot == o.dot &&
                   Arrays.equals(pattern, o.pattern) &&
                   Arrays.equals(word, o.word);
        }else
            return false;
    }
    public String toString() {
        String retStr=new String();
        
        retStr="(class="+goalClass+",length="+length+",goalPos:"+goalPos+",pattern= ";
        for(int i=0;i<length;i++) {retStr+=pattern[i]+" ";}
        retStr+=",hits="+hits+",misses="+misses+",rating:"+rating;
        retStr+=" )";
        
        return retStr;
        
    } // end toString()

    
    public void calcRating() {
	if ((hits+misses)>0) {rating=(1+(double)(hits-misses)/(double)(hits+misses))/2;} else {rating=0;}
    } // end calcRating


    /**
     * 
     */
    public void reset() {
        this.dot=0;        
    }

} // end class Pattern
