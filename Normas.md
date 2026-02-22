# Práctica - 2025/2026

## Contenido
* [Normas de la Práctica](#normas-de-la-práctica)
    * [Funcionamiento del Procesador](#funcionamiento-del-procesador)
    * [Presentación](#presentación)
    * [Calificación](#calificación)
    * [Plazos de entrega](#plazos-de-entrega)
* [Especificación de la Práctica](#especificación-de-la-práctica)
* [Lenguaje MyJS](#lenguaje-myjs)
* [Técnicas de Análisis](#técnicas-de-análisis)

---

## Normas de la Práctica

* **Grupos:** Las Prácticas están propuestas para ser realizadas en grupos de un tamaño máximo de 3 personas. En la valoración no se tendrá en cuenta el número de miembros, ni las dificultades de coordinación surgidas dentro del grupo.
* **Alta:** Los estudiantes matriculados deben formar los grupos de trabajo apuntando su composición a través de la página de grupos. Este es el mecanismo mediante el que se obtiene el número de grupo. Tras darse de alta, se asignarán las opciones del Procesador (características del lenguaje, tipo de Analizador Sintáctico, etc.).
* **Identificación:** Cada grupo se identifica por un número que debe figurar siempre en las memorias y materiales entregados. Para cualquier consulta será imprescindible conocer el número de grupo.
* **Objetivo:** La Práctica consiste en el diseño e implementación de un Procesador de un Lenguaje, que realice el **Análisis Léxico, Sintáctico y Semántico** (incluyendo la Tabla de Símbolos y el Gestor de Errores), para un determinado lenguaje de programación. 

* **Metodología:** El trabajo se abordará de una manera incremental durante el curso, realizando entregas intermedias.
* **Convocatoria Extraordinaria:** La Práctica (y las opciones asignadas) será la misma que la propuesta en el correspondiente semestre del mismo curso.

### Funcionamiento del Procesador
El Procesador deberá leer el programa fuente de un archivo de texto y entregar necesariamente varios archivos de texto. El funcionamiento tiene que ser obligatoriamente el siguiente:

**Entrada:**
* **Fichero fuente:** El Procesador ha de recibir un archivo de texto cuyo contenido es el programa que se desea analizar.

**Salida:**
Para facilitar las tareas de depuración y corrección, es obligatorio generar los siguientes ficheros:
1. **Fichero de tokens:** Listado de todos los tokens generados. El formato ha de ser, obligatoriamente, el indicado en la página de Documentación.
2. **Fichero de Tabla de Símbolos:** Volcado completo "legible" con toda la información de todas las Tablas de Símbolos (incluidas las locales). Cada Tabla deberá volcarse al fichero después de que haya sido completada y antes de su destrucción. El formato ha de ser el indicado en la Documentación.
3. **Fichero del parse:** Listado de los números de las reglas utilizadas para realizar el Análisis Sintáctico de la entrada. El formato de los ficheros de parse y gramática ha de ser el indicado en la Documentación (se utilizará como entrada para VASt).
4. **Listado de errores:** Si el programa es incorrecto, deberá proporcionarse un listado en formato libre (o por pantalla). Para cada error hay que indicar al menos: el **número de la línea**, el **tipo de error** (léxico, sintáctico o semántico) y un **mensaje claro** (en términos del usuario) que explique el error y permita su corrección.

**Uso de la herramienta VASt:**
Para poder visualizar gráficamente los árboles sintácticos construidos, se cuenta con la herramienta **VASt**, cuyo uso es **obligatorio**.

* Deberá utilizarse en la demostración para mostrar el árbol sintáctico (no se admitirá otra herramienta).
* VASt recibe la gramática independiente del contexto y un *parse*, y visualiza el árbol sintáctico correspondiente.
* **Nota:** Incumplir cualquiera de los requisitos de ficheros y formatos indicados supondrá **no aprobar** la Práctica.

### Presentación
La Práctica se deberá diseñar y programar de forma incremental, realizando **dos entregas parciales** (a través de Moodle) y una **presentación final** (presencial).

#### Primera entrega: Analizador Léxico y Tabla de Símbolos (y Gestor de Errores)
Se deberá entregar una memoria (máximo 10 páginas sin contar anexo) con:
* **Diseño del Analizador Léxico:** Tokens, gramática, diagrama del autómata, acciones semánticas y errores.
* **Diseño inicial de la Tabla de Símbolos:** Estructura y organización (no describir el formato del fichero).
* **Diseño inicial del Gestor de Errores:** Debe informar de los errores léxicos, dando mensaje claro y línea.
* **Anexo con 6 casos de prueba:** 3 correctos (con fichero fuente, tokens y tabla de símbolos) y 3 con errores léxicos (con fichero fuente y listado de errores). Generados sin edición manual.

#### Segunda entrega: Analizador Sintáctico (y Gestor de Errores)
Añadido a los módulos anteriores. Memoria (máximo 10 páginas sin contar anexo) con:
* **Diseño del Analizador Sintáctico:** Gramática, demostración de que es adecuada para el método asignado, y el autómata/tablas/pseudo-código según el tipo.
* **Diseño del Gestor de Errores:** Ampliado para informar de errores sintácticos (mensaje claro y línea).
* **Anexo con 6 casos de prueba:** 3 correctos (fuente, *parse* y árbol generado con VASt) y 3 con errores sintácticos (fuente y errores). Generados sin edición manual.

#### Presentación final (Presencial)
Consiste en:
1. **Una demostración:** Mostrará el funcionamiento del Procesador completo (ficheros generados y árbol con VASt) ante un profesor.
   * Se debe **reservar día y hora** con antelación.
   * Asistir con la memoria y el software preparado.
2. **Una memoria (máx. 30 páginas sin anexo):** Descripción del diseño final (sin incluir listados fuente ni detalles de implementación). Debe incluir:
   * Diseño actualizado del Analizador Léxico y Sintáctico.
   * Diseño del Analizador Semántico (Traducción Dirigida por la Sintaxis).
   * Diseño completo de la Tabla de Símbolos y Gestor de Errores.
   * **Anexo con 10 casos de prueba:** 5 correctos (tokens, árbol VASt, Tabla de Símbolos) y 5 erróneos (mensajes de error).
3. **La implementación:** Entrega de todos los ficheros (fuentes, ejecutable, dependencias, memoria, casos de prueba y gramática en formato VASt).
   * Compilados preferentemente para consola DOS o Windows.
   * Libres de virus (un virus supone el suspenso automático).
   * Trabajo personal: El uso de código de terceros, IAs o copias supone el **suspenso**.

**Evaluación de entregas:** Cada entrega parcial vale un **10%** de la nota. El resto corresponde a la presentación final. No entregar una parcial supone perder ese 10%, pero se puede continuar. No se pueden usar ejemplos de Draco ni otras fuentes; deben ser originales.

### Calificación
Para optar a una nota **no inferior a 4 puntos**, es **imprescindible** que la práctica esté **completa** (análisis léxico, sintáctico, semántico, tabla de símbolos y gestor de errores), produciendo todos los ficheros y permitiendo visualizar los árboles con VASt.

### Plazos de entrega
* **27 de octubre de 2025:** Entrega parcial (Analizador Léxico y Tabla de Símbolos).
* **24 de noviembre de 2025:** Entrega parcial (Léxico, Tabla de Símbolos y Analizador Sintáctico).
* **19 de enero de 2026:** Entrega final (Procesador completo) - Convocatoria Ordinaria.
* **26 de junio de 2026:** Entrega final (Procesador completo) - Convocatoria Extraordinaria.

*Es obligatorio apuntarse en el calendario de presentaciones antes de la fecha límite de entrega.*

---

## Especificación de la Práctica
Diseño y construcción de un Analizador de una versión del lenguaje JavaScript llamado **MyJS**. Se puede utilizar cualquier entorno o lenguaje de programación, siempre que el ejecutable funcione en un PC (preferentemente Windows).

Es necesario que:
* El analizador léxico guarde en un fichero de *tokens*.
* El analizador sintáctico sea visualizable con VASt.
* El analizador semántico y tabla de símbolos se guarden en el fichero de tabla de símbolos.
* Los errores sean descriptivos (línea e indicación).

### Lenguaje MyJS
El fuente estará en un único fichero `.javascript` o `.txt`.

**Parte común a todos los grupos:**
* Estructura general (funciones y declaraciones).
* Definición de funciones.
* Tipos: enteros, reales, lógicos, cadenas y vacío.
* Variables y su declaración.
* Constantes enteras, reales y cadenas.
* Sentencias: asignación, condicional sencilla, llamada a funciones y retorno.
* Entrada/salida por terminal.
* Expresiones y comentarios.
* Operadores (al menos uno de cada tipo):
  * Aritméticos: `+`, `-`, `*`, `/`, `%`
  * Relacionales: `==`, `!=`, `<`, `>`, `<=`, `>=`
  * Lógicos: `&&`, `||`, `!`

**Parte específica de cada grupo (según asignación):**
* **Comentarios:** Bloque (`/* */`) o línea (`//`).
* **Cadenas:** Comillas simples (`' '`) o dobles (`" "`).
* **Sentencias:** `if-else`, `switch-case`, `while`, `do-while`, `for`.
* **Operadores especiales:** `++` (pre/post), `--` (pre/post), `+=`, `-=`, `*=`, `/=`, `%=`, `&=`, `|=`.

### Técnicas de Análisis
Cada grupo tendrá asignado un método de Análisis Sintáctico:
1. Análisis Ascendente
2. Análisis Descendente con tablas
3. Análisis Descendente recursivo

*(Las opciones específicas asignadas a cada grupo se consultarán en la página de grupos).*
