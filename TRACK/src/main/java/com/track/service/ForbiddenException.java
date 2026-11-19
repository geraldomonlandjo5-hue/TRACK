package com.track.service;

/** Action refusée pour l'utilisateur connecté (rôle ou agence). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}
