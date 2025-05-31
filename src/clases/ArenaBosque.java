public class ArenaBosque extends Arena {
    public ArenaBosque() {
        super("Bosque", 700);
    }
    @Override public int getPremio() { return premioBase; }
    @Override public String getDetalle() { return "Bosque (" + premioBase + " créditos)"; }
}
