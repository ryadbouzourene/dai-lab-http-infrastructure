package ch.heig.bdr.projet.suiviDietetique.services;

import ch.heig.bdr.projet.suiviDietetique.dao.ConsommableDAO;
import ch.heig.bdr.projet.suiviDietetique.models.Consommable;

import java.util.List;

public class ConsommableService {
    private final ConsommableDAO consommableDAO = new ConsommableDAO();

    public List<Consommable> getAllConsommable() {
        return consommableDAO.getAllConsommables();
    }
}
