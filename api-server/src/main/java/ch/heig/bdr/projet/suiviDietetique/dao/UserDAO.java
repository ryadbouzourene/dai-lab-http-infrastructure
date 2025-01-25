package ch.heig.bdr.projet.suiviDietetique.dao;

import ch.heig.bdr.projet.suiviDietetique.config.Database;
import ch.heig.bdr.projet.suiviDietetique.models.User;
import ch.heig.bdr.projet.suiviDietetique.security.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Classe d'accès aux données (DAO) pour la table Utilisateur.
 * Gère toutes les opérations CRUD (Create, Read, Update, Delete) sur les utilisateurs
 * dans la base de données.
 */
public class UserDAO {

    // TODO : A voir pour lier à une personne
    /**
     * Insère un nouvel utilisateur dans la base de données.
     * 
     * @param email L'adresse email de l'utilisateur
     * @param hashedPassword Le mot de passe haché
     * @param role Le rôle de l'utilisateur
     */
    public void insert(String email, String hashedPassword, Role role) {
        try (Connection connection = Database.getConnection()) {
            // Préparation de la requête SQL avec cast du rôle en type personnalisé
            String query = "INSERT INTO Utilisateur (email, mdpHache, role, " +
                    "dateCreation) VALUES (?, ?, ?::suivi_dietetique.role_utilisateur, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                // Configuration des paramètres de la requête
                stmt.setString(1, email);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, role.toString());
                stmt.setTimestamp(4, Timestamp.from(Instant.now()));
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recherche un utilisateur par son adresse email.
     * 
     * @param email L'adresse email à rechercher
     * @return L'utilisateur trouvé ou null si aucun utilisateur ne correspond
     */
    public User findByEmail(String email) {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM Utilisateur WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Création d'un objet User à partir des données de la base
                        return new User(
                                rs.getInt("noSS"),
                                rs.getString("email"),
                                rs.getString("mdpHache"),
                                Role.fromName(rs.getString("role")),
                                rs.getDate("dateCreation")
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recherche un utilisateur par son numéro de sécurité sociale.
     * 
     * @param noss Le numéro de sécurité sociale à rechercher
     * @return L'utilisateur trouvé ou null si aucun utilisateur ne correspond
     */
    public User findByNoss(String noss) {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM Utilisateur WHERE noss = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Création d'un objet User à partir des données de la base
                        return new User(
                                rs.getInt("noSS"),
                                rs.getString("email"),
                                rs.getString("mdpHache"),
                                Role.fromName(rs.getString("role")),
                                rs.getDate("dateCreation")
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Supprime un utilisateur de la base de données.
     * 
     * @param email L'adresse email de l'utilisateur à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean deleteUser(String email) {
        try (Connection connection = Database.getConnection()) {
            String query = "DELETE FROM utilisateur WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);

                // Exécution de la suppression et vérification du nombre de lignes affectées
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }
}
