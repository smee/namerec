package basetagger;
// Author: Christian Biemann 
// Knoten für Pretree
// Bemerkung: viele Funktionen sind in Pretree implementiert, um Knoten so klein wie möglich zu halten

import java.io.Serializable;
import java.util.Vector;

public class Knoten implements Serializable {

    public Vector classes;
    public String inhalt=new String();      // Knoteninhalt
    public Vector kinder=new Vector();

    Knoten() {
	this.classes=new Vector();
    }

    Knoten(String neuinhalt) {  // Constructor mit Inhalt
	this.inhalt= neuinhalt;
    } // end Constructor


    void setClasses(Vector neuclasses) {
	this.classes=neuclasses;
    }


    Vector getClasses() {
	return this.classes;
    }



} //class knoten

