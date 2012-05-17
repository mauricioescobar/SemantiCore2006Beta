package semanticore.domain.model;

import java.io.Serializable;

public abstract class ComponentInterpreter<PATTERN, COMPONENT> implements
	Serializable {
    protected COMPONENT component;

    public ComponentInterpreter(Component c) {
	this.component = (COMPONENT) c;
    }

    public abstract Object evaluate(PATTERN a) throws Exception;
}
