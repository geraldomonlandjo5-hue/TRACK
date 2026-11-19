# Sécurité — tâches avant mise en production

1. Remplacer le login de démonstration codé en dur par une table `users`.
2. Stocker uniquement des mots de passe hachés (Argon2id ou bcrypt).
3. Ajouter une protection CSRF aux formulaires.
4. Ajouter une validation serveur complète et des contrôles de transitions de statuts.
5. Protéger toutes les routes privées, y compris les chemins imbriqués.
6. Ne pas exposer les téléphones et adresses privées sur la page publique de tracking.
7. Ajouter une limitation de tentatives sur le formulaire de suivi public.
8. Remplacer le générateur séquentiel en mémoire par une séquence PostgreSQL ou une génération aléatoire contrôlée avec contrainte UNIQUE.
9. Utiliser HTTPS, cookies Secure/HttpOnly/SameSite et secrets hors du dépôt.
10. Prévoir sauvegardes PostgreSQL, logs et tests automatisés.
