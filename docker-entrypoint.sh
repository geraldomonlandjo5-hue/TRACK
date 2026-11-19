#!/bin/bash
set -e

: "${DB_URL:?Variable DB_URL obligatoire}"
: "${DB_USER:?Variable DB_USER obligatoire}"
: "${DB_PASSWORD:?Variable DB_PASSWORD obligatoire}"

PORT="${PORT:-10000}"
WILDFLY="/opt/jboss/wildfly"

# Démarrer WildFly
"$WILDFLY/bin/standalone.sh" \
  -b 0.0.0.0 \
  -bmanagement 127.0.0.1 \
  -Djboss.http.port="$PORT" &

WILDFLY_PID=$!

# Attendre le démarrage du serveur
for i in $(seq 1 90); do
  if ! kill -0 "$WILDFLY_PID" 2>/dev/null; then
    echo "Erreur : WildFly s'est arrêté."
    exit 1
  fi

  if "$WILDFLY/bin/jboss-cli.sh" --connect --command=":read-attribute(name=server-state)" >/dev/null 2>&1; then
    break
  fi

  sleep 2
done

# Vérifier que WildFly est prêt
"$WILDFLY/bin/jboss-cli.sh" --connect --command=":read-attribute(name=server-state)" >/dev/null

# Installer le pilote JDBC
"$WILDFLY/bin/jboss-cli.sh" --connect --command="/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver)"

# Configurer PostgreSQL Neon
"$WILDFLY/bin/jboss-cli.sh" --connect --command="/subsystem=datasources/data-source=TrackDS:add(jndi-name=java:/jdbc/TrackDS,driver-name=postgresql,connection-url=\"$DB_URL\",user-name=\"$DB_USER\",password=\"$DB_PASSWORD\",min-pool-size=1,max-pool-size=10,enabled=true)"

# Tester le datasource
"$WILDFLY/bin/jboss-cli.sh" --connect --command="/subsystem=datasources/data-source=TrackDS:test-connection-in-pool"

wait "$WILDFLY_PID"
