package semanticore.agent.kernel.information;

import com.hp.hpl.jena.graph.Node;

public class FunctionBasedFact extends Fact {

    private String name;

    private String[] agruments;

    public FunctionBasedFact(String functionName, String[] agruments) {
	super();
	this.name = functionName;
	this.agruments = agruments;

	try {
	    // bba = new BaseBuiltAction ( name )
	    // {
	    // @Override
	    // public String getName ( )
	    // {
	    // return name;
	    // }
	    //
	    // @Override
	    // public void headAction ( Node [ ] args, int length, RuleContext
	    // context )
	    // {
	    // JOptionPane.showMessageDialog ( null, name );
	    // }
	    // };

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public String[] getAgruments() {
	return this.agruments;
    }

    public void setAgruments(String[] parameters) {
	this.agruments = parameters;
    }

    public String getFunctionName() {
	return name;
    }

    public Object run(Object input, Node[] args) {
	return null;
    }
}
