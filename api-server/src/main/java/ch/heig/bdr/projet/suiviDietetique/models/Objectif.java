package ch.heig.bdr.projet.suiviDietetique.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Date;

/**
 * Classe représentant un objectif diététique fixé pour un patient.
 * Un objectif est défini par un diététicien pour un patient spécifique,
 * avec une période de réalisation et un suivi de sa réussite.
 */
public class Objectif {
    /** Numéro unique identifiant l'objectif */
    @JsonProperty("numero")
    private int numero;

    /** Numéro de sécurité sociale du patient concerné */
    @JsonProperty("noss_patient")
    private String nossPatient;

    /** Numéro de sécurité sociale du diététicien ayant fixé l'objectif */
    @JsonProperty("noss_dieteticien")
    private String nossDieteticien;

    /** Date de début de l'objectif */
    @JsonProperty("date_debut")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateDebut;

    /** Date de fin prévue de l'objectif */
    @JsonProperty("date_fin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateFin;

    /** Titre ou description courte de l'objectif */
    @JsonProperty("titre")
    private String titre;

    /** Indique si l'objectif a été atteint */
    @JsonProperty("reussi")
    private boolean reussi;

    /** Commentaires additionnels sur l'objectif et son suivi */
    @JsonProperty("commentaire")
    private String commentaires;

    /**
     * Constructeur par défaut nécessaire pour la désérialisation JSON.
     */
    public Objectif() {}

    /**
     * Constructeur complet pour créer un nouvel objectif.
     * 
     * @param numero Numéro unique de l'objectif
     * @param nossPatient Numéro de sécurité sociale du patient
     * @param nossDieteticien Numéro de sécurité sociale du diététicien
     * @param dateDebut Date de début de l'objectif
     * @param dateFin Date de fin prévue
     * @param titre Description courte de l'objectif
     * @param reussi État de réussite de l'objectif
     * @param commentaires Notes additionnelles sur l'objectif
     */
    public Objectif(int numero, String nossPatient, String nossDieteticien, Date dateDebut,
                   Date dateFin, String titre, boolean reussi, String commentaires) {
        this.numero = numero;
        this.nossPatient = nossPatient;
        this.nossDieteticien = nossDieteticien;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.titre = titre;
        this.reussi = reussi;
        this.commentaires = commentaires;
    }

    /**
     * @return Le numéro unique de l'objectif
     */
    public int getNumero() {
        return numero;
    }

    /**
     * @param numero Le nouveau numéro de l'objectif
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * @return Le numéro de sécurité sociale du patient
     */
    public String getNossPatient() {
        return nossPatient;
    }

    /**
     * @param nossPatient Le nouveau numéro de sécurité sociale du patient
     */
    public void setNossPatient(String nossPatient) {
        this.nossPatient = nossPatient;
    }

    /**
     * @return Le numéro de sécurité sociale du diététicien
     */
    public String getNossDieteticien() {
        return nossDieteticien;
    }

    /**
     * @param nossDieteticien Le nouveau numéro de sécurité sociale du diététicien
     */
    public void setNossDieteticien(String nossDieteticien) {
        this.nossDieteticien = nossDieteticien;
    }

    /**
     * @return La date de début de l'objectif
     */
    public Date getDateDebut() {
        return dateDebut;
    }

    /**
     * @param dateDebut La nouvelle date de début
     */
    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    /**
     * @return La date de fin prévue de l'objectif
     */
    public Date getDateFin() {
        return dateFin;
    }

    /**
     * @param dateFin La nouvelle date de fin
     */
    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    /**
     * @return Le titre de l'objectif
     */
    public String getTitre() {
        return titre;
    }

    /**
     * @param titre Le nouveau titre
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * @return true si l'objectif est atteint, false sinon
     */
    public boolean isReussi() {
        return reussi;
    }

    /**
     * @param reussi Le nouvel état de réussite
     */
    public void setReussi(boolean reussi) {
        this.reussi = reussi;
    }

    /**
     * @return Les commentaires sur l'objectif
     */
    public String getCommentaires() {
        return commentaires;
    }

    /**
     * @param commentaires Les nouveaux commentaires
     */
    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    /**
     * Crée une représentation textuelle de l'objectif pour le débogage.
     * 
     * @return Une chaîne contenant toutes les informations de l'objectif
     */
    @Override
    public String toString() {
        return "Objectif{" +
                "numero=" + numero +
                ", nossPatient='" + nossPatient + '\'' +
                ", nossDieteticien='" + (nossDieteticien != null ? nossDieteticien : "null") + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", titre='" + titre + '\'' +
                ", reussi=" + reussi +
                ", commentaires='" + (commentaires != null ? commentaires : "null") + '\'' +
                '}';
    }
}
