package ch.heig.bdr.projet.suiviDietetique.services;

import ch.heig.bdr.projet.suiviDietetique.dao.AllergeneDAO;
import ch.heig.bdr.projet.suiviDietetique.models.Allergene;

import java.util.List;

/**
 * Service gérant les opérations métier liées aux allergènes.
 * Fait le lien entre les contrôleurs et la couche d'accès aux données (DAO).
 */
public class AllergeneService {
    /** DAO pour l'accès aux données des allergènes */
    private final AllergeneDAO allergeneDAO = new AllergeneDAO();

    /**
     * Récupère la liste complète des allergènes depuis la base de données.
     * 
     * @return Liste de tous les allergènes disponibles
     */
    public List<Allergene> getAllAllergeneDAO() {
        return allergeneDAO.getAllAllergenes();
    }
}
