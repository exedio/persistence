
package com.exedio.copernica;

import com.exedio.cope.lib.Type;
import java.util.Collection;

public interface Section extends Component
{
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Attribute attributes}.
	 */
	public Collection getAttributes(Type type);
	
}
