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


public interface StringFunction extends Function<String>
{
	// convenience methods for conditions and views ---------------------------------
	@Override
	default BindStringFunction bind(final Join join)
	{
		return new BindStringFunction(this, join);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is matches the given parameter.
	 */
	default LikeCondition like(final String value)
	{
		return new LikeCondition(this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * starts with the given parameter.
	 */
	default LikeCondition startsWith(final String value)
	{
		return LikeCondition.startsWith(this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * ends with the given parameter.
	 */
	default LikeCondition endsWith(final String value)
	{
		return LikeCondition.endsWith(this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * contains the given parameter.
	 */
	default LikeCondition contains(final String value)
	{
		return LikeCondition.contains(this, value);
	}

	default LengthView length()
	{
		return new LengthView(this);
	}

	default UppercaseView toUpperCase()
	{
		return new UppercaseView(this);
	}

	default LowercaseView toLowerCase()
	{
		return new LowercaseView(this);
	}

	default Condition equalIgnoreCase(final String value)
	{
		return UppercaseView.equalIgnoreCase(this, value);
	}

	default LikeCondition likeIgnoreCase(final String value)
	{
		return UppercaseView.likeIgnoreCase(this, value);
	}

	default LikeCondition startsWithIgnoreCase(final String value)
	{
		return UppercaseView.startsWithIgnoreCase(this, value);
	}

	default LikeCondition endsWithIgnoreCase(final String value)
	{
		return UppercaseView.endsWithIgnoreCase(this, value);
	}

	default LikeCondition containsIgnoreCase(final String value)
	{
		return UppercaseView.containsIgnoreCase(this, value);
	}

	long serialVersionUID = 6196781661929849730L;
}
