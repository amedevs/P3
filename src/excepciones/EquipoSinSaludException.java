package excepciones;

import clases.Entrenador;

public class EquipoSinSaludException extends Exception {
	private Entrenador entrenador;

	public EquipoSinSaludException(Entrenador entrenador) {
		super("Los pokemones de batalla de "+entrenador.getNombre()+" no tienen salud.\n");
		this.entrenador = entrenador;
	}
	
	public Entrenador getEntrenador() {
		return this.entrenador;
	}

	public String getMessage() {
		return super.getMessage();
	}
}
