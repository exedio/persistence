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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class DataVaultCharacterSetTest
{
	@Test void test()
	{
		assertEquals(null, MyBlank.blank.getAnnotatedVaultValue());
		assertEquals("MyBlank-ok-V", MyBlank.ok.getAnnotatedVaultValue());
		assertBroken("MyBlank.broken.V", "MyBlankBroken.broken", 7, MyBlankBroken.class);
		assertEquals("MyOk-V", MyOk.blank.getAnnotatedVaultValue());
		assertEquals("MyOk-ok-V", MyOk.ok.getAnnotatedVaultValue());
		assertBroken("MyOk.broken.V", "MyOkBroken.broken", 4, MyOkBroken.class);
		assertBroken("MyBroken.V", "MyBrokenBlank", 8, MyBrokenBlank.class);
		assertEquals("MyBroken-ok-V", MyBroken.ok.getAnnotatedVaultValue());
		assertBroken("MyBroken.broken.V", "MyBrokenBroken.broken", 8, MyBrokenBroken.class);

		assertFails(
				() -> TypesBound.newType(MyBlankEmpty.class, ap -> { throw new AssertionFailedError(); }),
				IllegalArgumentException.class,
				"@Vault at MyBlankEmpty.empty must not be empty");
	}

	private static void assertBroken(
			final String value, final String origin, final int position, final Class<? extends Item> javaClass)
	{
		assertFails(
				() -> TypesBound.newType(javaClass, ap -> { throw new AssertionFailedError(); }),
				IllegalArgumentException.class,
				"@Vault at " + origin + " must contain just [---,0-9,A-Z,a-z], " +
				"but was >" + value + "< containing a forbidden character at position " + position);
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyBlank extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField blank = new DataField();

		@Vault("MyBlank-ok-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField ok = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyBlank> TYPE = com.exedio.cope.TypesBound.newType(MyBlank.class,MyBlank::new);

		@com.exedio.cope.instrument.Generated
		protected MyBlank(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class MyBlankEmpty extends Item
	{
		@Vault("")
		@Wrapper(wrap="*", visibility=NONE)
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final DataField empty = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class MyBlankBroken extends Item
	{
		@Vault("MyBlank.broken.V")
		@Wrapper(wrap="*", visibility=NONE)
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final DataField broken = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@Vault("MyOk-V")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyOk extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField blank = new DataField();

		@Vault("MyOk-ok-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField ok = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyOk> TYPE = com.exedio.cope.TypesBound.newType(MyOk.class,MyOk::new);

		@com.exedio.cope.instrument.Generated
		protected MyOk(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Vault("MyOk-V")
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class MyOkBroken extends Item
	{
		@Vault("MyOk.broken.V")
		@Wrapper(wrap="*", visibility=NONE)
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final DataField broken = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@Vault("MyBroken.V")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyBroken extends Item
	{
		@Vault("MyBroken-ok-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField ok = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyBroken> TYPE = com.exedio.cope.TypesBound.newType(MyBroken.class,MyBroken::new);

		@com.exedio.cope.instrument.Generated
		protected MyBroken(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Vault("MyBroken.V")
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class MyBrokenBlank extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final DataField blank = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@Vault("MyBroken.V")
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class MyBrokenBroken extends Item
	{
		@Vault("MyBroken.broken.V")
		@Wrapper(wrap="*", visibility=NONE)
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final DataField broken = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}
}
