/**
 * ICS26012 - Operating Systems
 * Module: C-SCAN Disk Scheduling GUI (Classic OS Simulator Style + Path Graph)
 * Author: [Your Name Here]
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CSCANSimulatorUI_ABUDA extends JFrame {

    private final DiskScheduler_ABUDA scheduler = new CSCAN_ABUDA();

    private final JTextField queueField = new JTextField(24);
    private final JTextField headField = new JTextField(5);
    private final JTextField diskSizeField = new JTextField(5);
    private final JComboBox<DiskScheduler_ABUDA.Direction> directionBox =
            new JComboBox<>(DiskScheduler_ABUDA.Direction.values());

    private final JTextArea outputArea = new JTextArea();
    private final HeadPathPanel graphPanel = new HeadPathPanel();

    public CSCANSimulatorUI_ABUDA() {
        setTitle("OS Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(6, 6));

        // 1. Classic Monospaced Text Output Area (matches sample UI)
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setBackground(Color.WHITE);
        outputArea.setForeground(Color.BLACK);
        outputArea.setMargin(new Insets(10, 12, 10, 12));
        printBanner();

        JScrollPane textScroll = new JScrollPane(outputArea);

        // 2. Split Pane: Classic ASCII Output (Left) + Head Path Graph (Right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textScroll, graphPanel);
        splitPane.setDividerLocation(540);
        splitPane.setResizeWeight(0.55);
        add(splitPane, BorderLayout.CENTER);

        // 3. Bottom Control & Input Bar (Dark bar styling + required buttons)
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(new Color(50, 50, 50));
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        inputRow.setOpaque(false);
        inputRow.add(createLabel("Queue (comma/space):"));
        inputRow.add(queueField);
        inputRow.add(createLabel("Head:"));
        inputRow.add(headField);
        inputRow.add(createLabel("Disk Size:"));
        inputRow.add(diskSizeField);
        inputRow.add(createLabel("Direction:"));
        inputRow.add(directionBox);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        buttonRow.setOpaque(false);
        JButton runBtn = new JButton("Run C-SCAN");
        JButton sampleBtn = new JButton("Load Sample Data");
        JButton resetBtn = new JButton("Reset / Clear");

        runBtn.addActionListener(e -> runSimulation());
        sampleBtn.addActionListener(e -> loadSampleData());
        resetBtn.addActionListener(e -> resetAll());

        buttonRow.add(sampleBtn);
        buttonRow.add(resetBtn);
        buttonRow.add(runBtn);

        bottomContainer.add(inputRow, BorderLayout.CENTER);
        bottomContainer.add(buttonRow, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return lbl;
    }

    private void printBanner() {
        outputArea.setText(
            "============================================================\n" +
            "||                                                        ||\n" +
            "||             OS Scheduling Algorithms                   ||\n" +
            "||         Disk Scheduling - C-SCAN Module                ||\n" +
            "||                                                        ||\n" +
            "============================================================\n\n" +
            "Enter inputs below or click [Load Sample Data], then click [Run C-SCAN].\n"
        );
    }

    private void loadSampleData() {
        queueField.setText("98, 183, 37, 122, 14, 124, 65, 67");
        headField.setText("53");
        diskSizeField.setText("200");
        directionBox.setSelectedItem(DiskScheduler_ABUDA.Direction.RIGHT);
    }

    private void resetAll() {
        queueField.setText("");
        headField.setText("");
        diskSizeField.setText("");
        directionBox.setSelectedItem(DiskScheduler_ABUDA.Direction.RIGHT);
        printBanner();
        graphPanel.clear();
    }

    private void runSimulation() {
        try {
            List<Integer> requests = parseQueue(queueField.getText());
            int initialHead = Integer.parseInt(headField.getText().trim());
            int diskSize = Integer.parseInt(diskSizeField.getText().trim());
            DiskScheduler_ABUDA.Direction dir = (DiskScheduler_ABUDA.Direction) directionBox.getSelectedItem();

            DiskScheduleResult_ABUDA result = scheduler.schedule(requests, initialHead, diskSize, dir);
            displayResult(result, initialHead, diskSize, dir);
            graphPanel.setData(result.getSeekSequence(), diskSize);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid whole numbers for Queue, Initial Head, and Disk Size.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Integer> parseQueue(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("Request queue cannot be empty.");
        }
        String[] tokens = raw.trim().split("[,\\s]+");
        List<Integer> list = new ArrayList<>();
        for (String t : tokens) {
            if (!t.isEmpty()) {
                list.add(Integer.parseInt(t));
            }
        }
        return list;
    }

    private void displayResult(DiskScheduleResult_ABUDA res, int head, int diskSize, DiskScheduler_ABUDA.Direction dir) {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("||            C-SCAN Disk Scheduling Results              ||\n");
        sb.append("============================================================\n");
        sb.append(String.format("Disk Range: 0 - %d | Initial Head: %d | Direction: %s%n%n",
                diskSize - 1, head, dir));

        sb.append("====================== Movement Table ======================\n");
        sb.append(String.format("%-6s %-8s %-8s %-20s %-10s%n",
                "Step", "From", "To", "Calculation", "Movement"));
        sb.append("------------------------------------------------------------\n");

        List<Integer> seq = res.getSeekSequence();
        List<Integer> steps = res.getStepMovements();
        for (int i = 0; i < steps.size(); i++) {
            int from = seq.get(i);
            int to = seq.get(i + 1);
            int dist = steps.get(i);
            String calc = String.format("|%d - %d|", to, from);
            sb.append(String.format("%-6d %-8d %-8d %-20s %-10d%n",
                    i + 1, from, to, calc, dist));
        }

        sb.append("\n======================== Seek Sequence =====================\n");
        for (int i = 0; i < seq.size(); i++) {
            sb.append(seq.get(i));
            if (i < seq.size() - 1) sb.append(" -> ");
        }
        sb.append("\n\n========================= Summary ==========================\n");
        sb.append(String.format("Total Head Movement:   %d cylinders%n", res.getTotalHeadMovement()));
        sb.append(String.format("Average Seek Distance: %.2f cylinders%n", res.getAverageSeekDistance()));
        sb.append("============================================================\n");

        outputArea.setText(sb.toString());
        outputArea.setCaretPosition(0);
    }

    // Custom Panel to draw the Head Movement Path Graph
    static class HeadPathPanel extends JPanel {
        private List<Integer> sequence = new ArrayList<>();
        private int diskSize = 200;

        public HeadPathPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder("Head Movement Path"));
        }

        public void setData(List<Integer> sequence, int diskSize) {
            this.sequence = sequence;
            this.diskSize = diskSize;
            repaint();
        }

        public void clear() {
            this.sequence = new ArrayList<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (sequence == null || sequence.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padLeft = 35, padRight = 35, padTop = 50, padBottom = 30;
            int w = getWidth() - padLeft - padRight;
            int h = getHeight() - padTop - padBottom;
            int maxCyl = Math.max(1, diskSize - 1);

            // Draw top cylinder axis (0 to maxCyl)
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(padLeft, padTop, padLeft + w, padTop);
            g2.drawString("0", padLeft - 4, padTop - 10);
            g2.drawString(String.valueOf(maxCyl), padLeft + w - 12, padTop - 10);

            int n = sequence.size();
            int stepY = (n > 1) ? h / (n - 1) : h;

            for (int i = 0; i < n; i++) {
                int cyl = sequence.get(i);
                int x = padLeft + (int) ((double) cyl / maxCyl * w);
                int y = padTop + 15 + (i * Math.min(stepY, 45));

                if (i > 0) {
                    int prevCyl = sequence.get(i - 1);
                    int prevX = padLeft + (int) ((double) prevCyl / maxCyl * w);
                    int prevY = padTop + 15 + ((i - 1) * Math.min(stepY, 45));

                    // Highlight the return jump (0 <-> maxCyl) in red, normal moves in blue
                    boolean isJump = (prevCyl == maxCyl && cyl == 0) || (prevCyl == 0 && cyl == maxCyl);
                    g2.setColor(isJump ? Color.RED : new Color(30, 90, 180));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(prevX, prevY, x, y);
                }

                // Draw node & cylinder label
                g2.setColor(i == 0 ? new Color(0, 150, 0) : Color.BLACK);
                g2.fillOval(x - 4, y - 4, 8, 8);
                g2.setFont(new Font("Monospaced", Font.BOLD, 11));
                g2.drawString(String.valueOf(cyl), x - 8, y - 8);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CSCANSimulatorUI_ABUDA().setVisible(true));
    }
}