/*
 * Copyright (C) 2000  Ralf Wiebicke
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

package com.exedio.cope.instrument;

import com.exedio.cope.instrument.Lexer.CommentToken;
import java.util.ArrayList;

final class Instrumentor implements ParseConsumer
{

	/**
	 * Holds several properties of the class currently
	 * worked on.
	 */
	private JavaClass class_state=null;

	/**
	 * Collects the class states of outer classes,
	 * when operating on a inner class.
	 * @see #class_state
	 */
	private final ArrayList<JavaClass> class_state_stack = new ArrayList<JavaClass>();

	/**
	 * The last file level doccomment that was read.
	 */
	private String lastFileDocComment = null;

	public void onPackage(final JavaFile javafile)
	throws ParserException
	{
		// nothing to do here
	}

	public void onImport(final String importname)
	{
		// nothing to do here
	}

	public void onClass(final JavaClass jc)
			throws ParserException
	{
		//System.out.println("onClass("+jc.getName()+")");

		class_state_stack.add(class_state);
		class_state=jc;

		if(lastFileDocComment != null)
		{
			jc.setDocComment(lastFileDocComment);
			lastFileDocComment = null;
		}
	}

	public void onClassEnd(final JavaClass javaClass)
	throws ParserException
	{
		if(class_state!=javaClass)
			throw new RuntimeException();
		class_state = class_state_stack.remove(class_state_stack.size()-1);
	}

	public void onBehaviourHeader(final JavaBehaviour jb)
	{
		// nothing to do here
	}

	public void onFieldHeader(final JavaField ja)
	{
		// nothing to do here
	}

	public void onClassFeature(final JavaFeature jf, final CommentToken docComment)
	throws ParserException
	{
		//System.out.println("onClassFeature("+jf.name+" "+docComment+")");
		if(docComment!=null && jf instanceof JavaField)
			((JavaField)jf).setDocComment(docComment.comment);
	}

	public boolean onDocComment(final CommentToken docComment)
	{
		//System.out.println("onDocComment("+docComment+")");

		return !docComment.isSkipped();
	}

	public void onFileDocComment(final String docComment)
	throws ParserException
	{
		//System.out.println("onFileDocComment("+docComment+")");

		if (class_state != null)
		{
			// handle doccomment immediately
			class_state.setDocComment(docComment);
		}
		else
		{
			// remember to be handled as soon as we know what class we're talking about
			lastFileDocComment = docComment;
		}
	}

}
