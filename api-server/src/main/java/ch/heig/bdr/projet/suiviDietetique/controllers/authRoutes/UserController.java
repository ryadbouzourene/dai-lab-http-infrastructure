package ch.heig.bdr.projet.suiviDietetique.controllers.authRoutes;

import ch.heig.bdr.projet.suiviDietetique.security.Role;
import ch.heig.bdr.projet.suiviDietetique.services.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import ch.heig.bdr.projet.suiviDietetique.services.AuthService;
public class UserController {
    private static final UserService userService = new UserService();

    public static void registerRoutes(Javalin app) {
        app.post("/api/user",UserController::handleUserCreation,Role.ADMIN);
    }
  
    private static void handleUserCreation(Context ctx){
        String email = ctx.formParam("email");
        String hashedPassword = AuthService.hashPassword(ctx.formParam("password"));
        Role role = Role.fromName(ctx.formParam("role"));
        userService.createUser(email,hashedPassword,role);
    }
}