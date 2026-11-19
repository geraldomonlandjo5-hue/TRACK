# Version consolidée — corrections par rapport à l'archive d'origine

## Archive
- Une seule arborescence (la version la plus récente, auparavant imbriquée dans `TRACK/TRACK/`) ; plus de copies obsolètes,
  de `target/` ni de `.war` périmé (qui contenait l'ancien login de démonstration).
- `pom.xml` : balise `<n>` corrigée en `<name>`, JUnit 5 et Surefire ajoutés.

## Sécurité
- **XSS stocké corrigé** : `<c:out>` partout (dashboard, liste, formulaires, erreurs, titre de page).
- **Autorisation** par rôle (`Role`) et par agence (`shipments.agency_id`), appliquée dans `ShipmentService`.
- **Audit** : `tracking_events.created_by` ; **visibilité client** : `is_public` enfin mappé et respecté sur la page publique.
- **Limitation de tentatives** : login (e-mail + IP) et suivi public (IP) — `RateLimiter`.
- Login : jeton CSRF, hash factice pour égaliser le temps de réponse, révocation des comptes désactivés, `last_login_at` mis à jour.
- Déconnexion en POST + CSRF ; cookie de session `SameSite=Lax`, `Secure` activable ; en-têtes de sécurité et CSP.
- Ordre des filtres déterminé explicitement dans `web.xml`.
- `show_sql` désactivé, `schema-generation` à `none` ; mot de passe d'exemple retiré du script CLI et du README.

## Fonctionnel
- **Points de passage** : un nouveau scan « En transit / Arrivé / En livraison » sans changer de statut.
- **Liste des colis** : recherche (numéro, expéditeur, destinataire), filtre de statut, pagination (20/page) ; plus de limite à 100 colis.
- **Dashboard** : un seul `GROUP BY` ; 5 groupes dont la somme égale le total (en attente, en cours, livrés, incidents).
- Menu de statut : libellés français et uniquement les transitions valides pour le rôle.
- Commentaire vide → texte par défaut appliqué ; messages de validation précis affichés (le formulaire garde les valeurs saisies).
- Page publique : « Départ » et « Dernière position » issus des événements (plus de texte en dur).
- Page d'erreur 403/404/500.

## Technique
- `JpaUtil` : initialisation paresseuse, erreur non mémorisée (retente quand la base revient), fermeture à l'arrêt (`AppListener`).
- Verrou pessimiste sur le changement de statut ; machine à états déplacée dans `ShipmentStatus`.
- Datasource `jta=false` cohérente avec RESOURCE_LOCAL.
- Schéma : `schema.sql` complet, `migration-core.sql` pour base existante, seeds sans schéma dupliqué.
- 23 tests unitaires (mots de passe, limiteur, rôles/agences, transitions, statistiques, numéros de suivi).

## Vérifications effectuées / non effectuées
Effectué : compilation de tout le code `main` et `test` (API Servlet réelle + stubs JPA), exécution des 23 tests, contrôle
statique des JSP (balises équilibrées, aucune valeur utilisateur non échappée), validité XML de `pom.xml`, `web.xml`, `persistence.xml`.
**Non effectué** (pas de Maven, WildFly ni PostgreSQL dans l'environnement de génération) : déploiement, rendu réel des JSP,
exécution des scripts SQL et des requêtes JPA contre PostgreSQL. Faire un premier passage complet (connexion, création,
changement de statut, suivi public) avant toute mise en service.
