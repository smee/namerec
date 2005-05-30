/*
 * Created on 18.01.2005
 *
 */
package namerec.util;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * Singleton, Facade. Liefert vom user ausgewaehlte Datei zurueck. Es wird immer das zuletzt aktuelle Verzeichnis wieder besucht.
 * @author sdienst
 *
 */
public class FileSelector {
    private static File _lastdir=new File(".");
    /**
     * Type value indicating that the <code>JFileChooser</code> supports an 
     * "Open" file operation.
     */
    public static final int OPEN_DIALOG = 0;

    /**
     * Type value indicating that the <code>JFileChooser</code> supports a
     * "Save" file operation.
     */
    public static final int SAVE_DIALOG = 1;
    public static final FileFilter CSV_FILEFILTER = new FileFilter(){
        public boolean accept(File f) {
            if(f.isDirectory())
                return true;
            if(f.isFile() && (f.getName().endsWith(".csv") || f.getName().endsWith(".CSV")))
                return true;
            return false;
        }
        public String getDescription() {
            return "csvfiles (.csv)";
        }            
    };
    
    private  static void setLastUsedDirectory(File f) {
        if(f.isFile())
            _lastdir=f;
        else
            _lastdir=f.getParentFile();
    }

    public static File getUserSelectedFile(Component parent, String title, FileFilter filter, int dialogtype) {
        JFileChooser jfc=new JFileChooser(_lastdir);
        jfc.setDialogTitle(title);
        jfc.setFileFilter(filter);
        int result = -1;
        File toreturn = null;
        if(dialogtype == OPEN_DIALOG) 
            result = jfc.showOpenDialog(parent);
        else 
            result = jfc.showSaveDialog(parent);
        
        if(result == JFileChooser.APPROVE_OPTION) {
            toreturn  = jfc.getSelectedFile();
            setLastUsedDirectory(toreturn);
            if(dialogtype == SAVE_DIALOG && toreturn.exists()) {
                int answer=JOptionPane.showConfirmDialog(parent,"The contents of the file will be lost. Do you want to continue?");
                if(answer==JOptionPane.NO_OPTION)
                    return getUserSelectedFile(parent,title,filter,dialogtype);
                else if(answer==JOptionPane.CANCEL_OPTION)
                    return null;
            }
        }
        return toreturn;
    }
}
