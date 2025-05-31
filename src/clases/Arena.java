public abstract class Arena {
    protected String nombre;
    protected int premioBase; // ej. 700, 1000, 800

    public Arena(String nombre, int premioBase) {
        this.nombre = nombre;
        this.premioBase = premioBase;
    }
    public String getNombre() { return nombre; }
    public int getPremioBase() { return premioBase; }

    // Método que retorna el premio REAL según dificultad
    public abstract int getPremio();  
    public abstract String getDetalle(); 
}
