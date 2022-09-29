package exercises.trafficlight;

import runtime.EventWindow;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

public class Synchronizer implements IStateMachine {

  public static final String START = "T3";

  private enum STATES {
    S12,
    S3,
  }

  private Timer T12 = new Timer("T12");
  private Timer T3 = new Timer("T3");

  protected STATES state = STATES.S12;

  private Scheduler s1, s2, s3;

  private static final String SYNC = "SYNC";

  private PrintWriter out1, out2, out3;

  public Synchronizer(PrintWriter out1, PrintWriter out2, PrintWriter out3) {
    this.out1 = out1;
    this.out2 = out2;
    this.out3 = out3;
  }

  public int fire(String event, Scheduler scheduler) {
    switch (state) {
      case S12:
        if (event.equals("T3")) {
          System.out.println(event);
          out3.println(SYNC);
          T12.start(scheduler, 30000);
          state = STATES.S3;
          return EXECUTE_TRANSITION;
        }
        break;
      case S3:
        if (event.equals("T12")) {
          out1.println(SYNC);
          out2.println(SYNC);
          System.out.println(event);
          T3.start(scheduler, 30000);
          state = STATES.S12;
          return EXECUTE_TRANSITION;
        }
        break;
      default:
        return DISCARD_EVENT;
    }
    return DISCARD_EVENT;
  }

  public static void main(String[] args) {
    try {
        ServerSocket serverSocket = new ServerSocket(TrafficLightControllerMachine.PORT);
  
        Socket cs1 = serverSocket.accept();
        System.out.println(cs1.getRemoteSocketAddress());
        Socket cs2 = serverSocket.accept();
        System.out.println(cs2.getRemoteSocketAddress());
        Socket cs3 = serverSocket.accept();
        System.out.println(cs3.getRemoteSocketAddress());
        PrintWriter out1 = new PrintWriter(cs1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(cs2.getOutputStream(), true);
        PrintWriter out3 = new PrintWriter(cs3.getOutputStream(), true);

        IStateMachine synchronizer = new Synchronizer(out1, out2, out3);
        Scheduler synchronizerScheduler = new Scheduler(synchronizer);
        synchronizerScheduler.start();

        synchronizerScheduler.addToQueueLast(Synchronizer.START);
  
      } catch (IOException e) {
        System.out.println(
          "Exception caught when trying to listen on port " +
          TrafficLightControllerMachine.PORT +
          " or listening for a connection"
        );
        System.out.println(e.getMessage());
      }
    }
}
