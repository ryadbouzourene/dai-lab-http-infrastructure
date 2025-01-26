package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.PatientService;
import ch.heig.bdr.projet.suiviDietetique.models.Patient;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class PatientController {
    private static final PatientService patientService = new PatientService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/patients", PatientController::handleGetPatients,Role.DIETETICIEN, Role.INFIRMIER,Role.ADMIN);
        app.get("/api/patients/{id}",PatientController::handleGetPatient,
                Role.DIETETICIEN, Role.INFIRMIER,Role.ADMIN, Role.PATIENT);
        app.get("/api/dieteticiens/{id}/patients", PatientController::handleGetPatientDieteticien,Role.ADMIN,Role.DIETETICIEN);
        app.get("/api/patients/{id}/sante",PatientController::handleGetDonneeSante,Role.ADMIN, Role.INFIRMIER, Role.DIETETICIEN, Role.PATIENT);
        app.get("/api/patients/{id}/objectifs",PatientController::handleGetObjectif,Role.ADMIN, Role.INFIRMIER, Role.DIETETICIEN, Role.PATIENT);
        app.post("/api/patients", PatientController::handlePostPatient,Role.ADMIN, Role.DIETETICIEN);
        app.get("/api/patients/{id}/allergies",PatientController::handleGetAllergies,Role.ADMIN,Role.DIETETICIEN,Role.INFIRMIER,Role.PATIENT);
        app.put("/api/patients/{id}", PatientController::handleUpdatePatient,Role.ADMIN, Role.DIETETICIEN);
        app.delete("/api/patients/{id}", PatientController::handleDeletePatient,Role.ADMIN, Role.DIETETICIEN);
    }

    private static void handleGetPatients(Context ctx){
        try {
            ctx.json(patientService.getAllPatients());
        } catch (Exception e) {
            ctx.status(422).result("Error getting patients: " + e.getMessage());
        }
    }

    private static void handleGetPatientDieteticien(Context ctx){
        try {
            String noss = ctx.pathParam("id");
            ctx.json(patientService.getAllPatientDieteticien(noss));
        } catch (Exception e) {
            ctx.status(422).result("Error getting dietician's patients: " + e.getMessage());
        }
    }
    private static void handleGetPatient(Context ctx){
        try {
            String noss = ctx.pathParam("id");
            ctx.json(patientService.getOnePatient(noss));
        } catch (Exception e) {
            ctx.status(422).result("Error getting patient: " + e.getMessage());
        }
    }
    private static void handleGetDonneeSante(Context ctx){
        try {
            String noss = ctx.pathParam("id");
            ctx.json(patientService.getAllDonneeSantee(noss));
        } catch (Exception e) {
            ctx.status(422).result("Error getting health data: " + e.getMessage());
        }
    }
    private static void handleGetObjectif(Context ctx){
        try {
            String noss = ctx.pathParam("id");
            ctx.json(patientService.getAllObjectif(noss));
        } catch (Exception e) {
            ctx.status(422).result("Error getting objectives: " + e.getMessage());
        }
    }
    private static void handlePostPatient(Context ctx) {
        try {
            Patient patient = ctx.bodyAsClass(Patient.class);

            if (patient.getNoSS() == null || patient.getNoSS().isEmpty()) {
                ctx.status(422).result("Le numéro de sécurité sociale (noSS) est obligatoire.");
                return;
            }

            patientService.insertPatient(patient);
            ctx.status(201).json(patient);
        } catch (Exception e) {
            ctx.status(422).result("Error creating patient: " + e.getMessage());
        }
    }

    private static void handleGetAllergies(Context ctx){
        try {
            String noss = ctx.pathParam("id");
            ctx.json(patientService.getAllAllergies(noss));
        } catch (Exception e) {
            ctx.status(422).result("Error getting allergies: " + e.getMessage());
        }
    }

    private static void handleUpdatePatient(Context ctx){
        try {
            String noss = ctx.pathParam("id");
            Patient patient = ctx.bodyAsClass(Patient.class);
            patientService.updatePatient(patient,noss);
            ctx.status(201).json(patient);
        } catch (Exception e) {
            ctx.status(422).result("Error updating patient: " + e.getMessage());
        }
    }

    private static void handleDeletePatient(Context ctx){
        try {
            String noss = ctx.pathParam("id");
            if (patientService.deleteOnePatient(noss)){
                ctx.status(200).json(Map.of("message", "Patient supprimé avec succès"));
            }else{
                ctx.status(404).json(Map.of("message", "Patient introuvable"));}
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la suppression du patient",
                "details", e.getMessage()
            ));
        }
    }
}

