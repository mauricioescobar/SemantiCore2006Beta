package semanticore.agent.kernel.action;

import semanticore.agent.execution.ExecutionComponent;
import semanticore.agent.kernel.action.model.ExecutionEvent;
import semanticore.domain.model.SCOnt;

public class StartAction extends ExecutionEvent<String, ExecutionComponent> {
    private String actionName;

    private Object input;

    private Object result;

    public StartAction(String actionName, Object input, Object result) {
	super(SCOnt.EXE_ACTION, null);

	this.actionName = actionName;
	this.input = input;
	this.result = result;
    }

    @Override
    public Object evaluate(ExecutionComponent param) {
	param.getExecutionEngine().startAction(actionName,
		new Object[] { input, result });

	return null;
    }
}
