package NXTMSTSARobotDriver.robot;



import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;


/**
 * Created with IntelliJ IDEA.
 * User: tommy
 * Date: 2/23/13
 * Time: 5:44 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * @author Julio
 *
 */
public abstract class NXTRobot extends Robot {
	private DifferentialPilot pilot = null;
	private ColorSensor colorSensor = null;
	private double rotateAngle = 45;
	private double turnBackAngle = 93;
	private double sensorWheelDistance = 5;
	
	private double pathColor = Color.BLACK;
	private Color pathColorRGB;
	private double intersectionColor = Color.YELLOW;
	private Color intersectionColorRGB;
	private double  outOfPath = Color.WHITE;
	private Color outOfPathRGB;
	
	private double tolerancia = 70.0;
	private double toleranciaRGB = 70.0;
	
	private float velocidadMaxima = 100;
	
	//private Color colorLecturaRGB;
	double colorLecturaRaw;
	Color colorLecturaRGB;

	
	/**
	 * @return the toleranciaRGB
	 */
	public double getToleranciaRGB() {
		return toleranciaRGB;
	}

	/**
	 * @param toleranciaRGB the toleranciaRGB to set
	 */
	public void setToleranciaRGB(double toleranciaRGB) {
		this.toleranciaRGB = toleranciaRGB;
	}

	/**
	 * @return the velocidadMaxima
	 */
	public float getVelocidadMaxima() {
		return velocidadMaxima;
	}

	/**
	 * @param velocidadMaxima the velocidadMaxima to set
	 */
	public void setVelocidadMaxima(float velocidadMaxima) {
		this.velocidadMaxima = velocidadMaxima;
	}

	/**
	 * @return the sensorWheelDistance
	 */
	public double getSensorWheelDistance() {
		return sensorWheelDistance;
	}

	/**
	 * @param sensorWheelDistance the sensorWheelDistance to set
	 */
	public void setSensorWheelDistance(double sensorWheelDistance) {
		this.sensorWheelDistance = sensorWheelDistance;
	}

	/**
	 * @return the intersectionColor
	 */
	public double getIntersectionColor() {
		return intersectionColor;
	}

	/**
	 * @param intersectionColor the intersectionColor to set
	 */
	public void setIntersectionColor(double intersectionColor) {
		this.intersectionColor = intersectionColor;
	}

	/**
	 * @return the turnBackAngle
	 */
	public double getTurnBackAngle() {
		return turnBackAngle;
	}

	/**
	 * @param turnBackAngle the turnBackAngle to set
	 */
	public void setTurnBackAngle(double turnBackAngle) {
		this.turnBackAngle = turnBackAngle;
	}

	/**
	 * @return the tolerancia
	 */
	public double getTolerancia() {
		return tolerancia;
	}

	/**
	 * @param tolerancia the tolerancia to set
	 */
	public void setTolerancia(int tolerancia) {
		this.tolerancia = tolerancia;
	}


	/**
	 * @return the rotateAngle
	 */
	public double getRotateAngle() {
		return rotateAngle;
	}

	/**
	 * @param rotateAngle the rotateAngle to set
	 */
	public void setRotateAngle(double rotateAngle) {
		this.rotateAngle = rotateAngle;
	}

	/**
	 * @return the colorSensor
	 */
	public ColorSensor getColorSensor() {
		return colorSensor;
	}

	/**
	 * @param colorSensor the colorSensor to set
	 */
	public void setColorSensor(ColorSensor colorSensor) {
		this.colorSensor = colorSensor;
	}

	/**
	 * @return the pilot
	 */
	public DifferentialPilot getPilot() {
		return pilot;
	}

	/**
	 * @param pilot the pilot to set
	 */
	public void setPilot(DifferentialPilot pilot) {
		this.pilot = pilot;
	}
	
	
	
	/**
	 * Realiza la rotacion hacia la izquierda o derecha.
	 * @param sentido = -1 derecha | 1 izquierda | 0 darvuelta
	 * @throws Exception 
	 */
	private void doblar(int sentido) 
	{

		
		//Avanzo hasta ubicar la interseccion entre las ruedas
		getPilot().travel(this.getSensorWheelDistance());
		//Giro
		if (sentido == 1) 
			getPilot().rotate(-this.getRotateAngle());
		else if (sentido == -1)
			getPilot().rotate(this.getRotateAngle());
		else if (sentido == 0)
			getPilot().rotate(this.turnBackAngle);
			
		
		
		double correctionAngle;
		if (sentido == 1 ) 
			correctionAngle = -1; 
		else if (sentido == -1)
			correctionAngle = 1;
		else 
			correctionAngle = 2;
		
		int maxCorrection = 10;
		int attemp = 0;
		
		//Verifico estar el sensor sobre el camino. de no estarlo realizo correcciones
		
		
		while (calculatePathState(PathState.path) != PathState.path && attemp < maxCorrection)
		{
			getPilot().rotate(correctionAngle);
			attemp++;
		}
	
		
		getPilot().travel(-this.getSensorWheelDistance());
		
		attemp=0;
		double distanceCorrection = 0.1;
		while (calculatePathState(PathState.intersection) != PathState.intersection && attemp < maxCorrection )
		{
			getPilot().travel(-distanceCorrection);
			attemp++;
		}
	
	}

	public void doblarIzquierda() {

		doblar(1);
		
	}

	
	public void doblarDerecha() {
		doblar(-1);
	}


	private String colorToString(Color color)
	{
		return "(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
	}
	private String colorToString(int color)
	{
		String colorNames[] = {"None", "Red", "Green", "Blue", "Yellow",
                "Megenta", "Orange", "White", "Black", "Pink",
                "Grey", "Light Grey", "Dark Grey", "Cyan"};
		return colorNames[color + 1];
	}
	
	private double calculateRaw(Color rgb)
	{
		return ((double) rgb.getBlue() + (double) rgb.getRed() + (double) rgb.getGreen()) / 3.0;
	}
	/**
	 * Inicia rutina de calibracion. 
	 * 	Captura de colores: Camino - Interseccion
	 * 
	 */
	public void calibrar()
	{
		this.getColorSensor().setFloodlight(Color.WHITE);
		
		
		//Color del camino
		LCD.clear();
		LCD.drawString("Calibration",0,0);
		LCD.drawString("Path Color",0,1);
		LCD.drawString("<press>",0,2);
		LCD.refresh();
		Button.waitForAnyPress();
		
	
		pathColorRGB = this.getColorSensor().getColor();
		this.setPathColor(this.calculateRaw(pathColorRGB));
		
		
		LCD.clear();
		LCD.drawString("Color:",0,0);
		LCD.drawString(String.valueOf(this.getPathColor()),0,1);		
		LCD.refresh();
		Button.waitForAnyPress();
		
		//Color fuera
		LCD.clear();
		LCD.drawString("Out of",0,0);
		LCD.drawString("Path",0,1);
		LCD.drawString("<press>",0,2);
		LCD.refresh();
		Button.waitForAnyPress();
		
		outOfPathRGB = this.getColorSensor().getColor();
		this.setOutOfPath(this.calculateRaw(outOfPathRGB));
		
		LCD.clear();
		LCD.drawString("Color:",0,0);
		LCD.drawString(String.valueOf(this.getOutOfPath()),0,1);		
		LCD.refresh();
		Button.waitForAnyPress();	
		
		//Color Interseccion
		LCD.clear();
		LCD.drawString("Intersection",0,0);
		LCD.drawString("<press>",0,1);
		LCD.refresh();
		Button.waitForAnyPress();
		
		
		intersectionColorRGB = this.getColorSensor().getColor();
		this.setIntersectionColor(this.calculateRaw(intersectionColorRGB));
		
		LCD.clear();
		LCD.drawString("Color:",0,0);
		
		LCD.drawString(String.valueOf(this.getIntersectionColor()),0,1);	
		LCD.refresh();
		Button.waitForAnyPress();


		
		
		LCD.clear();
		LCD.drawString("Locate robot",0,0);
		LCD.drawString("on an",0,1);
		LCD.drawString("intersection",0,2);
		LCD.refresh();
		Button.waitForAnyPress();
		
		LCD.clear();
		LCD.refresh();
		
	}
	
	/**
	 * Identificacion del estado del terreno
	 * @author Julio
	 *
	 */
	private enum PathState 
	{
		intersection,
		path,
		outside,
		none
	}
	
	
	/**
	 * Funcion que calcula la distancia entre 2 colores
	 * @param e1
	 * @param e2
	 * @return
	 */
	private double colorDistance(Color e1, Color e2)
	{
		//TODO: Revisar si esta funcion no debe estar en un lib compartido
	    long rmean = ( (long)e1.getRed() + (long)e2.getRed() ) / 2;
	    long r = (long)e1.getRed() - (long)e2.getRed();
	    long g = (long)e1.getGreen() - (long)e2.getGreen();
	    long b = (long)e1.getBlue() - (long)e2.getBlue();
	    return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
	}
	
	
	private PathState decodeColor(PathState prevState)
	{
		double colorCamino = this.getPathColor();
		double colorIntersection = (int) this.getIntersectionColor();
		double colorOutOfPath = (int) this.getOutOfPath();
		//Version 1 
				
		if (colorDistance(colorLecturaRGB, intersectionColorRGB) < this.getToleranciaRGB())
		{
			return  PathState.intersection;
		}
		
		else if (colorDistance(colorLecturaRGB, pathColorRGB) < this.getToleranciaRGB())
		{
			return PathState.path;
		}  
		
		else if (colorDistance(colorLecturaRGB, outOfPathRGB) < this.getToleranciaRGB())
		{
			return PathState.outside;
		} else 
		{
			return prevState;
		}
	}
	/**
	 * @return reconocimiento del camino
	 */
	private PathState calculatePathState(PathState prevState)
	{

		
		
		
		PathState lectura = prevState;

		
	
		Color[] array = new Color[4];
		int red = 0, green = 0, blue = 0;
		for (int i=0; i<4; i++)
		{
			colorLecturaRGB = this.getColorSensor().getColor();
			red += colorLecturaRGB.getRed();
			green += colorLecturaRGB.getGreen();
			blue += colorLecturaRGB.getBlue();
			
		}
		
		colorLecturaRGB = new Color(red / 4, green / 4, blue / 4, 0, 0);  
		colorLecturaRaw = this.calculateRaw(colorLecturaRGB);
		lectura = decodeColor(prevState);
			

		return lectura;

	}
	/* Inicializar el objetivo de avanzar por el camino hasta el proximo gris
	 * (non-Javadoc)
	 * @see interfaces.IRobotAPI#avanzarHastaGris()
	 */
	public void avanzarHastaGris() throws RobotLostException {
		
		//Si el robot se encuentra en el camino o en la interseccion debe avanzar
		//si el sensor proveniente del camino detecta una interseccion, finaliza la tarea exitosamente
		//si el sensor detecta fuera de camino, finalizar notificando que se ha perdido
		PathState currentState = calculatePathState(PathState.intersection);
		if (currentState == null || currentState == PathState.outside)
		{
			if (!tryToFind(PathState.intersection) && !tryToFind(PathState.path))
				throw new RobotLostException();
		}
			
		
		
		//Leo en busca de interseccion
		if (currentState == PathState.intersection)
			exitIntersection();
		
		//LCD.clear();
		//LCD.drawString("After Inter",0,0);
		//LCD.drawString(String.valueOf(currentState),0,1);
		
		this.getPilot().travel(1);

		//Espero que se encuentre en el path
		currentState = calculatePathState(PathState.path); 
		if ( currentState != PathState.path )
			if ( !tryToFind(PathState.path))
				throw new RobotLostException();

		

		followPath(false);

		this.getPilot().stop();
		
		if (calculatePathState(PathState.none)== PathState.outside)
			if (!tryToFind(PathState.intersection))
				throw new RobotLostException();
		
		this.getPilot().travel(0.5);
		
		
		
	}

	/* (non-Javadoc)
	 * @see interfaces.IRobotAPI#darVuelta()
	 */
	public void darVuelta() {
		// TODO controlar que pilot no sea null
		
		verificarMovimientoYParar();
		doblar(0);

	}

	/* (non-Javadoc)
	 * @see interfaces.IRobotAPI#avanzar(double)
	 */
	public void avanzar(double distancia) {
		// TODO controlar que pilot no sea null
		
		verificarMovimientoYParar();
		getPilot().travel(distancia);	//distancia debe estar en las mismas unidades que la instancia del pilot
		
	}

	/* (non-Javadoc)
	 * @see interfaces.IRobotAPI#retroceder(double)
	 */
	public void retroceder(double distancia) {
		// TODO controlar que pilot no sea null
		
		verificarMovimientoYParar();
		getPilot().travel(-distancia);	//distancia debe estar en las mismas unidades que la instancia del pilot

	}

	private void verificarMovimientoYParar() {
		if (getPilot().isMoving())
			getPilot().stop();	//tambien se puede usar quickStop
	}

	/**
	 * @return the pathColor
	 */
	public double getPathColor() {
		return pathColor;
	}

	/**
	 * @param pathColor the pathColor to set
	 */
	public void setPathColor(double pathColor) {
		this.pathColor = pathColor;
	}

	private void exitIntersection()
	{
		
		 while (!Button.ESCAPE.isDown() && calculatePathState(PathState.intersection) == PathState.intersection){
		   		
	        //TODO: Revisar para utilizar Pilot
			 Motor.A.setSpeed((int) Math.round(this.getVelocidadMaxima() * 0.5));
			 Motor.C.setSpeed((int) Math.round(this.getVelocidadMaxima() * 0.5));
			 Motor.A.forward();
			 Motor.C.forward();      
        }
		
		
	}
	
	private boolean tryToFind(PathState pathState)
	{
		int attemps = 15;
	
		double distance = 0.2;
		double angle = 1;
		
		LCD.clear();
		LCD.drawString("Try to find",0,0);
		LCD.drawString(pathState.toString(),0,1);
		LCD.refresh();
		
		if (calculatePathState(pathState) == pathState) return true;
		
		this.getPilot().travel(distance * attemps, true);
		while (this.getPilot().isMoving())
		{
			if (calculatePathState(pathState) == pathState) return true;
			Thread.yield();
		}
		this.getPilot().travel(-2 * attemps * distance, true);
		while (this.getPilot().isMoving())
		{
			if (calculatePathState(pathState) == pathState) return true;
			Thread.yield();
		}
		
		
		this.getPilot().travel(attemps * distance, true);
		while (this.getPilot().isMoving())
		{
			if (calculatePathState(pathState) == pathState) return true;
			Thread.yield();
		}
		
		
		
		this.getPilot().rotate(attemps * angle, true);
		while (this.getPilot().isMoving())
		{
			if (calculatePathState(pathState) == pathState) return true;
			Thread.yield();
		}
		
		this.getPilot().rotate(-2 * attemps * angle);
		while (this.getPilot().isMoving())
		{
			if (calculatePathState(pathState) == pathState) return true;
			Thread.yield();
		}
		this.getPilot().rotate( attemps * angle);
		while (this.getPilot().isMoving())
		{
			if (calculatePathState(pathState) == pathState) return true;
			Thread.yield();
		}
		
		return false;
		
	}
	

	private void followPath(boolean reverse) throws RobotLostException
	{
	
		

		///////////////////////////////////////////////////////////////////////////
		//  Metodo para seguir la linea
		// http://www.lejosconlego.com/2012/07/como-hacer-un-line-follower-seguir-la.html
		
		//TODO: Revisar parametros Motor.A y Motor.C - deberia usarse el Pilot
		double extraRango = 20.0;
		double linea = this.getPathColor() ; //- extraRango;                // lectura del sensor de luz sobre la linea
		double suelo = this.getOutOfPath() ;                // lectura del sensor de luz fuera de la linea
		double velMax = this.getVelocidadMaxima(); // velocidad (grados por segundo) maxima de los motores
	    
	    //if (reverse) velMax = -velMax;
		double kp = 0;           // constante de proporcionalidad
		double ajuste;                  
		double error;            
		double offset;          

	    
	    long timeout = 2000;	//Tiempo de espera fuera del camino
	    double distanciaColor = 10;	//margen de similitud de colores
	    boolean fueraDeCamino = false;	//control de fuera de camino
	    long startTime = 0;		//para controlar el tiempo
	    

        offset  = (linea + suelo)/2;
        kp = velMax/(suelo-offset);
     
        
        while (!Button.ESCAPE.isDown()){
	   		
        	if (calculatePathState(PathState.none) == PathState.intersection) 
        		{
        		
        		break;
        		}
	   		
           
			//Utiliza ultimo valor leido
			double lecturaRaw = (double) this.colorLecturaRaw;
			error = lecturaRaw - offset;
			ajuste = kp * error;
			Motor.A.setSpeed((int) Math.round(velMax + ajuste));
			Motor.C.setSpeed((int) Math.round(velMax - ajuste));
			if (!reverse) 
			{
			Motor.A.forward();
			Motor.C.forward();      			   
			} else 
			{
			Motor.A.backward();
			Motor.C.backward();
			}
			
			
			//Analiza cuanto tiempo esta fuera del camino
			//y si excede el tiempo estipulado emite una excepcion RobotLost
			if (Math.abs(lecturaRaw - suelo) < distanciaColor)
			{
				if (!fueraDeCamino)
				{
					fueraDeCamino = true;
					startTime = System.currentTimeMillis();
				} else
				{
					if (System.currentTimeMillis() - startTime > timeout)
					{
						throw new RobotLostException();
					}
				} 
			} else
				//No esta fuera del camino
			{
				fueraDeCamino = false;
			}
			
        }
        this.getPilot().stop();
    }
	
	public Color readColor()
	{
		PathState prevState = PathState.none;

		while (!Button.ESCAPE.isDown())
		{
			prevState = calculatePathState(prevState);
			LCD.clear();
			LCD.drawString("Path - Action",0,0);
			LCD.drawString("CS:" + String.valueOf(prevState),0,1);
			LCD.drawString("Raw:" + String.valueOf(colorLecturaRaw),0,2);
			LCD.drawString(this.colorToString(colorLecturaRGB),0,3);
			LCD.refresh();	
			Button.waitForAnyPress();
			
		}
		
		return this.getColorSensor().getColor();
	}
    public NXTRobot(String name) {
        super(name);
    }


	/**
	 * @return the outOfPath
	 */
	public double getOutOfPath() {
		return outOfPath;
	}

	/**
	 * @param outOfPath the outOfPath to set
	 */
	public void setOutOfPath(double outOfPath) {
		this.outOfPath = outOfPath;
	}

}
