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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.TypesBound;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockActivationParameters;
import com.exedio.cope.pattern.BlockType;
import com.exedio.cope.pattern.Composite;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("SE_BAD_FIELD")
enum Kind
{
	item(
			Item.class,
			new Type(
					"The persistent type information for {0}.",
					com.exedio.cope.Type.class, TypesBound.class
			),
			true, // hasGenericConstructor
			ActivationParameters.class,
			true, // allowStaticClassToken
			false, // revertFeatureBody
			"", "", "this",
			JavaRepository.DummyItem.class
	),
	composite(
			Composite.class,
			null, // type
			true, // hasGenericConstructor
			null, // activationConstructor
			false, // allowStaticClassToken
			true, // revertFeatureBody
			"", "", "this",
			JavaRepository.DummyComposite.class
	),
	block(
			Block.class,
			new Type(
					"The type information for {0}.",
					BlockType.class, BlockType.class
			),
			false, // hasGenericConstructor
			BlockActivationParameters.class,
			false, // allowStaticClassToken
			false, // revertFeatureBody
			"field().of(", ")", "item()",
			JavaRepository.DummyBlock.class
	);


	final Class<?> topClass;
	final String top;
	final String topSimple;
	final Type type;
	final boolean hasGenericConstructor;
	final String activationConstructor;
	final boolean allowStaticClassToken;
	final boolean revertFeatureBody;
	final String featurePrefix;
	final String featurePostfix;
	final String featureThis;
	final Class<?> dummy;
	final boolean isItem; // TODO remove dependency on this field

	private Kind(
			final Class<?> topClass,
			final Type type,
			final boolean hasGenericConstructor,
			final Class<?> activationConstructor,
			final boolean allowStaticClassToken,
			final boolean revertFeatureBody,
			final String featurePrefix,
			final String featurePostfix,
			final String featureThis,
			final Class<?> dummy)
	{
		this.topClass = topClass;
		this.top = topClass.getName();
		this.topSimple = topClass.getSimpleName();
		this.type = type;
		this.hasGenericConstructor = hasGenericConstructor;
		this.activationConstructor = activationConstructor!=null ? activationConstructor.getName() : null;
		this.allowStaticClassToken = allowStaticClassToken;
		this.revertFeatureBody = revertFeatureBody;
		this.featurePrefix = featurePrefix;
		this.featurePostfix = featurePostfix;
		this.featureThis = featureThis;
		this.dummy = dummy;

		isItem = topClass==Item.class;
	}


	static final class Type
	{
		final String doc;
		final String field;
		final String factory;

		Type(
				final String doc,
				final Class<?> field,
				final Class<?> factory)
		{
			this.doc = doc;
			this.field = field.getName();
			this.factory = factory.getName();
		}
	}


	static Kind valueOf(final ClassVisitor visitor)
	{
		Kind result = null;
		for(final Kind kind : values())
		{
			if(visitor.context.isSubtype(visitor.getCurrentPath(), kind.topClass))
			{
				if(result!=null)
					throw new RuntimeException("" + result + '/' + kind);

				result = kind;
			}
		}
		return result;
	}
}