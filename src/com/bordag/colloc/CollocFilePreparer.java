package com.bordag.colloc;

import java.util.*;
import java.io.*;

/**
 * This class prepares the two tempFiles which are needed to calculate
 * similarities faster.
 * It takes the collocationstable file as input, which first must be dumped
 * from the database via the following command:
 *
 * select k.wort_nr1, k.wort_nr2, k.signifikanz into outfile '/var/roedel/ksim/kollok_sig.dump' from kollok_sig k order by k.wort_nr1 asc;
 *
 * The file must be in the working directory of this program.
 *
 * Assumes that wordnumers in the first column are mostly wothout holes.
 * It will fill up useless 8 bytes per missing wordnumber up to the next existing
 * wordnumber
 *
 * Format of first file: char[4] of numbers (collocations), char[4] of numbers (sigs)
 *
 * Format of second file: char[4]
 * Semantics: nth 4-byte-number gives location of end of collocationsnumbers of
 * the wordnumber n. Begin is stored at n-1
 *
 * ASSUMPTIONS:
 * column1: wordNrs don't have too large 'holes'
 *
 * @author Stefan Bordag
 */
public class CollocFilePreparer
{
  public static final String ext1 = ".k1";
  public static final String ext2 = ".k2";

  protected byte[] file1 = null;
  protected byte[] file2 = null;

  public CollocFilePreparer(String fileName)
  {
    createFiles(fileName);
//    readFiles(fileName);
//    testFiles(fileName);
  }

  protected void readFiles(String fileName)
  {
    if ( (new File(fileName+ext1)).exists() && (new File(fileName+ext1)).length() > 0 &&
         (new File(fileName+ext2)).exists() && (new File(fileName+ext2)).length() > 0 )
  //       (new File(fileName+ext3)).exists() && (new File(fileName+ext3)).length() > 0 )
    {
      System.out.println("length1 = "+(int)(new File(fileName+ext1)).length());
      System.out.println("length2 = "+(int)(new File(fileName+ext2)).length());

      this.file1 = new byte[(int)(new File(fileName+ext1)).length()];
      this.file2 = new byte[(int)(new File(fileName+ext2)).length()];
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
        reader1 = new RandomAccessFile( fileName + ext1 , "r" );
        reader2 = new RandomAccessFile( fileName + ext2 , "r" );
      }

      reader1.read(this.file1);
      reader2.read(this.file2);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  protected void createFiles(String fileName)
  {
    if ( (new File(fileName+ext1)).exists() && (new File(fileName+ext1)).length() > 0 &&
         (new File(fileName+ext2)).exists() && (new File(fileName+ext2)).length() > 0 )
    {
      return;
    }
    try
    {
      RandomAccessFile reader = null;
      if ( fileName != null)
      {
        reader = new RandomAccessFile(fileName, "r");
      }
      RandomAccessFile writer1 = null;
      writer1 = new RandomAccessFile(fileName+ext1,"rw");
      RandomAccessFile writer2 = null;
      writer2 = new RandomAccessFile(fileName+ext2,"rw");

      writeFiles(reader, writer1, writer2);

      reader.close();
      writer1.close();
      writer2.close();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  protected void writeFiles(RandomAccessFile reader, RandomAccessFile writer1, RandomAccessFile writer2) throws Exception
  {
    //String line = reader.readLine();
    String line = "1\t0\t0";  // FIXME: This is a workaround for the problematic case if the first wordnumber is not 1 but something above

    int count = 0;
    Integer curCollBlock = new Integer( line.substring(0, line.indexOf("\t")).trim() );
    while ( line != null )
    {
      Integer colloc1 = new Integer( line.substring(0, line.indexOf("\t")).trim() );
      Integer colloc2 = new Integer( line.substring(line.indexOf("\t"), line.lastIndexOf("\t")).trim() );
      Integer significance = new Integer ( line.substring(line.lastIndexOf("\t"), line.length()).trim() );

      writer1.write(Converter.IntegerToBytes(colloc2));

      writer1.write(Converter.IntegerToBytes(significance));
      if ( !curCollBlock.equals(colloc1) )
      {
        int diff = colloc1.intValue() - curCollBlock.intValue();
        if ( diff > 1 )
        {
          //System.out.println("WordNR hole with size "+diff+" detected after: "+curCollBlock);
          while ( diff > 1 )
          {
            writer2.write(Converter.IntegerToBytes(new Integer(count)));
            diff--;
          }
        }
        writer2.write(Converter.IntegerToBytes(new Integer(count)));
        curCollBlock = colloc1;
      }
      line = reader.readLine();
      count++;
    }
  }

  /**
   * Look up in file2 what's at position n-1 and n and returns the numbers
   * from n-1 to n from file1 as Integer[2]
   * @param wordNr
   * @return
   */
  public List getCollocsOf(Integer wordNr)
  {
    List retVec = new ArrayList();
    int fromPos = 0;
    if ( wordNr.intValue() > 1 )
    {
      fromPos = Converter.bytesToInteger(this.file2, (wordNr.intValue()-2)*4).intValue();
    }
    // -1 here, because we began to count with 0, though wordNrs begin with 1
    int toPos = Converter.bytesToInteger(this.file2, (wordNr.intValue()-1)*4) .intValue();

    Integer[] curVals = null;
    for ( int i = fromPos ; i < toPos ; i+=2)
    {
      curVals = new Integer[2];
      curVals[0] = Converter.bytesToInteger(this.file1, i*4);
      curVals[1] = Converter.bytesToInteger(this.file1, (i+1)*4);
      retVec.add(curVals);
    }
    return retVec;
  }


  public void testFiles(String fileName)
  {
    for ( int i = 1 ; i < 100 ; i++ )
    {
      for ( Iterator it = getCollocsOf(new Integer(i)).iterator() ; it.hasNext() ; )
      {
        Integer[] curVals = (Integer[])it.next();
        System.out.println(i+"\t"+curVals[0]+"\t"+curVals[1]);
      }
    }
  }

  public static void main(String[] args)
  {
    //CollocFilePreparer prep = new CollocFilePreparer("data/ksim/kollok_sig.dump");
    CollocFilePreparer prep = new CollocFilePreparer("data/frieder/kollok_sig_bolted.dump");
  }

}
