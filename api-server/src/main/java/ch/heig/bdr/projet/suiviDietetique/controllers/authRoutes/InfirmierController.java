package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.InfirmierService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class InfirmierController {
    private static final InfirmierService infirmierService = new InfirmierService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/infirmiers/{id}", InfirmierController::handleGetInfirmier,
                Role.DIETETICIEN, Role.INFIRMIER,Role.ADMIN, Role.PATIENT);
        app.get("/api/infirmiers/",InfirmierController::handleGetInfirmiers,Role.DIETETICIEN, Role.INFIRMIER, Role.ADMIN);
        app.get("/api/infirmiers/{id}/repas",
                InfirmierController::handleGetInfirmierRepas,Role.ADMIN,
                Role.DIETETICIEN, Role.INFIRMIER);
    }

    private static void handleGetInfirmier(Context ctx){
        String noss = ctx.pathParam("id");
        ctx.json(infirmierService.getOneInfirmier(noss));
    }

    private static void handleGetInfirmiers(Context ctx){
        ctx.json(infirmierService.getAllInfirmiers());
    }

    private static void handleGetInfirmierRepas(Context ctx){
        String noss = ctx.pathParam("id");
        ctx.json(infirmierService.getAllRepas(noss));
    }
}
