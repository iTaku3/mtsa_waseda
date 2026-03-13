package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import ltsa.updatingControllers.synthesis.UpdatingControllersAnimatorUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by lnahabedian on 08/08/16.
 */
public class PainterEnvironmentUI extends JFrame{

    protected JPanel animation;
    protected JSplitPane split;

    private JLabel arm1;
    private JLabel arm2;
    private JLabel product1;
    private JLabel product2;

    private BufferedImage imageArm1;
    private BufferedImage imageArm2;
    private BufferedImage usedArm1;
    private BufferedImage usedArm2;
    private BufferedImage armFailed1;
    private BufferedImage armFailed2;
    private JButton pauseButton;


    public PainterEnvironmentUI(String name) {
        super(name);

        // time input
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setMinimumSize(new Dimension(10, 20));
        FlowLayout fl_panel = new FlowLayout(FlowLayout.LEADING, 5, 5);
        panel.setLayout(fl_panel);

        pauseButton = new JButton("Pause Environment Simulation");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                UpdatingControllersAnimatorUtils.pauseEnvironment = 1 - UpdatingControllersAnimatorUtils.pauseEnvironment;
                if (UpdatingControllersAnimatorUtils.pauseEnvironment == 1){
                    pauseButton.setText("Resume Environment Simulation");
                } else {
                    pauseButton.setText("Pause Environment Simulation");
                }


            }
        });
        panel.add(pauseButton);

        SpinnerModel model = new SpinnerNumberModel(3,0,10,1);
        UpdatingControllersAnimatorUtils.environmentSpeed = new JSpinner(model);
        JLabel speedLabel = new JLabel("Environment Speed:");

        panel.add(pauseButton);
        panel.add(speedLabel);
        panel.add(UpdatingControllersAnimatorUtils.environmentSpeed);
        //

        this.imageArm1 = loadImage("Robotic-arm1.png");
        this.imageArm2 = loadImage("Robotic-arm2.png");

        this.usedArm1 = loadImage("Robotic-arm1_used.png");
        this.usedArm2 = loadImage("Robotic-arm2_used.png");

        this.armFailed1 = loadImage("Robotic-arm1Failed.png");
        this.armFailed2 = loadImage("Robotic-arm2Failed.png");


        arm1 = new JLabel(new ImageIcon(imageArm1.getScaledInstance(250, 200, Image.SCALE_SMOOTH)));
        arm2 = new JLabel(new ImageIcon(imageArm2.getScaledInstance(250, 200, Image.SCALE_SMOOTH)));
        arm1.setBounds( 20,30,250,200);
        arm2.setBounds( 270,30,250,200);

        product1 = new JLabel();
        product2 = new JLabel();
        product1.setBounds(170,150,70,50);
        product2.setBounds(300,150,70,50);

        animation = new JPanel();
        animation.setBackground(Color.WHITE);
        animation.setLayout(null);
        animation.add(this.arm1);

        animation.add(product1);
        animation.add(product2);
        animation.add(this.arm2);
        setVisible(true);

        animation.setVisible(true);

        split = new JSplitPane();
        split.setResizeWeight(0.7);
        split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        split.setLeftComponent(animation);


        UpdatingControllersAnimatorUtils.actualEnvironmentActionsPanel.setLayout(new BoxLayout(UpdatingControllersAnimatorUtils.actualEnvironmentActionsPanel, BoxLayout.Y_AXIS));

        UpdatingControllersAnimatorUtils.actualEnvironmentActionsPanel.setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(UpdatingControllersAnimatorUtils.actualEnvironmentActionsPanel);
        split.setRightComponent(scrollPane);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(800, 300);
        setLayout(new BorderLayout(5,5));
        add(panel, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);


        revalidate();
        repaint();

    }

    public void setProductImage(int line, BufferedImage image){

        ImageIcon product = new ImageIcon(image.getScaledInstance(70, 50, Image.SCALE_SMOOTH));
        if (line == 1){
            product1.setIcon(product);
            product1.setVisible(true);
        } else {
            product2.setIcon(product);
            product2.setVisible(true);
        }

        revalidate();
        repaint();
    }

    public void removeProduct(int line) {
        if (line == 1){
            product1.setVisible(false);
        } else {
            product2.setVisible(false);
        }

        revalidate();
        repaint();
    }

    public void downArm(int line) {
        if (line == 1){
            arm1.setIcon(new ImageIcon(imageArm1.getScaledInstance(250, 200, Image.SCALE_SMOOTH)));
        } else {
            arm2.setIcon(new ImageIcon(imageArm2.getScaledInstance(250, 200, Image.SCALE_SMOOTH)));
        }

        revalidate();
        repaint();
    }

    public void upArm1(BufferedImage image) {
        ImageIcon arm = new ImageIcon(image.getScaledInstance(250, 200, Image.SCALE_SMOOTH));

        arm1.setIcon(arm);
        arm1.setVisible(true);

        revalidate();
        repaint();
    }

    public void upArm2(BufferedImage image) {
        ImageIcon arm = new ImageIcon(image.getScaledInstance(250, 200, Image.SCALE_SMOOTH));

        arm2.setIcon(arm);
        arm2.setVisible(true);

        revalidate();
        repaint();
    }

    public void failArm(int line){
        if (line == 1){
            arm1.setIcon(new ImageIcon(armFailed1.getScaledInstance(250,200, Image.SCALE_SMOOTH)));
        } else {
            arm2.setIcon(new ImageIcon(armFailed2.getScaledInstance(250,200, Image.SCALE_SMOOTH)));
        }

        revalidate();
        repaint();
    }

    private BufferedImage loadImage(String filename) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(PainterEnvironmentSimulation.IMAGE_PATH + filename));
        } catch (IOException ex) {
//            System.out.println("Image " + filename + " not found");
        }
        return image;
    }
}


