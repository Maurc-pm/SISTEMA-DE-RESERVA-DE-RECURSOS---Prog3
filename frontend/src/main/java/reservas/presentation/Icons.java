package reservas.presentation;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Window;
import java.net.URL;

/** Iconos compartidos por las vistas, cargados desde el classpath. */
public final class Icons {
    private static final String BASE_PATH = "/reservas/presentation/icons/";
    private static final int MAX_SIZE = 20;

    private Icons() {
    }

    public static ImageIcon load(String filename) {
        URL resource = Icons.class.getResource(BASE_PATH + filename);
        if (resource == null) {
            return null;
        }

        ImageIcon icon = new ImageIcon(resource);
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }

        if (width > MAX_SIZE || height > MAX_SIZE) {
            double scale = (double) MAX_SIZE / Math.max(width, height);
            icon = new ImageIcon(icon.getImage().getScaledInstance(
                    Math.max(1, (int) Math.round(width * scale)),
                    Math.max(1, (int) Math.round(height * scale)),
                    Image.SCALE_SMOOTH));
        }
        return icon;
    }

    public static void set(AbstractButton button, String filename) {
        ImageIcon icon = load(filename);
        if (icon != null) {
            button.setIcon(icon);
        }
    }

    public static void setWindowIcon(Window window, String filename) {
        ImageIcon icon = load(filename);
        if (icon != null) {
            window.setIconImage(icon.getImage());
        }
    }
}
