package ch.heig.bdr.projet.suiviDietetique;

import ch.heig.bdr.projet.suiviDietetique.security.AppAccessManager;
import ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes.AuthRoutesController;
import ch.heig.bdr.projet.suiviDietetique.controllers.unauthRoutes.UnauthRoutesController;
import io.javalin.*;

/**
 * Classe principale de l'application de Suivi Diététique.
 * Cette classe initialise le serveur Javalin et configure les routes de l'application.
 */
public class Main {
    /**
     * Point d'entrée principal de l'application.
     * Configure et démarre le serveur Javalin avec les paramètres suivants :
     * - Port d'écoute : 7070
     * - Gestionnaire d'accès personnalisé pour la sécurité
     * - Routes authentifiées et non authentifiées
     *
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        // Création et configuration de l'instance Javalin
        Javalin app = Javalin.create(config -> {
            // Configuration du gestionnaire d'accès pour la sécurité
            config.accessManager(new AppAccessManager());
        }).start(7070);

        // Enregistrement des routes non authentifiées (publiques)
        UnauthRoutesController.registerRoutes(app);
        
        // Enregistrement des routes authentifiées (protégées)
        AuthRoutesController.registerRoutes(app);
    }
}