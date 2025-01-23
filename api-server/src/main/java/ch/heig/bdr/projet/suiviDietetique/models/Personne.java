package ch.heig.bdr.projet.suiviDietetique.models;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Classe représentant une personne dans le système de suivi diététique.
 * Cette classe sert de base pour les différents types d'utilisateurs du système
 * (patients, diététiciens, infirmiers).
 */
public class Personne {
    /** Numéro de sécurité sociale, identifiant unique de la personne */
    private String noSS;
    
    /** Nom de famille de la personne */
    private String nom;
    
    /** Prénom de la personne */
    private String prenom;
    
    /** Adresse email de contact */
    private String email;
    
    /** 
     * Date de naissance de la personne.
     * Format attendu : yyyy-MM-dd
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateNaissance;
    
    /** Sexe de la personne (utilise l'énumération Sexe) */
    private Sexe sexe;

    /**
     * Constructeur par défaut nécessaire pour la désérialisation JSON.
     */
    public Personne(){}

    /**
     * Constructeur complet pour créer une nouvelle personne.
     * 
     * @param noSS Numéro de sécurité sociale
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param email Adresse email
     * @param dateNaissance Date de naissance
     * @param sexe Sexe de la personne (sera converti en énumération Sexe)
     */
    public Personne(String noSS, String nom, String prenom, String email, Date dateNaissance,
                    String sexe) {
        this.noSS = noSS;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = Sexe.type(sexe);
        this.email = email;
    }

    /**
     * @return Le numéro de sécurité sociale
     */
    public String getNoSS() {
        return noSS;
    }

    /**
     * @return Le nom de famille
     */
    public String getNom() {
        return nom;
    }

    /**
     * @return Le prénom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @return La date de naissance
     */
    public Date getDateNaissance() {
        return dateNaissance;
    }

    /**
     * @return Le sexe sous forme de chaîne de caractères
     */
    public String getSexe() {
        return sexe.name;
    }

    /**
     * @return L'adresse email
     */
    public String getEmail(){
        return email;
    }

}
