#!/bin/bash
set -euo pipefail

# Configuración de parámetros del benchmark
HOST_URL="http://localhost:8080"
THREADS=8
CONNECTIONS=100
DURATION="30s"
WARMUP="15s"
ITERATIONS=3
ENDPOINTS=("/ping-trad" "/ping-reactive" "/ping-virtual")
RESULT_DIR="results"
TIMESTAMP=$(date '+%Y%m%d_%H%M%S')
OUTPUT_FILE="${RESULT_DIR}/benchmark_${TIMESTAMP}.txt"

# Crear directorio de resultados
mkdir -p "$RESULT_DIR"
touch "$OUTPUT_FILE"

# Redirigir toda la salida (stdout y stderr) con tee para pantalla y archivo
exec > >(tee -a "$OUTPUT_FILE") 2>&1

echo "=== Benchmark de endpoints Quarkus ==="
echo "Parámetros: duración=$DURATION, conexiones=$CONNECTIONS, hilos=$THREADS, repeticiones=$ITERATIONS"
echo "Resultados completos en: $OUTPUT_FILE"
echo

# Función para ejecutar wrk en un endpoint dado
run_benchmark() {
    local endpoint="$1"
    echo "Ejecutando endpoint '$endpoint':"
    echo "  - Calentamiento (${WARMUP})"
    # Calentamiento (output descartado)
    wrk -t"$THREADS" -c"$CONNECTIONS" -d"$WARMUP" "${HOST_URL}${endpoint}" > /dev/null

    # Tres corridas de prueba
    for i in $(seq 1 "$ITERATIONS"); do
        echo "  Corrida $i / $ITERATIONS:"
        # Ejecutar wrk con latencia
        output=$(wrk -t"$THREADS" -c"$CONNECTIONS" -d"$DURATION" --latency "${HOST_URL}${endpoint}")
        # Mostrar salida completa de wrk
        echo "$output"

        # Extraer métricas con awk
        req=$(echo "$output" | awk '/Requests\/sec/ {print $2}')
        # Latencia promedio (Avg) de la línea de Thread Stats
        avg=$(echo "$output" | awk '/Thread Stats/ {getline; print $2}')
        # Percentiles 90% y 99% de la distribución
        p90=$(echo "$output" | awk '/90%/ {print $2}')
        p99=$(echo "$output" | awk '/99%/ {print $2}')

        # Convertir valores a milisegundos (ms) numéricos para cálculo
        # Función auxiliar: extraer número y unidad
        convert_to_ms() {
            local val_unit="$1"
            local num="${val_unit//[!0-9.]/}"
            local unit="${val_unit//[0-9.]/}"
            case "$unit" in
                us)  awk "BEGIN {printf \"%.2f\", $num/1000}" ;;
                ms)  awk "BEGIN {printf \"%.2f\", $num}" ;;
                s)   awk "BEGIN {printf \"%.2f\", $num*1000}" ;;
                *)   echo "0" ;;
            esac
        }
        avg_ms=$(convert_to_ms "$avg")
        p90_ms=$(convert_to_ms "$p90")
        p99_ms=$(convert_to_ms "$p99")
        # Calcular p95 como promedio lineal entre p90 y p99
        p95_ms=$(awk "BEGIN {printf \"%.2f\", ($p90_ms + $p99_ms) / 2}")

        # Mostrar resumen de la corrida actual
        echo "    Req/s: $req"
        echo "    Latencia Avg: ${avg_ms}ms"
        echo "    p95 (aprox): ${p95_ms}ms"
        echo "    p99: ${p99_ms}ms"
        echo

        # Almacenar en arrays asociativos para tabla final
        summary_req["$endpoint,$i"]=$req
        summary_avg["$endpoint,$i"]=$avg_ms
        summary_p95["$endpoint,$i"]=$p95_ms
        summary_p99["$endpoint,$i"]=$p99_ms
    done
    echo "----------------------------------------"
}

# Declarar arrays asociativos para guardar resultados
declare -A summary_req summary_avg summary_p95 summary_p99

# Ejecutar benchmark para cada endpoint
for ep in "${ENDPOINTS[@]}"; do
    run_benchmark "$ep"
done

# Imprimir resumen tabular final
echo -e "\nResumen final:"
printf "%-15s %7s %12s %12s %12s %12s\n" \
    "Endpoint" "Corrida" "Req/s" "Avg Lat (ms)" "p95 (ms)" "p99 (ms)"
echo "--------------------------------------------------------------------------------"
for ep in "${ENDPOINTS[@]}"; do
    for i in $(seq 1 "$ITERATIONS"); do
        req="${summary_req[$ep,$i]}"
        avg_ms="${summary_avg[$ep,$i]}"
        p95_ms="${summary_p95[$ep,$i]}"
        p99_ms="${summary_p99[$ep,$i]}"
        printf "%-15s %7d %12s %12s %12s %12s\n" \
            "$ep" "$i" "$req" "${avg_ms}ms" "${p95_ms}ms" "${p99_ms}ms"
    done
done
