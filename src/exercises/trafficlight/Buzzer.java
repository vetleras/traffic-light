package exercises.trafficlight;

import com.pi4j.wiringpi.SoftTone;
// https://pi4j.com/1.4/apidocs/com/pi4j/wiringpi/SoftTone.html

public class Buzzer {
	private final int PIN = 10;

	public Buzzer() {
		SoftTone.softToneCreate(PIN);
	}

	public void pedestrian() {
		System.out.println("Pedestrian buzzing");
		SoftTone.softToneWrite(10, 700);
	}

	public void car() {
		System.out.println("Car buzz");
		SoftTone.softToneWrite(10, 1000);
	}

	public void off() {
		System.out.println("Buzzer off");
		SoftTone.softToneStop(PIN);
	}
}