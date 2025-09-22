package Esame;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String textFile = "src/Esame/testKMP.txt";

        // Chiede all'utente di inserire il pattern
        Scanner scanner = new Scanner(System.in);
        System.out.print("Inserisci il pattern da cercare: ");
        String pattern = scanner.nextLine();

        try {
            // Leggi tutto il contenuto dei file, rimuovendo eventuali ritorni a capo
            String text = Files.readString(Paths.get(textFile)).replaceAll("\\r?\\n", "");

            if (pattern.isEmpty()) {
                System.out.println("Errore: il pattern è vuoto.");
                return;
            }

            if (text.isEmpty()) {
                System.out.println("Errore: il testo da analizzare è vuoto.");
                return;
            }

            if (pattern.length() > text.length()) {
                System.out.println("Errore: il pattern è più lungo del testo.");
                return;
            }

            // Esegui la ricerca con KMP
            KMPMatcher matcher = new KMPMatcher();
            List<Integer> positions = matcher.search(text, pattern);

            if (positions.isEmpty()) {
                System.out.println("Pattern non trovato.");
            } else {
                System.out.print("Pattern trovato alle posizioni: ");
                for (int pos : positions) {
                    System.out.print(pos + " ");
                }
                System.out.println();
            }

        } catch (IOException e) {
            System.err.println("Errore nella lettura dei file: " + e.getMessage());
        }
    }
}

