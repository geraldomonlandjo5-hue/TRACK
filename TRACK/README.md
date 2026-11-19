# TRACK — Package Tracking System

Application Maven WAR (Java 21, Jakarta EE 11, WildFly, PostgreSQL) de suivi de colis pour une entreprise
organisée en agences. Architecture : Servlets / JSP (MVC), Service, DAO, JPA.

## Prérequis
- JDK 21, Apache NetBeans (ou Maven 3.9+)
- WildFly configuré avec Java 21
- PostgreSQL, driver installé comme module WildFly, datasource `java:/jdbc/TrackDS`

## 1. Base de données
```sql
CREATE DATABASE track_db;
CREATE USER track_user WITH ENCRYPTED PASSWORD 'CHANGER_CE_MOT_DE_PASSE';
GRANT ALL PRIVILEGES ON DATABASE track_db TO track_user;
ALTER DATABASE track_db SET timezone TO 'Africa/Porto-Novo';   -- même fuseau que la JVM (voir §5)
```
Puis, connecté à `track_db` (PostgreSQL 15+ : `GRANT ALL ON SCHEMA public TO track_user;`), exécuter **dans cet ordre** :

| Script | Quand |
|---|---|
| `database/schema.sql` | toujours (crée toutes les tables, relançable) |
| `database/seed-admin.sql` | une fois : rôles + compte administrateur initial |
| `database/seed-test-benin.sql` | **test uniquement** : 11 colis d'exemple |
| `database/seed-test-agences.sql` | **test uniquement** : 3 agences + 4 comptes de test |
| `database/migration-core.sql` | **à la place de `schema.sql`** si vous mettez à jour une base existante (sauvegardez avant) |

Le schéma n'est plus généré par Hibernate : les scripts SQL sont la seule source de vérité.

## 2. Datasource WildFly
`docs/wildfly-datasource.cli` déclare driver et datasource (non-JTA, cohérent avec l'unité de persistance
RESOURCE_LOCAL). Remplacez le mot de passe, puis :
```bash
$WILDFLY_HOME/bin/jboss-cli.sh --connect --file=docs/wildfly-datasource.cli
```

## 3. Lancer
NetBeans : *File > Open Project* sur le dossier `TRACK` → *Clean and Build* → *Run*. En ligne de commande :
`mvn clean package` puis déployer `target/track.war`.

- Application : `http://localhost:8080/track/` — Connexion : `/login` — Suivi public : `/track`
- Tests unitaires : `mvn test`

## 4. Comptes et rôles
Compte initial (après `seed-admin.sql`) : voir l'en-tête du script. **Changez ce mot de passe tout de suite** :
```bash
mvn -q compile
java -cp target/classes com.track.security.PasswordTool "NouveauMotDePasse"   # affiche le hash
# puis : UPDATE users SET password_hash = '<hash>' WHERE email = 'admin@track.local';
```
Comptes de test (`seed-test-agences.sql`, mot de passe `TestTrack2026!`) : `agent.cotonou@`, `responsable.cotonou@`,
`livreur.cotonou@`, `agent.portonovo@` + `@track.local`. À ne jamais créer en production.

| Rôle | Voit | Crée un colis | Peut mettre à jour vers |
|---|---|---|---|
| SUPER_ADMIN, ADMIN | toutes les agences | oui | tous les statuts |
| RESPONSABLE_AGENCE, AGENT | son agence | oui (rattaché à son agence) | tous les statuts |
| LIVREUR | son agence | non | En cours de livraison, Livré, Échec, Retourné |
| rôle inconnu / agent sans agence | rien | non | rien |

Chaque mise à jour est enregistrée avec son auteur (`tracking_events.created_by`) et peut être masquée au client
(case « Visible par le client »). Les colis créés par un administrateur n'ont pas d'agence : seuls les administrateurs les voient.

## 5. Mise en production (checklist)
- **HTTPS obligatoire**, puis activer le cookie `Secure` : option JVM `-Dtrack.secure.cookies=true`
  (ou variable `TRACK_SECURE_COOKIES=true`). `HttpOnly` et `SameSite=Lax` sont déjà posés.
- **Fuseau horaire** : lancer la JVM avec `-Duser.timezone=Africa/Porto-Novo` (le même que la base) pour que les dates
  créées par l'application et celles du SQL soient cohérentes.
- **Reverse proxy** : la limitation de tentatives (login : 5 échecs/15 min par e-mail, 20 par IP ; suivi public : 30/min
  par IP) utilise l'adresse IP de la connexion. Derrière un proxy, activez le transfert de l'IP cliente dans WildFly,
  sinon tous les visiteurs partagent la même limite. Le compteur est en mémoire (une instance) : en cluster, le remplacer.
- Bootstrap et Google Fonts viennent d'un CDN (autorisés par la Content-Security-Policy). Pour retirer cette dépendance
  externe, hébergez-les dans `assets/` et resserrez la CSP dans `SecurityHeadersFilter`.
- Ne pas exécuter `seed-*.sql` en production sans changer le mot de passe administrateur ; sauvegardes PostgreSQL régulières.

## 6. Structure
```
src/main/java/com/track/
  controller/  Login, Logout, Dashboard, Shipment, Tracking, Home (servlets)
  filter/      SecurityHeadersFilter, AuthenticationFilter, CsrfFilter (ordre déclaré dans web.xml)
  security/    Role, CurrentUser, Csrf, RateLimiter, PasswordUtil, PasswordTool
  service/     ShipmentService (validation + autorisation), DashboardStats, Page
  dao/         ShipmentDAO, UserDAO
  model/       Shipment, TrackingEvent, ShipmentStatus (statuts + transitions)
  listener/    AppListener (cookie de session, fermeture de l'EMF)
  util/        JpaUtil
src/main/webapp/WEB-INF/{views,tags}   JSP, fragments, tags status/datetime
database/      schema, migration, seeds       docs/   sécurité, datasource
```
Voir `CHANGELOG.md` pour le détail des corrections et `docs/NOTES-SECURITE.md` pour l'état de la sécurité.
