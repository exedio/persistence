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
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

@SuppressWarnings("synthetic-access")
final class ConvertTagsToAnnotations
{
	static void convert(final Params params, final ArrayList<File> classpathFiles) throws IOException, HumanReadableException
	{
		new JavacRunner<ConvertProcessor>()
		{
			@Override
			@SuppressWarnings("synthetic-access")
			ConvertProcessor createProcessor(final StandardJavaFileManager fileManager)
			{
				return new ConvertProcessor(System.lineSeparator());
			}

			@Override
			void validateProcessor(final ConvertProcessor processor)
			{
				// empty
			}
		}.run(params, classpathFiles);
	}

	@SupportedSourceVersion(SourceVersion.RELEASE_8)
	@SupportedAnnotationTypes("*")
	static final class ConvertProcessor extends AbstractProcessor
	{
		private final byte[] lineSeparatorBytes;

		ConvertProcessor(final String lineSeparator)
		{
			this.lineSeparatorBytes=lineSeparator.getBytes(StandardCharsets.US_ASCII);
		}

		@Override
		public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
		{
			final DocTrees docTrees=DocTrees.instance(processingEnv);
			for (final Element e: roundEnv.getRootElements())
			{
				final TreePath tp = docTrees.getPath(e);
				new ConvertScanner(docTrees).scan(tp, null);
			}
			return true;
		}

		private class ConvertScanner extends TreePathScanner<Void, Void>
		{
			private final DocTrees docTrees;
			final ByteReplacements replacements=new ByteReplacements();
			final Set<String> requiredAnnotationClassImports=new HashSet<>();
			final Set<String> requiredVisibilityEnumImport=new HashSet<>();

			TreeApiContext ctx;
			String startMessage=null;
			int previousDocCommentStart=Integer.MIN_VALUE;
			int previousDocCommentEnd=Integer.MIN_VALUE;

			ConvertScanner(final DocTrees docTrees)
			{
				this.docTrees=docTrees;
			}

			private void requireImport(final Annotation a)
			{
				requiredAnnotationClassImports.add(a.annotationType().getName());
			}

			private void requireImport(final Visibility v)
			{
				if (v!=null)
				{
					requiredVisibilityEnumImport.add(v.getClass().getName()+"."+v.name());
				}
			}

			@Override
			@SuppressWarnings("synthetic-access")
			public Void scan(final TreePath path, final Void p)
			{
				if (path.getLeaf() instanceof CompilationUnitTree)
				{
					final String fileName=((CompilationUnitTree)path.getLeaf()).getSourceFile().getName();
					if (fileName.endsWith("package-info.java"))
					{
						return null;
					}
					else
					{
						throw new RuntimeException(fileName);
					}
				}
				final ClassTree classTree=(ClassTree)path.getLeaf();
				startMessage="* "+classTree.getSimpleName();
				if (ctx!=null) throw new RuntimeException();
				ctx=new TreeApiContext(null, processingEnv, null, path.getCompilationUnit());

				final Void result=super.scan(path, p);
				if (!replacements.isEmpty())
				{
					final byte[] withAnnotations=replacements.applyReplacements(ctx.getAllBytes());
					final byte[] withImports=addImports(withAnnotations);
					try
					{
						final JavaFileObject f=path.getCompilationUnit().getSourceFile();
						try (final OutputStream os=f.openOutputStream())
						{
							os.write(withImports);
						}
					}
					catch (final IOException e)
					{
						throw new RuntimeException(e);
					}
				}
				return result;
			}

			private byte[] addImports(final byte[] original)
			{
				final Set<String> staticImports=new TreeSet<>();
				staticImports.addAll(ctx.getImports(true));
				staticImports.addAll(requiredVisibilityEnumImport);

				final Set<String> nonStaticImports=new TreeSet<>();
				nonStaticImports.addAll(ctx.getImports(false));
				nonStaticImports.addAll(requiredAnnotationClassImports);

				final StringBuilder newImports=new StringBuilder();
				final String lineSeparator=new String(lineSeparatorBytes, StandardCharsets.US_ASCII);
				for (final String staticImport: staticImports)
				{
					newImports.append("import static ").append(staticImport).append(";").append(lineSeparator);
				}
				if (!staticImports.isEmpty() && !nonStaticImports.isEmpty())
				{
					newImports.append(lineSeparator);
				}
				for (final Iterator<String> iter=nonStaticImports.iterator(); iter.hasNext();)
				{
					final String nonStaticImport=iter.next();
					newImports.append("import ").append(nonStaticImport).append(";");
					if (iter.hasNext()) newImports.append(lineSeparator);
				}
				final ByteReplacements importReplacements=new ByteReplacements();
				final int importsStart=Math.toIntExact(ctx.getImportsStartPosition());
				final int importsEnd=Math.toIntExact(ctx.getImportsEndPosition());
				final boolean didHaveImports=importsStart!=importsEnd;
				importReplacements.addReplacement(importsStart, importsEnd,
					(didHaveImports?"":lineSeparator)+newImports.toString()+(didHaveImports?"":lineSeparator)
				);
				return importReplacements.applyReplacements(original);
			}

			@Override
			public Void visitClass(final ClassTree node, final Void p)
			{
				try
				{
					convertCurrentPath(node, "[class]");
				}
				catch (final RuntimeException e)
				{
					throw new RuntimeException("while processing "+node.getSimpleName()+" in "+getCurrentPath().getCompilationUnit().getSourceFile().getName(), e);
				}
				return super.visitClass(node, p);
			}

			@Override
			public Void visitVariable(final VariableTree node, final Void p)
			{
				try
				{
					convertCurrentPath(node, node.getName().toString());
				}
				catch (final RuntimeException e)
				{
					throw new RuntimeException("while processing "+node.getName()+" in "+getCurrentPath().getCompilationUnit().getSourceFile().getName(), e);
				}
				return super.visitVariable(node, p);
			}

			@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR") // ctx initialised in 'scan'
			private void convertCurrentPath(final Tree node, final String positionForLog)
			{
				final DocCommentTree docCommentTree=docTrees.getDocCommentTree(getCurrentPath());
				if (docCommentTree!=null)
				{
					final int docContentStart=Math.toIntExact(docTrees.getSourcePositions().getStartPosition(getCurrentPath().getCompilationUnit(), docCommentTree, docCommentTree));
					final int docContentEnd=Math.toIntExact(docTrees.getSourcePositions().getEndPosition(getCurrentPath().getCompilationUnit(), docCommentTree, docCommentTree))+1;
					if (docContentStart==-1)
					{
						// this happens for empty comments
						return;
					}
					if (docContentEnd==0)
					{
						printProgressLog("WARNING: can't convert comment at "+positionForLog+" - check manually");
						return;
					}
					if (previousDocCommentStart==docContentStart && previousDocCommentEnd==docContentEnd)
					{
						// This happens for multi-assignments, must be skipped:
						// /** @cope.set none */
						// public static final StringField
						// 	firstWord = new StringField(),
						// 	secondWord = new StringField(),
						// 	...
						return;
					}
					previousDocCommentStart=docContentStart;
					previousDocCommentEnd=docContentEnd;
					final String docComment=ctx.getSourceString(docContentStart, docContentEnd).trim();
					if (docComment.contains("@cope.") && !docComment.contains("@cope.generated"))
					{
						printProgressLog(positionForLog);
						final int docStart=ctx.searchBefore(
							docContentStart,
							"/**".getBytes(StandardCharsets.US_ASCII)
						);
						final int docEnd=ctx.searchAfter(
							docContentEnd-2,
							"*/".getBytes(StandardCharsets.US_ASCII)
						);
						final int elementStart=Math.toIntExact(docTrees.getSourcePositions().getStartPosition(getCurrentPath().getCompilationUnit(), node));
						final String betweenCommentAndElement=ctx.getSourceString(docEnd, elementStart);
						final JavadocAndAnnotations javadocAndAnnotations=convert(docComment, betweenCommentAndElement);
						if ( javadocAndAnnotations.javadoc.trim().isEmpty() )
						{
							// drop complete comment
							final int lineSeparatorBeforeDocStart=ctx.searchBefore(docStart, lineSeparatorBytes);
							final int lineStart=lineSeparatorBeforeDocStart==-1?0:lineSeparatorBeforeDocStart+lineSeparatorBytes.length;
							final int lineEnd=ctx.searchAfter(docEnd-1, lineSeparatorBytes);
							final String lineAfterComment=ctx.getSourceString(docEnd, lineEnd);
							if (ctx.getSourceString(lineStart, docStart).trim().isEmpty() && lineAfterComment.trim().isEmpty())
							{
								// the comment is on its own line
								replacements.addReplacement(
									lineStart,
									lineEnd,
									""
								);

							}
							else
							{
								// the comment is in-line
								replacements.addReplacement(
									docStart,
									docEnd+countStartingWhitespace(lineAfterComment),
									""
								);
							}
						}
						else
						{
							replacements.addReplacement(
								docContentStart,
								docContentEnd,
								javadocAndAnnotations.javadoc
							);
						}
						replacements.addReplacement(
							elementStart,
							elementStart,
							javadocAndAnnotations.annotations
						);
					}
				}
			}

			private int countStartingWhitespace(final String s)
			{
				for (int i=0; i<s.length(); i++)
				{
					if (!Character.isWhitespace(s.charAt(i)))
					{
						return i;
					}
				}
				return s.length();
			}

			private JavadocAndAnnotations convert(final String s, final String afterEachAnnotation)
			{
				final StringBuilder annotations=new StringBuilder();
				int to=s.indexOf("@cope.");
				final String javadoc=dropTrailingSpaceAndStar(s.substring(0, to));
				final StringBuilder tagsForWrapperType=new StringBuilder();
				while(true)
				{
					final int from=to;
					to=s.indexOf("@cope.", from+1);
					final String tag=dropTrailingSpaceAndStar(s.substring(from, to==-1?s.length():to)).trim();
					if (tag.contains(CopeType.TAG_ACTIVATION_CONSTRUCTOR)
						||tag.contains(CopeType.TAG_GENERIC_CONSTRUCTOR)
						||tag.contains(CopeType.TAG_INDENT)
						||tag.contains(CopeType.TAG_INITIAL_CONSTRUCTOR)
						||tag.contains(CopeType.TAG_TYPE)
						)
					{
						tagsForWrapperType.append(tag).append(System.lineSeparator());
					}
					else
					{
						annotations.append(convertTag(tag));
						annotations.append(afterEachAnnotation);
					}
					if (to==-1) break;
				}
				if (tagsForWrapperType.length()>0)
				{
					annotations.append(formatAnnotation(Option.forType(tagsForWrapperType.toString())));
					annotations.append(afterEachAnnotation);
				}
				return new JavadocAndAnnotations(javadoc, annotations.toString()+(afterEachAnnotation.isEmpty()?" ":""));
			}

			private String dropTrailingSpaceAndStar(final String s)
			{
				for (int i=s.length()-1; i>=0; i--)
				{
					final char c=s.charAt(i);
					if (c!=' ' && c!='\t' && c!='*')
					{
						return s.substring(0, i+1);
					}
				}
				return "";
			}

			private String convertTag(final String tag)
			{
				if (tag.equals("@cope.initial"))
				{
					return formatAnnotation(Option.forInitial(tag));
				}
				else if (tag.equals("@cope.ignore"))
				{
					return formatAnnotation(Option.forIgnore(tag));
				}
				else
				{
					final Pattern wrapPattern=Pattern.compile("@cope\\.([^ ]*)( +(none|public|protected|package|private|override|boolean-as-is|non-final|internal))+");
					final Matcher matcher=wrapPattern.matcher(tag);
					if (!matcher.matches()) throw new RuntimeException(">"+tag+"<");
					final Wrapper option=Option.forFeatureLine(matcher.group(1), tag);
					return formatAnnotation(option);
				}
			}

			private <A extends Annotation> String formatAnnotation(final A annotation)
			{
				requireImport(annotation);
				final StringBuilder result=new StringBuilder();
				result.append("@");
				result.append(annotation.annotationType().getSimpleName());
				boolean parenthesis=false;
				final Method[] annotationMembers=annotation.annotationType().getDeclaredMethods();
				Arrays.sort(annotationMembers, new ConvertComparator("wrap"));
				for (final Method m: annotationMembers)
				{
					try
					{
						final Object value=m.invoke(annotation);
						if (!value.equals(m.getDefaultValue()))
						{
							if (!parenthesis)
							{
								parenthesis=true;
								result.append("(");
							}
							else
							{
								result.append(", ");
							}
							result.append(m.getName()).append("=").append(toJava(value));
						}
					}
					catch (InvocationTargetException|IllegalAccessException e)
					{
						throw new RuntimeException(e);
					}
				}
				if (parenthesis)
				{
					result.append(")");
				}
				return result.toString();
			}

			private void printProgressLog(final String message)
			{
				if (startMessage!=null)
				{
					System.out.println(startMessage);
					startMessage=null;
				}
				System.out.println("  * "+message);
			}

			private String toJava(final Object o)
			{
				if (o instanceof String)
				{
					return "\""+o+"\"";
				}
				else if (o instanceof Boolean || o instanceof Number)
				{
					return o.toString();
				}
				else if (o instanceof Visibility)
				{
					final Visibility visibility=(Visibility)o;
					requireImport(visibility);
					return (visibility).name();
				}
				else
				{
					throw new RuntimeException(o.toString());
				}
			}

		}
	}

	private static class JavadocAndAnnotations
	{
		final String javadoc;
		final String annotations;

		JavadocAndAnnotations(final String javadoc, final String annotations)
		{
			this.javadoc=javadoc;
			this.annotations=annotations;
		}
	}

	@SuppressFBWarnings("SE_COMPARATOR_SHOULD_BE_SERIALIZABLE")
	private static class ConvertComparator implements Comparator<Method>
	{
		final String preferredName;

		ConvertComparator(final String preferredName)
		{
			this.preferredName=preferredName;
		}

		@Override
		public int compare(final Method o1, final Method o2)
		{
			if (o1.getName().equals(preferredName))
			{
				return o2.getName().equals(preferredName)?0:-1;
			}
			else if (o2.getName().equals(preferredName))
			{
				return 1;
			}
			else
			{
				return o1.getName().compareTo(o2.getName());
			}
		}
	}

	private ConvertTagsToAnnotations()
	{
		// prevent instantiation
	}
}
