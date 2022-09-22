package exercises.trafficlight;

import runtime.EventWindow;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

public class TrafficLightControllerMachine implements IStateMachine {

	private static final String PEDESTRIAN_BUTTON_PRESSED = "Pedestrian Button";

	public static final String[] EVENTS = { PEDESTRIAN_BUTTON_PRESSED };

	private enum STATES {
		S0, S1, S2, S3, S4, S5, S6
	}

	private Timer t1 = new Timer("t1");
	private Timer t2 = new Timer("t2");
	private Timer t3 = new Timer("t3");
	private Timer t4 = new Timer("t4");
	private Timer t5 = new Timer("t5");
	private Timer t6 = new Timer("t6");

	protected STATES state = STATES.S0;

	private TrafficLight cars = new TrafficLight(true);
	private TrafficLight pedestrians = new TrafficLight(false);

	private boolean pedestrianButtonPressed = false;

	public TrafficLightControllerMachine() {
		cars.showGreen();
		pedestrians.showRed();
	}

	public int fire(String event, Scheduler scheduler) {
		switch (state) {
			case S0:
				if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
					cars.showYellow();
					t1.start(scheduler, 1000);
					t6.start(scheduler, 60*1000);
					state = STATES.S1;
					return EXECUTE_TRANSITION;
				}
				break;

			case S1:
				if (event.equals("t1")) {
					cars.showRed();
					t2.start(scheduler, 1000);
					state = STATES.S2;
					return EXECUTE_TRANSITION;
				}
				break;

			case S2:
				if (event.equals("t2")) {
					pedestrians.showGreen();
					t3.start(scheduler, 5000);
					state = STATES.S3;
					return EXECUTE_TRANSITION;
				}
				break;

			case S3:
				if (event.equals("t3")) {
					pedestrians.showRed();
					t4.start(scheduler, 1000);
					state = STATES.S4;
					return EXECUTE_TRANSITION;
				}
				break;

			case S4:
				if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
					pedestrianButtonPressed = true;
					return EXECUTE_TRANSITION;
				} else if (event.equals("t4")) {
					cars.showRedYellow();
					t5.start(scheduler, 1000);
					state = STATES.S5;
					return EXECUTE_TRANSITION;
				}
				break;

			case S5:
				if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
					pedestrianButtonPressed = true;
					return EXECUTE_TRANSITION;
				} else if (event.equals("t5")) {
					cars.showGreen();
					state = STATES.S6;
					return EXECUTE_TRANSITION;
				}
				break;

			case S6:
				if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
					pedestrianButtonPressed = true;
					return EXECUTE_TRANSITION;
				} else if (event.equals("t6")) {
					t6.start(scheduler, 60*1000);
					if (pedestrianButtonPressed) {
						cars.showYellow();
						t1.start(scheduler, 1000);
						state = STATES.S1;
						pedestrianButtonPressed = false;
					}
					return EXECUTE_TRANSITION;
				}
				break;
		}
		return DISCARD_EVENT;
	}

	public static void main(String[] args) {
		IStateMachine stm = new TrafficLightControllerMachine();
		Scheduler s = new Scheduler(stm);

		EventWindow w = new EventWindow(EVENTS, s);
		w.show();

		s.start();
	}

}
