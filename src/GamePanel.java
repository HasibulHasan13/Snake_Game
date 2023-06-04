import java.awt.*;
import java.awt.event.*;
import java.net.URI;

import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 625; // Updated height to include scoreboard
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * (SCREEN_HEIGHT - UNIT_SIZE)) / UNIT_SIZE; // Adjusted calculation for game units
    static final int DELAY = 160;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    private Image headImage;
    private Image appleImage;
    int highScore = 0; // Added high score variable
    boolean startScreen = true; // Added start screen flag
    String websiteURL = "https://sites.google.com/diu.edu.bd/olivesnakey/home"; // Replace with your desired website URL
    
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        headImage = new ImageIcon("head13.jpg").getImage();
        appleImage = new ImageIcon("apple1.jpg").getImage();
        if (!startScreen) {
            startGame();
        }
    }

    public void startGame() {
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0; // Start from the first column
            y[i] = UNIT_SIZE; // Start from the second row
        }
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (startScreen) {
            drawStartScreen(g);
        } else {
            draw(g);
        }
    }

    public void draw(Graphics g) {
        if (running) {
            /*for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, UNIT_SIZE, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE + UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE + UNIT_SIZE);
            }*/
            /*g.setColor(Color.green);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);*/
        	g.drawImage(appleImage, appleX, appleY, UNIT_SIZE, UNIT_SIZE, null);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.drawImage(headImage, x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
                } else {
                    g.setColor(Color.decode("#4e8640"));
                    g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Draw the scoreboard background
            g.setColor(Color.white);
            g.fillRect(0, 0, SCREEN_WIDTH, UNIT_SIZE);

            // Draw the scoreboard text
            g.setColor(Color.red);
            g.setFont(new Font("Int Free", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 30, UNIT_SIZE - 5);
            g.drawString("High Score: " + highScore, 440, UNIT_SIZE - 5);
        } else {
            gameOver(g);
        }
    }

    public void drawStartScreen(Graphics g) {
    	// Load the start screen image
        Image startScreenImage = new ImageIcon("snake.png").getImage();
        
        // Draw the start screen image
        g.drawImage(startScreenImage, 0, 0, SCREEN_WIDTH/2+280, SCREEN_HEIGHT/2+170, null);
        
        // Game title
        g.setColor(Color.decode("#ffffff"));
        g.setFont(new Font("Int Free", Font.BOLD, 35));
        FontMetrics titleMetrics = getFontMetrics(g.getFont());
        String title = "Welcome to Olive Snakey";
        int titleX = (SCREEN_WIDTH - titleMetrics.stringWidth(title)) / 2;
        int titleY = SCREEN_HEIGHT / 2 + 200;
        g.drawString(title, titleX, titleY);

        // Play text
        g.setColor(Color.green);
        g.setFont(new Font("Int Free", Font.ITALIC, 30));
        FontMetrics playMetrics = getFontMetrics(g.getFont());
        String playText = "Press P to Play";
        int playX = (SCREEN_WIDTH - playMetrics.stringWidth(playText)) / 2;
        int playY = SCREEN_HEIGHT / 2 + 250;
        g.drawString(playText, playX, playY);

        // Exit text
        g.setColor(Color.red);
        g.setFont(new Font("Int Free", Font.ITALIC, 30));
        FontMetrics exitMetrics = getFontMetrics(g.getFont());
        String exitText = "Press E to Exit";
        int exitX = (SCREEN_WIDTH - exitMetrics.stringWidth(exitText)) / 2;
        int exitY = SCREEN_HEIGHT / 2 + 280;
        g.drawString(exitText, exitX, exitY);
    }


    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) ((SCREEN_HEIGHT - UNIT_SIZE) / UNIT_SIZE)) * UNIT_SIZE + UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        // Check if head touches the borders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < UNIT_SIZE || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            if (applesEaten > highScore) {
                highScore = applesEaten;
            }
        }
    }

    public void gameOver(Graphics g) {
        // Score
        g.setColor(Color.green);
        g.setFont(new Font("Int Free", Font.BOLD, 50));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        int scoreX = (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2;
        int scoreY = SCREEN_HEIGHT / 2 - metrics1.getHeight() - 125;
        g.drawString("Score: " + applesEaten, scoreX, scoreY);

        // High Score text
        g.setColor(Color.orange);
        g.setFont(new Font("Int Free", Font.BOLD, 50));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("High Score: " + highScore, (SCREEN_WIDTH - metrics2.stringWidth("High Score: " + highScore)) / 2, SCREEN_HEIGHT / 2 - 100);

        // Game over text
        g.setColor(Color.red);
        g.setFont(new Font("Int Free", Font.BOLD, 75));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics3.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        g.setColor(Color.white); // Restart message
        g.setFont(new Font("Int Free", Font.BOLD, 25));
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        g.drawString("Press Enter to Restart", (SCREEN_WIDTH - metrics4.stringWidth("Press Enter to Restart")) / 2, SCREEN_HEIGHT / 2 + 70 );
        // Exit message
        g.drawString("Or", (SCREEN_WIDTH - metrics4.stringWidth("Or")) / 2, SCREEN_HEIGHT / 2 + 110);
        // Exit message
        g.drawString("Press Esc to Exit", (SCREEN_WIDTH - metrics4.stringWidth("Press Esc to Exit")) / 2, SCREEN_HEIGHT / 2 + 150);
        g.drawString("For More Information Click Here", (SCREEN_WIDTH - metrics4.stringWidth("For More Information Click Here")) / 2, SCREEN_HEIGHT / 2 + 200);
     // Button
        g.setColor(Color.blue);
        g.setFont(new Font("Int Free", Font.BOLD, 20));
        FontMetrics buttonMetrics = getFontMetrics(g.getFont());
        String buttonText = "Visit Website";
        int buttonX = (SCREEN_WIDTH - buttonMetrics.stringWidth(buttonText)) / 2;
        int buttonY = SCREEN_HEIGHT / 2 + 250;
        g.drawString(buttonText, buttonX, buttonY);

        // Draw a rectangle around the button
        //g.drawRect(buttonX - 10, buttonY - buttonMetrics.getHeight() + 5, buttonMetrics.stringWidth(buttonText) + 20, buttonMetrics.getHeight() - 10);

        // Get the bounds of the button
        Rectangle buttonBounds = new Rectangle(buttonX - 10, buttonY - buttonMetrics.getHeight() + 5, buttonMetrics.stringWidth(buttonText) + 20, buttonMetrics.getHeight() - 10);

        // Add a mouse listener to the panel
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if the mouse click is within the bounds of the button
                if (buttonBounds.contains(e.getPoint())) {
                    try {
                        // Open the website link in the default browser
                        Desktop.getDesktop().browse(new URI(websiteURL));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void restartGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = false;
        timer.stop();
        startGame();
        repaint();
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
                case KeyEvent.VK_P:
                    if (startScreen) {
                        startScreen = false;
                        startGame();
                    }
                    break;
                case KeyEvent.VK_E:
                    System.exit(0);
                    break;
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
                case KeyEvent.VK_SPACE:
                    running = !running;
                    if (running)
                        timer.start();
                    else
                        timer.stop();
                    break;
                case KeyEvent.VK_ENTER:
                    if (!running) {
                        restartGame();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;
            }
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
