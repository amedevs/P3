public class ArenaMedia extends Arena {
    public ArenaMedia(Arena base) {
        super(base.getNombre(), base.getPremioBase());
    }
    @Override
    public int getPremio() {
        return (int)(premioBase * 1.2); // 120%
    }
    @Override
    public String getDetalle() {
        return "Media (" + getPremio() + " créditos)";
    }
}