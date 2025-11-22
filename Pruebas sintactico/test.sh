#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Error: No se pasaron archivos de prueba como argumentos"
    echo "Uso: $0 archivo1.txt archivo2.txt ..."
    exit 1
fi

if [ ! -f "Procesador.java" ]; then
    echo "Error: No se encuentra Procesador.java en el directorio actual"
    exit 1
fi

if [ ! -f "Procesador.class" ] || [ "Procesador.java" -nt "Procesador.class" ]; then
    echo "Compilando Procesador.java..."
    javac Procesador.java
    if [ $? -ne 0 ]; then
        echo "Error: Falló la compilación de Procesador.java"
        exit 1
    fi
    echo "Compilación exitosa"
    echo ""
fi

contador=1
exitosas=0
fallidas=0

for archivo_prueba in "$@"; do
    
    echo "PRUEBA $contador: $archivo_prueba"
    
    if [ ! -f "$archivo_prueba" ]; then
        echo "El archivo '$archivo_prueba' no existe, saltando..."
        echo ""
        ((contador++))
        ((fallidas++))
        continue
    fi
    
    cp "$archivo_prueba" "entrada.txt"
    
    if [ $? -ne 0 ]; then
        echo "Error copiando $archivo_prueba"
        ((contador++))
        ((fallidas++))
        continue
    fi
    
    java Procesador
    
    resultado=$?
    if [ $resultado -eq 0 ]; then
        ((exitosas++))
    else
        ((fallidas++))
    fi
    
    echo ""
    ((contador++))
done

echo "RESUMEN: $((contador-1)) pruebas, $exitosas exitosas, $fallidas fallidas"