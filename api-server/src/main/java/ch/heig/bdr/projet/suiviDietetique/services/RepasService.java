package ch.heig.bdr.projet.suiviDietetique.services;

import ch.heig.bdr.projet.suiviDietetique.models.ConsommableQuantity;
import ch.heig.bdr.projet.suiviDietetique.models.Repas;

import ch.heig.bdr.projet.suiviDietetique.dao.RepasDao;

import java.time.OffsetDateTime;
import java.util.List;

public class RepasService {
    private final RepasDao repasDAO = new RepasDao();

    public void createRepas(Repas repas){
        repasDAO.insertRepas(repas);
    }
    public List<Repas> getAllRepasByPatient_All(String noss){
        return repasDAO.getRepasPatient_All(noss);
    }

    public List<Repas> getAllRepasByPatient_interval(String noss, String interval){
        return repasDAO.getRepasPatient_interval(noss, interval);
    }

    public Repas getRepas(String noss,OffsetDateTime date){
        return repasDAO.getOneRepas(noss, date);
    }

    public List<ConsommableQuantity> getAllConsommableByRepas(String noss, OffsetDateTime date){
        return repasDAO.getConsommablesOfRepas(noss, date);
    }

    public boolean deleteRepas(String noss, OffsetDateTime dateRepas){
        return repasDAO.deleteRepasPatient(noss, dateRepas);
    }
}
