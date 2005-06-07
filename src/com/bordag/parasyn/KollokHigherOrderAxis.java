package com.bordag.parasyn;

import java.util.*;

import com.bordag.parasyn.util.*;
import com.bordag.util.*;

/**
 * <p>Title: WortschatzTool</p>
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class KollokHigherOrderAxis extends ValueAxis
{
  DBUtilUncached extraUtil = null;

  public KollokHigherOrderAxis(DBUtilUncached dbUtil)
  {
    super(dbUtil);
    String url = "jdbc:mysql://aspra8.informatik.uni-leipzig.de/kollsk1?user=sbordag&password=ansiBla";
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    DBConnection connection = null;
    try
    {
      System.out.print("Creating extra connection for higher order sigs:");
      connection = new DBConnection(url, user, passwd);
      System.out.print("\t\t\tdone\nCreating util for higher order:");
      this.extraUtil = new DBUtilUncached(connection);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Returns the values for all pairs built from the given nr and the other elements in the list
   * First it gets all values stored from the nr, then matches them with the given ones,
   * defaulting to 0 if there was once something not stored in the db
   * @param nr
   * @param numbers
   * @return values List<Double>
   */
  public List getValues(Integer wordNr, List numbers)
  {
    if ( wordNr == null || wordNr.intValue() < 1 || numbers == null || numbers.size() < 1 )
    {
      return new Vector();
    }
    // FIXME: This block is a workaround for the problem that the wordNumbers don't match
    // get word bin from standard one
    CHString bin = this.dbUtil.getWordForNumber(new CHString(wordNr.toString()));
    // get wordnumber from this one for this wordBin
    wordNr = this.extraUtil.getNumberForWord(bin);
    numbers = translateNumbers(numbers);
    // end of FIXME

    List collocations = this.extraUtil.getCollocations(wordNr, "0");
    HashMap map = new HashMap();
    for ( Iterator it = collocations.iterator() ; it.hasNext() ; )
    {
      Integer[] curWord = (Integer[])it.next();
      map.put(curWord[0], curWord[1]);
    }
    Vector retVec = new Vector();
    for ( Iterator it = numbers.iterator() ; it.hasNext() ; )
    {
      Integer curWord = (Integer)it.next();
      Integer curVal = new Integer(0);
      if ( map.containsKey(curWord) )
      {
        curVal = (Integer)map.get(curWord);
      }
      retVec.add(new Double(curVal.doubleValue()));
    }
    return retVec;
  }

  protected List translateNumbers(List numbers)
  {
    List retList = new Vector();
    for ( Iterator it = numbers.iterator() ; it.hasNext() ; )
    {
      Integer curWordNr = (Integer)it.next();
      CHString curWord = this.dbUtil.getWordForNumber(new CHString(curWordNr.toString()));
      Integer transNr = this.extraUtil.getNumberForWord(curWord);
      retList.add(transNr);
    }
    return retList;
/*    List words = this.dbUtil.getWordsForNumbersSameOrder(numbers);
    List retNumbers = this.extraUtil.getNumbersForWordsSameOrder(words);
    return retNumbers;*/
  }

  public static void main(String[] args)
  {
    String url = "jdbc:mysql://aspra8.informatik.uni-leipzig.de/kollsk1?user=sbordag&password=ansiBla";
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    DBConnection connection = null;
    try
    {
      System.out.print("Creating connection:");
      connection = new DBConnection(url, user, passwd);
      System.out.print("\t\t\tdone\nCreating util:");
      DBUtilUncached util = new DBUtilUncached(connection);
      System.out.print("\t\tdone\nCreating KollokSigAxis:");
      ValueAxis yAxis = new KollokHigherOrderAxis(util);

      Integer testWordNr = new Integer(1001);
      Vector tempVec = new Vector();
      tempVec.add(new Integer(24001));
      tempVec.add(new Integer(169464));
      tempVec.add(new Integer(100000000));

      List values = yAxis.getValues(testWordNr, tempVec);
      System.out.println("I've got following values: "+values);

    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }
  }

}