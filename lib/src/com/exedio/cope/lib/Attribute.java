package persistence;

public class Attribute
{
	public Attribute()
	{
	}
	
	boolean initialized = false;
	
	boolean readOnly;
	boolean notNull;
	
	public void initialize(final boolean readOnly, final boolean notNull)
	{
		if(initialized)
			throw new RuntimeException();

		this.readOnly = readOnly;
		this.notNull = notNull;

		initialized = true;
	}

}


