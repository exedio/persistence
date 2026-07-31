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

package com.exedio.cope;

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.CopeSchemaNameElement;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import java.io.Serial;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a field within a {@link Type type},
 * that enables instances of that type to store a integer.
 *
 * @author Ralf Wiebicke
 */
public final class IntegerField extends NumberField<Integer>
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final int minimum;
	private final int maximum;
	private final boolean forbidZero;

	private IntegerField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final CopyFrom[] copyFrom,
			final DefaultSupplier<Integer> defaultS,
			final int minimum,
			final boolean forbidZero,
			final int maximum)
	{
		super(isfinal, optional, Integer.class, unique, copyFrom, defaultS);
		this.minimum = minimum;
		this.maximum = maximum;
		this.forbidZero = forbidZero;

		if(minimum>maximum)
			throw new IllegalArgumentException("maximum must be at least minimum, but was " + maximum + " and " + minimum);
		if(forbidZero)
		{
			if(minimum>=0)
				throw new IllegalArgumentException("forbidZero is outside of range with minimum " + minimum);
			if(maximum<=0)
				throw new IllegalArgumentException("forbidZero is outside of range with maximum " + maximum);
		}

		mountDefault();
	}

	private static final class DefaultNext extends DefaultSupplier<Integer>
	{
		final int start;
		private Sequence sequence;

		DefaultNext(final int start)
		{
			this.start = start;
		}

		Sequence getSequence()
		{
			assert sequence!=null;
			return sequence;
		}

		@Override
		Integer generate(final Context ctx)
		{
			return sequence.next();
		}

		@Override
		DefaultSupplier<Integer> forNewField()
		{
			return new DefaultNext(start);
		}

		@Override
		void mount(final FunctionField<Integer> field)
		{
			final IntegerField f = (IntegerField)field;
			try
			{
				field.check(start, null);
			}
			catch(final ConstraintViolationException e)
			{
				// BEWARE
				// Must not make exception available to public,
				// since it contains a reference to this function field,
				// which has not been constructed successfully.
				throw new IllegalArgumentException(
						"The start value " + start + " for defaultToNext of the field " +
						"does not comply to one of it's own constraints, " +
						"caused a " + e.getClass().getSimpleName() +
						": " + e.getMessageWithoutFeature());
			}
			assert sequence==null;
			sequence = new Sequence(f, start, f.getMinimum(), f.getMaximum());
		}

		@Override
		void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
		{
			getSequence().mount(type, sequenceName(name), new AnnotationProxy(annotationSource));
		}

		static String sequenceName(final String name)
		{
			return name + "-Seq";
		}

		private record AnnotationProxy(AnnotatedElement source) implements AnnotatedElement
		{
			@Override
			public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
			{
				if(CopeSchemaName.class==annotationClass)
				{
					return getAnnotation(annotationClass)!=null;
				}

				// do not propagate arbitrary annotations, consistently to UniqueConstraint and CopyConstraint
				return false;
			}

			@Override
			public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
			{
				if(CopeSchemaName.class==annotationClass)
				{
					final CopeSchemaName sourceName = source!=null ? source.getAnnotation(CopeSchemaName.class) : null;

					if(sourceName==null)
						return null;

					return annotationClass.cast(CopeSchemaNameElement.get(sequenceName(sourceName.value())));
				}

				// do not propagate arbitrary annotations, consistently to UniqueConstraint and CopyConstraint
				return null;
			}

			@Override
			public Annotation[] getAnnotations()
			{
				throw new RuntimeException();
			}

			@Override
			public Annotation[] getDeclaredAnnotations()
			{
				throw new RuntimeException();
			}
		}
	}

	/**
	 * Creates a new mandatory {@code IntegerField}.
	 */
	public IntegerField()
	{
		this(false, false, false, null, null, Integer.MIN_VALUE, false, Integer.MAX_VALUE);
	}

	@Override
	public IntegerField copy()
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultS, minimum, forbidZero, maximum);
	}

	@Override
	public IntegerField toFinal()
	{
		return new IntegerField(true, optional, unique, copyFrom, defaultS, minimum, forbidZero, maximum);
	}

	@Override
	public IntegerField optional()
	{
		return new IntegerField(isfinal, true, unique, copyFrom, defaultS, minimum, forbidZero, maximum);
	}

	@Override
	public IntegerField mandatory()
	{
		return new IntegerField(isfinal, false, unique, copyFrom, defaultS, minimum, forbidZero, maximum);
	}

	@Override
	public IntegerField unique()
	{
		return new IntegerField(isfinal, optional, true, copyFrom, defaultS, minimum, forbidZero, maximum);
	}

	@Override
	public IntegerField nonUnique()
	{
		return new IntegerField(isfinal, optional, false, copyFrom, defaultS, minimum, forbidZero, maximum);
	}

	@Override
	public IntegerField copyFrom(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.RESOLVE_TEMPLATE));
	}

	@Override
	public IntegerField copyFrom(final ItemField<?> target, final Supplier<? extends FunctionField<Integer>> template)
	{
		return copyFrom(new CopyFrom(target, template));
	}

	@Override
	public IntegerField copyFromSelf(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.SELF_TEMPLATE));
	}

	private IntegerField copyFrom(final CopyFrom copyFrom)
	{
		return new IntegerField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultS, minimum, forbidZero, maximum);
	}

	@Override
	public IntegerField noCopyFrom()
	{
		return new IntegerField(isfinal, optional, unique, null, defaultS, minimum, forbidZero, maximum);
	}

	@Override
	public IntegerField noDefault()
	{
		return defaultTo(null);
	}

	@Override
	public IntegerField defaultTo(final Integer defaultConstant)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultConstant(defaultConstant), minimum, forbidZero, maximum);
	}

	public IntegerField defaultToNext(final int start)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, new DefaultNext(start), minimum, forbidZero, maximum);
	}

	public IntegerField rangeEvenIfRedundant(final int minimum, final int maximum)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultS, minimum, forbidZero, maximum);
	}

	public IntegerField range(final int minimum, final int maximum)
	{
		return rangeEvenIfRedundant(minimum, maximum).failOnRedundantRange();
	}

	public IntegerField min(final int minimum)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultS, minimum, forbidZero, maximum).failOnRedundantRange();
	}

	public IntegerField max(final int maximum)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultS, minimum, forbidZero, maximum).failOnRedundantRange();
	}

	private IntegerField failOnRedundantRange()
	{
		if(minimum==maximum)
			throw new IllegalArgumentException(
					"Redundant field with minimum==maximum " +
					"(" + minimum + ") is probably a mistake. " +
					"You may call method rangeEvenIfRedundant if you are sure this is ok.");
		return this;
	}

	public IntegerField forbidZero()
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultS, minimum, true, maximum);
	}

	/**
	 * Returns whether this field defaults to next value of a sequence.
	 */
	public boolean isDefaultNext()
	{
		return defaultS instanceof DefaultNext;
	}

	/**
	 * @throws IllegalArgumentException if this field does not {@link #isDefaultNext() default to next value of a sequence}.
	 */
	public int getDefaultNextStartX()
	{
		return defaultNext().start;
	}

	/**
	 * @throws IllegalArgumentException if this field does not {@link #isDefaultNext() default to next value of a sequence}.
	 */
	public Sequence getDefaultNextSequence()
	{
		return defaultNext().getSequence();
	}

	private DefaultNext defaultNext()
	{
		if(!(defaultS instanceof DefaultNext))
			throw new IllegalArgumentException("is not defaultToNext: " + this);

		return (DefaultNext)defaultS;
	}

	public int getMinimum()
	{
		return minimum;
	}

	public int getMaximum()
	{
		return maximum;
	}

	/**
	 * @see #getMinimum()
	 */
	@Override
	public Integer getMinimumNumber()
	{
		return minimum;
	}

	/**
	 * @see #getMaximum()
	 */
	@Override
	public Integer getMaximumNumber()
	{
		return maximum;
	}

	public boolean isZeroForbidden()
	{
		return forbidZero;
	}

	@Override
	public boolean allowsZeroValue()
	{
		return (minimum<=0 && 0<=maximum) && !forbidZero;
	}

	@Override
	public boolean allowsNegativeValues()
	{
		return minimum<0;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		if(minimum!=Integer.MIN_VALUE || maximum!=Integer.MAX_VALUE)
			result.add(IntegerRangeViolationException.class);
		if(forbidZero)
			result.add(IntegerForbidViolationException.class);
		return result;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
	{
		return new IntegerColumn(table, name, false, optional, minimum, forbidZero, maximum, false);
	}

	@Override
	Integer get(final Row row)
	{
		return (Integer)row.get(column());
	}

	@Override
	void set(final Row row, final Integer surface)
	{
		row.put(column(), surface);
	}

	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	@Wrap(order=10, name="get{0}", doc=Wrap.GET_DOC, hide=OptionalGetter.class)
	public int getMandatory(@Nonnull final Item item)
	{
		return getMandatoryObject(item);
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			hide={FinalSettableGetter.class, RedundantByCopyConstraintGetter.class, OptionalGetter.class},
			thrownGetter=InitialThrown.class)
	public void set(@Nonnull final Item item, final int value)
	{
		set(item, Integer.valueOf(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 * @see FunctionField#searchUnique(Class, Object)
	 */
	@Wrap(order=100, name=Wrap.FOR_NAME,
			doc=Wrap.FOR_DOC,
			docReturn=Wrap.FOR_RETURN,
			hide={OptionalGetter.class, NonUniqueGetter.class})
	@Nullable
	public <P extends Item> P searchUnique(
			final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM) final int value)
	{
		return searchUnique(typeClass, Integer.valueOf(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws NullPointerException if value is null.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	@Wrap(order=110, name=Wrap.FOR_STRICT_NAME,
			doc=Wrap.FOR_DOC,
			hide={OptionalGetter.class, NonUniqueGetter.class},
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc="if there is no matching item."))
	@Nonnull
	public <P extends Item> P searchUniqueStrict(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final int value)
			throws IllegalArgumentException
	{
		return searchUniqueStrict(typeClass, Integer.valueOf(value));
	}

	@Override
	void checkNotNull(final Integer value, final Item exceptionItem)
	{
		final int valuePrimitive = value;
		if(valuePrimitive<minimum)
			throw new IntegerRangeViolationException(this, exceptionItem, value, true, minimum);
		if(valuePrimitive>maximum)
			throw new IntegerRangeViolationException(this, exceptionItem, value, false, maximum);
		if(forbidZero && valuePrimitive==0)
			throw new IntegerForbidViolationException(this, exceptionItem, value);
	}

	/**
	 * @throws IllegalArgumentException if this field does not {@link #isDefaultNext() default to next value of a sequence}.
	 */
	public SequenceInfo getDefaultToNextInfoX()
	{
		return getDefaultNextSequence().getInfo();
	}

	/**
	 * @throws IllegalArgumentException if this field does not {@link #isDefaultNext() default to next value of a sequence}.
	 */
	public SequenceBehindInfo checkSequenceBehindDefaultToNextX()
	{
		return getDefaultNextSequence().sequenceX.check(
				getType().getModel(), (IntegerColumn) column());
	}

	public IntegerField rangeDigits(final int digits)
	{
		return IntegerFieldRangeDigits.rangeDigits(this, digits);
	}

	public IntegerField rangeDigits(final int minimumDigits, final int maximumDigits)
	{
		return IntegerFieldRangeDigits.rangeDigits(this, minimumDigits, maximumDigits);
	}


	/**
	 * @deprecated Use {@link #is(Integer)} instead. You may use an "Inline Method" - functionality of your IDE to update your code.
	 * Needed for binary compatibility.
	 */
	@Deprecated @SuppressWarnings("deprecation") // OK: overriding deprecated method
	@Override
	public Condition equal(final Integer value)
	{
		return is(value);
	}

	@Override
	public Condition is(final Integer value)
	{
		if(value!=null)
		{
			// TODO does not work with BindFunction
			final int valuePrimitive = value;
			if(valuePrimitive<minimum || valuePrimitive>maximum) // TODO forbidZero
				return Condition.ofFalse(); // TODO COND bug: ofFalse.not() with null
			else
				return super.is(value);
		}
		else
			return isNull();
	}

	/**
	 * @deprecated Use {@link #isNot(Integer)} instead. You may use an "Inline Method" - functionality of your IDE to update your code.
	 * Needed for binary compatibility.
	 */
	@Deprecated @SuppressWarnings("deprecation") // OK: overriding deprecated method
	@Override
	public Condition notEqual(final Integer value)
	{
		return isNot(value);
	}

	@Override
	public Condition isNot(final Integer value)
	{
		if(value!=null)
		{
			// TODO does not work with BindFunction
			final int valuePrimitive = value;
			if(valuePrimitive<minimum || valuePrimitive>maximum) // TODO forbidZero
				return Condition.ofTrue(); // TODO COND bug: ofTrue.not() with null
			else
				return super.isNot(value);
		}
		else
			return isNotNull();
	}
	// TODO the same for less, lessEqual, greater, greaterEqual

	@Override
	public Condition isZero()
	{
		return is(0);
	}

	@Override
	public Condition lessZero()
	{
		return less(0);
	}
}
