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

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;
import static java.text.MessageFormat.format;

import com.exedio.cope.BooleanField;
import com.exedio.cope.SetValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class Generator
{
	private static final String SET_VALUE = SetValue.class.getName();

	private static final String CONSTRUCTOR_INITIAL = "Creates a new {0} with all the fields initially needed.";
	private static final String CONSTRUCTOR_INITIAL_PARAMETER = "the initial value for field {0}.";
	private static final String CONSTRUCTOR_INITIAL_CUSTOMIZE_TAGS = "It can be customized with the tags " +
																					"<tt>@" + CopeType.TAG_INITIAL_CONSTRUCTOR + ' ' +
																					Tags.TEXT_VISIBILITY_PUBLIC + '|' +
																					Tags.TEXT_VISIBILITY_PACKAGE + '|' +
																					Tags.TEXT_VISIBILITY_PROTECTED + '|' +
																					Tags.TEXT_VISIBILITY_PRIVATE + '|' +
																					Tags.TEXT_NONE +
																					"</tt> " +
																					"in the class comment and " +
																					"<tt>@" + CopeFeature.TAG_INITIAL + "</tt> in the comment of fields.";
	private static final String CONSTRUCTOR_INITIAL_CUSTOMIZE_ANNOTATIONS = getAnnotationsHint(WrapperType.class, "constructor", "...")+" and @"+WrapperInitial.class.getSimpleName();
	private static final String CONSTRUCTOR_GENERIC = "Creates a new {0} and sets the given fields initially.";
	private static final String CONSTRUCTOR_GENERIC_CUSTOMIZE_TAGS = "It can be customized with the tag " +
																					"<tt>@" + CopeType.TAG_GENERIC_CONSTRUCTOR + ' ' +
																					Tags.TEXT_VISIBILITY_PUBLIC + '|' +
																					Tags.TEXT_VISIBILITY_PACKAGE + '|' +
																					Tags.TEXT_VISIBILITY_PROTECTED + '|' +
																					Tags.TEXT_VISIBILITY_PRIVATE + '|' +
																					Tags.TEXT_NONE +
																					"</tt> " +
																					"in the class comment.";
	private static final String CONSTRUCTOR_GENERIC_CUSTOMIZE_ANNOTATIONS = getAnnotationsHint(WrapperType.class, "genericConstructor", "...");

	private static String getAnnotationsHint(final Class<? extends Annotation> annotation, final String annotationMember, final String value)
	{
		return "customize with @"+annotation.getSimpleName()+"("+annotationMember+"="+value+")";
	}

	private final JavaFile javaFile;
	private final StringBuilder output;
	private final String lineSeparator;
	private final boolean longJavadoc;
	private final String finalArgPrefix;
	private final boolean nullabilityAnnotations;
	private final boolean suppressUnusedWarningOnPrivateActivationConstructor;
	private final boolean serialVersionUID;
	private final String serialVersionUIDSuffix;
	private final boolean genericSetValueArray;
	private final boolean directSetValueMap;
	private final String hidingWarningSuppressor;
	private final boolean parenthesesOnEmptyMemberAnnotations;
	private final boolean deprecatedFullyQualified;
	private final boolean overrideOnSeparateLine;
	private final HintFormat hintFormat;
	private int typeIndent = Integer.MIN_VALUE;


	Generator(final JavaFile javaFile, final StringBuilder output, final Params params)
	{
		this.javaFile = javaFile;
		this.output = output;
		this.lineSeparator = System.lineSeparator();
		this.longJavadoc = params.longJavadoc;
		this.finalArgPrefix = params.finalArgs ? "final " : "";
		this.nullabilityAnnotations = params.nullabilityAnnotations;
		this.suppressUnusedWarningOnPrivateActivationConstructor = params.suppressUnusedWarningOnPrivateActivationConstructor;
		this.serialVersionUID = params.serialVersionUID;
		this.serialVersionUIDSuffix = params.serialVersionUIDSuffix.code;
		this.genericSetValueArray = params.genericSetValueArray;
		this.directSetValueMap = params.directSetValueMap;
		this.hidingWarningSuppressor = params.hidingWarningSuppressor;
		this.parenthesesOnEmptyMemberAnnotations = params.parenthesesOnEmptyMemberAnnotations;
		this.deprecatedFullyQualified = params.deprecatedFullyQualified;
		this.overrideOnSeparateLine = params.overrideOnSeparateLine;
		this.hintFormat = params.hintFormat;
	}

	private static final String toCamelCase(final String name)
	{
		final char first = name.charAt(0);
		if (Character.isUpperCase(first))
			return name;
		else
			return Character.toUpperCase(first) + name.substring(1);
	}

	private static final String lowerCamelCase(final String s)
	{
		final char first = s.charAt(0);
		if(Character.isLowerCase(first))
			return s;
		else
			return Character.toLowerCase(first) + s.substring(1);
	}

	private void writeThrowsClause(final Collection<Class<? extends Throwable>> exceptions)
	{
		if(!exceptions.isEmpty())
		{
			writeIndent(2);
			write("throws");
			final CharSeparator comma = new CharSeparator(',');
			for(final Class<? extends Throwable> e : exceptions)
			{
				comma.appendTo(output);
				write(lineSeparator);
				writeIndent(3);
				write(e.getCanonicalName());
			}
			write(lineSeparator);
		}
	}

	private void finishComment(final boolean addComments, final List<String> commentLines)
	{
		finishComment(addComments, commentLines, null);
	}

	private void finishComment(final boolean addComments, final List<String> commentLines, final String extraCommentForTags)
	{
		if (hintFormat==HintFormat.forTags && !addComments)
		{
			throw new RuntimeException("@WrapperType(comments=false) not supported for hintFormat=\""+HintFormat.forTags+"\"");
		}
		if (hintFormat==HintFormat.forTags)
		{
			commentLines.add(" * @" + CopeFeature.TAG_PREFIX + "generated" +
					" This feature has been generated by the cope instrumentor and will be overwritten by the build process.");
			if(extraCommentForTags!=null)
			{
				commentLines.add(" *       "+extraCommentForTags);
			}
		}
		if (hintFormat==HintFormat.forAnnotations)
		{
			write(lineSeparator);
			if (addComments && !commentLines.isEmpty())
				writeIndent();
		}
		if (addComments && !commentLines.isEmpty())
		{
			write("/**");
			write(lineSeparator);
			if(longJavadoc && hintFormat==HintFormat.forTags)
			{
				write(lineSeparator);
				writeIndent();
				write(" **");
				write(lineSeparator);
			}

			boolean first=false;
			for (final String commentLine: commentLines)
			{
				if (first)
				{
					first=false;
				}
				else
				{
					if (!commentLine.isEmpty())
						writeIndent();
				}
				write(commentLine);
				write(lineSeparator);
			}
			writeIndent();
			write(" */");
			write(lineSeparator);
		}
	}

	private void writeGeneratedAnnotation(final boolean addComments, final String extraCommentForAnnotations)
	{
		writeIndent();
		writeAnnotation(Generated.class);
		write("(\"" + Main.GENERATED_VALUE + "\")");
		if (addComments && hintFormat==HintFormat.forAnnotations && extraCommentForAnnotations!=null)
		{
			write(" // "+extraCommentForAnnotations);
		}
		write(lineSeparator);
	}

	private static final String link(final String target)
	{
		return "{@link #" + target + '}';
	}

	private void writeInitialConstructor(final LocalCopeType type)
	{
		if(!type.kind.hasGenericConstructor) // without generic constructor there can be no initial constructor
			return;
		if(!type.hasInitialConstructor())
			return;

		final List<CopeFeature> initialFeatures = type.getInitialFeatures();
		final SortedSet<Class<? extends Throwable>> constructorExceptions = type.getConstructorExceptions();

		final List<String> commentLines=new ArrayList<>();
		commentLines.add(" * "+format(CONSTRUCTOR_INITIAL, type.getName()));
		for(final CopeFeature feature : initialFeatures)
		{
			commentLines.add(" * @param "+feature.getName()+' '+format(CONSTRUCTOR_INITIAL_PARAMETER, link(feature.getName())));
		}
		for(final Class<?> constructorException : constructorExceptions)
		{
			final ConstructorComment a = constructorException.getAnnotation(ConstructorComment.class);
			if(a==null)
				continue;

			final StringSeparator comma = new StringSeparator(", ");
			final StringBuilder fields = new StringBuilder();
			for(final CopeFeature feature : initialFeatures)
			{
				if(!feature.getInitialExceptions().contains(constructorException))
					continue;

				comma.appendTo(fields);
				fields.append(feature.getName());
			}

			final String pattern = a.value();
			commentLines.add(" * @throws "+constructorException.getCanonicalName()+' '+format(pattern, fields.toString()));
		}
		finishComment(type.getOption().comments(), commentLines, CONSTRUCTOR_INITIAL_CUSTOMIZE_TAGS);
		writeGeneratedAnnotation(type.getOption().comments(), CONSTRUCTOR_INITIAL_CUSTOMIZE_ANNOTATIONS);

		writeIndent();
		writeModifier(type.getInitialConstructorModifier());
		write(type.getName());
		write('(');

		final CharSeparator comma = new CharSeparator(',');
		for(final CopeFeature feature : initialFeatures)
		{
			comma.appendTo(output);
			write(lineSeparator);
			writeIndent(3);
			if (nullabilityAnnotations)
			{
				if (feature.isMandatory())
				{
					if (!feature.isInitialTypePrimitive())
					{
						writeAnnotation(Nonnull.class);
						write(' ');
					}
				}
				else
				{
					writeAnnotation(Nullable.class);
					write(' ');
				}
			}
			write(finalArgPrefix);
			write(new Context(feature, feature.parent!=type).write(feature.getInitialType(), false));
			write(' ');
			write(feature.getName());
		}

		write(')');
		write(lineSeparator);
		writeThrowsClause(constructorExceptions);
		writeIndent();
		write('{');
		write(lineSeparator);
		writeIndent(1);
		write("this(new " + SET_VALUE);
		if(genericSetValueArray)
			write("<?>");
		write("[]{");
		write(lineSeparator);
		for(final CopeFeature feature : initialFeatures)
		{
			writeIndent(2);
			if(directSetValueMap)
				write(SET_VALUE + ".map(");
			final CopeType<?> parent = feature.parent;
			if(parent==type)
				write(type.getName());
			else
				write(parent.getCanonicalName());
			write('.');
			write(feature.getName());
			if(directSetValueMap)
				write(',');
			else
				write(".map(");
			write(feature.getName());
			write("),");
			write(lineSeparator);
		}
		writeIndent(1);
		write("});");
		write(lineSeparator);
		writeIndent();
		write('}');
		if (hintFormat==HintFormat.forAnnotations)
			write(lineSeparator);
	}

	private void writeGenericConstructor(final LocalCopeType type)
	{
		if(!type.kind.hasGenericConstructor)
			return;

		final Visibility option = type.getOption().genericConstructor();
		if(!option.exists())
			return;

		final List<String> commentLines=new ArrayList<>();
		commentLines.add(" * "+format(CONSTRUCTOR_GENERIC, type.getName()));
		finishComment(type.getOption().comments(), commentLines, CONSTRUCTOR_GENERIC_CUSTOMIZE_TAGS);
		writeGeneratedAnnotation(type.getOption().comments(), CONSTRUCTOR_GENERIC_CUSTOMIZE_ANNOTATIONS);

		writeIndent();
		writeModifier(option.getModifier(type.getSubtypeModifier()));
		write(type.getName());
		write('(');
		write(finalArgPrefix);
		write(SET_VALUE + "<?>... setValues)");
		write(lineSeparator);
		writeIndent();
		write('{');
		write(lineSeparator);
		writeIndent(1);
		write("super(setValues);");
		write(lineSeparator);
		writeIndent();
		write('}');
		if (hintFormat==HintFormat.forAnnotations)
			write(lineSeparator);
	}

	private void writeActivationConstructor(final LocalCopeType type)
	{
		final String activation = type.kind.activationConstructor;
		if(activation==null)
			return;

		final Visibility option = type.getOption().activationConstructor();
		if(!option.exists())
			return;

		final List<String> commentLines=new ArrayList<>();
		commentLines.add(" * "+"Activation constructor. Used for internal purposes only.");
		commentLines.add(" * @see "+type.kind.top+'#'+type.kind.topSimple+'('+activation+')');
		finishComment(type.getOption().comments(), commentLines);
		writeGeneratedAnnotation(type.getOption().comments(), null);

		writeIndent();
		if(suppressUnusedWarningOnPrivateActivationConstructor && !type.allowSubtypes())
			write("@SuppressWarnings(\"unused\") ");
		writeModifier(option.getModifier(type.getSubtypeModifier()));
		write(type.getName());
		write('(');
		write(finalArgPrefix);
		write(activation);
		write(" ap){super(ap);");
		if (hintFormat==HintFormat.forTags)
			write(lineSeparator);
		write('}');
		if (hintFormat==HintFormat.forAnnotations)
			write(lineSeparator);
	}

	private void writeFeature(final LocalCopeFeature feature)
	{
		final Object instance = feature.getInstance();
		final JavaClass javaClass = feature.getParent();
		final Kind kind = feature.parent.kind;
		for(final WrapperX wrapper : getWrappers(instance))
		{
			final String pattern = wrapper.getMethodWrapperPattern();
			final String modifierTag = wrapper.getOptionTagName()!=null ? wrapper.getOptionTagName() : pattern!=null ? format(pattern, "", "") : wrapper.getName();
			final Wrapper option = feature.getOption(modifierTag);

			final Visibility visibility = option.visibility();
			if(!visibility.exists())
				continue;
			if(!kind.allowStaticClassToken && wrapper.hasStaticClassToken())
				continue;

			final Context ctx = new Context(feature, wrapper);
			final String methodName = wrapper.getName();
			final java.lang.reflect.Type methodReturnType = wrapper.getReturnType();
			final List<WrapperX.Parameter> parameters = wrapper.getParameters();
			final Map<Class<? extends Throwable>, String[]> throwsClause = wrapper.getThrowsClause();
			final String featureNameCamelCase = toCamelCase(feature.getName());
			final boolean isStatic = wrapper.isStatic();
			final boolean internal = option.internal();
			final boolean override = option.override();
			final boolean useIs = instance instanceof BooleanField && methodName.startsWith("get");

			final Object[] arguments = new String[]{
					link(feature.getName()),
					feature.getName(),
					lowerCamelCase(feature.parent.getName()),
					featureNameCamelCase};
			{
				final List<String> commentLines=new ArrayList<>();
				collectCommentParagraph(commentLines, "", " ", wrapper.getCommentArray(), arguments);

				for(final WrapperX.Parameter parameter : wrapper.getParameters())
				{
					if(parameter.varargs==null)
					{
						collectCommentParagraph(
								commentLines,
								"@param " + format(parameter.getName(), arguments),
								"        ",
								parameter.getComment(), arguments);
					}
					else
					{
						for(final Object parameterInstance : parameter.varargs)
						{
							final String parameterName = javaClass.getFieldByInstance(parameterInstance).name;
							final CopeFeature parameterFeature = feature.parent.getFeature(parameterName);

							final Object[] parameterArguments = new String[]{
									link(parameterFeature.getName()),
									parameterFeature.getName(),
									lowerCamelCase(parameterFeature.parent.getName())};
							collectCommentParagraph(
									commentLines,
									"@param " + format(parameterName, parameterArguments),
									"        ",
									parameter.getComment(), parameterArguments);
						}
					}
				}
				collectCommentParagraph(
						commentLines,
						"@return",
						"         ",
						wrapper.getReturnComment(), arguments);

				for(final Map.Entry<Class<? extends Throwable>, String[]> e : throwsClause.entrySet())
				{
					collectCommentParagraph(
							commentLines,
							"@throws " + e.getKey().getCanonicalName(),
							"         ",
							e.getValue(), arguments);
				}
				finishComment(
					feature.parent.getOption().comments(),
					commentLines,
					modifierTag!=null
					?  "It can be customized with the tag " +
						"<tt>@" + CopeFeature.TAG_PREFIX + modifierTag + ' ' +
						Tags.TEXT_VISIBILITY_PUBLIC + '|' +
						Tags.TEXT_VISIBILITY_PACKAGE + '|' +
						Tags.TEXT_VISIBILITY_PROTECTED + '|' +
						Tags.TEXT_VISIBILITY_PRIVATE + '|' +
						Tags.TEXT_NONE + '|' +
						Tags.TEXT_NON_FINAL +
						(useIs ? '|' + Tags.TEXT_BOOLEAN_AS_IS : "") + "</tt> " +
						"in the comment of the field."
					: null
				);
				writeGeneratedAnnotation(
					feature.parent.getOption().comments(),
					modifierTag!=null
					?  getAnnotationsHint(Wrapper.class, "wrap", "\""+modifierTag+"\"")
					: null
				);
			}

			if(wrapper.isMethodDeprecated())
			{
				if(deprecatedFullyQualified)
					writeEmptyAnnotationOnSeparateLine(Deprecated.class);
				else
				{
					writeIndent();
					write("@Deprecated");
					writeEmptyParenthesesForAnnotation();
					write(lineSeparator);
				}
			}

			switch(wrapper.getMethodNullability())
			{
				case NONNULL:
					writeEmptyAnnotationOnSeparateLine(Nonnull.class);
					break;
				case NULLABLE:
					writeEmptyAnnotationOnSeparateLine(Nullable.class);
					break;
				case DEFAULT:
					// nothing to do
					break;
				default:
					throw new RuntimeException("invalid case");
			}

			if(override && overrideOnSeparateLine)
			{
				writeEmptyAnnotationOnSeparateLine(Override.class);
			}

			writeIndent();

			if(override && !overrideOnSeparateLine)
			{
				writeAnnotation(Override.class);
				writeEmptyParenthesesForAnnotation();
				write(' ');
			}

			writeModifier(
					visibility.getModifier(
							internal && visibility.isDefault()
							? PRIVATE
							: feature.getModifier()
					) |
					(isStatic ? STATIC : 0) |
					(option.asFinal() ? FINAL : 0));
			write(ctx.write(methodReturnType, false));
			if(useIs && option.booleanAsIs())
			{
				write(" is");
				write(featureNameCamelCase);
			}
			else
			{
				write(' ');
				if(pattern!=null)
				{
					if(feature.isDefault())
					{
						final String x = format(pattern, "", "");
						if(!isKeyword(x))
							write(x);
						else
							write(format(pattern, featureNameCamelCase, feature.getName()));
					}
					else
						write(format(pattern, featureNameCamelCase, feature.getName()));
				}
				else
				{
					if(feature.isDefault() && !isKeyword(methodName))
						write(methodName);
					else
						writeName(methodName, featureNameCamelCase);
				}
			}
			if(internal)
				write("Internal");
			write('(');
			{
				final CharSeparator comma = new CharSeparator(',');
				for (final Iterator<WrapperX.Parameter> iter = parameters.iterator(); iter.hasNext();)
				{
					final WrapperX.Parameter parameter = iter.next();
					if(parameter.varargs==null)
					{
						comma.appendTo(output);
						writeParameterNullability(parameter);
						write(finalArgPrefix);
						write(ctx.write(parameter.getType(), !iter.hasNext()));
						write(' ');
						write(format(parameter.getName(), arguments));
					}
					else
					{
						for(final Object parameterInstance : parameter.varargs)
						{
							comma.appendTo(output);
							final JavaField parameterField = javaClass.getFieldByInstance(parameterInstance);
							final CopeFeature parameterFeature = feature.parent.getFeature(parameterField.name);

							if (!parameterFeature.isInitialTypePrimitive())
							{
								writeParameterNullability(parameter);
							}
							write(finalArgPrefix);
							write(new Context(parameterFeature, false).write(parameterFeature.getInitialType(), !iter.hasNext()));
							write(' ');
							write(format(parameterField.name, arguments));
						}
					}
				}
			}
			write(')');
			write(lineSeparator);
			writeThrowsClause(throwsClause.keySet());
			writeIndent();
			write('{');
			write(lineSeparator);
			writeIndent(1);
			if(!methodReturnType.equals(void.class))
				write("return ");
		if(kind.revertFeatureBody)
		{
			write(methodName);
			write('(');
			{
				write(feature.parent.getName());
				write('.');
				write(feature.getName());
				for(final WrapperX.Parameter parameter : parameters)
				{
					write(',');
					write(format(parameter.getName(), arguments));
				}
			}
			write(')');
		}
		else
		{
			write(kind.featurePrefix);
			write(feature.parent.getName());
			write('.');
			write(feature.getName());
			write(kind.featurePostfix);
			write('.');
			write(methodName);
			write('(');
			{
				final CharSeparator comma = new CharSeparator(',');
				if(isStatic)
				{
					if(wrapper.hasStaticClassToken())
					{
						comma.appendTo(output);
						write(feature.parent.getName());
						write(".class");
						if(feature.parent.getTypeParameters()>0)
							write("Wildcard.value");
					}
				}
				else
				{
					comma.appendTo(output);
					write(kind.featureThis);
				}
				for(final WrapperX.Parameter parameter : parameters)
				{
					if(parameter.varargs==null)
					{
						comma.appendTo(output);
						write(format(parameter.getName(), arguments));
					}
					else
					{
						for(final Object parameterInstance : parameter.varargs)
						{
							comma.appendTo(output);
							write(format(javaClass.getFieldByInstance(parameterInstance).name, arguments));
						}
					}
				}
			}
			write(')');
		}
			write(';');
			write(lineSeparator);
			writeIndent();
			write('}');
			if (hintFormat==HintFormat.forAnnotations)
				write(lineSeparator);
		}
	}

	private void writeAnnotation(final Class<? extends Annotation> annotationClass)
	{
		write('@');
		write(annotationClass.getName());
	}

	private void writeParameterNullability(final WrapperX.Parameter parameter)
	{
		if ( nullabilityAnnotations )
		{
			if ( parameter.isNonnull() )
			{
				writeAnnotation(Nonnull.class);
				write(' ');
			}
			if ( parameter.isNullable() )
			{
				writeAnnotation(Nullable.class);
				write(' ');
			}
		}
	}

	private void writeEmptyParenthesesForAnnotation()
	{
		if(parenthesesOnEmptyMemberAnnotations)
		{
			write("()");
		}
	}

	private void writeEmptyAnnotationOnSeparateLine(final Class<? extends Annotation> annotationClass)
	{
		writeIndent();
		writeAnnotation(annotationClass);
		writeEmptyParenthesesForAnnotation();
		write(lineSeparator);
	}

	private List<WrapperX> getWrappers(final Object feature)
	{
		return getWrappers(feature.getClass(), feature);
	}

	private List<WrapperX> getWrappers(final Class<?> clazz, final Object feature)
	{
		return WrapperByAnnotations.make(
				clazz,
				feature,
				clazz.getSuperclass().isAnnotationPresent(WrapFeature.class)
				? getWrappers(clazz.getSuperclass(), feature)
				: Collections.<WrapperX>emptyList(),
				nullabilityAnnotations);
	}

	private void writeName(final String methodName, final String featureName)
	{
		for(int i = 0; i<methodName.length(); i++)
			if(Character.isUpperCase(methodName.charAt(i)))
			{
				write(methodName.substring(0, i));
				write(featureName);
				write(methodName.substring(i));
				return;
			}

		write(methodName);
		write(featureName);
	}

	private static void collectCommentParagraph(
			final List<String> commentLines,
			final String prefix1, final String prefixN,
			final String[] lines,
			final Object[] arguments)
	{
		if(lines.length>0)
		{
			final String line = lines[0];
			commentLines.add(" *"+(prefix1.isEmpty()?"":(" "+prefix1))+(line.isEmpty()?"":(" "+format(line, arguments))));
		}
		for(int i = 1; i<lines.length; i++)
		{
			final String line = lines[i];
			commentLines.add(" *"+(line.isEmpty()?"":(prefixN+format(line, arguments))));
		}
	}

	private void writeSerialVersionUID(final LocalCopeType type)
	{
		if(!serialVersionUID)
			return;

		final List<String> commentLines=new ArrayList<>();
		finishComment(type.getOption().comments(), commentLines);
		writeGeneratedAnnotation(type.getOption().comments(), null);

		writeIndent();
		writeModifier(PRIVATE|STATIC|FINAL);
		write("long serialVersionUID = 1");
		if(serialVersionUIDSuffix!=null)
			write(serialVersionUIDSuffix);
		write(';');
		if (hintFormat==HintFormat.forAnnotations)
			write(lineSeparator);
	}

	private void writeType(final LocalCopeType type)
	{
		final Kind.Type kind = type.kind.type;
		if(kind==null)
			return;

		final Visibility option = type.getOption().type();
		if(!option.exists())
			return;

		final List<String> commentLines=new ArrayList<>();
		commentLines.add(
			" * "+
			format(
				kind.doc,
				lowerCamelCase(type.getName()))
		);
		finishComment(
				type.getOption().comments(),
				commentLines,
				"It can be customized with the tag " +
				"<tt>@" + CopeType.TAG_TYPE + ' ' +
				Tags.TEXT_VISIBILITY_PUBLIC + '|' +
				Tags.TEXT_VISIBILITY_PACKAGE + '|' +
				Tags.TEXT_VISIBILITY_PROTECTED + '|' +
				Tags.TEXT_VISIBILITY_PRIVATE + '|' +
				Tags.TEXT_NONE +
				"</tt> " +
				"in the class comment."
		);
		writeGeneratedAnnotation(
			type.getOption().comments(),
			"customize with @"+WrapperType.class.getSimpleName()+"(type=...)"
		);

		if(hidingWarningSuppressor!=null && type.getSuperclass()!=null)
		{
			writeIndent();
			write("@SuppressWarnings(\"hiding\")");
			write(lineSeparator);
		}

		writeIndent();
		writeModifier(option.getModifier(type.getModifier()) | (STATIC|FINAL));
		write(kind.field);
		write('<');
		write(type.getName());
		writeWildcard(type);
		write("> TYPE = ");
		write(kind.factory);
		write(".newType(");
		write(type.getName());
		write(".class");
		if(type.getTypeParameters()>0)
			write("Wildcard.value");
		write(");");
		if (hintFormat==HintFormat.forAnnotations)
			write(lineSeparator);
	}

	private void writeWildcard(final LocalCopeType type)
	{
		final int typeParameters = type.getTypeParameters();
		if(typeParameters>0)
		{
			write("<?");
			for(int i = 1; i<typeParameters; i++)
				write(",?");
			write('>');
		}
	}

	void write(final Charset charset)
	{
		final String buffer = new String(javaFile.getSourceWithoutGeneratedFragments(), charset);
		int previousClassEndPosition = 0;
		for(final JavaClass javaClass : javaFile.getClasses())
		{
			final LocalCopeType type = LocalCopeType.getCopeType(javaClass);
			final int classEndPosition = javaClass.getClassEndPositionInSourceWithoutGeneratedFragments();
			if(type!=null)
			{
				assert previousClassEndPosition<=classEndPosition;
				if(previousClassEndPosition<classEndPosition)
					output.append(buffer, previousClassEndPosition, classEndPosition);

				try
				{
					typeIndent = type.getOption().indent();
					writeClassFeatures(type);
					typeIndent = Integer.MIN_VALUE;
				}
				catch (final RuntimeException e)
				{
					throw new RuntimeException("Failed to generate class features for " + javaClass, e);
				}
				previousClassEndPosition = classEndPosition;
			}
		}
		output.append(buffer, previousClassEndPosition, buffer.length());
	}

	private void writeClassFeatures(final LocalCopeType type)
	{
		if(type.isInterface())
			return;

		writeInitialConstructor(type);
		writeGenericConstructor(type);

		for(final LocalCopeFeature feature : type.getFeatures())
			writeFeature(feature);

		writeSerialVersionUID(type);
		writeType(type);
		writeActivationConstructor(type);
	}

	private void writeModifier(final int modifier)
	{
		final String modifierString = Modifier.toString(modifier);
		if(!modifierString.isEmpty())
		{
			write(modifierString);
			write(' ');
		}
	}

	private static boolean isKeyword(final String s)
	{
		return "for".equals(s); // TODO
	}

	private void writeIndent()
	{
		assert typeIndent>=0 : typeIndent;
		writeIndentInternal(typeIndent);
	}

	private void writeIndent(final int additionalLevel)
	{
		assert typeIndent>=0 : typeIndent;
		writeIndentInternal(typeIndent + additionalLevel);
	}

	private void writeIndentInternal(final int level)
	{
		for(int i = 0; i<level; i++)
			output.append('\t');
	}

	private void write(final String s)
	{
		output.append(s);
	}

	private void write(final char c)
	{
		output.append(c);
	}
}
