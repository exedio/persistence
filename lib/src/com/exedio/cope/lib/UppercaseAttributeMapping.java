
package persistence;

public final class UppercaseAttributeMapping implements AttributeMapping
{
	private final StringAttribute sourceAttribute;

	public UppercaseAttributeMapping(final StringAttribute sourceAttribute)
	{
		this.sourceAttribute = sourceAttribute;
	}

	public final Object mapJava(final Item item, final Object[] qualifiers)
	{
		return ((String)item.getAttribute(sourceAttribute, qualifiers)).toUpperCase();
	}
	
	public final String mapSQL()
	{
		return "UPPER(" + sourceAttribute.getName() + ')';
	}
	
	public final String toString()
	{
		return "upper(" + sourceAttribute.getName() + ')';
	}
	
}
