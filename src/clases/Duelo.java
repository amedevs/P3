package clases;

import java.util.ArrayList;
import java.util.Scanner;

import clases.hechizo.Hechizo;
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
	
	private void seleccionaPokemon(int indice, ArrayList<Pokemon> equipo, Pokemon pokemon) {
		do {
			indice++;
			pokemon = equipo.get(indice);
		} while (indice<maxPokemon && pokemon.getVida()<=0);
	}
	
	private void lanzarCartaDeHechizo(Entrenador entrenador, Pokemon pokemonAdversario) {
		int indiceHechizo;
		ArrayList<Hechizo> cartasHechizo = new ArrayList<Hechizo>();
		Scanner scanner = new Scanner(System.in);
		
		if (!entrenador.getHechizos().isEmpty()) {
			System.out.println("Elija un hechizo para tirar\n");
			if (!entrenador.getCartasDeNiebla().isEmpty())
				System.out.println("1 - Niebla\n");
			if (!entrenador.getCartasDeTormenta().isEmpty())
				System.out.println("2 - Tormenta\n");
			if (!entrenador.getCartasDeViento().isEmpty())
				System.out.println("3 - Viento\n");
			System.out.println("(otro) - ninguno\n");
			indiceHechizo = scanner.nextInt();
			switch(indiceHechizo) {
				case 1: cartasHechizo = entrenador.getCartasDeNiebla(); break;
				case 2: cartasHechizo = entrenador.getCartasDeTormenta(); break;
				case 3: cartasHechizo = entrenador.getCartasDeViento(); break;
				default: cartasHechizo.clear(); break;
			}
			entrenador.lanzarHechizoAAdversario(cartasHechizo, pokemonAdversario);
		}
	}
	
	public void iniciaDuelo() throws EntrenadorSinPoquemonesException {
		ArrayList<Pokemon> equipo1 = entrenador1.getPokemonesCombatientes(),
							equipo2 = entrenador2.getPokemonesCombatientes();
		int indice1 = -1,
			indice2 = -1;
		Pokemon pokemon1 = null,
				pokemon2 = null;		
		try {
			// Verifica que los equipos no estén vacíos
			if (equipo1.isEmpty())
				throw new EntrenadorSinPoquemonesException(entrenador1); // Acá hubo un comentario pidiendo que la línea haga lo que hace
			if (equipo2.isEmpty())
				throw new EntrenadorSinPoquemonesException(entrenador2); // Acá hubo un comentario simil al de arriba
			
			// Verifica que haya pokemones con salud y selecciona el primero apto de cada equipo
			// Equipo 1
			this.seleccionaPokemon(indice1, equipo1, pokemon1);
			if (indice1 == maxPokemon)
				throw new EquipoSinSaludException(entrenador1); // Acá hubo un comentario pidiendo que la línea haga lo que hace
			// Equipo 2
			this.seleccionaPokemon(indice2, equipo2, pokemon2);
			if (indice2 == maxPokemon)
				throw new EquipoSinSaludException(entrenador2);	// Acá hubo un comentario simil al de arriba		
			// Duelo
			while (indice1<maxPokemon && indice2<maxPokemon) {
				this.lanzarCartaDeHechizo(entrenador1, pokemon2); // Hechizo 1 a 2
				this.lanzarCartaDeHechizo(entrenador2, pokemon1); // Hechizo 2 a 1
				// Las pokepiñas en cuestión
				while (pokemon1.getVida()>0 && pokemon2.getVida()>0) {
					pokemon1.atacar(pokemon2);
					if (pokemon2.getVida()>0)
						pokemon2.atacar(pokemon1);
				}
				// Suma XP
				if (pokemon1.getVida()>0)
					pokemon1.setXP(pokemon1.getCategoria()+1);
				else
					pokemon2.setXP(pokemon2.getCategoria()+1);
				// El recambio
				if (pokemon1.getVida()<=0)
					this.seleccionaPokemon(indice1, equipo1, pokemon1);
				else
					this.seleccionaPokemon(indice2, equipo2, pokemon2);
			}
			if (indice1<maxPokemon)
				entrenador1.setCreditos(entrenador1.getCreditos()+500);
			else
				entrenador2.setCreditos(entrenador2.getCreditos()+500);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}