package ch.heig.bdr.projet.suiviDietetique.services;

import ch.heig.bdr.projet.suiviDietetique.dao.PatientDAO;
import ch.heig.bdr.projet.suiviDietetique.models.Allergene;
import ch.heig.bdr.projet.suiviDietetique.models.DonneeSante;
import ch.heig.bdr.projet.suiviDietetique.models.Patient;
import ch.heig.bdr.projet.suiviDietetique.models.Objectif;

import java.util.List;

public class PatientService {

    private final PatientDAO patientDAO = new PatientDAO();

    public void insertPatient(Patient patient) {
        patientDAO.insertPatient(patient);}

    public List<Patient> getAllPatients() {
        return patientDAO.getAll();
    }

    public List<Patient> getAllPatientDieteticien(String noss){
        return patientDAO.getPatientDieteticien(noss);
    }

    public Patient getOnePatient(String noss){
        return patientDAO.getPatient(noss);
    }

    public List<DonneeSante> getAllDonneeSantee(String noss){
        return patientDAO.getDonneeSantee(noss);
    }

    public List<Objectif> getAllObjectif(String noss){
        return patientDAO.getObjectif(noss);
    }

    public List<Allergene> getAllAllergies(String noss){
        return patientDAO.getAllergies(noss);
    }

    public void updatePatient(Patient patient, String noss){ 
        patientDAO.updatePatient(patient, noss);
    }

    public boolean deleteOnePatient(String noss){
         return patientDAO.deletePatient(noss);
    }

}