
package com.exedio.cope.instrument;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;
import java.lang.reflect.Modifier;
import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.AttributeValue;
import com.exedio.cope.lib.ConstraintViolationException;
import com.exedio.cope.lib.EnumerationValue;
import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.ReadOnlyViolationException;
import com.exedio.cope.lib.SystemException;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.UniqueConstraint;
import com.exedio.cope.lib.UniqueViolationException;
import com.exedio.cope.lib.util.ClassComparator;
import com.exedio.cope.lib.util.ReactivationConstructorDummy;

public final class Instrumentor implements InjectionConsumer
{
	private final Writer output;
	
	/**
	 * Holds several properties of the class currently
	 * worked on.
	 */
	private JavaClass class_state=null;
	
	/**
	 * Collects the class states of outer classes,
	 * when operating on a inner class.
	 * @see #class_state
	 * @element-type InstrumentorClass
	 */
	private ArrayList class_state_stack=new ArrayList();
	
	protected final String lineSeparator;
	
	/**
	 * The last file level doccomment that was read.
	 */
	private String lastFileDocComment = null;
	
	public Instrumentor(Writer output)
	{
		this.output=output;
		
		final String systemLineSeparator = System.getProperty("line.separator");
		if(systemLineSeparator==null)
		{
			System.out.println("warning: property \"line.separator\" is null, using LF (unix style).");
			lineSeparator = "\n";
		}
		else
			lineSeparator = systemLineSeparator;
	}
	
	public void onPackage(JavaFile javafile)
	throws InjectorParseException
	{
	}
	
	public void onImport(String importname)
	{
	}
	
	private boolean discardnextfeature=false;
	
	/**
	 * Tag name for persistent classes.
	 */
	private static final String PERSISTENT_CLASS = "persistent";

	/**
	 * Tag name for persistent attributes.
	 */
	private static final String PERSISTENT_ATTRIBUTE = PERSISTENT_CLASS;

	/**
	 * Tag name for unique attributes.
	 */
	private static final String UNIQUE_ATTRIBUTE = "unique";

	/**
	 * Tag name for read-only attributes.
	 */
	private static final String READ_ONLY_ATTRIBUTE = "read-only";
	
	/**
	 * Tag name for not-null attributes.
	 */
	private static final String NOT_NULL_ATTRIBUTE = "not-null";
	
	/**
	 * Tag name for mapped attributes.
	 */
	private static final String MAPPED_ATTRIBUTE = "mapped";
	
	/**
	 * Tag name for one qualifier of qualified attributes.
	 */
	private static final String ATTRIBUTE_QUALIFIER = "qualifier";
	
	/**
	 * Tag name for one variant of media attributes.
	 */
	private static final String VARIANT_MEDIA_ATTRIBUTE = "variant";
	
	/**
	 * Tag name for media attributes with a constant major mime type.
	 */
	private static final String MIME_MAJOR = "mime-major";
	
	/**
	 * Tag name for media attributes with a constant minor mime type.
	 */
	private static final String MIME_MINOR = "mime-minor";
	
	/**
	 * Tag name for enumeration values of enumeration attributes.
	 */
	private static final String ENUMERATION_VALUE = "value";
	
	/**
	 * All generated class features get this doccomment tag.
	 */
	private static final String GENERATED = "generated";

	private List uniqueConstraints=null;
	
	private void handleClassComment(final JavaClass jc, final String docComment)
	{
		if(containsTag(docComment, PERSISTENT_CLASS))
			jc.setPersistent();
		
		final String uniqueConstraint = Injector.findWholeDocTag(docComment, UNIQUE_ATTRIBUTE);
		if(uniqueConstraint!=null)
		{
			if(uniqueConstraints==null)
				uniqueConstraints = new ArrayList();
			uniqueConstraints.add(uniqueConstraint);
		}
	}
	
	public void onClass(final JavaClass jc)
	{
		//System.out.println("onClass("+jc.getName()+")");

		discardnextfeature=false;
		
		class_state_stack.add(class_state);
		class_state=jc;
		
		if(lastFileDocComment != null)
		{
			handleClassComment(jc, lastFileDocComment);
			lastFileDocComment = null;
		}
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
					output.write(',');
				final String parameter = (String)i.next();
				output.write("final ");
				output.write(parameter);
				output.write(' ');
				output.write(lowerCamelCase(parameter));
			}
		}
	}

	private void writeParameterCallList(final Collection parameters)
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
					output.write(',');
				final String parameter = (String)i.next();
				output.write(lowerCamelCase(parameter));
			}
		}
	}

	private void writeThrowsClause(final Collection exceptions)
	throws IOException
	{
		if(!exceptions.isEmpty())
		{
			output.write("\t\t\tthrows");
			boolean first = true;
			for(final Iterator i = exceptions.iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					output.write(',');
				output.write(lineSeparator);
				output.write("\t\t\t\t");
				output.write(((Class)i.next()).getName());
			}
			output.write(lineSeparator);
		}
	}

	private final void writeCommentHeader()
	throws IOException
	{
		output.write("/**");
		output.write(lineSeparator);
		output.write(lineSeparator);
		output.write("\t **");
		output.write(lineSeparator);
	}

	private final void writeCommentFooter()
	throws IOException
	{
		output.write("\t * @"+GENERATED);
		output.write(lineSeparator);
		output.write("\t *");
		output.write(lineSeparator);
		output.write(" */");
	}
	
	
	private static final HashMap constraintViolationText = new HashMap(5);
	
	static
	{
		constraintViolationText.put(NotNullViolationException.class, "not null");
		constraintViolationText.put(ReadOnlyViolationException.class, "read only");
		constraintViolationText.put(UniqueViolationException.class, "not unique");
	}

	public void writeConstructor(final JavaClass javaClass)
	throws IOException
	{
		final List initialAttributes = javaClass.getInitialAttributes();
		final SortedSet constructorExceptions = javaClass.getContructorExceptions();

		writeCommentHeader();
		output.write("\t * Constructs a new ");
		output.write(javaClass.getName());
		output.write(" with all the attributes initially needed.");
		for(Iterator i = initialAttributes.iterator(); i.hasNext(); )
		{
			final JavaAttribute initialAttribute = (JavaAttribute)i.next();
			output.write(lineSeparator);
			output.write("\t * @param initial");
			output.write(initialAttribute.getCamelCaseName());
			output.write(" the initial value for attribute {@link #");
			output.write(initialAttribute.getName());
			output.write("}.");
		}
		for(Iterator i = constructorExceptions.iterator(); i.hasNext(); )
		{
			final Class constructorException = (Class)i.next();
			output.write(lineSeparator);
			output.write("\t * @throws ");
			output.write(constructorException.getName());
			output.write(" if");
			boolean first = true;
			for(Iterator j = initialAttributes.iterator(); j.hasNext(); )
			{
				final JavaAttribute initialAttribute = (JavaAttribute)j.next();
				if(!initialAttribute.getSetterExceptions().contains(constructorException))
					continue;

				if(first)
					first = false;
				else
					output.write(',');
				output.write(" initial");
				output.write(initialAttribute.getCamelCaseName());
			}
			output.write(" is ");
			output.write((String)constraintViolationText.get(constructorException));
			output.write('.');
		}
		output.write(lineSeparator);
		writeCommentFooter();
		output.write(Modifier.toString(javaClass.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)));
		output.write(' ');
		output.write(javaClass.getName());
		output.write('(');
		
		boolean first = true;
		for(Iterator i = initialAttributes.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				output.write(',');
			final JavaAttribute initialAttribute = (JavaAttribute)i.next();
			output.write(lineSeparator);
			output.write("\t\t\t\tfinal ");
			output.write(initialAttribute.getBoxedType());
			output.write(" initial");
			output.write(initialAttribute.getCamelCaseName());
		}
		
		output.write(')');
		output.write(lineSeparator);
		writeThrowsClause(constructorExceptions);
		output.write("\t{");
		output.write(lineSeparator);
		output.write("\t\tsuper(TYPE, new "+AttributeValue.class.getName()+"[]{");
		output.write(lineSeparator);
		for(Iterator i = initialAttributes.iterator(); i.hasNext(); )
		{
			final JavaAttribute initialAttribute = (JavaAttribute)i.next();
			output.write("\t\t\tnew "+AttributeValue.class.getName()+"(");
			output.write(initialAttribute.getName());
			output.write(',');
			if(initialAttribute.isBoxed())
				output.write(initialAttribute.getBoxingPrefix());
			output.write("initial");
			output.write(initialAttribute.getCamelCaseName());
			if(initialAttribute.isBoxed())
				output.write(initialAttribute.getBoxingPostfix());
			output.write("),");
			output.write(lineSeparator);
		}
		output.write("\t\t});");
		output.write(lineSeparator);
		for(Iterator i = javaClass.getContructorExceptions().iterator(); i.hasNext(); )
		{
			final Class exception = (Class)i.next();
			output.write("\t\tthrowInitial");
			output.write(getShortName(exception));
			output.write("();");
			output.write(lineSeparator);
		}
		output.write("\t}");
	}
	
	public void writeReactivationConstructor(final JavaClass javaClass)
	throws IOException
	{
		writeCommentHeader();
		output.write("\t * Reactivation constructor. Used for internal purposes only.");
		output.write(lineSeparator);
		output.write("\t * @see Item#Item(Type, int)");
		output.write(lineSeparator);
		writeCommentFooter();
		output.write("private ");
		output.write(javaClass.getName());
		output.write("("+ReactivationConstructorDummy.class.getName()+" d, final int pk)");
		output.write(lineSeparator);
		output.write("\t{");
		output.write(lineSeparator);
		output.write("\t\tsuper(TYPE, pk);");
		output.write(lineSeparator);
		output.write("\t}");
	}
	
	private static final int ENUMERATION_NUMBER_AUTO_INCREMENT = 100;

	private void writeEnumerationClass(final JavaAttribute enumerationAttribute)
	throws IOException
	{
		// deactivated, since the parser cannot remove generated inner classes.
		if(true)
			return;

		writeCommentHeader();
		output.write("\t * A class representing the possible states of the persistent enumeration attribute {@link #");
		output.write(enumerationAttribute.getName());
		output.write("}.");
		output.write(lineSeparator);
		writeCommentFooter();
		
		output.write("public static final class ");
		output.write(enumerationAttribute.getCamelCaseName());
		output.write(" extends "+EnumerationValue.class.getName());
		output.write(lineSeparator);
		output.write("\t{");
		output.write(lineSeparator);
		int enumerationNumber = ENUMERATION_NUMBER_AUTO_INCREMENT;
		for(Iterator i = enumerationAttribute.getEnumerationValues().iterator(); i.hasNext(); )
		{
			final String enumerationValue = (String)i.next();

			output.write("\t\tpublic static final int ");
			output.write(enumerationValue);
			output.write("NUM = ");
			output.write(Integer.toString(enumerationNumber));
			output.write(';');
			output.write(lineSeparator);

			output.write("\t\tpublic static final ");
			output.write(enumerationAttribute.getCamelCaseName());
			output.write(' ');
			output.write(enumerationValue);
			output.write(" = new ");
			output.write(enumerationAttribute.getCamelCaseName());
			output.write('(');
			output.write(Integer.toString(enumerationNumber));
			output.write(", \"");
			output.write(enumerationValue);
			output.write("\");");
			output.write(lineSeparator);
			output.write(lineSeparator);
			
			enumerationNumber += ENUMERATION_NUMBER_AUTO_INCREMENT;
		}
		output.write("\t\tprivate ");
		output.write(enumerationAttribute.getCamelCaseName());
		output.write("(final int number, final String code)");
		output.write(lineSeparator);
		output.write("\t\t{");
		output.write(lineSeparator);
		output.write("\t\t\tsuper(number, code);");
		output.write(lineSeparator);
		output.write("\t\t}");
		output.write(lineSeparator);
		output.write("\t}");
	}

	private void writeAccessMethods(final JavaAttribute persistentAttribute)
	throws IOException
	{
		if(persistentAttribute.isEnumerationAttribute())
			writeEnumerationClass(persistentAttribute);

		final String methodModifiers = Modifier.toString(persistentAttribute.getMethodModifiers());
		final String type = persistentAttribute.getBoxedType();
		final List qualifiers = persistentAttribute.getQualifiers();

		// getter
		writeCommentHeader();
		output.write("\t * Returns the value of the persistent attribute {@link #");
		output.write(persistentAttribute.getName());
		output.write("}.");
		output.write(lineSeparator);
		writeCommentFooter();
		output.write(methodModifiers);
		output.write(' ');
		output.write(type);
		output.write(" get");
		output.write(persistentAttribute.getCamelCaseName());
		output.write('(');
		writeParameterDeclarationList(qualifiers);
		output.write(')');
		output.write(lineSeparator);
		output.write("\t{");
		output.write(lineSeparator);
		writeGetterBody(output, persistentAttribute);
		output.write("\t}");
		
		// setter
		if(persistentAttribute.hasSetter())
		{
			writeCommentHeader();
			output.write("\t * Sets a new value for the persistent attribute {@link #");
			output.write(persistentAttribute.getName());
			output.write("}.");
			output.write(lineSeparator);
			writeCommentFooter();
			output.write(methodModifiers);
			output.write(" void set");
			output.write(persistentAttribute.getCamelCaseName());
			output.write('(');
			if(qualifiers!=null)
			{
				writeParameterDeclarationList(qualifiers);
				output.write(',');
			}
			output.write("final ");
			output.write(type);
			output.write(' ');
			output.write(persistentAttribute.getName());
			output.write(')');
			output.write(lineSeparator);
			writeThrowsClause(persistentAttribute.getSetterExceptions());
			output.write("\t{");
			output.write(lineSeparator);
			writeSetterBody(output, persistentAttribute);
			output.write("\t}");
		}
	}

	private void writeMediaGetterMethod(final JavaAttribute mediaAttribute,
													final Class returnType,
													final String part,
													final String variant,
													final String literal,
													final String comment)
	throws IOException
	{
		final String methodModifiers = Modifier.toString(mediaAttribute.getMethodModifiers());
		final List qualifiers = mediaAttribute.getQualifiers();

		writeCommentHeader();
		output.write("\t * ");
		output.write(comment);
		output.write(" {@link #");
		output.write(mediaAttribute.getName());
		output.write("}.");
		output.write(lineSeparator);
		writeCommentFooter();
		output.write(methodModifiers);
		output.write(' ');
		output.write(returnType.getName());
		output.write(" get");
		output.write(mediaAttribute.getCamelCaseName());
		output.write(part);
		if(variant!=null)
			output.write(variant);
		output.write('(');
		writeParameterDeclarationList(qualifiers);
		output.write(')');
		output.write(lineSeparator);
		output.write("\t{");
		output.write(lineSeparator);
		output.write("\t\treturn ");
		if(literal!=null)
		{
			output.write('\"');
			output.write(literal);
			output.write("\";");
		}
		else
		{
			output.write("getMedia");
			output.write(part);
			output.write("(this.");
			output.write(mediaAttribute.getName());
			if(variant!=null)
			{
				if(variant.length()>0)
				{
					output.write(",\"");
					output.write(variant);
					output.write('\"');
				}
				else
					output.write(",null");
			}
			if(qualifiers!=null)
			{
				output.write(",new Object[]{");
				writeParameterCallList(qualifiers);
				output.write('}');
			}
			output.write(");");
		}
		output.write(lineSeparator);
		output.write("\t}");
	}
	
	private void writeMediaAccessMethods(final JavaAttribute mediaAttribute)
	throws IOException
	{
		final String methodModifiers = Modifier.toString(mediaAttribute.getMethodModifiers());
		final List qualifiers = mediaAttribute.getQualifiers();
		final String mimeMajor = mediaAttribute.getMimeMajor();
		final String mimeMinor = mediaAttribute.getMimeMinor();

		// getters
		writeMediaGetterMethod(mediaAttribute, String.class, "URL", "", null,
										"Returns a URL pointing to the data of the persistent attribute");
		final List mediaVariants = mediaAttribute.getMediaVariants();
		if(mediaVariants!=null)
		{
			for(Iterator i = mediaVariants.iterator(); i.hasNext(); )
				writeMediaGetterMethod(mediaAttribute, String.class, "URL", (String)i.next(), null,
												"Returns a URL pointing to the varied data of the persistent attribute");
		}
		writeMediaGetterMethod(mediaAttribute, String.class, "MimeMajor", null, mimeMajor,
										"Returns the major mime type of the persistent media attribute");
		writeMediaGetterMethod(mediaAttribute, String.class, "MimeMinor", null, mimeMinor,
										"Returns the minor mime type of the persistent media attribute");
		writeMediaGetterMethod(mediaAttribute, InputStream.class, "Data", null, null,
										"Returns a stream for fetching the data of the persistent media attribute");
		
		// setters
		if(mediaAttribute.hasSetter())
		{
			writeCommentHeader();
			output.write("\t * Provides data for the persistent media attribute {@link #");
			output.write(mediaAttribute.getName());
			output.write("}.");
			output.write(lineSeparator);
			writeCommentFooter();
			output.write(methodModifiers);
			output.write(" void set");
			output.write(mediaAttribute.getCamelCaseName());
			output.write("Data(");
			if(qualifiers!=null)
			{
				writeParameterDeclarationList(qualifiers);
				output.write(',');
			}
			output.write("final " + OutputStream.class.getName() + " data");
			if(mimeMajor==null)
				output.write(",final "+String.class.getName()+" mimeMajor");
			if(mimeMinor==null)
				output.write(",final "+String.class.getName()+" mimeMinor");
			output.write(')');
			final SortedSet setterExceptions = mediaAttribute.getSetterExceptions();
			writeThrowsClause(setterExceptions);
			if(setterExceptions.isEmpty())
				output.write("throws ");
			output.write(IOException.class.getName());
			output.write(lineSeparator);
			output.write("\t{");
			output.write(lineSeparator);
			
			final SortedSet exceptionsToCatch = new TreeSet(mediaAttribute.getExceptionsToCatchInSetter());
			exceptionsToCatch.remove(ReadOnlyViolationException.class);
			exceptionsToCatch.remove(UniqueViolationException.class);
			if(!exceptionsToCatch.isEmpty())
			{
				output.write("\t\ttry");
				output.write(lineSeparator);
				output.write("\t\t{");
				output.write(lineSeparator);
				output.write('\t');
			}
			output.write("\t\tsetMediaData(this.");
			output.write(mediaAttribute.getName());
			if(qualifiers!=null)
			{
				output.write(",new Object[]{");
				writeParameterCallList(qualifiers);
				output.write('}');
			}
			output.write(",data");
			output.write(mimeMajor==null ? ",mimeMajor" : ",null");
			output.write(mimeMinor==null ? ",mimeMinor" : ",null");
			output.write(");");
			output.write(lineSeparator);
			if(!exceptionsToCatch.isEmpty())
			{
				output.write("\t\t}");
				output.write(lineSeparator);

				for(Iterator i = exceptionsToCatch.iterator(); i.hasNext(); )
					writeViolationExceptionCatchClause(output, (Class)i.next());
			}
			output.write("\t}");
		}
	}
	
	private final void writeEquals(final JavaAttribute attribute)
	throws IOException
	{
		output.write("equal(");
		output.write(attribute.getName());
		output.write(",searched");
		output.write(attribute.getCamelCaseName());
		output.write(')');
	}
	
	private void writeUniqueFinder(final JavaAttribute[] persistentAttributes)
	throws IOException, InjectorParseException
	{
		int modifiers = -1;
		for(int i=0; i<persistentAttributes.length; i++)
		{
			if(modifiers==-1)
				modifiers = persistentAttributes[i].getMethodModifiers();
			else 
			{
				if(modifiers!=persistentAttributes[i].getMethodModifiers())
					throw new InjectorParseException("Tried to write unique finder and found attribues with different modifiers");
			}
		}
		final String methodModifiers = Modifier.toString(modifiers|Modifier.STATIC);
		final String className = persistentAttributes[0].getParent().getName();
		
		writeCommentHeader();
		output.write("\t * Finds a ");
		output.write(lowerCamelCase(className));
		output.write(" by it's unique attributes");
		for(int i=0; i<persistentAttributes.length; i++)
		{
			output.write(lineSeparator);
			output.write("\t * @param searched");
			output.write(persistentAttributes[i].getCamelCaseName());
			output.write(" shall be equal to attribute {@link #");
			output.write(persistentAttributes[i].getName());
			output.write("}.");
		}
		output.write(lineSeparator);
		writeCommentFooter();
		output.write(methodModifiers);
		output.write(' ');
		output.write(className);
		
		boolean first=true;
		for(int i=0; i<persistentAttributes.length; i++)
		{
			if(first)
			{
				output.write(" findBy");
				first = false;
			}
			else
				output.write("And");
			output.write(persistentAttributes[i].getCamelCaseName());
		}
		
		output.write('(');
		final Set qualifiers = new HashSet();
		for(int i=0; i<persistentAttributes.length; i++)
		{
			if(i>0)
				output.write(',');
			final JavaAttribute persistentAttribute = (JavaAttribute)persistentAttributes[i];
			if(persistentAttribute.getQualifiers() != null)
				qualifiers.addAll(persistentAttribute.getQualifiers());
			output.write("final ");
			output.write(persistentAttribute.getPersistentType());
			output.write(" searched");
			output.write(persistentAttribute.getCamelCaseName());
		}
		if(!qualifiers.isEmpty())
		{
			output.write(',');
			writeParameterDeclarationList(qualifiers);
		}
		output.write(')');
		output.write(lineSeparator);
		output.write("\t{");
		output.write(lineSeparator);
		output.write("\t\treturn (");
		output.write(className);
		output.write(")searchUnique(TYPE,");

		if(persistentAttributes.length==1)
			writeEquals(persistentAttributes[0]);
		else
		{
			output.write("and(");
			writeEquals(persistentAttributes[0]);
			for(int i = 1; i<persistentAttributes.length; i++)
			{
				output.write(',');
				writeEquals(persistentAttributes[i]);
			}
			output.write(')');
		}
		
		output.write(");");
		output.write(lineSeparator);
		output.write("\t}");
	}
	
	private final void writeType(final JavaClass javaClass)
	throws IOException
	{
		writeCommentHeader();
		output.write("\t * The persistent type information for ");
		output.write(lowerCamelCase(javaClass.getName()));
		output.write(".");
		output.write(lineSeparator);
		writeCommentFooter();
		
		// the TYPE variable
		output.write("public static final "+Type.class.getName()+" TYPE = ");
		output.write(lineSeparator);
		
		// open the constructor of type
		output.write("\t\tnew "+Type.class.getName()+"(");
		output.write(lineSeparator);
		
		// the class itself
		output.write("\t\t\t");
		output.write(javaClass.getName());
		output.write(".class,");
		output.write(lineSeparator);
		
		// the attributes of the class
		output.write("\t\t\tnew "+Attribute.class.getName()+"[]{");
		output.write(lineSeparator);
		for(Iterator i = javaClass.getPersistentAttributes().iterator(); i.hasNext(); )
		{
			final JavaAttribute persistentAttribute = (JavaAttribute)i.next();
			output.write("\t\t\t\t");
			output.write(persistentAttribute.getName());
			output.write(',');
			output.write(lineSeparator);
		}
		output.write("\t\t\t},");
		output.write(lineSeparator);
		
		// the unique contraints of the class
		output.write("\t\t\tnew "+UniqueConstraint.class.getName()+"[]{");
		output.write(lineSeparator);
		for(Iterator i = javaClass.getUniqueConstraints().iterator(); i.hasNext(); )
		{
			final JavaAttribute[] uniqueConstraint = (JavaAttribute[])i.next();
			if(uniqueConstraint.length==1)
			{
				// shorter notation, if unique contraint does not cover multive attributes
				output.write("\t\t\t\tnew "+UniqueConstraint.class.getName()+'(');
				output.write(uniqueConstraint[0].getName());
				output.write("),");
			}
			else
			{
				// longer notation otherwise
				output.write("\t\t\t\tnew "+UniqueConstraint.class.getName()+"(new "+Attribute.class.getName()+"[]{");
				for(int j = 0; j<uniqueConstraint.length; j++)
				{
					output.write(uniqueConstraint[j].getName());
					output.write(',');
				}
				output.write("}),");
			}
			output.write(lineSeparator);
		}
		output.write("\t\t\t},");
		output.write(lineSeparator);
		
		// the runnable initializing attributes
		output.write("\t\t\tnew Runnable()");
		output.write(lineSeparator);
		output.write("\t\t\t{");
		output.write(lineSeparator);
		output.write("\t\t\t\tpublic void run()");
		output.write(lineSeparator);
		output.write("\t\t\t\t{");
		output.write(lineSeparator);
		for(Iterator i = javaClass.getPersistentAttributes().iterator(); i.hasNext(); )
		{
			final JavaAttribute persistentAttribute = (JavaAttribute)i.next();
			output.write("\t\t\t\t\t");
			output.write(persistentAttribute.getName());
			output.write(".initialize(\"");
			output.write(persistentAttribute.getName());
			output.write("\",");
			output.write(persistentAttribute.isReadOnly() ? "true": "false");
			output.write(',');
			output.write(persistentAttribute.isNotNull() ? "true": "false");
			//private List qualifiers = null;
			output.write(");");
			output.write(lineSeparator);
		}
		output.write("\t\t\t\t}");
		output.write(lineSeparator);
		output.write("\t\t\t}");
		output.write(lineSeparator);
		
		// close the constructor of Type
		output.write("\t\t)");
		output.write(lineSeparator);
		output.write(";");
	}
	
	public void onClassEnd(JavaClass jc)
	throws IOException, InjectorParseException
	{
		if(uniqueConstraints != null)
		{
			for( final Iterator i=uniqueConstraints.iterator(); i.hasNext(); )
			{
				final String uniqueConstraint=(String)i.next();
				final List attributes = new ArrayList();
				for(final StringTokenizer t=new StringTokenizer(uniqueConstraint, " "); t.hasMoreTokens(); )
				{
					final String attributeName = t.nextToken();
					final JavaAttribute ja = jc.getPersistentAttribute(attributeName);
					if(ja==null)
						throw new InjectorParseException("Attribute with name "+attributeName+" does not exist!");
					attributes.add(ja);
				}
				if(attributes.isEmpty())
					throw new InjectorParseException("No attributes found in unique constraint "+uniqueConstraint);
				jc.makeUnique((JavaAttribute[])attributes.toArray(new JavaAttribute[]{}));
			}
		}
		//System.out.println("onClassEnd("+jc.getName()+")");

		if(!jc.isInterface() && jc.isPersistent())
		{
			writeConstructor(jc);
			writeReactivationConstructor(jc);
			for(final Iterator i = jc.getPersistentAttributes().iterator(); i.hasNext(); )
			{
				// write setter/getter methods
				final JavaAttribute persistentAttribute = (JavaAttribute)i.next();
				if(persistentAttribute.isMediaPersistentType())
					writeMediaAccessMethods(persistentAttribute);
				else
					writeAccessMethods(persistentAttribute);
			}
			for(final Iterator i = jc.getUniqueConstraints().iterator(); i.hasNext(); )
			{
				// write unique finder methods
				final JavaAttribute[] persistentAttributes = (JavaAttribute[])i.next();
				writeUniqueFinder(persistentAttributes);
			}
			writeType(jc);
		}
		
		if(class_state!=jc)
			throw new RuntimeException();
		class_state=(JavaClass)(class_state_stack.remove(class_state_stack.size()-1));
	}
	
	public void onBehaviourHeader(JavaBehaviour jb)
	throws java.io.IOException
	{
		output.write(jb.getLiteral());
	}
	
	public void onAttributeHeader(JavaAttribute ja)
	{
	}
	
	public void onClassFeature(final JavaFeature jf, final String docComment)
	throws IOException, InjectorParseException
	{
		//System.out.println("onClassFeature("+jf.getName()+" "+docComment+")");
		if(!class_state.isInterface())
		{
			if(jf instanceof JavaAttribute &&
			Modifier.isFinal(jf.getModifiers()) &&
			Modifier.isStatic(jf.getModifiers()) &&
			!discardnextfeature &&
			containsTag(docComment, PERSISTENT_ATTRIBUTE))
			{
				final String type = jf.getType();
				final JavaAttribute ja = (JavaAttribute)jf;
				final String persistentType;
				if("IntegerAttribute".equals(type))
					persistentType = "Integer";
				else if("BooleanAttribute".equals(type))
					persistentType = "Boolean";
				else if("StringAttribute".equals(type))
					persistentType = "String";
				else if("EnumerationAttribute".equals(type))
				{
					persistentType = ja.getCamelCaseName();
				}
				else if("ItemAttribute".equals(type))
				{
					persistentType = Injector.findDocTag(docComment, PERSISTENT_ATTRIBUTE);
				}
				else if("MediaAttribute".equals(type))
				{
					persistentType = JavaAttribute.MEDIA_TYPE;
				}
				else
					throw new RuntimeException();

				ja.makePersistent(persistentType);

				if(containsTag(docComment, UNIQUE_ATTRIBUTE))
					ja.getParent().makeUnique(new JavaAttribute[]{ja});
				
				if(containsTag(docComment, READ_ONLY_ATTRIBUTE))
					ja.makeReadOnly();
				
				if(containsTag(docComment, NOT_NULL_ATTRIBUTE))
					ja.makeNotNull();

				if(containsTag(docComment, MAPPED_ATTRIBUTE))
					ja.makeMapped();
				
				final String qualifier = Injector.findDocTag(docComment, ATTRIBUTE_QUALIFIER);
				if(qualifier!=null)
					ja.makeQualified(Collections.singletonList(qualifier));

				final String variant = Injector.findDocTag(docComment, VARIANT_MEDIA_ATTRIBUTE);
				if(variant!=null)
					ja.makeMediaVarianted(Collections.singletonList(variant));

				final String mimeMajor = Injector.findDocTag(docComment, MIME_MAJOR);
				final String mimeMinor = Injector.findDocTag(docComment, MIME_MINOR);
				if(mimeMajor!=null || mimeMinor!=null)
					ja.contrainMediaMime(mimeMajor, mimeMinor);

				final String enumerationValue = Injector.findDocTag(docComment, ENUMERATION_VALUE);
				if(enumerationValue!=null)
					ja.makeEnumerationAttribute(Collections.singletonList(enumerationValue));
			}
		}
		discardnextfeature=false;
	}
	
	public boolean onDocComment(String docComment)
	throws IOException
	{
		//System.out.println("onDocComment("+docComment+")");

		if(containsTag(docComment, GENERATED))
		{
			discardnextfeature=true;
			return false;
		}
		else
		{
			output.write(docComment);
			return true;
		}
	}
	
	public void onFileDocComment(String docComment)
	throws IOException
	{
		//System.out.println("onFileDocComment("+docComment+")");
		
		output.write(docComment);
		
		if (class_state != null)
		{
			// handle doccomment immediately
			handleClassComment(class_state, docComment);
		}
		else
		{
			// remember to be handled as soon as we know what class we're talking about
			lastFileDocComment = docComment;
		}
	}
	
	public void onFileEnd()
	{
		if(!class_state_stack.isEmpty())
			throw new RuntimeException();
	}
	
	private static final boolean containsTag(final String docComment, final String tagName)
	{
		return docComment!=null && docComment.indexOf('@'+tagName)>=0 ;
	}

	
	// ----------------- methods for a new interface abstracting the persistence
	// ----------------- implementation used, e.g. EJB.

	/**
	 * Identation contract:
	 * This methods is called, when output stream is immediatly after a line break,
	 * and it should return the output stream after immediatly after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeGetterBody(final Writer output, final JavaAttribute attribute)
	throws IOException
	{
		output.write("\t\treturn ");
		if(attribute.isBoxed())
			output.write(attribute.getUnBoxingPrefix());
		output.write('(');
		output.write(attribute.getPersistentType());
		output.write(")getAttribute(this.");
		output.write(attribute.getName());
		final List qualifiers = attribute.getQualifiers();
		if(qualifiers!=null)
		{
			output.write(",new Object[]{");
			writeParameterCallList(qualifiers);
			output.write('}');
		}
		output.write(')');
		if(attribute.isBoxed())
			output.write(attribute.getUnBoxingPostfix());
		output.write(';');
		output.write(lineSeparator);
	}

	/**
	 * Identation contract:
	 * This methods is called, when output stream is immediatly after a line break,
	 * and it should return the output stream after immediatly after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeSetterBody(final Writer output, final JavaAttribute attribute)
	throws IOException
	{
		final SortedSet exceptionsToCatch = attribute.getExceptionsToCatchInSetter();

		if(!exceptionsToCatch.isEmpty())
		{
			output.write("\t\ttry");
			output.write(lineSeparator);
			output.write("\t\t{");
			output.write(lineSeparator);
			output.write('\t');
		}
		output.write("\t\tsetAttribute(this.");
		output.write(attribute.getName());
		final List qualifiers = attribute.getQualifiers();
		if(qualifiers!=null)
		{
			output.write(",new Object[]{");
			writeParameterCallList(qualifiers);
			output.write('}');
		}
		output.write(',');
		if(attribute.isBoxed())
			output.write(attribute.getBoxingPrefix());
		output.write(attribute.getName());
		if(attribute.isBoxed())
			output.write(attribute.getBoxingPostfix());
		output.write(");");
		output.write(lineSeparator);
		if(!exceptionsToCatch.isEmpty())
		{
			output.write("\t\t}");
			output.write(lineSeparator);
			
			for(Iterator i = exceptionsToCatch.iterator(); i.hasNext(); )
				writeViolationExceptionCatchClause(output, (Class)i.next());
		}
	}
	
	private void writeViolationExceptionCatchClause(final Writer output, final Class exceptionClass)
	throws IOException
	{
		output.write("\t\tcatch("+exceptionClass.getName()+" e)");
		output.write(lineSeparator);
		output.write("\t\t{");
		output.write(lineSeparator);
		output.write("\t\t\tthrow new "+SystemException.class.getName()+"(e);");
		output.write(lineSeparator);
		output.write("\t\t}");
		output.write(lineSeparator);
	}
}


