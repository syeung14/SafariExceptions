package modem;

import java.io.IOException;
import java.net.Socket;

class ModemDidNotConnectException extends Exception {

}

class ModemDriver {

    public static void dialModem(String number) throws ModemDidNotConnectException {
    }
}

// Nope, not really.
// GO ahead with IOException
class InfrastructureException extends Exception {

    public InfrastructureException() {
    }

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InfrastructureException(Throwable cause) {
        super(cause);
    }

    public InfrastructureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

public class PointOfSale {

    private static boolean usingModem = false;

    public static void getPaid() throws /*ModemDidNotConnectException, IOException*/
        InfrastructureException {
        // preamble

        try {
            if (usingModem) {
                ModemDriver.dialModem("1234567");
            } else {
                Socket s = new Socket("127.0.0.1", 8000);
            }
            // get paid...
        } catch (ModemDidNotConnectException | IOException me) {
            // Always handle a problem as soon as you can...
            // if retries left, retry
            throw new InfrastructureException(me);
        }
        //    if (Math.random() > 0.5) {
        //      throw new NoMoneyException();
        //    }
    }

    public static void sellStuff() {
        // ...
        try {
            getPaid();
            //    } catch (ModemDidNotConnectException me) {
        } catch (InfrastructureException me) {
            // what next?
            // Business logic / flow of business process
            // solutions are usually "higher up" the callstack
        }
    }
}
