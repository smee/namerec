package com.bordag.paraexp;

import com.bordag.colloc.*;
import java.util.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class RankRaiser
{
  protected Collocations sigs = null;
  protected Collocations anz = null;
  protected FileWortliste wortliste = null;

  protected final static int MIN_SIG = 4;
  protected final static int MAX_COLL = 1000;
  protected final static int MIN_WORDNR = 500;

  public RankRaiser(String fileNameSigs, String fileNameAnz, String fileNameWortliste)
  {
    this.sigs = new FileColloc(fileNameSigs);
    this.anz = new FileColloc(fileNameAnz);
    this.wortliste = new FileWortliste(fileNameWortliste);
  }

  public ParaSynWordHashMap getResults(String word)
  {
    ParaSynWord word2 = new ParaSynWord(word, this.wortliste.getNumber(word));
    return getResults(word2);
  }

  /**
   * Those words which weren't in hte sigs set get added in the end - NO!
   * @param word
   * @return
   */
  public ParaSynWordHashMap getResults(ParaSynWord word)
  {
    ParaSynWordHashMap candidatesSetSigs = new ParaSynWordHashMap(1);
    // first make set of all candidates, first get sigs, then anz, values is
    // the simple counter
    double counter = 1.0;
    for ( Iterator it = this.sigs.getCollocs(word.wordNr, this.MIN_SIG, this.MAX_COLL, this.MIN_WORDNR).iterator() ; it.hasNext() ; counter+=1.0)
    {
      Integer curWordNr = (Integer)it.next();
      ParaSynWord curWord = new ParaSynWord(this.wortliste.getWord(curWordNr), curWordNr);
      candidatesSetSigs.put(curWord, new Double(counter));
    }

    ParaSynWordHashMap candidatesSetAnz = new ParaSynWordHashMap(1);
    // first make set of all candidates, first get sigs, then anz, values is
    // the simple counter
    counter = 1.0;
    for ( Iterator it = this.anz.getCollocs(word.wordNr, this.MIN_SIG, this.MAX_COLL, this.MIN_WORDNR).iterator() ; it.hasNext() ; counter+=1.0)
    {
      Integer curWordNr = (Integer)it.next();
      ParaSynWord curWord = new ParaSynWord(this.wortliste.getWord(curWordNr), curWordNr);
      candidatesSetAnz.put(curWord, new Double(counter));
    }
/*    System.out.println("Ranking sigs "+word+": "+candidatesSetSigs.toStringOrderedFirstNBy(100));
    System.out.println("Ranking anz  "+word+": "+candidatesSetAnz.toStringOrderedFirstNBy(100));*/

    ParaSynWordHashMap results = new ParaSynWordHashMap(1);
    for ( Iterator it = candidatesSetSigs.keySet().iterator() ; it.hasNext() ; )
    {
      ParaSynWord curWord = (ParaSynWord)it.next();
      double rankSigs = candidatesSetSigs.get(curWord).doubleValue();
      Double rankAnzD = candidatesSetAnz.get(curWord);
      if ( rankAnzD == null )
      {
        continue;
      }
      double rankAnz = candidatesSetAnz.get(curWord).doubleValue();
      //System.out.println("Word ["+curWord+"] Rank sigs: "+rankSigs+"  Rank anz: "+rankAnz);
      results.put(curWord,new Double( Math.pow( Math.log(rankAnz), 0.001 ) - Math.pow( Math.log(rankSigs), 0.001 ) ));
    }
    return results;
  }

  public static void main(String[] args)
  {
    int show = 50;
    String fileNameSigs = "data/ksim/kollok_sig.dump";
    String fileNameAnz = "data/ksim/kollok_sim_halfcos_anzahl.dump";
    String fileNameWortliste = "data/ksim/wortliste.dump";
    RankRaiser rank = new RankRaiser(fileNameSigs, fileNameAnz, fileNameWortliste);
    System.out.println("Ranking for Elefant "+rank.getResults("Elefant").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Giraffe "+rank.getResults("Giraffe").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Antilope "+rank.getResults("Antilope").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Gazelle "+rank.getResults("Gazelle").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Vaclav Havel "+rank.getResults("Vaclav Havel").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Wohnzimmer "+rank.getResults("Wohnzimmer").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Flur "+rank.getResults("Flur").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Gabel "+rank.getResults("Gabel").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Besteck "+rank.getResults("Besteck").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Quark "+rank.getResults("Quark").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Käse "+rank.getResults("Käse").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Joghurt "+rank.getResults("Joghurt").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for RTL "+rank.getResults("RTL").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for ZDF "+rank.getResults("ZDF").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for BMW "+rank.getResults("BMW").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Benz "+rank.getResults("Benz").toStringOrderedFirstNBy(show));
    System.out.println("Ranking for Yen "+rank.getResults("Yen").toStringOrderedFirstNBy(show));
  }

}