/*
 * Created on 22.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.exedio.cope.instrument;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.lib.AttributeValue;
import com.exedio.cope.lib.LengthViolationException;
import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.ReadOnlyViolationException;
import com.exedio.cope.lib.SystemException;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.UniqueViolationException;
import com.exedio.cope.lib.util.ReactivationConstructorDummy;

final class Generator
{
	private final Writer o;
	private final String lineSeparator;
	
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

	private void writeParameterDeclarationList(final Collection parameters)
	throws IOException
	{
		if(parameters!=null)
		{
			boolean first = true;
			for(Iterator i = parameters.iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					o.write(',');
				final String parameter = (String)i.next();
				o.write("final ");
				o.write(parameter);
				o.write(' ');
				o.write(lowerCamelCase(parameter));
			}
		}
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

	private final void writeCommentGenerated()
	throws IOException
	{
		o.write("\t * <p><small>"+Instrumentor.GENERATED+"</small>");
		o.write(lineSeparator);
	}
	
	private final void writeCommentFooter()
	throws IOException
	{
		o.write("\t *");
		o.write(lineSeparator);
		o.write(" */");
	}
	
	private static final HashMap constraintViolationText = new HashMap(5);
	
	static
	{
		constraintViolationText.put(NotNullViolationException.class, "not null");
		constraintViolationText.put(ReadOnlyViolationException.class, "read only");
		constraintViolationText.put(UniqueViolationException.class, "not unique");
	}

	private void writeConstructor(final PersistentClass javaClass)
	throws IOException
	{
		final List initialAttributes = javaClass.getInitialAttributes();
		final SortedSet constructorExceptions = javaClass.getConstructorExceptions();
		
		int constructorAccessModifier = javaClass.accessModifier;
		
		writeCommentHeader();
		o.write("\t * Constructs a new ");
		o.write(javaClass.getName());
		o.write(" with all the attributes initially needed.");
		o.write(lineSeparator);
		writeCommentGenerated();
		for(Iterator i = initialAttributes.iterator(); i.hasNext(); )
		{
			final PersistentAttribute initialAttribute = (PersistentAttribute)i.next();
			o.write("\t * @param initial");
			o.write(initialAttribute.getCamelCaseName());
			o.write(" the initial value for attribute {@link #");
			o.write(initialAttribute.getName());
			o.write("}.");
			o.write(lineSeparator);
			
			final int attributeAccessModifier = initialAttribute.accessModifier;
			if(constructorAccessModifier<attributeAccessModifier)
				constructorAccessModifier = attributeAccessModifier;
		}
		for(Iterator i = constructorExceptions.iterator(); i.hasNext(); )
		{
			final Class constructorException = (Class)i.next();
			o.write("\t * @throws ");
			o.write(constructorException.getName());
			o.write(" if");
			boolean first = true;
			for(Iterator j = initialAttributes.iterator(); j.hasNext(); )
			{
				final PersistentAttribute initialAttribute = (PersistentAttribute)j.next();
				if(!initialAttribute.getSetterExceptions().contains(constructorException))
					continue;

				if(first)
					first = false;
				else
					o.write(',');
				o.write(" initial");
				o.write(initialAttribute.getCamelCaseName());
			}
			o.write(" is ");
			o.write((String)constraintViolationText.get(constructorException));
			o.write('.');
			o.write(lineSeparator);
		}
		writeCommentFooter();
		o.write(JavaFeature.toAccessModifierString(constructorAccessModifier));
		o.write(javaClass.getName());
		o.write('(');
		
		boolean first = true;
		for(Iterator i = initialAttributes.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				o.write(',');
			final PersistentAttribute initialAttribute = (PersistentAttribute)i.next();
			o.write(lineSeparator);
			o.write("\t\t\t\tfinal ");
			o.write(initialAttribute.getBoxedType());
			o.write(" initial");
			o.write(initialAttribute.getCamelCaseName());
		}
		
		o.write(')');
		o.write(lineSeparator);
		writeThrowsClause(constructorExceptions);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tsuper(new "+AttributeValue.class.getName()+"[]{");
		o.write(lineSeparator);
		for(Iterator i = initialAttributes.iterator(); i.hasNext(); )
		{
			final PersistentAttribute initialAttribute = (PersistentAttribute)i.next();
			o.write("\t\t\tnew "+AttributeValue.class.getName()+"(");
			o.write(initialAttribute.getName());
			o.write(',');
			if(initialAttribute.isBoxed())
				o.write(initialAttribute.getBoxingPrefix());
			o.write("initial");
			o.write(initialAttribute.getCamelCaseName());
			if(initialAttribute.isBoxed())
				o.write(initialAttribute.getBoxingPostfix());
			o.write("),");
			o.write(lineSeparator);
		}
		o.write("\t\t});");
		o.write(lineSeparator);
		for(Iterator i = javaClass.getConstructorExceptions().iterator(); i.hasNext(); )
		{
			final Class exception = (Class)i.next();
			o.write("\t\tthrowInitial");
			o.write(getShortName(exception));
			o.write("();");
			o.write(lineSeparator);
		}
		o.write("\t}");
	}
	
	private void writeGenericConstructor(final PersistentClass persistentClass)
	throws IOException
	{
		writeCommentHeader();
		o.write("\t * Creates an item and sets the given attributes initially.");
		o.write(lineSeparator);
		writeCommentGenerated();
		writeCommentFooter();
		o.write("protected ");
		o.write(persistentClass.getName());
		o.write("(final "+AttributeValue.class.getName()+"[] initialAttributes)");
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tsuper(initialAttributes);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeReactivationConstructor(final PersistentClass persistentClass)
	throws IOException
	{
		final boolean abstractClass = persistentClass.isAbstract();
		writeCommentHeader();
		o.write("\t * Reactivation constructor. Used for internal purposes only.");
		o.write(lineSeparator);
		writeCommentGenerated();
		o.write("\t * @see Item#Item("
			+ ReactivationConstructorDummy.class.getName() + ",int)");
		o.write(lineSeparator);
		writeCommentFooter();
		o.write( abstractClass ? "protected " : "private " );
		o.write(persistentClass.getName());
		o.write("("+ReactivationConstructorDummy.class.getName()+" d,final int pk)");
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\tsuper(d,pk);");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeAccessMethods(final PersistentAttribute persistentAttribute)
	throws IOException
	{
		final String methodModifiers = Modifier.toString(persistentAttribute.getMethodModifiers());
		final String type = persistentAttribute.getBoxedType();
		final List qualifiers = persistentAttribute.qualifiers;

		// getter
		writeCommentHeader();
		o.write("\t * Returns the value of the persistent attribute {@link #");
		o.write(persistentAttribute.getName());
		o.write("}.");
		o.write(lineSeparator);
		writeCommentGenerated();
		writeCommentFooter();
		o.write(methodModifiers);
		o.write(' ');
		o.write(type);
		o.write(" get");
		o.write(persistentAttribute.getCamelCaseName());
		o.write('(');
		writeParameterDeclarationList(qualifiers);
		o.write(')');
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		writeGetterBody(persistentAttribute);
		o.write("\t}");
		
		// setter
		if(persistentAttribute.hasSetter())
		{
			writeCommentHeader();
			o.write("\t * Sets a new value for the persistent attribute {@link #");
			o.write(persistentAttribute.getName());
			o.write("}.");
			o.write(lineSeparator);
			writeCommentGenerated();
			writeCommentFooter();
			o.write(methodModifiers);
			o.write(" void set");
			o.write(persistentAttribute.getCamelCaseName());
			o.write('(');
			if(qualifiers!=null)
			{
				writeParameterDeclarationList(qualifiers);
				o.write(',');
			}
			o.write("final ");
			o.write(type);
			o.write(' ');
			o.write(persistentAttribute.getName());
			o.write(')');
			o.write(lineSeparator);
			writeThrowsClause(persistentAttribute.getSetterExceptions());
			o.write("\t{");
			o.write(lineSeparator);
			writeSetterBody(persistentAttribute);
			o.write("\t}");
		}
	}

	private void writeMediaGetterMethod(final PersistentAttribute mediaAttribute,
													final Class returnType,
													final String part,
													final String variant,
													final String comment)
	throws IOException
	{
		final String methodModifiers = Modifier.toString(mediaAttribute.getMethodModifiers());
		final List qualifiers = mediaAttribute.qualifiers;

		writeCommentHeader();
		o.write("\t * ");
		o.write(comment);
		o.write(" {@link #");
		o.write(mediaAttribute.getName());
		o.write("}.");
		o.write(lineSeparator);
		writeCommentGenerated();
		writeCommentFooter();
		o.write(methodModifiers);
		o.write(' ');
		o.write(returnType.getName());
		o.write(" get");
		o.write(mediaAttribute.getCamelCaseName());
		o.write(part);
		if(variant!=null)
			o.write(variant);
		o.write('(');
		writeParameterDeclarationList(qualifiers);
		o.write(')');
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\treturn getMedia");
		o.write(part);
		o.write("(this.");
		o.write(mediaAttribute.getName());
		if(variant!=null)
		{
			if(variant.length()>0)
			{
				o.write(",\"");
				o.write(variant);
				o.write('\"');
			}
			else
				o.write(",null");
		}
		o.write(");");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private void writeMediaAccessMethods(final PersistentMediaAttribute mediaAttribute)
	throws IOException
	{
		final String methodModifiers = Modifier.toString(mediaAttribute.getMethodModifiers());
		final List qualifiers = mediaAttribute.qualifiers;
		final String mimeMajor = mediaAttribute.mimeMajor;
		final String mimeMinor = mediaAttribute.mimeMinor;

		// getters
		writeMediaGetterMethod(mediaAttribute, String.class, "URL", "",
										"Returns a URL pointing to the data of the persistent attribute");
		final List mediaVariants = mediaAttribute.mediaVariants;
		if(mediaVariants!=null)
		{
			for(Iterator i = mediaVariants.iterator(); i.hasNext(); )
				writeMediaGetterMethod(mediaAttribute, String.class, "URL", (String)i.next(),
												"Returns a URL pointing to the varied data of the persistent attribute");
		}
		writeMediaGetterMethod(mediaAttribute, String.class, "MimeMajor", null,
										"Returns the major mime type of the persistent media attribute");
		writeMediaGetterMethod(mediaAttribute, String.class, "MimeMinor", null,
										"Returns the minor mime type of the persistent media attribute");
		writeMediaGetterMethod(mediaAttribute, InputStream.class, "Data", null,
										"Returns a stream for fetching the data of the persistent media attribute");
		
		// setters
		if(mediaAttribute.hasSetter())
		{
			writeCommentHeader();
			o.write("\t * Provides data for the persistent media attribute {@link #");
			o.write(mediaAttribute.getName());
			o.write("}.");
			o.write(lineSeparator);
			writeCommentGenerated();
			writeCommentFooter();
			o.write(methodModifiers);
			o.write(" void set");
			o.write(mediaAttribute.getCamelCaseName());
			o.write("Data(");
			if(qualifiers!=null)
			{
				writeParameterDeclarationList(qualifiers);
				o.write(',');
			}
			o.write("final " + InputStream.class.getName() + " data");
			if(mimeMajor==null)
				o.write(",final "+String.class.getName()+" mimeMajor");
			if(mimeMinor==null)
				o.write(",final "+String.class.getName()+" mimeMinor");
			o.write(')');
			final SortedSet setterExceptions = mediaAttribute.getSetterExceptions();
			writeThrowsClause(setterExceptions);
			if(setterExceptions.isEmpty())
				o.write("throws ");
			o.write(IOException.class.getName());
			o.write(lineSeparator);
			o.write("\t{");
			o.write(lineSeparator);
			
			final SortedSet exceptionsToCatch = new TreeSet(mediaAttribute.getExceptionsToCatchInSetter());
			exceptionsToCatch.remove(ReadOnlyViolationException.class);
			exceptionsToCatch.remove(LengthViolationException.class);
			exceptionsToCatch.remove(UniqueViolationException.class);
			if(!exceptionsToCatch.isEmpty())
			{
				o.write("\t\ttry");
				o.write(lineSeparator);
				o.write("\t\t{");
				o.write(lineSeparator);
				o.write('\t');
			}
			o.write("\t\tsetMediaData(this.");
			o.write(mediaAttribute.getName());
			o.write(",data");
			o.write(mimeMajor==null ? ",mimeMajor" : ",null");
			o.write(mimeMinor==null ? ",mimeMinor" : ",null");
			o.write(");");
			o.write(lineSeparator);
			if(!exceptionsToCatch.isEmpty())
			{
				o.write("\t\t}");
				o.write(lineSeparator);

				for(Iterator i = exceptionsToCatch.iterator(); i.hasNext(); )
					writeViolationExceptionCatchClause((Class)i.next());
			}
			o.write("\t}");
		}
	}
	
	private final void writeEquals(final PersistentAttribute persistentAttribute)
	throws IOException
	{
		o.write("equal(");
		o.write(persistentAttribute.getName());
		o.write(",searched");
		o.write(persistentAttribute.getCamelCaseName());
		o.write(')');
	}
	
	private void writeUniqueFinder(final PersistentUniqueConstraint constraint)
	throws IOException
	{
		final PersistentAttribute[] persistentAttributes = constraint.persistentAttributes;
		final String className = persistentAttributes[0].getParent().getName();
		
		writeCommentHeader();
		o.write("\t * Finds a ");
		o.write(lowerCamelCase(className));
		o.write(" by it's unique attributes.");
		o.write(lineSeparator);
		writeCommentGenerated();
		for(int i=0; i<persistentAttributes.length; i++)
		{
			o.write("\t * @param searched");
			o.write(persistentAttributes[i].getCamelCaseName());
			o.write(" shall be equal to attribute {@link #");
			o.write(persistentAttributes[i].getName());
			o.write("}.");
			o.write(lineSeparator);
		}
		writeCommentFooter();
		o.write(JavaAttribute.toAccessModifierString(constraint.accessModifier));
		o.write("static final ");
		o.write(className);
		o.write(" findBy");
		o.write(constraint.camelCaseName);
		
		o.write('(');
		final Set qualifiers = new HashSet();
		for(int i=0; i<persistentAttributes.length; i++)
		{
			if(i>0)
				o.write(',');
			final PersistentAttribute persistentAttribute = persistentAttributes[i];
			if(persistentAttribute.qualifiers != null)
				qualifiers.addAll(persistentAttribute.qualifiers);
			o.write("final ");
			o.write(persistentAttribute.getBoxedType());
			o.write(" searched");
			o.write(persistentAttribute.getCamelCaseName());
		}
		if(!qualifiers.isEmpty())
		{
			o.write(',');
			writeParameterDeclarationList(qualifiers);
		}
		o.write(')');
		o.write(lineSeparator);
		o.write("\t{");
		o.write(lineSeparator);
		o.write("\t\treturn (");
		o.write(className);
		o.write(")searchUnique(TYPE,");

		if(persistentAttributes.length==1)
			writeEquals(persistentAttributes[0]);
		else
		{
			o.write("and(");
			writeEquals(persistentAttributes[0]);
			for(int i = 1; i<persistentAttributes.length; i++)
			{
				o.write(',');
				writeEquals(persistentAttributes[i]);
			}
			o.write(')');
		}
		
		o.write(");");
		o.write(lineSeparator);
		o.write("\t}");
	}
	
	private final void writeType(final PersistentClass persistentClass)
	throws IOException
	{
		writeCommentHeader();
		o.write("\t * The persistent type information for ");
		o.write(lowerCamelCase(persistentClass.getName()));
		o.write(".");
		o.write(lineSeparator);
		writeCommentGenerated();
		writeCommentFooter();
		
		o.write("public static final "+Type.class.getName()+" TYPE = ");
		o.write(lineSeparator);

		o.write("\t\tnew "+Type.class.getName()+"(");
		o.write(persistentClass.getName());
		o.write(".class)");
		o.write(lineSeparator);

		o.write(";");
	}

	void writeClassFeatures(final PersistentClass persistentClass)
			throws IOException, InjectorParseException
	{
		if(!persistentClass.isInterface())
		{
			//System.out.println("onClassEnd("+jc.getName()+") writing");
			writeConstructor(persistentClass);
			if(persistentClass.isAbstract()) // TODO: create the constructor for all classes
				writeGenericConstructor(persistentClass);
			writeReactivationConstructor(persistentClass);
			for(final Iterator i = persistentClass.getPersistentAttributes().iterator(); i.hasNext(); )
			{
				// write setter/getter methods
				final PersistentAttribute persistentAttribute = (PersistentAttribute)i.next();
				//System.out.println("onClassEnd("+jc.getName()+") writing attribute "+persistentAttribute.getName());
				if(persistentAttribute instanceof PersistentMediaAttribute)
					writeMediaAccessMethods((PersistentMediaAttribute)persistentAttribute);
				else
					writeAccessMethods(persistentAttribute);
			}
			for(final Iterator i = persistentClass.getUniqueConstraints().iterator(); i.hasNext(); )
			{
				// write unique finder methods
				final PersistentUniqueConstraint constraint = (PersistentUniqueConstraint)i.next();
				writeUniqueFinder(constraint);
			}
			writeType(persistentClass);
		}
	}

	// ----------------- methods for a new interface abstracting the persistence
	// ----------------- implementation used, e.g. EJB.

	/**
	 * Identation contract:
	 * This methods is called, when o stream is immediatly after a line break,
	 * and it should return the o stream after immediatly after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeGetterBody(final PersistentAttribute attribute)
	throws IOException
	{
		o.write("\t\treturn ");
		if(attribute.isBoxed())
			o.write(attribute.getUnBoxingPrefix());
		o.write('(');
		o.write(attribute.getPersistentType());
		o.write(")getAttribute(this.");
		o.write(attribute.getName());
		o.write(')');
		if(attribute.isBoxed())
			o.write(attribute.getUnBoxingPostfix());
		o.write(';');
		o.write(lineSeparator);
	}

	/**
	 * Identation contract:
	 * This methods is called, when o stream is immediatly after a line break,
	 * and it should return the o stream after immediatly after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeSetterBody(final PersistentAttribute attribute)
	throws IOException
	{
		final SortedSet exceptionsToCatch = attribute.getExceptionsToCatchInSetter();

		if(!exceptionsToCatch.isEmpty())
		{
			o.write("\t\ttry");
			o.write(lineSeparator);
			o.write("\t\t{");
			o.write(lineSeparator);
			o.write('\t');
		}
		o.write("\t\tsetAttribute(this.");
		o.write(attribute.getName());
		o.write(',');
		if(attribute.isBoxed())
			o.write(attribute.getBoxingPrefix());
		o.write(attribute.getName());
		if(attribute.isBoxed())
			o.write(attribute.getBoxingPostfix());
		o.write(");");
		o.write(lineSeparator);
		if(!exceptionsToCatch.isEmpty())
		{
			o.write("\t\t}");
			o.write(lineSeparator);
			
			for(Iterator i = exceptionsToCatch.iterator(); i.hasNext(); )
				writeViolationExceptionCatchClause((Class)i.next());
		}
	}
	
	private void writeViolationExceptionCatchClause(final Class exceptionClass)
	throws IOException
	{
		o.write("\t\tcatch("+exceptionClass.getName()+" e)");
		o.write(lineSeparator);
		o.write("\t\t{");
		o.write(lineSeparator);
		o.write("\t\t\tthrow new "+SystemException.class.getName()+"(e);");
		o.write(lineSeparator);
		o.write("\t\t}");
		o.write(lineSeparator);
	}

}
