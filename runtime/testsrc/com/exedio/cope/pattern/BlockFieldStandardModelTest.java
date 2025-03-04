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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CopeIgnore;
import com.exedio.cope.EnumField;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.LocalizationKeys;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BlockFieldStandardModelTest
{
	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(BlockFieldStandardModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertSame(ABlock.TYPE, ABlock.TYPE.as(ABlock.class));
		assertSame(ABlock.TYPE, BlockType.forClass(ABlock.class));
		assertSame(ABlock.TYPE, BlockType.forClassUnchecked(ABlock.class));

		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				AnItem.TYPE.getThis(),
				AnItem.code,
				eins,
				eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem),
				eins.of(aColor), eins.of(aColor).getRGB(),
				eins.of(aRange), eins.of(aRange).getFrom(), eins.of(aRange).getTo(), eins.of(aRange).getUnison(),
				eins.of(aMedia), eins.of(aMedia).getBody(), eins.of(aMedia).getContentType(), eins.of(aMedia).getLastModified(), eins.of(aMedia).getUnison(),
				eins.of(aList),
				eins.of(aSet),
				eins.of(anEnumMap),
				eins.of(anEnumMap).getField(ABlock.AnEnum.facet1),
				eins.of(anEnumMap).getField(ABlock.AnEnum.facet2),
				zwei,
				zwei.of(aString), zwei.of(anInt), zwei.of(anEnum), zwei.of(anItem),
				zwei.of(aColor), zwei.of(aColor).getRGB(),
				zwei.of(aRange), zwei.of(aRange).getFrom(), zwei.of(aRange).getTo(), zwei.of(aRange).getUnison(),
				zwei.of(aMedia), zwei.of(aMedia).getBody(), zwei.of(aMedia).getContentType(), zwei.of(aMedia).getLastModified(), zwei.of(aMedia).getUnison(),
				zwei.of(aList),
				zwei.of(aSet),
				zwei.of(anEnumMap),
				zwei.of(anEnumMap).getField(ABlock.AnEnum.facet1),
				zwei.of(anEnumMap).getField(ABlock.AnEnum.facet2),
			}), AnItem.TYPE.getDeclaredFeatures());


		assertEquals(AnItem.TYPE, eins.of(aString).getType());
		assertEquals(AnItem.TYPE, eins.getType());
		assertEquals("eins-aString", eins.of(aString).getName());
		assertEquals("eins", eins.getName());
		assertEquals("com.exedio.cope.pattern.BlockFieldStandardModelTest$ABlock#aString", aString.toString());
		assertEquals("AnItem.eins-aString", eins.of(aString).toString());
		assertEquals("AnItem.eins", eins.toString());
		assertEquals(eins, eins.of(aString).getPattern());
		assertEqualsUnmodifiable(list(eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem), eins.of(aColor), eins.of(aRange), eins.of(aMedia), eins.of(aList), eins.of(aSet), eins.of(anEnumMap)), eins.getSourceFeatures());

		assertEquals(true,  eins.of(aString).isInitial());
		assertEquals(false, eins.of(aString).isFinal());
		assertEquals(true,  eins.of(aString).isMandatory());

		assertEquals(ABlock.class, ABlock.TYPE.getJavaClass());
		assertEquals(null, ABlock.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(), ABlock.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem, aColor, aRange, aMedia, aList, aSet, anEnumMap), ABlock.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem, aColor, aRange, aMedia, aList, aSet, anEnumMap), ABlock.TYPE.getFeatures());
		assertSame(anInt, ABlock.TYPE.getDeclaredFeature("anInt"));
		assertSame(anInt, ABlock.TYPE.getFeature("anInt"));
		assertSame(null, ABlock.TYPE.getDeclaredFeature(""));
		assertSame(null, ABlock.TYPE.getFeature(""));
		assertSame(null, ABlock.TYPE.getDeclaredFeature(null));
		assertSame(null, ABlock.TYPE.getFeature(null));

		assertEquals(ABlock.TYPE, eins.getValueType());
		assertEquals(ABlock.class, eins.getValueClass());

		assertSame(aString, eins.getTemplate(eins.of(aString)));
		assertSame(anInt,   eins.getTemplate(eins.of(anInt)));

		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem, aColor, aRange, aMedia, aList, aSet, anEnumMap), eins.getTemplates());
		assertEqualsUnmodifiable(list(eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem), eins.of(aColor), eins.of(aRange), eins.of(aMedia), eins.of(aList), eins.of(aSet), eins.of(anEnumMap)), eins.getComponents());

		assertSerializedSame(aString, 339);
		assertSerializedSame(aColor , 338);
		assertSerializedSame(eins.of(aString), 395);
		assertSerializedSame(eins.of(aColor ), 394);
		assertSerializedSame(eins, 387);
		assertSerializedSame(zwei, 387);
		assertSerializedSame(ABlock.TYPE, 295);

		assertFails(
				() -> eins.of(AnItem.code),
				IllegalArgumentException.class,
				"AnItem.code is not a template of AnItem.eins");
		assertFails(
				() -> eins.getTemplate(AnItem.code),
				IllegalArgumentException.class,
				"AnItem.code is not a component of AnItem.eins");
		final StringField zweiString = zwei.of(aString);
		assertFails(
				() -> eins.getTemplate(zweiString),
				IllegalArgumentException.class,
				"AnItem.zwei-aString is not a component of AnItem.eins");
	}

	/**
	 * @see com.exedio.cope.LocalizationKeysPatternTest#testVerbose()
	 */
	@Test public void testLocalizationKeys()
	{
		assertEquals(List.of(
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.ABlock",
				"BlockFieldStandardModelTest.ABlock"),
				ABlock.TYPE.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.ABlock",
				"BlockFieldStandardModelTest.ABlock"),
				LocalizationKeys.get(ABlock.class));
		assertEquals(List.of(
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.AnItem",
				"BlockFieldStandardModelTest.AnItem"),
				AnItem.TYPE.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.AnItem.eins",
				"BlockFieldStandardModelTest.AnItem.eins",
				"eins"),
				eins.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.AnItem.eins.anInt",
				"BlockFieldStandardModelTest.AnItem.eins.anInt",
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.ABlock.anInt",
				"BlockFieldStandardModelTest.ABlock.anInt",
				"anInt"),
				eins.of(anInt).getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.ABlock.anInt",
				"BlockFieldStandardModelTest.ABlock.anInt",
				"anInt"),
				ABlock.anInt.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.AnItem.eins.aList.SourceType",
				"BlockFieldStandardModelTest.AnItem.eins.aList.SourceType",
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.ABlock.aList.SourceType",
				"BlockFieldStandardModelTest.ABlock.aList.SourceType",
				"aList.SourceType",
				"com.exedio.cope.pattern.Entry",
				"Entry"),
				eins.of(aList).getEntryType().getLocalizationKeys());
		assertFails(
				aList::getEntryType,
				IllegalStateException.class,
				"feature not mounted");
		assertEquals(List.of(
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.AnItem.eins.aList.SourceType.order",
				"BlockFieldStandardModelTest.AnItem.eins.aList.SourceType.order",
				"com.exedio.cope.pattern.BlockFieldStandardModelTest.ABlock.aList.SourceType.order",
				"BlockFieldStandardModelTest.ABlock.aList.SourceType.order",
				"aList.SourceType.order",
				"com.exedio.cope.pattern.Entry.order",
				"Entry.order",
				"order"),
				eins.of(aList).getOrder().getLocalizationKeys());
		final IntegerField aListOrder = aList.getOrder();
		assertFails(
				aListOrder::getLocalizationKeys,
				IllegalStateException.class,
				"feature not mounted");
	}

	@WrapperType(indent=2)
	static final class ABlock extends Block
	{
		enum AnEnum
		{
			facet1, facet2
		}

		static final StringField aString = new StringField();
		static final IntegerField anInt = new IntegerField();
		static final EnumField<AnEnum> anEnum = EnumField.create(AnEnum.class);
		static final ItemField<AnItem> anItem = ItemField.create(AnItem.class).optional();
		static final ColorField aColor = new ColorField();
		static final RangeField<Integer> aRange = RangeField.create(new IntegerField().optional());
		@Wrapper(wrap="getURL", visibility=NONE)
		static final Media aMedia = new Media().optional().contentTypes("text/plain", "text/html");
		static final ListField<String> aList = ListField.create(new StringField());
		static final SetField<Integer> aSet = SetField.create(new IntegerField());
		static final EnumMapField<AnEnum,Integer> anEnumMap = EnumMapField.create(AnEnum.class, new IntegerField().optional());

		@SuppressWarnings("unused") // OK: is to be ignored
		@CopeIgnore @WrapperIgnore static final StringField ignored = new StringField();


		/**
		 * Returns the value of {@link #aString}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getAString()
		{
			return field().of(ABlock.aString).get(item());
		}

		/**
		 * Sets a new value for {@link #aString}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAString(@javax.annotation.Nonnull final java.lang.String aString)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			field().of(ABlock.aString).set(item(),aString);
		}

		/**
		 * Returns the value of {@link #anInt}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getAnInt()
		{
			return field().of(ABlock.anInt).getMandatory(item());
		}

		/**
		 * Sets a new value for {@link #anInt}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAnInt(final int anInt)
		{
			field().of(ABlock.anInt).set(item(),anInt);
		}

		/**
		 * Returns the value of {@link #anEnum}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		AnEnum getAnEnum()
		{
			return field().of(ABlock.anEnum).get(item());
		}

		/**
		 * Sets a new value for {@link #anEnum}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAnEnum(@javax.annotation.Nonnull final AnEnum anEnum)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			field().of(ABlock.anEnum).set(item(),anEnum);
		}

		/**
		 * Returns the value of {@link #anItem}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		AnItem getAnItem()
		{
			return field().of(ABlock.anItem).get(item());
		}

		/**
		 * Sets a new value for {@link #anItem}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAnItem(@javax.annotation.Nullable final AnItem anItem)
		{
			field().of(ABlock.anItem).set(item(),anItem);
		}

		/**
		 * Returns the value of {@link #aColor}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.awt.Color getAColor()
		{
			return field().of(ABlock.aColor).get(item());
		}

		/**
		 * Sets a new value for {@link #aColor}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAColor(@javax.annotation.Nonnull final java.awt.Color aColor)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.pattern.ColorAlphaViolationException
		{
			field().of(ABlock.aColor).set(item(),aColor);
		}

		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.pattern.Range<Integer> getARange()
		{
			return field().of(ABlock.aRange).get(item());
		}

		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setARange(@javax.annotation.Nonnull final com.exedio.cope.pattern.Range<? extends Integer> aRange)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			field().of(ABlock.aRange).set(item(),aRange);
		}

		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getFrom")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Integer getARangeFrom()
		{
			return field().of(ABlock.aRange).getFrom(item());
		}

		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getTo")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Integer getARangeTo()
		{
			return field().of(ABlock.aRange).getTo(item());
		}

		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setFrom")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setARangeFrom(@javax.annotation.Nullable final Integer aRange)
		{
			field().of(ABlock.aRange).setFrom(item(),aRange);
		}

		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setTo")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setARangeTo(@javax.annotation.Nullable final Integer aRange)
		{
			field().of(ABlock.aRange).setTo(item(),aRange);
		}

		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="doesContain")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean doesARangeContain(@javax.annotation.Nonnull final Integer aRange)
		{
			return field().of(ABlock.aRange).doesContain(item(),aRange);
		}

		/**
		 * Returns a Locator the content of {@link #aMedia} is available under.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		com.exedio.cope.pattern.MediaPath.Locator getAMediaLocator()
		{
			return field().of(ABlock.aMedia).getLocator(item());
		}

		/**
		 * Returns the content type of the media {@link #aMedia}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getAMediaContentType()
		{
			return field().of(ABlock.aMedia).getContentType(item());
		}

		/**
		 * Returns whether media {@link #aMedia} is null.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean isAMediaNull()
		{
			return field().of(ABlock.aMedia).isNull(item());
		}

		/**
		 * Returns the last modification date of media {@link #aMedia}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.util.Date getAMediaLastModified()
		{
			return field().of(ABlock.aMedia).getLastModified(item());
		}

		/**
		 * Returns the body length of the media {@link #aMedia}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		long getAMediaLength()
		{
			return field().of(ABlock.aMedia).getLength(item());
		}

		/**
		 * Returns the body of the media {@link #aMedia}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		byte[] getAMediaBody()
		{
			return field().of(ABlock.aMedia).getBody(item());
		}

		/**
		 * Writes the body of media {@link #aMedia} into the given stream.
		 * Does nothing, if the media is null.
		 * @throws java.io.IOException if accessing {@code body} throws an IOException.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void getAMediaBody(@javax.annotation.Nonnull final java.io.OutputStream body)
				throws
					java.io.IOException
		{
			field().of(ABlock.aMedia).getBody(item(),body);
		}

		/**
		 * Writes the body of media {@link #aMedia} into the given file.
		 * Does nothing, if the media is null.
		 * @throws java.io.IOException if accessing {@code body} throws an IOException.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void getAMediaBody(@javax.annotation.Nonnull final java.nio.file.Path body)
				throws
					java.io.IOException
		{
			field().of(ABlock.aMedia).getBody(item(),body);
		}

		/**
		 * Writes the body of media {@link #aMedia} into the given file.
		 * Does nothing, if the media is null.
		 * @throws java.io.IOException if accessing {@code body} throws an IOException.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@java.lang.Deprecated
		void getAMediaBody(@javax.annotation.Nonnull final java.io.File body)
				throws
					java.io.IOException
		{
			field().of(ABlock.aMedia).getBody(item(),body);
		}

		/**
		 * Sets the content of media {@link #aMedia}.
		 * @throws java.io.IOException if accessing {@code body} throws an IOException.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAMedia(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value aMedia)
				throws
					java.io.IOException
		{
			field().of(ABlock.aMedia).set(item(),aMedia);
		}

		/**
		 * Sets the content of media {@link #aMedia}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAMedia(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
		{
			field().of(ABlock.aMedia).set(item(),body,contentType);
		}

		/**
		 * Sets the content of media {@link #aMedia}.
		 * @throws java.io.IOException if accessing {@code body} throws an IOException.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAMedia(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			field().of(ABlock.aMedia).set(item(),body,contentType);
		}

		/**
		 * Sets the content of media {@link #aMedia}.
		 * @throws java.io.IOException if accessing {@code body} throws an IOException.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAMedia(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			field().of(ABlock.aMedia).set(item(),body,contentType);
		}

		/**
		 * Sets the content of media {@link #aMedia}.
		 * @throws java.io.IOException if accessing {@code body} throws an IOException.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAMedia(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			field().of(ABlock.aMedia).set(item(),body,contentType);
		}

		/**
		 * Returns the value of {@link #aList}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.List<String> getAList()
		{
			return field().of(ABlock.aList).get(item());
		}

		/**
		 * Returns a query for the value of {@link #aList}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.Query<String> getAListQuery()
		{
			return field().of(ABlock.aList).getQuery(item());
		}

		/**
		 * Adds a new value for {@link #aList}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void addToAList(@javax.annotation.Nonnull final String aList)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			field().of(ABlock.aList).add(item(),aList);
		}

		/**
		 * Removes all occurrences of {@code element} from {@link #aList}.
		 * @return {@code true} if the field set changed as a result of the call.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean removeAllFromAList(@javax.annotation.Nonnull final String aList)
		{
			return field().of(ABlock.aList).removeAll(item(),aList);
		}

		/**
		 * Sets a new value for {@link #aList}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAList(@javax.annotation.Nonnull final java.util.Collection<? extends String> aList)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			field().of(ABlock.aList).set(item(),aList);
		}

		/**
		 * Returns the value of {@link #aSet}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.Set<Integer> getASet()
		{
			return field().of(ABlock.aSet).get(item());
		}

		/**
		 * Returns a query for the value of {@link #aSet}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.Query<Integer> getASetQuery()
		{
			return field().of(ABlock.aSet).getQuery(item());
		}

		/**
		 * Sets a new value for {@link #aSet}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setASet(@javax.annotation.Nonnull final java.util.Collection<? extends Integer> aSet)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			field().of(ABlock.aSet).set(item(),aSet);
		}

		/**
		 * Adds a new element to {@link #aSet}.
		 * @return {@code true} if the field set changed as a result of the call.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean addToASet(@javax.annotation.Nonnull final Integer element)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			return field().of(ABlock.aSet).add(item(),element);
		}

		/**
		 * Removes an element from {@link #aSet}.
		 * @return {@code true} if the field set changed as a result of the call.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeFrom")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean removeFromASet(@javax.annotation.Nonnull final Integer element)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			return field().of(ABlock.aSet).remove(item(),element);
		}

		/**
		 * Returns the value mapped to {@code k} by the field map {@link #anEnumMap}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Integer getAnEnumMap(@javax.annotation.Nonnull final AnEnum k)
		{
			return field().of(ABlock.anEnumMap).get(item(),k);
		}

		/**
		 * Associates {@code k} to a new value in the field map {@link #anEnumMap}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAnEnumMap(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nullable final Integer anEnumMap)
		{
			field().of(ABlock.anEnumMap).set(item(),k,anEnumMap);
		}

		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.Map<AnEnum,Integer> getAnEnumMapMap()
		{
			return field().of(ABlock.anEnumMap).getMap(item());
		}

		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setAnEnumMapMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends Integer> anEnumMap)
		{
			field().of(ABlock.anEnumMap).setMap(item(),anEnumMap);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The type information for aBlock.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.pattern.BlockType<ABlock> TYPE = com.exedio.cope.pattern.BlockType.newType(ABlock.class,ABlock::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private ABlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	static final class AnItem extends Item
	{
		static final StringField code = new StringField().toFinal();

		static final BlockField<ABlock> eins = BlockField.create(ABlock.TYPE);
		static final BlockField<ABlock> zwei = BlockField.create(ABlock.TYPE);

		AnItem(final String code, final int n)
		{
			//noinspection UnnecessarilyQualifiedStaticUsage
			this(
				SetValue.map(AnItem.code, code),
				SetValue.map(AnItem.eins.of(aString), code + '-' + n + 'A'),
				SetValue.map(AnItem.eins.of(anInt), n),
				SetValue.map(AnItem.eins.of(anEnum), ABlock.AnEnum.facet1),
				SetValue.map(AnItem.eins.of(aColor), new Color(10, 20, 30)),
				SetValue.map(AnItem.zwei.of(aString), code + '-' + n + 'B'),
				SetValue.map(AnItem.zwei.of(anInt), n + 10),
				SetValue.map(AnItem.zwei.of(anEnum), ABlock.AnEnum.facet2),
				SetValue.map(AnItem.zwei.of(aColor), new Color(110, 120, 130)));
		}


		/**
		 * Creates a new AnItem with all the fields initially needed.
		 * @param code the initial value for field {@link #code}.
		 * @throws com.exedio.cope.MandatoryViolationException if code is null.
		 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		AnItem(
					@javax.annotation.Nonnull final java.lang.String code)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.code,code),
			});
		}

		/**
		 * Creates a new AnItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #code}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getCode()
		{
			return AnItem.code.get(this);
		}

		/**
		 * Returns the value of {@link #eins}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		ABlock eins()
		{
			return AnItem.eins.get(this);
		}

		/**
		 * Returns the value of {@link #zwei}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		ABlock zwei()
		{
			return AnItem.zwei.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for anItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	// workaround eclipse warnings about unused imports when using static imports instead
	static final StringField aString = ABlock.aString;
	static final IntegerField anInt = ABlock.anInt;
	static final EnumField<ABlock.AnEnum> anEnum = ABlock.anEnum;
	private static final ItemField<AnItem> anItem = ABlock.anItem;
	static final ColorField aColor = ABlock.aColor;
	private static final RangeField<Integer> aRange = ABlock.aRange;
	private static final Media aMedia = ABlock.aMedia;
	private static final ListField<String> aList = ABlock.aList;
	private static final SetField<Integer> aSet = ABlock.aSet;
	private static final EnumMapField<ABlock.AnEnum,Integer> anEnumMap = ABlock.anEnumMap;
	private static final BlockField<ABlock> eins = AnItem.eins;
	private static final BlockField<ABlock> zwei = AnItem.zwei;
}

