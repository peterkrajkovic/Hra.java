package sk.uniza.fri.hlavnyBalik;

import sk.uniza.fri.objekty.IObjekt;
import sk.uniza.fri.objekty.Kosik;
import sk.uniza.fri.objekty.Obrazok;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 26. 4. 2022 - 17:18
 *Trieda Platno reprezentuje grafické rozhranie hry, má svoj atribút zoznam objektov ktoré prekresľuje, obsahuje časovač, ktorým sa riadi trieda Hra.
 * @author petok
 */
public class Platno {
    private final JLabel najSkore;
    private ArrayList<IObjekt> objekty;
    private ArrayList<IObjekt> vymazane;
    private final Hra hra;
    private Timer timer;
    private Obrazok pozadie;
    private final MojCanvas canvas;
    private final Kosik kosik;
    private boolean pauza;
    private final JLabel skore;
    //private final JPanel stredny;

    public Platno(int sirka, int vyska, Hra hra, Kosik kosik, String cestaKPozadiu) {
        this.hra = hra;
        this.objekty = new ArrayList<>();
        this.vymazane = new ArrayList<>();
        this.kosik = kosik;
        this.pauza = false;
        try {
            this.pozadie = new Obrazok(cestaKPozadiu);
            this.pozadie.zmenVelkost(Math.max(Hra.dajSirkuPlatna(), Hra.dajVyskuPlatna()));
        } catch (IOException ex) {
            this.showError(ex.getMessage() + " pozadie");
            this.ukonci();
        }
        JFrame hlavnyPanel = new JFrame();
        hlavnyPanel.setSize(sirka, vyska + 10);
        hlavnyPanel.setTitle("Chytaj ovocie");
        hlavnyPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Platno.this.ukonci();
            }

        });
        hlavnyPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> Platno.this.kosik.posunSa("vlavo");
                    case KeyEvent.VK_RIGHT -> Platno.this.kosik.posunSa("vpravo");
                    case KeyEvent.VK_ESCAPE -> Platno.this.ukonci();
                    case KeyEvent.VK_SPACE -> Platno.this.pauza = !Platno.this.pauza;
                }
                Platno.this.prekresli();
            }
        });
        hlavnyPanel.setResizable(false);
        hlavnyPanel.setLayout(new BorderLayout(0, 0));
        JPanel hornaLista = new JPanel(new BorderLayout(200, 0));
        hornaLista.setBackground(Color.GREEN);
        JMenuBar menu = new JMenuBar();
        menu.setBackground(Color.GREEN);
        JMenu men = new JMenu("Hra");
        this.skore = new JLabel();
        this.skore.setText("Aktuálne skóre: " + this.hra.getSkore());
        this.najSkore = new JLabel();
        this.najSkore.setText("Najvyššie skóre: " + this.hra.getNajSkore() + "    ");
        JMenuItem ukonc = new JMenuItem("Koniec");
        JMenuItem hint = new JMenuItem("Nápoveda");
        JMenuItem pauz = new JMenuItem("Pauza");
        men.add(pauz);
        men.add(hint);
        menu.add(men);
        men.add(ukonc);
        hint.addActionListener(e -> {
            Platno.this.pauza = true;
            JOptionPane.showMessageDialog(hlavnyPanel, "Tvojou úlohou je chytať ovocie,\nhamburger je žolík ktorý ti dodá špeciálnu vlastnosť,\nak chytíš niečo iné, prehrávaš.", "Nápoveda", JOptionPane.INFORMATION_MESSAGE);
            Platno.this.pauza = false;
        });
        ukonc.addActionListener(e -> Platno.this.ukonci());
        pauz.addActionListener(e -> Platno.this.pauza = !Platno.this.pauza);
        hornaLista.add(BorderLayout.WEST, menu);
        hornaLista.add(BorderLayout.CENTER, this.skore);
        hornaLista.add(BorderLayout.EAST, this.najSkore);
        hlavnyPanel.add(BorderLayout.NORTH, hornaLista);
        this.canvas = new MojCanvas();
        //this.stredny = new JPanel();
        //hlavnyPanel.add(this.stredny);
        hlavnyPanel.add(BorderLayout.CENTER, this.canvas);
        hlavnyPanel.setVisible(true);
        this.novaHra();
    }
    public boolean isPauza() {
        return this.pauza;
    }

    /*vyhodi vyskakovacie okno na hracej ploche*/
    public void showError(String sprava) {
        JOptionPane.showMessageDialog(this.canvas, sprava + "\noprav cestu a skús znova", "Error", JOptionPane.WARNING_MESSAGE);
        this.ukonci();
    }

    public ArrayList<IObjekt> dajObjekty() {
        return this.objekty;
    }
    public void pridajObjekt(IObjekt objekt) {
        this.objekty.add(objekt);
    }
    public void odstranObjekt(IObjekt objekt) {
        this.vymazane.add(objekt);
    }
    public void ukonci() {
        this.hra.ulozSkore();
        System.exit(0);
    }
    public void prekresli() {
        this.canvas.update(this.canvas.getGraphics());
    }

    /*ak sú nejaké objekty, vymaže ich a spustí časovač*/
    public void novaHra() {
        this.objekty = new ArrayList<>();
        this.vymazane = new ArrayList<>();
        this.prepisSkore();
        this.pauza = false;
        this.timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!Platno.this.pauza) {
                    Platno.this.hra.tik();
                }
            }
        };
        this.timer.scheduleAtFixedRate(task, 100, 20);
    }
    /*zastaví časovač a na hracej ploche vyhodí vyskakovacie okno*/
    public void koniecHry() {
        this.timer.cancel();
        int x = JOptionPane.showOptionDialog(this.canvas, "Prehral si, tvoje skóre je " + this.hra.getSkore() + ".\nChceš začať odznova?", "Koniec hry.", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"ÁNO", "NIE"},
                "ÁNO");
        if (x == 0) {
            this.novaHra();
        } else {
            this.ukonci();
        }
    }
    /*vnorená trieda reprezentujúca hraciu plochu, jediná úloha kresliť obrázky*/
    private class MojCanvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            g.drawImage(Platno.this.pozadie.getImage(), 0, 0, null);
            g.drawImage(Platno.this.kosik.dajObrazok().getImage(), Platno.this.kosik.getSurX(), Platno.this.kosik.getSurY(), null);
            for (IObjekt objekt : Platno.this.objekty) {
                if (!Platno.this.vymazane.contains(objekt)) {
                    g.drawImage(objekt.dajObrazok().getImage(), objekt.getSurX(), objekt.getSurY(), null);
                }
            }
            for (IObjekt objekt : Platno.this.vymazane) {
                Platno.this.objekty.remove(objekt);
            }
            Platno.this.vymazane = new ArrayList<>();
        }
        @Override
        public void update(Graphics g) {
            if (!Platno.this.pauza) {
                this.repaint();
            }
        }
    }
    public void prepisSkore() {
        this.skore.setText("Aktuálne skóre: " + this.hra.getSkore());
        this.najSkore.setText("Najvyššie skóre: " + this.hra.getNajSkore() + "    ");
    }
}
