/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.RangeViolationException;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.util.ReactivationConstructorDummy;

final class Generator
{
	private static final String COLLECTION = Collection.class.getName();
	private static final String LIST = List.class.getName();
	private static final String SET_VALUE = SetValue.class.getName();
	private static final String ITEM = Item.class.getName();
	private static final String TYPE_NAME = Type.class.getName();
	private static final String REACTIVATION = ReactivationConstructorDummy.class.getName();
	
	private static final String THROWS_MANDATORY = "if {0} is null.";
	private static final String THROWS_UNIQUE    = "if {0} is not unique.";
	private static final String THROWS_RANGE     = "if {0} violates its range constraint.";
	private static final String THROWS_LENGTH    = "if {0} violates its length constraint.";
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
	private static final String CONSTRUCTOR_GENERIC_CALLED = "This constructor is called by {0}.";
	private static final String CONSTRUCTOR_GENERIC_CUSTOMIZE = "It can be customized with the tag " +
																					"<tt>@" + CopeType.TAG_GENERIC_CONSTRUCTOR + ' ' +
																					Option.TEXT_VISIBILITY_PUBLIC + '|' +
																					Option.TEXT_VISIBILITY_PACKAGE + '|' +
																					Option.TEXT_VISIBILITY_PROTECTED + '|' +
																					Option.TEXT_VISIBILITY_PRIVATE + '|' +
																					Option.TEXT_NONE +
																					"</tt> " +
																					"in the class comment.";
	private static final String CONSTRUCTOR_REACTIVATION = "Reactivation constructor. Used for internal purposes only.";
	private static final String FINDER_UNIQUE = "Finds a {0} by it''s unique fields.";
	private static final String FINDER_UNIQUE_PARAMETER = "shall be equal to field {0}.";
	private static final String FINDER_UNIQUE_RETURN = "null if there is no matching item.";
	private static final String TYPE = "The persistent type information for {0}.";
	private static final String TYPE_CUSTOMIZE = "It can be customized with the tag " +
																"<tt>@" + CopeType.TAG_TYPE + ' ' +
																Option.TEXT_VISIBILITY_PUBLIC + '|' +
																Option.TEXT_VISIBILITY_PACKAGE + '|' +
																Option.TEXT_VISIBILITY_PROTECTED + '|' +
																Option.TEXT_VISIBILITY_PRIVATE + '|' +
																Option.TEXT_NONE +
																"</tt> " +
																"in the class comment.";
	private static final String GENERATED = "This feature has been generated by the cope instrumentor and will be overwritten by the build process.";

	/**
	 * All generated class features get this doccomment tag.
	 */
	static final String TAG_GENERATED = CopeFeature.TAG_PREFIX + "generated";
	

	private final JavaFile javaFile;
	private final Writer o;
	private final CRC32 outputCRC = new CRC32();
	private final String lineSeparator;
	private final boolean longJavadoc;
	private final String finalArgPrefix;
	private final boolean serialVersionUID;
	private final boolean skipDeprecated;
	
	
	Generator(final JavaFile javaFile, final ByteArrayOutputStream outputStream, final Params params)
	{
		this.javaFile = javaFile;
		this.o = new OutputStreamWriter(new CheckedOutputStream(outputStream, outputCRC));
		
		final String systemLineSeparator = System.getProperty("line.separator");
		if(systemLineSeparator==null)
		{
			System.out.println("warning: property \"line.separator\" is null, using LF (unix style).");
			lineSeparator = "\n";
		}
		else
			lineSeparator = systemLineSeparator;
		
		this.longJavadoc = params.longJavadoc;
		this.finalArgPrefix = params.finalArgs ? "final " : "";
		this.serialVersionUID = params.serialVersionUID;
		this.skipDeprecated = !params.createDeprecated;
	}
	
	void close() throws IOException
	{
		if(o!=null)
			o.close();
	}
	
	long getCRC()
	{
		return outputCRC.getValue();
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
	throws IOException
	{
		if(!exceptions.isEmpty())
		{
			o.write("\t\t\tthrows");
			boolean first = true;
			for(final Class e : exceptions)
			{
				if(first)
					first = false;
				else
					o.write(',');
				o.write(lineSeparator);
				o.write("\t\t\t\t");
				o.write(e.getName());
			}
			o.write(lineSeparator);
		}
	}

	private void writeCommentHeader()
	throws IOException
	{
		o.write("/**");
		o.write(lineSeparator);
		if(longJavadoc)
		{
			o.write(lineSeparator);
			o.write("\t **");
			o.write(lineSeparator);
		}
	}

	private void writeCommentFooter()
	throws IOException
	{
		writeCommentFooter(null);
	}
	
	private void writeCommentFooter(final String extraComment)
	throws IOException
	{
		o.write("\t * @" + TAG_GENERATED + ' ');
		o.write(GENERATED);
		o.write(lineSeparator);
		if(extraComment!=null)
		{
			o.write("\t *       ");
			o.write(extraComment);
			o.write(lineSeparator);
		}
		o.write("\t */");
		o.write(lineSeparator);
	}
	
	private static final String link(final String target)
	{
		return "{@link #" + target + '}';
	}
	
	private static final String format(final String pattern, final Object... arguments)
	{
		return MessageFormat.format(pattern, arguments);
	}

	private void writeInitialConstructor(final CopeType type)
	throws IOException
	{
		if(!type.hasInitialConstructor())
			return;

		final List<CopeFeature> initialFeatures = type.getInitialFeatures();
		final SortedSet<Class<? extends Throwable>> constructorExceptions = type.getConstructorExceptions();
		
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(CONSTRUCTOR_INITIAL, type.name));
		o.write(lineSeparator);
		for(final CopeFeature feature : initialFeatures)
		{
			o.write("\t * @param ");
			o.write(feature.name);
			o.write(' ');
			o.write(format(CONSTRUCTOR_INITIAL_PARAMETER, link(feature.name)));
			o.write(lineSeparator);
		}
		for(final Class constructorException : constructorExceptions)
		{
			o.write("\t * @throws ");
			o.write(constructorException.getName());
			o.write(' ');

			boolean first = true;
			final StringBuilder initialAttributesBuf = new StringBuilder();
			for(final CopeFeature feature : initialFeatures)
			{
				if(!feature.getSetterExceptions().contains(constructorException))
					continue;

				if(first)
					first = false;
				else
					initialAttributesBuf.append(", ");
				initialAttributesBuf.append(feature.name);
			}

			final String pattern;
			if(MandatoryViolationException.class.equals(constructorException))
				pattern = THROWS_MANDATORY;
			else if(UniqueViolationException.class.equals(constructorException))
				pattern = THROWS_UNIQUE;
			else if(RangeViolationException.class.equals(constructorException))
				pattern = THROWS_RANGE;
			else if(LengthViolationException.class.equals(constructorException))
				pattern = THROWS_LENGTH;
			else
				throw new RuntimeException(constructorException.getName());

			o.write(format(pattern, initialAttributesBuf.toString()));
			o.write(lineSeparator);
		}
		writeCommentFooter(CONSTRUCTOR_INITIAL_CUSTOMIZE);
		
		o.write('\t');
		writeModifier(type.getInitialConstructorModifier());
		o.write(type.name);
		o.write('(');
		
		boolean first = true;
		for(final CopeFeature feature : initialFeatures)
		{
			if(first)
				first = false;
			else
				o.write(',');
			
			o.write(lineSeparator);
			o.write("\t\t\t\t");
			o.write(finalArgPrefix);
			o.write(toString(((Settable<?>)feature.getInstance()).getWrapperSetterType(), feature));
			o.write(' ');
			o.write(feature.name);
		}
		
		o.write(')');
		o.write(lineSeparator);
		writeThrowsClause(constructorExceptions);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tthis(new " + SET_VALUE + "[]{");
		o.write(lineSeparator);
		for(final CopeFeature feature : initialFeatures)
		{
			o.write("\t\t\t");
			o.write(type.name);
			o.write('.');
			o.write(feature.name);
			o.write(".map(");
			o.write(feature.name);
			o.write("),");
			o.write(lineSeparator);
		}
		o.write("\t\t});");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeGenericConstructor(final CopeType type)
	throws IOException
	{
		final Option option = type.genericConstructorOption;
		if(!option.exists)
			return;

		writeCommentHeader();
		o.write("\t * ");
		o.write(format(CONSTRUCTOR_GENERIC, type.name));
		o.write(lineSeparator);
		o.write("\t * ");
		o.write(format(CONSTRUCTOR_GENERIC_CALLED, "{@link " + TYPE_NAME + "#newItem Type.newItem}"));
		o.write(lineSeparator);
		writeCommentFooter(CONSTRUCTOR_GENERIC_CUSTOMIZE);
		
		o.write('\t');
		writeModifier(option.getModifier(type.allowSubTypes() ? PROTECTED : PRIVATE));
		o.write(type.name);
		o.write('(');
		o.write(finalArgPrefix);
		o.write(SET_VALUE + "... setValues)");
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tsuper(setValues);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeReactivationConstructor(final CopeType type)
	throws IOException
	{
		final Option option = type.reactivationConstructorOption;
		if(!option.exists)
			return;

		writeCommentHeader();
		o.write("\t * ");
		o.write(CONSTRUCTOR_REACTIVATION);
		o.write(lineSeparator);
		o.write("\t * @see " + ITEM + "#Item(" + REACTIVATION + ",int)");
		o.write(lineSeparator);
		writeCommentFooter();
		
		o.write('\t');
		if(!type.allowSubTypes())
			o.write("@SuppressWarnings(\"unused\") ");
		writeModifier(option.getModifier(type.allowSubTypes() ? PROTECTED : PRIVATE));
		o.write(type.name);
		o.write('(');
		o.write(finalArgPrefix);
		o.write(REACTIVATION + " d,");
		o.write(finalArgPrefix);
		o.write("int pk)");
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tsuper(d,pk);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeFeature(final CopeFeature feature)
	throws InjectorParseException, IOException
	{
		final Feature instance = feature.getInstance();
		for(final Wrapper wrapper : instance.getWrappers())
		{
			final boolean deprecated = wrapper.isDeprecated();
			if(deprecated && skipDeprecated)
				continue;
			
			final String modifierTag = wrapper.getModifier();
			final Option option =
				modifierTag!=null
				? new Option(Injector.findDocTagLine(
									feature.docComment,
									CopeFeature.TAG_PREFIX + modifierTag),
						true)
				: null;
			
			if(option!=null && !option.exists)
				continue;

			final String methodName = wrapper.getName();
			final java.lang.reflect.Type methodReturnType = wrapper.getReturnType();
			final List<java.lang.reflect.Type> parameterTypes = wrapper.getParameterTypes();
			final List<String> parameterNames = wrapper.getParameterNames(); 
			final String featureNameCamelCase = toCamelCase(feature.name);
			final boolean isStatic = wrapper.isStatic();
			final int modifier = feature.modifier;
			final boolean useIs = instance instanceof BooleanField && methodName.startsWith("get");
			
			final Object[] arguments = new String[]{
					link(feature.name),
					feature.name,
					lowerCamelCase(feature.parent.name)};
			{
				writeCommentHeader();
				o.write("\t * ");
				o.write(format(wrapper.getComment(), arguments));
				o.write(lineSeparator);
				if(deprecated)
				{
					o.write("\t * @deprecated ");
					o.write(format(wrapper.getDeprecationComment(), arguments));
					o.write(lineSeparator);
				}
				for(final String comment : wrapper.getComments())
				{
					o.write("\t * ");
					o.write(format(comment, arguments));
					o.write(lineSeparator);
				}
				{
					final Iterator<String> parameterNameIter = parameterNames.iterator();
					for(final String comment : wrapper.getParameterComments())
					{
						final String name = parameterNameIter.next();
						if(comment!=null)
						{
							o.write("\t * @param ");
							o.write(format(name, arguments)); // TODO reuse
							o.write(' ');
							o.write(format(comment, arguments));
							o.write(lineSeparator);
						}
					}
				}
				{
					final String comment = wrapper.getReturnComment();
					if(comment!=null)
					{
						o.write("\t * @return ");
						o.write(format(comment, arguments));
						o.write(lineSeparator);
					}
				}
				{
					// TODO reuse
					final TreeMap<Class<? extends Throwable>, String> exceptions = new TreeMap<Class<? extends Throwable>, String>(CopeType.CLASS_COMPARATOR);
					exceptions.putAll(wrapper.getThrowsClause());
					for(final Map.Entry<Class<? extends Throwable>, String> e : exceptions.entrySet())
					{
						final String comment = e.getValue();
						if(comment!=null)
						{
							o.write("\t * @throws ");
							o.write(e.getKey().getName());
							o.write(' ');
							o.write(format(comment, arguments));
							o.write(lineSeparator);
						}
					}
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
			
			if(deprecated)
			{
				o.write('\t');
				o.write("@Deprecated");
				o.write(lineSeparator);
			}
			
			o.write('\t');
			writeModifier(
				(
					option!=null
					? option.getModifier(modifier)
					: ((modifier & (PUBLIC|PROTECTED|PRIVATE)) | FINAL)
				)
				|
				(isStatic ? STATIC : 0)
			);
			o.write(toString(methodReturnType, feature));
			if(option!=null && useIs && option.booleanAsIs)
			{
				o.write(" is");
				o.write(featureNameCamelCase);
			}
			else
			{
				o.write(' ');
				final String pattern = wrapper.getMethodWrapperPattern();
				if(pattern!=null)
					o.write(MessageFormat.format(pattern, featureNameCamelCase, feature.name));
				else
					writeName(methodName, featureNameCamelCase);
			}
			if(option!=null)
				o.write(option.suffix);
			o.write('(');
			boolean first = true;
			final Iterator<String> parameterNameIter = parameterNames.iterator();
			for(final java.lang.reflect.Type parameter : parameterTypes)
			{
				if(first)
					first = false;
				else
					o.write(',');
				
				o.write(finalArgPrefix);
				o.write(toString(parameter, feature));
				o.write(' ');
				final String name = parameterNameIter.next();
				o.write(format(name, arguments));
			}
			o.write(')');
			o.write(lineSeparator);
			{
				final TreeMap<Class<? extends Throwable>, String> exceptions = new TreeMap<Class<? extends Throwable>, String>(CopeType.CLASS_COMPARATOR);
				exceptions.putAll(wrapper.getThrowsClause());
				writeThrowsClause(exceptions.keySet());
			}
			o.write("\t{");
			o.write(lineSeparator);
			o.write("\t\t");
			if(!methodReturnType.equals(void.class))
				o.write("return ");
			o.write(feature.parent.name);
			o.write('.');
			o.write(feature.name);
			o.write('.');
			o.write(methodName);
			o.write('(');
			first = true;
			if(isStatic)
			{
				if(first)
					first = false;
				else
					o.write(',');
				
				o.write(feature.parent.name);
				o.write(".class");
			}
			else
			{
				first = false;
				o.write("this");
			}
			for(final String name : parameterNames)
			{
				if(first)
					first = false;
				else
					o.write(',');
				
				o.write(format(name, arguments));
			}
			o.write(')');
			o.write(';');
			o.write(lineSeparator);
			o.write("\t}");
		}
	}
	
	private void writeName(final String methodName, final String featureName) throws IOException
	{
		for(int i = 0; i<methodName.length(); i++)
			if(Character.isUpperCase(methodName.charAt(i)))
			{
				o.write(methodName.substring(0, i));
				o.write(featureName);
				o.write(methodName.substring(i));
				return;
			}
		
		o.write(methodName);
		o.write(featureName);
	}
	
	private static final String toString(final Class c, final CopeFeature feature)
	{
		if(Wrapper.ClassVariable.class.equals(c))
			return feature.parent.name;
		else if(Wrapper.TypeVariable0.class.equals(c))
			return toStringType(feature, 0);
		else if(Wrapper.TypeVariable1.class.equals(c))
			return toStringType(feature, 1);
		else if(Wrapper.DynamicModelType.class.equals(c))
			return "com.exedio.cope.pattern.DynamicModel<" + toStringType(feature, 0) + ">.Type";
		else if(Wrapper.DynamicModelField.class.equals(c))
			return "com.exedio.cope.pattern.DynamicModel<" + toStringType(feature, 0) + ">.Field";
		else
			return c.getCanonicalName();
	}
	
	private static final String toStringType(final CopeFeature feature, final int number)
	{
		return Injector.getGenerics(feature.javaAttribute.type).get(number);
	}
	
	private static final String toString(final ParameterizedType t, final CopeFeature feature)
	{
		final StringBuilder bf = new StringBuilder(toString(t.getRawType(), feature));
		bf.append('<');
		boolean first = true;
		for(final java.lang.reflect.Type a : t.getActualTypeArguments())
		{
			bf.append(toString(a, feature));
			
			if(first)
				first = false;
			else
				bf.append(',');
		}
		bf.append('>');
		
		return bf.toString();
	}
	
	private static final String toString(final Wrapper.ExtendsType t, final CopeFeature feature)
	{
		final StringBuilder bf = new StringBuilder(toString(t.getRawType(), feature));
		bf.append('<');
		boolean first = true;
		for(final java.lang.reflect.Type a : t.getActualTypeArguments())
		{
			bf.append("? extends ");
			bf.append(toString(a, feature));
			
			if(first)
				first = false;
			else
				bf.append(',');
		}
		bf.append('>');
		
		return bf.toString();
	}
	
	private static final String toString(final java.lang.reflect.Type t, final CopeFeature feature)
	{
		if(t instanceof Class)
			return toString((Class)t, feature);
		else if(t instanceof ParameterizedType)
			return toString((ParameterizedType)t, feature);
		else if(t instanceof Wrapper.ExtendsType)
			return toString((Wrapper.ExtendsType)t, feature);
		else
			throw new RuntimeException(t.toString());
	}
	
	private void writeUniqueFinder(final CopeUniqueConstraint constraint, final boolean deprecated)
	throws IOException, InjectorParseException
	{
		final Option option = new Option(
				Injector.findDocTagLine(constraint.docComment, CopeFeature.TAG_PREFIX + "finder"), true);
		if(option!=null && !option.exists)
			return;
		
		final CopeAttribute[] attributes = constraint.getAttributes();
		final String className = attributes[0].getParent().name;
		
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(FINDER_UNIQUE, lowerCamelCase(className)));
		o.write(lineSeparator);
		if(deprecated)
		{
			o.write("\t * @deprecated use for");
			o.write(toCamelCase(constraint.name));
			o.write(" instead.");
			o.write(lineSeparator);
		}
		for(int i=0; i<attributes.length; i++)
		{
			o.write("\t * @param ");
			o.write(attributes[i].name);
			o.write(' ');
			o.write(format(FINDER_UNIQUE_PARAMETER, link(attributes[i].name)));
			o.write(lineSeparator);
		}
		o.write("\t * @return ");
		o.write(FINDER_UNIQUE_RETURN);
		o.write(lineSeparator);

		writeCommentFooter();
		
		if(deprecated)
		{
			o.write('\t');
			o.write("@Deprecated");
			o.write(lineSeparator);
		}
		
		o.write('\t');
		writeModifier((option!=null ? option.getModifier(constraint.modifier) : (constraint.modifier&(PRIVATE|PROTECTED|PUBLIC))) | (STATIC|FINAL) );
		o.write(className);
		o.write(deprecated ? " findBy" : " for");
		o.write(toCamelCase(constraint.name));
		
		o.write('(');
		for(int i=0; i<attributes.length; i++)
		{
			if(i>0)
				o.write(',');
			final CopeAttribute attribute = attributes[i];
			o.write(finalArgPrefix);
			o.write(getBoxedType(attribute));
			o.write(' ');
			o.write(attribute.name);
		}
		o.write(')');
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\treturn ");

		o.write(attributes[0].parent.name);
		o.write('.');
		o.write(constraint.name);
		o.write(".searchUnique(");
		o.write(className);
		o.write(".class,");
		o.write(attributes[0].name);
		for(int i = 1; i<attributes.length; i++)
		{
			o.write(',');
			o.write(attributes[i].name);
		}
		o.write(");");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	@SuppressWarnings("deprecation")
	private String getBoxedType(final CopeAttribute a)
	{
		return a.getBoxedType();
	}
	
	private void writeSerialVersionUID() throws IOException
	{
		if(serialVersionUID)
		{
			writeCommentHeader();
			writeCommentFooter(null);
			
			o.write('\t');
			writeModifier(PRIVATE|STATIC|FINAL);
			o.write("long serialVersionUID = 1l;");
		}
	}
	
	private void writeType(final CopeType type)
	throws IOException
	{
		final Option option = type.typeOption;
		if(option.exists)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(format(TYPE, lowerCamelCase(type.name)));
			o.write(lineSeparator);
			writeCommentFooter(TYPE_CUSTOMIZE);
			
			o.write('\t');
			writeModifier(option.getModifier(type.javaClass.modifier) | (STATIC|FINAL));
			o.write(TYPE_NAME + '<');
			o.write(type.name);
			o.write("> TYPE = newType(");
			o.write(type.name);
			o.write(".class)");
			o.write(lineSeparator);
	
			o.write(';');
		}
	}
	
	void write() throws IOException, InjectorParseException
	{
		final String buffer = javaFile.buffer.toString();
		int previousClassEndPosition = 0;
		for(final JavaClass javaClass : javaFile.getClasses())
		{
			final CopeType type = CopeType.getCopeType(javaClass);
			final int classEndPosition = javaClass.getClassEndPosition();
			if(type!=null)
			{
				assert previousClassEndPosition<=classEndPosition;
				if(previousClassEndPosition<classEndPosition)
					o.write(buffer, previousClassEndPosition, classEndPosition-previousClassEndPosition);

				writeClassFeatures(type);
				previousClassEndPosition = classEndPosition;
			}
		}
		o.write(buffer, previousClassEndPosition, buffer.length()-previousClassEndPosition);
	}

	private void writeClassFeatures(final CopeType type)
			throws IOException, InjectorParseException
	{
		if(!type.isInterface())
		{
			writeInitialConstructor(type);
			writeGenericConstructor(type);
			writeReactivationConstructor(type);
			
			for(final CopeFeature feature : type.getFeatures())
			{
				writeFeature(feature);
				if(feature instanceof CopeUniqueConstraint)
				{
					writeUniqueFinder((CopeUniqueConstraint)feature, false);
					if(!skipDeprecated)
						writeUniqueFinder((CopeUniqueConstraint)feature, true);
				}
			}
			for(final CopeQualifier qualifier : sort(type.getQualifiers()))
				writeQualifier(qualifier);
			for(final CopeRelation relation : sort(type.getRelations(true)))
				writeRelation(relation, false);
			for(final CopeRelation relation : sort(type.getRelations(false)))
				writeRelation(relation, true);
			
			writeSerialVersionUID();
			writeType(type);
		}
	}
	
	private static final <X extends CopeFeature> List<X> sort(final List<X> l)
	{
		final ArrayList<X> result = new ArrayList<X>(l);
		Collections.sort(result, new Comparator<X>()
				{
					public int compare(final X a, final X b)
					{
						return a.parent.javaClass.getFullName().compareTo(b.parent.javaClass.getFullName());
					}
				});
		return result;
	}

	private void writeModifier(final int modifier) throws IOException
	{
		final String modifierString = Modifier.toString(modifier);
		if(modifierString.length()>0)
		{
			o.write(modifierString);
			o.write(' ');
		}
	}
	
	// --------------------------------------- deprecated stuff ----------------------------------------------
	
	@Deprecated
	private static final String QUALIFIER = "Returns the qualifier.";
	@Deprecated
	private static final String QUALIFIER_GETTER = "Returns the qualifier.";
	@Deprecated
	private static final String QUALIFIER_SETTER = "Sets the qualifier.";
	@Deprecated
	private static final String RELATION_GETTER  = "Returns the items associated to this item by the relation.";
	@Deprecated
	private static final String RELATION_ADDER   = "Adds an item to the items associated to this item by the relation.";
	@Deprecated
	private static final String RELATION_REMOVER = "Removes an item from the items associated to this item by the relation.";
	@Deprecated
	private static final String RELATION_SETTER  = "Sets the items associated to this item by the relation.";
	
	@Deprecated
	private void writeQualifierParameters(final CopeQualifier qualifier)
	throws IOException, InjectorParseException
	{
		final CopeAttribute[] keys = qualifier.getKeyAttributes();
		for(int i = 0; i<keys.length; i++)
		{
			if(i>0)
				o.write(',');
			o.write(finalArgPrefix);
			o.write(keys[i].persistentType);
			o.write(' ');
			o.write(keys[i].name);
		}
	}
	
	@Deprecated
	private void writeQualifierCall(final CopeQualifier qualifier)
	throws IOException, InjectorParseException
	{
		final CopeAttribute[] keys = qualifier.getKeyAttributes();
		for(int i = 0; i<keys.length; i++)
		{
			o.write(',');
			o.write(keys[i].name);
		}
	}
	
	@Deprecated
	private void writeQualifier(final CopeQualifier qualifier)
	throws IOException, InjectorParseException
	{
		final String qualifierClassName = qualifier.parent.javaClass.getFullName();

		writeCommentHeader();
		o.write("\t * ");
		o.write(QUALIFIER);
		o.write(lineSeparator);
		writeCommentFooter();
		writeDeprecated();

		o.write("\tpublic final ");
		o.write(qualifierClassName);
		o.write(" get");
		o.write(toCamelCase(qualifier.name));
		o.write('(');
		writeQualifierParameters(qualifier);
		o.write(')');
		o.write(lineSeparator);

		o.write("\t{");
		o.write(lineSeparator);

		o.write("\t\treturn (");
		o.write(qualifierClassName);
		o.write(')');
		o.write(qualifierClassName);
		o.write('.');
		o.write(qualifier.name);
		o.write(".getQualifier(this");
		writeQualifierCall(qualifier);
		o.write(");");
		o.write(lineSeparator);

		o.write("\t}");
		
		final List<CopeAttribute> qualifierAttributes = Arrays.asList(qualifier.getAttributes());
		for(final CopeFeature feature : qualifier.parent.getFeatures())
		{
			if(feature instanceof CopeAttribute)
			{
				final CopeAttribute attribute = (CopeAttribute)feature;
				if(qualifierAttributes.contains(attribute))
					continue;
				writeQualifierGetter(qualifier, attribute);
				writeQualifierSetter(qualifier, attribute);
			}
		}
	}

	@Deprecated
	private void writeQualifierGetter(final CopeQualifier qualifier, final CopeAttribute attribute)
	throws IOException, InjectorParseException
	{
		if(attribute.getterOption.exists)
		{
			final String qualifierClassName = qualifier.parent.javaClass.getFullName();
			writeCommentHeader();
			o.write("\t * ");
			o.write(QUALIFIER_GETTER);
			o.write(lineSeparator);
			writeCommentFooter();
			writeDeprecated();
	
			o.write('\t');
			writeModifier(attribute.getGeneratedGetterModifier());
			o.write(attribute.persistentType);
			o.write(" get");
			o.write(toCamelCase(attribute.name));
			o.write(attribute.getterOption.suffix);
			o.write('(');
			writeQualifierParameters(qualifier);
			o.write(')');
			o.write(lineSeparator);
	
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\treturn ");
			o.write(qualifierClassName);
			o.write('.');
			o.write(qualifier.name);
			o.write(".get(");
			o.write(qualifierClassName);
			o.write('.');
			o.write(attribute.name);
			o.write(",this");
			writeQualifierCall(qualifier);
			o.write(");");
			o.write(lineSeparator);
	
			o.write("\t}");
		}
	}

	@Deprecated
	private void writeQualifierSetter(final CopeQualifier qualifier, final CopeAttribute attribute)
	throws IOException, InjectorParseException
	{
		if(attribute.setterOption.exists)
		{
			final String qualifierClassName = qualifier.parent.javaClass.getFullName();
			writeCommentHeader();
			o.write("\t * ");
			o.write(QUALIFIER_SETTER);
			o.write(lineSeparator);
			writeCommentFooter();
			writeDeprecated();
	
			o.write('\t');
			writeModifier(attribute.getGeneratedSetterModifier());
			o.write("void set");
			o.write(toCamelCase(attribute.name));
			o.write(attribute.setterOption.suffix);
			o.write('(');
			writeQualifierParameters(qualifier);
			o.write(',');
			o.write(finalArgPrefix);
			o.write(attribute.getBoxedType());
			o.write(' ');
			o.write(attribute.name);
			o.write(')');
			o.write(lineSeparator);
			
			writeThrowsClause(attribute.getSetterExceptions());
	
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\t");
			o.write(qualifierClassName);
			o.write('.');
			o.write(qualifier.name);
			o.write(".set(");
			o.write(qualifierClassName);
			o.write('.');
			o.write(attribute.name);
			o.write(',');
			o.write(attribute.name);
			o.write(",this");
			writeQualifierCall(qualifier);
			o.write(");");
			o.write(lineSeparator);
			o.write("\t}");
		}
	}
	
	@Deprecated
	private void writeDeprecated() throws IOException
	{
		o.write("\t@SuppressWarnings(\"deprecation\") // pattern is deprecated");
		o.write(lineSeparator);
	}
	
	@Deprecated
	private void writeRelation(final CopeRelation relation, final boolean source)
	throws IOException
	{
		final boolean vector = relation.vector;
		final String endType = relation.getEndType(source);
		final String endName = relation.getEndName(source);
		final String endNameCamel = toCamelCase(endName);
		final String methodName = source ? "Sources" : "Targets";
		final String className = relation.parent.javaClass.getFullName();
		
		// getter
		if(!vector || !source)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(RELATION_GETTER);
			o.write(lineSeparator);
			writeCommentFooter();
			writeDeprecated();
	
			o.write("\tpublic final " + LIST + '<');
			o.write(endType);
			o.write("> get");
			o.write(endNameCamel);
			o.write("()");
			o.write(lineSeparator);
	
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\treturn ");
			o.write(className);
			o.write('.');
			o.write(relation.name);
			o.write(".get");
			o.write(methodName);
			o.write("(this);");
			o.write(lineSeparator);
	
			o.write("\t}");
		}

		// adder
		if(!vector)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(RELATION_ADDER);
			o.write(lineSeparator);
			writeCommentFooter();
			writeDeprecated();
	
			o.write("\tpublic final boolean addTo");
			o.write(endNameCamel);
			o.write('(');
			o.write(finalArgPrefix);
			o.write(endType);
			o.write(' ');
			o.write(endName);
			o.write(')');
			o.write(lineSeparator);
	
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\treturn ");
			o.write(className);
			o.write('.');
			o.write(relation.name);
			o.write(".addTo");
			o.write(methodName);
			o.write("(this,");
			o.write(endName);
			o.write(");");
			o.write(lineSeparator);
	
			o.write("\t}");
		}

		// remover
		if(!vector)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(RELATION_REMOVER);
			o.write(lineSeparator);
			writeCommentFooter();
			writeDeprecated();
	
			o.write("\tpublic final boolean removeFrom");
			o.write(endNameCamel);
			o.write('(');
			o.write(finalArgPrefix);
			o.write(endType);
			o.write(' ');
			o.write(endName);
			o.write(')');
			o.write(lineSeparator);
	
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\treturn ");
			o.write(className);
			o.write('.');
			o.write(relation.name);
			o.write(".removeFrom");
			o.write(methodName);
			o.write("(this,");
			o.write(endName);
			o.write(");");
			o.write(lineSeparator);
	
			o.write("\t}");
		}

		// setter
		if(!vector || !source)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(RELATION_SETTER);
			o.write(lineSeparator);
			writeCommentFooter();
			writeDeprecated();
	
			o.write("\tpublic final void set");
			o.write(endNameCamel);
			o.write('(');
			o.write(finalArgPrefix);
			o.write(COLLECTION + "<? extends ");
			o.write(endType);
			o.write("> ");
			o.write(endName);
			o.write(')');
			o.write(lineSeparator);
	
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\t");
			o.write(className);
			o.write('.');
			o.write(relation.name);
			o.write(".set");
			o.write(methodName);
			o.write("(this,");
			o.write(endName);
			o.write(");");
			o.write(lineSeparator);
	
			o.write("\t}");
		}
	}
}
