
package com.exedio.cope.instrument;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.SystemException;

public final class Instrumentor implements InjectionConsumer
{
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
	
	/**
	 * The last file level doccomment that was read.
	 */
	private String lastFileDocComment = null;
	
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
	 * All generated class features get this doccomment tag.
	 */
	static final String GENERATED_AUTHOR_TAG = "@author cope instrumentor";

	private List uniqueConstraints=null;
	
	private void handleClassComment(final JavaClass jc, final String docComment)
	{
		if(containsTag(docComment, PERSISTENT_CLASS))
		{
			PersistentClass pc = new PersistentClass(jc);
		
			final String uniqueConstraint = Injector.findWholeDocTag(docComment, UNIQUE_ATTRIBUTE);
			if(uniqueConstraint!=null)
			{
				if(uniqueConstraints==null)
					uniqueConstraints = new ArrayList();
				uniqueConstraints.add(uniqueConstraint);
			}
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

	public void onClassEnd(final JavaClass javaClass, final Writer output)
	throws IOException, InjectorParseException
	{
		//System.out.println("onClassEnd("+javaClass.getName()+")");

		final PersistentClass persistentClass = PersistentClass.getPersistentClass(javaClass);
		if(persistentClass!=null)
			(new Generator(output)).writeClassFeatures(persistentClass, uniqueConstraints);
		
		if(class_state!=javaClass)
			throw new RuntimeException();
		class_state=(JavaClass)(class_state_stack.remove(class_state_stack.size()-1));
	}

	public void onBehaviourHeader(JavaBehaviour jb)
	throws java.io.IOException
	{
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
				
				final boolean readOnly;
				final boolean notNull;
				final String secondArgument;
				{
					final String initializer = ja.getInitializerTokens();
	
					final int openBracketPos = initializer.indexOf('(');
					if(openBracketPos<0)
						throw new RuntimeException("attribute initializer must start must contain '(', but was "+initializer);
	
					final int closeBracketPos = initializer.indexOf(')', openBracketPos);
					if(closeBracketPos<0)
						throw new RuntimeException("attribute initializer must start must contain ')', but was "+initializer);

					final int firstArgumentEnd;	
					final int firstCommaPos = initializer.indexOf(',', openBracketPos+1);
					if(firstCommaPos<0)
					{
						firstArgumentEnd = closeBracketPos;
						secondArgument = null;
					}
					else
					{
						firstArgumentEnd = firstCommaPos;
						final int secondCommaPos = initializer.indexOf(',', firstCommaPos+1);
						if(secondCommaPos>0)
							secondArgument = initializer.substring(firstCommaPos+1, secondCommaPos).trim();
						else
							secondArgument = initializer.substring(firstCommaPos+1, closeBracketPos).trim();
					}

					final String optionString = initializer.substring(openBracketPos+1, firstArgumentEnd).trim();
					if("null".equals(optionString))
					{
						readOnly = false;
						notNull = false;
					}
					else
					{
						try
						{
							//System.out.println(optionString);
							final Item.Option option = 
								(Item.Option)Item.class.getDeclaredField(optionString).get(null);
							readOnly = option.readOnly;
							notNull = option.notNull;
						}
						catch(NoSuchFieldException e)
						{
							throw new SystemException(e);
						}
						catch(IllegalAccessException e)
						{
							throw new SystemException(e);
						}
					}
				}
	
				final String persistentType;
				final int persistentTypeType;

				if("IntegerAttribute".equals(type))
				{
					persistentType = "Integer";
					persistentTypeType = PersistentAttribute.TYPE_INTEGER;
				}
				else if("BooleanAttribute".equals(type))
				{
					persistentType = "Boolean";
					persistentTypeType = PersistentAttribute.TYPE_BOOLEAN;
				}
				else if("StringAttribute".equals(type))
				{
					persistentType = "String";
					persistentTypeType = PersistentAttribute.TYPE_STRING;
				}
				else if("EnumerationAttribute".equals(type))
				{
					if(secondArgument==null)
						throw new RuntimeException("second argument required");
					if(!secondArgument.endsWith(".class"))
						throw new RuntimeException("second argument must end with .class: \'"+secondArgument+'\'');
					persistentType = secondArgument.substring(0, secondArgument.length()-".class".length());
					persistentTypeType = PersistentAttribute.TYPE_ENUMERATION;
				}
				else if("ItemAttribute".equals(type))
				{
					if(secondArgument==null)
						throw new RuntimeException("second argument required");
					if(!secondArgument.endsWith(".class"))
						throw new RuntimeException("second argument must end with .class: \'"+secondArgument+'\'');
					persistentType = secondArgument.substring(0, secondArgument.length()-".class".length());
					persistentTypeType = PersistentAttribute.TYPE_ITEM;
				}
				else if("MediaAttribute".equals(type))
				{
					persistentType = PersistentAttribute.MEDIA_TYPE;
					persistentTypeType = PersistentAttribute.TYPE_MEDIA;
				}
				else
					throw new RuntimeException();

				final boolean mapped = containsTag(docComment, MAPPED_ATTRIBUTE);
				
				final String qualifier = Injector.findDocTag(docComment, ATTRIBUTE_QUALIFIER);
				final List qualifiers;
				if(qualifier!=null)
					qualifiers = Collections.singletonList(qualifier);
				else
					qualifiers = null;

				final PersistentAttribute persistentAttribute;
				switch(persistentTypeType)
				{
					case PersistentAttribute.TYPE_MEDIA:
					{
						final String variant = Injector.findDocTag(docComment, VARIANT_MEDIA_ATTRIBUTE);
						final List variants;
						if(variant!=null)
							variants = Collections.singletonList(variant);
						else
							variants = null;
	
						final String mimeMajor = Injector.findDocTag(docComment, MIME_MAJOR);
						final String mimeMinor = Injector.findDocTag(docComment, MIME_MINOR);
						persistentAttribute =
							new PersistentMediaAttribute(
								ja,
								readOnly, notNull, mapped, qualifiers,
								variants, mimeMajor, mimeMinor);
						break;
					}
					case PersistentAttribute.TYPE_ENUMERATION:
					{
						persistentAttribute =
							new PersistentEnumerationAttribute(
								ja, persistentType,
								readOnly, notNull, mapped, qualifiers);
						break;
					}
					default:
						persistentAttribute =
							new PersistentAttribute(
								ja, persistentType, persistentTypeType,
								readOnly, notNull, mapped, qualifiers);
						break;
				}

				if(containsTag(docComment, UNIQUE_ATTRIBUTE))
					persistentAttribute.persistentClass.makeUnique(new PersistentAttribute[]{persistentAttribute});
			}
		}
		discardnextfeature=false;
	}
	
	public boolean onDocComment(String docComment, final Writer output)
	throws IOException
	{
		//System.out.println("onDocComment("+docComment+")");

		if(docComment.indexOf(GENERATED_AUTHOR_TAG)>=0)
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
	
	public void onFileDocComment(String docComment, final Writer output)
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

}


