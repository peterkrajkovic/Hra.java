package sk.uniza.fri.objekty;

import sk.uniza.fri.hlavnyBalik.Hra;
import java.io.IOException;

/**
 * 26. 4. 2022 - 17:18
 *
 * @author petok
 */
public class Banan implements IObjekt {

    private final int konstantaPohybu;
    private final Obrazok obrazok;
    private final int surX;
    private int surY;

    public Banan(int konstantaPohybu, int surX, String cesta) throws IOException {
        this.konstantaPohybu = konstantaPohybu;
        this.surX = surX;
        this.surY = 0;
        try {
            this.obrazok = new Obrazok(cesta);
            this.obrazok.zmenVelkost(Hra.dajVyskuPlatna() / 12);
        } catch (IOException ex) {
            throw new IOException(ex.getMessage() + " banan");
        }
    }
    @Override
    public int getSurX() {
        return this.surX;
    }

    @Override
    public int getSurY() {
        return this.surY;
    }

    @Override
    public boolean siZly() {
        return false;
    }

    @Override
    public int hodnota() {
        return 2;
    }

    @Override
    public Obrazok dajObrazok() {
        return this.obrazok;
    }

    @Override
    public void hybSa() {
        this.surY += this.konstantaPohybu;
    }
}