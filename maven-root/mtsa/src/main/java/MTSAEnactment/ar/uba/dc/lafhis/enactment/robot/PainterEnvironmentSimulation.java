package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;

import javax.imageio.ImageIO;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by lnahabedian on 18/07/16.
 */
public class PainterEnvironmentSimulation<State, Action> extends
        RobotAdapter<State, Action> {

    public static String IMAGE_PATH = System.getProperty("user.dir") + "/src/main/resources/PainterUpdateImg/";
    // controllable
    protected Action gPaint1, gPaint2;
    protected Action dry1, dry2;
    protected Action out1, out2;
    // uncontrollable
    protected Action in1, in2;
    protected Action gPaintOk1, gPaintOk2;
    protected Action gPaintNOk1, gPaintNOk2;
    protected Action dryOk1, dryOk2;
    protected Action dryNOk1, dryNOk2;
    protected Action reset1, reset2;
    // new controllable
    protected Action mPaint1, mPaint2;
    protected Action varnish1, varnish2;
    // new uncontrollable
    protected Action mPaintOk1, mPaintOk2;
    protected Action mPaintNOk1, mPaintNOk2;
    protected Action varnishOk1, varnishOk2;
    protected Action varnishNOk1, varnishNOk2;

    private PainterEnvironmentUI simulationWindow;
    private int statusLine1;
    private int statusLine2;


    public PainterEnvironmentSimulation(String name, Action success, Action failure, Action lost, Action gPaint1, Action gPaint2, Action dry1, Action dry2, Action out1, Action out2, Action in1, Action in2, Action gPaintOk1, Action gPaintOk2, Action gPaintNOk1, Action gPaintNOk2, Action dryOk1, Action dryOk2, Action dryNOk1, Action dryNOk2, Action reset1, Action reset2, Action mPaint1, Action mPaint2, Action varnish1, Action varnish2, Action mPaintOk1, Action mPaintOk2, Action mPaintNOk1, Action mPaintNOk2, Action varnishOk1, Action varnishOk2, Action varnishNOk1, Action varnishNOk2) {
        super(name, success, failure, lost);
        this.gPaint1 = gPaint1;
        this.gPaint2 = gPaint2;
        this.dry1 = dry1;
        this.dry2 = dry2;
        this.out1 = out1;
        this.out2 = out2;
        this.in1 = in1;
        this.in2 = in2;
        this.gPaintOk1 = gPaintOk1;
        this.gPaintOk2 = gPaintOk2;
        this.gPaintNOk1 = gPaintNOk1;
        this.gPaintNOk2 = gPaintNOk2;
        this.dryOk1 = dryOk1;
        this.dryOk2 = dryOk2;
        this.dryNOk1 = dryNOk1;
        this.dryNOk2 = dryNOk2;
        this.reset1 = reset1;
        this.reset2 = reset2;
        this.mPaint1 = mPaint1;
        this.mPaint2 = mPaint2;
        this.varnish1 = varnish1;
        this.varnish2 = varnish2;
        this.mPaintOk1 = mPaintOk1;
        this.mPaintOk2 = mPaintOk2;
        this.mPaintNOk1 = mPaintNOk1;
        this.mPaintNOk2 = mPaintNOk2;
        this.varnishOk1 = varnishOk1;
        this.varnishOk2 = varnishOk2;
        this.varnishNOk1 = varnishNOk1;
        this.varnishNOk2 = varnishNOk2;

        this.statusLine1 = 0;
        this.statusLine2 = 0;
    }

    @Override
    protected void primitiveHandleTransitionEvent(TransitionEvent<Action> transitionEvent) throws Exception {

        if (transitionEvent.getAction().equals(in1)) {
            downArm(1);
            inImageAt(1);
            statusLine1 = 1;
        } else if (transitionEvent.getAction().equals(in2)) {
            downArm(2);
            inImageAt(2);
            statusLine2 = 1;
        } else if (transitionEvent.getAction().equals(gPaintOk1)) {
            downArm(1);
            gPaintImageAt(1);
            statusLine1 = 2;
        } else if (transitionEvent.getAction().equals(gPaintOk2)) {
            downArm(2);
            gPaintImageAt(2);
            statusLine2 = 2;
        } else if (transitionEvent.getAction().equals(dryOk1)) {
            downArm(1);
            statusLine1 = dryImageAt(1, statusLine1);
        } else if (transitionEvent.getAction().equals(dryOk2)) {
            downArm(2);
            statusLine2 = dryImageAt(2, statusLine2);
        } else if (transitionEvent.getAction().equals(mPaintOk1)) {
            downArm(1);
            mPaintImageAt(1);
            statusLine1 = 4;
        } else if (transitionEvent.getAction().equals(mPaintOk2)) {
            downArm(2);
            mPaintImageAt(2);
            statusLine2 = 4;
        } else if (transitionEvent.getAction().equals(varnishOk1)) {
            downArm(1);
            varnishImageAt(1);
            statusLine1 = 3;
        } else if (transitionEvent.getAction().equals(varnishOk2)) {
            downArm(2);
            varnishImageAt(2);
            statusLine2 = 3;
        } else if (transitionEvent.getAction().equals(out1)) {
            outImageAt(1);
        } else if (transitionEvent.getAction().equals(out2)) {
            outImageAt(2);
        } else if (transitionEvent.getAction().equals(reset1)) {
            resetImageAt(1);
            statusLine1 = 0;
        } else if (transitionEvent.getAction().equals(reset2)) {
            resetImageAt(2);
            statusLine2 = 0;
        } else if (transitionEvent.getAction().equals(dry1)){
            drying(1);
        } else if (transitionEvent.getAction().equals(dry2)){
            drying(2);
        } else if (transitionEvent.getAction().equals(mPaint1)){
            mPainting(1);
        } else if (transitionEvent.getAction().equals(mPaint2)){
            mPainting(2);
        } else if (transitionEvent.getAction().equals(gPaint1)){
            gPainting(1);
        } else if (transitionEvent.getAction().equals(gPaint2)){
            gPainting(2);
        } else if (transitionEvent.getAction().equals(varnish1)){
            varnishing(1);
        } else if (transitionEvent.getAction().equals(varnish2)){
            varnishing(2);
        } else if (isNotOkArm1(transitionEvent.getAction())) {
            failArm(1);
        } else if (isNotOkArm2(transitionEvent.getAction())) {
            failArm(2);
        }

        simulationWindow.revalidate();
        simulationWindow.repaint();

    }

    private void failArm(int line) {

        simulationWindow.failArm(line);
    }

    private void downArm(int line) {

        simulationWindow.downArm(line);

    }

    private void drying(int line) {

        BufferedImage image = null;
        if (line == 1){
            image = loadImage("drying1.png");
            simulationWindow.upArm1(image);
        } else {
            image = loadImage("drying2.png");
            simulationWindow.upArm2(image);
        }

    }

    private void mPainting(int line) {

        BufferedImage image = null;
        if (line == 1){
            image = loadImage("mPainting1.png");
            simulationWindow.upArm1(image);
        } else {
            image = loadImage("mPainting2.png");
            simulationWindow.upArm2(image);
        }
    }

    private void gPainting(int line) {

        BufferedImage image = null;
        if (line == 1){
            image = loadImage("gPainting1.png");
            simulationWindow.upArm1(image);
        } else {
            image = loadImage("gPainting2.png");
            simulationWindow.upArm2(image);
        }
    }

    private void varnishing(int line) {

        BufferedImage image = null;
        if (line == 1){
            image = loadImage("varnishing1.png");
            simulationWindow.upArm1(image);
        } else {
            image = loadImage("varnishing2.png");
            simulationWindow.upArm2(image);
        }
    }

    private boolean isNotOkArm1(Action action) {

        return action.equals(dryNOk1) || action.equals(mPaintNOk1) ||
                action.equals(gPaintNOk1) || action.equals(varnishNOk1);
    }

    private boolean isNotOkArm2(Action action) {

        return action.equals(dryNOk2) || action.equals(mPaintNOk2) ||
                action.equals(gPaintNOk2) || action.equals(varnishNOk2);
    }

    private void inImageAt(int line) {

        BufferedImage image = loadImage("carRaw.png");
        simulationWindow.setProductImage(line, image);
    }

    private void gPaintImageAt(int line) {

        BufferedImage image = loadImage("glossyCar.png");
        setProductImage(line, image);
    }

    private void mPaintImageAt(int line) {

        BufferedImage image = loadImage("mattedCar.png");
        setProductImage(line, image);
    }

    private int dryImageAt(int line, int status) {

        BufferedImage image = null;
        int newStatus = 99999;

        if (status == 2) { // glossyCar
            image = loadImage("finishedCar.png");
            newStatus = 3;
        } else if (status == 4) { // mattedCar
            image = loadImage("driedMattedCar.png");
            newStatus = 5;
        } else {
            return status;
        }

        setProductImage(line, image);
        return newStatus;
    }

    private void varnishImageAt(int line) {

        BufferedImage image = loadImage("finishedCar.png");
        setProductImage(line, image);
    }

    private void outImageAt(int line) {
        BufferedImage image = loadImage("OutCar.png");
        setProductImage(line, image);
    }

    private void resetImageAt(int line) {

        simulationWindow.removeProduct(line);

    }

    @Override
    public void setUp() {
//        System.out.println("SET UP");

        simulationWindow = new PainterEnvironmentUI("Painter Environment");

    }

    private void setProductImage(int line, BufferedImage image) {

        simulationWindow.setProductImage(line, image);

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

    @Override
    public void tearDown() {
//        System.out.println("TEAR DOWN");
        simulationWindow.dispatchEvent(new WindowEvent(simulationWindow, WindowEvent.WINDOW_CLOSING));

    }
}





