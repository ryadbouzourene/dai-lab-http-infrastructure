package ch.heig.bdr.projet.suiviDietetique.dao;

import ch.heig.bdr.projet.suiviDietetique.config.Database;
import ch.heig.bdr.projet.suiviDietetique.models.Allergene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'accès aux données (DAO) pour la table des allergènes.
 * Gère les opérations de lecture des allergènes dans la base de données.
 */
public class AllergeneDAO {
    /**
     * Récupère la liste de tous les allergènes uniques depuis la base de données.
     * La requête utilise DISTINCT pour éviter les doublons dans la liste des allergènes.
     * 
     * @return Liste des allergènes uniques présents dans la base de données
     */
    public List<Allergene> getAllAllergenes() {
        List<Allergene> allergenes = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            // Requête SQL pour obtenir les noms d'allergènes uniques
            String query = "SELECT DISTINCT(nom_allergene) FROM suivi_dietetique.est_allergique";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                // Pour chaque allergène trouvé, créer un nouvel objet Allergene
                while (rs.next()) {
                    allergenes.add(new Allergene(
                            rs.getString("nom_allergene"))
                    );
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return allergenes;
    }
}
