import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    private final int TILE_SIZE = 25;
    private final int WIDTH = 20;   // 20 tiles width
    private final int HEIGHT = 20;  // 20 tiles height
    private final int SCREEN_WIDTH = WIDTH * TILE_SIZE;
    private final int SCREEN_HEIGHT = HEIGHT * TILE_SIZE;
    private final int DELAY = 110;

    private final int x[] = new int[WIDTH * HEIGHT];
    private final int y[] = new int[WIDTH * HEIGHT];

    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;

    private char direction = 'R'; // Start moving right
    private boolean running = false;

    private Timer timer;
    private Random random;

    public SnakeGame() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT + 40));
        this.setBackground(new Color(255, 253, 246)); // Cream background for cuteness
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        placeApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void placeApple() {
        appleX = random.nextInt(WIDTH) * TILE_SIZE;
        appleY = random.nextInt(HEIGHT) * TILE_SIZE;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // Draw apple (cute red circle with glossy effect)
            g.setColor(new Color(237, 76, 103)); // pastel red apple
            g.fillOval(appleX + 4, appleY + 4, TILE_SIZE - 8, TILE_SIZE - 8);
            g.setColor(new Color(255, 183, 197)); // light shine
            g.fillOval(appleX + 7, appleY + 7, TILE_SIZE / 3, TILE_SIZE / 3);

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    // Head - cute face with eyes
                    g.setColor(new Color(52, 152, 219)); // bright blue head
                    g.fillRoundRect(x[i], y[i], TILE_SIZE, TILE_SIZE, 15, 15);
                    // Eyes
                    g.setColor(Color.white);
                    g.fillOval(x[i] + 6, y[i] + 6, 6, 6);
                    g.fillOval(x[i] + 13, y[i] + 6, 6, 6);
                    g.setColor(Color.black);
                    g.fillOval(x[i] + 8, y[i] + 8, 3, 3);
                    g.fillOval(x[i] + 15, y[i] + 8, 3, 3);
                    // Smile
                    g.setColor(new Color(255, 105, 180)); // pink smile
                    g.drawArc(x[i] + 6, y[i] + 14, 13, 6, 0, -180);
                } else {
                    // Body - lighter blue rectangles with rounded corners
                    g.setColor(new Color(135, 206, 250)); // sky blue body
                    g.fillRoundRect(x[i], y[i], TILE_SIZE, TILE_SIZE, 15, 15);
                }
            }

            // Draw grid lightly (for aesthetic)
            g.setColor(new Color(225, 225, 225));
            for (int i = 0; i < HEIGHT; i++) {
                g.drawLine(0, i * TILE_SIZE, SCREEN_WIDTH, i * TILE_SIZE);
            }
            for (int i = 0; i < WIDTH; i++) {
                g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, SCREEN_HEIGHT);
            }

            // Draw score with cute font
            g.setColor(new Color(52, 152, 219));
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String scoreText = "Score: " + applesEaten;
            g.drawString(scoreText, (SCREEN_WIDTH - metrics.stringWidth(scoreText)) / 2, SCREEN_HEIGHT + 30);

        } else {
            gameOver(g);
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - TILE_SIZE;
                break;
            case 'D':
                y[0] = y[0] + TILE_SIZE;
                break;
            case 'L':
                x[0] = x[0] - TILE_SIZE;
                break;
            case 'R':
                x[0] = x[0] + TILE_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            placeApple();
        }
    }

    public void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // Check if head touches left or right border
        if (x[0] < 0) {
            running = false;
        }
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }
        // Check if head touches top or bottom border
        if (y[0] < 0) {
            running = false;
        }
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Display game over text cute style
        g.setColor(new Color(237, 76, 103));
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        String msg = "Game Over!";
        g.drawString(msg, (SCREEN_WIDTH - metrics1.stringWidth(msg)) / 2, SCREEN_HEIGHT / 2);

        g.setColor(new Color(52, 152, 219));
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String scoreMsg = "Your score: " + applesEaten;
        g.drawString(scoreMsg, (SCREEN_WIDTH - metrics2.stringWidth(scoreMsg)) / 2, SCREEN_HEIGHT / 2 + 40);

        String restartMsg = "Press ENTER to Restart";
        g.drawString(restartMsg, (SCREEN_WIDTH - metrics2.stringWidth(restartMsg)) / 2, SCREEN_HEIGHT / 2 + 80);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if (!running) {
                        // Restart the game
                        bodyParts = 6;
                        applesEaten = 0;
                        direction = 'R';
                        for (int i = 0; i < bodyParts; i++) {
                            x[i] = 0;
                            y[i] = 0;
                        }
                        startGame();
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cute Snake Game");
        SnakeGame gamePanel = new SnakeGame();
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null); // center window
        frame.setVisible(true);
    }
}