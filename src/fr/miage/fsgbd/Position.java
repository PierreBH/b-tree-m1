package fr.miage.fsgbd;

public class Position implements java.io.Serializable {
    private int niveau;
    private int index;

    public Position(int niveau, int index) {
        this.niveau = niveau;
        this.index = index;
    }

    public int getNiveau() {
        return niveau;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Position{" +
                "niveau=" + niveau +
                ", index=" + index +
                '}';
    }
}