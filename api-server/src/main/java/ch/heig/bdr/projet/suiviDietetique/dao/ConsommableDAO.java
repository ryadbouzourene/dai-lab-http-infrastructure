package ch.heig.bdr.projet.suiviDietetique.dao;

import ch.heig.bdr.projet.suiviDietetique.config.Database;
import ch.heig.bdr.projet.suiviDietetique.models.Consommable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'accès aux données (DAO) pour la table des consommables.
 * Gère les opérations de lecture des aliments et boissons consommables
 * dans la base de données, avec leurs valeurs nutritionnelles détaillées.
 */
public class ConsommableDAO {
    
    /**
     * Récupère la liste de tous les consommables disponibles dans la base de données.
     * Les consommables sont triés par ID croissant et incluent toutes leurs
     * valeurs nutritionnelles : calories, protéines, glucides (fibres et sucres),
     * lipides (total et gras saturés), vitamines et minéraux.
     * 
     * @return Liste de tous les consommables avec leurs valeurs nutritionnelles
     */
    public List<Consommable> getAllConsommables() {
        List<Consommable> consommables = new ArrayList<>();
        String query = "SELECT id, " +
                "nom, " +
                "type, " +
                "calories, " +
                "proteines, " +
                "(glucides).fibres AS fibres, " +
                "(glucides).sucre AS sucre, " +
                "(lipides).total AS lipides_total, " +
                "(lipides).gras_satures AS gras_satures, " +
                "potassium, " +
                "cholesterol, " +
                "sodium, " +
                "vit_A, " +
                "vit_C, " +
                "vit_D, " +
                "calcium, " +
                "fer " +
                "FROM suivi_dietetique.consommable " +
                "ORDER BY id ASC";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Création d'un nouvel objet Consommable avec toutes ses valeurs nutritionnelles
                consommables.add(new Consommable(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("calories"),
                        rs.getFloat("proteines"),
                        rs.getFloat("fibres"),
                        rs.getFloat("sucre"),
                        rs.getFloat("lipides_total"),
                        rs.getFloat("gras_satures"),
                        rs.getFloat("potassium"),
                        rs.getFloat("cholesterol"),
                        rs.getFloat("sodium"),
                        rs.getFloat("vit_a"),
                        rs.getFloat("vit_c"),
                        rs.getFloat("vit_d"),
                        rs.getFloat("calcium"),
                        rs.getFloat("fer")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return consommables;
    }
}
