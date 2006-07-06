/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.function.LengthView;
import com.exedio.cope.function.UppercaseView;

public interface StringFunction extends Function<String>
{
	// convenience methods for conditions and views ---------------------------------
	JoinedStringFunction bind(Join join);
	LikeCondition like(String value);
	LikeCondition startsWith(String value);
	LikeCondition endsWith(String value);
	LikeCondition contains(String value);
	LengthView length();
	UppercaseView toUpperCase();
	EqualCondition equalIgnoreCase(String value);
	LikeCondition likeIgnoreCase(String value);
	LikeCondition startsWithIgnoreCase(String value);
	LikeCondition endsWithIgnoreCase(String value);
	LikeCondition containsIgnoreCase(String value);
}
