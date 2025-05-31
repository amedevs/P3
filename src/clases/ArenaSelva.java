public class ArenaSelva extends Arena {
    public ArenaSelva() {
        super("Selva", 800);
    }
    @Override public int getPremio() { return premioBase; }
    @Override public String getDetalle() { return "Selva (" + premioBase + " créditos)"; }
}
