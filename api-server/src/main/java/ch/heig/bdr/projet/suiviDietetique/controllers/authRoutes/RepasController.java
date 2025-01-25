package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import ch.heig.bdr.projet.suiviDietetique.models.ConsommableQuantity;
import ch.heig.bdr.projet.suiviDietetique.models.Repas;

import ch.heig.bdr.projet.suiviDietetique.security.Role;

import ch.heig.bdr.projet.suiviDietetique.services.RepasService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class RepasController {
    private static final RepasService repasService = new RepasService();

    public static void registerRoutes(Javalin app) {
        app.post("/api/repas",RepasController::handleRepasCreation,Role.ADMIN, Role.PATIENT, Role.INFIRMIER, Role.DIETETICIEN);
        app.get("/api/patients/{id}/repas",RepasController::handleGetAllRepasByPatient,Role.ADMIN, Role.INFIRMIER, Role.DIETETICIEN, Role.PATIENT);
        app.get("/api/repas/consommables",RepasController::handleGetConsommablebyRepas,Role.ADMIN, Role.INFIRMIER, Role.DIETETICIEN, Role.PATIENT);
        app.get("/api/repas",RepasController::handleGetRepas,Role.ADMIN, Role.INFIRMIER, Role.DIETETICIEN, Role.PATIENT);
        app.delete("/api/repas", RepasController::handleDeleteRepas,Role.ADMIN, Role.INFIRMIER, Role.DIETETICIEN, Role.PATIENT);
    }
  
    private static void handleRepasCreation(Context ctx) {
        try {
            Repas repas = ctx.bodyAsClass(Repas.class);
            repasService.createRepas(repas);
            ctx.status(201).result("Repas créé avec succès.");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Erreur d'allergie")) {
                ctx.status(422).json(Map.of(
                    "error", "Un consommable contient un allergène pour ce patient.",
                    "details", e.getMessage()
                ));
            } else {
                ctx.status(422).json(Map.of(
                    "error", "Erreur lors de la création du repas.",
                    "details", e.getMessage()
                ));
            }
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la création du repas.",
                "details", e.getMessage()
            ));
        }
    }

    private static void handleGetAllRepasByPatient(Context ctx){
        try {
            String noss = ctx.pathParam("id");
            String interval = ctx.queryParam("interval");
            if (interval != null && !interval.isEmpty()){
                ctx.json(repasService.getAllRepasByPatient_interval(noss,interval));
            } else {
                ctx.json(repasService.getAllRepasByPatient_All(noss));
            }
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la récupération des repas du patient.",
                "details", e.getMessage()
            ));
        }
    }

    private static void handleGetRepas(Context ctx){
        try {
            String noss = ctx.queryParam("noss");
            OffsetDateTime date = OffsetDateTime.parse(ctx.queryParam("date"));
            ctx.json(repasService.getRepas(noss, date));
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la récupération du repas.",
                "details", e.getMessage()
            ));
        }
    }

    private static void handleGetConsommablebyRepas(Context ctx){
        try {
            String noss = ctx.queryParam("noss");
            OffsetDateTime date = OffsetDateTime.parse(ctx.queryParam("date"));
            List<ConsommableQuantity> consommables = repasService.getAllConsommableByRepas(noss, date);
            ctx.json(consommables);
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la récupération des consommables du repas.",
                "details", e.getMessage()
            ));
        }
    }

    private static void handleDeleteRepas(Context ctx){
        try {
            String noss = ctx.queryParam("noss");
            OffsetDateTime date = OffsetDateTime.parse(ctx.queryParam("date"));
            repasService.deleteRepas(noss, date);
            ctx.status(200).result("Repas supprimé avec succès.");
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la suppression du repas.",
                "details", e.getMessage()
            ));
        }
    }
}
