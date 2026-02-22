# Procesador MyJS - Grupo 48

[cite_start]**Asignatura**: Procesadores de Lenguajes [cite: 13]
[cite_start]**Institución**: Universidad Politécnica de Madrid (UPM) - Escuela Técnica Superior de Ingenieros Informáticos [cite: 2, 3, 4]

## Descripción del Proyecto

[cite_start]Este proyecto consiste en un procesador para el lenguaje MyJS[cite: 5]. [cite_start]El procesador desarrollado analiza programas escritos en MyJS, realizando análisis léxico, sintáctico y semántico, gestionando tablas de símbolos y reportando errores (léxicos, sintácticos y semánticos)[cite: 20]. 
[cite_start]El programa fuente se procesa desde un único fichero y el resultado de cada módulo se vuelca en los ficheros exigidos por la práctica (`tokens.txt`, `tablas.txt`, `parse.txt`)[cite: 21]. [cite_start]El análisis sigue un paradigma *fail-fast*, abortando el proceso ante el primer error detectado[cite: 331].

### Opciones Asignadas (Grupo 48)
[cite_start]El subconjunto específico del lenguaje implementado por este grupo incluye[cite: 29]:
* [cite_start]**Sentencia repetitiva**: `for`[cite: 29].
* [cite_start]**Operador especial**: asignación con suma (`+=`)[cite: 29].
* [cite_start]**Comentarios**: de bloque `/* ... */`[cite: 29].
* [cite_start]**Cadenas de caracteres**: con comillas simples `'...'`[cite: 29].
* [cite_start]**Técnica de análisis sintáctico**: Descendente recursivo[cite: 29].

---

## Flujo de Ejecución

1. [cite_start]**Análisis léxico**: Lee el código fuente y genera la secuencia de tokens[cite: 23]. [cite_start]Además, da de alta los identificadores en la tabla de símbolos activa cuando aparecen por primera vez[cite: 24].
2. [cite_start]**Análisis sintáctico**: Realizado de forma descendente recursiva basándose en una gramática LL(1)[cite: 25, 104]. [cite_start]Consume la secuencia de tokens mediante procedimientos asociados a los no terminales de la gramática y va registrando la traza del *parse* para su visualización posterior con VASt[cite: 25].
3. [cite_start]**Análisis semántico**: Durante el reconocimiento sintáctico se ejecutan las acciones semánticas[cite: 26]. [cite_start]Estas acciones consultan y actualizan la tabla de símbolos y comprueban las restricciones del lenguaje[cite: 26].
4. [cite_start]**Gestión de errores**: Si se detecta un error, se informa al usuario indicando la línea y el motivo, y se aborta el análisis[cite: 27].

---

## Estructura del Repositorio

La jerarquía del proyecto se organiza de la siguiente manera:

```text
📁 Procesador-MyJS
├── 📁 Gramatica                           # Ficheros relacionados con la gramática y VASt
├── 📁 Pruebas Draco                       # Casos de prueba adicionales/externos
├── 📁 Pruebas Semantico                   # Casos de prueba enfocados en la semántica
├── 📁 Pruebas Sintactico                  # Casos de prueba enfocados en la sintaxis
├── 📁 tslib                               # Librerías y dependencias necesarias
├── 📄 .gitignore                          # Archivos ignorados por Git
├── 📄 ALexico.java                        # Implementación del Analizador Léxico
├── 📄 ASintacticoSemantico.java           # Analizador Sintáctico y Semántico combinados (Traducción Dirigida por Sintaxis)
├── 📄 ExcepcionLexico.java                # Manejo de excepciones léxicas personalizadas
├── 📄 ExcepcionSintacticoSemantico.java   # Manejo de excepciones sintácticas y semánticas
├── 📄 InfoToken.java                      # Estructura auxiliar para preservar información del token (ej. línea) en errores
├── 📄 Procesador.java                     # Clase principal (Main) que orquesta la ejecución
├── 📄 ProcesadorMyJS.jar                  # Ejecutable compilado del proyecto
├── 📄 TablaSimbolos.java                  # Módulo para la gestión de la TS Global y las TS Locales
├── 📄 entrada.txt                         # Fichero de texto con el código fuente MyJS a analizar
├── 📄 parse.txt                           # Fichero de salida con las reglas de derivación aplicadas
├── 📄 tablas.txt                          # Fichero de salida con el volcado de la Tabla de Símbolos
├── 📄 tokens.txt                          # Fichero de salida con la lista de tokens reconocidos
└── 📄 README.md                           # Documentación del proyecto
