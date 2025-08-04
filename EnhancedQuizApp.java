import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// Explicitly avoid java.util.Timer by importing only swing Timer
// We'll use fully qualified name if needed, but prefer javax.swing.Timer

public class EnhancedQuizApp extends JFrame {

    private String[] questions = {
        "What is the capital of France?",
        "Which planet is known as the Red Planet?",
        "Who wrote 'Romeo and Juliet'?",
        "What is 15 * 3?",
        "Which language is used to build Android apps?"
    };

    private String[][] options = {
        {"London", "Berlin", "Paris", "Madrid"},
        {"Venus", "Mars", "Jupiter", "Saturn"},
        {"Charles Dickens", "Mark Twain", "William Shakespeare", "Leo Tolstoy"},
        {"35", "45", "55", "65"},
        {"Python", "Java", "C++", "Swift"}
    };

    private int[] answers = {2, 1, 2, 1, 1}; // Index of correct option

    private int currentQuestion = 0;
    private int score = 0;
    private int timeLeft = 20; // seconds per quiz

    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup buttonGroup;
    private JButton nextButton, prevButton;
    private JLabel timerLabel;
    private JProgressBar progressBar;

    // ✅ Explicitly use javax.swing.Timer
    private javax.swing.Timer timer;

    public EnhancedQuizApp() {
        setTitle("Enhanced Quiz App");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(240, 248, 255)); // AliceBlue background
        setLayout(new BorderLayout());

        // === Header ===
        JLabel header = new JLabel("QUIZ APPLICATION", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(25, 25, 112));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        // === Center Panel ===
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(Color.WHITE);

        // Question Label
        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        centerPanel.add(questionLabel, BorderLayout.NORTH);

        // Options Panel
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        optionButtons = new JRadioButton[4];
        buttonGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton(" " + options[0][i]);
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            optionButtons[i].setBackground(Color.WHITE);
            optionButtons[i].setFocusPainted(false);
            buttonGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }
        centerPanel.add(optionsPanel, BorderLayout.CENTER);

        // Timer & Progress
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        timerLabel = new JLabel("⏱️ Time: 20s", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.DARK_GRAY);
        bottomPanel.add(timerLabel, BorderLayout.NORTH);

        progressBar = new JProgressBar(0, 20);
        progressBar.setValue(20);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(30, 144, 255));
        progressBar.setBackground(Color.LIGHT_GRAY);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // === Button Panel (South) ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        prevButton = new JButton("◀ Previous");
        prevButton.setFont(new Font("Arial", Font.PLAIN, 14));
        prevButton.setBackground(new Color(70, 130, 180));
        prevButton.setForeground(Color.WHITE);
        prevButton.setFocusPainted(false);
        prevButton.setEnabled(false);

        nextButton = new JButton("Next ▶");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 14));
        nextButton.setBackground(new Color(34, 139, 34));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // === Action Listeners ===
        nextButton.addActionListener(e -> nextQuestion());
        prevButton.addActionListener(e -> previousQuestion());

        // === Timer Setup ===
        // ✅ Use javax.swing.Timer explicitly
        timer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("⏱️ Time: " + timeLeft + "s");
                progressBar.setValue(timeLeft);

                if (timeLeft <= 10) {
                    timerLabel.setForeground(Color.ORANGE);
                }
                if (timeLeft <= 5) {
                    timerLabel.setForeground(Color.RED);
                }

                if (timeLeft <= 0) {
                    timer.stop();
                    nextQuestion(); // Auto-submit when time runs out
                }
            }
        });

        // === Initialize First Question ===
        showQuestion(0);
        startTimer();

        setVisible(true);
    }

    private void showQuestion(int index) {
        questionLabel.setText((index + 1) + ". " + questions[index]);
        buttonGroup.clearSelection();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(" " + options[index][i]);
        }
        progressBar.setValue(timeLeft);
        updateNavigationButtons();
    }

    private void startTimer() {
        timer.stop(); // Reset if already running
        timeLeft = 20;
        progressBar.setMaximum(20);
        progressBar.setValue(20);
        timerLabel.setText("⏱️ Time: 20s");
        timerLabel.setForeground(Color.DARK_GRAY);
        timer.start();
    }

    private void nextQuestion() {
        // Check answer before moving
        if (currentQuestion < questions.length) {
            if (getSelectedOption() == answers[currentQuestion]) {
                score++;
            }
        }

        currentQuestion++;
        if (currentQuestion >= questions.length) {
            showResult();
        } else {
            showQuestion(currentQuestion);
            startTimer();
        }
    }

    private void previousQuestion() {
        if (currentQuestion > 0) {
            currentQuestion--;
            showQuestion(currentQuestion);
            startTimer();
        }
    }

    private int getSelectedOption() {
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                return i;
            }
        }
        return -1; // No selection
    }

    private void updateNavigationButtons() {
        prevButton.setEnabled(currentQuestion > 0);
        nextButton.setText(currentQuestion == questions.length - 1 ? "Submit" : "Next ▶");
    }

    private void showResult() {
        timer.stop();
        StringBuilder result = new StringBuilder();
        result.append("<html><div style='text-align:center; font-size:16px;'>");
        result.append("<h2>🎉 Quiz Completed!</h2>");
        result.append("<p>Score: ").append(score).append(" / ").append(questions.length).append("</p>");
        result.append("<p>Accuracy: ").append(String.format("%.1f", (double) score / questions.length * 100))
              .append("%</p>");
        result.append("<p>Thank you for playing!</p>");
        result.append("</div></html>");

        JOptionPane.showMessageDialog(this, result.toString(), "Final Result",
                JOptionPane.INFORMATION_MESSAGE);

        int choice = JOptionPane.showConfirmDialog(this,
                "Do you want to restart the quiz?", "Restart?",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            restartQuiz();
        } else {
            System.exit(0);
        }
    }

    private void restartQuiz() {
        currentQuestion = 0;
        score = 0;
        showQuestion(0);
        startTimer();
    }

    // ============= MAIN METHOD =============
    public static void main(String[] args) {
        // ✅ Correct way to set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); // In case L&F fails
        }

        SwingUtilities.invokeLater(() -> new EnhancedQuizApp());
    }
}