
package com.exedio.copernica;

import java.util.Collection;

public interface Category extends Component
{
	/**
	 * @return a collection of {@link Category categories}.
	 */
	// TODO: rename to getCopernicaSubCategories
	public Collection getSubCategories();
	
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Type types}.
	 */
	// TODO: rename to getCopernicaTypes
	public Collection getTypes();
	
}
