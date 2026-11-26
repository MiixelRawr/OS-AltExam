import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;

public class FCFSSimulatorApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("FCFS Disk Scheduling");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new FCFSPanel());
                frame.setSize(1200, 820);
                frame.setMinimumSize(new Dimension(980, 680));
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
