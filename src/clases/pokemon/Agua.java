package clases.pokemon;

public class Agua extends Pokemon {
	private static final double escudoPD = 100,
                            	ataquePD = 120,
                            	vidaPD = 500,
                            	costo = 100;
	public Agua(String nombre) {
    		super(nombre,escudoPD,ataquePD,vidaPD,0);
	}
    
	// "Heredaciones"  ------------------------------------------------------------
	public void atacar(Pokemon adversario) {
		adversario.recibeDano(this.getAtaque()/10);
	}
	
	public void recibeDano(double dano) {
    	if (this.getEscudo()>0) {
    		dano = dano/2;
        	this.setEscudo(this.getEscudo()-dano);
        	this.setVida(this.getVida()-dano);
        	if (this.getEscudo()<0) {
        		this.setVida(this.getVida()+this.getEscudo());
            	this.setEscudo(0);
        	}
    	} else
    		this.setVida(this.getVida()-dano);
	}
	public void recargar() {
    	this.setEscudo(escudoPD);
    	this.setAtaque(ataquePD);
    	this.setVida(vidaPD);
	}
    
	public void hechizoNiebla() {
    	this.setVida(this.getVida()/2);
	}
	public void hechizoViento() {
 	   	this.setAtaque(this.getAtaque()*9/10);
 	   	this.setVida(this.getVida()*9/10);
	}
	public void hechizoTormenta() {
    		this.setEscudo(this.getEscudo()*0.1);
	}
    
	public double getCosto() {
    	return costo;
	}
	
	public boolean esClonable() {
    	return true;
	}
	
}