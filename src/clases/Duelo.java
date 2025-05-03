package clases;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import clases.hechizo.Hechizo;
import clases.hechizo.Hechizo.TipoHechizo;
import clases.pokemon.Pokemon;
import excepciones.EntrenadorSinPoquemonesException;
import excepciones.EquipoSinSaludException;

public class Duelo {
	private final int maxPokemon = 3;
	private Entrenador entrenador1, entrenador2;
	
	public Duelo(Entrenador e1, Entrenador e2) {
		this.entrenador1 = e1;
		this.entrenador2 = e2;
	}
	
	// Devuelve el primer Pokémon combatiente con salud
	private Pokemon seleccionaPokemon(int indice, ArrayList<Pokemon> equipo) {
		Pokemon pokemon = null;
		
		indice++;
		while (indice<equipo.size() && indice<maxPokemon) {
			pokemon = equipo.get(indice);
			if (pokemon.getVida()<=0)
				indice++;
			else
				indice = maxPokemon;
		}

		return pokemon;
	}
	
	private void lanzarCartaDeHechizo(Entrenador entrenador, Pokemon pokemonAdversario) {
		if (!entrenador.getHechizos().isEmpty()) {
			System.out.println("\n" + entrenador.getNombre() + " elige un hechizo para tirar");
	        if (!entrenador.getHechizosPorTipo(TipoHechizo.NIEBLA).isEmpty())
	            System.out.println("1 - Niebla");
	        if (!entrenador.getHechizosPorTipo(TipoHechizo.TORMENTA).isEmpty())
	            System.out.println("2 - Tormenta");
	        if (!entrenador.getHechizosPorTipo(TipoHechizo.VIENTO).isEmpty())
	            System.out.println("3 - Viento");
	        System.out.println("(otro) - ninguno");
	        
	        Scanner scanner = new Scanner(System.in);
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
	            default:
	                cartasHechizo.clear();
	                break;
	        }
	        if (!cartasHechizo.isEmpty()) {
	            entrenador.lanzarHechizoAAdversario(cartasHechizo.get(0), pokemonAdversario);
	        } else {
	            System.out.println(entrenador.getNombre() + "no lanzó ningún hechizo.");
	        }
		}
	}
	
	public void iniciaDuelo() throws EntrenadorSinPoquemonesException {
		ArrayList<Pokemon> equipo1 = entrenador1.getPokemonesCombatientes(),
							equipo2 = entrenador2.getPokemonesCombatientes();
		int indice1, indice2;
		Pokemon pokemon1, pokemon2;
		
		try {
			// Verifica que los equipos no estén vacíos
			if (equipo1.isEmpty())
				throw new EntrenadorSinPoquemonesException(entrenador1); // Acá hubo un comentario pidiendo que la línea haga lo que hace
			if (equipo2.isEmpty())
				throw new EntrenadorSinPoquemonesException(entrenador2); // Acá hubo un comentario simil al de arriba
			
			// Verifica que haya pokemones con salud y selecciona el primero apto de cada equipo
			// Equipo 1
			pokemon1 = this.seleccionaPokemon(-1, equipo1);
			if (pokemon1 == null)
				throw new EquipoSinSaludException(entrenador1);
			else
				indice1 = equipo1.indexOf(pokemon1);
			// Equipo 2
			pokemon2 = this.seleccionaPokemon(-1, equipo2);
			if (pokemon2 == null)
				throw new EquipoSinSaludException(entrenador2);
			else
				indice2 = equipo2.indexOf(pokemon2);		
			// Duelo
			System.out.println("\n¡El combate va a comenzar!");
            System.out.println(entrenador1.getNombre() + " vs " + entrenador2.getNombre());
            System.out.println("--------------------------------------------------");
            while (indice1 < maxPokemon && indice2 < maxPokemon) {
                System.out.println("\n¡Nuevo enfrentamiento!");
                System.out.println("⚔️  " + pokemon1.getNombre() + " (" + entrenador1.getNombre() + ") VS " +
                                   pokemon2.getNombre() + " (" + entrenador2.getNombre() + ")");

                this.lanzarCartaDeHechizo(entrenador1, pokemon2); // Hechizo 1 a 2
                this.lanzarCartaDeHechizo(entrenador2, pokemon1); // Hechizo 2 a 1

                int turnos = 0;
                boolean combateTerminado = false;

                //enfrentamiento 1 a 1
                while (pokemon1.getVida() > 0 && pokemon2.getVida() > 0 && !combateTerminado) {
                	turnos++;
                    // Solo mostrar mensajes de ataque y defensa cada 5 turnos
                    boolean mostrarMensajes = (turnos % 5 == 0);
                    // Ataque de pokemon1 a pokemon2
                    if (mostrarMensajes) {
                        System.out.println("👉 " + pokemon1.getNombre() + " " + fraseAleatoria(ATAQUES) + " a " + pokemon2.getNombre());
                    }
                    double vidaAntes = pokemon2.getVida();
                    
                    pokemon1.atacar(pokemon2);
                    if (mostrarMensajes && pokemon2.getVida() < vidaAntes) {
                        System.out.println("   " + pokemon2.getNombre() + " " + fraseAleatoria(DEFENSAS));
                    }
                    if (pokemon2.getVida() <= 0) {
                        combateTerminado = true;
                    } else {
                        // Ataque de pokemon2 a pokemon1
                        if (mostrarMensajes) {
                            System.out.println("👉 " + pokemon2.getNombre() + " " + fraseAleatoria(ATAQUES) + " a " + pokemon1.getNombre());
                        }
                        vidaAntes = pokemon1.getVida();
                        pokemon2.atacar(pokemon1);
                        if (mostrarMensajes && pokemon1.getVida() < vidaAntes) {
                            System.out.println("   " + pokemon1.getNombre() + " " + fraseAleatoria(DEFENSAS));
                        }
                        if (pokemon1.getVida() <= 0) {
                            combateTerminado = true;
                        }
                    }
                }
                // Mensaje de derrota
                if (pokemon1.getVida() <= 0) {
                    System.out.println("💀 " + pokemon1.getNombre() + " ha sido derrotado...");
                    System.out.println("🏆 " + pokemon2.getNombre() + " celebra su victoria en este duelo.");
                } else {
                    System.out.println("💀 " + pokemon2.getNombre() + " ha sido derrotado...");
                    System.out.println("🏆 " + pokemon1.getNombre() + " celebra su victoria en este duelo.");
                }

                // Suma XP
                if (pokemon1.getVida() > 0)
                    pokemon1.setXP(pokemon1.getCategoria() + 1);
                else
                    pokemon2.setXP(pokemon2.getCategoria() + 1);

                // El recambio
                if (pokemon1.getVida() <= 0) {
                    pokemon1 = this.seleccionaPokemon(indice1, equipo1);
                    indice1 = (pokemon1 != null) ? equipo1.indexOf(pokemon1) : maxPokemon;
                    if (pokemon1 != null)
                        System.out.println("\ n " entrenador1.getNombre() + " envía a " + pokemon1.getNombre() + " al combate.");
                    else
                        System.out.println("\ n "entrenador1.getNombre() + " se ha quedado sin Pokémon disponibles.");
                } else {
                    pokemon2 = this.seleccionaPokemon(indice2, equipo2);
                    indice2 = (pokemon2 != null) ? equipo2.indexOf(pokemon2) : maxPokemon;
                    if (pokemon2 != null)
                        System.out.println("\ n "entrenador2.getNombre() + " envía a " + pokemon2.getNombre() + " al combate.");
                    else
                        System.out.println("\ n "entrenador2.getNombre() + " se ha quedado sin Pokémon disponibles.");
                }
                System.out.println("--------------------------------------------------");
            }

            // Mensaje final de combate
            System.out.println("\n¡El combate ha terminado!");
            if (indice1 < maxPokemon) {
                System.out.println("🎉 ¡" + entrenador1.getNombre() + " es el ganador del duelo!");
                entrenador1.setCreditos(entrenador1.getCreditos() + 500);
            } else {
                System.out.println("🎉 ¡" + entrenador2.getNombre() + " es el ganador del duelo!");
                entrenador2.setCreditos(entrenador2.getCreditos() + 500);
            }
            System.out.println("--------------------------------------------------");
            System.out.println("Créditos de " + entrenador1.getNombre() + ": " + entrenador1.getCreditos());
            System.out.println("Créditos de " + entrenador2.getNombre() + ": " + entrenador2.getCreditos());
        }
        catch(Exception e) {
            System.out.println("\ n ❗ Error durante el combate: " + e.getMessage());
        }
    }

   // Frases y emojis para el combate
    private static final String[] ATAQUES = {
        "lanza un poderoso ataque 💥",
        "usa una técnica secreta 🌀",
        "golpea con todas sus fuerzas 🥊",
        "ataca velozmente ⚡",
        "desata su furia 🔥",
        "hace un movimiento sorpresa 🤸"
    };

    private static final String[] DEFENSAS = {
        "resiste el golpe como un campeón 🛡️",
        "esquiva hábilmente el ataque 🏃‍♂️",
        "bloquea el ataque a tiempo ⛔",
        "soporta el daño con valentía 💪",
        "se tambalea pero sigue en pie 😵‍💫"
    };

    private String fraseAleatoria(String[] frases) {
        return frases[new Random().nextInt(frases.length)];
    }
}
