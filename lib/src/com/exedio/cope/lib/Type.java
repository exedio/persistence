
package persistence;

public class Type
{
	final Class javaClass;
	final Attribute[] attributes;
	
	public Type(final Class javaClass, final Attribute[] attributes, final Runnable initializer)
	{
		this.javaClass = javaClass;
		this.attributes = attributes;
	}
	
}
