
package src;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class gui extends JFrame {

    JTextField headField;
    JTextField diskField;
    JTextArea queueField;
    JComboBox<String> directionBox;
    JTextArea resultArea;
    DiskGraph graphPanel;

    public gui() {

        setTitle("C-LOOK Disk Scheduling Simulator");
        setSize(850, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(15, 15));

        // TITLE
        JLabel title = new JLabel(
            "C-LOOK DISK SCHEDULING SIMULATOR",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // INPUT PANEL
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 15));
        inputPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 10, 30)
        );

        inputPanel.add(new JLabel("Initial Head Position:"));
        headField = new JTextField();
        inputPanel.add(headField);

        inputPanel.add(new JLabel("Disk Size:"));
        diskField = new JTextField();
        inputPanel.add(diskField);

        inputPanel.add(new JLabel("Request Queue:"));

        queueField = new JTextArea(2, 30);
        queueField.setLineWrap(true);
        queueField.setWrapStyleWord(true);

        JScrollPane queueScroll = new JScrollPane(queueField);
        inputPanel.add(queueScroll);

        inputPanel.add(new JLabel("Initial Direction:"));

        String[] directions = {"UP", "DOWN"};
        directionBox = new JComboBox<>(directions);
        inputPanel.add(directionBox);

       
        JButton simulateButton = new JButton("SIMULATE");
        JButton clearButton = new JButton("CLEAR");
        JButton sampleButton = new JButton("LOAD SAMPLE DATA");

        simulateButton.setPreferredSize(new Dimension(150, 45));
        clearButton.setPreferredSize(new Dimension(150, 45));
        sampleButton.setPreferredSize(new Dimension(190, 45));

        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 15, 5)
        );

        buttonPanel.add(simulateButton);
        buttonPanel.add(sampleButton);
        buttonPanel.add(clearButton);

        resultArea = new JTextArea(12, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(
            BorderFactory.createTitledBorder("Simulation Results")
        );


        graphPanel = new DiskGraph();

        JPanel outputPanel = new JPanel(new BorderLayout(5, 10));

        outputPanel.add(scrollPane, BorderLayout.CENTER);
        outputPanel.add(graphPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 15));

        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        centerPanel.add(outputPanel, BorderLayout.CENTER);
        
        centerPanel.setBorder(
            BorderFactory.createEmptyBorder(0, 10, 15, 10)
        );

        add(centerPanel, BorderLayout.CENTER);

        simulateButton.addActionListener(e -> simulate());
        sampleButton.addActionListener(e -> loadSampleData());
        clearButton.addActionListener(e -> clearFields());

        setVisible(true);
    }

    public void simulate() {

        try {

            int head = Integer.parseInt(headField.getText().trim());
            int diskSize = Integer.parseInt(diskField.getText().trim());

            if (diskSize <= 0) {
                throw new IllegalArgumentException(
                    "Disk size must be greater than zero."
                );
            }

            if (head < 0 || head >= diskSize) {
                throw new IllegalArgumentException(
                    "Head position must be between 0 and "
                    + (diskSize - 1) + "."
                );
            }

            String queueInput = queueField.getText().trim();

            if (queueInput.isEmpty()) {
                throw new IllegalArgumentException(
                    "Please enter the request queue."
                );
            }

            String[] values = queueInput.split("[,\\s]+");            ArrayList<Integer> requests = new ArrayList<>();
            for (String value : values) {

                int request = Integer.parseInt(value.trim());

                if (request < 0 || request >= diskSize) {
                    throw new IllegalArgumentException(
                        "Request " + request
                        + " is outside the disk range (0 to "
                        + (diskSize - 1) + ")."
                    );
                }

                requests.add(request);
            }

            String direction =
                (String) directionBox.getSelectedItem();

            ArrayList<Integer> sequence =
                clook.schedule(requests, head, direction);

            int totalMovement =
                clook.calculateTotalMovement(sequence);

            double averageSeek =
                clook.calculateAverageSeek(
                    totalMovement, requests.size()
                );

            StringBuilder output = new StringBuilder();

            output.append("C-LOOK DISK SCHEDULING RESULTS\n\n");

            output.append("Seek Sequence:\n");
            output.append(sequence).append("\n\n");

            output.append("Head Movement per Step:\n");

            for (int i = 1; i < sequence.size(); i++) {

                int movement = Math.abs(
                    sequence.get(i) - sequence.get(i - 1)
                );

                output.append(sequence.get(i - 1))
                      .append(" -> ")
                      .append(sequence.get(i))
                      .append(" = ")
                      .append(movement)
                      .append("\n");
            }

            output.append("\nTotal Head Movement: ")
                  .append(totalMovement);

            output.append("\nAverage Seek Distance: ")
                  .append(String.format("%.2f", averageSeek));

            resultArea.setText(output.toString());
            resultArea.setCaretPosition(0);
            graphPanel.setPath(sequence, diskSize);

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter valid numbers.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void loadSampleData() {

        headField.setText("53");
        diskField.setText("200");
        queueField.setText("98,183,37,122,14,124,65,67");

        directionBox.setSelectedItem("UP");

        resultArea.setText("");
        graphPanel.setPath(new ArrayList<>(), 200);
    }
        
    public void clearFields() {

        headField.setText("");
        diskField.setText("");
        queueField.setText("");
        directionBox.setSelectedIndex(0);
        resultArea.setText("");

        graphPanel.setPath(new ArrayList<>(), 200);
    }

    public static void main(String[] args) {

        new gui();
    }

    class DiskGraph extends JPanel {

        ArrayList<Integer> path = new ArrayList<>();
        int diskSize = 200;

        public DiskGraph() {
            setPreferredSize(new Dimension(600, 180));
            setBorder(
                BorderFactory.createTitledBorder("Disk Head Movement Graph")
            );
            setBackground(Color.WHITE);
        }

        public void setPath(ArrayList<Integer> sequence, int size) {
            path = new ArrayList<>(sequence);
            diskSize = size;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (path.isEmpty()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int left = 60;
            int right = getWidth() - 30;
            int top = 35;
            int bottom = getHeight() - 45;

            g2.setColor(Color.BLACK);
            g2.drawLine(left, top, left, bottom);
            g2.drawLine(left, bottom, right, bottom);

            g2.drawString("Cylinder", 5, 20);
            g2.drawString("Order of Service", getWidth() / 2 - 50,
                          getHeight() - 10);

            int maxCylinder = diskSize - 1;

            for (int i = 0; i < path.size(); i++) {

                int x = left + i * (right - left)
                        / Math.max(1, path.size() - 1);

                int y = bottom - (path.get(i) * (bottom - top)
                        / Math.max(1, maxCylinder));

                g2.setColor(Color.BLUE);
                g2.fillOval(x - 5, y - 5, 10, 10);

                g2.setColor(Color.BLACK);
                g2.drawString(
                    String.valueOf(path.get(i)),
                    x - 10,
                    y - 10
                );

                if (i > 0) {

                    int previousX = left + (i - 1) * (right - left)
                            / Math.max(1, path.size() - 1);

                    int previousY = bottom - (path.get(i - 1)
                            * (bottom - top)
                            / Math.max(1, maxCylinder));

                    g2.setColor(Color.BLUE);
                    g2.drawLine(previousX, previousY, x, y);
                }
            }
        }
    }

}
