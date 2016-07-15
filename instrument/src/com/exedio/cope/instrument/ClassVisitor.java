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

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Modifier;

class ClassVisitor extends TreePathScanner<Void,Void>
{
	private static final Set<Modifier> REQUIRED_MODIFIERS_FOR_COPE_FEATURE = EnumSet.of(Modifier.FINAL, Modifier.STATIC);

	private final JavaClass outerClass;
	private final TreeApiContext context;

	private JavaClass javaClass;

	/** @param outer may be null (for non-inner classes) */
	ClassVisitor(final TreeApiContext context, final JavaClass outer)
	{
		this.context=context;
		this.outerClass=outer;
	}

	@Override
	public Void visitClass(final ClassTree ct, final Void ignore)
	{
		if (javaClass==null)
		{
			final String classExtends=ct.getExtendsClause()==null?null:ct.getExtendsClause().toString();
			javaClass = new JavaClass(context.javaFile, outerClass, TreeApiHelper.toModifiersInt(ct.getModifiers()), ct.getKind()==Tree.Kind.ENUM, getSimpleName(ct), classExtends);
			javaClass.setDocComment(context.getDocComment(getCurrentPath()));
			javaClass.setClassEndPosition( Math.toIntExact(context.getEndPosition(ct))-1 );
			return super.visitClass(ct, ignore);
		}
		else
		{
			// we found an inner class -> delegate to new ClassVisitor
			final ClassVisitor classVisitor = new ClassVisitor(context, javaClass);
			classVisitor.scan(getCurrentPath(), ignore);
			return null;
		}
	}

	@Override
	public Void visitVariable(final VariableTree node, final Void p)
	{
		if ( hasCopeIgnoreJavadocTag() )
		{
			return null;
		}
		final VariableVisitor variableVisitor=new VariableVisitor();
		variableVisitor.visitVariable(node, null);
		final boolean generated=checkGenerated(node, variableVisitor.currentVariableHasGeneratedAnnotation);
		if ( !generated && node.getModifiers().getFlags().containsAll(REQUIRED_MODIFIERS_FOR_COPE_FEATURE) )
		{
			new JavaField(
				javaClass,
				TreeApiHelper.toModifiersInt(node.getModifiers()),
				removeSpacesAfterCommas(node.getType().toString()),
				node.getName().toString(),
				context.getDocComment(getCurrentPath()),
				node.getInitializer().toString()
			);
		}
		return null;
	}

	@Override
	public Void visitMethod(final MethodTree mt, final Void ignore)
	{
		final MethodVisitor methodVisitor=new MethodVisitor();
		methodVisitor.visitMethod(mt, null);
		checkGenerated(mt, methodVisitor.currentMethodHasGeneratedAnnotation);
		return null;
	}

	private void addGeneratedFragment(final int start, final int end)
	{
		context.markFragmentAsGenerated(start, end);
	}

	private boolean checkGenerated(final Tree mt, final boolean hasGeneratedAnnotation) throws RuntimeException
	{
		if ( hasGeneratedAnnotation || hasCopeGeneratedJavadocTag() )
		{
			final int start=Math.toIntExact(context.getStartPosition(mt));
			final int end=Math.toIntExact(context.getEndPosition(mt));
			if ( start<0 || end<0 ) throw new RuntimeException();
			final DocCommentTree docCommentTree=context.getDocCommentTree(getCurrentPath());
			if ( docCommentTree==null )
			{
				addGeneratedFragment(start, end);
			}
			else
			{
				final int docStart=context.searchBefore( Math.toIntExact(context.getStartPosition(docCommentTree, docCommentTree)), "/**".getBytes(StandardCharsets.US_ASCII) );
				final int docEnd=context.searchAfter( Math.toIntExact(context.getEndPosition(docCommentTree, docCommentTree)), "*/".getBytes(StandardCharsets.US_ASCII) );
				if ( docEnd>=start ) throw new RuntimeException();
				final String commentSource=context.getSourceString(docStart, docEnd);
				final String inBetween=context.getSourceString(docEnd+1, start-1);
				if ( !commentSource.startsWith("/**") ) throw new RuntimeException();
				if ( !commentSource.endsWith("*/") ) throw new RuntimeException();
				if ( !allWhitespace(inBetween) ) throw new RuntimeException(">"+inBetween+"<");
				addGeneratedFragment(docStart, end);
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	private boolean hasCopeGeneratedJavadocTag()
	{
		return hasJavadocTag("@"+CopeFeature.TAG_PREFIX+"generated");
	}

	private boolean hasCopeIgnoreJavadocTag()
	{
		return hasJavadocTag("@"+CopeFeature.TAG_PREFIX+"ignore");
	}

	private boolean hasJavadocTag(final String tag)
	{
		final String docComment=context.getDocComment(getCurrentPath());
		return docComment!=null && docComment.contains(tag);
	}

	private static String removeSpacesAfterCommas(final String s)
	{
		final StringBuilder result = new StringBuilder(s.length());
		boolean foundComma = false;
		for (int i=0; i < s.length(); i++)
		{
			final char c = s.charAt(i);
			if ( foundComma )
			{
				if ( c!=' ' )
				{
					foundComma = false;
					result.append(c);
				}
			}
			else
			{
				result.append(c);
				if ( c==',' )
				{
					foundComma = true;
				}
			}
		}
		return result.toString();
	}
	private static String getSimpleName(final ClassTree ct)
	{
		String simpleName=ct.getSimpleName().toString();
		if ( !ct.getTypeParameters().isEmpty() )
		{
			simpleName += "<"+ct.getTypeParameters().toString()+">";
		}
		return simpleName;
	}

	private static boolean allWhitespace(final String s)
	{
		for (final char c: s.toCharArray())
		{
			if ( !Character.isWhitespace(c) )
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public Void visitAnnotation(final AnnotationTree node, final Void p)
	{
		if ( node.getAnnotationType().toString().contains("javax.annotation.Generated")
			&& node.getArguments().size()==1
			&& node.getArguments().get(0).toString().equals("value = \"com.exedio.cope.instrument\"")
			)
		{
			throw new RuntimeException("'Generated' but not a method or variable");
		}
		return super.visitAnnotation(node, p);
	}

	@Override
	public Void visitBlock(final BlockTree node, final Void p)
	{
		if ( !node.isStatic() )
		{
			throw new RuntimeException("unexpected - visiting methods is delegated");
		}
		return null;
	}
}
