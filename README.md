# POOBSoccer

Juego de fútbol 2D de uno contra uno hecho en Java con Swing. Es el proyecto final del curso de Desarrollo Orientado por Objetos (DOPO), Escuela Colombiana de Ingeniería Julio Garavito.

Autores: Forero J. y Salinas S.

## Qué tiene el juego

- **Modalidades:** jugador contra jugador y jugador contra la máquina.
- **Máquina:** cuatro perfiles de juego.
  - `Clumsy`: se mueve al azar.
  - `Offensive`: siempre va por el balón para hacer gol.
  - `Defensive`: se queda cerca de su arco y despeja cuando el balón entra a su mitad.
  - `Expert`: decide si ataca o defiende según el marcador, el tiempo y las posiciones.
- **Personajes y poderes:**

  | Personaje | Poder | Efecto en el rival |
  |-----------|-------|--------------------|
  | Dipper | Blue Aura | Tiro recto y rápido que lo empuja hacia su arco |
  | Gumball | Ray | Lo electrocuta: va a media velocidad y no salta |
  | Jack | Tornado | Tiro por el suelo que lo lanza al aire y lo deja inconsciente |
  | Raven | Illusion | Aparecen 7 balones falsos |
  | Rigby | Sin poder | |

- **Barra de poder:** se llena con el tiempo y cuando se recibe un gol. Con la barra llena se puede lanzar el poder.
- **Vida:** las patadas y los poderes le quitan vida al rival.
- **Estadios:** tres, cada uno con su gravedad.
- **Balones y arcos:** cuatro balones y tres tamaños de arco para escoger.
- **Formas de terminar:** por tiempo o por goles. Si hay empate por tiempo, se juega muerte súbita.
- **Tienda de mejoras:** velocidad, salto, patada y supervivencia, hasta el nivel 10. El ganador de un partido recibe 100 puntos y el perdedor 25.
- **Guardar y abrir:** el menú Game permite guardar un partido en un archivo `.poob` y abrirlo después. Los puntos y las mejoras se guardan solos en `progress.dat`.

## Cómo se juega

| Acción | Jugador 1 | Jugador 2 |
|--------|-----------|-----------|
| Izquierda | A | Flecha izquierda |
| Derecha | D | Flecha derecha |
| Saltar | W | Flecha arriba |
| Patear | S | Flecha abajo |
| Poder | Q | Ctrl derecho |

Con Esc se sale del partido y se vuelve al inicio. El botón de pausa está junto al marcador.

## Cómo ejecutarlo

Necesitas Java 17 o superior (JDK). Desde la carpeta del proyecto:

```
javac -d bin src/domain/*.java src/presentation/*.java
java -cp bin presentation.PoobSoccerGUI
```

El juego debe ejecutarse desde la raíz del proyecto, porque las imágenes y la música se cargan desde la carpeta `lib/` con rutas relativas.

## Pruebas

Las pruebas usan JUnit 4. Con los `.jar` de JUnit y Hamcrest en la variable `CP`:

```
javac -d bin -cp "bin;$CP" src/domain/*.java src/presentation/*.java src/tests/*.java
java -cp "bin;$CP" org.junit.runner.JUnitCore tests.MatchTest tests.BallTest tests.KickTest tests.GoalTest tests.PowerTest tests.BlueAuraTest tests.RayTest tests.TornadoTest tests.IllusionTest tests.StunTest tests.EndTest tests.TimerTest tests.LogTest tests.StatsTest tests.PersistenceTest tests.MachineTest
```

En Linux o Mac el separador del classpath es `:` en vez de `;`.

## Estructura

```
src/
  domain/         Lógica del juego. La clase principal es PoobSoccer (singleton).
  presentation/   Pantallas de Swing, una clase por vista.
  tests/          Pruebas unitarias.
lib/              Imágenes y música.
```

La presentación solo habla con el dominio a través de `PoobSoccer`. Los errores inesperados se guardan en `POOBSoccer.log`.
