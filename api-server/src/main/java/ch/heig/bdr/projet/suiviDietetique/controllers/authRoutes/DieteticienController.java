package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import java.util.List;

import ch.heig.bdr.projet.suiviDietetique.models.Objectif;
import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.DieteticienService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class DieteticienController {
    private static final DieteticienService dieteticienService = new DieteticienService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/dieteticiens/{id}", DieteticienController::handleGetdieteticien, Role.DIETETICIEN,Role.ADMIN);
        app.get("/api/dieteticiens/",DieteticienController::handleGetdieteticiens, Role.DIETETICIEN, Role.ADMIN);
        app.get("/api/patients/{id}/dieteticien",
                DieteticienController::handleGetdieteticienPatient,Role.ADMIN,
                Role.DIETETICIEN, Role.INFIRMIER, Role.PATIENT);
        app.get("/api/dieteticiens/{id}/objectifs",DieteticienController::handleGetObjectif,Role.ADMIN, Role.DIETETICIEN);
    }

    private static void handleGetdieteticien(Context ctx){
        String noss = ctx.pathParam("id");
        ctx.json(dieteticienService.getOneDieteticien(noss));
    }

    private static void handleGetdieteticienPatient(Context ctx){
        String noss = ctx.pathParam("id");
        ctx.json(dieteticienService.getOneDieteticienPatient(noss));
    }

    private static void handleGetdieteticiens(Context ctx){
        ctx.json(dieteticienService.getAllDieteticiens());
    }

    private static void handleGetObjectif(Context ctx) {
        String noss = ctx.pathParam("id");
        try {
            List<Objectif> objectifs = dieteticienService.getAllObjectif(noss);
            if (objectifs == null || objectifs.isEmpty()) {
                ctx.status(404).json("No objectifs found");
            } else {
                System.out.println("Objectifs fetched: " + objectifs); // Log pour validation
                ctx.json(objectifs);
            }
        } catch (Exception e) {
            System.err.println("Error in handleGetObjectif: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).json("Internal Server Error");
        }
    }

        
}
