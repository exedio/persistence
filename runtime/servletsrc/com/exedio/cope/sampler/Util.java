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

package com.exedio.cope.sampler;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.pattern.MediaPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
final class Util
{
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	/**
	 * cutAndMap
	 */
	static SetValue<String> maC(
			final StringField settable,
			final String s)
	{
		return SetValue.map(settable, shortify(settable, s));
	}

	private static String shortify(final StringField settable, final String s)
	{
		assert settable.getMinimumLength()==1 : settable;
		assert settable.getMaximumLength()>=POSTFIX.length() : settable;

		if(s==null || s.isEmpty())
			return null;

		final int max = settable.getMaximumLength();
		if(s.length()>max)
			return s.substring(0, max-POSTFIX.length()) + POSTFIX;

		return s;
	}

	private static final String POSTFIX = " ...";


	/**
	 * diffAndMap
	 */
	static SetValue<Integer> maD(
			final IntegerField settable,
			final int from,
			final int to)
	{
		return SetValue.map(settable, to - from);
	}

	/**
	 * diffAndMap
	 */
	static SetValue<Integer> maD(
			final IntegerField settable,
			final long from,
			final long to)
	{
		final long result = to - from;
		if(result>Integer.MAX_VALUE)
		{
			if(logger.isWarnEnabled())
				logger.warn("integer exceeded for {} {}", settable, result);

			return settable.map(Integer.MAX_VALUE);
		}
		return SetValue.map(settable, (int)result);
	}

	static int same(final int from, final int to)
	{
		assert from==to;
		return to;
	}

	/**
	 * assertSameAndMap
	 */
	static SetValue<SamplerMediaId> maS(
			final ItemField<SamplerMediaId> settable,
			final MediaPath from,
			final MediaPath to)
	{
		assert from==to;
		return SetValue.map(settable, SamplerMediaId.get(to));
	}


	static IntegerField field(final int minimum)
	{
		return new IntegerField().toFinal().min(minimum);
	}

	static <E extends Item> ItemField<E> field(final Class<E> c)
	{
		return ItemField.create(c).toFinal();
	}


	private Util()
	{
		// prevent instantiation
	}
}
