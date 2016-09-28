/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.instrument.CopeFeature.TAG_PREFIX;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

final class CopeType
{
	static final String TAG_TYPE                   = TAG_PREFIX + "type";
	static final String TAG_INITIAL_CONSTRUCTOR    = TAG_PREFIX + "constructor";
	static final String TAG_GENERIC_CONSTRUCTOR    = TAG_PREFIX + "generic.constructor";
	static final String TAG_ACTIVATION_CONSTRUCTOR = TAG_PREFIX + "activation.constructor";
	static final String TAG_INDENT                 = TAG_PREFIX + "indent";

	private static enum Kind
	{
		item, composite, block
	}

	private static Kind toKind(final boolean isItem, final boolean isBlock, final boolean isComposite)
	{
		if (isItem)
		{
			if (isBlock||isComposite) throw new RuntimeException();
			return Kind.item;
		}
		else if (isBlock)
		{
			if (isItem||isComposite) throw new RuntimeException();
			return Kind.block;
		}
		else if (isComposite)
		{
			if (isItem||isBlock) throw new RuntimeException();
			return Kind.composite;
		}
		else
		{
			throw new RuntimeException();
		}
	}

	private static final HashMap<JavaClass, CopeType> copeTypeByJavaClass = new HashMap<>();

	static final CopeType getCopeType(final JavaClass javaClass)
	{
		final CopeType result = copeTypeByJavaClass.get(javaClass);
		//System.out.println("getCopeClass "+javaClass.getFullName()+" "+(result==null?"NULL":result.getName()));
		return result;
	}


	final JavaClass javaClass;
	private final Kind kind;
	final String name;
	final InternalVisibility visibility;
	final WrapperType option;

	private final ArrayList<CopeFeature> features = new ArrayList<>();
	private final TreeMap<String, CopeFeature> featureMap = new TreeMap<>();

	CopeType(final JavaClass javaClass, final boolean isItem, final boolean isBlock, final boolean isComposite)
	{
		this.javaClass = javaClass;
		this.kind = toKind(isItem, isBlock, isComposite);
		if (javaClass.classExtends==null) throw new RuntimeException();
		this.name = javaClass.name;
		this.visibility = javaClass.getVisibility();
		this.option = Tags.cascade(
				javaClass,
				Tags.forType(javaClass.docComment),
				javaClass.typeOption,
				OPTION_DEFAULT);
		copeTypeByJavaClass.put(javaClass, this);

		javaClass.nameSpace.importStatic(Item.class);
		javaClass.file.repository.add(this);
	}

	private static final WrapperType OPTION_DEFAULT = new WrapperType()
	{
		@Override public Class<? extends Annotation> annotationType() { throw new RuntimeException(); }
		@Override public Visibility type() { return Visibility.DEFAULT; }
		@Override public Visibility constructor() { return Visibility.DEFAULT; }
		@Override public Visibility genericConstructor() { return Visibility.DEFAULT; }
		@Override public Visibility activationConstructor() { return Visibility.DEFAULT; }
		@Override public int indent() { return 1; }
		@Override public boolean comments() { return true; }
	};

	private boolean isFinal()
	{
		return javaClass.isFinal();
	}

	public boolean isInterface()
	{
		return javaClass.isInterface();
	}

	boolean isBlock()
	{
		return kind==Kind.block;
	}

	boolean isItem()
	{
		return kind==Kind.item;
	}

	boolean isComposite()
	{
		return kind==Kind.composite;
	}

	/** @return null if the type has no field with that name */
	JavaField getField(final String name)
	{
		return javaClass.getField(name);
	}

	private CopeType supertype;

	void endBuildStage()
	{
		assert !javaClass.file.repository.isBuildStage();
		assert javaClass.file.repository.isGenerateStage();

		if(!isItem())
			return;

		final Class<?> externalType = javaClass.file.findTypeExternally(javaClass.classExtends);
		if(externalType==Item.class)
		{
			supertype = null;
		}
		else
		{
			supertype = javaClass.file.repository.getCopeType(javaClass.classExtends);
			if (!supertype.isItem()) throw new RuntimeException();
		}
	}

	public CopeType getSuperclass()
	{
		assert !javaClass.file.repository.isBuildStage();

		return supertype;
	}

	boolean allowSubtypes()
	{
		assert !javaClass.file.repository.isBuildStage();

		return !isFinal();
	}

	int getSubtypeModifier()
	{
		return allowSubtypes() ? PROTECTED : PRIVATE;
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
		return option.constructor().exists();
	}

	public int getInitialConstructorModifier()
	{
		InternalVisibility inheritedVisibility = visibility;
		for(final CopeFeature initialFeature : getInitialFeatures())
		{
			final InternalVisibility intialFeatureVisibility = initialFeature.visibility;
			if(inheritedVisibility.ordinal()<intialFeatureVisibility.ordinal())
				inheritedVisibility = intialFeatureVisibility;
		}

		return option.constructor().getModifier(inheritedVisibility.modifier);
	}

	private ArrayList<CopeFeature> initialFeatures = null;
	private TreeSet<Class<? extends Throwable>> constructorExceptions = null;

	private final void makeInitialFeaturesAndConstructorExceptions()
	{
		initialFeatures = new ArrayList<>();
		constructorExceptions = new TreeSet<>(CLASS_COMPARATOR);

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

	static final Comparator<Class<?>> CLASS_COMPARATOR =
			(c1, c2) -> c1.getName().compareTo(c2.getName());

	int getSerialVersionUID()
	{
		return name.hashCode();
	}

	@Override
	public String toString()
	{
		return name;
	}

	int getTypeParameters()
	{
		return javaClass.typeParameters;
	}

	String getFullName()
	{
		return javaClass.getFullName();
	}

	int getModifier()
	{
		return javaClass.modifier;
	}
}
