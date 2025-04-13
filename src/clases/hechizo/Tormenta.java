package clases.hechizo;

import interfaces.Hechizable;

public class Tormenta extends Hechizo {
	
	public Tormenta() {}
	
	@Override
	public void hechizar(Hechizable hechizado) {
		hechizado.hechizadoTormenta();
	}
}
