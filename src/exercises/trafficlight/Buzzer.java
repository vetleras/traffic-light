package exercises.trafficlight;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Buzzer {

	private GpioController controller;
	private GpioPinDigitalOutput buzzer;

    private static int 
        PEDPULSE = 2000,
        CARPULSE = 1000;

	public Buzzer() {
        this.controller = GpioFactory.getInstance();
        this.buzzer = this.controller.provisionDigitalOutputPin(
            RaspiPin.GPIO_10,
            "Buzzer",
            PinState.LOW
        );
        this.buzzer.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
    }

    public void pedestrian() {
        System.out.println("Pedestrian buzzing");
        buzzer.high();
        this.buzzer.pulse(PEDPULSE);
    }

    public void car() {
        System.out.println("Car buzzing");
        buzzer.high();
        this.buzzer.pulse(CARPULSE);
    }

    public void off() {
        System.out.println("Buzzer off");
        buzzer.low();
        this.buzzer.low();
    }

}