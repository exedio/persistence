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

import java.lang.reflect.Modifier;

final class LocalCopeType extends CopeType<LocalCopeFeature>
{
	final JavaClass javaClass;
	private final String name;
	private final WrapperType option;

	private CopeType<?> supertype;

	LocalCopeType(final JavaClass javaClass, final Kind kind)
	{
		super(kind);
		this.javaClass=javaClass;
		this.name = javaClass.name;
		this.option = AnnotationHelper.getOrDefault(javaClass.typeOption);
		registerFeatures();
	}

	private void registerFeatures()
	{
		for(final JavaField javaField : javaClass.getFields())
		{
			final int modifier = javaField.modifier;
			if(!Modifier.isFinal(modifier) || !Modifier.isStatic(modifier))
				continue;

			if(javaField.wrapperIgnore!=null)
				continue;

			final Class<?> typeClass;
			try
			{
				typeClass = javaField.file.findTypeExternallyOrFail(javaField.typeFullyQualified);
			}
			catch(final ClassNotFoundException e)
			{
				throw new RuntimeException("can't find "+javaField.typeFullyQualified+" for "+javaClass.name+"#"+javaField.name, e);
			}
			if(!typeClass.isAnnotationPresent(WrapFeature.class))
				throw new RuntimeException(typeClass+" must be annotated by @WrapFeature, required for "+javaClass.name+"#"+javaField.name);
			register(new LocalCopeFeature(this, javaField));
		}
	}

	@Override
	String getName()
	{
		return name;
	}

	@Override
	WrapperType getOption()
	{
		return option;
	}

	boolean isInterface()
	{
		return javaClass.isInterface();
	}

	void endBuildStage()
	{
		javaClass.file.repository.assertGenerateStage();

		final Class<?> externalType = javaClass.file.findTypeExternally(javaClass.fullyQualifiedSuperclass);
		if(externalType==kind.topClass)
		{
			supertype = null;
		}
		else if (externalType!=null)
		{
			supertype = new ExternalCopeType(kind, externalType);
		}
		else
		{
			supertype = javaClass.file.repository.getCopeType(javaClass.fullyQualifiedSuperclass);
			if (supertype.kind.topClass!=kind.topClass) throw new RuntimeException();
		}
	}

	@Override
	CopeType<?> getSuperclass()
	{
		javaClass.file.repository.assertNotBuildStage();

		return supertype;
	}

	@Override
	int getTypeParameters()
	{
		return javaClass.typeParameters;
	}

	@Override
	String getCanonicalName()
	{
		return javaClass.getCanonicalName();
	}

	@Override
	int getModifier()
	{
		return javaClass.modifier;
	}

	@Override
	void assertNotBuildStage()
	{
		javaClass.file.repository.assertNotBuildStage();
	}

	@Override
	void assertNotGenerateStage()
	{
		javaClass.file.repository.assertNotGenerateStage();
	}

	@Override
	CopeFeature getDeclaredFeatureByInstance(final Object instance)
	{
		final JavaField field = javaClass.getFieldByInstance(instance);
		if (field!=null)
		{
			final LocalCopeFeature localFeature = getFeature(field.name);
			if (localFeature==null)
			{
				if (field.wrapperIgnore!=null)
				{
					throw new RuntimeException("attempt to access ignored field '"+field.name+"'");
				}
				else
				{
					// should not happen
					throw new RuntimeException();
				}
			}
			localFeature.assertJavaField(field);
			return localFeature;
		}
		return null;
	}
}
