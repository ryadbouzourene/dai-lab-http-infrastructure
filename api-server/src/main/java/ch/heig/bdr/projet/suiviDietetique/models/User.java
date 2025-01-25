package ch.heig.bdr.projet.suiviDietetique.models;

import ch.heig.bdr.projet.suiviDietetique.security.Role;

import java.util.Date;

/**
 * Classe représentant un utilisateur du système.
 * Cette classe gère les informations d'authentification et d'autorisation
 * des utilisateurs, qu'ils soient patients, diététiciens ou infirmiers.
 */
public class User {
    /** Numéro de sécurité sociale servant d'identifiant unique */
    private int noss;

    /** Nom d'utilisateur pour la connexion */
    private String username;

    /** Mot de passe hashé pour la sécurité */
    private String hashedPassword;

    /** Rôle définissant les permissions de l'utilisateur */
    private Role role;

    /** Date de création du compte utilisateur */
    private Date creationDate;

    /**
     * Constructeur pour créer un nouvel utilisateur.
     * 
     * @param noss Numéro de sécurité sociale
     * @param username Nom d'utilisateur choisi
     * @param hashedPassword Mot de passe déjà hashé
     * @param role Rôle attribué (PATIENT, DIETETICIEN, INFIRMIER)
     * @param creationDate Date de création du compte
     */
    public User(int noss, String username, String hashedPassword,
               Role role, Date creationDate) {
        this.noss = noss;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.creationDate = creationDate;
    }

    /**
     * @return Le numéro de sécurité sociale de l'utilisateur
     */
    public int getNoss() {
        return noss;
    }

    /**
     * @return Le nom d'utilisateur
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Le mot de passe hashé
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * @return Le rôle de l'utilisateur
     */
    public Role getRole() {
        return role;
    }

    /**
     * @return La date de création du compte
     */
    public Date getCreationDate() {
        return creationDate;
    }
}
