package exercises;

import runtime.EventWindow;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

public class PeriodicTimerMachine implements IStateMachine {

	private static final String START = "Start", STOP = "Stop", EXIT = "Exit", TIMER_1 = "t1";
	public static final String[] EVENTS = { START, STOP, EXIT };

	private enum STATES {
		IDLE, ACTIVE, FINAL
	}

	private Timer t1 = new Timer("t1");
	protected STATES state = STATES.IDLE;

	private final int NUM_TICKS;
	private int tickCounter = 0;

	public PeriodicTimerMachine(int numTicks) {
		NUM_TICKS = numTicks;
	}

	public PeriodicTimerMachine() {
		this(4);
	}

	public int fire(String event, Scheduler scheduler) {
		if (state == STATES.IDLE) {
			if (event.equals(START)) {
				tickCounter = 0;
				t1.start(scheduler, 1000);
				state = STATES.ACTIVE;
				return EXECUTE_TRANSITION;
			} else if (event.equals(EXIT)) {
				state = STATES.FINAL;
				return EXECUTE_TRANSITION;
			}
		} else if (state == STATES.ACTIVE) {
			if (event.equals(STOP)) {
				t1.stop();
				state = STATES.IDLE;
				return EXECUTE_TRANSITION;
			} else if (event.equals(TIMER_1)) {
				System.out.println("tick " + tickCounter);
				tickCounter++;
				if (tickCounter < NUM_TICKS) {
					t1.start(scheduler, 1000);
					state = STATES.ACTIVE;
				} else {
					state = STATES.IDLE;
				}
				return EXECUTE_TRANSITION;
			} else if (event.equals(EXIT)) {
				state = STATES.FINAL;
				return EXECUTE_TRANSITION;
			}
		}
		return DISCARD_EVENT;
	}

	public static void main(String[] args) {
		IStateMachine stm = new PeriodicTimerMachine();
		Scheduler s = new Scheduler(stm);

		EventWindow w = new EventWindow(EVENTS, s);
		w.show();

		s.start();
	}
}
