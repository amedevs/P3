package clases.arma;

import clases.pokemon.Pokemon;

public class Hacha extends Arma {
    
	private final static double minAtaque = 50,
                            	maxAtaque = 150;
    
	public Hacha() {
    	super(minAtaque,80);
	}
    
	public void atacar(Pokemon adversario) {
    	adversario.recibeDano(this.getAtaque() + (maxAtaque-minAtaque)*Math.random());
	}
	
	public boolean esClonable() {
		return false;
	}
}