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

public enum Visibility
{
	/** don't generate the element */
	NONE,

	/**
	 * Use the generator's default visibility for the element.
	 * The default visibility depends on context, see
	 * {@link Wrapper#visibility()},
	 * {@link WrapperType#type()},
	 * {@link WrapperType#constructor()},
	 * {@link WrapperType#genericConstructor()}, and
	 * {@link WrapperType#activationConstructor()}.
	 */
	DEFAULT,
	PRIVATE,
	PACKAGE,
	PROTECTED,
	PUBLIC;


	boolean exists()
	{
		return this!=NONE;
	}

	boolean isDefault()
	{
		return this==DEFAULT;
	}

	int getModifier(final int defaultModifier)
	{
		switch(this)
		{
			case NONE:      throw new RuntimeException();
			case DEFAULT:   return defaultModifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);
			case PRIVATE:   return Modifier.PRIVATE;
			case PACKAGE:   return 0;
			case PROTECTED: return Modifier.PROTECTED;
			case PUBLIC:    return Modifier.PUBLIC;
			default:
				throw new RuntimeException(String.valueOf(this));
		}
	}
}
