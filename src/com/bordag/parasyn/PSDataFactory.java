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

public class PSDataFactory
{
  protected DBUtil dbUtil = null;

  public PSDataFactory(DBUtil dbUtil)
  {
    this.dbUtil = dbUtil;
  }

  /**
   * This starts the whole process and returns (hopefully) the Dataobject.
   * @param word The word for which the whole thing is to be calculated
   * @return Vector(ParaSynMap)
   */
  public Vector getPSData(PSWord word)
  {
//--    Map disambs2 = getDisambMap(this.dbUtil.getDisambiguation(word.wordNr));
    Map disambs2 = getDisambMap(this.dbUtil.getDisambiguationFallback(word.wordNr, Options.getInstance().getParaMinSignifikanz()));
// TODO: Remove word from everywhere from here on!!!!!

/*--    if ( disambs2.size() > 1 )
    {
      disambs2.remove( new Integer( 0 ) ); // throw away those meaningless words
    }
    else
    {
      System.err.println("Word ["+word+"]: is not disambiguated!");
      disambs2 = getDisambMap(this.dbUtil.getDisambiguationFallback(word.wordNr));
    }*/

    Vector psDataInstances = getInitialPsDataInstances(word, disambs2);
    Vector finalInstances = new Vector();
    for ( Iterator it = psDataInstances.iterator() ; it.hasNext() ; )
    {
      PSData curData = (PSData)it.next();
      //System.out.println("New: I've got "+curData.getCollocationsList().size()+" collocations");
      curData = addWordStrings(curData);
      curData = addCollocationsOfCollocations(curData);
      curData = addSignificances(curData);
      curData.normalize();
      curData.prune();
      finalInstances.add(curData.getParaSynMap());
    }
//      psDataInstances = addWordStrings(psDataInstances);
      //System.out.println("Disambs4: "+psDataInstances);

//    psDataInstances = addCollocationsOfCollocations(psDataInstances);
//    System.out.println("Disambs5: "+psDataInstances);
    return finalInstances;
  }

//------------- protected methods

//------------- data fetch methods

  /**
   * Fetches the significances from the database and adds them to the given
   * PSData instance
   * @param curData
   * @return
   */
  protected PSData addSignificances(PSData curData)
  {
    List collocations = curData.getCollocationsList();
    Integer[] colls = new Integer[collocations.size()];
    for ( int i = 0 ; i < colls.length ; i++ )
    {
      colls[i] = ((PSWord)collocations.get(i)).wordNr;
    }
    Arrays.sort(colls, new CHString(""));

    Integer[] significances = this.dbUtil.getSignificancesForNumbers(curData.word.wordNr, colls);
    Map sigMap = new HashMap();
    for ( int i = 0 ; i < colls.length ; i++ )
    {
      sigMap.put(colls[i], significances[i]);
    }
    // zum Schluss brauche ich eine Map, in der zu einer WortNr eine SignifikanzNr steht
    // dann gehe ich durch die PSWordListe und mache diesen:
    // get the data from a given PSWord and reput it with a modified version of PSWord
    for ( Iterator it = collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curWord = (PSWord)it.next();
      curWord.significance = ((Integer)sigMap.get(curWord.wordNr)).doubleValue();
      // put curData back where it was or is that already enough?
    }
//    Output.println("SigOutput: "+curData);
    return curData;
  }

  /**
   * For each PSWord instance found here adds the wordstring
   * @param psDataInstances
   * @return
   */
  protected PSData addWordStrings(PSData curData)
  {
    List collocations = curData.getCollocationsList();
    Integer[] colls = new Integer[collocations.size()];
    for ( int i = 0 ; i < colls.length ; i++ )
    {
      colls[i] = ((PSWord)collocations.get(i)).wordNr;
    }
    Arrays.sort(colls, new CHString(""));

    CHString[] strings = this.dbUtil.getWordsForNumbersSameOrder(colls);
    Map sigMap = new HashMap();
    for ( int i = 0 ; i < colls.length ; i++ )
    {
      sigMap.put(colls[i], strings[i]);
    }
    // zum Schluss brauche ich eine Map, in der zu einer WortNr eine SignifikanzNr steht
    // dann gehe ich durch die PSWordListe und mache diesen:
    // get the data from a given PSWord and reput it with a modified version of PSWord
    for ( Iterator it = collocations.iterator() ; it.hasNext() ; )
    {
      PSWord curWord = (PSWord)it.next();
      curWord.word = (CHString)sigMap.get(curWord.wordNr);
      // put curData back where it was or is that already enough?
    }
    return curData;
  }

  /**
   * Gets the direct output from the DB in form of integers and returns a vector
   * of PSData instances
   * @param word
   * @param disambs
   * @return
   */
  protected Vector getInitialPsDataInstances(PSWord word, Map disambs)
  {
    Vector psDataInstances = new Vector(disambs.size());
    for ( Iterator it = disambs.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curMeaning = (Integer)it.next();
      PSData psData = new PSData(word);
      for ( Iterator it2 = ((Vector)disambs.get(curMeaning)).iterator() ; it2.hasNext() ; )
      {
        Integer curWordNr = (Integer)it2.next();
        psData.addCollocation(new PSWord(curWordNr, 0.0, curMeaning.intValue()));
      }
      psDataInstances.add(psData);
    }
    return psDataInstances;
  }

  /**
   * Returns a map where a key is a number of a meaning as Integer
   * and a value a Vector of Integers belonging to that meaning
   * @param disambs
   * @return
   */
  protected Map getDisambMap(List disambs)
  {
    Map disambs2 = new HashMap();
    for ( Iterator it = disambs.iterator() ; it.hasNext() ; )
    {
      Integer[] curWord = (Integer[])it.next();
      Vector v = null;
      if ( disambs2.containsKey(curWord[1]) )
      {
        v = (Vector)disambs2.get(curWord[1]);
      }
      else
      {
        v = new Vector();
      }
      v.add(curWord[0]);
      disambs2.put(curWord[1], v);
    }
    return disambs2;
  }

  /**
   * Returns the list of PSDatas which is now enriched with the collocations of
   * collocations.
   * First get a fixed list of all wordNrs for which collocations are needed.
   * // todo: this fixed list has not yet been implemented!
   * //       instead it just goes through each word and gets its collocations
   * //       which's increases time twice in the normal case and by a factor
   * //       of 100 in the case that mysql 4.0 is installed!
   * (because some of them are double and because we might use mysql 4.0 union
   * to improve speed)
   * The go through each PSData instance
   *   for each collocation add its collocations from the retrieved list
   * @param List psData
   * @return
   */
  protected PSData addCollocationsOfCollocations(PSData curData)
  {
    List curCollocations = curData.getCollocationsList();
    // iterating through the collocations of the current meaning
    for ( Iterator it2 = curCollocations.iterator() ; it2.hasNext() ; )
    {
      PSWord cW = (PSWord)it2.next();
      Integer curWordNr = cW.wordNr;
//--      Map disambs = getDisambMap(this.dbUtil.getDisambiguation(curWordNr));
      Map disambs = getDisambMap(this.dbUtil.getDisambiguationFallback(curWordNr, Options.getInstance().getParaMinSecondarySignifikanz()));

      if ( disambs == null || disambs.size() == 0 )
      {
        continue;
      }
/*--      if ( disambs.size() < 2 )
      {
        disambs = getDisambMap(this.dbUtil.getDisambiguationFallback(curWordNr));
        System.err.println("Wordnr ["+cW+"]: had not enough disambiguation data!");
      }*/
      //System.out.println("disambs: "+disambs);
      Integer correctMeaning = getCorrectMeaning(curCollocations, disambs);
      // put the collocations as instances of PSWords into curData for the given word
      Set PSWords = new HashSet();
      // iterate through all collocations of the current collocation and makes
      // PSWord instances out of them
      System.out.print(".");
      //System.out.println("New: For word "+cW.word+" I've got "+((List)disambs.get(correctMeaning)).size()+" collocations");
      for ( Iterator it3 = ((List)disambs.get(correctMeaning)).iterator() ; it3.hasNext() ; )
      {
        Integer curWord = (Integer)it3.next();
        // No collocations of collocations should contain the initial word as collocation
        if ( curWord.intValue() != curData.word.wordNr.intValue() )
        {
          PSWord curPSWord = new PSWord( curWord, 0.0, correctMeaning.intValue() );
          PSWords.add( curPSWord );
        }
      }
      curData.addCollocation(cW, PSWords);
    }
    System.out.println();
    //Output.println("Output: "+curData);
    return curData;
  }

  /**
   * returns the key of the set from 'possibleMeanings' which is most similar to
   * the words in 'relativeSet'
   * @param relativeSet List of PSWords
   * @param possibleMeanings Map of Integer->Vector(Integer)
   * @return
   */
  protected Integer getCorrectMeaning(List relativeSet, Map possibleMeanings)
  {
    //System.out.println("\n\ngetCorrMean: "+relativeSet+" \n\nand\n\n "+possibleMeanings);
    Integer maxMeaning = null;
    double maxRating = 0.0;
    // make IntegerVector from relativeSet
    List relSet = PSData.getPSWordsAsIntegers(relativeSet);
    for ( Iterator it = possibleMeanings.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curKey = (Integer)it.next();
      Vector v = (Vector)possibleMeanings.get(curKey);
      double curRating = compare(relSet,v);
      if ( curRating > maxRating )
      {
        maxRating = curRating;
        maxMeaning = curKey;
      }
    }
    if ( maxMeaning == null )
    {
      return (Integer)possibleMeanings.keySet().iterator().next();
    }
    //System.out.println("return "+maxMeaning);
    return maxMeaning;
  }

  /**
   *
   * @param v1 Vector(Integer)
   * @param v2 Vector(Integer)
   * @return [0..1]
   */
  protected static double compare(List v1, List v2)
  {
    double count = 0.0;
    List smaller = v1;
    List bigger = v2;
    if ( v1.size() > v2.size() ) {smaller = v2; bigger = v1;}
    for ( Iterator it = smaller.iterator() ; it.hasNext() ; )
    {
      Integer curEntry = (Integer)it.next();
      if ( bigger.contains(curEntry) )
      {
        count += 1.0;
      }
    }
    if ( smaller.size() < 1 )
    {
      return 0;
    }
    //count = count / smaller.size();
    return count;
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
      System.out.print("\t\tdone\nCreating factory:");
      PSDataFactory factory = new PSDataFactory(util);
      CHString wordString = new CHString("Stich");
      PSWord word = new PSWord(wordString, util.getNumberForWord(wordString), 0.0, 0);
      System.out.print("\t\tdone\nCreating PSData element for word "+word+":");
      System.out.println("\t"+factory.getPSData(word));
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }
  }

}