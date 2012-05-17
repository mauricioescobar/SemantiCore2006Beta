package semanticore.agent.kernel.action.model;

import semanticore.domain.model.Event;

public abstract class SensorialEvent<T, SensorialComponent> extends
	Event<T, SensorialComponent> {
    public static String NAMESPACE = "http://semanticore.pucrs.br#";

    public static String SENSOR_REMOVED = NAMESPACE + "SensorRemoved";

    public static String SENSOR_ADDED = NAMESPACE + "SensorAdded";

    public SensorialEvent(String descriptor, T parameter) {
	super(descriptor, parameter);
    }
}
