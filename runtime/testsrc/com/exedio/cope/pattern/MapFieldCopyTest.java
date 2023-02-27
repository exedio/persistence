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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.CopyViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class MapFieldCopyTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(MapFieldCopyItem.TYPE);

	private static final CopyConstraint copyKeyParent = (CopyConstraint)MapFieldCopyItem.keyShared.getRelationType().getFeature("fieldCopyFromparent");
	private static final CopyConstraint copyKeyKey = (CopyConstraint)MapFieldCopyItem.keyShared.getRelationType().getFeature("fieldCopyFromkey");

	private static final CopyConstraint copyValueParent = (CopyConstraint)MapFieldCopyItem.valueShared.getRelationType().getFeature("fieldCopyFromparent");
	private static final CopyConstraint copyValueValue = (CopyConstraint)MapFieldCopyItem.valueShared.getRelationType().getFeature("fieldCopyFromvalue");

	private static final CopyConstraint copyBothDifferentParent = (CopyConstraint)MapFieldCopyItem.bothDifferentShared.getRelationType().getFeature("fieldCopyFromparent");
	private static final CopyConstraint copyBothDifferentKey = (CopyConstraint)MapFieldCopyItem.bothDifferentShared.getRelationType().getFeature("fieldCopyFromkey");
	private static final CopyConstraint copyBothDifferentParentOther = (CopyConstraint)MapFieldCopyItem.bothDifferentShared.getRelationType().getFeature("otherFieldCopyFromparent");
	private static final CopyConstraint copyBothDifferentValue = (CopyConstraint)MapFieldCopyItem.bothDifferentShared.getRelationType().getFeature("otherFieldCopyFromvalue");

	private static final CopyConstraint copyBothSameParent = (CopyConstraint)MapFieldCopyItem.bothSameShared.getRelationType().getFeature("fieldCopyFromparent");
	private static final CopyConstraint copyBothSameKey = (CopyConstraint)MapFieldCopyItem.bothSameShared.getRelationType().getFeature("fieldCopyFromkey");
	private static final CopyConstraint copyBothSameValue = (CopyConstraint)MapFieldCopyItem.bothSameShared.getRelationType().getFeature("fieldCopyFromvalue");

	private static final CopyConstraint copyBothDoubleFieldParent = (CopyConstraint)MapFieldCopyItem.bothDoubleShared.getRelationType().getFeature("fieldCopyFromparent");
	private static final CopyConstraint copyBothDoubleFieldKey = (CopyConstraint)MapFieldCopyItem.bothDoubleShared.getRelationType().getFeature("fieldCopyFromkey");
	private static final CopyConstraint copyBothDoubleFieldValue = (CopyConstraint)MapFieldCopyItem.bothDoubleShared.getRelationType().getFeature("fieldCopyFromvalue");
	private static final CopyConstraint copyBothDoubleOtherParent = (CopyConstraint)MapFieldCopyItem.bothDoubleShared.getRelationType().getFeature("otherFieldCopyFromparent");
	private static final CopyConstraint copyBothDoubleOtherKey = (CopyConstraint)MapFieldCopyItem.bothDoubleShared.getRelationType().getFeature("otherFieldCopyFromkey");
	private static final CopyConstraint copyBothDoubleOtherValue = (CopyConstraint)MapFieldCopyItem.bothDoubleShared.getRelationType().getFeature("otherFieldCopyFromvalue");

	public MapFieldCopyTest()
	{
		super(MODEL);
	}

	@Test void testModel()
	{
		assertEquals(emptyList(), MapFieldCopyItem.TYPE.getDeclaredCopyConstraints());
		assertEquals(asList(copyKeyParent, copyKeyKey), MapFieldCopyItem.keyShared.getRelationType().getDeclaredCopyConstraints());
		assertEquals(asList(copyValueParent, copyValueValue), MapFieldCopyItem.valueShared.getRelationType().getDeclaredCopyConstraints());
		assertEquals(asList(copyBothDifferentParent, copyBothDifferentKey, copyBothDifferentParentOther, copyBothDifferentValue), MapFieldCopyItem.bothDifferentShared.getRelationType().getDeclaredCopyConstraints());
		assertEquals(asList(copyBothSameParent, copyBothSameKey, copyBothSameValue), MapFieldCopyItem.bothSameShared.getRelationType().getDeclaredCopyConstraints());
		assertEquals(
			asList(
				copyBothDoubleFieldParent, copyBothDoubleFieldKey, copyBothDoubleFieldValue,
				copyBothDoubleOtherParent, copyBothDoubleOtherKey, copyBothDoubleOtherValue
			),
			MapFieldCopyItem.bothDoubleShared.getRelationType().getDeclaredCopyConstraints()
		);
	}

	@Test void testKey()
	{
		final MapFieldCopyItem itemA1 = new MapFieldCopyItem("a");
		final MapFieldCopyItem itemA2 = new MapFieldCopyItem("a");
		final MapFieldCopyItem itemB = new MapFieldCopyItem("b");
		itemA1.setKeyShared(itemA2, itemB);
		try
		{
			itemA1.setKeyShared(itemB, itemB);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation for " + copyKeyParent + " and " + copyKeyKey + ", " +
				"expected 'a' from target " + itemA1 + " " +
				"but also 'b' from target " + itemB,
				e.getMessage()
			);
		}
	}

	@Test void testValue()
	{
		final MapFieldCopyItem itemA = new MapFieldCopyItem("a");
		final MapFieldCopyItem itemB = new MapFieldCopyItem("b");
		itemA.setValueShared(itemB, itemA);
		try
		{
			itemA.setValueShared(itemB, itemB);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation on MapFieldCopyItem-valueShared-0 " +
				"for " + copyValueValue + ", " +
				"expected 'b' from target " + itemB + ", but was 'a'",
				e.getMessage()
			);
		}
		assertEquals(itemA, itemA.getValueShared(itemB));
		try
		{
			itemA.setValueShared(itemA, itemB);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation for " + copyValueParent + " and " + copyValueValue + ", " +
				"expected 'a' from target " + itemA + " " +
				"but also 'b' from target " + itemB,
				e.getMessage()
			);
		}
	}

	@Test void testBothDifferent()
	{
		final MapFieldCopyItem itemAA = new MapFieldCopyItem("a", "A");
		final MapFieldCopyItem itemAB = new MapFieldCopyItem("a", "B");
		final MapFieldCopyItem itemBA = new MapFieldCopyItem("b", "A");
		itemAA.setBothDifferentShared(itemAA, itemAA);
		itemAA.setBothDifferentShared(itemAB, itemBA);
		try
		{
			itemAA.setBothDifferentShared(itemBA, itemAA);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation for " + copyBothDifferentParent + " and " + copyBothDifferentKey + ", " +
				"expected 'a' from target " + itemAA + " " +
				"but also 'b' from target " + itemBA,
				e.getMessage()
			);
		}
		try
		{
			itemAA.setBothDifferentShared(itemAA, itemAB);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation on MapFieldCopyItem-bothDifferentShared-0 " +
				"for " + copyBothDifferentValue + ", " +
				"expected 'B' from target " + itemAB + ", " +
				"but was 'A'",
				e.getMessage()
			);
		}
	}

	@Test void testBothSame()
	{
		final MapFieldCopyItem itemA = new MapFieldCopyItem("a");
		final MapFieldCopyItem itemB = new MapFieldCopyItem("b");
		itemA.setBothSameShared(itemA, itemA);
		itemA.setBothSameShared(itemA, null);
		try
		{
			itemA.setBothSameShared(itemB, itemA);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation for " + copyBothSameParent + " and " + copyBothSameKey + ", " +
				"expected 'a' from target " + itemA + " " +
				"but also 'b' from target " + itemB,
				e.getMessage()
			);
		}
		assertEquals(emptyMap(), itemA.getBothSameSharedMap());
		try
		{
			itemA.setBothSameShared(itemA, itemB);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation for " + copyBothSameParent + " and " + copyBothSameValue + ", " +
				"expected 'a' from target " + itemA + " " +
				"but also 'b' from target " + itemB,
				e.getMessage()
			);
		}
		assertEquals(emptyMap(), itemA.getBothSameSharedMap());
		itemA.setBothSameShared(itemA, itemA);
		try
		{
			itemA.setBothSameShared(itemA, itemB);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation on MapFieldCopyItem-bothSameShared-1 " +
				"for " + copyBothSameValue + ", " +
				"expected 'b' from target " + itemB + ", " +
				"but was 'a'",
				e.getMessage()
			);
		}
	}

	@WrapperType(comments=false, indent=2)
	private static class MapFieldCopyItem extends Item
	{
		static final StringField field = new StringField().toFinal();

		static final StringField otherField = new StringField().toFinal();

		static final MapField<MapFieldCopyItem,MapFieldCopyItem> keyShared = MapField.create(ItemField.create(MapFieldCopyItem.class), ItemField.create(MapFieldCopyItem.class)).copyKeyWith(field);

		static final MapField<MapFieldCopyItem,MapFieldCopyItem> valueShared = MapField.create(ItemField.create(MapFieldCopyItem.class), ItemField.create(MapFieldCopyItem.class)).copyValueWith(field);

		/** copy constraints on both key and value, checking for two different fields */
		static final MapField<MapFieldCopyItem,MapFieldCopyItem> bothDifferentShared = MapField.create(ItemField.create(MapFieldCopyItem.class), ItemField.create(MapFieldCopyItem.class)).copyKeyWith(field).copyValueWith(otherField);

		/** copy constraints on both key and value, checking for two the same field */
		static final MapField<MapFieldCopyItem,MapFieldCopyItem> bothSameShared = MapField.create(ItemField.create(MapFieldCopyItem.class), ItemField.create(MapFieldCopyItem.class)).copyKeyWith(field).copyValueWith(field);

		/** two copy constraints each on both key and value */
		static final MapField<MapFieldCopyItem,MapFieldCopyItem> bothDoubleShared = MapField.create(ItemField.create(MapFieldCopyItem.class), ItemField.create(MapFieldCopyItem.class)).copyKeyWith(field).copyKeyWith(otherField).copyValueWith(field).copyValueWith(otherField);

		MapFieldCopyItem(@javax.annotation.Nonnull final java.lang.String field)
		{
			this(field, "other");
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MapFieldCopyItem(
					@javax.annotation.Nonnull final java.lang.String field,
					@javax.annotation.Nonnull final java.lang.String otherField)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MapFieldCopyItem.field,field),
				com.exedio.cope.SetValue.map(MapFieldCopyItem.otherField,otherField),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected MapFieldCopyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.lang.String getField()
		{
			return MapFieldCopyItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.lang.String getOtherField()
		{
			return MapFieldCopyItem.otherField.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final MapFieldCopyItem getKeyShared(@javax.annotation.Nonnull final MapFieldCopyItem k)
		{
			return MapFieldCopyItem.keyShared.get(this,k);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setKeyShared(@javax.annotation.Nonnull final MapFieldCopyItem k,@javax.annotation.Nullable final MapFieldCopyItem keyShared)
		{
			MapFieldCopyItem.keyShared.set(this,k,keyShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.Map<MapFieldCopyItem,MapFieldCopyItem> getKeySharedMap()
		{
			return MapFieldCopyItem.keyShared.getMap(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setKeySharedMap(@javax.annotation.Nonnull final java.util.Map<? extends MapFieldCopyItem,? extends MapFieldCopyItem> keyShared)
		{
			MapFieldCopyItem.keyShared.setMap(this,keyShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final com.exedio.cope.ItemField<MapFieldCopyItem> keySharedParent()
		{
			return MapFieldCopyItem.keyShared.getParent(MapFieldCopyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final MapFieldCopyItem getValueShared(@javax.annotation.Nonnull final MapFieldCopyItem k)
		{
			return MapFieldCopyItem.valueShared.get(this,k);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setValueShared(@javax.annotation.Nonnull final MapFieldCopyItem k,@javax.annotation.Nullable final MapFieldCopyItem valueShared)
		{
			MapFieldCopyItem.valueShared.set(this,k,valueShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.Map<MapFieldCopyItem,MapFieldCopyItem> getValueSharedMap()
		{
			return MapFieldCopyItem.valueShared.getMap(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setValueSharedMap(@javax.annotation.Nonnull final java.util.Map<? extends MapFieldCopyItem,? extends MapFieldCopyItem> valueShared)
		{
			MapFieldCopyItem.valueShared.setMap(this,valueShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final com.exedio.cope.ItemField<MapFieldCopyItem> valueSharedParent()
		{
			return MapFieldCopyItem.valueShared.getParent(MapFieldCopyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final MapFieldCopyItem getBothDifferentShared(@javax.annotation.Nonnull final MapFieldCopyItem k)
		{
			return MapFieldCopyItem.bothDifferentShared.get(this,k);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setBothDifferentShared(@javax.annotation.Nonnull final MapFieldCopyItem k,@javax.annotation.Nullable final MapFieldCopyItem bothDifferentShared)
		{
			MapFieldCopyItem.bothDifferentShared.set(this,k,bothDifferentShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.Map<MapFieldCopyItem,MapFieldCopyItem> getBothDifferentSharedMap()
		{
			return MapFieldCopyItem.bothDifferentShared.getMap(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setBothDifferentSharedMap(@javax.annotation.Nonnull final java.util.Map<? extends MapFieldCopyItem,? extends MapFieldCopyItem> bothDifferentShared)
		{
			MapFieldCopyItem.bothDifferentShared.setMap(this,bothDifferentShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final com.exedio.cope.ItemField<MapFieldCopyItem> bothDifferentSharedParent()
		{
			return MapFieldCopyItem.bothDifferentShared.getParent(MapFieldCopyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final MapFieldCopyItem getBothSameShared(@javax.annotation.Nonnull final MapFieldCopyItem k)
		{
			return MapFieldCopyItem.bothSameShared.get(this,k);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setBothSameShared(@javax.annotation.Nonnull final MapFieldCopyItem k,@javax.annotation.Nullable final MapFieldCopyItem bothSameShared)
		{
			MapFieldCopyItem.bothSameShared.set(this,k,bothSameShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.Map<MapFieldCopyItem,MapFieldCopyItem> getBothSameSharedMap()
		{
			return MapFieldCopyItem.bothSameShared.getMap(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setBothSameSharedMap(@javax.annotation.Nonnull final java.util.Map<? extends MapFieldCopyItem,? extends MapFieldCopyItem> bothSameShared)
		{
			MapFieldCopyItem.bothSameShared.setMap(this,bothSameShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final com.exedio.cope.ItemField<MapFieldCopyItem> bothSameSharedParent()
		{
			return MapFieldCopyItem.bothSameShared.getParent(MapFieldCopyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final MapFieldCopyItem getBothDoubleShared(@javax.annotation.Nonnull final MapFieldCopyItem k)
		{
			return MapFieldCopyItem.bothDoubleShared.get(this,k);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setBothDoubleShared(@javax.annotation.Nonnull final MapFieldCopyItem k,@javax.annotation.Nullable final MapFieldCopyItem bothDoubleShared)
		{
			MapFieldCopyItem.bothDoubleShared.set(this,k,bothDoubleShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.Map<MapFieldCopyItem,MapFieldCopyItem> getBothDoubleSharedMap()
		{
			return MapFieldCopyItem.bothDoubleShared.getMap(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setBothDoubleSharedMap(@javax.annotation.Nonnull final java.util.Map<? extends MapFieldCopyItem,? extends MapFieldCopyItem> bothDoubleShared)
		{
			MapFieldCopyItem.bothDoubleShared.setMap(this,bothDoubleShared);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final com.exedio.cope.ItemField<MapFieldCopyItem> bothDoubleSharedParent()
		{
			return MapFieldCopyItem.bothDoubleShared.getParent(MapFieldCopyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MapFieldCopyItem> TYPE = com.exedio.cope.TypesBound.newType(MapFieldCopyItem.class,MapFieldCopyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MapFieldCopyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}
