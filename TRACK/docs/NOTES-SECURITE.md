# Sécurité — état et tâches restantes

## Fait
- Authentification en base, mots de passe PBKDF2-HMAC-SHA256 (210 000 itérations, sel aléatoire), comparaison en temps constant.
- Session renouvelée à la connexion, expiration 30 min, cookie HttpOnly + SameSite=Lax (+ Secure, voir README §5).
- Comptes désactivés : session révoquée au plus tard 60 s après la désactivation.
- Verrouillage anti-force-brute (5 échecs/15 min par e-mail, 20/15 min par IP) ; temps de réponse identique que le compte existe ou non.
- CSRF sur le formulaire de connexion et sur tous les POST connectés ; déconnexion en POST.
- XSS : toutes les valeurs utilisateur sont échappées (`<c:out>`) ; Content-Security-Policy, X-Frame-Options, nosniff, Referrer-Policy.
- Autorisation par rôle ET par agence appliquée dans la couche service ; un colis hors périmètre est traité comme introuvable.
- Validation serveur (longueurs, téléphone, poids), transitions de statuts contrôlées, verrou pessimiste contre les mises à jour concurrentes.
- Suivi public : pas de téléphone ni d'adresse, événements internes masquables, recherche limitée à 30/min/IP, format du numéro contrôlé.
- Numéros de suivi aléatoires (48 bits), contrainte UNIQUE.
- Hibernate : plus de génération de schéma ni de log SQL par défaut.

## Reste à faire
1. Écrans d'administration des agences et des utilisateurs (aujourd'hui : SQL ; hash via `PasswordTool`).
2. Changement de mot de passe par l'utilisateur et politique de mots de passe.
3. Héberger Bootstrap/polices localement (ou ajouter SRI) et resserrer la CSP.
4. Limiteur partagé si plusieurs instances ; transfert de l'IP cliente derrière un proxy.
5. HTTPS, secrets hors du dépôt (credential store WildFly), sauvegardes, supervision des logs.
6. Tests d'intégration (DAO sur PostgreSQL réel, parcours HTTP).
