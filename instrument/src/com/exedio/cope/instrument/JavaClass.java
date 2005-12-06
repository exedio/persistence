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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.exedio.cope.EnumAttribute;
import com.exedio.cope.EnumValue;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.Attribute.Option;
import com.exedio.cope.pattern.Hash;

/**
 * Represents a class parsed by the java parser.
 * Is an inner class, if parent is not null.
 * @see Injector
 * 
 * @author Ralf Wiebicke
 */
class JavaClass extends JavaFeature
{
	private HashMap attributes = new HashMap();
	final List classExtends;
	final List classImplements;

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
		this.classExtends = Collections.unmodifiableList(classExtends);
		this.classImplements = Collections.unmodifiableList(classImplements);
	}
	
	void add(final JavaFeature f)
	{
		if(!(f instanceof JavaAttribute))
			return;
		
		if(attributes.put(f.name, f)!=null)
			throw new RuntimeException(name+'/'+f.name);
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
	private static final Value ANY_CLASS = new Value(null, Class.class);
	private static final Value TRUE = new Value(true);
	private static final Value FALSE = new Value(false);
	
	final static class Value
	{
		final Object instance;
		final Class clazz;
		
		Value(final Object instance, final Class clazz)
		{
			this.instance = instance;
			this.clazz = clazz;
		}
		
		Value(final boolean instance)
		{
			this.instance = instance ? Boolean.TRUE : Boolean.FALSE;
			this.clazz = boolean.class;
		}
		
		Value(final int instance)
		{
			this.instance = new Integer(instance);
			this.clazz = int.class;
		}
	}
	
	boolean isInt(final String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}
	
	Value[] evaluateArgumentList(String s)
	{
		//System.out.println("++"+s);
		final ArrayList result = new ArrayList();
		int lastcomma = 0;
		for(int comma = s.indexOf(','); comma>0; comma = s.indexOf(',', lastcomma))
		{
			final String si = s.substring(lastcomma, comma);
			result.add(evaluate(si));
			lastcomma = comma+1;
		}
		final String sl = s.substring(lastcomma).trim();
		if(sl.length()==0)
			return new Value[0];
		
		result.add(evaluate(sl));
		//System.out.println("------"+arguments);
		
		return (Value[])result.toArray(new Value[result.size()]);
	}
	
	Class[] toClasses(final Value[] values)
	{
		final Class[] result = new Class[values.length];
		for(int i = 0; i<values.length; i++)
			result[i] = values[i].clazz;
		return result;
	}
			
	Object[] toInstances(final Value[] values, final Class insteadOfAny)
	{
		final Object[] result = new Object[values.length];
		for(int i = 0; i<values.length; i++)
		{
			if(values[i]==ANY_CLASS)
			{
				if(insteadOfAny==null)
					throw new RuntimeException();
				result[i] = insteadOfAny;
			}
			else
				result[i] = values[i].instance;
		}
		return result;
	}
			
	
	Value evaluate(String s)
	{
		//System.out.println("--------------evaluate-"+name+"--"+s+"--");

		s = s.trim();
		final JavaFeature feature;

		if("true".equals(s))
			return TRUE;
		else if("false".equals(s))
			return FALSE;
		else if(isInt(s))
		{
			try
			{
				return new Value(Integer.parseInt(s));
			}
			catch(NumberFormatException e)
			{
				throw new RuntimeException(e);
			}
		}
		else if(s.startsWith(NEW))
		{
			s = s.substring(NEW.length());
			final int openParent = s.indexOf('(');
			if(openParent<=0)
				throw new RuntimeException(s);
			final String newClassName = s.substring(0, openParent);
			
			// TODO make a function
			Class newClass;
			try
			{
				newClass = file.findType(newClassName.trim());
			}
			catch(InjectorParseException e)
			{
				if(newClassName.endsWith("Hash"))
					newClass = AnyHash.class;
				else
					throw new RuntimeException(e);
			}
			
			
			if(!Feature.class.isAssignableFrom(newClass))
				throw new RuntimeException(newClass.toString());
			final int closeParent = s.lastIndexOf(')');
			if(closeParent<=openParent)
				throw new RuntimeException(s);
			
			final Value[] arguments = evaluateArgumentList(s.substring(openParent+1, closeParent));
			
			final Class insteadOfAny;
			if(newClass==EnumAttribute.class)
				insteadOfAny = EnumValue.class;
			else if(newClass==ItemAttribute.class)
				insteadOfAny = Item.class;
			else
				insteadOfAny = null;

			final Object[] argumentInstances = toInstances(arguments, insteadOfAny);
			final Class[] argumentClasses = toClasses(arguments);
			
			final Constructor constructor;
			try
			{
				constructor = findConstructor(newClass, argumentClasses);
			}
			catch(NoSuchMethodException e)
			{
				throw new RuntimeException(e);
			}
			
			try
			{
				return new Value(constructor.newInstance(argumentInstances), newClass);
			}
			catch(InstantiationException e)
			{
				throw new RuntimeException(e);
			}
			catch(InvocationTargetException e)
			{
				throw new RuntimeException(e.getTargetException());
			}
			catch(IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}
		else if((feature = (JavaFeature)attributes.get(s))!=null)
		{
			if(!(feature instanceof JavaAttribute))
				throw new RuntimeException(feature.toString());
			final JavaAttribute a = (JavaAttribute)feature;
			if((a.modifier & (Modifier.STATIC|Modifier.FINAL))!=(Modifier.STATIC|Modifier.FINAL))
				throw new RuntimeException(feature.toString()+'-'+Modifier.toString(a.modifier));
			//System.out.println("----------"+feature.toString());
			return a.evaluate();
		}
		else if(s.endsWith(CLASS))
		{
			final String className = s.substring(0, s.length()-CLASS.length());
			try
			{
				return new Value(file.findType(className.trim()), Class.class);
			}
			catch(InjectorParseException e)
			{
				return ANY_CLASS;
			}
		}
		else
		{
			final int dot = s.indexOf('.');
			if(dot>=0)
			{
				final String prefix = s.substring(0, dot);
				final String postfix = s.substring(dot+1);
				CopeClass aClass = null;
				try
				{
					aClass = file.repository.getCopeClass(prefix);
				}
				catch(RuntimeException e)
				{
					if(!e.getMessage().startsWith("no cope class for ")) // TODO better exception
						throw new RuntimeException("bad exception", e);
				}

				if(aClass!=null)
				{
					return aClass.javaClass.evaluate(postfix);
				}
				else
				{
					final Value left = evaluate(prefix);

					int openParent = postfix.indexOf('(');
					if(openParent<0)
						throw new RuntimeException(postfix);
					final String featureName = postfix.substring(0, openParent);
					
					final int closeParent = postfix.lastIndexOf(')');
					if(closeParent<=openParent)
						throw new RuntimeException(s);

					final Value[] arguments = evaluateArgumentList(postfix.substring(openParent+1, closeParent));
					
					final Object[] argumentInstances = toInstances(arguments, null);
					final Class[] argumentClasses = toClasses(arguments);
					

					final Method m;
					try
					{
						m = findMethod(left.clazz, featureName, argumentClasses);
					}
					catch(NoSuchMethodException e2)
					{
						throw new RuntimeException(e2);
					}
					
					try
					{
						return new Value(m.invoke(left.instance, argumentInstances), m.getReturnType());
					}
					catch(IllegalAccessException e2)
					{
						throw new RuntimeException(e2);
					}
					catch(InvocationTargetException e2)
					{
						throw new RuntimeException(e2.getTargetException());
					}
				}
			}
			else
			{
				int openParent = s.indexOf('(');
				if(openParent>=0)
				{
					final String methodName = s.substring(0, openParent);
					
					final int closeParent = s.lastIndexOf(')');
					if(closeParent<=openParent)
						throw new RuntimeException(s);

					final Value[] arguments = evaluateArgumentList(s.substring(openParent+1, closeParent));
					
					final Object[] argumentInstances = toInstances(arguments, null);
					final Class[] argumentClasses = toClasses(arguments);
					
					final Method m;
					try
					{
						m = findMethod(Item.class, methodName, argumentClasses);
					}
					catch(NoSuchMethodException e2)
					{
						throw new RuntimeException(e2);
					}
					
					try
					{
						return new Value(m.invoke(null, argumentInstances), m.getReturnType());
					}
					catch(IllegalAccessException e2)
					{
						throw new RuntimeException(e2);
					}
					catch(InvocationTargetException e2)
					{
						throw new RuntimeException(e2.getTargetException());
					}
				}
				else
				{
					try
					{
						final Field f = Item.class.getField(s);
						final int m = f.getModifiers();
						if((m & (Modifier.STATIC|Modifier.FINAL))!=(Modifier.STATIC|Modifier.FINAL))
							throw new RuntimeException(feature.toString()+'-'+Modifier.toString(m));
						//System.out.println("----------"+f.getName());
						final Object value = f.get(null);
						//System.out.println("----------"+value.toString());
						return new Value(value, f.getType());
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
	}
	
	private static final Constructor findConstructor(final Class aClass, final Class[] params) throws NoSuchMethodException
	{
		final int paramsLength = params.length;
		final Constructor[] all = aClass.getConstructors();
		Constructor result = null;
		
		//System.out.println("------------"+params);
		constructorloop:
		for(int i = 0; i<all.length; i++)
		{
			final Constructor c = all[i];
			//System.out.println("--------------"+c);
			final Class[] currentParams = c.getParameterTypes();
			if(paramsLength!=currentParams.length)
				continue;

			for(int j = 0; j<paramsLength; j++)
			{
				if(!currentParams[j].isAssignableFrom(params[j]))
					continue constructorloop;
			}
			if(result!=null)
				throw new RuntimeException("ambigous constructor");
			result = c;
		}
		if(result==null)
		{
			aClass.getConstructor(params);
			throw new RuntimeException(); // must not happen
		}
		
		return result;
	}

	private static final Method findMethod(final Class aClass, final String name, final Class[] params) throws NoSuchMethodException
	{
		final int paramsLength = params.length;
		final Method[] all = aClass.getMethods();
		Method result = null;
		
		//System.out.println("------------"+params);
		constructorloop:
		for(int i = 0; i<all.length; i++)
		{
			final Method m = all[i];
			//System.out.println("--------------"+c);
			if(!name.equals(m.getName()))
				continue;
					
			final Class[] currentParams = m.getParameterTypes();
			if(paramsLength!=currentParams.length)
				continue;

			for(int j = 0; j<paramsLength; j++)
			{
				if(!currentParams[j].isAssignableFrom(params[j]))
					continue constructorloop;
			}
			if(result!=null)
				throw new RuntimeException("ambigous constructor");
			result = m;
		}
		if(result==null)
		{
			aClass.getConstructor(params);
			throw new RuntimeException(); // must not happen
		}
		
		return result;
	}

	static class AnyHash extends Hash
	{
		public AnyHash(final StringAttribute storage)
		{
			super(storage);
		}

		public AnyHash(final Option storageOption)
		{
			super(storageOption);
		}

		public String hash(final String plainText)
		{
			throw new RuntimeException(); // should not happen
		}
	}
}
