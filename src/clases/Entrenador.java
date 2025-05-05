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
	
	// Constructor --------------------------------------------------------
	public Entrenador(String nombre) {
		this.nombre = nombre;
	}
	
	// "Herencias" -----------------------------------------------------
	/**Metodo que se encarga de clonar al entrenador<br>
	 * 
	 * Genera un clon del entrenador <br>
	 * Clona los Pokemons que posee <br>
	 * Clona los Pokemons que estan selecionados para batalla <br>
	 * Clona los hechizoz que tiene y <br>
	 * CLona las armas que dispone el entrenador.
	 *
	 * @return Devuelve el clon completo del entrenador.
	 * @throws CloneNotSupportedException 
	 */
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
	/**Metodo para la adquisicion de un poquemon<br>
	 * 
	 * <br>Precondicion:</br> El parametro pokemon debe ser distinto de null
	 * 
	 * @param pokemon Pokemon que se desea comprar
	 * @throws CompraImposibleException Se lanza cuando el entrenador no tiene los creditos suficientes para comprar al pokemon
	 */
	public void comprarPokemon(Pokemon pokemon) throws CompraImposibleException {
		if (this.creditos>=pokemon.getCosto()) {
			if (!this.pokemones.contains(pokemon)) {
				this.creditos -= pokemon.getCosto();
				this.pokemones.add(pokemon);
			}
		} else
			throw new CompraImposibleException(this.creditos,pokemon.getCosto());
	}
	/**Metodo para la adquisicion de un arma<br>
	 * 
	 * <br>Precondicion:</br> El parametro arma debe ser distinto de null
	 * 
	 * @param arma Arma que se desea comprar
	 * @throws CompraImposibleException Se lanza cuando el entrenador no tiene los creditos suficientes para comprar el arma
	 */
	public void comprarArma(Arma arma) throws CompraImposibleException {
		if (this.creditos>=arma.getCosto()) {
			this.creditos -= arma.getCosto();
			this.armas.add(arma);
		} else
			throw new CompraImposibleException(this.creditos,arma.getCosto());
	}
	
	/**Metodo para otorgar un arma a un Pokemon
	 * 
	 * <br>Precondiciones:</br> 
	 *		El parametro piedra debe ser distinto de null<br>
	 *		El parametro arma debe ser distinto de null
	 * 
	 * @param arma Arma que se desea asignar
	 * @param piedra Pokemon de tipo piedra al que se le quiere dar el arma
	 */
	public void asignarArma(Arma arma, Piedra piedra) {
		if (this.pokemones.contains(piedra) && this.armas.contains(arma)) {
			piedra.setArma(arma);
			this.armas.remove(arma);	
		}
	}
	/**Metodo para quitar un arma a un Pokemon
	 * <br>Precondicion:</br> El parametro piedra debe ser distinto de null
	 * @param piedra Pokemon de tipo piedra al que se le quiere quitar el arma
	 */
	public void desasignarArma(Piedra piedra) {
		if (this.pokemones.contains(piedra) && piedra.getArma()!=null) {
			this.armas.add(piedra.getArma());
			piedra.setArma(null);
		}
	}

	/**Metodo para generar equipo de batalla de pokemons
	 * 
	 * @param pokemon Pokemon que se quiere usar para combatir
	 */
	public void anadirPokemonCombatiente(Pokemon pokemon) {
		if (this.pokemonesCombatientes.size()<maxCombatientes &&
			this.pokemones.contains(pokemon) &&
			!this.pokemonesCombatientes.contains(pokemon)
		)
			this.pokemonesCombatientes.add(pokemon);
	}

	/**Metodo para quitar pokemon del equipo de batalla
	 * 
	 * @param pokemon Pokemon que se quiere quitar
	 */
	public void quitarPokemonCombatiente(Pokemon pokemon) {
		this.pokemonesCombatientes.remove(pokemon);
	}
	
	// Métodos para manejar hechizos
	/**Metodo para manejar hechizos
	 * 
	 * @param tipo Tipo del hechizo
	 * @return devuelve un arreglo con los hechizos del mismo tipo
	 */
	public ArrayList<Hechizo> getHechizosPorTipo(Hechizo.TipoHechizo tipo) {
		ArrayList<Hechizo> filtrados = new ArrayList<>();
		filtrados.clear();
		for (Hechizo h : hechizos) {
			if (h.getTipo() == tipo) filtrados.add(h);
		}
		return filtrados;
	}
	
	/**Metodo para utilizar los hechizos
	 * 
	 * @param carta Hechizo seleccionado para atacar
	 * @param adversario Pokemon del equipo contrario, que recibira el hechizo
	 */
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
