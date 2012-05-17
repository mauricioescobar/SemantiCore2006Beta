package semanticore.domain.services.notification;

public class TerminalNotification implements Notification {
    public boolean enabled = true;

    public void print(String message) {
	if (enabled)
	    System.out.println(message);
    }

    public void setEnabled(boolean arg) {
	enabled = arg;
    }
}
