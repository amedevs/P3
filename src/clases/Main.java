package clases;

import clases.arma.Arma;
import clases.arma.Espada;
import clases.arma.Hacha;
import clases.hechizo.Hechizo;
import clases.hechizo.Niebla;
import clases.hechizo.Tormenta;
import clases.hechizo.Viento;
import clases.pokemon.Agua;
import clases.pokemon.Fuego;
import clases.pokemon.Hielo;
import clases.pokemon.Piedra;
import clases.pokemon.Pokemon;

public class Main {
    public static void main(String[] args) {
        try {
            // Crear entrenadores
            Entrenador ash = new Entrenador("Ash");
            Entrenador brock = new Entrenador("Brock");
            
            // Dar créditos iniciales
            ash.setCreditos(1000);
            brock.setCreditos(1000);
            
            // Crear pokémones
            Pokemon squirtle = new Agua("Squirtle");
            Pokemon charmander = new Fuego("Charmander");
            Pokemon articuno = new Hielo("Articuno");
            Pokemon geodude = new Piedra("Geodude");
            
            // Crear armas
            Arma espada = new Espada();
            Arma hacha = new Hacha();
            
            // Crear hechizos
            Hechizo niebla = new Niebla();
            Hechizo tormenta = new Tormenta();
            Hechizo viento = new Viento();
            
            // Comprar pokémones
            System.out.println("Comprando pokémones...");
            ash.comprarPokemon(squirtle);
            ash.comprarPokemon(charmander);
            brock.comprarPokemon(articuno);
            brock.comprarPokemon(geodude);
            
            // Comprar armas
            System.out.println("Comprando armas...");
            brock.comprarArma(espada);
            brock.comprarArma(hacha);
            
            // Asignar arma a pokémon de piedra
            brock.asignarArma(espada, (Piedra)geodude);
            
            // Añadir hechizos
            ash.getCartasDeNiebla().add(niebla);
            ash.getCartasDeTormenta().add(tormenta);
            brock.getCartasDeViento().add(viento);
            
            // Preparar pokémones para combate
            System.out.println("Preparando pokémones para combate...");
            ash.anadirPokemonCombatiente(squirtle);
            ash.anadirPokemonCombatiente(charmander);
            brock.anadirPokemonCombatiente(articuno);
            brock.anadirPokemonCombatiente(geodude);
            
            // Probar clonación
            System.out.println("Probando clonación...");
            Entrenador ashClon = (Entrenador)ash.clone();
            System.out.println("Ash clonado: " + ashClon.getNombre());
            System.out.println("Pokémones de Ash clon: " + ashClon.getPokemones().size());
            
            // Iniciar duelo
            System.out.println("\nIniciando duelo...");
            Duelo duelo = new Duelo(ash, brock);
            duelo.iniciaDuelo();
            
            // Mostrar resultados
            System.out.println("\nResultados finales:");
            System.out.println("Créditos de Ash: " + ash.getCreditos());
            System.out.println("Créditos de Brock: " + brock.getCreditos());
            System.out.println("Categoría de Ash: " + ash.getCategoria());
            System.out.println("Categoría de Brock: " + brock.getCategoria());
            
        } catch (CloneNotSupportedException e) {
            System.out.println("Error al clonar: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
        }
    }
} 
