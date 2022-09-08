package exercises.trafficlight;

import runtime.EventWindow;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

public class TrafficLightControllerMachine implements IStateMachine {
	
	private static final String PEDESTRIAN_BUTTON_PRESSED = "Pedestrian Button";
	
	public static final String[] EVENTS = {PEDESTRIAN_BUTTON_PRESSED};
	
	private enum STATES {S0, S1, S2, S3, S4, S5}
	
	private Timer t1 = new Timer("t1");
	private Timer t2 = new Timer("t2");
	private Timer t3 = new Timer("t3");
	private Timer t4 = new Timer("t4");
	private Timer t5 = new Timer("t5");
	
	protected STATES state = STATES.S0;
	
	private TrafficLight cars = new TrafficLight("Cars", true);
	private TrafficLight pedestrians = new TrafficLight("Pedestrians", false);
	
	public TrafficLightControllerMachine() {
		// initial transition
		cars.setVisible(true);
		pedestrians.setVisible(true);
		cars.showGreen();
		pedestrians.showRed();		
	}

	public int fire(String event, Scheduler scheduler) {
		if(state==STATES.S0) {
			if(event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				cars.showYellow();
				t1.start(scheduler, 1000);
				state = STATES.S1;
				return EXECUTE_TRANSITION;
			}  
		} else if(state==STATES.S1) {
			// TODO add stuff here
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
