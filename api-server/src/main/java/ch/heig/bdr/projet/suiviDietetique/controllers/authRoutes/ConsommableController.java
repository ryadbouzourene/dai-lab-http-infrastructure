package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.ConsommableService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ConsommableController {
    private static final ConsommableService consommableService = new ConsommableService();

    public static void registerRoutes(Javalin app) {
        try {
            app.get("/api/consommables", ConsommableController::getAllConsommables, Role.ADMIN, Role.DIETETICIEN, Role.INFIRMIER, Role.PATIENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getAllConsommables(Context ctx) {
        ctx.json(consommableService.getAllConsommable());
    }
}
