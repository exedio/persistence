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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

class InstrumentorVisitor extends TreePathScanner<Void, Void>
{

	private final DocSourcePositions sourcePositions;
	private final CompilationUnitTree compilationUnit;
	private final DocTrees docTrees;
	private final JavaFile javaFile;

	private final Deque<JavaClass> javaClassStack=new ArrayDeque<>();

	private byte[] allBytes;

	private final List<GeneratedFragment> generatedFragments = new ArrayList<>();

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

	private void addGeneratedFragment(int start, int end)
	{
		generatedFragments.add( new GeneratedFragment(start, end) );
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

	private byte[] getSourceBytes(int start, int end)
	{
		return Arrays.copyOfRange(getAllBytes(), start, end);
	}

	private String getSourceString(int start, int end)
	{
		return new String(getSourceBytes(start, end), StandardCharsets.US_ASCII);
	}

	private static String getSimpleName(ClassTree ct)
	{
		String simpleName=ct.getSimpleName().toString();
		if ( !ct.getTypeParameters().isEmpty() )
		{
			simpleName += "<"+ct.getTypeParameters().toString()+">";
		}
		return simpleName;
	}

	@Override
	public Void visitClass(ClassTree ct, Void ignore)
	{
		JavaClass parent = javaClassStack.isEmpty() ? null : javaClassStack.getLast();
		final String classExtends=ct.getExtendsClause()==null?null:ct.getExtendsClause().toString();
		JavaClass javaClass = new JavaClass(javaFile, parent, toModifiersInt(ct.getModifiers()), ct.getKind()==Tree.Kind.ENUM, getSimpleName(ct), classExtends);
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
	public Void visitVariable(VariableTree node, Void p)
	{
		if ( hasCopeIgnoreJavadocTag() )
		{
			return null;
		}
		final Set<Modifier> required = new HashSet<>();
		required.add(Modifier.FINAL);
		required.add(Modifier.STATIC);
		final VariableVisitor variableVisitor=new VariableVisitor();
		variableVisitor.visitVariable(node, null);
		final boolean generated = checkGenerated(node, variableVisitor.currentVariableHasGeneratedAnnotation);
		if ( !generated && node.getModifiers().getFlags().containsAll(required) )
		{
			final JavaField javaField = new JavaField(getCurrentJavaClass(), toModifiersInt(node.getModifiers()), removeSpacesAfterCommas(node.getType().toString()), node.getName().toString());
			javaField.setDocComment(getDocComment());
			//TODO COPE-10:
			for(char c: node.getInitializer().toString().toCharArray())
			{
				javaField.addToInitializer(c);
			}
		}
		return null;
	}

	private static String removeSpacesAfterCommas(String s)
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

	private boolean hasJavadocTag(String tag)
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
		final MethodVisitor methodVisitor=new MethodVisitor();
		methodVisitor.visitMethod(mt, null);
		checkGenerated(mt, methodVisitor.currentMethodHasGeneratedAnnotation);
		return null;
	}

	private boolean  checkGenerated(final Tree mt, final boolean hasGeneratedAnnotation) throws RuntimeException
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
				final String inBetween = getSourceString(docEnd+1, start-1);
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
		if ( !node.isStatic() )
		{
			throw new RuntimeException();
		}
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

	void removeAllGeneratedFragments()
	{
		if ( generatedFragments.isEmpty() )
		{
			return;
		}
		final JavaFileObject sourceFile=compilationUnit.getSourceFile();
		System.out.println(sourceFile);
		int start = 0;
		try (final OutputStream os = sourceFile.openOutputStream())
		{
			int end;
			final byte[] allBytes=getAllBytes();
			for (final InstrumentorVisitor.GeneratedFragment generatedFragment: generatedFragments)
			{
				end = generatedFragment.fromInclusive;
				os.write(allBytes, start, end-start);
				start = generatedFragment.endExclusive;
			}
			os.write(allBytes, start, allBytes.length-start);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}

	private int toModifiersInt(ModifiersTree modifiers)
	{
		int result = 0;
		for (Modifier flag: modifiers.getFlags())
		{
			result |= toModifiersInt(flag);
		}
		return result;
	}

	private int toModifiersInt(Modifier flag)
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
