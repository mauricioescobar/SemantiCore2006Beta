package semanticore.domain.control;

public class DomainPart extends Domain {
    private String domainPartName;

    public DomainPart(String address, String port, String domainName,
	    String domainPartName) {
	super(address, port, domainName);

	this.domainPartName = domainPartName;
    }

    public String getDomainPartName() {
	return domainPartName;
    }
}
