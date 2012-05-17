package semanticore.agent.kernel.information;

import semanticore.agent.kernel.information.LogicalRelation.logicalOperator;

public class ComposedFact extends Fact {
    private Fact term1;

    private Fact term2;

    public ComposedFact(Fact term1, Fact term2, logicalOperator op) {
	super();
	this.term1 = term1;
	this.term2 = term2;
	this.operator = op;

    }

    public ComposedFact(Fact term1, Fact term2) {
	super();
	this.term1 = term1;
	this.term2 = term2;
	this.operator = logicalOperator.AND;
    }

    public logicalOperator getOperator() {
	return operator;
    }

    public Fact getTerm1() {
	return term1;
    }

    public Fact getTerm2() {
	return term2;
    }

}
