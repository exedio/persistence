
package com.exedio.copernica;

import java.util.Collection;

import com.exedio.cope.lib.Type;

public interface Section extends Component
{
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Attribute attributes}.
	 */
	// TODO: rename to getCopernicaAttributes
	public Collection getAttributes(Type type);
	
}
