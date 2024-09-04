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

package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.DEFAULT;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.ListField;
import com.exedio.cope.pattern.SetField;
import org.junit.jupiter.api.Test;

public class ListFieldCopyTemplateTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(ItemWithComplexTemplate.TYPE);

	public ListFieldCopyTemplateTest()
	{
		super(MODEL);
	}

	@Test void copyMustNotIncludeCopyConstraintsOfTemplates()
	{
		final Type<?> listRelationType = ItemWithComplexTemplate.list.getRelationType();
		final CopyConstraint ccListParent = (CopyConstraint)listRelationType.getFeature("valueCopyFromparent");
		final CopyConstraint ccListElement = (CopyConstraint)listRelationType.getFeature("valueCopyFromelement");
		assertEquals(asList(ccListParent, ccListElement), listRelationType.getDeclaredCopyConstraints());

		final Type<?> setRelationType = ItemWithComplexTemplate.set.getRelationType();
		final CopyConstraint ccSetParent = (CopyConstraint)setRelationType.getFeature("valueCopyFromparent");
		final CopyConstraint ccSetElement = (CopyConstraint)setRelationType.getFeature("valueCopyFromelement");
		assertEquals(asList(ccSetParent, ccSetElement), setRelationType.getDeclaredCopyConstraints());

		final CopyConstraint ccValueOther = (CopyConstraint)ItemWithComplexTemplate.TYPE.getFeature("valueCopyFromother");
		assertEquals(singletonList(ccValueOther), ItemWithComplexTemplate.TYPE.getDeclaredCopyConstraints());
		assertEquals(null, listRelationType.getFeature(ccValueOther.getName()));
		assertEquals(null, setRelationType.getFeature(ccValueOther.getName()));
	}

	@Test void copyMustNotBeUniqueMeta()
	{
		assertNotNull(ItemWithComplexTemplate.value.getImplicitUniqueConstraint());
		assertEquals(null, ItemWithComplexTemplate.list.getCopyWithCopyField(ItemWithComplexTemplate.value).getImplicitUniqueConstraint());
		assertEquals(null, ItemWithComplexTemplate.set.getCopyWithCopyField(ItemWithComplexTemplate.value).getImplicitUniqueConstraint());
	}

	@Test void copyMustNotBeUnique()
	{
		final ItemWithComplexTemplate item = new ItemWithComplexTemplate(null, "x");
		item.addToList(item);
		item.addToList(item);
	}


	@WrapperType(comments=false, indent=2)
	private static class ItemWithComplexTemplate extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final ItemField<ItemWithComplexTemplate> other = ItemField.create(ItemWithComplexTemplate.class).optional().toFinal();

		@WrapperInitial
		@Wrapper(wrap="*", visibility=NONE)
		private static final StringField value = new StringField().copyFromSelf(other).unique().toFinal().defaultTo("defaultValue");

		@Wrapper(wrap="*", visibility=NONE)
		@Wrapper(wrap="addTo", visibility=DEFAULT)
		private static final ListField<ItemWithComplexTemplate> list = ListField.create(ItemField.create(ItemWithComplexTemplate.class)).copyWith(value);

		@Wrapper(wrap="*", visibility=NONE)
		private static final SetField<ItemWithComplexTemplate> set = SetField.create(ItemField.create(ItemWithComplexTemplate.class)).copyWith(value);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ItemWithComplexTemplate(
					@javax.annotation.Nullable final ItemWithComplexTemplate other,
					@javax.annotation.Nonnull final java.lang.String value)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(ItemWithComplexTemplate.other,other),
				com.exedio.cope.SetValue.map(ItemWithComplexTemplate.value,value),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected ItemWithComplexTemplate(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void addToList(@javax.annotation.Nonnull final ItemWithComplexTemplate list)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			ItemWithComplexTemplate.list.add(this,list);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemWithComplexTemplate> TYPE = com.exedio.cope.TypesBound.newType(ItemWithComplexTemplate.class,ItemWithComplexTemplate::new);

		@com.exedio.cope.instrument.Generated
		protected ItemWithComplexTemplate(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

}
