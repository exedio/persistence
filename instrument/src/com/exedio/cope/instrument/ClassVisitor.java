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

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

class ClassVisitor extends GeneratedAwareScanner
{
	private static final Set<Modifier> REQUIRED_MODIFIERS_FOR_COPE_FEATURE = EnumSet.of(Modifier.FINAL, Modifier.STATIC);
	private final byte[] LINE_SEPARATOR_BYTES=System.lineSeparator().getBytes(StandardCharsets.US_ASCII);

	private final JavaClass outerClass;

	private JavaClass javaClass;
	private int previousGeneratedFragmentEnd = Integer.MAX_VALUE;

	/** @param outer may be null (for non-inner classes) */
	ClassVisitor(final TreeApiContext context, final JavaClass outer)
	{
		super(context);
		this.outerClass=outer;
	}

	@Override
	public Void visitClass(final ClassTree ct, final Void ignore)
	{
		if (getAnnotation(WrapperIgnore.class)!=null)
		{
			new WarnForGeneratedVisitor(context).scan(getCurrentPath(), ignore);
			return null;
		}
		else if (javaClass==null)
		{
			final String classExtends=context.getFullyQualifiedSuperclass(getCurrentPath());
			previousGeneratedFragmentEnd = 0;
			javaClass = new JavaClass(
				context.javaFile,
				outerClass,
				TreeApiHelper.toModifiersInt(ct.getModifiers()),
				getSimpleName(ct),
				context.getDocComment(getCurrentPath()),
				context.getSourcePosition(ct),
				ct.getKind()==Tree.Kind.ENUM,
				Kind.valueOf(getAnnotation(WrapType.class)),
				classExtends,
				getWrapperType(),
				findClassEndPosition(ct)
			);
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

	private int findClassEndPosition(final ClassTree ct)
	{
		final int positionOfClosingBrace=Math.toIntExact(context.getEndPosition(ct));
		if (context.extendGeneratedFragmentsToLineBreaks)
		{
			return includeLeadingWhitespaceLine(positionOfClosingBrace-1, false);
		}
		else
		{
			return positionOfClosingBrace-1;
		}
	}

	@Override
	public Void visitVariable(final VariableTree node, final Void p)
	{
		super.visitVariable(node, p);
		if ( !hasGeneratedAnnotation() && node.getModifiers().getFlags().containsAll(REQUIRED_MODIFIERS_FOR_COPE_FEATURE) )
		{
			new JavaField(
				javaClass,
				TreeApiHelper.toModifiersInt(node.getModifiers()),
				removeSpacesAfterCommas(node.getType().toString()),
				node.getName().toString(),
				context.getDocComment(getCurrentPath()),
				context.getSourcePosition(node),
				node.getInitializer()==null?null:node.getInitializer().toString(),
				getAnnotation(WrapperInitial.class),
				getAnnotation(WrapperIgnore.class),
				Arrays.asList(getAnnotations(Wrapper.class))
			);
		}
		return null;
	}

	private void addGeneratedFragment(final int start, final int end)
	{
		if (start<previousGeneratedFragmentEnd) throw new RuntimeException(""+start+"<"+previousGeneratedFragmentEnd);
		final int realStart;
		final int realEnd;
		if (context.extendGeneratedFragmentsToLineBreaks)
		{
			realStart=Math.max(previousGeneratedFragmentEnd, includeLeadingWhitespaceLine(start, true));
			final int lineEnd=context.searchAfter(end-1, LINE_SEPARATOR_BYTES);
			if (lineEnd==-1)
			{
				realEnd=end;
			}
			else
			{
				final String lineAfterEnd=context.getSourceString(end, lineEnd);
					if (allWhitespace(lineAfterEnd))
						realEnd=lineEnd;
					else
						realEnd=end;
			}
		}
		else
		{
			realStart=start;
			realEnd=end;
		}
		context.markFragmentAsGenerated(realStart, realEnd);
		previousGeneratedFragmentEnd = realEnd;
	}

	/** if the line before 'pos' is all whitespace, return the position of the start position of that line
	 * (if 'posAfterLineSep': index of the line separator; otherwise index after line separator);
	 * otherwise return 'pos'
	 */
	private int includeLeadingWhitespaceLine(final int pos, final boolean posOfLineSep)
	{
		final int lineStart=context.searchBefore(pos, LINE_SEPARATOR_BYTES);
		if (lineStart==-1)
		{
			return pos;
		}
		else
		{
			final String lineBeforeStart=context.getSourceString(lineStart, pos);
			if (allWhitespace(lineBeforeStart))
				return lineStart+(posOfLineSep?0:LINE_SEPARATOR_BYTES.length);
			else
				return pos;
		}
	}

	@Override
	void visitGeneratedPath()
	{
		final Tree mt=getCurrentPath().getLeaf();
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
			final int docStart;
			final int docEnd;
			if ( docCommentTree.getFirstSentence().isEmpty() && docCommentTree.getBody().isEmpty() && docCommentTree.getBlockTags().isEmpty() )
			{
				// getStartPosition doesn't work for empty comments - search from commented element instead:
				docStart=context.searchBefore( Math.toIntExact(context.getStartPosition(mt)), "/**".getBytes(StandardCharsets.US_ASCII) );
				docEnd=context.searchAfter( Math.toIntExact(docStart), "*/".getBytes(StandardCharsets.US_ASCII) );
			}
			else
			{
				docStart=context.searchBefore( Math.toIntExact(context.getStartPosition(docCommentTree)), "/**".getBytes(StandardCharsets.US_ASCII) );
				docEnd=context.searchAfter( Math.toIntExact(context.getEndPosition(docCommentTree)), "*/".getBytes(StandardCharsets.US_ASCII) );
			}
			if ( docEnd>=start ) throw new RuntimeException();
			final String commentSource=context.getSourceString(docStart, docEnd);
			final String inBetween=context.getSourceString(docEnd+1, start-1);
			if ( !commentSource.startsWith("/**") ) throw new RuntimeException();
			if ( !commentSource.endsWith("*/") ) throw new RuntimeException();
			if ( !allWhitespace(inBetween) ) throw new RuntimeException(">"+inBetween+"<");
			addGeneratedFragment(docStart, end);
		}
	}

	private <T extends Annotation> T[] getAnnotations(final Class<T> annotationType)
	{
		final Element element=context.getElement(getCurrentPath());
		return element.getAnnotationsByType(annotationType);
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
			simpleName += "<"+ct.getTypeParameters()+">";
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
	public Void visitBlock(final BlockTree node, final Void p)
	{
		// methods don't get visited - but still this method is called for
		// static and non-static initializers
		return null;
	}

	private WrapperType getWrapperType()
	{
		final Element element=context.getElement(getCurrentPath());
		return element.getAnnotation(WrapperType.class);
	}
}
