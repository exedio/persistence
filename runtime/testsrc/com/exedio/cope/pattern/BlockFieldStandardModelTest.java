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
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.EnumField;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import java.awt.Color;
import java.util.Arrays;
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
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				AnItem.TYPE.getThis(),
				AnItem.code,
				eins,
				eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem),
				eins.of(aColor), eins.of(aColor).getRGB(),
				eins.of(aMedia), eins.of(aMedia).getBody(), eins.of(aMedia).getContentType(), eins.of(aMedia).getLastModified(), eins.of(aMedia).getUnison(),
				eins.of(aList),
				eins.of(aSet),
				zwei,
				zwei.of(aString), zwei.of(anInt), zwei.of(anEnum), zwei.of(anItem),
				zwei.of(aColor), zwei.of(aColor).getRGB(),
				zwei.of(aMedia), zwei.of(aMedia).getBody(), zwei.of(aMedia).getContentType(), zwei.of(aMedia).getLastModified(), zwei.of(aMedia).getUnison(),
				zwei.of(aList),
				zwei.of(aSet),
			}), AnItem.TYPE.getDeclaredFeatures());


		assertEquals(AnItem.TYPE, eins.of(aString).getType());
		assertEquals(AnItem.TYPE, eins.getType());
		assertEquals("eins-aString", eins.of(aString).getName());
		assertEquals("eins", eins.getName());
		assertEquals("com.exedio.cope.pattern.BlockFieldStandardModelTest$ABlock#aString", aString.toString());
		assertEquals("AnItem.eins-aString", eins.of(aString).toString());
		assertEquals("AnItem.eins", eins.toString());
		assertEquals(eins, eins.of(aString).getPattern());
		assertEqualsUnmodifiable(list(eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem), eins.of(aColor), eins.of(aMedia), eins.of(aList), eins.of(aSet)), eins.getSourceFeatures());

		assertEquals(true,  eins.of(aString).isInitial());
		assertEquals(false, eins.of(aString).isFinal());
		assertEquals(true,  eins.of(aString).isMandatory());

		assertEquals(ABlock.class, ABlock.TYPE.getJavaClass());
		assertEquals(null, ABlock.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(), ABlock.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem, aColor, aMedia, aList, aSet), ABlock.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem, aColor, aMedia, aList, aSet), ABlock.TYPE.getFeatures());
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

		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem, aColor, aMedia, aList, aSet), eins.getTemplates());
		assertEqualsUnmodifiable(list(eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem), eins.of(aColor), eins.of(aMedia), eins.of(aList), eins.of(aSet)), eins.getComponents());

		assertSerializedSame(aString, 339);
		assertSerializedSame(aColor , 338);
		assertSerializedSame(eins.of(aString), 395);
		assertSerializedSame(eins.of(aColor ), 394);
		assertSerializedSame(eins, 387);
		assertSerializedSame(zwei, 387);
		assertSerializedSame(ABlock.TYPE, 295);

		try
		{
			eins.of(AnItem.code);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("AnItem.code is not a template of AnItem.eins", e.getMessage());
		}
		try
		{
			eins.getTemplate(AnItem.code);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("AnItem.code is not a component of AnItem.eins", e.getMessage());
		}
		final StringField zweiString = zwei.of(aString);
		try
		{
			eins.getTemplate(zweiString);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("AnItem.zwei-aString is not a component of AnItem.eins", e.getMessage());
		}
	}

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
		@Wrapper(wrap="getURL", visibility=NONE)
		static final Media aMedia = new Media().optional().contentType("text/plain", "text/html");
		static final ListField<String> aList = ListField.create(new StringField());
		static final SetField<Integer> aSet = SetField.create(new IntegerField());


	/**
	 * Returns the value of {@link #aString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getAString()
	{
		return field().of(ABlock.aString).get(item());
	}

	/**
	 * Sets a new value for {@link #aString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getAnInt()
	{
		return field().of(ABlock.anInt).getMandatory(item());
	}

	/**
	 * Sets a new value for {@link #anInt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAnInt(final int anInt)
	{
		field().of(ABlock.anInt).set(item(),anInt);
	}

	/**
	 * Returns the value of {@link #anEnum}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	AnEnum getAnEnum()
	{
		return field().of(ABlock.anEnum).get(item());
	}

	/**
	 * Sets a new value for {@link #anEnum}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAnEnum(@javax.annotation.Nonnull final AnEnum anEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		field().of(ABlock.anEnum).set(item(),anEnum);
	}

	/**
	 * Returns the value of {@link #anItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	AnItem getAnItem()
	{
		return field().of(ABlock.anItem).get(item());
	}

	/**
	 * Sets a new value for {@link #anItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAnItem(@javax.annotation.Nullable final AnItem anItem)
	{
		field().of(ABlock.anItem).set(item(),anItem);
	}

	/**
	 * Returns the value of {@link #aColor}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.awt.Color getAColor()
	{
		return field().of(ABlock.aColor).get(item());
	}

	/**
	 * Sets a new value for {@link #aColor}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAColor(@javax.annotation.Nonnull final java.awt.Color aColor)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.pattern.ColorAlphaViolationException
	{
		field().of(ABlock.aColor).set(item(),aColor);
	}

	/**
	 * Returns a Locator the content of {@link #aMedia} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getAMediaLocator()
	{
		return field().of(ABlock.aMedia).getLocator(item());
	}

	/**
	 * Returns the content type of the media {@link #aMedia}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	java.lang.String getAMediaContentType()
	{
		return field().of(ABlock.aMedia).getContentType(item());
	}

	/**
	 * Returns whether media {@link #aMedia} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isAMediaNull()
	{
		return field().of(ABlock.aMedia).isNull(item());
	}

	/**
	 * Returns the last modification date of media {@link #aMedia}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getAMediaLastModified()
	{
		return field().of(ABlock.aMedia).getLastModified(item());
	}

	/**
	 * Returns the body length of the media {@link #aMedia}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getAMediaLength()
	{
		return field().of(ABlock.aMedia).getLength(item());
	}

	/**
	 * Returns the body of the media {@link #aMedia}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAMedia(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value aMedia)
			throws
				java.io.IOException
	{
		field().of(ABlock.aMedia).set(item(),aMedia);
	}

	/**
	 * Sets the content of media {@link #aMedia}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAMedia(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		field().of(ABlock.aMedia).set(item(),body,contentType);
	}

	/**
	 * Sets the content of media {@link #aMedia}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAMedia(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		field().of(ABlock.aMedia).set(item(),body,contentType);
	}

	/**
	 * Returns the value of {@link #aList}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.List<String> getAList()
	{
		return field().of(ABlock.aList).get(item());
	}

	/**
	 * Returns a query for the value of {@link #aList}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	com.exedio.cope.Query<String> getAListQuery()
	{
		return field().of(ABlock.aList).getQuery(item());
	}

	/**
	 * Adds a new value for {@link #aList}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addTo")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="removeAllFrom")
	boolean removeAllFromAList(@javax.annotation.Nonnull final String aList)
	{
		return field().of(ABlock.aList).removeAll(item(),aList);
	}

	/**
	 * Sets a new value for {@link #aList}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Set<Integer> getASet()
	{
		return field().of(ABlock.aSet).get(item());
	}

	/**
	 * Returns a query for the value of {@link #aSet}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	com.exedio.cope.Query<Integer> getASetQuery()
	{
		return field().of(ABlock.aSet).getQuery(item());
	}

	/**
	 * Sets a new value for {@link #aSet}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addTo")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="removeFrom")
	boolean removeFromASet(@javax.annotation.Nonnull final Integer element)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		return field().of(ABlock.aSet).remove(item(),element);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The type information for aBlock.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.pattern.BlockType<ABlock> TYPE = com.exedio.cope.pattern.BlockType.newType(ABlock.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ABlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
}

	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final StringField code = new StringField().toFinal();

		static final BlockField<ABlock> eins = BlockField.create(ABlock.TYPE);
		static final BlockField<ABlock> zwei = BlockField.create(ABlock.TYPE);

		AnItem(final String code, final int n)
		{
			this(
				AnItem.code.map(code),
				AnItem.eins.of(aString).map(code + '-' + n + 'A'),
				AnItem.eins.of(anInt).map(n),
				AnItem.eins.of(anEnum).map(ABlock.AnEnum.facet1),
				AnItem.eins.of(aColor).map(new Color(10, 20, 30)),
				AnItem.zwei.of(aString).map(code + '-' + n + 'B'),
				AnItem.zwei.of(anInt).map(n + 10),
				AnItem.zwei.of(anEnum).map(ABlock.AnEnum.facet2),
				AnItem.zwei.of(aColor).map(new Color(110, 120, 130)));
		}


	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnItem(
				@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.code.map(code),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getCode()
	{
		return AnItem.code.get(this);
	}

	/**
	 * Returns the value of {@link #eins}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	@javax.annotation.Nonnull
	ABlock eins()
	{
		return AnItem.eins.get(this);
	}

	/**
	 * Returns the value of {@link #zwei}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	@javax.annotation.Nonnull
	ABlock zwei()
	{
		return AnItem.zwei.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}


	// workaround eclipse warnings about unused imports when using static imports instead
	static final StringField aString = ABlock.aString;
	static final IntegerField anInt = ABlock.anInt;
	static final EnumField<ABlock.AnEnum> anEnum = ABlock.anEnum;
	private static final ItemField<AnItem> anItem = ABlock.anItem;
	static final ColorField aColor = ABlock.aColor;
	private static final Media aMedia = ABlock.aMedia;
	private static final ListField<String> aList = ABlock.aList;
	private static final SetField<Integer> aSet = ABlock.aSet;
	private static final BlockField<ABlock> eins = AnItem.eins;
	private static final BlockField<ABlock> zwei = AnItem.zwei;
}

