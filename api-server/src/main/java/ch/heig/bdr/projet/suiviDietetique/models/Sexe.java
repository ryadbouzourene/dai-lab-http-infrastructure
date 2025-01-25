package ch.heig.bdr.projet.suiviDietetique.models;

/**
 * Énumération représentant le sexe biologique d'une personne.
 * Utilisée pour la gestion des données médicales et le calcul des besoins nutritionnels.
 */
public enum Sexe {
    /** Représente le sexe masculin */
    HOMME("Homme"),
    /** Représente le sexe féminin */
    FEMME("Femme");

    /** 
     * Nom lisible du sexe en français
     */
    public final String name;

    /**
     * Constructeur privé de l'énumération.
     * 
     * @param s Le nom lisible du sexe
     */
    private Sexe(String s) {
        this.name = s;
    }

    /**
     * Convertit une chaîne de caractères en valeur de l'énumération Sexe.
     * La comparaison est insensible à la casse.
     * 
     * @param s La chaîne de caractères à convertir ("Homme" ou "Femme")
     * @return La valeur de l'énumération correspondante
     * @throws IllegalArgumentException Si la chaîne ne correspond à aucune valeur de l'énumération
     */
    public static Sexe type(String s) {
        for (Sexe type : Sexe.values()) {
            if (type.name.equalsIgnoreCase(s)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Aucun type correspondant pour le nom : " + s);
    }
}
