package com.exedio.copernica;

import java.util.Collection;

public interface CopernicaCategory extends Component
{
	/**
	 * @return a collection of {@link CopernicaCategory categories}.
	 */
	public Collection getCopernicaSubCategories();
	
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Type types}.
	 */
	public Collection getCopernicaTypes();
	
}
