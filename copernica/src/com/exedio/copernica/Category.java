
package com.exedio.copernica;

import java.util.Collection;

public interface Category extends Component
{
	/**
	 * @return a collection of {@link Category categories}.
	 */
	public Collection getSubCategories();
	
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Type types}.
	 */
	public Collection getTypes();
	
}
