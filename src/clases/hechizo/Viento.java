package clases.hechizo;

import interfaces.Hechizable;

public class Viento extends Hechizo {
	
	public Viento() {}
	
	@Override
	public void hechizar(Hechizable hechizado) {
		hechizado.hechizadoViento();
	}
}
