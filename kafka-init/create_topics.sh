#!/bin/bash
set -euo pipefail

BOOTSTRAP=${KAFKA_BOOTSTRAP_SERVERS:-kafka:9092}
TOPICS=${TOPICS:-booking.events}

# Find kafka-topics binary
KAFKA_TOPICS_BIN=""
if command -v kafka-topics >/dev/null 2>&1; then
  KAFKA_TOPICS_BIN=$(command -v kafka-topics)
elif [ -x "/opt/bitnami/kafka/bin/kafka-topics.sh" ]; then
  KAFKA_TOPICS_BIN="/opt/bitnami/kafka/bin/kafka-topics.sh"
elif [ -x "/usr/bin/kafka-topics" ]; then
  KAFKA_TOPICS_BIN="/usr/bin/kafka-topics"
elif [ -x "/usr/local/bin/kafka-topics" ]; then
  KAFKA_TOPICS_BIN="/usr/local/bin/kafka-topics"
fi

if [ -z "${KAFKA_TOPICS_BIN}" ]; then
  echo "kafka-init: error: kafka-topics command not found in container PATH or known locations"
  exit 1
fi

echo "kafka-init: using kafka-topics at ${KAFKA_TOPICS_BIN}"

echo "kafka-init: waiting for Kafka at ${BOOTSTRAP}..."
# Wait for Kafka to accept connections
for i in {1..60}; do
  if ${KAFKA_TOPICS_BIN} --bootstrap-server "${BOOTSTRAP}" --list >/dev/null 2>&1; then
    echo "kafka-init: Kafka is available"
    break
  fi
  echo "kafka-init: Kafka not ready yet... (${i}/60)"
  sleep 2
done

# Create topics (comma separated)
IFS=',' read -ra TOPIC_ARR <<< "${TOPICS}"
for t in "${TOPIC_ARR[@]}"; do
  topic=$(echo "$t" | xargs)
  if [ -z "$topic" ]; then
    continue
  fi
  echo "kafka-init: checking topic '$topic'"
  if ${KAFKA_TOPICS_BIN} --bootstrap-server "${BOOTSTRAP}" --describe --topic "${topic}" >/dev/null 2>&1; then
    echo "kafka-init: topic '${topic}' already exists"
  else
    echo "kafka-init: creating topic '${topic}'"
    ${KAFKA_TOPICS_BIN} --bootstrap-server "${BOOTSTRAP}" --create --topic "${topic}" --partitions 1 --replication-factor 1 || true
  fi
done

echo "kafka-init: done"
