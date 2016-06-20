package com.exedio.cope.instrument;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.DocSourcePositions;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePathScanner;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;

class InstrumentorVisitor extends TreePathScanner<Void, Void>
{

	private final DocSourcePositions sourcePositions;
	private final CompilationUnitTree compilationUnit;
	private final DocTrees docTrees;

	private final Deque<ClassTree> classStack=new ArrayDeque<>();
	private MethodTree method;
	private boolean currentMethodHasGeneratedAnnotation = false;

	private byte[] allBytes;

	final List<GeneratedFragment> generatedFragments = new ArrayList<>();

	InstrumentorVisitor(CompilationUnitTree compilationUnit, DocTrees docTrees)
	{
		this.sourcePositions=docTrees.getSourcePositions();
		this.compilationUnit=compilationUnit;
		this.docTrees=docTrees;
	}

	private void addGeneratedFragment(int start, int end)
	{
		generatedFragments.add( new GeneratedFragment(start, end) );
	}

	private void addPotentialFeature(ClassTree clazz, VariableTree variable, ExpressionTree initializer)
	{
		System.out.println("potential feature "+clazz.getSimpleName()+" "+variable.getName()+": "+initializer);
	}

	private byte[] getAllBytes()
	{
		if ( allBytes==null )
		{
			try
			{
				allBytes=readFully(compilationUnit.getSourceFile().openInputStream());
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		return allBytes;
	}

	private byte[] getSourceBytes(int start, int end)
	{
		return Arrays.copyOfRange(getAllBytes(), start, end);
	}

	private String getSourceString(int start, int end)
	{
		return new String(getSourceBytes(start, end), StandardCharsets.US_ASCII);
	}

	@Override
	public Void visitClass(ClassTree ct, Void ignore)
	{
		classStack.addLast(ct);
		final Void result=super.visitClass(ct, ignore);
		if (classStack.removeLast() != ct)
		{
			throw new RuntimeException();
		}
		return result;
	}

	private ClassTree getCurrentClass()
	{
		final ClassTree result=classStack.getLast();
		if (result == null)
		{
			throw new RuntimeException();
		}
		return result;
	}

	@Override
	public Void visitVariable(VariableTree node, Void p)
	{
		Set<Modifier> required = new HashSet<>();
		required.add(Modifier.FINAL);
		required.add(Modifier.STATIC);
		if ( node.getModifiers().getFlags().containsAll(required) )
		{
			addPotentialFeature(getCurrentClass(), node, node.getInitializer());
		}
		return super.visitVariable(node, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Void visitAnnotation(AnnotationTree node, Void p)
	{
		if ( method!=null && node.getAnnotationType().toString().contains("javax.annotation.Generated")
			&& node.getArguments().size()==1
			&& node.getArguments().get(0).toString().equals("value = \"com.exedio.cope.instrument\"")
			)
		{
			currentMethodHasGeneratedAnnotation = true;
		}
		return super.visitAnnotation(node, p);
	}

	private boolean hasCopeGeneratedJavadocTag()
	{
		final String docComment=docTrees.getDocComment(getCurrentPath());
		return docComment!=null && docComment.contains("@cope.generated");
	}

	private int searchBefore(final int pos, final byte[] search)
	{
		int searchPos = pos-search.length;
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
		int searchPos = pos+1;
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
		for (int i=0; i < search.length; i++)
		{
			if ( getAllBytes()[pos+i]!=search[i] )
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public Void visitMethod(MethodTree mt, Void ignore)
	{
		if ( method!=null ) throw new RuntimeException();
		method = mt;
		final Void result=super.visitMethod(mt, ignore); // collects annotation info
		if ( currentMethodHasGeneratedAnnotation || hasCopeGeneratedJavadocTag() )
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
				final String inBetween = getSourceString(docEnd+1, start-1);
				if ( !commentSource.startsWith("/**") ) throw new RuntimeException();
				if ( !commentSource.endsWith("*/") ) throw new RuntimeException();
				if ( !allWhitespace(inBetween) ) throw new RuntimeException(">"+inBetween+"<");
				addGeneratedFragment(docStart, end);
			}
			currentMethodHasGeneratedAnnotation = false;
		}
		method = null;
		return result;
	}

	private boolean allWhitespace(String s)
	{
		for (char c: s.toCharArray())
		{
			if ( !Character.isWhitespace(c) )
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public Void visitBlock(BlockTree node, Void p)
	{
		// nobody cares -> don't traverse
		return null;
	}

	/** here is some great javadoc {@link InstrumentorProcessor}
	 second line */
	@SuppressWarnings("x")
	private static byte[] readFully(InputStream fis) throws IOException
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		while ( (b=fis.read())!=-1 )
		\u007B
			baos.write(b);
		}
		return baos.toByteArray();
	}

	static class GeneratedFragment
	{
		final int fromInclusive;
		final int endExclusive;

		GeneratedFragment(int fromInclusive, int endExclusive)
		{
			this.fromInclusive=fromInclusive;
			this.endExclusive=endExclusive;
		}

		@Override
		public String toString()
		{
			return String.format("%s-%s", fromInclusive, endExclusive);
		}
	}
}
