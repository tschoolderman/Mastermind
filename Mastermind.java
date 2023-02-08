package mastermind;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

// Class altijd met CamelCase
public class Mastermind {
    
	static DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
	static String currentWorkingDirectory = System.getProperty("user.dir");  // getcwd() van de gebruiker
    static File logBestand = new File(currentWorkingDirectory + File.separator + df.format(new Date()) + "_Mastermind_log.txt");  // concat cwd met file separator is '\'
	
    // Method altijd met snakeCase
	public static void main(String[] args) {
		// TODO controleren op juiste input, wanneer int en string
		// TODO method voor het beter resultaten vergelijken
		// TODO menu maken om files te laden. keuze: laden of nieuw -> bestand lezen -> waarde van poging/code/feedback opnieuw opslaan en door
		// TODO files schrijven/laden
		// TODO unieke datum/tijd per filenaam geven

		Scanner scanner = new Scanner(System.in);  // creeër scanner om stdin van terminal te lezen
		maakFile();  // creeërt logBestand indien deze nog niet bestaat

		int aantalKansen = 2;  // Aantal gegeven kansen door de opdracht
		   
		while (true) {
			ArrayList<String> actieveSpelers = aantalSpelers(scanner);  // method om aantal speler + naam te krijgen
			
			// Onderstaande 4 regels worden 1 keer laten zien 
			System.out.println("\nStart spel");
			System.out.println("De code bestaat uit 4 letters in het bereik van a t/m f.\n");

			Hashtable<String, Integer> resultaatDict = new Hashtable<String, Integer>();
			
			// voor elke actieve speler wordt er een ronde gedaan om de code te raden
			for (String naam : actieveSpelers) {
				int aantalPogingen = startSpel(aantalKansen, naam);
				
				int resultaat = aantalKansen - aantalPogingen;
				resultaatDict.put(naam, resultaat);
			}
			
			// loop om de laagste waarde van de dictionary te krijgen
			// werkt niet bij gelijk spel of verlies van alle spelers
			// wanneer er meer dan 1 speler is wordt er een winnaar uitgeroepen
			if (actieveSpelers.size() > 1) {
				Entry<String, Integer> minstePogingen = null;
				for (Entry<String, Integer> entry : resultaatDict.entrySet()) {
				    if (minstePogingen == null || minstePogingen.getValue() > entry.getValue()) {
				        minstePogingen = entry;
				    }
				}
				System.out.println("De winnaar is: " + minstePogingen.getKey());
			// wanneer er maar 1 speler is wordt deze regel uitgevoerd
			} else {
				System.out.println("Tot de volgende keer.");
			}
			
			// scanner wordt gesloten en spel beeindigt na succesvolle loop
			System.out.println("Bedankt voor het spelen.");
			scanner.close();  // scanner sluiten om "resource leak" te voorkomen (memory consumption)
			break;  // breekt de eerste while-loop en eindigt het programma
			}
		}
	
	
	/** Methode om de code te genereren */
	public static String genereerRandomCode() {
		String karakters = "abcdef";  // Gegeven data van de opdracht
		String code = "";  // variabele om de code op te slaan
		Random random = new Random();

		// for-loop om een 4-alphanumerische code te creeëren
		for (int i = 0; i < 4; i++) {
			int index = random.nextInt(karakters.length());
			// sla de een letter op in de variabele na iedere iteratie (4x)
			code += karakters.charAt(index);
		}
		return code;
	}
	
	
	/** Methode om het aantal spelers te krijgen per naam <p> 
	 * Gebruikt de input van de Scanner onder de main voor de method
	 */
    public static ArrayList<String> aantalSpelers(Scanner scanner) {
        System.out.print("Hoeveel spelers zijn er: ");
        int spelers = scanner.nextInt(); // leest de input als een int
        ArrayList<String> speler = new ArrayList<String>();
        // Een for loop om de naam van elke speler te krijgen
        for (int i = 0; i < spelers; i++) {
            System.out.print("Hoe heet speler" + (i+1) + ": ");
            String naam = scanner.next();  // nextLine() skipt de iteratie
            // Sla elke naam van de spelers op in de ArrayList
            speler.add(naam);
        }
        return speler;
    }
    
    
    /** De main loop van het spel<p>
     * Er wordt per speler een randomcode gegenereerd. Elke speler krijgt de volledige ronde.<p>
     * Wanneer de eerste speler klaar is mag de volgende.<p>
     * De code wordt gelijk gecontroleerd op input.
     * */
    public static int startSpel(int aantalKansen, String naam) {
    	Scanner scanner = new Scanner(System.in);
    	
    	// code hier genereren, zodat elke speler een andere code heeft
    	String code = genereerRandomCode();  // initialiseer de code
		
    	System.out.println("Druk \"q\" om te stoppen met spelen.");
    	naarBestandMetPrint("Het spel voor \"" + naam + "\" wordt gestart!" +
    				"\nVeel succes!\n");

		while ((aantalKansen > 0)) {
			System.out.println(code);
			
			naarBestandZonderPrint("code: " + code);
			naarBestandMetPrint("Speler: " + naam);
			naarBestandMetPrint("Je hebt nog " + aantalKansen + " kansen over.");
			
			System.out.print("Doe een gok: ");  // print, zodat de code erachter wordt ingevuld
			String spelerGok = scanner.nextLine().toLowerCase();  // transformeer naar lowercase
			
			naarBestandZonderPrint("Doe een gok: " + spelerGok);
			
			String feedback = "";  // variabele om de user input te controleren
			
			// controleer elke loop of de speler nog wil spelen
			if (!spelerGok.equals("q")) {
				// controleer of de speler wel 4 letters typt, m.u.v. "q"
				if (spelerGok.length() != 4) {
					System.out.println("\nDe code moet 4 karakters lang zijn.\n");
					continue;
				}
					

				// deze loop kan nog 3x gemaakt worden, maar heeft hetzelfde effect als de volgende for-loop
				if (spelerGok.charAt(0) == code.charAt(0)) {
					System.out.println("Goed gedaan!");
				} else {
					System.out.println("Helaas.");
				}

				// Korte for-loop om de code tegen de user input te controleren
				for (int i = 0; i < code.length(); i++) {
					if (spelerGok.charAt(i) == code.charAt(i)) {
						feedback += "+";
					} else if (code.contains(Character.toString(spelerGok.charAt(i)))) {
						feedback += "?";
					} else {
						feedback += "_";
					}
				}
				
				aantalKansen--;  // Iedere loop vermindert het aantal kansen met 1

				// Als de code gekraakt is
				if (feedback.equals("++++")) {
					naarBestandMetPrint("Feedback: " + feedback +
								"\nGefeciliteerd! De code is inderdaad: " + code +
								"\n-------------\n");
					break;
				// volgende beurt
				} else {
					naarBestandMetPrint("Feedback: " + feedback +
								"\n-------------\n");
				}

				// als de kansen op zijn
				if (aantalKansen == 0) {
					naarBestandMetPrint("\nGame over...\n" +
								"De code was: " + code +
								"\n-------------\n");
				}

			} else {
				System.out.println("Bedankt voor het spelen.");
				scanner.close();  // scanner sluiten ivm bad practice
				System.exit(0);  // dit sluit scanner ook, maar is bad practice
			}
		}
//		scanner.close();  // wanneer ik de scanner hier sluit krijg je error ook al wordt de loop opnieuw gedaan
		// Eclipse zegt resource leak, maar omdat de method onder main wordt uitgevoerd en scanner wordt daar gesloten?
		return aantalKansen;
    }
    
    
    /** Kijken of er een log is en maken indien nodig */
    public static void maakFile() {
        try {
            // createNewFile controleert of het bestand al bestaat, zo niet maakt het
            if (logBestand.createNewFile()) {
              System.out.println("Log gemaakt: " + logBestand.getName());
            } else {
              System.out.println("Log is al aanwezig");
            }
          } catch (IOException error) {
            System.out.println("An error occurred.");
            error.printStackTrace();
          }
    }
    
    /** methode om te schrijven en te printen naar het scherm tegelijk */
 	public static void naarBestandMetPrint(String text) {
 		// try block met exception
 		try {
 		    PrintWriter pw = new PrintWriter(new FileWriter(logBestand, true));
 		    pw.println(text);
 		    System.out.println(text);
 		    pw.close();
 		} catch (IOException e) {
 		    e.printStackTrace();
 		}
 	}
 	
 	/** methode om te schrijven naar bestand zonder printen naar scherm */
 	public static void naarBestandZonderPrint(String text) {
 		// try block met exception
 		try {
 		    PrintWriter pw = new PrintWriter(new FileWriter(logBestand, true));
 		    pw.println(text);
 		    pw.close();
 		} catch (IOException e) {
 		    e.printStackTrace();
 		}
 	}
 }

