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
import java.util.Set;
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
				new TreePathScanner<Void, Void>()
				{
					final ByteReplacements replacements=new ByteReplacements();
					TreeApiContext ctx;
					String startMessage=null;

					@Override
					@SuppressWarnings("synthetic-access")
					public Void scan(final TreePath path, final Void p)
					{
						startMessage="* "+((ClassTree)path.getLeaf()).getSimpleName();
						if (ctx!=null) throw new RuntimeException();
						ctx=new TreeApiContext(null, processingEnv, null, path.getCompilationUnit());

						final Void result=super.scan(path, p);
						if (!replacements.isEmpty())
						{
							final byte[] newSource=replacements.applyReplacements(ctx.getAllBytes());
							try
							{
								final JavaFileObject f=tp.getCompilationUnit().getSourceFile();
								try (final OutputStream os=f.openOutputStream())
								{
									os.write(newSource);
								}
							}
							catch (final IOException e)
							{
								throw new RuntimeException(e);
							}
						}
						return result;
					}

					@Override
					@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR") // ctx initialised in 'scan'
					public Void visitVariable(final VariableTree node, final Void p)
					{
						try
						{
							final DocCommentTree docCommentTree=docTrees.getDocCommentTree(getCurrentPath());
							if (docCommentTree!=null)
							{
								final int docContentStart=Math.toIntExact(docTrees.getSourcePositions().getStartPosition(getCurrentPath().getCompilationUnit(), docCommentTree, docCommentTree));
								final int docContentEnd=Math.toIntExact(docTrees.getSourcePositions().getEndPosition(getCurrentPath().getCompilationUnit(), docCommentTree, docCommentTree))+1;
								final String docComment=ctx.getSourceString(docContentStart, docContentEnd).trim();
								if (docComment.contains("@cope.") && !docComment.contains("@cope.generated"))
								{
									if (startMessage!=null)
									{
										System.out.println(startMessage);
										startMessage=null;
									}
									System.out.println("  * "+node.getName());
									final int docStart=ctx.searchBefore(
										docContentStart,
										"/**".getBytes(StandardCharsets.US_ASCII)
									);
									final int docEnd=ctx.searchAfter(
										docContentEnd-1,
										"*/".getBytes(StandardCharsets.US_ASCII)
									);
									final int elementStart=Math.toIntExact(docTrees.getSourcePositions().getStartPosition(getCurrentPath().getCompilationUnit(), node));
									final String betweenCommentAndElement=ctx.getSourceString(docEnd, elementStart);
									final JavadocAndAnnotations javadocAndAnnotations=convert(docComment, betweenCommentAndElement);
									if ( javadocAndAnnotations.javadoc.trim().isEmpty() )
									{
										// drop complete comment
										final int lineStart=ctx.searchBefore(docStart, lineSeparatorBytes)+lineSeparatorBytes.length;
										final int lineEnd=ctx.searchAfter(docEnd-1, lineSeparatorBytes);
										final String lineAfterComment=ctx.getSourceString(docEnd, lineEnd);
										if (ctx.getSourceString(lineStart, docStart).trim().isEmpty() && lineAfterComment.trim().isEmpty())
										{
											replacements.addReplacement(
												lineStart,
												lineEnd,
												""
											);
										}
										else
										{
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
						catch (final IllegalArgumentException e)
						{
							throw new RuntimeException("while processing "+node.getName()+" in "+tp.getCompilationUnit().getSourceFile().getName(), e);
						}
						return super.visitVariable(node, p);
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
						while(true)
						{
							final int from=to;
							to=s.indexOf("@cope.", from+1);
							final String tag=dropTrailingSpaceAndStar(s.substring(from, to==-1?s.length():to)).trim();
							annotations.append(convertTag(tag));
							annotations.append(afterEachAnnotation);
							if (to==-1) break;
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
				}.scan(tp, null);
			}
			return true;
		}

		private String toJava(Object o)
		{
			if (o instanceof String)
			{
				return "\""+o+"\"";
			}
			else if (o instanceof Boolean)
			{
				return o.toString();
			}
			else if (o instanceof Enum)
			{
				return o.getClass().getSimpleName()+"."+o;
			}
			else
			{
				throw new RuntimeException(o.toString());
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

		ConvertComparator(String preferredName)
		{
			this.preferredName=preferredName;
		}

		@Override
		public int compare(Method o1, Method o2)
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
