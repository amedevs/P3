package clases.hechizo;

import interfaces.Hechizable;

public abstract class Hechizo implements Cloneable {
	
	public abstract void hechizar(Hechizable hechizado);
	
	public Object clone() throws CloneNotSupportedException {
		return (Hechizo)super.clone();
	}
}
