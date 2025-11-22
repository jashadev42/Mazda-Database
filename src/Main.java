import javax.swing.SwingUtilities;
import ui.MainMenuUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuUI());
    }
}
