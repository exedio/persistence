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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.BooleanField;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockActivationParameters;
import com.exedio.cope.pattern.BlockType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
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
	private static final String CONSTRUCTOR_INITIAL_CUSTOMIZE = "It can be customized with the tags " +
																					"<tt>@" + CopeType.TAG_INITIAL_CONSTRUCTOR + ' ' +
																					Option.TEXT_VISIBILITY_PUBLIC + '|' +
																					Option.TEXT_VISIBILITY_PACKAGE + '|' +
																					Option.TEXT_VISIBILITY_PROTECTED + '|' +
																					Option.TEXT_VISIBILITY_PRIVATE + '|' +
																					Option.TEXT_NONE +
																					"</tt> " +
																					"in the class comment and " +
																					"<tt>@" + CopeFeature.TAG_INITIAL + "</tt> in the comment of fields.";
	private static final String CONSTRUCTOR_GENERIC = "Creates a new {0} and sets the given fields initially.";
	private static final String CONSTRUCTOR_GENERIC_CUSTOMIZE = "It can be customized with the tag " +
																					"<tt>@" + CopeType.TAG_GENERIC_CONSTRUCTOR + ' ' +
																					Option.TEXT_VISIBILITY_PUBLIC + '|' +
																					Option.TEXT_VISIBILITY_PACKAGE + '|' +
																					Option.TEXT_VISIBILITY_PROTECTED + '|' +
																					Option.TEXT_VISIBILITY_PRIVATE + '|' +
																					Option.TEXT_NONE +
																					"</tt> " +
																					"in the class comment.";

	/**
	 * All generated class features get this doccomment tag.
	 */
	static final String TAG_GENERATED = CopeFeature.TAG_PREFIX + "generated";


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

	private void writeCommentHeader()
	{
		write("/**");
		write(lineSeparator);
		if(longJavadoc)
		{
			write(lineSeparator);
			writeIndent();
			write(" **");
			write(lineSeparator);
		}
	}

	private void writeCommentFooter()
	{
		writeCommentFooter(null);
	}

	private void writeCommentFooter(final String extraComment)
	{
		writeIndent();
		write(" * @" + TAG_GENERATED +
				" This feature has been generated by the cope instrumentor and will be overwritten by the build process.");
		write(lineSeparator);
		if(extraComment!=null)
		{
			writeIndent();
			write(" *       ");
			write(extraComment);
			write(lineSeparator);
		}
		writeIndent();
		write(" */");
		write(lineSeparator);

		writeIndent();
		writeAnnotation(Generated.class);
		write("(\"" + Main.GENERATED_VALUE + "\")");
		write(lineSeparator);
	}

	private static final String link(final String target)
	{
		return "{@link #" + target + '}';
	}

	private void writeInitialConstructor(final CopeType type)
	{
		if(type.isBlock)
			return;
		if(!type.hasInitialConstructor())
			return;

		final List<CopeFeature> initialFeatures = type.getInitialFeatures();
		final SortedSet<Class<? extends Throwable>> constructorExceptions = type.getConstructorExceptions();

		writeCommentHeader();
		writeIndent();
		write(" * ");
		write(format(CONSTRUCTOR_INITIAL, type.name));
		write(lineSeparator);
		for(final CopeFeature feature : initialFeatures)
		{
			writeIndent();
			write(" * @param ");
			write(feature.name);
			write(' ');
			write(format(CONSTRUCTOR_INITIAL_PARAMETER, link(feature.name)));
			write(lineSeparator);
		}
		for(final Class<?> constructorException : constructorExceptions)
		{
			final ConstructorComment a = constructorException.getAnnotation(ConstructorComment.class);
			if(a==null)
				continue;

			writeIndent();
			write(" * @throws ");
			write(constructorException.getCanonicalName());
			write(' ');

			final StringSeparator comma = new StringSeparator(", ");
			final StringBuilder fields = new StringBuilder();
			for(final CopeFeature feature : initialFeatures)
			{
				if(!feature.getInitialExceptions().contains(constructorException))
					continue;

				comma.appendTo(fields);
				fields.append(feature.name);
			}

			final String pattern = a.value();
			write(format(pattern, fields.toString()));
			write(lineSeparator);
		}
		writeCommentFooter(CONSTRUCTOR_INITIAL_CUSTOMIZE);

		writeIndent();
		writeModifier(type.getInitialConstructorModifier());
		write(type.name);
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
			write(new Context(feature, feature.parent!=type).write(feature.getInitialType()));
			write(' ');
			write(feature.name);
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
			final CopeType parent = feature.parent;
			if(parent==type)
				write(type.name);
			else
				write(parent.javaClass.getFullName());
			write('.');
			write(feature.name);
			if(directSetValueMap)
				write(',');
			else
				write(".map(");
			write(feature.name);
			write("),");
			write(lineSeparator);
		}
		writeIndent(1);
		write("});");
		write(lineSeparator);
		writeIndent();
		write('}');
	}

	private void writeGenericConstructor(final CopeType type)
	{
		if(type.isBlock)
			return;

		final Option option = type.genericConstructorOption;
		if(!option.exists)
			return;

		writeCommentHeader();
		writeIndent();
		write(" * ");
		write(format(CONSTRUCTOR_GENERIC, type.name));
		write(lineSeparator);
		writeCommentFooter(CONSTRUCTOR_GENERIC_CUSTOMIZE);

		writeIndent();
		writeModifier(option.getModifier(type.getSubtypeModifier()));
		write(type.name);
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
	}

	private void writeActivationConstructor(final CopeType type)
	{
		if(type.isComposite)
			return;

		final Option option = type.activationConstructorOption;
		if(!option.exists)
			return;

		final boolean block = type.isBlock;
		final Class<?> constructor = block ? Block.class : Item.class;
		final String activation = (block ? BlockActivationParameters.class : ActivationParameters.class).getName();

		writeCommentHeader();
		writeIndent();
		write(" * ");
		write("Activation constructor. Used for internal purposes only.");
		write(lineSeparator);
		writeIndent();
		write(" * @see ");
		write(constructor.getName());
		write('#');
		write(constructor.getSimpleName());
		write('(');
		write(activation);
		write(')');
		write(lineSeparator);
		writeCommentFooter();

		writeIndent();
		if(suppressUnusedWarningOnPrivateActivationConstructor && !type.allowSubtypes())
			write("@SuppressWarnings(\"unused\") ");
		writeModifier(option.getModifier(type.getSubtypeModifier()));
		write(type.name);
		write('(');
		write(finalArgPrefix);
		write(activation);
		write(" ap){super(ap);");
		write(lineSeparator);
		write('}');
	}

	private void writeFeature(final CopeFeature feature)
	{
		final Object instance = feature.getInstance();
		final JavaClass javaClass = feature.getParent();
		for(final WrapperX wrapper : getWrappers(instance))
		{
			final String pattern = wrapper.getMethodWrapperPattern();
			final String modifierTag = wrapper.getOptionTagName()!=null ? wrapper.getOptionTagName() : pattern!=null ? format(pattern, "", "") : wrapper.getName();
			final Option option = new Option(Tags.getLine(feature.docComment, CopeFeature.TAG_PREFIX + modifierTag), true);

			if(!option.exists)
				continue;
			if(feature.parent.isBlock && wrapper.hasStaticClassToken())
				continue;

			final Context ctx = new Context(feature, wrapper);
			final String methodName = wrapper.getName();
			final java.lang.reflect.Type methodReturnType = wrapper.getReturnType();
			final List<WrapperX.Parameter> parameters = wrapper.getParameters();
			final Map<Class<? extends Throwable>, String[]> throwsClause = wrapper.getThrowsClause();
			final String featureNameCamelCase = toCamelCase(feature.name);
			final boolean isStatic = wrapper.isStatic();
			final int modifier = feature.modifier;
			final boolean useIs = instance instanceof BooleanField && methodName.startsWith("get");

			final Object[] arguments = new String[]{
					link(feature.name),
					feature.name,
					lowerCamelCase(feature.parent.name),
					featureNameCamelCase};
			{
				writeCommentHeader();
				writeCommentParagraph("", " ", wrapper.getCommentArray(), arguments);

				for(final WrapperX.Parameter parameter : wrapper.getParameters())
				{
					if(parameter.varargs==null)
					{
						writeCommentParagraph(
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
									link(parameterFeature.name),
									parameterFeature.name,
									lowerCamelCase(parameterFeature.parent.name)};
							writeCommentParagraph(
									"@param " + format(parameterName, parameterArguments),
									"        ",
									parameter.getComment(), parameterArguments);
						}
					}
				}
				writeCommentParagraph(
						"@return",
						"         ",
						wrapper.getReturnComment(), arguments);

				for(final Map.Entry<Class<? extends Throwable>, String[]> e : throwsClause.entrySet())
				{
					writeCommentParagraph(
							"@throws " + e.getKey().getCanonicalName(),
							"         ",
							e.getValue(), arguments);
				}
				writeCommentFooter(
					modifierTag!=null
					?  "It can be customized with the tag " +
						"<tt>@" + CopeFeature.TAG_PREFIX + modifierTag + ' ' +
						Option.TEXT_VISIBILITY_PUBLIC + '|' +
						Option.TEXT_VISIBILITY_PACKAGE + '|' +
						Option.TEXT_VISIBILITY_PROTECTED + '|' +
						Option.TEXT_VISIBILITY_PRIVATE + '|' +
						Option.TEXT_NONE + '|' +
						Option.TEXT_NON_FINAL +
						(useIs ? '|' + Option.TEXT_BOOLEAN_AS_IS : "") + "</tt> " +
						"in the comment of the field."
					: null);
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

			if(option.override && overrideOnSeparateLine)
			{
				writeEmptyAnnotationOnSeparateLine(Override.class);
			}

			writeIndent();

			if(option.override && !overrideOnSeparateLine)
			{
				writeAnnotation(Override.class);
				writeEmptyParenthesesForAnnotation();
				write(' ');
			}

			writeModifier(option.getModifier(modifier) | (isStatic ? STATIC : 0));
			write(ctx.write(methodReturnType));
			if(useIs && option.booleanAsIs)
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
							write(format(pattern, featureNameCamelCase, feature.name));
					}
					else
						write(format(pattern, featureNameCamelCase, feature.name));
				}
				else
				{
					if(feature.isDefault() && !isKeyword(methodName))
						write(methodName);
					else
						writeName(methodName, featureNameCamelCase);
				}
			}
			write(option.suffix);
			write('(');
			{
				final CharSeparator comma = new CharSeparator(',');
				for(final WrapperX.Parameter parameter : parameters)
				{
					if(parameter.varargs==null)
					{
						comma.appendTo(output);
						writeParameterNullability(parameter);
						write(finalArgPrefix);
						write(ctx.write(parameter.getType()));
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

							writeParameterNullability(parameter);
							write(finalArgPrefix);
							write(new Context(parameterFeature, false).write(parameterFeature.getInitialType()));
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
		if(feature.parent.isComposite)
		{
			write(methodName);
			write('(');
			{
				write(feature.parent.name);
				write('.');
				write(feature.name);
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
			final boolean block = feature.parent.isBlock;
			if(block)
				write("field().of(");
			write(feature.parent.name);
			write('.');
			write(feature.name);
			if(block)
				write(')');
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
						write(feature.parent.name);
						write(".class");
						if(feature.parent.javaClass.typeParameters>0)
							write("Wildcard.value");
					}
				}
				else
				{
					comma.appendTo(output);
					write(block ? "item()" : "this");
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

	private void writeCommentParagraph(
			final String prefix1, final String prefixN,
			final String[] lines,
			final Object[] arguments)
	{
		if(lines.length>0)
		{
			final String line = lines[0];
			writeIndent();
			write(" *");
			if(!prefix1.isEmpty())
			{
				write(' ');
				write(prefix1);
			}
			if(!line.isEmpty())
			{
				write(' ');
				write(format(line, arguments));
			}
			write(lineSeparator);
		}
		for(int i = 1; i<lines.length; i++)
		{
			final String line = lines[i];
			writeIndent();
			write(" *");
			if(!line.isEmpty())
			{
				write(prefixN);
				write(format(line, arguments));
			}
			write(lineSeparator);
		}
	}

	private void writeSerialVersionUID()
	{
		if(!serialVersionUID)
			return;

		writeCommentHeader();
		writeCommentFooter();

		writeIndent();
		writeModifier(PRIVATE|STATIC|FINAL);
		write("long serialVersionUID = 1");
		if(serialVersionUIDSuffix!=null)
			write(serialVersionUIDSuffix);
		write(';');
	}

	private void writeType(final CopeType type)
	{
		if(type.isComposite)
			return;

		final Option option = type.typeOption;
		if(!option.exists)
			return;

		final boolean block = type.isBlock;

		writeCommentHeader();
		writeIndent();
		write(" * ");
		write(format(
				block ? "The type information for {0}." : "The persistent type information for {0}.",
				lowerCamelCase(type.name)));
		write(lineSeparator);
		writeCommentFooter(
				"It can be customized with the tag " +
				"<tt>@" + CopeType.TAG_TYPE + ' ' +
				Option.TEXT_VISIBILITY_PUBLIC + '|' +
				Option.TEXT_VISIBILITY_PACKAGE + '|' +
				Option.TEXT_VISIBILITY_PROTECTED + '|' +
				Option.TEXT_VISIBILITY_PRIVATE + '|' +
				Option.TEXT_NONE +
				"</tt> " +
				"in the class comment.");

		if(hidingWarningSuppressor!=null && type.getSuperclass()!=null)
		{
			writeIndent();
			write("@SuppressWarnings(\"hiding\")");
			write(lineSeparator);
		}

		writeIndent();
		writeModifier(option.getModifier(type.javaClass.modifier) | (STATIC|FINAL));
		write((block ? BlockType.class : Type.class).getName());
		write('<');
		write(type.name);
		writeWildcard(type);
		write("> TYPE = ");
		write((block ? BlockType.class : TypesBound.class).getName());
		write(".newType(");
		write(type.name);
		write(".class");
		if(type.javaClass.typeParameters>0)
			write("Wildcard.value");
		write(");");
	}

	private void writeWildcard(final CopeType type)
	{
		final int typeParameters = type.javaClass.typeParameters;
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
			final CopeType type = CopeType.getCopeType(javaClass);
			final int classEndPosition = javaClass.getClassEndPositionInSourceWithoutGeneratedFragments();
			if(type!=null)
			{
				assert previousClassEndPosition<=classEndPosition;
				if(previousClassEndPosition<classEndPosition)
					output.append(buffer, previousClassEndPosition, classEndPosition);

				try
				{
					typeIndent = type.indent;
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

	private void writeClassFeatures(final CopeType type)
	{
		if(type.isInterface())
			return;

		writeInitialConstructor(type);
		writeGenericConstructor(type);

		for(final CopeFeature feature : type.getFeatures())
			writeFeature(feature);

		writeSerialVersionUID();
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
