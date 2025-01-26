package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import io.javalin.Javalin;

/**
 * Contrôleur principal pour l'enregistrement de toutes les routes authentifiées de l'API.
 * Centralise l'enregistrement des routes qui nécessitent une authentification.
 * Les routes sont organisées par domaine fonctionnel (patients, utilisateurs, etc.).
 */
public class AuthRoutesController {
    
    /**
     * Enregistre toutes les routes authentifiées de l'application.
     * Cette méthode initialise les routes pour :
     * - La gestion des patients
     * - La gestion des utilisateurs
     * - La gestion des infirmiers
     * - La gestion des diététiciens
     * - La gestion des repas
     * - La gestion des allergènes
     * - La gestion des consommables
     * - La gestion des données de santé
     *
     * @param app L'instance Javalin pour l'enregistrement des routes
     */
    public static void registerRoutes(Javalin app) {
        // Enregistrement des routes pour la gestion des patients
        PatientController.registerRoutes(app);
        
        // Enregistrement des routes pour la gestion des utilisateurs
        UserController.registerRoutes(app);
        
        // Enregistrement des routes pour la gestion des infirmiers
        InfirmierController.registerRoutes(app);
        
        // Enregistrement des routes pour la gestion des diététiciens
        DieteticienController.registerRoutes(app);
        
        // Enregistrement des routes pour la gestion des repas
        RepasController.registerRoutes(app);
        
        // Enregistrement des routes pour la gestion des allergènes
        AllergeneController.registerRoutes(app);
        
        // Enregistrement des routes pour la gestion des consommables
        ConsommableController.registerRoutes(app);
        
        // Enregistrement des routes pour la gestion des données de santé
        DonneeSanteController.registerRoutes(app);

        // Enregistrement des routes pour la gestion des personnes
        PersonneController.registerRoutes(app);
    }
}
