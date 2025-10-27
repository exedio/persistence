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

import static com.exedio.cope.util.Check.requireAtLeast;
import static com.exedio.cope.util.Check.requireNonNegative;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Function;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.ReflectionTypes;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
public final class LimitedListField<E> extends AbstractListField<E> implements Settable<Collection<E>>
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final int minimumSize;
	private final IntegerField length;
	private final FunctionField<E>[] sources;
	private final boolean initial;
	private final boolean isFinal;
	private final CheckConstraint unison;

	private LimitedListField(
			final boolean templateIsMandatory,
			final int minimumSize,
			final FunctionField<E>[] sources)
	{
		this.minimumSize = minimumSize;
		{
			boolean initial = minimumSize>0;
			boolean isFinal = false;
			for(final FunctionField<E> source : sources)
			{
				initial = initial || source.isInitial();
				isFinal = isFinal || source.isFinal();
			}
			this.initial = initial;
			this.isFinal = isFinal;
		}

		this.length = addSourceFeature(
				modify(
						new IntegerField().range(minimumSize, sources.length),
						isFinal),
				"Len", ComputedElement.get());

		this.sources = sources;

		int i = 0;
		for(final FunctionField<E> source : sources)
			addSourceFeature(source, String.valueOf(i++), ComputedElement.get());

		final ArrayList<Condition> unisonConditions = new ArrayList<>(sources.length);
		for(int a = minimumSize; a<sources.length; a++)
		{
			final FunctionField<E> s = sources[a];
			unisonConditions.add(templateIsMandatory
					? length.greater(a).and(s.isNotNull()).or(length.lessOrEqual(a).and(s.isNull()))
					: length.greater(a).or(s.isNull()));
		}
		this.unison = addSourceFeature(new CheckConstraint(Cope.and(unisonConditions)), "unison");
	}

	private static IntegerField modify(
			IntegerField field,
			final boolean isfinal)
	{
		if(isfinal)
			field = field.toFinal();
		if(field.getMinimum()==0)
			field = field.defaultTo(0);
		return field;
	}

	private LimitedListField(final FunctionField<E> template, final int minimumSize, final int maximumSize)
	{
		this(template.isMandatory(), minimumSize, template2Sources(template, minimumSize, maximumSize));
	}

	public static <E> LimitedListField<E> create(final FunctionField<E> template, final int minimumSize, final int maximumSize)
	{
		return new LimitedListField<>(template, minimumSize, maximumSize);
	}

	public static <E> LimitedListField<E> create(final FunctionField<E> template, final int maximumSize)
	{
		return create(template, 0, maximumSize);
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: no generic array creation
	private static <X> FunctionField<X>[] cast(final FunctionField[] o)
	{
		return o;
	}

	private static <Y> FunctionField<Y>[] template2Sources(
			final FunctionField<Y> template,
			final int minimumSize,
			final int maximumSize)
	{
		requireNonNegative(minimumSize, "minimumSize");
		requireAtLeast(maximumSize, "maximumSize", Math.max(2, minimumSize));
		// TODO support exact length. Then there will be no length field.
		if(minimumSize==maximumSize)
			throw new IllegalArgumentException("minimumSize==maximumSize==" + minimumSize + " not yet supported");

		final FunctionField<Y>[] result = cast(new FunctionField<?>[maximumSize]);

		for(int i = 0; i<maximumSize; i++)
			result[i] = i<minimumSize ? template.copy() : template.optional();

		return result;
	}


	/**
	 * @deprecated
	 * Use methods {@link #lengthIs(int)}}, {@link #lengthLess(int)} etc.
	 * or (as the last resort) {@link #getLengthIfExists()}
	 * instead
	 */
	@Deprecated
	public IntegerField getLength()
	{
		return getLengthIfExists();
	}

	/**
	 * <b>BEWARE:</b>
	 * Prepare for a future version, where this method may return null!
	 * <p>
	 * Currently this method always returns an {@code IntegerField}.
	 * But, when {@link #create(FunctionField, int, int)} supports
	 * {@code minimumSize==maximumSize} there will be no length field and
	 * consequentially this method will return {@code null}.
	 * <p>
	 * Therefore, better use methods {@link #lengthIs(int)}}, {@link #lengthLess(int)} etc.
	 * if this is what you need.
	 * Or, ask the maintainer for similar methods if needed.
	 */
	public IntegerField getLengthIfExists()
	{
		return length;
	}

	public List<FunctionField<E>> getListSources()
	{
		return List.of(sources);
	}

	public CheckConstraint getUnison()
	{
		return unison;
	}

	@Override
	public FunctionField<E> getElement()
	{
		return sources[0];
	}

	@Override
	public int getMinimumSize()
	{
		return minimumSize;
	}

	@Override
	public int getMaximumSize()
	{
		return sources.length;
	}

	@Override
	public boolean isInitial()
	{
		return initial;
	}

	@Override
	public boolean isFinal()
	{
		return isFinal;
	}

	/**
	 * Does always return {@code true}.
	 * A {@code LimitedListField} may store an empty list,
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
		return ReflectionTypes.parameterized(Collection.class, sources[0].getValueClass());
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = sources[0].getInitialExceptions();
		for(int i = 1; i<sources.length; i++)
			result.addAll(sources[i].getInitialExceptions());
		return result;
	}

	@Wrap(order=10, doc=Wrap.GET_DOC)
	@Override
	@Nonnull
	public List<E> get(@Nonnull final Item item)
	{
		final int length = this.length.getMandatory(item);
		final ArrayList<E> result = new ArrayList<>(length);

		for(int i = 0; i<length; i++)
		{
			result.add(sources[i].get(item));
		}
		return Collections.unmodifiableList(result);
	}

	private void assertValue(final Collection<?> value, final Item exceptionItem)
	{
		if(value==null)
			throw MandatoryViolationException.create(this, exceptionItem);
		final int valueSize = value.size();
		if(valueSize<minimumSize)
			throw new ListSizeViolationException(this, exceptionItem, true,  valueSize, minimumSize);
		if(valueSize>sources.length)
			throw new ListSizeViolationException(this, exceptionItem, false, valueSize, sources.length);
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			thrownGetter=LimitedListThrown.class)
	@Override
	public void set(@Nonnull final Item item, @Nonnull final Collection<? extends E> value)
	{
		FinalViolationException.check(this, item);

		assertValue(value, item);
		int i = 0;
		final SetValue<?>[] setValues = new SetValue<?>[sources.length+1];

		//noinspection ForLoopThatDoesntUseLoopVariable
		for(final Iterator<? extends E> it = value.iterator(); it.hasNext(); i++)
			setValues[i] = SetValue.map(sources[i], it.next());

		final int length = i;

		for(; i<sources.length; i++)
			setValues[i] = SetValue.map(sources[i], null);

		setValues[i] = SetValue.map(this.length, length);

		item.set(setValues);
	}

	@Override
	public SetValue<?>[] execute(final Collection<E> value, final Item exceptionItem)
	{
		assertValue(value, exceptionItem);
		int i = 0;
		final SetValue<?>[] result = new SetValue<?>[sources.length+1];

		for(final Object v : value)
			result[i] = SetValue.mapCasted(sources[i++], v);

		final int length = i;

		for(; i<sources.length; i++)
			result[i] = SetValue.map(sources[i], null);

		result[i] = SetValue.map(this.length, length);

		return result;
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition equal(final Join join, final Collection<E> value)
	{
		return bind(equal(value), join);
	}

	/**
	 * @deprecated Use {@link #is(Collection)} instead.
	 */
	@Deprecated
	public Condition equal(final Collection<E> value)
	{
		return is(value);
	}

	public Condition is(final Collection<E> value)
	{
		int i = 0;
		final Condition[] conditions = new Condition[sources.length];

		//noinspection ForLoopThatDoesntUseLoopVariable
		for(final Iterator<E> it = value.iterator(); it.hasNext(); i++)
			conditions[i] = sources[i].is(it.next());

		for(; i<sources.length; i++)
			conditions[i] = sources[i].is((E) null);

		return Cope.and(conditions);
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition notEqual(final Join join, final Collection<E> value)
	{
		return bind(notEqual(value), join);
	}

	/**
	 * @deprecated Use {@link #isNot(Collection)} instead.
	 */
	@Deprecated
	public Condition notEqual(final Collection<E> value)
	{
		return isNot(value);
	}

	public Condition isNot(final Collection<E> value)
	{
		int i = 0;
		final Condition[] conditions = new Condition[sources.length];

		for(final E v : value)
		{
			conditions[i] = sources[i].isNot(v).or(sources[i].isNull());
			i++;
		}

		for(; i<sources.length; i++)
			conditions[i] = sources[i].isNotNull();

		return Cope.or(conditions);
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition contains(final Join join, final E value)
	{
		return bind(contains(value), join);
	}

	public Condition contains(final E value)
	{
		final Condition[] conditions = new Condition[sources.length];

		final Function<Integer> l = value==null ? length : null;
		for(int i = 0; i<sources.length; i++)
		{
			conditions[i] = sources[i].is(value);
			if(value==null)
				conditions[i] = conditions[i].and(l.greater(i));
		}

		return Cope.or(conditions);
	}

	public Condition contains(@Nonnull final Function<? extends E> value)
	{
		return contains(null, value);
	}

	public Condition contains(final Join join, @Nonnull final Function<? extends E> value)
	{
		final Condition[] conditions = new Condition[sources.length];

		for(int i = 0; i<sources.length; i++)
			conditions[i] = bind(sources[i], join).is(requireNonNull(value, "value"));

		return Cope.or(conditions);
	}

	private static <E> Function<E> bind(final FunctionField<E> source, final Join join)
	{
		return join == null ? source : source.bind(join);
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition containsAny(final Join join, final Collection<E> set)
	{
		return bind(containsAny(set), join);
	}

	public Condition containsAny(final Collection<E> set)
	{
		final Condition[] conditions = new Condition[set.size()];
		int i = 0;
		for(final E item : set)
			conditions[i++] = contains(item);

		return Cope.or(conditions);
	}

	/**
	 * @deprecated Use {@link #lengthIs(int)} instead.
	 */
	@Deprecated
	public Condition lengthEqual(final int value)
	{
		return lengthIs(value);
	}

	public Condition lengthIs(final int value)
	{
		return length.is(value);
	}

	/**
	 * @deprecated Use {@link #lengthIsNot(int)} instead.
	 */
	@Deprecated
	public Condition lengthNotEqual(final int value)
	{
		return lengthIsNot(value);
	}

	public Condition lengthIsNot(final int value)
	{
		return length.isNot(value);
	}

	public Condition lengthLess(final int value)
	{
		return length.less(value);
	}

	public Condition lengthLessOrEqual(final int value)
	{
		return length.lessOrEqual(value);
	}

	public Condition lengthGreater(final int value)
	{
		return length.greater(value);
	}

	public Condition lengthGreaterOrEqual(final int value)
	{
		return length.greaterOrEqual(value);
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition lengthEqual(final Join join, final int value)
	{
		return bind(lengthEqual(value), join);
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition lengthNotEqual(final Join join, final int value)
	{
		return bind(lengthNotEqual(value), join);
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition lengthLess(final Join join, final int value)
	{
		return bind(lengthLess(value), join);
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition lengthLessOrEqual(final Join join, final int value)
	{
		return bind(lengthLessOrEqual(value), join);
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition lengthGreater(final Join join, final int value)
	{
		return bind(lengthGreater(value), join);
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition lengthGreaterOrEqual(final Join join, final int value)
	{
		return bind(lengthGreaterOrEqual(value), join);
	}

	private static Condition bind(final Condition condition, final Join join)
	{
		return join==null ? condition : condition.bind(join);
	}
}
