package ch.heig.bdr.projet.suiviDietetique.models;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe représentant un repas consommé par un patient.
 * Un repas est caractérisé par son type, sa date de consommation,
 * les aliments consommés et leurs quantités, ainsi que les valeurs
 * nutritionnelles totales.
 */
public class Repas {
    /** Numéro de sécurité sociale du patient ayant consommé le repas */
    public String nossPatient;

    /** Numéro de sécurité sociale de l'infirmier ayant supervisé le repas */
    public String nossInfirmier;

    /** Date et heure de consommation du repas, avec le fuseau horaire */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public OffsetDateTime dateConsommation;

    /** Commentaires sur le repas (appétit, difficultés, etc.) */
    public String comment;

    /** Type de repas (petit déjeuner, déjeuner, dîner, etc.) */
    public TypeRepas type;

    /** Liste des aliments consommés avec leurs quantités */
    @JsonProperty("consommables")
    public List<ConsommableQuantity> consommableQuantities;

    /** Total des protéines en grammes */
    public Double totalProteines;

    /** Total des glucides en grammes */
    public Double totalGlucides;

    /** Total des lipides en grammes */
    public Double totalLipides;

    /** Total des calories */
    public Double totalCalories;

    /** Total de l'hydratation en millilitres */
    public int totalHydratation;

    /**
     * Constructeur par défaut nécessaire pour la désérialisation JSON.
     */
    public Repas() {
    }

    /**
     * Constructeur minimal avec seulement le patient et la date.
     * 
     * @param nossPatient Numéro de sécurité sociale du patient
     * @param dateConsommation Date et heure de consommation
     */
    public Repas(String nossPatient, OffsetDateTime dateConsommation) {
        this.nossPatient = nossPatient;
        this.dateConsommation = dateConsommation;
    }

    /**
     * Constructeur avec les informations de base et la liste des consommables.
     * 
     * @param nossPatient Numéro de sécurité sociale du patient
     * @param dateConsommation Date et heure de consommation
     * @param comment Commentaires sur le repas
     * @param type Type de repas
     * @param consommableQuantity Liste des aliments consommés
     */
    public Repas(String nossPatient, OffsetDateTime dateConsommation, String comment, TypeRepas type,
                 List<ConsommableQuantity> consommableQuantity) {
        this.nossPatient = nossPatient;
        this.dateConsommation = dateConsommation;
        this.comment = comment;
        this.type = type;
        this.consommableQuantities = consommableQuantity;
    }

    /**
     * Constructeur complet avec les totaux nutritionnels.
     * 
     * @param nossPatient Numéro de sécurité sociale du patient
     * @param nossInfirmier Numéro de sécurité sociale de l'infirmier
     * @param dateConsommation Date et heure de consommation
     * @param comment Commentaires sur le repas
     * @param type Type de repas
     * @param totalProteines Total des protéines
     * @param totalGlucides Total des glucides
     * @param totalLipides Total des lipides
     * @param totalCalories Total des calories
     * @param totalHydratation Total de l'hydratation
     */
    public Repas(String nossPatient, String nossInfirmier, OffsetDateTime dateConsommation, String comment, TypeRepas type,
                 Double totalProteines, Double totalGlucides, Double totalLipides, Double totalCalories,
                 int totalHydratation) {
        this.nossPatient = nossPatient;
        this.nossInfirmier = nossInfirmier;
        this.dateConsommation = dateConsommation;
        this.comment = comment;
        this.type = type;
        this.totalProteines = totalProteines;
        this.totalGlucides = totalGlucides;
        this.totalLipides = totalLipides;
        this.totalCalories = totalCalories;
        this.totalHydratation = totalHydratation;
    }

    /**
     * Constructeur sans liste de consommables.
     * 
     * @param nossPatient Numéro de sécurité sociale du patient
     * @param dateConsommation Date et heure de consommation
     * @param comment Commentaires sur le repas
     * @param type Type de repas
     */
    public Repas(String nossPatient, OffsetDateTime dateConsommation, String comment, TypeRepas type) {
        this.nossPatient = nossPatient;
        this.dateConsommation = dateConsommation;
        this.comment = comment;
        this.type = type;
    }

    /**
     * Constructeur sans liste de consommables mais avec infirmier.
     * 
     * @param nossPatient Numéro de sécurité sociale du patient
     * @param nossInfirmier Numéro de sécurité sociale de l'infirmier
     * @param dateConsommation Date et heure de consommation
     * @param comment Commentaires sur le repas
     * @param type Type de repas
     */
    public Repas(String nossPatient, String nossInfirmier, OffsetDateTime dateConsommation, String comment, TypeRepas type) {
        this.nossPatient = nossPatient;
        this.dateConsommation = dateConsommation;
        this.comment = comment;
        this.type = type;
        this.nossInfirmier = nossInfirmier;
    }

    /**
     * Constructeur sans date de consommation ni infirmier.
     * 
     * @param nossPatient Numéro de sécurité sociale du patient
     * @param comment Commentaires sur le repas
     * @param type Type de repas
     * @param consommables Liste des aliments consommés
     */
    public Repas(String nossPatient, String comment, TypeRepas type, List<ConsommableQuantity> consommables) {
        this.nossPatient = nossPatient;
        this.comment = comment;
        this.type = type;
        this.consommableQuantities = consommables;
        this.nossInfirmier = null;
    }

    /**
     * Constructeur sans date de consommation mais avec infirmier.
     * 
     * @param nossPatient Numéro de sécurité sociale du patient
     * @param nossInfirmier Numéro de sécurité sociale de l'infirmier
     * @param comment Commentaires sur le repas
     * @param type Type de repas
     * @param consommables Liste des aliments consommés
     */
    public Repas(String nossPatient, String nossInfirmier, String comment, TypeRepas type, List<ConsommableQuantity> consommables) {
        this.nossPatient = nossPatient;
        this.nossInfirmier = nossInfirmier;
        this.comment = comment;
        this.type = type;
        this.consommableQuantities = consommables;
    }
}
