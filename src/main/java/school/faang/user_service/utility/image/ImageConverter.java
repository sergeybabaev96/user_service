package school.faang.user_service.utility.image;

import org.springframework.stereotype.Component;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

@Component
public class ImageConverter {

    public BufferedImage resizeImage(BufferedImage originalImage, int maxSize) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        if (width <= maxSize && height <= maxSize) {
            return originalImage;
        }

        double scale = (double) maxSize / Math.max(width, height);
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return resizedImage;
    }
}