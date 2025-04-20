package clases.arma;

import interfaces.Hostil;
import interfaces.Valuable;

public abstract class Arma implements Hostil, Valuable, Cloneable {
	double ataque,
      	costo;
    
	public Arma(double ataque, double costo) {
    	this.ataque = ataque;
    	this.costo = costo;
	}

	public double getAtaque() {
    	return ataque;
	}
	public void setAtaque(double ataque) {
    	this.ataque = ataque;
	}

	public double getCosto() {
    	return costo;
	}

    public Object clone() throws CloneNotSupportedException{
		return (Arma)super.clone();
	}
}
