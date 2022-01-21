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
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import com.exedio.cope.BooleanField;
import com.exedio.cope.SetValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class Generator
{
	private static final String CLASS     = Class   .class.getName();
	private static final String SET_VALUE = SetValue.class.getName();

	private static final String CONSTRUCTOR_INITIAL = "Creates a new {0} with all the fields initially needed.";
	private static final String CONSTRUCTOR_INITIAL_PARAMETER = "the initial value for field {0}.";
	private static final String CONSTRUCTOR_INITIAL_CUSTOMIZE = hintCustomize(WrapperType.class, "constructor", "...") + " and @" + WrapperInitial.class.getSimpleName();
	private static final String CONSTRUCTOR_GENERIC = "Creates a new {0} and sets the given fields initially.";
	private static final String CONSTRUCTOR_GENERIC_CUSTOMIZE = hintCustomize(WrapperType.class, "genericConstructor", "...");
	private static final String WILDCARD_CUSTOMIZE = hintCustomize(WrapperType.class, "wildcardClass", "...");
	private static final String TYPE_CUSTOMIZE = hintCustomize(WrapperType.class, "type", "...");

	private static String hintCustomize(final Class<? extends Annotation> annotation, final String annotationMember, final String value)
	{
		return "customize with @"+annotation.getSimpleName()+"("+annotationMember+"="+value+")";
	}

	private final JavaFile javaFile;
	private final StringBuilder output;
	private final String lineSeparator;
	private final boolean nullabilityAnnotations;
	private final String serialVersionUIDSuffix;
	private final boolean directSetValueMap;
	private final boolean finalMethodInFinalClass;
	private final boolean useConstantForEmptySetValuesArray;
	private final Set<Method> generateDeprecateds;
	private final Set<Method> disabledWraps;
	private final Params.Suppressor suppressWarningsType;
	private final Params.Suppressor suppressWarningsConstructor;
	private final Params.Suppressor suppressWarningsWrapper;

	Generator(final JavaFile javaFile, final StringBuilder output, final Params params, final Set<Method> generateDeprecateds, final Set<Method> disabledWraps)
	{
		this.javaFile = javaFile;
		this.output = output;
		this.lineSeparator = System.lineSeparator();
		this.nullabilityAnnotations = params.nullabilityAnnotations;
		this.serialVersionUIDSuffix = params.serialVersionUIDSuffix.code;
		this.directSetValueMap = params.directSetValueMap;
		this.finalMethodInFinalClass = params.finalMethodInFinalClass;
		this.useConstantForEmptySetValuesArray = params.useConstantForEmptySetValuesArray;
		//noinspection AssignmentToCollectionOrArrayFieldFromParameter
		this.generateDeprecateds = generateDeprecateds;
		//noinspection AssignmentToCollectionOrArrayFieldFromParameter
		this.disabledWraps = disabledWraps;
		this.suppressWarningsType        = params.suppressWarningsType;
		this.suppressWarningsConstructor = params.suppressWarningsConstructor;
		this.suppressWarningsWrapper     = params.suppressWarningsWrapper;
	}

	private static String toCamelCase(final String name)
	{
		final char first = name.charAt(0);
		if (Character.isUpperCase(first))
			return name;
		else
			return Character.toUpperCase(first) + name.substring(1);
	}

	private static String lowerCamelCase(final String s)
	{
		final char first = s.charAt(0);
		if(Character.isLowerCase(first))
			return s;
		else
			return Character.toLowerCase(first) + s.substring(1);
	}

	private void writeThrowsClause(final Collection<Class<? extends Throwable>> exceptions)
	{
		if(exceptions.isEmpty())
			return;

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

	private void writeComment(final List<String> commentLines)
	{
		write(lineSeparator);
		if(!typeContext.comments || commentLines.isEmpty())
			return;

		writeIndent();
		write("/**");
		write(lineSeparator);

		for (final String commentLine: commentLines)
		{
			writeIndent();
			write(" *");
			if (!commentLine.isEmpty())
				write(' ');

			write(commentLine);
			write(lineSeparator);
		}
		writeIndent();
		write(" */");
		write(lineSeparator);
	}

	private void writeGeneratedAnnotation(
			final String extraCommentForAnnotations)
	{
		writeIndent();
		writeAnnotation(Generated.class);
		if(typeContext.comments && extraCommentForAnnotations!=null)
		{
			write(" // ");
			write(extraCommentForAnnotations);
		}
		write(lineSeparator);
	}

	private void writeSuppressWarnings(
			final Params.Suppressor byParams,
			final String[] byAnnotation)
	{
		final SortedSet<String> byParamsSet = byParams.get();
		final SortedSet<String> set;
		if(byAnnotation.length==0)
			set = byParamsSet;
		else
		{
			set = new TreeSet<>(byParamsSet);
			set.addAll(asList(byAnnotation));
		}
		if(set.isEmpty())
			return;

		writeIndent();
		writeAnnotation(SuppressWarnings.class);
		write('(');
		final boolean multi = set.size()>1;
		if(multi)
			write('{');
		final CharSeparator comma = new CharSeparator(',');
		for(final String w : set)
		{
			comma.appendTo(output);
			write('"');
			write(w);
			write('"');
		}
		if(multi)
			write('}');
		write(')');
		write(lineSeparator);
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
		commentLines.add(format(CONSTRUCTOR_INITIAL, type.getName()));
		for(final CopeFeature feature : initialFeatures)
		{
			commentLines.add("@param " + feature.getName() + ' ' + format(CONSTRUCTOR_INITIAL_PARAMETER, feature.getJavadocReference()));
		}
		for(final Class<? extends Throwable> constructorException : constructorExceptions)
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
			commentLines.add("@throws " + constructorException.getCanonicalName() + ' ' + format(pattern, fields.toString()));
		}
		writeComment(commentLines);
		writeGeneratedAnnotation(CONSTRUCTOR_INITIAL_CUSTOMIZE);
		writeSuppressWarnings(suppressWarningsConstructor, type.getOption().constructorSuppressWarnings());

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
			write("final ");
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
		if (useConstantForEmptySetValuesArray && initialFeatures.isEmpty())
		{
			write("this(" + SET_VALUE + ".EMPTY_ARRAY);");
		}
		else
		{
			write("this(new " + SET_VALUE + "<?>[]{");
			write(lineSeparator);
			for(final CopeFeature feature : initialFeatures)
			{
				writeIndent(2);
				if(directSetValueMap)
					write(SET_VALUE + ".map(");
				final CopeType<?> parent = feature.parent;
				if(parent == type)
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
		}
		write(lineSeparator);
		writeIndent();
		write('}');
		write(lineSeparator);
	}

	private void writeGenericConstructor(final LocalCopeType type)
	{
		if(!type.kind.hasGenericConstructor)
			return;

		final Visibility visibility = type.getOption().genericConstructor();
		if(!visibility.exists())
			return;

		writeComment(singletonList(format(CONSTRUCTOR_GENERIC, type.getName())));
		writeGeneratedAnnotation(CONSTRUCTOR_GENERIC_CUSTOMIZE);

		writeIndent();
		writeModifier(visibility.getModifier(type.getSubtypeModifier()));
		write(type.getName());
		write("(final " + SET_VALUE + "<?>... setValues){super(setValues);}");
		write(lineSeparator);
	}

	private void writeActivationConstructor(final LocalCopeType type)
	{
		final String activation = type.kind.activationConstructor;
		if(activation==null)
			return;

		final Visibility visibility = type.getOption().activationConstructor();
		if(!visibility.exists())
			return;

		writeComment(
				asList(
					"Activation constructor. Used for internal purposes only.",
					"@see " + type.kind.top + '#' + type.kind.topSimple + '(' + activation + ')')
		);
		writeGeneratedAnnotation(null);

		writeIndent();
		writeModifier(visibility.getModifier(type.getSubtypeModifier()));
		write(type.getName());
		write("(final ");
		write(activation);
		write(" ap){super(ap);}");
		write(lineSeparator);
	}

	@SuppressWarnings({"ConstantConditions", "RedundantSuppression"}) // just happens sporadically
	private void writeFeature(final LocalCopeFeature feature)
	{
		final Object instance = feature.getInstance();
		if (instance==null) throw new RuntimeException("instance==null for "+feature);
		final Kind kind = feature.parent.kind;
		for(final WrapperX wrapper : getWrappers(instance))
		{
			if (wrapper.isMethodDeprecated() && !generateDeprecateds.contains(wrapper.method))
				continue;
			if (disabledWraps.contains(wrapper.method))
				continue;
			final String pattern = wrapper.getMethodWrapperPattern();
			final String modifierTag = wrapper.getOptionTagName()!=null ? wrapper.getOptionTagName() : pattern!=null ? format(pattern, "", "") : wrapper.name;
			final List<WrapperX.Parameter> parameters = wrapper.getParameters();
			final Wrapper option = feature.getOption(modifierTag, getRawTypes(parameters));

			final Visibility visibility = option.visibility();
			if(!visibility.exists())
				continue;
			if(!kind.allowStaticClassToken && wrapper.hasStaticClassToken())
				continue;

			final Context ctx = new Context(feature, wrapper);
			final String methodName = wrapper.name;
			final java.lang.reflect.Type methodReturnType = wrapper.getReturnType();
			final Map<Class<? extends Throwable>, String[]> throwsClause = wrapper.getThrowsClause();
			final String featureNameCamelCase = toCamelCase(feature.getName());
			final boolean isStatic = wrapper.isStatic();
			final boolean internal = option.internal();
			final boolean override = option.override();
			final String[] customAnnotations = option.annotate();
			final boolean useIs = instance instanceof BooleanField && methodName.startsWith("get");

			final Object[] arguments = new String[]{
					feature.getJavadocReference(),
					feature.getName(),
					lowerCamelCase(feature.parent.getName()),
					featureNameCamelCase};
			{
				final List<String> commentLines=new ArrayList<>();
				collectCommentParagraph(commentLines, "", "", wrapper.getCommentArray(), arguments);

				for(final WrapperX.Parameter parameter : parameters)
				{
					if(parameter.varargs==null)
					{
						collectCommentParagraph(
								commentLines,
								"@param " + format(parameter.name, arguments),
								"       ",
								parameter.getComment(), arguments);
					}
					else
					{
						for(final Object parameterInstance : parameter.varargs)
						{
							final CopeFeature parameterFeature=feature.getFeatureByInstance(parameterInstance, methodName);
							final String parameterName = parameterFeature.getName();

							final Object[] parameterArguments = new String[]{
									parameterFeature.getJavadocReference(),
									parameterFeature.getName(),
									lowerCamelCase(parameterFeature.parent.getName())};
							collectCommentParagraph(
									commentLines,
									"@param " + format(parameterName, parameterArguments),
									"       ",
									parameter.getComment(), parameterArguments);
						}
					}
				}
				collectCommentParagraph(
						commentLines,
						"@return",
						"        ",
						wrapper.getReturnComment(), arguments);

				for(final Map.Entry<Class<? extends Throwable>, String[]> e : throwsClause.entrySet())
				{
					collectCommentParagraph(
							commentLines,
							"@throws " + e.getKey().getCanonicalName(),
							"        ",
							e.getValue(), arguments);
				}
				writeComment(commentLines);
				writeGeneratedAnnotation(
					modifierTag!=null
					? hintCustomize(Wrapper.class, "wrap", "\"" + modifierTag + "\"")
					: null);
				writeSuppressWarnings(suppressWarningsWrapper, option.suppressWarnings());
			}

			if(wrapper.isMethodDeprecated() || feature.isDeprecated())
			{
				writeEmptyAnnotationOnSeparateLine(Deprecated.class);
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

			if(override)
			{
				writeEmptyAnnotationOnSeparateLine(Override.class);
			}
			for (final String customAnnotation: customAnnotations)
			{
				writeIndent();
				write(customAnnotation);
				write(lineSeparator);
			}

			writeIndent();

			final int visibilityModifier =
					visibility.getModifier(
							internal && visibility.isDefault()
							? PRIVATE
							: feature.getModifier()
					);
			writeModifier(
					visibilityModifier |
					(isStatic ? STATIC : 0) |
					(option.asFinal()
							&& visibilityModifier!=PRIVATE
							&& (!feature.parent.isFinal()||finalMethodInFinalClass) ? FINAL : 0));
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
					final boolean makeVarargs = wrapper.isVarArgs() && !iter.hasNext();
					if(parameter.varargs==null)
					{
						comma.appendTo(output);
						writeParameterNullability(parameter);
						write("final ");
						write(ctx.write(parameter.genericType, makeVarargs));
						write(' ');
						write(format(parameter.name, arguments));
					}
					else
					{
						for(final Object parameterInstance : parameter.varargs)
						{
							comma.appendTo(output);
							final CopeFeature parameterFeature=feature.getFeatureByInstance(parameterInstance, methodName);

							if (!parameterFeature.isInitialTypePrimitive())
							{
								writeParameterNullability(parameter);
							}
							write("final ");
							write(new Context(parameterFeature, false).write(parameterFeature.getInitialType(), makeVarargs));
							write(' ');
							write(format(parameterFeature.getName(), arguments));
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
		//noinspection IfStatementWithIdenticalBranches keep opening and closing parenthesis together
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
					write(format(parameter.name, arguments));
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
						writeClass(feature.parent);
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
						write(format(parameter.name, arguments));
					}
					else
					{
						for(final Object parameterInstance : parameter.varargs)
						{
							comma.appendTo(output);
							write(format(feature.getFeatureByInstance(parameterInstance, methodName).getName(), arguments));
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
		if(!nullabilityAnnotations)
			return;

		if(parameter.isNonnull())
		{
			writeAnnotation(Nonnull.class);
			write(' ');
		}
		if(parameter.isNullable())
		{
			writeAnnotation(Nullable.class);
			write(' ');
		}
	}

	private void writeEmptyAnnotationOnSeparateLine(final Class<? extends Annotation> annotationClass)
	{
		writeIndent();
		writeAnnotation(annotationClass);
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
				: Collections.emptyList(),
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
			commentLines.add(prefix1 + (line.isEmpty()?"":((prefix1.isEmpty()?"":" ")+format(line, arguments))));
		}
		for(int i = 1; i<lines.length; i++)
		{
			final String line = lines[i];
			commentLines.add(line.isEmpty()?"":(prefixN+format(line, arguments)));
		}
	}

	private void writeSerialVersionUID(final LocalCopeType type)
	{
		write(lineSeparator);
		writeGeneratedAnnotation(null);

		writeIndent();
		writeModifier(PRIVATE|STATIC|FINAL);
		write("long serialVersionUID = ");
		// When an existing item type is made abstract, it will no longer be possible
		// to de-serialize serialized instances. Therefore, abstract item classes get
		// a different serialVersionUID.
		write( Modifier.isAbstract(type.getModifier()) ? '2' : '1' );
		if(serialVersionUIDSuffix!=null)
			write(serialVersionUIDSuffix);
		write(';');
		write(lineSeparator);
	}

	/**
	 * Classes of non-toplevel types must override this constant
	 * for working around https://bugs.java.com/view_bug.do?bug_id=7101374
	 */
	private void writeWildcardClass(final LocalCopeType type)
	{
		if(type.getTypeParameters()==0)
			return;

		final Visibility visibility = type.getOption().wildcardClass();
		if(!visibility.exists())
			return;

		writeComment(singletonList(
				format("Use {0}.classWildcard.value instead of {0}.class to avoid rawtypes warnings.", type.getName())
		));
		writeGeneratedAnnotation(WILDCARD_CUSTOMIZE);
		writeIndent();
		final int modifier = visibility.getModifier(type.getModifier());
		writeModifier(modifier | (STATIC | FINAL));
		write("class classWildcard { public static final " + CLASS + "<");
		write(type.getName());
		writeWildcard(type);
		write("> value = ");
		write(type.kind.wildcardClassCaster);
		write(".cast(");
		write(type.getName());
		write(".class); ");
		if(modifier!=PRIVATE) // otherwise the default constructor is private already
			write("private classWildcard(){} ");
		write('}');
		write(lineSeparator);
	}

	private void writeType(final LocalCopeType type)
	{
		final Kind kind = type.kind;
		if(kind.typeField==null)
			return;

		final WrapperType option = type.getOption();
		final Visibility visibility = option.type();
		if(!visibility.exists())
			return;

		writeComment(singletonList(format(kind.typeDoc, lowerCamelCase(type.getName()))));
		writeGeneratedAnnotation(TYPE_CUSTOMIZE);
		writeSuppressWarnings(suppressWarningsType, option.typeSuppressWarnings());
		writeIndent();
		writeModifier(visibility.getModifier(type.getModifier()) | (STATIC|FINAL));
		write(kind.typeField);
		write('<');
		write(type.getName());
		writeWildcard(type);
		write("> TYPE = ");
		write(kind.typeFactory);
		write("." + Kind.TYPE_FACTORY_METHOD + "(");
		writeClass(type);
		write(");");
		write(lineSeparator);
	}

	private void writeWildcard(final LocalCopeType type)
	{
		final int typeParameters = type.getTypeParameters();
		if(typeParameters==0)
			return;

		write("<?");
		for(int i = 1; i<typeParameters; i++)
			write(",?");
		write('>');
	}

	private void writeClass(final CopeType<?> type)
	{
		final boolean wildcard = type.getTypeParameters()>0;
		if(!wildcard)
		{
			write(type.getName());
			write('.');
		}
		write("class");
		if(wildcard)
			write("Wildcard.value");
	}

	private TypeContext typeContext = null;

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
					if(typeContext!=null)
						throw new RuntimeException(type.getName());
					typeContext = new TypeContext(type.getOption());
					writeClassFeatures(type);
					typeContext = null;
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
		writeWildcardClass(type);
		writeType(type);
		writeActivationConstructor(type);
	}

	private void writeModifier(final int modifier)
	{
		final String modifierString = Modifier.toString(modifier);
		if(modifierString.isEmpty())
			return;

		write(modifierString);
		write(' ');
	}

	private static boolean isKeyword(final String s)
	{
		return "for".equals(s); // TODO
	}

	private void writeIndent()
	{
		writeIndent(0);
	}

	private void writeIndent(final int additionalLevel)
	{
		write(typeContext.indent[additionalLevel]);
	}

	private void write(final String s)
	{
		output.append(s);
	}

	private void write(final char c)
	{
		output.append(c);
	}

	private static Class<?>[] getRawTypes(final List<WrapperX.Parameter> parameters)
	{
		final Class<?>[] result = new Class<?>[parameters.size()];
		Arrays.setAll(result, i -> parameters.get(i).rawType);
		return result;
	}

	private static final class TypeContext
	{
		final String[] indent = new String[4];
		final boolean comments;

		TypeContext(final WrapperType option)
		{
			final int level = option.indent();
			final StringBuilder bf = new StringBuilder();
			for(int i = 0; i<level; i++)
				bf.append('\t');
			for(int i = 0; i<indent.length; i++)
			{
				indent[i] = bf.toString();
				bf.append('\t');
			}

			this.comments = option.comments();
		}
	}
}
