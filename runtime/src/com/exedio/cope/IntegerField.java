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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;
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
	private static final long serialVersionUID = 1l;

	final Sequence defaultToNextSequence;
	private final int minimum;
	private final int maximum;

	private IntegerField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final DefaultSource<Integer> defaultSource,
			final int minimum,
			final int maximum)
	{
		super(isfinal, optional, Integer.class, unique, copyFrom, defaultSource);
		this.minimum = minimum;
		this.maximum = maximum;

		if(minimum>=maximum)
			throw new IllegalArgumentException("maximum must be greater than minimum, but was " + maximum + " and " + minimum);

		mountDefaultSource();
		this.defaultToNextSequence =
				(this.defaultSource instanceof DefaultNext)
				? ((DefaultNext)this.defaultSource).getSequence()
				: null;
	}

	private static final class DefaultNext extends DefaultSource<Integer>
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
		@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR") // TODO think of a better design
		Integer generate(final long now)
		{
			return sequence.next();
		}

		@Override
		DefaultSource<Integer> forNewField()
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
						"The start value for defaultToNext of the field " +
						"does not comply to one of it's own constraints, " +
						"caused a " + e.getClass().getSimpleName() +
						": " + e.getMessageWithoutFeature() +
						" Start value was '" + start + "'.");
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

		private final class AnnotationProxy implements AnnotatedElement
		{
			private final AnnotatedElement source;

			AnnotationProxy(final AnnotatedElement source)
			{
				this.source = source;
			}

			@Override
			public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
			{
				if(CopeSchemaName.class==annotationClass)
				{
					return getAnnotation(annotationClass)!=null;
				}

				if(source==null)
					return false;

				return source.isAnnotationPresent(annotationClass);
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

				if(source==null)
					return null;

				return source.getAnnotation(annotationClass);
			}

			@Override
			public Annotation[] getAnnotations()
			{
				throw new RuntimeException(DefaultNext.this.toString());
			}

			@Override
			public Annotation[] getDeclaredAnnotations()
			{
				throw new RuntimeException(DefaultNext.this.toString());
			}

			@Override
			public String toString()
			{
				return DefaultNext.this.toString() + "-sourceFeatureAnnotations";
			}
		}
	}

	/**
	 * Creates a new mandatory <tt>IntegerField</tt>.
	 */
	public IntegerField()
	{
		this(false, false, false, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public IntegerField copy()
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public IntegerField toFinal()
	{
		return new IntegerField(true, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public IntegerField optional()
	{
		return new IntegerField(isfinal, true, unique, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public IntegerField unique()
	{
		return new IntegerField(isfinal, optional, true, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public IntegerField nonUnique()
	{
		return new IntegerField(isfinal, optional, false, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public IntegerField copyFrom(final ItemField<?> copyFrom)
	{
		return new IntegerField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultSource, minimum, maximum);
	}

	@Override
	public IntegerField noDefault()
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, null, minimum, maximum);
	}

	@Override
	public IntegerField defaultTo(final Integer defaultConstant)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultConstant(defaultConstant), minimum, maximum);
	}

	public IntegerField defaultToNext(final int start)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, new DefaultNext(start), minimum, maximum);
	}

	public IntegerField range(final int minimum, final int maximum)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	public IntegerField min(final int minimum)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	public IntegerField max(final int maximum)
	{
		return new IntegerField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	public boolean isDefaultNext()
	{
		return defaultSource instanceof DefaultNext;
	}

	public Integer getDefaultNextStart()
	{
		return (defaultSource instanceof DefaultNext) ? ((DefaultNext)defaultSource).start : null;
	}

	public Sequence getDefaultNext()
	{
		return (defaultSource instanceof DefaultNext) ? ((DefaultNext)defaultSource).getSequence() : null;
	}

	public int getMinimum()
	{
		return minimum;
	}

	public int getMaximum()
	{
		return maximum;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		if(minimum!=Integer.MIN_VALUE || maximum!=Integer.MAX_VALUE)
			result.add(IntegerRangeViolationException.class);
		return result;
	}

	@Override
	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, name, false, optional, minimum, maximum, false);
	}

	@Override
	Integer get(final Row row)
	{
		return (Integer)row.get(getColumn());
	}

	@Override
	void set(final Row row, final Integer surface)
	{
		row.put(getColumn(), surface);
	}

	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	@Wrap(order=10, name="get{0}", doc="Returns the value of {0}.", hide=OptionalGetter.class)
	public int getMandatory(@Nonnull final Item item)
	{
		return getMandatoryObject(item).intValue();
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			hide={FinalSettableGetter.class, OptionalGetter.class},
			thrownGetter=InitialThrown.class)
	public void set(@Nonnull final Item item, final int value)
	{
		set(item, Integer.valueOf(value));
	}

	/**
	 * Finds an item by it's unique fields.
	 * @return null if there is no matching item.
	 * @see FunctionField#searchUnique(Class, Object)
	 */
	@Wrap(order=100, name="for{0}",
			doc="Finds a {2} by it''s {0}.",
			docReturn="null if there is no matching item.",
			hide={OptionalGetter.class, NonUniqueGetter.class})
	@Nullable
	public <P extends Item> P searchUnique(
			final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final int value)
	{
		return searchUnique(typeClass, Integer.valueOf(value));
	}

	@Override
	void checkNotNull(final Integer value, final Item exceptionItem)
	{
		final int valuePrimitive = value.intValue();
		if(valuePrimitive<minimum)
			throw new IntegerRangeViolationException(this, exceptionItem, value, true, minimum);
		if(valuePrimitive>maximum)
			throw new IntegerRangeViolationException(this, exceptionItem, value, false, maximum);
	}

	public SequenceInfo getDefaultToNextInfo()
	{
		return defaultToNextSequence!=null ? defaultToNextSequence.getInfo() : null;
	}

	/**
	 * @throws IllegalStateException is a transaction is bound to the current thread
	 * @deprecated Use {@link #checkSequenceBehindDefaultToNext()}.{@link SequenceBehindInfo#isBehindBy() isBehindBy}() instead
	 */
	@Deprecated
	public int checkDefaultToNext()
	{
		return SequenceBehindInfo.isBehindBy(checkSequenceBehindDefaultToNext());
	}

	/**
	 * @throws IllegalStateException is a transaction is bound to the current thread
	 */
	public SequenceBehindInfo checkSequenceBehindDefaultToNext()
	{
		if(defaultToNextSequence==null)
			return null;

		return defaultToNextSequence.sequenceX.check(
				getType().getModel(), (IntegerColumn)getColumn());
	}

	String getDefaultToNextSequenceName()
	{
		if(defaultToNextSequence==null)
			throw new IllegalArgumentException("is not defaultToNext: " + this);

		return defaultToNextSequence.sequenceX.getSchemaName();
	}

	public IntegerField rangeDigits(final int digits)
	{
		return IntegerFieldRangeDigits.rangeDigits(this, digits);
	}

	public IntegerField rangeDigits(final int minimumDigits, final int maximumDigits)
	{
		return IntegerFieldRangeDigits.rangeDigits(this, minimumDigits, maximumDigits);
	}


	@Override
	public Condition equal(final Integer value)
	{
		if(value!=null)
		{
			final int valuePrimitive = value.intValue();
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.FALSE;
			else
				return super.equal(value);
		}
		else
			return isNull();
	}

	@Override
	public Condition notEqual(final Integer value)
	{
		if(value!=null)
		{
			final int valuePrimitive = value.intValue();
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.TRUE;
			else
				return super.notEqual(value);
		}
		else
			return isNotNull();
	}
	// TODO the same for less, lessEqual, greater, greaterEqual
}
