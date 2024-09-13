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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.UsageEntryPoint;
import org.junit.jupiter.api.Test;

public class SetFieldCopyModelTest
{
	@SuppressWarnings("unused") // OK: Model that is never connected
	static final Model MODEL = new Model(SetFieldItemWithCopyConstraints.TYPE);

	@Test void testCopyOnlyForItemFields()
	{
		final SetField<Integer> set = SetField.create(new IntegerField());
		assertFails(
				() -> set.copyWith(new IntegerField()),
				IllegalStateException.class,
				"copyWith requires the SetField's element to be an ItemField");
	}

	@Test void testCopyOnlyForFinalFields()
	{
		assertFails(
				() -> new Model(BrokenNonFinalTemplate.TYPE),
				IllegalArgumentException.class,
				"insufficient template for CopyConstraint BrokenNonFinalTemplate-set.stuff.copyFrom(parent,BrokenNonFinalTemplate.stuff) (BrokenNonFinalTemplate-set.stuffCopyFromparent): " +
				"BrokenNonFinalTemplate.stuff is not final");
	}

	@Test void testCopyConstraints()
	{
		final Type<? extends Item> relationType = SetFieldItemWithCopyConstraints.sameAAndB.getEntryType();
		final CopyConstraint copyAParent = (CopyConstraint)relationType.getFeature("aCopyFromparent");
		final CopyConstraint copyAElement = (CopyConstraint)relationType.getFeature("aCopyFromelement");
		final CopyConstraint copyBParent = (CopyConstraint)relationType.getFeature("bCopyFromparent");
		final CopyConstraint copyBElement = (CopyConstraint)relationType.getFeature("bCopyFromelement");
		assertEquals(asList(copyAParent, copyAElement, copyBParent, copyBElement), relationType.getDeclaredCopyConstraints());

		assertEquals(SetFieldItemWithCopyConstraints.sameAAndB.getParent(), copyAParent.getTarget());
		assertEquals(SetFieldItemWithCopyConstraints.a, copyAParent.getTemplate());
		assertEquals(SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithCopyField(SetFieldItemWithCopyConstraints.a), copyAParent.getCopyField());
		assertSame(copyAParent.getCopyField(), copyAParent.getCopyFunction());
		assertEquals(false, copyAParent.isChoice());

		assertEquals(SetFieldItemWithCopyConstraints.sameAAndB.getElement(), copyAElement.getTarget());
		assertEquals(SetFieldItemWithCopyConstraints.a, copyAElement.getTemplate());
		assertEquals(SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithCopyField(SetFieldItemWithCopyConstraints.a), copyAElement.getCopyField());
		assertSame(copyAElement.getCopyField(), copyAElement.getCopyFunction());
		assertEquals(false, copyAElement.isChoice());

		assertEquals(SetFieldItemWithCopyConstraints.sameAAndB.getParent(), copyBParent.getTarget());
		assertEquals(SetFieldItemWithCopyConstraints.b, copyBParent.getTemplate());
		assertEquals(SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithCopyField(SetFieldItemWithCopyConstraints.b), copyBParent.getCopyField());
		assertEquals(copyBParent.getCopyField(), copyBParent.getCopyFunction());
		assertEquals(false, copyBParent.isChoice());

		assertEquals(SetFieldItemWithCopyConstraints.sameAAndB.getElement(), copyBElement.getTarget());
		assertEquals(SetFieldItemWithCopyConstraints.b, copyBElement.getTemplate());
		assertEquals(SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithCopyField(SetFieldItemWithCopyConstraints.b), copyBElement.getCopyField());
		assertEquals(copyBElement.getCopyField(), copyBElement.getCopyFunction());
		assertEquals(false, copyBElement.isChoice());
	}

	@Test void testGetCopyWithTemplates()
	{
		assertEquals(
			emptyList(),
			SetFieldItemWithCopyConstraints.any.getCopyWithTemplateFields()
		);
		assertEquals(
			asList(SetFieldItemWithCopyConstraints.a, SetFieldItemWithCopyConstraints.b),
			SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithTemplateFields()
		);
		assertFails(
				() -> SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithTemplateFields().add(SetFieldItemWithCopyConstraints.parent),
				UnsupportedOperationException.class,
				null);
	}

	@Test void testGetCopyWithCopy()
	{
		final FunctionField<String> aCopy = SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithCopyField(SetFieldItemWithCopyConstraints.a);
		final FunctionField<String> bCopy = SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithCopyField(SetFieldItemWithCopyConstraints.b);
		assertEquals("a", aCopy.getName());
		assertEquals(SetFieldItemWithCopyConstraints.sameAAndB.getEntryType(), aCopy.getType());
		assertEquals(
			asList(SetFieldItemWithCopyConstraints.sameAAndB.getParent(), SetFieldItemWithCopyConstraints.sameAAndB.getElement(), aCopy, bCopy),
			SetFieldItemWithCopyConstraints.sameAAndB.getEntryType().getFields()
		);
		assertFails(
				() -> SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithCopyField(aCopy),
				IllegalArgumentException.class,
				"field from wrong type: expected SetFieldItemWithCopyConstraints " +
				"but was SetFieldItemWithCopyConstraints-sameAAndB");
	}

	@Test void testGetCopyFieldWrongField()
	{
		assertFails(
				() -> SetFieldItemWithCopyConstraints.sameAAndB.getCopyWithCopyField(SetFieldItemWithCopyConstraints.parent),
				IllegalStateException.class,
				"no copy for SetFieldItemWithCopyConstraints.parent");
	}

	@Test void testGetCopyFieldNoCopies()
	{
		assertFails(
				() -> SetFieldItemWithCopyConstraints.any.getCopyWithCopyField(SetFieldItemWithCopyConstraints.parent),
				IllegalStateException.class,
				"no CopyConstraints declared");
	}

	@Test void testGetCopyFieldNotMounted()
	{
		final SetField<String> setField = SetField.create(new StringField());
		assertFails(
				() -> setField.getCopyWithCopyField(new StringField()),
				IllegalStateException.class,
				"feature not mounted");
	}

	@Test void testNoTarget()
	{
		assertFails(
				() -> new Model(DoesntHaveField.TYPE, NoTargetItem.TYPE),
				IllegalArgumentException.class,
				"insufficient template for CopyConstraint NoTargetItem-noTarget.field.copyFrom(element,\"field\") (NoTargetItem-noTarget.fieldCopyFromelement): " +
				"feature >field< at type DoesntHaveField not found");
	}

	@Test void testAddTwice()
	{
		final StringField stringField = new StringField();
		final SetField<SetFieldItemWithCopyConstraints> setField = SetField.create(ItemField.create(SetFieldItemWithCopyConstraints.class)).copyWith(stringField);
		assertFails(
				() -> setField.copyWith(stringField),
				IllegalArgumentException.class,
				"added '"+stringField+"' twice");
		assertEquals(
			asList(stringField),
			setField.getCopyWithTemplateFields()
		);
	}

	@Test void testCopyWithCopy()
	{
		final StringField stringField = new StringField();
		final SetField<SetFieldItemWithCopyConstraints> setField = SetField.create(ItemField.create(SetFieldItemWithCopyConstraints.class)).copyWith(stringField);
		assertFails(
				() -> setField.copy(new CopyMapper()),
				IllegalStateException.class,
				"cannot copy if copyWith is set");
	}

	@WrapperType(comments=false, indent=2, constructor=NONE, genericConstructor=NONE)
	private static class SetFieldItemWithCopyConstraints extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField a = new StringField().toFinal();

		@Wrapper(wrap="*", visibility=NONE)
		static final StringField b = new StringField().toFinal();

		@Wrapper(wrap="*", visibility=NONE)
		static final StringField parent = new StringField().toFinal();

		@Wrapper(wrap="*", visibility=NONE)
		static final SetField<SetFieldItemWithCopyConstraints> sameAAndB = SetField.create(ItemField.create(SetFieldItemWithCopyConstraints.class)).copyWith(a).copyWith(b);

		@Wrapper(wrap="*", visibility=NONE)
		static final SetField<SetFieldItemWithCopyConstraints> any = SetField.create(ItemField.create(SetFieldItemWithCopyConstraints.class));


		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<SetFieldItemWithCopyConstraints> TYPE = com.exedio.cope.TypesBound.newType(SetFieldItemWithCopyConstraints.class,SetFieldItemWithCopyConstraints::new);

		@com.exedio.cope.instrument.Generated
		protected SetFieldItemWithCopyConstraints(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2, constructor=NONE, genericConstructor=NONE)
	private static class NoTargetItem extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField field = new StringField().toFinal();

		@Wrapper(wrap="*", visibility=NONE)
		@UsageEntryPoint
		static final SetField<DoesntHaveField> noTarget = SetField.create(ItemField.create(DoesntHaveField.class)).copyWith(field);


		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<NoTargetItem> TYPE = com.exedio.cope.TypesBound.newType(NoTargetItem.class,NoTargetItem::new);

		@com.exedio.cope.instrument.Generated
		protected NoTargetItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2, constructor=NONE, genericConstructor=NONE)
	private static class BrokenNonFinalTemplate extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final IntegerField stuff = new IntegerField();

		@Wrapper(wrap="*", visibility=NONE)
		@UsageEntryPoint
		static final SetField<BrokenNonFinalTemplate> set = SetField.create(ItemField.create(BrokenNonFinalTemplate.class)).copyWith(stuff);


		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BrokenNonFinalTemplate> TYPE = com.exedio.cope.TypesBound.newType(BrokenNonFinalTemplate.class,BrokenNonFinalTemplate::new);

		@com.exedio.cope.instrument.Generated
		protected BrokenNonFinalTemplate(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2, constructor=NONE, genericConstructor=NONE)
	private static class DoesntHaveField extends Item
	{

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<DoesntHaveField> TYPE = com.exedio.cope.TypesBound.newType(DoesntHaveField.class,DoesntHaveField::new);

		@com.exedio.cope.instrument.Generated
		protected DoesntHaveField(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
