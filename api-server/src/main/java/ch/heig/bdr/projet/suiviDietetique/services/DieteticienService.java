package ch.heig.bdr.projet.suiviDietetique.services;

import java.util.List;

import ch.heig.bdr.projet.suiviDietetique.dao.DieteticienDAO;
import ch.heig.bdr.projet.suiviDietetique.models.Dieteticien;
import ch.heig.bdr.projet.suiviDietetique.models.Objectif;

public class DieteticienService {
    private final DieteticienDAO dieteticienDAO = new DieteticienDAO();
    public Dieteticien getOneDieteticien(String noss){
        return dieteticienDAO.getDieteticien(noss);
    }

    public Dieteticien getOneDieteticienPatient(String noss){
        return dieteticienDAO.getDieteticienPatient(noss);
    }

    public List<Dieteticien> getAllDieteticiens() {
        return dieteticienDAO.getAll();
    }

    public List<Objectif> getAllObjectif(String noss){
        return dieteticienDAO.getObjectifs(noss);
    }
}
