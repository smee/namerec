           package com.biemann.pendel;
           import java.sql.*;
           import java.util.*;
           import java.io.*;

          public class Verify implements Runnable {
            static boolean d=true; // debugging
            boolean running;
            int id;
            NameTable checkedArr;
            String actItemss;
            String actClass;
             Connection Verbindung;
             int n_cands;
             NameTable alleRegexp;
             NameTable allesWissen;
             NameTable klassKeys;
             Vector rules;
             int maybeCount;
             String maybeFile;
             boolean maybeWrite;
             String itemFile;
             boolean itemWrite;
             String sourceItem;
             double schwelle;
            Pendel pendel;

            Thread verifyThread=null;

            public Verify(Pendel c_pendel, int c_count, NameTable c_checkedArr, String c_actItem, String c_actClass, double c_schwelle, Connection c_Verbindung, int c_n_cands, NameTable c_alleRegexp, NameTable c_allesWissen, NameTable c_klassKeys, Vector c_rules, int c_maybeCount, String c_maybeFile, boolean c_maybeWrite, String c_itemFile, boolean c_itemWrite,String c_sourceItem) {
                 pendel=c_pendel;
                 id=(int)((c_count*10)/10)+0;
                 checkedArr=c_checkedArr;
                 actItemss=new String(c_actItem);
                 actClass=new String(c_actClass);
                 Verbindung=c_Verbindung;
                 n_cands=c_n_cands;
                 alleRegexp=c_alleRegexp;
                 allesWissen=c_allesWissen;
                 klassKeys=c_klassKeys;
                 rules=c_rules;
                 maybeCount=c_maybeCount;
                 maybeFile=c_maybeFile;
                 maybeWrite=c_maybeWrite;
                 schwelle=c_schwelle;
                 itemFile=c_itemFile;
                 itemWrite=c_itemWrite;
                 sourceItem=c_sourceItem;
                 running=true;
                 verifyThread=new Thread(this);
                 verifyThread.start();
                 try {
                 verifyThread.sleep(100);
                 } catch (InterruptedException e) {}
            }

            public void run() {
              try {
              pendel.checkedArr.insert(checkCandidate());
              } catch (SQLException e) {}
                catch (IOException e) {}
                //catch (InterruptedException e) {}

              running=false;
              verifyThread.stop();
            }

            public void stop() {
              if (verifyThread!=null) verifyThread.stop();
              verifyThread=null;
            }


            private NameTable checkCandidate() throws SQLException, IOException{
             	String itemText;
              String actItem=new String();
              actItem=new String(actItemss);
	            Vector wordVec=new Vector();
	            Vector classVec=new Vector();
              Annotate anno=new Annotate();
              Matcher matcher = new Matcher();
              DBaccess db= new DBaccess();
              Vector ergTest;
              int actClassInt=new Integer(klassKeys.get(actClass).toString()).intValue();
             // nehme nächsten Kandidaten
             NameTable checked=new NameTable();
	           //System.out.println("Ueberpruefe Kandidat "+actItem+"ID:"+id);
	           itemText=db.getNof(actItem,n_cands,Verbindung);    // finde Sätze, die Kandidaten enthalten
	           wordVec=anno.tokenize(itemText);
             try {
	           classVec=anno.annotate(wordVec,alleRegexp,allesWissen,klassKeys);
             } catch (gnu.regexp.REException e) {} ;

	           ergTest=matcher.getClassificationsOf(actItem,wordVec,classVec,klassKeys,rules);     //extrahiere Liste, wie aktName klassifiziert wurde

             //System.out.println("Item '"+actItem+"' klassifiert als "+ergTest.toString());


	           int anzOk=0;
             int anzTot=0;
             int ergClass=0;

	           for (Enumeration e=ergTest.elements();e.hasMoreElements();) {
		             ergClass=new Integer(e.nextElement().toString()).intValue();

                 anzTot++;
		             if ((ergClass & actClassInt)==actClassInt) {anzOk++;}        // Zähle Gesamtzahl und Zahl der auf NN klassifizierten
             } // rof Enumeration ergTest


	           if (d) {System.out.print(" "+actItem+"\t"+actClass+"\t"+anzOk+"/"+anzTot+"\t");}

	           if (anzTot>0) { // wenn überhaupt gefunden
		          if (((double)anzOk/(double)anzTot)>schwelle) {
		           if (anzOk>maybeCount) {
			           checked.put(actItem,actClass);           // Falls Quotient über Schwelle, akzeptiere Kandidaten
			           if (d) {System.out.println("+\t+");}
                   if (itemWrite) {
		                 NameTable nitem=new NameTable();
	                   nitem.put(actItem, actClass+"\t"+anzOk+"/"+anzTot+"\t"+sourceItem);
                     nitem.appendFile(itemFile);
                  }


               } // fi anzOK
               else { // elsif anzOK
			          if (d) {System.out.println("+\t-");}
                  if (maybeWrite) {
		                 NameTable maybes=new NameTable();
	                   maybes.put(actItem, actClass+"\t"+anzOk+"/"+anzTot+"\t"+sourceItem);
                     maybes.appendFile(maybeFile);
                  }
               } // esle
              } // fi schwelle
              else {  // nicht akzeptiert und kein maybe
                   System.out.println("-\t-");
              }
             } // fi anzTot

	return checked;
  } // end checkCandidate

}