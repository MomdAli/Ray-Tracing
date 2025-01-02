import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JFrame;

import math.Color;
import math.Ray;
import math.RayHit;
import math.Vector3;
import world.Camera;
import world.Light;

public class Viewport extends JPanel {

    private static final int FRAME_RATE = 144;

    private Camera camera = Main.scene.getCamera();
    private Robot robot;

    private boolean captureCursor = true;

    private float resolution = 0.5f;
    private float movementSpeed = 10f;
    private float mouseSensitivity = .1f;

    long beginTime = 0;
    long deltaTime = 0;
    float frameRate = 0;

    private Vector3 cameraMotion = Vector3.zero();
    private float cameraYaw = camera.getYaw();
    private float cameraPitch = camera.getPitch();

    public Viewport(JFrame frame) {

        new Timer(1 / FRAME_RATE, e -> update()).start();

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    cameraMotion.set(Vector3.right());
                }
                else if (e.getKeyCode() == KeyEvent.VK_A) {
                    cameraMotion.set(Vector3.left());
                }
                else if (e.getKeyCode() == KeyEvent.VK_W) {
                    cameraMotion.set(Vector3.forward());
                }
                else if (e.getKeyCode() == KeyEvent.VK_S) {
                    cameraMotion.set(Vector3.backward());
                }
                else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    cameraMotion.set(Vector3.up());
                }
                else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    cameraMotion.set(Vector3.down());
                }
                else if (e.getKeyCode() == KeyEvent.VK_1) {
                    resolution = 1f;
                    System.out.println("Changed resolution to  " + resolution);
                }
                else if (e.getKeyCode() == KeyEvent.VK_2) {
                    resolution = .75f;
                    System.out.println("Changed resolution t " +  resolution);
                }
                else if (e.getKeyCode() == KeyEvent.VK_3) {
                    resolution = .5f;
                    System.out.println("Changed resolution to " + resolution);
                }
                else if (e.getKeyCode() == KeyEvent.VK_4) {
                    resolution = .25f;
                    System.out.println("Changed resolution to " + resolution);
                }
                else if (e.getKeyCode() == KeyEvent.VK_5) {
                    resolution = .125f;
                    System.out.println("Changed resolution to " + resolution);
                }
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    captureCursor = false;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_D ||
                    e.getKeyCode() == KeyEvent.VK_A ||
                    e.getKeyCode() == KeyEvent.VK_W ||
                    e.getKeyCode() == KeyEvent.VK_S ||
                    e.getKeyCode() == KeyEvent.VK_SPACE ||
                    e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    cameraMotion.set(Vector3.zero());
                }
            }
        });

        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (captureCursor) {
                    int centerX = frame.getX() + frame.getWidth() / 2;
                    int centerY = frame.getY() + frame.getHeight() / 2;

                    int mouseXOffset = e.getXOnScreen() - centerX;
                    int mouseYOffset = e.getYOnScreen() - centerY;
                    cameraYaw = camera.getYaw() + mouseXOffset * mouseSensitivity;
                    cameraPitch = (Math.min(90, Math.max(-90, camera.getPitch() + mouseYOffset * mouseSensitivity)));
                    robot.mouseMove(centerX, centerY);
                }
            }
        });

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!captureCursor)
                    captureCursor = true;
            }
        });

        try {
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * !Updates the viewport
     * ?This is the main loop of the program
     * *It updates the camera position and repaints the screen
     * *It also updates the frame rate
     */
    public void update() {
        // Timer end
        deltaTime = System.currentTimeMillis() - beginTime;
        frameRate = 1e3f / deltaTime;

        if (!cameraMotion.isZero()) {
            camera.move(cameraMotion.rotateYP(camera.getYaw(), 0).scale(movementSpeed * deltaTime / 1e3f));
        }

        camera.setYaw(cameraYaw);
        camera.setPitch(cameraPitch);

        repaint();

        float randomX = (float) Math.random() - 0.5f;
        float randomY = (float) Math.random() - 0.5f;


        Main.scene.getSolids().get(0).translate(Vector3.right().scale(0.1f));
        Main.scene.getGlobalLight().setPosition(Main.scene.getGlobalLight().getPosition().add(Vector3.right().scale(randomX)));
        Main.scene.getGlobalLight().setPosition(Main.scene.getGlobalLight().getPosition().add(Vector3.forward().scale(randomY)));

        // Timer start
        beginTime = System.currentTimeMillis();
    }

    @Override
    public void paintComponent(Graphics g2) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        int blockSize = (int) (1 / resolution);

        Graphics g = image.getGraphics();

        for (int i = 0; i < getWidth(); i += blockSize) {
            for (int j = 0; j < getHeight(); j += blockSize) {
                float x = (float)(i - getWidth()/2+getHeight()/2) / getHeight() * 2 - 1;
                float y =  -((float) j / getHeight() * 2 - 1);

                Vector3 eyePos = new Vector3(0, 0, (float)(-1/Math.tan(Math.toRadians(camera.getFov()/2))));

                Vector3 direction = new Vector3(x, y, 0)
                                .sub(eyePos)
                                .normalize()
                                .rotateYP(camera.getYaw(), camera.getPitch());

                RayHit rayHit = Main.scene.raycast(new Ray(eyePos.add(camera.getPosition()), direction));

                java.awt.Color color;
                if (rayHit != null) {
                    color = shadowRay(rayHit).toAWTColor();
                } else {
                    color = java.awt.Color.LIGHT_GRAY;
                }

                synchronized (g) {
                    g.setColor(color);
                    g.fillRect(i, j, blockSize + 1, blockSize + 1);
                }
            }
        }
        g2.drawImage(image, 0, 0, null);
        g2.setColor(java.awt.Color.YELLOW);
        g2.drawString("FPS: " + frameRate, 0, 10);
    }

    public Color shadowRay(RayHit hitSolid) {
        Light light = Main.scene.getGlobalLight();
        Vector3 lightDir = light.getPosition().sub(hitSolid.getHitPosition()).normalize();
        Ray lightRay = new Ray(light.getPosition(), lightDir);
        RayHit lightRayHit = Main.scene.raycast(lightRay);

        float brightness;
        if (lightRayHit != null && lightRayHit.getHitSolid() != hitSolid.getHitSolid()) {
            brightness = 0.3f;
        } else {
            brightness = Math.max(0.3f, Math.min(1, hitSolid.getNormal()
                        .dot(lightDir)) * light.getIntensity());
            // System.out.println(brightness);
        }
        return hitSolid.getHitSolid().getColor().mult(brightness);
    }
}
