package com.bordag.colloc;

import java.util.*;
import java.io.*;

/**
 * This class prepares the two tempFiles which are needed to access word_bins
 * faster.
 * It takes the wordlist file as input, which first must be dumped
 * from the database via the following command:
 *
 * select w.wort_nr, w.wort_bin, w.anzahl into outfile '/var/roedel/ksim/wortliste.dump' from wortliste w order by w.wort_nr asc;
 *
 * The file must be in the working directory of this program under data/ksim/ or
 * as specified.
 *
 * Assumes that wordnumers in the first column are mostly wothout holes.
 * It will fill up useless 4 bytes per missing wordnumber up to the next existing
 * wordnumber in the indexfile
 *
 * Format of first file: char[?] of words, char[4] of numbers (anzahl)
 *
 * Format of second file: char[4]
 * Semantics: nth 4-byte-number gives location of end of collocationsnumbers of
 * the wordnumber n. Begin is stored at n-1
 *
 * ASSUMPTIONS:
 * column1: wordNrs don't have too large 'holes'
 *
 * @author Stefan Bordag
 * @date   24.11.2003
 */

public class WortlisteFilePreparer
{
  public static final String ext1 = ".w1";
  public static final String ext2 = ".w2";

  public WortlisteFilePreparer(String fileName)
  {
    createFiles(fileName);
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
    String line = reader.readLine();
    int count = 0;
    Integer oldWordNr = new Integer( line.substring(0, line.indexOf("\t")).trim() );
    while ( line != null )
    {
      Integer curWordNr = new Integer( line.substring(0, line.indexOf("\t")).trim() );
      String wort_bin = line.substring(line.indexOf("\t"), line.lastIndexOf("\t")).trim();
      Integer anzahl = new Integer ( line.substring(line.lastIndexOf("\t"), line.length()).trim() );

      byte[] wort_bin_bytes = wort_bin.getBytes();
      writer1.write(wort_bin_bytes);
      writer1.write(Converter.IntegerToBytes(anzahl));

      int diff = curWordNr.intValue() - oldWordNr.intValue();
      if ( diff > 1 )
      {
        for ( int i = diff ; i > 1 ; i-- )
        {
          writer2.write(Converter.IntegerToBytes(new Integer(count)));
        }
      }
      count+=wort_bin_bytes.length+4;
      writer2.write(Converter.IntegerToBytes(new Integer(count)));
      oldWordNr = curWordNr;
      line = reader.readLine();
    }
  }


  public static void main(String[] args)
  {
    WortlisteFilePreparer prep = new WortlisteFilePreparer("data/frieder/wortliste_bolted.dump");
  }


}