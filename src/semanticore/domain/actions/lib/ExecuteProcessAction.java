package semanticore.domain.actions.lib;

import java.util.Hashtable;

import semanticore.agent.execution.model.Action;
import semanticore.domain.model.SCOnt;

public class ExecuteProcessAction extends Action {

    public ExecuteProcessAction(String planName, Hashtable arguments) {
	super("", SCOnt.EXE_PLAN);

	this.parameter = planName;
	this.arguments = arguments;
    }

    public ExecuteProcessAction(String planName) {
	super("", SCOnt.EXE_PLAN);

	this.parameter = planName;
	this.arguments = new Hashtable();
    }

    @Override
    public void exec() {
	// TODO Auto-generated method stub
    }
}
