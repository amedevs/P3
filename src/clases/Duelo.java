package clases;

import java.util.ArrayList;

import clases.pokemon.Pokemon;

public class Duelo {
	private final int maxPokemon = 3;
	private Entrenador e1,
					   e2;
	private ArrayList<Pokemon> equipo1 = new ArrayList<Pokemon>(),
							   equipo2 = new ArrayList<Pokemon>();
	
	public Duelo(Entrenador e1, Entrenador e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	
	public void anadirPokemon(Entrenador entrenador, Pokemon pokemon) {
		if (!entrenador.equals(e1) && !entrenador.equals(e2))
			System.out.println("El entrenador no está en el duelo.\n");
		else
		if (!entrenador.getPokemones().contains(pokemon))
			System.out.println("El entrenador no posee ese pokemon.\n");
		else
		if ((entrenador.equals(e1) && equipo1.size()==maxPokemon) || (entrenador.equals(e2) && equipo2.size()==maxPokemon))
			System.out.println("El entrenador tiene equipo lleno.\n");
		else
		if (entrenador.equals(e1))
			equipo1.add(pokemon);
		else
			equipo2.add(pokemon);
	}
	
	public void iniciaDuelo() {
		if (e1 == null || e2 == null)
			System.out.println("Faltan entrenadores.\n");
		else
		if (equipo1.size() == 0)	// Excepción
			System.out.println("Entrenador 1 sin pokemones.\n");
		else if (equipo2.size() == 0)
				System.out.println("Entrenador 2 sin pokemones.\n");
		else {
			
		}
	}
}
