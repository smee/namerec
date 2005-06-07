package com.bordag.paraexp.eval;

import com.bordag.paraexp.*;
import com.bordag.colloc.*;

import java.util.*;
import java.io.*;

/**
 * Format der Evaluierungsdatei:
 *
 * Hyperonymwortform1 ... n : {Hyponymwf1 ... n} hyponym2 hyponym3
 *
 *
 * Beispiel:
 * Jahreszeit Jahreszeiten : {Frühling Frühlings} Herbst Sommer Winter
 * Sommer Sommers: {Frühsommer Frühsommers} Spätsommer Hochsommer
 *
 *
 * Dabei wird bei den hyponymen ein Knoten angelegt, dessen Wortformen
 * spaeter aufgefuellt werden, wenn ein solcher Hyperonymknoten gefunden
 * wird, welcher diese Wortform beinhaltet.
 *
 * Algorithmus:
 * - Vor dem Dopelpunkt ergibt einen Knoten ( wenn nicht schon ein Knoten
 *   existiert, der eine der Wortformen enthaelt)
 * - Nebenbei zum Tree erstelle immer noch einen Eintrag in einer Map, wobei
 *   ParaSynWord -> EvalNode ist
 * - Somit koennen einem Knoten spaeter noch Wortformen hinzugefuegt werden,
 *   ohne im Baum herumsuchen zu muessen.
 *
 *
 * @author Stefan Bordag
 */
public class EvalTreeFactory
{
  protected FileWortliste wortliste = null;
  protected Map PSMap = null;

  public static final String DELIMITER = ":";


  public EvalTreeFactory(String fileName)
  {
    this.PSMap = new HashMap();
    //String fileName = "data/psdata/pseval.txt";
    this.wortliste = new FileWortliste("data/ksim/wortliste.dump");
    List lines = readFile(fileName);
    for ( Iterator it = lines.iterator() ; it.hasNext() ; )
    {
      String curLine = (String)it.next();
      processLine(curLine);
    }
    System.out.println("Finally map: "+this.PSMap);
  }

  /**
   * First try to find hyperonym node or create new one, putting all wforms there
   * Then try to find for each son it's nod and if not existant, create it
   * @param curLine
   */
  protected void processLine(String curLine)
  {
    boolean sameNode = false; //
    List collectedNodes = new Vector(); //

    // find all nodes which exist to these hyperonyms
    EvalNode fathernode = findOrCreateHypNode(getParaSynWords(curLine, this.DELIMITER, 0));
    for ( Iterator it = getParaSynWords(curLine, this.DELIMITER, 1).iterator() ; it.hasNext() ; )
    {
      ParaSynWord curWord = (ParaSynWord)it.next();
      if ( curWord.word.equals("{") ) //
      {                               //
        sameNode = true;              //
        continue;                     //
      }                               //
      if ( curWord.word.equals("}") ) //
      {                               //
        sameNode = false;             //
        EvalNode newCollectedNode = new EvalNode();
        newCollectedNode.father = fathernode;
        for ( Iterator it2 = collectedNodes.iterator() ; it2.hasNext() ; )
        {
          EvalNode curNode = (EvalNode)it2.next();
          fathernode.sons.remove(curNode);
          this.PSMap.remove(curNode);
          newCollectedNode.wordForms.addAll(curNode.wordForms);
          this.PSMap.put(curNode.wordForms.get(0),newCollectedNode);
        }
        fathernode.addSon(newCollectedNode);
        collectedNodes = new Vector();//
        continue;                     //
      }                               //
      EvalNode curLowerNode = null;
      if ( this.PSMap.containsKey(curWord) )
      {
        curLowerNode = (EvalNode)this.PSMap.get(curWord);
        if ( curLowerNode.father == null )
        {
          curLowerNode.father = fathernode;
          fathernode.addSon(curLowerNode);
        }
        else
        {
          System.err.println("Warning: At node ["+fathernode+"] the node ["+curLowerNode+"] is in conflict with ["+curWord+"]");
        }
      }
      else
      {
        curLowerNode = new EvalNode(curWord);
        curLowerNode.father = fathernode;
        fathernode.addSon(curLowerNode);
        this.PSMap.put(curWord, curLowerNode);
      }
      if ( sameNode )                     //
      {                                   //
        collectedNodes.add(curLowerNode); //
      }                                   //
    }
  }

  /**
   * For each of the words tries to find an already existing EvalNode. Puts then
   * all the words into the found one or finally creates a new one, putting the
   * wordforms there.
   *
   * @param psWords
   * @return
   */
  protected EvalNode findOrCreateHypNode(List psWords)
  {
    for ( Iterator it = psWords.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curHyperonym = (ParaSynWord)it.next();
      if ( PSMap.containsKey(curHyperonym) )
      {
        return (EvalNode)this.PSMap.get(curHyperonym);
      }
    }
    EvalNode retNode = new EvalNode();
    for ( Iterator it = psWords.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curHyperonym = ( ParaSynWord )it.next();
      retNode.addWordForm(curHyperonym);
      this.PSMap.put(curHyperonym, retNode);
    }
    return retNode;
  }

  /**
   * Returns a list of ParaSynWord instances from all the words after the :
   * @param line
   * @return
   */
  public List getParaSynWords(String line, String delimiter, int fieldNum)
  {
    List retVec = new Vector();
    String[] s = line.split(delimiter);
    if ( s.length > 1 )
    {
      String[] words = s[fieldNum].trim().split( " " );
      for ( int i = 0; i < words.length; i++ )
      {
        if ( words[i].equals("{") || words[i].equals("}") )
        {
          retVec.add(new ParaSynWord(words[i], new Integer(0)));
          continue;
        }
        Integer number = this.wortliste.getNumber(words[i]);
        if ( number != null && number.intValue() > 0 )
        {
          retVec.add( new ParaSynWord( words[i], number ) );
        }
      }
    }
    return retVec;
  }

  /**
   * Puts each line of the file into a separate entry of a Vector which is then
   * returned. The method String.trim is also called on each line before putting
   * it into the vector.
   * @param filename
   * @return A vector with the file contents
   */
  public static Vector readFile(String fileName)
  {
    Vector retVec = new Vector();
    try
    {
      BufferedReader reader = null;
      if ( fileName != null)
      {
        reader = new BufferedReader(new FileReader(fileName));
      }

      String temp = null;
      while ( reader.ready() )
      {
        temp = reader.readLine().trim();
        if ( temp != null && temp.length() > 0 )
        {
          temp = temp.replaceAll("\t"," ");
          temp = temp.replaceAll("[\\(\\[]","\\{");
          temp = temp.replaceAll("[\\)\\]]","\\}");
          temp = temp.replaceAll( "\\{", " { " );
          temp = temp.replaceAll( "\\}", " } " );
          while ( temp.indexOf("  ") >= 0 )
          {
            temp = temp.replaceAll("  "," ");
          }
          retVec.add(temp);
        }
      }
    }
    catch (Exception e)
    {
      System.out.println("Failed to read file "+fileName);
      //e.printStackTrace();
    }
    return retVec;
  }

  public static EvalTree makeTree(String fileName)
  {
    EvalTreeFactory fac = new EvalTreeFactory(fileName);
    EvalTree tree = new EvalTree(fac.PSMap);
    return tree;
  }

  public static void main(String[] args)
  {
    EvalTreeFactory fac = new EvalTreeFactory("data/psdata/pseval.txt");
    System.out.println("DONE");
  }

}