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

package com.exedio.cope.revstat;

import static com.exedio.cope.revstat.RevisionStatistics.getExplicitTypes;
import static com.exedio.cope.revstat.RevisionStatistics.isContainedIn;
import static com.exedio.cope.revstat.RevisionStatisticsTest.MODEL;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class RevisionStatisticsModelTest
{
	@Test void containedIn()
	{
		assertEquals(true, isContainedIn(MODEL));
	}

	@Test void containedInOther()
	{
		assertEquals(false, isContainedIn(OTHER_MODEL));
	}

	@Test void explicitTypes()
	{
		assertEqualsUnmodifiable(
				asList(Revstat.TYPE, RevstatBody.TYPE),
				getExplicitTypes(MODEL));
	}

	@Test void explicitTypesOther()
	{
		try
		{
			getExplicitTypes(OTHER_MODEL);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("model does not contain RevisionStatistics", e.getMessage());
		}
	}


	@WrapperType(indent=2, comments=false)
	private static final class OtherItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private OtherItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@com.exedio.cope.instrument.Generated
		private OtherItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<OtherItem> TYPE = com.exedio.cope.TypesBound.newType(OtherItem.class);

		@com.exedio.cope.instrument.Generated
		private OtherItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model OTHER_MODEL = new Model(OtherItem.TYPE);
}
