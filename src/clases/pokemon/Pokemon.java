package clases.pokemon;

import interfaces.Clasificable;
import interfaces.Hostil;
import interfaces.Valuable;
import interfaces.Hechizable;

public abstract class Pokemon implements Hostil, Valuable, Clasificable, Cloneable, Hechizable {
	private String nombre;
	private double escudo,
              	ataque,
              	vida;
	private int xp;
    
	// Constructor -----------------------------------------------------------------
	public Pokemon(String nombre, double escudo, double ataque, double vida, int xp) {
    	this.nombre = nombre;
    	this.escudo = escudo;
    	this.ataque = ataque;
    	this.vida = vida;
    	this.xp = xp;
	}
    
	// Interfaces -----------------------------------------------------------------
	public int getCategoria() {
    	return xp;
	}
	public void setXP(int XP) {
    	this.xp = XP;
	}
	
	// Métodos -----------------------------------------------------------------
	public abstract void recibeDano(double dano);
	public abstract void recargar();
	
	// Getters y setters -----------------------------------------------------------------
	public String getNombre() {
    	return nombre;
	}
	public void setNombre(String nombre) {
    	this.nombre = nombre;
	}

	public double getEscudo() {
    	return escudo;
	}
	public void setEscudo(double escudo) {
    	this.escudo = escudo;
	}

	public double getAtaque() {
    	return ataque;
	}
	public void setAtaque(double ataque) {
    	this.ataque = ataque;
	}

	public double getVida() {
    	return vida;
	}
	public void setVida(double vida) {
    	this.vida = vida;
	}

	/**Metodo que se encarga de clonar al Pokemon<br>
	 * 
	 * Genera un clon del Pokemon <br>
	 * @return Devuelve el clon del pokemon.
	 * @throws CloneNotSupportedException 
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
          return (Pokemon)super.clone();
	}
}
