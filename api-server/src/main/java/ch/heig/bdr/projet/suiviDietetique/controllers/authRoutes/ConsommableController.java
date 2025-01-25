package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.ConsommableService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;

public class ConsommableController {
    private static final ConsommableService consommableService = new ConsommableService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/consommables", ConsommableController::getAllConsommables, Role.ADMIN, Role.DIETETICIEN, Role.INFIRMIER, Role.PATIENT);
    }

    public static void getAllConsommables(Context ctx) {
        try {
            ctx.json(consommableService.getAllConsommable());
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la récupération des consommables",
                "details", e.getMessage()
            ));
        }
    }
}
