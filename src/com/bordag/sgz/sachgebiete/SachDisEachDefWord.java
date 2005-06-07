package com.bordag.sgz.sachgebiete;

// app specific imports
import com.bordag.sgz.algorithms.*;
import com.bordag.sgz.clustering.*;
import com.bordag.sgz.util.*;

// standard imports
import java.util.*;

/**
 * Disambiguate each defining word, take all cluster vectors from them, and
 * cluster the whole thing again, print results
 *
 * @author  Stefan Bordag
 * @date    09.05.2002
 */
public class SachDisEachDefWord extends Sachgebiet
{

  private Set defWordNrs = null;

  public SachDisEachDefWord()
  {
  }

  public SachDisEachDefWord(DBConnection connection, ComparableStringBuffer sachgebiet)
  {
    super(connection, sachgebiet);
    this.sachgebietNr = this.connection.getNumberForSachgebiet(sachgebiet);
    this.defWordNrs = this.connection.getSachgebietsWordNumbers(this.sachgebietNr);
    this.defWordNrs.add(this.connection.getNumberForWord(sachgebiet));
    Output.println("Starte Sachgebiet "+sachgebiet+" mit Wörtern : "+this.connection.getWordsForNumbers(this.defWordNrs));
    runAlgorithm();
  }

  public SachDisEachDefWord(DBConnection connection, Set defWoerter)
  {
    super(connection);
    this.defWordNrs = this.connection.getNumbersForWords(defWoerter);
    Output.println("Starte Sachgebietsanalyse mit Wörtern : "+this.connection.getWordsForNumbers(this.defWordNrs));
    runAlgorithm();
  }

  /**
   * durschnttl. freq. mal c sollte gute bewrtung der mengen sein (und mehr als 10 elem.
   **/
  protected void runAlgorithm()
  {
    TreeClustering mainClusterer = new TreeClustering();
    for ( Iterator it = this.defWordNrs.iterator() ; it.hasNext() ; )
    {
      ComparableStringBuffer curWordNr = (ComparableStringBuffer)it.next();
      NonThreadedDisambiguator dis = new NonThreadedDisambiguator(this.connection, curWordNr, new Integer(Options.getInstance().getSachDisStepsPerWord()).intValue());
      mainClusterer.addClusterVectorSet(dis.getCluster().getClusterVectorsAsSet(), curWordNr);
      System.out.println("Currently:");
      mainClusterer.printClusterVectors(this.connection);
    }
    Output.println("Finally:");
    mainClusterer.printClusterVectors(this.connection);
  }

  public static void main(String[] argv)
  {
    if ( argv.length < 1 )
    {
      Output.println("Arguments incomplete.");
      System.exit(0);
    }
    Sachgebiet sach = new SachDisEachDefWord(getStandardConnection(), new ComparableStringBuffer(argv[0]));
  }

}
