package clases.hechizo;

import interfaces.Hechizable;

public abstract class Hechizo implements Cloneable {
    public enum TipoHechizo { NIEBLA, TORMENTA, VIENTO }
    private final TipoHechizo tipo;

    public Hechizo(TipoHechizo tipo) {
        this.tipo = tipo;
    }

    public TipoHechizo getTipo() {
        return tipo;
    }

    public abstract void hechizar(Hechizable hechizado);

}