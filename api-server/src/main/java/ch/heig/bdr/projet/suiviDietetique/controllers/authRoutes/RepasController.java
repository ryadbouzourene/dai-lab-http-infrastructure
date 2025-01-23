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
            // Récupérer l'objet Repas depuis le body de la requête
            Repas repas = ctx.bodyAsClass(Repas.class);

            // Appeler le service pour créer le repas
            repasService.createRepas(repas);

            // Si tout va bien, répondre avec un statut 201
            ctx.status(201).result("Repas créé avec succès.");
        } catch (RuntimeException e) {
            // Vérifier si l'erreur est liée aux allergies
            if (e.getMessage().contains("Erreur d'allergie")) {
                System.err.println("Erreur d'allergie détectée : " + e.getMessage());
                
                // Répondre avec un code HTTP 422 et un message explicatif
                ctx.status(422).json(Map.of(
                    "status", 422,
                    "error", "Un consommable contient un allergène pour ce patient.",
                    "details", e.getMessage()
                ));
            } else {
                // Gérer les autres erreurs de manière générique
                System.err.println("Erreur inattendue lors de la création du repas : " + e.getMessage());
                
                // Répondre avec un code HTTP 500 pour une erreur serveur
                ctx.status(500).json(Map.of(
                    "status", 500,
                    "error", "Erreur serveur lors de la création du repas.",
                    "details", e.getMessage()
                ));
            }
        } catch (Exception e) {
            // Gérer les erreurs générales non couvertes
            System.err.println("Erreur inattendue : " + e.getMessage());
            ctx.status(500).result("Une erreur inattendue est survenue.");
        }
    }


    private static void handleGetAllRepasByPatient(Context ctx){
        String noss = ctx.pathParam("id");
        String interval = ctx.queryParam("interval");
        if (interval != null && !interval.isEmpty()){
            ctx.json(repasService.getAllRepasByPatient_interval(noss,interval));
        } else {
            ctx.json(repasService.getAllRepasByPatient_All(noss));
        }
    }

    private static void handleGetRepas(Context ctx){
        String noss = ctx.queryParam("noss");
        try{OffsetDateTime date  = OffsetDateTime.parse(ctx.queryParam("date"));
            ctx.json(repasService.getRepas(noss, date));
        }catch(Exception e){
            System.err.println(e);
        }
        
    }

    private static void handleGetConsommablebyRepas(Context ctx){
        String noss = ctx.queryParam("noss");
        try{
            OffsetDateTime date  = OffsetDateTime.parse(ctx.queryParam("date"));
            List<ConsommableQuantity> test = repasService.getAllConsommableByRepas(noss, date);
            for(var e : test){
                System.out.println(e);
            }
            ctx.json(test);
           
        }catch(Exception e){
            System.err.println(e);
        }
    }

    private static void handleDeleteRepas(Context ctx){
        try {
            String noss = ctx.queryParam("noss");
            OffsetDateTime date  = OffsetDateTime.parse(ctx.queryParam("date"));
            repasService.deleteRepas(noss, date);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
