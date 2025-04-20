package clases.pokemon;

import clases.arma.Arma;

public class Piedra extends Pokemon {
	private static final double escudoPD = 300,
                               	ataquePD = 150,
                               	vidaPD = 600,
                               	costo = 200;
	private Arma arma;
    
	public Piedra(String nombre) {
    	super(nombre,escudoPD,ataquePD,vidaPD,0);
	}
	public Piedra(String nombre, Arma arma) {
    	super(nombre,escudoPD,ataquePD,vidaPD,0);
    	this.arma = arma;
	}
    
	// "Heredaciones" -----------------------------------------------------------------
	public void atacar(Pokemon adversario) {
    	if (this.arma != null)
        	this.arma.atacar(adversario);
    	else
        	adversario.recibeDano(this.getAtaque()*3/20);
    	this.setAtaque(this.getAtaque()/20);
	}
	
	public void recibeDano(double dano) {
    	if (this.getEscudo()>0) {
    		dano = (double)dano/4;
        	this.setEscudo(this.getEscudo()-3*dano);
        	this.setVida(this.getVida()-dano);
        	if (this.getEscudo()<0) {
            	this.setVida(this.getVida()+this.getEscudo());
            	this.setEscudo(0);
        	}
    	} else
        	this.setVida(this.getVida()-dano);
	}
	
	public void recargar() {
    	this.setEscudo(escudoPD*(80 + 10*this.getCategoria())/100);
    	if (this.getEscudo()>2*escudoPD)
        	this.setEscudo(2*escudoPD);
    	this.setAtaque(ataquePD*(80 + 10*this.getCategoria())/100);
    	if (this.getAtaque()>2*ataquePD)
        	this.setAtaque(2*ataquePD);
    	this.setVida(vidaPD*(80 + 10*this.getCategoria())/100);
    	if (this.getVida()>2*vidaPD)
        	this.setVida(2*vidaPD);
	}
    
	public void hechizadoNiebla() {
    	this.setAtaque(this.getAtaque()*4/10);
	}
	public void hechizadoViento() {
    	this.setVida(this.getVida()/4);
	}
	public void hechizadoTormenta() {
    	this.setEscudo(0);
    	this.setAtaque(this.getAtaque()*7/10);
	}
    
	public double getCosto() {
    	return costo;
	}
	
	public Arma getArma() {
		return this.arma;
	}
	public void setArma(Arma arma) {
		this.arma = arma;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Piedra clonPiedra = (Piedra)super.clone();
		clonPiedra.arma = (Arma)arma.clone();
		return clonPiedra;
	}
}
