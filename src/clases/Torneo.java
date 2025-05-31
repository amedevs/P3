public class Torneo {
    private static final int MAX_ENTRENADORES = 8;

    private List<Entrenador> inscritos = new ArrayList<>();
    private final Semaphore semEntrenadores = new Semaphore(MAX_ENTRENADORES, true);
    private ArenaManager arenaManager;  // inyectado por el Facade

    public Torneo(ArenaManager manager) {
        this.arenaManager = manager;
    }

    /**
     * Inscribe un entrenador. Si ya hay 8, bloquea hasta que alguien
     * se retire o el torneo comience.
     */
    public void inscribir(Entrenador e) throws InterruptedException {
        // Si no hay permisos (8 ocupados), el hilo queda esperando aquí.
        semEntrenadores.acquire();
        synchronized(inscritos) {
            inscritos.add(e);
        }
    }

    /**
     * Llama a este método cuando quieras comenzar el torneo.
     * Verifica que haya exactamente 8 inscritos, sino lanza excepción.
     */
    public void iniciarTorneo() throws IllegalStateException {
        if (inscritos.size() < MAX_ENTRENADORES) {
            throw new IllegalStateException("Faltan entrenadores: ya no se permiten menos de 8 para iniciar.");
        }
        // A partir de aquí, ningún otro entrenador puede inscribirse, 
        // (semEntrenadores se “congela” en 0 permisos).
        // Se arma el bracket de cuartos → semis → final.
        armarBracketYLanzarDuelo(inscritos);
    }

    private void armarBracketYLanzarDuelo(List<Entrenador> lista) {
        // Ejemplo muy simplificado: cuartos de final se componen
        // de 4 duelos entre pares de la lista, luego semis y final.
        List<Duelo> duelosCuartos = new ArrayList<>();
        for (int i = 0; i < 8; i += 2) {
            // Para armar cada duelo, hacemos:
            Duelo d = new Duelo(lista.get(i), lista.get(i+1), solicitaArenaYGeneraDecorator());
            duelosCuartos.add(d);
        }

        // Lanzamos esos 4 duelos en hilos separados (concurrentes).
        List<Thread> hilos = new ArrayList<>();
        for (Duelo d : duelosCuartos) {
            Thread t = new Thread(() -> {
                try {
                    // Poseer una arena antes de correr el duelo:
                    Arena a = arenaManager.solicitaArena();
                    try {
                        d = new Duelo(d.getEntrenador1(), d.getEntrenador2(), a);
                        d.run();
                    } finally {
                        arenaManager.liberaArena(a);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            });
            hilos.add(t);
            d.addObserver(/*algún observer, e.g. GUI o log*/);
            t.start();
        }

        // Esperar a que terminen todos los cuartos:
        for (Thread t : hilos) {
            try { t.join(); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        }

        // Obtener los 4 ganadores y repetir el proceso para semifinales, etc.
        // (aquí se simplifica: suponemos que Duelo.run() ya actualizó e.posiciónTorneo y créditos)
        List<Entrenador> ganadoresCuartos = extraerGanadores(duelosCuartos);
        // Semifinales:
        // 2 duelos, se repite el mismo esquema (p. ej. con hilos nuevos).
        // Final, etc.
    }

    private List<Entrenador> extraerGanadores(List<Duelo> listaDuelo) {
        List<Entrenador> ganadores = new ArrayList<>();
        for (Duelo d : listaDuelo) {
            // Asumimos que Duelo almacena internamente quién ganó:
            ganadores.add(d.getGanador());
        }
        return ganadores;
    }

    /**
     * Este método construye/decorates una arena, p. ej. tomamos
     * una Arena base y la “decoramos” con dificultad aleatoria.
     */
    private Arena solicitaArenaYGeneraDecorator() {
        // Simplemente para ejemplificar, pedimos una arena base al ArenaManager
        // y luego la decoramos con dificultad:
        Arena base = null;
        try { 
            base = arenaManager.solicitaArena();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Suponiendo que basta retornar la base (ya adquirido por el semáforo).
        // Si queremos dificultad aleatoria:
        int nivel = new Random().nextInt(3); // 0=Fácil,1=Medio,2=Difícil
        switch (nivel) {
            case 0: return new ArenaFacil(base);
            case 1: return new ArenaMedia(base);
            default: return new ArenaDificil(base);
        }
    }
}
