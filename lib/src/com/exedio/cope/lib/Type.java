
package persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Type
{
	private final Class javaClass;
	
	private final Attribute[] attributes;
	private final List attributeList;

	private final UniqueConstraint[] uniqueConstraints;
	private final List uniqueConstraintList;
	
	public Type(final Class javaClass, final Attribute[] attributes, final UniqueConstraint[] uniqueConstraints, final Runnable initializer)
	{
		this.javaClass = javaClass;
		this.attributes = attributes;
		this.attributeList = Collections.unmodifiableList(Arrays.asList(attributes));
		this.uniqueConstraints = uniqueConstraints;
		this.uniqueConstraintList = Collections.unmodifiableList(Arrays.asList(uniqueConstraints));
		initializer.run();
	}
	
	public final Class getJavaClass()
	{
		return javaClass;
	}
	
	public final List getAttributes()
	{
		return attributeList;
	}
	
	public final List getUniqueConstraints()
	{
		return uniqueConstraintList;
	}
	

	private String toStringCache = null;
	
	public final String toString()
	{
		if(toStringCache!=null)
			return toStringCache;
		
		final StringBuffer buf = new StringBuffer();
		
		buf.append(javaClass.getName());
		for(int i = 0; i<uniqueConstraints.length; i++)
		{
			buf.append(' ');
			buf.append(uniqueConstraints[i].toString());
		}
		
		toStringCache = buf.toString();
		return toStringCache;
	}
	
}
