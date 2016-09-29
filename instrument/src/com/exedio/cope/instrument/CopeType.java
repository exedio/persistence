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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

abstract class CopeType<F extends CopeFeature>
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


	private final Kind kind;

	private final ArrayList<F> features = new ArrayList<>();
	private final TreeMap<String, F> featureMap = new TreeMap<>();

	CopeType(final boolean isItem, final boolean isBlock, final boolean isComposite)
	{
		this.kind = toKind(isItem, isBlock, isComposite);
	}

	abstract String getName();

	abstract WrapperType getOption();

	final InternalVisibility getVisibility()
	{
		return InternalVisibility.forModifier(getModifier());
	}

	final boolean isFinal()
	{
		return Modifier.isFinal(getModifier());
	}

	abstract boolean isInterface();

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

	abstract Evaluatable getField(final String name);

	abstract CopeType<?> getSuperclass();

	final boolean allowSubtypes()
	{
		assertNotBuildStage();

		return !isFinal();
	}

	int getSubtypeModifier()
	{
		return allowSubtypes() ? PROTECTED : PRIVATE;
	}

	public void register(final F feature)
	{
		assertNotBuildStage();
		assertNotGenerateStage();

		features.add(feature);
		final Object collision = featureMap.put(feature.getName(), feature);
		assert collision==null : feature.getName();
	}

	public CopeFeature getFeature(final String name)
	{
		assertNotBuildStage();
		return featureMap.get(name);
	}

	public List<F> getFeatures()
	{
		assertNotBuildStage();
		return Collections.unmodifiableList(features);
	}

	public boolean hasInitialConstructor()
	{
		return getOption().constructor().exists();
	}

	public int getInitialConstructorModifier()
	{
		InternalVisibility inheritedVisibility = getVisibility();
		for(final CopeFeature initialFeature : getInitialFeatures())
		{
			final InternalVisibility intialFeatureVisibility = initialFeature.getVisibility();
			if(inheritedVisibility.ordinal()<intialFeatureVisibility.ordinal())
				inheritedVisibility = intialFeatureVisibility;
		}

		return getOption().constructor().getModifier(inheritedVisibility.modifier);
	}

	private ArrayList<CopeFeature> initialFeatures = null;
	private TreeSet<Class<? extends Throwable>> constructorExceptions = null;

	private final void makeInitialFeaturesAndConstructorExceptions()
	{
		initialFeatures = new ArrayList<>();
		constructorExceptions = new TreeSet<>(CLASS_COMPARATOR);

		final CopeType<?> superclass = getSuperclass();
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
		return getName().hashCode();
	}

	@Override
	public String toString()
	{
		return getName();
	}

	abstract int getTypeParameters();

	abstract String getFullName();

	abstract int getModifier();

	abstract void assertNotBuildStage();

	abstract void assertGenerateStage();

	abstract void assertNotGenerateStage();

}
