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

import static com.exedio.cope.misc.Check.requireNonEmpty;

import com.exedio.cope.Item;
import java.util.HashMap;
import java.util.function.Supplier;

final class Kind
{
	final String top;
	final String topSimple;
	final String wildcardClassCaster;

	final String typeField;
	final String typeFactory;
	final String typeDoc;

	final boolean hasGenericConstructor;
	final String activationConstructor;
	final boolean allowStaticClassToken;
	final boolean revertFeatureBody;
	final String featurePrefix;
	final String featurePostfix;
	final String featureThis;
	final boolean isItem; // TODO remove dependency on this field

	private Kind(final WrapType anno)
	{
		wildcardClassCaster = TypeMirrorHelper.get(anno::wildcardClassCaster).getName();

		final Class<?> typeFactoryClass = TypeMirrorHelper.get(anno::type);
		if(typeFactoryClass==StringGetterDefault.class)
		{
			typeField = null;
			typeFactory = null;
			typeDoc = null;
		}
		else
		{
			final java.lang.reflect.Method method;
			try
			{
				method = typeFactoryClass.getMethod(TYPE_FACTORY_METHOD, Class.class);
			}
			catch(final NoSuchMethodException e)
			{
				throw new RuntimeException(e);
			}
			typeField = method.getReturnType().getName();
			typeFactory = typeFactoryClass.getName();
			typeDoc = requireNonEmpty(anno.typeDoc(), "@WrapType#typeDoc");
		}

		hasGenericConstructor = anno.hasGenericConstructor();
		activationConstructor = name(anno::activationConstructor);
		allowStaticClassToken = anno.allowStaticClassToken();
		revertFeatureBody = anno.revertFeatureBody();
		featurePrefix = anno.featurePrefix();
		featurePostfix = anno.featurePostfix();
		featureThis = anno.featureThis();

		final Class<?> topClass = TypeMirrorHelper.get(anno::top);
		top = topClass.getName();
		topSimple = topClass.getSimpleName();

		isItem = topClass==Item.class;
	}

	static final String TYPE_FACTORY_METHOD = "newType";


	private static final HashMap<WrapType, Kind> kinds = new HashMap<>();

	static Kind valueOf(final WrapType anno)
	{
		if(anno==null)
			return null;

		return kinds.computeIfAbsent(anno, Kind::new);
	}


	static <T> String name(final Supplier<Class<T>> clazz)
	{
		final String result = TypeMirrorHelper.get(clazz).getName();

		if(result.equals(DEFAULT_NAME))
			return null;

		return result;
	}

	private static final String DEFAULT_NAME = StringGetterDefault.class.getName();
}
