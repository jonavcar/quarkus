#!/usr/bin/env bash
set -euo pipefail

# Benchmark script para endpoints de Threads en Quarkus

# Configuración
URL_BASE="http://localhost:8080/threads"
ENDPOINTS=("default" "custom" "virtual")
DURATION="30s"
WARMUP_DURATION="15s"
THREADS=12
CONNECTIONS=200
REPEATS=3

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RESULT_DIR="results"
RESULT_FILE="${RESULT_DIR}/benchmark_results_${TIMESTAMP}.txt"

mkdir -p "${RESULT_DIR}"

echo "Benchmark iniciado: $(date)" > "${RESULT_FILE}"
echo "Base URL: ${URL_BASE}" >> "${RESULT_FILE}"
echo "Parámetros: threads=${THREADS}, connections=${CONNECTIONS}, duration=${DURATION}, warmup=${WARMUP_DURATION}, repeats=${REPEATS}" >> "${RESULT_FILE}"
echo "--------------------------------------------------" >> "${RESULT_FILE}"

for ep in "${ENDPOINTS[@]}"; do
  echo -e "\n>>> Endpoint: /${ep}" | tee -a "${RESULT_FILE}"

  # Warm-up
  echo "  * Warm-up (${WARMUP_DURATION})..." | tee -a "${RESULT_FILE}"
  wrk -t${THREADS} -c${CONNECTIONS} -d${WARMUP_DURATION} "${URL_BASE}/${ep}" >/dev/null 2>&1

  # Repeticiones formales
  for i in $(seq 1 "${REPEATS}"); do
    echo "  * Run #${i}" | tee -a "${RESULT_FILE}"
    wrk -t${THREADS} -c${CONNECTIONS} -d${DURATION} "${URL_BASE}/${ep}" | tee -a "${RESULT_FILE}"
    echo "" >> "${RESULT_FILE}"
  done

  echo "--------------------------------------------------" >> "${RESULT_FILE}"
done

echo -e "\nBenchmark finalizado: $(date)" | tee -a "${RESULT_FILE}"
echo "Resultados guardados en ${RESULT_FILE}"
