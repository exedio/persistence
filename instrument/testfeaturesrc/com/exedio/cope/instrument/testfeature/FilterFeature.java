package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;

@WrapFeature
public final class FilterFeature
{
	private final OptionFeature source;

	public FilterFeature(final OptionFeature source)
	{
		this.source=source;
	}

	@SuppressWarnings("static-method")
	@Wrap(order=10)
	public void simple(@SuppressWarnings("unused") final Item item)
	{
		throw new RuntimeException();
	}

	public FilterFeature sourceNotNull()
	{
		if ( source==null )
		{
			throw new RuntimeException();
		}
		return this;
	}
}
