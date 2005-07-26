/*
 * Created on 16.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package namerec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author IBM Anwender
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileDataSource implements SatzDatasource {
	private final BufferedReader br;
    private int count;
	
	public FileDataSource(String filename) throws IOException{
		br=new BufferedReader(new FileReader(filename));
        count =-1;
	}
	public String getNextSentence() {
		try {
			String line= br.readLine();
			if(line == null){
				br.close();
				return "END";
			}
			return line;
		} catch (IOException e) {
			return "END";
		}
		
	}
    /**
     * Vorsicht, danach liefert getNextSentence wieder den ersten Satz!
     */
    public int getNumOfSentences() {
        if(count ==-1){
            int c=0;
            try {
                br.reset();
                count = 0;
                while(br.readLine()!=null)
                    c++;
                br.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.count=c;
        }
        return this.count;
    }

}
