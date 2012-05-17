package semanticore.domain.actions.lib;

import semanticore.agent.kernel.information.FunctionBasedFact;
import semanticore.domain.model.Goal;

import com.hp.hpl.jena.graph.Node;

public class ExecuteGoalAction extends FunctionBasedFact {
    private Goal goal;

    private boolean alreadyStart = false;

    public ExecuteGoalAction(Goal g) {
	super("ExecuteGoal_" + g.getID() + "_" + g.getTimestamp(),
		new String[] { "" });

	this.goal = g;
    }

    @Override
    public Object run(Object input, Node[] args) {
	if (!alreadyStart) {
	    goal.start(input);
	    alreadyStart = true;
	}

	return null;
    }
}
