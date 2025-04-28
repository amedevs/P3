package excepciones;

import clases.Entrenador;

public class EntrenadorSinPoquemonesException extends Exception {
	
	private Entrenador entrenador;

	public EntrenadorSinPoquemonesException(Entrenador entrenador) {
		super("El entrenador " + entrenador.getNombre() + " no posee pokemones de batalla");
		this.entrenador = entrenador;
	}

	public Entrenador getEntrenador() {
		return entrenador;
	}
	
	public String getMessage() {
		return super.getMessage();
	}

}
