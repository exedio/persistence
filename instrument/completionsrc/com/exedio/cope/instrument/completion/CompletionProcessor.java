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

package com.exedio.cope.instrument.completion;

import com.exedio.cope.instrument.Wrap;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Completions;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;

@SupportedAnnotationTypes("com.exedio.cope.instrument.Wrapper")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SuppressWarnings("LiteralAsArgToStringEquals")
public class CompletionProcessor extends AbstractProcessor
{
	private static final Logger log = java.util.logging.Logger.getLogger(CompletionProcessor.class.getName());

	@SuppressWarnings("unused")
	private static void debug(final String message, final Object... params)
	{
		// uncomment to activate debug logs:
		// log.log(Level.INFO, message, params);
	}

	public CompletionProcessor()
	{
		debug("create");
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		return true;
	}

	private static Completion stringCompletion(final String value, final String message)
	{
		return Completions.of("\""+value+"\"", message);
	}

	private static Iterable<? extends Completion> toCompletions(final Map<String,List<String>> completionData)
	{
		final List<Completion> result=new ArrayList<>();
		for (final Map.Entry<String,List<String>> entry: completionData.entrySet())
		{
			result.add(stringCompletion(entry.getKey(), concatLines(entry.getValue())));
		}
		return result;
	}

	private static String concatLines(final List<String> lines)
	{
		final StringBuilder result=new StringBuilder();
		for (final String line: lines)
		{
			result.append(line).append(System.lineSeparator());
		}
		return result.toString();
	}

	@Override
	public Iterable<? extends Completion> getCompletions(final Element element, final AnnotationMirror annotation, final ExecutableElement member, final String userText)
	{
		debug("getCompletions element={0} annotation={1} member={2} userText={3}", element, annotation, member, userText);

		if (member.getSimpleName().toString().equals("wrap"))
		{
			return getCompletionsForWrap(element);
		}
		return List.of();
	}

	private static Iterable<? extends Completion> getCompletionsForWrap(final Element element)
	{
		final TypeMirror asType=element.asType();
		if (!(asType instanceof DeclaredType))
		{
			log.log(Level.WARNING, "expected DeclaredType but found {0}", asType.getClass());
			return List.of();
		}
		final DeclaredType declaredType=(DeclaredType)asType;
		if (!(declaredType.asElement() instanceof TypeElement))
		{
			log.log(Level.WARNING, "expected TypeElement but found {0}", declaredType.getClass());
			return List.of();
		}
		final Map<String,List<String>> completionData=new HashMap<>();
		declaredType.accept(new CompletionCollector(completionData), null);
		completionData.put("*", List.of("fallback configuration"));
		return toCompletions(completionData);
	}

	private static final class CompletionCollector implements TypeVisitor<Void, Void>
	{
		private final Map<String, List<String>> completionData;

		@SuppressWarnings("unused")
		private CompletionCollector(final Map<String, List<String>> completionData)
		{
			this.completionData=completionData;
		}

		@Override
		public Void visit(final TypeMirror t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visit(TM,V)");
			return null;
		}

		@Override
		public Void visit(final TypeMirror t)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visit(TM)");
			return null;
		}

		@Override
		public Void visitPrimitive(final PrimitiveType t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitPrimitive");
			return null;
		}

		@Override
		public Void visitNull(final NullType t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitNull");
			return null;
		}

		@Override
		public Void visitArray(final ArrayType t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitArray");
			return null;
		}

		@Override
		public Void visitDeclared(final DeclaredType declaredType, final Void p)
		{
			debug("CompletionCollector visitDeclared "+declaredType);
			final TypeElement typeElement=(TypeElement)declaredType.asElement();
			for (final Element enclosedElement: typeElement.getEnclosedElements())
			{
				final Wrap wrap=enclosedElement.getAnnotation(Wrap.class);
				if (wrap!=null)
				{
					final String value;
					if (!hasDefaultNameGetter(wrap))
					{
						// needs feature instantiation - skip:
						continue;
					}
					if (!wrap.optionTagname().isEmpty())
					{
						value=wrap.optionTagname();
					}
					else if (!wrap.name().isEmpty())
					{
						value=MessageFormat.format(wrap.name(), "", "");
					}
					else
					{
						value=enclosedElement.getSimpleName().toString();
					}
					final List<String> comments=completionData.computeIfAbsent(value, k -> new ArrayList<>());
					final StringBuilder comment=new StringBuilder(enclosedElement.getSimpleName().toString());
					comment.append("(");
					boolean first=true;
					boolean needsComma=false;
					for (final VariableElement var: ((ExecutableElement)enclosedElement).getParameters())
					{
						if (!first || !(var.asType().toString().equals("com.exedio.cope.Item")||var.asType().toString().equals("java.lang.Class")))
						{
							if (needsComma) comment.append(",");
							comment.append(var.asType().toString().trim());
							needsComma=true;
						}
						first=false;
					}
					comment.append(")");
					if (hasAnyHides(wrap))
					{
						comment.append("?");
					}
					comments.add(comment.toString());
				}
			}
			typeElement.getSuperclass().accept(new CompletionCollector(completionData), null);
			return null;
		}

		private static boolean hasDefaultNameGetter(final Wrap wrap)
		{
			final String stringGetterDefaultClassName="com.exedio.cope.instrument.StringGetterDefault";
			try
			{
				return wrap.nameGetter().getName().equals(stringGetterDefaultClassName);
			}
			catch (final MirroredTypeException e)
			{
				return e.getTypeMirror().toString().equals(stringGetterDefaultClassName);
			}
		}

		private static boolean hasAnyHides(final Wrap wrap)
		{
			try
			{
				return wrap.hide().length>0;
			}
			catch (final MirroredTypesException e)
			{
				return !e.getTypeMirrors().isEmpty();
			}
		}

		@Override
		public Void visitError(final ErrorType t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitError");
			return null;
		}

		@Override
		public Void visitTypeVariable(final TypeVariable t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitTypeVariable");
			return null;
		}

		@Override
		public Void visitWildcard(final WildcardType t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitWildcard");
			return null;
		}

		@Override
		public Void visitExecutable(final ExecutableType t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitExecutable");
			return null;
		}

		@Override
		public Void visitNoType(final NoType t, final Void p)
		{
			debug("CompletionCollector visitNoType");
			return null;
		}

		@Override
		public Void visitUnknown(final TypeMirror t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitUnknown");
			return null;
		}

		@Override
		public Void visitUnion(final UnionType t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitUnion");
			return null;
		}

		@Override
		public Void visitIntersection(final IntersectionType t, final Void p)
		{
			log.log(Level.WARNING, "unexpected CompletionCollector visitIntersection");
			return null;
		}
	}
}
