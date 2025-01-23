package ch.heig.bdr.projet.suiviDietetique.models;

import java.sql.Date;

/**
 * Classe représentant un diététicien dans le système.
 * Hérite de la classe Employe et représente un professionnel spécialisé
 * dans la nutrition et le conseil diététique.
 */
public class Dieteticien extends Employe {

    /**
     * Constructeur pour créer un nouveau diététicien.
     * 
     * @param noSS Numéro de sécurité sociale du diététicien
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param email Adresse email professionnelle
     * @param dateNaissance Date de naissance
     * @param sexe Genre du diététicien
     * @param idService Identifiant du service auquel le diététicien est rattaché
     * @param dateEmabauche Date d'embauche dans l'établissement
     * @param statut Statut professionnel (ex: actif, congé, etc.)
     */
    public Dieteticien(String noSS, String nom, String prenom, String email, Date dateNaissance,
                      String sexe, int idService, Date dateEmabauche, String statut) {
        super(noSS, nom, prenom, email, dateNaissance, sexe, idService, dateEmabauche, statut);
    }
}
