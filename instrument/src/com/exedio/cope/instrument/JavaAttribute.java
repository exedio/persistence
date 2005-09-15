/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an attribute of a class parsed by the
 * java parser.
 * Contains additional information about this attribute
 * described in the doccomment of this attribute.
 * @see Injector
 * 
 * @author Ralf Wiebicke
 */
final class JavaAttribute
	extends JavaFeature
	implements TokenConsumer
{
	private JavaClass.Value rtvalue = null;

	JavaAttribute(
		JavaClass parent,
		int modifiers,
		String type,
		String name)
		throws InjectorParseException
	{
		// parent must not be null
		super(parent.file, parent, modifiers, type, name);
		if (type == null)
			throw new RuntimeException();
	}

	/**
	 * Constructs a java attribute with the same
	 * <code>parent</code>, <code>modifiers</code> and <code>type</code>
	 * but the given name.
	 * Needed for comma separated attributes.
	 */
	JavaAttribute(JavaAttribute ja, String name)
		throws InjectorParseException
	{
		this(ja.parent, ja.modifier, ja.type, name);
	}

	/**
	 * Return a fully qualified name of the attribute,
	 * including class and package path.
	 * Syntax follows the javadoc tags,
	 * with a '#' between class and attribute name.
	 * Is used for type tracing log files.
	 */
	final String getFullDocName()
	{
		return file.getPackageName()
			+ '.'
			+ parent.name
			+ '#'
			+ name;
	}

	/**
	 * See Java Specification 8.3.1 &quot;Field Modifiers&quot;
	 */
	final int getAllowedModifiers()
	{
		return Modifier.PUBLIC
			| Modifier.PROTECTED
			| Modifier.PRIVATE
			| Modifier.FINAL
			| Modifier.STATIC
			| Modifier.TRANSIENT
			| Modifier.VOLATILE;
	}
	
	private final ArrayList initializerArguments = new ArrayList();
	private final StringBuffer currentArgument = new StringBuffer();
	private int bracketLevel = 0;
	
	public void addToken(final char token)
	{
		switch(token)
		{
			case '(':
			{
				bracketLevel++;
				break;
			}
			case ')':
			{
				if(bracketLevel==1)
				{
					initializerArguments.add(currentArgument.toString());
					currentArgument.setLength(0);
				}
				bracketLevel--;
				break;
			}
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				break;
			case ',':
			{
				if(bracketLevel==1)
				{
					initializerArguments.add(currentArgument.toString());
					currentArgument.setLength(0);
					break;
				}
			}
			default:
			{
				if(bracketLevel==1)
				{
					currentArgument.append(token);
				}
				break;
			}
		}
	}
	
	List getInitializerArguments()
	{
		return initializerArguments;
	}
	
	// --------------------
	
	private StringBuffer initializer = new StringBuffer();

	public void addChar(char c)
	{
		initializer.append(c);
	}
	
	String getInitializer()
	{
		return initializer.toString();
	}
	
	JavaClass.Value evaluate()
	{
		if(rtvalue==null)
		{
			rtvalue = parent.evaluate(getInitializer());
			parent.file.repository.putRtValue(this, rtvalue);
		}
		
		return rtvalue;
	}

}
