package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import ch.heig.bdr.projet.suiviDietetique.security.Role;

import ch.heig.bdr.projet.suiviDietetique.services.PersonneService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;

public class PersonneController {
    private static PersonneService personneService = new PersonneService();

    public static void registerRoutes(Javalin app) {
        app.delete("/api/personnes/{id}", PersonneController::handleDeletePersonne,Role.ADMIN);
    }

    private static void handleDeletePersonne(Context ctx) {
        try {
            String noss = ctx.pathParam("id");
            personneService.deleteOnePersonne(noss);
            ctx.status(200).json(Map.of("message", "Personne supprimée avec succès"));
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la suppression de la personne",
                "details", e.getMessage()
            ));
        }
    }
}
