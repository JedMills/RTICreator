package utils;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.Point;
import java.util.LinkedList;

/**
 * Provides methods used for detecting the light angle from the specular balls in the highlight detection projects.
 */
public class ImageProcessing {

    /**
     * Converts and image to greyscale.
     *
     * @param source        the colourful image
     * @return              the grey image
     */
    public static BufferedImage convertToGrayscale(BufferedImage source) {
        BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        return op.filter(source, null);
    }

    /**
     * The code from this method comes from the original Viewer. Don't really know what it does.
     *
     * @param pic       ?
     * @param pic1      ?
     */
    public static void blendFilterGray(BufferedImage pic, BufferedImage pic1) {

        int Xdim = pic1.getWidth();
        int Ydim = pic1.getHeight();

        int[] tmp = new int[1];
        int[] tmp1 = new int[1];
        int[] tmp2 = new int[1];


        if (pic == null) {
            pic = new BufferedImage(Xdim, Ydim, pic1.getType());
        }
        for (int i = 0; i < Ydim; i++) {
            for (int j = 0; j < Xdim; j++) {
                pic.getRaster().getPixel(j, i, tmp1);
                pic1.getRaster().getPixel(j, i, tmp2);
                tmp[0] = Math.max(tmp1[0], tmp2[0]);
                pic.getRaster().setPixel(j, i, tmp);
            }
        }
    }


    /**
     * Removes all the pixels not within the radius of the center of the ball
     *
     * @param source        the source image of the ball
     * @param circle        the [x, y, r] of the selected circle
     * @return              the image with pixels cleared from outside the circle
     */
    static public BufferedImage clearOutside(BufferedImage source, float[] circle) {
        int[] black = {0};
        for (int i = 0; i < source.getWidth(); i++) {
            for (int j = 0; j < source.getHeight(); j++) {
                float x = (float) (i - source.getWidth() / 2);
                float y = (float) (j - source.getHeight() / 2);
                if (Math.sqrt(x * x + y * y) > 0.95 * circle[2]) {
                    source.getRaster().setPixel(i, j, black);
                }
            }
        }

        return source;
    }


    /**
     * Finds the highlight pos in the image and givs it in coordinates for the sub-image of the ball. It does
     * this by finding  a group of pixels that have colour intensity higher than the threshold value.
     *
     * @param img       image to highlight detect
     * @param th        threshold value for grou pof pixels with intensity above this value
     * @return          the coordinates of the light spot in the image
     */
    public static float[] findTh(BufferedImage img, int th) {
        float[] ret = new float[4];
        float[] result = new float[3];
        float[] center = new float[4];
        float[] min = new float[2];
        float[] max = new float[2];
        int[] color = new int[1];
        int[] cor = {th - 1};

        float xc = img.getWidth() / 2.0f, yc = img.getHeight() / 2.0f;

        center[0] = -1; // X

        center[1] = -1; // Y

        center[2] = 0; // Radius

        center[3] = 0; // Area

        LinkedList<Point> pqueue = new LinkedList<Point>();
        java.awt.image.WritableRaster data = img.getRaster();


        for (int y = 0; y < img.getHeight(); y++) {
            // Linha
            for (int x = 0; x < img.getWidth(); x++) {
                // Coluna
                data.getPixel(x, y, color);
                if (color[0] >= th) {
                    pqueue.add(new Point(x, y));
                    data.setPixel(x, y, cor);
                    min[0] = x;
                    min[1] = y;
                    max[0] = x;
                    max[1] = y;

                    ret[0] = x;
                    ret[1] = y;
                    ret[3] = 1;

                    while (!pqueue.isEmpty()) {
                        Point p = pqueue.removeFirst();
                        ret[0] += p.x; // SUM(Xi)

                        ret[1] += p.y; // SUM(Yj)

                        ret[3]++; // Area

                        if (p.x < min[0]) {
                            min[0] = p.x;
                        }
                        if (p.y < min[1]) {
                            min[1] = p.y;
                        }
                        if (p.x > max[0]) {
                            max[0] = p.x;
                        }
                        if (p.y > max[1]) {
                            max[1] = p.y;
                        }
                        data.setPixel(p.x, p.y, cor);
                        for (int y1 = -1; y1 <= 1; y1++) {
                            // Linha
                            for (int x1 = -1; x1 <= 1; x1++) {
                                // Coluna
                                int px = p.x + x1;
                                int py = p.y + y1;
                                color[0] = 0;
                                if (px > 0 && px < data.getWidth() && py > 0 && py < data.getHeight()) {

                                    data.getPixel(px, py, color);

                                    if (color[0] >= th) {
                                        pqueue.add(new Point(px, py));
                                        data.setPixel(px, py, cor);
                                    }
                                }

                            }
                        }
                    }

                    float xr = (ret[0] / ret[3]) - xc + 0.5f;
                    float yr = yc - (ret[1] / ret[3]) + 0.5f;
                    float r = Math.min(max[0] - min[0], max[1] - min[1]) / 2;
                    float cxr = center[0] - xc;
                    float cyr = yc - center[1];
                    float sr = xr * xr + yr * yr;
                    float sc = cxr * cxr + cyr * cyr;


                    // center - highlight already detected
                    // ret - highlight under detection
                    if (ret[3] >= center[3] * 0.2) {
                        // Select if >= 20% previous hl area
                        if (ret[3] >= (center[3] * 5)) {
                            // Select if 5 times larger then hl
                            center[0] = ret[0] / ret[3];
                            center[1] = ret[1] / ret[3];
                            center[2] = r;
                            center[3] = ret[3];
                        } else {
                            if (sr < sc) {
                                center[0] = ret[0] / ret[3];
                                center[1] = ret[1] / ret[3];
                                center[2] = r;
                                center[3] = ret[3];
                            }
                        }
                    }
                }
            }
        }
        result[0] = center[0];
        result[1] = center[1];
        result[2] = center[2];
        return result;
    }


    /**
     * converts the coordinates of the highlight spot on the image of a specular ball to a light direction vector. The
     * code for this method comes from the original viewer.
     *
     * @param ballCenter    [x, y, r] of the ball in the sub-image given
     * @param highlight     [x, y] position of the highlight spot
     * @return              the light direction vector for this image
     */
    public static float[] calculateLightPosition(float[] ballCenter, float[] highlight) {

        float[] lpdir = {0.0f, 0.0f, 0.0f};

        float Sx = (highlight[0] - ballCenter[0]) / ballCenter[2];
        float Sy = (ballCenter[1] - highlight[1]) / ballCenter[2];
        double Sz = Math.sqrt(1.0 - Sx * Sx - Sy * Sy);


        double phi_n = Math.acos(Sz);
        double phi_l = 2.0 * phi_n;


        phi_l = (phi_l > (Math.PI/2)) ? phi_l = (Math.PI/2-phi_l) + Math.PI/2 : phi_l;

        double t = (Sx !=0 ) ? Math.atan(Sy / Sx) :  Math.PI/2;

        lpdir[0] = (float) (Math.sin(phi_l) * Math.cos(t));
        lpdir[1] = (float) (Math.sin(phi_l) * Math.sin(t));
        lpdir[2] = (float) Math.cos(phi_l);


        if (Sx >= 0) {
            lpdir[0] = Math.abs(lpdir[0]);
        } else {
            lpdir[0] = -Math.abs(lpdir[0]);
        }
        if (Sy >= 0) {
            lpdir[1] = Math.abs(lpdir[1]);
        } else {
            lpdir[1] = -Math.abs(lpdir[1]);
        }


        return lpdir;
    }
}
