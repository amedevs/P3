package clases.pokemon;

public class Hielo extends Pokemon {
	private static final double escudoPD = 120,
                               	ataquePD = 100,
                               	vidaPD = 400,
                               	costo = 100;
    
	public Hielo(String nombre) {
    	super(nombre,escudoPD,ataquePD,vidaPD,0);
	}
    
	// "Heredaciones" -----------------------------------------------------------------
	/**Metodo para atacar un pokemon<br>
	 * 
	 * <b>Precondicion:</b> El parametro "adversario" debe ser distinto de null
	 * 
	 */
	public void atacar(Pokemon adversario) {
    	adversario.recibeDano(this.getAtaque()*3/20);
    	this.setAtaque(this.getAtaque()/20);
	}

	/**Metodo para recibir daño<br>
	 * 
	 * <b>Precondicion:</b> El parametro "dano" debe ser > 0
	 * 
	 */
	public void recibeDano(double dano) {
    	if (this.getEscudo()>0) {
        	this.setEscudo(this.getEscudo()-dano);
        	if (this.getEscudo()<0) {
            	this.setVida(this.getVida()+this.getEscudo());
            	this.setEscudo(0);
        	}
    	} else
        	this.setVida(this.getVida()-dano);
	}
	
	public void recargar() {
    	this.setEscudo(this.getEscudo()+100);
    	this.setAtaque(this.getAtaque()+100);
    	this.setVida(this.getVida()+200);
	}

	/**Metodo que se encarga de clonar al pokemon Hielo<br>
	 * 
	 * Genera un clon de pokemon Hielo (siempre es clonable)<br>
	 * @return Devuelve el clon del pokemon
	 * @throws CloneNotSupportedException 
	 */
	@Override
	public Object clone(){
		Hielo h = null;
		
		try {h = (Hielo)super.clone();}
		catch (CloneNotSupportedException e) {}
		
		return h;
	}
    
	public void hechizadoNiebla() {
    		this.setVida(this.getVida()*4/10);
	}
	
	public void hechizadoViento() {
	    	this.setAtaque(this.getAtaque()*8/10);
	    	this.setVida(this.getVida()*8/10);
	}
	
	public void hechizadoTormenta() {
    		this.setEscudo(this.getEscudo()*0.2);
	}
    
	public double getCosto() {
    	return costo;
	}
	
	
}
