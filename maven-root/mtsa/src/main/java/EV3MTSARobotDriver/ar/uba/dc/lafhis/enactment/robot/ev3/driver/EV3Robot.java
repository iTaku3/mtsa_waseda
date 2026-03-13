package EV3MTSARobotDriver.ar.uba.dc.lafhis.enactment.robot.ev3.driver;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;


/**
 * @author Julio
 *
 */
public class EV3Robot extends Robot{
	
	private EV3ColorSensor colorSensor;
	private DifferentialPilot pilot;
	private SampleProvider colorProvider;
	private RegulatedMotor leftMotor;
	private RegulatedMotor rightMotor;
	
	private double rotateAngle = 45;
	private double turnBackAngle = 93;
	private double sensorWheelDistance = 5;
	private double pathColor;
	//private Color pathColorRGB;
	private double intersectionColor;;
	//private Color intersectionColorRGB;
	private double  outOfPath;
	//private Color outOfPathRGB;
	private double tolerancia = 0.15;
	//private double toleranciaRGB = 70.0;	
	private float velocidadMaxima = 50;
	double colorLectura;
	//Color colorLecturaRGB;
	
	private GraphicsLCD LCD = LocalEV3.get().getGraphicsLCD();
	private RobotDriverComm comm;
	
	
	private boolean debugMode = false;
	

	/////////////////////////////////////
	// Constructor
	public EV3Robot(String name, RobotDriverComm comm, EV3ColorSensor ev3ColorSensor, DifferentialPilot differentialPilot, RegulatedMotor leftMotor, RegulatedMotor rightMotor, double sensorWheelDistance)
	{
		super(name);
		
		this.setColorSensor(ev3ColorSensor);
		this.setPilot(differentialPilot);		
		this.setLeftMotor(leftMotor);
		this.setRightMotor(rightMotor);
		this.setColorProvider( new AutoAdjustFilter(this.getColorSensor().getRedMode()));		
		//this.setColorProvider(this.getColorSensor().getRedMode());
		this.setSensorWheelDistance(sensorWheelDistance);
		this.setComm(comm);
		
		
	}
	
	public void testReadColor()
	{
		Brick brick = BrickFinder.getDefault();
		Key escape = brick.getKey("Escape");
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		
		
//		System.out.println("Start test color");
		while (!escape.isDown()) {
			double readColor = this.readColor();
//			System.out.println(formatter.format(readColor));
			
			Button.waitForAnyPress();
		}
//		System.out.println("End test color");
		
	}
	private double readColor()
	{
		//int sampleSize = colorProvider.sampleSize();
	    //float[] sample = new float[sampleSize];
	    //colorProvider.fetchSample(sample, 0);
	    
		SampleProvider redMode = this.getColorSensor().getRedMode();
		
		float[] sample = new float[redMode.sampleSize()];
		redMode.fetchSample(sample, 0);
		//this.getColorSensor().close();
				
	    colorLectura = sample[0];
	    if (this.isDebugMode()) this.getComm().sendColorSensorValue(colorLectura);
	    return colorLectura;
	}
	
	private Color readRGBColor()
	{
		
		

	    int sampleSize = colorProvider.sampleSize();
	    
	    if (sampleSize != 3) return null;
	    
	    float[] sample = new float[sampleSize];
	    colorProvider.fetchSample(sample, 0);
	    	
	    int red = Math.round(sample[0] * 255 );
	    int green = Math.round(sample[1] * 255 );
	    int blue = Math.round(sample[2] * 255 );	    	    
	    
	    return new Color(red, green, blue);
	    
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
			//this.getColorSensor().setFloodlight(Color.WHITE);			
			
			LCD.clear();
//			System.out.println("Warming up...");
//			System.out.println("Move robot between colors.");
//			System.out.println("Press ESCAPE when done");
			double value = Double.NaN;
			double minValue = 0;
			double maxValue = 0;
			double diffValue = 0;
			Brick brick = BrickFinder.getDefault();
			Key escape = brick.getKey("Escape");
			//while (value == Float.NaN || (diffValue < 0.9 ))
			//for (int i=0; i<50; i++)
			
			
//		    Port s4 = brick.getPort("S1");
//		    EV3ColorSensor sensor = new EV3ColorSensor(s4);
//		    SampleProvider redMode = sensor.getRedMode();
//		    SampleProvider reflectedLight = new AutoAdjustFilter(redMode);
//		    
//		    float sample[] = new float[reflectedLight.sampleSize()];
		    
			while (!escape.isDown()) 
			{
				value = this.readColor();
				//reflectedLight.fetchSample(sample, 0);
				//value = sample[0];
				
				if (value != Float.NaN && value < minValue) minValue = value;
				if (value != Float.NaN && value > maxValue) maxValue = value;
				diffValue = Math.abs(minValue - maxValue);				
//				//System.out.println(value + " " + diffValue);
				Delay.msDelay(100);
			}
			//sensor.close();
			
			//Color del camino
			LCD.clear();			
			//LCD.drawString("Calibration",0,0,0);
//			System.out.println("Calibration");
			//LCD.drawString("Path Color",0,1,0);
//			System.out.println("Path color");			
			//LCD.drawString("<press>",0,2,0);
//			System.out.println("<press>");
			//LCD.refresh();
			Button.waitForAnyPress();
			
			
		
			//pathColorRGB = this.readRGBColor();
			this.setPathColor(this.readColor());			
			//this.setPathColor(0);
			
			
			LCD.clear();
//			System.out.println("Color:");
//			System.out.println(String.valueOf(this.getPathColor()));
			Button.waitForAnyPress();
			
			//Color fuera
			LCD.clear();
//			System.out.println("Out of");
//			System.out.println("Path");
//			System.out.println("<press>");
			Button.waitForAnyPress();
			
			//outOfPathRGB = this.readRGBColor();			
			this.setOutOfPath(this.readColor());
			//this.setOutOfPath(1);
			
			LCD.clear();
//			System.out.println("Color");
//			System.out.println(String.valueOf(this.getOutOfPath()));
			Button.waitForAnyPress();	
			
			//Color Interseccion
			LCD.clear();
//			System.out.println("Intersection");
//			System.out.println("<press>");
			Button.waitForAnyPress();
			
					
			this.setIntersectionColor(this.readColor());
			
			LCD.clear();
//			System.out.println("Color:");
			
//			System.out.println(String.valueOf(this.getIntersectionColor()));
			Button.waitForAnyPress();

			LCD.clear();
//			System.out.println("Locate robot");
//			System.out.println("over");
//			System.out.println("intersection");			
			Button.waitForAnyPress();
			
			LCD.clear();
			
			
		
	}
	
		
	/*
	Inicializar el objetivo de avanzar por el camino hasta el proximo gris
	*/
	public void avanzarHastaGris() throws RobotLostException {
		//Si el robot se encuentra en el camino o en la interseccion debe avanzar
		//si el sensor proveniente del camino detecta una interseccion, finaliza la tarea exitosamente
		//si el sensor detecta fuera de camino, finalizar notificando que se ha perdido
		PathState currentState = calculatePathState(PathState.intersection);
		
//		System.out.println("Curr Path: " + currentState.toString());
		
		if (currentState == null || currentState == PathState.outside)
		{
			if (!tryToFind(PathState.intersection) && !tryToFind(PathState.path))
				throw new RobotLostException();
		}
		
		//Leo en busca de interseccion
		if (currentState == PathState.intersection)
			exitIntersection();
		
		
		this.getPilot().travel(1);

		//Espero que se encuentre en el path
		currentState = calculatePathState(PathState.path);
//		System.out.println("PathState: " + currentState);
		
		if ( currentState != PathState.path )
			if ( !tryToFind(PathState.path))
				throw new RobotLostException();
		
//		System.out.println("Follow Path");
		followPath(false);

//		System.out.println("End Follow");
		this.getPilot().stop();
		
		if (calculatePathState(PathState.none)== PathState.outside)
			if (!tryToFind(PathState.intersection))
				throw new RobotLostException();
		
		this.getPilot().travel(0.5);
			
	}
	
	public void darVuelta() {
		verificarMovimientoYParar();
		doblar(0);
	}
	
	public void doblarDerecha() {
		doblar(-1);
	}
	
	public void doblarIzquierda() {
		doblar(1);
	}
	
	public void avanzar(double distancia) {
		verificarMovimientoYParar();
		getPilot().travel(distancia);	//distancia debe estar en las mismas unidades que la instancia del pilot		
		
	}

	///////////////////////////////////////////	
	//Support functions & methods

	
	private void verificarMovimientoYParar() {
		if (getPilot().isMoving())
			getPilot().stop();	//tambien se puede usar quickStop
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


	/**
	 * @return reconocimiento del camino
	 */
	private PathState calculatePathState(PathState prevState)
	{
		PathState lectura = prevState;
		int cantLecturas = 10;
		double lecturas = 0;
		for (int i=0; i<4; i++)
		{
			//colorLecturaRGB = this.readRGBColor();
			lecturas += this.readColor();
			//red += colorLecturaRGB.getRed();
			//green += colorLecturaRGB.getGreen();
			//blue += colorLecturaRGB.getBlue();			
		}		
		//colorLecturaRGB = new Color(red / 4, green / 4, blue / 4);  
		//colorLecturaRaw = this.calculateRaw(colorLecturaRGB);
		colorLectura = lecturas / cantLecturas;
//		//System.out.println("Path State for:");
//		//System.out.println(colorLectura);
		lectura = decodeColor(prevState);			
//		//System.out.println(lectura);
		return lectura;
	}
	
	private PathState decodeColor(PathState prevState)
	{

		//Version 1 
//		if (colorDistance(colorLecturaRGB, intersectionColorRGB) < this.getToleranciaRGB())
//		{
//			return  PathState.intersection;
//		}
//		
//		else if (colorDistance(colorLecturaRGB, pathColorRGB) < this.getToleranciaRGB())
//		{
//			return PathState.path;
//		}  
//		
//		else if (colorDistance(colorLecturaRGB, outOfPathRGB) < this.getToleranciaRGB())
//		{
//			return PathState.outside;
//		} else 
//		{
//			return prevState;
//		}
		
		if (Math.abs(colorLectura - intersectionColor) < this.getTolerancia())
		{
			return  PathState.intersection;
		}
		
		else if (Math.abs(colorLectura - pathColor) < this.getTolerancia())
		{
			return PathState.path;
		}  
		
		else if (Math.abs(colorLectura - outOfPath) < this.getTolerancia())
		{
			return PathState.outside;
		} else 
		{
			return prevState;
		}
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
	
	private boolean tryToFind(PathState pathState)
	{
		int attemps = 15;
	
		double distance = 0.2;
		double angle = 1;
		
		LCD.clear();
		LCD.drawString("Try to find",0,0,0);
		LCD.drawString(pathState.toString(),0,1,0);
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
	
	
	private void exitIntersection()
	{	
//		System.out.println("Exit intersection");
		 this.getLeftMotor().setSpeed((int) Math.round(this.getVelocidadMaxima() * 0.5));
		 this.getRightMotor().setSpeed((int) Math.round(this.getVelocidadMaxima() * 0.5));

		 while (!Button.ESCAPE.isDown() && calculatePathState(PathState.intersection) == PathState.intersection){		   		
	        //TODO: Revisar para utilizar Pilot			 
			 this.getLeftMotor().forward();
			 this.getRightMotor().forward();      
        }
		 
	}
	
	private void followPath(boolean reverse) throws RobotLostException
	{
//		System.out.println("Start followPath");
			///////////////////////////////////////////////////////////////////////////
		//  Metodo para seguir la linea
		// http://www.lejosconlego.com/2012/07/como-hacer-un-line-follower-seguir-la.html
		
		//TODO: Revisar parametros Motor.A y Motor.C - deberia usarse el Pilot
		
		double linea = this.getPathColor() ; //- extraRango;                // lectura del sensor de luz sobre la l�nea
		double suelo = this.getOutOfPath() ;                // lectura del sensor de luz fuera de la l�nea
		double velMax = this.getVelocidadMaxima(); // velocidad (grados por segundo) m�xima de los motores
	    
	    //if (reverse) velMax = -velMax;
		double kp = 0;           // constante de proporcionalidad
		double ajuste;                  
		double error;            
		double offset;          

	    
	    long timeout = 2000;	//Tiempo de espera fuera del camino
	    double distanciaColor = getTolerancia();	//margen de similitud de colores
	    boolean fueraDeCamino = false;	//control de fuera de camino
	    long startTime = 0;		//para controlar el tiempo
	    
//	    System.out.println("Tolerancia: " + getTolerancia());
        offset  = (linea + suelo)/2;
//        System.out.println("Offset: " + offset);
        kp = velMax/(suelo-offset);
//        System.out.println("Kp: " + kp);
        
//        System.out.println("Path state: " + calculatePathState(PathState.none));
		if (!reverse) 
		{
			this.getLeftMotor().forward();
			this.getRightMotor().forward();      			   
		} else 
		{
			this.getLeftMotor().backward();
			this.getRightMotor().backward();
		}
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
        while (!Button.ESCAPE.isDown()){
	   		
        	if (calculatePathState(PathState.none) == PathState.intersection) 
        		{
//        		System.out.println("Llego interseccion");
        		break;
        		}
	   		
           
			//Utiliza ultimo valor leido
			double lecturaRaw = this.colorLectura;
//			System.out.println("Lectura: " + formatter.format(lecturaRaw));
			error = lecturaRaw - offset;
//			//System.out.println("Error " + error);
			ajuste = kp * error;
//			//System.out.println("Ajuste: " + ajuste);
			this.getLeftMotor().setSpeed((int) Math.round(velMax + ajuste));
			this.getRightMotor().setSpeed((int) Math.round(velMax - ajuste));

			
			//Analiza cuanto tiempo esta fuera del camino
			//y si excede el tiempo estipulado emite una excepcion RobotLost
			if (Math.abs(lecturaRaw - suelo) < distanciaColor)
			{
				if (!fueraDeCamino)
				{
					fueraDeCamino = true;
					startTime = System.currentTimeMillis();
//					System.out.println("fuera de camino. comienza tiempo.");
				} else
				{
					if (System.currentTimeMillis() - startTime > timeout)
					{
//						System.out.println("Time out fuera de camino.");
						throw new RobotLostException();
					}
				} 
			} else
				//No esta fuera del camino
			{
				fueraDeCamino = false;
			}
			
			Delay.msDelay(50);
        }
//        System.out.println("Fin de ciclo");
        this.getPilot().stop();
    }
	
	private String colorToString(Color color)
	{
		return "(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
	}
	
	//////////////////////////////////////////////
	// Getters & Setters
	
	/**
	 * @return the colorSensor
	 */
	private EV3ColorSensor getColorSensor() {
		return colorSensor;
	}

	/**
	 * @param colorSensor the colorSensor to set
	 */
	private void setColorSensor(EV3ColorSensor colorSensor) {
		this.colorSensor = colorSensor;
	}

	/**
	 * @return the rgbProvider
	 */
	private SampleProvider getColorProvider() {
		return colorProvider;
	}

	/**
	 * @param rgbProvider the rgbProvider to set
	 */
	private void setColorProvider(SampleProvider colorProvider) {
		this.colorProvider = colorProvider;
	}



	/**
	 * @return the pathColorRGB
	 */
	//private Color getPathColorRGB() {
	//	return pathColorRGB;
	//}

	/**
	 * @param pathColorRGB the pathColorRGB to set
	 */
	//private void setPathColorRGB(Color pathColorRGB) {
	//	this.pathColorRGB = pathColorRGB;
	//}

	/**
	 * @return the pathColor
	 */
	public double getPathColor() {
		return pathColor;
	}

	/**
	 * @param pathColor the pathColor to set
	 */
	private void setPathColor(double pathColor) {
		this.pathColor = pathColor;
		//if (this.isDebugMode()) this.getComm().sendPathValue(pathColor);
	}

	/**
	 * @return the rotateAngle
	 */
	private double getRotateAngle() {
		return rotateAngle;
	}

	/**
	 * @param rotateAngle the rotateAngle to set
	 */
	private void setRotateAngle(double rotateAngle) {
		this.rotateAngle = rotateAngle;
	}

	/**
	 * @return the turnBackAngle
	 */
	private double getTurnBackAngle() {
		return turnBackAngle;
	}

	/**
	 * @param turnBackAngle the turnBackAngle to set
	 */
	private void setTurnBackAngle(double turnBackAngle) {
		this.turnBackAngle = turnBackAngle;
	}

	/**
	 * @return the sensorWheelDistance
	 */
	private double getSensorWheelDistance() {
		return sensorWheelDistance;
	}

	/**
	 * @param sensorWheelDistance the sensorWheelDistance to set
	 */
	private void setSensorWheelDistance(double sensorWheelDistance) {
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
	private void setIntersectionColor(double intersectionColor) {
		this.intersectionColor = intersectionColor;
		//if (this.isDebugMode()) this.getComm().sendIntersectionValue(intersectionColor);
	}

	/**
	 * @return the intersectionColorRGB
	 */
	//private Color getIntersectionColorRGB() {
	//	return intersectionColorRGB;
	//}

	/**
	 * @param intersectionColorRGB the intersectionColorRGB to set
	 */
	//private void setIntersectionColorRGB(Color intersectionColorRGB) {
	//	this.intersectionColorRGB = intersectionColorRGB;
	//}

	/**
	 * @return the outOfPath
	 */
	public double getOutOfPath() {
		return outOfPath;
	}

	/**
	 * @param outOfPath the outOfPath to set
	 */
	private void setOutOfPath(double outOfPath) {
		this.outOfPath = outOfPath;
		//if (this.isDebugMode()) this.getComm().sendOutOfPathValue(outOfPath);
	}

	/**
	 * @return the outOfPathRGB
	 */
	//private Color getOutOfPathRGB() {
	//	return outOfPathRGB;
	//}

	/**
	 * @param outOfPathRGB the outOfPathRGB to set
	 */
	//private void setOutOfPathRGB(Color outOfPathRGB) {
	//	this.outOfPathRGB = outOfPathRGB;
	//}

	/**
	 * @return the tolerancia
	 */
	private double getTolerancia() {
		return tolerancia;
	}

	/**
	 * @param tolerancia the tolerancia to set
	 */
	private void setTolerancia(double tolerancia) {
		this.tolerancia = tolerancia;
	}

	/**
	 * @return the toleranciaRGB
	 */
	//private double getToleranciaRGB() {
	//	return toleranciaRGB;
	//}

	/**
	 * @param toleranciaRGB the toleranciaRGB to set
	 */
	//private void setToleranciaRGB(double toleranciaRGB) {
	//	this.toleranciaRGB = toleranciaRGB;
	//}

	/**
	 * @return the velocidadMaxima
	 */
	private float getVelocidadMaxima() {
		return velocidadMaxima;
	}

	/**
	 * @param velocidadMaxima the velocidadMaxima to set
	 */
	private void setVelocidadMaxima(float velocidadMaxima) {
		this.velocidadMaxima = velocidadMaxima;
	}

	/**
	 * @return the colorLecturaRaw
	 */
	public double getColorLectura() {
		return colorLectura;
	}

	/**
	 * @param colorLecturaRaw the colorLecturaRaw to set
	 */
	private void setColorLectura(float colorLectura) {
		this.colorLectura = colorLectura;
	}

	/**
	 * @return the colorLecturaRGB
	 */
	//private Color getColorLecturaRGB() {
	//	return colorLecturaRGB;
	//}

	/**
	 * @param colorLecturaRGB the colorLecturaRGB to set
	 */
	//private void setColorLecturaRGB(Color colorLecturaRGB) {
	//	this.colorLecturaRGB = colorLecturaRGB;
	//}

	/**
	 * @return the lCD
	 */
	private GraphicsLCD getLCD() {
		return LCD;
	}

	/**
	 * @param lCD the lCD to set
	 */
	private void setLCD(GraphicsLCD lCD) {
		LCD = lCD;
	}
	
		
	/**
	 * @return the pilot
	 */
	protected DifferentialPilot getPilot() {
		return pilot;
	}

	/**
	 * @param pilot the pilot to set
	 */
	protected void setPilot(DifferentialPilot pilot) {
		this.pilot = pilot;
	}



	/**
	 * @return the leftMotor
	 */
	private RegulatedMotor getLeftMotor() {
		return leftMotor;
	}

	/**
	 * @param leftMotor the leftMotor to set
	 */
	private void setLeftMotor(RegulatedMotor leftMotor) {
		this.leftMotor = leftMotor;
	}

	/**
	 * @return the rightMotor
	 */
	private RegulatedMotor getRightMotor() {
		return rightMotor;
	}

	/**
	 * @param rightMotor the rightMotor to set
	 */
	private void setRightMotor(RegulatedMotor rightMotor) {
		this.rightMotor = rightMotor;
	}



	/**
	 * @return the debugMode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * @param debugMode the debugMode to set
	 */
	public void setDebugMode(boolean debugMode) {
			
		this.debugMode = debugMode;
		if (debugMode)
			System.out.println("Debug mode on");
		else
			System.out.println("Debug mode off");
	}



	/**
	 * @return the comm
	 */
	private RobotDriverComm getComm() {
		return comm;
	}

	/**
	 * @param comm the comm to set
	 */
	private void setComm(RobotDriverComm comm) {
		this.comm = comm;
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
	
	
	
}
