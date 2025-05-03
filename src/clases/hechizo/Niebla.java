package clases.hechizo;

import interfaces.Hechizable;

public class Niebla extends Hechizo {
	
	public Niebla() {
		super(TipoHechizo.NIEBLA);
	}
	
	@Override
	public void hechizar(Hechizable hechizado) {
		hechizado.hechizadoNiebla();
	}
}
