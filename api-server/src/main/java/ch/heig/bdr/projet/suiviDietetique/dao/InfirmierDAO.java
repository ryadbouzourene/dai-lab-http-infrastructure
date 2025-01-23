package ch.heig.bdr.projet.suiviDietetique.dao;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.heig.bdr.projet.suiviDietetique.config.Database;
import ch.heig.bdr.projet.suiviDietetique.models.Infirmier;
import ch.heig.bdr.projet.suiviDietetique.models.Repas;
import ch.heig.bdr.projet.suiviDietetique.models.TypeRepas;

/**
 * Classe d'accès aux données (DAO) pour les infirmiers.
 * Gère les opérations de lecture des infirmiers et de leurs repas servis
 * dans la base de données. Utilise la vue infirmier_vue pour accéder
 * aux informations complètes des infirmiers, y compris leurs certificats.
 */
public class InfirmierDAO {

    /**
     * Récupère un infirmier par son numéro de sécurité sociale.
     * 
     * @param noss Le numéro de sécurité sociale de l'infirmier
     * @return L'infirmier trouvé ou null si aucun infirmier ne correspond
     */
    public Infirmier getInfirmier(String noss) {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM infirmier_vue WHERE noss = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Récupération et conversion du tableau de certificats SQL en tableau Java
                        Array sqlArray = rs.getArray("certificats");
                        String[] certificats = (sqlArray != null) ? (String[]) sqlArray.getArray() : null;
                        
                        return new Infirmier(
                                rs.getString("noss"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getDate("datenaissance"),
                                rs.getString("sexe"),
                                rs.getInt("id_service"),
                                rs.getDate("dateEmbauche"),
                                rs.getString("statut"),
                                certificats
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
     * Récupère la liste de tous les infirmiers.
     * 
     * @return La liste de tous les infirmiers enregistrés dans le système
     */
    public List<Infirmier> getAll() {
        List<Infirmier> infirmiers = new ArrayList<>();

        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM infirmier_vue";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    infirmiers.add(new Infirmier(
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
        return infirmiers;
    }

    /**
     * Récupère tous les repas servis par un infirmier spécifique.
     * 
     * @param nossInfirmier Le numéro de sécurité sociale de l'infirmier
     * @return La liste des repas servis par cet infirmier
     */
    public List<Repas> getRepas(String nossInfirmier) {
        List<Repas> repas = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM repas WHERE noss_infirmier = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(nossInfirmier));

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Récupération de la date de consommation avec le fuseau horaire
                        OffsetDateTime dateConsommation = rs.getObject("date_consommation", OffsetDateTime.class);

                        // Conversion de l'ENUM PostgreSQL vers un ENUM Java
                        String typeRepasString = rs.getString("type");
                        TypeRepas typeRepas = TypeRepas.type(typeRepasString);
                        String nossPatient = Integer.toString(rs.getInt("noss_patient"));

                        // Création et ajout du repas dans la liste
                        repas.add(new Repas(
                                nossPatient,
                                nossInfirmier,
                                dateConsommation,
                                rs.getString("remarque"),
                                typeRepas
                        ));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repas;
    }
}
