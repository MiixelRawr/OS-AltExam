import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

// Scheduling variable here is the sample algo code, just edit it since the algos are in diffent file and name.
public class Group10Scheduler extends JFrame {
    static Color BG, INK, MUTED, DARK, ACCENT, LINE, PALE, PEACH, TERRACOTTA;
    static final Color PAPER = Color.WHITE, GREEN = Color.decode("#386F59"), RED = Color.decode("#A32332");
    static Color[] COLORS;
    static Color c(String h) {
        return Color.decode("#" + h);
    }
    static Color blend(Color a, Color b, double t) {
        return new Color((int) Math.round(a.getRed() * (1 - t) + b.getRed() * t),
                (int) Math.round(a.getGreen() * (1 - t) + b.getGreen() * t),
                (int) Math.round(a.getBlue() * (1 - t) + b.getBlue() * t));
    }
    static void theme(Scheduling.Algorithm a) {
        String[] p;
        if (a == Scheduling.Algorithm.PRIORITY)
            p = new String[]{"590909", "89181e", "be886a", "e2b49a", "f5ecea"};
        else if (a == Scheduling.Algorithm.SJF)
            p = new String[]{"691883", "b148d2", "e79aff", "f3ccff", "fbeeff"};
        else if (a == Scheduling.Algorithm.CSCAN)
            p = new String[]{"071f35", "24496b", "446d92", "98b1c8", "f7fafe"};
        else if (a == Scheduling.Algorithm.CLOOK)
            p = new String[]{"71391e", "ff7e47", "ffa07a", "ffb482", "fffdf4"};
        else if (a == Scheduling.Algorithm.FCFS)
            p = new String[]{"6e4f32", "8c6e52", "a28973", "bba591", "d9c9ba"};
        else
            p = new String[]{"252831", "414956", "647184", "d9dfe7", "f5f6f8"};
        DARK = c(p[0]);
        ACCENT = c(p[1]);
        TERRACOTTA = c(p[2]);
        PEACH = c(p[3]);
        BG = c(p[4]);
        if (a == Scheduling.Algorithm.FCFS)
            BG = blend(BG, PAPER, .68);
        PALE = a == Scheduling.Algorithm.CLOOK ? c("fff2bd") : blend(PEACH, PAPER, .70);
        LINE = blend(PEACH, PAPER, .33);
        INK = blend(DARK, c("24242a"), .32);
        MUTED = blend(DARK, c("5b5b63"), .48);
        COLORS = new Color[]{DARK, ACCENT, TERRACOTTA, PEACH, blend(DARK, PEACH, .45), blend(ACCENT, PAPER, .34),
                blend(DARK, ACCENT, .45), blend(TERRACOTTA, PAPER, .35), blend(DARK, TERRACOTTA, .6),
                blend(PEACH, PAPER, .45)};
    }
    static {
        theme(null);
    }
    static Color chartText(Color c) {
        double l = 0.2126 * linear(c.getRed()) + 0.7152 * linear(c.getGreen()) + 0.0722 * linear(c.getBlue());
        return l > 0.179 ? Color.BLACK : Color.WHITE;
    }
    static double linear(int n) {
        double x = n / 255.0;
        return x <= 0.04045 ? x / 12.92 : Math.pow((x + 0.055) / 1.055, 2.4);
    }
    JPanel screen = new JPanel(new BorderLayout());
    static Font font(int size, boolean bold) {
        return new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size);
    }
    static JLabel label(String text, int size, boolean bold, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(size, bold));
        l.setForeground(color);
        return l;
    }
    static JPanel panel(LayoutManager layout, Color color) {
        JPanel p = new JPanel(layout);
        p.setBackground(color);
        return p;
    }
    static JPanel vertical(Color color) {
        JPanel p = panel(null, color);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }
    static void add(JPanel p, Component c) {
        if (c instanceof JComponent)
            ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(c);
    }
    static void gap(JPanel p, int h) {
        p.add(Box.createVerticalStrut(h));
    }
    static class RoundedPanel extends JPanel {
        final Color fill, outline;
        final int radius;
        RoundedPanel(LayoutManager layout, Color fill, Color outline, int radius) {
            super(layout);
            this.fill = fill;
            this.outline = outline;
            this.radius = radius;
            setOpaque(false);
        }
        protected void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr.create();
            quality(g);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (outline != null) {
                g.setColor(outline);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g.dispose();
            super.paintComponent(gr);
        }
    }
    static JPanel roundedVertical(Color fill, Color outline) {
        JPanel p = new RoundedPanel(null, fill, outline, 18);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }
    static JPanel card(String title, String subtitle) {
        JPanel p = roundedVertical(PAPER, LINE);
        p.setBorder(new EmptyBorder(17, 20, 17, 20));
        add(p, label(title, 18, true, DARK));
        if (!subtitle.isEmpty()) {
            gap(p, 4);
            add(p, label(subtitle, 12, false, MUTED));
        }
        gap(p, 14);
        return p;
    }
    static JButton button(String s, boolean primary) {
        final Color base = primary ? ACCENT : PALE, outline = primary ? ACCENT : LINE,
                fg = primary ? chartText(ACCENT) : DARK, focus = DARK;
        JButton b = new JButton(s) {
            protected void paintComponent(Graphics gr) {
                Graphics2D g = (Graphics2D) gr.create();
                quality(g);
                Color fill = getModel().isPressed()
                        ? blend(base, focus, .18)
                        : getModel().isRollover() ? blend(base, focus, .08) : base;
                g.setColor(fill);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 13, 13);
                g.setColor(isFocusOwner() ? focus : outline);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 13, 13);
                g.dispose();
                super.paintComponent(gr);
            }
        };
        b.setFont(font(13, true));
        b.setForeground(fg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 15, 10, 15));
        return b;
    }
    static JTextField field(String s, int cols) {
        JTextField f = new JTextField(s, cols);
        f.setFont(font(14, false));
        f.setForeground(INK);
        f.setBackground(PAPER);
        f.setSelectionColor(PALE);
        f.setSelectedTextColor(INK);
        final Color border = LINE, focus = ACCENT;
        f.setBorder(new CompoundBorder(new LineBorder(border, 1, true), new EmptyBorder(7, 8, 7, 8)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(focus, 1, true), new EmptyBorder(7, 8, 7, 8)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(border, 1, true), new EmptyBorder(7, 8, 7, 8)));
            }
        });
        return f;
    }
    static class SlimScroll extends BasicScrollBarUI {
        final Color track, thumb;
        SlimScroll(Color track, Color thumb) {
            this.track = track;
            this.thumb = thumb;
        }
        protected JButton createDecreaseButton(int orientation) {
            return zero();
        }
        protected JButton createIncreaseButton(int orientation) {
            return zero();
        }
        JButton zero() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }
        protected Dimension getMinimumThumbSize() {
            return new Dimension(25, 25);
        }
        protected void paintTrack(Graphics gr, JComponent c, Rectangle r) {
            Graphics2D g = (Graphics2D) gr.create();
            quality(g);
            g.setColor(track);
            boolean v = scrollbar.getOrientation() == JScrollBar.VERTICAL;
            g.fillRoundRect(r.x + (v ? 3 : 0), r.y + (v ? 0 : 3), Math.max(0, r.width - (v ? 6 : 0)),
                    Math.max(0, r.height - (v ? 0 : 6)), 8, 8);
            g.dispose();
        }
        protected void paintThumb(Graphics gr, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled())
                return;
            Graphics2D g = (Graphics2D) gr.create();
            quality(g);
            g.setColor(isThumbRollover() ? thumb.darker() : thumb);
            boolean v = scrollbar.getOrientation() == JScrollBar.VERTICAL;
            g.fillRoundRect(r.x + (v ? 3 : 0), r.y + (v ? 0 : 3), Math.max(0, r.width - (v ? 6 : 0)),
                    Math.max(0, r.height - (v ? 0 : 6)), 8, 8);
            g.dispose();
        }
    }
    static JScrollPane scroll(Component c) {
        JScrollPane s = new JScrollPane(c);
        s.setOpaque(false);
        s.setBorder(BorderFactory.createEmptyBorder());
        s.getViewport().setBackground(BG);
        s.getVerticalScrollBar().setUnitIncrement(22);
        for (JScrollBar bar : new JScrollBar[]{s.getVerticalScrollBar(), s.getHorizontalScrollBar()}) {
            bar.setUI(new SlimScroll(PALE, TERRACOTTA));
            bar.setOpaque(false);
            bar.setPreferredSize(new Dimension(12, 12));
        }
        return s;
    }
    static JTable table(DefaultTableModel model) {
        final Color ink = INK, stripe = blend(BG, WHITE(), .37), selected = PALE, header = DARK,
                hover = blend(PEACH, PAPER, .78);
        JTable t = new JTable(model) {
            int hoverRow = -1;
            {
                addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseMoved(MouseEvent e) {
                        int n = rowAtPoint(e.getPoint());
                        if (n != hoverRow) {
                            hoverRow = n;
                            repaint();
                        }
                    }
                });
                addMouseListener(new MouseAdapter() {
                    public void mouseExited(MouseEvent e) {
                        hoverRow = -1;
                        repaint();
                    }
                });
            }
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) c).setBorder(new EmptyBorder(0, 6, 0, 6));
                }
                if (!isCellSelected(row, col)) {
                    c.setBackground(row == hoverRow ? hover : row % 2 == 0 ? PAPER : stripe);
                    c.setForeground(ink);
                }
                return c;
            }
        };
        t.setRowHeight(30);
        t.setFont(font(13, false));
        t.setGridColor(blend(LINE, PAPER, .5));
        t.setShowVerticalLines(false);
        t.setSelectionBackground(selected);
        t.setSelectionForeground(ink);
        t.setFillsViewportHeight(true);
        t.putClientProperty("terminateEditOnFocusLost", true);
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                    int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, col);
                l.setOpaque(true);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBackground(header);
                l.setForeground(Color.WHITE);
                l.setFont(font(12, true));
                l.setBorder(new EmptyBorder(9, 5, 9, 5));
                return l;
            }
        });
        h.setPreferredSize(new Dimension(0, 36));
        return t;
    }
    static Color WHITE() {
        return PAPER;
    }
    static JScrollPane tableScroll(JTable t, int rows) {
        JScrollPane s = scroll(t);
        s.setBorder(new LineBorder(LINE));
        s.setPreferredSize(new Dimension(100, 36 + 30 * rows + 3));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36 + 30 * rows + 3));
        return s;
    }
    static DefaultTableModel model(Object[] cols, boolean edit) {
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return edit;
            }
        };
    }
    public Group10Scheduler() {
        super("Group 10 | CPU & Disk Scheduling Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1020, 700));
        setSize(1370, 880);
        setLocationRelativeTo(null);
        setContentPane(screen);
        home();
    }
    void showPage(JPanel p) {
        screen.removeAll();
        screen.add(p, BorderLayout.CENTER);
        screen.revalidate();
        screen.repaint();
    }
    JPanel shell(String section) {
        JPanel root = panel(new BorderLayout(), BG);
        JPanel bar = panel(new BorderLayout(), DARK);
        bar.setBorder(new EmptyBorder(15, 24, 15, 24));
        JPanel brand = vertical(DARK);
        add(brand, label("SCHEDULING LAB  |  GROUP 10", 17, true, Color.WHITE));
        add(brand, label("OPERATING SYSTEMS  •  CPU & DISK SIMULATOR", 11, false, PEACH));
        bar.add(brand, BorderLayout.WEST);
        bar.add(label(section, 13, true, BG), BorderLayout.EAST);
        root.add(bar, BorderLayout.NORTH);
        return root;
    }
    void home() {
        theme(null);
        JPanel root = shell("01  |  MAIN MENU");
        JPanel body = vertical(BG);
        body.setBorder(new EmptyBorder(28, 34, 30, 34));
        add(body, label("Choose a scheduling algorithm", 32, true, INK));
        gap(body, 6);
        add(body, label("Explore process execution and disk head movement with an interactive simulator.", 15, false,
                MUTED));
        gap(body, 25);
        JPanel groups = panel(new GridLayout(1, 2, 18, 0), BG);
        JPanel cpu = card("01  CPU Scheduling", "Process execution • time-based analysis"),
                disk = card("02  Disk Scheduling", "Request service • cylinder-based analysis");
        for (Scheduling.Algorithm a : Scheduling.Algorithm.values()) {
            theme(a);
            JPanel group = a.cpu ? cpu : disk;
            JPanel tile = new RoundedPanel(new BorderLayout(10, 0), PALE, LINE, 15);
            tile.setBorder(new EmptyBorder(14, 14, 14, 14));
            JPanel text = vertical(PALE);
            text.setOpaque(false);
            add(text, label(a.title, 17, true, DARK));
            gap(text, 4);
            String info = a == Scheduling.Algorithm.PRIORITY
                    ? "Highest priority ready process runs first"
                    : a == Scheduling.Algorithm.SJF
                            ? "Shortest available job runs to completion"
                            : a == Scheduling.Algorithm.CLOOK
                                    ? "Circular sweep between pending requests"
                                    : a == Scheduling.Algorithm.CSCAN
                                            ? "Circular sweep through disk boundaries"
                                            : "Requests served in arrival order";
            add(text, label(info, 12, false, MUTED));
            tile.add(text, BorderLayout.CENTER);
            JButton open = button("Open >", true);
            open.addActionListener(e -> open(a));
            tile.add(open, BorderLayout.EAST);
            tile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            add(group, tile);
            gap(group, 10);
        }
        theme(null);
        groups.add(cpu);
        groups.add(disk);
        groups.setMaximumSize(new Dimension(Integer.MAX_VALUE, 370));
        add(body, groups);
        gap(body, 24);
        JPanel row = panel(new FlowLayout(FlowLayout.LEFT, 10, 0), BG);
        JButton about = button("About", false), exit = button("Exit", false);
        about.addActionListener(e -> about());
        exit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Exit the simulator?", "Exit",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                dispose();
        });
        row.add(about);
        row.add(exit);
        add(body, row);
        gap(body, 16);
        add(body, label("5 ALGORITHMS  |  EDITABLE DATASETS  |  VISUAL RESULTS", 12, true, MUTED));
        root.add(scroll(body), BorderLayout.CENTER);
        showPage(root);
    }
    void about() {
        theme(null);
        JPanel root = shell("ABOUT");
        JPanel body = vertical(BG);
        body.setBorder(new EmptyBorder(28, 34, 28, 34));
        JButton back = button("Main Menu", false);
        back.addActionListener(e -> home());
        add(body, back);
        gap(body, 20);
        JPanel c = card("About", "CPU & Disk Scheduling Simulator • Operating Systems");
        add(c, label("Algorithms", 15, true, INK));
        for (Scheduling.Algorithm a : Scheduling.Algorithm.values()) {
            gap(c, 7);
            add(c, label((a.cpu ? "CPU  |  " : "DISK  |  ") + a.title, 14, false, INK));
        }
        gap(c, 18);
        add(c, label("Group members", 16, true, INK));
        gap(c, 6);
        JTextArea names = new JTextArea(6, 35);
        names.setFont(font(14, false));
        names.setForeground(INK);
        names.setBackground(BG);
        names.setLineWrap(true);
        names.setEditable(false);
        names.setText(
                "Abuda, Micah Isabelle F.\n"
                        + "Aquino, Naejhel Chae E.\n"
                        + "Aurelio, Ronette P.\n"
                        + "Ciar, Jade Anne Denise C.\n"
                        + "Reyes, Ryanne Aaliyah Nicole D.");
        add(c, scroll(names));
        add(body, c);
        root.add(scroll(body), BorderLayout.CENTER);
        showPage(root);
    }
    void open(Scheduling.Algorithm a) {
        theme(a);
        Module m = new Module(a);
        showPage(m.root);
    }
    static int number(Object o, String name, int min, int max) {
        String s = o == null ? "" : o.toString().trim();
        try {
            int n = Integer.parseInt(s);
            if (n < min || n > max)
                throw new NumberFormatException();
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a whole number from " + min + " to " + max + ".");
        }
    }
    static String fmt(double x) {
        return String.format(Locale.US, "%.2f", x);
    }
    class Module {
        final Scheduling.Algorithm a;
        final JPanel root, results;
        final JLabel status;
        final JTextField count = field("10", 5), head = field("", 6), size = field("", 6);
        final JComboBox<String> direction = new JComboBox<>(new String[]{"Increasing (right)", "Decreasing (left)"});
        final DefaultTableModel inputs;
        final JTable inputTable;
        final JScrollPane resultScroll;
        Module(Scheduling.Algorithm a) {
            this.a = a;
            root = shell(a.cpu ? "CPU SCHEDULING" : "DISK SCHEDULING");
            JPanel center = panel(new BorderLayout(0, 14), BG);
            center.setBorder(new EmptyBorder(16, 24, 20, 24));
            JPanel top = panel(new BorderLayout(12, 0), BG);
            JPanel heading = vertical(BG);
            JButton back = button("Main Menu", false);
            back.addActionListener(e -> home());
            add(heading, label(a.title, 26, true, INK));
            gap(heading, 3);
            add(heading, label(
                    a.cpu
                            ? "Time starts at 0  •  Context switch = 0  •  " + (a == Scheduling.Algorithm.PRIORITY
                                    ? "Smaller number = higher priority"
                                    : "Non-preemptive execution")
                            : "Cylinder range: 0 to disk size - 1  •  Circular wrap movement is counted",
                    12, false, MUTED));
            top.add(heading, BorderLayout.CENTER);
            top.add(back, BorderLayout.EAST);
            center.add(top, BorderLayout.NORTH);
            JPanel input = card(a.cpu ? "01  Process inputs" : "01  Disk inputs", "Minimum 10 entries • Maximum 100");
            JPanel row = panel(new FlowLayout(FlowLayout.LEFT, 7, 0), PAPER);
            row.add(label(a.cpu ? "Processes" : "Requests", 13, true, INK));
            row.add(count);
            JButton set = button("Set rows", false);
            row.add(set);
            add(input, row);
            gap(input, 10);
            if (!a.cpu) {
                JPanel fields = panel(new GridLayout(0, 2, 8, 8), PAPER);
                fields.add(label("Initial head", 13, true, INK));
                fields.add(head);
                fields.add(label("Disk size", 13, true, INK));
                fields.add(size);
                if (a != Scheduling.Algorithm.FCFS) {
                    fields.add(label("Direction", 13, true, INK));
                    direction.setFont(font(12, false));
                    direction.setBackground(PALE);
                    direction.setForeground(INK);
                    direction.setBorder(new LineBorder(LINE, 1, true));
                    fields.add(direction);
                }
                add(input, fields);
                gap(input, 10);
            }
            Object[] cols = a.cpu
                    ? (a == Scheduling.Algorithm.PRIORITY
                            ? new Object[]{"PID", "Arrival", "Burst", "Priority"}
                            : new Object[]{"PID", "Arrival", "Burst"})
                    : new Object[]{"Request #", "Cylinder"};
            inputs = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return a.cpu || c == 1;
                }
            };
            inputTable = table(inputs);
            inputTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            add(input, tableScroll(inputTable, a.cpu ? 10 : 8));
            gap(input, 8);
            add(input,
                    label(a.cpu
                            ? "Double-click a cell to edit. Burst must be positive."
                            : "Double-click a cylinder to edit. Repeats are allowed.", 12, false, MUTED));
            gap(input, 12);
            JPanel buttons = panel(new GridLayout(0, 1, 0, 7), PAPER);
            JButton run = button("Run Simulation", true), sample = button("Load Sample Data", false),
                    reset = button("Reset / Clear", false);
            buttons.add(run);
            buttons.add(sample);
            buttons.add(reset);
            add(input, buttons);
            gap(input, 10);
            status = label("Enter values or load the demonstration dataset.", 12, false, MUTED);
            add(input, status);
            gap(input, 6);
            JPanel left = panel(new BorderLayout(), BG);
            left.add(input, BorderLayout.NORTH);
            JScrollPane leftScroll = scroll(left);
            leftScroll.setPreferredSize(new Dimension(385, 500));
            leftScroll.setMinimumSize(new Dimension(320, 200));
            results = vertical(BG);
            resultScroll = scroll(results);
            resultScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, resultScroll);
            split.setBorder(null);
            final Color splitColor = BG;
            split.setUI(new BasicSplitPaneUI() {
                public BasicSplitPaneDivider createDefaultDivider() {
                    return new BasicSplitPaneDivider(this) {
                        public void paint(Graphics g) {
                            g.setColor(splitColor);
                            g.fillRect(0, 0, getWidth(), getHeight());
                        }
                    };
                }
            });
            split.setDividerSize(12);
            split.setContinuousLayout(true);
            split.setResizeWeight(0);
            split.setDividerLocation(390);
            center.add(split, BorderLayout.CENTER);
            root.add(center, BorderLayout.CENTER);
            set.addActionListener(e -> {
                try {
                    stopEdit();
                    resize(number(count.getText(), "Number of entries", 10, 100), true);
                    clearResults();
                    message("Rows updated. Enter values and run.", MUTED);
                } catch (IllegalArgumentException ex) {
                    error(ex);
                }
            });
            sample.addActionListener(e -> sample());
            reset.addActionListener(e -> {
                if (inputTable.isEditing())
                    inputTable.getCellEditor().cancelCellEditing();
                count.setText("10");
                head.setText("");
                size.setText("");
                direction.setSelectedIndex(0);
                resize(10, false);
                clearResults();
                message("Cleared. Enter a new dataset.", MUTED);
            });
            run.addActionListener(e -> run());
            resize(10, false);
            clearResults();
        }
        void stopEdit() {
            if (inputTable.isEditing() && !inputTable.getCellEditor().stopCellEditing())
                throw new IllegalArgumentException("Finish editing the selected cell.");
        }
        void resize(int n, boolean preserve) {
            if (!preserve)
                inputs.setRowCount(0);
            while (inputs.getRowCount() > n)
                inputs.removeRow(inputs.getRowCount() - 1);
            while (inputs.getRowCount() < n) {
                int i = inputs.getRowCount() + 1;
                inputs.addRow(a.cpu
                        ? (a == Scheduling.Algorithm.PRIORITY
                                ? new Object[]{"P" + i, "", "", ""}
                                : new Object[]{"P" + i, "", ""})
                        : new Object[]{i, ""});
            }
            count.setText("" + n);
        }
        void message(String s, Color color) {
            status.setText("<html><body style='width:245px'>" + s.replace("&", "&amp;").replace("<", "&lt;")
                    + "</body></html>");
            status.setForeground(color);
        }
        void error(Exception e) {
            message(e.getMessage(), RED);
            JOptionPane.showMessageDialog(Group10Scheduler.this, e.getMessage(), "Check input",
                    JOptionPane.WARNING_MESSAGE);
        }
        void sample() {
            if (inputTable.isEditing())
                inputTable.getCellEditor().cancelCellEditing();
            resize(10, false);
            if (a.cpu) {
                int[] ar = {2, 3, 4, 6, 6, 9, 12, 13, 18, 26}, bu = {7, 3, 1, 5, 2, 4, 6, 2, 3, 4},
                        pr = {4, 2, 1, 3, 2, 5, 1, 4, 2, 3};
                for (int i = 0; i < 10; i++) {
                    inputs.setValueAt(ar[i], i, 1);
                    inputs.setValueAt(bu[i], i, 2);
                    if (a == Scheduling.Algorithm.PRIORITY)
                        inputs.setValueAt(pr[i], i, 3);
                }
            } else {
                int[] q = {98, 183, 37, 122, 14, 124, 65, 67, 190, 45};
                head.setText("53");
                size.setText("200");
                direction.setSelectedIndex(0);
                for (int i = 0; i < 10; i++)
                    inputs.setValueAt(q[i], i, 1);
            }
            clearResults();
            message("Demonstration sample loaded (10 entries). Edit as needed.", GREEN);
        }
        void clearResults() {
            results.removeAll();
            JPanel c = card("02  Simulation results", "Awaiting a valid dataset");
            add(c, label("Ready when you are.", 22, true, INK));
            gap(c, 8);
            add(c, label("Enter your data or load the sample, then run the simulation.", 13, false, MUTED));
            gap(c, 20);
            add(c, label(a.cpu
                    ? "Results table  |  Gantt chart  |  Performance metrics"
                    : "Movement table  |  Head path graph  |  Seek metrics", 13, true, ACCENT));
            add(results, c);
            refresh();
        }
        void refresh() {
            results.revalidate();
            results.repaint();
            SwingUtilities.invokeLater(() -> resultScroll.getVerticalScrollBar().setValue(0));
        }
        void run() {
            try {
                stopEdit();
                int n = number(count.getText(), "Number of entries", 10, 100);
                if (n != inputs.getRowCount())
                    throw new IllegalArgumentException("Click Set rows to apply the entry count first.");
                if (a.cpu) {
                    List<Scheduling.Process> p = new ArrayList<>();
                    Set<String> ids = new HashSet<>();
                    for (int i = 0; i < n; i++) {
                        Object v = inputs.getValueAt(i, 0);
                        String pid = v == null ? "" : v.toString().trim();
                        if (pid.isEmpty() || pid.length() > 24 || pid.equalsIgnoreCase("IDLE") || !ids.add(pid))
                            throw new IllegalArgumentException(
                                    "Row " + (i + 1) + ": use a unique PID (1–24 characters; not IDLE).");
                        p.add(new Scheduling.Process(pid,
                                number(inputs.getValueAt(i, 1), "Arrival for " + pid, 0, 1000000),
                                number(inputs.getValueAt(i, 2), "Burst for " + pid, 1, 1000000),
                                a == Scheduling.Algorithm.PRIORITY
                                        ? number(inputs.getValueAt(i, 3), "Priority for " + pid, 0, 1000000)
                                        : 0));
                    }
                    cpuResults(Scheduling.cpu(a, p));
                } else {
                    int z = number(size.getText(), "Disk size", 2, 1000000),
                            h = number(head.getText(), "Initial head", 0, z - 1);
                    List<Integer> q = new ArrayList<>();
                    for (int i = 0; i < n; i++)
                        q.add(number(inputs.getValueAt(i, 1), "Request " + (i + 1), 0, z - 1));
                    diskResults(Scheduling.disk(a, q, h, z, direction.getSelectedIndex() == 0));
                }
                message("Simulation completed successfully.", GREEN);
            } catch (IllegalArgumentException e) {
                clearResults();
                error(e);
            }
        }
        void section(JPanel c) {
            add(results, c);
            gap(results, 12);
        }
        void cpuResults(Scheduling.CpuResult r) {
            results.removeAll();
            JPanel c = card("02  Process results",
                    "CT = completion  •  TAT = turnaround  •  WT = waiting  •  RT = response");
            boolean pr = a == Scheduling.Algorithm.PRIORITY;
            DefaultTableModel m = model(pr
                    ? new Object[]{"PID", "AT", "BT", "Priority", "CT", "TAT", "WT", "RT"}
                    : new Object[]{"PID", "AT", "BT", "CT", "TAT", "WT", "RT"}, false);
            for (int i = 0; i < r.processes.size(); i++) {
                Scheduling.Process p = r.processes.get(i);
                m.addRow(pr
                        ? new Object[]{p.pid, p.arrival, p.burst, p.priority, r.completion[i], r.tat(i), r.wt(i),
                                r.rt(i)}
                        : new Object[]{p.pid, p.arrival, p.burst, r.completion[i], r.tat(i), r.wt(i), r.rt(i)});
            }
            JTable t = table(m);
            add(c, tableScroll(t, Math.min(10, r.processes.size())));
            section(c);
            JPanel chart = card("03  Gantt chart",
                    "Scaled execution timeline • gray = idle • scroll horizontally for detail");
            Gantt g = new Gantt(r);
            JScrollPane gs = scroll(g);
            gs.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            gs.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            gs.setPreferredSize(new Dimension(600, 157));
            gs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 157));
            add(chart, gs);
            gap(chart, 6);
            JTextArea order = readout(sequence(r));
            add(chart, scrollText(order, 62));
            section(chart);
            JPanel metrics = card("04  Performance summary", "All time metrics use the same units as your input.");
            add(metrics,
                    metricGrid(new String[][]{{"Average TAT", fmt(r.avg(0)) + " units"},
                            {"Average WT", fmt(r.avg(1)) + " units"}, {"Average RT", fmt(r.avg(2)) + " units"},
                            {"CPU utilization", fmt(r.utilization()) + "%"},
                            {"Throughput", String.format(Locale.US, "%.4f", r.throughput()) + " proc/unit"},
                            {"Total elapsed", r.elapsed + " units"}, {"CPU busy", r.busy + " units"},
                            {"CPU idle", (r.elapsed - r.busy) + " units"}}));
            section(metrics);
            refresh();
        }
        void diskResults(Scheduling.DiskResult r) {
            results.removeAll();
            JPanel c = card("02  Movement breakdown",
                    "Boundary and wrap steps are counted, but are not extra requests.");
            DefaultTableModel m = model(new Object[]{"Step", "From", "To", "Movement", "Action"}, false);
            int i = 0;
            for (Scheduling.Step s : r.steps)
                m.addRow(new Object[]{++i, s.from, s.to, s.distance(), s.type});
            add(c, tableScroll(table(m), Math.min(10, r.steps.size())));
            gap(c, 12);
            add(c, label("Order of service (requests only)", 13, true, INK));
            gap(c, 5);
            add(c, scrollText(readout(join(r.order)), 58));
            section(c);
            JPanel chart = card("03  Disk head path",
                    "Cylinder position × step  •  dashed = wrap  •  " + (a == Scheduling.Algorithm.CSCAN
                            ? "diamond"
                            : a == Scheduling.Algorithm.CLOOK ? "rounded square" : "paw print") + " = request");
            DiskGraph graph = new DiskGraph(r, a);
            JScrollPane gs = scroll(graph);
            gs.setPreferredSize(new Dimension(600, 440));
            gs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 440));
            add(chart, gs);
            section(chart);
            JPanel metrics = card("04  Seek summary", "Average seek distance = total movement / number of requests.");
            add(metrics, metricGrid(new String[][]{{"Total head movement", r.total + " cylinders"},
                    {"Average seek distance", fmt(r.average()) + " cylinders"},
                    {"Requests serviced", String.valueOf(r.order.size())}, {"Initial head", String.valueOf(r.head)}}));
            section(metrics);
            refresh();
        }
    }
    static JTextArea readout(String s) {
        JTextArea t = new JTextArea(s);
        t.setEditable(false);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setBackground(BG);
        t.setForeground(INK);
        t.setFont(font(13, false));
        t.setBorder(new EmptyBorder(8, 9, 8, 9));
        return t;
    }
    static JScrollPane scrollText(JTextArea t, int height) {
        JScrollPane sp = scroll(t);
        sp.setPreferredSize(new Dimension(400, height));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return sp;
    }
    static String join(List<Integer> q) {
        StringJoiner j = new StringJoiner("  →  ");
        for (int x : q)
            j.add("" + x);
        return j.toString();
    }
    static String sequence(Scheduling.CpuResult r) {
        StringJoiner j = new StringJoiner("  →  ");
        for (Scheduling.Segment s : r.segments)
            j.add((s.process < 0 ? "IDLE" : r.processes.get(s.process).pid) + " [" + s.start + "–" + s.end + "]");
        return j.toString();
    }
    static JPanel metricGrid(String[][] data) {
        JPanel grid = panel(new GridLayout(0, 2, 8, 8), PAPER);
        for (String[] item : data) {
            JPanel p = roundedVertical(PALE, LINE);
            p.setBorder(new EmptyBorder(13, 15, 13, 15));
            add(p, label(item[0], 12, true, MUTED));
            gap(p, 4);
            add(p, label(item[1], 19, true, INK));
            grid.add(p);
        }
        return grid;
    }
    static void quality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    static class Gantt extends JPanel {
        final Scheduling.CpuResult r;
        Gantt(Scheduling.CpuResult r) {
            this.r = r;
            setBackground(PAPER);
            int min = Integer.MAX_VALUE;
            for (Scheduling.Segment s : r.segments)
                min = Math.min(min, s.end - s.start);
            int width = (int) Math.min(18000,
                    Math.max(650, Math.max(r.segments.size() * 48.0, 20.0 * r.elapsed / min)));
            setPreferredSize(new Dimension(width, 130));
            setToolTipText("Move over a segment for its time range");
        }
        int x(int t) {
            return 24 + (int) Math.round((getWidth() - 50) * (double) t / r.elapsed);
        }
        public String getToolTipText(MouseEvent e) {
            for (Scheduling.Segment s : r.segments)
                if (e.getX() >= x(s.start) && e.getX() <= x(s.end))
                    return (s.process < 0 ? "IDLE" : r.processes.get(s.process).pid) + ": " + s.start + " – " + s.end;
            return null;
        }
        protected void paintComponent(Graphics gr) {
            super.paintComponent(gr);
            Graphics2D g = (Graphics2D) gr.create();
            quality(g);
            g.setFont(font(12, true));
            int lastLabel = -100;
            for (Scheduling.Segment s : r.segments) {
                int l = x(s.start), w = Math.max(0, x(s.end) - l);
                String name = s.process < 0 ? "IDLE" : r.processes.get(s.process).pid;
                Color fill = s.process < 0 ? new Color(135, 136, 136) : COLORS[s.process % COLORS.length];
                g.setColor(fill);
                if (w > 0)
                    g.fillRoundRect(l, 24, Math.max(1, w - 1), 48, Math.min(8, w), 8);
                g.setColor(chartText(fill));
                if (w > g.getFontMetrics().stringWidth(name) + 5)
                    g.drawString(name, l + (w - g.getFontMetrics().stringWidth(name)) / 2, 53);
                g.setColor(INK);
                g.drawLine(l, 72, l, 80);
                String st = "" + s.start;
                if (l - lastLabel > g.getFontMetrics().stringWidth(st) + 12) {
                    g.drawString(st, l - g.getFontMetrics().stringWidth(st) / 2, 96);
                    lastLabel = l;
                }
            }
            int end = x(r.elapsed);
            g.setColor(INK);
            g.drawLine(end, 72, end, 80);
            String st = "" + r.elapsed;
            g.drawString(st, end - g.getFontMetrics().stringWidth(st) / 2, 112);
            g.setFont(font(11, false));
            g.setColor(MUTED);
            g.drawString("TIME", 24, 124);
            g.dispose();
        }
    }
    static class DiskGraph extends JPanel {
        final Scheduling.DiskResult r;
        final Scheduling.Algorithm algorithm;
        DiskGraph(Scheduling.DiskResult r, Scheduling.Algorithm a) {
            this.r = r;
            this.algorithm = a;
            setBackground(PAPER);
            setPreferredSize(new Dimension(620, Math.max(310, 78 + 30 * r.steps.size())));
            setToolTipText("Disk head movement");
        }
        void marker(Graphics2D g, int x, int y, boolean serviced) {
            g.setColor(DARK);
            if (!serviced) {
                g.setColor(PAPER);
                g.fillRect(x - 4, y - 4, 8, 8);
                g.setColor(DARK);
                g.drawRect(x - 4, y - 4, 8, 8);
                return;
            }
            if (algorithm == Scheduling.Algorithm.CSCAN) {
                Path2D p = new Path2D.Double();
                p.moveTo(x, y - 7);
                p.lineTo(x + 7, y);
                p.lineTo(x, y + 7);
                p.lineTo(x - 7, y);
                p.closePath();
                g.setColor(ACCENT);
                g.fill(p);
                g.setColor(DARK);
                g.draw(p);
            } else if (algorithm == Scheduling.Algorithm.CLOOK) {
                g.setColor(ACCENT);
                g.fillRoundRect(x - 6, y - 6, 12, 12, 5, 5);
                g.setColor(DARK);
                g.drawRoundRect(x - 6, y - 6, 12, 12, 5, 5);
            } else {
                Graphics2D paw = (Graphics2D) g.create();
                paw.translate(x, y);
                paw.setColor(PAPER);
                paw.fillOval(-9, -10, 18, 20);
                paw.setColor(DARK);
                for (int j = 0; j < 4; j++) {
                    double xx = -6 + j * 4, yy = (j == 0 || j == 3) ? -5 : -8;
                    paw.fill(new Ellipse2D.Double(xx - 1.7, yy - 2.1, 3.4, 4.8));
                }
                Path2D pad = new Path2D.Double();
                pad.moveTo(-5, 2);
                pad.curveTo(-3, -3, 3, -3, 5, 2);
                pad.curveTo(8, 7, 3, 8, 0, 6);
                pad.curveTo(-3, 8, -8, 7, -5, 2);
                pad.closePath();
                paw.fill(pad);
                paw.dispose();
            }
        }
        int x(int cylinder) {
            return 55 + (int) Math.round((getWidth() - 90) * (double) cylinder / (r.size - 1));
        }
        protected void paintComponent(Graphics gr) {
            super.paintComponent(gr);
            Graphics2D g = (Graphics2D) gr.create();
            quality(g);
            g.setFont(font(11, false));
            int dy = 30;
            int top = 40;
            int bottom = top + dy * r.steps.size();
            for (int i = 0; i <= 5; i++) {
                int v = (int) Math.round((r.size - 1) * i / 5.0), x = x(v);
                g.setColor(LINE);
                g.drawLine(x, top, x, bottom);
                g.setColor(MUTED);
                String s = "" + v;
                g.drawString(s, x - g.getFontMetrics().stringWidth(s) / 2, 20);
            }
            int prevX = x(r.head), prevY = top;
            g.setColor(ACCENT);
            marker(g, prevX, prevY, true);
            g.setColor(INK);
            g.drawString("H", 15, top + 4);
            g.drawString("" + r.head, prevX + 7, prevY - 4);
            int i = 0;
            for (Scheduling.Step s : r.steps) {
                int px = x(s.to), py = top + (++i) * dy;
                g.setColor(LINE);
                g.setStroke(new BasicStroke(1));
                g.drawLine(45, py, getWidth() - 25, py);
                g.setColor(MUTED);
                g.drawString("" + i, 15, py + 4);
                g.setColor(s.wrap ? MUTED : ACCENT);
                g.setStroke(s.wrap
                        ? new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{7, 5}, 0)
                        : new BasicStroke(2.5f));
                g.drawLine(prevX, prevY, px, py);
                g.setStroke(new BasicStroke(1.2f));
                marker(g, px, py, s.service);
                g.setColor(INK);
                String v = "" + s.to;
                int tx = px + 12;
                if (tx + g.getFontMetrics().stringWidth(v) > getWidth() - 4)
                    tx = px - 12 - g.getFontMetrics().stringWidth(v);
                g.drawString(v, tx, py - 4);
                prevX = px;
                prevY = py;
            }
            g.dispose();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("Label.font", font(13, false));
            UIManager.put("Button.font", font(13, true));
            UIManager.put("ComboBox.selectionBackground", PALE);
            UIManager.put("ComboBox.selectionForeground", INK);
            UIManager.put("TextField.selectionBackground", PEACH);
            UIManager.put("TextField.selectionForeground", INK);
            Group10Scheduler f = new Group10Scheduler();
            f.setVisible(true);
            if (args.length > 0 && args[0].equals("--preview")) {
                try {
                    String dir = args[1];
                    f.savePreview(dir + "/menu.png");
                    for (Scheduling.Algorithm alg : Scheduling.Algorithm.values()) {
                        theme(alg);
                        Module m = f.new Module(alg);
                        f.showPage(m.root);
                        m.sample();
                        m.run();
                        String key = alg.name().toLowerCase(Locale.ROOT);
                        f.savePreview(dir + "/" + key + ".png");
                        m.resultScroll.getVerticalScrollBar().setValue(m.results.getComponent(2).getY());
                        f.savePreview(dir + "/" + key + "-chart.png");
                        m.resultScroll.getVerticalScrollBar()
                                .setValue(m.resultScroll.getVerticalScrollBar().getMaximum());
                        f.savePreview(dir + "/" + key + "-summary.png");
                    }
                    f.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }
    void savePreview(String path) throws Exception {
        validate();
        BufferedImage img = new BufferedImage(getContentPane().getWidth(), getContentPane().getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        getContentPane().printAll(g);
        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }
}
