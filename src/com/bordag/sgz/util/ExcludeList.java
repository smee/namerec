package com.bordag.sgz.util;

// standard imports
import java.util.*;
import java.io.*;

/**
 * This class represents an exclude list. It provides methods to check
 * arrays which returns new arrays containing filtered items, that means
 * which are not in the exclude list.
 *
 * Can read a file at initialization time or can be created empty and
 * filled with time
 *
 * Note: All the items you put in here should override the equals and
 * the hashval methods in order to not be compared simply by the hashvalue
 * but by what they mean instead. see ComparableStringBuffer
 *
 * @author   Stefan Bordag
 * @date     11.04.2002
 **/
public class ExcludeList
{

  protected HashSet items = null;

  /**
   * Initializes an empty instance of an Exclude list and if not filled
   * with items acts like a null filter.
   **/
  public ExcludeList()
  {
    init();
  }

  /**
   * Creates an instance of an ExcludeList which at initialization time
   * reads the items given in the filename. If reading file fails,
   * initializes empty.
   **/
  public ExcludeList(String fileName)
  {
    init();
    if ( fileName != null && fileName.length() > 0 )
    {
      fillItemsFromFile(fileName);
    }
  }

  /**
   * Default initializations
   **/
  private void init()
  {
    this.items = new HashSet();
  }

  /**
   * Tries to read the given file and fill the items set with the elements
   * read from there
   * Note: Reads the first string off every line up to the next space
   **/
  private void fillItemsFromFile(String fileName)
  {
    BufferedReader reader = null;
    int i = 0;
    try
    {
      reader = new BufferedReader(new FileReader(fileName));

      String line = reader.readLine();
      while ( line != null )
      {
        if ( line == null || line.length() < 1 )
        {
          line = reader.readLine();
          continue;
        }
        StringTokenizer tokenizer = new StringTokenizer(line," ");
        ComparableStringBuffer buffer = new ComparableStringBuffer(tokenizer.nextToken());
        if ( buffer != null )
        {
          addItem(buffer);
        }
        line = reader.readLine();
        i++;
      }
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }
//--    Output.println("Read "+i+" exclude items from file "+fileName);
  }

  /**
   * Returns a new HashSet containig only elements which weren't in the
   * internat exclude list
   **/
  public HashSet filterSet(HashSet toBeFiltered)
  {
    HashSet retVal = new HashSet();
    if ( this.items == null || this.items.size() < 1 ||
         toBeFiltered == null || toBeFiltered.size() < 1 )
    {
      return toBeFiltered;
    }
    for ( Iterator it = toBeFiltered.iterator() ; it.hasNext() ; )
    {
      Object curObject = it.next();
      if ( ! this.items.contains(curObject) )
      {
        retVal.add(curObject);
      }
    }
    return retVal;
  }

  /**
   * Returns whether the given item is contained in the set of items
   **/
  public boolean contains(Object item)
  {
    if ( item == null )
    {
      return false;
    }
    return this.items.contains(item);
  }

  /**
   * Adds a set of items
   **/
  public void addItems(Object[] items)
  {
    if ( items == null || items.length < 1 )
    {
      return;
    }
    for ( int i = 0 ; i < items.length ; i++ )
    {
      this.items.add(items[i]);
    }
  }

  /**
   * Adds a set of items
   **/
  public void addItems(HashSet items)
  {
    if ( items == null || items.size() < 1 )
    {
      return;
    }
    this.items.addAll(items);
  }

  /**
   * Adds a new item to the exclude list
   **/
  public void addItem(Object item)
  {
    if ( item != null )
    {
      this.items.add(item);
    }
  }

  public HashSet getItems()
  {
    return this.items;
  }

  /**
   * Prints out the whole list (mainly for debugging purposes)
   **/
  public void printAllItems()
  {
    Output.println("Exclude list contains following elements: ");
    for( Iterator it = this.items.iterator() ; it.hasNext() ; )
    {
      Output.print(it.next()+" ");
    }
  }

  /**
   * Test whether this class works properly
   **/
  public static void main(String argv[])
  {
    ExcludeList list = new ExcludeList(Options.getInstance().getGenStopwortFile());
//    list.printAllItems();
    HashSet set = new HashSet();
    set.add(new ComparableStringBuffer("74607"));
    set.add(new ComparableStringBuffer("0"));
    System.out.println("size before is "+set.size());
    HashSet set2 = list.filterSet(set);
    System.out.println("size after is "+set2.size());
  }
}
