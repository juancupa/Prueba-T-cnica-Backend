#!/bin/bash
set -e
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
  CREATE DATABASE productos_db;
  CREATE DATABASE inventario_db;
EOSQL
