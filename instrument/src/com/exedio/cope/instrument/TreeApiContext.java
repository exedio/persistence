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
import javax.lang.model.element.Element;

class TreeApiContext
{
	private final DocTrees docTrees;
	final JavaFile javaFile;
	private final CompilationUnitTree compilationUnit;
	private final DocSourcePositions sourcePositions;

	private byte[] allBytes;

	TreeApiContext(final DocTrees docTrees, final JavaFile javaFile, final CompilationUnitTree compilationUnit)
	{
		this.docTrees=docTrees;
		this.javaFile=javaFile;
		this.compilationUnit=compilationUnit;
		this.sourcePositions=docTrees.getSourcePositions();
	}

	void markFragmentAsGenerated(final int start, final int end)
	{
		javaFile.markFragmentAsGenerated(start, end);
	}

	String getDocComment(final TreePath path)
	{
		return docTrees.getDocComment(path);
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

	private byte[] getAllBytes()
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
