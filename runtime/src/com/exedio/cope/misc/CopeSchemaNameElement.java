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

package com.exedio.cope.misc;

import com.exedio.cope.CopeSchemaName;
import java.lang.annotation.Annotation;
import java.util.Objects;

public final class CopeSchemaNameElement
{
	public static CopeSchemaName get(final String value)
	{
		Objects.requireNonNull(value, "value");

		return new CopeSchemaName()
		{
			public Class<? extends Annotation> annotationType()
			{
				return CopeSchemaName.class;
			}

			public String value()
			{
				return value;
			}
		};
	}

	public static CopeSchemaName getEmpty()
	{
		return get("");
	}

	private CopeSchemaNameElement()
	{
		// prevent instantiation
	}
}
