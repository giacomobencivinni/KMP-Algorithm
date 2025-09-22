
# Obiettivo progetto 
Implementare in linguaggio Java un sistema che consenta di cercare, in modo efficiente e affidabile, tutte le occorrenze di un pattern fornito in input, restituendo tutte le posizioni nel testo in cui esso compare. Per raggiungere questo obiettivo si è utilizzato un algoritmo KMP (Knuth-Morris-Pratt): un algoritmo di pattern matching progettato per trovare occorrenze di un pattern $P$ di lunghezza $m$ all'interno di un testo $T$ di lunghezza $n$, con $n \gg m$.
### Motivazione scelta dell'algoritmo KMP 
Il principale vantaggio di questo algoritmo è la sua capacità di scansionare il testo in modo intelligente senza riconsiderare i caratteri del testo già esaminati in caso di mismatch con il pattern. Questo accorgimento permette di ottenere una complessità computazionale lineare $O(n+m)$, rispetto all'approccio brute-force in cui la riesaminazione dei caratteri comporta un costo computazionale di $O(mn)$ confronti. 
### Requisiti 
1. Efficienza per testi molto lunghi.
2. Evitare ripetizioni di confronti già effettuati.
3. Gestire correttamente casi in cui il pattern si sovrappone nel testo.
4. Ottimizzare il confronto per evitare di ricominciare da capo inutilmente dopo ogni carattere non corrispondente.
5. Dato un testo e un pattern in input, restituire in output tutte le posizioni del testo in cui il pattern inizia.
6. Il confronto deve essere case-sensitive
### Scelta di implementazione: confronto tra DFA e array LPS
Una delle prime decisioni progettuali è stata quella di valutare quale modello teorico e pratico utilizzare per effettuare la ricerca del pattern: concettualmente l'algoritmo KMP si basa sull'utilizzo di un DFA (Automa a Stati Finiti), costruito in base al pattern da riconoscere che data una sequenza di caratteri, transita attraverso gli stati in base ai simboli letti, fino a riconoscere (o meno) il pattern. In caso di mismatch, il DFA permette di mantenere l'informazione sulla porzione di pattern riconosciuta fino a quel momento. 

Tuttavia, l'implementazione esplicita del DFA presenta due criticità:
- Richiede la costruzione esplicita di una tabella delle transizioni, in cui per ogni stato e per ogni simbolo dell’alfabeto si deve definire lo stato successivo. Considerato $Σ$ la dimensione dell’alfabeto, allora la costruzione del DFA comporta un costo spaziale di $O(m × |Σ|)$. Per alfabeti molto estesi, questa soluzione risulta poco efficiente.
- È poco flessibile: nel caso volessimo modificare il pattern da riconoscere, bisognerebbe rigenerare l’intero automa e la relativa tabella di transizione.

Per queste ragioni, è stato preferito un approccio alternativo che simula il comportamento del DFA in modo implicito: l'array LPS (Longest-Prefix-Suffix).
- Complessità spaziale pari a $O(m)$
- Ogni indice i contiene la lungheza del massimo prefisso-suffisso, simulando in modo efficiente le transizioni del DFA in caso di mismatch

# Funzionamento dell’algoritmo KMP con array LPS
L’algoritmo KMP opera in due fasi principali:
1. **Preprocessing del pattern (costruzione dell’array LPS):**  
   In questa fase, viene calcolato per ciascun carattere del pattern il valore LPS corrispondente, ovvero la lunghezza del prefisso più lungo che è anche un suffisso della sottostringa considerata. Questo array consente di evitare, durante la fase di confronto, di riesaminare caratteri già confrontati precedentemente in caso di mismatch.

2. **Ricerca nel testo:**  
   Si scorre il testo carattere per carattere, confrontandolo con il pattern. In caso di corrispondenza si avanza; in caso di mismatch, l’array LPS permette di saltare direttamente alla posizione utile successiva nel pattern, riducendo drasticamente i confronti.

### Gestione pattern sovrapposti
Un elemento particolarmente interessante è la gestione automatica dei pattern sovrapposti. Dopo che un’occorrenza completa del pattern è stata trovata, l’algoritmo non riparte da zero, ma utilizza ancora una volta l’array LPS per determinare se ci sono porzioni del pattern già verificate che possono corrispondere a una nuova occorrenza che inizia prima della fine di quella appena riconosciuta. Questo meccanismo consente di rilevare pattern anche in presenza di sovrapposizioni.
Questo è reso possibile dall’istruzione $j = lps[j - 1]$, che aggiorna correttamente l’indice `j` nel pattern per continuare il confronto da un punto coerente, senza tornare all’inizio.

### Gestione case-sensitive
L'algoritmo KMP è già di per se case-sensitive in quanto il codice confronta i codici Unicode e non apporta trasformazioni alle stringhe prese in analisi: $text.charAt(i) == pattern.charAt(j)$

# Struttura del programma
Il progetto è composto da due classi principali:
- `KMPMatcher`: implementa l’algoritmo KMP. La funzione computeLPSArray() costruisce l’array LPS, mentre la funzione search() esegue la ricerca vera e propria all’interno del testo e restituisce una lista delle posizioni in cui il pattern compare.
- `Main`: gestisce l’interazione con l’utente. Richiede in input il pattern da cercare, legge un file di testo, esegue la ricerca tramite `KMPMatcher` e stampa le posizioni trovate.

### `computeLPSArray()`
Questo metodo calcola per ciascun carattere del pattern il valore LPS corrispondente:
- $len$: rappresenta la lunghezza del prefisso-suffisso più lungo trovato finora
- $i$: indice per scorrere il pattern partendo dal secondo carattere ($i = 1$)
Il primo elemento viene sempre inizializzato a zero ($lps[0] = 0$) poiché non può esistere un prefisso-suffisso per un singolo carattere. 
Il cuore dell'algoritmo di preprocessing è il ciclo $while (i < m)$ che gestisce due casi:
- Match: quando $pattern.charAt(i) == pattern.charAt(len)$, si incrementa $len$ e si assegna $lps[i] = len$
- Mismatch: quando i caratteri non corrispondono, si utilizza il "salto intelligente" con $len = lps[len - 1]$ invece di ricominciare da capo

### `search(String text, String pattern)`
Il codice utilizza due indici: $i$ per scorrere il testo, $j$ per scorrere il pattern. All'inizio del metodo si effettua il controllo dei casi limite:
- Pattern vuoto: $m == 0$ restituisce una lista vuota
- Testo vuoto: $n == 0$ restituisce una lista vuota
- Pattern più lungo del testo: $m > n$ restituisce una lista vuota
- Nessuna corrispondenza: il metodo restituisce una lista vuota che viene gestita nel `Main` con il messaggio "Pattern non trovato"
```java
if (m == 0 || n == 0 || m > n) return result;
```

Il ciclo principale `while (i < n)` gestisce tre scenari:
1. Caratteri corrispondenti: $text.charAt(i) == pattern.charAt(j)$. In questo caso si avanza in entrambe le stringhe.
2. Pattern completo trovato: $j == m$. Si aggiunge la posizione $i - j$ alla lista risultati
3. Mismatch: si utilizza l'array LPS per il salto con $j = lps[j - 1]$

# Complessità temporale
Dal codice visto precedentemente possiamo fare le seguenti considerazioni: 

Durante la fase di costruzione dell'array LPS:
- L'indice $i$, avanza in modo costante senza tornare mai indietro. Quindi ogni carattere del pattern viene analizzato al massimo una volta. 
- L'indice $len$ può subire delle regressioni quando si verifica un mismatch, ma in ogni caso il suo valore viene aggiornato utilizzando valori già calcolati nell'array LPS quindi il costo dell'operazione consiste solamente nell'accesso all'array: $O(1)$

In conclusione, ogni carattere del pattern può essere processato al massimo due volte: una volta durante il primo confronto e una seconda volta in seguito a un salto causato da un mismatch. Quindi, la complessità temporale di questa fase è pari a $O(2m) = O(m)$

Durante la fase di ricerca: 
- Si scorre ogni carattere del testo in cerca di match con il pattern. Quindi, la complessità temporale è pari a $O(n)$

Complessità temprale totale pari a: $O(n+m)$

# Complessità spaziale 
L’unico spazio ausiliario significativo ai fini dell'algoritmo è l’array $lps[]$ di dimensione $m$, da cui deriva una complessità spaziale pari a $O(m)$.

Inoltre, se consideriamo anche l'output, il programma utilizza una lista per memorizzare le posizioni dei match trovati; possiamo considerare una complessità spaziale di $O(k)$, dove $k$ corrisponde al numero di match trovati. Nel caso pessimo $k = n$.


