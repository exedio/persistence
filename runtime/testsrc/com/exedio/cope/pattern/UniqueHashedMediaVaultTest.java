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
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Vault;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.Test;

public class UniqueHashedMediaVaultTest
{
	@Test void testUniqueHashedMediaAnnotation()
	{
		final UniqueHashedMedia blob  = BlobItem .f;
		final UniqueHashedMedia vault = VaultItem.f;
		assertEquals(null,      blob .getAnnotation(Vault.class));
		assertEquals("vaultsk", vault.getAnnotation(Vault.class).value());
		assertEquals(false,     blob .isAnnotationPresent(Vault.class));
		assertEquals(true,      vault.isAnnotationPresent(Vault.class));
	}

	@Test void testMediaAnnotation()
	{
		final Media blob  = BlobItem .f.getMedia();
		final Media vault = VaultItem.f.getMedia();
		assertEquals(null,      blob .getAnnotation(Vault.class));
		assertEquals("vaultsk", vault.getAnnotation(Vault.class).value());
		assertEquals(false,     blob .isAnnotationPresent(Vault.class));
		assertEquals(true,      vault.isAnnotationPresent(Vault.class));
	}

	@Test void testDataFieldAnnotation()
	{
		final DataField blob  = BlobItem .f.getMedia().getBody();
		final DataField vault = VaultItem.f.getMedia().getBody();
		assertEquals(null,      blob .getAnnotation(Vault.class));
		assertEquals("vaultsk", vault.getAnnotation(Vault.class).value());
		assertEquals(false,     blob .isAnnotationPresent(Vault.class));
		assertEquals(true,      vault.isAnnotationPresent(Vault.class));
	}

	@Test void testDataFieldAnnotationGetter()
	{
		final DataField blob  = BlobItem .f.getMedia().getBody();
		final DataField vault = VaultItem.f.getMedia().getBody();
		assertEquals(null,      blob .getAnnotatedVaultValue());
		assertEquals("vaultsk", vault.getAnnotatedVaultValue());
		assertEquals(false,     blob .isAnnotatedVault());
		assertEquals(true,      vault.isAnnotatedVault());
	}

	@Test void testDataFieldServiceKey()
	{
		final DataField blob  = BlobItem .f.getMedia().getBody();
		final DataField vault = VaultItem.f.getMedia().getBody();

		MODEL.connect(ConnectProperties.create(Sources.cascade(
				TestSources.single("vault", true),
				TestSources.single("vault.services", "vaultsk"),
				TestSources.single("vault.service.vaultsk", VaultMockService.class),
				TestSources.minimal()
		)));
		try
		{
			assertEquals(null,      blob .getVaultServiceKey());
			assertEquals("vaultsk", vault.getVaultServiceKey());
		}
		finally
		{
			if(MODEL.isConnected())
				MODEL.disconnect();
		}
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class BlobItem extends Item
	{
		@WrapperIgnore static final UniqueHashedMedia f = new UniqueHashedMedia(new Media());

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BlobItem> TYPE = com.exedio.cope.TypesBound.newType(BlobItem.class);

		@com.exedio.cope.instrument.Generated
		private BlobItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class VaultItem extends Item
	{
		@Vault("vaultsk")
		@WrapperIgnore static final UniqueHashedMedia f = new UniqueHashedMedia(new Media());

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<VaultItem> TYPE = com.exedio.cope.TypesBound.newType(VaultItem.class);

		@com.exedio.cope.instrument.Generated
		private VaultItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(BlobItem.TYPE, VaultItem.TYPE);
}
