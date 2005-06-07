package com.bordag.sc.util;

import java.io.*;
import java.util.*;

/**
 * Parses a file for sentences and returns them in a vector of String[]
 * In this prototypical implementation the file is alsready supposed to contain
 * one sentence per line.
 */
public class SentenceParser
{
  protected Vector sentences = null;

  public SentenceParser(String fileName)
  {
    Vector lines = readFile(fileName);
    this.sentences = new Vector();
    for ( Iterator it = lines.iterator() ; it.hasNext() ; )
    {
      String curLine = (String)it.next();
//      curLine = curLine.replaceAll(" "," ");
      curLine = curLine.replaceAll("\\!|\\\"|\\#|\\$|\\%|\\&|\\'|\\(|\\)|\\*|\\+|\\,|\\.|\\/|\\:|\\;|\\<|\\=|\\>|\\?|\\@|\\[|\\]|\\^|\\_|\\`|\\{|\\|}|\\~",""); // \\!|\\"|\\#|\\$|\\%|\\&|\\'|\\(|\\)|\\*|\\+|\\,|\\.|\\/|\\:|\\;|\\<|\\=|\\>|\\?|\\@|\\[|\\\|\\]|\\^|\\_|\\`|\\{|\\|}|\\~  // !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
      curLine = curLine.replaceAll("\\d","");
      curLine = curLine.replaceAll("  "," ");
      curLine = curLine.replaceAll("  "," ");
      curLine = curLine.replaceAll("  "," ");
      String[] sentence = curLine.split(" ");
      sentence = filterBadWords(sentence);
      if ( sentence.length > 3 )
      {
        this.sentences.add(sentence);
      }
    }
  }

  protected String[] filterBadWords(String[] sentence)
  {
    Vector temp = new Vector();
    for ( int i = 0 ; i < sentence.length ; i++ )
    {
      if ( sentence[i].length() > 3 )
      {
        temp.add(sentence[i]);
      }
    }
    String[] retVal = new String[temp.size()];
    for ( int i = 0 ; i < retVal.length ; i++ )
    {
      retVal[i] = (String)temp.get(i);
    }
    return retVal;
  }

  public Vector getSentences()
  {
    return this.sentences;
  }

  /**
   * Puts each line of the file into a separate entry of a Vector which is then
   * returned. The method String.trim is also called on each line before putting
   * it into the vector.
   * @param filename
   * @return A vector with the file contents
   */
  private Vector readFile(String fileName)
  {
    Vector retVec = new Vector();
    try
    {
      BufferedReader reader = null;
      if ( fileName != null)
      {
        reader = new BufferedReader(new FileReader(fileName));
      }

      String temp = null;
      while ( reader.ready() )
      {
        temp = reader.readLine().trim();
        if ( temp != null && temp.length() > 0 )
        {
          retVec.add(temp);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return retVec;
  }

}