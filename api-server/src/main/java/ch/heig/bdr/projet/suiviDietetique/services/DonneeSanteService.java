package ch.heig.bdr.projet.suiviDietetique.services;

import java.time.OffsetDateTime;

import ch.heig.bdr.projet.suiviDietetique.dao.DonneeSanteDAO;
import ch.heig.bdr.projet.suiviDietetique.models.DonneeSante;

/**
 * Service gérant les opérations liées aux données de santé des patients.
 * Cette classe fait le lien entre les contrôleurs et la couche d'accès aux données (DAO).
 * Elle permet d'insérer et de supprimer des données de santé dans la base de données.
 */
public class DonneeSanteService {
    /** Instance du DAO pour l'accès aux données de santé */
    private final DonneeSanteDAO donneeSanteDAO = new DonneeSanteDAO();

    /**
     * Insère une nouvelle donnée de santé dans la base de données.
     * 
     * @param donneeSante L'objet DonneeSante contenant les informations à insérer
     */
    public void insertDonneeSante(DonneeSante donneeSante) {
        donneeSanteDAO.insertDonneeSante(donneeSante);
    }

    /**
     * Supprime une donnée de santé spécifique de la base de données.
     * 
     * @param noss Le numéro de sécurité sociale du patient
     * @param date La date et l'heure de la donnée de santé à supprimer
     */
    public void deleteDonneeSante(String noss, OffsetDateTime date) {
        donneeSanteDAO.deleteDonneeSante(noss, date);
    }
}
