package pointofsale;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

// BAD IDEA--in this case, we should use IOException as our general infrastructure problem
class InfrastructureFailureException extends Exception {
    public InfrastructureFailureException(Throwable cause) {
        super(cause);
    }
}

// THIS ONE MIGHT be reasonable :) represents semantic meaning TO THE CALLER
// and hints at the course of action for recover!
class NoMoneyException extends Exception {}
class ModemDidNotConnectException extends Exception {

}

class ModemDriver {
    public static void dialModem(int number) throws ModemDidNotConnectException {

    }
}
public class SellStuff {
    private static final boolean USE_MODEM = false;
    public static void getPaidByCard(int cardNum, int amount)
//            throws ModemDidNotConnectException, UnknownHostException, IOException {
//            throws InfrastructureFailureException {
            throws IOException {
        // get ready:
        // dial modem
        int retries = 3;
        boolean success = false;
        while (!success && retries > 0) {
            try {
                if (USE_MODEM) {
                    ModemDriver.dialModem(8001234);
                } else {
                    Socket s = new Socket("127.0.0.1", 8000);
                }
            } catch (ModemDidNotConnectException | IOException me) {
//                if (--retries == 0) throw new InfrastructureFailureException(me);
                if (--retries == 0) throw new IOException(me);
            }
        }
    }
    public static void sellStuff() {
        // infinite loop:
        // greet customer
        // (human here!!! -- does this mean we have the "resource" necessary to fix a loose phone jack)
        // or perhaps the human can ask for a differnt card
        // add up items
        // get paid!
        try {
            getPaidByCard(1234, 1000);
//        } catch (ModemDidNotConnectException me) {
//        } catch (InfrastructureFailureException me) {
        } catch (IOException me) {
            // involve the human?
            me.getCause(); // the implementation detail you can show to the human or debug logs
        }
    }
    public static void main(String[] args) {
        sellStuff();
    }
}
