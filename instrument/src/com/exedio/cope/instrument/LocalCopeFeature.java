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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

final class LocalCopeFeature extends CopeFeature
{
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	private final JavaField javaField;
	private final Boolean initialByConfiguration;

	LocalCopeFeature(final LocalCopeType parent, final JavaField javaField)
	{
		super(parent);
		this.javaField=javaField;
		final WrapperInitial initialConfig = javaField.wrapperInitial;
		this.initialByConfiguration = initialConfig==null ? null : initialConfig.value();
	}

	@Override
	String getName()
	{
		return javaField.name;
	}

	@Override
	int getModifier()
	{
		return javaField.modifier;
	}

	boolean isDeprecated()
	{
		return javaField.deprecated;
	}

	@Override
	Boolean getInitialByConfiguration()
	{
		return initialByConfiguration;
	}

	String getTypeParameter(final int number)
	{
		return javaField.getTypeParameter(number);
	}

	@Override
	Object evaluate()
	{
		return javaField.evaluate();
	}

	Wrapper getOption(final String modifierTag, final Type[] parameterTypes)
	{
		final Wrapper option = javaField.getWrappers(modifierTag, parameterTypes);
		return option!=null ? option : OPTION_DEFAULT;
	}

	/**
	 * assert that the given JavaField is the one wrapped by this LocalCopeFeature
	 */
	void assertJavaField(final JavaField javaField)
	{
		if (javaField!=this.javaField)
			throw new RuntimeException();
	}

	private static final Wrapper OPTION_DEFAULT = new Wrapper()
	{
		@Override public Class<? extends Annotation> annotationType() { throw new RuntimeException(); }
		@Override public String wrap() { throw new RuntimeException(); }
		@Override public Class<?>[] parameters() { throw new RuntimeException(); }
		@Override public Visibility visibility() { return Visibility.DEFAULT; }
		@Override public boolean internal() { return false; }
		@Override public boolean booleanAsIs() { return false; }
		@Override public boolean asFinal() { return true; }
		@Override public boolean override() { return false; }
		@Override public String[] suppressWarnings() { return EMPTY_STRING_ARRAY; }
		@Override public String[] annotate() { return EMPTY_STRING_ARRAY; }
	};

	@Override
	String getJavadocReference()
	{
		return link(getName());
	}

	/**
	 * @throws RuntimeException if the feature cannot be resolved
	 */
	CopeFeature getFeatureByInstance(final Object instance, final String methodName)
	{
		final CopeFeature result = parent.getFeatureByInstance(instance);
		if(result==null)
			throw new RuntimeException("cannot resolve parameter "+instance+" for "+getName()+"/"+methodName);
		return result;
	}

	@Override
	String applyTypeShortcuts(final String type)
	{
		return javaField.applyTypeShortcuts(type);
	}
}
