package exercises.trafficlight;

import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class TrafficLightControllerMachinePi implements IStateMachine {

	public static final int PORT = 10001;
	public static final String HOSTNAME = "192.168.0.194";

	private static final String PEDESTRIAN_BUTTON_PRESSED = "Pedestrian Button";
	private static final String TIMER_1 = "t1";
	private static final String TIMER_2 = "t2";
	private static final String TIMER_3 = "t3";
	private static final String TIMER_35 = "t35";

	private static final String TIMER_4 = "t4";
	private static final String TIMER_5 = "t5";

	public static final String[] EVENTS = { PEDESTRIAN_BUTTON_PRESSED };

	public static final String SYNC = "SYNC";

	private enum STATES {
		S0, S1, S2, S3, S35, S4, S5, WAIT
	}

	private Timer t1 = new Timer("t1");
	private Timer t2 = new Timer("t2");
	private Timer t3 = new Timer("t3");
	private Timer t35 = new Timer("t35");
	private Timer t4 = new Timer("t4");
	private Timer t5 = new Timer("t5");

	protected STATES state = STATES.WAIT;

	private TrafficLightPi cars = new TrafficLightPi(true);
	private TrafficLightPi pedestrians = new TrafficLightPi(false);

	private boolean buttonFlag = false;

	private Buzzer buzzer = new Buzzer();

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
				buzzer.pedestrian();
				t3.start(scheduler, 2000);
				state = STATES.S3;
				return EXECUTE_TRANSITION;
			}
		} else if (state == STATES.S3) {
			if (event.equals(TIMER_3)) {
				pedestrians.showRed();
				buzzer.car();
				t35.start(scheduler, 2000);
				state = STATES.S35;
				return EXECUTE_TRANSITION;
			}
		} else if (state == STATES.S35) {
			if (event.equals(TIMER_35)) {
				buzzer.off();
				t4.start(scheduler, 2000);
				state = STATES.S4;
				return EXECUTE_TRANSITION;
			}
		} else if (state == STATES.S4) {
			if (event.equals(TIMER_4)) {
				cars.showRedYellow();
				t5.start(scheduler, 1000);
				state = STATES.S5;
				return EXECUTE_TRANSITION;
			} else if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				buttonFlag = true;
				return EXECUTE_TRANSITION;
			}
		} else if (state == STATES.S5) {
			if (event.equals(TIMER_5)) {
				cars.showGreen();
				state = STATES.WAIT;
				return EXECUTE_TRANSITION;
			} else if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				buttonFlag = true;
				return EXECUTE_TRANSITION;
			}
		} else if (state == STATES.WAIT) {
			if (event.equals("SYNC")) {
				// t0.start(scheduler, 60000);
				if (buttonFlag) {
					state = STATES.S1;
					cars.showYellow();
					t1.start(scheduler, 2000);
					buttonFlag = false;
				} else {
					state = STATES.WAIT;
				}
				return EXECUTE_TRANSITION;
			} else if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				buttonFlag = true;
				return EXECUTE_TRANSITION;
			}
		}
		return DISCARD_EVENT;
	}

	public static void main(String[] args) {
		IStateMachine tl = new TrafficLightControllerMachinePi();
		Scheduler s = new Scheduler(tl);

		new ButtonListener(s, TrafficLightControllerMachinePi.PEDESTRIAN_BUTTON_PRESSED);

		s.start();

		try {
			Socket clientSocket = new Socket(TrafficLightControllerMachine.HOSTNAME,
					TrafficLightControllerMachine.PORT);
			System.out.println("Connected to" + clientSocket.getRemoteSocketAddress());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while (true) {
				tl.fire(in.readLine(), s);
			}
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port " + TrafficLightControllerMachine.PORT
					+ " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
}
