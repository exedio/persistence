package com.exedio.cope;

import com.exedio.cope.function.LengthView;
import com.exedio.cope.function.UppercaseView;

public class JoinedStringFunction extends JoinedFunction<String> implements StringFunction
{

	final StringFunction stringFunction;
	
	/**
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see IntegerFunction#bindInt(Join)
	 */
	public JoinedStringFunction(final StringFunction function, final Join join)
	{
		super(function, join);
		this.stringFunction = function;
	}
	
	/**
	 * Return this.
	 * It makes no sense wrapping a JoinedFunction into another JoinedFunction,
	 * because the inner JoinedFunction &quot;wins&quot;.
	 * @see JoinedFunction#bind(Join)
	 * @see JoinedIntegerFunction#bindInt(Join)
	 * @see JoinedItemFunction#bindItem(Join)
	 */
	public JoinedStringFunction bindString(final Join join)
	{
		return new JoinedStringFunction(stringFunction, join); // using "integerFunction" instead of "this" is a small short-cut
	}
	
	public final LikeCondition like(final String value)
	{
		return new LikeCondition(this, value);
	}
	
	public final LikeCondition startsWith(final String value)
	{
		return LikeCondition.startsWith(this, value);
	}
	
	public final LikeCondition endsWith(final String value)
	{
		return LikeCondition.endsWith(this, value);
	}
	
	public final LikeCondition contains(final String value)
	{
		return LikeCondition.contains(this, value);
	}
	
	public final LengthView length()
	{
		return new LengthView(this);
	}
	
	public final UppercaseView toUpperCase()
	{
		return new UppercaseView(this);
	}
	
	public final EqualCondition equalIgnoreCase(final String value)
	{
		return toUpperCase().equal(value.toUpperCase());
	}
	
	public final LikeCondition likeIgnoreCase(final String value)
	{
		return toUpperCase().like(value.toUpperCase());
	}
	
	public final LikeCondition startsWithIgnoreCase(final String value)
	{
		return LikeCondition.startsWith(toUpperCase(), value.toUpperCase());
	}
	
	public final LikeCondition endsWithIgnoreCase(final String value)
	{
		return LikeCondition.endsWith(toUpperCase(), value.toUpperCase());
	}
	
	public final LikeCondition containsIgnoreCase(final String value)
	{
		return LikeCondition.contains(toUpperCase(), value.toUpperCase());
	}
}
