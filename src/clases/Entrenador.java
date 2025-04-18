package clases;

import java.util.ArrayList;

import clases.arma.Arma;
import clases.hechizo.Hechizo;
import clases.pokemon.Piedra;
import clases.pokemon.Pokemon;
import interfaces.Clasificable;

public class Entrenador implements Cloneable, Clasificable {
	private final int maxCombatientes = 3;
	
	private String nombre;
	private ArrayList<Pokemon> pokemones = new ArrayList<Pokemon>();
	private ArrayList<Pokemon> pokemonesCombatientes = new ArrayList<Pokemon>();
	private ArrayList<Hechizo> hechizosNiebla = new ArrayList<Hechizo>();
	private ArrayList<Hechizo> hechizosTormenta = new ArrayList<Hechizo>();
	private ArrayList<Hechizo> hechizosViento = new ArrayList<Hechizo>();
	private ArrayList<Arma> armas = new ArrayList<Arma>();

	private int creditos = 0;
	
	public Entrenador(String nombre) {
		this.nombre = nombre;
	}
	
	// "Heredaciones" -----------------------------------------------------
	public Object clone() throws CloneNotSupportedException {
		Entrenador clonEntrenador = (Entrenador)super.clone();
		
		// Clonar pokémones
		
		for(Pokemon pokemon : this.pokemones) {
			clonEntrenador.pokemones.add((Pokemon)pokemon.clone());
		}
		
		// Clonar pokémones combatientes
	
		for(Pokemon pokemon : this.pokemonesCombatientes) {
			clonEntrenador.pokemonesCombatientes.add((Pokemon)pokemon.clone());
		}
		
		// Clonar hechizos
		
		for(Hechizo hechizo : this.hechizosNiebla) {
			clonEntrenador.hechizosNiebla.add((Hechizo)hechizo.clone());
		}
		for(Hechizo hechizo : this.hechizosTormenta) {
			clonEntrenador.hechizosTormenta.add((Hechizo)hechizo.clone());
		}
		for(Hechizo hechizo : this.hechizosViento) {
			clonEntrenador.hechizosViento.add((Hechizo)hechizo.clone());
		}
		
		// Clonar armas
		for(Arma arma : this.armas) {
			clonEntrenador.armas.add((Arma)arma.clone());
		}
		
		return clonEntrenador;
	}
	
	public int getCategoria() {
		int categoria = 0;
		
		for(int i=0;i<pokemones.size();i++)
			categoria += pokemones.get(i).getCategoria();
		
		return categoria;
	}
	
	// Métodos -----------------------------------------------------
	public void anadirPokemon(Pokemon pokemon) {
		this.pokemones.add(pokemon);
	}
	
	public void comprarPokemon(Pokemon pokemon) {
		if (this.creditos>=pokemon.getCosto()) {
			this.creditos -= pokemon.getCosto();
			this.pokemones.add(pokemon);
		} /*else
			lanzar CompraImposibleException */
	}
	
	public void comprarArma(Arma arma) {
		if (this.creditos>=arma.getCosto()) {
			this.creditos -= arma.getCosto();
			this.armas.add(arma);
		} /*else
			lanzar CompraImposibleException */
	}
	
	public void asignarArma(Arma arma, Piedra piedra) {
		piedra.setArma(arma);
		this.armas.remove(arma);
	}
	public void desasignarArma(Piedra piedra) {
		this.armas.add(piedra.getArma());
		piedra.setArma(null);
	}

	public void anadirPokemonCombatiente(Pokemon pokemon) {
		if (this.pokemonesCombatientes.size()<maxCombatientes)
			this.pokemonesCombatientes.add(pokemon);
	}
	
	// Getters y setters
	public String getNombre() {
		return this.nombre;
	}
	
	public ArrayList<Pokemon> getPokemones() {
		return this.pokemones;
	}
	public ArrayList<Pokemon> getPokemonesCombatientes() {
		return this.pokemonesCombatientes;
	}
	
	public ArrayList<Hechizo> getHechizos() {
		ArrayList<Hechizo> hechizos = new ArrayList<Hechizo>();
		hechizos.addAll(this.hechizosNiebla);
		hechizos.addAll(this.hechizosTormenta);
		hechizos.addAll(this.hechizosViento);
		
		return hechizos;
	}
	public ArrayList<Hechizo> getCartasDeNiebla() {
		return this.hechizosNiebla;
	}
	public ArrayList<Hechizo> getCartasDeTormenta() {
		return this.hechizosTormenta;
	}
	public ArrayList<Hechizo> getCartasDeViento() {
		return this.hechizosViento;
	}
	
	public int getCreditos() {
		return this.creditos;
	}
	public void setCreditos(int nuevosCreditos) {
		this.creditos = nuevosCreditos;
	}
}



