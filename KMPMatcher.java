package Esame;
import java.util.ArrayList;
import java.util.List;

public class KMPMatcher {

    // creo l'array LPS per evitare confronti ripetuti
    // ogni elemento lps[i] rappresenta la lunghezza del prefisso più lungo che è anche suffisso nella sottostringa pattern[0..i]
    private int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0; // lunghezza del prefisso-suffisso più lungo trovato finora
        int i = 1; // indice per scorrere l'input patendo dal secondo carattere

        // nessun prefisso-suffisso possibile per il primo carattere
        lps[0] = 0;

        while (i < m) {
            // verifico se il prefisso della sottostringa considerata del pattern è anche suffisso
            if (pattern.charAt(i) == pattern.charAt(len)) {
                // len rappresenta un indice (che va da 0 a len-1), incrementiamo di 1 per memorizzare in lps la lunghezza effettiva del prefisso-suffisso
                len++;
                lps[i] = len;
                // incremento i per continuare la ricerca del pattern
                i++;
            } else {
                // se si verifica un mismatch, invece di ricominciare da capo, ripartiamo dall'indice dell'ultimo prefisso-suffisso più lungo trovato (-1 perchè altrimenti len indica la lunghezza, non l'indice)
                if (len != 0) {
                    len = lps[len - 1]; // torno indietro nel pattern
                } else {

                    lps[i] = 0;
                    i++;
                }
            }
        }
        // resituisco l'array popolato con i salti intelligenti per ogni prefisso-suffisso nel pattern
        return lps;
    }

    // cerca tutte le occorrenze del pattern in una stringa di input
    // restituisce una lista di indici, cioè il punto di inizio dei pattern riconosciuti el testo
    public List<Integer> search(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // pattern vuoto, testo vuoto o pattern più lungo del testo
        if (m == 0 || n == 0 || m > n) return result;

        // calcolo l'LPS in base al pattern specificato
        int[] lps = computeLPSArray(pattern);

        int i = 0; // indice nel testo
        int j = 0; // indice nel pattern

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            // match completo del pattern
            if (j == m) {
                result.add(i - j); // pattern trovato
                j = lps[j - 1];    // cerco match sovrapposti
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1]; // salta nel pattern
                } else {
                    i++;
                }
            }
        }
        // restituisce la lista delle posizioni in cui il pattern è stato trovato
        return result;
    }
}
