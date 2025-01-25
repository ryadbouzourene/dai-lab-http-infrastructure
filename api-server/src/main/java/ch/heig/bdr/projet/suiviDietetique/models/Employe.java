package ch.heig.bdr.projet.suiviDietetique.models;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe abstraite représentant un employé de l'établissement.
 * Hérite de la classe Personne et ajoute les informations spécifiques
 * aux employés comme le service, la date d'embauche et le statut.
 */
public abstract class Employe extends Personne {
    /** Identifiant du service auquel l'employé est rattaché */
    @JsonProperty("idService")
    private int idService;

    /** Date d'embauche de l'employé */
    @JsonProperty("dateEmbauche")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateEmbauche;

    /** Statut actuel de l'employé (actif, congé, retraité) */
    @JsonProperty("statut")
    private StatutEmploye statut;

    /**
     * Constructeur pour créer un nouvel employé.
     * 
     * @param noSS Numéro de sécurité sociale
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param email Adresse email professionnelle
     * @param dateNaissance Date de naissance
     * @param sexe Genre de l'employé
     * @param idService Identifiant du service de rattachement
     * @param dateEmabauche Date d'embauche dans l'établissement
     * @param statut Statut professionnel (actif, congé, retraité)
     */
    public Employe(String noSS, String nom, String prenom, String email, Date dateNaissance,
                  String sexe, int idService, Date dateEmabauche, String statut) {
        super(noSS, nom, prenom, email, dateNaissance, sexe);
        this.idService = idService;
        this.dateEmbauche = dateEmabauche;
        this.statut = StatutEmploye.statut(statut);
    }
}

/**
 * Énumération représentant les différents statuts possibles d'un employé.
 * Un employé peut être :
 * - ACTIF : En activité normale
 * - CONGE : En congé (maladie, maternité, etc.)
 * - RETRAITE : A la retraite
 */
enum StatutEmploye {
    /** Employé en activité */
    ACTIF("actif"), 
    /** Employé en congé */
    CONGE("conge"), 
    /** Employé retraité */
    RETRAITE("retraite");

    /** Nom du statut tel qu'utilisé dans la base de données */
    public String name;

    /**
     * Constructeur privé pour initialiser un statut avec son nom.
     * 
     * @param s Le nom du statut
     */
    private StatutEmploye(String s) {
        this.name = s;
    }

    /**
     * Recherche un statut par son nom.
     * La recherche est insensible à la casse.
     * 
     * @param s Le nom du statut à rechercher
     * @return Le statut correspondant au nom
     * @throws IllegalArgumentException Si aucun statut ne correspond au nom donné
     */
    public static StatutEmploye statut(String s) {
        for (StatutEmploye statut : StatutEmploye.values()) {
            if (statut.name.equalsIgnoreCase(s)) {
                return statut;
            }
        }
        throw new IllegalArgumentException("Aucun rôle correspondant pour le nom : " + s);
    }
}