
package persistence.search;

import persistence.Attribute;
import persistence.StringAttribute;

public final class EqualCondition
{
	final Attribute attribute;
	final Object value;

	public EqualCondition(final StringAttribute attribute, final String value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
}
