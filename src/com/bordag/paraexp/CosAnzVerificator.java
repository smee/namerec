package com.bordag.paraexp;

import java.util.*;
import com.bordag.colloc.*;

/**
 * This class takes a word number as input,
 * calculates it's CosAnzMap,
 * gets the resulting cohyponyms,
 * creates their maps,
 * checks which words hit which element from 1hyperonymset most
 * and prints it all out
 *
 * @author Stefan Bordag
 */
public class CosAnzVerificator
{
  protected Collocations sigValues = null;

  protected Collocations anzValues = null;

  protected FileWortliste wortliste = null;


  public CosAnzVerificator(Integer wordNr, Collocations sigs, Collocations anz, FileWortliste wortliste)
  {
    this.sigValues = sigs;
    this.anzValues = anz;
    this.wortliste = wortliste;
    runAlgorithm(wordNr);
/*    ParaSynMap map = new ParaSynMap(wordNr, sigs, anz, wortliste, true);
    System.out.println("LING COLL: ");
    System.out.println(map.getLinguisticCollocationsRadian().toStringOrderedBy(0));
    System.out.println("HYP: ");
    System.out.println(map.getHyperonymsRadian().toStringOrderedBy(0));
    System.out.println("COHYP: ");
    System.out.println(map.getCohyponymsRadian().toStringOrderedBy(0));*/
  }

  protected void runAlgorithm(Integer wordNr)
  {
    // construct a vector space from all present words

      // taking cohyponym with, say 0.3
      // if his hyperonym has 0.5, multiply it with 1-0.3 that is (0.7)
      // that is multiply all hyperonyms with 0.7
      // from this word all hyperonyms then represent a vector/set
      // do this for all words

        // for each word in my data
        ParaSynWord inputWord = new ParaSynWord(wordNr);
        CosAnzMap map = new CosAnzMap(wordNr, this.sigValues, this.anzValues, this.wortliste, true);

        ParaSynWordHashMap curCohyps = map.getCohyponymsRadian();
        ParaSynWordHashMap curHyperons = map.getHyperonymsRadian();

        ParaSynWordHashMap results = new ParaSynWordHashMap();

        Map maps = new HashMap();
        for ( Iterator it = curCohyps.keySet().iterator() ; it.hasNext() ; )
        {
          ParaSynWord curWord = (ParaSynWord)it.next();
//          new ParaSynMap(curWord.wordNr, this.sigValues, this.anzValues, this.wortliste, false).getHyperonymsRadian();
          maps.put(curWord.wordNr, new CosAnzMap(curWord.wordNr, this.sigValues, this.anzValues, this.wortliste, false).getHyperonymsRadian());
        }

        for ( Iterator it = curCohyps.keySet().iterator() ; it.hasNext() ; )
        {
          ParaSynWord curWord = (ParaSynWord)it.next();
          double schnitt = 0.0;
          double count = 0.0;
//          System.out.println("\nFuer das Wort Elefant hat das moegliche Kohyponym "+curWord.word+" ");
          for ( Iterator it2 = curCohyps.keySet().iterator() ; it2.hasNext() ; )
          {
            count += 1.0;
            ParaSynWord curSecWord = (ParaSynWord)it2.next();
            if ( curSecWord.equals(curWord) ) { }
            else if ( curSecWord.equals(inputWord) )
            {
              // nimm hypWert direkt
              double hypWert = curHyperons.get(curWord).doubleValue();
              schnitt += hypWert;
            }
            else
            {
              //        -------------------------------------curSecWord stand hier
              double curSecWordCohypStrength = curCohyps.get(curSecWord).doubleValue(); // this is coh strength viewed directly from input word
              // in der map vom x, den Wert von hyp z nachschlagen
//              ParaSynWordHashMap[] map2 = (PSWordHashMap[])map.get(curSecWord);
//              PSWordHashMap map3 = map2[1];
              //ParaSynWordHashMap map3 = new ParaSynMap(curSecWord.wordNr, this.sigValues, this.anzValues, this.wortliste, false).getHyperonymsRadian();
              ParaSynWordHashMap map3 = (ParaSynWordHashMap)maps.get(curSecWord.wordNr);
//              System.out.print(".");
              Double xx = map3.get(curWord);
              if ( xx != null )
              {
                double x = xx.doubleValue();
                if ( x == Double.NaN || new String(""+x).equals(new String("NaN"))
                  || x == Double.NEGATIVE_INFINITY || x == Double.POSITIVE_INFINITY )
                {
                }
                else
                {
                  double curWordHypInSecMap = x;
                schnitt += curSecWordCohypStrength * curWordHypInSecMap ;
                }
              }
            }
          }
          double [] result = new double[2];
          result[0] = schnitt;
          schnitt = schnitt / count;
          result[1] = schnitt;
          if ( curWord.wordNr.intValue() != wordNr.intValue() )
          {
            results.put( curWord, result );
          }
          //System.out.println("Hyp value for: "+curWord+"   <---> "+schnitt);
        }

        System.out.println("\n\ninitial: <<"+this.wortliste.getWord(wordNr)+">> ("+wordNr+") Hyp values: "+results.toStringOrderedFirstNBy(5));

// EHER DIE ERSTEN X RAUSSCHMEISSEN KOMPLETT (KLEINES X)
        Collocations cosValues = new FileColloc("data/ksim/kollok_sim_cos.dump");
        ParaSynWordHashMap cohypMap = new ParaSynWordHashMap();
        for ( Iterator it = cosValues.getCollocsAndSigs(wordNr).iterator(); it.hasNext() ; )
        {
          Integer[] curEl = (Integer[])it.next();
          ParaSynWord word = new ParaSynWord(this.wortliste.getWord(curEl[0]),curEl[0],0.0,0.0);
          double[] curVal = new double[1];
          curVal[0] = curEl[1].doubleValue()/100000;
          cohypMap.put(word, curVal);
        }

        ParaSynWord maxWord = (ParaSynWord)cohypMap.getWinner();
        if ( maxWord == null )
        {
          return ;
        }
        double maxVal = cohypMap.get(maxWord).doubleValue();
        for ( Iterator it = cohypMap.values().iterator() ; it.hasNext() ; )
        {
          double[] curVal = (double[])it.next();
          curVal[0] = curVal[0]/maxVal;
        }
        System.out.println("cohyps: <<"+this.wortliste.getWord(wordNr)+">> ("+wordNr+") Hyp values: "+cohypMap.toStringOrderedFirstNBy(500));

        for ( Iterator it = results.keySet().iterator() ; it.hasNext() ; )
        {
          ParaSynWord curWord = (ParaSynWord)it.next();
          double curVal = results.get(curWord).doubleValue();

          if ( cohypMap.get(curWord) == null )
          {
            continue;
          }
          //System.out.println(curWord.word+" having this rating: "+curVal[1]+" will be devided with this cos rating: "+cohypMap.get(curWord)[0]);
          //curVal[1] = curVal[1] - cohypMap.get(curWord)[0]/20.0;
// FIXME: This is broken now
          System.err.println("Due to interface changes this algorithm is utterly broken now");
          System.exit(0);
/////          curVal[1] = curVal[1] * (1.0 - cohypMap.get(curWord)[0]);
          results.remove(curWord);
          results.put(curWord, new Double(curVal));
        }

        System.out.println("final: <<"+this.wortliste.getWord(wordNr)+">> ("+wordNr+") Hyp values: "+results.toStringOrderedFirstNBy(10));
        //System.out.println("Hyp values unnormalized: "+results.toStringOrderedBy(0));
      //this.data



//---------------------

// -1. Alles ausrechnen und dann umgekehrt schauen, wer alles dem Wort w zugeordnet wurde.
// diese Wortmenge Clustern und bestes(?) cluster nehmen, also groesstes oder
// dichtestes oder sowas.

// 0.
// Wenn Wortnummer (/ Frequenz) des Eingabewortes etwa gleich oder gar kleiner
// ist, als die des gefunden Kandidaten, dann diesen verwerfen (und entweder
// Wort als nicht Hyperonymfaehig deklarieren oder wenn innerhalb der ersten
// fuenf ein Wort dieses Kriterium erfuellen wuerde - dieses nehmen)

// 1.
// Take another "independant" measure for cohyponyms, like full_cos and filter
// those with high values away from here

// 2.
// use grfNAVS to take only nouns (N)

// 3.
// Disambiguierung einbauen wieder?

// 4.
// Zu GermaNet vergleichen

// 5.
// Mit * Wissensquelle kombinieren: Internet-Nutzer -> Nutzer, weil *nutzer sind wohl alle Nutzer

// 6.
// Nach rumspielen mit Parameter eine groessere Anzahl versuche analysieren
// also gefundene Relation bewerten usw.

// 7.
// Bei Namen scheint's immer der typische Beruf zu sein -> CB's Namenserkenner um
// grosse Anzahl 'fehler' rauszuschmeissen

//----------------------
      // calculate mean for each hyperonym, over all cohyponyms, the one with
      // the highest wins ( use PSWordHashMap again for easy ordering print )

    // retrieve vectors from maps, delete maps

  }


  public static void main(String[] args)
  {
    Collocations sigs = new FileColloc("data/ksim/kollok_sim_cos.dump");
//    Collocations anz = new FileColloc("data/ksim/kollok_sim_anz.dump");

    Collocations anz = new FileColloc("data/ksim/kollok_sim_halfcos_anzahl.dump");
    FileWortliste wortliste = new FileWortliste("data/ksim/wortliste.dump");
    CosAnzVerificator v = new CosAnzVerificator( new Integer( 20782 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 2475 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 11179 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 12550 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 30061 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 3803 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 1410 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 1398 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 10865 ), sigs, anz, wortliste );

    v = new CosAnzVerificator( new Integer( 21136 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 3552 ), sigs, anz, wortliste );
    v = new CosAnzVerificator( new Integer( 11740 ), sigs, anz, wortliste );
/*    for ( int i = 29000 ; i < 100000 ; i+=100 )
    {
      ParaSynVerificator v = new ParaSynVerificator( new Integer( i ), sigs, anz, wortliste );
    }*/
  }
}
