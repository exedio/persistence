/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.EnumAnnotatedElement;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public final class EnumSetField<E extends Enum<E>> extends Pattern implements Settable<EnumSet<E>>
{
	private static final long serialVersionUID = 1l;

	private final Class<E> elementClass;
	private final EnumMap<E, BooleanField> fields;
	private final boolean isFinal;

	private EnumSetField(
			final Class<E> elementClass,
			final boolean isFinal)
	{
		this.elementClass = elementClass;
		this.fields = new EnumMap<>(elementClass);
		this.isFinal = isFinal;

		for(final E element : elementClass.getEnumConstants())
		{
			BooleanField value = new BooleanField().defaultTo(false);
			if(isFinal)
				value = value.toFinal();
			addSource(value, element.name(), EnumAnnotatedElement.get(element));
			fields.put(element, value);
		}
	}

	public static final <E extends Enum<E>> EnumSetField<E> create(final Class<E> elementClass)
	{
		return new EnumSetField<>(elementClass, false);
	}

	public EnumSetField<E> toFinal()
	{
		return new EnumSetField<>(elementClass, true);
	}

	public Class<E> getElementClass()
	{
		return elementClass;
	}

	public BooleanField getField(final E element)
	{
		return fields.get(element);
	}

	private void assertElement(final E element)
	{
		requireNonNull(element, "element");
		if(elementClass!=element.getClass())
			throw new ClassCastException("expected a " + elementClass.getName() + ", but was a " + element.getClass().getName());
	}

	@Wrap(order=10)
	public boolean contains(final Item item, @Parameter("element") final E element)
	{
		assertElement(element);
		return fields.get(element).get(item);
	}

	@Wrap(order=20, hide=FinalSettableGetter.class)
	public void add(final Item item, @Parameter("element") final E element)
	{
		assertElement(element);
		fields.get(element).set(item, true);
	}

	@Wrap(order=30, hide=FinalSettableGetter.class)
	public void remove(final Item item, @Parameter("element") final E element)
	{
		assertElement(element);
		fields.get(element).set(item, false);
	}

	/**
	 * BEWARE:
	 * Results are not unmodifiable, since EnumSets cannot be unmodifiable.
	 */
	@Wrap(order=40)
	public EnumSet<E> get(final Item item)
	{
		final EnumSet<E> result = EnumSet.<E>noneOf(elementClass);
		for(final E element : fields.keySet())
		{
			if(fields.get(element).getMandatory(item))
				result.add(element);
		}
		return result;
	}

	@Wrap(order=50, hide=FinalSettableGetter.class)
	public void set(final Item item, final EnumSet<E> value)
	{
		final SetValue<?>[] setValues = new SetValue<?>[fields.size()];
		int i = 0;
		for(final E element : fields.keySet())
			setValues[i++] = fields.get(element).map(value.contains(element));
		item.set(setValues);
	}

	@Override
	public SetValue<EnumSet<E>> map(final EnumSet<E> value)
	{
		return SetValue.map(this, value);
	}

	@Override
	public SetValue<?>[] execute(final EnumSet<E> value, final Item exceptionItem)
	{
		if(value==null)
			throw MandatoryViolationException.create(this, exceptionItem);

		final E[] elements = elementClass.getEnumConstants();
		final SetValue<?>[] result = new SetValue<?>[elements.length];
		for(final E element : elements)
			result[element.ordinal()] = fields.get(element).map(value.contains(element));
		return result;
	}

	@Override
	public boolean isFinal()
	{
		return isFinal;
	}

	@Override
	public boolean isMandatory()
	{
		return true; // set is never null
	}

	@Override
	public boolean isInitial()
	{
		return false;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return Collections.emptySet();
	}

	public Condition contains(final E element)
	{
		assertElement(element);
		return getField(element).equal(true);
	}

	// ------------------- deprecated stuff -------------------

	@Override
	@Deprecated
	public Type getInitialType()
	{
		throw new RuntimeException("not implemented");
	}

	/**
	 * @deprecated Use {@link #create(Class)} instead
	 */
	@Deprecated
	public static final <E extends Enum<E>> EnumSetField<E> newSet(final Class<E> elementClass)
	{
		return create(elementClass);
	}

	/**
	 * @deprecated Use {@link #getElementClass()} instead
	 */
	@Deprecated
	public Class<E> getKeyClass()
	{
		return getElementClass();
	}
}
