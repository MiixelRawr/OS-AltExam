import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

public class FCFSPanel extends JPanel {
    private final Color page = new Color(234, 220, 204);
    private final Color card = new Color(247, 239, 230);
    private final Color darkBrown = new Color(77, 48, 30);
    private final Color brown = new Color(111, 72, 48);
    private final Color mediumBrown = new Color(142, 98, 68);
    private final Color lightBrown = new Color(224, 204, 182);
    private final Color cream = new Color(252, 248, 242);
    private final Color line = new Color(178, 142, 111);

    private JTextField headField = new JTextField("73");
    private JTextField sizeField = new JTextField("200");
    private JTextArea requestArea = new JTextArea("33, 93, 53, 113, 63, 13", 3, 25);

    private JTextArea serviceOrder = new JTextArea(2, 25);
    private JLabel totalValue = new JLabel("300");
    private JLabel averageValue = new JLabel("50.00");
    private JLabel countValue = new JLabel("6");

    private DefaultTableModel tableModel = new DefaultTableModel(
        new String[] {"STEP", "FROM", "TO", "CALCULATION", "MOVEMENT", "TOTAL"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private JTable movementTable = new JTable(tableModel);

    private HeadGraph graph = new HeadGraph();
    private FCFSDiskScheduler scheduler = new FCFSDiskScheduler();

    public FCFSPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(page);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel heading = new JLabel("FCFS Disk Scheduling");
        heading.setOpaque(true);
        heading.setBackground(darkBrown);
        heading.setForeground(Color.WHITE);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(heading, BorderLayout.NORTH);

        JPanel dashboard = new JPanel(new GridLayout(2, 2, 14, 14));
        dashboard.setOpaque(false);
        dashboard.add(buildInputPanel());
        dashboard.add(buildSummaryPanel());
        dashboard.add(buildMovementPanel());
        dashboard.add(buildGraphPanel());
        add(dashboard, BorderLayout.CENTER);

        loadSampleData();
        runFCFS();
    }

    private Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkBrown, 2),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(line, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
            )
        );
    }

    private JPanel makeCard(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(card);
        panel.setBorder(createCardBorder());

        JLabel label = new JLabel(title);
        label.setOpaque(true);
        label.setBackground(brown);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    private JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(darkBrown);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(cream);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(line, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    private JPanel buildInputPanel() {
        JPanel panel = makeCard("Disk Requests");

        styleInput(headField);
        styleInput(sizeField);

        requestArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        requestArea.setLineWrap(true);
        requestArea.setWrapStyleWord(true);
        requestArea.setBackground(cream);
        requestArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel fields = new JPanel(new GridLayout(2, 2, 10, 8));
        fields.setOpaque(false);
        fields.add(formLabel("Initial head"));
        fields.add(formLabel("Disk size"));
        fields.add(headField);
        fields.add(sizeField);

        JPanel queuePanel = new JPanel(new BorderLayout(4, 4));
        queuePanel.setOpaque(false);
        queuePanel.add(formLabel("Queue"), BorderLayout.NORTH);

        JScrollPane queueScroll = new JScrollPane(requestArea);
        queueScroll.setBorder(BorderFactory.createLineBorder(line, 1));
        queuePanel.add(queueScroll, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(8, 10));
        center.setOpaque(false);
        center.add(fields, BorderLayout.NORTH);
        center.add(queuePanel, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JButton runButton = new JButton("Run");
        JButton sampleButton = new JButton("Load Sample Data");
        JButton clearButton = new JButton("Clear");

        stylePrimaryButton(runButton);
        styleSecondaryButton(sampleButton);
        styleSecondaryButton(clearButton);

        runButton.addActionListener(e -> runFCFS());
        sampleButton.addActionListener(e -> {
            loadSampleData();
            runFCFS();
        });
        clearButton.addActionListener(e -> resetForm());

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(runButton);
        buttons.add(sampleButton);
        buttons.add(clearButton);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(darkBrown);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(lightBrown);
        button.setForeground(darkBrown);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = makeCard("Summary");

        serviceOrder.setEditable(false);
        serviceOrder.setLineWrap(true);
        serviceOrder.setWrapStyleWord(true);
        serviceOrder.setFont(new Font("Segoe UI", Font.BOLD, 15));
        serviceOrder.setForeground(darkBrown);
        serviceOrder.setBackground(cream);
        serviceOrder.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel orderPanel = new JPanel(new BorderLayout(4, 4));
        orderPanel.setOpaque(false);
        orderPanel.add(formLabel("Service order"), BorderLayout.NORTH);

        JScrollPane orderScroll = new JScrollPane(serviceOrder);
        orderScroll.setBorder(BorderFactory.createLineBorder(line, 1));
        orderPanel.add(orderScroll, BorderLayout.CENTER);

        JPanel values = new JPanel(new GridLayout(1, 3, 8, 0));
        values.setOpaque(false);
        values.add(makeMetric("Total", totalValue));
        values.add(makeMetric("Average", averageValue));
        values.add(makeMetric("Requests", countValue));

        JPanel center = new JPanel(new BorderLayout(8, 10));
        center.setOpaque(false);
        center.add(orderPanel, BorderLayout.CENTER);
        center.add(values, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private JPanel makeMetric(String title, JLabel value) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 2, 2));
        panel.setBackground(lightBrown);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(line, 1),
            BorderFactory.createEmptyBorder(8, 9, 8, 9)
        ));

        JLabel name = new JLabel(title);
        name.setFont(new Font("Segoe UI", Font.BOLD, 11));
        name.setForeground(darkBrown);

        value.setFont(new Font("Segoe UI", Font.BOLD, 24));
        value.setForeground(darkBrown);
        value.setHorizontalAlignment(SwingConstants.LEFT);

        panel.add(name);
        panel.add(value);
        return panel;
    }

    private JPanel buildMovementPanel() {
        JPanel panel = makeCard("Movement Breakdown");

        movementTable.setRowHeight(27);
        movementTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        movementTable.setBackground(cream);
        movementTable.setForeground(darkBrown);
        movementTable.setGridColor(line);
        movementTable.setFillsViewportHeight(true);

        // Keep the six table columns visible, with a clear dark-brown header.
        movementTable.getTableHeader().setVisible(true);
        movementTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        movementTable.getTableHeader().setBackground(darkBrown);
        movementTable.getTableHeader().setForeground(Color.WHITE);
        movementTable.getTableHeader().setReorderingAllowed(false);
        movementTable.getTableHeader().setPreferredSize(new Dimension(0, 34));

        JScrollPane tableScroll = new JScrollPane(movementTable);
        tableScroll.setColumnHeaderView(movementTable.getTableHeader());
        tableScroll.setBorder(BorderFactory.createLineBorder(line, 1));

        panel.add(tableScroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildGraphPanel() {
        JPanel panel = makeCard("Head Movement Path");
        graph.setPreferredSize(new Dimension(450, 260));
        graph.setBorder(BorderFactory.createLineBorder(line, 1));
        panel.add(graph, BorderLayout.CENTER);
        return panel;
    }

    private void runFCFS() {
        try {
            int initialHead = Integer.parseInt(headField.getText().trim());
            int diskSize = Integer.parseInt(sizeField.getText().trim());

            List<Integer> requests = new ArrayList<Integer>();
            String text = requestArea.getText().trim();

            if (text.isEmpty()) {
                throw new IllegalArgumentException("Enter at least one request.");
            }

            String[] values = text.split("[,\\s]+");
            for (String value : values) {
                requests.add(Integer.parseInt(value));
            }

            FCFSDiskScheduleResult result = scheduler.schedule(initialHead, requests, diskSize);
            showResults(result, diskSize);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter whole numbers only.", "Invalid Input",
                JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showResults(FCFSDiskScheduleResult result, int diskSize) {
        String order = "";
        for (int i = 0; i < result.getServiceOrder().size(); i++) {
            if (i > 0) {
                order += "  →  ";
            }
            order += result.getServiceOrder().get(i);
        }

        serviceOrder.setText(order);
        totalValue.setText(String.valueOf(result.getTotalMovement()));
        averageValue.setText(String.format("%.2f", result.getAverageMovement()));
        countValue.setText(String.valueOf(result.getServiceOrder().size()));

        tableModel.setRowCount(0);
        List<Integer> path = result.getSeekPath();
        List<Integer> movements = result.getMovements();
        long runningTotal = 0;

        for (int i = 0; i < movements.size(); i++) {
            int from = path.get(i);
            int to = path.get(i + 1);
            int movement = movements.get(i);
            runningTotal += movement;

            tableModel.addRow(new Object[] {
                i + 1, from, to, "|" + to + " - " + from + "|",
                movement, runningTotal
            });
        }

        graph.setData(path, diskSize);
    }

    private void loadSampleData() {
        headField.setText("73");
        sizeField.setText("200");
        requestArea.setText("33, 93, 53, 113, 63, 13");
    }

    private void resetForm() {
        headField.setText("");
        sizeField.setText("");
        requestArea.setText("");
        serviceOrder.setText("");
        totalValue.setText("—");
        averageValue.setText("—");
        countValue.setText("—");
        tableModel.setRowCount(0);
        graph.clear();
    }

    private class HeadGraph extends JPanel {
        private List<Integer> path = new ArrayList<Integer>();
        private int diskSize = 200;

        public HeadGraph() {
            setBackground(cream);
        }

        public void setData(List<Integer> newPath, int newDiskSize) {
            path = new ArrayList<Integer>(newPath);
            diskSize = newDiskSize;
            repaint();
        }

        public void clear() {
            path.clear();
            repaint();
        }

        private void drawPaw(Graphics2D graph, int centerX, int centerY) {
            graph.setColor(mediumBrown);
            graph.fillOval(centerX - 6, centerY - 1, 12, 9);
            graph.fillOval(centerX - 8, centerY - 10, 4, 5);
            graph.fillOval(centerX - 3, centerY - 12, 4, 5);
            graph.fillOval(centerX + 2, centerY - 12, 4, 5);
            graph.fillOval(centerX + 6, centerY - 10, 4, 5);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graph = (Graphics2D) g;

            int left = 45;
            int right = getWidth() - 20;
            int top = 20;
            int bottom = getHeight() - 35;
            int width = right - left;
            int height = bottom - top;
            int max = Math.max(1, diskSize - 1);

            g.setColor(lightBrown);
            for (int i = 0; i <= 4; i++) {
                int y = bottom - (i * height / 4);
                g.drawLine(left, y, right, y);
                g.setColor(brown);
                g.drawString(String.valueOf(i * max / 4), 7, y + 4);
                g.setColor(lightBrown);
            }

            if (path.isEmpty()) {
                g.setColor(brown);
                g.drawString("Run to display the head path.", left, top + 20);
                return;
            }

            int[] x = new int[path.size()];
            int[] y = new int[path.size()];

            for (int i = 0; i < path.size(); i++) {
                x[i] = left + (path.size() == 1 ? width / 2 : i * width / (path.size() - 1));
                y[i] = bottom - (int) (path.get(i) * height / (double) max);
            }

            graph.setColor(new Color(144, 92, 61));
            graph.setStroke(new BasicStroke(2));
            for (int i = 0; i < path.size() - 1; i++) {
                graph.drawLine(x[i], y[i], x[i + 1], y[i + 1]);
            }

            for (int i = 0; i < path.size(); i++) {
                drawPaw(graph, x[i], y[i]);
                graph.setColor(darkBrown);
                graph.drawString(String.valueOf(path.get(i)), x[i] - 8, y[i] - 13);
            }

            g.setColor(darkBrown);
            g.drawString("Start", left, getHeight() - 10);
            g.drawString("Request order", right - 85, getHeight() - 10);
        }
    }
}
