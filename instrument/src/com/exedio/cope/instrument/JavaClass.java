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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;

/**
 * Represents a class parsed by the java parser.
 * Is an inner class, if parent is not null.
 * @see Injector
 * 
 * @author Ralf Wiebicke
 */
class JavaClass extends JavaFeature
{
	private HashMap features = new HashMap();

	/**
	 * @param parent may be null for non-inner classes
	 */
	public JavaClass(JavaFile file, JavaClass parent, int modifiers, String name)
	throws InjectorParseException
	{
		super(file, parent, modifiers, null, name);
	}
	
	void add(final JavaFeature f)
	{
		features.put(f.name, f);
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
	
	private static final String NEW = "new ";
	private static final String CLASS = ".class";
	void evaluate(String s)
	{
		System.out.println("--------------evaluate-"+name+"--"+s);

		s = s.trim();
		final JavaFeature feature;

		if(s.startsWith(NEW))
		{
			s = s.substring(NEW.length());
			final int openParent = s.indexOf('(');
			if(openParent<=0)
				throw new RuntimeException(s);
			final String newClassName = s.substring(0, openParent);
			final Class newClass;
			try
			{
				newClass = file.findType(newClassName.trim());
			}
			catch(InjectorParseException e)
			{
				throw new RuntimeException(e);
			}
			if(!Feature.class.isAssignableFrom(newClass))
				throw new RuntimeException(newClass.toString());
			final int closeParent = s.lastIndexOf(')');
			if(closeParent<=openParent)
				throw new RuntimeException(s);
			s = s.substring(openParent+1, closeParent);
			System.out.println("++"+s);
			final ArrayList arguments = new ArrayList();
			int lastcomma = 0;
			for(int comma = s.indexOf(','); comma>0; comma = s.indexOf(',', lastcomma))
			{
				final String si = s.substring(lastcomma, comma);
				arguments.add(si);
				lastcomma = comma+1;
			}
			final String sl = s.substring(lastcomma);
			arguments.add(sl);
			System.out.println("------"+arguments);
			for(Iterator i = arguments.iterator(); i.hasNext(); )
			{
				final String argument = (String)i.next();
				evaluate(argument);
			}
		}
		else if((feature = (JavaFeature)features.get(s))!=null)
		{
			if(!(feature instanceof JavaAttribute))
				throw new RuntimeException(feature.toString());
			final JavaAttribute a = (JavaAttribute)feature;
			if((a.modifier & (Modifier.STATIC|Modifier.FINAL))!=(Modifier.STATIC|Modifier.FINAL))
				throw new RuntimeException(feature.toString()+'-'+Modifier.toString(a.modifier));
			System.out.println("----------"+feature.toString());
			a.evaluate();
		}
		else if(s.endsWith(CLASS))
		{
			final String className = s.substring(0, s.length()-CLASS.length());
			Class aClass;
			try
			{
				aClass = file.findType(className.trim());
			}
			catch(InjectorParseException e)
			{
				aClass = Item.class;
			}
			System.out.println("----------"+aClass.toString());
		}
		else
		{
			try
			{
				final Field f = Item.class.getField(s);
				final int m = f.getModifiers();
				if((m & (Modifier.STATIC|Modifier.FINAL))!=(Modifier.STATIC|Modifier.FINAL))
					throw new RuntimeException(feature.toString()+'-'+Modifier.toString(m));
				System.out.println("----------"+f.getName());
				final Object value = f.get(null);
				System.out.println("----------"+value.toString());
			}
			catch(NoSuchFieldException e)
			{
				throw new RuntimeException(e);
			}
			catch(IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

}
