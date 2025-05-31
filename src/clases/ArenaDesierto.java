
public class ArenaDesierto extends Arena {
    public ArenaDesierto() {
        super("Desierto", 1000);
    }
    @Override public int getPremio() { return premioBase; }
    @Override public String getDetalle() { return "Desierto (" + premioBase + " créditos)"; }
}

