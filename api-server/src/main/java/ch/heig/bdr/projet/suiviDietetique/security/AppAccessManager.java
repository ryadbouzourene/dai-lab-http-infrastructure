package ch.heig.bdr.projet.suiviDietetique.security;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import io.javalin.security.AccessManager;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Gestionnaire d'accès de l'application qui implémente la logique de contrôle d'accès
 * pour toutes les routes de l'API. Cette classe vérifie les autorisations des utilisateurs
 * en fonction de leur rôle et des permissions requises pour chaque route.
 */
public class AppAccessManager implements AccessManager {

    /**
     * Gère l'accès aux routes de l'application en vérifiant les permissions de l'utilisateur.
     * 
     * @param handler Le gestionnaire de la route à exécuter
     * @param ctx Le contexte de la requête HTTP
     * @param permittedRoles L'ensemble des rôles autorisés pour cette route
     * @throws Exception Si une erreur survient pendant la gestion de l'accès
     */
    @Override
    public void manage(@NotNull Handler handler, @NotNull Context ctx, @NotNull Set<? extends RouteRole> permittedRoles) throws Exception {
        // Si la route est accessible à tous, exécuter directement le handler
        if (permittedRoles.contains(Role.ANYONE)) {
            handler.handle(ctx);
            return;
        }

        // Récupérer le rôle de l'utilisateur depuis la session
        String userRole = ctx.sessionAttribute("user-role");

        // Vérifier si l'utilisateur est connecté
        if (userRole == null) {
            throw new UnauthorizedResponse("Accès refusé : vous devez vous " +
                    "connecter pour accéder à cette ressource.");
        }

        // Vérifier si l'utilisateur a les permissions nécessaires
        if (permittedRoles.contains(Role.fromName(userRole))) {
            handler.handle(ctx);
        } else {
            throw new UnauthorizedResponse("Accès refusé : vous n'avez pas les permissions nécessaires.");
        }
    }
}
