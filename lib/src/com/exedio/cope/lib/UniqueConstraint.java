
package persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class UniqueConstraint
{
	private final Attribute[] uniqueAttributes;
	private final List uniqueAttributeList;

	public UniqueConstraint(final Attribute[] uniqueAttributes)
	{
		this.uniqueAttributes = uniqueAttributes;
		this.uniqueAttributeList = Collections.unmodifiableList(Arrays.asList(uniqueAttributes));
	}
	
	public UniqueConstraint(final Attribute uniqueAttribute)
	{
		this(new Attribute[]{uniqueAttribute});
	}
	
	public final List getUniqueAttributes()
	{
		return uniqueAttributeList;
	}
	

	private String toStringCache = null;
	
	public final String toString()
	{
		if(toStringCache!=null)
			return toStringCache;
		
		final StringBuffer buf = new StringBuffer();
		
		buf.append("unique(");
		buf.append(uniqueAttributes[0].getName());
		for(int i = 1; i<uniqueAttributes.length; i++)
		{
			buf.append(',');
			buf.append(uniqueAttributes[i].getName());
		}
		buf.append(')');
		
		toStringCache = buf.toString();
		return toStringCache;
	}

}
