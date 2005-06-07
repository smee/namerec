package com.bordag.sgz.clustering;

// app specific imports
import com.bordag.sgz.graphen.*;
import com.bordag.sgz.util.*;

// standard imports
import java.util.*;

/**
 * Represents a vector which is used for clustering specifically in this program.
 *
 * Specifically means that these are not just vectors, they have a key which
 * names them and which are sets of words which are merged as clustering
 * proceeds.
 *
 * Contains the value by which the clustering happens as well as the keys and
 * references to the vector to which this and some other vector has been
 * clustered together.
 *
 * Neither the keys nor the values are treated in any way ordered for efficiency
 * purposes
 *
 * @author  Stefan Bordag
 * @date    10.04.2002
 * @see     com.bordag.clustering.TreeClustering
 */
public class ClusterVector implements Cloneable
{

  /**
   * Reference to the keys which produced this vector
   */
  protected Object[] clusterKey = null;

  /**
   * reference to the value on which this clustering is being calculated
   */
  protected Object[] clusterValue = null;

  /**
   * This hash stores the information from which word which vector derived
   */
  protected Hashtable seenWith = null;

  /**
   * The vector to which this and some other have been clustered together
   */
  protected ClusterVector nextUpperVector = null;

  /**
   * Sole constructor, takes a non-null and non 0 length keys along with value
   * under same conditions
   */
  public ClusterVector(Object[] clusterKey, Object[] clusterValue)
  {
    if ( clusterKey == null || clusterValue == null ||
         clusterKey.length < 1 || clusterValue.length < 1 )
    {
      throw new IllegalArgumentException("ClusterVector.ClusterVector(Object[],Object[]) illegal argument");
    }
    this.clusterKey = clusterKey;
    this.clusterValue = clusterValue;
    this.seenWith = new Hashtable();
  }

  /**
   * Sole constructor, takes a non-null and non 0 length keys along with value
   * under same conditions
   */
  public ClusterVector(Object[] clusterKey, Object[] clusterValue, Hashtable seens)
  {
    if ( clusterKey == null || clusterValue == null ||
         clusterKey.length < 1 || clusterValue.length < 1 )
    {
      throw new IllegalArgumentException("ClusterVector.ClusterVector(Object[],Object[]) illegal argument");
    }
    this.clusterKey = clusterKey;
    this.clusterValue = clusterValue;
    this.seenWith = seens;
  }

  /**
   * in der Hashtable sind die wortnummer wie in value gespeichert und als
   * wert wieder eine hashtable, wo die sachgebietswortnummern mit einem
   * Integer als Wert drinstehen
   **/
  public void addSeenWith(ComparableStringBuffer wordNr)
  {
    if ( this.seenWith.containsKey(wordNr) )
    {
      this.seenWith.put(wordNr, new Integer(clusterValue.length + ((Integer)this.seenWith.get(wordNr)).intValue()));
    }
    else
    {
      this.seenWith.put(wordNr, new Integer(clusterValue.length) );
    }
  }

  /**
   *
   **/
  public Hashtable getSeenWith()
  {
    return this.seenWith;
  }

  /**
   * Returns the key array
   */
  public Object[] getKey()
  {
    return clusterKey;
  }

  /**
   * Returns the key array
   */
  public Vector getKeyAsVector()
  {
    return getArrayAsVector(clusterKey);
  }

  /**
   * Returns the value array
   */
  public Object[] getValue()
  {
    return clusterValue;
  }

  /**
   * Returns the value array
   */
  public Vector getValueAsVector()
  {
    return getArrayAsVector(clusterValue);
  }

  private Vector getArrayAsVector(Object[] array)
  {
    Vector retVal = new Vector();
    if ( array == null )
    {
      return retVal;
    }
    for ( int i = 0 ; i < array.length ; i++ )
    {
      retVal.add(array[i]);
    }
    return retVal;
  }

  /**
   * Merges the vector with the given one and returns a new Vector containing
   * the merged result
   */
  public ClusterVector mergeWithVector(ClusterVector vector)
  {
    if ( vector == null )
    {
      return null;
    }
    Object[] newClusterKey = mergeArrays(this.clusterKey, vector.getKey());
    Object[] newClusterValue = mergeArrays(this.clusterValue, vector.getValue());
    Hashtable seens = mergeHashtables(this.seenWith, vector.getSeenWith());
    return new ClusterVector(newClusterKey, newClusterValue, seens);
  }

  private Hashtable mergeHashtables(Hashtable table1, Hashtable table2)
  {
    Hashtable newHash = (Hashtable)table1.clone();
    for ( Enumeration enum = table2.keys() ; enum.hasMoreElements() ; )
    {
      Object curWordNr = enum.nextElement();
      if ( newHash.containsKey(curWordNr) )
      {
        newHash.put(curWordNr, new Integer( ((Integer)newHash.get(curWordNr)).intValue() + ((Integer)table2.get(curWordNr)).intValue() ));
      }
      else
      {
        newHash.put(curWordNr, table2.get(curWordNr));
      }
    }
    return newHash;
  }

  /**
   * Merges the two given arrays eliminating double elements.
   * Might use more memory then expected due to it's temporary HashMap of
   * references.
   * Does not check for null values!
   */
  public static Object[] mergeArrays(Object[] array1, Object[] array2)
  {
    if ( array1 == null || array2 == null ||
         array1.length < 1 || array2.length < 1 )
    {
      throw new IllegalArgumentException("ClusterVector.mergeArrays(Object[],Object[]) illegal argument");
    }
    HashMap table = new HashMap();
    for ( int i = 0 ; i < array1.length ; i++ )
    {
      table.put(array1[i], null);
    }
    for ( int i = 0 ; i < array2.length ; i++ )
    {
      table.put(array2[i], null);
    }
    Object[] newArray = new Object[ table.size() ];
    int i = 0;
    for ( Iterator iterator = table.keySet().iterator() ; iterator.hasNext() ; i++ )
    {
      newArray[i] = iterator.next();
    }
    return newArray;
  }

  /**
   * Represents a good descriptions of the contents of the vector
   */
  public String toString()
  {
    String retString = new String("CV [ ");
    for ( int i = 0 ; i < this.clusterKey.length ; i++ )
    {
      retString = retString + this.clusterKey[i] + " ";
    }
    retString = retString + "] : [ ";
    for ( int i = 0 ; i < this.clusterValue.length ; i++ )
    {
      retString = retString + this.clusterValue[i] + " ";
    }
    retString = retString + "]";
    return retString;
  }

  private String getCliqAndDensity(DBConnection connection, Vector words)
  {
    String retVal = "";
    String show = Options.getInstance().getGenShowClusteringCoeff();
    if ( show.equalsIgnoreCase("1") || show.equalsIgnoreCase("true") ||
         show.equalsIgnoreCase("yes") || show.equalsIgnoreCase("y") )
    {
      SubGraph graph = new SubGraph(connection, words);
      retVal = retVal+"( c = "+graph.calculateCliquishness()+" )";
    }
    return retVal;
  }

  private Hashtable getSeenVector()
  {
    return this.seenWith;
  }

  private String getSeenVectorString(DBConnection connection, Hashtable seenVector)
  {
    String retVal = "";
    for ( Enumeration enum = seenVector.keys() ; enum.hasMoreElements() ; )
    {
      Object key = enum.nextElement();
      Integer value = (Integer)seenVector.get(key);
      retVal = retVal + connection.getWordForNumber((ComparableStringBuffer)key) + "=" + value + " ";
    }
    return retVal;
  }

  /**
   * Represents a good description of the contents of the vector using the
   * connection to resolve wordnumbers
   */
  public String toString(DBConnection connection)
  {
    String retString = new String("CV [ ");
    ComparableStringBuffer[] keys = connection.getWordsForNumbers(this.clusterKey);
    for ( int i = 0 ; i < keys.length ; i++ )
    {
      retString = retString + "\"" + keys[i] + "\" ";
    }
    retString = retString + "] : [ ";
    ComparableStringBuffer[] values = connection.getWordsForNumbers(this.clusterValue);
    for ( int i = 0 ; i < values.length ; i++ )
    {
      retString = retString + "\"" + values[i] + "\" ";
    }
    retString = retString + "]";
    return retString;
/*    for ( int i = 0 ; i < this.clusterKey.length ; i++ )
    {
      retString = retString + "\"" + connection.getWordForNumber((ComparableStringBuffer)this.clusterKey[i]) + "\" ";
    }
    retString = retString + "] : [ ";
    for ( int i = 0 ; i < this.clusterValue.length ; i++ )
    {
      retString = retString + "\"" + connection.getWordForNumber((ComparableStringBuffer)this.clusterValue[i]) + "\" ";
    }
    retString = retString + "]";
    retString = retString + " " + getCliqAndDensity(connection, getKeyAsVector());
    retString = retString + " : " + getCliqAndDensity(connection, getValueAsVector());
    String getSeenVector = getSeenVectorString(connection, getSeenVector());
    if ( getSeenVector.length() > 0 )
    {
      retString = retString + " v = { " + getSeenVectorString(connection,getSeenVector()) + "} ";
    }
    return retString;*/
  }

  /**
   * testing
   */
  public static void main(String argv[])
  {
    Object[] k1 = new Object[1];
    k1[0] = "1";
    Object[] o1 = new Object[3];
    o1[0] = "A";
    o1[1] = "B";
    o1[2] = "C";
    Object[] k2 = new Object[1];
    k2[0] = "2";
    Object[] o2 = new Object[3];
    o2[0] = "D";
    o2[1] = "E";
    o2[2] = "C";
    ClusterVector c1 = new ClusterVector(k1,o1);
    ClusterVector c2 = new ClusterVector(k2,o2);
//    System.out.println("Similaroty of c1 and c2 is : "+TreeClustering.calcSimilarityOf(c1,c2));
    ClusterVector mc = c1.mergeWithVector(c2);
    System.out.println(mc.toString());
  }
}
