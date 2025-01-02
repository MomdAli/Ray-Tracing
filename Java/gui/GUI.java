import javax.swing.JFrame;

public class GUI extends JFrame {

    public Viewport viewport;

    public GUI() {
        super("Ray Tracing");
        init();

        viewport.update();
    }

    public void init() {
        setSize(800, 450);
        setDefaultCloseOperation(3);
        setVisible(true);

        viewport = new Viewport(this);
        setContentPane(viewport);
    }
}
