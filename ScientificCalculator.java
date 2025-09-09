import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * A scientific calculator with a polished UI and full functionality including
 * basic and scientific operations, degree/radian mode, memory functions, and expression parsing.
 */
public class ScientificCalculator extends JFrame implements ActionListener {

    private final JTextField display;
    private final JPanel buttonPanel;
    private boolean degreeMode = true;
    private boolean scientificMode = false;
    private String lastInput = "";
    private String memory = "0";
    private final List<String> history = new ArrayList<>();

    private static final String[] BASIC_BUTTONS = {
            "OFF", "Ans", "DEL", "C",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "(", ")", "%", "SCI"
    };

    private static final String[] SCI_BUTTONS = {
            "OFF", "Deg", "Rad", "MC", "MR", "C",
            "!", "nPr", "nCr", "exp", "log", "ln",
            "x^3", "x^2", "sqrt", "cbrt", "%", "π",
            "sin\u207B\u00B9", "cos\u207B\u00B9", "tan\u207B\u00B9", "sin", "cos", "tan",
            "7", "8", "9", "DEL", "Ans", "abs",
            "4", "5", "6", "*", "/", "mod",
            "1", "2", "3", "+", "-", "I/P",
            "0", ".", "(", ")", "=", "BSC"
    };

    /**
     * Constructor - sets up the UI components and event handling.
     */
    public ScientificCalculator() {
        super("Scientific Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(550, 670);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 2));

        display = new JTextField();
        display.setFont(new Font("Consolas", Font.BOLD, 28));
        display.setEditable(true);  // Made editable to allow typing
        display.setBackground(new Color(219, 242, 201));
        display.setForeground(Color.BLACK);
        display.setMargin(new Insets(12, 18, 12, 12));

        // Top panel holds display and mode indicator label (updated by timer)
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel modeLabel = new JLabel("Degrees", SwingConstants.RIGHT);
        modeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        modeLabel.setForeground(new Color(30, 70, 180));
        topPanel.add(modeLabel, BorderLayout.SOUTH);
        topPanel.add(display, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel for buttons
        buttonPanel = new JPanel();
        setupButtons();

        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // Timer to update mode label text every 200 ms
        javax.swing.Timer timer = new javax.swing.Timer(200, e -> {
            modeLabel.setText(degreeMode ? "Degrees" : "Radians");
        });
        timer.start();

        setVisible(true);
    }

    /**
     * Setup buttons according to current mode (scientific/basic).
     */
    private void setupButtons() {
        buttonPanel.removeAll();
        buttonPanel.setLayout(new GridLayout(scientificMode ? 8 : 6, scientificMode ? 6 : 4, 8, 8));

        // Colors matching your Casio fx-82 setup:
        Color blackBtn = new Color(0x17191a);      // true black for ops/functions
        Color grayBtn = new Color(0x444444);       // dark gray for number buttons
        Color greenBtn = new Color(0x7ccd75);      // soft Casio-style green for DEL/AC

        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        String[] buttons = scientificMode ? SCI_BUTTONS : BASIC_BUTTONS;

        for (String txt : buttons) {
            if (txt.isEmpty()) {
                buttonPanel.add(new JLabel(""));
            } else {
                JButton button = new JButton(txt);
                button.setFont(new Font("Segoe UI", Font.BOLD, 18));

                // --- Casio color logic ---
                if (txt.equals("C") || txt.equals("DEL") || txt.equals("AC")) {
                    button.setBackground(greenBtn);              // Green DEL/AC
                    button.setForeground(Color.BLACK);
                } else if (txt.matches("[0-9]|\\.")) {
                    button.setBackground(grayBtn);               // Gray for digits
                    button.setForeground(Color.WHITE);
                } else {
                    button.setBackground(blackBtn);              // Black for all other
                    button.setForeground(Color.WHITE);
                }

                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setOpaque(true);
                button.addActionListener(this);
                buttonPanel.add(button);
            }
        }
        buttonPanel.validate();
        buttonPanel.repaint();
    }

    /**
     * Handle button presses, including wrapping functions around current input.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        try {
            switch (cmd) {
                case "OFF" -> System.exit(0);
                case "C" -> {
                    display.setText("");
                    lastInput = "";
                }
                case "DEL" -> {
                    String s = display.getText();
                    if (!s.isEmpty()) display.setText(s.substring(0, s.length() - 1));
                }
                case "BSC" -> {
                    scientificMode = false;
                    setupButtons();
                }
                case "SCI" -> {
                    scientificMode = true;
                    setupButtons();
                }
                case "Deg" -> degreeMode = true;
                case "Rad" -> degreeMode = false;
                case "Ans" -> {
                    // Insert last answer inline at caret position
                    String ansText = lastInput.isEmpty() ? "0" : lastInput;
                    if (ansText.contains("=")) {
                        ansText = ansText.substring(ansText.indexOf('=') + 1);
                    }
                    int pos = display.getCaretPosition();
                    String currentText = display.getText();
                    String newText = currentText.substring(0, pos) + ansText + currentText.substring(pos);
                    display.setText(newText);
                    display.setCaretPosition(pos + ansText.length());
                }
                case "I/P" -> display.setText(lastInput);
                case "MC" -> memory = "0";
                case "MR" -> display.setText(memory);
                case "M+" -> memory = formatResult(safeParseDouble(memory) + safeParseDouble(display.getText()));
                case "M-" -> memory = formatResult(safeParseDouble(memory) - safeParseDouble(display.getText()));
                case "π" -> display.setText(display.getText() + Math.PI);
                case "mod", "%" -> display.setText(display.getText() + "%");

                // Wrap function calls around current input
                case "abs", "exp", "sqrt", "cbrt", "log", "ln",
                     "sin", "cos", "tan", "sin⁻¹", "cos⁻¹", "tan⁻¹",
                     "fact" -> wrapFunc(cmd);

                case "x^2" -> wrapWrap("^2");
                case "x^3" -> wrapWrap("^3");

                case "!" -> wrapFunc("fact");
                case "nPr" -> display.setText(display.getText() + "P");
                case "nCr" -> display.setText(display.getText() + "C");

                case "=" -> calculateResult();

                default -> display.setText(display.getText() + cmd);
            }
        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    /**
     * Wrap current display content in a function call.
     * E.g. if func="sin", and current input is "45", becomes "sin(45)".
     */
    private void wrapFunc(String func) {
        String s = display.getText();
        if (s.isEmpty()) {
            display.setText(func + "(");
        } else if (s.endsWith(")")) {
            display.setText(func + "(" + s + ")");
        } else {
            display.setText(func + "(" + s + ")");
        }
    }

    /**
     * Wrap current display content with suffix.
     * E.g., if suffix is "^2", and input is "5", result is "(5)^2".
     */
    private void wrapWrap(String suffix) {
        String s = display.getText();
        if (!s.isEmpty()) {
            display.setText("(" + s + ")" + suffix);
        }
    }

    /**
     * Insert last answer from history into the display.
     */
    private void insertLastAnswer() {
        if (!history.isEmpty()) {
            String[] parts = history.get(history.size() - 1).split("=");
            if (parts.length == 2) display.setText(parts[1]);
        }
    }

    /**
     * Compute the result of the expression currently shown in the display.
     * Supports permutation (P) and combination (C) syntaxes.
     */
    private void calculateResult() {
        String expr = display.getText();
        if (expr.isEmpty()) return;

        try {
            if (expr.contains("P")) {
                String[] parts = expr.split("P");
                if (parts.length == 2) {
                    int n = Integer.parseInt(parts[0]);
                    int r = Integer.parseInt(parts[1]);
                    display.setText(String.valueOf(permutation(n, r)));
                    lastInput = expr + "=" + display.getText();
                    history.add(lastInput);
                    return;
                } else {
                    throw new RuntimeException("Invalid nPr syntax");
                }
            }
            if (expr.contains("C")) {
                String[] parts = expr.split("C");
                if (parts.length == 2) {
                    int n = Integer.parseInt(parts[0]);
                    int r = Integer.parseInt(parts[1]);
                    display.setText(String.valueOf(combination(n, r)));
                    lastInput = expr + "=" + display.getText();
                    history.add(lastInput);
                    return;
                } else {
                    throw new RuntimeException("Invalid nCr syntax");
                }
            }

            double result = new ExpressionParser(expr, degreeMode).parse();

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                display.setText("Error");
            } else {
                String output = formatResult(result);
                display.setText(output);
                lastInput = expr + "=" + output;
                history.add(lastInput);
            }
        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    /**
     * Calculates nPr permutations.
     */
    private int permutation(int n, int r) {
        if (n < 0 || r < 0 || r > n) return 0;
        return (int) (factorial(n) / factorial(n - r));
    }

    /**
     * Calculates nCr combinations.
     */
    private int combination(int n, int r) {
        if (n < 0 || r < 0 || r > n) return 0;
        return (int) (factorial(n) / (factorial(r) * factorial(n - r)));
    }

    /**
     * Factorial function for positive integers.
     */
    private long factorial(int n) {
        if (n < 0) return 0;
        long f = 1;
        for (int i = 2; i <= n; i++) f *= i;
        return f;
    }

    /**
     * Safe parsing of double values. Returns 0 if parsing fails.
     */
    private double safeParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Formats output by rounding near-zero to zero,
     * removing trailing zeros for floating points,
     * and showing integer result if decimal part is zero.
     */
    private String formatResult(double val) {
        if (Math.abs(val) < 1E-10) return "0";
        if (val == Math.floor(val)) return Long.toString((long) val);
        return String.format("%.12f", val).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScientificCalculator::new);
    }
}


/**
 * ExpressionParser parses and evaluates mathematical expressions.
 * Supports functions: sin, cos, tan, asin, acos, atan, log, ln, sqrt, cbrt, exp, abs, fact.
 * Supports basic arithmetic operations +, -, *, /, %, ^.
 */
class ExpressionParser {

    private final String input;
    private int pos = -1, ch;
    private final boolean degreeMode;

    public ExpressionParser(String input, boolean degreeMode) {
        // Replace displayed inverse trig with internal function names for parsing:
        String replaced = input.replaceAll("sin\u207B\u00B9", "asin")
                                .replaceAll("cos\u207B\u00B9", "acos")
                                .replaceAll("tan\u207B\u00B9", "atan")
                                .replaceAll("√", "sqrt")
                                .replaceAll("∛", "cbrt")
                                .replaceAll("π", String.valueOf(Math.PI));
                replaced = replaced.replaceAll("(?<![a-zA-Z])e(?![a-zA-Z])", String.valueOf(Math.E));
        this.input = replaced;
        this.degreeMode = degreeMode;
        nextChar();
    }

    private void nextChar() {
        ch = (++pos < input.length()) ? input.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    public double parse() {
        double x = parseExpression();
        if (pos < input.length())
            throw new RuntimeException("Unexpected: '" + (char) ch + "'");
        return x;
    }

    // Grammar rules:

    private double parseExpression() {
        double x = parseTerm();
        for (; ; ) {
            if (eat('+')) x += parseTerm();
            else if (eat('-')) x -= parseTerm();
            else return x;
        }
    }

    private double parseTerm() {
        double x = parseFactor();
        for (; ; ) {
            if (eat('*')) x *= parseFactor();
            else if (eat('/')) {
                double d = parseFactor();
                if (d == 0) throw new ArithmeticException("Division by zero");
                x /= d;
            } else if (eat('%')) {
                double d = parseFactor();
                if (d == 0) throw new ArithmeticException("Modulo by zero");
                x %= d;
            } else return x;
        }
    }

    private double parseFactor() {
        if (eat('+')) return parseFactor();
        if (eat('-')) return -parseFactor();

        double x;
        int startPos = this.pos;

        if (eat('(')) {
            x = parseExpression();
            if (!eat(')')) throw new RuntimeException("Missing ')'");
        } else if ((ch >= '0' && ch <= '9') || ch == '.') {
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(input.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') {
            while (ch >= 'a' && ch <= 'z') nextChar();
            String func = input.substring(startPos, this.pos);

            if ("fact".equals(func)) {
                if (!eat('(')) throw new RuntimeException("Expected '(' after 'fact'");
                double arg = parseExpression();
                if (!eat(')')) throw new RuntimeException("Missing ')' after factorial arg");
                x = factorial(arg);
            } else {
                if (!eat('(')) throw new RuntimeException("Expected '(' after function");
                double arg = parseExpression();
                if (!eat(')')) throw new RuntimeException("Missing ')' after function argument");
                x = applyFunc(func, arg);
            }
        } else {
            throw new RuntimeException("Unexpected: '" + (char) ch + "'");
        }

        if (eat('^')) x = Math.pow(x, parseFactor());

        return x;
    }

    private double applyFunc(String func, double arg) {
        return switch (func) {
            case "abs" -> Math.abs(arg);
            case "exp" -> Math.exp(arg);
            case "sqrt" -> {
                if (arg < 0) throw new ArithmeticException("sqrt domain error");
                yield Math.sqrt(arg);
            }
            case "cbrt" -> Math.cbrt(arg);
            case "sin" -> degreeMode ? Math.sin(Math.toRadians(arg)) : Math.sin(arg);
            case "cos" -> degreeMode ? Math.cos(Math.toRadians(arg)) : Math.cos(arg);
            case "tan" -> degreeMode ? Math.tan(Math.toRadians(arg)) : Math.tan(arg);
            case "asin" -> {
                double a = Math.asin(arg);
                yield degreeMode ? Math.toDegrees(a) : a;
            }
            case "acos" -> {
                double a = Math.acos(arg);
                yield degreeMode ? Math.toDegrees(a) : a;
            }
            case "atan" -> {
                double a = Math.atan(arg);
                yield degreeMode ? Math.toDegrees(a) : a;
            }
            case "log10" -> {
                if (arg <= 0) throw new ArithmeticException("log10 domain error");
                yield Math.log10(arg);
            }
            case "log" -> {
                if (arg <= 0) throw new ArithmeticException("log domain error");
                yield Math.log(arg);
            }
            default -> throw new RuntimeException("Unknown function: " + func);
        };
    }

    private static double factorial(double n) {
        if (n < 0 || n != Math.floor(n)) throw new ArithmeticException("Factorial domain error");
        long res = 1;
        for (int i = 2; i <= (int) n; i++) res *= i;
        return res;
    }
}
