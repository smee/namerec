package com.bordag.parasyn;

import java.util.*;
import java.io.*;

import com.bordag.parasyn.util.*;
import com.bordag.util.*;

/**
 *
 */

public class PSPipePreC
{
  protected DBUtil dbUtil = null;
  protected ParaSynMap data = null;


  public PSPipePreC(ParaSynMap data, DBUtil dbUtil, PSDataFactoryPreC factory)
  {
    this.data = data;
    this.dbUtil = dbUtil;
    // retrieve maps for each word of this map
    HashMap map = null;
    map = importFile();
    if ( map == null )
    {
      map = new HashMap();
      for ( Iterator it = this.data.collocations.iterator(); it.hasNext(); )
      {
        PSWord curWord = ( PSWord )it.next();
        ParaSynMap curPS = factory.getPSData( curWord ) ;
        PSWordHashMap[] twoVectors = new PSWordHashMap[2];
        twoVectors[0] = curPS.getCohyponymsRadian(); // 0=cohyponyms
        twoVectors[1] = curPS.getHyperonymsRadian(); // 1=hyperonyms
        map.put( curWord, twoVectors );
      }
      exportFile(map);
    }
    // run Algorithm on the map of PSWords -> two Vectors (cohyps and hypos)

    runAlgorithm(map);
  }

  protected HashMap importFile()
  {
    File f = new File(this.data.word.word.toString());
    HashMap retMap = null;
    try
    {
      FileInputStream fi = new FileInputStream(f);
      ObjectInputStream si = new ObjectInputStream(fi);
      retMap = (HashMap)si.readObject();
      si.close();
    }
    catch (Exception e)
    {
      System.err.println("import failed, file probably not written or outdated");
      return null;
    }
    System.out.println("import of file "+f+" successful");
    return retMap;
  }

  protected void exportFile(HashMap map)
  {
    File f = new File(this.data.word.word.toString());
    try
    {
      FileOutputStream fo = new FileOutputStream(f);
      ObjectOutputStream wc = new ObjectOutputStream(fo);
      wc.writeObject(map);
      wc.flush();
      wc.close();
      System.out.println("export successful to file "+f);
    }
    catch (Exception e)
    {
      System.err.println("export to file "+f+" failed");
      e.printStackTrace();
      return;
    }
  }

  /**
   *
   * @param map HashMap ( PSWord --> PSWordHashMap[] with 0=cohyps and 1=hypos )
   */
  protected void runAlgorithm2(HashMap map)
  {
        // for each word in my data
        PSWord inputWord = this.data.word;

        PSWordHashMap inputCohyps = this.data.getCohyponymsRadian();
        PSWordHashMap inputHyperons = this.data.getHyperonymsRadian();

        PSWordHashMap results = new PSWordHashMap(2);
        System.out.println("For "+inputWord+" going through it's cohyponyms: ");

        for ( Iterator it = inputCohyps.keySet().iterator() ; it.hasNext() ; )
        {
          PSWord inputCohyp = (PSWord)it.next();

          // this is coh strength viewed directly from input word
          double inputCohypStrength = inputCohyps.get(inputCohyp)[0];

          double schnitt = 0.0;
          double count = 0.0;

          System.out.println("> "+inputCohyp+" : "+inputCohypStrength);
          for ( Iterator it2 = inputHyperons.keySet().iterator() ; it2.hasNext() ; count += 1.0 )
          {
            PSWord curHyperon = (PSWord)it2.next();

            if ( curHyperon.equals(inputCohyp) ) { }
            else if ( curHyperon.equals(inputWord) )
            {
              // nimm hypWert direkt
//              double hypWert = inputHyperons.get(curHyperon)[0];
  //            schnitt += hypWert;
            }
            else
            {
              PSWordHashMap[] map2 = (PSWordHashMap[])map.get(inputCohyp);
              PSWordHashMap map3 = map2[1]; // take hyperonVals
              double[] xx = map3.get(curHyperon);
              if ( xx != null )
              {
                double x = xx[0];
                if ( x == Double.NaN || new String(""+x).equals(new String("NaN"))
                  || x == Double.NEGATIVE_INFINITY || x == Double.POSITIVE_INFINITY )
                {  }
                else
                {
                  System.out.println(" >> "+curHyperon+" : "+x);
                  schnitt += inputCohypStrength * x ;
                }
              }
            }
          }
          double [] result = new double[2];
          result[0] = schnitt;
          schnitt = schnitt / count;
          result[1] = schnitt;
          results.put(inputCohyp, result);
        }
        System.out.println("Hyp values: "+results.toStringOrderedBy(1));
  }


  /**
   *
   * @param map HashMap ( PSWord --> PSWordHashMap[] with 0=cohyps and 1=hypos )
   */
  protected void runAlgorithm(HashMap map)
  {
//    System.out.println("LC --> "+this.data.getLinguisticCollocationsRadian().toStringOrderedBy(0));
//    System.out.println("CO --> "+this.data.getCohyponymsRadian().toStringOrderedBy(0));
//    System.out.println("HY --> "+this.data.getHyperonymsRadian().toStringOrderedBy(0));

    // construct a vector space from all present words

      // taking cohyponym with, say 0.3
      // if his hyperonym has 0.5, multiply it with 1-0.3 that is (0.7)
      // that is multiply all hyperonyms with 0.7
      // from this word all hyperonyms then represent a vector/set
      // do this for all words

        // for each word in my data
        PSWord inputWord = this.data.word;

        PSWordHashMap curCohyps = this.data.getCohyponymsRadian();
        PSWordHashMap curHyperons = this.data.getHyperonymsRadian();

        PSWordHashMap results = new PSWordHashMap(2);

        for ( Iterator it = curCohyps.keySet().iterator() ; it.hasNext() ; )
        {
          PSWord curWord = (PSWord)it.next();
          //double curCohypStrength = curCohyps.get(curWord)[0]; // this is coh strength viewed directly from input word
          double schnitt = 0.0;
          double count = 0.0;
          System.out.print("Fuer das Wort Elefant hat das moegliche Kohyponym "+curWord.word+" ");
          for ( Iterator it2 = curCohyps.keySet().iterator() ; it2.hasNext() ; )
          {
            count += 1.0;
            PSWord curSecWord = (PSWord)it2.next();
            if ( curSecWord.equals(curWord) ) { }
            else if ( curSecWord.equals(inputWord) )
            {
              // nimm hypWert direkt
              double hypWert = curHyperons.get(curWord)[0];
              ////schnitt += (2.0-hypWert);
              schnitt += hypWert;
            }
            else
            {
              //        -------------------------------------curSecWord stand hier
              double curSecWordCohypStrength = curCohyps.get(curSecWord)[0]; // this is coh strength viewed directly from input word
              // in der map vom x, den Wert von hyp z nachschlagen
              PSWordHashMap[] map2 = (PSWordHashMap[])map.get(curSecWord);
              PSWordHashMap map3 = map2[1];
              double[] xx = map3.get(curWord);
              if ( xx != null )
              {
                double x = xx[0];
                //System.out.println("Checking whether "+x+" is NaN!");
                if ( x == Double.NaN || new String(""+x).equals(new String("NaN"))
                  || x == Double.NEGATIVE_INFINITY || x == Double.POSITIVE_INFINITY )
                {
                }
                else
                {
                  double curWordHypInSecMap = x; //((double[])((PSWordHashMap[])map.get(curSecWord))[1].get(curWord))[0];
    //                  ((PSWordHashMap[])map.get(localWord))[0];
                ////schnitt += ( 2.0 - curSecWordCohypStrength ) * ( 2.0 - curWordHypInSecMap );
                schnitt += curSecWordCohypStrength * curWordHypInSecMap ;
                }
              }
            }
          }
          double [] result = new double[2];
          result[0] = schnitt;
          schnitt = schnitt / count;
          result[1] = schnitt;
          results.put(curWord, result);
          //System.out.println("Hyp value for: "+curWord+"   <---> "+schnitt);
        }
        System.out.println("Hyp values: "+results.toStringOrderedBy(1));
        //System.out.println("Hyp values unnormalized: "+results.toStringOrderedBy(0));
      //this.data

      // calculate mean for each hyperonym, over all cohyponyms, the one with
      // the highest wins ( use PSWordHashMap again for easy ordering print )

    // retrieve vectors from maps, delete maps

  }

  /**
   *
   * @param args
   */
  public static void main(String[] args)
  {
    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    DBConnection connection = null;
    try
    {
      System.out.print("Creating connection:");
      connection = new DBConnection(url, user, passwd);
      System.out.print("\t\t\tdone\nCreating util:");
      DBUtil util = new DBUtil(connection);
      CHString wordString = new CHString( "Elefant" );
      if ( args.length > 0 )
      {
        wordString = new CHString( args[0] );
      }
      util.cacheHint(wordString);
      System.out.print("\t\tdone\nCreating factory:");
      PSDataFactoryPreC factory = new PSDataFactoryPreC(util);
      PSWord word = new PSWord(wordString, util.getNumberForWord(wordString), 0.0, 0);
      System.out.print("\t\tdone\nCreating PSData element for word "+word+":");

      PSPipePreC pipe = new PSPipePreC( factory.getPSData(word) , util, factory );
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }
  }
}
