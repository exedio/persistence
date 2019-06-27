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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

final class ExternalCopeType extends CopeType<ExternalCopeFeature>
{
	private final Class<?> itemClass;

	ExternalCopeType(final Kind kind, final Class<?> itemClass)
	{
		super(kind);
		if (!kind.topClass.isAssignableFrom(itemClass)) throw new RuntimeException();
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
	ExternalCopeType getSuperclass()
	{
		final Class<?> superclass = itemClass.getSuperclass();
		if(superclass==kind.topClass)
			return null;
		else
			return new ExternalCopeType(kind, superclass);
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
	CopeFeature getDeclaredFeatureByInstance(final Object instance)
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
	void assertNotGenerateStage()
	{
		// empty
	}
}
