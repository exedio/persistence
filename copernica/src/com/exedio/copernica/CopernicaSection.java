package com.exedio.copernica;

import java.util.Collection;

import com.exedio.cope.lib.Type;

public interface CopernicaSection extends Component
{
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Attribute attributes}.
	 */
	public Collection getCopernicaAttributes(Type type);
	
}
