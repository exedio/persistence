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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

final class TreeApiContext
{
	private final Params.ConfigurationByJavadocTags javadocTagHandling;
	private final DocTrees docTrees;
	private final Messager messager;
	final JavaFile javaFile;
	private final CompilationUnitTree compilationUnit;
	private final DocSourcePositions sourcePositions;

	private byte[] allBytes;
	boolean foundJavadocControlTags=false;

	TreeApiContext(final Params.ConfigurationByJavadocTags javadocTagHandling, final ProcessingEnvironment processingEnv, final JavaFile javaFile, final CompilationUnitTree compilationUnit)
	{
		this.javadocTagHandling=javadocTagHandling;
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

	String getDocComment(final TreePath path)
	{
		if (javadocTagHandling==Params.ConfigurationByJavadocTags.ignore)
		{
			return null;
		}
		final String docComment=docTrees.getDocComment(path);
		if (javadocTagHandling==Params.ConfigurationByJavadocTags.warn||javadocTagHandling==Params.ConfigurationByJavadocTags.error)
		{
			if (docComment!=null && docComment.contains('@'+CopeFeature.TAG_PREFIX))
			{
				final Diagnostic.Kind messageKind;
				if (javadocTagHandling==Params.ConfigurationByJavadocTags.warn)
				{
					messageKind=Diagnostic.Kind.WARNING;
				}
				else
				{
					messageKind=Diagnostic.Kind.ERROR;
				}
				messager.printMessage(messageKind, "use of javadoc tags to control instrumentor is deprecated", getElement(path));
				foundJavadocControlTags=true;
			}
		}
		return docComment;
	}

	long getStartPosition(final Tree mt)
	{
		return sourcePositions.getStartPosition(compilationUnit, mt);
	}

	long getEndPosition(final Tree mt)
	{
		return sourcePositions.getEndPosition(compilationUnit, mt);
	}

	String getSourcePosition(final Tree t)
	{
		final long bytePos=getStartPosition(t);
		final long lineNumber=compilationUnit.getLineMap().getLineNumber(bytePos);
		return getFileName()+":"+lineNumber;
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

	byte[] getAllBytes()
	{
		if ( allBytes==null )
		{
			try (final InputStream inputStream=compilationUnit.getSourceFile().openInputStream())
			{
				allBytes=readFully(inputStream);
			}
			catch (final IOException e)
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

	String getSourceString(final int start, final int end)
	{
		return new String(getSourceBytes(start, end), StandardCharsets.US_ASCII);
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

	int searchBefore(final int pos, final byte[] search)
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

	int searchAfter(final int pos, final byte[] search)
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

}
