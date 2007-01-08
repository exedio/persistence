/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

/**
 * Represents a method of a class parsed by the java parser.
 * @see Injector
 *
 * @author Ralf Wiebicke
 */
final class JavaMethod extends JavaBehaviour
{
	
	public JavaMethod(JavaClass parent, int modifiers, String type, String name)
	throws InjectorParseException
	{
		// parent must not be null
		super(parent, modifiers, type, name);
		
		if(type==null)
			throw new RuntimeException();
	}
	
	/**
	 * See Java Specification 8.4.3 &quot;Method Modifiers&quot;
	 */
	@Override
	public final int getAllowedModifiers()
	{
		return
		java.lang.reflect.Modifier.PUBLIC |
		java.lang.reflect.Modifier.PROTECTED |
		java.lang.reflect.Modifier.PRIVATE |
		java.lang.reflect.Modifier.FINAL |
		java.lang.reflect.Modifier.STATIC |
		java.lang.reflect.Modifier.ABSTRACT |
		java.lang.reflect.Modifier.NATIVE |
		java.lang.reflect.Modifier.SYNCHRONIZED;
	}
	
}
