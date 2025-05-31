public class ArenaDificil extends Arena {
    public ArenaDificil(Arena base) {
        super(base.getNombre(), base.getPremioBase());
    }
    @Override
    public int getPremio() {
        return (int)(premioBase * 1.5); // 150%
    }
    @Override
    public String getDetalle() {
        return "Difícil (" + getPremio() + " créditos)";
    }
}