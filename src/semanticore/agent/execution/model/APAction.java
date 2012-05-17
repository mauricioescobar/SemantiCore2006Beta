package semanticore.agent.execution.model;

public class APAction extends Action {

    public APAction(String n) {
	super(n, "");
    }

    private Context context;

    public void run(Object params) {

    }

    public void setContext(Context c) {
	context = c;
    }

    @Override
    public void exec() {
	// TODO Auto-generated method stub

    }
}
