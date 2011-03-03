/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.util.List;

import com.exedio.cope.Condition;
import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.util.CharSet;

	final class SubContentType extends ContentType<String>
	{
		private final String major;
		private final String prefix;
		private final int prefixLength;

		SubContentType(
				final String major,
				final boolean isfinal,
				final boolean optional)
		{
			super(makeField(30, new CharSet('-', '.', '0', '9', 'a', 'z')), isfinal, optional, "minor");
			this.major = major;
			this.prefix = major + '/';
			this.prefixLength = this.prefix.length();

			if(major==null)
				throw new NullPointerException("fixedMimeMajor");
		}

		@Override
		SubContentType copy()
		{
			return new SubContentType(major, field.isFinal(), !field.isMandatory());
		}

		@Override
		SubContentType toFinal()
		{
			return new SubContentType(major, true, !field.isMandatory());
		}

		@Override
		SubContentType optional()
		{
			return new SubContentType(major, field.isFinal(), true);
		}

		@Override
		boolean check(final String contentType)
		{
			return contentType.startsWith(prefix);
		}

		@Override
		String describe()
		{
			return prefix + '*';
		}

		@Override
		List<String> getAllowed()
		{
			return null;
		}

		@Override
		String get(final Item item, final DateField nullSensor)
		{
			final String minor = field.get(item);
			return (minor!=null) ? (prefix + minor) : null;
		}

		@Override
		String set(final String contentType)
		{
			assert check(contentType);
			return contentType.substring(prefixLength);
		}

		@Override
		Condition equal(final String contentType)
		{
			return
				contentType.startsWith(prefix)
				? field.equal(contentType.substring(prefixLength))
				: Condition.FALSE;
		}
	}
