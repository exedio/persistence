package com.exedio.cope.instrument;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;

class ExternalCopeFeature extends CopeFeature
{
	private final Field field;

	ExternalCopeFeature(final ExternalCopeType parent, final Field field)
	{
		super(parent);
		this.field=field;
	}

	@Override
	String getName()
	{
		return field.getName();
	}

	@Override
	int getModifier()
	{
		return field.getModifiers();
	}

	@Override
	Boolean getInitialByConfiguration()
	{
		final WrapperInitial annotation=field.getAnnotation(WrapperInitial.class);
		return annotation==null?null:annotation.value();
	}

	@Override
	String getType()
	{
		return field.getGenericType().toString().replace('$', '.');
	}

	@Override
	@SuppressFBWarnings("DP_DO_INSIDE_DO_PRIVILEGED")
	Object evaluate()
	{
		field.setAccessible(true);
		try
		{
			return field.get(null);
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
