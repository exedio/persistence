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
import java.util.HashMap;
import java.util.List;

import bsh.UtilEvalError;

import com.exedio.cope.StringAttribute;
import com.exedio.cope.pattern.Hash;

final class JavaRepository
{
	/**
	 * Defines a name space, that does not depend on
	 * information gathered by the instrumentor,
	 * thus can be used in build stage.
	 * Using this in JavaFile greatly reduces number of top name spaces,
	 * for which a new BshClassManager must be created.
	 */
	final CopeNameSpace externalNameSpace = new CopeNameSpace(null);
	
	final CopeNameSpace nameSpace = new NameSpace();
	
	/**
	 * Distiguishes two stages in life cycle of this repository,
	 * and its contents:
	 * building the repository and querying the repository.
	 */
	private boolean buildStage = true;
	
	private boolean generateStage = false;
	
	private final ArrayList<JavaFile> files = new ArrayList<JavaFile>();
	private final HashMap<String, JavaClass> javaClassByShortName = new HashMap<String, JavaClass>();
	private final HashMap<String, JavaClass> javaClassByFullName = new HashMap<String, JavaClass>();
	
	private final HashMap<JavaClass, CopeType> copeTypeByJavaClass = new HashMap<JavaClass, CopeType>();
	
	void endBuildStage()
	{
		assert buildStage;
		assert !generateStage;
		
		generateStage = true;
		
		for(final CopeType ct : copeTypeByJavaClass.values())
			ct.endBuildStage();
		
		buildStage = false;
	}
	
	boolean isBuildStage()
	{
		return buildStage;
	}

	boolean isGenerateStage()
	{
		return generateStage;
	}

	void add(final JavaFile file)
	{
		assert buildStage;
		files.add(file);
	}
	
	final List<JavaFile> getFiles()
	{
		assert !buildStage;
		return files;
	}
	
	void add(final JavaClass javaClass)
	{
		assert buildStage && !generateStage;
		
		//final JavaClass previous =
		javaClassByShortName.put(javaClass.name, javaClass);
		
		//if(previous!=null) System.out.println("collision:"+previous.getFullName()+','+javaClass.getFullName());
		
		if(javaClassByFullName.put(javaClass.getFullName(), javaClass)!=null)
			throw new RuntimeException(javaClass.getFullName());
	}
	
	void add(final CopeType copeType)
	{
		assert buildStage && !generateStage;
		if(copeTypeByJavaClass.put(copeType.javaClass, copeType)!=null)
			throw new RuntimeException(copeType.javaClass.getFullName());
		//System.out.println("--------- put cope type: "+name);
	}
	
	CopeType getCopeType(final String className)
	{
		assert generateStage;
		
		final JavaClass javaClass = (className.indexOf('.')<0) ? javaClassByShortName.get(className) : javaClassByFullName.get(className);
		if(javaClass==null)
			throw new RuntimeException("no java class for "+className);
		
		final CopeType result = copeTypeByJavaClass.get(javaClass);
		if(result==null)
			throw new RuntimeException("no cope type for "+className);
		
		return result;
	}

	final class NameSpace extends CopeNameSpace
	{
		private static final long serialVersionUID = 8362587526354862l;
		
		NameSpace()
		{
			super((CopeNameSpace)null);
		}
		
		public Class getClass(final String name) throws UtilEvalError
		{
			assert generateStage;
			
			final Class superResult = super.getClass(name);
			if(superResult!=null)
				return superResult;
			
			if(name.endsWith("Hash")) // TODO this is a hack
				return DummyHash.class;
			
			return null;
		}
	}
	
	public static final class DummyHash extends Hash
	{
		public DummyHash(final StringAttribute storage)
		{
			super(storage);
		}
		
		public DummyHash(final com.exedio.cope.Attribute.Option storageOption)
		{
			super(storageOption);
		}
		
		public String hash(final String plainText)
		{
			throw new RuntimeException(); // should not happen
		}
	}
}
