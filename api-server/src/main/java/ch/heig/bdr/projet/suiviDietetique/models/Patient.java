package ch.heig.bdr.projet.suiviDietetique.models;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Classe représentant un patient dans le système.
 * Hérite de la classe Personne et ajoute les informations spécifiques
 * aux patients comme leur diététicien référent et leur date d'admission.
 */
public class Patient extends Personne {
    /** Numéro de sécurité sociale du diététicien référent */
    private String nossDieteticien;

    /** Date d'admission du patient dans l'établissement */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateAdmission;

    /**
     * Constructeur par défaut nécessaire pour la désérialisation JSON.
     */
    public Patient() {}
    
    /**
     * Constructeur complet pour créer un nouveau patient avec sa date d'admission.
     * 
     * @param noSS Numéro de sécurité sociale du patient
     * @param nossDieteticien Numéro de sécurité sociale du diététicien référent
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param email Adresse email
     * @param dateNaissance Date de naissance
     * @param sexe Genre du patient
     * @param dateAdmission Date d'admission dans l'établissement
     */
    public Patient(String noSS, String nossDieteticien, String nom, String prenom, String email, Date dateNaissance,
                  String sexe, Date dateAdmission) {
        super(noSS, nom, prenom, email, dateNaissance, sexe);
        this.dateAdmission = dateAdmission;
        this.nossDieteticien = nossDieteticien;
    }

    /**
     * Constructeur pour créer un nouveau patient sans date d'admission.
     * Utile pour les patients non encore admis ou en consultation externe.
     * 
     * @param noSS Numéro de sécurité sociale du patient
     * @param nossDieteticien Numéro de sécurité sociale du diététicien référent
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param email Adresse email
     * @param dateNaissance Date de naissance
     * @param sexe Genre du patient
     */
    public Patient(String noSS, String nossDieteticien, String nom, String prenom, String email, Date dateNaissance,
                  String sexe) {
        super(noSS, nom, prenom, email, dateNaissance, sexe);
        this.nossDieteticien = nossDieteticien;
    }

    /**
     * @return La date d'admission du patient, ou null si non admis
     */
    public Date getDateAdmission() {
        return dateAdmission;
    }

    /**
     * @return Le numéro de sécurité sociale du diététicien référent
     */
    public String getNossDieteticien() {
        return nossDieteticien;
    }
}
