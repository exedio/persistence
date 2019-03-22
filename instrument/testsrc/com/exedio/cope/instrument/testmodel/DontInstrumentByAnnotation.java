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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.CopeWarnings;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.testfeature.SimpleSettable;
import javax.annotation.Generated;

/**
 * This class tests that the {@link WrapperIgnore} annotation works.
 */
@WrapperIgnore
@SuppressWarnings("unused") // OK: just for testing instrumentor
class DontInstrumentByAnnotation extends Item
{
	private static final long serialVersionUID=1L;

	static final SimpleSettable makeInstrumentorFail = null;

	@Generated("com.exedio.cope.instrument")
	@SuppressWarnings(CopeWarnings.NON_GENERATED)
	int generatedVariable;

	@Generated("other.than.com.exedio.cope.instrument")
	void generatedMethod()
	{
		// empty
	}
}
