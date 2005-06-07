package com.bordag.paraexp;

import java.util.*;

/**
 * Just overrides the standard HashMap in order to not write over and over the
 * casts
 * The keys here are ParaSynWords and the values doubles
 *
 * @author Stefan Bordag
 * @date 25.10.2003
 */
public class ParaSynWordHashMap implements Map,java.io.Serializable
{

  // this variable tells how many fields in the put method to expect from
  // the values
  protected myComparator comp = null;
  protected SortedSet words = null;
  protected Map ratingMap = null;

  public ParaSynWordHashMap(int direction)
  {
    this.words = new TreeSet(new myComparator(direction));
    this.ratingMap = new HashMap();
 }

  public ParaSynWordHashMap()
  {
    this.words = new TreeSet(new myComparator(-1));
    this.ratingMap = new HashMap();
 }

  public void putAll(Map map)
  {
    this.ratingMap.putAll(map);
    this.words.addAll(map.keySet());
  }
  public Set keySet()
  {
    return this.words;
  }
  public Set entrySet()
  {
    return this.ratingMap.entrySet();
  }
  public Collection values()
  {
    return this.ratingMap.values();
  }
  public void clear()
  {
    this.ratingMap.clear();
  }
  public Object remove(Object o)
  {
    Object o2 = this.ratingMap.remove(o);
    this.words.remove(o);
    return o2;
  }

  public Object put(Object key, Object value)
  {
    return put((ParaSynWord)key,(Double)value);
  }
  public Object put(ParaSynWord word, Double value)
  {
    if ( value == null )
    {
      throw new IllegalArgumentException("Wrong usage of PSWordHashMap, value: "+value);
    }
    Object o = this.ratingMap.put(word, value);
    this.words.add(word);
    return o;
  }

  public Object get(Object key)
  {
    return get((ParaSynWord)key);
  }
  public Double get(ParaSynWord word)
  {
    return (Double)this.ratingMap.get(word);
  }

  public boolean containsValue(Object val)
  {
    return this.ratingMap.containsValue(val);
  }
  public boolean containsKey(Object val)
  {
    return this.ratingMap.containsKey(val);
  }
  public boolean isEmpty()
  {
    return this.ratingMap.isEmpty();
  }
  public int size()
  {
    return this.ratingMap.size();
  }

  /**
   * Returns -1 or the ranking of the given word in this list
   * @param word
   * @param column
   * @return
   */
  public int getRankingOf(ParaSynWord word)
  {
    int count = 0;
    for ( Iterator it = this.words.iterator() ; it.hasNext() ; count++ )
    {
      ParaSynWord curWord = (ParaSynWord)it.next();
      if ( word.equals(curWord) )
      {
        return count;
      }
    }
    return -1;
  }

  public ParaSynWord getWinner()
  {
//    System.out.println("get winner on size: "+this.words.size());
    if ( this.words.size() < 1 )
    {
      return null;
    }
    return (ParaSynWord)this.words.first();
  }

  public String toStringOrderedFirstNBy(int n)
  {
    String retString = "PSWordHashMapOrdered : \n";
    if ( n < 1 || this.words.size() < 1 )
    {
      return retString+"<<empty>>\n";
    }
    int count = 0;
    for ( Iterator it = this.words.iterator() ; it.hasNext() && count <= n ; count++)
    {
      ParaSynWord curWord = (ParaSynWord)it.next();
      retString=retString + curWord + " " + this.get(curWord) +"\n";
    }
    return retString;
  }

  public String toStringOrderedBy(int column)
  {
    return toString();
  }

  public String toString()
  {
    String retString = "PSWordHashMapOrdered : \n";
    if ( this.words.size() < 1 )
    {
      return retString+"<<empty>>\n";
    }
    for ( Iterator it = this.words.iterator() ; it.hasNext() ; )
    {
      ParaSynWord curWord = (ParaSynWord)it.next();
      retString=retString + curWord + "" + this.get(curWord) + "\n";
    }
    return retString;
  }

  /**
   * Compares keys of this TreeMap
   */
  class myComparator implements Comparator
  {
    protected int direction = 1;
    public static final int ASC = 1;
    public static final int DESC = -1;

    public myComparator(int direction)
    {
      this.direction = direction;
    }

    public int compare(Object o1, Object o2)
    {
      if ( o1 instanceof ParaSynWord && o2 instanceof ParaSynWord )
      {
        double val1 = ((Double)ratingMap.get((ParaSynWord)o1)).doubleValue();
        double val2 = ((Double)ratingMap.get((ParaSynWord)o2)).doubleValue();
        if ( val1 > val2 )
        {
          return 1*this.direction;
        }
        else if ( val1 < val2 )
        {
          return -1*this.direction;
        }
        else
        {
          return 0;
//          return ((ParaSynWord)o1).compareTo((ParaSynWord)o2);
        }
      }
      return 0;
    }

    public boolean equals(Object o)
    {
      if ( o instanceof myComparator )
      {
        return true;
      }
      return false;
    }
  }

}
