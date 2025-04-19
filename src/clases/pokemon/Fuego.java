package clases.pokemon;

public class Fuego extends Pokemon {
	private static final double escudoPD = 200,
                           	ataquePD = 80,
                           	vidaPD = 530,
                           	costo = 120;  
	public Fuego(String nombre) {
    	super(nombre,escudoPD,ataquePD,vidaPD,0);
	}
	
	// "Heredaciones" -----------------------------------------------------------------
    
	public void atacar(Pokemon adversario) {
    	double nuevoAtaque;
   	 
    	adversario.recibeDano(this.getAtaque()/5);
    	nuevoAtaque = this.getAtaque()*3/4;
    	if (nuevoAtaque>10)
        	this.setAtaque(nuevoAtaque);
	}
	
	public void recibeDano(double dano) {
    	if (this.getEscudo()>0) {
    		dano = dano/4;
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
    	this.setEscudo(escudoPD*(80 + 5*this.getCategoria())/100);
    	this.setAtaque(ataquePD*(80 + 5*this.getCategoria())/100);
    	this.setVida(vidaPD*(80 + 5*this.getCategoria())/100);
	}
    
	public void hechizadoNiebla() {
    	this.setAtaque(this.getAtaque()/2);
	}
	public void hechizadoViento() {
    	this.setVida(this.getVida()/2);
	}
	public void hechizadoTormenta() {
    	this.setEscudo(this.getEscudo()*0.8);
    	this.setAtaque(this.getAtaque()*0.8);
	}
    
	public double getCosto() {
    	return costo;
	}
	
	public boolean esClonable() {
    	return false;
	}
}