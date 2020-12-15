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

package com.exedio.cope.pattern;

import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class DynamicModelModelTest
{
	@Test void testIt()
	{
		assertEquals(asList(
				"AnItem.explicitCapacities-string0",
				"AnItem.explicitCapacities-bool0",
				"AnItem.explicitCapacities-bool1",
				"AnItem.explicitCapacities-int0",
				"AnItem.explicitCapacities-int1",
				"AnItem.explicitCapacities-int2",
				"AnItem.explicitCapacities-double0",
				"AnItem.explicitCapacities-double1",
				"AnItem.explicitCapacities-double2",
				"AnItem.explicitCapacities-double3",
				"AnItem.explicitCapacities-enum0",
				"AnItem.explicitCapacities-enum1",
				"AnItem.explicitCapacities-enum2",
				"AnItem.explicitCapacities-enum3",
				"AnItem.explicitCapacities-enum4",
				"AnItem.explicitCapacities-type"),
				AnItem.explicitCapacities.getSourceFeatures().stream().map(Feature::getID).collect(toList()));
		assertEquals(asList(
				"AnItem.defaultCapacities-string0",
				"AnItem.defaultCapacities-string1",
				"AnItem.defaultCapacities-string2",
				"AnItem.defaultCapacities-string3",
				"AnItem.defaultCapacities-string4",
				"AnItem.defaultCapacities-bool0",
				"AnItem.defaultCapacities-bool1",
				"AnItem.defaultCapacities-bool2",
				"AnItem.defaultCapacities-bool3",
				"AnItem.defaultCapacities-bool4",
				"AnItem.defaultCapacities-int0",
				"AnItem.defaultCapacities-int1",
				"AnItem.defaultCapacities-int2",
				"AnItem.defaultCapacities-int3",
				"AnItem.defaultCapacities-int4",
				"AnItem.defaultCapacities-double0",
				"AnItem.defaultCapacities-double1",
				"AnItem.defaultCapacities-double2",
				"AnItem.defaultCapacities-double3",
				"AnItem.defaultCapacities-double4",
				"AnItem.defaultCapacities-enum0",
				"AnItem.defaultCapacities-enum1",
				"AnItem.defaultCapacities-enum2",
				"AnItem.defaultCapacities-enum3",
				"AnItem.defaultCapacities-enum4",
				"AnItem.defaultCapacities-type"),
				AnItem.defaultCapacities.getSourceFeatures().stream().map(Feature::getID).collect(toList()));
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@WrapperIgnore
		static final DynamicModel<String> explicitCapacities =
				DynamicModel.create(new StringField(), 1, 2, 3, 4, 5);

		@WrapperIgnore
		static final DynamicModel<String> defaultCapacities =
				DynamicModel.create(new StringField());

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
