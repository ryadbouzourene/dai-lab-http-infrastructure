package ch.heig.bdr.projet.suiviDietetique.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.util.Locale;

import static java.lang.Math.pow;

/**
 * Classe représentant les données de santé d'un patient à un moment donné.
 * Cette classe stocke les mesures anthropométriques et le niveau d'activité physique,
 * et calcule automatiquement l'Indice de Masse Corporelle (IMC).
 */
public class DonneeSante {
    /** Numéro de sécurité sociale du patient */
    @JsonProperty("noss_patient")
    private String nossPatient;

    /** Date et heure de la prise des mesures, avec le fuseau horaire */
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime date;

    /** Taille du patient en centimètres */
    @JsonProperty("taille")
    private int taille;

    /** Poids du patient en kilogrammes */
    @JsonProperty("poids")
    private int poids;

    /** Tour de taille du patient en centimètres */
    @JsonProperty("tour_de_taille")
    private int tourDeTaille;

    /** Niveau d'activité physique du patient */
    @JsonProperty("niveau_activite_physique")
    private NiveauActivitePhysique niveauActivitePhysique;

    /** 
     * Indice de Masse Corporelle (IMC) calculé.
     * Formule : poids (kg) / (taille (m))²
     */
    @JsonProperty("IMC")
    private Double IMC;

    /**
     * Constructeur par défaut nécessaire pour la désérialisation JSON avec Jackson.
     */
    public DonneeSante() {}

    /**
     * Constructeur complet pour créer une nouvelle donnée de santé.
     * Calcule automatiquement l'IMC à partir du poids et de la taille.
     * 
     * @param nossPatient Numéro de sécurité sociale du patient
     * @param date Date et heure de la prise des mesures
     * @param taille Taille en centimètres
     * @param poids Poids en kilogrammes
     * @param tourDeTaille Tour de taille en centimètres
     * @param niveauActivitePhysique Niveau d'activité physique (sera converti en énumération)
     * @throws RuntimeException Si une erreur survient lors du calcul de l'IMC
     */
    public DonneeSante(String nossPatient, OffsetDateTime date, int taille, int poids, int tourDeTaille, String niveauActivitePhysique) {
        this.nossPatient = nossPatient;
        this.date = date;
        this.taille = taille;
        this.poids = poids;
        this.tourDeTaille = tourDeTaille;
        this.niveauActivitePhysique = NiveauActivitePhysique.type(niveauActivitePhysique);

        // Calcul de l'IMC avec conversion de la taille en mètres
        double imcValue = (double) poids / pow((((double) taille) / 100.0), 2);
        try {
            // Formatage de l'IMC avec deux décimales selon la locale française
            NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
            this.IMC = nf.parse(String.format(Locale.FRANCE, "%.2f", imcValue)).doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("Erreur de conversion de l'IMC", e);
        }
    }

    /**
     * @return Le numéro de sécurité sociale du patient
     */
    public String getNossPatient() {
        return nossPatient;
    }

    /**
     * @return La date et l'heure de la prise des mesures
     */
    public OffsetDateTime getDate() {
        return date;
    }

    /**
     * @return La taille en centimètres
     */
    public int getTaille() {
        return taille;
    }

    /**
     * @return Le poids en kilogrammes
     */
    public int getPoids() {
        return poids;
    }

    /**
     * @return Le tour de taille en centimètres
     */
    public int getTourDeTaille() {
        return tourDeTaille;
    }

    /**
     * @return Le niveau d'activité physique sous forme de chaîne de caractères
     */
    public String getNiveauActivitePhysique() {
        return niveauActivitePhysique.name;
    }

    /**
     * Crée une représentation textuelle de l'objet pour le débogage.
     * 
     * @return Une chaîne contenant toutes les informations de santé
     */
    @Override
    public String toString() {
        return "DonneesSante{" +
                "nossPatient='" + nossPatient + '\'' +
                ", date=" + date +
                ", taille=" + taille +
                ", poids=" + poids +
                ", tourDeTaille=" + tourDeTaille +
                ", niveauActivitePhysique='" + niveauActivitePhysique + '\'' +
                ", IMC=" + IMC +
                '}';
    }
}
