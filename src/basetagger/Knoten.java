package basetagger;
// Author: Christian Biemann 
// Knoten für Pretree
// Bemerkung: viele Funktionen sind in Pretree implementiert, um Knoten so klein wie möglich zu halten

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Knoten implements Serializable {

    public List classes;
    public String inhalt=new String();      // Knoteninhalt
    public List kinder=new ArrayList();

    Knoten() {
	this.classes=new Vector();
    }

    Knoten(String neuinhalt) {  // Constructor mit Inhalt
	this.inhalt= neuinhalt;
    } // end Constructor


    void setClasses(List neuclasses) {
	this.classes=neuclasses;
    }


    List getClasses() {
	return this.classes;
    }



} //class knoten

