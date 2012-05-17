package semanticore.agent.kernel.information;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import semanticore.agent.kernel.information.LogicalRelation.logicalOperator;

public abstract class Fact implements Serializable {
    protected logicalOperator operator;

    protected int id;

    private static int uniqueId = 0;

    public boolean checked = false;

    public Fact() {
	this.id = uniqueId++;
    }

    public logicalOperator getOperator() {
	return operator;
    }

    public int getId() {
	return id;
    }

    public boolean evaluate(Fact fact) {
	try {
	    if (fact instanceof SimpleFact)
		return factHelper(this, (SimpleFact) fact);

	    if (fact instanceof ComposedFact) {
		Vector<SimpleFact> facts = new Vector<SimpleFact>(1, 1);

		Iterator<SimpleFact> iter = facts.iterator();

		while (iter.hasNext()) {
		    factHelper(this, iter.next());
		}
	    }
	} catch (Exception e) {
	    System.err.println("[ E ] SemantiCore Fact Error : "
		    + e.getMessage());
	    e.printStackTrace();
	}

	return false;
    }

    public String canonicalForm() {
	try {
	    String cForm = canonicalForm(this);
	    return cForm;
	} catch (Exception e) {
	    // e.printStackTrace ( );
	}

	return "";
    }

    private String canonicalForm(Fact fact) {
	String sFact = "";

	if (fact == null) {

	} else if (fact instanceof ComposedFact) {
	    String sEsquerda = canonicalForm(((ComposedFact) fact).getTerm1());

	    String sDireita = canonicalForm(((ComposedFact) fact).getTerm2());

	    switch (((ComposedFact) fact).getOperator()) {
	    case AND:
		sFact = "( " + sEsquerda + " AND " + sDireita + " )";
		break;
	    case OR:
		sFact = "( " + sEsquerda + " OR " + sDireita + " )";
		break;
	    }
	} else {
	    SimpleFact f1 = (SimpleFact) fact;

	    sFact = "( " + f1.getSubject() + " " + f1.getPredicate() + " "
		    + f1.getObject() + " )";
	}

	return sFact;
    }

    private void canonicalForm(Fact fact, Vector<SimpleFact> v) {
	if (fact == null) {
	} else if (fact instanceof ComposedFact) {
	    canonicalForm(((ComposedFact) fact).getTerm1(), v);

	    canonicalForm(((ComposedFact) fact).getTerm2(), v);

	    switch (((ComposedFact) fact).getOperator()) {
	    // case AND :
	    // sFact = "( " + sEsquerda + " AND " + sDireita + " )";
	    // break;
	    // case OR :
	    // sFact = "( " + sEsquerda + " OR " + sDireita + " )";
	    // break;
	    }
	} else {
	    SimpleFact f1 = (SimpleFact) fact;

	    // v.add ( "( " + f1.getSubject ( ) + ", " + f1.getPredicate ( ) +
	    // ", " + f1.getObject ( ) + " )" );
	    v.add(f1);
	}
    }

    private boolean factHelper(Fact fact, SimpleFact fact2) {
	if ((fact == null) || (fact2 == null)) {
	    return true;
	}

	if (fact instanceof ComposedFact) {
	    factHelper(((ComposedFact) fact).getTerm1(), fact2);

	    factHelper(((ComposedFact) fact).getTerm2(), fact2);

	    switch (((ComposedFact) fact).getOperator()) {
	    case AND:
		if (((ComposedFact) fact).getTerm1().checked
			&& ((ComposedFact) fact).getTerm2().checked) {
		    fact.checked = true;
		    return true;
		}
		break;
	    case OR:
		if (((ComposedFact) fact).getTerm1().checked
			|| ((ComposedFact) fact).getTerm2().checked) {
		    fact.checked = true;
		    return true;
		}
		break;
	    }
	} else {
	    SimpleFact f1 = (SimpleFact) fact;
	    SimpleFact f2 = (SimpleFact) fact2;

	    if (f1.getSubject().equals(f2.getSubject()))
		if (f1.getPredicate().equals(f2.getPredicate()))
		    if (f1.getObject().equals(f2.getObject())) {
			fact.checked = true;
			return true;
		    } else
			return false;
		else
		    return false;
	    else
		return false;
	}

	return checked;
    }
}
