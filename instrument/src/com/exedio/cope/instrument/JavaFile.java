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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Represents a parsed java file.
 * Manages the mapping of type names and types.
 * This depends on the current package and all
 * imported packages/classes.
 * 
 * @author Ralf Wiebicke
 */
final class JavaFile
{
	private String packagename;
	
	/**
	 * Contains all imported classes of this source file.
	 *
	 * More formally it contains the TypeName's imported
	 * by a SingleTypeImportDeclaration as defined in
	 * Java Language Specification 7.5.1
	 * as values, and the their corresponding simple names
	 * as key.
	 *
	 * @element-type String
	 * @key-type String
	 */
	private HashMap import_single=new HashMap();
	
	/**
	 * Contains all imported packages of this source file
	 * with a trailing dot.
	 *
	 * More formally it contains the PackageName's imported
	 * by a TypeImportOnDemandDeclaration as defined in
	 * Java Language Specification 7.5.2
	 * suffixed by a single dot.
	 *
	 * This implies, that the package names dont have the
	 * trailing '*' anymore, but the dot before '*' is still
	 * present.
	 *
	 * Additionally contains &quot;java.lang.&quot; to implement
	 * Java Language Specification 7.5.3. &quot;Automatic Imports&quot;.
	 *
	 * @element-type String
	 */
	private HashSet import_demand=new HashSet();
	
	/**
	 * Distiguishes two stages in life cycle of this object:
	 * getting imports via addImport and finding types via findType.
	 * @see #addImport
	 * @see #findType
	 */
	private boolean buildStage=true;
	
	final JavaRepository repository;
	final ArrayList classes = new ArrayList();
	
	final StringWriter buffer = new StringWriter();

	public JavaFile(final JavaRepository repository)
	{
		// implements Java Language Specification 7.5.3. "Automatic Imports"
		import_demand.add("java.lang.");
		this.repository = repository;
		repository.add(this);
	}
	
	void add(JavaClass javaClass)
	{
		assert repository.isBuildStage();
		classes.add(javaClass);
	}
	
	List getClasses()
	{
		assert !repository.isBuildStage();
		return Collections.unmodifiableList(classes);
	}
	
	/**
	 * Sets the package of this file.
	 * Necessary, since the package is not known at construction time.
	 * @param packagename may be null for root package
	 * @throws InjectorParseException if called more than once.
	 */
	public final void setPackage(String packagename)
	throws InjectorParseException
	{
		if(!buildStage)
			throw new RuntimeException();
		if(this.packagename!=null)
			throw new InjectorParseException("only one package statement allowed.");
		
		this.packagename=packagename;
	}
	
	/**
	 * Gets the value of the package statement encountered
	 * in this java file.
	 * Is null, if no package statement found.
	 */
	public final String getPackageName()
	{
		return packagename;
	}
	
	/**
	 * Adds the value of an import statement.
	 */
	public final void addImport(String importname)
	throws InjectorParseException
	{
		if(!buildStage)
			throw new RuntimeException();
		
		if(importname.endsWith(".*"))
			import_demand.add(importname.substring(0,importname.length()-1));
		else
		{
			// implements Java Language Specification 7.5.1 "Single-Type-Import Declaration"
			String s=(String)(import_single.put(extractClassName(importname), importname));
			if(s!=null)
			{
				// this simple name was imported before
				if(!s.equals(importname))
					throw new InjectorParseException(
					"imported "+s+
					" and "+importname+
					" which is forbidden by "+
					"Java Language Specification 7.5.1 'Single-Type-Import Declaration'");
				// else this is a duplicate import statement and therefore ignored
			}
		}
	}
	
	private static HashMap nativeTypes;
	
	/**
	 * Maps type names to types.
	 * This mapping depends on the import statements encountered
	 * in this java file.
	 *
	 * Nearly (TODO) implements
	 * Java Language Specification 6.5.4 &quot;Meaning of Type Names&quot;
	 * and
	 * Java Language Specification 7.5 &quot;Import Declarations&quot;
	 *
	 * Note, that the result depends also on the classes that are
	 * available (in the CLASSPATH) when running this method.
	 * Using this method in the ocl injector assumes,
	 * that at injection time the same classes are available
	 * as at compile time of the modified user code.
	 *
	 * @throws InjectorParseException if no type could be found.
	 *         Never returns null.
	 */
	public final Class findType(String typename)
	throws InjectorParseException
	{
		//System.out.println("findtype: >"+typename+"<");
		
		buildStage=false;
		
		final ClassLoader classLoader = getClass().getClassLoader();

		// implements Java Language Specification 6.5.4.2 "Qualified Type Names"
		// I dont know, how this should work for inner classes, TODO.
		if(typename.indexOf('.')>=0)
		{
			try
			{
				//System.out.println("findType("+typename+"): try explicit :"+typename);
				return Class.forName(typename, false, classLoader);
			}
			catch(ClassNotFoundException e)
			{
				throw new InjectorParseException(e.toString());
			}
		}
		
		// prepare native types
		if(nativeTypes==null)
		{
			nativeTypes = new HashMap(10);
			nativeTypes.put("boolean", boolean.class);
			nativeTypes.put("byte", byte.class);
			nativeTypes.put("short", short.class);
			nativeTypes.put("int", int.class);
			nativeTypes.put("long", long.class);
			nativeTypes.put("float", float.class);
			nativeTypes.put("double", double.class);
			nativeTypes.put("char", char.class);
			nativeTypes.put("void", void.class);
		}
		
		// native types
		{
			final Object nativeType = nativeTypes.get(typename);
			if(nativeType!=null)
				return (Class)nativeType;
		}
		
		
		// implements Java Language Specification 6.5.4.1 "Simple Type Names"
		
		// implements items 1 and 2 of 6.5.4.1
		try
		{
			//System.out.println("findType("+typename+"): try by package :"+arrayBefore + (packagename!=null ? packagename+'.'+typename : typename) + arrayAfter);
			return Class.forName(
			(
				packagename!=null ?
					packagename+'.'+typename :
					typename
			),
			false, classLoader);
		}
		catch(ClassNotFoundException e)
		{};
		
		// implements item 1 of 6.5.4.1
		try
		{
			String s=(String)(import_single.get(typename));
			if(s!=null)
			{
				//System.out.println("findType("+typename+"): try by single :"+s);
				return Class.forName(s, false, classLoader);
			}
		}
		catch(ClassNotFoundException e)
		{
			throw new InjectorParseException(e.toString());
		};
		
		// implements item 3 and 4 of 6.5.4.1
		// java.lang is already in imports_demand
		final ArrayList result = new ArrayList();
		for(Iterator i=import_demand.iterator(); i.hasNext(); )
		{
			String importString=(String)i.next();
			String full_element_type=importString+typename;
			try
			{
				//System.out.println("findType("+typename+"): try by demand :"+full_element_type);
				result.add(Class.forName(full_element_type, false, classLoader));
			}
			catch(ClassNotFoundException e)
			{};
		}

		switch(result.size())
		{
			case 1:
				return (Class)result.iterator().next();
			case 0:
				throw new InjectorParseException("type "+typename+" not found.");
			default:
				final TreeSet packages = new TreeSet();
				for(Iterator i = result.iterator(); i.hasNext(); )
					packages.add(((Class)i.next()).getPackage().getName());

				throw new InjectorParseException(
					"type "+typename+
					" found in imported packages "+packages.toString()+
					". This is ambigous and forbidden by "+
					"Java Language Specification 6.5.4.1. 'Simple Type Names' item 4.");
		}
	}
	
	/**
	 * Extracts the class name from a fully qualified class name
	 * (including package path.)
	 */
	public static String extractClassName(String fullclassname)
	{
		int pos=fullclassname.lastIndexOf('.');
		if(pos>=0)
			return fullclassname.substring(pos+1, fullclassname.length());
		else
			return fullclassname;
	}
	
	/**
	 * Extracts the package path (without trailing dot)
	 * from a fully qualified class name.
	 */
	public static String extractPackageName(String fullclassname)
	{
		int pos=fullclassname.lastIndexOf('.');
		if(pos>=0)
			return fullclassname.substring(0, pos);
		else
			return null;
	}
	
	int getBufferPosition()
	{
		return buffer.getBuffer().length();
	}
	
}
