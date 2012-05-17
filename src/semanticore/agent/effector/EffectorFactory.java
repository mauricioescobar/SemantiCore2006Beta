package semanticore.agent.effector;

import java.lang.reflect.Constructor;
import semanticore.domain.SemantiCoreDefinitions;

public class EffectorFactory {
    public EffectorFactory() {

    }

    public Effector getEffectorEngine(EffectorComponent dc) {
	Effector effectorEngine = null;

	Class[] args = new Class[1];

	try {
	    Class clName = Class.forName(getEffectorEngineClass());
	    args[0] = Class
		    .forName(SemantiCoreDefinitions.EFFECTOR_COMPONENT_NAME);
	    Constructor cons = clName.getDeclaredConstructor(args);
	    Object[] arg = new Object[1];
	    arg[0] = dc;
	    Object o = cons.newInstance(arg);
	    effectorEngine = (Effector) o;
	} catch (Exception ex) {
	    System.err.println("[ E ] Effector Component | Effector Engine: "
		    + ex.getMessage());
	} finally {
	    return effectorEngine;
	}
    }

    private String getEffectorEngineClass() {
	return SemantiCoreDefinitions.EFFECTOR_ENGINE_HOTSPOT;
    }
}
