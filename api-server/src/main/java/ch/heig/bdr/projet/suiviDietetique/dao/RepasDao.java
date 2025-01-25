package ch.heig.bdr.projet.suiviDietetique.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import ch.heig.bdr.projet.suiviDietetique.config.Database;
import ch.heig.bdr.projet.suiviDietetique.models.ConsommableQuantity;
import ch.heig.bdr.projet.suiviDietetique.models.Repas;
import ch.heig.bdr.projet.suiviDietetique.models.TypeRepas;

/**
 * Classe d'accès aux données pour les repas.
 * Gère toutes les opérations CRUD (Create, Read, Update, Delete) sur les repas
 * et leurs consommables associés dans la base de données.
 */
public class RepasDao {

    /**
     * Insère un nouveau repas avec ses consommables associés dans la base de données.
     * Cette opération est transactionnelle : soit tout est inséré, soit rien ne l'est.
     * Vérifie également les allergies du patient avant l'insertion.
     * 
     * @param repas Le repas à insérer avec ses consommables
     * @throws RuntimeException Si une erreur survient lors de l'insertion ou si une allergie est détectée
     */
    public void insertRepas(Repas repas) {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false); // Commencer une transaction
    
            String insertRepasQuery = "INSERT INTO suivi_dietetique.repas " +
                                    "(noSS_patient, noSS_infirmier, type, remarque) " +
                                    "VALUES (?, ?, ?::suivi_dietetique.type_repas, ?) RETURNING date_consommation";
    
            String insertConsommableQuery = "INSERT INTO suivi_dietetique.quantite_repas_consommable " +
                                          "(id_consommable, noSS_patient, date_consommation, quantite) " +
                                          "VALUES (?, ?, ?, ?)";
    
            try (PreparedStatement repasStmt = connection.prepareStatement(insertRepasQuery)) {
                // Ajouter les paramètres pour le repas
                repasStmt.setInt(1, Integer.parseInt(repas.nossPatient));
    
                if (repas.nossInfirmier == null || repas.nossInfirmier.isEmpty()) {
                    repasStmt.setNull(2, java.sql.Types.INTEGER);
                } else {
                    repasStmt.setInt(2, Integer.parseInt(repas.nossInfirmier));
                }
    
                repasStmt.setString(3, repas.type.name);
                repasStmt.setString(4, repas.comment);
    
                try (ResultSet rs = repasStmt.executeQuery()) {
                    if (rs.next()) {
                        OffsetDateTime dateConsommation = rs.getObject("date_consommation", OffsetDateTime.class);
    
                        // Insérer les consommables associés
                        try (PreparedStatement consommableStmt = connection.prepareStatement(insertConsommableQuery)) {
                            for (var conso : repas.consommableQuantities) {
                                consommableStmt.setInt(1, conso.getId());
                                consommableStmt.setInt(2, Integer.parseInt(repas.nossPatient));
                                consommableStmt.setObject(3, dateConsommation);
                                consommableStmt.setInt(4, conso.getQuantite());
                                consommableStmt.addBatch();
                            }
                            consommableStmt.executeBatch(); // Exécuter toutes les requêtes du batch
                        }
                    }
                }
    
                connection.commit(); // Valider la transaction
            } catch (Exception e) {
                connection.rollback(); // Annuler toute la transaction en cas d'erreur
                if (e.getMessage().contains("allergique")) {
                    throw new RuntimeException("Erreur d'allergie détectée : " + e.getMessage());
                }
                throw e; // Rejeter d'autres erreurs
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'insertion du repas : " + e.getMessage());
        }
    }

    /**
     * Récupère la liste des repas d'un patient sur un intervalle de temps spécifique.
     * Les repas sont triés par date de consommation décroissante et incluent
     * les statistiques nutritionnelles calculées.
     * 
     * @param noSS_patient Le numéro de sécurité sociale du patient
     * @param interval L'intervalle de temps (format accepté par PostgreSQL)
     * @return La liste des repas avec leurs statistiques nutritionnelles
     */
    public List<Repas> getRepasPatient_interval(String noSS_patient, String interval) {
        List<Repas> repas = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * " +
                         "FROM suivi_dietetique.getRepasWithStats_Interval(?, ?)" +
                         "ORDER BY date_consommation DESC";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noSS_patient));
                stmt.setString(2, interval);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Conversion de la date (timestamptz vers OffsetDateTime)
                        OffsetDateTime dateConsommation = rs.getObject("date_consommation", OffsetDateTime.class);

                        // Conversion de l'ENUM PostgreSQL vers un ENUM Java
                        String typeRepasString = rs.getString("type_repas");
                        TypeRepas typeRepas = TypeRepas.type(typeRepasString);

                        repas.add(new Repas(
                                noSS_patient,
                                rs.getString("noss_infirmier"),
                                dateConsommation,
                                rs.getString("remarque"),
                                typeRepas,
                                rs.getDouble("total_proteines"),
                                rs.getDouble("total_glucides"),
                                rs.getDouble("total_lipides"),
                                rs.getDouble("total_calories"),
                                rs.getInt("total_hydratation")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repas;
    }

    /**
     * Récupère tous les repas d'un patient, triés par date de consommation décroissante.
     * Inclut les statistiques nutritionnelles calculées pour chaque repas.
     * 
     * @param noSS_patient Le numéro de sécurité sociale du patient
     * @return La liste complète des repas du patient avec leurs statistiques
     */
    public List<Repas> getRepasPatient_All(String noSS_patient) {
        List<Repas> repas = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * " +
                         "FROM suivi_dietetique.getAllRepasWithStats(?) " +
                         "ORDER BY date_consommation DESC";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noSS_patient));

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        OffsetDateTime dateConsommation = rs.getObject("date_consommation", OffsetDateTime.class);
                        String typeRepasString = rs.getString("type_repas");
                        TypeRepas typeRepas = TypeRepas.type(typeRepasString);

                        repas.add(new Repas(
                                noSS_patient,
                                rs.getString("noss_infirmier"),
                                dateConsommation,
                                rs.getString("remarque"),
                                typeRepas,
                                rs.getDouble("total_proteines"),
                                rs.getDouble("total_glucides"),
                                rs.getDouble("total_lipides"),
                                rs.getDouble("total_calories"),
                                rs.getInt("total_hydratation")
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

    /**
     * Récupère un repas spécifique d'un patient à une date donnée.
     * 
     * @param noss_Patient Le numéro de sécurité sociale du patient
     * @param date La date et l'heure exacte du repas
     * @return Le repas trouvé ou null si aucun repas ne correspond
     */
    public Repas getOneRepas(String noss_Patient, OffsetDateTime date) {
        Repas repas = null;
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * " +
                         "FROM suivi_dietetique.getAllRepasWithStats(?) " +
                         "WHERE date_trunc('second', date_consommation) = date_trunc('second', ?)";
    
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss_Patient));
    
                // Convertir la date en UTC pour la comparaison
                OffsetDateTime utcDate = date.withOffsetSameInstant(ZoneOffset.UTC);
                stmt.setObject(2, utcDate);
    
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String typeRepasString = rs.getString("type_repas");
                        TypeRepas typeRepas = TypeRepas.type(typeRepasString);
            
                        repas = new Repas(
                                noss_Patient,
                                rs.getString("noss_infirmier"),
                                date,
                                rs.getString("remarque"),
                                typeRepas,
                                rs.getDouble("total_proteines"),
                                rs.getDouble("total_glucides"),
                                rs.getDouble("total_lipides"),
                                rs.getDouble("total_calories"),
                                rs.getInt("total_hydratation")
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repas;
    }

    /**
     * Récupère la liste des consommables et leurs quantités pour un repas spécifique.
     * Les consommables sont triés par nom décroissant et incluent toutes leurs
     * informations nutritionnelles.
     * 
     * @param noss Le numéro de sécurité sociale du patient
     * @param date La date et l'heure exacte du repas
     * @return La liste des consommables avec leurs quantités et valeurs nutritionnelles
     */
    public List<ConsommableQuantity> getConsommablesOfRepas(String noss, OffsetDateTime date) {
        List<ConsommableQuantity> consommableQuantities = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * " +
                         "FROM suivi_dietetique.consommables_repas " +
                         "WHERE noss_patient = ? AND date_trunc('second', date_consommation) = date_trunc('second', ?) " +
                         "ORDER BY consommable_nom DESC";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss));

                // Convertir la date en UTC pour la requête
                OffsetDateTime utcDate = date.withOffsetSameInstant(ZoneOffset.UTC);
                stmt.setObject(2, utcDate);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        consommableQuantities.add(new ConsommableQuantity(
                                rs.getInt("id"),
                                rs.getString("consommable_nom"),
                                rs.getString("type_consommable"),
                                rs.getInt("total_calories"),
                                rs.getFloat("total_proteines"),
                                rs.getFloat("total_glucides"),
                                rs.getFloat("total_lipides"),
                                rs.getFloat("total_potassium"),
                                rs.getFloat("total_cholesterol"),
                                rs.getFloat("total_sodium"),
                                rs.getFloat("total_vit_A"),
                                rs.getFloat("total_vit_C"),
                                rs.getFloat("total_vit_D"),
                                rs.getFloat("total_calcium"),
                                rs.getFloat("total_fer"),
                                rs.getInt("quantite")
                        ));
                    }

                    if (consommableQuantities.isEmpty()) {
                        System.out.println("Aucun consommable trouvé pour le repas du patient " +
                                         noss + " à la date " + date);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des consommables pour le repas:");
            e.printStackTrace();
        }
        return consommableQuantities;
    }

    /**
     * Supprime un repas spécifique de la base de données.
     * La suppression se fait en cascade sur les quantités de consommables associées.
     * 
     * @param noSS_patient Le numéro de sécurité sociale du patient
     * @param dateConsommation La date et l'heure exacte du repas à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean deleteRepasPatient(String noSS_patient, OffsetDateTime dateConsommation) {
        try (Connection connection = Database.getConnection()) {
            String query = "DELETE FROM suivi_dietetique.repas WHERE noss_patient = ? " +
                         "AND date_trunc('second', date_consommation) = date_trunc('second', ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noSS_patient));
                stmt.setObject(2, dateConsommation);
                stmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
