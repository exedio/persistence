
package injection;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.lang.reflect.Modifier;
import persistence.SystemException;
import persistence.UniqueViolationException;

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
	 * Tag name for one qualifier of qualified attributes.
	 */
	private static final String ATTRIBUTE_QUALIFIER = "qualifier";
	
	/**
	 * All generated class features get this doccomment tag.
	 */
	private static final String GENERATED = "generated";

	private void handleClassComment(final JavaClass jc, final String docComment)
	{
		if(containsTag(docComment, PERSISTENT_CLASS))
			jc.setPersistent();
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
			output.write(" throws ");
			boolean first = true;
			for(final Iterator i = exceptions.iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					output.write(',');
				output.write(((Class)i.next()).getName());
			}
		}
	}

	public void writeConstructor(final JavaClass javaClass)
	throws IOException
	{
		output.write("/**");
		output.write(lineSeparator);
		output.write("\t * This is a generated constructor.");
		output.write(lineSeparator);
		output.write("\t * @"+GENERATED);
		output.write(lineSeparator);
		output.write("\t */");
		output.write(Modifier.toString(javaClass.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)));
		output.write(' ');
		output.write(javaClass.getName());
		output.write('(');
		boolean first = true;
		final ArrayList readOnlyAttributes = new ArrayList();
		final TreeSet setterExceptions = new TreeSet();
		for(Iterator i = javaClass.getPersistentAttributes().iterator(); i.hasNext(); )
		{
			final JavaAttribute persistentAttribute = (JavaAttribute)i.next();
			if(persistentAttribute.isReadOnly())
			{
				readOnlyAttributes.add(persistentAttribute);
				setterExceptions.addAll(persistentAttribute.getSetterExceptions());
				if(first)
					first = false;
				else
					output.write(',');
				output.write("final ");
				output.write(persistentAttribute.getPersistentType());
				output.write(' ');
				output.write(persistentAttribute.getName());
			}
		}
		output.write(')');
		writeThrowsClause(setterExceptions);
		output.write(lineSeparator);
		output.write("\t{");
		output.write(lineSeparator);
		output.write("\t\tsuper(1.0);");
		output.write(lineSeparator);
		for(Iterator i = readOnlyAttributes.iterator(); i.hasNext(); )
		{
			final JavaAttribute readOnlyAttribute = (JavaAttribute)i.next();
			output.write("\t\tset");
			output.write(readOnlyAttribute.getCamelCaseName());
			output.write('(');
			output.write(readOnlyAttribute.getName());
			output.write(");");
			output.write(lineSeparator);
		}
		output.write("\t}");
	}
	
	private void writeAccessMethods(final JavaAttribute persistentAttribute)
	throws IOException
	{
		final String methodModifiers = Modifier.toString(persistentAttribute.getMethodModifiers());
		final String type = persistentAttribute.getPersistentType();
		final List qualifiers = persistentAttribute.getQualifiers();

		// getter
		output.write("/**");
		output.write(lineSeparator);
		output.write("\t * This is a generated getter method.");
		output.write(lineSeparator);
		output.write("\t * @"+GENERATED);
		output.write(lineSeparator);
		output.write("\t */");
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
		output.write("/**");
		output.write(lineSeparator);
		output.write("\t * This is a generated setter method.");
		output.write(lineSeparator);
		output.write("\t * @"+GENERATED);
		output.write(lineSeparator);
		output.write("\t */");
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
		writeThrowsClause(persistentAttribute.getSetterExceptions());
		output.write(lineSeparator);
		output.write("\t{");
		output.write(lineSeparator);
		writeSetterBody(output, persistentAttribute);
		output.write("\t}");
	}
	
	public void onClassEnd(JavaClass jc)
	throws IOException, InjectorParseException
	{
		//System.out.println("onClassEnd("+jc.getName()+")");

		if(!jc.isInterface() && jc.isPersistent())
		{
			writeConstructor(jc);
			for(Iterator i = jc.getPersistentAttributes().iterator(); i.hasNext(); )
			{
				// write setter/getter methods
				final JavaAttribute persistentAttribute = (JavaAttribute)i.next();
				writeAccessMethods(persistentAttribute);
			}
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
				final String persistentType;
				if("IntegerAttribute".equals(type))
					persistentType = "Integer";
				else if("StringAttribute".equals(type))
					persistentType = "String";
				else if("ItemAttribute".equals(type))
				{
					persistentType = Injector.findDocTag(docComment, PERSISTENT_ATTRIBUTE);
				}
				else
					throw new RuntimeException();

				final JavaAttribute ja = (JavaAttribute)jf;
				ja.makePersistent(persistentType);

				if(containsTag(docComment, UNIQUE_ATTRIBUTE))
					ja.makeUnique();
				
				if(containsTag(docComment, READ_ONLY_ATTRIBUTE))
					ja.makeReadOnly();
				
				final String qualifier = Injector.findDocTag(docComment, ATTRIBUTE_QUALIFIER);
				if(qualifier!=null)
					ja.makeQualified(Collections.singletonList(qualifier));
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

	
	// ----------------- methods for a new interface

	/**
	 * Identation contract:
	 * This methods is called, when output stream is immediatly after a line break,
	 * and it should return the output stream after immediatly after a line break.
	 * This means, doing nothing fullfils the contract.
	 */
	private void writeGetterBody(final Writer output, final JavaAttribute attribute)
	throws IOException
	{
		output.write("\t\treturn (");
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
		output.write(");");
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
		if(!attribute.isUnique())
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
		output.write(attribute.getName());
		output.write(");");
		output.write(lineSeparator);
		if(!attribute.isUnique())
		{
			output.write("\t\t}");
			output.write(lineSeparator);
			output.write("\t\tcatch("+UniqueViolationException.class.getName()+" e)");
			output.write(lineSeparator);
			output.write("\t\t{");
			output.write(lineSeparator);
			output.write("\t\t\tthrow new "+SystemException.class.getName()+"(e);");
			output.write(lineSeparator);
			output.write("\t\t}");
			output.write(lineSeparator);
		}
	}
}


