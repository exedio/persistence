package com.exedio.cope;

import java.util.Arrays;

final class UpdateCount
{
	private static final int[] INITIAL_PLACEHOLDER = new int[0];

	private static final int UNDEFINED = -2000;

	static UpdateCount initial(final Type<?> type)
	{
		return new UpdateCount(type, INITIAL_PLACEHOLDER);
	}

	static Builder forStoredValue(final Type<?> type)
	{
		return new Builder(type);
	}

	private final Type<?> type;
	private final int[] values;

	private UpdateCount(final Type<?> type, final int[] values)
	{
		if (getClass().desiredAssertionStatus())
		{
			//noinspection ArrayEquality
			if (values!=INITIAL_PLACEHOLDER)
			{
				assert values.length == type.inheritanceDepth+1;
				for(Type<?> t = type; t != null; t = t.getSupertype())
				{
					//noinspection RedundantIfStatement
					if(t.getTable().updateCounter != null)
						assert values[t.inheritanceDepth] >= 0;
				}
			}
		}
		this.type = type;
		this.values = values;
	}

	Modifier modifier()
	{
		return new Modifier();
	}

	private void check(final Type<?> t)
	{
		if (!t.isAssignableFrom(type))
			throw new IllegalArgumentException("unexpected request for "+t+" in UpdateCount for "+type);
		if (t.getTable().updateCounter==null)
			throw new IllegalArgumentException("unexpected request for unmodifiable "+t);
	}

	int getValue(final Type<?> type)
	{
		check(type);
		if (this.isInitial())
			throw new RuntimeException("initial");
		return values[type.inheritanceDepth];
	}

	boolean isInitial()
	{
		//noinspection ArrayEquality
		return values==INITIAL_PLACEHOLDER;
	}

	@Override
	public String toString()
	{
		return "UpdateCount(" + (isInitial()?"initial":values) + ")";
	}

	final class Modifier
	{
		private static final int[] NOT_YET_TOUCHED = new int[0];

		private int[] nextValues = NOT_YET_TOUCHED;

		private static final int UNCHANGED = -1000;

		private Modifier()
		{
		}

		private boolean isNotYetTouched()
		{
			//noinspection ArrayEquality
			return nextValues==NOT_YET_TOUCHED;
		}

		UpdateCount nextUpdateCount()
		{
			if (nextValues==null)
				throw new RuntimeException("already used");
			if (isNotYetTouched())
				return UpdateCount.this;
			else
			{
				for (int i=0; i<nextValues.length; i++)
				{
					if (nextValues[i]==UNCHANGED)
						nextValues[i] = isInitial() ? 0 : values[i];
				}
				final UpdateCount result = new UpdateCount(type, nextValues);
				nextValues = null;
				return result;
			}
		}

		int nextValue(final Type<?> type)
		{
			assert type.isAssignableFrom(UpdateCount.this.type);
			if (nextValues==null)
				throw new RuntimeException("already used");
			if (isNotYetTouched())
			{
				nextValues = new int[UpdateCount.this.type.inheritanceDepth+1];
				Arrays.fill(nextValues, UNCHANGED);
			}
			if (nextValues[type.inheritanceDepth]!=UNCHANGED)
				return nextValues[type.inheritanceDepth];
			final int result;
			if (isInitial())
			{
				result = 0;
			}
			else
			{
				final int currentValue = values[type.inheritanceDepth];
				result = currentValue==Integer.MAX_VALUE ? 0 : currentValue + 1;
			}
			nextValues[type.inheritanceDepth] = result;
			return result;
		}
	}

	static final class Builder
	{
		private final Type<?> type;

		private final int[] values;

		private Builder(final Type<?> type)
		{
			this.type = type;
			values = new int[type.inheritanceDepth+1];
			Arrays.fill(values, UNDEFINED);
		}

		/**
		 * @return this
		 */
		Builder set(final Type<?> t, final int value)
		{
			assert value>=0 : value;
			if (!t.isAssignableFrom(type))
			{
				throw new IllegalArgumentException("unexpected request for "+t+" in UpdateCount.Builder for "+type);
			}
			if (t.getTable().updateCounter==null)
			{
				throw new IllegalArgumentException("unexpected call for unmodifiable "+t);
			}
			assert values[t.inheritanceDepth]==UNDEFINED;
			values[t.inheritanceDepth] = value;
			return this;
		}

		UpdateCount build()
		{
			if (getClass().desiredAssertionStatus())
			{
				for(Type<?> t = type; t != null; t = t.getSupertype())
				{
					if(t.getTable().updateCounter == null)
						assert values[t.inheritanceDepth] == UNDEFINED;
					else
						assert values[t.inheritanceDepth] >= 0;
				}
			}
			return new UpdateCount(type, values);
		}
	}
}
