package ch.heig.bdr.projet.suiviDietetique.models;

/**
 * Énumération représentant les différents types de certificats médicaux.
 * Ces certificats définissent les qualifications spécifiques des infirmiers
 * et sont utilisés pour déterminer leurs domaines de compétence.
 */
public enum Certificat {
    /** Certificat pour les soins généraux */
    SOINSGENERAUX("Soins généraux"), 
    /** Certificat pour les soins intensifs */
    SOINSINTENSIFS("Soins intensifs"), 
    /** Certificat pour les soins en anesthésie */
    SOINSANESTHESIE("Soins anesthésie"),
    /** Certificat pour les soins oncologiques */
    SOINSONCOLOGIQUE("Soins oncologique"),
    /** Certificat BLS-AED (Basic Life Support - Automated External Defibrillator) */
    BLSAED("bls-aed"),
    /** Certificat pour les soins diabétologiques */
    DIABETOLOGIQUE("diabétologique");

    /** Nom complet du certificat tel qu'affiché dans l'interface */
    public String name;

    /**
     * Constructeur privé pour initialiser un certificat avec son nom.
     * 
     * @param s Le nom complet du certificat
     */
    private Certificat(String s){
        this.name = s;
    }

    /**
     * Recherche un certificat par son nom.
     * La recherche est insensible à la casse.
     * 
     * @param s Le nom du certificat à rechercher
     * @return Le certificat correspondant au nom
     * @throws IllegalArgumentException Si aucun certificat ne correspond au nom donné
     */
    public static Certificat certificat(String s){
        for (Certificat certificat : Certificat.values()) {
            if (certificat.name.equalsIgnoreCase(s)) {
                return certificat;
            }
        }
        throw new IllegalArgumentException("Aucun certificat correspondant pour le nom : " + s);
    }
}
