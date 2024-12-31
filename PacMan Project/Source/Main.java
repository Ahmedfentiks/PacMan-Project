import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

class PacMan extends JPanel implements KeyListener, ActionListener {
    class block {
        int x, y, height, startX, startY, VelocityX = 0, VelocityY = 0;
        float width = 0;
        char direction = 'U';
        Image image;

        block(int x, int y, float width, int height, Image image) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
            this.startX = x;
            this.startY = y;
        }

        void update_direction(char direction) {
            char previous_direction = this.direction;
            this.direction = direction;
            update_velocity();
            this.x += this.VelocityX;
            this.y += this.VelocityY;
            for (block wall : Wallset) {
                if (collision(this, wall)) {
                    this.x -= this.VelocityX;
                    this.y -= this.VelocityY;
                    this.direction = previous_direction;
                    update_velocity();
                    break;
                }
            }
        }

        void update_velocity() {
            if (direction == 'U') {
                VelocityX = 0;
                VelocityY = -tile / 4;
            } else if (direction == 'D') {
                VelocityX = 0;
                VelocityY = tile / 4;
            } else if (direction == 'L') {
                VelocityX = -tile / 4;
                VelocityY = 0;
            } else if (direction == 'R') {
                VelocityX = tile / 4;
                VelocityY = 0;
            }
        }

        void reset_position() {
            this.x = startX;
            this.y = startY;
        }
    }

    private int columns = 19, rows = 21, tile = 32;
    private int framewidth = tile * columns, frameheight = tile * rows;
    private Image WallImage, CherryImage, orangeGhostImage, redGhostImage, pinkGhostImage, blueGhostImage;
    private Image PacmanRightImage, PacmanLeftImage, PacmanUpImage, PacmanDownImage;
    private HashSet<block> Wallset, Foodset, Ghostset;
    private ArrayList<Point> emptySpaces;
    private block Pacman;
    private Timer timer, powerUpTimer;
    private char[] directions = { 'U', 'L', 'D', 'R' };
    private Random random = new Random();
    private char nextDirection = ' ';
    private int lives = 3, score = 0;
    private boolean gameOver = false;
    private boolean powerUpActive = false;
    String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    char[][] tileMap2D = new char[21][19];

    public void convert_2d() {
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < columns; ++c) {
                char obj = tileMap[r].charAt(c);
                tileMap2D[r][c] = obj;
            }
        }
    }

    public PacMan() {
        setPreferredSize(new Dimension(framewidth, frameheight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        WallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        PacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        PacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        PacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        PacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        CherryImage = new ImageIcon(getClass().getResource("./ff.png")).getImage();
        load_map();
        timer = new Timer(32, this);
        timer.start();
        for (block ghost : Ghostset) {
            char new_direction = directions[random.nextInt(4)];
            ghost.update_direction(new_direction);
        }
    }

    public void load_map() {

        convert_2d();
        Wallset = new HashSet<>();
        Foodset = new HashSet<>();
        Ghostset = new HashSet<>();
        emptySpaces = new ArrayList<>();

        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < columns; ++c) {
                char obj = tileMap2D[r][c];
                int x = c * tile, y = r * tile;

                if (obj == 'X')
                    Wallset.add(new block(x, y, tile, tile, WallImage));
                else if (obj == 'b')
                    Ghostset.add(new block(x, y, (tile - 2) + 0.01f, tile, blueGhostImage));
                else if (obj == 'p')
                    Ghostset.add(new block(x, y, (tile - 2) + 0.02f, tile, pinkGhostImage));
                else if (obj == 'o')
                    Ghostset.add(new block(x, y, (tile - 2) + 0.03f, tile, orangeGhostImage));
                else if (obj == 'r')
                    Ghostset.add(new block(x, y, (tile - 2) + 0.04f, tile, redGhostImage));
                else if (obj == 'P')
                    Pacman = new block(x, y, tile, tile, PacmanRightImage);
                else if (obj == ' ') {
                    Foodset.add(new block(x, y, tile, tile, CherryImage));
                    emptySpaces.add(new Point(c, r));
                }
            }
        }
    }

    public void move() {

        if (Pacman.x == 0) {
            Pacman.x = framewidth;
        } else if (Pacman.x == framewidth) {
            Pacman.x = 0;
        }

        if (Pacman.x % tile == 0 && Pacman.y % tile == 0) {
            if (canMove(nextDirection)) {
                Pacman.update_direction(nextDirection);
                if (Pacman.direction == 'U')
                    Pacman.image = PacmanUpImage;
                else if (Pacman.direction == 'D')
                    Pacman.image = PacmanDownImage;
                else if (Pacman.direction == 'L')
                    Pacman.image = PacmanLeftImage;
                else if (Pacman.direction == 'R')
                    Pacman.image = PacmanRightImage;
                nextDirection = ' ';
            }
        }

        Pacman.x += Pacman.VelocityX;
        Pacman.y += Pacman.VelocityY;

        for (block wall : Wallset) {
            if (collision(Pacman, wall)) {
                Pacman.x -= Pacman.VelocityX;
                Pacman.y -= Pacman.VelocityY;
                break;
            }
        }

        for (block ghost : Ghostset) {

            if (ghost.y == 9 * tile && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.update_direction('U');
            }

            if (collision(Pacman, ghost)) {
                if (powerUpActive) {
                    Ghostset.remove(ghost);
                    score += 50;
                    break;
                } else {
                    lives--;
                    resetPositions();
                    if (lives == 0) {
                        gameOver = true;
                        return;
                    }
                }
            }

            ghost.x += ghost.VelocityX;
            ghost.y += ghost.VelocityY;
            for (block wall : Wallset) {
                if (collision(ghost, wall) || ghost.x < 0 || ghost.x + ghost.width >= framewidth) {
                    ghost.x -= ghost.VelocityX;
                    ghost.y -= ghost.VelocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.update_direction(newDirection);
                }
            }
        }

        block foodEaten = null;
        for (block food : Foodset) {
            if (collision(Pacman, food)) {
                score += 5;
                if (score % 500 == 0 && !powerUpActive)
                    activatePowerUp();

                foodEaten = food;

                if (score % 500 == 0)
                    add_blocks();
                break;
            }
        }
        Foodset.remove(foodEaten);

        if (Foodset.isEmpty()) {
            load_map();
            resetPositions();
        }
    }

    private void activatePowerUp() {
        powerUpActive = true;
        powerUpTimer = new Timer(5000, e -> deactivatePowerUp());
        powerUpTimer.setRepeats(false);
        powerUpTimer.start();
        for (block ghost : Ghostset) {
            ghost.image = new ImageIcon(getClass().getResource("./scaredGhost.png")).getImage();
        }
    }

    private void deactivatePowerUp() {
        powerUpActive = false;
        powerUpTimer.stop();
        for (block ghost : Ghostset) {
            if (Math.abs(ghost.width - 30.01) < 0.01)
                ghost.image = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
            else if (Math.abs(ghost.width - 30.02) < 0.01)
                ghost.image = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
            else if (Math.abs(ghost.width - 30.03) < 0.01)
                ghost.image = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
            else if (Math.abs(ghost.width - 30.04) < 0.01)
                ghost.image = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        }
    }

    private void add_blocks() {
        boolean check = false;
        while (!check) {
            Point randomSpace = emptySpaces.get(random.nextInt(emptySpaces.size()));
            int rows = randomSpace.y;
            int col = randomSpace.x;
            block wall = null;
            block food = null;
            char obj = tileMap2D[rows][col];
            if (obj == ' ') {
                tileMap2D[rows][col] = 'X';
                wall = new block(col * tile, rows * tile, tile, tile, WallImage);
            
                block foodToRemove = null;

                for (block foods : Foodset) {
                    if (foods.x == col * tile && foods.y == rows * tile) {
                        foodToRemove = foods;
                        break;
                    }
                }
                if (foodToRemove != null) {
                    Foodset.remove(foodToRemove);
                }
            
                Wallset.add(wall);
                emptySpaces.remove(randomSpace);
                check = true;
            }
            
        }

    }

    private boolean canMove(char direction) {
        int newX = Pacman.x, newY = Pacman.y;
        if (direction == 'U')
            newY -= tile;
        else if (direction == 'D')
            newY += tile;
        else if (direction == 'L')
            newX -= tile;
        else if (direction == 'R')
            newX += tile;

        block tempBlock = new block(newX, newY, Pacman.width, Pacman.height, null);
        for (block wall : Wallset) {
            if (collision(tempBlock, wall))
                return false;
        }
        return true;
    }

    public boolean collision(block a, block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void resetPositions() {
        Pacman.reset_position();
        Pacman.VelocityX = 0;
        Pacman.VelocityY = 0;
        Pacman.direction = 'R';
        Pacman.image = PacmanRightImage;
        for (block ghost : Ghostset) {
            ghost.reset_position();
            char new_direction = directions[random.nextInt(4)];
            ghost.update_direction(new_direction);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for (block wall : Wallset)
            g.drawImage(wall.image, wall.x, wall.y, (int) wall.width, wall.height, null);
        for (block ghost : Ghostset)
            g.drawImage(ghost.image, ghost.x, ghost.y, (int) ghost.width, ghost.height, null);
        g.drawImage(Pacman.image, Pacman.x, Pacman.y, (int) Pacman.width, Pacman.height, null);

        for (block food : Foodset)
            g.drawImage(food.image, food.x, food.y, (int) food.width, food.height, null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (gameOver)
            g.drawString("Game Over | Score: " + score, tile / 2, tile / 2);
        else
            g.drawString("Lives: " + lives + " | Score: " + score, tile / 2, tile / 2);

        if (powerUpActive) {
            g.setColor(Color.YELLOW);
            g.drawString("Power-Up Active!", framewidth / 2 - 50, tile / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver)
            timer.stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            load_map();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            timer.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP)
            nextDirection = 'U';
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            nextDirection = 'D';
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            nextDirection = 'L';
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            nextDirection = 'R';

    }
}

class Main {
    public static void main(String[] args) {
        int columns = 19, rows = 21, tile = 32;
        int framewidth = tile * columns, frameheight = tile * rows;

        JFrame frame = new JFrame("Pac-Man Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(framewidth, frameheight);

        PacMan game = new PacMan();
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}