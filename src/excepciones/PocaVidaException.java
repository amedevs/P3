package excepciones;

public class PocaVidaException extends Exception {
	
	protected double vida;
	protected double vidaPD;

	public PocaVidaException(double vida, double vidaPD) {
		super("El Pokemon no tiene la vida llena, su vida actual es: " + vida + " y su vida por defecto es: " + vidaPD);
		this.vida = vida;
		this.vidaPD = vidaPD;
	}

	public double getVida() {
		return vida;
	}

}
