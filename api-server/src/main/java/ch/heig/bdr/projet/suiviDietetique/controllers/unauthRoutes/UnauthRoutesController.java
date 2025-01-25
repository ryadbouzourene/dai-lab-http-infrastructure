package ch.heig.bdr.projet.suiviDietetique.controllers.unauthRoutes;

import ch.heig.bdr.projet.suiviDietetique.models.User;
import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.AuthService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

/**
 * Contrôleur gérant les routes publiques de l'API, notamment l'authentification.
 * Ces routes sont accessibles sans authentification préalable et permettent :
 * - La connexion des utilisateurs
 * - La déconnexion
 * - La vérification de l'état de la session
 */
public class UnauthRoutesController {
    // TODO : Ajouter app.path("/api") par défaut pour éviter les répétitions
    /** Service gérant l'authentification des utilisateurs */
    private static final AuthService authService = new AuthService();

    /**
     * Enregistre les routes publiques de l'application.
     * Toutes les routes sont préfixées par /api et accessibles par n'importe quel rôle.
     *
     * @param app L'instance Javalin pour l'enregistrement des routes
     */
    public static void registerRoutes(Javalin app) {
        app.post("/api/login", UnauthRoutesController::handleLogin, Role.ANYONE);
        app.post("/api/logout", UnauthRoutesController::handleLogout, Role.ANYONE);
        app.get("/api/session", UnauthRoutesController::handleCheckSession, Role.ANYONE);
    }

    /**
     * Gère la connexion d'un utilisateur.
     * Attend un objet JSON contenant username et password dans le corps de la requête.
     * En cas de succès, crée une session avec les informations de l'utilisateur.
     * 
     * @param ctx Le contexte de la requête HTTP
     * @return 200 OK avec les informations de l'utilisateur si l'authentification réussit
     * @return 401 Unauthorized si les identifiants sont incorrects
     */
    private static void handleLogin(Context ctx) {
        try {
            // Extraire les identifiants du corps de la requête
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            if (!body.containsKey("username") || !body.containsKey("password")) {
                ctx.status(422).json(Map.of(
                    "error", "Données manquantes",
                    "details", "Le nom d'utilisateur et le mot de passe sont requis"
                ));
                return;
            }

            String username = body.get("username").toString();
            String password = body.get("password").toString();

            // Tenter l'authentification
            User user = authService.authenticate(username, password);

            if (user != null) {
                // Créer les attributs de session
                ctx.sessionAttribute("user", username);
                ctx.sessionAttribute("user-role", user.getRole().toString());
                ctx.sessionAttribute("user-noss", user.getNoss());
                
                // Configuration de la durée de la session (1 heure)
                ctx.req().getSession().setMaxInactiveInterval(60 * 60);
                
                // Renvoyer les informations de l'utilisateur
                ctx.json(Map.of("username", username, "role",
                        user.getRole().toString(), "noss", user.getNoss()));
            } else {
                ctx.status(401).json(Map.of(
                    "error", "Authentification échouée",
                    "details", "Email ou mot de passe incorrect"
                ));
            }
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la connexion",
                "details", e.getMessage()
            ));
        }
    }

    /**
     * Gère la déconnexion d'un utilisateur.
     * Invalide la session courante.
     * 
     * @param ctx Le contexte de la requête HTTP
     * @return 200 OK avec un message de confirmation
     */
    private static void handleLogout(Context ctx) {
        try {
            // Invalider la session
            ctx.req().getSession().invalidate();
            ctx.status(200).json(Map.of("message", "Déconnexion réussie"));
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la déconnexion",
                "details", e.getMessage()
            ));
        }
    }

    /**
     * Vérifie l'état de la session courante.
     * Permet aux clients de valider si leur session est toujours active
     * et de récupérer les informations de l'utilisateur connecté.
     * 
     * @param ctx Le contexte de la requête HTTP
     * @return 200 OK avec les informations de l'utilisateur si la session est active
     * @return 401 Unauthorized si la session n'est pas active
     */
    private static void handleCheckSession(Context ctx) {
        try {
            // Récupérer les attributs de session
            String username = ctx.sessionAttribute("user");
            String userRole = ctx.sessionAttribute("user-role");
            Integer userNoss = ctx.sessionAttribute("user-noss");

            // Vérifier si la session est valide
            if (username == null || userRole == null || userNoss == null) {
                ctx.status(401).json(Map.of(
                    "error", "Non authentifié",
                    "details", "La session n'est pas valide"
                ));
            } else {
                ctx.json(Map.of("username", username, "role", userRole, "noss", userNoss));
            }
        } catch (Exception e) {
            ctx.status(422).json(Map.of(
                "error", "Erreur lors de la vérification de la session",
                "details", e.getMessage()
            ));
        }
    }
}
