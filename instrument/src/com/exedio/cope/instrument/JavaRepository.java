/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bsh.Interpreter;
import bsh.UtilEvalError;

import com.exedio.cope.Field;
import com.exedio.cope.BooleanField;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.DayField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Function;
import com.exedio.cope.IntegerFunction;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.StringField;
import com.exedio.cope.StringFunction;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.FieldList;
import com.exedio.cope.pattern.FieldListLimited;
import com.exedio.cope.pattern.FieldMap;
import com.exedio.cope.pattern.FieldMapLimited;
import com.exedio.cope.pattern.FieldSet;
import com.exedio.cope.pattern.Hash;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.Qualifier;
import com.exedio.cope.pattern.Relation;
import com.exedio.cope.pattern.VectorRelation;

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
	
	// reusing externalNameSpace is more efficient than another root nameSpace
	final CopeNameSpace nameSpace = new NS(externalNameSpace);

	final Interpreter interpreter = new Interpreter();
	
	static enum Stage
	{
		BUILD,
		BETWEEN,
		GENERATE;
	}
	
	private Stage stage = Stage.BUILD;
	
	private final ArrayList<JavaFile> files = new ArrayList<JavaFile>();
	private final HashMap<String, JavaClass> javaClassBySimpleName = new HashMap<String, JavaClass>();
	private final HashMap<String, JavaClass> javaClassByFullName = new HashMap<String, JavaClass>();
	
	private final HashMap<JavaClass, CopeType> copeTypeByJavaClass = new HashMap<JavaClass, CopeType>();
	
	void endBuildStage()
	{
		assert stage==Stage.BUILD;
		stage = Stage.BETWEEN;
		
		// TODO put this into a new class CopeType
		for(final JavaClass javaClass : javaClassByFullName.values())
		{
			if(javaClass.isInterface())
				continue;
			
			if(isItem(javaClass))
			{
				final CopeType type = new CopeType(javaClass);

				for(final JavaAttribute javaAttribute : javaClass.getAttributes())
				{
					final int modifier = javaAttribute.modifier;

					if(Modifier.isFinal(modifier) && Modifier.isStatic(modifier))
					{
						final Class typeClass = javaAttribute.file.findTypeExternally(javaAttribute.type);

						if(typeClass!=null)
						{
							if(Function.class.isAssignableFrom(typeClass)||Field.class.isAssignableFrom(typeClass))
							{
								if(
									IntegerFunction.class.isAssignableFrom(typeClass) ||
									LongField.class.equals(typeClass) ||
									DoubleField.class.equals(typeClass) ||
									BooleanField.class.equals(typeClass) ||
									DateField.class.equals(typeClass) ||
									DayField.class.equals(typeClass) ||
									StringFunction.class.isAssignableFrom(typeClass))
								{
									new CopeNativeAttribute(type, javaAttribute, typeClass);
								}
								else if(
									EnumField.class.equals(typeClass)||
									ItemField.class.equals(typeClass))
								{
									new CopeObjectAttribute(type, javaAttribute);
								}
								else if(DataField.class.equals(typeClass))
								{
									new CopeDataAttribute(type, javaAttribute);
								}
								else
									throw new RuntimeException(typeClass.toString());
							}
							else if(UniqueConstraint.class.isAssignableFrom(typeClass))
								new CopeUniqueConstraint(type, javaAttribute);
							else if(Qualifier.class.isAssignableFrom(typeClass))
								new CopeQualifier(type, javaAttribute);
							else if(Hash.class.isAssignableFrom(typeClass))
								new CopeHash(type, javaAttribute);
							else if(FieldList.class.isAssignableFrom(typeClass) || FieldListLimited.class.isAssignableFrom(typeClass))
								new CopeAttributeList(type, javaAttribute, false);
							else if(FieldSet.class.isAssignableFrom(typeClass))
								new CopeAttributeList(type, javaAttribute, true);
							else if(FieldMap.class.isAssignableFrom(typeClass) || FieldMapLimited.class.isAssignableFrom(typeClass))
								new CopeAttributeMap(type, javaAttribute);
							else if(Media.class.isAssignableFrom(typeClass))
								new CopeMedia(type, javaAttribute);
							else if(Relation.class.isAssignableFrom(typeClass))
								new CopeRelation(type, javaAttribute, false);
							else if(VectorRelation.class.isAssignableFrom(typeClass))
								new CopeRelation(type, javaAttribute, true);
						}
					}
				}
			}
		}
		
		stage = Stage.GENERATE;

		for(final CopeType ct : copeTypeByJavaClass.values())
			ct.endBuildStage();
	}
	
	boolean isBuildStage()
	{
		return stage==Stage.BUILD;
	}

	boolean isGenerateStage()
	{
		return stage==Stage.GENERATE;
	}
	
	boolean isItem(JavaClass javaClass)
	{
		//System.out.println("--------------"+javaClass.getFullName());
		while(true)
		{
			final String classExtends = javaClass.classExtends;
			if(classExtends==null)
				return false;
			
			//System.out.println("--------------**"+javaClass.getFullName());
			{
				final Class extendsClass = javaClass.file.findTypeExternally(classExtends);
				//System.out.println("--------------*1"+extendsClass);
				if(extendsClass!=null)
					return Item.class.isAssignableFrom(extendsClass);
			}
			{
				final JavaClass byName = getJavaClass(classExtends);
				//System.out.println("--------------*2"+byName);
				if(byName!=null)
				{
					javaClass = byName;
					continue;
				}
			}
			System.out.println("unknown type " + classExtends + " in " + javaClass);
			return false;
		}
	}

	void add(final JavaFile file)
	{
		assert stage==Stage.BUILD;
		files.add(file);
	}
	
	final List<JavaFile> getFiles()
	{
		assert stage==Stage.GENERATE;
		return files;
	}
	
	void add(final JavaClass javaClass)
	{
		assert stage==Stage.BUILD;
		
		//final JavaClass previous =
		javaClassBySimpleName.put(javaClass.name, javaClass);
		
		//if(previous!=null) System.out.println("collision:"+previous.getFullName()+','+javaClass.getFullName());
		
		if(javaClassByFullName.put(javaClass.getFullName(), javaClass)!=null)
			throw new RuntimeException(javaClass.getFullName());
	}
	
	final JavaClass getJavaClass(final String name)
	{
		return (name.indexOf('.')<0) ? javaClassBySimpleName.get(name) : javaClassByFullName.get(name);
	}
	
	void add(final CopeType copeType)
	{
		assert stage==Stage.BETWEEN;
		
		if(copeTypeByJavaClass.put(copeType.javaClass, copeType)!=null)
			throw new RuntimeException(copeType.javaClass.getFullName());
		//System.out.println("--------- put cope type: "+name);
	}
	
	CopeType getCopeType(final String className)
	{
		assert stage==Stage.BETWEEN || stage==Stage.GENERATE;
		
		final JavaClass javaClass = getJavaClass(className);
		if(javaClass==null)
			throw new RuntimeException("no java class for "+className);
		
		final CopeType result = copeTypeByJavaClass.get(javaClass);
		if(result==null)
			throw new RuntimeException("no cope type for "+className);
		
		return result;
	}

	private final class NS extends CopeNameSpace
	{
		private static final long serialVersionUID = 8362587526354862l;
		
		NS(final CopeNameSpace parent)
		{
			super(parent);
		}
		
		@Override
		public Class getClass(final String name) throws UtilEvalError
		{
			assert stage==Stage.GENERATE;
			
			final Class superResult = super.getClass(name);
			if(superResult!=null)
				return superResult;
			
			if(name.endsWith("Hash")) // TODO this is a hack
				return DummyHash.class;
			
			final JavaClass javaClass = getJavaClass(name);
			if(javaClass!=null)
			{
				//System.out.println("++++++++++++++++getClass(\""+name+"\") == "+javaClass+","+javaClass.isEnum);
				if(javaClass.isEnum)
					return EnumBeanShellHackClass.class;
				if(isItem(javaClass))
					return DummyItem.class;
			}
			
			return null;
		}
	}
	
	// BEWARE
	// The name of this enum and its only enum value
	// must match the names used in the hack of the beanshell.
	// see bsh-core.PATCH
	public static enum EnumBeanShellHackClass
	{
		BEANSHELL_HACK_ATTRIBUTE;
	}
	
	static class DummyItem extends Item
	{
		// just a dummy
	}
	
	public static final class DummyHash extends Hash
	{
		public DummyHash()
		{
			super();
		}
		
		public DummyHash(final StringField storage)
		{
			super(storage);
		}
		
		public DummyHash(final com.exedio.cope.Field.Option storageOption)
		{
			super();
		}
		
		@Override
		public DummyHash optional()
		{
			return new DummyHash(getStorage().optional());
		}
		
		@Override
		public String hash(final String plainText)
		{
			throw new RuntimeException(); // should not happen
		}
	}
}
