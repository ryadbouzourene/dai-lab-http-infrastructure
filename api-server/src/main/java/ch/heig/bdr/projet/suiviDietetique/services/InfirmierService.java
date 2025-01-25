package ch.heig.bdr.projet.suiviDietetique.services;

import java.util.List;

import ch.heig.bdr.projet.suiviDietetique.dao.InfirmierDAO;
import ch.heig.bdr.projet.suiviDietetique.models.Infirmier;
import ch.heig.bdr.projet.suiviDietetique.models.Repas;

public class InfirmierService {
    private final InfirmierDAO infirmierDAO = new InfirmierDAO();
    public Infirmier getOneInfirmier(String noss){
        return infirmierDAO.getInfirmier(noss);
    }

    public List<Infirmier> getAllInfirmiers() {
        return infirmierDAO.getAll();
    }
    public List<Repas> getAllRepas(String nossInfirmier){
        return infirmierDAO.getRepas(nossInfirmier);
    }
}
