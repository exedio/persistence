/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.util.Collection;
import java.util.Collections;


final class CopeHash extends CopeFeature
{
	private final String initializerArgument;

	public CopeHash(final JavaAttribute javaAttribute)
	{
		super(javaAttribute);
		this.initializerArgument = null;
	}

	public CopeHash(final JavaAttribute javaAttribute, final String initializerArgument)
	{
		super(javaAttribute);
		assert initializerArgument!=null;
		this.initializerArgument = initializerArgument;
	}

	final int getGeneratedCheckerModifier()
	{
		return (modifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)) | Modifier.FINAL;
		// TODO: implement checker option: return checkerOption.getModifier(javaAttribute.modifier);
	}

	final int getGeneratedSetterModifier()
	{
		return (modifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)) | Modifier.FINAL;
		// TODO: implement getter option: return setterOption.getModifier(javaAttribute.modifier);
	}
	
	Collection<Class> getSetterExceptions() throws InjectorParseException
	{
		if(initializerArgument==null)
			return Collections.<Class>emptyList();
		else
		{
			final CopeAttribute result = (CopeAttribute)type.getFeature(initializerArgument);
			if(result==null)
				throw new InjectorParseException("attribute >"+initializerArgument+"< in hash "+name+" not found.");
			return result.getSetterExceptions();
		}
	}
	
}
