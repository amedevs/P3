public class HiloDuelo implements Runnable {
    private Entrenador ent1, ent2;
    private ArenaManager arenaManager;

    public HiloDuelo(Entrenador ent1, Entrenador ent2, ArenaManager arenaManager) {
        this.ent1 = ent1;
        this.ent2 = ent2;
        this.arenaManager = arenaManager;
    }

    @Override
    public void run() {
        Arena arenaSolicitada = null;
        try {
            arenaSolicitada = arenaManager.solicitaArena();
            Arena arenaDecorada = decorarArenaAleatoria(arenaSolicitada);

            ArrayList<Pokemon> equipo1Duelo = new ArrayList<>(ent1.getPokemonesCombatientes());
            ArrayList<Pokemon> equipo2Duelo = new ArrayList<>(ent2.getPokemonesCombatientes());

            Duelo duelo = new Duelo(ent1, equipo1Duelo, ent2, equipo2Duelo, arenaDecorada);
            duelo.addObserver((obs, msg) -> System.out.println("[Duelo] " + msg));
            duelo.run();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (arenaSolicitada != null) {
                arenaManager.liberaArena(arenaSolicitada);
            }
        }
    }

    private Arena decorarArenaAleatoria(Arena base) {
        // tu lógica para decorar arena
        return base; // temporal
    }
}
