/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;

final class CopeType
{
	private static final String TAG_PREFIX = CopeFeature.TAG_PREFIX;
	static final String TAG_TYPE                     = TAG_PREFIX + "type";
	static final String TAG_INITIAL_CONSTRUCTOR      = TAG_PREFIX + "constructor";
	static final String TAG_GENERIC_CONSTRUCTOR      = TAG_PREFIX + "generic.constructor";
	static final String TAG_REACTIVATION_CONSTRUCTOR = TAG_PREFIX + "reactivation.constructor";
	
	private static final HashMap<JavaClass, CopeType> copeTypeByJavaClass = new HashMap<JavaClass, CopeType>();
	
	static final CopeType getCopeType(final JavaClass javaClass)
	{
		final CopeType result = copeTypeByJavaClass.get(javaClass);
		//System.out.println("getCopeClass "+javaClass.getFullName()+" "+(result==null?"NULL":result.getName()));
		return result;
	}


	final JavaClass javaClass;
	final String name;
	final Visibility visibility;
	final Option typeOption;
	final Option initialConstructorOption;
	final Option genericConstructorOption;
	final Option reactivationConstructorOption;

	private final ArrayList<CopeFeature> features = new ArrayList<CopeFeature>();
	private final TreeMap<String, CopeFeature> featureMap = new TreeMap<String, CopeFeature>();
	
	public CopeType(final JavaClass javaClass)
		throws InjectorParseException
	{
		this.javaClass = javaClass;
		this.name = javaClass.name;
		this.visibility = javaClass.getVisibility();
		copeTypeByJavaClass.put(javaClass, this);
		
		final String docComment = javaClass.getDocComment();
		this.typeOption                    = new Option(Injector.findDocTagLine(docComment, TAG_TYPE),                     false);
		this.initialConstructorOption      = new Option(Injector.findDocTagLine(docComment, TAG_INITIAL_CONSTRUCTOR),      false);
		this.genericConstructorOption      = new Option(Injector.findDocTagLine(docComment, TAG_GENERIC_CONSTRUCTOR),      false);
		this.reactivationConstructorOption = new Option(Injector.findDocTagLine(docComment, TAG_REACTIVATION_CONSTRUCTOR), false);
		//System.out.println("copeTypeByJavaClass "+javaClass.getName());
		javaClass.nameSpace.importStatic(Item.class);
		javaClass.file.repository.add(this);
	}
	
	public boolean isAbstract()
	{
		return javaClass.isAbstract();
	}

	public boolean isInterface()
	{
		return javaClass.isInterface();
	}
	
	private CopeType supertype;
	private ArrayList<CopeType> subtypes = new ArrayList<CopeType>();
	
	void endBuildStage()
	{
		assert !javaClass.file.repository.isBuildStage();
		assert javaClass.file.repository.isGenerateStage();
		
		final String extname = javaClass.classExtends;
		
		if(extname==null)
		{
			supertype = null;
		}
		else
		{
			final Class externalType = javaClass.file.findTypeExternally(extname);
			if(externalType==Item.class)
			{
				supertype = null;
			}
			else
			{
				supertype = javaClass.file.repository.getCopeType(extname);
				supertype.addSubtype(this);
			}
		}
	}

	void addSubtype(final CopeType subtype)
	{
		assert !javaClass.file.repository.isBuildStage();
		assert javaClass.file.repository.isGenerateStage();
		
		subtypes.add(subtype);
	}
	
	public CopeType getSuperclass()
	{
		assert !javaClass.file.repository.isBuildStage();
		
		return supertype;
	}
	
	public List<CopeType> getSubtypes()
	{
		assert !javaClass.file.repository.isBuildStage();
		
		return subtypes;
	}
	
	boolean allowSubTypes()
	{
		assert !javaClass.file.repository.isBuildStage();

		return isAbstract() || !getSubtypes().isEmpty();
	}
	
	public void register(final CopeFeature feature)
	{
		assert !javaClass.file.repository.isBuildStage();
		assert !javaClass.file.repository.isGenerateStage();
		
		features.add(feature);
		final Object collision = featureMap.put(feature.name, feature);
		assert collision==null : feature.name;
	}
	
	public CopeFeature getFeature(final String name)
	{
		assert !javaClass.file.repository.isBuildStage();
		return featureMap.get(name);
	}
	
	public List<CopeFeature> getFeatures()
	{
		assert !javaClass.file.repository.isBuildStage();
		return Collections.unmodifiableList(features);
	}
	
	public boolean hasInitialConstructor()
	{
		return initialConstructorOption.exists;
	}
	
	public int getInitialConstructorModifier()
	{
		Visibility inheritedVisibility = visibility;
		for(final CopeFeature initialFeature : getInitialFeatures())
		{
			final Visibility intialFeatureVisibility = initialFeature.visibility;
			if(inheritedVisibility.ordinal()<intialFeatureVisibility.ordinal())
				inheritedVisibility = intialFeatureVisibility;
		}
		
		return initialConstructorOption.getModifier(inheritedVisibility.modifier);
	}
	
	private ArrayList<CopeFeature> initialFeatures = null;
	private TreeSet<Class<? extends Throwable>> constructorExceptions = null;
	
	private final void makeInitialFeaturesAndConstructorExceptions()
	{
		initialFeatures = new ArrayList<CopeFeature>();
		constructorExceptions = new TreeSet<Class<? extends Throwable>>(CLASS_COMPARATOR);
		
		final CopeType superclass = getSuperclass();
		if(superclass!=null)
		{
			initialFeatures.addAll(superclass.getInitialFeatures());
			constructorExceptions.addAll(superclass.getConstructorExceptions());
		}
		
		for(final CopeFeature feature : getFeatures())
		{
			if(feature.isInitial())
			{
				initialFeatures.add(feature);
				constructorExceptions.addAll(feature.getInitialExceptions());
			}
		}
		constructorExceptions.remove(FinalViolationException.class);
	}

	public final List<CopeFeature> getInitialFeatures()
	{
		if(initialFeatures == null)
			makeInitialFeaturesAndConstructorExceptions();
		return initialFeatures;
	}

	/**
	 * Returns all exceptions, the generated constructor of this class should throw.
	 * This is the unification of throws clauses of all the setters of the
	 * {@link #getInitialFeatures() initial attributes},
	 * but without the FinalViolationException,
	 * because final attributes can only be written in the constructor.
	 */
	public final SortedSet<Class<? extends Throwable>> getConstructorExceptions()
	{
		if(constructorExceptions == null)
			makeInitialFeaturesAndConstructorExceptions();
		return constructorExceptions;
	}

	static final Comparator<Class> CLASS_COMPARATOR = new Comparator<Class>()
	{
		public int compare(final Class c1, final Class c2)
		{
			return c1.getName().compareTo(c2.getName());
		}
	};
	
	int getSerialVersionUID()
	{
		return name.hashCode();
	}
}
