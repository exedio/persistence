/*
 * Copyright (C) 2000  Ralf Wiebicke
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

/**
 * Represents an attribute of a class.
 * Contains additional information about this attribute
 * described in the doccomment of this attribute.
 *
 * @author Ralf Wiebicke
 */
final class JavaField
	extends JavaFeature
{
	private final String initializer;

	private Object rtvalue = null;

	JavaField(
		final JavaClass parent,
		final int modifiers,
		final String type,
		final String name,
		final String docComment,
		final String initializer)
	{
		// parent must not be null
		super(parent.file, parent, modifiers, type, name, docComment);
		if (type == null)
			throw new RuntimeException();
		this.initializer=initializer;

		parent.add(this);
	}

	@Override
	final int getAllowedModifiers()
	{
		return Modifier.fieldModifiers();
	}

	String getInitializer()
	{
		return initializer;
	}

	Object evaluate()
	{
		assert !file.repository.isBuildStage();

		if(rtvalue==null)
		{
			if ( getInitializer()==null ) throw new RuntimeException("getInitializer() null");
			rtvalue = parent.evaluate(getInitializer());
			assert rtvalue!=null : getInitializer()+'/'+parent+'/'+name;
			parent.registerInstance(this, rtvalue);
		}

		return rtvalue;
	}
}
