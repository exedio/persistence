package persistence;

public class Attribute
{
	public Attribute()
	{
	}
	
	private boolean initialized = false;
	
	private String name;
	private boolean readOnly;
	private boolean notNull;
	
	public void initialize(final String name, final boolean readOnly, final boolean notNull)
	{
		if(initialized)
			throw new RuntimeException();

		this.name = name;
		this.readOnly = readOnly;
		this.notNull = notNull;

		initialized = true;
	}
	
	public final String toString()
	{
		// should be precomputed
		final StringBuffer buf = new StringBuffer();
		buf.append(name);
		buf.append('{');
		boolean first = true;
		if(readOnly)
		{
			if(first)
				first = false;
			else
				buf.append(',');
			buf.append("read-only");
		}
		if(notNull)
		{
			if(first)
				first = false;
			else
				buf.append(',');
			buf.append("not-null");
		}
		buf.append('}');
		return buf.toString();
	}

}


