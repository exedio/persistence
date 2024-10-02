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

import static com.exedio.cope.DataItem.TYPE;
import static com.exedio.cope.DataItem.data;
import static com.exedio.cope.RuntimeAssert.assertCondition;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.SI;
import com.exedio.cope.util.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StartsWithConditionTest extends TestWithEnvironment
{
	public StartsWithConditionTest()
	{
		super(DataModelTest.MODEL);
	}

	private DataItem item0, item4, item4o3, item6, item6o4, item6x4, item6x4o4;

	@BeforeEach final void setUp()
	{
		item0 = new DataItem();
		item4 = new DataItem();
		item4o3 = new DataItem();
		item6 = new DataItem();
		item6o4 = new DataItem();
		item6x4 = new DataItem();
		item6x4o4 = new DataItem();
		new DataItem(); // is null
		item0.setData(bytes0);
		item4.setData(bytes4);
		item4o3.setData(concat( new byte[]{0,0,0}, bytes4));
		item6.setData(bytes6);
		item6o4.setData(concat( new byte[]{0,0,0,0}, bytes6));
		item6x4.setData(bytes6x4);
		item6x4o4.setData(concat( new byte[]{0,0,0,0}, bytes6x4));
	}

	@Test void testCondition()
	{
		final boolean vault = data.getVaultBucket()!=null;
		final String trunk =
				"SELECT " + SI.pk(TYPE) + "," + SI.type(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + (
				vault
				? "JOIN " + q("VaultTrail_default") + " " + q("DataItem.data") + " " +
				  "ON " + SI.col(data) + "=" + q("DataItem.data") + "." + q("hash") + " "
				: "");
		final String dataColumn = vault
				? q("DataItem.data") + "." + q("start20")
				: SI.col(data);
		final String expression0;
		final String expression3;
		switch(dialect)
		{
			case hsqldb ->
			{
				expression0 = "LEFT(RAWTOHEX("   + dataColumn +   "),8)='aa7af817'";
				expression3 = "SUBSTR(RAWTOHEX(" + dataColumn + "),7,8)='aa7af817'";
			}
			case mysql ->
			{
				expression0 = "LEFT("      + dataColumn +   ",4)=x'aa7af817'";
				expression3 = "SUBSTRING(" + dataColumn + ",4,4)=x'aa7af817'";
			}
			case postgresql ->
			{
				expression0 = "SUBSTRING(" + dataColumn +        " FOR 4)=E'\\\\xaa7af817'";
				expression3 = "SUBSTRING(" + dataColumn + " FROM 4 FOR 4)=E'\\\\xaa7af817'";
			}
			default ->
				throw new RuntimeException(dialect.name());
		}
		assertEquals(
				trunk + "WHERE " + expression0,
				SchemaInfo.search(TYPE.newQuery(data.startsWithIfSupported(bytes4))));
		assertEquals(
				trunk + "WHERE " + expression3,
				SchemaInfo.search(TYPE.newQuery(data.startsWithIfSupported(3, bytes4))));

		assertCondition(item4, TYPE, data.startsWithIfSupported(bytes4));
		assertCondition(item4o3, TYPE, data.startsWithIfSupported(3, bytes4));
		assertCondition(item6, TYPE, data.startsWithIfSupported(bytes6));
		assertCondition(item6o4, TYPE, data.startsWithIfSupported(4, bytes6));
		assertCondition(item6, item6x4, TYPE, data.startsWithIfSupported(bytes6x4));
		assertCondition(item6o4, item6x4o4, TYPE, data.startsWithIfSupported(4, bytes6x4));
	}

	@Test void testNot()
	{
		restartTransactionForVaultTrail(data);

		assertCondition(asList(item0,        item4o3, item6, item6o4, item6x4, item6x4o4), TYPE, data.startsWithIfSupported(   bytes4).not(), data.startsWithIfSupported(   bytes4));
		assertCondition(asList(item0, item4,          item6, item6o4, item6x4, item6x4o4), TYPE, data.startsWithIfSupported(3, bytes4).not(), data.startsWithIfSupported(3, bytes4));
		assertCondition(asList(item0, item4, item4o3,        item6o4, item6x4, item6x4o4), TYPE, data.startsWithIfSupported(   bytes6).not(), data.startsWithIfSupported(   bytes6));
		assertCondition(asList(item0, item4, item4o3, item6,          item6x4, item6x4o4), TYPE, data.startsWithIfSupported(4, bytes6).not(), data.startsWithIfSupported(4, bytes6));
		assertCondition(asList(item0, item4, item4o3,        item6o4,          item6x4o4), TYPE, data.startsWithIfSupported(   bytes6x4).not(), data.startsWithIfSupported(   bytes6x4));
		assertCondition(asList(item0, item4, item4o3, item6,          item6x4           ), TYPE, data.startsWithIfSupported(4, bytes6x4).not(), data.startsWithIfSupported(4, bytes6x4));
	}


	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes6x4= {-97,35,-126,86};

	private static byte[] concat(final byte[] bytes1, final byte[] bytes2)
	{
		return Hex.decodeLower( Hex.encodeLower(bytes1) + Hex.encodeLower(bytes2) );
	}

	private String q(final String s)
	{
		return SchemaInfo.quoteName(model, s);
	}
}
