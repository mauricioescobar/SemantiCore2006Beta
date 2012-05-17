package semanticore.domain.services.notification;

import semanticore.domain.SemantiCoreDefinitions;

public class NotificationFactory {
    Notification notification;

    public NotificationFactory() {
	super();

	Class[] args = new Class[1];

	try {
	    Class clName = Class.forName(getNotificationClass());
	    Object o = clName.newInstance();
	    notification = (Notification) o;

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private String getNotificationClass() {
	return SemantiCoreDefinitions.NOTIFICATION_CLASS;
    }

    public Notification getNotification() {
	return notification;
    }

}
