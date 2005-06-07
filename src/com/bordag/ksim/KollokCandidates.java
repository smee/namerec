package com.bordag.ksim;

import java.util.*;

/**
 * Keeps track of possible words which could have a non-zero value of similarity.
 * Basically these are all direct collocations and their collocations.
 * Returns these in batches, of given size.
 * Uses the bitfield in order to recognize already calculated pairs to not
 * calculate (and print) them twice
 *
 * @author Stefan Bordag
 * @date   15.11.2003
 */
final public class KollokCandidates
{
  protected Integer wordNr = null;

  /**
   * This bitfield contains information about which words were already done
   * earlier so that they don't have to be calculated again
   */
  protected BitField doneNumbers = null;

  /**
   * This bitfield contains information for the current word, which collocations
   * of collocations have already been passed as cadidates in order to not do
   * them again and again for each now collocation of the inputword.
   */
  protected BitField doubletteNumbers = null;

  protected DBUtil dbUtil = null;

  protected List collocs = null;

  protected List candidates = null;

  // if this is set to false, it will ignore the collocations of collocations
  protected boolean takeCollocOfColloc = true;

  public KollokCandidates(Integer wordNr, BitField doneNumbers, DBUtil dbUtil, boolean takeCollocOfColloc)
  {
    this.wordNr = wordNr;
    this.doneNumbers = doneNumbers;
    if ( takeCollocOfColloc )
    {
      this.doubletteNumbers = new BitField( this.doneNumbers.size() );
    }
    this.dbUtil = dbUtil;
    this.takeCollocOfColloc = takeCollocOfColloc;
    this.collocs = new ArrayList(KSimOptions.getInstance().getBatchSize());
    this.candidates = new ArrayList(KSimOptions.getInstance().getBatchSize());
    // fetch colls of wordNr and put them both in collocs and in candidates
    // then later the can be batchwise removed from the candidates while keeping
    // them in collocs. rom there they are only removed one by one as their
    // collocs then are fetched batchwise
    this.collocs.addAll(fetchInitialCollocs(this.wordNr));
    this.collocs = removeDoneNrs(this.collocs);
    this.candidates.addAll(this.collocs);
  }

  /**
   * Looks up in the bitfield, which numbers are done and returns the list
   * without these
   * @return
   */
  protected List removeDoneNrs(List toBeCleared)
  {
    List retVec = new ArrayList(toBeCleared.size());
    for ( Iterator it = toBeCleared.iterator() ; it.hasNext() ; )
    {
      Integer curInteger = (Integer)it.next();
      if ( this.doneNumbers.getPosition(curInteger.intValue()) == false )
      {
        retVec.add(curInteger);
      }
    }
    return retVec;
  }

  /**
   * The algorithm starts off by getting the normal collocations from the DB
   * @param wordNr
   * @return
   */
  protected List fetchInitialCollocs(Integer wordNr)
  {
    List retVec = new ArrayList(KSimOptions.getInstance().getBatchSize());
    retVec.addAll( dbUtil.getCollocations(wordNr, KSimOptions.getInstance().getMinSignifikance(), KSimOptions.getInstance().getMaxKollokationen1(), KSimOptions.getInstance().getMinWordNr()) );
    if ( this.doubletteNumbers != null )
    {
      for ( Iterator it = retVec.iterator(); it.hasNext(); )
      {
        this.doubletteNumbers.setPosition( ( ( Integer )it.next() ).intValue(), true );
      }
    }
    return retVec;
  }

  public List getCollocs()
  {
    return this.collocs;
  }

  public List getCurCandidates()
  {
    return this.candidates;
  }

  /**
   * Returns the next batch of candiadates. If there are not enough left,
   * tries to produce new ones from the collocs list, removing used items
   * from collocs list
   * @param batchSize
   * @return
   */
  public List getNextBatch(int batchSize)
  {
    List retVec = new ArrayList();
    if ( this.candidates.size() < batchSize && this.takeCollocOfColloc )
    {
      // call addFurtherCandiadates(batchSize)
      List furtherCandidates = fetchFurtherCandidates(batchSize);
      // adds new candidates if and only if the have not already been seen
      if ( this.doubletteNumbers != null )
      {
        for ( Iterator it = furtherCandidates.iterator(); it.hasNext(); )
        {
          Integer curWordNr = ( Integer )it.next();
          if ( !this.doubletteNumbers.getPosition( curWordNr.intValue() ) )
          {
            this.candidates.add( curWordNr ); // adding here
            this.doubletteNumbers.setPosition( curWordNr.intValue(), true );
          }
        }
      }
      else
      {
        this.candidates.addAll(furtherCandidates);
      }
    }
    if ( !this.takeCollocOfColloc ) // if fetchFurther is never called which
      // removes collocations, we have to do it here
    {
      splitXOff(this.collocs, batchSize);
    }
    return removeDoneNrs(splitXOff(this.candidates, batchSize));
  }

  /**
   * Returns the first numItems elements of the given ArrayList, removing
   * them from this ArrayList
   * @param longerList
   * @param numItems
   * @return
   */
  protected List splitXOff(List longerList, int numItems)
  {
    List retVec = new ArrayList(numItems);
    Iterator it = longerList.iterator();
    for ( int i = 0 ; it.hasNext() && i < numItems ; i++ )
    {
      retVec.add(it.next());
    }
    longerList.removeAll(retVec);
    return retVec;
  }

  /**
   * Gets collocations of next "batchSize" items from collocs list
   * Removes these items from collocs list.
   * @param batchSize
   * @return
   */
  protected List fetchFurtherCandidates(int batchSize)
  {
    List nextCollocsToFetch = splitXOff(this.collocs, batchSize);
    return this.dbUtil.getCollocationsStack(nextCollocsToFetch, KSimOptions.getInstance().getMinSignifikance(), KSimOptions.getInstance().getMaxKollokationen12(), KSimOptions.getInstance().getMinWordNr());
  }

  public boolean hasNextBatch()
  {
    if ( this.candidates.size() < 1 && this.collocs.size() < 1 )
    {
      return false;
    }
    return true;
  }

}