
package com.exedio.cope.lib;

public abstract class AttributeMapping
{
	public final Attribute sourceAttribute;
	final String sqlMappingStart;
	final String sqlMappingEnd;
	private final String functionName;

	public AttributeMapping(final Attribute sourceAttribute,
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
	
}
