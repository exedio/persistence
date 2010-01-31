/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.Set;

import com.exedio.cope.info.SequenceInfo;

/**
 * Represents a field within a {@link Type type},
 * that enables instances of that type to store a integer.
 *
 * @author Ralf Wiebicke
 */
public final class IntegerField extends NumberField<Integer>
{
	private final Integer defaultNextStart;
	final Sequence defaultToNextSequence;
	private final int minimum;
	private final int maximum;

	private IntegerField(
			final boolean isfinal, final boolean optional, final boolean unique,
			final Integer defaultConstant, final Integer defaultNextStart,
			final int minimum, final int maximum)
	{
		super(isfinal, optional, unique, Integer.class, defaultConstant);
		this.defaultNextStart = defaultNextStart;
		this.minimum = minimum;
		this.maximum = maximum;

		if(defaultConstant!=null && defaultNextStart!=null)
			throw new IllegalStateException("cannot use defaultConstant and defaultNext together");
		if(minimum>=maximum)
			throw new IllegalArgumentException("maximum must be greater than mimimum, but was " + maximum + " and " + minimum + '.');

		checkDefaultConstant();
		if(defaultNextStart!=null)
		{
			try
			{
				check(defaultNextStart, null);
			}
			catch(ConstraintViolationException e)
			{
				// BEWARE
				// Must not make exception e available to public,
				// since it contains a reference to this function field,
				// which has not been constructed successfully.
				throw new IllegalArgumentException(
						"The start value for defaultToNext of the field " +
						"does not comply to one of it's own constraints, " +
						"caused a " + e.getClass().getSimpleName() +
						": " + e.getMessageWithoutFeature() +
						" Start value was '" + defaultNextStart + "'.");
			}
			this.defaultToNextSequence = new Sequence(this, defaultNextStart.intValue(), minimum, maximum);
		}
		else
		{
			this.defaultToNextSequence = null;
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
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	@Override
	public IntegerField toFinal()
	{
		return new IntegerField(true, optional, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	@Override
	public IntegerField optional()
	{
		return new IntegerField(isfinal, true, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}

	@Override
	public IntegerField unique()
	{
		return new IntegerField(isfinal, optional, true, defaultConstant, defaultNextStart, minimum, maximum);
	}

	@Override
	public IntegerField nonUnique()
	{
		return new IntegerField(isfinal, optional, false, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	public IntegerField defaultTo(final Integer defaultConstant)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	public IntegerField defaultToNext(final int start)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, start, minimum, maximum);
	}
	
	public IntegerField range(final int minimum, final int maximum)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	public IntegerField min(final int minimum)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, minimum, Integer.MAX_VALUE);
	}
	
	public IntegerField max(final int maximum)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, Integer.MIN_VALUE, maximum);
	}
	
	public boolean isDefaultNext()
	{
		return defaultNextStart!=null;
	}
	
	public Integer getDefaultNextStart()
	{
		return defaultNextStart;
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
	 * Returns true, if a value for the field should be specified
	 * on the creation of an item.
	 * This implementation returns
	 * <tt>({@link #isFinal() isFinal()} || {@link #isMandatory() isMandatory()}) && !{@link #isDefaultNext()}</tt>.
	 */
	@Override
	public boolean isInitial()
	{
		return (defaultNextStart==null) && super.isInitial();
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
	public Class getInitialType()
	{
		return optional ? Integer.class : int.class;
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		final IntegerColumn result = new IntegerColumn(table, this, name, optional, minimum, maximum, false);
		if(defaultToNextSequence!=null)
		{
			final Database database = table.database;
			defaultToNextSequence.connect(database, result);
			database.addSequence(defaultToNextSequence);
		}
		return result;
	}
	
	@Override
	void disconnect()
	{
		if(defaultToNextSequence!=null)
			defaultToNextSequence.disconnect();
		super.disconnect();
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
	public int getMandatory(final Item item)
	{
		if(optional)
			throw new IllegalArgumentException("field " + toString() + " is not mandatory");
		
		return get(item).intValue();
	}
	
	public void set(final Item item, final int value)
		throws
			UniqueViolationException,
			FinalViolationException,
			IntegerRangeViolationException
	{
		set(item, Integer.valueOf(value));
	}

	@Override
	void checkNotNull(final Integer value, final Item exceptionItem) throws IntegerRangeViolationException
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
	
	public int checkDefaultToNext()
	{
		return defaultToNextSequence!=null ? defaultToNextSequence.check(getType().getModel().getCurrentTransaction().getConnection()) : 0;
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
