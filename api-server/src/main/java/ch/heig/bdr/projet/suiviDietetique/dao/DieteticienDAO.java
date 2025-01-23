package ch.heig.bdr.projet.suiviDietetique.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.heig.bdr.projet.suiviDietetique.config.Database;
import ch.heig.bdr.projet.suiviDietetique.models.Dieteticien;
import ch.heig.bdr.projet.suiviDietetique.models.Objectif;

/**
 * Classe d'accès aux données (DAO) pour les diététiciens.
 * Gère les opérations de lecture des diététiciens et de leurs objectifs
 * dans la base de données. Utilise la vue dieteticien_vue pour accéder
 * aux informations complètes des diététiciens.
 */
public class DieteticienDAO {
    
    /**
     * Récupère un diététicien par son numéro de sécurité sociale.
     * 
     * @param noss Le numéro de sécurité sociale du diététicien
     * @return Le diététicien trouvé ou null si aucun diététicien ne correspond
     */
    public Dieteticien getDieteticien(String noss) {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM dieteticien_vue WHERE noss = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Dieteticien(
                                rs.getString("noss"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getDate("datenaissance"),
                                rs.getString("sexe"),
                                rs.getInt("id_service"),
                                rs.getDate("dateEmbauche"),
                                rs.getString("statut")
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère le diététicien qui suit un patient spécifique.
     * 
     * @param noss Le numéro de sécurité sociale du patient
     * @return Le diététicien qui suit le patient ou null si aucun diététicien n'est assigné
     */
    public Dieteticien getDieteticienPatient(String noss) {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM dieteticien_vue " +
                         "WHERE noss = (SELECT noss_dieteticien " +
                         "             FROM patient_vue " +
                         "             WHERE noss = ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Dieteticien(
                                rs.getString("noss"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getDate("datenaissance"),
                                rs.getString("sexe"),
                                rs.getInt("id_service"),
                                rs.getDate("dateEmbauche"),
                                rs.getString("statut")
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère la liste de tous les diététiciens.
     * 
     * @return La liste de tous les diététiciens enregistrés dans le système
     */
    public List<Dieteticien> getAll() {
        List<Dieteticien> dieteticiens = new ArrayList<>();

        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM dieteticien_vue";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dieteticiens.add(new Dieteticien(
                            rs.getString("noss"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getDate("datenaissance"),
                            rs.getString("sexe"),
                            rs.getInt("id_service"),
                            rs.getDate("dateEmbauche"),
                            rs.getString("statut")
                    ));
                }
            } catch (Exception e) {
                System.err.println("Error:" + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
        return dieteticiens;
    }

    /**
     * Récupère tous les objectifs fixés par un diététicien spécifique.
     * 
     * @param nossDieteticien Le numéro de sécurité sociale du diététicien
     * @return La liste des objectifs fixés par ce diététicien ou null en cas d'erreur
     */
    public List<Objectif> getObjectifs(String nossDieteticien) {
        List<Objectif> objectifs = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM objectif WHERE noss_dieteticien = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(nossDieteticien));
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        objectifs.add(new Objectif(
                                rs.getInt("numero"),
                                rs.getString("noss_patient"),
                                nossDieteticien,
                                rs.getDate("datedebut"),
                                rs.getDate("datefin"),
                                rs.getString("titre"),
                                rs.getBoolean("reussi"),
                                rs.getString("commentaire")
                        ));
                    }
                    return objectifs;
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }
}
