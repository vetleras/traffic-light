package exercises.trafficlight;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class TrafficLightPi {
	private Signal red, yellow, green;

	public TrafficLightPi(boolean showYellow){
		if (showYellow) {
			red = new Signal(RaspiPin.GPIO_07);
			yellow = new Signal(RaspiPin.GPIO_03);
			green = new Signal(RaspiPin.GPIO_13);
		} else {
			red = new Signal(RaspiPin.GPIO_00);
			green = new Signal(RaspiPin.GPIO_14);
		}
	}

	public void showGreen() {
		red.turnOff();
		if(yellow!=null) yellow.turnOff();
		green.turnOn();
	}

	public void showRed() {
		red.turnOn();
		if(yellow!=null) yellow.turnOff();
		green.turnOff();
	}

	public void showRedYellow() {
		if(yellow==null) {
			throw new UnsupportedOperationException("Traffic light has no yellow light.");
		}
		red.turnOn();
		yellow.turnOn();
		green.turnOff();
	}

	public void showYellow() {
		if(yellow==null) {
			throw new UnsupportedOperationException("Traffic light has no yellow light.");
		}
		red.turnOff();
		yellow.turnOn();
		green.turnOff();
	}

	public void switchAllOff() {
		red.turnOff();
		if(yellow!=null) yellow.turnOff();
		green.turnOff();
	}
	
	
	private class Signal{
		private GpioPinDigitalOutput pin;

		Signal(Pin pin){
			this.pin = GpioFactory.getInstance().provisionDigitalOutputPin(pin, PinState.LOW);
			this.pin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		}

		public void turnOn(){
			pin.setState(PinState.HIGH);       
		}

		public void turnOff(){
			pin.setState(PinState.LOW);       
		}
	}
}     
