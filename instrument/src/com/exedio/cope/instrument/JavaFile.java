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
import java.util.Collections;
import java.util.List;

import bsh.UtilEvalError;

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
	final CopeNameSpace nameSpace;
	
	/**
	 * Defines a name space, that does not depend on
	 * information gathered by the instrumentor,
	 * thus can be used in build stage.
	 */
	private final CopeNameSpace externalNameSpace;
	
	private String packagename;
	
	/**
	 * Distiguishes two stages in life cycle of this object:
	 * getting imports via addImport and finding types via findType.
	 * @see #addImport
	 * @see #findType
	 */
	private boolean buildStageForImports = true;
	
	final JavaRepository repository;
	final ArrayList<JavaClass> classes = new ArrayList<JavaClass>();
	
	final StringBuffer buffer = new StringBuffer();

	public JavaFile(final JavaRepository repository)
	{
		this.nameSpace = new CopeNameSpace(repository.nameSpace);
		this.externalNameSpace = new CopeNameSpace(repository.externalNameSpace);
		
		this.repository = repository;
		repository.add(this);
	}
	
	void add(JavaClass javaClass)
	{
		assert repository.isBuildStage();
		classes.add(javaClass);
	}
	
	List<JavaClass> getClasses()
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
		if(!buildStageForImports)
			throw new RuntimeException();
		if(this.packagename!=null)
			throw new InjectorParseException("only one package statement allowed.");
		
		this.packagename=packagename;
		nameSpace.importPackage(packagename);
		externalNameSpace.importPackage(packagename);
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
		if(!buildStageForImports)
			throw new RuntimeException();
		
		if(importname.endsWith(".*"))
		{
			final String packageName = importname.substring(0,importname.length()-2);
			nameSpace.importPackage(packageName);
			externalNameSpace.importPackage(packageName);
		}
		else
		{
			nameSpace.importClass(importname);
			externalNameSpace.importClass(importname);
		}
	}
	
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
	 * Returns null if no such type is found.
	 */
	public final Class findType(String typename)
	{
		//System.out.println("findtype: >"+typename+"<");
		
		buildStageForImports=false;
		
		try
		{
			return externalNameSpace.getClass(typename);
		}
		catch(UtilEvalError e)
		{
			throw new RuntimeException(typename, e);
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
	
	int getBufferPosition()
	{
		assert repository.isBuildStage();
		return buffer.length();
	}
	
}
