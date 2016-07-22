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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
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
							return formatAnnotation(WrapperInitial.class, Collections.emptyMap());
						}
						else if (tag.equals("@cope.ignore"))
						{
							return formatAnnotation(WrapperIgnore.class, Collections.emptyMap());
						}
						else
						{
							final Pattern wrapPattern=Pattern.compile("@cope\\.([^ ]*)( +(none|public|protected|package|private|override|boolean-as-is|non-final|internal))+");
							final Matcher matcher=wrapPattern.matcher(tag);
							if (!matcher.matches()) throw new RuntimeException(">"+tag+"<");
							final Wrapper option=Option.forFeatureLine(tag);

							final Map<String,String> annotationValues=new LinkedHashMap<>();
							annotationValues.put("wrap", "\""+matcher.group(1)+"\"");
							if (option.visibility()!=Visibility.DEFAULT)
							{
								annotationValues.put("visibility", Visibility.class.getSimpleName()+"."+option.visibility());
							}
							if (!option.asFinal())
							{
								annotationValues.put("asFinal", "false");
							}
							if (option.booleanAsIs())
							{
								annotationValues.put("booleanAsIs", "true");
							}
							if (option.internal())
							{
								annotationValues.put("internal", "true");
							}
							if (option.override())
							{
								annotationValues.put("override", "true");
							}
							return formatAnnotation(Wrapper.class, annotationValues);
						}
					}

					private String formatAnnotation(final Class<? extends Annotation> annotationClass, final Map<String,String> values)
					{
						final StringBuilder result=new StringBuilder();
						result.append("@");
						result.append(annotationClass.getSimpleName());
						if (!values.isEmpty())
						{
							result.append("(");
							for (final Iterator<Map.Entry<String, String>> iter=values.entrySet().iterator(); iter.hasNext();)
							{
								final Map.Entry<String, String> next=iter.next();
								result.append(next.getKey());
								result.append("=");
								result.append(next.getValue());
								if (iter.hasNext())
								{
									result.append(", ");
								}
							}
							result.append(")");
						}
						return result.toString();
					}
				}.scan(tp, null);
			}
			return true;
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


	private ConvertTagsToAnnotations()
	{
		// prevent instantiation
	}
}
