
package persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Type
{
	private final Class javaClass;
	private final Attribute[] attributes;
	private final List attributeList;
	
	public Type(final Class javaClass, final Attribute[] attributes, final Runnable initializer)
	{
		this.javaClass = javaClass;
		this.attributes = attributes;
		this.attributeList = Collections.unmodifiableList(Arrays.asList(attributes));
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
	
	public final String toString()
	{
		return javaClass.getName();
	}
	
}
