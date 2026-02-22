# Procesador MyJS
Este repositorio alberga el código de la práctica de la asignatura Procesadores de Lenguajes (PdL) impartida en la Universidad Politécnica de Madrid. El objetivo es construir un procesador para un [subconjunto del lenguaje MyJS](Especificacion.md), realizando [análisis léxico](ALexico.java), [sintáctico, semántico](ASintacticoSemantico.java) y la gestión de errores.  

El programa fuente se procesa desde un único fichero `entrada.txt` y el resultado de cada módulo se vuelca en los ficheros exigidos por la práctica (`tokens.txt`, `tablas.txt`, `parse.txt`).

El análisis sigue un paradigma *fail-fast*, abortando el proceso ante el primer error detectado, si bien se proporciona la opción de seguir el análisis léxico en caso de un error sintáctico/semántico, el cual parará si se encuentra un error léxico. Para activar dicha opcion, vaya a [Procesador.java](Procesador.java) y cambie `boolean modoDebug = true`

### Notas de Implementación y Diseño

* **Manejo del Fin de Fichero (`EOF`)**: 
  Durante el desarrollo temprano del Lexer no se consideraba `<eof,>` como un token explícito. Esto generaba un problema en el Parser: si el fichero `entrada.txt` terminaba con un salto de línea (`\n`), el léxico devolvía un token vacío `""` justo antes de llegar al símbolo de fin de cadena (`$`). Al no encontrar el `$`, el Parser daba error y abortaba. Para resolver esta inconsistencia y estabilizar el análisis, se añadió `<eof,>` como un token oficial.
* **Desplazamientos en la Tabla de Símbolos**: 
  Dado que el lenguaje MyJS especificado **no permite la anidación de funciones**, la gestión de ámbitos se simplificó. Para las direcciones o desplazamientos de memoria, se implementó una estrategia visualmente clara: se utilizan índices **positivos** para los registros almacenados en la Tabla Global, e índices **negativos** para las variables y parámetros de las Tablas Locales.


### Opciones Asignadas (Grupo 48)
El subconjunto específico del lenguaje implementado por este grupo incluye:
* **Sentencia repetitiva**: `for`.
* **Operador especial**: asignación con suma (`+=`).
* **Comentarios**: de bloque `/* ... */`.
* **Cadenas de caracteres**: con comillas simples `'...'`.
* **Técnica de análisis sintáctico**: Descendente recursivo.

---

## Flujo de Ejecución

1. **Análisis léxico**: Lee el código fuente y genera la secuencia de tokens. Además, da de alta los identificadores en la tabla de símbolos activa cuando aparecen por primera vez.
2. **Análisis sintáctico**: Realizado de forma descendente recursiva basándose en una gramática LL(1). Consume la secuencia de tokens mediante procedimientos asociados a los no terminales de la gramática y va registrando la traza del *parse* para su visualización posterior con VASt.
3. **Análisis semántico**: Durante el reconocimiento sintáctico se ejecutan las acciones semánticas. Estas acciones consultan y actualizan la tabla de símbolos y comprueban las restricciones del lenguaje.
4. **Gestión de errores**: Si se detecta un error, se informa al usuario indicando la línea y el motivo, y se aborta el análisis.

> [!Note]
> El proceso va encadenado, los módulos se están comunicando SIN generar archivos/bufferes intermedios.

---

## Estructura del Repositorio

- **Código fuente Java:** archivos `.java` en la raíz y en `tslib/`.
- **Archivos de E/S:** `entrada.txt`, `tokens.txt`, `parse.txt`, `tablas.txt`.
- **Gramática:** directorio `Gramatica/` con la gramática y ejemplos válidos de formato.
- **Pruebas:** carpetas `Pruebas Draco/`, `Pruebas Sintactico/` y `Pruebas Semantico/` contienen
  numerosos casos de prueba para las distintas fases del procesador.

> [!Note]
> Sobre las [pruebas de draco](Pruebas%20Draco) mencionar que **no están todas las soluciones (ficheros de salida esperados)** de las pruebas:
> - Léxico: solo tres que, además, corresponden a una versión anterior a la inclusión del token `<eof,>`.
> - Tabla de Símbolos: todas
> - Sintáctico: todos los parse para pruebas sin fallos, pero para las pruebas con fallos nada.
> - Semántico: ninguna solución  
> 
> A pesar de esta ausencia, el sistema fue evaluado con múltiples casos de prueba rigurosos y no presentó ningún fallo durante la comprobación y defensa final de la práctica.

La jerarquía del proyecto se organiza de la siguiente manera (los relevantes):

```text
📁 Procesador-MyJS
├── 📁 Gramatica                           # Ficheros de gramática (con ejemplos de formato)
├── 📁 Pruebas Draco                       # Casos de prueba de Draco de todos los módulos, algunas con soluciones
├── 📁 Pruebas Semantico                   # Casos de prueba propios para el módulo semántico
├── 📁 Pruebas Sintactico                  # Casos de prueba propios para el módulo sintáctico
├── 📁 tslib                               # Librería de la Tabla de Símbolos
├── 📄 ALexico.java                        # Analizador Léxico
├── 📄 ASintacticoSemantico.java           # Analizador Sintáctico y Semántico combinados (Traducción Dirigida por Sintaxis)
├── 📄 ExcepcionLexico.java                # Manejo de excepciones léxicas personalizadas
├── 📄 ExcepcionSintacticoSemantico.java   # Manejo de excepciones sintácticas y semánticas personalizadas
├── 📄 InfoToken.java                      # Estructura auxiliar para preservar información del token en errores
├── 📄 Procesador.java                     # Main
├── 📄 ProcesadorMyJS.jar                  # Ejecutable compilado del proyecto
├── 📄 TablaSimbolos.java                  # Módulo que gestiona tslib, añade una capa más de abstracción, para limpieza
├── 📄 entrada.txt                         # Fichero con el código fuente MyJS a analizar
├── 📄 parse.txt                           # Fichero de salida con las reglas de derivación aplicadas
├── 📄 tablas.txt                          # Fichero de salida con el volcado de las Tablas de Símbolos
├── 📄 tokens.txt                          # Fichero de salida con la lista de tokens reconocidos
```

---

## Cómo Ejecutar
1. Escribe el programa fuente que deseas analizar dentro del archivo entrada.txt.
2. Desde `Procesador.java` presiona el botón `Run` del IDE, o ejecuta el archivo Java compilado: `java -jar ProcesadorMyJS.jar`
3. Verifica los resultados en los archivos de salida generados:
   - tokens.txt: secuencia de todos los tokens identificados.
   - parse.txt: secuencia de reglas sintácticas, lista para ser insertada en la herramienta VASt para visualizar el Árbol Sintáctico.
   - tablas.txt: volcado final de las Tablas de Símbolos (Global y Locales).
   - Consola: Vacío si éxito, mensaje y línea si error.

## Más información
Para más información: [Memoria.pdf](https://github.com/user-attachments/files/25468721/Memoria.Pdl.VFinal.pdf)
