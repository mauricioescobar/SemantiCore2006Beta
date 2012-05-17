package semanticore.agent.decision.hotspots;

import java.util.Vector;

import semanticore.agent.kernel.information.FunctionBasedFact;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class BaseBuiltAction extends BaseBuiltin {
    // private String name;

    private FunctionBasedFact function;

    private Vector result = null;

    private Vector input = null;

    /**
     * 
     * @param function
     * @param result
     *            Devo passar um alguma estrutura para armazenar o retorno das
     *            funcoes, esse retorno ira para o executor
     */
    public BaseBuiltAction(FunctionBasedFact function, Vector input,
	    Vector result) {
	this.function = function;

	this.input = input;
	this.result = result;
    }

    public String getName() {
	// TODO Auto-generated method stub
	return function.getFunctionName();
    }

    @Override
    public void headAction(Node[] args, int length, RuleContext context) {
	checkArgs(length, context);

	Object param = null;
	if (!input.isEmpty())
	    param = input.get(0);

	Object o;
	if ((o = function.run(param, args)) != null)
	    result.add(o);
    }
}
