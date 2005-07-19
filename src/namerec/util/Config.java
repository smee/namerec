/*
 * Created on 10.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package namerec.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Config {
    private Properties prop;
    
    public Config(String filename) throws FileNotFoundException, IOException {
        this(new File(filename));
    }
    public Config(File f) throws FileNotFoundException, IOException {
        this();
        prop.load(new FileInputStream(f));
    }
    public Config() {
        prop=new Properties();
    }
    public String getString(String key, String deflt) {
        if(!prop.containsKey(key))
            return deflt;
        return prop.getProperty(key);
    }
    public int getInteger(String key, int deflt) {
        if(!prop.containsKey(key))
            return deflt;
        return Integer.parseInt(prop.getProperty(key));
    }
    public double getDouble(String key, double deflt) {
        if(!prop.containsKey(key))
            return deflt;
        return Double.parseDouble(prop.getProperty(key));
    }
    public void set(String key, String prop) {
        this.prop.setProperty(key,prop);
    }
    /**
     * @param string
     * @return
     */
    public String getJDBCStringWS(String deflt) {
        StringBuffer sb=new StringBuffer("jdbc:mysql://");
        if(!prop.containsKey("DB.HOSTWS"))
            return deflt;
        sb.append(prop.getProperty("DB.HOSTWS"));
        sb.append("/");
        if(!prop.containsKey("DB.DBNAMEWS"))
            return deflt;
        sb.append(prop.getProperty("DB.DBNAMEWS"));
        sb.append("?user=");
        if(!prop.containsKey("DB.USERNAMEWS"))
            return deflt;
        sb.append(prop.getProperty("DB.USERNAMEWS"));
        sb.append("&password=");
        if(!prop.containsKey("DB.PASSWORDWS"))
            return deflt;
        sb.append(prop.getProperty("DB.PASSWORDWS"));
        return sb.toString();        
    }
    public String getJDBCStringAKT(String deflt) {
        StringBuffer sb=new StringBuffer("jdbc:mysql://");
        if(!prop.containsKey("DB.HOSTAKT"))
            return deflt;
        sb.append(prop.getProperty("DB.HOSTAKT"));
        sb.append("/");
        if(!prop.containsKey("DB.DBNAMEAKT"))
            return deflt;
        sb.append(prop.getProperty("DB.DBNAMEAKT"));
        sb.append("?user=");
        if(!prop.containsKey("DB.USERNAMEAKT"))
            return deflt;
        sb.append(prop.getProperty("DB.USERNAMEAKT"));
        sb.append("&password=");
        if(!prop.containsKey("DB.PASSWORDAKT"))
            return deflt;
        sb.append(prop.getProperty("DB.PASSWORDAKT"));
        return sb.toString();        
    }
    public String toString() {
        StringBuffer sb =new StringBuffer("Einstellungen:\n-------------");
        
        sb.append("\n Klassen: "+prop.getProperty("IN.CLASSNAMES"));
        sb.append("\n Wissen Items: "+prop.getProperty("IN.KNOWLEDGE"));
        sb.append("\n Wissen Regexp: "+prop.getProperty("IN.REGEXP"));
        sb.append("\n Wissen Regeln: "+prop.getProperty("IN.PATFILE"));
        sb.append("\n Regeln für NEs "+prop.getProperty("IN.PATFILENE"));
        sb.append("\n Anzahl Sätze zur Kandidatenüberprüfung "+prop.getProperty("OPTION.CANDIDATESNO"));
        sb.append("\n Threshhold Anerkennung Item "+prop.getProperty("OPTION.ACCEPTITEM"));
        sb.append("\n Beginne bei Satz: "+prop.getProperty("OPTION.STARTNO"));
        sb.append("\n Ende bei Satz: "+prop.getProperty("OPTION.ENDNO"));
        sb.append("\n Datei für neue Items: "+prop.getProperty("OUT.ITEMSFOUND"));
        sb.append("\n Datei für eventuelle Items: "+prop.getProperty("OUT.MAYBE"));
        sb.append("\n Datei für Kontexte, wenn Regeln irgendwie zuschlagen: "+prop.getProperty("OUT.CONTEXT"));
        sb.append("\n Datei für komplett bekannte Namen: "+prop.getProperty("OUT.COMPLEXNAMES"));
        sb.append("\n Anzahl der Verifikationsthreads: "+prop.getProperty("OPTION.NUMOFTHREADS"));
        
        return sb.toString();
    }
    public void saveToFile(File f) throws FileNotFoundException, IOException {
        prop.store(new FileOutputStream(f),null);
    }
    public void updateConfigFile(String filename)  throws IOException{
        
            BufferedReader in= new BufferedReader(new FileReader(filename));
            StringBuffer sb= new StringBuffer();
            String temp,line;
            
            Set s = new HashSet(prop.keySet()); //alle mir bekannten Parameter
            
            while((line = in.readLine()) != null) {
                temp=line.trim();
                if (!temp.startsWith("#") && temp.indexOf('=') != -1) {
                    temp= temp.substring(0, temp.indexOf('=')).trim();
                    if(temp.length() > 0 && prop.containsKey(temp)){
                        sb.append(temp + '=');
                        sb.append(prop.getProperty(temp) + '\n');
                        s.remove(temp);//hat mer schon
                    }else {//mir nicht bekannter parameter
                        if(!"=".equals(line.trim()))//einzelnes = lassen wir weg
                            sb.append(line + '\n');
                    }
                } else {//kommentar oder unsinnige zeile, kein Parameter
                    //wird weggelassen, weil es ja eh n problem beim spaeteren einlesen geben wuerde.
                    //System.out.println("unknown parameterline: "+temp);
                    sb.append(line + '\n');
                }
            }
            in.close();
            //add remaining Parameters
            for (Iterator it = s.iterator(); it.hasNext();) {
                String key = (String) it.next();
                sb.append(key); sb.append('=');
                temp = prop.getProperty(key);
                if(temp != null)
                    sb.append(temp);
                sb.append('\n');
            }
            BufferedWriter out= new BufferedWriter(new FileWriter(filename));
            out.write(sb.toString());
            out.close();
    }
    public boolean getBoolean(String key, boolean deflt) {
        if(!prop.containsKey(key))
            return deflt;
        return Boolean.valueOf(prop.getProperty(key)).booleanValue();    
    }
}
