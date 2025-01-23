package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.PatientService;
import ch.heig.bdr.projet.suiviDietetique.models.Patient;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class PatientController {
    private static final PatientService patientService = new PatientService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/patients", PatientController::handleGetPatients,Role.DIETETICIEN, Role.INFIRMIER,Role.ADMIN);
        app.get("/api/patients/{id}",PatientController::handleGetPatient,
                Role.DIETETICIEN, Role.INFIRMIER,Role.ADMIN, Role.PATIENT);
        app.delete("/api/patients/{id}",PatientController::handleDeletePatient, Role.ADMIN, Role.DIETETICIEN);
        app.get("/api/dieteticiens/{id}/patients", PatientController::handleGetPatientDieteticien,Role.ADMIN,Role.DIETETICIEN);
        app.get("api/patients/{id}/sante",PatientController::handleGetDonneeSante,Role.ADMIN, Role.INFIRMIER, Role.DIETETICIEN, Role.PATIENT);
        app.get("api/patients/{id}/objectifs",PatientController::handleGetObjectif,Role.ADMIN, Role.INFIRMIER, Role.DIETETICIEN, Role.PATIENT);
        app.post("api/patients", PatientController::handlePostPatient,Role.ADMIN, Role.DIETETICIEN);
        app.get("/api/patients/{id}/allergies",PatientController::handleGetAllergies,Role.ADMIN,Role.DIETETICIEN,Role.INFIRMIER,Role.PATIENT);
    }

    private static void handleGetPatients(Context ctx){
        ctx.json(patientService.getAllPatients());        
    }

    private static void handleGetPatientDieteticien(Context ctx){
        String noss = ctx.pathParam("id");
        ctx.json(patientService.getAllPatientDieteticien(noss));
    }
    private static void handleGetPatient(Context ctx){
        String noss = ctx.pathParam("id");
        ctx.json(patientService.getOnePatient(noss));
    }
    private static void handleDeletePatient(Context ctx){
        String noss = ctx.pathParam("id");
        patientService.deleteOnePatient(noss);
    }
    private static void handleGetDonneeSante(Context ctx){
        String noss = ctx.pathParam("id");
        ctx.json(patientService.getAllDonneeSantee(noss));
    }
    private static void handleGetObjectif(Context ctx){
        String noss = ctx.pathParam("id");
        ctx.json(patientService.getAllObjectif(noss)); 
    }
    private static void handlePostPatient(Context ctx) {
        try {
            // Parse le JSON reçu dans le corps de la requête en un objet Patient
            Patient patient = ctx.bodyAsClass(Patient.class);

            // Vérifie que tous les champs obligatoires sont présents
            if (patient.getNoSS() == null || patient.getNoSS().isEmpty()) {
                ctx.status(400).result("Le numéro de sécurité sociale (noSS) est obligatoire.");
                return;
            }

            // Appelle le service pour insérer le patient dans la base de données
            patientService.insertPatient(patient);

            // Retourne une réponse de succès
            ctx.status(201).json(patient);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Une erreur est survenue lors de la création du patient.");
        }

    }
    private static void handleGetAllergies(Context ctx){
        String noss = ctx.pathParam("id");
        ctx.json(patientService.getAllAllergies(noss)); 
    }

}