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

import com.sun.source.tree.ModifiersTree;
import javax.lang.model.element.Modifier;

final class TreeApiHelper
{
	static int toModifiersInt(final ModifiersTree modifiers)
	{
		int result=0;
		for (final Modifier flag: modifiers.getFlags())
		{
			result |= toModifiersInt(flag);
		}
		return result;
	}

	private static int toModifiersInt(final Modifier flag)
	{
		switch (flag)
		{
			case ABSTRACT: return java.lang.reflect.Modifier.ABSTRACT;
			case DEFAULT: throw new RuntimeException("unexpected DEFAULT modifier");
			case SEALED: throw new RuntimeException("unexpected SEALED modifier");
			case NON_SEALED: throw new RuntimeException("unexpected NON_SEALED modifier");
			case FINAL: return java.lang.reflect.Modifier.FINAL;
			case NATIVE: return java.lang.reflect.Modifier.NATIVE;
			case PRIVATE: return java.lang.reflect.Modifier.PRIVATE;
			case PROTECTED: return java.lang.reflect.Modifier.PROTECTED;
			case PUBLIC: return java.lang.reflect.Modifier.PUBLIC;
			case STATIC: return java.lang.reflect.Modifier.STATIC;
			case STRICTFP: return java.lang.reflect.Modifier.STRICT;
			case SYNCHRONIZED: return java.lang.reflect.Modifier.SYNCHRONIZED;
			case TRANSIENT: return java.lang.reflect.Modifier.TRANSIENT;
			case VOLATILE: return java.lang.reflect.Modifier.VOLATILE;
			default: throw new RuntimeException(flag.toString());
		}
	}


	private TreeApiHelper()
	{
		// prevent instantiation
	}
}
