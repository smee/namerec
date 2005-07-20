package namerec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/* Class NameTable
Author: Christian Biemann, 01/002


Implementiert File-IO für Hashtables.

Das File-Format sieht so aus:

Key1 TAB Val1
Key2 TAB Val2
...


Funktionen:

Nametable NameTable.loadFile(filename): Fügt neues File in die Tabelle ein. Alte Elemente bleiben bestehen!

void NameTable.saveFile(filename): Schreibt Tabelle in File.
void NameTabelle.appendFile(filename): Hängt Tabelle an File an.



*/

public class NameTable extends java.util.Hashtable {


    public void writeFile(String filename,boolean append) throws IOException{
	FileWriter file=new FileWriter(filename,append);
	String outstr=new String();
	char outChar;

	outstr=this.toString();  // wandle um in "{Key1=Val1, Key2=Val2, ...}"
	try {
	     for (int pos=0;pos<outstr.length();pos++) {
		 outChar=outstr.charAt(pos);
		 if (outChar=='{') { continue;}
		 if (outChar=='}') { continue;}
		 if (outChar==' ') { continue;}
		 if (outChar=='|') { continue;}
		 if (outChar==',') {file.write(13);file.write(10);continue;} // Neue Zeile
		 if (outChar=='=') {file.write(9);continue;} // Tabulator
		 
		 file.write(outChar);                                                                       
	     } //rof
	     file.write(13);file.write(10);  //Endet mit CR&LF
	} catch (IOException e){System.out.println("Can.t write "+filename);} 
	finally {file.close();}

    } // end writeFile (für saveFile und appendFile)

    public void appendFile(String filename) throws IOException {
	try{this.writeFile(filename,true);} catch (IOException e){System.out.println("Can.t write "+filename);}
    }

    public void saveFile(String filename) throws IOException {
	try{this.writeFile(filename,false);}  catch (IOException e){System.out.println("Can.t write "+filename);}
    }

    public static NameTable loadFromFile(String filename) throws IOException {
        NameTable table=new NameTable();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = null;
        while((line=br.readLine())!=null) {
            StringTokenizer st=new StringTokenizer(line,"\t");
            if(st.countTokens()>=2)
                table.put(st.nextToken(),st.nextToken());
        }
        return table;
    }


    

} //end NameTable
