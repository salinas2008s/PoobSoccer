<p align="center">
  <img src="lib/Stadium1.png" alt="Estadio de POOBSoccer" width="100%">
</p>

<h1 align="center">⚽ POOBSoccer ⚽</h1>

<p align="center">
  <img src="lib/PoobSoccer.png" alt="POOBSoccer" width="460">
</p>

<p align="center">
  <b>Cabezas grandes, poderes locos y un solo objetivo: meter más goles que tu rival.</b><br>
  Fútbol 2D uno contra uno hecho 100% en Java con Swing.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white" alt="Java 17+">
  <img src="https://img.shields.io/badge/Interfaz-Swing-blue" alt="Swing">
  <img src="https://img.shields.io/badge/Pruebas-JUnit%204-green" alt="JUnit 4">
  <img src="https://img.shields.io/badge/Jugadores-1%20o%202-red" alt="1 o 2 jugadores">
  <img src="https://img.shields.io/badge/Motor%20de%20f%C3%ADsica-hecho%20a%20mano-purple" alt="Física hecha a mano">
</p>

<p align="center">
  <a href="#-cómo-ejecutarlo">Ejecutar</a> ·
  <a href="#-los-personajes">Personajes</a> ·
  <a href="#-los-poderes">Poderes</a> ·
  <a href="#-juega-contra-la-máquina">Máquina</a> ·
  <a href="#-la-tienda-de-mejoras">Tienda</a> ·
  <a href="#-cómo-está-hecho">Código</a>
</p>

---

## 🎮 ¿De qué se trata?

POOBSoccer es el proyecto final del curso de **Desarrollo Orientado por Objetos (DOPO)** de la Escuela Colombiana de Ingeniería Julio Garavito.

Escoge tu personaje, entra a la cancha y demuestra quién manda: patea, salta, carga tu barra de poder y lanza un tiro especial que deja a tu rival electrocutado, volando por los aires o viendo balones por todos lados. Juega con un amigo en el mismo teclado o enfréntate a la máquina.

> Todo el motor de física (gravedad, rebotes, choques con los postes y contacto entre jugadores y balón) está hecho a mano, sin librerías de juegos.

<p align="center">
  <img src="lib/Marcador.png" alt="Marcador" width="520">
</p>

## 🕹️ Elige cómo jugar

<table align="center">
  <tr>
    <td align="center"><img src="lib/PlayerVSPlayer.png" width="330"><br><b>Jugador vs Jugador</b><br>Dos personas, un teclado</td>
    <td align="center"><img src="lib/PlayerVsAI.png" width="330"><br><b>Jugador vs Máquina</b><br>Cuatro perfiles de dificultad</td>
  </tr>
</table>

<p align="center">
  <img src="lib/PoobSelectPlayer.png" alt="Selección de personajes" width="80%">
</p>

## 🧑‍🎤 Los personajes

<table align="center">
  <tr>
    <td align="center"><img src="lib/Dipper1.png" height="170"><br><b>Dipper</b></td>
    <td align="center"><img src="lib/Gumball1.png" height="170"><br><b>Gumball</b></td>
    <td align="center"><img src="lib/Jack1.png" height="170"><br><b>Jack</b></td>
    <td align="center"><img src="lib/Raven1.png" height="170"><br><b>Raven</b></td>
    <td align="center"><img src="lib/Rigby1.png" height="170"><br><b>Rigby</b></td>
  </tr>
</table>

Cada uno patea con su propia animación y se voltea según hacia dónde camina:

<table align="center">
  <tr>
    <td align="center"><img src="lib/Dipper2.png" height="130"><br>Patada</td>
    <td align="center"><img src="lib/Dipper3.png" height="130"><br>Mirando a la izquierda</td>
    <td align="center"><img src="lib/DipperPo1.png" height="130"><br>Con el aura del poder</td>
    <td align="center"><img src="lib/GumballPo1.png" height="130"><br>Gumball con aura</td>
    <td align="center"><img src="lib/JackPo1.png" height="130"><br>Jack con aura</td>
  </tr>
</table>

## ⚡ Los poderes

Cada personaje tiene su propio poder. La barra se llena con el tiempo, y **recibir un gol la sube un cuarto**: el que va perdiendo siempre tiene una oportunidad.

<p align="center">
  <img src="lib/Power0.png" width="220"> <img src="lib/Power2.png" width="220"> <img src="lib/Power5.png" width="220">
  <br><i>La barra de poder, de vacía a llena</i>
</p>

| Personaje | Poder | Qué le hace al rival |
|-----------|-------|----------------------|
| **Dipper** | 🔵 Blue Aura | Un tiro recto y veloz que lo empuja hasta su propio arco |
| **Gumball** | ⚡ Ray | Lo electrocuta: va a media velocidad y no puede saltar |
| **Jack** | 🌪️ Tornado | Un tiro por el suelo que lo lanza al aire y lo deja inconsciente |
| **Raven** | 🔮 Illusion | Aparecen 7 balones falsos para confundirlo |
| **Rigby** | — | Sin poder, solo habilidad |

<table align="center">
  <tr>
    <td align="center"><img src="lib/Ray.png" height="150"><br><b>Rayo</b><br>sobre el jugador electrocutado</td>
    <td align="center"><img src="lib/Storm.png" height="150"><br><b>Tormenta</b><br>bajo el jugador del Tornado</td>
  </tr>
</table>

Y un detalle importante: **un tiro de poder siempre le llega al rival**, aunque esté pateando.

## 🤖 Juega contra la máquina

Cuatro perfiles, cada uno con su personalidad:

<table align="center">
  <tr>
    <td align="center"><img src="lib/Clumsy.png" width="210"><br><b>Clumsy</b><br>Se mueve al azar</td>
    <td align="center"><img src="lib/Offensive.png" width="210"><br><b>Offensive</b><br>Siempre va por el balón</td>
    <td align="center"><img src="lib/Defensive.png" width="210"><br><b>Defensive</b><br>Cuida su arco</td>
    <td align="center"><img src="lib/Expert.png" width="210"><br><b>Expert</b><br>Lee el partido y decide</td>
  </tr>
</table>

El **Expert** mira el marcador, el tiempo y las posiciones: ataca cuando va perdiendo o en muerte súbita, y se encierra a defender cuando va ganando o cuando el rival está a un gol de ganar.

## 🏟️ Todo para personalizar tu partido

### Tres estadios, tres gravedades

<table align="center">
  <tr>
    <td align="center"><img src="lib/Stadium1.png" width="320"><br><b>Estadio 1</b><br>Gravedad normal</td>
    <td align="center"><img src="lib/Stadium2.png" width="320"><br><b>Estadio 2</b><br>Gravedad baja: saltas muy alto</td>
    <td align="center"><img src="lib/Stadium3.png" width="320"><br><b>Estadio 3</b><br>Gravedad alta: casi no despegas</td>
  </tr>
</table>

### Cuatro balones

<p align="center">
  <img src="lib/Ball1.png" height="90"> &nbsp;
  <img src="lib/Ball2.png" height="90"> &nbsp;
  <img src="lib/Ball3.png" height="90"> &nbsp;
  <img src="lib/Ball4.png" height="90">
</p>

### Y más opciones

- **3 tamaños de arco** a elección.
- **Por tiempo (1, 2 o 3 minutos) o por goles (5, 7 o 10)**. Si hay empate al final del tiempo, se define en **muerte súbita**.
- **Vida y patadas que duelen**: cada patada al rival le quita vida, y los jugadores no pueden atravesarse.

<p align="center">
  <img src="lib/5Life.png" width="300"> &nbsp; <img src="lib/2.5Life.png" width="300"> &nbsp; <img src="lib/0.5Life.png" width="300">
  <br><i>La barra de vida, de llena a casi vacía</i>
</p>

## 🛒 La tienda de mejoras

Cada partido deja puntos: **100 para el ganador y 25 para el perdedor**. Gástalos en la tienda y sube hasta el nivel 10 en:

<table align="center">
  <tr>
    <td align="center"><img src="lib/UpgradeSpeed.png" height="90"><br>🏃 <b>Speed</b><br>Caminas más rápido</td>
    <td align="center"><img src="lib/UpgradeJump.png" height="90"><br>🦘 <b>Jump</b><br>Saltas más alto</td>
    <td align="center"><img src="lib/UpgradeKick.png" height="90"><br>🦵 <b>Kick</b><br>Patadas y poderes con más daño</td>
    <td align="center"><img src="lib/UpgradeSurvival.png" height="90"><br>❤️ <b>Survival</b><br>Más vida</td>
  </tr>
</table>

<p align="center">
  <img src="lib/Kick1.png" width="380"> &nbsp; <img src="lib/Kick5.png" width="380"> &nbsp; <img src="lib/Kick10.png" width="380">
  <br><i>La barra de Kick: nivel 1, 5 y 10</i>
</p>

Los precios se duplican en cada nivel (500, 1000, 2000...), así que cada mejora hay que pensarla.

<p align="center">
  <img src="lib/PoobUpgrates.png" alt="Tienda" width="80%">
</p>

## 🏆 El podio

Al terminar el partido, el ganador sube al podio. Si le ganas a la máquina, la que celebra es ella.

<table align="center">
  <tr>
    <td align="center"><img src="lib/Player1Win.png" width="330"><br><b>Gana el jugador 1</b></td>
    <td align="center"><img src="lib/Player2Win.png" width="330"><br><b>Gana el jugador 2</b></td>
    <td align="center"><img src="lib/IAWin.png" width="330"><br><b>Gana la máquina</b></td>
  </tr>
</table>

## 💾 Nunca pierdes tu progreso

- Tus **puntos y mejoras** se guardan solos en `progress.dat`.
- Desde el menú **Game** puedes **guardar un partido** en un archivo `.poob` y **abrirlo después** justo donde lo dejaste, con marcador, reloj, posiciones, vida y balón.

## ⌨️ Controles

| Acción | Jugador 1 | Jugador 2 |
|--------|:---------:|:---------:|
| Izquierda | `A` | `←` |
| Derecha | `D` | `→` |
| Saltar | `W` | `↑` |
| Patear | `S` | `↓` |
| Poder | `Q` | `Ctrl` derecho |

`Esc` sale del partido. El botón de pausa está junto al marcador.

## 🚀 Cómo ejecutarlo

Necesitas el **JDK de Java 17 o superior**. Desde la carpeta del proyecto:

```bash
javac -d bin src/domain/*.java src/presentation/*.java
java -cp bin presentation.PoobSoccerGUI
```

> Ejecútalo siempre desde la raíz del proyecto: las imágenes y la música se cargan desde `lib/` con rutas relativas.

## 🧪 Pruebas

Más de 150 pruebas con **JUnit 4** cubren los partidos, la física del balón, los goles, los cuatro poderes, el aturdimiento, el final del partido, la tienda, la persistencia y la máquina.

```bash
# Con los .jar de JUnit 4 y Hamcrest en la variable CP (en Linux y Mac el separador es ":")
javac -d bin -cp "bin;$CP" src/domain/*.java src/presentation/*.java src/tests/*.java
java -cp "bin;$CP" org.junit.runner.JUnitCore tests.MatchTest tests.BallTest tests.KickTest tests.GoalTest tests.PowerTest tests.BlueAuraTest tests.RayTest tests.TornadoTest tests.IllusionTest tests.StunTest tests.EndTest tests.TimerTest tests.LogTest tests.StatsTest tests.PersistenceTest tests.MachineTest
```

## 🧱 Cómo está hecho

El proyecto separa el dominio de la presentación:

```
src/
├── domain/         Lógica del juego, sin nada de Swing
├── presentation/   Una pantalla (vista) por clase
└── tests/          Pruebas unitarias
lib/                Imágenes y música
```

- **`PoobSoccer`** es la clase principal y un **singleton**: la presentación solo habla con el dominio a través de ella.
- **Polimorfismo en lugar de condicionales:** `Player` es abstracta, con `HumanPlayer` (teclado) y `AIPlayer` (máquina). `AIPlayer` también es abstracta y de ella heredan `Clumsy`, `Offensive`, `Defensive` y `Expert`.
- **Poderes** como interfaz `Power`, con una clase por cada poder.
- **Excepciones propias** (`PoobSoccerException`) con mensajes claros para el jugador, y un **log** (`POOBSoccer.log`) para los errores inesperados.
- **Persistencia** con serialización de Java.

## 👥 Autores

Hecho con mucho café por **Forero J.** y **Salinas S.**

<p align="center">
  <img src="lib/cup.png" width="300"><br>
  <i>¿Listo para el saque inicial? 🏆</i>
</p>
