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

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.Primitive;
import bsh.UtilEvalError;

/**
 * Represents a class parsed by the java parser.
 * Is an inner class, if parent is not null.
 * @see Injector
 * 
 * @author Ralf Wiebicke
 */
final class JavaClass extends JavaFeature
{
	final CopeNameSpace nameSpace;
	
	private HashMap attributes = new HashMap();
	final List classExtends;
	final List classImplements;
	private int classEndPosition = -1;

	/**
	 * @param parent may be null for non-inner classes
	 */
	public JavaClass(
			final JavaFile file, final JavaClass parent,
			final int modifiers, final String name,
			final List classExtends, final List classImplements)
	throws InjectorParseException
	{
		super(file, parent, modifiers, null, name);
		this.nameSpace = new NameSpace(file.nameSpace);
		this.classExtends = Collections.unmodifiableList(classExtends);
		this.classImplements = Collections.unmodifiableList(classImplements);
		if(classExtends.contains("EnumValue")) // TODO nicify
			file.repository.addEnumClass(this);
		file.add(this);
	}
	
	void add(final JavaFeature f)
	{
		assert file.repository.isBuildStage();
		
		if(!(f instanceof JavaAttribute))
			return;
		
		if(attributes.put(f.name, f)!=null)
			throw new RuntimeException(name+'/'+f.name);
	}
	
	JavaAttribute getAttribute(final String name)
	{
		assert !file.repository.isBuildStage();
		
		return (JavaAttribute)attributes.get(name);
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
		for(JavaClass i=this; i!=null; i=i.parent)
		{
			if(i!=this)
				buf.insert(pos, '$');
			buf.insert(pos, i.name);
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
		for(JavaClass i=this; i!=null; i=i.parent)
		{
			buf.insert(pos, i.name);
			buf.insert(pos, '_');
		}
		return buf.toString();
	}
	
	public final boolean isInterface()
	{
		return (modifier & Modifier.INTERFACE) > 0;
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
	
	void notifyClassEnd()
	{
		assert file.repository.isBuildStage();
		assert classEndPosition==-1;
		
		classEndPosition = file.getBufferPosition();
		
		assert classEndPosition>=0;
	}
	
	int getClassEndPosition()
	{
		assert classEndPosition>=0;
		
		return classEndPosition;
	}
	
	Object evaluate(final String s)
	{
		assert !file.repository.isBuildStage();
		
		final Interpreter ip = new Interpreter();
		try
		{
			final Object result = ip.eval(s, nameSpace);
			//System.out.println("--------evaluate("+s+") == "+result);
			return result;
		}
		catch(EvalError e) // TODO method should throw this
		{
			throw new RuntimeException(e);
		}
	}
	
	final class NameSpace extends CopeNameSpace
	{
		NameSpace(final CopeNameSpace parent)
		{
			super(parent);
		}
		
	   public Object getVariable(final String name) throws UtilEvalError
	   {
			//System.out.println("++++++++++++++++1--------getVariable(\""+name+"\")");
			final Object superResult = super.getVariable(name);
			if(superResult!=Primitive.VOID)
			{
				//System.out.println("#####"+superResult+"--"+superResult.getClass());
				return superResult;
			}
			
			//System.out.println("++++++++++++++++2--------getVariable(\""+name+"\")");
			final JavaAttribute ja = getAttribute(name);
			if(ja!=null)
				return ja.evaluate();
			
			return Primitive.VOID;
	   }
	}
}
