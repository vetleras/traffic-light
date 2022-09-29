package exercises.trafficlight;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinDirection;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;

import runtime.Scheduler;

import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.event.PinEventType;

public class ButtonListener {
	
	private Scheduler scheduler;
	private GpioController gpio;
	
	GpioPinDigitalInput myButton;
	
	public ButtonListener(Scheduler scheduler, GpioController gpio) {
		this.scheduler = scheduler;
		this.gpio = gpio;
		
		myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_11,             // PIN NUMBER
                "PedButton",                   // PIN FRIENDLY NAME (optional)
                PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)
		
		myButton.addListener(new GpioUsageExampleListener(scheduler));
	}
	
	public static class GpioUsageExampleListener implements GpioPinListenerDigital {
		private Scheduler scheduler;
		
		public GpioUsageExampleListener(Scheduler scheduler) {
			this.scheduler = scheduler;
		}
		
	    @Override
	    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
	        if(event.getState() == PinState.HIGH) {
	        	scheduler.addToQueueLast("Pedestrian Button");
	        }
	    }
	}

}
