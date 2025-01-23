package ch.heig.bdr.projet.suiviDietetique.dao;
import java.sql.PreparedStatement;
import java.time.OffsetDateTime;
import java.sql.Connection;

import ch.heig.bdr.projet.suiviDietetique.config.Database;
import ch.heig.bdr.projet.suiviDietetique.models.DonneeSante;

/**
 * Classe d'accès aux données pour les données de santé des patients.
 * Gère les opérations CRUD (Create, Read, Update, Delete) sur la table donnees_sante
 * de la base de données.
 */
public class DonneeSanteDAO {
    
    /**
     * Insère une nouvelle donnée de santé dans la base de données.
     * Les valeurs sont automatiquement converties dans les types appropriés
     * pour la base de données, y compris la conversion du niveau d'activité
     * en type énuméré PostgreSQL.
     * 
     * @param donneeSante L'objet contenant les données de santé à insérer
     * @throws RuntimeException Si une erreur survient lors de l'insertion
     */
    public void insertDonneeSante(DonneeSante donneeSante) {
        try (Connection connection = Database.getConnection()) {
            String insertQuery = "INSERT INTO suivi_dietetique.donnees_sante " +
                               "(noSS_patient, taille, poids, tourDeTaille, niveauActivitePhysique) " +
                               "VALUES (?, ?, ?, ?, ?::suivi_dietetique.niveau_activite)";
            
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                // Configuration des paramètres de la requête
                stmt.setInt(1, Integer.parseInt(donneeSante.getNossPatient()));
                stmt.setInt(2, donneeSante.getTaille());
                stmt.setInt(3, donneeSante.getPoids());
                stmt.setInt(4, donneeSante.getTourDeTaille());
                stmt.setString(5, donneeSante.getNiveauActivitePhysique());

                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'insertion des données de santé : " + e.getMessage());
        }
    }

    /**
     * Supprime une donnée de santé spécifique de la base de données.
     * La suppression se fait sur la base du numéro de sécurité sociale du patient
     * et de la date exacte (tronquée à la seconde) de la mesure.
     * 
     * @param nossPatient Le numéro de sécurité sociale du patient
     * @param date La date et l'heure exacte de la donnée à supprimer
     * @throws RuntimeException Si aucune donnée n'est trouvée ou si une erreur survient
     */
    public void deleteDonneeSante(String nossPatient, OffsetDateTime date) {
        try (Connection connection = Database.getConnection()) {
            // La fonction date_trunc permet de comparer les dates sans les millisecondes
            String query = "DELETE FROM suivi_dietetique.donnees_sante WHERE noss_patient = ? AND date_trunc('second', date) = date_trunc('second', ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(nossPatient));
                stmt.setObject(2, date);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RuntimeException("Aucune donnée de santé trouvée pour le patient " + nossPatient + " à la date " + date);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de la donnée de santé : " + e.getMessage());
        }
    }
}
