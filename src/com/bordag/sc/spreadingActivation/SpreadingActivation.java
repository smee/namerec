package com.bordag.sc.spreadingActivation;

import de.wortschatz.util.*;

import java.util.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SpreadingActivation
{

  private static int MIN_SIGNIFIKANZ = 1;
  private static int MIN_WORDNUM = 1;
  private static int MAX_KOLLOK = 500;
  // A HashMap which stores wordnumbers as keys and wordstrings as values
  // stores only those words which are also in the graph
  private HashMap wordNrMap = null;

  private DBConnection connection = null;

  private HashMap stopWords = null;

  private ActivationGraph graph = null;

  public SpreadingActivation(DBConnection connection)
  {
    this.wordNrMap = new HashMap();
    this.stopWords = new HashMap();
    // fill stopWordMap from file...

    this.connection = connection;
    this.graph = new ActivationGraph();
  }

  public void nextWordNr(Integer number, String word)
  {
    //System.out.println("receiving number: "+number);
    // if word is not stopword
    if ( ! this.stopWords.containsKey(number) )
    {
      // add word to my own mapping
      this.wordNrMap.put(number, word);
      // get neighbours of word
      Set neighbours = this.connection.getSatzKollokationen(number, this.MIN_SIGNIFIKANZ, this.MIN_WORDNUM, this.MAX_KOLLOK);
      // add all neighbours to wordNrmapping
      addMapping(neighbours);
      // add node with neighbours to ActivationGraph
      this.graph.addNode(number, neighbours);
      // call timeStep-activation on that word
      this.graph.activate(number, 1.0, 0, true);
      // call pruning of graph to keep it small
      //this.graph.pruneGraph();
    }
    //System.out.println("Graph looks now like this: "+graph);
  }

  /**
   * Retrieves also the strings for the neighbours
   * @param neighbours
   */
  private void addMapping(Set neighbours)
  {
    Integer[] numbers = new Integer[neighbours.size()];
    int i = 0;
    for ( Iterator it = neighbours.iterator() ; it.hasNext() ; i++ )
    {
      numbers[i] = (Integer)it.next();
    }
    Arrays.sort(numbers);
    i = 0;
    String[] words = this.connection.getWordsForNumbers(numbers);
    for ( int j = 0; j < numbers.length ; j++ )
    {
      this.wordNrMap.put(numbers[j], words[j]);
    }
  }

  public String correct(String word)
  {
    if ( word == null || word.length() < 1 )
    {
      return word;
    }
    // create HashMap with value:
    // key = wordNumber
    // value = clever combination(tm) = LD*(1-activation)
    HashMap values = new HashMap();
    HashMap activations = this.graph.getActivations();
    for ( Iterator it = activations.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curNr = (Integer)it.next();
      double curActivation = ((Double)activations.get(curNr)).doubleValue();
      double levenshteinDistance = (double)LevenshteinDistance.LD(word, (String)this.wordNrMap.get(curNr));
      double combination = levenshteinDistance * ( 1.0 - curActivation );
      //double combination = levenshteinDistance;
      values.put(this.wordNrMap.get(curNr),new Double(combination));
    }
    //System.out.println("\nMap looks finally like that: "+values);

    double minVal = Double.MAX_VALUE;
    String minString = word;
    for ( Iterator it = values.keySet().iterator() ; it.hasNext() ; )
    {
      String curString = (String) it.next();
      double curVal = ((Double)values.get(curString)).doubleValue();
      if ( curVal < minVal )
      {
        minVal = curVal;
        minString = curString;
      }
    }


    System.out.print(" LD="+(double)LevenshteinDistance.LD(word, minString));
    System.out.print(" ac="+this.graph.getActivations().get(getNrOfWord(minString)));
    System.out.print(" tg="+minVal+"*");
    return minString;
  }

  private Integer getNrOfWord(String word)
  {
    for ( Iterator it = this.wordNrMap.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curKey = (Integer)it.next();
      if ( this.wordNrMap.get(curKey).equals(word) )
      {
        return curKey;
      }
    }
    return null;
  }
}