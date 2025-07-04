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

import com.exedio.cope.util.CharSet;
import java.io.Serial;

public interface StringFunction extends Function<String>
{
	// convenience methods for conditions and views ---------------------------------
	@Override
	StringFunction bind(Join join); // narrows return type for implementations

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is matches the given parameter.
	 */
	@SuppressWarnings("deprecation")
	default LikeCondition like(final String value)
	{
		return new LikeCondition(this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * starts with the given parameter.
	 */
	default Condition startsWith(final String value)
	{
		return LikeCondition.startsWith(this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * ends with the given parameter.
	 */
	default Condition endsWith(final String value)
	{
		return LikeCondition.endsWith(this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * contains the given parameter.
	 */
	default Condition contains(final String value)
	{
		return LikeCondition.contains(this, value);
	}

	default LengthView length()
	{
		return new LengthView(this);
	}

	@SuppressWarnings("deprecation") // OK: moved api
	default UppercaseView toUpperCase()
	{
		return new UppercaseView(this);
	}

	default CaseView toLowerCase()
	{
		return new CaseView(this, false);
	}

	/**
	 * To be deprecated, use {@link #isIgnoreCase(String)} instead.
	 */
	@SuppressWarnings("unused") // OK: no longer to be used
	default Condition equalIgnoreCase(final String value)
	{
		return isIgnoreCase(value);
	}

	default Condition isIgnoreCase(final String value)
	{
		return CaseView.isIgnoreCase(this, value);
	}

	/**
	 * To be deprecated, use {@link #isIgnoreCase(Function)} instead.
	 */
	@SuppressWarnings("unused") // OK: no longer to be used
	default Condition equalIgnoreCase(final Function<String> right)
	{
		return isIgnoreCase(right);
	}

	default Condition isIgnoreCase(final Function<String> right)
	{
		return CaseView.isIgnoreCase(this, right);
	}

	default LikeCondition likeIgnoreCase(final String value)
	{
		return CaseView.likeIgnoreCase(this, value);
	}

	default Condition startsWithIgnoreCase(final String value)
	{
		return CaseView.startsWithIgnoreCase(this, value);
	}

	default Condition endsWithIgnoreCase(final String value)
	{
		return CaseView.endsWithIgnoreCase(this, value);
	}

	default Condition containsIgnoreCase(final String value)
	{
		return CaseView.containsIgnoreCase(this, value);
	}

	// TODO
	// StringField could override this method and return FALSE, if its own
	// Charset constraint is disjunctive to the CharSet given to this method.
	@SuppressWarnings("deprecation")
	default Condition conformsTo(final CharSet charSet)
	{
		return new CharSetCondition(this, charSet);
	}

	default RegexpLikeCondition regexpLike(final String pattern)
	{
		return new RegexpLikeCondition(this, pattern);
	}

	@Serial
	long serialVersionUID = 6196781661929849730L;
}
