package semanticore.agent.kernel.action.model;

import semanticore.agent.execution.ExecutionComponent;
import semanticore.domain.model.Event;

public abstract class ExecutionEvent<T, ExecutionComponent> extends
	Event<T, ExecutionComponent> {
    public static String NAMESPACE = "http://semanticore.pucrs.br#";

    public static String START_ACTION = NAMESPACE + "StartAction";

    public ExecutionEvent(String descriptor, T parameter) {
	super(descriptor, parameter);
    }
}
