package com.bordag.colloc;

import java.util.*;
import java.io.*;

/**
 * Reads the stored files for wordstrings.
 *
 * @author Stefan Bordag
 * @date   24.11.2003
 */
public class FileWortliste
{
  protected byte[] indexFile = null;
  protected RandomAccessFile dataFileReader = null;

  protected String fileName = null;

  public FileWortliste(String fileName)
  {
    this.fileName = fileName;
    if ( existsFileDump(fileName) )
    {
      this.indexFile = new byte[(int)(new File(fileName+WortlisteFilePreparer.ext2)).length()];
    }
    else
    {
      return;
    }

    try
    {
      RandomAccessFile reader1 = null;
      if ( fileName != null )
      {
        reader1 = new RandomAccessFile(  fileName + WortlisteFilePreparer.ext2, "r" );
        this.dataFileReader = new RandomAccessFile(  fileName + WortlisteFilePreparer.ext1, "r" );
      }

      reader1.read(this.indexFile);
      reader1.close();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  /**
   * Look up in file2 what's at position n-1 and n and returns the wort_bin
   * from n-1 to n from file1 as Object[2] with Object[0]=String and Object[1]=Integer
   * @param wordNr
   * @return
   */
  public Object[] getWordAndAnzahl(Integer wordNr)
  {
    Object[] retVal = new Object[2];
    retVal[0] = "";
    retVal[1] = new Integer(0);
    if ( (wordNr.intValue()-1)*4 >= this.indexFile.length )
    {
      return retVal;
    }
    try
    {
      int fromPos = 0;
      if ( wordNr.intValue() > 1 )
      {
        fromPos = Converter.bytesToInteger(this.indexFile, (wordNr.intValue()-2)*4).intValue();
      }
      // -1 here and -2 above, because we began to count with 0, though wordNrs begin with 1
      int toPos = Converter.bytesToInteger(this.indexFile, (wordNr.intValue()-1)*4) .intValue();
      if ( (toPos - fromPos) < 1 )
      {
        return retVal;
      }

      this.dataFileReader.seek(fromPos);

      Integer[] curVals = null;
      byte[] byteBuf = new byte[(toPos-fromPos)-4];
      byte[] byteBuf2 = new byte[4];
      this.dataFileReader.read(byteBuf);
      retVal[0] = new String(byteBuf);
      this.dataFileReader.read(byteBuf2);
      retVal[1] = Converter.bytesToInteger(byteBuf2);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
    return retVal;
  }

  public int getMaxWordNr()
  {
    return this.indexFile.length/4;
  }

  /**
   * Binaersuche zum auffinden von Wortnummern
   * @param word
   * @return
   */
  public Integer getNumber(String word)
  {
    int count = 1;
    while ( count < this.getMaxWordNr() )
    {
      if ( getWord( new Integer( count ) ).equals(word) )
      {
        return new Integer(count);
      }
      count++;
    }
    return new Integer(0);
  }
/*    int lower = 1;
    int upper = this.getMaxWordNr();
    int median = (upper-lower)/2 + lower;
    String curWord = null;
    while ( true )
    {
      System.out.println("lower: "+lower+" median: "+median+" upper: "+upper);
      curWord = getWord( new Integer( median ) );
      if ( curWord.compareTo( word ) == 0 )
      {
        return new Integer( median );
      }
      else if ( curWord.compareTo( word ) < 0 )
      {
        upper = median;
        median = (upper-lower)/2 + lower;
      }
      else
      {
        lower = median;
        median = (upper-lower)/2 + lower;
      }
    }*/
//  }

  /**
   * Look up in file2 what's at position n-1 and n and returns the wort_bin
   * from n-1 to n from file1 as Object[2] with Object[0]=String and Object[1]=Integer
   * @param wordNr
   * @return
   */
  public String getWord(Integer wordNr)
  {
    String retVal = "";
    if ( (wordNr.intValue()-1)*4 >= this.indexFile.length )
    {
      return retVal;
    }
    try
    {
      int fromPos = 0;
      if ( wordNr.intValue() > 1 )
      {
        fromPos = Converter.bytesToInteger(this.indexFile, (wordNr.intValue()-2)*4).intValue();
      }
      // -1 here, because we began to count with 0, though wordNrs begin with 1
      int toPos = Converter.bytesToInteger(this.indexFile, (wordNr.intValue()-1)*4) .intValue();
      if ( (toPos - fromPos) < 1 )
      {
        return retVal;
      }

      this.dataFileReader.seek(fromPos);

      Integer[] curVals = null;
      byte[] byteBuf = new byte[(toPos-fromPos)-4];
      this.dataFileReader.read(byteBuf);
      retVal = new String(byteBuf);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
    return retVal;
  }

  public List getWordsForNumbersSameOrder(List numbers)
  {
    ArrayList retVec = new ArrayList( numbers.size() );
    for ( Iterator it = numbers.iterator(); it.hasNext(); )
    {
      Integer number = (Integer)it.next();
      retVec.add(getWord(number));
    }
    return retVec;
  }

  /**
   * Tests, whether the required files for this class, given with the fileName
   * exist and are not of null-length (like after a failed start of FilePreparer
   *
   * @param fileName relative or absolute filename without index or data extensions
   * @return
   */
  public static boolean existsFileDump(String fileName)
  {
    if ( (new File(fileName+WortlisteFilePreparer.ext1)).exists() &&
         (new File(fileName+WortlisteFilePreparer.ext1)).length() > 0 &&
         (new File(fileName+WortlisteFilePreparer.ext2)).exists() &&
         (new File(fileName+WortlisteFilePreparer.ext2)).length() > 0 )
    {
      return true;
    }
    return false;
  }

  public void testFiles(String fileName)
  {
    for ( int i = 50001 ; i > 49999 ; i-- )
    {
      System.out.println(i+" = "+getWordAndAnzahl(new Integer(i))[0]+" with anzahl = "+getWordAndAnzahl(new Integer(i))[1]);
    }
  }

  public static void main(String[] args)
  {
    FileWortliste prep = new FileWortliste("data/ksim/wortliste.dump");
    prep.testFiles("data/ksim/wortliste.dump");
  }


}