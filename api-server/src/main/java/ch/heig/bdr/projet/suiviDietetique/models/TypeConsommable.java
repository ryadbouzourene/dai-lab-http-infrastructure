package ch.heig.bdr.projet.suiviDietetique.models;

public enum TypeConsommable {
    ALIMENT("ALIMENT"),
    BOISSON("BOISSON");

    public final String name;

    private TypeConsommable(String s){
        this.name = s ;
    }

    public static TypeConsommable type(String s){
        for (TypeConsommable type : TypeConsommable.values()) {
            if (type.name.equalsIgnoreCase(s)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Aucun type correspondant pour le nom : " + s);
    }
}
