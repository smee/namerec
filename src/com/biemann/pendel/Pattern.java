package com.biemann.pendel;
import java.io.*;

public class Pattern implements Serializable {

    public String goalClass;
    public int length;
    public int goalPos;
    public int dot;
    public String[] pattern;
    public String[] word;
    public int hits;
    public int misses;
    public double rating;


    public void init(String gC,int l,int gP, String[] p) {
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

    public void partialInit() {
      	this.word=new String[length];
        for(int i=0;i<length;i++) {
	              this.word[i]="nix";
        } // rof
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

} // end class Pattern
