package com.bordag.colloc;

import java.util.*;
import java.io.*;

/**
 * Tihs class gives access to collocations which is slower but less
 * memory-intense. It loads up only the index file and operates on the file
 * per random accesses.
 *
 * @author Stefan Bordag
 * @date   20.11.2003
 */
public class FileColloc implements Collocations
{
  protected byte[] indexFile = null;
  protected RandomAccessFile dataFileReader = null;

  protected String fileName = null;

  public FileColloc(String fileName)
  {
    this.fileName = fileName;
    if ( existsFileDump(fileName) )
    {
      this.indexFile = new byte[(int)(new File(fileName+CollocFilePreparer.ext2)).length()];
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
        reader1 = new RandomAccessFile(  fileName + CollocFilePreparer.ext2, "r" );
        this.dataFileReader = new RandomAccessFile(  fileName + CollocFilePreparer.ext1, "r" );
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

  public List getCollocs(Integer wordNr, int myMinSignificance, int myMaxCollocs, int myMinWordNr)
  {
    List retVec = new ArrayList();
    if ( (wordNr.intValue()-1)*4 >= this.indexFile.length )
    {
      return retVec;
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

      this.dataFileReader.seek(fromPos*8);

      // create the vector of the needed size, which is either the maxColls or
      // maximum of collocations possible, whichever is smaller
      retVec = new ArrayList( Math.min(myMaxCollocs, toPos-fromPos) );

      Integer curVal = null;
      byte[] byteBuf = new byte[4];
      byte[] byteBuf2 = new byte[4];
      int realCount = 0;
      for ( int i = fromPos ; i < toPos && realCount < myMaxCollocs ; i++)
      {
        this.dataFileReader.read(byteBuf);
        curVal = Converter.bytesToInteger(byteBuf);
        this.dataFileReader.read(byteBuf2);
        Integer curSig = Converter.bytesToInteger(byteBuf2);
        if ( curVal.intValue() > myMinWordNr && curSig.intValue() >= myMinSignificance )
        {
          retVec.add(curVal);
          realCount++;
        }

      }
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
    if ( retVec == null )
    {
      return new ArrayList();
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
    ArrayList retVec = new ArrayList();
    if ( (wordNr.intValue()-1)*4 >= this.indexFile.length )
    {
      return retVec;
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

      this.dataFileReader.seek(fromPos*8);

      // create the vector of the needed size, which is either the maxColls or
      // maximum of collocations possible, whichever is smaller
      retVec = new ArrayList( Math.min(myMaxCollocs, toPos-fromPos) );

      Integer[] curVals = null;
      byte[] byteBuf = new byte[4];
      byte[] byteBuf2 = new byte[4];
      int realCount = 0;
      for ( int i = fromPos ; i < toPos && realCount < myMaxCollocs ; i++)
      {
        curVals = new Integer[2];
        this.dataFileReader.read(byteBuf);
        curVals[0] = Converter.bytesToInteger(byteBuf);
        this.dataFileReader.read(byteBuf2);
        curVals[1] = Converter.bytesToInteger(byteBuf2);
        if ( curVals[0].intValue() > myMinWordNr && curVals[1].intValue() >= myMinSignificance ) // if significance really higher then threshold
        {
          retVec.add( curVals ); // then add element
          realCount++;           // and increase counter of added elements
        }
      }
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
    if ( retVec == null ) { return new ArrayList();}
    return retVec;
  }

  public void testFiles(String fileName)
  {
    for ( int i = 394 ; i > 320 ; i-- )
    {
      System.out.println(i+" has "+this.getCollocsAndSigs(new Integer(i),20,20,20).size()+" collocations");
/*      for ( Iterator it = getCollocsOf(new Integer(i)).iterator() ; it.hasNext() ; )
      {
        Integer[] curVals = (Integer[])it.next();
        System.out.println(i+"\t"+curVals[0]+"\t"+curVals[1]);
      }*/
    }
  }

  public static void main(String[] args)
  {
    FileColloc prep = new FileColloc("data/ksim/kollok_sig.dump");
    prep.testFiles("data/ksim/kollok_sig.dump");
  }

}
