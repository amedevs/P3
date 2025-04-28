package excepciones;

public class CompraImposibleException extends Exception {
	
	private int creditos;
	private double costo;
	
	public CompraImposibleException(int creditos, double costo) {
		super("Compra no realizada. El entrenador posee " + creditos + " creditos y el costo es de " + costo);
		this.creditos = creditos;
		this.costo = costo;
	}
	public int getCreditos() {
		return creditos;
	}
	public double getCosto() {
		return costo;
	}
}
