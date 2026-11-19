package com.track.security;

/**
 * Outil en ligne de commande pour générer un hash à coller dans la table users.
 * Usage : java -cp target/classes com.track.security.PasswordTool "MotDePasse"
 */
public final class PasswordTool {
    private PasswordTool() {}

    public static void main(String[] args) {
        if (args.length != 1 || args[0].length() < 10) {
            System.err.println("Usage : PasswordTool \"<mot de passe de 10 caractères minimum>\"");
            System.exit(1);
        }
        System.out.println(PasswordUtil.hash(args[0].toCharArray()));
    }
}
