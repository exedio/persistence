
package com.exedio.cope.lib;

public abstract class AttributeMapping implements Function // TODO rename class to MappedFunction
{
	// TODO make it possible to have more than one source attribute, for arithmetic functions
	public final ObjectAttribute sourceAttribute;
	final String sqlMappingStart;
	final String sqlMappingEnd;
	private final String functionName;

	public AttributeMapping(final ObjectAttribute sourceAttribute,
									final String sqlMappingStart,
									final String sqlMappingEnd,
									final String functionName)
	{
		this.sourceAttribute = sourceAttribute;
		this.sqlMappingStart = sqlMappingStart;
		this.sqlMappingEnd = sqlMappingEnd;
		this.functionName = functionName;
	}

	public abstract Object mapJava(Object sourceValue);

	public final String toString()
	{
		return functionName + '(' + sourceAttribute.getName() + ')';
	}

	// second initialization phase ---------------------------------------------------

	private Type type;
	private String name;
	
	final void initialize(final Type type, final String name)
	{
		if(type==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();

		if(this.type!=null)
			throw new RuntimeException();
		if(this.name!=null)
			throw new RuntimeException();

		this.type = type;
		this.name = name;
	}
	
	public final Type getType()
	{
		if(this.type==null)
			throw new RuntimeException();

		return type;
	}
	
	public final String getName()
	{
		if(this.type==null)
			throw new RuntimeException();

		return name;
	}
	
}
