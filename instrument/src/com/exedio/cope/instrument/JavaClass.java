
package injection;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import persistence.ReadOnlyViolationException;
import tools.ClassComparator;

/**
 * Represents a class parsed by the java parser.
 * Is an inner class, if parent is not null.
 * @see #getParent()
 * @see Injector
 */
public class JavaClass extends JavaFeature
{
	
	private ArrayList persistentAttributes = null;
	private Map persistentAttributeMap = new TreeMap();
	private ArrayList uniqueConstraints = null;
	
	/**
	 * @parameter parent may be null for non-inner classes
	 * @parameter packagename may be null for root package
	 */
	public JavaClass(JavaFile file, JavaClass parent, int modifiers, String name)
	throws InjectorParseException
	{
		super(file, parent, modifiers, null, name);
	}
	
	public void setPersistent()
	{
		if(persistentAttributes != null)
			throw new RuntimeException();
		persistentAttributes = new ArrayList();
	}
	
	public boolean isPersistent()
	{
		return persistentAttributes != null;
	}
	
	public void addPersistentAttribute(final JavaAttribute persistentAttribute)
	{
		if(persistentAttributes == null)
		{
			persistentAttributes = new ArrayList();
			persistentAttributeMap = new TreeMap();
		}
		persistentAttributes.add(persistentAttribute);
		persistentAttributeMap.put(persistentAttribute.getName(), persistentAttribute);
	}
	
	/**
	 * @returns unmodifiable list of {@link JavaAttribute}
	 */
	public List getPersistentAttributes()
	{
		return Collections.unmodifiableList(persistentAttributes);
	}
	
	public JavaAttribute getPersistentAttribute(final String name)
	{
		return (JavaAttribute)persistentAttributeMap.get(name);
	}
	
	public void makeUnique(final JavaAttribute[] uniqueAttributes)
	{
		if(uniqueConstraints==null)
			uniqueConstraints=new ArrayList();
		
		uniqueConstraints.add(uniqueAttributes);
	}
	
	/**
	 * @returns unmodifiable list of {@link JavaAttribute[]}
	 */
	public List getUniqueConstraints()
	{
		return Collections.unmodifiableList(uniqueConstraints);
	}
	
	/**
	 * Constructs the fully qualified name of this class,
	 * including package path.
	 */
	public String getFullName()
	{
		StringBuffer buf=new StringBuffer();
		String packagename=getPackageName();
		if(packagename!=null)
		{
			buf.append(packagename);
			buf.append('.');
		}
		int pos=buf.length();
		for(JavaClass i=this; i!=null; i=i.getParent())
		{
			if(i!=this)
				buf.insert(pos, '$');
			buf.insert(pos, i.getName());
		}
		return buf.toString();
	}
	
	/**
	 * Constructs the fully qualified name of this class,
	 * including package path.
	 * The same as {@link #getFullName()}, but without
	 * dots and dollars, so that this string can be used
	 * as part of a java identifier.
	 */
	public String getFullNameEscaped()
	{
		StringBuffer buf=new StringBuffer();
		String packagename=getPackageName();
		if(packagename!=null)
		{
			buf.append('_');
			for(int i=0; i<packagename.length(); i++)
			{
				char c=packagename.charAt(i);
				if(c=='.')
					buf.append('_');
				else
					buf.append(c);
			}
		}
		int pos=buf.length();
		for(JavaClass i=this; i!=null; i=i.getParent())
		{
			buf.insert(pos, i.getName());
			buf.insert(pos, '_');
		}
		return buf.toString();
	}
	
	private ArrayList initialAttributes = null;
	private TreeSet contructorExceptions = null;
	
	private final void makeInitialAttributesAndContructorExceptions()
	{
		initialAttributes = new ArrayList();
		contructorExceptions = new TreeSet(ClassComparator.newInstance());
		for(Iterator i = getPersistentAttributes().iterator(); i.hasNext(); )
		{
			final JavaAttribute persistentAttribute = (JavaAttribute)i.next();
			if(persistentAttribute.isReadOnly() || persistentAttribute.isNotNull())
			{
				initialAttributes.add(persistentAttribute);
				contructorExceptions.addAll(persistentAttribute.getSetterExceptions());
			}
		}
		contructorExceptions.remove(ReadOnlyViolationException.class);
	}

	/**
	 * Return all initial attributes of this class.
	 * Initial attributes are all attributes, which are read-only or not-null.
	 */
	public final List getInitialAttributes()
	{
		if(initialAttributes == null)
			makeInitialAttributesAndContructorExceptions();
		return initialAttributes;
	}

	/**
	 * Returns all exceptions, the generated constructor of this class should throw.
	 * This is the unification of throws clauses of all the setters of the
	 * {@link #getInitialAttributes() initial attributes},
	 * but without the ReadOnlyViolationException,
	 * because read-only attributes can only be written in the constructor.
	 */
	public final SortedSet getContructorExceptions()
	{
		if(contructorExceptions == null)
			makeInitialAttributesAndContructorExceptions();
		return contructorExceptions;
	}

	public final boolean isInterface()
	{
		return (getModifiers() & Modifier.INTERFACE) > 0;
	}
	
	public final int getAllowedModifiers()
	{
		return
		Modifier.INTERFACE |
		Modifier.PUBLIC |
		Modifier.PROTECTED |
		Modifier.PRIVATE |
		Modifier.FINAL |
		Modifier.STATIC |
		Modifier.ABSTRACT;
	}
	
	public final void printMore(java.io.PrintStream o)
	{
		o.println("    package: >"+getPackageName()+"<");
		o.println("    fullnam: >"+getFullName()+"<");
	}
	
}
