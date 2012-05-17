package semanticore.agent.execution;

import java.lang.reflect.Constructor;

import semanticore.domain.SemantiCore;
import semanticore.domain.SemantiCoreDefinitions;

public class ExecutionFactory {

    public ExecutionFactory() {
	super();
    }

    public ExecutionEngine getExecutionEngine(ExecutionComponent dc) {
	ExecutionEngine executionEngine = null;
	Class[] args = new Class[1];

	try {
	    Class clName = Class.forName(getExecutionEngineClass());
	    args[0] = Class
		    .forName(SemantiCoreDefinitions.EXECUTION_COMPONENT_NAME);
	    Constructor cons = clName.getDeclaredConstructor(args);
	    Object[] arg = new Object[1];
	    arg[0] = dc;
	    Object o = cons.newInstance(arg);
	    executionEngine = (ExecutionEngine) o;
	} catch (Exception ex) {
	    SemantiCore.notification
		    .print("ERROR | Execution Component | Execution Engine: "
			    + ex.getMessage());
	} finally {
	    return executionEngine;
	}
    }

    private String getExecutionEngineClass() {
	return SemantiCoreDefinitions.EXECUTION_ENGINE_HOTSPOT;
    }
}
