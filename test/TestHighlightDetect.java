import org.junit.Test;
import utils.ImageProcessing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Jed on 16-Jul-17.
 */
public class TestHighlightDetect {


    @Test
    public void test() throws Exception {
        BufferedImage image = ImageIO.read(new File("C:/Users/Jed/Desktop/fish.jpg"));
        image = image.getSubimage(64, 1085, 170, 170);

        BufferedImage grey = ImageProcessing.convertToGrayscale(image);

        BufferedImage blend = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        ImageProcessing.blendFilterGray(blend, grey);

        float[] ballInfo = new float[]{85, 85, 85};

        BufferedImage clear = ImageProcessing.clearOutside(grey, ballInfo);

        float[] highlights = {-1.0f, -1.0f};

        highlights = ImageProcessing.findTh(clear, 230);

        float[] lightVec = ImageProcessing.calculateLightPosition(ballInfo, highlights);

        System.out.println("Highlight: (" + highlights[0] + ", " + highlights[1] + ")");
        System.out.println("Light pos: (" + lightVec[0] + ", " + lightVec[1] + ", " + lightVec[2] + ")");

        ImageIO.write(clear, "png", new File("trial.png"));
    }
}
