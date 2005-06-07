package com.bordag.parasyn;

import java.util.*;

import com.bordag.parasyn.util.*;
import com.bordag.util.*;

/**
 * First fetches the collocations of the given word
 * Then fetches collocations of the collocations according to the
 *   settings (class Options)
 * Then creates PSData objects where each one contains the word,
 * it's collocations and the collocations of the collocations, matched by sense
 */

public class PSDataFactoryPreC
{
  protected DBUtil dbUtil = null;

  public PSDataFactoryPreC(DBUtil dbUtil)
  {
    this.dbUtil = dbUtil;
  }

  /**
   * A ParaSynMap object contains colls of a given word, along with both
   * retrieved and normalized numbers
   *
   * 1. create ParaSyn object
   * 2. get colls of word put to object
   * 3. getXVals for each word
   * 4. normalize X put them to object
   * 5. getYVals for each word
   * 6. normalize Y put them to object
   * 7. done
   *
   * This starts the whole process and returns (hopefully) the Dataobject.
   *
   * @param word The wordNr for which the whole thing is to be calculated
   * @return ParaSynMap
   */
  public ParaSynMap getPSData(PSWord word)
  {
    ParaSynMap retMap = null;
    Output.println("Beginning to get PSData");

    // 1. create ParaSyn object
    // 2. get colls of word put to object
    // 3. getXVals for each word
    List collsAndSigs = this.dbUtil.getCollocations(word.wordNr, "0");
    HashSet collsPSData = new HashSet();
    Vector collsOnly = new Vector();
    Vector sigValues = new Vector();

    for ( Iterator it = collsAndSigs.iterator() ; it.hasNext() ; )
    {
      Integer[] curPair = (Integer[])it.next();
      collsOnly.add(curPair[0]);
      sigValues.add(curPair[1]);
    }
    // 4. getYVals for each word
    //KollokSimShortAxis simAxis = new KollokSimShortAxis(this.dbUtil);
    ValueAxis simAxis = new KollokHigherOrderAxis(this.dbUtil);
    List simValues = simAxis.getValues(word.wordNr, collsOnly);
    int i = 0;
    System.out.println("sigValues : "+sigValues);
    System.out.println("simValues : "+simValues);
    for ( Iterator it = collsAndSigs.iterator() ; it.hasNext() ; i++ )
    {
      Integer[] curWord = (Integer[])it.next();
      //PSWord curPSWord = new PSWord(curWord[0], curWord[1].doubleValue(), 42);
      PSWord curPSWord = new PSWord(this.dbUtil.getWordForNumber(new CHString(curWord[0].toString())),
                                    curWord[0], ((Integer)sigValues.get(i)).doubleValue(), 42);
      curPSWord.xValue = ((Double)simValues.get(i)).doubleValue();
      collsPSData.add(curPSWord);
      collsOnly.add(curWord[0]);
    }
    retMap = new ParaSynMap(collsPSData);
    retMap.setWord(word);
    // 5. normalize X put them to object
    retMap = normalizeValues(retMap);
    // 6. normalize Y put them to object
    // 7. done
    return retMap;
  }

  protected ParaSynMap normalizeValues(ParaSynMap map)
  {
//    System.out.println("Callin' normalize on "+map.collocations);
    // get maxSig
    double maxSig = Double.MIN_VALUE;
    double minSig = Double.MAX_VALUE;
    double maxSim = Double.MIN_VALUE;
    double minSim = Double.MAX_VALUE;

    for ( Iterator it = map.collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curWord = (PSWord)it.next();
      ////System.out.print("New: word: ["+curWord+"] gets normalized to ");
      //curWord.significance = Math.log( curWord.significance );
      System.out.println("["+curWord+"]");
      if ( curWord.significance > maxSig )
      {
        maxSig = curWord.significance;
      }
      if ( curWord.significance < minSig )
      {
        minSig = curWord.significance;
      }

      if ( curWord.xValue > maxSim )
      {
        maxSim = curWord.xValue;
      }
      if ( curWord.xValue < minSim )
      {
        minSim = curWord.xValue;
      }

    }
    maxSig = maxSig - minSig;
    maxSim = maxSim - minSim;

    maxSig = Math.log(maxSig);
    maxSim = Math.log(maxSim);
//    System.out.println("--ml--> maxSig = "+maxSig+" minSig = "+minSig);
//    System.out.println("--ml--> maxSim = "+maxSim+" minSim = "+minSim);
    // then divide each one by maxSig, after taking log (of max as well)
    for ( Iterator it = map.collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curWord = (PSWord)it.next();
      ////System.out.print("New2: word: ["+curWord+"] gets normalized to ");
      //curWord.significance = Math.log(curWord.significance) / maxSig;
      curWord.significance = curWord.significance - minSig;
      curWord.xValue = curWord.xValue - minSim;
      if ( curWord.significance > 0.0 ) { curWord.significance = Math.log (curWord.significance); }
      if ( curWord.xValue > 0.0 ) { curWord.xValue = Math.log (curWord.xValue); }
      curWord.significance = curWord.significance / maxSig;
      curWord.xValue = curWord.xValue / maxSim;
      ////System.out.println("["+curWord+"]");
    }

//    System.out.println("Calculated' normalize on "+map.collocations);
    return map;
  }

//------------- protected methods

//------------- data fetch methods


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
      System.out.print("\t\tdone\nCreating factory:");
      PSDataFactoryPreC factory = new PSDataFactoryPreC(util);
      CHString wordString = new CHString("Elefant");
      PSWord word = new PSWord(wordString, util.getNumberForWord(wordString), 0.0, 0);
      System.out.print("\t\tdone\nCreating PSData element for word "+word+":");
      ParaSynMap map = factory.getPSData(word);
      Output.println("\t"+map);
      Output.println("LIN: "+map.getLinguisticCollocationsRadian().toStringOrderedBy(0));
      Output.println("COH: "+map.getCohyponymsRadian().toStringOrderedBy(0));
      Output.println("HYP: "+map.getHyperonymsRadian().toStringOrderedBy(0));
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }
  }

}
