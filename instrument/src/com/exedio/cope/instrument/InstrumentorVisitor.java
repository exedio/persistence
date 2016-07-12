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
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.DocSourcePositions;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePathScanner;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Modifier;

final class InstrumentorVisitor extends TreePathScanner<Void, Void>
{
	static final Set<Modifier> REQUIRED_MODIFIERS_FOR_COPE_FEATURE = EnumSet.of(Modifier.FINAL, Modifier.STATIC);

	private final DocSourcePositions sourcePositions;
	private final CompilationUnitTree compilationUnit;
	private final DocTrees docTrees;
	private final JavaFile javaFile;

	private final Deque<JavaClass> javaClassStack=new ArrayDeque<>();

	private byte[] allBytes;

	InstrumentorVisitor(final CompilationUnitTree compilationUnit, final DocTrees docTrees, final JavaFile javaFile)
	{
		this.sourcePositions=docTrees.getSourcePositions();
		this.compilationUnit=compilationUnit;
		this.docTrees=docTrees;
		this.javaFile = javaFile;
	}

	JavaFile getJavaFile()
	{
		return javaFile;
	}

	private void addGeneratedFragment(final int start, final int end)
	{
		javaFile.markFragmentAsGenerated(start, end);
	}

	private byte[] getAllBytes()
	{
		if ( allBytes==null )
		{
			try (final InputStream inputStream=compilationUnit.getSourceFile().openInputStream())
			{
				allBytes=readFully(inputStream);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		return allBytes;
	}

	private byte[] getSourceBytes(final int start, final int end)
	{
		return Arrays.copyOfRange(getAllBytes(), start, end);
	}

	private String getSourceString(final int start, final int end)
	{
		return new String(getSourceBytes(start, end), StandardCharsets.US_ASCII);
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

	@Override
	public Void visitClass(final ClassTree ct, final Void ignore)
	{
		final JavaClass parent=javaClassStack.isEmpty()?null:javaClassStack.getLast();
		final String classExtends=ct.getExtendsClause()==null?null:ct.getExtendsClause().toString();
		final JavaClass javaClass = new JavaClass(javaFile, parent, toModifiersInt(ct.getModifiers()), ct.getKind()==Tree.Kind.ENUM, getSimpleName(ct), classExtends);
		javaClass.setDocComment(getDocComment());
		javaClass.setClassEndPosition( Math.toIntExact(sourcePositions.getEndPosition(compilationUnit, ct))-1 );
		javaClassStack.addLast(javaClass);
		final Void result=super.visitClass(ct, ignore);
		if (javaClassStack.removeLast() != javaClass)
		{
			throw new RuntimeException();
		}
		return result;
	}

	private JavaClass getCurrentJavaClass()
	{
		final JavaClass result=javaClassStack.getLast();
		if (result == null)
		{
			throw new RuntimeException();
		}
		return result;
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
				getCurrentJavaClass(),
				toModifiersInt(node.getModifiers()),
				removeSpacesAfterCommas(node.getType().toString()),
				node.getName().toString(),
				getDocComment(),
				node.getInitializer().toString()
			);
		}
		return null;
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

	@Override
	public Void visitAnnotation(AnnotationTree node, Void p)
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
		final String docComment=getDocComment();
		return docComment!=null && docComment.contains(tag);
	}

	private String getDocComment()
	{
		return docTrees.getDocComment(getCurrentPath());
	}

	private int searchBefore(final int pos, final byte[] search)
	{
		int searchPos=pos-search.length;
		while (true)
		{
			if ( bytesMatch(searchPos, search) )
			{
				return searchPos;
			}
			else
			{
				searchPos--;
			}
		}
	}

	private int searchAfter(final int pos, final byte[] search)
	{
		int searchPos=pos+1;
		while (true)
		{
			if ( bytesMatch(searchPos, search) )
			{
				return searchPos+search.length;
			}
			else
			{
				searchPos++;
			}
		}
	}

	private boolean bytesMatch(final int pos, final byte[] search)
	{
		for (int i=0; i<search.length; i++)
		{
			if ( getAllBytes()[pos+i]!=search[i] )
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public Void visitMethod(final MethodTree mt, final Void ignore)
	{
		final MethodVisitor methodVisitor=new MethodVisitor();
		methodVisitor.visitMethod(mt, null);
		checkGenerated(mt, methodVisitor.currentMethodHasGeneratedAnnotation);
		return null;
	}

	private boolean checkGenerated(final Tree mt, final boolean hasGeneratedAnnotation) throws RuntimeException
	{
		if ( hasGeneratedAnnotation || hasCopeGeneratedJavadocTag() )
		{
			final int start=Math.toIntExact(sourcePositions.getStartPosition(compilationUnit, mt));
			final int end=Math.toIntExact(sourcePositions.getEndPosition(compilationUnit, mt));
			if ( start<0 || end<0 ) throw new RuntimeException();
			final DocCommentTree docCommentTree=docTrees.getDocCommentTree(getCurrentPath());
			if ( docCommentTree==null )
			{
				addGeneratedFragment(start, end);
			}
			else
			{
				final int docStart=searchBefore( Math.toIntExact(sourcePositions.getStartPosition(compilationUnit, docCommentTree, docCommentTree)), "/**".getBytes(StandardCharsets.US_ASCII) );
				final int docEnd=searchAfter( Math.toIntExact(sourcePositions.getEndPosition(compilationUnit, docCommentTree, docCommentTree)), "*/".getBytes(StandardCharsets.US_ASCII) );
				if ( docEnd>=start ) throw new RuntimeException();
				final String commentSource=getSourceString(docStart, docEnd);
				final String inBetween=getSourceString(docEnd+1, start-1);
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

	private boolean allWhitespace(final String s)
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
		if ( !node.isStatic() )
		{
			throw new RuntimeException();
		}
		return null;
	}

	private static byte[] readFully(final InputStream fis) throws IOException
	{
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		int b;
		while ( (b=fis.read())!=-1 )
		{
			baos.write(b);
		}
		return baos.toByteArray();
	}

	private int toModifiersInt(final ModifiersTree modifiers)
	{
		int result=0;
		for (final Modifier flag: modifiers.getFlags())
		{
			result |= toModifiersInt(flag);
		}
		return result;
	}

	private int toModifiersInt(final Modifier flag)
	{
		switch (flag)
		{
			case ABSTRACT: return java.lang.reflect.Modifier.ABSTRACT;
			case DEFAULT: throw new RuntimeException("unexpected DEFAULT modifier");
			case FINAL: return java.lang.reflect.Modifier.FINAL;
			case NATIVE: return java.lang.reflect.Modifier.NATIVE;
			case PRIVATE: return java.lang.reflect.Modifier.PRIVATE;
			case PROTECTED: return java.lang.reflect.Modifier.PROTECTED;
			case PUBLIC: return java.lang.reflect.Modifier.PUBLIC;
			case STATIC: return java.lang.reflect.Modifier.STATIC;
			case STRICTFP: return java.lang.reflect.Modifier.STRICT;
			case SYNCHRONIZED: return java.lang.reflect.Modifier.SYNCHRONIZED;
			case TRANSIENT: return java.lang.reflect.Modifier.TRANSIENT;
			case VOLATILE: return java.lang.reflect.Modifier.VOLATILE;
			default: throw new RuntimeException(flag.toString());
		}
	}
}
