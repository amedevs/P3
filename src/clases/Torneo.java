
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Semaphore;

/**
 * Clase Torneo que organiza un torneo de eliminación directa entre entrenadores Pokémon.
 * - Limita inscripciones a 8 entrenadores (Semaphore).
 * - Arma bracket: cuartos → semis → final.
 * - Cada duelo se ejecuta en un hilo separado, pidiendo arenas a ArenaManager.
 * - Espera a que terminen todos los duelos de una ronda antes de pasar a la siguiente.
 * - Extrae ganadores (según Duelo.getGanador()) y repite hasta determinar campeón.
 *
 * @author 
 */
public class Torneo {

    private static final int MAX_ENTRENADORES = 8;

    /** Lista de entrenadores inscritos en el torneo, en orden de inscripción */
    private final List<Entrenador> inscritos = new ArrayList<>();

    /** Semaphore que permite inscribir hasta 8 entrenadores a la vez */
    private final Semaphore semEntrenadores = new Semaphore(MAX_ENTRENADORES, true);

    /** Manager de arenas, provee y libera arenas (internamente usa un Semaphore propio) */
    private final ArenaManager arenaManager;

    /**
     * Construye un Torneo que usará el ArenaManager dado para asignar arenas a cada Duelo.
     * @param manager ArenaManager ya inicializado con la lista de arenas disponibles.
     */
    public Torneo(ArenaManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("ArenaManager no puede ser null");
        }
        this.arenaManager = manager;
    }

    /**
     * Inscribe a un entrenador en el torneo.  
     * Si ya hay 8 inscritos, el hilo queda bloqueado hasta que se libere un permiso.
     * @param e Entrenador a inscribir (no puede ser null).
     * @throws InterruptedException si el hilo es interrumpido mientras espera permiso.
     */
    public void inscribir(Entrenador e) throws InterruptedException {
        if (e == null) {
            throw new IllegalArgumentException("Entrenador no puede ser null");
        }
        semEntrenadores.acquire(); // consume uno de los 8 permisos disponibles
        synchronized (inscritos) {
            inscritos.add(e);
            // Inicializamos posiciónTorneo en 0 (antes de ganar ningún duelo).
            e.setPosicionTorneo(0);
        }
    }

    /**
     * Inicia el torneo completo, verificando que haya exactamente 8 entrenadores inscritos.
     * @throws EntrenadoresInsuficientesException si hay menos de 8 inscritos.
     */
    public void iniciarTorneo() {
        synchronized (inscritos) {
            if (inscritos.size() < MAX_ENTRENADORES) {
                throw new EntrenadoresInsuficientesException(inscritos.size());
            }
        }
        // A partir de este punto, no permitimos más inscripciones.
        // Construimos un listado separado para no alterar la lista original mientras corren los duelos.
        List<Entrenador> participantes = new ArrayList<>();
        synchronized(inscritos) {
            participantes.addAll(inscritos);
        }
        armarBracketYLanzarDuelo(participantes);
    }

    /**
     * Arma el bracket de eliminación directa en tres rondas: cuartos, semifinales y final.
     * @param participantes Lista de exactamente 8 entrenadores (asegurado por iniciarTorneo()).
     */
    private void armarBracketYLanzarDuelo(List<Entrenador> participantes) {
        // 1) Cuartos de final (8 → 4)
        ejecutarRonda(participantes, "Cuartos de Final");

        // 2) Semifinales (4 → 2)
        List<Entrenador> ganadoresCuartos = extraerGanadores();
        ejecutarRonda(ganadoresCuartos, "Semifinales");

        // 3) Final (2 → 1)
        List<Entrenador> ganadoresSemis = extraerGanadores();
        ejecutarRonda(ganadoresSemis, "Final");

        // 4) Al final, el único ganador queda en la lista
        List<Entrenador> campeonLista = extraerGanadores();
        if (!campeonLista.isEmpty()) {
            Entrenador campeon = campeonLista.get(0);
            System.out.println("🎉 ¡" + campeon.getNombre() + " es el campeón del torneo!");
        }
    }

    /**
     * Ejecuta una ronda de duelos dada la lista de participantes (de tamaño par).
     * - Para cada par (i, i+1), crea un hilo que:
     *   1) solicita una arena al ArenaManager,
     *   2) decora la arena con dificultad aleatoria,
     *   3) crea un objeto Duelo(e1, equipo1, e2, equipo2, arenaDecorada),
     *   4) registra un Observer (p. ej. la GUI),
     *   5) ejecuta duelo.run(),
     *   6) libera la arena al terminar.
     * - Luego hace join() sobre todos los hilos para esperar a que terminen.
     *
     * @param participantes Lista de entrenadores en esta ronda (debe tener tamaño par).
     * @param nombreRonda   Descripción de la ronda (p.ej. "Cuartos de Final").
     */
    private void ejecutarRonda(List<Entrenador> participantes, String nombreRonda) {
        System.out.println("▶ Iniciando ronda: " + nombreRonda);
       // Array de hilos para el control de rondas
        List<Thread> hilos = new ArrayList<>();


        for (int i = 0; i < participantes.size(); i += 2) {
            Entrenador e1 = participantes.get(i);
            Entrenador e2 = participantes.get(i + 1);

            // Capturamos referencias finales para usar dentro del Runnable
            final Entrenador ent1 = e1;
            final Entrenador ent2 = e2;

            Thread hiloDuelo = new Thread(() -> {
                Arena arenaSolicitada = null;
                try {
                    // 1) Solicitar una arena (si no hay libres, queda bloqueado hasta que alguna se libere)
                    arenaSolicitada = arenaManager.solicitaArena();

                    // 2) Decorar la arena con nivel de dificultad aleatorio
                    Arena arenaDecorada = decorarArenaAleatoria(arenaSolicitada);

                    // 3) Construir listas de Pokémon combatientes (una copia de las originales)
                    //    Usamos “new ArrayList<>(...)” para no alterar listas compartidas.
                    ArrayList<Pokemon> equipo1Duelo = new ArrayList<>(ent1.getPokemonesCombatientes());
                    ArrayList<Pokemon> equipo2Duelo = new ArrayList<>(ent2.getPokemonesCombatientes());

                    // 4) Crear objeto Duelo con el constructor refactorizado
                    Duelo duelo = new Duelo(ent1, equipo1Duelo, ent2, equipo2Duelo, arenaDecorada);

                    // 5) Registrar Observers si hay alguna GUI o logger
                    //    Ejemplo: registrar un Observer que imprime mensajes en consola:
                    duelo.addObserver((obs, mensaje) -> System.out.println("[Duelo] " + mensaje));
                    //    En tu aplicación real, podrías registrar la clase que maneje la Vista
                    //    para que dichos mensajes aparezcan en un JTextArea.

                    // 6) Ejecutar el duelo (run() corre todo el combate y asigna créditos/posición)
                    duelo.run();
                    // Nota: si quisieras correrlo en Background y no bloquear este hilo en `run()`,
                    // bastaría con `duelo.run()` porque ya estamos dentro de un Thread propio.
                    // Alternativamente, podrías hacer `new Thread(duelo).start()` sin `join()` aquí,
                    // pero queremos control estricto de la ronda, así que usamos `run()` directo
                    // dentro de este hilo dedicado.

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.err.println("⚠️ Hilo de duelo interrumpido para " 
                        + ent1.getNombre() + " vs " + ent2.getNombre());
                } finally {
                    // 7) Liberar la arena para que otro duelo pueda usarla
                    if (arenaSolicitada != null) {
                        arenaManager.liberaArena(arenaSolicitada);
                    }
                }
            });

            hilos.add(hiloDuelo);
            hiloDuelo.start();
        }

        // Esperar a que todos los hilos de esta ronda terminen
        for (Thread t : hilos) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("⚠️ Esperando finalización de duelo: hilo interrumpido");
            }
        }

        System.out.println("✔ Ronda " + nombreRonda + " finalizada.\n");
    }

    /**
     * Tras terminar una ronda, extrae los entrenadores ganadores para la siguiente:
     * - Recorre la lista “inscritos” y se queda con aquellos cuyo posicionTorneo > 0.
     * - Limpia la lista “inscritos” y la rellena con los ganadores (para la siguiente ronda).
     *
     * @return Lista de entrenadores que avanzan a la siguiente ronda.
     */
    private List<Entrenador> extraerGanadores() {
        List<Entrenador> ganadores = new ArrayList<>();
        synchronized(inscritos) {
            for (Entrenador e : inscritos) {
                if (e.getPosicionTorneo() > 0) {
                    // Ha incrementado su posición en el duelo que ganó
                    ganadores.add(e);
                }
            }
            // Dejar inscritos solo con los ganadores
            inscritos.clear();
            inscritos.addAll(ganadores);
        }
        return ganadores;
    }

    /**
     * Decora una arena base (ya solicitada a arenaManager) con un nivel de dificultad aleatorio.
     * 0 → Fácil, 1 → Medio, 2 → Difícil.
     *
     * @param base Arena “sin decorar” (por ejemplo, ArenaBosque, ArenaDesierto, etc.).
     * @return Una nueva instancia de Arena con el decorador correspondiente.
     */
    private Arena decorarArenaAleatoria(Arena base) {
        int nivel = new Random().nextInt(3); // {0,1,2}
        switch (nivel) {
            case 0:
                return new ArenaFacil(base);
            case 1:
                return new ArenaMedia(base);
            default:
                return new ArenaDificil(base);
        }
    }
}
