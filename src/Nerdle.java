
import extensions.File;
import extensions.CSVFile;

class Nerdle extends Program {

    boolean debug = false;
    String pseudo = "";
    int scoreTotal = 0;
    int motsTrouves = 0;
    String mode = "1";//mode 1 -> Histoire mode 2 ->Arcade

    CSVFile scores = loadCSV("./scores.csv");

    String[] topPseudos = new String[10];
    int[] topScores = new int[10];

    void clear() {
        println("\033[H\033[2J");
    }

    String intToString(int score) {
        return "" + score;
    }

    void afficher(String FILENAME) {
        clear();
        File f = newFile(FILENAME);
        while (ready(f)) {
            println(readLine(f));
        }
        println();
    }

    void retour() {//permet de retourner au menu principal
        String choix = readString();
        if (choix == "") {
            algorithm();
        }
    }

    void algorithm() {
        debug = false;
        afficher("Menu.txt");
        initTopScores(topPseudos, topScores);
        chargerTopScoresCSV(topPseudos, topScores);
        print("Meilleur joueur: ");
        if (topScores[0] != -1) {
            println(topPseudos[0] + " : " + topScores[0]);
        } else {
            println("Aucun score");
        }
        println("Appuyez sur 1 pour Jouer, 2 pour acceder aux Scores, 3 pour acceder aux Règles, 4 pour acceder au Dictionnaire,et 5 pour Quitter:");
        println("(Attention: Appuyer sur n'importe quelle autre touche dans le menu fermera le jeu !)");
        String choix = readString();

        if (equals(choix, "1")) {
            clear();
            boolean choisi = false;
            boolean choisis = false;
            while (!choisi) {
                println("choisissez le mode: Histoire/Arcade (entrer 1/2)");//choisi un mode arcade ou histoire
                mode = readString();                                           //par defaut histoire
                if (equals(mode, "1") || equals(mode, "2")) {
                    choisi = true;
                } else {
                    println("Erreur,veuillez réessayer");
                    choisi = false;
                }
            }
            while (!choisis) {
                println("Entrez votre pseudo :");//evite les pseudos vide
                pseudo = readString();
                if (length(pseudo) < 1) {
                    choisis = false;
                    println("Erreur,veuillez réessayer");
                } else {
                    choisis = true;
                }
            }

            scoreTotal = 0;
            motsTrouves = 0;
            nerdle();
        }

        if (equals(choix, "2")) {
            scores(); // demande le nbr de score a afficher puis affiche les scores
            println("Appuyez sur Entrée");
            retour();
        }

        if (equals(choix, "3")) {
            afficher("Règles.txt");
            retour();
        }

        if (equals(choix, "4")) {
            dico();
        }

        if (equals(choix, "5")) {
            clear();
        }
        if (equals(choix, "6")) {
            debug = true;
            clear();
            boolean choisi = false;
            while (!choisi) {
                println("choisissez le mode: Histoire/Arcade (entrer 1/2)");//choisi un mode arcade ou histoire
                mode = readString();                                           //par defaut histoire
                if (equals(mode, "1") || equals(mode, "2")) {
                    choisi = true;
                } else {
                    println("Erreur,veuillez réessayer");
                    choisi = false;
                }
            }
            println("Entrez votre pseudo :");
            pseudo = readString();
            scoreTotal = 0;
            motsTrouves = 0;
            nerdle();
        }
    }

    boolean comparerMot(String tentative, String reponse) {
        int bp = 0;
        for (int i = 0; i < length(tentative); i++) {
            if (charAt(tentative, i) == charAt(reponse, i)) {
                print("\033[32m" + charAt(tentative, i) + "\033[0m"); // Bien placé
                bp++;
            } else if (contains(reponse, substring(tentative, i, i + 1))) {
                print("\033[33m" + charAt(tentative, i) + "\033[0m"); // Présent mais mal placé
            } else {
                print("\033[31m" + charAt(tentative, i) + "\033[0m");
            }
        }
        println();
        if (bp == length(reponse)) {
            return true;
        } else {
            return false;
        }
    }

    int verifScore(int score) {
        if (score > 200) {
            return 7;
        } else if (score > 100) {
            return 6;
        } else {
            return 5;
        }
    }

    void nerdle() {
        clear();
        String reponse = "";
        boolean trouve = false;
        int essais = 0;
        if (equals(mode, "1")) {
            reponse = motAleatoire(verifScore(scoreTotal));
            println("mode histoire");
        } else if (equals(mode, "2")) {
            reponse = motAleatoire(random(5, 7));
            println("mode arcade");
        }

        if (debug) {
            println(reponse);
        }
        while (!trouve && essais < 6) {
            println("Mot de " + length(reponse) + " lettres :");
            String mot = readString();

            if (equals(mot, "quitter")) {
                algorithm();
                return;
            }

            if (length(mot) != length(reponse)) {
                println("Mot invalide, ce mot ne fait pas " + length(reponse) + " lettres");
            } else {
                int correct = 0;
                essais++;

                if (comparerMot(mot, reponse)) {
                    trouve = true;
                }
            }
        }

        if (trouve) {
            int bonus = 6 - essais;
            scoreTotal += 10 + bonus;
            motsTrouves++;

            println("Bravo !");
            println("Score : " + scoreTotal);
            initTopScores(topPseudos, topScores);
            chargerTopScoresCSV(topPseudos, topScores);
            if (topScores[0] != -1 && scoreTotal > topScores[0]) {
                println("Nouveau Record !!!");
            }

        } else {
            println("Perdu ! Mot : " + reponse);
        }

        println("Rejouer ? (o/n)");
        char c = readChar();

        if (c == 'o') {
            nerdle();
        } else {
            enregistrerScoreFinal(newScores(pseudo, scoreTotal, motsTrouves), pseudo);
            algorithm();
        }
    }

    void dico() {
        clear();
        println("Voici la liste de mots disponible:");
        Mots[] liste = Mots.values();
        println("MOTS DE 5 LETTRES :");
        for (int i = 0; i < length(liste); i++) {
            String mot = "" + liste[i];
            if (length(mot) == 5) {
                print(mot + " ");
            }
        }
        println();

        println("MOTS DE 6 LETTRES :");
        for (int i = 0; i < length(liste); i++) {
            String mot = "" + liste[i];
            if (length(mot) == 6) {
                print(mot + " ");
            }
        }
        println();

        println("MOTS DE 7 LETTRES :");
        for (int i = 0; i < length(liste); i++) {
            String mot = "" + liste[i];
            if (length(mot) == 7) {
                print(mot + " ");
            }
        }
        println();
        println("Appuyez sur Entrée pour quitter");
        retour();
    }

    String motAleatoire(int nbrLettres) {
        String mot = "";
        while (length(mot) != nbrLettres) {
            Mots[] liste = Mots.values();
            int i = (int) (random() * length(liste));
            mot = liste[i].name();
        }
        return mot;
    }

    Score newScores(String nom, int score, int motTrouve) {
        Score s = new Score();
        s.nom = nom;
        s.score = score;
        s.motsTrouves = motTrouve;
        return s;
    }

    void enregistrerScoreFinal(Score s, String pseudo) {
        String[][] data = new String[rowCount(scores) + 1][2];

        for (int i = 0; i < rowCount(scores); i++) {
            data[i][0] = getCell(scores, i, 0);
            data[i][1] = getCell(scores, i, 1);
        }

        data[rowCount(scores)][0] = s.nom;
        data[rowCount(scores)][1] = intToString(s.score);

        saveCSV(data, "scores.csv");
        scores = loadCSV("scores.csv");
    }

    void scores() {//afficher scores
        clear();
        println("Combien de scores voulez-vous afficher ?");
        println("(ex: 5, 10, 20)");
        int maxScores = 5;
        String saisie = readString();
        if (equals(saisie, "")) {
            saisie = "5";
        }
        if (maxScores > 0) {
            maxScores = stringToInt(saisie);
        }

        String[] tPseudos = new String[maxScores];
        int[] tScores = new int[maxScores];
        initTopScores(tPseudos, tScores);
        chargerTopScoresCSV(tPseudos, tScores);
        afficherTopScores(tPseudos, tScores);
        println("Appuyez sur Entrée pour quitter");
        retour();
    }

    void initTopScores(String[] pseudos, int[] scoresTab) {
        for (int i = 0; i < length(scoresTab); i++) {
            pseudos[i] = "";
            scoresTab[i] = -1;
        }
    }

    void ajouterTopScore(String[] pseudos, int[] scoresTab, String pseudo, int score) {
        for (int i = 0; i < length(scoresTab); i++) {
            if (scoresTab[i] == -1) {
                scoresTab[i] = score;
                pseudos[i] = pseudo;
                trierTopScores(pseudos, scoresTab);
                return;
            }
        }

        int idxMin = indiceMinTop(scoresTab);
        if (score > scoresTab[idxMin]) {
            scoresTab[idxMin] = score;
            pseudos[idxMin] = pseudo;
            trierTopScores(pseudos, scoresTab);
        }
    }

    int indiceMinTop(int[] scoresTab) {
        int min = scoresTab[0];
        int idx = 0;
        for (int i = 1; i < length(scoresTab); i++) {
            if (scoresTab[i] < min) {
                min = scoresTab[i];
                idx = i;
            }
        }
        return idx;
    }

    void trierTopScores(String[] pseudos, int[] scoresTab) {
        for (int i = 0; i < length(scoresTab) - 1; i++) {
            for (int j = i + 1; j < length(scoresTab); j++) {
                if (scoresTab[j] > scoresTab[i]) {
                    int ts = scoresTab[i];
                    scoresTab[i] = scoresTab[j];
                    scoresTab[j] = ts;

                    String tp = pseudos[i];
                    pseudos[i] = pseudos[j];
                    pseudos[j] = tp;
                }
            }
        }
    }

    void chargerTopScoresCSV(String[] pseudos, int[] scoresTab) {
        for (int i = 0; i < rowCount(scores); i++) {
            String p = getCell(scores, i, 0);
            int s = stringToInt(getCell(scores, i, 1));
            ajouterTopScore(pseudos, scoresTab, p, s);
        }
    }

    void afficherTopScores(String[] pseudos, int[] scoresTab) {
        println();
        print(" Meilleur joueur | ");
        if (scoresTab[0] != -1) {
            println(pseudos[0] + " : " + scoresTab[0]);
        } else {
            println("Aucun score");
        }
        println("---------------------------------------------");

        for (int i = 0; i < length(scoresTab); i++) {
            if (scoresTab[i] == -1) {
                break;
            }
            println((i + 1) + "| " + pseudos[i] + " : " + scoresTab[i]);
            println();
        }
        println();
    }

    void testClassement() {//test trier top score
        String[] pseudos = new String[4];
        int[] scores = new int[4];

        pseudos[0] = "A";
        scores[0] = 40;
        pseudos[1] = "B";
        scores[1] = 10;
        pseudos[2] = "C";
        scores[2] = 70;
        pseudos[3] = "D";
        scores[3] = 30;

        trierTopScores(pseudos, scores);

        assertEquals(70, scores[0]);
        assertEquals("C", pseudos[0]);

        assertEquals(40, scores[1]);
        assertEquals("A", pseudos[1]);

        assertEquals(30, scores[2]);
        assertEquals("D", pseudos[2]);

        assertEquals(10, scores[3]);
        assertEquals("B", pseudos[3]);
    }

    void testComparerMot() {
        boolean res1 = comparerMot("table", "table");
        assertEquals(true, res1);

        boolean res2 = comparerMot("table", "tiger");
        assertEquals(false, res2);

        boolean res3 = comparerMot("abcde", "fghij");
        assertEquals(false, res3);
    }

    void testAjouterTopScore() {
        String[] pseudos = new String[3];
        int[] scores = new int[3];
        initTopScores(pseudos, scores);

        ajouterTopScore(pseudos, scores, "A", 20);
        ajouterTopScore(pseudos, scores, "B", 50);
        ajouterTopScore(pseudos, scores, "C", 30);
        ajouterTopScore(pseudos, scores, "D", 40); // doit virer A

        assertEquals(50, scores[0]);
        assertEquals("B", pseudos[0]);

        assertEquals(40, scores[1]);
        assertEquals("D", pseudos[1]);

        assertEquals(30, scores[2]);
        assertEquals("C", pseudos[2]);
    }
}
