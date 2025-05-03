package clases.hechizo;

import interfaces.Hechizable;

public class Viento extends Hechizo {
	
	public Viento() {
		super(TipoHechizo.VIENTO);
	}
	
	@Override
	public void hechizar(Hechizable hechizado) {
		hechizado.hechizadoViento();
	}
}
