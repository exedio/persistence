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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Item;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.function.Supplier;

final class Kind
{
	final String top;
	final String topSimple;
	final String wildcardClassCaster;
	final Type type;
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
		wildcardClassCaster = TypeMirrorHelper.get(anno::wildcardClassCaster, false).getName();
		type = Type.valueOf(anno.type());
		hasGenericConstructor = anno.hasGenericConstructor();
		activationConstructor = name(anno::activationConstructor);
		allowStaticClassToken = anno.allowStaticClassToken();
		revertFeatureBody = anno.revertFeatureBody();
		featurePrefix = anno.featurePrefix();
		featurePostfix = anno.featurePostfix();
		featureThis = anno.featureThis();

		final Class<?> topClass = TypeMirrorHelper.get(anno::top, false);
		top = topClass.getName();
		topSimple = topClass.getSimpleName();

		isItem = topClass==Item.class;
	}


	static final class Type
	{
		final String doc;
		final String field;
		final String factory;

		private Type(
				final String doc,
				final String field,
				final String factory)
		{
			this.doc = requireNonNull(doc);
			this.field = requireNonNull(field);
			this.factory = requireNonNull(factory);
			if(doc.isEmpty())
				throw new IllegalArgumentException("@WrapType#doc must not be empty");
		}

		@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
		@SuppressFBWarnings("NP_NULL_PARAM_DEREF")
		static Type valueOf(final WrapType.Type anno)
		{
			final String doc = anno.doc();
			final String field = name(anno::field);
			final String factory = name(anno::factory);
			if(doc.isEmpty() && field==null && factory==null)
				return null;

			return new Type(doc, field, factory);
		}
	}


	private static final HashMap<WrapType, Kind> kinds = new HashMap<>();

	static Kind valueOf(final WrapType anno)
	{
		if(anno==null)
			return null;

		return kinds.computeIfAbsent(anno, Kind::new);
	}


	static <T> String name(final Supplier<Class<T>> clazz)
	{
		final String result = TypeMirrorHelper.get(clazz, false).getName();

		if(result.equals(DEFAULT_NAME))
			return null;

		return result;
	}

	private static final String DEFAULT_NAME = StringGetterDefault.class.getName();
}
