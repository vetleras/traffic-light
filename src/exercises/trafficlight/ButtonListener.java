package exercises.trafficlight;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import runtime.Scheduler;

public class ButtonListener implements GpioPinListenerDigital {
    private Scheduler scheduler;
    private String event;

    ButtonListener(Scheduler scheduler, String event) {
        this.scheduler = scheduler;
        this.event = event;

        final GpioPinDigitalInput button = GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_11, PinPullResistance.PULL_DOWN);
        button.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        button.addListener(this);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent pinEvent) {
        if (pinEvent.getState() == PinState.LOW) {
            scheduler.addToQueueLast(event);
        } 
    }
}