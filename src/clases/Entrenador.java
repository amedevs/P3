package clases;

import java.util.ArrayList;

import clases.pokemon.Pokemon;
import interfaces.Clonable;

public class Entrenador implements Clonable {
	private String nombre;
	private ArrayList<Pokemon> pokemones = new ArrayList<Pokemon>();
	private ArrayList<Pokemon> pokemonesCombatientes = new ArrayList<Pokemon>();
	
	public Entrenador(String nombre) {
		this.nombre = nombre;
	}
	
	// "Heredaciones" -----------------------------------------------------
	public boolean esClonable() {
		boolean aux = true;
		int i = 0;
		
		while (aux && i<this.pokemones.size()) {
			aux = this.pokemones.get(i).esClonable();
			i++;
		}
		
		return aux;
	}
	
	// Métodos -----------------------------------------------------
	public void anadirPokemon(Pokemon pokemon) {
		this.pokemones.add(pokemon);
	}
	/*
	public void retirarPokemonDerrotado() {
		this.pokemones.remove(0);
	}*/
	public void retirarPokemon(Pokemon pokemon) {
		this.pokemones.remove(pokemon);
	}
	
	public ArrayList<Pokemon> getPokemones() {
		return this.pokemones;
	}
}

