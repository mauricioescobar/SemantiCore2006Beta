package semanticore.agent.decision;

import java.lang.reflect.Constructor;
import semanticore.domain.SemantiCoreDefinitions;

public class DecisionFactory {

    public DecisionFactory() {
    }

    // Este m�todo deve instanciar din�micamente o mecanismo decis�rio de acordo
    // com o arquivo de configura��o
    public DecisionEngine getDecisionEngine(DecisionComponent dc) {
	DecisionEngine decisionEngine = null;
	Class[] args = new Class[1];

	// sem parametro no construtor
	try {
	    Class clName = Class.forName(getDecisionEngineClass());
	    Constructor cons = clName.getDeclaredConstructor(null);
	    Object o = cons.newInstance(null);
	    decisionEngine = (DecisionEngine) o;
	} catch (Exception ex) {
	    System.err.println("[ E ] DecisionComponent : Decision Engine: "
		    + ex.getMessage());
	    ex.printStackTrace();
	} finally {
	    return decisionEngine;
	}

	// com parametro no construtor
	// try
	// {
	// Class clName = Class.forName ( getDecisionEngineClass ( ) );
	// args [ 0 ] = Class.forName (
	// SemantiCoreDefinitions.DECISION_COMPONENT_NAME );
	// Constructor cons = clName.getDeclaredConstructor ( args );
	// Object [ ] arg = new Object [ 1 ];
	// arg [ 0 ] = dc;
	// Object o = cons.newInstance ( arg );
	// decisionEngine = ( DecisionEngine ) o;
	// }
	// catch ( Exception ex )
	// {
	// System.err.println ( "[ E ] DecisionComponent : Decision Engine: " +
	// ex.getMessage ( ) );
	// }
	// finally
	// {
	// return decisionEngine;
	// }
    }

    // Recupera o nome do motor de tomada de decis�es do arquivo de configura��o
    private String getDecisionEngineClass() {
	return SemantiCoreDefinitions.DECISION_ENGINE_HOTSPOT;
    }
}
