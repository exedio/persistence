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
import javax.lang.model.element.Element;

/**
 * Represents a java feature.
 * May be a class (even an inner class), an attribute or
 * a method.
 *
 * @author Ralf Wiebicke
 */
abstract class JavaFeature
{
	/**
	 * The java file, which contains this feature.
	 * Is never null.
	 */
	final JavaFile file;

	/**
	 * The class, which contains this feature.
	 * Is null for top-level (not inner) classes.
	 */
	final JavaClass parent;

	/**
	 * The modifier of this feature.
	 * @see java.lang.reflect.Modifier
	 */
	final int modifier;

	final String name;

	final Element sourceLocation;

	JavaFeature(
							final JavaFile file,
							final JavaClass parent,
							final int modifier,
							final String name,
							final Element sourceLocation)
	{
		this.file=file;
		this.parent=parent;
		this.modifier=modifier;
		this.name=name;
		this.sourceLocation=sourceLocation;
		if (name.contains("-")) throw new RuntimeException(name);

		if(file==null)
			throw new RuntimeException();

		if(parent!=null && file!=parent.file) // JavaFile objects are flyweight
			throw new RuntimeException();

		final int over=modifier&~getAllowedModifiers();
		if(over!=0)
			throw new RuntimeException(
			"modifier(s) "+Modifier.toString(over)+
			" not allowed for class feature "+name+
			" of type "+getClass().getName()+'.');
	}

	/**
	 * Subclasses use this method to specify,
	 * which modifiers are allowed for the specific kind
	 * of feature.
	 */
	abstract int getAllowedModifiers();

	@Override
	public String toString()
	{
		return getClass().getName()+'('+name+')';
	}
}
