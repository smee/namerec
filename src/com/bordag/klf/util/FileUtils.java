package com.bordag.klf.util;

// standard imports
import java.util.*;
import java.io.*;

/**
 *  This class can not be instantiated, it is only to provide the static 
 *  methods which help to read data in the desired format.
 *  @author		Stefan Bordag
 **/
public class FileUtils
{
	/**
	 *  The only constructor private to prevent instantiaing from this class
	 **/
	private FileUtils()
	{
	}
	
	/**
	 *  Reads data from file and returns all the pairs as 
	 *  Strings in a Vector
	 **/
	public static Vector getDataFromFile(File file)
	{
		Vector data = new Vector();;
		FileInputStream f;
		try // to open the file
		{
			f = new FileInputStream(file);
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			return data;
		}
		
		try // to read the file
		{
			data = readData(f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try // to close the file
		{
			f.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return data;
		}
		
		return data;
		
	}
	
	/**
	 *  helper method to do the raw reading from the opened file
	 **/
	private static Vector readData(FileInputStream f)
	{
		Vector data = new Vector();
		boolean cont = true;
		String buf = new String("");
		while (cont)
		{
			int i=0;
			try  // read next char
			{
				i = f.read();
				if (i==-1){cont=false;}
			}
			catch(Exception e){return data;}
			
			if ((char)i=='\n') // End of line reached
			{
				buf = convert(buf);
				if (StringUtils.checkForm(buf))
				{
					data.add(buf);
				}
				buf = "";
				continue;
			}
			// add char to string
			if (i!=13){buf = buf+""+String.valueOf((char)i);}
		}		
		return data;
	}
	
	/**
	 *  Tries to make a valid input data format from the raw read input
	 *  basically puts the first string a whitespace and the last found string together
	 **/
	private static String convert(String buf)
	{
		if ( buf.length() < 2 ){return "";}
		buf = buf.trim();
		buf = buf.replace('\t', ' ');
		if ( buf.indexOf(' ') < 0 ){return "";}
		String a1 = buf.substring(0,buf.indexOf(' ')).trim();
		String a2 = buf.substring(buf.lastIndexOf(' ')).trim();
		return a1+" "+a2;
	}
} // End of FileUtils