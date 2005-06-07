package com.bordag.colloc;

import java.util.*;
import java.io.*;

/**
 * Tihs class gives access to collocations which is slower but less
 * memory-intense. It loads up only the index file and operates on the file
 * per random accesses.
 *
 * @author Stefan Bordag
 * @date   1.12.2003
 */
public class FileGrfNAVS
{
  protected byte[] indexFile = null;
  protected RandomAccessFile dataFileReader = null;

  protected String fileName = null;

  public FileGrfNAVS(String fileName)
  {
    this.fileName = fileName;
    if ( existsFileDump(fileName) )
    {
      this.indexFile = new byte[(int)(new File(fileName+GrfNAVSFilePreparer.ext2)).length()];
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
        reader1 = new RandomAccessFile(  fileName + GrfNAVSFilePreparer.ext2, "r" );
        this.dataFileReader = new RandomAccessFile(  fileName + GrfNAVSFilePreparer.ext1, "r" );
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
    if ( (new File(fileName+GrfNAVSFilePreparer.ext1)).exists() &&
         (new File(fileName+GrfNAVSFilePreparer.ext1)).length() > 0 &&
         (new File(fileName+GrfNAVSFilePreparer.ext2)).exists() &&
         (new File(fileName+GrfNAVSFilePreparer.ext2)).length() > 0 )
    {
      return true;
    }
    return false;
  }

  public List getWortarten(Integer wordNr)
  {
    List retVec = null;
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

      // 5 means 1 + 4 as wortart is stored as a one-char-string and then it's only grp_nr with 4 bytes
      this.dataFileReader.seek(fromPos*5);

      // create the vector of the needed size, which is either the maxColls or
      // maximum of collocations possible, whichever is smaller
      retVec = new ArrayList( toPos-fromPos );

      String curVal = null;
      byte[] byteBuf = new byte[1];
      byte[] byteBuf2 = new byte[4];
      for ( int i = fromPos ; i < toPos ; i++)
      {
        this.dataFileReader.read(byteBuf);
        curVal = new String(byteBuf);
        this.dataFileReader.read(byteBuf2);
        // do something with byteBuf2 ?
        retVec.add(curVal);
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

/*  public List getCollocsAndSigs(Integer wordNr)
  {
    return getCollocsAndSigs(wordNr, 0, Integer.MAX_VALUE, 0);
  }*/

  /**
   * Look up in file2 what's at position n-1 and n and returns the numbers
   * from n-1 to n from file1 as Integer[2]
   * @param wordNr
   * @return
   */
/*  public List getCollocsAndSigs(Integer wordNr, int myMinSignificance, int myMaxCollocs, int myMinWordNr)
  {
    ArrayList retVec = null;
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
  }*/

  public void testFiles(String fileName)
  {
    for ( int i = 394 ; i > 320 ; i-- )
    {
      System.out.println(i+" has "+this.getWortarten(new Integer(i)).size()+" grammars?! : "+this.getWortarten(new Integer(i)));
    }
  }

  public static void main(String[] args)
  {
    FileGrfNAVS prep = new FileGrfNAVS("data/ksim/grfNAVS.dump");
    prep.testFiles("data/ksim/grfNAVS.dump");
  }

}
