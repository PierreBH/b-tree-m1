package fr.miage.fsgbd;

import java.io.Serializable;

public class Ligne implements Serializable {
    int numSecu;
    String nom;
    String prenom;

    public Ligne(int numSecu, String nom, String prenom) {
        this.numSecu = numSecu;
        this.nom = nom;
        this.prenom = prenom;
    }

    public int getNumSecu() {
        return numSecu;
    }

    //créer une fonction qui génère un nom aléatoire
    public static String generatePrenom(){
        String[] names = {"Jean", "Paul", "Jacques", "Pierre", "Marie", "Anne", "Julie", "Sophie", "Alice", "Bob", "Claire", "David", "Eve", "François", "Gérard", "Hector", "Isabelle", "Jules", "Kévin", "Léa", "Maurice", "Nathalie", "Olivier", "Pascal", "Quentin", "Romain", "Sylvie", "Thierry", "Ulysse", "Victor", "William", "Xavier", "Yves", "Zoé"};
        int random = (int) (Math.random() * names.length);
        return names[random];
    }

    // créer une fonction qui génère des prénoms aléatoires à partir d'une liste de prénoms
    public static String generateRandomFirstName() {
        String[] firstNames = {"Smith", "Doe", "Williams", "Brown"};
        int randomIndex = (int) (Math.random() * firstNames.length);
        return firstNames[randomIndex];
    }
}
