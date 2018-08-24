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

import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Pattern;
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
	final Kind kind;

	private final ArrayList<F> features = new ArrayList<>();
	private final TreeMap<String, F> featureMap = new TreeMap<>();

	CopeType(final Kind kind)
	{
		this.kind = requireNonNull(kind);
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

	final boolean isItem()
	{
		return kind.isItem;
	}

	/** return null if the type has no persistent supertype */
	abstract CopeType<?> getSuperclass();

	final boolean allowSubtypes()
	{
		assertNotBuildStage();

		return !isFinal();
	}

	final int getSubtypeModifier()
	{
		return allowSubtypes() ? PROTECTED : PRIVATE;
	}

	final void register(final F feature)
	{
		assertNotBuildStage();
		assertNotGenerateStage();

		features.add(feature);
		final Object collision = featureMap.put(feature.getName(), feature);
		assert collision==null : feature.getName();
	}

	final CopeFeature getFeatureByInstance(final Object instance)
	{
		final CopeFeature own = getDeclaredFeatureByInstance(instance);
		if (own!=null)
			return own;
		for (final CopeFeature container: getFeatures())
		{
			final Object containerInstance = container.getInstance();
			if (containerInstance instanceof Pattern)
			{
				final Pattern patternInstance = (Pattern) containerInstance;
				for (final Feature namedSource: patternInstance.getSourceFeatures())
				{
					if (namedSource==instance)
					{
						final String componentName = namedSource.getName().substring(patternInstance.getName().length() + 1);
						return new ComponentFeature(container, namedSource, componentName);
					}
				}
			}
		}
		final CopeType<?> superclass = getSuperclass();
		if (superclass!=null)
		{
			//noinspection TailRecursion
			return superclass.getFeatureByInstance(instance);
		}
		return null;
	}

	abstract CopeFeature getDeclaredFeatureByInstance(final Object instance);

	final F getFeature(final String name)
	{
		assertNotBuildStage();
		return featureMap.get(name);
	}

	final List<F> getFeatures()
	{
		assertNotBuildStage();
		return Collections.unmodifiableList(features);
	}

	final boolean hasInitialConstructor()
	{
		return getOption().constructor().exists();
	}

	final int getInitialConstructorModifier()
	{
		InternalVisibility result = getVisibility();
		for(final CopeFeature initialFeature : getInitialFeatures())
		{
			final InternalVisibility initialFeatureVisibility = initialFeature.getVisibility();
			if(result.ordinal()<initialFeatureVisibility.ordinal())
				result = initialFeatureVisibility;
		}

		return getOption().constructor().getModifier(result.modifier);
	}

	private ArrayList<CopeFeature> initialFeatures = null;
	private TreeSet<Class<? extends Throwable>> constructorExceptions = null;

	private void makeInitialFeaturesAndConstructorExceptions()
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

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // method is not public
	final List<CopeFeature> getInitialFeatures()
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
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // method is not public
	final SortedSet<Class<? extends Throwable>> getConstructorExceptions()
	{
		if(constructorExceptions == null)
			makeInitialFeaturesAndConstructorExceptions();
		return constructorExceptions;
	}

	static final Comparator<Class<?>> CLASS_COMPARATOR =
			Comparator.comparing(Class::getName);

	@Override
	public final String toString()
	{
		return getName();
	}

	abstract int getTypeParameters();

	abstract String getCanonicalName();

	abstract int getModifier();

	abstract void assertNotBuildStage();

	abstract void assertNotGenerateStage();

}
