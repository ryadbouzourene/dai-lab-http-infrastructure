package ch.heig.bdr.projet.suiviDietetique.models;

/**
 * Énumération représentant les différents niveaux d'activité physique d'un patient.
 * Cette classification permet d'évaluer le niveau d'activité physique global
 * et aide à déterminer les besoins énergétiques quotidiens.
 */
public enum NiveauActivitePhysique {

    /** Peu ou pas d'exercice, travail de bureau */
    SEDENTAIRE("Sédentaire"),
    
    /** Activité légère 1-3 fois par semaine */
    LEGEREMENT_ACTIF("Légèrement actif"),
    
    /** Activité modérée 3-5 fois par semaine */
    MODEREMENT_ACTIF("Modérément actif"),
    
    /** Activité intense 6-7 fois par semaine */
    TRES_ACTIF("Très actif"),
    
    /** Activité très intense quotidienne ou athlète professionnel */
    EXTREMEMENT_ACTIF("Extrêmement actif");

    /** 
     * Nom lisible du niveau d'activité physique en français
     */
    public final String name;

    /**
     * Constructeur privé de l'énumération.
     * 
     * @param s Le nom lisible du niveau d'activité physique
     */
    private NiveauActivitePhysique(String s) {
        this.name = s;
    }

    /**
     * Convertit une chaîne de caractères en valeur de l'énumération NiveauActivitePhysique.
     * La comparaison est insensible à la casse.
     * 
     * @param s La chaîne de caractères à convertir
     * @return La valeur de l'énumération correspondante
     * @throws IllegalArgumentException Si la chaîne ne correspond à aucune valeur de l'énumération
     */
    public static NiveauActivitePhysique type(String s) {
        for (NiveauActivitePhysique type : NiveauActivitePhysique.values()) {
            if (type.name.equalsIgnoreCase(s)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Aucun type correspondant pour le nom : " + s);
    }
}
