package com.bordag.sc;

import java.util.*;

import com.bordag.sc.util.*;
import com.bordag.sgz.util.*;
import com.bordag.sc.spreadingActivation.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SpellChecker
{
  protected de.wortschatz.util.DBConnection connection = null;
  protected WordServer server = null;
  protected int maxKollok = 0;
  protected double LDInfluence = 2.0;

  public SpellChecker(de.wortschatz.util.DBConnection connection, WordServer server, int maxKollok, double LDInfluence)
  {
    this.LDInfluence = LDInfluence;
    this.connection = connection;
    this.server = server;
    this.maxKollok = maxKollok;
  }

  public String[] getCorrectedSentence(String[] sentence)
  {
    SpreadingCounterActivation spreading = new SpreadingCounterActivation(this.connection, this.LDInfluence);
    spreading.setMaxKollok(this.maxKollok);
    String[] newSentence = new String[sentence.length];
    for ( int i = 0 ; i < sentence.length ; i++ )
    {
      if ( this.server.getNrForWord(sentence[i]) != null && sentence[i].length() > 0 )
      {
        spreading.nextWordNr(this.server.getNrForWord(sentence[i]), sentence[i]);
        newSentence[i] = sentence[i];
      }
      else
      {
        String correctedWord = spreading.correct(sentence[i]);
        newSentence[i] = correctedWord;
        System.out.println(sentence[i]+" -> "+newSentence[i]);
//        newSentence[i] = "noDecision";
      }
    }
    return newSentence;
  }

}