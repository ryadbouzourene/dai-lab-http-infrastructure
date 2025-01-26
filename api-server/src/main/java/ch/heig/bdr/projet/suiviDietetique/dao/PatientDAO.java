package ch.heig.bdr.projet.suiviDietetique.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.heig.bdr.projet.suiviDietetique.config.Database;
import ch.heig.bdr.projet.suiviDietetique.models.Allergene;
import ch.heig.bdr.projet.suiviDietetique.models.DonneeSante;
import ch.heig.bdr.projet.suiviDietetique.models.Objectif;
import ch.heig.bdr.projet.suiviDietetique.models.Patient;

/**
 * Classe d'accès aux données pour les patients.
 * Gère toutes les opérations CRUD (Create, Read, Update, Delete) sur les patients
 * et leurs données associées (données de santé, objectifs, allergies).
 */
public class PatientDAO {

    /**
     * Insère un nouveau patient dans la base de données.
     * Cette opération est transactionnelle et insère les données à la fois dans
     * la table personne (informations générales) et la table patient (informations spécifiques).
     *
     * @param patient Le patient à insérer
     * @throws RuntimeException Si une erreur survient lors de l'insertion
     */
    public void insertPatient(Patient patient) {
        try (Connection connection = Database.getConnection()) {
            // Désactiver l'auto-commit pour gérer manuellement la transaction
            connection.setAutoCommit(false);

            try {
                // Insérer dans la table personne
                String insertPersonneQuery = "INSERT INTO personne (noSS, nom, prenom, dateNaissance, sexe) " +
                        "VALUES (?, ?, ?, ?, ?::suivi_dietetique.sexe)";
                try (PreparedStatement stmt = connection.prepareStatement(insertPersonneQuery)) {
                    stmt.setInt(1, Integer.parseInt(patient.getNoSS()));
                    stmt.setString(2, patient.getNom());
                    stmt.setString(3, patient.getPrenom());
                    stmt.setDate(4, patient.getDateNaissance());
                    stmt.setString(5, patient.getSexe()); // Le sexe est transformé en string à partir de l'enum
                    stmt.executeUpdate();
                }

                // Insérer dans la table patient
                String insertPatientQuery = "INSERT INTO suivi_dietetique.patient (noSS, noSS_dieteticien) " +
                        "VALUES (?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(insertPatientQuery)) {
                    if (patient.getNossDieteticien() == null) {
                        stmt.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        stmt.setInt(2, Integer.parseInt(patient.getNossDieteticien()));
                    }
                    stmt.setInt(1, Integer.parseInt(patient.getNoSS()));
                    stmt.executeUpdate();
                }

                String insertAllergy = "INSERT INTO est_allergique (noss_patient, nom_allergene) " +
                    " VALUES (?,?)";
                try (PreparedStatement stmt = connection.prepareStatement(insertAllergy)) {
                    for (Allergene allergene : patient.getAllergenes()) {
                        stmt.setInt(1, Integer.parseInt(patient.getNoSS()));
                        stmt.setString(2, allergene.getNomAllergene());
                        stmt.executeUpdate();
                    }
                }   

                // Si tout est réussi, on valide la transaction
                connection.commit();

            } catch (Exception ex) {
                // Si une exception survient, annuler la transaction
                connection.rollback();
                throw new RuntimeException("Erreur lors de l'insertion du patient. Transaction annulée : " + ex.getMessage(), ex);
            } finally {
                // Restaurer le mode auto-commit
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            // Gestion des erreurs lors de la connexion ou de la transaction
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'insertion du patient : " + e.getMessage());
        }
    }


    /**
     * Récupère la liste de tous les patients.
     * Utilise la vue patient_vue qui combine les informations des tables
     * personne et patient.
     * 
     * @return La liste de tous les patients
     */
    public List<Patient> getAll() {
        List<Patient> patients = new ArrayList<>();

        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM patient_vue";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(new Patient(
                            rs.getString("noss"),
                            rs.getString("noss_dieteticien"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getDate("datenaissance"),
                            rs.getString("sexe"),
                            rs.getDate("dateadmission")
                    ));
                }
            } catch (Exception e) {
                System.err.println("Error:" + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
        return patients;
    }

    /**
     * Récupère un patient spécifique par son numéro de sécurité sociale.
     * 
     * @param noss Le numéro de sécurité sociale du patient
     * @return Le patient trouvé ou null si aucun patient ne correspond
     */
    public Patient getPatient(String noss) {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM patient_vue WHERE noss = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Patient(
                                rs.getString("noss"),
                                rs.getString("noss_dieteticien"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getDate("datenaissance"),
                                rs.getString("sexe"),
                                rs.getDate("dateadmission")
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
     * Récupère tous les patients suivis par un diététicien spécifique.
     * 
     * @param noss Le numéro de sécurité sociale du diététicien
     * @return La liste des patients suivis par ce diététicien
     */
    public List<Patient> getPatientDieteticien(String noss) {
        List<Patient> patients = new ArrayList<>();

        try (Connection connection = Database.getConnection()) {
            String query = "SELECT * FROM patient_vue WHERE noss_dieteticien = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss));
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        patients.add(new Patient(
                                rs.getString("noss"),
                                rs.getString("noss_dieteticien"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getDate("datenaissance"),
                                rs.getString("sexe"),
                                rs.getDate("dateadmission")
                        ));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error:" + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }
        return patients;
    }

    /**
     * Récupère l'historique des données de santé d'un patient.
     * 
     * @param noss Le numéro de sécurité sociale du patient
     * @return La liste des données de santé du patient
     */
    public List<DonneeSante> getDonneeSantee(String noss) {
        List<DonneeSante> donneesSanteList = new ArrayList<>();
        String query = "SELECT * FROM donnees_sante WHERE noSS_patient = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(noss));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    donneesSanteList.add(new DonneeSante(
                        rs.getString("noSS_patient"),
                        rs.getObject("date", OffsetDateTime.class),
                        rs.getInt("taille"), 
                        rs.getInt("poids"), 
                        rs.getInt("tourDeTaille"), 
                        rs.getString("niveauActivitePhysique")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return donneesSanteList;
    }

    /**
     * Récupère la liste des objectifs d'un patient.
     * 
     * @param noss Le numéro de sécurité sociale du patient
     * @return La liste des objectifs du patient
     */
    public List<Objectif> getObjectif(String noss) {
        List<Objectif> objectifs = new ArrayList<>();
        String query = "SELECT * FROM objectif WHERE noSS_patient = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(noss));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    objectifs.add(new Objectif(
                        rs.getInt("numero"),
                        rs.getString("noSS_patient"),
                        rs.getString("noSS_dieteticien"),
                        rs.getDate("dateDebut"), 
                        rs.getDate("dateFin"),   
                        rs.getString("titre"),
                        rs.getBoolean("reussi"),
                        rs.getString("commentaire")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return objectifs;
    }

    /**
     * Récupère la liste des allergies d'un patient.
     * 
     * @param noss Le numéro de sécurité sociale du patient
     * @return La liste des allergènes auxquels le patient est allergique
     */
    public List<Allergene> getAllergies(String noss) {
        List<Allergene> allergenes = new ArrayList<>();
        String query = "SELECT nom_allergene FROM est_allergique WHERE noSS_patient = ?";
    
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(noss));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    allergenes.add(new Allergene(rs.getString("nom_allergene")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return allergenes;
    }

    /**
     * Met à jour les informations d'un patient.
     * Cette méthode met à jour à la fois les informations générales (table personne)
     * et les informations spécifiques au patient (table patient).
     * Si l'une des mises à jour échoue, toutes les modifications sont annulées.
     *
     * @param updatedPatient Le patient avec les nouvelles informations
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updatePatient(Patient updatedPatient, String noss) {
        try (Connection connection = Database.getConnection()) {
            // Désactiver l'auto-commit pour gérer manuellement la transaction
            connection.setAutoCommit(false);

            try {
                // Mise à jour des informations générales dans la table personne
                String query1 = "UPDATE personne SET nom = ?, prenom = ?, datenaissance = ?, sexe = ?::suivi_dietetique.sexe " +
                        "WHERE noss = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query1)) {
                    stmt.setString(1, updatedPatient.getNom());
                    stmt.setString(2, updatedPatient.getPrenom());
                    stmt.setDate(3, new java.sql.Date(updatedPatient.getDateNaissance().getTime()));
                    stmt.setString(4, updatedPatient.getSexe());
                    stmt.setInt(5, Integer.parseInt(noss));

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected <= 0) {
                        throw new RuntimeException("Échec de la mise à jour des informations générales.");
                    }
                }

                // Mise à jour des informations spécifiques au patient dans la table patient
                String query2 = "UPDATE patient SET dateadmission = ?, noss_dieteticien = ? WHERE noss = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query2)) {
                    stmt.setDate(1, new java.sql.Date(updatedPatient.getDateAdmission().getTime()));
                    stmt.setInt(2, Integer.parseInt(updatedPatient.getNossDieteticien()));
                    stmt.setInt(3, Integer.parseInt(noss));

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected <= 0) {
                        throw new RuntimeException("Échec de la mise à jour des informations spécifiques au patient.");
                    }
                }

                String deleteAllergy = "DELETE FROM est_allergique WHERE noss_patient = ?" ;
                try (PreparedStatement stmt = connection.prepareStatement(deleteAllergy)) {
                    stmt.setInt(1, Integer.parseInt(noss));
                    stmt.executeUpdate();
                }

                if (updatedPatient.getAllergenes() != null) {
                    String insertAllergy = "INSERT INTO est_allergique (noss_patient, nom_allergene) VALUES (?, ?)" ;
                    try (PreparedStatement stmt = connection.prepareStatement(insertAllergy)) {
                
                        for (Allergene allergene : updatedPatient.getAllergenes()) {
                            stmt.setInt(1, Integer.parseInt(noss));
                            stmt.setString(2, allergene.getNomAllergene());
                            stmt.executeUpdate();
                        }
                    }
                }
                // Valider la transaction si toutes les mises à jour réussissent
                connection.commit();
                return true;

            } catch (Exception ex) {
                // Annuler la transaction en cas d'erreur
                connection.rollback();
                System.err.println("Transaction annulée : " + ex.getMessage());
                return false;
            } finally {
                // Restaurer le mode auto-commit
                connection.setAutoCommit(true);
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du patient : " + e.getMessage());
            return false;
        }
    }

    public boolean deletePatient(String noss) {
        try (Connection connection = Database.getConnection()) {
            // Vérifie que la personne est un patient avant de la supprimer
            String query = "DELETE FROM personne WHERE noss = ? AND noss IN (SELECT noss FROM patient)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(noss));
    
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

}
