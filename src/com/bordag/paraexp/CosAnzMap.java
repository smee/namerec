package com.bordag.paraexp;

import java.util.*;
import com.bordag.colloc.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CosAnzMap extends ParaSynMap
{
  public CosAnzMap(Integer wordNr, Collocations cos, Collocations anz, FileWortliste wortliste, boolean resolveStrings)
  {
    this.word = new ParaSynWord(wordNr);
    this.sigValues = cos;
    this.anzValues = anz;
    this.wortliste = wortliste;
    this.collocations = new HashSet();
    this.resolveStrings = resolveStrings;

    //super(wordNr, cos, anz, wortliste, resolveStrings);
  }

  public ParaSynWordHashMap getLinguisticCollocationsRadian()
  {
    return new ParaSynWordHashMap();
  }

  public ParaSynWordHashMap getCohyponymsRadian()
  {
    ParaSynWordHashMap retMap = new ParaSynWordHashMap();
    for ( Iterator it = this.sigValues.getCollocsAndSigs(this.word.wordNr, ParaSynOptions.getInstance().getPsmapMinSig(), ParaSynOptions.getInstance().getPsmapMaxColl(), ParaSynOptions.getInstance().getPsmapMinWordNr()).iterator(); it.hasNext() ; )
    {
      Integer[] curEl = (Integer[])it.next();
      ParaSynWord word = new ParaSynWord(this.wortliste.getWord(curEl[0]),curEl[0],0.0,0.0);
      double[] curVal = new double[1];
      curVal[0] = curEl[1].doubleValue()/100000;
      retMap.put(word, curVal);
    }

    ParaSynWord maxWord = (ParaSynWord)retMap.getWinner();
    if ( maxWord == null )
    {
//      System.out.println("Map ist really empty? size = "+retMap.size());
      return retMap;
    }
//    System.out.println("Is "+maxWord+" in the map?");
    double maxVal = retMap.get(maxWord).doubleValue();
    for ( Iterator it = retMap.values().iterator() ; it.hasNext() ; )
    {
      double[] curVal = (double[])it.next();
      curVal[0] = curVal[0]/maxVal;
    }

    return retMap;
  }

  public ParaSynWordHashMap getHyperonymsRadian()
  {
    ParaSynWordHashMap retMap = new ParaSynWordHashMap();
    for ( Iterator it = this.anzValues.getCollocsAndSigs(this.word.wordNr, ParaSynOptions.getInstance().getPsmapMinSig(), ParaSynOptions.getInstance().getPsmapMaxColl(), ParaSynOptions.getInstance().getPsmapMinWordNr()).iterator(); it.hasNext() ; )
    {
      Integer[] curEl = (Integer[])it.next();
      ParaSynWord word = new ParaSynWord(this.wortliste.getWord(curEl[0]),curEl[0],0.0,0.0);
      double[] curVal = new double[1];
      curVal[0] = curEl[1].doubleValue();
      retMap.put(word, curVal);
    }
    ParaSynWord maxWord = (ParaSynWord)retMap.getWinner();
    if ( maxWord == null )
    {
//      System.out.println("Map ist really empty? size = "+retMap.size());
      return retMap;
    }
//    System.out.println("Is "+maxWord+" in the map?");
    double maxVal = retMap.get(maxWord).doubleValue();
    for ( Iterator it = retMap.values().iterator() ; it.hasNext() ; )
    {
      double[] curVal = (double[])it.next();
      curVal[0] = curVal[0]/maxVal;
    }
    return retMap;
  }
}