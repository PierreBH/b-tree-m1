package fr.miage.fsgbd;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Galli Gregory, Mopolo Moke Gabriel
 */
public class GUI extends JFrame implements ActionListener {
    TestInteger testInt = new TestInteger();
    BTreePlus<Integer> bInt;
    private JButton buttonClean, buttonRemove, buttonLoad, buttonSave, buttonAddMany, buttonAddItem, buttonRefresh, buttonParcoursSequentiel, buttonGenerateFile, buttonRechercheSequentielle;
    private JTextField txtNbreItem, txtNbreSpecificItem, txtU, txtFile, removeSpecific, numberToFind;
    private final JTree tree = new JTree();

    private List<Long> allAverageTime;
    private long minTimeToFoundSeq, maxTimeToFoundSeq, minTimeToFoundIndex, maxTimeToFoundIndex;
    private int nbFoundRechercheSequentielle, nbFoundRechercheIndex;

    public GUI() {
        super();
        build();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonLoad || e.getSource() == buttonClean || e.getSource() == buttonSave || e.getSource() == buttonRefresh) {
            if (e.getSource() == buttonLoad) {
                BDeserializer<Integer> load = new BDeserializer<Integer>();
                bInt = load.getArbre(txtFile.getText());
                if (bInt == null)
                    System.out.println("Echec du chargement.");

            } else if (e.getSource() == buttonClean) {
                if (Integer.parseInt(txtU.getText()) < 2)
                    System.out.println("Impossible de cr?er un arbre dont le nombre de clés est inférieur ? 2.");
                else
                    bInt = new BTreePlus<Integer>(Integer.parseInt(txtU.getText()), testInt);
            } else if (e.getSource() == buttonSave) {
                BSerializer<Integer> save = new BSerializer<Integer>(bInt, txtFile.getText());
            }else if (e.getSource() == buttonRefresh) {
                tree.updateUI();
            }
        } else {
            if (bInt == null)
                bInt = new BTreePlus<Integer>(Integer.parseInt(txtU.getText()), testInt);

            if (e.getSource() == buttonAddMany) {
                for (int i = 0; i < Integer.parseInt(txtNbreItem.getText()); i++) {
                    int valeur = (int) (Math.random() * 10 * Integer.parseInt(txtNbreItem.getText()));
                    boolean done = bInt.addValeur(valeur);

					/*
					  On pourrait forcer l'ajout mais on risque alors de tomber dans une boucle infinie sans "r?gle" faisant sens pour en sortir

					while (!done)
					{
						valeur =(int) (Math.random() * 10 * Integer.parseInt(txtNbreItem.getText()));
						done = bInt.addValeur(valeur);
					}
					 */
                }

            } else if (e.getSource() == buttonAddItem) {
                if (!bInt.addValeur(Integer.parseInt(txtNbreSpecificItem.getText())))
                    System.out.println("Tentative d'ajout d'une valeur existante : " + txtNbreSpecificItem.getText());
                txtNbreSpecificItem.setText(
                        String.valueOf(
                                Integer.parseInt(txtNbreSpecificItem.getText()) + 2
                        )
                );

            } else if (e.getSource() == buttonRemove) {
                bInt.removeValeur(Integer.parseInt(removeSpecific.getText()));
            }
            else if(e.getSource() == buttonParcoursSequentiel){
                List<Noeud<Integer>> feuilles = bInt.parcoursSequentiel();
                JDialog dialog = new JDialog(this, "Parcours seq des feuilles", true);

                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                AtomicInteger index = new AtomicInteger();
                JLabel label = new JLabel();
                label.setText("<html>Parcours seq des feuilles : <br/>");
                JScrollPane jScrollPane = new JScrollPane(label);
                jScrollPane.setPreferredSize(new Dimension(400,200));
                panel.add(jScrollPane, BorderLayout.NORTH);

                JButton button = new JButton();
                button.setText("Suivant");

                button.addActionListener(e1 -> {
                    label.setText(label.getText() + feuilles.get(index.get()).toString() + "<br/>");
                    index.addAndGet(1);
                });

                JButton closebutton = new JButton();
                closebutton.setText("Fermer");
                closebutton.addActionListener(e12 -> dialog.setVisible(false));

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(button);
                buttonPanel.add(closebutton);
                panel.add(buttonPanel, BorderLayout.SOUTH);

                dialog.add(panel);

                dialog.setSize(new Dimension(400, 300));
                dialog.setLocationRelativeTo(this);

                dialog.setVisible(true);
            }
            else if(e.getSource() == buttonGenerateFile){
                this.generer10kLignesInFile();
                this.readAndMakeTree();
            }
            else if(e.getSource() == buttonRechercheSequentielle){
                if (!Objects.equals(numberToFind.getText(), "")) {
                    this.nbFoundRechercheSequentielle = 0;
                    this.nbFoundRechercheIndex = 0;
                    this.minTimeToFoundSeq = 999999999;
                    this.maxTimeToFoundSeq = 0;
                    this.minTimeToFoundIndex = 999999999;
                    this.maxTimeToFoundIndex = 0;
                    this.allAverageTime = new ArrayList<>();
                    for(int i = 0; i < 50; i++){
                        this.generer10kLignesInFile();
                        this.readAndMakeTree();
                        this.calculateTimeMethodRechercheSequentielle();
                        this.calculateTimeMethodRechercheIndex();
                    }
                }
                this.displayStats();
            }
        }
        tree.setModel(new DefaultTreeModel(bInt.bArbreToJTree()));
        for (int i = 0; i < tree.getRowCount(); i++)
            tree.expandRow(i);

        tree.updateUI();
    }

    private void displayStats() {
        System.out.println("----- Recherches séquentielles -----");
        System.out.println("Temps min : "+this.minTimeToFoundSeq+" nanosecondes");
        System.out.println("Temps max : "+this.maxTimeToFoundSeq+" nanosecondes");
        Long avrgTime = 0L;
        for (Long time : allAverageTime)
            avrgTime += time;
        System.out.println("Temps moyen : "+(avrgTime/50)+" nanosecondes");
        System.out.println("----- Recherches par index -----");
        System.out.println("Temps min : "+this.minTimeToFoundIndex+" nanosecondes");
        System.out.println("Temps max : "+this.maxTimeToFoundIndex+" nanosecondes");
        Long avrgTimeIndex = 0L;
        for (Long time : allAverageTime)
            avrgTimeIndex += time;
        System.out.println("Temps moyen : "+(avrgTimeIndex/50)+" nanosecondes");
    }

    private void build() {
        setTitle("Indexation - B Arbre");
        setSize(760, 760);
        setLocationRelativeTo(this);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(buildContentPane());
    }

    public void generer10kLignesInFile() {
        try {
            FileWriter fileWriter = new FileWriter("10kLignes.txt");
            for (int i = 0; i < 10000; i++) {
                // générer un numéro social compris entre 0 et 10000
                int numeroSocial = (int) (Math.random() * 10000);
                String nom = Ligne.generateRandomFirstName();
                String prenom = Ligne.generatePrenom();
                fileWriter.write(numeroSocial + "," + nom + "," + prenom + "\n");
            }
            fileWriter.close();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readAndMakeTree() {
        try{
            File file = new File("10kLignes.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()){
                String line = scanner.nextLine().replace(" ", "");
                Ligne ligne = Ligne.convertToLigne(line);
                bInt.addValeur(ligne.getNumSecu());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void calculateTimeMethodRechercheSequentielle(){
        long startTime = System.nanoTime();
        this.rechercheSequentielleInFile(Integer.parseInt(numberToFind.getText()));
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        if (elapsedTime < this.minTimeToFoundSeq)
            this.minTimeToFoundSeq = elapsedTime;
        if (elapsedTime > this.maxTimeToFoundSeq)
            this.maxTimeToFoundSeq = elapsedTime;
        this.allAverageTime.add(elapsedTime);
    }

    public void calculateTimeMethodRechercheIndex(){
        long startTime = System.nanoTime();
        this.bInt.rechercheParIndex(Integer.parseInt(numberToFind.getText()));
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        if (elapsedTime < this.minTimeToFoundIndex)
            this.minTimeToFoundIndex = elapsedTime;
        if (elapsedTime > this.maxTimeToFoundIndex)
            this.maxTimeToFoundIndex = elapsedTime;
        this.allAverageTime.add(elapsedTime);
    }

    public boolean rechercheSequentielleInFile(int valueToFind){
        try{
            File file = new File("10kLignes.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()){
                String line = scanner.nextLine().replace(" ", "");
                Ligne ligne = Ligne.convertToLigne(line);
                if(valueToFind == ligne.getNumSecu())
                    return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private JPanel buildContentPane() {
        GridBagLayout gLayGlob = new GridBagLayout();

        JPanel pane1 = new JPanel();
        pane1.setLayout(gLayGlob);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 2, 0);

        JLabel labelU = new JLabel("Nombre max de cles par noeud (2m): ");
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        pane1.add(labelU, c);

        txtU = new JTextField("4", 7);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 2;
        pane1.add(txtU, c);

        JLabel labelBetween = new JLabel("Nombre de clefs a ajouter:");
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        pane1.add(labelBetween, c);

        txtNbreItem = new JTextField("10000", 7);
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        pane1.add(txtNbreItem, c);


        buttonAddMany = new JButton("Ajouter n elements aleatoires a l'arbre");
        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonAddMany, c);

        JLabel labelSpecific = new JLabel("Ajouter une valeur specifique:");
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(labelSpecific, c);

        txtNbreSpecificItem = new JTextField("50", 7);
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(txtNbreSpecificItem, c);

        buttonAddItem = new JButton("Ajouter l'element");
        c.gridx = 2;
        c.gridy = 3;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonAddItem, c);

        JLabel labelRemoveSpecific = new JLabel("Retirer une valeur specifique:");
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(labelRemoveSpecific, c);

        removeSpecific = new JTextField("54", 7);
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(removeSpecific, c);

        buttonRemove = new JButton("Supprimer l'element n de l'arbre");
        c.gridx = 2;
        c.gridy = 4;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonRemove, c);

        JLabel labelFilename = new JLabel("Nom de fichier : ");
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(labelFilename, c);

        txtFile = new JTextField("arbre.abr", 7);
        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(txtFile, c);

        JLabel labelNumberToFind = new JLabel("Nombre à trouver - recherche séquentielle et index : ");
        c.gridx = 0;
        c.gridy = 10;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(labelNumberToFind, c);

        numberToFind = new JTextField("", 7);
        c.gridx = 1;
        c.gridy = 10;
        c.weightx = 1;
        c.gridwidth = 1;
        pane1.add(numberToFind, c);

        buttonSave = new JButton("Sauver l'arbre");
        c.gridx = 2;
        c.gridy = 5;
        c.weightx = 0.5;
        c.gridwidth = 1;
        pane1.add(buttonSave, c);

        buttonLoad = new JButton("Charger l'arbre");
        c.gridx = 3;
        c.gridy = 5;
        c.weightx = 0.5;
        c.gridwidth = 1;
        pane1.add(buttonLoad, c);

        buttonClean = new JButton("Reset");
        c.gridx = 2;
        c.gridy = 6;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonClean, c);

        buttonRefresh = new JButton("Refresh");
        c.gridx = 2;
        c.gridy = 7;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonRefresh, c);


        //Bouton pour lancer la recherche sequentiel
        buttonParcoursSequentiel = new JButton("Parcours sequentiel");
        c.gridx = 2;
        c.gridy = 8;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonParcoursSequentiel, c);

        buttonGenerateFile = new JButton("Générer 10k lignes");
        c.gridx = 2;
        c.gridy = 9;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonGenerateFile, c);

        buttonRechercheSequentielle = new JButton("Recherche séquentielle et index");
        c.gridx = 2;
        c.gridy = 10;
        c.weightx = 1;
        c.gridwidth = 2;
        pane1.add(buttonRechercheSequentielle, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 400;       //reset to default
        c.weighty = 1.0;   //request any extra vertical space
        c.gridwidth = 4;   //2 columns wide
        c.gridx = 0;
        c.gridy = 11;

        JScrollPane scrollPane = new JScrollPane(tree);
        pane1.add(scrollPane, c);

        tree.setModel(new DefaultTreeModel(null));
        tree.updateUI();

        txtNbreItem.addActionListener(this);
        buttonAddItem.addActionListener(this);
        buttonAddMany.addActionListener(this);
        buttonLoad.addActionListener(this);
        buttonSave.addActionListener(this);
        buttonRemove.addActionListener(this);
        buttonClean.addActionListener(this);
        buttonRefresh.addActionListener(this);
        buttonParcoursSequentiel.addActionListener(this);
        buttonGenerateFile.addActionListener(this);
        buttonRechercheSequentielle.addActionListener(this);

        return pane1;
    }
}

