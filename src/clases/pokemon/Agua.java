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
	/**Metodo para atacar un pokemon<br>
	 * 
	 * <b>Precondicion:</b> El parametro "adversario" debe ser distinto de null
	 * 
	 */
	
	public void atacar(Pokemon adversario) {
		adversario.recibeDano(this.getAtaque()/10);
	}

	/**Metodo para recibir daño<br>
	 * 
	 * <b>Precondicion:</b> El parametro "dano" debe ser > 0
	 * 
	 */
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
    
	public void hechizadoNiebla() {
    		this.setVida(this.getVida()/2);
	}
	
	public void hechizadoViento() {
 	   	this.setAtaque(this.getAtaque()*9/10);
 	   	this.setVida(this.getVida()*9/10);
	}
	
	public void hechizadoTormenta() {
    		this.setEscudo(this.getEscudo()*0.1);
	}

	/**Metodo que se encarga de clonar al pokemon agua<br>
	 * 
	 * Genera un clon de pokemon agua (siempre es clonable)<br>
	 * @return Devuelve el clon del pokemon
	 * @throws CloneNotSupportedException 
	 */
	@Override
	public Object clone(){
		Agua a = null;
		
		try {
			a = (Agua)super.clone();
		}
		catch (CloneNotSupportedException e) {}
		
		return a;
	}
    
	public double getCosto() {
    	return costo;
	}
}
