
package injection;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.lang.reflect.Modifier;

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
	
	private static final String PERSISTENT_CLASS = "persistent";
	private static final String PERSISTENT_ATTRIBUTE = PERSISTENT_CLASS;
	
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
		System.out.println("onClass("+jc.getName()+")");

		discardnextfeature=false;
		
		class_state_stack.add(class_state);
		class_state=jc;
		
		if(lastFileDocComment != null)
		{
			handleClassComment(jc, lastFileDocComment);
			lastFileDocComment = null;
		}
	}
	
	private void writeAccessMethods(final JavaAttribute persistentAttribute)
	throws IOException
	{
		final String methodModifiers = Modifier.toString(persistentAttribute.getMethodModifiers());
		final String type = "String";

		// getter
		output.write("/** @"+GENERATED);
		output.write(lineSeparator);
		output.write("*/");
		output.write(methodModifiers);
		output.write(' ');
		output.write(type);
		output.write(" get");
		output.write(persistentAttribute.getCamelCaseName());
		output.write("(){");
		output.write(lineSeparator);
		output.write('}');
		
		// setter
		output.write("/** @"+GENERATED);
		output.write(lineSeparator);
		output.write("*/");
		output.write(methodModifiers);
		output.write(" void set");
		output.write(persistentAttribute.getCamelCaseName());
		output.write('(');
		output.write(type);
		output.write(' ');
		output.write(persistentAttribute.getName());
		output.write("){");
		output.write(lineSeparator);
		output.write('}');
	}
	
	public void onClassEnd(JavaClass jc)
	throws IOException, InjectorParseException
	{
		System.out.println("onClassEnd("+jc.getName()+")");

		if(!jc.isInterface() && jc.isPersistent())
		{
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
		System.out.println("onClassFeature("+jf.getName()+" "+docComment+")");
		if(!class_state.isInterface())
		{
			if(jf instanceof JavaAttribute &&
			Modifier.isFinal(jf.getModifiers()) &&
			Modifier.isStatic(jf.getModifiers()) &&
			!discardnextfeature &&
			containsTag(docComment, PERSISTENT_ATTRIBUTE))
			{
				((JavaAttribute)jf).makePersistent();
			}
		}
		discardnextfeature=false;
	}
	
	public boolean onDocComment(String docComment)
	throws IOException
	{
		System.out.println("onDocComment("+docComment+")");

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
		System.out.println("onFileDocComment("+docComment+")");
		
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

}


