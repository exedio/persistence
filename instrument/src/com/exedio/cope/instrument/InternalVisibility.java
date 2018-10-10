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

enum InternalVisibility
{
	PUBLIC(Modifier.PUBLIC),
	PROTECTED(Modifier.PROTECTED),
	PACKAGE(0),
	PRIVATE(Modifier.PRIVATE);

	final int modifier;

	InternalVisibility(final int modifier)
	{
		this.modifier = modifier;
	}

	static InternalVisibility forModifier(final int modifier)
	{
		switch(modifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE))
		{
			case Modifier.PUBLIC:
				return PUBLIC;
			case Modifier.PROTECTED:
				return PROTECTED;
			case 0:
				return PACKAGE;
			case Modifier.PRIVATE:
				return PRIVATE;
			default:
				throw new RuntimeException(Integer.toString(modifier));
		}
	}
}
