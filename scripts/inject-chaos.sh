#!/bin/bash
set -e

TOXIPROXY_URL="http://localhost:8474"

echo "Injecting chaos: 2000ms latency (±500ms jitter) to postgres_proxy, redis_proxy and finnhub_proxy..."

curl --fail -X POST "$TOXIPROXY_URL/proxies/postgres_proxy/toxics" \
  -H "Content-Type: application/json" \
  -d '{"name":"latency_downstream","type":"latency","stream":"downstream","attributes":{"latency":2000,"jitter":500}}'

curl --fail -X POST "$TOXIPROXY_URL/proxies/redis_proxy/toxics" \
  -H "Content-Type: application/json" \
  -d '{"name":"latency_downstream","type":"latency","stream":"downstream","attributes":{"latency":2000,"jitter":500}}'

curl --fail -X POST "$TOXIPROXY_URL/proxies/finnhub_proxy/toxics" \
  -H "Content-Type: application/json" \
  -d '{"name":"latency_downstream","type":"latency","stream":"downstream","attributes":{"latency":2000,"jitter":500}}'

echo "Chaos injected. Monitor your Resilience4j Circuit Breaker status."