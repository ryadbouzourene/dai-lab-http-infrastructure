package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import java.util.List;
import java.util.Map;

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
        try {
            String noss = ctx.pathParam("id");
            ctx.json(dieteticienService.getOneDieteticien(noss));
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la récupération du diététicien",
                "details", e.getMessage()
            ));
        }
    }

    private static void handleGetdieteticienPatient(Context ctx){
        try {
            String noss = ctx.pathParam("id");
            ctx.json(dieteticienService.getOneDieteticienPatient(noss));
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la récupération du diététicien du patient",
                "details", e.getMessage()
            ));
        }
    }

    private static void handleGetdieteticiens(Context ctx){
        try {
            ctx.json(dieteticienService.getAllDieteticiens());
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la récupération des diététiciens",
                "details", e.getMessage()
            ));
        }
    }

    private static void handleGetObjectif(Context ctx) {
        try {
            String noss = ctx.pathParam("id");
            List<Objectif> objectifs = dieteticienService.getAllObjectif(noss);
            if (objectifs == null || objectifs.isEmpty()) {
                ctx.status(422).json(Map.of(
                    "error", "Aucun objectif trouvé",
                    "details", "Aucun objectif n'existe pour ce diététicien"
                ));
            } else {
                ctx.json(objectifs);
            }
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la récupération des objectifs",
                "details", e.getMessage()
            ));
        }
    }
}
