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
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MediaQueryContentTypeTest extends TestWithEnvironment
{
	MediaQueryContentTypeTest()
	{
		super(model);
	}

	@Test void test()
	{
		final Condition def1 = MyItem.def.contentTypeEqual("major/minor1");
		final Condition def2 = MyItem.def.contentTypeEqual("major/minor2");
		final Condition def3 = MyItem.def.contentTypeEqual("major/minor3");
		final Condition defN = MyItem.def.contentTypeEqual(null);
		final Condition def12= MyItem.def.contentTypeIn("major/minor1", "major/minor2");
		final Condition def13= MyItem.def.contentTypeIn("major/minor1", "major/minor3");
		final Condition def23= MyItem.def.contentTypeIn("major/minor2", "major/minor3");
		final Condition def1N= MyItem.def.contentTypeIn("major/minor1", null);
		final Condition def3N= MyItem.def.contentTypeIn("major/minor3", null);
		assertEquals("MyItem.def-contentType='major/minor1'", def1.toString());
		assertEquals("MyItem.def-contentType='major/minor2'", def2.toString());
		assertEquals("MyItem.def-contentType='major/minor3'", def3.toString());
		assertEquals("MyItem.def-contentType in ('major/minor1','major/minor2')", def12.toString());
		assertEquals("MyItem.def-contentType in ('major/minor1','major/minor3')", def13.toString());
		assertEquals("MyItem.def-contentType in ('major/minor2','major/minor3')", def23.toString());
		assertEquals("MyItem.def-contentType is null", defN.toString());
		assertEquals("(MyItem.def-contentType is null or MyItem.def-contentType='major/minor1')", def1N.toString());
		assertEquals("(MyItem.def-contentType is null or MyItem.def-contentType='major/minor3')", def3N.toString());

		final Condition enu1 = MyItem.enu.contentTypeEqual("major/minor1");
		final Condition enu2 = MyItem.enu.contentTypeEqual("major/minor2");
		final Condition enu3 = MyItem.enu.contentTypeEqual("major/minor3");
		final Condition enuF = MyItem.enu.contentTypeEqual("major/minor9");
		final Condition enuN = MyItem.enu.contentTypeEqual(null);
		final Condition enu12= MyItem.enu.contentTypeIn("major/minor1", "major/minor2");
		final Condition enu13= MyItem.enu.contentTypeIn("major/minor1", "major/minor3");
		final Condition enu23= MyItem.enu.contentTypeIn("major/minor2", "major/minor3");
		final Condition enu1N= MyItem.enu.contentTypeIn("major/minor1", null);
		final Condition enu3N= MyItem.enu.contentTypeIn("major/minor3", null);
		assertEquals("MyItem.enu-contentType='0'", enu1.toString());
		assertEquals("MyItem.enu-contentType='1'", enu2.toString());
		assertEquals("MyItem.enu-contentType='2'", enu3.toString());
		assertEquals("FALSE", enuF.toString());
		assertEquals("MyItem.enu-contentType in ('0','1')", enu12.toString());
		assertEquals("MyItem.enu-contentType in ('0','2')", enu13.toString());
		assertEquals("MyItem.enu-contentType in ('1','2')", enu23.toString());
		assertEquals("MyItem.enu-contentType is null", enuN.toString());
		assertEquals("(MyItem.enu-contentType is null or MyItem.enu-contentType='0')", enu1N.toString());
		assertEquals("(MyItem.enu-contentType is null or MyItem.enu-contentType='2')", enu3N.toString());

		final Condition sub1 = MyItem.sub.contentTypeEqual("major/minor1");
		final Condition sub2 = MyItem.sub.contentTypeEqual("major/minor2");
		final Condition sub3 = MyItem.sub.contentTypeEqual("major/minor3");
		final Condition subF = MyItem.sub.contentTypeEqual("major9/minor");
		final Condition subFt= MyItem.sub.contentTypeEqual("major");
		final Condition subFs= MyItem.sub.contentTypeEqual("major/");
		final Condition subN = MyItem.sub.contentTypeEqual(null);
		final Condition sub12= MyItem.sub.contentTypeIn("major/minor1", "major/minor2");
		final Condition sub13= MyItem.sub.contentTypeIn("major/minor1", "major/minor3");
		final Condition sub23= MyItem.sub.contentTypeIn("major/minor2", "major/minor3");
		final Condition sub1N= MyItem.sub.contentTypeIn("major/minor1", null);
		final Condition sub3N= MyItem.sub.contentTypeIn("major/minor3", null);
		assertEquals("MyItem.sub-minor='minor1'", sub1.toString());
		assertEquals("MyItem.sub-minor='minor2'", sub2.toString());
		assertEquals("MyItem.sub-minor='minor3'", sub3.toString());
		assertEquals("FALSE", subF.toString());
		assertEquals("FALSE", subFt.toString());
		assertEquals("MyItem.sub-minor in ('minor1','minor2')", sub12.toString());
		assertEquals("MyItem.sub-minor in ('minor1','minor3')", sub13.toString());
		assertEquals("MyItem.sub-minor in ('minor2','minor3')", sub23.toString());
		assertEquals("MyItem.sub-minor=''", subFs.toString()); // TODO could be FALSE, as minor has minimum length of 1
		assertEquals("MyItem.sub-minor is null", subN.toString());
		assertEquals("(MyItem.sub-minor is null or MyItem.sub-minor='minor1')", sub1N.toString());
		assertEquals("(MyItem.sub-minor is null or MyItem.sub-minor='minor3')", sub3N.toString());

		final Condition fix1 = MyItem.fix.contentTypeEqual("major/minor");
		final Condition fixF = MyItem.fix.contentTypeEqual("major9/minor9");
		final Condition fixN = MyItem.fix.contentTypeEqual(null);
		final Condition fix1N= MyItem.fix.contentTypeIn("major/minor", null);
		final Condition fixN1= MyItem.fix.contentTypeIn(null, "major/minor");
		assertEquals("MyItem.fix-lastModified is not null", fix1.toString()); // must query lastModified as there is no field for contentType
		assertEquals("FALSE", fixF.toString());
		assertEquals("MyItem.fix-lastModified is null", fixN.toString()); // must query lastModified as there is no field for contentType
		assertEquals("TRUE", fix1N.toString());
		assertEquals("TRUE", fixN1.toString());

		final MyItem item1 = new MyItem(value("major/minor1"), value("major/minor1"), value("major/minor1"), value("major/minor"));
		final MyItem item2 = new MyItem(value("major/minor2"), value("major/minor2"), value("major/minor2"), value("major/minor"));
		final MyItem itemN = new MyItem(null, null, null, null);

		assertEquals(List.of(item1), MyItem.TYPE.search(def1));
		assertEquals(List.of(item2), MyItem.TYPE.search(def2));
		assertEquals(List.of(     ), MyItem.TYPE.search(def3));
		assertEquals(List.of(itemN), MyItem.TYPE.search(defN));
		assertEquals(List.of(item1, item2), MyItem.TYPE.search(def12));
		assertEquals(List.of(item1       ), MyItem.TYPE.search(def13));
		assertEquals(List.of(item2       ), MyItem.TYPE.search(def23));
		assertEquals(List.of(item1, itemN), MyItem.TYPE.search(def1N));
		assertEquals(List.of(       itemN), MyItem.TYPE.search(def3N));

		assertEquals(List.of(item1), MyItem.TYPE.search(enu1));
		assertEquals(List.of(item2), MyItem.TYPE.search(enu2));
		assertEquals(List.of(     ), MyItem.TYPE.search(enu3));
		assertEquals(List.of(     ), MyItem.TYPE.search(enuF));
		assertEquals(List.of(itemN), MyItem.TYPE.search(enuN));
		assertEquals(List.of(item1, item2), MyItem.TYPE.search(enu12));
		assertEquals(List.of(item1       ), MyItem.TYPE.search(enu13));
		assertEquals(List.of(item2       ), MyItem.TYPE.search(enu23));
		assertEquals(List.of(item1, itemN), MyItem.TYPE.search(enu1N));
		assertEquals(List.of(       itemN), MyItem.TYPE.search(enu3N));

		assertEquals(List.of(item1), MyItem.TYPE.search(sub1));
		assertEquals(List.of(item2), MyItem.TYPE.search(sub2));
		assertEquals(List.of(     ), MyItem.TYPE.search(sub3));
		assertEquals(List.of(     ), MyItem.TYPE.search(subF));
		assertEquals(List.of(     ), MyItem.TYPE.search(subFt));
		assertEquals(List.of(     ), MyItem.TYPE.search(subFs));
		assertEquals(List.of(itemN), MyItem.TYPE.search(subN));
		assertEquals(List.of(item1, item2), MyItem.TYPE.search(sub12));
		assertEquals(List.of(item1       ), MyItem.TYPE.search(sub13));
		assertEquals(List.of(item2       ), MyItem.TYPE.search(sub23));
		assertEquals(List.of(item1, itemN), MyItem.TYPE.search(sub1N));
		assertEquals(List.of(       itemN), MyItem.TYPE.search(sub3N));

		assertEquals(List.of(item1, item2), MyItem.TYPE.search(fix1));
		assertEquals(List.of(     ), MyItem.TYPE.search(fixF));
		assertEquals(List.of(itemN), MyItem.TYPE.search(fixN));
		assertEquals(List.of(item1, item2, itemN), MyItem.TYPE.search(fix1N));
		assertEquals(List.of(item1, item2, itemN), MyItem.TYPE.search(fixN1));


		// contentTypeIn special cases

		assertFails(() -> MyItem.def.contentTypeIn((String[])null), NullPointerException.class, "Cannot read the array length because \"values\" is null");
		assertFails(() -> MyItem.enu.contentTypeIn((String[])null), NullPointerException.class, "Cannot read the array length because \"contentTypes\" is null");
		assertFails(() -> MyItem.sub.contentTypeIn((String[])null), NullPointerException.class, "Cannot read the array length because \"contentTypes\" is null");
		assertFails(() -> MyItem.fix.contentTypeIn((String[])null), NullPointerException.class, "Cannot read the array length because \"<local5>\" is null");

		assertEquals(defN.toString(), MyItem.def.contentTypeIn((String)null).toString());
		assertEquals(enuN.toString(), MyItem.enu.contentTypeIn((String)null).toString());
		assertEquals(subN.toString(), MyItem.sub.contentTypeIn((String)null).toString());
		assertEquals(fixN.toString(), MyItem.fix.contentTypeIn((String)null).toString());

		assertEquals(enu1 .toString(), MyItem.enu.contentTypeIn("major/minor1",                 "major/minor9").toString());
		assertEquals(enu12.toString(), MyItem.enu.contentTypeIn("major/minor1", "major/minor2", "major/minor9").toString());
		assertEquals(enu1N.toString(), MyItem.enu.contentTypeIn("major/minor1", null,           "major/minor9").toString());
		assertEquals(enuN .toString(), MyItem.enu.contentTypeIn(                null,           "major/minor9").toString());

		assertEquals(sub1 .toString(), MyItem.sub.contentTypeIn("major/minor1",                 "major9/minor").toString());
		assertEquals(sub12.toString(), MyItem.sub.contentTypeIn("major/minor1", "major/minor2", "major9/minor").toString());
		assertEquals(sub1N.toString(), MyItem.sub.contentTypeIn("major/minor1", null,           "major9/minor").toString());
		assertEquals(subN .toString(), MyItem.sub.contentTypeIn(                null,           "major9/minor").toString());

		assertEquals(fix1 .toString(), MyItem.fix.contentTypeIn("major/minor",       "major9/minor").toString());
		assertEquals(fix1N.toString(), MyItem.fix.contentTypeIn("major/minor", null, "major9/minor").toString());
		assertEquals(fixN1.toString(), MyItem.fix.contentTypeIn("major/minor", null, "major9/minor").toString());
		assertEquals(fixN .toString(), MyItem.fix.contentTypeIn(               null, "major9/minor").toString());
	}

	@WrapperType(indent=2, comments=false)
	private static class MyItem extends Item
	{
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		static final Media def = new Media().optional().toFinal();

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		static final Media enu = new Media().optional().toFinal().contentTypes("major/minor1", "major/minor2", "major/minor3");

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		static final Media sub = new Media().optional().toFinal().contentTypeSub("major");

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		static final Media fix = new Media().optional().toFinal().contentType("major/minor");

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value def,
					@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value enu,
					@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value sub,
					@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value fix)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.def,def),
				com.exedio.cope.SetValue.map(MyItem.enu,enu),
				com.exedio.cope.SetValue.map(MyItem.sub,sub),
				com.exedio.cope.SetValue.map(MyItem.fix,fix),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model model = new Model(MyItem.TYPE);

	private static Media.Value value(final String contentType)
	{
		return Media.toValue(new byte[]{1, 2, 3}, contentType);
	}
}
