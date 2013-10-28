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
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.aString;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.anEnum;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.anInt;
import static com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.anItem;
import static com.exedio.cope.pattern.BatzenFieldModelTest.AnItem.eins;
import static com.exedio.cope.pattern.BatzenFieldModelTest.AnItem.zwei;

import com.exedio.cope.EnumField;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;
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
				zwei,
				zwei.of(aString), zwei.of(anInt), zwei.of(anEnum), zwei.of(anItem),
			}), AnItem.TYPE.getDeclaredFeatures());


		assertEquals(true,  eins.of(aString).isInitial());
		assertEquals(false, eins.of(aString).isFinal());
		assertEquals(true,  eins.of(aString).isMandatory());

		assertEquals(ABatzen.TYPE, eins.getValueType());
		assertEquals(ABatzen.class, eins.getValueClass());

		assertSame(aString, eins.getTemplate(eins.of(aString)));
		assertSame(anInt,   eins.getTemplate(eins.of(anInt)));

		assertEqualsUnmodifiable(list(aString, anInt, anEnum, anItem), eins  .getTemplates());

		assertEqualsUnmodifiable(list(eins.  of(aString), eins  .of(anInt), eins  .of(anEnum), eins  .of(anItem)), eins  .getComponents());

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

		/**
		 * TODO generate by instrumentor
		 */
		public String getAString()
		{
			return field().of(ABatzen.aString).get(item());
		}

		/**
		 * TODO generate by instrumentor
		 */
		public int getAnInt()
		{
			return field().of(ABatzen.anInt).getMandatory(item());
		}

		/**
		 * TODO generate by instrumentor
		 */
		public AnEnum getAnEnum()
		{
			return field().of(ABatzen.anEnum).get(item());
		}

		/**
		 * TODO generate by instrumentor
		 */
		public AnItem getAnItem()
		{
			return field().of(ABatzen.anItem).get(item());
		}

		/**
		 * TODO generate by instrumentor
		 */
		public void setAnItem(final AnItem value)
		{
			field().of(ABatzen.anItem).set(item(), value);
		}

		/**
		 * TODO generate by instrumentor
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * TODO generate by instrumentor
		 */
		static final BatzenType<ABatzen> TYPE = BatzenType.newType(ABatzen.class);

		/**
		 * TODO generate by instrumentor
		 */
		protected ABatzen(final BatzenField<?> field, final Item item)
		{
			super(field, item);
		}
	}

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
				AnItem.zwei.of(aString).map(code + '-' + n + 'B'),
				AnItem.zwei.of(anInt).map(n + 10),
				AnItem.zwei.of(anEnum).map(ABatzen.AnEnum.facet2));
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

