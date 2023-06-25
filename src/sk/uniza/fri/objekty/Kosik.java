package sk.uniza.fri.objekty;

import sk.uniza.fri.hlavnyBalik.Hra;
import java.io.IOException;
/**
 * 26. 4. 2022 - 17:18
 *
 * @author petok
 */
public class Kosik {
    private final int konstantaPosunu;
    private final int surY;
    private final Obrazok obrazok;
    private int surX;
    private int dlzka;
    private boolean zvecseny;

    public Kosik(int konstantaPosunu, int surX, int surY, String cesta) throws IOException {
        this.konstantaPosunu = konstantaPosunu;
        this.dlzka = 120;
        this.surY = surY;
        this.surX = surX;
        this.zvecseny = false;
        try {
            this.obrazok = new Obrazok(cesta);
        } catch (IOException ex) {
            throw new IOException(ex.getMessage() + " kosik");
        }
        this.obrazok.zmenVelkost(this.dlzka);
    }
    public int getDlzka() {
        return this.dlzka / 3 * 2;
    }

    public int getSurX() {
        return this.surX;
    }
    public int getSurY() {
        return this.surY;
    }
    public void posunSa(String smer) {
        switch (smer) {
            case "vlavo":
                if (this.surX > 0) {
                    this.surX -= this.konstantaPosunu;
                }
                break;
            case "vpravo":
                if (this.surX < Hra.dajSirkuPlatna() - this.dlzka - this.konstantaPosunu) {
                    this.surX += this.konstantaPosunu;
                }
                break;
            default:
        }
    }
    public void predlzSa() {
        if (!this.zvecseny) {
            this.obrazok.zmenDlzku(50);
            this.dlzka += 50;
            this.zvecseny = true;
        }
    }
    public void zmensiSa() {
        if (this.zvecseny) {
            this.obrazok.zmenDlzku(-50);
            this.zvecseny = false;
            this.dlzka -= 50;
        }
    }
    public Obrazok dajObrazok() {
        return this.obrazok;
    }
}
