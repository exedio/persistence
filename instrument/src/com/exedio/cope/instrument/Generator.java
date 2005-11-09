/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.AttributeValue;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.NestingRuntimeException;
import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.util.ClassComparator;
import com.exedio.cope.util.ReactivationConstructorDummy;

final class Generator
{
	private static final String THROWS_NULL   = "if {0} is null.";
	private static final String THROWS_UNIQUE = "if {0} is not unique.";
	private static final String THROWS_LENGTH = "if {0} violates its length constraint.";
	private static final String CONSTRUCTOR_INITIAL = "Creates a new {0} with all the attributes initially needed.";
	private static final String CONSTRUCTOR_INITIAL_PARAMETER = "the initial value for attribute {0}.";
	private static final String CONSTRUCTOR_INITIAL_CUSTOMIZE = "It can be customized with the tags " +
																					"<code>@"+Instrumentor.CLASS_INITIAL_CONSTRUCTOR+" public|package|protected|private|none</code> " +
																					"in the class comment and " +
																					"<code>@"+Instrumentor.ATTRIBUTE_INITIAL+"</code> in the comment of attributes.";
	private static final String CONSTRUCTOR_GENERIC = "Creates a new {0} and sets the given attributes initially.";
	private static final String CONSTRUCTOR_GENERIC_CALLED = "This constructor is called by {0}.";
	private static final String CONSTRUCTOR_GENERIC_CUSTOMIZE = "It can be customized with the tag " +
																					"<code>@"+Instrumentor.CLASS_GENERIC_CONSTRUCTOR+" public|package|protected|private|none</code> " +
																					"in the class comment.";
	private static final String CONSTRUCTOR_REACTIVATION = "Reactivation constructor. Used for internal purposes only.";
	private static final String GETTER = "Returns the value of the persistent attribute {0}.";
	private static final String GETTER_CUSTOMIZE = "It can be customized with the tag " +
																  "<code>@"+Instrumentor.ATTRIBUTE_GETTER+" public|package|protected|private|none|non-final|boolean-as-is</code> " +
																  "in the comment of the attribute.";
	private static final String CHECKER = "Returns whether the given value corresponds to the hash in {0}.";
	private static final String SETTER = "Sets a new value for the persistent attribute {0}.";
	private static final String SETTER_CUSTOMIZE = "It can be customized with the tag " +
																  "<code>@"+Instrumentor.ATTRIBUTE_SETTER+" public|package|protected|private|none|non-final</code> " +
																  "in the comment of the attribute.";
	private static final String SETTER_MEDIA = "Sets the new data for the media {0}.";
	private static final String SETTER_MEDIA_IOEXCEPTION = "if accessing {0} throws an IOException.";
	private static final String GETTER_MEDIA_IS_NULL = "Returns whether this media {0} has data available.";
	private static final String GETTER_MEDIA_URL   = "Returns a URL the data of the media {0} is available under.";
	private static final String GETTER_MEDIA_MAJOR = "Returns the major mime type of the media {0}.";
	private static final String GETTER_MEDIA_MINOR = "Returns the minor mime type of the media {0}.";
	private static final String GETTER_MEDIA_CONTENT_TYPE = "Returns the content type of the media {0}.";
	private static final String GETTER_MEDIA_LENGTH_TYPE = "Returns the data length of the media {0}.";
	private static final String GETTER_MEDIA_LASTMODIFIED_TYPE = "Returns the last modification date of the media {0}.";
	private static final String GETTER_MEDIA_DATA  = "Returns the data of the media {0}.";
	private static final String GETTER_MEDIA_DATA_FILE = "Reads data of media {0}, and writes it into the given file.";
	private static final String GETTER_MEDIA_DATA_FILE2 = "Does nothing, if there is no data for the media.";
	private static final String GETTER_STREAM_WARNING  = "<b>You are responsible for closing the stream, when you are finished!</b>";
	private static final String TOUCHER = "Sets the current date for the date attribute {0}.";
	private static final String FINDER_UNIQUE = "Finds a {0} by it''s unique attributes.";
	private static final String FINDER_UNIQUE_PARAMETER = "shall be equal to attribute {0}.";
	private static final String FINDER_UNIQUE_RETURN = "null if there is no matching item.";
	private static final String QUALIFIER = "Returns the qualifier.";
	private static final String QUALIFIER_GETTER = "Returns the qualifier.";
	private static final String QUALIFIER_SETTER = "Sets the qualifier.";
	private static final String VECTOR_GETTER = "Returns the value of the vector.";
	private static final String VECTOR_SETTER = "Sets the vector.";
	private static final String TYPE = "The persistent type information for {0}.";
	private static final String TYPE_CUSTOMIZE = "It can be customized with the tag " +
																"<code>@"+Instrumentor.CLASS_TYPE+" public|package|protected|private|none</code> " +
																"in the class comment.";
	private static final String GENERATED = "This feature has been generated by the cope instrumentor and will be overwritten by the build process.";

	private final Writer o;
	private final String lineSeparator;
	
	private static final boolean show = false;
	
	Generator(final Writer output)
	{
		this.o=output;
		
		final String systemLineSeparator = System.getProperty("line.separator");
		if(systemLineSeparator==null)
		{
			System.out.println("warning: property \"line.separator\" is null, using LF (unix style).");
			lineSeparator = "\n";
		}
		else
			lineSeparator = systemLineSeparator;
	}

	public static final String toCamelCase(final String name)
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
	
	private static final String getShortName(final Class aClass)
	{
		final String name = aClass.getName();
		final int pos = name.lastIndexOf('.');
		return name.substring(pos+1);
	}

	private void writeThrowsClause(final Collection exceptions)
	throws IOException
	{
		if(!exceptions.isEmpty())
		{
			o.write("\t\t\tthrows");
			boolean first = true;
			for(final Iterator i = exceptions.iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					o.write(',');
				o.write(lineSeparator);
				o.write("\t\t\t\t");
				o.write(((Class)i.next()).getName());
			}
			o.write(lineSeparator);
		}
	}

	private final void writeCommentHeader()
	throws IOException
	{
		o.write("/**");
		o.write(lineSeparator);
		o.write(lineSeparator);
		o.write("\t **");
		o.write(lineSeparator);
	}

	private final void writeCommentFooter()
	throws IOException
	{
		writeCommentFooter(null);
	}
	
	private final void writeCommentFooter(final String extraComment)
	throws IOException
	{
		o.write("\t * @"+Instrumentor.GENERATED+' ');
		o.write(GENERATED);
		o.write(lineSeparator);
		if(extraComment!=null)
		{
			o.write("\t *       ");
			o.write(extraComment);
			o.write(lineSeparator);
		}
		o.write("\t *");
		o.write(lineSeparator);
		o.write(" */");
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

	private void writeInitialConstructor(final CopeClass copeClass) 
	throws IOException
	{
		if(!copeClass.hasInitialConstructor())
			return;

		final List initialAttributes = copeClass.getInitialAttributes();
		final SortedSet constructorExceptions = copeClass.getConstructorExceptions();
		
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(CONSTRUCTOR_INITIAL, copeClass.getName()));
		o.write(lineSeparator);
		for(Iterator i = initialAttributes.iterator(); i.hasNext(); )
		{
			final CopeAttribute initialAttribute = (CopeAttribute)i.next();
			o.write("\t * @param ");
			o.write(initialAttribute.getName());
			o.write(' ');
			o.write(format(CONSTRUCTOR_INITIAL_PARAMETER, link(initialAttribute.getName())));
			o.write(lineSeparator);
		}
		for(Iterator i = constructorExceptions.iterator(); i.hasNext(); )
		{
			final Class constructorException = (Class)i.next();
			o.write("\t * @throws ");
			o.write(constructorException.getName());
			o.write(' ');

			boolean first = true;
			final StringBuffer initialAttributesBuf = new StringBuffer();
			for(Iterator j = initialAttributes.iterator(); j.hasNext(); )
			{
				final CopeAttribute initialAttribute = (CopeAttribute)j.next();
				if(!initialAttribute.getSetterExceptions().contains(constructorException))
					continue;

				if(first)
					first = false;
				else
					initialAttributesBuf.append(", ");
				initialAttributesBuf.append(initialAttribute.getName());
			}

			final String pattern;
			if(MandatoryViolationException.class.equals(constructorException))
				pattern = THROWS_NULL;
			else if(UniqueViolationException.class.equals(constructorException))
				pattern = THROWS_UNIQUE;
			else if(LengthViolationException.class.equals(constructorException))
				pattern = THROWS_LENGTH;
			else
				throw new RuntimeException(constructorException.getName());

			o.write(format(pattern, initialAttributesBuf.toString()));
			o.write(lineSeparator);
		}
		writeCommentFooter(CONSTRUCTOR_INITIAL_CUSTOMIZE);
		final String modifier = Modifier.toString(copeClass.getInitialConstructorModifier());
		if(modifier.length()>0)
		{
			o.write(modifier);
			o.write(' ');
		}
		o.write(copeClass.getName());
		o.write('(');
		
		boolean first = true;
		for(Iterator i = initialAttributes.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				o.write(',');
			final CopeAttribute initialAttribute = (CopeAttribute)i.next();
			o.write(lineSeparator);
			o.write("\t\t\t\tfinal ");
			o.write(initialAttribute.getBoxedType());
			o.write(' ');
			o.write(initialAttribute.getName());
		}
		
		o.write(')');
		o.write(lineSeparator);
		writeThrowsClause(constructorExceptions);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tthis(new "+AttributeValue.class.getName()+"[]{");
		o.write(lineSeparator);
		for(Iterator i = initialAttributes.iterator(); i.hasNext(); )
		{
			final CopeAttribute initialAttribute = (CopeAttribute)i.next();
			o.write("\t\t\tnew "+AttributeValue.class.getName()+"(");
			o.write(initialAttribute.copeClass.getName());
			o.write('.');
			o.write(initialAttribute.getName());
			o.write(',');
			writeAttribute(initialAttribute);
			o.write("),");
			o.write(lineSeparator);
		}
		o.write("\t\t});");
		o.write(lineSeparator);
		for(Iterator i = copeClass.getConstructorExceptions().iterator(); i.hasNext(); )
		{
			final Class exception = (Class)i.next();
			o.write("\t\tthrowInitial");
			o.write(getShortName(exception));
			o.write("();");
			o.write(lineSeparator);
		}
		o.write("\t}");
	}
	
	private void writeGenericConstructor(final CopeClass copeClass)
	throws IOException
	{
		final Option option = copeClass.genericConstructorOption;
		if(!option.exists)
			return;

		writeCommentHeader();
		o.write("\t * ");
		o.write(format(CONSTRUCTOR_GENERIC, copeClass.getName()));
		o.write(lineSeparator);
		o.write("\t * ");
		o.write(format(CONSTRUCTOR_GENERIC_CALLED, "{@link " + Type.class.getName() + "#newItem Type.newItem}"));
		o.write(lineSeparator);
		writeCommentFooter(CONSTRUCTOR_GENERIC_CUSTOMIZE);
		o.write( Modifier.toString( option.getModifier(copeClass.isAbstract() ? Modifier.PROTECTED : Modifier.PRIVATE) ) );
		o.write(' ');
		o.write(copeClass.getName());
		o.write("(final "+AttributeValue.class.getName()+"[] initialAttributes)");
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tsuper(initialAttributes);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeReactivationConstructor(final CopeClass copeClass)
	throws IOException
	{
		writeCommentHeader();
		o.write("\t * ");
		o.write(CONSTRUCTOR_REACTIVATION);
		o.write(lineSeparator);
		o.write("\t * @see Item#Item("
			+ ReactivationConstructorDummy.class.getName() + ",int)");
		o.write(lineSeparator);
		writeCommentFooter();
		o.write( copeClass.isAbstract() ? "protected " : "private " );
		o.write(copeClass.getName());
		o.write("("+ReactivationConstructorDummy.class.getName()+" d,final int pk)");
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tsuper(d,pk);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeAccessMethods(final CopeAttribute attribute)
	throws IOException
	{
		if(show) attribute.show();

		final String type = attribute.getBoxedType();

		// getter
		if(attribute.getterOption.exists)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(format(GETTER, link(attribute.getName())));
			o.write(lineSeparator);
			writeStreamWarning(type);
			writeCommentFooter(GETTER_CUSTOMIZE);
			attribute.writeGeneratedGetterModifier(o);
			o.write(type);
			if(attribute.hasIsGetter())
				o.write(" is");
			else
				o.write(" get");
			o.write(toCamelCase(attribute.getName()));
			o.write("()");
			o.write(lineSeparator);
			o.write("\t{");
			o.write(lineSeparator);
			writeGetterBody(attribute);
			o.write("\t}");
		}
		
		// setter
		if(attribute.hasGeneratedSetter())
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(format(SETTER, link(attribute.getName())));
			o.write(lineSeparator);
			writeCommentFooter(SETTER_CUSTOMIZE);
			attribute.writeGeneratedSetterModifier(o);
			o.write("void set");
			o.write(toCamelCase(attribute.getName()));
			o.write("(final ");
			o.write(type);
			o.write(' ');
			o.write(attribute.getName());
			o.write(')');
			o.write(lineSeparator);
			writeThrowsClause(attribute.getSetterExceptions());
			o.write("\t{");
			o.write(lineSeparator);
			writeSetterBody(attribute);
			o.write("\t}");
			
			// touch for date attributes
			if(attribute.isTouchable())
			{
				writeCommentHeader();
				o.write("\t * ");
				o.write(format(TOUCHER, link(attribute.getName())));
				o.write(lineSeparator);
				writeCommentFooter();
				attribute.writeGeneratedSetterModifier(o);
				o.write("void touch");
				o.write(toCamelCase(attribute.getName()));
				o.write("()");
				o.write(lineSeparator);
				writeThrowsClause(attribute.getToucherExceptions());
				o.write("\t{");
				o.write(lineSeparator);
				writeToucherBody(attribute);
				o.write("\t}");
			}
		}
		for(Iterator i = attribute.getHashes().iterator(); i.hasNext(); )
		{
			final CopeHash hash = (CopeHash)i.next();
			writeHash(hash);
		}
	}
	
	private void writeHash(final CopeHash hash)
	throws IOException
	{
		// checker
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(CHECKER, link(hash.name)));
		o.write(lineSeparator);
		writeCommentFooter();
		o.write(Modifier.toString(hash.getGeneratedCheckerModifier()));
		o.write(" boolean check");
		o.write(toCamelCase(hash.name));
		o.write("(final ");
		o.write(String.class.getName());
		o.write(' ');
		o.write(hash.name);
		o.write(')');
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		writeCheckerBody(hash);
		o.write("\t}");

		// setter
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(SETTER, link(hash.name)));
		o.write(lineSeparator);
		writeCommentFooter();
		o.write(Modifier.toString(hash.getGeneratedSetterModifier()));
		o.write(" void set");
		o.write(toCamelCase(hash.name));
		o.write("(final ");
		o.write(String.class.getName());
		o.write(' ');
		o.write(hash.name);
		o.write(')');
		o.write(lineSeparator);
		writeThrowsClause(hash.storageAttribute.getSetterExceptions());
		o.write("\t{");
		o.write(lineSeparator);
		writeSetterBody(hash);
		o.write("\t}");
	}
	
	private void writeDataGetterMethod(final CopeMedia media,
													final Class returnType,
													final String part,
													final String commentPattern,
													final int getterModifier)
	throws IOException
	{
		final String prefix = (boolean.class==returnType) ? "is" : "get";
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(commentPattern, link(media.getName())));
		o.write(lineSeparator);
		writeStreamWarning(returnType.getName());
		writeCommentFooter();
		o.write(Modifier.toString(getterModifier|Modifier.FINAL));
		o.write(' ');
		o.write(returnType.getName());
		o.write(' ');
		o.write(prefix);
		o.write(toCamelCase(media.getName()));
		o.write(part);
		o.write("()");
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\treturn ");
		o.write(media.copeClass.getName());
		o.write('.');
		o.write(media.getName());
		o.write('.');
		o.write(prefix);
		o.write(part);
		o.write("(this);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeDataSetterMethod(final CopeMedia media, final Class dataType)
	throws IOException
	{
		final String mimeMajor = media.mimeMajor;
		final String mimeMinor = media.mimeMinor;

		writeCommentHeader();
		o.write("\t * ");
		o.write(format(SETTER_MEDIA, link(media.getName())));
		o.write(lineSeparator);
		o.write("\t * @throws ");
		o.write(IOException.class.getName());
		o.write(' ');
		o.write(format(SETTER_MEDIA_IOEXCEPTION, "<code>data</code>"));
		o.write(lineSeparator);
		writeCommentFooter();
		o.write(Modifier.toString(media.getGeneratedSetterModifier()));
		o.write(" void set");
		o.write(toCamelCase(media.getName()));
		o.write("(final " + dataType.getName() + " data");
		if(mimeMajor==null)
			o.write(",final "+String.class.getName()+" mimeMajor");
		if(mimeMinor==null)
			o.write(",final "+String.class.getName()+" mimeMinor");
		o.write(')');
		final SortedSet setterExceptions = new TreeSet();
		setterExceptions.addAll(Arrays.asList(new Class[]{IOException.class})); // TODO
		o.write(lineSeparator);
		writeThrowsClause(setterExceptions);
		o.write("\t{");
		o.write(lineSeparator);
		
		final SortedSet exceptionsToCatch = new TreeSet(ClassComparator.getInstance());
		exceptionsToCatch.addAll(setterExceptions); // TODO
		exceptionsToCatch.remove(IOException.class);
		writeTryCatchClausePrefix(exceptionsToCatch);
		o.write("\t\t");
		o.write(media.copeClass.getName());
		o.write('.');
		o.write(media.getName());
		o.write(".set(this,data");
		o.write(mimeMajor==null ? ",mimeMajor" : ",null");
		o.write(mimeMinor==null ? ",mimeMinor" : ",null");
		o.write(");");
		o.write(lineSeparator);
		writeTryCatchClausePostfix(exceptionsToCatch);
		o.write("\t}");
	}
	
	private void writeDataAccessMethods(final CopeMedia media)
	throws IOException
	{
		// getters
		final int getterModifier = media.getGeneratedGetterModifier();
		writeDataGetterMethod(media, boolean.class,     "Null",        GETTER_MEDIA_IS_NULL,      getterModifier);
		writeDataGetterMethod(media, String.class,      "URL",         GETTER_MEDIA_URL,          getterModifier);
		writeDataGetterMethod(media, String.class,      "MimeMajor",   GETTER_MEDIA_MAJOR,        getterModifier);
		writeDataGetterMethod(media, String.class,      "MimeMinor",   GETTER_MEDIA_MINOR,        getterModifier);
		writeDataGetterMethod(media, String.class,      "ContentType", GETTER_MEDIA_CONTENT_TYPE, getterModifier);
		writeDataGetterMethod(media, long.class,        "Length",      GETTER_MEDIA_LENGTH_TYPE, getterModifier);
		writeDataGetterMethod(media, long.class,        "LastModified",GETTER_MEDIA_LASTMODIFIED_TYPE, getterModifier);
		writeDataGetterMethod(media, InputStream.class, "Data",        GETTER_MEDIA_DATA,         getterModifier);
		
		// file getter
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(format(GETTER_MEDIA_DATA_FILE, link(media.getName())));
			o.write(lineSeparator);
			o.write("\t * ");
			o.write(GETTER_MEDIA_DATA_FILE2);
			o.write(lineSeparator);
			o.write("\t * @throws ");
			o.write(IOException.class.getName());
			o.write(' ');
			o.write(format(SETTER_MEDIA_IOEXCEPTION, "<code>data</code>"));
			o.write(lineSeparator);
			writeCommentFooter();
			o.write(Modifier.toString(media.getGeneratedGetterModifier()|Modifier.FINAL));
			o.write(" void get");
			o.write(toCamelCase(media.getName()));
			o.write("Data(final " + File.class.getName() + " data)");
			o.write(lineSeparator);
			final SortedSet setterExceptions = new TreeSet();
			setterExceptions.addAll(Arrays.asList(new Class[]{IOException.class})); // TODO
			writeThrowsClause(setterExceptions);
			o.write("\t{");
			o.write(lineSeparator);
			o.write("\t\t");
			o.write(media.copeClass.getName());
			o.write('.');
			o.write(media.getName());
			o.write(".getData(this,data);");
			o.write(lineSeparator);
			o.write("\t}");
		}

		// setters
		if(media.setterOption.exists)
		{
			writeDataSetterMethod(media, InputStream.class);
			writeDataSetterMethod(media, File.class);
		}
	}
	
	private void writeUniqueFinder(final CopeUniqueConstraint constraint)
	throws IOException
	{
		if(show) constraint.show();

		final CopeAttribute[] atributes = constraint.attributes;
		final String className = atributes[0].getParent().name;
		
		writeCommentHeader();
		o.write("\t * ");
		o.write(format(FINDER_UNIQUE, lowerCamelCase(className)));
		o.write(lineSeparator);
		for(int i=0; i<atributes.length; i++)
		{
			o.write("\t * @param ");
			o.write(atributes[i].getName());
			o.write(' ');
			o.write(format(FINDER_UNIQUE_PARAMETER, link(atributes[i].getName())));
			o.write(lineSeparator);
		}
		o.write("\t * @return ");
		o.write(FINDER_UNIQUE_RETURN);
		o.write(lineSeparator);

		writeCommentFooter();
		o.write(Modifier.toString((constraint.modifier & (Modifier.PRIVATE|Modifier.PROTECTED|Modifier.PUBLIC)) | (Modifier.STATIC|Modifier.FINAL) ));
		o.write(' ');
		o.write(className);
		o.write(" findBy");
		o.write(toCamelCase(constraint.name));
		
		o.write('(');
		for(int i=0; i<atributes.length; i++)
		{
			if(i>0)
				o.write(',');
			final CopeAttribute attribute = atributes[i];
			o.write("final ");
			o.write(attribute.getBoxedType());
			o.write(' ');
			o.write(attribute.getName());
		}
		o.write(')');
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\treturn (");
		o.write(className);
		o.write(')');

		if(atributes.length==1)
		{
			o.write(atributes[0].copeClass.getName());
			o.write('.');
			o.write(atributes[0].getName());
			o.write(".searchUnique(");
			writeAttribute(atributes[0]);
		}
		else
		{
			o.write(atributes[0].copeClass.getName());
			o.write('.');
			o.write(constraint.name);
			o.write(".searchUnique(new Object[]{");
			writeAttribute(atributes[0]);
			for(int i = 1; i<atributes.length; i++)
			{
				o.write(',');
				writeAttribute(atributes[i]);
			}
			o.write('}');
		}
		
		o.write(");");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeAttribute(final CopeAttribute attribute)
			throws IOException
	{
		if(attribute.isBoxed())
			o.write(attribute.getBoxingPrefix());
		o.write(attribute.getName());
		if(attribute.isBoxed())
			o.write(attribute.getBoxingPostfix());
	}
	
	private void writeQualifierParameters(final CopeQualifier qualifier)
	throws IOException
	{
		final CopeAttribute[] keys = qualifier.keyAttributes;
		for(int i = 0; i<keys.length; i++)
		{
			if(i>0)
				o.write(',');
			o.write("final ");
			o.write(keys[i].persistentType);
			o.write(' ');
			o.write(keys[i].javaAttribute.name);
		}
	}
	
	private void writeQualifierCall(final CopeQualifier qualifier)
	throws IOException
	{
		final CopeAttribute[] keys = qualifier.keyAttributes;
		for(int i = 0; i<keys.length; i++)
		{
			o.write(',');
			o.write(keys[i].javaAttribute.name);
		}
	}
	
	private void writeQualifier(final CopeQualifier qualifier)
	throws IOException
	{
		if(show) qualifier.show();
		
		writeCommentHeader();
		o.write("\t * ");
		o.write(QUALIFIER);
		o.write(lineSeparator);
		writeCommentFooter();

		o.write("public final "); // TODO: obey attribute visibility
		o.write(qualifier.qualifierClassString);
		o.write(" get");
		o.write(toCamelCase(qualifier.name));
		o.write('(');
		writeQualifierParameters(qualifier);
		o.write(')');
		o.write(lineSeparator);

		o.write("\t{");
		o.write(lineSeparator);

		o.write("\t\treturn (");
		o.write(qualifier.qualifierClassString);
		o.write(")");
		o.write(qualifier.name);
		o.write(".getQualifier(new Object[]{this");
		writeQualifierCall(qualifier);
		o.write("});");
		o.write(lineSeparator);

		o.write("\t}");
		
		final List qualifierAttributes = Arrays.asList(qualifier.uniqueConstraint.attributes);
		for(Iterator i = qualifier.qualifierClass.getCopeAttributes().iterator(); i.hasNext(); )
		{
			final CopeAttribute attribute = (CopeAttribute)i.next();
			if(qualifierAttributes.contains(attribute))
				continue;
			writeQualifierGetter(qualifier, attribute);
			writeQualifierSetter(qualifier, attribute);
		}
	}

	private void writeQualifierGetter(final CopeQualifier qualifier, final CopeAttribute attribute)
	throws IOException
	{
		if(attribute.getterOption.exists)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(QUALIFIER_GETTER);
			o.write(lineSeparator);
			writeCommentFooter();
	
			final String resultType = attribute.persistentType;
			attribute.writeGeneratedGetterModifier(o);
			o.write(resultType);
			o.write(" get");
			o.write(toCamelCase(attribute.getName()));
			o.write('(');
			writeQualifierParameters(qualifier);
			o.write(')');
			o.write(lineSeparator);
	
			o.write("\t{");
			o.write(lineSeparator);
	
			o.write("\t\treturn (");
			o.write(resultType);
			o.write(')');
			o.write(qualifier.name);
			o.write(".get(new Object[]{this");
			writeQualifierCall(qualifier);
			o.write("},");
			o.write(qualifier.qualifierClassString);
			o.write('.');
			o.write(attribute.getName());
			o.write(");");
			o.write(lineSeparator);
	
			o.write("\t}");
		}
	}

	private void writeQualifierSetter(final CopeQualifier qualifier, final CopeAttribute attribute)
	throws IOException
	{
		if(attribute.setterOption.exists)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(QUALIFIER_SETTER);
			o.write(lineSeparator);
			writeCommentFooter();
	
			final String resultType = attribute.persistentType;
			attribute.writeGeneratedSetterModifier(o);
			o.write("void set");
			o.write(toCamelCase(attribute.getName()));
			o.write('(');
			writeQualifierParameters(qualifier);
			o.write(",final ");
			o.write(resultType);
			o.write(' ');
			o.write(attribute.getName());
			o.write(')');
			o.write(lineSeparator);
			
			writeThrowsClause(attribute.getSetterExceptions());
	
			o.write("\t{");
			o.write(lineSeparator);
	
			final SortedSet exceptionsToCatch = new TreeSet(ClassComparator.getInstance());
			exceptionsToCatch.addAll(attribute.getExceptionsToCatchInSetter());
			exceptionsToCatch.remove(UniqueViolationException.class);
			writeTryCatchClausePrefix(exceptionsToCatch);
	
			o.write("\t\t");
			o.write(qualifier.name);
			o.write(".set(new Object[]{this");
			writeQualifierCall(qualifier);
			o.write("},");
			o.write(qualifier.qualifierClassString);
			o.write('.');
			o.write(attribute.getName());
			o.write(',');
			o.write(attribute.getName());
			o.write(");");
			o.write(lineSeparator);
	
			writeTryCatchClausePostfix(exceptionsToCatch);
			o.write("\t}");
		}
	}
	
	private void writeVector(final CopeVector vector)
		throws IOException
	{
		writeCommentHeader();
		o.write("\t * ");
		o.write(VECTOR_GETTER);
		o.write(lineSeparator);
		writeCommentFooter();

		o.write("public final "); // TODO: obey attribute visibility
		o.write(List.class.getName());
		o.write(" get");
		o.write(toCamelCase(vector.name));
		o.write("()");
		o.write(lineSeparator);

		o.write("\t{");
		o.write(lineSeparator);

		o.write("\t\treturn ");
		o.write(vector.copeClass.getName());
		o.write('.');
		o.write(vector.name);
		o.write(".get(this);");
		o.write(lineSeparator);

		o.write("\t}");

		writeCommentHeader();
		o.write("\t * ");
		o.write(VECTOR_SETTER);
		o.write(lineSeparator);
		writeCommentFooter();

		o.write("public final void set"); // TODO: obey attribute visibility
		o.write(toCamelCase(vector.name));
		o.write("(final ");
		o.write(Collection.class.getName());
		o.write(' ');
		o.write(vector.name);
		o.write(')');
		o.write(lineSeparator);

		writeThrowsClause(Arrays.asList(new Class[]{
				UniqueViolationException.class,
				MandatoryViolationException.class,
				LengthViolationException.class,
				ReadOnlyViolationException.class,
				ClassCastException.class}));

		o.write("\t{");
		o.write(lineSeparator);

		o.write("\t\t");
		o.write(vector.copeClass.getName());
		o.write('.');
		o.write(vector.name);
		o.write(".set(this,");
		o.write(vector.name);
		o.write(");");
		o.write(lineSeparator);

		o.write("\t}");
	}

	private final void writeType(final CopeClass copeClass)
	throws IOException
	{
		final Option option = copeClass.typeOption;
		if(option.exists)
		{
			writeCommentHeader();
			o.write("\t * ");
			o.write(format(TYPE, lowerCamelCase(copeClass.getName())));
			o.write(lineSeparator);
			writeCommentFooter(TYPE_CUSTOMIZE);
			
			o.write(Modifier.toString(option.getModifier(Modifier.PUBLIC) | Modifier.STATIC | Modifier.FINAL)); // TODO obey class visibility
			o.write(" "+Type.class.getName()+" TYPE =");
			o.write(lineSeparator);
	
			o.write("\t\tnew "+Type.class.getName()+"(");
			o.write(copeClass.getName());
			o.write(".class)");
			o.write(lineSeparator);
	
			o.write(";");
		}
	}

	void writeClassFeatures(final CopeClass copeClass)
			throws IOException, InjectorParseException
	{
		if(!copeClass.isInterface())
		{
			//System.out.println("onClassEnd("+jc.getName()+") writing");
			writeInitialConstructor(copeClass);
			writeGenericConstructor(copeClass);
			writeReactivationConstructor(copeClass);
			for(final Iterator i = copeClass.getCopeAttributes().iterator(); i.hasNext(); )
			{
				// write setter/getter methods
				final CopeAttribute attribute = (CopeAttribute)i.next();
				//System.out.println("onClassEnd("+jc.getName()+") writing attribute "+attribute.getName());
				writeAccessMethods(attribute);
			}
			for(final Iterator i = copeClass.getUniqueConstraints().iterator(); i.hasNext(); )
			{
				// write unique finder methods
				final CopeUniqueConstraint constraint = (CopeUniqueConstraint)i.next();
				writeUniqueFinder(constraint);
			}
			for(final Iterator i = copeClass.getQualifiers().iterator(); i.hasNext(); )
			{
				final CopeQualifier qualifier = (CopeQualifier)i.next();
				writeQualifier(qualifier);
			}
			for(final Iterator i = copeClass.getVectors().iterator(); i.hasNext(); )
			{
				final CopeVector vector = (CopeVector)i.next();
				writeVector(vector);
			}
			for(final Iterator i = copeClass.getMedia().iterator(); i.hasNext(); )
			{
				final CopeMedia media = (CopeMedia)i.next();
				writeDataAccessMethods(media);
			}
			writeType(copeClass);
		}
	}

	/**
	 * Identation contract:
	 * This methods is called, when o stream is immediately after a line break,
	 * and it should return the o stream after immediately after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeGetterBody(final CopeAttribute attribute)
	throws IOException
	{
		o.write("\t\treturn ");
		if(attribute.accessOnItem)
		{
			if(attribute.isBoxed())
				o.write(attribute.getUnBoxingPrefix());
			o.write('(');
			o.write(attribute.persistentType);
			o.write(")get(");
			o.write(attribute.copeClass.getName());
			o.write('.');
			o.write(attribute.getName());
			o.write(')');
			if(attribute.isBoxed())
				o.write(attribute.getUnBoxingPostfix());
		}
		else
		{
			if(attribute.isBoxed())
				o.write(attribute.getUnBoxingPrefix());
			o.write(attribute.copeClass.getName());
			o.write('.');
			o.write(attribute.getName());
			o.write(".get(this)");
			if(attribute.isBoxed())
				o.write(attribute.getUnBoxingPostfix());
		}
		o.write(';');
		o.write(lineSeparator);
	}

	/**
	 * Identation contract:
	 * This methods is called, when o stream is immediately after a line break,
	 * and it should return the o stream after immediately after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeSetterBody(final CopeAttribute attribute)
	throws IOException
	{
		final SortedSet exceptionsToCatch = attribute.getExceptionsToCatchInSetter();
		writeTryCatchClausePrefix(exceptionsToCatch);
		if(attribute.accessOnItem)
		{
			o.write("\t\tset(");
			o.write(attribute.copeClass.getName());
			o.write('.');
			o.write(attribute.getName());
			o.write(',');
			if(attribute.isBoxed())
				o.write(attribute.getBoxingPrefix());
			o.write(attribute.getName());
			if(attribute.isBoxed())
				o.write(attribute.getBoxingPostfix());
		}
		else
		{
			o.write("\t\t");
			o.write(attribute.copeClass.getName());
			o.write('.');
			o.write(attribute.getName());
			o.write(".set(this,");
			if(attribute.isBoxed())
				o.write(attribute.getBoxingPrefix());
			o.write(attribute.getName());
			if(attribute.isBoxed())
				o.write(attribute.getBoxingPostfix());
		}
		o.write(");");
		o.write(lineSeparator);
		writeTryCatchClausePostfix(exceptionsToCatch);
	}
	
	/**
	 * Identation contract:
	 * This methods is called, when o stream is immediately after a line break,
	 * and it should return the o stream after immediately after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeToucherBody(final CopeAttribute attribute)
	throws IOException
	{
		final SortedSet exceptionsToCatch = attribute.getExceptionsToCatchInToucher();
		writeTryCatchClausePrefix(exceptionsToCatch);
		o.write("\t\ttouch(");
		o.write(attribute.copeClass.getName());
		o.write('.');
		o.write(attribute.getName());
		o.write(");");
		o.write(lineSeparator);
		writeTryCatchClausePostfix(exceptionsToCatch);
	}
	
	/**
	 * Identation contract:
	 * This methods is called, when o stream is immediately after a line break,
	 * and it should return the o stream after immediately after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeCheckerBody(final CopeHash hash)
	throws IOException
	{
		o.write("\t\treturn ");
		o.write(hash.copeClass.getName());
		o.write('.');
		o.write(hash.name);
		o.write(".check(this,");
		o.write(hash.name);
		o.write(");");
		o.write(lineSeparator);
	}

	/**
	 * Identation contract:
	 * This methods is called, when o stream is immediately after a line break,
	 * and it should return the o stream after immediately after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeSetterBody(final CopeHash hash)
	throws IOException
	{
		final CopeAttribute storage = hash.storageAttribute;
		final SortedSet exceptionsToCatch = storage.getExceptionsToCatchInSetter();
		writeTryCatchClausePrefix(exceptionsToCatch);
		o.write("\t\t");
		o.write(hash.copeClass.getName());
		o.write('.');
		o.write(hash.name);
		o.write(".set(this,");
		o.write(hash.name);
		o.write(");");
		o.write(lineSeparator);
		writeTryCatchClausePostfix(exceptionsToCatch);
	}
	
	private void writeTryCatchClausePrefix(final SortedSet exceptionsToCatch)
	throws IOException
	{
		if(!exceptionsToCatch.isEmpty())
		{
			o.write("\t\ttry");
			o.write(lineSeparator);
			o.write("\t\t{");
			o.write(lineSeparator);
			o.write('\t');
		}
	}
	
	private void writeTryCatchClausePostfix(final SortedSet exceptionsToCatch)
	throws IOException
	{
		if(!exceptionsToCatch.isEmpty())
		{
			o.write("\t\t}");
			o.write(lineSeparator);
			
			for(Iterator i = exceptionsToCatch.iterator(); i.hasNext(); )
			{
				final Class exceptionClass = (Class)i.next();
				o.write("\t\tcatch("+exceptionClass.getName()+" e)");
				o.write(lineSeparator);
				o.write("\t\t{");
				o.write(lineSeparator);
				o.write("\t\t\tthrow new "+NestingRuntimeException.class.getName()+"(e);");
				o.write(lineSeparator);
				o.write("\t\t}");
				o.write(lineSeparator);
			}
		}
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

}
