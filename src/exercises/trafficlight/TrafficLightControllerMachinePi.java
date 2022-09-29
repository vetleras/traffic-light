package exercises.trafficlight;

import runtime.EventWindow;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class TrafficLightControllerMachinePi implements IStateMachine {

	public static final int PORT = 10001;
	public static final String HOSTNAME = "192.168.0.194";

	private static final String PEDESTRIAN_BUTTON_PRESSED = "Pedestrian Button";
	private static final String TIMER_0 = "t0";
	private static final String TIMER_1 = "t1";
	private static final String TIMER_2 = "t2";
	private static final String TIMER_3 = "t3";
	private static final String TIMER_4 = "t4";
	private static final String TIMER_5 = "t5";

	public static final String[] EVENTS = { PEDESTRIAN_BUTTON_PRESSED };

	public static final String SYNC = "SYNC";

	private enum STATES {
		S0, S1, S2, S3, S4, S5, WAIT
	}

	private Timer t0 = new Timer("t0");
	private Timer t1 = new Timer("t1");
	private Timer t2 = new Timer("t2");
	private Timer t3 = new Timer("t3");
	private Timer t4 = new Timer("t4");
	private Timer t5 = new Timer("t5");


	protected STATES state = STATES.WAIT;
	
	static final GpioController gpio = GpioFactory.getInstance();
	private TrafficLight cars = new TrafficLightPi("Cars", true, gpio);
	private TrafficLight pedestrians = new TrafficLightPi("Pedestrians", false, gpio);

	
	private boolean buttonFlag = false;

	public TrafficLightControllerMachinePi() {
		// initial transition
		cars.showGreen();
		pedestrians.showRed();
	}

	public int fire(String event, Scheduler scheduler) {
		if (state == STATES.S1) {
			if (event.equals(TIMER_1)) {
				cars.showRed();
				t2.start(scheduler, 2000);
				state = STATES.S2;
				return EXECUTE_TRANSITION;
			}
		} else if (state == STATES.S2) {
			if (event.equals(TIMER_2)) {
				pedestrians.showGreen();
				t3.start(scheduler, 4000);
				state = STATES.S3;
				return EXECUTE_TRANSITION;
			}
		} else if(state == STATES.S3) {
			if(event.equals(TIMER_3)) {
				pedestrians.showRed();
				t4.start(scheduler, 4000);
				state = STATES.S4;
				return EXECUTE_TRANSITION;
			}
		} else if(state == STATES.S4) {
			if (event.equals(TIMER_4)) {
				cars.showRedYellow();
				t5.start(scheduler, 1000);
				state = STATES.S5;
				return EXECUTE_TRANSITION;
			} else if(event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				buttonFlag = true;
				return EXECUTE_TRANSITION;
			}
		} else if(state == STATES.S5) {
			if(event.equals(TIMER_5)) {
				cars.showGreen();
				state = STATES.WAIT;
				return EXECUTE_TRANSITION;
			} else if(event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				buttonFlag = true;
				return EXECUTE_TRANSITION;
			}
		} else if(state == STATES.WAIT) {
			if(event.equals("SYNC")) {
				//t0.start(scheduler, 60000);
				if(buttonFlag) {
					state = STATES.S1;
					cars.showYellow();
					t1.start(scheduler, 2000);
					buttonFlag = false;
				} else {
					state = STATES.WAIT;
				}
				return EXECUTE_TRANSITION;
			} else if(event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				buttonFlag = true;
				return EXECUTE_TRANSITION;
			}
		}	
		return DISCARD_EVENT;
	}

	public static void main(String[] args) {
		IStateMachine tl = new TrafficLightControllerMachine();
        Scheduler s = new Scheduler(tl);
        EventWindow w = new EventWindow(TrafficLightControllerMachine.EVENTS, s);
        w.show();
        s.start();

        try {
            Socket clientSocket = new Socket(TrafficLightControllerMachine.HOSTNAME, TrafficLightControllerMachine.PORT);
			System.out.println("Connected to" + clientSocket.getRemoteSocketAddress());
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while (true) {
                tl.fire(in.readLine(), s); 
            }
        }
        catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "+ TrafficLightControllerMachine.PORT + " or listening for a connection");
            System.out.println(e.getMessage()); 
        }
	}
}
