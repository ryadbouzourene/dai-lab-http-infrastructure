package ch.heig.bdr.projet.suiviDietetique.models;

import java.util.ArrayList;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe représentant un infirmier dans le système.
 * Hérite de la classe Employe et ajoute la gestion des certificats
 * qui définissent les compétences spécifiques de l'infirmier.
 */
public class Infirmier extends Employe {

    /** Liste des certificats détenus par l'infirmier */
    @JsonProperty("certificats")
    private ArrayList<Certificat> certificats = new ArrayList<>();

    /**
     * Constructeur de base pour créer un nouvel infirmier sans certificats.
     * 
     * @param noSS Numéro de sécurité sociale
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param email Adresse email professionnelle
     * @param dateNaissance Date de naissance
     * @param sexe Genre de l'infirmier
     * @param idService Identifiant du service de rattachement
     * @param dateEmabauche Date d'embauche dans l'établissement
     * @param statut Statut professionnel (actif, congé, retraité)
     */
    public Infirmier(String noSS, String nom, String prenom, String email, Date dateNaissance,
                    String sexe, int idService, Date dateEmabauche, String statut) {
        super(noSS, nom, prenom, email, dateNaissance, sexe, idService, dateEmabauche, statut);
    }

    /**
     * Constructeur complet pour créer un nouvel infirmier avec ses certificats.
     * 
     * @param noSS Numéro de sécurité sociale
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param email Adresse email professionnelle
     * @param dateNaissance Date de naissance
     * @param sexe Genre de l'infirmier
     * @param idService Identifiant du service de rattachement
     * @param dateEmabauche Date d'embauche dans l'établissement
     * @param statut Statut professionnel (actif, congé, retraité)
     * @param cert Tableau des noms des certificats détenus par l'infirmier
     */
    public Infirmier(String noSS, String nom, String prenom, String email, Date dateNaissance,
                    String sexe, int idService, Date dateEmabauche, String statut, String[] cert) {
        super(noSS, nom, prenom, email, dateNaissance, sexe, idService, dateEmabauche, statut);
        // Convertir chaque nom de certificat en objet Certificat et l'ajouter à la liste
        for (var c : cert) {
            certificats.add(Certificat.certificat(c));
        }
    }
}
