# Introducción a MyJS para la Práctica - 2025/26

## Contenido
- [Generalidades](#generalidades)
- [Estructura de un Programa](#estructura-de-un-programa)
- [Comentarios](#comentarios)
- [Constantes](#constantes)
- [Operadores](#operadores)
- [Identificadores](#identificadores)
- [Declaraciones](#declaraciones)
- [Tipos de Datos](#tipos-de-datos)
- [Instrucciones de entrada/salida](#instrucciones-de-entradasalida)
- [Sentencias](#sentencias)
- [Funciones](#funciones)
- [Más información sobre el lenguaje](#más-información-sobre-el-lenguaje)

---

## Generalidades
JavaScript es un lenguaje de programación ideado en 1995 en Netscape a partir de los lenguajes C, C++ y Java.

Este resumen presenta las principales características de la variante de JavaScript denominada **MyJS** que es la que hay que utilizar para la práctica de la asignatura. No hay que considerar los elementos de JavaScript no mencionados en este resumen y se deben considerar los elementos y características tal como aparecen aquí descritos. Con el fin de facilitar la implementación de la Práctica, las características mostradas en esta página pueden no coincidir al 100% con el estándar del lenguaje JavaScript, por lo que, en caso de duda, se deberá implementar siempre el comportamiento aquí descrito. Entre corchetes `[...]` se dan indicaciones sobre la obligatoriedad u opcionalidad de algunas partes del lenguaje en cuanto a su implementación.

* **Case sensitive**: MyJS es un lenguaje en el que se diferencian las minúsculas y las mayúsculas.
* **Formato libre**: Se admiten espacios, tabuladores, saltos de línea y comentarios en cualquier parte del código. Las sentencias simples y las declaraciones de variables finalizan en punto y coma (`;`).
* **Palabras clave**: Son reservadas (se escriben siempre en minúsculas). Cada grupo de prácticas sólo ha de tener en cuenta las palabras asignadas a su grupo.
* **Estructura de bloques**: Se definen mediante la utilización de las llaves `{ }` y, por tanto, maneja los conceptos de identificadores globales y locales. Los identificadores declarados fuera de cualquier función son **globales** y pueden ser utilizados desde cualquier función definida con posterioridad. Los declarados en el interior de una función son **locales** a dicha función.
* **Declaración implícita**: En MyJS no es obligatorio declarar todos los identificadores antes de que se utilicen; en este caso, un uso de un identificador no declarado se considera como una variable global entera. 
* **Recursividad**: Hay que realizar la implementación considerando que es un lenguaje con recursividad, por lo que cualquier función puede ser recursiva. 
* **Funciones anidadas**: El lenguaje no permite la definición de funciones anidadas.

---

## Estructura de un Programa
Debe considerarse que un programa en MyJS estará compuesto por un único fichero que puede tener declaraciones de variables globales, sentencias y declaración de funciones, en cualquier orden.

### Programa Principal
El programa principal (por donde comenzará la ejecución del programa) estará formado por todas las sentencias ubicadas fuera de las funciones.

Por tanto, la ejecución comenzaría por la primera sentencia que se encuentre en el fuente (fuera de una función) y proseguiría secuencialmente hasta el final del fichero ejecutando todas las sentencias situadas fuera de las funciones. Hay que tener en cuenta que una función se ejecuta únicamente cuando es invocada.

---

## Comentarios
En MyJS hay dos tipos de comentarios *[cada grupo deberá implementar obligatoriamente solo el que le corresponda]*:

### Comentario de bloque
Se utilizan los caracteres `/*` para abrir el comentario, y `*/` para cerrarlo. No se permiten comentarios anidados. Los comentarios pueden ocupar más de una línea y pueden ir colocados en cualquier parte del código:
```javascript
/* ¡Comentario con apertura y cierre! */
```

### Comentario de línea
Los comentarios comienzan por los caracteres `//` y finalizan al acabar la línea. Este tipo de comentario sólo ocupa una línea y puede ir colocado en cualquier parte del código:
```javascript
// Comentario de línea...
```

---

## Constantes
El lenguaje dispone de varios tipos de constantes *[implementación obligatoria de las constantes numéricas y cadenas]*:

### Números enteros
Para representar las constantes enteras se utilizan los dígitos decimales. Por ejemplo: `378`.
Los números enteros se tienen que poder representar con una palabra (16 bits, incluido el signo), por lo que el máximo entero válido será el `32767`.

### Números reales
Para representar las constantes reales se utilizan dígitos y el punto para separar la parte entera de la decimal. Es obligatorio que haya al menos un dígito antes del punto y al menos un dígito después del punto (y no se puede usar la notación científica). Por ejemplo: `3.78`, `0.78`, `3.0` (no serían válidos `78.` ni `.378`).
Los números reales se tienen que poder representar con dos palabras (32 bits, incluido el signo), por lo que el máximo real válido será el `117549436.0`.

### Cadenas de Caracteres
Las constantes cadena van encerradas entre comillas dobles (`"¡Hola, mundo!"`) o entre comillas simples (`'¡Hola, mundo!'`) *[cada grupo deberá implementar obligatoriamente solo la que le corresponda]*. Puede aparecer cualquier carácter imprimible en la cadena (por tanto, no es válido un salto de línea como tal; para usar el salto de línea, se pueden emplear las secuencias de escape).

Para representar caracteres especiales dentro de una cadena se utiliza una secuencia de escape. Una secuencia de escape se representa mediante el carácter barra inversa seguido de un determinado carácter. Algunos de estos caracteres son: el salto de línea (`\n`) o el tabulador (`\t`) *[la implementación de estos caracteres especiales es opcional]*.

Una cadena puede estar vacía o contener hasta un máximo de 64 caracteres.

### Lógicas
En MyJS existen dos constantes lógicas para representar verdadero y falso: `true` y `false` *[es opcional implementar las constantes lógicas, que son las palabras reservadas true y false]*.

---

## Operadores
Este lenguaje presenta un conjunto de operadores con los que escribir distintas expresiones. Además, se pueden utilizar los paréntesis para agrupar subexpresiones *[es obligatorio implementar los paréntesis]*. Las expresiones pueden tener varios operadores, varios operandos, paréntesis... Las expresiones se pueden utilizar en multitud de construcciones del lenguaje: asignaciones, condiciones, parámetros de una función, instrucciones de salida, instrucciones de retorno...

### Operadores Aritméticos
Son los operadores que permiten realizar la suma, resta, producto, división y módulo: `+`, `-`, `*`, `/` y `%` *[obligatorio implementar al menos uno y dos como máximo]*. Se aplican sobre datos ambos enteros o ambos reales, proporcionando un resultado entero o real, respectivamente (en el caso de la división entera, redondeando el valor si es necesario).

También existen los operadores más y menos unarios: `+`, `-` *[implementación opcional]*. Estos operadores se pueden utilizar delante de una constante entera o real, una variable o una expresión.

### Operadores de Relación
Son los operadores que permiten realizar las comparaciones de igual, distinto, menor, mayor, menor o igual, mayor o igual: `==`, `!=`, `<`, `>`, `<=` y `>=` *[obligatorio implementar al menos uno de los operadores y dos como máximo]*. Se aplican sobre datos numéricos del mismo tipo y proporcionan un resultado lógico.

### Operadores Lógicos
Representan las operaciones de conjunción, disyunción y negación: `&&`, `||` y `!` *[obligatorio implementar al menos uno y dos como máximo]*. Se aplican sobre datos lógicos y devuelven un resultado lógico.

### Operadores de Incremento y Decremento
Permiten auto-incrementar o auto-decrementar el valor de una variable entera: `++` y `--` (pueden actuar como prefijos o como sufijos) *[algunos grupos tienen que implementar uno de estos operadores]*. Se aplican sobre variables enteras y devuelven un resultado entero modificando también el valor de la variable. Ejemplo:
```javascript
a = j++; /* si j valía 5, ahora a == 5 y j == 6 */
a = ++j; /* si j valía 5, ahora a == 6 y j == 6 */
```

### Operadores de asignación
Permiten realizar asignaciones simples o realizando simultáneamente una operación: `=` (asignación), `+=` (asignación con suma), `-=` (asignación con resta), `*=` (asignación con producto), `/=` (asignación con división), `%=` (asignación con módulo), `&=` (asignación con y lógico) y `|=` (asignación con o lógico) *[todos los grupos tienen que implementar la asignación simple (=) y algunos grupos deberán implementar uno de los operadores de asignación con operación]*. Ejemplo:
```javascript
n += m;    /* es equivalente a n = n + m */
b1 &= b2;  /* es equivalente a b1 = b1 && b2 */
```
No se permite usar estos operadores de asignación como parte de las expresiones.

### Precedencia de Operadores
En la tabla siguiente se muestra la precedencia de los operadores con el siguiente significado: los operadores del mismo grupo tienen la misma precedencia y, conforme se desciende por la tabla, la precedencia aumenta. La asociatividad de cada grupo de operadores se indica también en la tabla. En cualquier caso, el uso de paréntesis permite alterar el orden de evaluación de las expresiones *[es obligatorio para todos los grupos tener en cuenta la precedencia y asociatividad de los operadores utilizados]*.

| Operadores | Significado | Asociatividad |
| :--- | :--- | :--- |
| `||` | O lógico | Izquierda a derecha |
| `&&` | Y lógico | Izquierda a derecha |
| `==`, `!=` | Igual, Distinto | Izquierda a derecha |
| `>`, `>=`, `<`, `<=` | Mayor, Mayor o igual, Menor, Menor o igual | Izquierda a derecha |
| `+`, `-` | Suma, Resta | Izquierda a derecha |
| `*`, `/`, `%` | Producto, División, Módulo | Izquierda a derecha |
| `!`, `++`, `--`, `+` (unario), `-` (unario) | Negación lógica, Autoincremento, Autodecremento, Más unario, Menos unario | Derecha a izquierda |

---

## Identificadores
Los nombres de identificadores están formados por cualquier cantidad de letras, dígitos y subrayados (`_`), siendo el primero siempre una letra o un subrayado. Ejemplos: `a`, `a3`, `A3`, `_Sueldo_de_Trabajador`, `z__9_9__`...

Como ya se ha dicho, el lenguaje diferencia minúsculas y mayúsculas, por lo que los nombres `a3` y `A3` son identificadores distintos.

---

## Declaraciones
El lenguaje MyJS no exige declaración explícita de las variables que se utilicen. En el caso de que se use un nombre de variable que no ha sido declarado previamente, se considera que dicha variable es global y entera (declaración implícita).

Para realizar una declaración explícita de una variable, se coloca la palabra `let` seguida del tipo (que deberá ser entero, real, lógico o cadena) y del nombre de la variable.
```javascript
let Tipo variable;
```
Pueden realizarse declaraciones explícitas en cualquier lugar dentro de una función; en este caso, la variable será local y visible desde ese punto hasta el final de la función. También pueden realizarse declaraciones explícitas fuera de las funciones en cualquier parte del código (variables globales), pero esas variables solo pueden utilizarse a partir de dicha línea.

Opcionalmente, puede inicializarse una variable en la misma instrucción de la declaración, colocando el operador de asignación (`=`) seguido de una expresión *[es opcional implementar la inicialización de variables]*.
```javascript
let Tipo var4 = expresión4;
```
Si una variable no se inicializa cuando se declara, se realiza una inicialización por omisión basándose en su tipo: `0` si es entera, `0.0` si es real, falso si es lógica y la cadena vacía (`""` o `''`) si es cadena.

En resumen, el ámbito de una variable será global si se declara fuera de cualquier función (o si no se declara), y será local si se declara dentro del cuerpo de una función. Cualquier otro bloque (por ejemplo, los usados por las sentencias de control) no define un ámbito nuevo. Por otro lado, el nombre de las funciones siempre estará en el ámbito global. No se admite la redeclaración del mismo identificador en un mismo ámbito.

---

## Tipos de Datos
El lenguaje dispone de distintos tipos de datos básicos. Se deben considerar sólo los siguientes tipos de datos básicos: entero, real, lógico, cadena y vacío. El lenguaje no tiene conversiones automáticas entre tipos.

* **entero (`int`)**: Se refiere a un número entero que ocupa un tamaño de 1 palabra (16 bits).
* **real (`float`)**: Se refiere a un número real que ocupa un tamaño de 2 palabras (32 bits).
* **lógico (`boolean`)**: Se refiere a un valor lógico. El tipo lógico se almacena como un entero, por lo que ocupa también un tamaño de 1 palabra (16 bits). Las expresiones relacionales y lógicas devuelven un valor lógico.
* **cadena (`string`)**: Se refiere a una secuencia de caracteres. Una variable de tipo cadena ocupa 64 palabras (128 bytes), es decir, un máximo de 64 caracteres.
* **vacío (`void`)**: Permite indicar la ausencia de tipo. Solamente tiene sentido como el tipo de retorno de una función o para indicar ausencia de parámetros en una función. No se pueden declarar variables de tipo vacío.

Ejemplos:
```javascript
let int i = 11;     // variable entera
let string st;      // variable cadena 
let boolean b;      // variable lógica
let int c= 66+i;    // variable entera
let float f = 0.1;  // variable real

b = i != c + 1;     // i y c+1 son enteros; b valdrá verdadero
c = c + i;          // i y c son enteras; c valdrá 88
i = b + i;          // Error: no se puede sumar un lógico con un entero
i = f * i;          // Error: no se puede multiplicar un real con un entero
b = ! i;            // Error: el operador de negación solo puede aplicarse a lógicos
```

---

## Instrucciones de entrada/salida
Las instrucciones de entrada/salida disponibles en el lenguaje son dos. Su uso tiene la sintaxis de una sentencia.

La instrucción `write` evalúa la expresión e imprime el resultado por pantalla. La expresión puede ser de tipo cadena, real o entera. Por ejemplo:
```javascript
c = 50; 
write c * 2 + 16 ;        /** imprime: 116 **/
a = 'Adiós';
write 'Hola'; write a;    // imprime HolaAdiós
```

La instrucción `read` lee un número o una cadena del teclado y lo almacena en la variable indicada, que tiene que ser de tipo entero, real o cadena. Por ejemplo:
```javascript
let int a;
let string c;
let float r;
read a;      /* lee un número entero */
read r;      /* lee un número real */
write a * a; // imprime el cuadrado del entero leído 
write r * r; // imprime el cuadrado del real leído 
write "Pon tu nombre";
read c;      /* lee una cadena */
write ("Hola, ");
write(c);    // imprime las cadenas
```

---

## Sentencias
De todo el grupo de sentencias del lenguaje JavaScript, se han seleccionado para ser implementadas las que aparecen a continuación *[opcional u obligatoriamente, según se indique en cada caso]*. Además de las sentencias aquí indicadas, también se consideran sentencias en este lenguaje las instrucciones de entrada/salida (`write` y `read`), así como las declaraciones de variables.

### Sentencias de Asignación
Existe una sentencia de asignación en MyJS, que se construye mediante el símbolo de asignación `=` *[es obligatorio implementar la sentencia de asignación por todos los grupos; los grupos que tengan el operador de asignación con operación deberán implementar también la sentencia de asignación con operación con el operador asignado]*. 
Su sintaxis general es: `variable operador-asignación expresión`. 
```javascript
i = 8 + 6;
```
Como ya se ha indicado, no hay conversiones entre tipos, por lo que tanto el identificador como la expresión han de ser del mismo tipo.
```javascript
i = 123;       // i es una variable global entera no declarada
let string cad;
let float r;
write i;       // imprime el valor entero 123
cad = 'hola';
r= -1234.56;
write cad;     // imprime el valor cadena "hola"
i = r > 8.8;   // Error: no se puede asignar un lógico a un entero
```

### Sentencia de Llamada a una Función
Esta sentencia permite invocar la ejecución de una función que debe estar previamente definida *[implementación obligatoria]*.
La llamada a una función se realiza mediante el nombre de la función seguido de los parámetros actuales (separados por comas) entre paréntesis (si no tiene parámetros, hay que poner los paréntesis vacíos). Los parámetros actuales tienen que coincidir en número y tipo con los parámetros formales.
```javascript
p1 (5.5);       /* llamada a una función con un argumento real */ 
p2 ();          /* llamada sin parámetros a una función */ 
p3 ("", i - 8); /* llamada con dos argumentos a una función */
```
Si una función devuelve un valor, podrá incluirse una llamada a dicha función dentro de cualquier expresión. Si la llamada se realiza como una sentencia, se invocará a la función pero el valor devuelto se perderá:
```javascript
if (fun1 (9))    /* llamada a una función con un argumento entero */ 
c = b + fun2 (b, fun3() != 8); 
fun2 (c, true);  /* el valor devuelto por fun2 se pierde */
```

### Sentencia de Retorno de una Función
MyJS dispone de la sentencia `return` para finalizar la ejecución de una función y volver al punto desde el que fue llamada *[implementación obligatoria]*.
* Una función finalizará su ejecución cuando se ejecute una instrucción `return` o al llegar al final del cuerpo de la función.
* Si una función tiene tipo devuelto, sus sentencias `return` deberán contener una expresión del mismo tipo.
* Si una función no tiene tipo devuelto (`void`), sus sentencias `return` no deberán contener una expresión.

```javascript
function int ProductoDoble (int _a, int _b) {
  j = _a * _b;	// j no está declarada, por lo que es global y entera
  return j * 2;
}

function void pro (int x) {
  x = ProductoDoble (x - 1, x);
  if (x > (194/2)) return; // finaliza la ejecución si se ejecuta
  write ProductoDoble (x, x);
}
```

### Sentencia Condicional sencilla
Selecciona la ejecución de una sentencia, dependiendo del valor correspondiente de una condición de tipo lógico *[implementación obligatoria para todos los grupos]*:
```javascript
if (condición) sentencia
```
Si la condición lógica es cierta, se ejecuta la sentencia simple (asignación, E/S, llamada a función, retorno, break, incremento/decremento).
```javascript
if (a > b) c = b;
if (fin) write("adiós");
```

### Sentencia Condicional compuesta
Selecciona la ejecución de una de las secuencias de sentencias que encierra, dependiendo del valor de una condición lógica. Tiene dos formatos *[implementación obligatoria de ambos para los grupos que les corresponda]*:
```javascript
if (condición) {
   cuerpo1
}

if (condición) {
   cuerpo1
} else {
   cuerpo2
}
```
Ejemplo:
```javascript
if (a > b) {
  c = b;
} else {
  c = a;
  if (fin) {
    write "adiós";
  }
}
```

### Sentencia Repetitiva `while`
Esta sentencia permite repetir la ejecución de unas sentencias basándose en el resultado de una expresión lógica *[implementación obligatoria para algunos grupos]*.
```javascript
while (condición) {
   cuerpo
}
```
Ejemplo:
```javascript
while (n <= 10) {
    n = n + 1;
    write n;
}
```

### Sentencia Repetitiva `do while`
Esta sentencia permite repetir la ejecución de las sentencias del bucle mientras se cumpla una condición *[implementación obligatoria para algunos grupos]*. Primero se ejecuta el cuerpo y luego se evalúa la condición.
```javascript
do {
   cuerpo
} while (condición);
```
Ejemplo:
```javascript
do {
  c = a++;
  c *= b;
} while (a < b);
```

### Sentencia Repetitiva `for`
Permite ejecutar un bucle según una condición *[implementación obligatoria para algunos grupos]*.
```javascript
for (inicialización; condición; actualización) {
   cuerpo
}
```
La inicialización debe ser una sentencia de asignación sencilla o nada; la condición debe ser una expresión lógica; y la actualización puede ser una asignación, autoincremento/decremento o estar vacía.
```javascript
for (i = 1; i < 10; i++) {
 f *= i;
}
```

### Sentencia de Selección Múltiple
Selecciona y ejecuta unas sentencias basándose en el resultado de una expresión entera *[implementación obligatoria para algunos grupos]*. 
```javascript
switch (expresión) {
  case valor1: cuerpo1
  case valor2: cuerpo2
  /* . . . */
  default: cuerpon
}
```
*[El `default` es opcional para la implementación]*.
```javascript
switch (dia) {
   case 1: write 'lunes';
   case 2: write 'martes';
   case 3: write 'miércoles';
   case 4: write 'jueves';
   case 5: write 'viernes';
   default: write 'fiesta';
}
```

### Sentencia `break`
Aborta la ejecución de un `switch` *[implementación obligatoria para los grupos que tengan switch]*.
```javascript
switch (dia) {
   case 0: break;
   case 1: write "lunes"; break;
   case 2: write "martes";
   case 3: write "miércoles"; break;
   case 4: write "jueves"; break;
   case 5: write "viernes"; break;
   case 6: if (error) break; write("sábado");
   default: if (dia < 8) write "fiesta";
}
```

---

## Funciones
Es necesario definir cada función antes de poder utilizarla. Las funciones pueden devolver un valor de uno de los tipos básicos o no retornar nada (`void`). Los parámetros se pasan siempre **por valor**. MyJS admite recursividad y el lenguaje **no** permite la definición de funciones anidadas.

```javascript
function Tipo nombre (lista de argumentos) {
   sentencias | declaración de variables
}
```

Dentro de una función se tiene acceso a las variables globales, a sus argumentos y a sus variables locales. Si en una función se declara una variable local o un argumento con el mismo nombre que un identificador global, este último no es accesible desde dicha función.

```javascript
function void _hola_ (void) // función sin parámetros
{
   let string x;  // local
   x = "Hello!";
   write x;
}

let int x;  // global

function int factorial (int x) 
{
  _hola_();
  if (x > 1) 
    return x * factorial (x - 1);
  return 1;
}

function boolean Suma (int aux, int fin, boolean b)
{
    for (x= 1; (x < fin) && b; x= x + 2)
    {
      aux += factorial (aux-1);
    }
    return aux > 10000;
}

function void Imprime (int a)
{
    write (a);
    return;
}

read x;
Imprime (factorial (Suma (x, 3, true)));  
```

---

## Más información sobre el lenguaje
Hay que tener en cuenta que para esta práctica no es necesario tener un conocimiento total del lenguaje JavaScript. Tan solo hay que incorporar el funcionamiento del pequeño subconjunto del lenguaje que se ha explicado aquí, necesario para implementar las partes comunes a todos los grupos, así como las partes obligatorias de cada grupo. 

En caso de inconsistencia con JavaScript estándar, deberá elegirse el comportamiento aquí descrito.
