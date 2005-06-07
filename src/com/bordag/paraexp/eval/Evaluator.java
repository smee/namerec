package com.bordag.paraexp.eval;

import java.util.*;

import com.bordag.paraexp.*;
import com.bordag.colloc.*;

/**
 * This class makes an evaluation of the paraexp algorithm.
 * Creates an EvalTree instance either from a wordstrings file or (later) from
 * wordnet data
 * Then starts the algorithm for each wordform whose node has a non-null father
 * and performs various counts
 *
 * @author Stefan Bordag
 * @version 1.0
 */

public class Evaluator
{
  EvalTree tree = null;

  public Evaluator(String fileName)
  {
    EvalTree tree = EvalTreeFactory.makeTree(fileName);

    Collocations sigs = new FileColloc("data/ksim/kollok_sig.dump");
    Collocations anz = new FileColloc("data/ksim/kollok_sim_halfcos_anzahl.dump");
    FileWortliste wortliste = new FileWortliste("data/ksim/wortliste.dump");
    FileGrfNAVS gramm = null;//new FileGrfNAVS("data/ksim/grfNAVS.dump");
    ParaSynVerificator v = null;

    double overallCount = 0.0;
    double correctCount = 0.0;
    double overallPossiblesRankingSum = 0.0;
    double overallPossibleCount = 0.0;
    for ( Iterator it = tree.getLeavesInnerNodes() ; it.hasNext() ; )
    {
      ParaSynWord curWord = (ParaSynWord)it.next();
      v = new ParaSynVerificator(curWord.wordNr, sigs, anz, wortliste, gramm);
      System.out.println("For ["+curWord+"] the winner was: ["+v.getResults().getWinner(1)+"]");
      if ( tree.isFatherOf( curWord, v.getResults().getWinner(1)) )
      {
        correctCount+=1.0;
      }
      else
      {
        System.out.println("Calculating nearest place");
        int possible = getNearestPlacing(tree, curWord, v.getResults() );
        if ( possible > -1 )
        {
          overallPossibleCount+=1.0;
          overallPossiblesRankingSum += possible;
        }
      }
      overallCount+=1.0;
    }
    System.out.println(overallCount+" were calculated of which "+correctCount
                       +" were correct, \nPrecision: "+((correctCount/overallCount)*100.0)
                       +"\n Of the "+(overallCount-correctCount)+" incorrect ones "+overallPossibleCount+" were possible with "+(overallPossiblesRankingSum/overallPossibleCount)+" average ranking");
    System.out.println("Options were: "+ParaSynOptions.getInstance());
  }

  /**
   * For each element in tree upwards from the word determines it's ranking in
   * results and returns the place of the best ranking. (So if it's a correct
   * result it should return ranking=1)
   * @param tree
   * @param word
   * @param results
   * @return
   */
  public int getNearestPlacing(EvalTree tree, ParaSynWord word, ParaSynWordHashMap results)
  {
    int bestRanking = Integer.MAX_VALUE;
    System.out.println("Obtaining iterator");
    for ( Iterator it = tree.getAllFathersWordForms(word) ; it.hasNext() ; )
    {
      ParaSynWord curFather = (ParaSynWord)it.next();
      int curRanking = results.getRankingOf(curFather,1);
      if ( curRanking > -1 )
      {
        bestRanking = Math.min(curRanking, bestRanking);
      }
      System.out.println("Best ranking is currently: "+bestRanking);
    }
    if ( bestRanking == Integer.MAX_VALUE )
    {
      return -1;
    }
    return bestRanking;
  }

  public static void main(String[] args)
  {
    Evaluator eval = new Evaluator("data/psdata/pseval.txt");
    System.out.println("Evaluation DONE");
  }

}