Documentacion Torneo Pokemon
  - Entidades:
        Gimnasio = tiene un torneo asociado
        torneo = tiene un listado de DueloDePokemones (por ahora)
        DueloPokemon = tiene asociado dos entrenadores (claro, por que los entrenadores tienen a los pokemones)
        Entrenador = tienen dos arrays, uno con su listado total de pokemones, otro con su equipo de combate (<= 3), 
                     nombre, creditos de compra y un categoria (suma del xp de los pokemones). (Puede tener hechizo  
                     asociado)
        Pokemones = La clase abstracta pokemon tiene los metodos comunes entre los pokemones, especificando en cada  
                    tipo los metodos. Los atributos comunes son vitalidad,escudo,fuerza,costo,categoria,xp.
        Arma= Tiene dos atributos de costo y daño

  - Intefaces:
        Hostil= define el metodo void atacar, implementado por arma y pokemones
        Valuable= define el metodod double getCosto, implementado por arma y pokemones
        Clasificables= define el metodo int getCategoria, que implementa pokemones y entrenadores
        Clonable=define el metodo Object clone(Ahora les digo), que implentan en 3 niveles
                 -siempre:espadas, pokemones agua, pokemones hielo
                 -condicional: pokemones piedra (segun arma) y entrenadores (si todos los pokemones lo son)
                 -nunca: hachas y pokemones fuego
   
   - Excepciones:
         CompraImposibleExepcion = compra sin fondos (Emite entrenador y recibe tienda (Posible)). Atributos:creditos 
                                    disponibles, creditos requeridos.
         EntrenadorSinPokemonesExeccion= entrenador sin pokemon. Es emitido por el entrenador y recibido por el 
                                         enfrentamiento. Atributos:entrenador


                              Definicion de Clases, metodos, subclases y patrones

     POKEMON: clase abtracta. Esta define metodos comunes:
                             -constructor: establece los atributos comunes (ya mencionados)
                             -getCategoria: devuelve categoria
                             -recibe dano y recargar: metodos abstractos redifinidos en cada subclase
                             -getters y setter de los atributos

     SUBCLASES DE POKEMONES: Tienen atributos que sirven de informacion (PD) y  metodos definidos en las intefaces 

        - POKEMON de agua:
        - POKEMON de fuego:
        - POKEMON de hielo:
        - POKEMON de piedra:
   
     Entrenadores:Clase concreta no?
