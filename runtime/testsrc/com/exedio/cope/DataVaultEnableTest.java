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
import static com.exedio.cope.tojunit.TestSources.minimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DataVaultEnableTest
{
	@Test void testAnnotationsField()
	{
		assertEquals(false, MyBlank.blankF.isAnnotationPresent(Vault.class));
		assertEquals(false, MyBlank.blankM.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyBlank.vaultF.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyBlank.vaultM.isAnnotationPresent(Vault.class));
		assertEquals(false, AnVault.blankF.isAnnotationPresent(Vault.class));
		assertEquals(false, AnVault.blankM.isAnnotationPresent(Vault.class));
		assertEquals(true,  AnVault.vaultF.isAnnotationPresent(Vault.class));
		assertEquals(true,  AnVault.vaultM.isAnnotationPresent(Vault.class));
	}
	@Test void testAnnotationsType()
	{
		assertEquals(false, MyBlank.TYPE.isAnnotationPresent(Vault.class));
		assertEquals(true,  AnVault.TYPE.isAnnotationPresent(Vault.class));
	}
	@Test void testIsAnnotatedVault()
	{
		assertEquals(false, MyBlank.blankF.isAnnotatedVault());
		assertEquals(false, MyBlank.blankM.isAnnotatedVault());
		assertEquals(true,  MyBlank.vaultF.isAnnotatedVault());
		assertEquals(true,  MyBlank.vaultM.isAnnotatedVault());
		assertEquals(true,  AnVault.blankF.isAnnotatedVault());
		assertEquals(true,  AnVault.blankM.isAnnotatedVault());
		assertEquals(true,  AnVault.vaultF.isAnnotatedVault());
		assertEquals(true,  AnVault.vaultM.isAnnotatedVault());
	}
	@Test void testGetAnnotatedVaultValue()
	{
		assertEquals(null,               MyBlank.blankF.getAnnotatedVaultValue());
		assertEquals(null,               MyBlank.blankM.getAnnotatedVaultValue());
		assertEquals("MyBlank-vaultF-V", MyBlank.vaultF.getAnnotatedVaultValue());
		assertEquals("MyBlank-vaultM-V", MyBlank.vaultM.getAnnotatedVaultValue());
		assertEquals("AnVault-V",        AnVault.blankF.getAnnotatedVaultValue());
		assertEquals("AnVault-V",        AnVault.blankM.getAnnotatedVaultValue());
		assertEquals("AnVault-vaultF-V", AnVault.vaultF.getAnnotatedVaultValue());
		assertEquals("AnVault-vaultM-V", AnVault.vaultM.getAnnotatedVaultValue());
	}
	@Test void testDisabled()
	{
		model.connect(ConnectProperties.create(minimal()));
		assertIt(MyBlank.blankF);
		assertIt(MyBlank.blankM);
		assertIt(MyBlank.vaultF);
		assertIt(MyBlank.vaultM);
		assertIt(AnVault.blankF);
		assertIt(AnVault.blankM);
		assertIt(AnVault.vaultF);
		assertIt(AnVault.vaultM);
	}
	@Test void testEnabledAny()
	{
		model.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.services", "default MyBlank-vaultF-V MyBlank-vaultM-V AnVault-V AnVault-vaultF-V AnVault-vaultM-V"),
				single("vault.service.default",          VaultMockService.class),
				single("vault.service.default.example",          "default-X"),
				single("vault.service.MyBlank-vaultF-V", VaultMockService.class),
				single("vault.service.MyBlank-vaultF-V.example", "MyBlank-vaultF-X"),
				single("vault.service.MyBlank-vaultM-V", VaultMockService.class),
				single("vault.service.MyBlank-vaultM-V.example", "MyBlank-vaultM-X"),
				single("vault.service.AnVault-V",        VaultMockService.class),
				single("vault.service.AnVault-V.example",        "AnVault-X"),
				single("vault.service.AnVault-vaultF-V", VaultMockService.class),
				single("vault.service.AnVault-vaultF-V.example", "AnVault-vaultF-X"),
				single("vault.service.AnVault-vaultM-V", VaultMockService.class),
				single("vault.service.AnVault-vaultM-V.example", "AnVault-vaultM-X"),
				single("vault.isAppliedToAllFields", true),
				minimal()
		)));
		assertIt(MyBlank.blankF, "VaultMockService:default-X");
		assertIt(MyBlank.blankM, "VaultMockService:default-X");
		assertIt(MyBlank.vaultF, "VaultMockService:MyBlank-vaultF-X");
		assertIt(MyBlank.vaultM, "VaultMockService:MyBlank-vaultM-X");
		assertIt(AnVault.blankF, "VaultMockService:AnVault-X");
		assertIt(AnVault.blankM, "VaultMockService:AnVault-X");
		assertIt(AnVault.vaultF, "VaultMockService:AnVault-vaultF-X");
		assertIt(AnVault.vaultM, "VaultMockService:AnVault-vaultM-X");
	}
	@Test void testEnabled()
	{
		model.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.services", "MyBlank-vaultF-V MyBlank-vaultM-V AnVault-V AnVault-vaultF-V AnVault-vaultM-V"),
				single("vault.service.MyBlank-vaultF-V", VaultMockService.class),
				single("vault.service.MyBlank-vaultF-V.example", "MyBlank-vaultF-X"),
				single("vault.service.MyBlank-vaultM-V", VaultMockService.class),
				single("vault.service.MyBlank-vaultM-V.example", "MyBlank-vaultM-X"),
				single("vault.service.AnVault-V",        VaultMockService.class),
				single("vault.service.AnVault-V.example",        "AnVault-X"),
				single("vault.service.AnVault-vaultF-V", VaultMockService.class),
				single("vault.service.AnVault-vaultF-V.example", "AnVault-vaultF-X"),
				single("vault.service.AnVault-vaultM-V", VaultMockService.class),
				single("vault.service.AnVault-vaultM-V.example", "AnVault-vaultM-X"),
				minimal()
		)));
		assertIt(MyBlank.blankF);
		assertIt(MyBlank.blankM);
		assertIt(MyBlank.vaultF, "VaultMockService:MyBlank-vaultF-X");
		assertIt(MyBlank.vaultM, "VaultMockService:MyBlank-vaultM-X");
		assertIt(AnVault.blankF, "VaultMockService:AnVault-X");
		assertIt(AnVault.blankM, "VaultMockService:AnVault-X");
		assertIt(AnVault.vaultF, "VaultMockService:AnVault-vaultF-X");
		assertIt(AnVault.vaultM, "VaultMockService:AnVault-vaultM-X");
	}
	@Test void testMissingServiceAny()
	{
		final ConnectProperties props = ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.services", "MyBlank-vaultF-V MyBlank-vaultM-V"),
				single("vault.service.MyBlank-vaultF-V", VaultMockService.class),
				single("vault.service.MyBlank-vaultM-V", VaultMockService.class),
				single("vault.isAppliedToAllFields", true),
				minimal()
		));
		assertFails(
				() -> model.connect(props),
				IllegalArgumentException.class,
				"@Vault for [default, AnVault-V, AnVault-vaultF-V, AnVault-vaultM-V] " +
				"not supported by ConnectProperties.");
	}
	@Test void testMissingService()
	{
		final ConnectProperties props = ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.services", "MyBlank-vaultF-V MyBlank-vaultM-V"),
				single("vault.service.MyBlank-vaultF-V", VaultMockService.class),
				single("vault.service.MyBlank-vaultM-V", VaultMockService.class),
				minimal()
		));
		assertFails(
				() -> model.connect(props),
				IllegalArgumentException.class,
				"@Vault for [AnVault-V, AnVault-vaultF-V, AnVault-vaultM-V] " +
				"not supported by ConnectProperties.");
	}
	@Test void testAlgorithm()
	{
		model.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.algorithm", "MD5"),
				single("vault.services", "MyBlank-vaultF-V MyBlank-vaultM-V AnVault-V AnVault-vaultF-V AnVault-vaultM-V"),
				single("vault.service.MyBlank-vaultF-V", VaultMockService.class),
				single("vault.service.MyBlank-vaultM-V", VaultMockService.class),
				single("vault.service.AnVault-V",        VaultMockService.class),
				single("vault.service.AnVault-vaultF-V", VaultMockService.class),
				single("vault.service.AnVault-vaultM-V", VaultMockService.class),
				minimal()
		)));
		final DataFieldVaultInfo info = MyBlank.vaultF.getVaultInfo();
		assertNotNull(info);
		assertSame(MyBlank.vaultF, info.getField(), "field");
		assertEquals("MyBlank-vaultF-V", info.getServiceKey(), "serviceKey");
		assertEquals("VaultMockService:exampleDefault", info.getService(), "service");
		assertEquals("VARCHAR(32) not null", type(MyBlank.vaultF));
	}


	private static void assertIt(
			final DataField field)
	{
		assertNull(field.getVaultInfo());
		assertEquals("BLOB not null", type(field));
	}

	private static void assertIt(
			final DataField field,
			final String service)
	{
		final DataFieldVaultInfo actual = field.getVaultInfo();
		assertNotNull(actual);
		assertSame(field, actual.getField(), "field");
		assertEquals(service, actual.getService(), "service");
		assertEquals("VARCHAR(128) not null", type(field));
	}

	private static void assertIt(
			final Media media)
	{
		assertIt(media.getBody());
	}

	private static void assertIt(
			final Media media,
			final String service)
	{
		assertIt(media.getBody(), service);
	}

	private static String type(final DataField field)
	{
		return model.
				getSchema().
				getTable(SchemaInfo.getTableName(field.getType())).
				getColumn(SchemaInfo.getColumnName(field)).
				getType();
	}

	@AfterEach final void tearDown()
	{
		if(model.isConnected())
			model.disconnect();
	}

	static final Model model = new Model(MyBlank.TYPE, AnVault.TYPE);

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static class MyBlank extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField blankF = new DataField();

		@Vault("MyBlank-vaultF-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField vaultF = new DataField();

		@Wrapper(wrap="*", visibility=NONE)
		static final Media blankM = new Media();

		@Vault("MyBlank-vaultM-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final Media vaultM = new Media();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyBlank> TYPE = com.exedio.cope.TypesBound.newType(MyBlank.class);

		@com.exedio.cope.instrument.Generated
		protected MyBlank(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Vault("AnVault-V")
	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static class AnVault extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField blankF = new DataField();

		@Vault("AnVault-vaultF-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField vaultF = new DataField();

		@Wrapper(wrap="*", visibility=NONE)
		static final Media blankM = new Media();

		@Vault("AnVault-vaultM-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final Media vaultM = new Media();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnVault> TYPE = com.exedio.cope.TypesBound.newType(AnVault.class);

		@com.exedio.cope.instrument.Generated
		protected AnVault(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
