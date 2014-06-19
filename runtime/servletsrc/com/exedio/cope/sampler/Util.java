/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.MediaPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Util
{
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	static final SetValue<String> cutAndMap(final StringField f, final String s)
	{
		return f.map(shortify(f, s));
	}

	private static final String shortify(final StringField f, final String s)
	{
		assert f.getMinimumLength()==1 : f;
		assert f.getMaximumLength()>=POSTFIX.length() : f;

		if(s==null || s.isEmpty())
			return null;

		final int max = f.getMaximumLength();
		if(s.length()>max)
			return s.substring(0, max-POSTFIX.length()) + POSTFIX;

		return s;
	}

	private static final String POSTFIX = " ...";


	static final SetValue<Integer> map(final IntegerField f, final int from, final int to)
	{
		return f.map(to - from);
	}

	static final SetValue<Integer> map(final IntegerField f, final long from, final long to)
	{
		final long result = to - from;
		if(result>Integer.MAX_VALUE)
		{
			if(logger.isWarnEnabled())
				logger.warn("integer exceeded for {} {}", f, result);

			return f.map(Integer.MAX_VALUE);
		}
		return f.map((int)result);
	}

	static final int same(final int from, final int to)
	{
		assert from==to;
		return to;
	}

	static final Type<?> same(final Type<?> from, final Type<?> to)
	{
		assert from==to;
		return to;
	}

	static final MediaPath same(final MediaPath from, final MediaPath to)
	{
		assert from==to;
		return to;
	}

	private Util()
	{
		// prevent instantiation
	}
}
