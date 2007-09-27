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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.RangeViolationException;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.ReactivationConstructorDummy;

final class Generator
{
	private static final String STRING = String.class.getName();
	private static final String COLLECTION = Collection.class.getName();
	private static final String LIST = List.class.getName();
	private static final String IO_EXCEPTION = IOException.class.getName();
	private static final String SET_VALUE = SetValue.class.getName();
	private static final String ITEM = Item.class.getName();
	private static final String TYPE_NAME = Type.class.getName();
	private static final String REACTIVATION = ReactivationConstructorDummy.class.getName();
	
	private static final char ATTRIBUTE_MAP_KEY = 'k';
	
	private static final String THROWS_MANDATORY = "if {0} is null.";
	private static final String THROWS_UNIQUE    = "if {0} is not unique.";
	private static final String THROWS_RANGE     = "if {0} violates its range constraint.";
	private static final String THROWS_LENGTH    = "if {0} violates its length constraint.";
	private static final String CONSTRUCTOR_INITIAL = "Creates a new {0} with all the fields initially needed.";
	private static final String CONSTRUCTOR_INITIAL_PARAMETER = "the initial value for field {0}.";
	private static final String CONSTRUCTOR_INITIAL_CUSTOMIZE = "It can be customized with the tags " +
																					"<tt>@" + CopeType.TAG_INITIAL_CONSTRUCTOR + " public|package|protected|private|none</tt> " +
																					"in the class comment and " +
																					"<tt>@" + CopeFeature.TAG_INITIAL + "</tt> in the comment of fields.";
	private static final String CONSTRUCTOR_GENERIC = "Creates a new {0} and sets the given fields initially.";
	private static final String CONSTRUCTOR_GENERIC_CALLED = "This constructor is called by {0}.";
	private static final String CONSTRUCTOR_GENERIC_CUSTOMIZE = "It can be customized with the tag " +
																					"<tt>@" + CopeType.TAG_GENERIC_CONSTRUCTOR + " public|package|protected|private|none</tt> " +
																					"in the class comment.";
	private static final String CONSTRUCTOR_REACTIVATION = "Reactivation constructor. Used for internal purposes only.";
	private static final String GETTER = "Returns the value of the persistent field {0}.";
	private static final String GETTER_CUSTOMIZE = "It can be customized with the tag " +
																  "<tt>@" + CopeFeature.TAG_GETTER + " public|package|protected|private|none|non-final|boolean-as-is</tt> " +
																  "in the comment of the field.";
	private static final String CHECKER = "Returns whether the given value corresponds to the hash in {0}.";
	private static final String SETTER = "Sets a new value for the persistent field {0}.";
	private static final String SETTER_CUSTOMIZE = "It can be customized with the tag " +
																  "<tt>@" + CopeFeature.TAG_SETTER + " public|package|protected|private|none|non-final</tt> " +
																  "in the comment of the field.";
	private static final String SETTER_MEDIA              = "Sets the content of media {0}.";
	private static final String SETTER_MEDIA_IOEXCEPTION  = "if accessing {0} throws an IOException.";
	private static final String GETTER_MEDIA_IS_NULL      = "Returns whether media {0} is null.";
	private static final String GETTER_MEDIA_URL          = "Returns a URL the content of the media {0} is available under.";
	private static final String GETTER_MEDIA_PATH_URL     = "Returns a URL the content of {0} is available under.";
	private static final String GETTER_MEDIA_CONTENT_TYPE = "Returns the content type of the media {0}.";
	private static final String GETTER_MEDIA_LENGTH = "Returns the body length of the media {0}.";
	private static final String GETTER_MEDIA_LASTMODIFIED = "Returns the last modification date of media {0}.";
	private static final String GETTER_MEDIA_BODY_BYTE    = "Returns the body of the media {0}.";
	private static final String GETTER_MEDIA_BODY_STREAM  = "Writes the body of media {0} into the given stream.";
	private static final String GETTER_MEDIA_BODY_FILE    = "Writes the body of media {0} into the given file.";
	private static final String GETTER_MEDIA_BODY_EXTRA = "Does nothing, if the media is null.";
	private static final String GETTER_STREAM_WARNING  = "<b>You are responsible for closing the stream, when you are finished!</b>";
	private static final String TOUCHER = "Sets the current date for the date field {0}.";
	private static final String FINDER_UNIQUE = "Finds a {0} by it''s unique fields.";
	private static final String FINDER_UNIQUE_PARAMETER = "shall be equal to field {0}.";
	private static final String FINDER_UNIQUE_RETURN = "null if there is no matching item.";
	private static final String QUALIFIER = "Returns the qualifier.";
	private static final String QUALIFIER_GETTER = "Returns the qualifier.";
	private static final String QUALIFIER_SETTER = "Sets the qualifier.";
	private static final String ATTIBUTE_LIST_GETTER = "Returns the contents of the field list {0}.";
	private static final String ATTIBUTE_LIST_SETTER = "Sets the contents of the field list {0}.";
	private static final String ATTIBUTE_SET_GETTER = "Returns the contents of the field set {0}.";
	private static final String ATTIBUTE_SET_SETTER = "Sets the contents of the field set {0}.";
	private static final String ATTIBUTE_MAP_GETTER = "Returns the value mapped to <tt>" + ATTRIBUTE_MAP_KEY + "</tt> by the field map {0}.";
	private static final String ATTIBUTE_MAP_SETTER = "Associates <tt>" + ATTRIBUTE_MAP_KEY + "</tt> to a new value in the field map {0}.";
	private static final String RELATION_GETTER  = "Returns the items associated to this item by the relation.";
	private static final String RELATION_ADDER   = "Adds an item to the items associated to this item by the relation.";
	private static final String RELATION_REMOVER = "Removes an item from the items associated to this item by the relation.";
	private static final String RELATION_SETTER  = "Sets the items associated to this item by the relation.";
	private static final String PARENT = "Returns the parent field of the type of {0}.";
	private static final String TYPE = "The persistent type information for {0}.";
	private static final String TYPE_CUSTOMIZE = "It can be customized with the tag " +
																"<tt>@" + CopeType.TAG_TYPE + " public|package|protected|private|none</tt> " +
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
	private static final String localFinal = "final "; // TODO make switchable from ant target
	
	
	Generator(final JavaFile javaFile, final ByteArrayOutputStream outputStream, final boolean longJavadoc)
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
		
		this.longJavadoc = longJavadoc;
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
	
	private void writeThrowsClause(final Collection<Class> exceptions)
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
		o.write('\t'); // TODO put this into calling methods
	}
	
	private static final String link(final String target)
	{
		return "{@link #" + target + '}';
	}
	
	private static final String link(final String target, final String name)
	{
		return "{@link #" + target + ' ' + name + '}';
	}
	
	private static final String format(final String pattern, final String parameter1)
	{
		return MessageFormat.format(pattern, new Object[]{ parameter1 });
	}

	private static final String format(final String pattern, final String parameter1, final String parameter2)
	{
		return MessageFormat.format(pattern, new Object[]{ parameter1, parameter2 });
	}

	private void writeInitialConstructor(final CopeType type)
	throws IOException
	{
		if(!type.hasInitialConstructor())
			return;

		final List<CopeFeature> initialFeatures = type.getInitialFeatures();
		final SortedSet<Class> constructorExceptions = type.getConstructorExceptions();
		
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
			final StringBuffer initialAttributesBuf = new StringBuffer();
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
			o.write("\t\t\t\tfinal ");
			o.write(feature.getBoxedType());
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
		writeModifier(option.getModifier(type.allowSubTypes() ? Modifier.PROTECTED : Modifier.PRIVATE));
		o.write(type.name);
		o.write('(');
		o.write(localFinal);
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
		writeModifier(option.getModifier(type.allowSubTypes() ? Modifier.PROTECTED : Modifier.PRIVATE));
		o.write(type.name);
		o.write('(');
		o.write(localFinal);
		o.write(REACTIVATION + " d,");
		o.write(localFinal);
		o.write("int pk)");
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tsuper(d,pk);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeAccessMethods(final CopeAttribute attribute)
	throws InjectorParseException, IOException
	{
		final String type = attribute.getBoxedType();

		// getter
		if(attribute.getterOption.exists)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(format(GETTER, link(attribute.name)));
			o.write(lineSeparator);
			writeStreamWarning(type);
			writeCommentFooter(GETTER_CUSTOMIZE);
			writeModifier(attribute.getGeneratedGetterModifier());
			o.write(type);
			if(attribute.hasIsGetter())
				o.write(" is");
			else
				o.write(" get");
			o.write(toCamelCase(attribute.name));
			o.write(attribute.getterOption.suffix);
			o.write("()");
			o.write(lineSeparator);
			o.write("\t{");
			o.write(lineSeparator);
			o.write("\t\treturn ");
			o.write(attribute.parent.name);
			o.write('.');
			o.write(attribute.name);
			o.write(".get");
			if(attribute.isBoxed())
				o.write("Mandatory");
			if(attribute instanceof CopeDataAttribute)
				o.write("Array");
			o.write("(this)");
			o.write(';');
			o.write(lineSeparator);
			o.write("\t}");
		}
		writeSetter(attribute);
		writeUniqueFinder(attribute);
	}
	
	private void writeSetter(final CopeFeature feature) throws IOException
	{
		final String type = feature.getBoxedType();
		if(feature.hasGeneratedSetter())
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(format(SETTER, link(feature.name)));
			o.write(lineSeparator);
			writeCommentFooter(SETTER_CUSTOMIZE);
			writeModifier(feature.getGeneratedSetterModifier());
			o.write("void set");
			o.write(toCamelCase(feature.name));
			o.write(feature.setterOption.suffix);
			o.write('(');
			o.write(localFinal);
			o.write(type);
			o.write(' ');
			o.write(feature.name);
			o.write(')');
			o.write(lineSeparator);
			writeThrowsClause(feature.getSetterExceptions());
			o.write("\t{");
			o.write(lineSeparator);
			o.write("\t\t");
			o.write(feature.parent.name);
			o.write('.');
			o.write(feature.name);
			o.write(".set(this,");
			o.write(feature.name);
			o.write(");");
			o.write(lineSeparator);
			o.write("\t}");
			
			// touch for date attributes
			if(feature.isTouchable())
			{
				writeCommentHeader();
				o.write("\t * ");
				o.write(format(TOUCHER, link(feature.name)));
				o.write(lineSeparator);
				writeCommentFooter();
				writeModifier(feature.getGeneratedSetterModifier());
				o.write("void touch");
				o.write(toCamelCase(feature.name));
				o.write("()");
				o.write(lineSeparator);
				writeThrowsClause(feature.getToucherExceptions());
				o.write("\t{");
				o.write(lineSeparator);
				o.write("\t\t");
				o.write(feature.parent.name);
				o.write('.');
				o.write(feature.name);
				o.write(".touch(this);");
				o.write(lineSeparator);
				o.write("\t}");
			}
		}
	}
	
	private void writeHash(final CopeHash hash)
	throws IOException, InjectorParseException
	{
		// checker
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(CHECKER, link(hash.name)));
		o.write(lineSeparator);
		writeCommentFooter();
		writeModifier(hash.getGeneratedCheckerModifier());
		o.write("boolean check");
		o.write(toCamelCase(hash.name));
		o.write('(');
		o.write(localFinal);
		o.write(STRING + ' ');
		o.write(hash.name);
		o.write(')');
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\treturn ");
		o.write(hash.parent.name);
		o.write('.');
		o.write(hash.name);
		o.write(".check(this,");
		o.write(hash.name);
		o.write(");");
		o.write(lineSeparator);
		o.write("\t}");

		writeSetter(hash);
	}
	
	private void writeMediaGetter(final CopeMedia media,
											final Class returnType,
											final String part,
											final String commentPattern)
	throws IOException
	{
		final String prefix = (boolean.class==returnType) ? "is" : "get";
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(commentPattern, link(media.name)));
		o.write(lineSeparator);
		writeStreamWarning(returnType.getName());
		writeCommentFooter();
		writeModifier(media.getGeneratedGetterModifier());
		o.write(returnType.getName());
		if(returnType==byte.class)
			o.write("[]");
		o.write(' ');
		o.write(prefix);
		o.write(toCamelCase(media.name));
		o.write(part);
		o.write("()");
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\treturn ");
		o.write(media.parent.name);
		o.write('.');
		o.write(media.name);
		o.write('.');
		o.write(prefix);
		o.write(part);
		o.write("(this);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeMediaGetter(
			final CopeMedia media,
			final Class dataType,
			final String commentPattern)
	throws IOException
	{
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(commentPattern, link(media.name)));
		o.write(lineSeparator);
		o.write("\t * ");
		o.write(GETTER_MEDIA_BODY_EXTRA);
		o.write(lineSeparator);
		o.write("\t * @throws " + IO_EXCEPTION + ' ');
		o.write(format(SETTER_MEDIA_IOEXCEPTION, "<tt>body</tt>"));
		o.write(lineSeparator);
		writeCommentFooter();
		writeModifier(media.getGeneratedGetterModifier());
		o.write("void get");
		o.write(toCamelCase(media.name));
		o.write("Body(");
		o.write(localFinal);
		o.write(dataType.getName());
		o.write(" body)");
		o.write(lineSeparator);
		final TreeSet<Class> setterExceptions = new TreeSet<Class>();
		setterExceptions.addAll(Arrays.asList(new Class[]{IOException.class})); // TODO
		writeThrowsClause(setterExceptions);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\t");
		o.write(media.parent.name);
		o.write('.');
		o.write(media.name);
		o.write(".getBody(this,body);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeMediaSetter(final CopeMedia media, final Class dataType)
	throws IOException
	{
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(SETTER_MEDIA, link(media.name)));
		o.write(lineSeparator);
		if(dataType!=byte.class)
		{
			o.write("\t * @throws " + IO_EXCEPTION + ' ');
			o.write(format(SETTER_MEDIA_IOEXCEPTION, "<tt>body</tt>"));
			o.write(lineSeparator);
		}
		writeCommentFooter();
		writeModifier(media.getGeneratedSetterModifier());
		o.write("void set");
		o.write(toCamelCase(media.name));
		o.write('(');
		o.write(localFinal);
		o.write(dataType.getName());
		if(dataType==byte.class)
			o.write("[]");
		o.write(" body,");
		o.write(localFinal);
		o.write(STRING + " contentType)");
		o.write(lineSeparator);
		if(dataType!=byte.class)
		{
			final SortedSet<Class> setterExceptions = new TreeSet<Class>();
			setterExceptions.addAll(Arrays.asList(new Class[]{IOException.class})); // TODO
			writeThrowsClause(setterExceptions);
		}
		o.write("\t{");
		o.write(lineSeparator);
		
		o.write("\t\t");
		o.write(media.parent.name);
		o.write('.');
		o.write(media.name);
		o.write(".set(this,body,contentType);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeMedia(final CopeMedia media)
	throws IOException
	{
		final MediaPath instance = (MediaPath)media.getInstance();
		if(instance instanceof Media)
			writeMediaGetter(media, boolean.class,     "Null",         GETTER_MEDIA_IS_NULL);
		
		writeMediaGetter(media, String.class,      "URL",          (instance instanceof Media) ? GETTER_MEDIA_URL : GETTER_MEDIA_PATH_URL);
		if(instance instanceof MediaFilter)
			writeMediaGetter(media, String.class,      "URLWithFallbackToSource", GETTER_MEDIA_PATH_URL);
		writeMediaGetter(media, String.class,      "ContentType",  GETTER_MEDIA_CONTENT_TYPE);
		
		if(instance instanceof Media)
		{
			writeMediaGetter(media, long.class,        "LastModified", GETTER_MEDIA_LASTMODIFIED);
			writeMediaGetter(media, long.class,        "Length",       GETTER_MEDIA_LENGTH);
			writeMediaGetter(media, byte.class,        "Body",         GETTER_MEDIA_BODY_BYTE);
			writeMediaGetter(media, OutputStream.class,                GETTER_MEDIA_BODY_STREAM);
			writeMediaGetter(media, File.class,                        GETTER_MEDIA_BODY_FILE);
	
			if(media.setterOption.exists)
			{
				writeMediaSetter(media, byte.class);
				writeMediaSetter(media, InputStream.class);
				writeMediaSetter(media, File.class);
			}
		}
	}
	
	private void writeUniqueFinder(final CopeAttribute attribute)
	throws IOException, InjectorParseException
	{
		if(!attribute.isImplicitlyUnique())
			return;
		
		final String className = attribute.getParent().name;
		
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(FINDER_UNIQUE, lowerCamelCase(className)));
		o.write(lineSeparator);
		o.write("\t * @param ");
		o.write(attribute.name);
		o.write(' ');
		o.write(format(FINDER_UNIQUE_PARAMETER, link(attribute.name)));
		o.write(lineSeparator);
		o.write("\t * @return ");
		o.write(FINDER_UNIQUE_RETURN);
		o.write(lineSeparator);

		writeCommentFooter();
		writeModifier((attribute.modifier & (Modifier.PRIVATE|Modifier.PROTECTED|Modifier.PUBLIC)) | (Modifier.STATIC|Modifier.FINAL) );
		o.write(className);
		o.write(" findBy");
		o.write(toCamelCase(attribute.name));
		
		o.write('(');
		o.write(localFinal);
		o.write(attribute.getBoxedType());
		o.write(' ');
		o.write(attribute.name);
		o.write(')');
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\treturn (");
		o.write(className);
		o.write(')');

		o.write(attribute.parent.name);
		o.write('.');
		o.write(attribute.name);
		o.write(".searchUnique(");
		attribute.write(o);
		
		o.write(");");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeUniqueFinder(final CopeUniqueConstraint constraint)
	throws IOException, InjectorParseException
	{
		final CopeAttribute[] attributes = constraint.getAttributes();
		final String className = attributes[0].getParent().name;
		
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(FINDER_UNIQUE, lowerCamelCase(className)));
		o.write(lineSeparator);
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
		writeModifier((constraint.modifier & (Modifier.PRIVATE|Modifier.PROTECTED|Modifier.PUBLIC)) | (Modifier.STATIC|Modifier.FINAL) );
		o.write(className);
		o.write(" findBy");
		o.write(toCamelCase(constraint.name));
		
		o.write('(');
		for(int i=0; i<attributes.length; i++)
		{
			if(i>0)
				o.write(',');
			final CopeAttribute attribute = attributes[i];
			o.write(localFinal);
			o.write(attribute.getBoxedType());
			o.write(' ');
			o.write(attribute.name);
		}
		o.write(')');
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\treturn (");
		o.write(className);
		o.write(')');

		o.write(attributes[0].parent.name);
		o.write('.');
		o.write(constraint.name);
		o.write(".searchUnique(");
		attributes[0].write(o);
		for(int i = 1; i<attributes.length; i++)
		{
			o.write(',');
			attributes[i].write(o);
		}
		o.write(");");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeQualifierParameters(final CopeQualifier qualifier)
	throws IOException, InjectorParseException
	{
		final CopeAttribute[] keys = qualifier.getKeyAttributes();
		for(int i = 0; i<keys.length; i++)
		{
			if(i>0)
				o.write(',');
			o.write(localFinal);
			o.write(keys[i].persistentType);
			o.write(' ');
			o.write(keys[i].name);
		}
	}
	
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
	
	private void writeQualifier(final CopeQualifier qualifier)
	throws IOException, InjectorParseException
	{
		final String qualifierClassName = qualifier.parent.javaClass.getFullName();

		writeCommentHeader();
		o.write("\t * ");
		o.write(QUALIFIER);
		o.write(lineSeparator);
		writeCommentFooter();

		o.write("public final "); // TODO: obey attribute visibility
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
	
			writeModifier(attribute.getGeneratedSetterModifier());
			o.write("void set");
			o.write(toCamelCase(attribute.name));
			o.write(attribute.setterOption.suffix);
			o.write('(');
			writeQualifierParameters(qualifier);
			o.write(',');
			o.write(localFinal);
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
	
	private void write(final CopeAttributeList list)
		throws IOException
	{
		final String type = list.getType();
		final String name = list.name;
		
		writeCommentHeader();
		o.write("\t * ");
		o.write(MessageFormat.format(list.set?ATTIBUTE_SET_GETTER:ATTIBUTE_LIST_GETTER, link(name)));
		o.write(lineSeparator);
		writeCommentFooter();

		o.write("public final "); // TODO: obey attribute visibility
		o.write((list.set?Set.class:List.class).getName());
		o.write('<');
		o.write(type);
		o.write("> get");
		o.write(toCamelCase(list.name));
		o.write("()");
		o.write(lineSeparator);

		o.write("\t{");
		o.write(lineSeparator);

		o.write("\t\treturn ");
		o.write(list.parent.name);
		o.write('.');
		o.write(list.name);
		o.write(".get(this);");
		o.write(lineSeparator);

		o.write("\t}");

		writeCommentHeader();
		o.write("\t * ");
		o.write(MessageFormat.format(list.set?ATTIBUTE_SET_SETTER:ATTIBUTE_LIST_SETTER, link(name)));
		o.write(lineSeparator);
		writeCommentFooter();

		o.write("public final void set"); // TODO: obey attribute visibility
		o.write(toCamelCase(list.name));
		o.write('(');
		o.write(localFinal);
		o.write(COLLECTION + "<? extends ");
		o.write(type);
		o.write("> ");
		o.write(list.name);
		o.write(')');
		o.write(lineSeparator);

		writeThrowsClause(Arrays.asList(new Class[]{
				UniqueViolationException.class,
				MandatoryViolationException.class,
				LengthViolationException.class,
				FinalViolationException.class,
				ClassCastException.class}));

		o.write("\t{");
		o.write(lineSeparator);

		o.write("\t\t");
		o.write(list.parent.name);
		o.write('.');
		o.write(list.name);
		o.write(".set(this,");
		o.write(list.name);
		o.write(");");
		o.write(lineSeparator);

		o.write("\t}");
		
		if(list.hasParent)
			writeParent(list);
	}
	
	private void write(final CopeAttributeMap map) throws IOException
	{
		if(true) // TODO SOON getter option
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(MessageFormat.format(ATTIBUTE_MAP_GETTER, link(map.name)));
			o.write(lineSeparator);
			writeCommentFooter();
	
			o.write("public final "); // TODO SOON getter option
			o.write(map.getValueType());
			o.write(" get");
			o.write(toCamelCase(map.name));
			o.write('(');
			o.write(localFinal);
			o.write(map.getKeyType());
			o.write(" " + ATTRIBUTE_MAP_KEY + ")");
			o.write(lineSeparator);
	
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\treturn ");
			o.write(map.parent.name);
			o.write('.');
			o.write(map.name);
			o.write(".get(this," + ATTRIBUTE_MAP_KEY + ");");
			o.write(lineSeparator);
	
			o.write("\t}");
		}
		if(true) // TODO SOON setter option
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(MessageFormat.format(ATTIBUTE_MAP_SETTER, link(map.name)));
			o.write(lineSeparator);
			writeCommentFooter();
	
			o.write("public final "); // TODO SOON setter option
			o.write("void set");
			o.write(toCamelCase(map.name));
			o.write('(');
			o.write(localFinal);
			o.write(map.getKeyType());
			o.write(" " + ATTRIBUTE_MAP_KEY + ',');
			o.write(localFinal);
			o.write(map.getValueType());
			o.write(' ');
			o.write(map.name);
			o.write(')');
			o.write(lineSeparator);
			
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\t");
			o.write(map.parent.name);
			o.write('.');
			o.write(map.name);
			o.write(".set(this," + ATTRIBUTE_MAP_KEY + ',');
			o.write(map.name);
			o.write(");");
			o.write(lineSeparator);
			o.write("\t}");
		}
		if(map.hasParent)
			writeParent(map);
	}
	
	private void writeParent(final CopeFeature f) throws IOException
	{
		if(true) // TODO SOON parent option
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(MessageFormat.format(PARENT, link(f.name)));
			o.write(lineSeparator);
			writeCommentFooter();
	
			o.write(Modifier.toString(f.modifier | (Modifier.STATIC | Modifier.FINAL)));
			o.write(' ');
			o.write(ItemField.class.getName());
			o.write('<');
			o.write(f.parent.name);
			o.write('>');
			o.write(' ');
			o.write(f.name);
			o.write("Parent()");
			o.write(lineSeparator);
	
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\treturn ");
			o.write(f.parent.name);
			o.write('.');
			o.write(f.name);
			o.write(".getParent(");
			o.write(f.parent.name);
			o.write(".class);");
			o.write(lineSeparator);
	
			o.write("\t}");
		}
	}
	
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
	
			o.write("public final " + LIST + '<'); // TODO: obey attribute visibility
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
	
			o.write("public final boolean addTo"); // TODO: obey attribute visibility
			o.write(endNameCamel);
			o.write('(');
			o.write(localFinal);
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
	
			o.write("public final boolean removeFrom"); // TODO: obey attribute visibility
			o.write(endNameCamel);
			o.write('(');
			o.write(localFinal);
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
	
			o.write("public final void set"); // TODO: obey attribute visibility
			o.write(endNameCamel);
			o.write('(');
			o.write(localFinal);
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

	private void writeSerialVersionUID() throws IOException
	{
		// TODO make disableable
		{
			writeCommentHeader();
			writeCommentFooter(null);
			
			writeModifier(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
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
			
			writeModifier(option.getModifier(Modifier.PUBLIC) | Modifier.STATIC | Modifier.FINAL); // TODO obey class visibility
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
				if(feature instanceof CopeAttribute)
					writeAccessMethods((CopeAttribute)feature);
				else if(feature instanceof CopeUniqueConstraint)
					writeUniqueFinder((CopeUniqueConstraint)feature);
				else if(feature instanceof CopeAttributeList)
					write((CopeAttributeList)feature);
				else if(feature instanceof CopeAttributeMap)
					write((CopeAttributeMap)feature);
				else if(feature instanceof CopeMedia)
					writeMedia((CopeMedia)feature);
				else if(feature instanceof CopeHash)
					writeHash((CopeHash)feature);
				else if(feature instanceof CopeRelation || feature instanceof CopeQualifier)
					; // is handled below
				else
					throw new RuntimeException(feature.getClass().getName());
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

	private void writeStreamWarning(final String type) throws IOException
	{
		if(InputStream.class.getName().equals(type))
		{
			o.write("\t * ");
			o.write(GETTER_STREAM_WARNING);
			o.write(lineSeparator);
		}
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
}
