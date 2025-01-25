package ch.heig.bdr.projet.suiviDietetique.dao;

import ch.heig.bdr.projet.suiviDietetique.config.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Classe d'accès aux données (DAO) pour la table des personnes.
 * Gère les opérations de suppression des personnes dans la base de données.
 * Cette classe sert de base pour les autres DAO spécialisés (Patient, Infirmier, Dieteticien).
 */
public class PersonneDAO {
    
    /**
     * Supprime une personne de la base de données.
     * Cette opération est définitive et supprime également toutes les données associées
     * à cette personne dans les tables liées (en cascade).
     * 
     * @param noss Le numéro de sécurité sociale de la personne à supprimer
     * @return true si la suppression a réussi, false si la personne n'existe pas ou en cas d'erreur
     */
    public boolean deletePersonne(String noss) {
        try (Connection connection = Database.getConnection()) {
            String query = "DELETE FROM personne WHERE noss = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss)); // Le NSS est transformé en int à partir de l'enum noss);

                // Exécute la suppression et vérifie si au moins une ligne a été affectée
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }
}
