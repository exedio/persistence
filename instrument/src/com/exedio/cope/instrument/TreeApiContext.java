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
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.DocSourcePositions;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

final class TreeApiContext
{
	private final DocTrees docTrees;
	final Messager messager;
	final JavaFile javaFile;
	private final CompilationUnitTree compilationUnit;
	private final DocSourcePositions sourcePositions;

	private byte[] allBytes;

	TreeApiContext(final ProcessingEnvironment processingEnv, final JavaFile javaFile, final CompilationUnitTree compilationUnit)
	{
		this.docTrees=DocTrees.instance(processingEnv);
		this.messager=processingEnv.getMessager();
		this.javaFile=javaFile;
		this.compilationUnit=compilationUnit;
		this.sourcePositions=docTrees.getSourcePositions();
	}

	String getFileName()
	{
		return compilationUnit.getSourceFile().getName();
	}

	void markFragmentAsGenerated(final int start, final int end)
	{
		javaFile.markFragmentAsGenerated(start, end);
	}

	long getStartPosition(final Tree mt)
	{
		return sourcePositions.getStartPosition(compilationUnit, mt);
	}

	long getEndPosition(final Tree mt)
	{
		return sourcePositions.getEndPosition(compilationUnit, mt);
	}

	DocCommentTree getDocCommentTree(final TreePath currentPath)
	{
		return docTrees.getDocCommentTree(currentPath);
	}

	long getStartPosition(final DocCommentTree docCommentTree)
	{
		return sourcePositions.getStartPosition(compilationUnit, docCommentTree, docCommentTree);
	}

	long getEndPosition(final DocCommentTree docCommentTree)
	{
		return sourcePositions.getEndPosition(compilationUnit, docCommentTree, docCommentTree);
	}

	Element getElement(final TreePath tp)
	{
		return docTrees.getElement(tp);
	}

	Element getElementForTree(final Tree tree)
	{
		final TreePath path = docTrees.getPath(compilationUnit, tree);
		if (path==null)
			throw new IllegalArgumentException("can't find '"+tree+" ("+tree.getKind()+") in "+compilationUnit);
		final Element element = docTrees.getElement(path);
		if (element==null)
			throw new NullPointerException("can't find element for '"+tree+"' ("+tree.getKind()+")");
		return element;
	}

	private byte[] getAllBytes()
	{
		if ( allBytes==null )
		{
			try (final InputStream inputStream=compilationUnit.getSourceFile().openInputStream())
			{
				allBytes=inputStream.readAllBytes();
			}
			catch (final IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		return allBytes;
	}

	String getSourceString(final int start, final int end)
	{
		return new String(getAllBytes(), start, end-start, StandardCharsets.US_ASCII);
	}

	/** @return -1 if not found */
	int searchBefore(final int pos, final byte[] search)
	{
		int searchPos=pos-search.length;
		while (true)
		{
			if ( searchPos<0 )
				return -1;
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

	/** @return -1 if not found */
	int searchAfter(final int pos, final byte[] search)
	{
		int searchPos=pos+1;
		final byte[] allBytes=getAllBytes();
		while (searchPos+search.length<=allBytes.length)
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
		return -1;
	}

	private boolean bytesMatch(final int pos, final byte[] search)
	{
		if (pos<0) throw new ArrayIndexOutOfBoundsException(pos);
		final byte[] allBytes=getAllBytes();
		if (pos+search.length>allBytes.length)
			throw new IllegalArgumentException(pos+"+"+search.length+">"+allBytes.length);
		for (int i=0; i<search.length; i++)
		{
			if ( allBytes[pos+i]!=search[i] )
			{
				return false;
			}
		}
		return true;
	}

	String getFullyQualifiedSuperclass(final TreePath typePath)
	{
		final Element element=getElement(typePath);
		if(!element.getKind().isClass())
			return null;
		return ((DeclaredType)((TypeElement)element).getSuperclass()).asElement().toString();
	}
}
