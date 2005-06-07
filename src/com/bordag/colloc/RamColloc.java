package com.bordag.colloc;

import java.util.*;
import java.io.*;

/**
 * Tihs class gives access to collocations which is faster but much more
 * memory-intense. It loads up the data file and the index file and operates
 * on the created byte arrays per random accesses.
 *
 * @author Stefan Bordag
 * @date   20.11.2003
 */
public class RamColloc implements Collocations
{
  protected byte[] dataFile = null;
  protected byte[] indexFile = null;

  protected String fileName = null;

  public RamColloc(String fileName)
  {
    this.fileName = fileName;
    if ( existsFileDump(fileName) )
    {
      this.dataFile = new byte[(int)(new File(fileName+CollocFilePreparer.ext1)).length()];
      this.indexFile = new byte[(int)(new File(fileName+CollocFilePreparer.ext2)).length()];
    }
    else
    {
      return;
    }

    try
    {
      RandomAccessFile reader1 = null;
      RandomAccessFile reader2 = null;
      if ( fileName != null )
      {
        reader1 = new RandomAccessFile(  fileName + CollocFilePreparer.ext1, "r" );
        reader2 = new RandomAccessFile(  fileName + CollocFilePreparer.ext2, "r" );
      }

      reader1.read(this.dataFile);
      reader1.close();
      reader2.read(this.indexFile);
      reader2.close();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
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
    if ( (new File(fileName+CollocFilePreparer.ext1)).exists() &&
         (new File(fileName+CollocFilePreparer.ext1)).length() > 0 &&
         (new File(fileName+CollocFilePreparer.ext2)).exists() &&
         (new File(fileName+CollocFilePreparer.ext2)).length() > 0 )
    {
      return true;
    }
    return false;
  }

  public List getCollocs(Integer wordNr)
  {
    return getCollocs(wordNr, 0, Integer.MAX_VALUE, 0);
  }

  /**
   * Look up in file2 what's at position n-1 and n and returns the numbers
   * from n-1 to n from file1 as Integer[2]
   * @param wordNr
   * @return
   */
  public List getCollocs(Integer wordNr, int myMinSignificance, int myMaxCollocs, int myMinWordNr)
  {
    ArrayList retVec = new ArrayList();
    if ( (wordNr.intValue()-1)*4 >= this.indexFile.length )
    {
      return new ArrayList();
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

      retVec = new ArrayList( Math.min(myMaxCollocs, ((toPos-fromPos)/4) ) );

      Integer curVal = null;
      Integer curSig = null;
      int realCount = 0;
      for ( int i = fromPos ; i < toPos && realCount < myMaxCollocs ; i++)
      {
        curVal = Converter.bytesToInteger(this.dataFile,i*8);
        curSig = Converter.bytesToInteger(this.dataFile,(i*8)+4);
        if ( curVal.intValue() > myMinWordNr && curSig.intValue() >= myMinSignificance )
        {
          retVec.add( curVal );
          realCount++;
        }
      }
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
    return retVec;
  }


  public List getCollocsAndSigs(Integer wordNr)
  {
    return getCollocsAndSigs(wordNr, 0, Integer.MAX_VALUE, 0);
  }
  /**
   * Look up in file2 what's at position n-1 and n and returns the numbers
   * from n-1 to n from file1 as Integer[2]
   * @param wordNr
   * @return
   */
  public List getCollocsAndSigs(Integer wordNr, int myMinSignificance, int myMaxCollocs, int myMinWordNr)
  {
    ArrayList retVec = null;
    if ( (wordNr.intValue()-1)*4 >= this.indexFile.length )
    {
      return new ArrayList();
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

      retVec = new ArrayList( Math.min(myMaxCollocs, ((toPos-fromPos)/4) ) );

      Integer[] curVals = null;
      int realCount = 0;
      for ( int i = fromPos ; i < toPos && realCount < myMaxCollocs ; i++)
      {
        curVals = new Integer[2];
        curVals[0] = Converter.bytesToInteger(this.dataFile,i*8);
        curVals[1] = Converter.bytesToInteger(this.dataFile,(i*8)+4);
        if ( curVals[0].intValue() > myMinWordNr && curVals[1].intValue() >= myMinSignificance )
        {
          retVec.add(curVals);
          realCount++;
        }
      }
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
    return retVec;
  }

  public void testFiles(String fileName)
  {
    for ( int i = 1 ; i < 5000000 ; i++ )
    {
//      System.out.println(i+"\t"+getCollocsAndSigs(new Integer(i)).size()+" collocations");
      for ( Iterator it = getCollocsAndSigs(new Integer(i)).iterator() ; it.hasNext() ; )
      {
        Integer[] curVals = (Integer[])it.next();
        System.out.println(i+"\t"+curVals[0]+"\t"+curVals[1]);
      }
    }
  }

  public static void main(String[] args)
  {
    RamColloc prep = new RamColloc("data/ksim/kollok_sig.dump");
    prep.testFiles("data/ksim/kollok_sig.dump");
  }

}
