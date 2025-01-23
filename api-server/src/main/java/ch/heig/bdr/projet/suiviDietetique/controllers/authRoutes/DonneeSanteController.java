package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import java.time.OffsetDateTime;
import java.util.Map;

import ch.heig.bdr.projet.suiviDietetique.models.DonneeSante;
import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.DonneeSanteService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * Contrôleur gérant les routes API pour les données de santé des patients.
 * Fournit des endpoints pour la création et la suppression des données de santé.
 * Seuls les administrateurs et les infirmiers ont accès à ces fonctionnalités.
 */
public class DonneeSanteController {
    /** Service gérant les opérations sur les données de santé */
    private static final DonneeSanteService donneeSanteService = new DonneeSanteService();

    /**
     * Enregistre les routes de l'API pour les données de santé.
     * Routes disponibles :
     * - POST /api/sante : Création d'une nouvelle donnée de santé
     * - DELETE /api/sante : Suppression d'une donnée de santé existante
     *
     * @param app L'instance Javalin pour l'enregistrement des routes
     */
    public static void registerRoutes(Javalin app) {
        app.post("api/sante", DonneeSanteController::handleCreateDonneeSante,
                Role.ADMIN, Role.DIETETICIEN, Role.INFIRMIER);
        app.delete("api/sante", DonneeSanteController::handleDeleteDonneeSante,
                Role.ADMIN, Role.DIETETICIEN, Role.INFIRMIER);
    }

    /**
     * Gère la création d'une nouvelle donnée de santé.
     * Attend un objet DonneeSante au format JSON dans le corps de la requête.
     * 
     * @param ctx Le contexte de la requête HTTP
     * @return 201 Created si la création réussit
     * @return 400 Bad Request si les données sont invalides
     */
    private static void handleCreateDonneeSante(Context ctx) {
        try {
            // Mapper le JSON reçu en objet DonneeSante
            DonneeSante donneeSante = ctx.bodyAsClass(DonneeSante.class);

            // Appeler la méthode du service pour insérer la donnée
            donneeSanteService.insertDonneeSante(donneeSante);

            // Répondre au client avec un statut de réussite
            ctx.status(201).json(Map.of("message", "Donnée de santé créée avec succès"));
        } catch (Exception e) {
            // Gérer les erreurs et répondre avec un message approprié
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Gère la suppression d'une donnée de santé.
     * Requiert deux paramètres de requête :
     * - noss : Le numéro de sécurité sociale du patient
     * - date : La date et l'heure de la donnée à supprimer (format ISO-8601)
     * 
     * @param ctx Le contexte de la requête HTTP
     * @return 204 No Content si la suppression réussit
     * @return 400 Bad Request si les paramètres sont manquants ou invalides
     * @return 404 Not Found si la donnée n'existe pas
     * @return 500 Internal Server Error en cas d'erreur serveur
     */
    private static void handleDeleteDonneeSante(Context ctx) {
        try {
            // Récupérer et valider le numéro de sécurité sociale
            String nossPatient = ctx.queryParam("noss");
            String dateParam = ctx.queryParam("date");
            
            // Vérifier la présence du paramètre date
            if (dateParam == null) {
                ctx.status(400).json("Paramètre 'date' manquant");
                return;
            }
    
            // Parser et valider le format de la date
            OffsetDateTime date;
            try {
                date = OffsetDateTime.parse(dateParam);
            } catch (Exception e) {
                ctx.status(400).json("Format de date invalide. Utilisez ISO-8601.");
                return;
            }
    
            // Tenter la suppression
            donneeSanteService.deleteDonneeSante(nossPatient, date);
            ctx.status(204); // No Content
        } catch (RuntimeException e) {
            // Erreur si la donnée n'existe pas
            ctx.status(404).json(e.getMessage());
        } catch (Exception e) {
            // Erreur serveur inattendue
            ctx.status(500).json("Erreur interne du serveur");
        }
    }
}
