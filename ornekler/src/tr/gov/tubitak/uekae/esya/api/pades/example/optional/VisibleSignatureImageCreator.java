package tr.gov.tubitak.uekae.esya.api.pades.example.optional;

import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.SignaturePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class VisibleSignatureImageCreator {

    public static byte [] createImage(String image, String text, SignaturePanel imagePanel, Dimension imageSize) throws IOException
    {

        BufferedImage bufferedImage = new BufferedImage(imagePanel.getWidth(), imagePanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, imagePanel.getWidth(), imagePanel.getHeight());

        File imageFile = new File(image);
        BufferedImage logo = ImageIO.read(imageFile);
        g2d.drawImage(logo,0,0, imageSize.width, imageSize.height,null);

        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g2d.setColor(Color.black);
        g2d.drawString(text,  imageSize.width,  imageSize.height/2);
        g2d.dispose();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, imageFile.getName().split("\\.")[1] , os);

        return os.toByteArray();
    }
}
