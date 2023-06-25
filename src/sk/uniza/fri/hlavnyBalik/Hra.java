package sk.uniza.fri.hlavnyBalik;

import sk.uniza.fri.objekty.Banan;
import sk.uniza.fri.objekty.Hamburger;
import sk.uniza.fri.objekty.IObjekt;
import sk.uniza.fri.objekty.Jablko;
import sk.uniza.fri.objekty.Jahoda;
import sk.uniza.fri.objekty.Kosik;
import sk.uniza.fri.objekty.Odpad;
import sk.uniza.fri.objekty.Pohar;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * 26. 4. 2022 - 17:18
 *Trieda Hra vytvára padajúce objekty, zapisuje skóre a ovláda plátno.
 * @author petok
 */
public class Hra {
    private static final int SIRKA_PLATNA = 700;
    private static final int VYSKA_PLATNA = 700;
    //poradie:kosik, pozadie, apple, strawberry, trash, hamburger, score, pohar, banan
    private static final ArrayList<String> CESTY = new ArrayList<>(Arrays.asList("C:\\Users\\petok\\IdeaProjects\\PadajuceObjekty\\src\\sk\\uniza\\fri\\objekty\\obrazky\\basket.png", "C:\\Users\\petok\\IdeaProjects\\PadajuceObjekty\\src\\sk\\uniza\\fri\\objekty\\obrazky\\pozadie.png", "C:\\Users\\petok\\IdeaProjects\\PadajuceObjekty\\src\\sk\\uniza\\fri\\objekty\\obrazky\\apple.png", "C:\\Users\\petok\\IdeaProjects\\PadajuceObjekty\\src\\sk\\uniza\\fri\\objekty\\obrazky\\strawberry.png", "C:\\Users\\petok\\IdeaProjects\\PadajuceObjekty\\src\\sk\\uniza\\fri\\objekty\\obrazky\\trash.png", "C:\\Users\\petok\\IdeaProjects\\PadajuceObjekty\\src\\sk\\uniza\\fri\\objekty\\obrazky\\hamburger.png", "C:\\Users\\petok\\IdeaProjects\\PadajuceObjekty\\src\\sk\\uniza\\fri\\hlavnyBalik\\score.txt", "C:\\Users\\petok\\IdeaProjects\\PadajuceObjekty\\src\\sk\\uniza\\fri\\objekty\\obrazky\\pohar.png", "C:\\Users\\petok\\IdeaProjects\\PadajuceObjekty\\src\\sk\\uniza\\fri\\objekty\\obrazky\\banan.png"));
    private int konstantaCasu;
    private Kosik kosik;
    private int skore;
    private int najSkore;
    private int casKosika;
    private Platno platno;
    private int cas;
    private int rychlost;

    public static int dajVyskuPlatna() {
        return Hra.VYSKA_PLATNA;
    }
    public static int dajSirkuPlatna() {
        return Hra.SIRKA_PLATNA;
    }

    public Hra() {
        this.cas = 20;
        this.casKosika = 0;
        this.nacitajSkore();
        try {
            this.kosik = new Kosik(15, 300, 550, Hra.CESTY.get(0));
            this.platno = new Platno(Hra.dajSirkuPlatna(), Hra.dajVyskuPlatna(), this, this.kosik, Hra.CESTY.get(1));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + "\noprav cestu a skús znova", "Error", JOptionPane.WARNING_MESSAGE);
            System.exit(1);
        }
        this.hraj();
    }
    private void hraj() {
        /*zaciatok kazdej hry- nastaví sa rýchlosť padania, prepíše skóre, obnoví dĺžku košíka*/
        this.konstantaCasu = 50;
        this.rychlost = 1;
        this.skore = 0;
        this.cas = 30;
        this.casKosika = 0;
        this.kosik.zmensiSa();
        this.nacitajSkore();
        this.vytvorNovyObjekt();
        this.platno.prepisSkore();
    }

    private void vytvorNovyObjekt() {
        /*každý objekt sa vytvára s určitou náhodnosťou, Hra mu určí rýchlosť aj pozíciu*/
        Random rand = new Random();
        int i = rand.nextInt(20);
        int konstantaPohybu = rand.nextInt(2) + this.rychlost;
        int surX = rand.nextInt(Hra.dajSirkuPlatna() - 100) + 30;
        try {
            if (i < 4) {
                Jablko jabko = new Jablko(konstantaPohybu, surX, Hra.CESTY.get(2));
                this.platno.pridajObjekt(jabko);
            } else if (i < 8) {
                Jahoda jahoda = new Jahoda(konstantaPohybu, surX, Hra.CESTY.get(3));
                this.platno.pridajObjekt(jahoda);
            } else if (i < 10) {
                Banan banan = new Banan(konstantaPohybu, surX, Hra.CESTY.get(8));
                this.platno.pridajObjekt(banan);
            } else if (i < 14) {
                Pohar pohar = new Pohar(konstantaPohybu, surX, Hra.CESTY.get(7));
                this.platno.pridajObjekt(pohar);
            } else if (i < 19) {
                Odpad odpad = new Odpad(konstantaPohybu, surX, Hra.CESTY.get(4));
                this.platno.pridajObjekt(odpad);
            } else {
                Hamburger ham = new Hamburger(konstantaPohybu + 1, surX, Hra.CESTY.get(5));
                this.platno.pridajObjekt(ham);
            }
        } catch (IOException ex) {
            this.platno.showError(ex.getMessage());
        }
    }

    public int getSkore() {
        return this.skore;
    }
    public int getNajSkore() {
        return this.najSkore;
    }
    public void tik() {
        /*tik sa prijíma od timerListenera, Hra skontroluje každý objekt či nekoliduje s košíkom alebo sa dostal mimo hernej plochy*/
        if (this.platno.isPauza()) {
            return;
        }
        ArrayList<IObjekt> padajuce = this.platno.dajObjekty();
        for (IObjekt objekt : padajuce) {
            this.skontrolujObjekt(objekt);
        }
        if (this.casKosika > 0) {
            this.casKosika--;
            if (this.casKosika == 0) {
                this.kosik.zmensiSa();
            }
        }
        if (this.cas - 1 <= 0) {
            this.vytvorNovyObjekt();
            this.cas = this.konstantaCasu;
        } else if (this.cas == this.konstantaCasu / 2 && this.skore > 25) {
            this.vytvorNovyObjekt();
            this.cas--;
        } else {
            this.cas -= 1;
        }
        for (IObjekt objekt : this.platno.dajObjekty()) {
            objekt.hybSa();
        }
        this.platno.prekresli();
    }

    private void skontrolujObjekt(IObjekt objekt) {
        /*pomocná metóda pre tik(), kontroluje či objekt či nekoliduje s košíkom alebo sa dostal mimo hernej plochy*/
        int surKosikX = this.kosik.getSurX();
        int surKosikY = this.kosik.getSurY();
        int surObjektX = objekt.getSurX();
        int surObjektY = objekt.getSurY();
        int dlzkaKosika = this.kosik.getDlzka();
        if ((surObjektY >= surKosikY - 10 && surObjektY < surKosikY + 5 && surObjektX > surKosikX && surObjektX < surKosikX + dlzkaKosika)) {
            if (objekt.siZly()) {
                this.koniecHry();
            } else {
                int predSkore = this.skore;
                this.skore += objekt.hodnota();
                if (this.skore % 20 >= 0 && this.skore % 20 <= 2 && predSkore % 20 >= 17) {
                    this.rychlost++;
                    this.konstantaCasu -= 2;
                }
                this.platno.prepisSkore();
                if (objekt.hodnota() == 3) {
                    this.kosik.predlzSa();
                    this.casKosika = 350;
                }
                this.platno.odstranObjekt(objekt);
            }
            if (surObjektY > Hra.dajVyskuPlatna()) {
                this.platno.odstranObjekt(objekt);
            }
        }
    }

    private void koniecHry() {
        /*ukončí hru, uloží skóre a začne novú hru*/
        this.platno.koniecHry();
        this.ulozSkore();
        this.hraj();
    }
    private void nacitajSkore() {
        /*skúsi nájsť textový súbor, ak v ňom niečo je zapíše to do najSkore, ak neexistuje dokument, vytvorí nový*/
        try {
            File subor = new File(Hra.CESTY.get(6));
            Scanner skener = new Scanner(subor);
            if (skener.hasNextInt()) {
                this.najSkore = skener.nextInt();
            } else {
                this.najSkore = 0;
            }
            skener.close();
        } catch (FileNotFoundException e) {
            this.najSkore = 0;
            File subor = new File(Hra.CESTY.get(6));
            try {
                //noinspection ResultOfMethodCallIgnored
                subor.createNewFile();
            } catch (IOException ex) {
                this.platno.showError(ex.getMessage());
            }
        }
    }
    public void ulozSkore() {
        /*porovna ci aktuálne skóre je väčšie ako najSkore, ak áno tak prepíše*/
        if (this.skore > this.najSkore) {
            File subor = new File(Hra.CESTY.get(6));
            try (PrintWriter zapisovac = new PrintWriter(subor)) {
                zapisovac.write(String.valueOf(this.skore));
            } catch (FileNotFoundException e) {
                this.platno.showError(e.getMessage());
            }
        }
    }

}
