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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.EnumMapField.assertEnum;
import static com.exedio.cope.pattern.EnumMapField.name;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.EnumAnnotatedElement;
import com.exedio.cope.misc.ReflectionTypes;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
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
			addSourceFeature(value, name(element), EnumAnnotatedElement.get(element));
			fields.put(element, value);
		}
	}

	public static <E extends Enum<E>> EnumSetField<E> create(final Class<E> elementClass)
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

	private BooleanField field(final E element)
	{
		assertEnum("element", elementClass, element);
		return fields.get(element);
	}

	@Wrap(order=10)
	public boolean contains(@Nonnull final Item item, @Nonnull @Parameter("element") final E element)
	{
		return field(element).get(item);
	}

	@Wrap(order=20, hide=FinalSettableGetter.class)
	public void add(@Nonnull final Item item, @Nonnull @Parameter("element") final E element)
	{
		FinalViolationException.check(this, item);

		field(element).set(item, true);
	}

	@Wrap(order=30, hide=FinalSettableGetter.class)
	public void remove(@Nonnull final Item item, @Nonnull @Parameter("element") final E element)
	{
		FinalViolationException.check(this, item);

		field(element).set(item, false);
	}

	/**
	 * BEWARE:
	 * Results are not unmodifiable, since EnumSets cannot be unmodifiable.
	 */
	@Wrap(order=40)
	@Nonnull
	public EnumSet<E> get(@Nonnull final Item item)
	{
		final EnumSet<E> result = EnumSet.noneOf(elementClass);
		for(final E element : fields.keySet())
		{
			if(fields.get(element).getMandatory(item))
				result.add(element);
		}
		return result;
	}

	@Wrap(order=50, hide=FinalSettableGetter.class)
	public void set(@Nonnull final Item item, @Nonnull final EnumSet<E> value)
	{
		FinalViolationException.check(this, item);
		MandatoryViolationException.requireNonNull(value, this, item);

		final SetValue<?>[] setValues = new SetValue<?>[fields.size()];
		int i = 0;
		for(final E element : fields.keySet())
			setValues[i++] = SetValue.map(fields.get(element), value.contains(element));
		item.set(setValues);
	}

	@Override
	public SetValue<?>[] execute(final EnumSet<E> value, final Item exceptionItem)
	{
		if(value==null)
			throw MandatoryViolationException.create(this, exceptionItem);

		final E[] elements = elementClass.getEnumConstants();
		final SetValue<?>[] result = new SetValue<?>[elements.length];
		for(final E element : elements)
			result[element.ordinal()] = SetValue.map(fields.get(element), value.contains(element));
		return result;
	}

	@Override
	public boolean isFinal()
	{
		return isFinal;
	}

	/**
	 * Does always return {@code true}.
	 * An {@code EnumSetField} may store an empty set,
	 * but never null.
	 */
	@Override
	public boolean isMandatory()
	{
		return true;
	}

	@Override
	public java.lang.reflect.Type getInitialType()
	{
		return ReflectionTypes.parameterized(EnumSet.class, elementClass);
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
		return field(element).equal(true);
	}

	public Condition isEmpty()
	{
		final Condition[] c = new Condition[fields.size()];
		int i = 0;
		for(final Enum<E> e : elementClass.getEnumConstants())
			//noinspection SuspiciousMethodCalls OK: bug in idea
			c[i++] = fields.get(e).equal(false);
		return Cope.and(c);
	}

	public Condition isNotEmpty()
	{
		final Condition[] c = new Condition[fields.size()];
		int i = 0;
		for(final Enum<E> e : elementClass.getEnumConstants())
			//noinspection SuspiciousMethodCalls OK: bug in idea
			c[i++] = fields.get(e).equal(true);
		return Cope.or(c);
	}
}
