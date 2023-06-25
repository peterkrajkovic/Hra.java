package sk.uniza.fri.objekty;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 * 26. 4. 2022 - 17:18
 *Trieda Obrazok reprezentuje BufferedImage jednotlivého objektu, dokáže zmeniť veľkosť, zmeniť dĺžku a vrátiť BufferedImage
 * @author petok
 */
public class Obrazok {

    private BufferedImage image;

    public Obrazok(String cesta) throws IOException {
        this.image = null;
        try {
            this.image = ImageIO.read(new File(cesta));
        } catch (IOException ex) {
            throw new IOException("nepodarilo sa nacitat subor");
        }
    }

    public void zmenVelkost(int novaVelkost) {
        BufferedImage novyImage = new BufferedImage(novaVelkost, novaVelkost, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = novyImage.createGraphics();
        graphics2D.drawImage(this.image, 0, 0, novaVelkost, novaVelkost, null);
        graphics2D.dispose();
        this.image = novyImage;
    }

    public void zmenDlzku(int oKolko) {
        BufferedImage novyImage = new BufferedImage(this.image.getWidth() + oKolko, this.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = novyImage.createGraphics();
        graphics2D.drawImage(this.image, 0, 0, this.image.getWidth() + oKolko, this.image.getHeight(), null);
        graphics2D.dispose();
        this.image = novyImage;
    }

    public BufferedImage getImage() {
        return this.image;
    }

}
