import java.awt.*;
import java.awt.image.*;

public class Displayer extends Frame {
    Image image;

    public void Displayer(String s) {
	Toolkit toolkit = Toolkit.getDefaultToolkit();
     Image image1 = toolkit.getImage(s);
    }

    public void paint(Graphics g) {
        //Draw image at its natural size first.
        g.drawImage(image, 0, 0, this); //85x62 image
	}
}


