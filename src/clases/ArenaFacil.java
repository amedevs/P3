public class ArenaFacil extends Arena {
    public ArenaFacil(Arena base) {
        super(base.getNombre(), base.getPremioBase());
    }
    @Override
    public int getPremio() {
        return (int)(premioBase * 0.9); // 90%
    }
    @Override
    public String getDetalle() {
        return "Fácil (" + getPremio() + " créditos)";
    }
}
