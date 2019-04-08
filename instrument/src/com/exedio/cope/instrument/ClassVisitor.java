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
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WildcardTree;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

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
	Void visitClassInternal(final ClassTree ct, final Void ignore)
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
				ct.getSimpleName().toString(),
				ct.getTypeParameters().size(),
				context.getSourcePosition(ct),
				Kind.valueOf(getAnnotation(WrapType.class)),
				classExtends,
				getWrapperType(),
				findClassEndPosition(ct)
			);
			return super.visitClassInternal(ct, ignore);
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
		return includeLeadingWhitespaceLine(positionOfClosingBrace-1, false);
	}

	private String getFullyQualifiedName(final Tree typeTree)
	{
		//noinspection EnumSwitchStatementWhichMissesCases
		switch (typeTree.getKind())
		{
			case PRIMITIVE_TYPE:
				return typeTree.toString();
			case ARRAY_TYPE:
				return getFullyQualifiedName(((ArrayTypeTree)typeTree).getType())+"[]";
			case IDENTIFIER:
			case PARAMETERIZED_TYPE:
			case MEMBER_SELECT:
				//noinspection RedundantCast: make sure this is a TypeElement
				return ((TypeElement)context.getElementForTree(typeTree)).toString();
			default:
				throw new RuntimeException("unhandled kind "+typeTree.getKind()+" for '"+typeTree+"'");
		}
	}

	@Override
	public Void visitVariable(final VariableTree node, final Void p)
	{
		super.visitVariable(node, p);
		if(javaClass.kind==null)
			// we only care about items, composites and blocks
			return null;
		if(hasGeneratedAnnotation())
			// we don't look at elements we have generated ourselves
			return null;
		final Tree.Kind kindOfNode = node.getType().getKind();
		//noinspection EnumSwitchStatementWhichMissesCases
		switch(kindOfNode)
		{
			case IDENTIFIER:
			case PARAMETERIZED_TYPE:
			case MEMBER_SELECT:
				// expected, handled
				break;
			case PRIMITIVE_TYPE:
			case ARRAY_TYPE:
				// expected, not handled
				return null;
			default:
				// unexpected
				throw new RuntimeException("unexpected kind of node "+kindOfNode+" for "+node);
		}
		if(context.getElementForTree(node.getType()).getAnnotation(WrapFeature.class)==null)
			return null;
		if( !node.getModifiers().getFlags().containsAll(REQUIRED_MODIFIERS_FOR_COPE_FEATURE) )
		{
			printWarning(CopeWarnings.FEATURE_NOT_STATIC_FINAL, "non-static/final feature");
			return null;
		}
		final String variableType = getFullyQualifiedName(node.getType());
		final VariableElement fieldElement = (VariableElement)context.getElementForTree(node);
		final JavaField javaField = new JavaField(
			javaClass,
			TreeApiHelper.toModifiersInt(node.getModifiers()),
			context.getElementForTree(node).getAnnotation(Deprecated.class)!=null,
			fieldElement.asType(),
			variableType,
			node.getName().toString(),
			context.getSourcePosition(node),
			node.getInitializer()==null?null:node.getInitializer().toString(),
			getAnnotation(WrapperInitial.class),
			getAnnotation(WrapperIgnore.class),
			Arrays.asList(getAnnotations(Wrapper.class))
		);
		registerTypeShortcuts(javaField, node.getType());
		return null;
	}

	private void registerTypeShortcuts(final JavaField javaField, final Tree typeTree)
	{
		//noinspection EnumSwitchStatementWhichMissesCases
		switch (typeTree.getKind())
		{
			case PRIMITIVE_TYPE:
			case UNBOUNDED_WILDCARD:
				break;
			case IDENTIFIER:
			case MEMBER_SELECT:
				final TypeElement identifierElement = (TypeElement)context.getElementForTree(typeTree);
				javaField.addTypeShortcut(identifierElement.getQualifiedName().toString(), typeTree.toString());
				break;
			case PARAMETERIZED_TYPE:
				registerTypeShortcuts(javaField, ((ParameterizedTypeTree)typeTree).getType());
				for (final Tree typeArgument : ((ParameterizedTypeTree)typeTree).getTypeArguments())
				{
					registerTypeShortcuts(javaField, typeArgument);
				}
				break;
			case ARRAY_TYPE:
				registerTypeShortcuts(javaField, ((ArrayTypeTree)typeTree).getType());
				break;
			case EXTENDS_WILDCARD:
				registerTypeShortcuts(javaField, ((WildcardTree)typeTree).getBound());
				break;
			default:
				throw new RuntimeException(typeTree+" - "+typeTree.getKind());
		}

	}

	private void addGeneratedFragment(final int start, final int end)
	{
		if (start<previousGeneratedFragmentEnd) throw new RuntimeException(""+start+"<"+previousGeneratedFragmentEnd);
		final int realStart=Math.max(previousGeneratedFragmentEnd, includeLeadingWhitespaceLine(start, true));
		final int lineEnd=context.searchAfter(end-1, LINE_SEPARATOR_BYTES);
		final int realEnd;
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
