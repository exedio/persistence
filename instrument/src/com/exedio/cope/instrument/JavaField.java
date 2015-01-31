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
 * Represents an attribute of a class parsed by the
 * java parser.
 * Contains additional information about this attribute
 * described in the doccomment of this attribute.
 * @see Parser
 *
 * @author Ralf Wiebicke
 */
final class JavaField
	extends JavaFeature
	implements InitializerConsumer
{
	private String docComment;

	private Object rtvalue = null;

	JavaField(
		final JavaClass parent,
		final int modifiers,
		final String type,
		final String name)
		throws ParserException
	{
		// parent must not be null
		super(parent.file, parent, modifiers, type, name);
		if (type == null)
			throw new RuntimeException();

		parent.add(this);
	}

	/**
	 * Constructs a java attribute with the same
	 * <tt>parent</tt>, <tt>modifiers</tt> and <tt>type</tt>
	 * but the given name.
	 * Needed for comma separated attributes.
	 */
	JavaField(final JavaField template, final String name)
		throws ParserException
	{
		this(template.parent, template.modifier, template.type, name);
	}

	@Override
	final int getAllowedModifiers()
	{
		return Modifier.fieldModifiers();
	}

	void setDocComment(final String docComment)
	{
		assert this.docComment==null;
		this.docComment = docComment;
	}

	String getDocComment()
	{
		return docComment;
	}

	// --------------------

	private StringBuilder initializerBuf = new StringBuilder();
	private String initializer = null;

	public void addToInitializer(final char c)
	{
		initializerBuf.append(c);
	}

	String getInitializer()
	{
		if(initializerBuf!=null)
		{
			assert initializer==null;
			initializer = initializerBuf.length()>0 ? initializerBuf.toString() : null;
			initializerBuf = null;
		}

		return initializer;
	}

	Object evaluate()
	{
		assert !file.repository.isBuildStage();

		if(rtvalue==null)
		{
			rtvalue = parent.evaluate(getInitializer());
			assert rtvalue!=null;
			parent.registerInstance(this, rtvalue);
		}

		return rtvalue;
	}

}
