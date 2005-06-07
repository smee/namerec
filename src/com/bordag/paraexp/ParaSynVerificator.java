package com.bordag.paraexp;

import java.util.*;
import com.bordag.colloc.*;

/**
 * This class takes a word number as input,
 * calculates it's ParaSynMap,
 * gets the resulting cohyponyms,
 * creates their maps,
 * checks which words hit which element from 1hyperonymset most
 * and prints it all out
 *
 * @author Stefan Bordag
 */
public class ParaSynVerificator
{
  protected Collocations sigValues = null;

  protected Collocations anzValues = null;

  protected FileWortliste wortliste = null;

  protected FileGrfNAVS gramm = null;

  protected ParaSynWordHashMap results = null;

  public ParaSynVerificator(Integer wordNr, Collocations sigs, Collocations anz, FileWortliste wortliste, FileGrfNAVS gramm)
  {
    this.sigValues = sigs;
    this.anzValues = anz;
    this.wortliste = wortliste;
    this.gramm = gramm;
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
        ParaSynMap map = new ParaSynMap(wordNr, this.sigValues, this.anzValues, this.wortliste, true);

        ParaSynWordHashMap curCohyps = map.getCohyponymsRadian();
        ParaSynWordHashMap curHyperons = map.getHyperonymsRadian();

        results = new ParaSynWordHashMap();

        Map maps = new HashMap();
        for ( Iterator it = curCohyps.keySet().iterator() ; it.hasNext() ; )
        {
          ParaSynWord curWord = (ParaSynWord)it.next();
//          new ParaSynMap(curWord.wordNr, this.sigValues, this.anzValues, this.wortliste, false).getHyperonymsRadian();
          maps.put(curWord.wordNr, new ParaSynMap(curWord.wordNr, this.sigValues, this.anzValues, this.wortliste, false).getHyperonymsRadian());
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
              double hypWert = curHyperons.get(curWord).doubleValue(); //// Hier war [0], da unten auch
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

        System.out.println("<<"+this.wortliste.getWord(wordNr)+">> ("+wordNr+") Hyp values: "+results.toStringOrderedFirstNBy(5));

/*
        try
        {
          String inputWordString = this.wortliste.getWord(wordNr);
          double inputFreq = Math.log(  ((Integer)((Object[])this.wortliste.getWordAndAnzahl(wordNr))[1]).doubleValue()  );
          ParaSynWord resultPSWord = (ParaSynWord)results.getMax( results, 0 );
          int count = 5;
          while ( resultPSWord != null )
          {
            double curFreq = Math.log(  ((Integer)((Object[])this.wortliste.getWordAndAnzahl(resultPSWord.wordNr))[1]).doubleValue()  );
            if ( Math.abs ( curFreq - inputFreq ) < 0.5 )
            {
              //--System.out.println("Freq NOT: "+inputWordString+"\t"+resultPSWord.word);
              results.remove(resultPSWord);
              resultPSWord = (ParaSynWord)results.getMax( results, 0 );
              continue;
            }
            String curString = resultPSWord.word;
            if ( curString.startsWith(inputWordString) || curString.endsWith(inputWordString) ||
                 inputWordString.startsWith(curString) || inputWordString.endsWith(curString) )
            {
              //--System.out.println("lemma NOT: "+inputWordString+"\t"+resultPSWord.word);
              results.remove(resultPSWord);
              resultPSWord = (ParaSynWord)results.getMax( results, 0 );
              continue;
            }
//            System.out.println(curString.matches("^[A-Z]")+" "+inputWordString.matches("^[a-z]")+" "+curString.matches("^[a-z]")+" "+inputWordString.matches("^[A-Z]"));

            boolean contains = false;
            for ( Iterator it = this.gramm.getWortarten(wordNr).iterator() ; it.hasNext() ; )
            {

              if ( this.gramm.getWortarten(resultPSWord.wordNr).contains(it.next()) )
              {
                contains = true;
              }
            }
            if ( !contains )
            {
              //--System.out.println("POS NOT: "+inputWordString+"\t"+resultPSWord.word);
              results.remove(resultPSWord);
              resultPSWord = (ParaSynWord)results.getMax( results, 0 );
              continue;

            }

            System.out.println( "" + this.wortliste.getWord( wordNr ) + "\t\t" +
                              ( ( ParaSynWord )results.getMax( results, 0 ) ).word );
            results.remove(resultPSWord);
            resultPSWord = (ParaSynWord)results.getMax( results, 0 );
            count--;
            if ( count <=0 )
            {
              break;
            }
          }
//          String resultWord =
        }
        catch ( Exception ex )
        {
          ex.printStackTrace();
        }
*/
        //System.out.println("Hyp values unnormalized: "+results.toStringOrderedBy(0));
      //this.data


//---------------------
// -1.
// Groessere Gewichtung der Entfernungen, z.Bsp. durch quadrieren, so dass nahe
// Punkte bei den Kohyponymen mehr Stimmengewicht gegenueber den weiter entfernten erhalten

// 0. probiert - unklar - echte Evaluierung hierfuer benoetigt
// Wenn Wortnummer (/ Frequenz) des Eingabewortes etwa gleich oder gar kleiner
// ist, als die des gefunden Kandidaten, dann diesen verwerfen (und entweder
// Wort als nicht Hyperonymfaehig deklarieren oder wenn innerhalb der ersten
// fuenf ein Wort dieses Kriterium erfuellen wuerde - dieses nehmen)

// 1. probiert
// Take another "independant" measure for cohyponyms, like full_cos and filter
// those with high values away from here - eher schlechter soweit aber hartes
// Herausschneiden von vermuteten Kohyponymen vielleicht besser

// 2. probiert
// use grfNAVS to take only nouns (N) - eher schlecht soweit

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


  public ParaSynWordHashMap getResults()
  {
    return this.results;
  }

  public static void main(String[] args)
  {
    Collocations sigs = new FileColloc("data/ksim/kollok_sig.dump");

//    Collocations anz = new FileColloc("data/ksim/kollok_sim_anz.dump");
    Collocations anz = new FileColloc("data/ksim/kollok_sim_halfcos_anzahl.dump");

    FileWortliste wortliste = new FileWortliste("data/ksim/wortliste.dump");
    FileGrfNAVS gramm = new FileGrfNAVS("data/ksim/grfNAVS.dump");
    ParaSynVerificator v = null;


    v = new ParaSynVerificator( wortliste.getNumber("Gedicht"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Gedichte"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Novelle"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Novellen"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Roman"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Romane"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Bestseller"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Prosa"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Poesie"), sigs, anz, wortliste, gramm );

    v = new ParaSynVerificator( wortliste.getNumber("Tauben"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Trauben"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Weintrauben"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Erdbeere"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Erdbeeren"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Himbeere"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Himbeeren"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Brombeere"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Brombeeren"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Heidelbeere"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Heidelbeeren"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Wandervogel"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Wandervögel"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Zugvogel"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Zugvögel"), sigs, anz, wortliste, gramm );

    v = new ParaSynVerificator( wortliste.getNumber("Kieselstein"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Möven"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Adler"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Fink"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Amsel"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Amseln"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Spatz"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Spatzen"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Meiße"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Meißen"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Sperling"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Sperlinge"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Sperlings"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Taube"), sigs, anz, wortliste, gramm );

    v = new ParaSynVerificator( wortliste.getNumber("Tschechisch"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Polnisch"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Russisch"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Französisch"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Latein"), sigs, anz, wortliste, gramm );

    v = new ParaSynVerificator( wortliste.getNumber("Muttersprache"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Fremdsprache"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Zweitsprache"), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( wortliste.getNumber("Drittsprache"), sigs, anz, wortliste, gramm );

    v = new ParaSynVerificator( wortliste.getNumber("Stiere"), sigs, anz, wortliste, gramm ); // Verhandlungstisch
    v = new ParaSynVerificator( wortliste.getNumber("Stieres"), sigs, anz, wortliste, gramm ); // Stier
    v = new ParaSynVerificator( wortliste.getNumber("Stier"), sigs, anz, wortliste, gramm ); // Stier
    v = new ParaSynVerificator( new Integer( 16189 ), sigs, anz, wortliste, gramm ); // Zwillinge
    v = new ParaSynVerificator( new Integer( 18533 ), sigs, anz, wortliste, gramm ); // Jungfrau
    v = new ParaSynVerificator( new Integer( 59154 ), sigs, anz, wortliste, gramm ); // Widder
    v = new ParaSynVerificator( new Integer( 5616 ), sigs, anz, wortliste, gramm ); // Krebs


    v = new ParaSynVerificator( new Integer( 14163 ), sigs, anz, wortliste, gramm ); // Schlafzimmer
    v = new ParaSynVerificator( new Integer( 5962 ), sigs, anz, wortliste, gramm ); // Bad
    v = new ParaSynVerificator( new Integer( 11758 ), sigs, anz, wortliste, gramm ); // Flur
    v = new ParaSynVerificator( new Integer( 7235 ), sigs, anz, wortliste, gramm ); // Wohnzimmer
    v = new ParaSynVerificator( new Integer( 1082 ), sigs, anz, wortliste, gramm ); // Wohnung
    v = new ParaSynVerificator( new Integer( 2421 ), sigs, anz, wortliste, gramm ); // Zimmer

    v = new ParaSynVerificator( new Integer( 1748 ), sigs, anz, wortliste, gramm ); // Fenster
    v = new ParaSynVerificator( new Integer( 3262 ), sigs, anz, wortliste, gramm ); // Bett

    v = new ParaSynVerificator( new Integer( 10824 ), sigs, anz, wortliste, gramm ); // Teller
    v = new ParaSynVerificator( new Integer( 19635 ), sigs, anz, wortliste, gramm ); // Tasse
    v = new ParaSynVerificator( new Integer( 61034 ), sigs, anz, wortliste, gramm ); // Kaffeetasse
    v = new ParaSynVerificator( new Integer( 109514 ), sigs, anz, wortliste, gramm ); // Teekanne
    v = new ParaSynVerificator( new Integer( 31672 ), sigs, anz, wortliste, gramm ); // Gabel
    v = new ParaSynVerificator( new Integer( 4773 ), sigs, anz, wortliste, gramm ); // Messer
    v = new ParaSynVerificator( new Integer( 46589 ), sigs, anz, wortliste, gramm ); // Besteck





    v = new ParaSynVerificator( new Integer( 1386 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 12988 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 3262 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 10824 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 75117 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 107369 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 9725 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 193150 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 32980 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 9669 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 2475 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 11179 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 12550 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 30061 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 3803 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 1410 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 1398 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 10865 ), sigs, anz, wortliste, gramm );

    v = new ParaSynVerificator( new Integer( 21136 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 3552 ), sigs, anz, wortliste, gramm );
    v = new ParaSynVerificator( new Integer( 11740 ), sigs, anz, wortliste, gramm );
/*    for ( int i = 29000 ; i < 100000 ; i+=1 )
    {
      ParaSynVerificator v = new ParaSynVerificator( new Integer( i ), sigs, anz, wortliste, gramm );
    }*/
  }
}