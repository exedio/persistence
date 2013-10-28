/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.aColor;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.aList;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.aMedia;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.aString;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.anEnum;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.anInt;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.anItem;
import static com.exedio.cope.pattern.BatzenFieldModelTest.AnItem.eins;
import static com.exedio.cope.pattern.BatzenFieldModelTest.AnItem.zwei;

import com.exedio.cope.EnumField;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;
import java.awt.Color;
import java.util.Arrays;

public class BatzenFieldModelTest extends CopeAssert
{
	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(BatzenFieldModelTest.class, "MODEL");
	}

	public void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				AnItem.TYPE.getThis(),
				AnItem.code,
				eins,
				eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem),
				eins.of(aColor), eins.of(aColor).getRGB(),
				eins.of(aMedia), eins.of(aMedia).getBody(), eins.of(aMedia).getLastModified(),
				eins.of(aList),
				zwei,
				zwei.of(aString), zwei.of(anInt), zwei.of(anEnum), zwei.of(anItem),
				zwei.of(aColor), zwei.of(aColor).getRGB(),
				zwei.of(aMedia), zwei.of(aMedia).getBody(), zwei.of(aMedia).getLastModified(),
				zwei.of(aList),
			}), AnItem.TYPE.getDeclaredFeatures());


		assertEquals(AnItem.TYPE, eins.of(aString).getType());
		assertEquals(AnItem.TYPE, eins.getType());
		assertEquals("eins-aString", eins.of(aString).getName());
		assertEquals("eins", eins.getName());
		assertEquals("com.exedio.cope.pattern.BatzenFieldModelTest$ABatzen#aString", aString.toString());
		assertEquals("AnItem.eins-aString", eins.of(aString).toString());
		assertEquals("AnItem.eins", eins.toString());
		assertEquals(eins, eins.of(aString).getPattern());
		assertEqualsUnmodifiable(list(eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem), eins.of(aColor), eins.of(aMedia), eins.of(aList)), eins.getSourceFeatures());

		assertEquals(true,  eins.of(aString).isInitial());
		assertEquals(false, eins.of(aString).isFinal());
		assertEquals(true,  eins.of(aString).isMandatory());

		assertEquals(ABatzen.TYPE, eins.getValueType());
		assertEquals(ABatzen.class, eins.getValueClass());

		assertSame(aString, eins.getTemplate(eins.of(aString)));
		assertSame(anInt,   eins.getTemplate(eins.of(anInt)));

		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem, aColor, aMedia, aList), eins.getTemplates());
		assertEqualsUnmodifiable(list(eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem), eins.of(aColor), eins.of(aMedia), eins.of(aList)), eins.getComponents());

		assertSerializedSame(eins, 380);
		assertSerializedSame(zwei, 380);

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
		try
		{
			eins.getTemplate(zwei.of(aString));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("AnItem.zwei-aString is not a component of AnItem.eins", e.getMessage());
		}
	}

	static final class ABatzen extends Batzen
	{
		enum AnEnum
		{
			facet1, facet2;
		}

		static final StringField aString = new StringField();
		static final IntegerField anInt = new IntegerField();
		static final EnumField<AnEnum> anEnum = EnumField.create(AnEnum.class);
		static final ItemField<AnItem> anItem = ItemField.create(AnItem.class).optional();
		static final ColorField aColor = new ColorField();
		static final Media aMedia = new Media().optional().contentType("text/plain");
		static final ListField<String> aList = ListField.create(new StringField());


	/**

	 **
	 * Returns the value of {@link #aString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final java.lang.String getAString()
	{
		return field().of(ABatzen.aString).get(item());
	}/**

	 **
	 * Sets a new value for {@link #aString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAString(final java.lang.String aString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		field().of(ABatzen.aString).set(item(),aString);
	}/**

	 **
	 * Returns the value of {@link #anInt}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final int getAnInt()
	{
		return field().of(ABatzen.anInt).getMandatory(item());
	}/**

	 **
	 * Sets a new value for {@link #anInt}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAnInt(final int anInt)
	{
		field().of(ABatzen.anInt).set(item(),anInt);
	}/**

	 **
	 * Returns the value of {@link #anEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final AnEnum getAnEnum()
	{
		return field().of(ABatzen.anEnum).get(item());
	}/**

	 **
	 * Sets a new value for {@link #anEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAnEnum(final AnEnum anEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		field().of(ABatzen.anEnum).set(item(),anEnum);
	}/**

	 **
	 * Returns the value of {@link #anItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final AnItem getAnItem()
	{
		return field().of(ABatzen.anItem).get(item());
	}/**

	 **
	 * Sets a new value for {@link #anItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAnItem(final AnItem anItem)
	{
		field().of(ABatzen.anItem).set(item(),anItem);
	}/**

	 **
	 * Returns the value of {@link #aColor}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final java.awt.Color getAColor()
	{
		return field().of(ABatzen.aColor).get(item());
	}/**

	 **
	 * Sets a new value for {@link #aColor}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAColor(final java.awt.Color aColor)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.pattern.ColorAlphaViolationException
	{
		field().of(ABatzen.aColor).set(item(),aColor);
	}/**

	 **
	 * Returns a URL the content of {@link #aMedia} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getURL public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final java.lang.String getAMediaURL()
	{
		return field().of(ABatzen.aMedia).getURL(item());
	}/**

	 **
	 * Returns a Locator the content of {@link #aMedia} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLocator public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final com.exedio.cope.pattern.MediaPath.Locator getAMediaLocator()
	{
		return field().of(ABatzen.aMedia).getLocator(item());
	}/**

	 **
	 * Returns whether media {@link #aMedia} is null.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.isNull public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final boolean isAMediaNull()
	{
		return field().of(ABatzen.aMedia).isNull(item());
	}/**

	 **
	 * Returns the last modification date of media {@link #aMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLastModified public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final java.util.Date getAMediaLastModified()
	{
		return field().of(ABatzen.aMedia).getLastModified(item());
	}/**

	 **
	 * Returns the body length of the media {@link #aMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getLength public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final long getAMediaLength()
	{
		return field().of(ABatzen.aMedia).getLength(item());
	}/**

	 **
	 * Returns the body of the media {@link #aMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final byte[] getAMediaBody()
	{
		return field().of(ABatzen.aMedia).getBody(item());
	}/**

	 **
	 * Writes the body of media {@link #aMedia} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void getAMediaBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		field().of(ABatzen.aMedia).getBody(item(),body);
	}/**

	 **
	 * Writes the body of media {@link #aMedia} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getBody public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void getAMediaBody(final java.io.File body)
			throws
				java.io.IOException
	{
		field().of(ABatzen.aMedia).getBody(item(),body);
	}/**

	 **
	 * Sets the content of media {@link #aMedia}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAMedia(final com.exedio.cope.pattern.Media.Value aMedia)
			throws
				java.io.IOException
	{
		field().of(ABatzen.aMedia).set(item(),aMedia);
	}/**

	 **
	 * Sets the content of media {@link #aMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAMedia(final byte[] body,final java.lang.String contentType)
	{
		field().of(ABatzen.aMedia).set(item(),body,contentType);
	}/**

	 **
	 * Sets the content of media {@link #aMedia}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAMedia(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		field().of(ABatzen.aMedia).set(item(),body,contentType);
	}/**

	 **
	 * Sets the content of media {@link #aMedia}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAMedia(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		field().of(ABatzen.aMedia).set(item(),body,contentType);
	}/**

	 **
	 * Returns the value of {@link #aList}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final java.util.List<String> getAList()
	{
		return field().of(ABatzen.aList).get(item());
	}/**

	 **
	 * Returns a query for the value of {@link #aList}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getQuery public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final com.exedio.cope.Query<String> getAListQuery()
	{
		return field().of(ABatzen.aList).getQuery(item());
	}/**

	 **
	 * Adds a new value for {@link #aList}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.addTo public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void addToAList(final String aList)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		field().of(ABatzen.aList).add(item(),aList);
	}/**

	 **
	 * Sets a new value for {@link #aList}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAList(final java.util.Collection<? extends String> aList)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		field().of(ABatzen.aList).set(item(),aList);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private static final long serialVersionUID = 1l;/**

	 **
	 * The type information for aBatzen.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	static final com.exedio.cope.pattern.BatzenType<ABatzen> TYPE = com.exedio.cope.pattern.BatzenType.newType(ABatzen.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Batzen#Batzen(com.exedio.cope.pattern.Batzen.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@SuppressWarnings("unused") private ABatzen(final com.exedio.cope.pattern.Batzen.ActivationParameters ap){super(ap);
}}

	static final class AnItem extends com.exedio.cope.Item
	{
		static final StringField code = new StringField().toFinal();

		static final BatzenField<ABatzen> eins = BatzenField.create(ABatzen.TYPE);
		static final BatzenField<ABatzen> zwei = BatzenField.create(ABatzen.TYPE);

		AnItem(final String code, final int n)
		{
			this(
				AnItem.code.map(code),
				AnItem.eins.of(aString).map(code + '-' + n + 'A'),
				AnItem.eins.of(anInt).map(n),
				AnItem.eins.of(anEnum).map(ABatzen.AnEnum.facet1),
				AnItem.eins.of(aColor).map(new Color(10, 20, 30)),
				AnItem.zwei.of(aString).map(code + '-' + n + 'B'),
				AnItem.zwei.of(anInt).map(n + 10),
				AnItem.zwei.of(anEnum).map(ABatzen.AnEnum.facet2),
				AnItem.zwei.of(aColor).map(new Color(110, 120, 130)));
		}


	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	AnItem(
				final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.code.map(code),
		});
	}/**

	 **
	 * Creates a new AnItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #code}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final java.lang.String getCode()
	{
		return AnItem.code.get(this);
	}/**

	 **
	 * Returns the value of {@link #eins}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final ABatzen getEins()
	{
		return AnItem.eins.get(this);
	}/**

	 **
	 * Returns the value of {@link #zwei}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final ABatzen getZwei()
	{
		return AnItem.zwei.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
}

