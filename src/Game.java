import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {

    public static JFrame frame;
    private final int WIDTH = 190;
    private Thread thread;
    private boolean isRunning = true;
    private final int HEIGHT = 120;
    private final int SCALE = 7;

    private BufferedImage image;

    private Spritesheet sheet;
    private BufferedImage[] player;
    private int frames = 0;
    private int maxFrames = 7; // velocidade
    private int curAnimation = 0, maxAnimation = 8; // número de sprites player

    public Game() {
        sheet = new Spritesheet("/Spritesheet.png");
        player = new BufferedImage[maxAnimation];

        setSprites(player, 0, 20, 0);

        this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        initFrame();
        image = new BufferedImage(190, 120, BufferedImage.TYPE_INT_RGB);
    }

    public void setSprites(BufferedImage[] value, int loops, int j, int k) {
        for (int i = loops; i < value.length; i++) {
            player[i] = sheet.getSprite(j, k, 20, 20);
            j = j + 20;
            if (j > 140) {
                k = k + 20;
                j = 0;
            }
        }

    }

    public void initFrame() {
        frame = new JFrame("SexyGirls");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();

    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }

    public void tick() {
        frames++;
        if (frames > maxFrames) {
            frames = 0;
            curAnimation++;
            if (curAnimation >= maxAnimation) {
                curAnimation = 0;

            }
        }

    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = image.getGraphics();
        g.setColor(new Color(71, 74, 130));
        g.fillRect(0, 0, 190, 120);

        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(Math.toRadians(0), 90 + 10, 90 + 10);

        int j = 10;

        for (int i = 0; i < 7; i++) {
            g.drawImage(player[curAnimation], j, 50, null);
            j = j + 25;
        }

        g.dispose();

        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
        bs.show();
    }

    @Override
    public void run() {

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                tick();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer > -1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }

        }
        stop();

    }

    public static void main(String[] args) throws Exception {
        Game game = new Game();
        game.start();

    }
}
