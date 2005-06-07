package com.bordag.klf.util;

// standard imports
import java.util.*;

/**
 *  StringUtils is a collection of frequently used methods which aren't
 *  in the standard library and it was faster to just implement then 
 *  then to find them somewhere else
 *  @author 	Stefan Bordag
 **/
public class StringUtils
{
	/**
	 *  Hide the constructor so no one can misuse it
	 **/
	private StringUtils()
	{
	}
	
	/**
	 *  Splits a String by the first whitespace into two Strings
	 **/
	public static String[] split(String text)
	{
		String[] output = new String[2];
		output[0] = text.substring(0,text.indexOf(' '));
		output[1] = text.substring(text.indexOf(' '));
		output[0] = output[0].trim();
		output[1] = output[1].trim();
		return output;
	}
	
	/**
	 *	Checks whether a string has the form of two 
	 *	Strings divided by a whitespace
	 **/
	public static boolean checkForm(String text)
	{
		text = text.trim();
		if(text.lastIndexOf(' ')==text.indexOf(' ')&&text.indexOf(' ')!=-1)
		{
			return true;
		}
		return false;
	}
	
	/**
	 *  returns the last character of the given String
	 **/
	public static String getLastChar(String word)
	{
		if (word.length()==0)
		{
			return " ";
		}
		char[] t = new char[1];
		t[0] = word.charAt(word.length()-1);
		return new String(t);
	}
	
	/**
	 *  Chops off the last char of the given String and
	 *  returns the shorter String
	 **/
	public static String chopLastChar(String word)
	{
		if (word.length()<1)
		{
			return " ";
		}
		return word.substring(0,word.length()-1);
	}
	
	/**
	 *  Returns the last character of the given String
	 **/
	public static String getFirstChar(String word)
	{
		if ( word.length() == 0 )
		{
			return " ";
		}
		char[] t = new char[1];
		t[0] = word.charAt(0);
		return new String(t);
	}
	
	/**
	 *  Chops off the last char of the given String and
	 *  returns the shorter String
	 **/
	public static String chopFirstChar(String word)
	{
		if (word.length()<1)
		{
			return " ";
		}
		return word.substring(1,word.length());
	}
	
}// End of StringUtils.java
