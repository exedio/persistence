/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.instrument;

import com.exedio.cope.Item;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

final class ExternalCopeType extends CopeType<ExternalCopeFeature>
{
	private final Class<?> itemClass;

	ExternalCopeType(final Class<?> itemClass)
	{
		super(Kind.valueOf(Item.class.getAnnotation(WrapType.class)));
		if (!Item.class.isAssignableFrom(itemClass)) throw new RuntimeException();
		this.itemClass=itemClass;
		registerFeatures();
	}

	private void registerFeatures()
	{
		for (final Field declaredField: itemClass.getDeclaredFields())
		{
			final int modifiers=declaredField.getModifiers();
			if (!Modifier.isFinal(modifiers) || !Modifier.isStatic(modifiers))
				continue;
			if (declaredField.getAnnotation(WrapperIgnore.class)!=null)
				continue;
			if (declaredField.getType().isAnnotationPresent(WrapFeature.class))
				register(new ExternalCopeFeature(this, declaredField));
		}
	}

	@Override
	String getName()
	{
		return itemClass.getSimpleName();
	}

	@Override
	WrapperType getOption()
	{
		throw new RuntimeException("unexpected call - should only be needed for generating the class itself");
	}

	@Override
	boolean isInterface()
	{
		return itemClass.isInterface();
	}

	@Override
	Evaluatable getField(final String name)
	{
		try
		{
			final Field field=itemClass.getDeclaredField(name);
			if (Modifier.isStatic(field.getModifiers()))
			{
				return new ExternalEvaluatable(field);
			}
			else
			{
				return null;
			}
		}
		catch (final NoSuchFieldException e)
		{
			return null;
		}
	}

	@Override
	ExternalCopeType getSuperclass()
	{
		if (itemClass.getSuperclass()==Item.class)
			return null;
		else
			return new ExternalCopeType(itemClass.getSuperclass());
	}

	@Override
	int getTypeParameters()
	{
		throw new RuntimeException("unexpected call - should only be needed for generating the class itself");
	}

	@Override
	String getCanonicalName()
	{
		return itemClass.getCanonicalName();
	}

	@Override
	int getModifier()
	{
		return itemClass.getModifiers();
	}

	@Override
	CopeFeature getOwnFeatureByInstance(final Object instance)
	{
		for (final ExternalCopeFeature feature: getFeatures())
		{
			if (instance==feature.evaluate())
			{
				return feature;
			}
		}
		return null;
	}

	@Override
	void assertNotBuildStage()
	{
		// empty
	}

	@Override
	void assertGenerateStage()
	{
		// empty
	}

	@Override
	void assertNotGenerateStage()
	{
		// empty
	}

	private static class ExternalEvaluatable implements Evaluatable
	{
		private final Field field;

		ExternalEvaluatable(final Field field)
		{
			if (!Modifier.isStatic(field.getModifiers())) throw new RuntimeException();
			this.field=field;
		}

		@Override
		@SuppressFBWarnings("DP_DO_INSIDE_DO_PRIVILEGED")
		public Object evaluate()
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
}
