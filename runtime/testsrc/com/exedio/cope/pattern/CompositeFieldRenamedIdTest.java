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
import static com.exedio.cope.pattern.Composite.getTemplateName;
import static com.exedio.cope.pattern.CompositeFieldRenamedIdTest.MyComposite.virgnTemp;
import static com.exedio.cope.pattern.CompositeFieldRenamedIdTest.MyComposite.wrongTemp;
import static com.exedio.cope.pattern.CompositeFieldRenamedIdTest.MyItem.TYPE;
import static com.exedio.cope.pattern.CompositeFieldRenamedIdTest.MyItem.virgnComp;
import static com.exedio.cope.pattern.CompositeFieldRenamedIdTest.MyItem.wrongComp;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CopeName;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CompositeFieldRenamedIdTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(CompositeFieldRenamedIdTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(null,        ann(virgnTemp));
		assertEquals("namedTemp", ann(wrongTemp));
		assertEquals(null,        ann(virgnComp));
		assertEquals("namedComp", ann(wrongComp));

		assertEquals(null,                  ann(virgnComp.of(virgnTemp)));
		assertEquals(          "namedTemp", ann(virgnComp.of(wrongTemp))); // TODO virgnComp-namedTemp
		assertEquals(null                 , ann(wrongComp.of(virgnTemp))); // TODO namedComp-virgnTemp
		assertEquals(          "namedTemp", ann(wrongComp.of(wrongTemp))); // TODO namedComp-namedTemp

		assertEquals("virgnTemp", getTemplateName(virgnTemp));
		assertEquals("namedTemp", getTemplateName(wrongTemp));

		assertEquals("virgnComp-virgnTemp", virgnComp.of(virgnTemp).getName());
		assertEquals("virgnComp-namedTemp", virgnComp.of(wrongTemp).getName());
		assertEquals("namedComp-virgnTemp", wrongComp.of(virgnTemp).getName());
		assertEquals("namedComp-namedTemp", wrongComp.of(wrongTemp).getName());
	}

	private static String ann(final Feature f)
	{
		final CopeName a = f.getAnnotation(CopeName.class);
		return a!=null ? a.value() : null;
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	static final class MyComposite extends Composite
	{
		@WrapperIgnore static final StringField virgnTemp = new StringField();
		@CopeName("namedTemp")
		@WrapperIgnore static final StringField wrongTemp = new StringField();

		@com.exedio.cope.instrument.Generated
		private MyComposite(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@WrapperIgnore static final CompositeField<MyComposite> virgnComp = CompositeField.create(MyComposite.class);
		@CopeName("namedComp")
		@WrapperIgnore static final CompositeField<MyComposite> wrongComp = CompositeField.create(MyComposite.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
