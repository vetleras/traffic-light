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
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.event.PinEventType;

public class TrafficLightPi implements TrafficLight {
	
	private GpioPinDigitalOutput green;
	private GpioPinDigitalOutput yellow;
	private GpioPinDigitalOutput red;
	
	private GpioController gpio;

	public TrafficLightPi(String title, boolean showYellow, GpioController gpio){
		this.gpio = gpio;

		if(showYellow) {
			// Car traffic light
			green = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13, "Car Green",  PinState.LOW);
			yellow = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Car Yellow",  PinState.LOW);
			red = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "Car Red",  PinState.LOW);
		} else {
			// Pedestrian Light
			green = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, "Ped Green",  PinState.LOW);
			yellow = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "Ped Yellow",  PinState.LOW);
			red = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Ped Red",  PinState.LOW);
		}
		
	}
	
	public void finalize() {
		red.low();
		if(yellow!=null) yellow.low();
		green.low();
	}
	
	public void showGreen() {
		red.low();
		if(yellow!=null) yellow.low();
		green.high();
	}

	public void showRed() {
		red.high();
		if(yellow!=null) yellow.low();
		green.low();
	}

	public void showRedYellow() {
		if(yellow==null) {
			throw new UnsupportedOperationException("Traffic light has no yellow light.");
		}
		red.high();
		yellow.high();
		green.low();
	}

	public void showYellow() {
		if(yellow==null) {
			throw new UnsupportedOperationException("Traffic light has no yellow light.");
		}
		red.low();
		yellow.high();
		green.low();
	}

	public void switchAllOff() {
		red.low();
		if(yellow!=null) yellow.low();
		green.high();
	}
	
}     

