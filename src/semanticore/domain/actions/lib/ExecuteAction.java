package semanticore.domain.actions.lib;

import semanticore.agent.kernel.action.StartAction;
import semanticore.agent.kernel.information.FunctionBasedFact;

import com.hp.hpl.jena.graph.Node;

public class ExecuteAction extends FunctionBasedFact {
    public String actionName;

    public ExecuteAction(String actionName, String timestamp, String args) {
	super("ExecuteAction_" + actionName + "_" + timestamp,
		new String[] { args });

	this.actionName = actionName;
    }

    @Override
    public Object run(Object input, Node[] args) {
	return new StartAction(actionName, input, args);
    }
}
