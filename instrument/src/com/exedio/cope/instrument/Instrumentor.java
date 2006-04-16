/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.List;

import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.DataAttribute;
import com.exedio.cope.DateAttribute;
import com.exedio.cope.DayAttribute;
import com.exedio.cope.DoubleAttribute;
import com.exedio.cope.EnumAttribute;
import com.exedio.cope.IntegerFunction;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.LongAttribute;
import com.exedio.cope.StringFunction;

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
	private ArrayList<JavaClass> class_state_stack = new ArrayList<JavaClass>();
	
	/**
	 * The last file level doccomment that was read.
	 */
	private String lastFileDocComment = null;
	
	public void onPackage(JavaFile javafile)
	throws InjectorParseException
	{
		// nothing to do here
	}
	
	public void onImport(String importname)
	{
		// nothing to do here
	}
	
	// TODO move to CopeModel
	private static final String TAG_PREFIX = "cope.";

	/**
	 * Tag name for the generated getter option.
	 */
	// TODO move to CopeModel
	static final String ATTRIBUTE_GETTER = TAG_PREFIX + "getter";

	/**
	 * Tag name for the generated setter option.
	 */
	// TODO move to CopeModel
	static final String ATTRIBUTE_SETTER = TAG_PREFIX + "setter";

	/**
	 * Tag name for the generated initial option.
	 */
	// TODO move to CopeModel
	static final String ATTRIBUTE_INITIAL = TAG_PREFIX + "initial";

	/**
	 * Tag name for the generated initial constructor option.
	 */
	// TODO move to CopeModel
	static final String CLASS_INITIAL_CONSTRUCTOR = TAG_PREFIX + "constructor";
	
	/**
	 * Tag name for the generated generic constructor option.
	 */
	// TODO move to CopeModel
	static final String CLASS_GENERIC_CONSTRUCTOR = TAG_PREFIX + "generic.constructor";
	
	/**
	 * Tag name for the generated reactivation constructor option.
	 */
	// TODO move to CopeModel
	static final String CLASS_REACTIVATION_CONSTRUCTOR = TAG_PREFIX + "reactivation.constructor";
	
	/**
	 * Tag name for the generated type option.
	 */
	// TODO move to CopeModel
	static final String CLASS_TYPE = TAG_PREFIX + "type";
	
	/**
	 * All generated class features get this doccomment tag.
	 */
	// TODO move to CopeModel
	static final String GENERATED = TAG_PREFIX + "generated";
	

	private void handleClassComment(final JavaClass jc, final String docComment)
			throws InjectorParseException
	{
		jc.setDocComment(docComment);
	}
	
	public void onClass(final JavaClass jc)
			throws InjectorParseException
	{
		//System.out.println("onClass("+jc.getName()+")");

		class_state_stack.add(class_state);
		class_state=jc;
		
		if(lastFileDocComment != null)
		{
			handleClassComment(jc, lastFileDocComment);
			lastFileDocComment = null;
		}
	}

	public void onClassEnd(final JavaClass javaClass)
	throws InjectorParseException
	{
		javaClass.notifyClassEnd();
		if(class_state!=javaClass)
			throw new RuntimeException();
		class_state = class_state_stack.remove(class_state_stack.size()-1);
	}

	public void onBehaviourHeader(JavaBehaviour jb)
	{
		// nothing to do here
	}
	
	public void onAttributeHeader(JavaAttribute ja)
	{
		// nothing to do here
	}
	
	// TODO move to CopeModel
	static final void handleAttribute(final JavaAttribute ja, final Class typeClass)
		throws InjectorParseException
	{
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
				ja, typeClass);
		}
		else if(
			EnumAttribute.class.equals(typeClass)||
			ItemAttribute.class.equals(typeClass))
		{
			new CopeObjectAttribute(
				ja, typeClass);
		}
		else if(DataAttribute.class.equals(typeClass))
		{
			new CopeDataAttribute(
				ja, typeClass);
		}
		else
			throw new RuntimeException(typeClass.toString());
	}
	
	// TODO move to CopeModel
	static final void handleUniqueConstraint(final JavaAttribute ja)
		throws InjectorParseException
	{
		new CopeUniqueConstraint(ja);
	}
	
	// TODO move to CopeModel
	static final void handleQualifier(final JavaAttribute ja)
		throws InjectorParseException
	{
		final List<String> initializerArguments = ja.getInitializerArguments();
		//System.out.println("---------"+initializerArguments);
		new CopeQualifier(ja, initializerArguments);
	}

	// TODO move to CopeModel
	static final void handleHash(final JavaAttribute ja)
		throws InjectorParseException
	{
		final List<String> initializerArguments = ja.getInitializerArguments();
		if(initializerArguments.size()<1)
			throw new InjectorParseException("attribute >"+ja.name+"< has invalid initializer arguments: "+initializerArguments);
		//System.out.println("---------"+initializerArguments);
		final String initializerArgument = initializerArguments.get(0);
		if("newStringAttribute".equals(initializerArgument))
		{
			// implicitExternal
			new CopeHash(ja);
		}
		else
		{
			boolean internal = false;
			try
			{
				CopeAttribute.getOption(initializerArgument);
				internal = true;
			}
			catch(RuntimeException e)
			{
				// then internal is false
			}
			
			if(internal)
			{
				// internal
				new CopeHash(ja);
			}
			else
			{
				// explicitExternal
				new CopeHash(ja, initializerArgument);
			}
		}
	}

	// TODO move to CopeModel
	static final void handleVector(final JavaAttribute ja)
		throws InjectorParseException
	{
		new CopeVector(ja);
	}
	
	// TODO move to CopeModel
	static final void handleMedia(final JavaAttribute ja)
		throws InjectorParseException
	{
		new CopeMedia(ja);
	}

	public void onClassFeature(final JavaFeature jf, final String docComment)
	throws InjectorParseException
	{
		//System.out.println("onClassFeature("+jf.name+" "+docComment+")");
		if(jf instanceof JavaAttribute)
			((JavaAttribute)jf).setDocComment(docComment);
	}
	
	public boolean onDocComment(final String docComment)
	{
		//System.out.println("onDocComment("+docComment+")");

		return !(docComment.indexOf('@'+GENERATED)>=0 ||
				docComment.indexOf("<p><small>Generated by the cope instrumentor.</small>")>=0 || // detect legacy markers
				docComment.indexOf("@author cope instrumentor")>=0);
	}
	
	public void onFileDocComment(final String docComment)
	throws InjectorParseException
	{
		//System.out.println("onFileDocComment("+docComment+")");
		
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
	
	// TODO move to CopeModel
	static final boolean containsTag(final String docComment, final String tagName)
	{
		return docComment!=null && docComment.indexOf('@'+tagName)>=0 ;
	}

}


