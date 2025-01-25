package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.AllergeneService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * Contrôleur gérant les routes API pour les allergènes.
 * Fournit des endpoints pour consulter la liste des allergènes.
 * Accessible à tous les utilisateurs authentifiés (admin, diététicien, infirmier, patient).
 */
public class AllergeneController {
    /** Service gérant les opérations sur les allergènes */
    private static final AllergeneService allergeneService = new AllergeneService();

    /**
     * Enregistre les routes de l'API pour les allergènes.
     * Routes disponibles :
     * - GET /api/allergenes : Récupération de la liste complète des allergènes
     *
     * @param app L'instance Javalin pour l'enregistrement des routes
     */
    public static void registerRoutes(Javalin app) {
        try {
            // Route accessible à tous les utilisateurs authentifiés
            app.get("/api/allergenes", AllergeneController::getAllAllergene, 
                   Role.ADMIN, Role.DIETETICIEN, Role.INFIRMIER, Role.PATIENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gère la récupération de tous les allergènes.
     * Retourne la liste complète des allergènes au format JSON.
     * 
     * @param ctx Le contexte de la requête HTTP
     * @return 200 OK avec la liste des allergènes
     */
    public static void getAllAllergene(Context ctx) {
        ctx.json(allergeneService.getAllAllergeneDAO());
    }
}