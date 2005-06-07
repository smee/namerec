package com.bordag.ksim;

import com.bordag.util.*;

/**
 * Options to make optionable:
 *
 * @author Stefan Bordag
 * @date   19.11.2003
 */
public class KSimOptions //extends Options
{

  private int batchSize = 100;

  private int beginNr = 501;
  private int endNr = 6544477;
  private boolean takeColls = true;
  private boolean loadCollocsToRam = false;
  private boolean showProgress = false;

  // this gives the number of collocations which will be fetched directly from the starting word
  private int maxColls1 = 80;
  // this gives the number of collocations which will be fetched from each direct collocation
  private int maxColls12 = 80;
  // this is the number of colls which are used to compare the words (that is, vectors are this long)
  private int maxColls2 = 500;

  private int minSignificance = 4;
  private int minWordNr = 2000;
  private int maxResultSize = 500;
  private int resultsFactor = 100000;

  private String ConURL = "jdbc:mysql://alf.rz.uni-leipzig.de/wortschatz?user=sbordag&password=ansiBla";
  private String ConUser = "sbordag";
  private String ConPasswd = "ansiBla";
  private String ConDriver = "org.gjt.mm.mysql.Driver";

  private String outputMainString = "results";

  private String QueryWordForNumber = "select w.wort_bin from wortliste w where w.wort_nr = #ARG#;";
  private String QueryNumberForWord = "select w.wort_nr from wortliste w where w.wort_bin = \"#ARG#\"";
  private String QueryFrequenyForNumber = "select w.anzahl from wortliste w where w.wort_nr = #ARG#;";
  private String QueryDisambigForNumber = "SELECT d.wort_nr2,d.wort_grp FROM disamb_sig d WHERE d.wort_nr2 > #ARG# and d.wort_nr1 = #ARG# order by d.wort_grp desc;";
  private String QueryKollokationen = "SELECT k.wort_nr2,k.signifikanz FROM kollok_sig k WHERE k.signifikanz > #ARG# and k.wort_nr2 > #ARG# and k.wort_nr1 = #ARG# order by k.signifikanz desc limit #ARG#;";
  private String QueryWordForm = "select wortart from grfNAVS g where g.wort_nr = #ARG#;";

  protected KSimOptions(){}

  /**
   * The sole instance of options for the whole program
   */
  private static KSimOptions instance = null;

  public static KSimOptions getInstance()
  {
    if ( instance == null )
    {
      synchronized(KSimOptions.class)
      {
        instance = new KSimOptions();
      }
    }
    return instance;
  }

  public int getBatchSize()
  {
    return this.batchSize;
  }
  public void setBatchSize(int size)
  {
    this.batchSize = size;
  }

  public int getBeginNr()
  {
    return this.beginNr;
  }
  public void setBeginNr(int beginNr)
  {
    this.beginNr = beginNr;
  }

  public int getEndNr()
  {
    return this.endNr;
  }
  public void setEndNr(int endNr)
  {
    this.endNr = endNr;
  }

  public int getMinSignifikance()
  {
    return this.minSignificance;
  }
  public void setMinSignifikance(int minSig)
  {
    this.minSignificance = minSig;
  }


  public boolean getTakeColls()
  {
    return this.takeColls;
  }
  public void setTakeColls(boolean takeColls)
  {
    this.takeColls = takeColls;
  }

  public boolean getLoadCollsToRam()
  {
    return this.loadCollocsToRam;
  }
  public void setLoadCollsToRam(boolean loadColls)
  {
    this.loadCollocsToRam = loadColls;
  }

  public boolean getShowProgress()
  {
    return this.showProgress;
  }
  public void setShowProgress(boolean show)
  {
    this.showProgress = show;
  }


  public int getMaxKollokationen1()
  {
    return this.maxColls1;
  }
  public void setMaxKollokationen1(int newVal)
  {
    this.maxColls1 = newVal;
  }

  public int getMaxKollokationen12()
  {
    return this.maxColls12;
  }
  public void setMaxKollokationen12(int newVal)
  {
    this.maxColls12 = newVal;
  }

  public int getMaxKollokationen2()
  {
    return this.maxColls2;
  }
  public void setMaxKollokationen2(int newVal)
  {
    this.maxColls2 = newVal;
  }


  public String getConUrl()
  {
    //return "jdbc:mysql://alf.rz.uni-leipzig.de/wortschatz?user=sbordag&password=ansiBla";
    return this.ConURL;
  }

  public String getConUser()
  {
    return this.ConUser;
  }

  public String getConPasswd()
  {
    return this.ConPasswd;
  }

  public String getOutputMainString()
  {
    return this.outputMainString;
  }

  public int getResultsFactor()
  {
    return this.resultsFactor;
  }

  public String getQueryNumberForWord()
  {
    return this.QueryNumberForWord;
  }

  public String getQueryWordForNumber()
  {
    return this.QueryWordForNumber;
  }

  public String getQueryFrequency()
  {
    return this.QueryFrequenyForNumber;
  }

  public String getQueryWordForm()
  {
    return this.QueryWordForm;
  }

  public int getMinWordNr()
  {
    return this.minWordNr;
  }
  public void setMinWordNr(int newVal)
  {
    this.minWordNr = newVal;
  }

  public String getQueryDisambig()
  {
    return this.QueryDisambigForNumber;
  }



  public String getQueryKollokationen()
  {
    return this.QueryKollokationen;
  }

  public String getConDriver()
  {
    return this.ConDriver;
  }

  public int getMaxResultsSizePerWord()
  {
    return this.maxResultSize;
  }


}