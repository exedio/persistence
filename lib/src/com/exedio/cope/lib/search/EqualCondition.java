
package persistence.search;

import persistence.Attribute;
import persistence.StringAttribute;

public final class EqualCondition extends Condition
{
	public final Attribute attribute;
	public final Object value;

	public EqualCondition(final StringAttribute attribute, final String value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public final String toString()
	{
		return attribute.getName() + "='" + value + '\'';
	}
	
}
