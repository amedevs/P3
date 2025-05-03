package clases;

import java.util.ArrayList;

import clases.arma.Arma;
import clases.hechizo.Hechizo;
import clases.pokemon.Piedra;
import clases.pokemon.Pokemon;
import excepciones.CompraImposibleException;
import interfaces.Clasificable;

public class Entrenador implements Cloneable, Clasificable {
	private final int maxCombatientes = 3;
	
	private String nombre;
	private ArrayList<Pokemon> pokemones = new ArrayList<Pokemon>();
	private ArrayList<Pokemon> pokemonesCombatientes = new ArrayList<Pokemon>();
	private ArrayList<Hechizo> hechizos = new ArrayList<Hechizo>();
	private ArrayList<Arma> armas = new ArrayList<Arma>();

	private int creditos = 0;
	
	public Entrenador(String nombre) {
		this.nombre = nombre;
	}
	
	// "Heredaciones" -----------------------------------------------------
	public Object clone() throws CloneNotSupportedException {
		Entrenador clonEntrenador = (Entrenador)super.clone();
		
		// Clonar pokémones
		clonEntrenador.pokemones = (ArrayList<Pokemon>)pokemones.clone();
		clonEntrenador.pokemones.clear();
		for(Pokemon pokemon : this.pokemones) {
			clonEntrenador.pokemones.add((Pokemon)pokemon.clone());
		}
		
		// Clonar pokémones combatientes
	        clonEntrenador.pokemonesCombatientes = (ArrayList<Pokemon>)pokemonesCombatientes.clone();
		clonEntrenador.pokemonesCombatientes.clear();
		for(Pokemon pokemon : this.pokemonesCombatientes) {
			clonEntrenador.pokemonesCombatientes.add((Pokemon)pokemon.clone());
		}
		
		// Clonar hechizos
		clonEntrenador.hechizos = (ArrayList<Hechizo>) hechizos.clone();
		clonEntrenador.hechizos.clear();
		for(Hechizo hechizo : this.hechizos) {
			clonEntrenador.hechizos.add(hechizo);
		}
		
		// Clonar armas
		clonEntrenador.armas = (ArrayList<Arma>)armas.clone();
		clonEntrenador.armas.clear();
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
	public void comprarPokemon(Pokemon pokemon) throws CompraImposibleException {
		if (this.creditos>=pokemon.getCosto()) {
			if (!this.pokemones.contains(pokemon)) {
				this.creditos -= pokemon.getCosto();
				this.pokemones.add(pokemon);
			}
		} else
			throw new CompraImposibleException(this.creditos,pokemon.getCosto());
	}
	
	public void comprarArma(Arma arma) throws CompraImposibleException {
		if (this.creditos>=arma.getCosto()) {
			this.creditos -= arma.getCosto();
			this.armas.add(arma);
		} else
			throw new CompraImposibleException(this.creditos,arma.getCosto());
	}
	
	public void asignarArma(Arma arma, Piedra piedra) {
		if (this.pokemones.contains(piedra) && this.armas.contains(arma)) {
			piedra.setArma(arma);
			this.armas.remove(arma);	
		}
	}
	public void desasignarArma(Piedra piedra) {
		if (this.pokemones.contains(piedra) && piedra.getArma()!=null) {
			this.armas.add(piedra.getArma());
			piedra.setArma(null);
		}
	}

	public void anadirPokemonCombatiente(Pokemon pokemon) {
		if (this.pokemonesCombatientes.size()<maxCombatientes &&
			this.pokemones.contains(pokemon) &&
			!this.pokemonesCombatientes.contains(pokemon)
		)
			this.pokemonesCombatientes.add(pokemon);
	}
	
	// Métodos para manejar hechizos
	public ArrayList<Hechizo> getHechizosPorTipo(Hechizo.TipoHechizo tipo) {
		ArrayList<Hechizo> filtrados = new ArrayList<>();
		filtrados.clear();
		for (Hechizo h : hechizos) {
			if (h.getTipo() == tipo) filtrados.add(h);
		}
		return filtrados;
	}
	
	public void lanzarHechizoAAdversario(Hechizo carta, Pokemon adversario) {
		if (this.hechizos.contains(carta)) {
			carta.hechizar(adversario);
			this.hechizos.remove(carta);
		}
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
		return this.hechizos;
	}
	
	public int getCreditos() {
		return this.creditos;
	}
	public void setCreditos(int nuevosCreditos) {
		this.creditos = nuevosCreditos;
	}
}
