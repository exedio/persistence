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
