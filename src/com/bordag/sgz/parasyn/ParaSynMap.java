package com.bordag.sgz.parasyn;

import java.util.*;
import java.awt.geom.*;

import com.bordag.sgz.util.*;

/**
 * <p>
 * Instances of this class represent a word and a comparison of its
 * collocations with the collocations of the collocations.
 * </p>
 * <p>
 * It will fetch the collocations and their collocations on construction and
 * then flush the connection cache. Then it will do it's calculations and then
 * be ready for comparisons.
 * </p>
 *
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * @author Stefan Bordag
 */
public class ParaSynMap
{
  /**
   * The number of the word which is represented by this map
   */
  protected Integer wordNr = null;

  /**
   * This is actually the map which stores for each wordnumber the information
   * in an ParaSunInfo Object
   */
  protected Hashtable myMap = null;

  /**
   * This map contains ParaSynMap objects for the disambiguated versions of the
   * word represented by myMap
   */
  protected Hashtable myDisambMap = null;

  /**
   * The connection from where to fetch collocations
   */
  protected CachedDBConnection connection = null;


  /**
   * The two fields define the region from where word numbers will be dropped
   * in any case in order to reduce datasize by removing anyways uninteresting
   * data.
   */
  protected Polygon pruneRegion = null;
  protected static final double[] pruneRegionEdges = {-1.0,-1.0, -1.0,0.5, 0.4,0.5, 0.5,0.4, 0.5,-1.0, -1.0,-1.0};

  /**
   * Checks whether all arguments are okay and creates this instance then
   * @param word a non-null or zero-length word which should be in the DB or
   * and Exception will be thrown
   * @param connection The connection to the DB without which this instance
   * doesn't make sense.
   */
  public ParaSynMap(String word, CachedDBConnection connection) throws Exception
  {
    if ( connection == null || word == null || word.length() < 1 )
    {
      throw new IllegalArgumentException("In ParaSynMap.ParaSynMap(String word, CachedDBConnection connection) arguments are null or so. arguments were: word=["+word+"] connection=["+connection+"]");
    }
    this.connection = connection;
    this.wordNr = new Integer(this.connection.getNumberForWord(new ComparableStringBuffer(word)).toString());
    if ( this.wordNr == null || this.wordNr.intValue() < 0 )
    {
      throw new Exception("In ParaSynMap.ParaSynMap(String word, CachedDBConnection connection) the word ["+word+"] had no wordnumber!");
    }
    init();
  }

  /**
   * Assumes the the wordNr which should be anywhere between 0 and 10Mio is in
   * the DB and doesn't check it!
   * @param wordNr
   * @param connection
   */
  public ParaSynMap(Integer wordNr, CachedDBConnection connection)
  {
    if ( connection == null || wordNr == null || wordNr.intValue() < 0 || wordNr.intValue() > 10000000 )
    {
      throw new IllegalArgumentException("In ParaSynMap.ParaSynMap(Integer wordNr, CachedDBConnection connection) arguments are null or so. arguments were: wordNr=["+wordNr+"] connection=["+connection+"]");
    }
    this.connection = connection;
    this.wordNr = wordNr;
    init();
  }

  /**
   * This is only an inner constructor and should not be accessed from the
   * outside.
   * It is used to hold information about the disambiguation of the given word
   * and to be able to work with it.
   * @param wordNr
   * @param paraSynInfoObjects
   * @param connection
   */
  protected ParaSynMap(Integer wordNr, Hashtable paraSynInfoObjects, CachedDBConnection connection)
  {
    this.connection = connection;
    this.wordNr = wordNr;
    this.myMap = paraSynInfoObjects;
    normalizeNumbers();
    pruneMyMap();
  }

  private void init()
  {
    System.out.println("Fetch collocations, cache their collocations. ["+this.wordNr+"]");
    fillMyMap();
    System.out.println("clearing cache");
    this.connection.clearCache();

    // now retrieve the disambiguation
    //System.out.println("Retrieving disambiguation of word.");
    //disambiguate();

    System.out.println("Normalize numbers");
    normalizeNumbers();

    System.out.println("Prune Map");
    pruneMyMap();

  }

  public Integer getMyWordNr()
  {
    return this.wordNr;
  }

  /**
   * Fetches collocations, creates for each a ParaSynInfo object and fills it
   * with proper data
   */
  private void fillMyMap()
  {
    this.myMap = new Hashtable();
    // first get all collocations in a vector of two-element objects 1. wordnr 2. sig
System.out.println("Getting collocations");
    Vector collocations = getCollocations(this.wordNr);
System.out.println("Old: I've got "+collocations.size()+" collocations");
    // get string of all collocations into a hashtable
    ComparableStringBuffer[] strings = getWordStrings(collocations);

System.out.println("create infoObjects");
    // then go and create the Info objects befilling them with the two
    // numbers and the wordString taken from the Hashtable
    int i = 0;
    for ( Iterator it = collocations.iterator() ; it.hasNext() ; i++ )
    {
      Integer[] curPair = (Integer[])it.next();
//      System.out.println("Processing "+strings[i]+" with pair ["+curPair[0]+"]  ["+curPair[1]+"]");
      double loggedSig = 0.0;
      if ( curPair[1].doubleValue() > loggedSig )
      {
        loggedSig = Math.log( (double)curPair[1].doubleValue() );
      }
//System.out.println("Old: normalizing "+strings[i].toString()+":"+curPair[1]+" to "+loggedSig);
      ParaSynInfo curInfo = new ParaSynInfo(curPair[0], strings[i].toString(), loggedSig, 0.0);
//      System.out.println("curInfo = "+curInfo);
      this.myMap.put(curPair[0], curInfo);
//System.out.println("Old1.5: "+curInfo.getX());
    }

System.out.println("cache collocations of collocations");
    // now cache collocations of collocations
    Vector collocOnly = new Vector(collocations.size());
    for ( Iterator it = collocations.iterator() ; it.hasNext() ; )
    {
      Integer[] curPair = (Integer[])it.next();
      collocOnly.add(curPair[0]);
    }
    try
    {
      this.connection.cacheCollocationsOf(new ComparableStringBuffer(this.wordNr.toString()), collocOnly);
    }
    catch ( Exception ex )
    {
      System.err.println("Exception while caching collocations, can't continue.");
      ex.printStackTrace();
      System.exit(1);
    }


System.out.println("calculate y-values");
    // and go through the map, calculating the y-values
    for ( Iterator it = collocOnly.iterator() ; it.hasNext() ; )
    {
      Integer curWordNr = (Integer)it.next();
      ComparableStringBuffer[][] collocs = this.connection.getTriAssociatedOf(new ComparableStringBuffer(curWordNr.toString()));
      Vector curCollocVec = getCompBufArrayAsIntegerVector(collocs);
      // Here we compare them
      int equalCount = countMatchingElements(collocOnly, curCollocVec);
      ParaSynInfo p = (ParaSynInfo)this.myMap.get(curWordNr);
//System.out.println("Old: For word "+p.wordString+" I've got "+curCollocVec.size()+" collocations");
System.out.print("Old4: Word: "+p.getWordString()+" having "+curCollocVec.size()+" collocations has count: "+equalCount+" log: ");
      double loggedEqualCount = 0.0;
      if ( equalCount > loggedEqualCount )
      {
        loggedEqualCount = Math.log( (double)equalCount );
      }
      p.setY(loggedEqualCount);
      System.out.println(p.getY()+"");
      this.myMap.put(curWordNr, p);
    }
  }

  /**
   * The numbers are not yet scaled logarithmically and are not yet between 0
   * and 1 which is being done here.
   */
  private void normalizeNumbers()
  {
    Hashtable tempTable = new Hashtable();
    double[] maximums = getMaximums();
    // calculate maximums here
    for ( Enumeration enum = this.myMap.keys() ; enum.hasMoreElements() ; )
    {
      Integer curKey = (Integer)enum.nextElement();
      ParaSynInfo curP = (ParaSynInfo)this.myMap.get(curKey);
      ////System.out.print("Old5: normalizing "+curP.getWordString()+":"+curP.getY()+" to ");
      curP.setX( curP.getX() / maximums[0] );
      curP.setY( curP.getY() / maximums[1] );
      ////System.out.println(curP.getY()+" maximum was: "+maximums[1]);
      tempTable.put(curKey, curP);
    }
    this.myMap = tempTable;
  }


  /**
   * Gets data from the disambiguated collocations and create a new map for
   * each meaning (and even for the non-meaning one!) which contains only its
   * collocation.
   * Note: should be done before normalization in order for normalization to
   * normalize each map on its own meaningmaximums
   */
  protected void disambiguate()
  {
    Vector disambVec = getDisambiguation(this.wordNr);
    // put vectors of ParaSynInfo objects into this vector newMaps
    Hashtable newMaps = new Hashtable();
    for ( Iterator it = disambVec.iterator() ; it.hasNext() ; )
    {
      Integer[] curDis = (Integer[])it.next();
      // if we know this wordnumber
      if ( this.myMap.get( curDis[0] ) != null )
      {
        Hashtable t = null;
        if ( newMaps.containsKey(curDis[1]) )// and the meaning number already occured
        {
          t = (Hashtable)newMaps.get(curDis[1]);
        }
        else   // this meaning number has not yet been seen, so put the currently
        {      // referenced word into the newMaps
          t = new Hashtable();
        }
        t.put(curDis[0],this.myMap.get(curDis[0]));
        newMaps.put(curDis[1], t);
      }
    }
    // then for each vector in newMaps create a new ParaSynMap
    this.myDisambMap = new Hashtable();
    for ( Enumeration enum = newMaps.keys() ; enum.hasMoreElements() ; )
    {
      Integer curKey = (Integer)enum.nextElement();
      Hashtable curHash = (Hashtable)newMaps.get(curKey);
      this.myDisambMap.put(curKey, new ParaSynMap(this.wordNr, curHash ,this.connection));
    }
System.out.println("myDisambMap looks like now this: "+this.myDisambMap);
    // how to make the other maps available?
    // - in the access functions always specify which meaning and also provide
    // a method which tells how many meanings there are
  }

  /**
   * If this is called on the root object, this returns the number of meanings
   * the given represents.
   *
   * @return 0 or more where 0 means that there is at least the non-assignable
   * words or -1 if this has been accidently called on a leaf-object.
   */
  public int getNumOfMeanings()
  {
    if ( this.myDisambMap != null )
    {
      return this.myDisambMap.size();
    }
    return -1;
  }

  /**
   * Returns the number of elements which were in both of the vectors.
   * Assumes that the elements are comparable (i.e. both Integers or so)
   * @param v1
   * @param v2
   * @return
   */
  public static int countMatchingElements(Vector v1, Vector v2)
  {
    int retVal = 0;
    Vector smallerVector = v2;
    Vector biggerVector = v1;
    if ( v1.size() < v2.size() )
    {
      smallerVector = v1;
      biggerVector = v2;
    }
    for ( Iterator it = smallerVector.iterator() ; it.hasNext() ; )
    {
      if ( biggerVector.contains( it.next() ) )
      {
        retVal++;
      }
    }
    return retVal;
  }

  /**
   * Transforms output of database into something more useful, namely into a
   * vector containing the numbers already as integers in the same order
   * as they came from the database.
   * @param buf
   * @return
   */
  protected Vector getCompBufArrayAsIntegerVector(ComparableStringBuffer[][] buf)
  {
    Vector retVec = new Vector(buf.length);
    for ( int i = 0 ; i < buf.length ; i++ )
    {
      retVec.add(new Integer(buf[i][0].toString()));
    }
    return retVec;
  }

  /**
   * Retrieves the collocations of a given word(number)
   */
  protected Vector getCollocations(Integer word)
  {
    Vector retVec = new Vector();
    String[] s = new String[4];
    s[0] = Options.getInstance().getParaMinSignifikanz();
    s[1] = Options.getInstance().getParaMinWordNr();
    s[2] = word.toString();
    s[3] = Options.getInstance().getParaMaxKollokationen();
    String query = Options.getInstance().getParaQueryKollokationen();
    ComparableStringBuffer[][] buffer = this.connection.getResultsOf(query,s);
    if ( buffer == null || buffer.length < 1 )
    {
      System.err.println("Wordnr ["+word+"]: had no collocations!");
      return retVec;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        Integer[] val = new Integer[2];
        val[0] = new Integer(buffer[i][0].toString());
        val[1] = new Integer(buffer[i][1].toString());
        val[1] = new Integer( val[1].intValue() - new Integer( Options.getInstance().getParaMinSignifikanz() ).intValue() );
        if ( ! this.wordNr.equals(val[0]) )
        {
          retVec.add(val);
        }
      }
    }
    return retVec;
  }

  /**
   * Retrieves the collocations of a given word(number)
   */
  protected Vector getDisambiguation(Integer word)
  {
    Vector retVec = new Vector();
    String[] s = new String[2];
    s[0] = Options.getInstance().getParaMinWordNr();
    s[1] = word.toString();
    String query = Options.getInstance().getParaQueryDisambig();
    System.out.println("Query is ["+query+"]");
    ComparableStringBuffer[][] buffer = this.connection.getResultsOf(query,s);
    if ( buffer == null || buffer.length < 1 )
    {
      System.err.println("Wordnr ["+word+"]: had no collocations!");
      return retVec;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        Integer[] val = new Integer[2];
        val[0] = new Integer(buffer[i][0].toString());
        val[1] = new Integer(buffer[i][1].toString());
        if ( ! this.wordNr.equals(val[0]) )
        {
          retVec.add(val);
        }
      }
    }
    return retVec;
  }


  /**
   * Assumes the Vector to be a vector of two-elements Integers and
   * takes the first element as wordnrs and resolves them into words,
   * return the array.
   * @param collocations
   * @return
   */
  protected ComparableStringBuffer[] getWordStrings(Vector collocations)
  {
    ComparableStringBuffer[] wordNrs = new ComparableStringBuffer[collocations.size()];
    int i = 0;
    for ( Iterator it = collocations.iterator() ; it.hasNext() ; i++ )
    {
      wordNrs[i] = new ComparableStringBuffer( ((Integer[])it.next())[0].toString() );
    }
    return getWordsForNumbers(wordNrs);
  }

  /**
   * Returns the strings of words (because the one in DBConnection orders them,
   * thus loosing the connection to the wordnumber
   * @param numbers
   * @return
   */
  public ComparableStringBuffer[] getWordsForNumbers(Object[] numbers)
  {
    if ( numbers == null || numbers.length < 1 )
    {
      return null;
    }

    String queryBegin = "(select w.wort_bin from wortliste w where ";
    String queryMid1   = "w.wort_nr=";
    String queryMid2   = ") union all ( select w.wort_bin from wortliste w where ";
    String queryEnd    = " );";
    String query = queryBegin;
    for ( int i = 0 ; i < numbers.length ; i++ )
    {
      query = query + "" + queryMid1 + numbers[i].toString();
      if ( i < numbers.length - 1 )
      {
        query = query + queryMid2;
      }
    }
    query = query + "" + queryEnd;
    ComparableStringBuffer[][] temp = this.connection.getResultsOf(query);
    ComparableStringBuffer[] temp2 = new ComparableStringBuffer[temp.length];
    if ( temp != null && temp.length > 0 && temp[0].length > 0 )
    {
      for ( int i = 0 ; i < temp.length ; i++ )
      {
        temp2[i]=temp[i][0];
      }
    }
    return temp2;
  }

  /**
   * Returns the maximum of both X and Y from the current map
   * @return
   */
  public double[] getMaximums()
  {
    double[] maximums = new double[2];
    for ( Enumeration enum = this.myMap.elements() ; enum.hasMoreElements() ; )
    {
      ParaSynInfo curP = (ParaSynInfo)enum.nextElement();
      maximums[0] = Math.max( maximums[0], curP.getX() );
      maximums[1] = Math.max( maximums[1], curP.getY() );
    }
    return maximums;
  }

  /**
   * Using the prunePolygon, this map is being pruned
   */
  private void pruneMyMap()
  {
    // create the pruning region
    this.pruneRegion = new Polygon(this.pruneRegionEdges);
    // now prune it
    Hashtable newMap = new Hashtable((int)((double)this.myMap.size()/2.0));
    for ( Enumeration enum = this.myMap.keys() ; enum.hasMoreElements() ; )
    {
      Object curKey = enum.nextElement();
      ParaSynInfo curInfo = (ParaSynInfo)this.myMap.get(curKey);
      if ( this.pruneRegion.outside(curInfo.getX(), curInfo.getY()) )
      {
        newMap.put(curKey, curInfo);
      }
    }
    this.myMap = newMap;
 }


  /**
   * Returns the wordNrs which are in the region. The region is understood to
   * be quadratic and 1 long and 1 height. That means that
   * 0.5;0.5 1;0.5 1;1 0.5;1 gives the upper right quarter of the region.
   *
   * @param region The region from which to return wordNrs
   * @return returns a possibly empty Hashset of word numbers (never returns
   * null)
   */
  public HashSet getWordNrsFrom(Polygon region)
  {
    HashSet retSet = new HashSet();
    for ( Enumeration enum = this.myMap.elements() ; enum.hasMoreElements() ; )
    {
/*      ParaSynInfo curP = (ParaSynInfo)enum.nextElement();
      if ( region.inside(curP.getX(), curP.getY()) )
      {
        retSet.add(curP);
      }*/
    }
    return retSet;
  }

  /**
   * Returns the ParaSynInfo objects which are in the region. The region is
   * understood to be quadratic and 1 long and 1 height. That means that
   * 0.5;0.5 1;0.5 1;1 0.5;1 gives the upper right quarter of the region.
   *
   * @param region The region from which to return wordNrs
   * @return returns a possibly empty Hashset of ParaSynSet (never returns
   * null)
   */
  public HashSet getParaSynInfos(Polygon region)
  {
    HashSet retSet = new HashSet();
    for ( Enumeration enum = this.myMap.elements() ; enum.hasMoreElements() ; )
    {
      ParaSynInfo curP = (ParaSynInfo)enum.nextElement();
      if ( region.inside(curP.getX(), curP.getY()) )
      {
        retSet.add(curP);
      }
    }
    return retSet;
  }

  /**
   * Gives a first glance on what possibly linguistic collocations might be in
   * this map
   * @return
   */
  public HashSet getLinguisticCollocations()
  {
    double[] curPoly = {0.6,-1.0, 1.5,-1.0, 1.5,1.0, 0.6,0.5, 0.6,-1.0};
    return getParaSynInfos(new Polygon(curPoly));
  }

  public HashSet getCohyponyms()
  {
    double[] curPoly = {0.3,0.2, 0.2,0.3, 1.5,1.0, 1.0,1.5, 0.3,0.2};
    return getParaSynInfos(new Polygon(curPoly));
  }

  public HashSet getHyperonyms()
  {
    double[] curPoly = {-1.0,0.6, -1.0,1.5, 1.0,1.5, 0.5,0.6, -1.0,0.6};
    return getParaSynInfos(new Polygon(curPoly));
  }

  /**
   * Returns the precalculated map
   * @return The internal Hashtable which represents actually the map.
   */
  public Hashtable getParaSynMap()
  {
    return this.myMap;
  }

  /**
   * Returns all words from the map, ordered by the distance from the point
   * specified by x and y up to a given threshold. x and y must be somewhere
   * between 0 and 1.
   * @param x
   * @param y
   * @return a Vector containing the wordNrs only(! for now) never returns null;
   */
  public Vector getWordNrsOrderedByDistance(double x, double y, double threshold)
  {
    if ( x < 0.0 || x > 1.0 || y < 0.0 || y > 1.0 )
    {
      throw new IllegalArgumentException("In ParaSynMap.getWordNrsOrderedByDistance(double x, double y, double threshold) arguments out of bounds. Must be between 0 and 1 but were: x="+x+" y="+y);
    }
    return new Vector();
  }

  public String toString()
  {
    return "ParaSynMap ("+this.connection.getWordForNumber( new ComparableStringBuffer( this.wordNr.toString() ) )+"): "+this.myMap+" size="+this.myMap.size();
  }

  public static void main(String[] args)
  {
    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    CachedDBConnection connection = null;
    try
    {
      connection = new CachedDBConnection(url, user, passwd);
      ParaSynMap p = new ParaSynMap("Affe", connection);
      System.out.println("After some processed: "+p);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }

  }

}