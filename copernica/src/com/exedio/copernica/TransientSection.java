package com.exedio.copernica;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.exedio.cope.lib.Attribute;

public class TransientSection
	extends TransientComponent
	implements CopernicaSection
{
	private final List attributes;
	private final HashMap names = new HashMap();
	
	public TransientSection(final String id, final Attribute[] attributes)
	{
		super(id);
		this.attributes = Collections.unmodifiableList(Arrays.asList(attributes));
	}
	
	public Collection getCopernicaAttributes()
	{
		return attributes;
	}
}
