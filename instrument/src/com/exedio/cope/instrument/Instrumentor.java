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

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.Attribute;
import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.DataAttribute;
import com.exedio.cope.DateAttribute;
import com.exedio.cope.DayAttribute;
import com.exedio.cope.DoubleAttribute;
import com.exedio.cope.EnumAttribute;
import com.exedio.cope.Function;
import com.exedio.cope.IntegerFunction;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.LongAttribute;
import com.exedio.cope.NestingRuntimeException;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.StringFunction;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.Hash;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.Qualifier;
import com.exedio.cope.pattern.Vector;

final class Instrumentor implements InjectionConsumer
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
	
	private static final String TAG_PREFIX = "cope.";

	/**
	 * Tag name for persistent classes.
	 */
	private static final String PERSISTENT_CLASS = TAG_PREFIX + "persistent";

	/**
	 * Tag name for the generated getter option.
	 */
	static final String ATTRIBUTE_GETTER = TAG_PREFIX + "getter";

	/**
	 * Tag name for the generated setter option.
	 */
	static final String ATTRIBUTE_SETTER = TAG_PREFIX + "setter";

	/**
	 * Tag name for the generated initial option.
	 */
	static final String ATTRIBUTE_INITIAL = TAG_PREFIX + "initial";

	/**
	 * Tag name for the generated initial constructor option.
	 */
	static final String CLASS_INITIAL_CONSTRUCTOR = TAG_PREFIX + "constructor";
	
	/**
	 * Tag name for the generated generic constructor option.
	 */
	static final String CLASS_GENERIC_CONSTRUCTOR = TAG_PREFIX + "generic.constructor";
	
	/**
	 * Tag name for the generated type option.
	 */
	static final String CLASS_TYPE = TAG_PREFIX + "type";
	
	/**
	 * All generated class features get this doccomment tag.
	 */
	static final String GENERATED = TAG_PREFIX + "generated";
	

	private void handleClassComment(final JavaClass jc, final String docComment)
			throws InjectorParseException
	{
		if(containsTag(docComment, PERSISTENT_CLASS))
		{
			final String typeOption = Injector.findDocTagLine(docComment, CLASS_TYPE);
			final String initialConstructorOption = Injector.findDocTagLine(docComment, CLASS_INITIAL_CONSTRUCTOR);
			final String genericConstructorOption = Injector.findDocTagLine(docComment, CLASS_GENERIC_CONSTRUCTOR);
			new CopeClass(jc, typeOption, initialConstructorOption, genericConstructorOption);
		}
	}
	
	public void onClass(final JavaClass jc)
			throws InjectorParseException
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
		final CopeClass copeClass = CopeClass.getCopeClass(javaClass);

		if(copeClass!=null)
			(new Generator(output)).writeClassFeatures(copeClass);
		
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
	
	private final void handleAttribute(final JavaAttribute ja, final Class typeClass, final String docComment)
		throws InjectorParseException
	{
		final List initializerArguments = ja.getInitializerArguments();
		//System.out.println(initializerArguments);
					
		final String getterOption = Injector.findDocTagLine(docComment, ATTRIBUTE_GETTER);
		final String setterOption = Injector.findDocTagLine(docComment, ATTRIBUTE_SETTER);
		final boolean initial = Injector.hasTag(docComment, ATTRIBUTE_INITIAL);

		if(
			IntegerFunction.class.isAssignableFrom(typeClass) ||
			LongAttribute.class.equals(typeClass) ||
			DoubleAttribute.class.equals(typeClass) ||
			BooleanAttribute.class.equals(typeClass) ||
			DateAttribute.class.equals(typeClass) ||
			DayAttribute.class.equals(typeClass) ||
			StringFunction.class.isAssignableFrom(typeClass))
		{
			new CopeNativeAttribute(
				ja, typeClass,
				initializerArguments, getterOption, setterOption, initial);
		}
		else if(
			EnumAttribute.class.equals(typeClass)||
			ItemAttribute.class.equals(typeClass))
		{
			new CopeObjectAttribute(
				ja, typeClass,
				initializerArguments, getterOption, setterOption, initial);
		}
		else if(DataAttribute.class.equals(typeClass))
		{
			new CopeDataAttribute(
				ja, typeClass,
				initializerArguments, getterOption, setterOption, initial);
		}
		else
			throw new RuntimeException(typeClass.toString());
	}
	
	private final void handleUniqueConstraint(final JavaAttribute ja, final Class typeClass)
		throws InjectorParseException
	{
		final JavaClass jc = ja.parent;
		final List initializerArguments = ja.getInitializerArguments();
		//System.out.println(initializerArguments);
		final CopeClass copeClass = CopeClass.getCopeClass(jc);
		final ArrayList copeAttributes = new ArrayList(initializerArguments.size());
		for(Iterator i = initializerArguments.iterator(); i.hasNext(); )
		{
			final String initializerArgument = (String)i.next();
			final CopeAttribute copeAttribute = copeClass.getAttribute(initializerArgument);
			if(copeAttribute==null)
				throw new InjectorParseException("attribute >"+initializerArgument+"< in unique constraint "+ja.name+" not found.");
			copeAttributes.add(copeAttribute);
		}
		copeClass.makeUnique(
			new CopeUniqueConstraint(ja,
				(CopeAttribute[])copeAttributes.toArray(new CopeAttribute[copeAttributes.size()])));
	}
	
	private final void handleQualifier(final JavaAttribute ja, final Class typeClass)
		throws InjectorParseException
	{
		final List initializerArguments = ja.getInitializerArguments();
		//System.out.println("---------"+initializerArguments);
		new CopeQualifier(ja, initializerArguments);
	}

	private final void handleHash(final JavaAttribute ja, final Class typeClass)
		throws InjectorParseException
	{
		final JavaClass jc = ja.parent;
		final CopeClass copeClass = CopeClass.getCopeClass(jc);
		final List initializerArguments = ja.getInitializerArguments();
		if(initializerArguments.size()<1)
			throw new InjectorParseException("attribute >"+ja.name+"< has invalid initializer arguments: "+initializerArguments);
		//System.out.println("---------"+initializerArguments);
		final String initializerArgument = (String)initializerArguments.get(0);
		final CopeAttribute storageAttribute;
		if("newStringAttribute".equals(initializerArgument))
		{
			// implicitExternal
			storageAttribute = new CopeNativeAttribute(ja, StringAttribute.class, Collections.singletonList("OPTIONAL"), "none", "none", false); // TODO make some useful assumption about option
		}
		else
		{
			boolean internal = false;
			try
			{
				CopeAttribute.getOption(initializerArgument);
				internal = true;
			}
			catch(NestingRuntimeException e)
			{
			}
			
			if(internal)
			{
				// internal
				storageAttribute = new CopeNativeAttribute(ja, StringAttribute.class, Collections.singletonList(initializerArgument), "none", "none", false);
			}
			else
			{
				// explicitExternal
				storageAttribute = (CopeAttribute)copeClass.getAttribute(initializerArgument);
				if(storageAttribute==null)
					throw new InjectorParseException("attribute >"+initializerArgument+"< in hash "+ja.name+" not found.");
			}
		}
		new CopeHash(ja, storageAttribute);
	}

	private final void handleVector(final JavaAttribute ja, final Class typeClass)
		throws InjectorParseException
	{
		new CopeVector(ja);
	}
	
	private final void handleMedia(final JavaAttribute ja, final String docComment)
		throws InjectorParseException
	{
		final String setterOption = Injector.findDocTagLine(docComment, ATTRIBUTE_SETTER);
		new CopeMedia(ja, setterOption);
	}

	public void onClassFeature(final JavaFeature jf, final String docComment)
	throws IOException, InjectorParseException
	{
		//System.out.println("onClassFeature("+jf.getName()+" "+docComment+")");
		if(!class_state.isInterface() &&
			jf instanceof JavaAttribute &&
			!discardnextfeature)
		{
			final JavaAttribute ja = (JavaAttribute)jf;
			final int modifier = ja.modifier;

			if(Modifier.isFinal(modifier) && Modifier.isStatic(modifier))
			{
				Class typeClass = null;
				try
				{
					typeClass = ja.file.findType(ja.type);
				}
				catch(InjectorParseException e)
				{
				}

				if(typeClass!=null)
				{
					if(Function.class.isAssignableFrom(typeClass)||Attribute.class.isAssignableFrom(typeClass))
						handleAttribute(ja, typeClass, docComment);
					else if(UniqueConstraint.class.isAssignableFrom(typeClass))
						handleUniqueConstraint(ja, typeClass);
					else if(Qualifier.class.isAssignableFrom(typeClass))
						handleQualifier(ja, typeClass);
					else if(Hash.class.isAssignableFrom(typeClass))
						handleHash(ja, typeClass);
					else if(Vector.class.isAssignableFrom(typeClass))
						handleVector(ja, typeClass);
					else if(Media.class.isAssignableFrom(typeClass))
						handleMedia(ja, docComment);
				}
			}
		}
		discardnextfeature=false;
	}
	
	public boolean onDocComment(final String docComment, final Writer output)
	throws IOException
	{
		//System.out.println("onDocComment("+docComment+")");

		if(docComment.indexOf('@'+GENERATED)>=0 ||
				docComment.indexOf("<p><small>Generated by the cope instrumentor.</small>")>=0 || // detect legacy markers
				docComment.indexOf("@author cope instrumentor")>=0)
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
	throws IOException, InjectorParseException
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
	
	private static final boolean containsTag(final String docComment, final String tagName)
	{
		return docComment!=null && docComment.indexOf('@'+tagName)>=0 ;
	}

}


