package com.bordag.klf.ui;

import java.util.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
*	This class implements the abstract FileFilter to give us the possibility
*	to implement filters in our FileChoosers
*	@author		Stefan Bordag
**/
public class SprachFileFilter extends FileFilter
{
	private Vector extensions;
	private String description = "Filter";
	
	/**
	 *  Default constructor, initializes with no filters, i.e. letting 
	 *  through all kinds of files
	 */
	public SprachFileFilter()
	{
		extensions = new Vector();
	}
	
	/**
	*	The filechooser goes through all files it sees and asks the 
	*	Filter whether it will let it pass
	**/
	public boolean accept(File file)
	{
		if (file.isDirectory()) // allow all directories
		{
			return true;
		}
		// try all known extensions
		for(Enumeration e=extensions.elements();e.hasMoreElements();)
		{
			if (file.getName().endsWith((String)e.nextElement()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	*	An extension can be added to the known extensions
	**/
	public void addExtension(String ext)
	{
		extensions.add("."+ext); // also adds a dot
	}
	
	/**
	*	Sets the description which appears in the Filechooser
	**/
	public void setDescription(String text)
	{
		description = text;
	}
	
	/**
	*	Returns the description (the Filechooser wants it)
	**/
	public String getDescription()
	{
		return description+" "+extensions;
	}

}// End of SprachFileFilter.java
