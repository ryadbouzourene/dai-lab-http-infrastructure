package ch.heig.bdr.projet.suiviDietetique.models;

/**
 * Énumération représentant les différents types de repas dans une journée.
 * Cette classification permet de catégoriser les repas et de suivre
 * les habitudes alimentaires des patients.
 */
public enum TypeRepas {
    /** Premier repas de la journée */
    PETITDEJEUNER("Petit-déjeuner"),
    
    /** Repas du midi */
    DEJEUNER("Déjeuner"), 
    
    /** Repas du soir */
    DINER("Dîner"), 
    
    /** Petit repas entre les repas principaux */
    COLLATION("Collation");

    /** Nom lisible du type de repas en français */
    public String name;

    /**
     * Constructeur privé de l'énumération.
     * 
     * @param s Le nom lisible du type de repas
     */
    private TypeRepas(String s) {
        this.name = s;
    }

    /**
     * Convertit une chaîne de caractères en valeur de l'énumération TypeRepas.
     * La comparaison est insensible à la casse.
     * 
     * @param s La chaîne de caractères à convertir
     * @return La valeur de l'énumération correspondante
     * @throws IllegalArgumentException Si la chaîne ne correspond à aucune valeur de l'énumération
     */
    public static TypeRepas type(String s) {
        for (TypeRepas statut : TypeRepas.values()) {
            if (statut.name.equalsIgnoreCase(s)) {
                return statut;
            }
        }
        throw new IllegalArgumentException("Aucun type correspondant pour le nom : " + s);
    }
}