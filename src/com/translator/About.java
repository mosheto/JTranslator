package com.translator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class About extends JPanel implements Runnable {

    private int x, y;
    private BufferedImage aboutImg;
    private BufferedImage backButtonUp, backButtonDown;

    private Display display;

    private Thread thread;
    private boolean running = false;

    private MouseManager mouseManager;

    public About(Display display) {
        this.display = display;
        this.mouseManager = new MouseManager();
        init();
    }

    private void init() {
        addMouseListener(mouseManager);
        addMouseMotionListener(mouseManager);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(Display.WIDTH, Display.HEIGHT));
        setDoubleBuffered(true);

        aboutImg = Translator.loadImage("/about.jpg");
        BufferedImage backButton = Translator.loadImage("/back.gif");

        backButtonUp = backButton.getSubimage(0, 0, 55, 33);
        backButtonDown = backButton.getSubimage(55, 0, 55, 33);

        x = 0;
        y = Display.HEIGHT;
    }

    //update
    private void tick() {
        --y;
        if (y == -Display.HEIGHT) y = Display.HEIGHT;

        if (isHovering() && mouseManager.isLeftPressed()) {
            display.show(Display.SETTINGS_GUI);
        }
    }

    //draw
    private void render(Graphics g) {
        g.drawImage(aboutImg, x, y, null);

        if (isHovering()) {
            g.drawImage(backButtonDown, 5, 5, null);

        } else {
            g.drawImage(backButtonUp, 5, 5, null);
        }
    }

    private boolean isHovering() {
        return new Rectangle(5, 5, 55, 33).contains(mouseManager.getMouseX(), mouseManager.getMouseY());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    @Override
    public void addNotify() {
        super.addNotify();

        if (thread == null) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    //Loop
    @Override
    public void run() {

        init();

        int fps = 30;
        double timePerTick = 1e9 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();

        while (running) {

            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            lastTime = now;

            if (delta >= 1) {
                tick();
                repaint();
                delta--;
            }
        }

        try {
            if (thread != null)
                this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MouseManager implements MouseListener, MouseMotionListener {

    private boolean leftPressed, rightPressed;
    private int mouseX, mouseY;

    @Override
    public void mousePressed(MouseEvent e) {

        if(e.getButton() == MouseEvent.BUTTON1)
            leftPressed = true;

        else if (e.getButton() == MouseEvent.BUTTON3)
            rightPressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if(e.getButton() == MouseEvent.BUTTON1)
            leftPressed = false;

        else if (e.getButton() == MouseEvent.BUTTON3)
            rightPressed = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.mouseX = e.getX();
        this.mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    //GETTERS AND SETTERS

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
