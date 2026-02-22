#!/bin/bash
set -e
echo "Configuring Toxiproxy proxies..."

curl -X POST http://localhost:8474/proxies \
  -H "Content-Type: application/json" \
  -d '{"name":"postgres_proxy","listen":"0.0.0.0:20001","upstream":"stock-quotes-postgres:5432","enabled":true}'

curl -X POST http://localhost:8474/proxies \
  -H "Content-Type: application/json" \
  -d '{"name":"redis_proxy","listen":"0.0.0.0:20002","upstream":"stock-quotes-redis:6379","enabled":true}'


curl -X POST http://localhost:8474/proxies \
  -H "Content-Type: application/json" \
  -d '{
    "name":"finnhub_proxy",
    "listen":"0.0.0.0:20003",
    "upstream":"finnhub.io:443",
    "enabled":true
  }'

echo "Toxiproxy configuration complete."