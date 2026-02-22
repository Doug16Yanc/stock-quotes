#!/bin/bash
set -e

TOXIPROXY_URL="http://localhost:8474"

echo "Removing chaos from postgres_proxy, redis_proxy and finnhub_proxy..."

curl --fail -X DELETE "$TOXIPROXY_URL/proxies/postgres_proxy/toxics/latency_downstream"

curl --fail -X DELETE "$TOXIPROXY_URL/proxies/redis_proxy/toxics/latency_downstream"

curl --fail -X DELETE "$TOXIPROXY_URL/proxies/finnhub_proxy/toxics/latency_downstream"

echo "Chaos removed. System should be recovering — watch the Circuit Breaker close."