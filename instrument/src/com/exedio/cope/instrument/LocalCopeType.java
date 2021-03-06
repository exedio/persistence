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
import java.util.HashMap;

final class LocalCopeType extends CopeType<LocalCopeFeature>
{
	private static final HashMap<JavaClass, LocalCopeType> copeTypeByJavaClass = new HashMap<>();

	static LocalCopeType getCopeType(final JavaClass javaClass)
	{
		@SuppressWarnings("UnnecessaryLocalVariable")
		final LocalCopeType result = copeTypeByJavaClass.get(javaClass);
		//System.out.println("getCopeClass "+javaClass.getFullName()+" "+(result==null?"NULL":result.getName()));
		return result;
	}


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
		//noinspection ThisEscapedInObjectConstruction
		copeTypeByJavaClass.put(javaClass, this);

		//noinspection ThisEscapedInObjectConstruction
		javaClass.file.repository.add(this);

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

			final Class<?> typeClass = javaField.file.findTypeExternally(javaField.typeFullyQualified);
			if(typeClass==null || !typeClass.isAnnotationPresent(WrapFeature.class))
				throw new RuntimeException("can't find "+javaField.typeFullyQualified+" for "+javaClass.name+"#"+javaField.name);
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
		assert !javaClass.file.repository.isBuildStage();
		assert javaClass.file.repository.isGenerateStage();

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
		assert !javaClass.file.repository.isBuildStage();

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
		assert !javaClass.file.repository.isBuildStage();
	}

	@Override
	void assertNotGenerateStage()
	{
		assert !javaClass.file.repository.isGenerateStage();
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
