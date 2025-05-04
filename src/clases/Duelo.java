package clases;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import clases.hechizo.Hechizo;
import clases.hechizo.Hechizo.TipoHechizo;
import clases.pokemon.Pokemon;
import excepciones.EntrenadorSinPoquemonesException;
import excepciones.EquipoSinSaludException;

/**
 * Clase que maneja el sistema de combate entre dos entrenadores Pokémon.
 * Permite la selección de Pokémon, uso de hechizos y manejo de turnos de combate.
 * 
 * @author Sistema de Combate Pokémon
 * @version 1.0
 */
public class Duelo {
	/** Número máximo de Pokémon permitidos por equipo */
	private final int maxPokemon = 3;
	
	/** Entrenadores participantes en el duelo */
	private Entrenador entrenador1, entrenador2;
	
	/** Scanner para la entrada del usuario */
	private Scanner scanner;
	
	/** Frases de ataque disponibles para los combates */
	private static final String[] ATAQUES = {
		"lanza un poderoso ataque 💥",
		"usa una técnica secreta 🌀",
		"golpea con todas sus fuerzas 🥊",
		"ataca velozmente ⚡",
		"desata su furia 🔥",
		"hace un movimiento sorpresa 🤸"
	};

	/** Frases de defensa disponibles para los combates */
	private static final String[] DEFENSAS = {
		"resiste el golpe como un campeón 🛡️",
		"esquiva hábilmente el ataque 🏃‍♂️",
		"bloquea el ataque a tiempo ⛔",
		"soporta el daño con valentía 💪",
		"se tambalea pero sigue en pie 😵‍💫"
	};
	
	/**
	 * Constructor de la clase Duelo.
	 * 
	 * @param e1 Primer entrenador participante
	 * @param e2 Segundo entrenador participante
	 * @throws IllegalArgumentException Si alguno de los entrenadores es null
	 */
	public Duelo(Entrenador e1, Entrenador e2) {
		if (e1 == null || e2 == null) {
			throw new IllegalArgumentException("Los entrenadores no pueden ser null");
		}
		this.entrenador1 = e1;
		this.entrenador2 = e2;
		this.scanner = new Scanner(System.in);
	}
	
	/**
	 * Permite al entrenador seleccionar un Pokémon para el combate.
	 * 
	 * @param indice Índice actual del Pokémon en el equipo
	 * @param equipo Lista de Pokémon disponibles del entrenador
	 * @param entrenador Entrenador que realiza la selección
	 * @return El Pokémon seleccionado, o null si no hay Pokémon disponibles
	 */
	private Pokemon seleccionaPokemon(int indice, ArrayList<Pokemon> equipo, Entrenador entrenador) {
		System.out.println("\n" + entrenador.getNombre() + ", selecciona tu Pokémon para el combate:");
		ArrayList<Pokemon> pokemonesDisponibles = filtrarPokemonesConVida(equipo);
		
		if (pokemonesDisponibles.isEmpty()) {
			System.out.println("No hay Pokémon disponibles con vida.");
			return null;
		}
		
		mostrarPokemonesDisponibles(pokemonesDisponibles);
		return obtenerSeleccionPokemon(pokemonesDisponibles, indice, equipo, entrenador);
	}
	
	/**
	 * Filtra los Pokémon que tienen vida mayor a 0.
	 * 
	 * @param equipo Lista de Pokémon a filtrar
	 * @return Lista de Pokémon con vida disponible
	 */
	private ArrayList<Pokemon> filtrarPokemonesConVida(ArrayList<Pokemon> equipo) {
		ArrayList<Pokemon> pokemonesDisponibles = new ArrayList<>();
		for (int i = 0; i < equipo.size() && i < maxPokemon; i++) {
			Pokemon p = equipo.get(i);
			if (p.getVida() > 0) {
				pokemonesDisponibles.add(p);
			}
		}
		return pokemonesDisponibles;
	}
	
	/**
	 * Muestra la lista de Pokémon disponibles para selección.
	 * 
	 * @param pokemonesDisponibles Lista de Pokémon a mostrar
	 */
	private void mostrarPokemonesDisponibles(ArrayList<Pokemon> pokemonesDisponibles) {
		for (int i = 0; i < pokemonesDisponibles.size(); i++) {
			Pokemon p = pokemonesDisponibles.get(i);
			System.out.println((i + 1) + " - " + p.getNombre() + " (Vida: " + p.getVida() + ")");
		}
	}
	
	/**
	 * Obtiene la selección del usuario para un Pokémon.
	 * 
	 * @param pokemonesDisponibles Lista de Pokémon disponibles
	 * @param indice Índice actual del Pokémon
	 * @param equipo Equipo completo del entrenador
	 * @param entrenador Entrenador que realiza la selección
	 * @return El Pokémon seleccionado
	 */
	private Pokemon obtenerSeleccionPokemon(ArrayList<Pokemon> pokemonesDisponibles, int indice, ArrayList<Pokemon> equipo, Entrenador entrenador) {
		int seleccion = scanner.nextInt() - 1;
		if (seleccion >= 0 && seleccion < pokemonesDisponibles.size()) {
			return pokemonesDisponibles.get(seleccion);
		} else {
			System.out.println("Selección inválida. Intenta de nuevo.");
			return seleccionaPokemon(indice, equipo, entrenador);
		}
	}
	
	/**
	 * Permite al entrenador lanzar un hechizo al Pokémon adversario.
	 * 
	 * @param entrenador Entrenador que lanza el hechizo
	 * @param pokemonAdversario Pokémon objetivo del hechizo
	 */
	private void lanzarCartaDeHechizo(Entrenador entrenador, Pokemon pokemonAdversario) {
		if (entrenador.getHechizos().isEmpty()) return;
		
		mostrarOpcionesHechizos(entrenador);
		Hechizo hechizoSeleccionado = obtenerHechizoSeleccionado(entrenador);
		
		if (hechizoSeleccionado != null) {
			entrenador.lanzarHechizoAAdversario(hechizoSeleccionado, pokemonAdversario);
		} else {
			System.out.println(entrenador.getNombre() + " no lanzó ningún hechizo.");
		}
	}
	
	/**
	 * Muestra las opciones de hechizos disponibles para el entrenador.
	 * 
	 * @param entrenador Entrenador que selecciona el hechizo
	 */
	private void mostrarOpcionesHechizos(Entrenador entrenador) {
		System.out.println("\n" + entrenador.getNombre() + " elige un hechizo para tirar");
		if (!entrenador.getHechizosPorTipo(TipoHechizo.NIEBLA).isEmpty())
			System.out.println("1 - Niebla");
		if (!entrenador.getHechizosPorTipo(TipoHechizo.TORMENTA).isEmpty())
			System.out.println("2 - Tormenta");
		if (!entrenador.getHechizosPorTipo(TipoHechizo.VIENTO).isEmpty())
			System.out.println("3 - Viento");
		System.out.println("(otro) - ninguno");
	}
	
	/**
	 * Obtiene el hechizo seleccionado por el entrenador.
	 * 
	 * @param entrenador Entrenador que selecciona el hechizo
	 * @return El hechizo seleccionado, o null si no se seleccionó ninguno
	 */
	private Hechizo obtenerHechizoSeleccionado(Entrenador entrenador) {
		int indiceHechizo = scanner.nextInt();
		ArrayList<Hechizo> cartasHechizo = new ArrayList<>();
		
		switch (indiceHechizo) {
			case 1:
				cartasHechizo = entrenador.getHechizosPorTipo(TipoHechizo.NIEBLA);
				break;
			case 2:
				cartasHechizo = entrenador.getHechizosPorTipo(TipoHechizo.TORMENTA);
				break;
			case 3:
				cartasHechizo = entrenador.getHechizosPorTipo(TipoHechizo.VIENTO);
				break;
		}
		
		return cartasHechizo.isEmpty() ? null : cartasHechizo.get(0);
	}
	
	/**
	 * Realiza un turno de ataque entre dos Pokémon.
	 * 
	 * @param atacante Pokémon que realiza el ataque
	 * @param defensor Pokémon que recibe el ataque
	 * @param mostrarMensajes Indica si se deben mostrar mensajes del combate
	 */
	private void realizarTurnoAtaque(Pokemon atacante, Pokemon defensor, boolean mostrarMensajes) {
		if (mostrarMensajes) {
			System.out.println("👉 " + atacante.getNombre() + " " + fraseAleatoria(ATAQUES) + " a " + defensor.getNombre());
		}
		
		double vidaAntes = defensor.getVida();
		atacante.atacar(defensor);
		
		if (mostrarMensajes && defensor.getVida() < vidaAntes) {
			System.out.println("   " + defensor.getNombre() + " " + fraseAleatoria(DEFENSAS));
		}
	}
	
	/**
	 * Muestra el resultado de un combate individual.
	 * 
	 * @param ganador Pokémon ganador del combate
	 * @param perdedor Pokémon perdedor del combate
	 */
	private void mostrarResultadoCombate(Pokemon ganador, Pokemon perdedor) {
		System.out.println("💀 " + perdedor.getNombre() + " ha sido derrotado...");
		System.out.println("🏆 " + ganador.getNombre() + " celebra su victoria en este duelo.");
	}
	
	/**
	 * Actualiza la experiencia del Pokémon ganador.
	 * 
	 * @param ganador Pokémon al que se le actualiza la experiencia
	 */
	private void actualizarXP(Pokemon ganador) {
		ganador.setXP(ganador.getCategoria() + 1);
	}
	
	/**
	 * Realiza el recambio de un Pokémon derrotado.
	 * 
	 * @param entrenador Entrenador que realiza el recambio
	 * @param equipo Equipo del entrenador
	 * @param indice Índice del Pokémon actual
	 * @return El nuevo Pokémon seleccionado, o null si no hay disponibles
	 */
	private Pokemon realizarRecambio(Entrenador entrenador, ArrayList<Pokemon> equipo, int indice) {
		Pokemon nuevoPokemon = seleccionaPokemon(indice, equipo, entrenador);
		if (nuevoPokemon != null) {
			System.out.println("\n" + entrenador.getNombre() + " envía a " + nuevoPokemon.getNombre() + " al combate.");
		} else {
			System.out.println("\n" + entrenador.getNombre() + " se ha quedado sin Pokémon disponibles.");
		}
		return nuevoPokemon;
	}
	
	/**
	 * Muestra el resultado final del duelo.
	 * 
	 * @param ganador Entrenador ganador del duelo
	 * @param perdedor Entrenador perdedor del duelo
	 */
	private void mostrarResultadoFinal(Entrenador ganador, Entrenador perdedor) {
		System.out.println("\n¡El combate ha terminado!");
		System.out.println("🎉 ¡" + ganador.getNombre() + " es el ganador del duelo!");
		ganador.setCreditos(ganador.getCreditos() + 500);
		
		System.out.println("--------------------------------------------------");
		System.out.println("Créditos de " + ganador.getNombre() + ": " + ganador.getCreditos());
		System.out.println("Créditos de " + perdedor.getNombre() + ": " + perdedor.getCreditos());
	}
	
	/**
	 * Inicia el duelo entre los dos entrenadores.
	 * 
	 * @throws EntrenadorSinPoquemonesException Si algún entrenador no tiene Pokémon
	 * @throws EquipoSinSaludException Si ningún Pokémon tiene vida disponible
	 */
	public void iniciaDuelo() throws EntrenadorSinPoquemonesException {
		ArrayList<Pokemon> equipo1 = entrenador1.getPokemonesCombatientes();
		ArrayList<Pokemon> equipo2 = entrenador2.getPokemonesCombatientes();
		
		try {
			validarEquipos(equipo1, equipo2);
			
			Pokemon pokemon1 = seleccionaPokemon(-1, equipo1, entrenador1);
			Pokemon pokemon2 = seleccionaPokemon(-1, equipo2, entrenador2);
			
			if (pokemon1 == null) throw new EquipoSinSaludException(entrenador1);
			if (pokemon2 == null) throw new EquipoSinSaludException(entrenador2);
			
			int indice1 = equipo1.indexOf(pokemon1);
			int indice2 = equipo2.indexOf(pokemon2);
			
			iniciarCombate(pokemon1, pokemon2, indice1, indice2, equipo1, equipo2);
			
		} catch(Exception e) {
			System.out.println("\n❗ Error durante el combate: " + e.getMessage());
		}
	}
	
	/**
	 * Valida que los equipos de los entrenadores no estén vacíos.
	 * 
	 * @param equipo1 Equipo del primer entrenador
	 * @param equipo2 Equipo del segundo entrenador
	 * @throws EntrenadorSinPoquemonesException Si algún equipo está vacío
	 */
	private void validarEquipos(ArrayList<Pokemon> equipo1, ArrayList<Pokemon> equipo2) throws EntrenadorSinPoquemonesException {
		if (equipo1.isEmpty()) throw new EntrenadorSinPoquemonesException(entrenador1);
		if (equipo2.isEmpty()) throw new EntrenadorSinPoquemonesException(entrenador2);
	}
	
	/**
	 * Inicia el combate entre los dos entrenadores.
	 * 
	 * @param pokemon1 Pokémon inicial del primer entrenador
	 * @param pokemon2 Pokémon inicial del segundo entrenador
	 * @param indice1 Índice del Pokémon del primer entrenador
	 * @param indice2 Índice del Pokémon del segundo entrenador
	 * @param equipo1 Equipo del primer entrenador
	 * @param equipo2 Equipo del segundo entrenador
	 */
	private void iniciarCombate(Pokemon pokemon1, Pokemon pokemon2, int indice1, int indice2, 
							  ArrayList<Pokemon> equipo1, ArrayList<Pokemon> equipo2) {
		System.out.println("\n¡El combate va a comenzar!");
		System.out.println(entrenador1.getNombre() + " vs " + entrenador2.getNombre());
		System.out.println("--------------------------------------------------");
		
		while (indice1 < maxPokemon && indice2 < maxPokemon) {
			System.out.println("\n¡Nuevo enfrentamiento!");
			System.out.println("⚔️  " + pokemon1.getNombre() + " (" + entrenador1.getNombre() + ") VS " +
							 pokemon2.getNombre() + " (" + entrenador2.getNombre() + ")");

			lanzarCartaDeHechizo(entrenador1, pokemon2);
			lanzarCartaDeHechizo(entrenador2, pokemon1);

			realizarEnfrentamiento(pokemon1, pokemon2);
			
			if (pokemon1.getVida() <= 0) {
				mostrarResultadoCombate(pokemon2, pokemon1);
				actualizarXP(pokemon2);
				pokemon1 = realizarRecambio(entrenador1, equipo1, indice1);
				indice1 = (pokemon1 != null) ? equipo1.indexOf(pokemon1) : maxPokemon;
			} else {
				mostrarResultadoCombate(pokemon1, pokemon2);
				actualizarXP(pokemon1);
				pokemon2 = realizarRecambio(entrenador2, equipo2, indice2);
				indice2 = (pokemon2 != null) ? equipo2.indexOf(pokemon2) : maxPokemon;
			}
			System.out.println("--------------------------------------------------");
		}
		
		mostrarResultadoFinal(
			indice1 < maxPokemon ? entrenador1 : entrenador2,
			indice1 < maxPokemon ? entrenador2 : entrenador1
		);
	}
	
	/**
	 * Realiza el enfrentamiento entre dos Pokémon.
	 * 
	 * @param pokemon1 Primer Pokémon en combate
	 * @param pokemon2 Segundo Pokémon en combate
	 */
	private void realizarEnfrentamiento(Pokemon pokemon1, Pokemon pokemon2) {
		int turnos = 0;
		boolean combateTerminado = false;

		while (pokemon1.getVida() > 0 && pokemon2.getVida() > 0 && !combateTerminado) {
			turnos++;
			boolean mostrarMensajes = (turnos % 5 == 0);
			
			realizarTurnoAtaque(pokemon1, pokemon2, mostrarMensajes);
			if (pokemon2.getVida() <= 0) {
				combateTerminado = true;
			} else {
				realizarTurnoAtaque(pokemon2, pokemon1, mostrarMensajes);
				if (pokemon1.getVida() <= 0) {
					combateTerminado = true;
				}
			}
		}
	}

	/**
	 * Selecciona una frase aleatoria del array proporcionado.
	 * 
	 * @param frases Array de frases disponibles
	 * @return Una frase aleatoria del array
	 */
	private String fraseAleatoria(String[] frases) {
		return frases[new Random().nextInt(frases.length)];
	}
}
