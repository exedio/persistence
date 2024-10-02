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

import static com.exedio.cope.DataField.toValue;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.util.Hex.decodeLower;
import static com.exedio.cope.util.Hex.encodeLower;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.SI;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultProperties;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class StartsWithConditionJoinTest extends TestWithEnvironment
{
	StartsWithConditionJoinTest()
	{
		super(model);
	}


	@Test void testSingleField()
	{
		final MyItem a = new MyItem("33aa00", "000000", "000000");
		final MyItem n = new MyItem("000000", "000000", "000000");
		final Query<MyItem> queryPos = MyItem.TYPE.newQuery(MyItem.alpha.startsWithIfSupported(decodeLower("33aa")));
		final Query<MyItem> queryNeg = MyItem.TYPE.newQuery(MyItem.alpha.startsWithIfSupported(decodeLower("33aa")).not());
		assertEquals(
				"SELECT " + SI.pk(MyItem.TYPE) + " FROM " + SI.tab(MyItem.TYPE) + " " +
				ifVault("JOIN " + q("VaultTrail_default") + " " + q("MyItem.alpha") + " ON " + SI.col(MyItem.alpha) + "=" + q("MyItem.alpha") + "." + q("hash") + " ") +
				"WHERE " + startsWith(MyItem.alpha, "33aa", "MyItem.alpha"),
				SchemaInfo.search(queryPos));
		assertEquals(
				"SELECT " + SI.pk(MyItem.TYPE) + " FROM " + SI.tab(MyItem.TYPE) + " " +
				ifVault("JOIN " + q("VaultTrail_default") + " " + q("MyItem.alpha") + " ON " + SI.col(MyItem.alpha) + "=" + q("MyItem.alpha") + "." + q("hash") + " ") +
				"WHERE NOT (" + startsWith(MyItem.alpha, "33aa", "MyItem.alpha") + ")",
				SchemaInfo.search(queryNeg));
		assertEquals(List.of(a), queryPos.search());
		assertEquals(List.of(n), queryNeg.search());
	}

	@Test void testSameField() throws SQLException
	{
		final MyItem a = new MyItem("33aa00", "000000", "000000");
		final MyItem b = new MyItem("33bb00", "000000", "000000");
		new MyItem("000000", "000000", "000000");
		final Query<MyItem> query = MyItem.TYPE.newQuery(
				MyItem.alpha.startsWithIfSupported(decodeLower("33aa")).or(
				MyItem.alpha.startsWithIfSupported(decodeLower("33bb"))));
		assertEquals(
				"SELECT " + SI.pk(MyItem.TYPE) + " FROM " + SI.tab(MyItem.TYPE) + " " +
				ifVault("JOIN " + q("VaultTrail_default") + " " + q("MyItem.alpha") + " ON " + SI.col(MyItem.alpha) + "=" + q("MyItem.alpha") + "." + q("hash") + " ") +
				"WHERE (" +
				startsWith(MyItem.alpha, "33aa", "MyItem.alpha") + ") OR (" +
				startsWith(MyItem.alpha, "33bb", "MyItem.alpha") + ")",
				SchemaInfo.search(query));
		query.setOrderByThis(true);
		restartTransactionForVaultTrail(MyItem.alpha);
		assertEquals(List.of(a, b), query.search());

		// test incomplete trail
		final VaultProperties vp = model.getConnectProperties().vault;
		if(vp!=null)
		{
			model.commit();
			final String hashA = encodeLower(vp.bucket("default").getAlgorithmFactory().digest(decodeLower("33aa00")));
			try(Connection con = SchemaInfo.newConnection(model);
				 Statement stmt = con.createStatement())
			{
				//noinspection SqlSourceToSinkFlow
				assertEquals(1, stmt.executeUpdate(
						"DELETE FROM " + q("VaultTrail_default") + " " +
						"WHERE " + q("hash") + "='" + hashA + "'"));
			}
			model.clearCache();
			model.startTransaction(StartsWithConditionJoinTest.class.getName());
			assertEquals(List.of(b), query.search());
		}
	}

	@Test void testDifferentField()
	{
		final MyItem a = new MyItem("33aa00", "000000", "000000");
		final MyItem b = new MyItem("000000", "33bb00", "000000");
		new MyItem("000000", "000000", "000000");
		final Query<MyItem> query = MyItem.TYPE.newQuery(
				MyItem.alpha.startsWithIfSupported(decodeLower("33aa")).or(
				MyItem.beta .startsWithIfSupported(decodeLower("33bb"))));
		assertEquals(
				"SELECT " + SI.pk(MyItem.TYPE) + " FROM " + SI.tab(MyItem.TYPE) + " " +
				ifVault("JOIN " + q("VaultTrail_default") + " " + q("MyItem.alpha") + " ON " + SI.col(MyItem.alpha) + "=" + q("MyItem.alpha") + "." + q("hash") + " ") +
				ifVault("JOIN " + q("VaultTrail_default") + " " + q("MyItem.beta" ) + " ON " + SI.col(MyItem.beta ) + "=" + q("MyItem.beta" ) + "." + q("hash") + " ") +
				"WHERE (" +
				startsWith(MyItem.alpha, "33aa", "MyItem.alpha") + ") OR (" +
				startsWith(MyItem.beta , "33bb", "MyItem.beta" ) + ")",
				SchemaInfo.search(query));
		query.setOrderByThis(true);
		restartTransactionForVaultTrail(MyItem.alpha);
		assertEquals(List.of(a, b), query.search());
	}

	@Test
	void testDifferentBucket()
	{
		final MyItem a = new MyItem("33aa00", "000000", "000000");
		final MyItem b = new MyItem("000000", "000000", "33bb00");
		new MyItem("000000", "000000", "000000");
		final Query<MyItem> query = MyItem.TYPE.newQuery(
				MyItem.alpha.startsWithIfSupported(decodeLower("33aa")).or(
				MyItem.jota .startsWithIfSupported(decodeLower("33bb"))));
		assertEquals(
				"SELECT " + SI.pk(MyItem.TYPE) + " FROM " + SI.tab(MyItem.TYPE) + " " +
				ifVault("JOIN " + q("VaultTrail_default") + " " + q("MyItem.alpha") + " ON " + SI.col(MyItem.alpha) + "=" + q("MyItem.alpha") + "." + q("hash") + " ") +
				ifVault("JOIN " + q("VaultTrail_jo_ta"  ) + " " + q("MyItem.jota" ) + " ON " + SI.col(MyItem.jota ) + "=" + q("MyItem.jota" ) + "." + q("hash") + " ") +
				"WHERE (" +
				startsWith(MyItem.alpha, "33aa", "MyItem.alpha") + ") OR (" +
				startsWith(MyItem.jota,  "33bb", "MyItem.jota" ) + ")",
				SchemaInfo.search(query));
		query.setOrderByThis(true);
		restartTransactionForVaultTrail(MyItem.alpha);
		assertEquals(List.of(a, b), query.search());
	}

	@Test
	void testLimit()
	{
		final VaultProperties vp = model.getConnectProperties().vault;
		if(vp!=null)
			assertEquals(20, vp.bucket(Vault.DEFAULT).getTrailStartLimit());

		final MyItem a = new MyItem(
				"0011223344556677889900112233445566778899aa", "000000", "000000");
		new MyItem(
				"00112233445566778899001122334455667788ffaa", "000000", "000000");
		new MyItem("000000", "000000", "000000");
		final Query<MyItem> query = MyItem.TYPE.newQuery(
				MyItem.alpha.startsWithIfSupported(   decodeLower("0011223344556677889900112233445566778899")));
		final Query<MyItem> queryExceeds = MyItem.TYPE.newQuery(
				MyItem.alpha.startsWithIfSupported(   decodeLower("0011223344556677889900112233445566778899aa")));
		final Query<MyItem> queryOffset = MyItem.TYPE.newQuery(
				MyItem.alpha.startsWithIfSupported(5, decodeLower(          "556677889900112233445566778899")));
		final Query<MyItem> queryOffsetExceeds = MyItem.TYPE.newQuery(
				MyItem.alpha.startsWithIfSupported(5, decodeLower(          "556677889900112233445566778899aa")));
		assertEquals(
				"SELECT " + SI.pk(MyItem.TYPE) + " FROM " + SI.tab(MyItem.TYPE) + " " +
				ifVault("JOIN " + q("VaultTrail_default") + " " + q("MyItem.alpha") + " ON " + SI.col(MyItem.alpha) + "=" + q("MyItem.alpha") + "." + q("hash") + " ") +
				"WHERE " +
				startsWith(MyItem.alpha, "0011223344556677889900112233445566778899", "MyItem.alpha"),
				SchemaInfo.search(query));
		if(vp==null)
		{
			assertEquals(
					"SELECT " + SI.pk(MyItem.TYPE) + " FROM " + SI.tab(MyItem.TYPE) + " " +
					"WHERE " +
					startsWith(MyItem.alpha, "0011223344556677889900112233445566778899aa", "MyItem.alpha"),
					SchemaInfo.search(queryExceeds));
		}
		else
		{
			assertFails(
					() -> SchemaInfo.search(queryExceeds),
					UnsupportedQueryException.class,
					"DataField MyItem.alpha does not support startsWith as it has vault enabled, " +
					"trail supports up to 20 bytes only but condition requires 21 bytes");
			assertFails(
					() -> SchemaInfo.search(queryOffsetExceeds),
					UnsupportedQueryException.class,
					"DataField MyItem.alpha does not support startsWith as it has vault enabled, " +
					"trail supports up to 20 bytes only but condition requires 21 bytes (offset 5 plus value 16)");
		}
		assertEquals(List.of(a), query.search());
		assertEquals(List.of(a), queryOffset.search());
		if(vp==null)
		{
			assertEquals(List.of(a), queryExceeds.search());
			assertEquals(List.of(a), queryOffsetExceeds.search());
		}
		else
		{
			assertFails(
					queryExceeds::search,
					UnsupportedQueryException.class,
					"DataField MyItem.alpha does not support startsWith as it has vault enabled, " +
					"trail supports up to 20 bytes only but condition requires 21 bytes");
			assertFails(
					queryOffsetExceeds::search,
					UnsupportedQueryException.class,
					"DataField MyItem.alpha does not support startsWith as it has vault enabled, " +
					"trail supports up to 20 bytes only but condition requires 21 bytes (offset 5 plus value 16)");
		}
	}


	private static String ifVault(final String s)
	{
		return MyItem.alpha.getVaultBucket()!=null ? s : "";
	}

	private String startsWith(final DataField field, final String val, final String alias)
	{
		final String bucket = field.getVaultBucket();
		final String col = bucket==null ? SI.col(field) : (q(alias) + "." + q("start20"));
		return switch(dialect)
		{
			case hsqldb     -> "LEFT(RAWTOHEX(" + col + "),"    + val.length()   + ")='" + val + "'";
			case mysql      -> "LEFT("          + col + ","     + val.length()/2 + ")=x'" + val + "'";
			case postgresql -> "SUBSTRING("     + col + " FOR " + val.length()/2 + ")=E'\\\\x" + val + "'";
		};
	}

	@Override
	public Properties.Source override(final Properties.Source s)
	{
		if(!"true".equals(s.get("vault")))
			return s;

		assertEquals(null, s.get("vault.buckets")); // thus defaults to "default"
		final String service = s.get("vault.default.service");
		assertNotNull(service);
		return Sources.cascade(
				TestSources.single("vault.buckets", "default jo-ta"),
				TestSources.single("vault.default.service", service),
				TestSources.single("vault.jo-ta.service", service),
				s);
	}

	private static final Model model = new Model(MyItem.TYPE);

	@WrapperType(indent=2, comments=false)
	private static class MyItem extends Item
	{
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		static final DataField alpha = new DataField().toFinal();

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		static final DataField beta = new DataField().toFinal();

		@Vault("jo-ta")
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		static final DataField jota = new DataField().toFinal();

		private MyItem(
				@Nonnull final String alpha,
				@Nonnull final String beta,
				@Nonnull final String jota)
				throws MandatoryViolationException
		{
			this(
					toValue(decodeLower(alpha)),
					toValue(decodeLower(beta)),
					toValue(decodeLower(jota))
			);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value alpha,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value beta,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value jota)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.alpha,alpha),
				com.exedio.cope.SetValue.map(MyItem.beta,beta),
				com.exedio.cope.SetValue.map(MyItem.jota,jota),
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

	private static String q(final String s)
	{
		return SchemaInfo.quoteName(model, s);
	}
}
