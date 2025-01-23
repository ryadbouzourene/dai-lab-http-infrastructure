package ch.heig.bdr.projet.suiviDietetique.security;

import io.javalin.security.RouteRole;

/**
 * Énumération des rôles disponibles dans l'application.
 * Implémente l'interface RouteRole de Javalin pour la gestion des autorisations des routes.
 * Chaque rôle représente un niveau d'accès différent dans l'application.
 */
public enum Role implements RouteRole {
    /** Rôle pour les routes accessibles à tous les utilisateurs, même non authentifiés */
    ANYONE("Anyone"),
    /** Rôle pour les diététiciens qui peuvent gérer les suivis nutritionnels */
    DIETETICIEN("Diététicien"),
    /** Rôle pour les patients qui peuvent consulter leur suivi */
    PATIENT("Patient"),
    /** Rôle pour les infirmiers qui peuvent enregistrer les données de santé */
    INFIRMIER("Infirmier"),
    /** Rôle administrateur avec accès complet au système */
    ADMIN("Admin");

    /** Nom lisible du rôle */
    private final String name;

    /**
     * Constructeur privé pour l'énumération des rôles.
     * 
     * @param name Le nom lisible du rôle
     */
    Role(String name) {
        this.name = name;
    }

    /**
     * Retourne une représentation textuelle du rôle.
     * 
     * @return Le nom lisible du rôle
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Convertit un nom de rôle en sa valeur énumérée correspondante.
     * La comparaison est insensible à la casse.
     * 
     * @param name Le nom du rôle à convertir
     * @return Le rôle correspondant au nom
     * @throws IllegalArgumentException Si aucun rôle ne correspond au nom fourni
     */
    public static Role fromName(String name) {
        for (Role role : Role.values()) {
            if (role.name.equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Aucun rôle correspondant pour le nom : " + name);
    }
}