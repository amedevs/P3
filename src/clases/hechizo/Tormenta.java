package clases.hechizo;

import interfaces.Hechizable;

public class Tormenta extends Hechizo {
	
	public Tormenta() {
		super(TipoHechizo.TORMENTA);
	}
	
	@Override
	public void hechizar(Hechizable hechizado) {
		hechizado.hechizadoTormenta();
	}
}
