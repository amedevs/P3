A continuación te propongo una forma de organizar e implementar toda la segunda parte del trabajo (la que incorpora MVC, Facade, concurrencia, semáforos, Observer/Observable, decorator para las arenas, persistencia, etc.), **usando únicamente los conceptos y estructuras que ya hemos visto a lo largo de este chat**. El objetivo es darte un esqueleto arquitectónico, con los puntos clave señalados, de modo que veas claramente:

1. Cómo encajar la clase `Duelo` (que ya tenés) dentro de un modelo MVC.
2. Dónde aplica la fachada (Facade) para “esconder” la complejidad de iniciar un torneo.
3. Cómo usar semáforos para limitar:

   * A 8 entrenadores simultáneos en el torneo.
   * A 1 duelo por arena (recurso compartido).
4. Cómo llevar el flujo de un torneo por eliminación (cuartos, semis, final).
5. Cómo usar Observer/Observable para notificar el progreso del `Duelo` a la GUI.
6. Cómo aplicar el patrón Decorator para las arenas (modificar premio según dificultad).
7. Dónde manejar la persistencia (al inicio y al final del programa).
8. Dónde lanzar las excepciones si no hay suficientes entrenadores.

### 1. Arquitectura general (alta vista)

En lugar de tener todo mezclado, vamos a separar el sistema en **capas y clases**:

```
┌────────────────────────────────────────────────────────────────────────┐
│                            Interfaz Gráfica (GUI)                     │
│  - View  (por ejemplo, VentanaPrincipalView, TorneoView, DueloView…) │
│  - Observa cambios en modelos y actualiza pantallas                   │
│  - Llama a controladores cuando el usuario hace clic                  │
└────────────────────────────────────────────────────────────────────────┘
             ▲                    ▲                    ▲
             │                    │                    │
             │                    │                    │
┌────────────┴────────────┐ ┌─────┴─────┐ ┌────────────┴────────────┐
│     Controladores       │ │ Facade    │ │   Observers / Listeners │
│ (el “C” en MVC)         │ │Tournament │ │  (por ejemplo, DueloLog)│
│  - MainController       │ │Facade     │ │  (implementan Observer) │
│  - DueloController      │ │           │ │                         │
│  - ArenaController      │ │           │ └─────────────────────────┘
│  - EntrenadorController │ │           │
└────────────┬────────────┘ └───────────┘
             │
             │
             ▼
┌────────────────────────────────────────────────────────────────────────┐
│                               Modelo                                  │
│                                                                        │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐   ┌──────────┐ │
│  │  Entrenador  │   │   Pokemon    │   │     Arena     │   │  Duelo   │ │
│  │(id, nombre,  │   │(stats, tipo, │   │(nombre, dño,  │   │(e1, e2,  │ │
│  │  pokémons…)  │   │   hechizos…) │   │  premioBase)  │   │ método   │ │
│  └──────────────┘   └──────────────┘   └──────────────┘   └──────────┘ │
│        ▲                   ▲                    ▲                       │
│        │                   │                    │                       │
│        ▼                   ▼                    ▼                       │
│  ┌──────────────────────┐ ┌───────────────────┐ ┌───────────────────┐    │
│  │   Torneo (thread)    │ │ ArenaManager      │ │ Persistencia (DAO │    │
│  │ (maneja bracket, semáforos  │(controla el pool   │  o simple XML/BD) │    │
│  │  inscripciones, etc.)│ │  de arenas libres,   ││  de arenas/entren.)│    │
│  └──────────────────────┘ └───────────────────┘ └───────────────────┘    │
└────────────────────────────────────────────────────────────────────────┘
```

* **Vista (View)**: clases Swing/JavaFX (por ejemplo) que muestran listas de entrenadores, pokémons, arenas, estado de duelos, etc.
* **Controladores (Controller)**: reciben acciones de la GUI (botones, formularios) y llaman al Facade para ejecutar procesos completos (por ejemplo, “inscribir 8 entrenadores” o “iniciar torneo”).
* **Fachada (TournamentFacade)**: expone métodos simples como `inscribirEntrenador(Entrenador e)`, `iniciarTorneo()`, `persistirSistema()`, `cargarSistema()`. Internamente orquesta validaciones (≤ 8 entrenadores), semáforos, creación del bracket, hilos de `Duelo`, notificaciones a Observer, etc.
* **Modelo**:

  * **Entrenador**, **Pokemon**, **Arena**, **Duelo**: ya las tenés (Duelo es tu clase de combate).
  * **Torneo**: clase que sabe quiénes están inscritos, cuántas arenas hay, levanta semáforos, crea los hilos de `Duelo` para cada enfrentamiento, actualiza posiciones, cobra premios.
  * **ArenaManager** (u “pool de arenas”): administra un `Semaphore` de “cantidad de arenas disponibles” y entrega/recupera instancias de `Arena` cuando un hilo las necesita.
* **Observadores (Observer)**: por ejemplo, las vistas de “estado de duelo” o “log de batallas” se subscriben a eventos de cada `Duelo` (cada ‘Duelo’ extiende `Observable`). Cuando avanza un turno, notifica, y la Vista se actualiza.
* **Persistencia**: puede hacerse con un DAO sencillo (p. ej. XML, JSON, o BD). Cada vez que el programa arranca, se lee un archivo/BD y se reconstruye la lista de `Entrenador`, `Pokemon` y `Arena`. Al cerrar, se guardan.

---

## 2. Clases y responsabilidades

### 2.1. Modelo principal

#### 2.1.1. `Entrenador`

```java
public class Entrenador {
    private String nombre;
    private List<Pokemon> pokemones;            // todos sus Pokémons
    private List<Pokemon> pokemonesCombatientes; // los que lleva al torneo
    private int creditos;
    // atributo nuevo para seguimiento de ronda:
    private int posicionTorneo = -1; 
    //   -1 = fuera del torneo
    //    1 = cuartos, 2 = semis, 3 = final, 4 = campeón

    // getters y setters...
}
```

* Ya tienes gran parte de esta clase.
* Agregamos `posicionTorneo` para “cuál ronda superó o en qué fase está”.

#### 2.1.2. `Arena` (patrón Decorator para modificar el premio)

```java
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
```

##### 2.1.2.1. Decorators de dificultad

```java
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
```

##### 2.1.2.2. Arenas concretas (sin dificultad)

```java
public class ArenaBosque extends Arena {
    public ArenaBosque() {
        super("Bosque", 700);
    }
    @Override public int getPremio() { return premioBase; }
    @Override public String getDetalle() { return "Bosque (" + premioBase + " créditos)"; }
}

public class ArenaDesierto extends Arena {
    public ArenaDesierto() {
        super("Desierto", 1000);
    }
    @Override public int getPremio() { return premioBase; }
    @Override public String getDetalle() { return "Desierto (" + premioBase + " créditos)"; }
}

public class ArenaSelva extends Arena {
    public ArenaSelva() {
        super("Selva", 800);
    }
    @Override public int getPremio() { return premioBase; }
    @Override public String getDetalle() { return "Selva (" + premioBase + " créditos)"; }
}
```

* **Uso del Decorator**: si en el torneo asignás “ArenaBosque + dificultadMedia”, harías:

  ```java
  Arena base = new ArenaBosque();
  Arena arenaMedia = new ArenaMedia(base);
  ```

  y `arenaMedia.getPremio()` te dará 120% de 700 = 840.

#### 2.1.3. `Duelo` (observable y runnable para concurrencia)

```java
public class Duelo extends Observable implements Runnable {
    private Entrenador e1, e2;
    private Arena arena;             // arena donde se disputa
    private int premioAlGanador;      // calculado de arena.getPremio()
    private boolean terminado = false;

    public Duelo(Entrenador e1, Entrenador e2, Arena arena) {
        if (e1 == null || e2 == null || arena == null)
            throw new IllegalArgumentException("Entrenadores y arena no pueden ser null");
        this.e1 = e1;
        this.e2 = e2;
        this.arena = arena;
        this.premioAlGanador = arena.getPremio();
    }

    @Override
    public void run() {
        // 1) Notificar inicio de duelo
        setChanged(); notifyObservers("Inicio duelo: " + e1.getNombre() + " vs " + e2.getNombre() + " en " + arena.getDetalle());

        // 2) Realizar combate: acá podés reusar tu código de Duelo.iniciaDuelo()
        //    pero adaptado para que use `this.e1` y `this.e2`, sin Scanner (ya es concurrente).
        Entrenador ganador = simularCombateConcurrente();

        // 3) Asignar premio
        ganador.setCreditos(ganador.getCreditos() + premioAlGanador);

        // 4) Actualizar posición del entrenador ganador
        ganador.setPosicionTorneo(ganador.getPosicionTorneo() + 1);

        // 5) Notificar fin de duelo
        setChanged(); 
        notifyObservers("Fin duelo: " + ganador.getNombre() + " obtuvo " + premioAlGanador + " créditos.");
        terminado = true;
    }

    private Entrenador simularCombateConcurrente() {
        // Aquí podés copiar/adaptar la lógica de tu clase Duelo,
        // pero reemplaza los System.out.println por notificaciones a Observers:
        //   setChanged(); notifyObservers("… mensaje de turno …");
        // Y al final devuelve el entrenador ganador.
        // Para simplificar el ejemplo, devolvemos aleatorio:
        return new Random().nextBoolean() ? e1 : e2;
    }

    public boolean isTerminado() { return terminado; }
}
```

* **implements Runnable** para que cada `Duelo` se ejecute en su propio hilo.
* **extends Observable** para que la GUI (o cualquier otro Observer) escuche cada `notifyObservers(...)` y actualice la pantalla en tiempo real.

---

### 2.2. Gestión de recursos compartidos: semáforos

#### 2.2.1. `ArenaManager` (controla el pool de arenas disponibles)

```java
public class ArenaManager {
    private final Semaphore semArenas;
    private final List<Arena> listaArenas; // todas las arenas que existen
    private final Queue<Arena> arenasLibres; // fila de arenas disponibles

    public ArenaManager(List<Arena> todasArenas) {
        this.listaArenas    = new ArrayList<>(todasArenas);
        this.arenasLibres   = new ArrayDeque<>(todasArenas);
        this.semArenas      = new Semaphore(todasArenas.size(), true);
    }

    /** 
     * Bloquea hasta que haya al menos 1 arena libre,
     * devuelve la arena y la saca de arenasLibres.
     */
    public Arena solicitaArena() throws InterruptedException {
        semArenas.acquire(); // si no hay arenas libres, el hilo queda esperando aquí

        synchronized(arenasLibres) {
            return arenasLibres.poll();
        }
    }

    /**
     * Libera la arena (la vuelve a poner en arenasLibres y hace release()).
     */
    public void liberaArena(Arena arena) {
        synchronized(arenasLibres) {
            arenasLibres.offer(arena);
        }
        semArenas.release();
    }
}
```

* El semáforo `semArenas` tiene permisos igual al número de arenas totales.
* `solicitaArena()` bloquea al hilo si todas las arenas están ocupadas, y cuando libera una, cede pase a otro hilo que espera.
* `liberaArena()` se llama al terminar un duelo, para devolver esa arena al pool.

#### 2.2.2. `Torneo` (controla inscripciones, semáforo de entrenadores y bracket)

```java
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
```

* **`semEntrenadores`** limita a 8 entrenadores a la vez. Quienes llamen `inscribir(...)` después de los primeros 8 quedarán bloqueados.
* Cuando `iniciarTorneo()` se ejecuta, se valida `inscritos.size() == 8`. Si es menor, se lanza excepción (`IllegalStateException`), tal como pide la consigna: “si se quiere iniciar con menos de 8, lanza excepción personalizada”.
* Cada `Duelo` se lanza en su propio hilo; antes de correr el `Duelo`, se usa `ArenaManager.solicitaArena()` para “tomar prestada” un arena (si no hay, espera).
* Al finalizar cada `Duelo`, se libera la arena con `arenaManager.liberaArena(...)`, y se integran los ganadores en la siguiente ronda.

---

## 3. Patrón Facade: `TournamentFacade`

La “fachada” es el único punto de entrada que ve la GUI o los controladores principales. Simplifica enormemente:

```java
public class TournamentFacade {
    private Torneo torneo;
    private ArenaManager arenaManager;
    private List<Arena> todasArenas;  
    private List<Entrenador> todosEntrenadores;  // listado global
    private Persistencia persistencia;           // interfaz para guardar/leer

    public TournamentFacade() {
        // 1. Cargar sistema de persistencia (BD, XML o JSON).
        this.persistencia = new PersistenciaXML(); // ejemplo de clase que implementa la lectura/escritura
        SistemaGuardado datos = persistencia.cargar(); 
        this.todosEntrenadores = datos.getEntrenadores();
        this.todasArenas = datos.getArenas();

        // 2. Inicializar ArenaManager con las arenas cargadas
        this.arenaManager = new ArenaManager(todasArenas);

        // 3. Crear torneo
        this.torneo = new Torneo(arenaManager);
    }

    /** Permite a la GUI inscribir a un entrenador */
    public void inscribirEntrenador(Entrenador e) throws InterruptedException {
        if (!todosEntrenadores.contains(e)) {
            todosEntrenadores.add(e);
            persistencia.guardar(todosEntrenadores, todasArenas); // persistir al inscribir
        }
        torneo.inscribir(e);
    }

    /** Devuelve la lista de entrenadores existentes (para la GUI) */
    public List<Entrenador> getListaEntrenadores() {
        return Collections.unmodifiableList(todosEntrenadores);
    }

    /** Inicia el torneo (cuando la GUI solicite “Comenzar torneo”) */
    public void iniciarTorneo() {
        torneo.iniciarTorneo();
    }

    /** Persiste TODO el estado actual (entrenadores, pokemons, arenas, posiciones) */
    public void cerrarYGuardar() {
        // Ejemplo: 
        PersistenciaXML.save(todosEntrenadores, todasArenas);
    }
}
```

* La GUI llama únicamente a `inscribirEntrenador(...)`, `getListaEntrenadores()` y `iniciarTorneo()`.
* **La fachada oculta**:

  * La mecánica del semáforo de entrenadores.
  * El armado del bracket y la concurrencia de duelos.
  * El pool de arena con semáforo.
  * El detalle de persistir en XML/BD.

---

## 4. MVC y Observer/Observable

### 4.1. Modelo (lo ya presentado):

* Clases: `Entrenador`, `Pokemon`, `Arena`, `Duelo`, `Torneo`, `ArenaManager`.

### 4.2. Vista (View):

* **`MainWindow`** (ventana principal) con pestañas o botones para:

  1. Dar de alta entrenadores / pokemones / arenas (Etapa 1).
  2. Inscribir 8 entrenadores (Etapa 2).
  3. Mostrar el bracket y el progreso del torneo (Etapa 3).

```java
public class MainWindow implements Observer {
    private TournamentFacade facade;
    private JFrame frame;
    private JButton btnInscribir, btnIniciarTorneo;
    private JTable tablaEntrenadores;
    private JTextArea areaLogDuelo;

    public MainWindow(TournamentFacade facade) {
        this.facade = facade;
        construirGUI();
        // No hay que registrar como Observer aún; cada Duelo registrará
    }

    private void construirGUI() {
        frame = new JFrame("Torneo Pokémon");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // ... agregar componentes: 
        //  - Formulario de alta de Entrenador (etapa 1)
        //  - Tabla de entrenadores actuales (llamar facade.getListaEntrenadores())
        //  - Botón “Inscribir en torneo” → btnInscribir.addActionListener(e -> inscribirSeleccionado())
        //  - Botón “Iniciar torneo” → btnIniciarTorneo.addActionListener(e -> facade.iniciarTorneo())
        //  - Área de texto o panel donde mostraremos actualizaciones de cada Duelo
    }

    private void inscribirSeleccionado() {
        // Tomar el entrenador seleccionado de la tabla 
        Entrenador e = /*…*/;
        try {
            facade.inscribirEntrenador(e);
            JOptionPane.showMessageDialog(frame, "Entrenador inscrito en torneo");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error al inscribir: " + ex.getMessage());
        }
    }

    /**
     * Método que se llamará cuando cualquiera de los Duelo.llame a notifyObservers(mensaje).
     */
    @Override
    public void update(Observable o, Object arg) {
        // Por ejemplo, cada Duelo puede notificar “Inicio duelo: X vs Y… 
        //” y “Fin duelo: Z ganó…”.
        // Simplemente agregamos ese mensaje al área de log:
        SwingUtilities.invokeLater(() -> {
            areaLogDuelo.append(arg.toString() + "\n");
        });
    }
}
```

* Cada vez que se crea un nuevo `Duelo` (en el `Torneo`), antes de arrancar el hilo, hacemos:

  ```java
  duelo.addObserver(mainWindow);
  ```

  De ese modo, **cada `notifyObservers(...)` dentro de `Duelo.run()`** (o durante el combate) va quedando registrado en la vista.

### 4.3. Controlador (Controller):

* Podría ser una clase intermedia copiada dentro de la GUI, pero en este caso la **fachada** hace de “controlador central”.
* Si quisieras separar aún más, podrías crear un `MainController` que reciba los eventos de `MainWindow` y llame a `TournamentFacade`.
* Ejemplo mínimo:

  ```java
  public class MainController {
      private TournamentFacade facade;
      private MainWindow view;

      public MainController() {
          facade = new TournamentFacade();
          view = new MainWindow(facade);
          // La vista tendrá un método setController(this) para que llame a controller
      }
      public void start() {
          view.setVisible(true);
      }
  }
  ```

---

## 5. Manejo de Posiciones en el Torneo

* En la clase `Entrenador` agregamos:

  ```java
  private int posicionTorneo = -1; // -1=fuera, 1=cuartos, 2=semis, 3=final, 4=campeón
  public int getPosicionTorneo() { return posicionTorneo; }
  public void setPosicionTorneo(int pos) { this.posicionTorneo = pos; }
  ```
* Al terminar cada duelo (en `Duelo.run()`), cuando determinás el ganador:

  ```java
  ganador.setPosicionTorneo(ganador.getPosicionTorneo() + 1);
  ```

  * Si partía en -1, al inscribirse el torneo deberá inicializarlo en 0 o 1 para cuartos.
  * Tras ganar cuartos, su posición pasa de 1→2 (pasa a semifinales), etc.
* El `Torneo` es quien crea los `Duelo` por ronda, y al final del hilo de cada `Duelo` se actualiza el atributo `posicionTorneo`.

---

## 6. Semáforos para controlar concurrencia

1. **Límite de 8 entrenadores**

   * En `Torneo.inscribir(Entrenador)`, usamos `semEntrenadores.acquire()`.
   * El primer entrenador adquiere 1 permiso, el segundo adquiere otro, etc.
   * Al octavo, `semEntrenadores` ya no tiene más permisos libres (0 disponibles), por lo que el noveno que intente inscribirse se bloqueará hasta que:

     * O bien otro entrenador se “retire” (no hacemos retiro dinámico, pero conceptualmente se liberaría el permiso),
     * O el torneo comience (si decidimos “congelar” el semáforo durante el torneo, no se liberará nunca).
   * Cuando el torneo arranca, en lugar de liberar, simplemente se ignoran más inscripciones y si hay menos de 8, lanzamos excepción.

2. **Límite de 1 duelo por arena**

   * El `ArenaManager` se inicializa con tantos permisos en el semáforo como arenas haya.
   * Cada hilo de `Duelo` llama a `arenaManager.solicitaArena()`: si hay permisos (i.e. arenas libres), avanza y “toma” una arena.
   * Si no hay arenas libres, el hilo queda bloqueado hasta que otro hilo llame `arenaManager.liberaArena(...)`.

---

## 7. Excepciones personalizadas

### 7.1. `EntrenadoresInsuficientesException`

```java
public class EntrenadoresInsuficientesException extends RuntimeException {
    public EntrenadoresInsuficientesException(int inscritos) {
        super("No se puede iniciar torneo: faltan " + 
              (8 - inscritos) + " entrenadores para completar 8.");
    }
}
```

* Se lanza en `Torneo.iniciarTorneo()` si `inscritos.size() < 8`.

### 7.2. `ArenaNoDisponibleException` (opcional)

```java
public class ArenaNoDisponibleException extends Exception {
    public ArenaNoDisponibleException() {
        super("No hay arenas disponibles en este momento.");
    }
}
```

* Podrías lanzarla en `ArenaManager.solicitaArena()` si decidís no bloquear, sino devolver error inmediatamente.
* En nuestro diseño actual usamos bloqueo en el semáforo, pero podríamos cambiar para que devuelva excepción si no hay permisos.

---

## 8. Persistencia (DAO simple o fichero XML/JSON)

Para guardar el sistema completo al cerrar la aplicación y recuperarlo al iniciar:

### 8.1. Interfaz `Persistencia`

```java
public interface Persistencia {
    SistemaGuardado cargar() throws IOException, ...;
    void guardar(List<Entrenador> entrenadores, List<Arena> arenas) throws IOException, ...;
}
```

* `SistemaGuardado` podría ser una clase POJO con:

  ```java
  public class SistemaGuardado {
      private List<Entrenador> entrenadores;
      private List<Arena> arenas;
      // getters y setters
  }
  ```
* **Implementación simple**: con un archivo JSON o XML (p. ej. usando `XMLEncoder/Decoder` de Java).
* Al iniciar la `TournamentFacade()`, se llama `persistencia.cargar()` y se rellenan `todosEntrenadores` y `todasArenas`.
* Al cerrar, se llama `persistencia.guardar(...)`.

---

## 9. Flujo completo de uso

1. **Arranca la aplicación**

   ```java
   public static void main(String[] args) {
       TournamentFacade facade = new TournamentFacade();
       MainController    controller = new MainController(facade);
       controller.start(); // muestra la GUI
   }
   ```

   * `TournamentFacade` en su constructor invoca `persistencia.cargar()`.
   * Se reconstruyen `todosEntrenadores` y `todasArenas`.

2. **Alta de entrenadores, pokémones y arenas** (Etapa 1)

   * La **Vista** muestra un formulario para dar de alta entrenadores y pokémones:

     * Llama a `facade.getListaEntrenadores()` para mostrar los ya cargados.
     * Cuando el usuario llena “Nombre del entrenador” y “Pokémons iniciales”,
       el controlador hace `facade.altaEntrenador(nuevoEntrenador)`.
     * `TournamentFacade.altaEntrenador(...)`:

       1. Agrega a `todosEntrenadores`.
       2. Llama a `persistencia.guardar(...)`.
   * Para arenas, la vista pide “crear ArenaBosque/Dificultad Media” y llama a
     `facade.altaArena(new ArenaMedia(new ArenaBosque()))`.

3. **Inscripción de 8 entrenadores** (Etapa 2)

   * La Vista muestra la lista de entrenadores actuales (traída de `facade.getListaEntrenadores()`).
   * El usuario selecciona hasta 8 y presiona “Inscribir en torneo”.

     * El controlador hace, para cada uno, `facade.inscribirEntrenador(seleccionado)`.

       * Internamente: `torneo.inscribir(...)` (hace `semEntrenadores.acquire()` y agrega a la lista interna de inscritos).
       * Si son más de 8 simultáneamente, los extras quedan bloqueados hasta que se libere un permiso (o se cierre el torneo).
   * Si el usuario presiona “Comenzar torneo” y hay menos de 8, se lanza `EntrenadoresInsuficientesException`, se muestra error en GUI.

4. **Desarrollo del torneo** (Etapa 3)

   * Al hacer clic en “Iniciar torneo”, el controlador llama `facade.iniciarTorneo()`.
   * Esto se traduce en `torneo.iniciarTorneo()`:

     1. Verifica que haya exactamente 8 inscritos, sino lanza excepción.
     2. Llama a `armarBracketYLanzarDuelo(inscritos)`.
     3. Dentro de ese método:

        * Por cada enfrentamiento de cuartos (4 duelos), crea un objeto `Duelo(e1, e2, arenaDecorada)`.
        * Registra la vista como `Observer` de cada `Duelo`.
        * Lanza un hilo para cada `Duelo`: el hilo pide un arena a `arenaManager` (si no hay, bloquea hasta que se libere).
        * El hilo ejecuta `duelo.run()`, que:

          * Notifica “Inicio duelo…” con `notifyObservers(...)`.
          * Ejecuta la lógica de combate (adaptada de tu clase `Duelo`), enviando actualizaciones parciales también con `notifyObservers("… turno X …")`.
          * Al final, notifica “Fin duelo: X ganó…” y actualiza `Entrenador.posicionTorneo` y créditos.
          * Llama a `arenaManager.liberaArena(...)` para devolver la arena al pool.
        * El método principal de `Torneo` hace `t.join()` sobre todos los hilos de cuartos.
        * Una vez terminados los cuartos, extrae los 4 ganadores (con `d.getGanador()`),
          arma los 2 duelos de semifinal, y repite el mismo proceso.
        * Luego lanza el duelo final, etc.

5. **Observación en tiempo real (Observer/Observable)**

   * Cada `Duelo` extiende `Observable`. Cuando ocurra un evento relevante (inicio, turno, fin), hace:

     ```java
     setChanged();
     notifyObservers("… texto de estado …");
     ```
   * La **Vista**, que ya se registró con `duelo.addObserver(ventana)`, recibe en su método `update(Observable o, Object arg)` cada mensaje.

     * Por ejemplo, `arg` contiene “Charmander ataca a Pikachu… 🥊” o “Fin duelo: Ash ganó 700 créditos”.
   * La Vista actualiza un componente (`JTextArea` o `JTable`) con esa línea de log.

6. **Persistencia al finalizar y al iniciar**

   * Al cerrar la aplicación (por ejemplo, al atrapar el evento `windowClosing` de la ventana principal), el controlador llama `facade.cerrarYGuardar()`.

     * Eso hace `persistencia.guardar(todosEntrenadores, todasArenas)`, volcando todo en XML o JSON.
   * Al arrancar la aplicación, el constructor de `TournamentFacade` invoca `persistencia.cargar()` y carga la lista de entrenadores y arenas.

---

## 10. Esquema completo de excepciones

* **`EntrenadoresInsuficientesException`**: si el torneo intenta arrancar con menos de 8.
* **`InscripcionException`**: si alguien quiere inscribirse y la lista de entrenadores ya contiene al Pokémon, o cualquier validación extra.
* **`ArenaNoDisponibleException`** (opcional): si, en vez de bloquear, querés lanzar excepción cuando no hay arena libre.
* **`EntrenadorSinPokemonesException`** y **`EquipoSinSaludException`**: ya los tenías en tu Duelo para validar cada combate.

---

## 11. Recapitulación final: ¿Dónde usar cada patrón/estructura?

| Patrón / Estructura         | ¿Dónde?                                                                                                                                                                                                                                                          |
| --------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **MVC**                     | - **Modelo**: clases `Entrenador`, `Pokemon`, `Arena`, `Duelo`, `Torneo`, `ArenaManager`.                        <br> - **Vista**: `MainWindow` (y sub-vistas).                                  <br> - **Controlador**: `MainController` (o la propia fachada). |
| **Facade**                  | - `TournamentFacade`: expone métodos simples como `inscribirEntrenador()`, `iniciarTorneo()`, `cerrarYGuardar()`.                                                                                                                                                |
| **Singleton**               | - `Mundo` (si querés seguir usándolo), o bien el `TournamentFacade` puede implementarse como singleton.                                                                                                                                                          |
| **Observer/Observable**     | - `Duelo extends Observable` y la Vista implementa `Observer` para recibir actualizaciones en tiempo real.                                                                                                                                                       |
| **Decorator**               | - Para las arenas: `ArenaFacil`, `ArenaMedia`, `ArenaDificil` decoran una arena base (`ArenaBosque`, etc.).                                                                                                                                                      |
| **Concurrency / Semáforos** | - En `Torneo`: `Semaphore semEntrenadores(8)`.                              <br> - En `ArenaManager`: `Semaphore semArenas(numArenas)`.                                                                                                                          |
| **DAO / Persistencia**      | - Interfaz `Persistencia` y su implementación (`PersistenciaXML` o similar) para guardar/leer entrenadores y arenas.                                                                                                                                             |
| **DTO**                     | - No es estrictamente obligatorio aquí, dado que el GUI puede usar directamente `Entrenador`; <br>   si quisieras separar, podrías tener un `EntrenadorDTO` para transferir solo “nombre, créditos y posición”.                                                  |

---

## 12. Respuesta a tu duda concreta sobre el ejemplo

> **“No entiendo, no entendía por qué `ClienteService.java` no creaba ningún TransferObject, porque creía que el DataSource creaba el DTO”**

En el ejemplo de DAO/DTO que vimos, **el DAO devolvía la entidad completa** (p. ej. un `Cliente`), y era el **Service (BusinessObject)** quien creaba el DTO con los campos que realmente interesaban para la capa de presentación.
Ese mismo razonamiento apply aquí:

* **`Duelo` no crea DTO**: su “DTO” equivalente es el conjunto de notificaciones (`Strings`) que envía con `notifyObservers(...)`.
* La **Vista** simplemente recibe esas “líneas de texto” y las muestra en pantalla; no necesita un objeto complejo.
* Si quisiéramos separar “los datos puros” de un duelo para mostrarlos en una tabla, podríamos tener un `DueloDTO` (por ejemplo, con campos “entrenador1”, “entrenador2”, “ganador”, “premio”, “ronda”), y crearlo justo después de que el combate haya terminado, para pasárselo a la Vista. Pero en este caso utilizamos el propio `Observable` para enviar mensajes de texto directos.

---

## 13. Apuntes finales

* **Duelo** (tu clase original) puede quedar tal cual para la Etapa 1 (combate uno a uno por consola).
* Para la Etapa 2, **no modificas la lógica interna de `Duelo`**, sino que adaptas `Duelo` para quitar el `Scanner` (no pedir input manual en consola, sino basarte en decisiones preestablecidas o simuladas) y para que, en lugar de `System.out.println()`, use `notifyObservers(...)` para que la GUI reciba cada mensaje.
* **`Torneo`** se encarga de instanciar múltiples `Duelo` en hilos distintos, pero controlados por semáforos.
* **La Vista** se limita a dibujar formularios y log de texto, **suscribiéndose** a cada `Duelo` que se lanza.
* La **fachada** es el punto de enganche único: la Vista/Controlador nunca toca directamente `Torneo`, `ArenaManager`, `Duelo` o semáforos; solo llama a `TournamentFacade`.

Con esta estructura tienes una **arquitectura sólida**, que:

* **Separa responsabilidades** (lógica, acceso a datos, presentación).
* **Cumple todos los requisitos**: MVC, Facade, concurrencia con semáforos, Observer/Observable, Decorator en arenas, persistencia antes/después de torneo.
* **Es escalable y mantenible**, porque si mañana cambia la forma de persistir (pasa de XML a MySQL), solo modificás la clase que implementa `Persistencia`, sin tocar la lógica de torneo ni la GUI.

---

### ¡Listo! Con esto deberías ver con claridad cómo encajan todos los conceptos y cómo organizar tu código para la segunda entrega. Si te queda alguna duda concreta o querés ver un trozo de código más detallado (por ejemplo, la implementación de `Torneo` completa o el setup de la GUI), avisame y lo vemos. ¡Éxitos con esa segunda etapa!
