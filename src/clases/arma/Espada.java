package clases.arma;

import clases.pokemon.Pokemon;

public class Espada extends Arma {
    
	public Espada() {
    	super(100,50);
	}
    
	public void atacar(Pokemon adversario) {
    	adversario.recibeDano(this.ataque);
	}

	@Override
	public Object clone() {
		Espada e = null;
		try {
			e = (Espada)super.clone();
		}
		catch (CloneNotSupportedException exc) {}
		return e;
	}
}
