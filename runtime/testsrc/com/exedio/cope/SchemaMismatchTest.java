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

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Node;
import com.exedio.dsmf.Node.Color;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

@WrapInterim
public abstract class SchemaMismatchTest extends TestWithEnvironment
{
	private final Model modelB;

	protected SchemaMismatchTest(final Model modelA, final Model modelB)
	{
		super(modelA);
		copeRule.omitTransaction();
		this.modelB = requireNonNull(modelB, "modelB");
	}

	@BeforeEach final void setUpSchemaMismatchTest()
	{
		modelB.connect(model.getConnectProperties());
	}

	@AfterEach final void tearDownSchemaMismatchTest()
	{
		modelB.disconnect();
	}

	protected static final String name(final Type<?> element)
	{
		return SchemaInfo.getTableName(element);
	}

	protected static final String name(final This<?> element)
	{
		return SchemaInfo.getPrimaryKeyColumnName(element.getType());
	}

	protected static final String name(final Field<?> element)
	{
		return SchemaInfo.getColumnName(element);
	}

	protected static final String name(final Sequence element)
	{
		return SchemaInfo.getSequenceName(element);
	}

	protected static final String namePk(final This<?> element)
	{
		// TODO this is guessing and works just for short names
		return element.getType() + "_PK";
	}

	protected final String name(final UniqueConstraint element)
	{
		// TODO this is guessing and works just for short names
		final String base = element.getType() + "_" + element.getName();
		return element.getFields().size()>1 ? unq(base) : base;
	}

	protected static final String nameFk(final ItemField<?> element)
	{
		// TODO this is guessing and works just for short names
		return element.getType() + "_" + element.getName() + "_Fk";
	}

	protected static final String nameUnq(final Field<?> element)
	{
		// TODO this is guessing and works just for short names
		return element.getType() + "_" + element.getName() + "_Unq";
	}

	protected static final String nameCkEnum(final BooleanField element)
	{
		// TODO this is guessing and works just for short names
		return element.getType() + "_" + element.getName() + "_EN";
	}

	protected static final String nameCkMax(final IntegerField element)
	{
		// TODO this is guessing and works just for short names
		return element.getType() + "_" + element.getName() + "_MX";
	}

	protected static final String nameSeq(final This<?> element)
	{
		return SchemaInfo.getPrimaryKeySequenceName(element.getType());
	}

	protected static final String type(final This<?> element)
	{
		return
				element.getType().getModel().connect().dialect.getIntegerType(PK.MIN_VALUE, element.getType().createLimit);
	}

	protected static final String type(final IntegerField element)
	{
		return
				element.getType().getModel().connect().dialect.getIntegerType(element.getMinimum(), element.getMaximum()) +
				(element.isMandatory() ? " not null" : "");
	}

	protected static final String type(final BooleanField element)
	{
		return
				element.getType().getModel().connect().dialect.getIntegerType(0, 1) +
				(element.isMandatory() ? " not null" : "");
	}

	protected static final String type(final StringField element)
	{
		return
				element.getType().getModel().connect().dialect.getStringType(element.getMaximumLength(), null) +
				(element.isMandatory() ? " not null" : "");
	}

	protected final String q(final String name)
	{
		return SchemaInfo.quoteName(model, name);
	}

	protected static final void assertIt(
			final String expectedError,
			final Color expectedParticularColor,
			final Color expectedCumulativeColor,
			final Node actual)
	{
		assertNotNull(actual, "notNull");
		assertEquals(expectedError, actual.getError(), "error");
		assertEquals(expectedParticularColor, actual.getParticularColor(), "particularColor");
		assertEquals(expectedCumulativeColor, actual.getCumulativeColor(), "cumulativeColor");
	}

	protected static final void assertIt(
			final String expectedError,
			final Color expectedParticularColor,
			final Color expectedCumulativeColor,
			final Constraint.Type expectedType,
			final Constraint actual)
	{
		assertIt(expectedError, expectedParticularColor, expectedCumulativeColor, actual);
		assertEquals(expectedType, actual.getType(), "type");
	}

	protected static final void assertExistance(
			final boolean expectedRequired,
			final boolean expectedExists,
			final Node node)
	{
		assertAll(
				() -> assertEquals(expectedRequired, node.required(), "required"),
				() -> assertEquals(expectedExists, node.exists(), "exists"));
	}

	protected List<Table> withTrail(
			final Schema schema,
			final Table... tables)
	{
		final List<Table> plain = Arrays.asList(tables);

		final VaultProperties vaultProperties =
				model.getConnectProperties().getVaultProperties();
		if(vaultProperties==null)
			return plain;

		// insert trailTable before the first unused table, or append to the end if there is no unused table
		final Table trailTable = schema.getTable("VaultTrail_default");
		assertNotNull(trailTable);
		final ArrayList<Table> withTrail = new ArrayList<>();
		boolean done = false;
		for(final Table tab : plain)
		{
			if(!done && !tab.required())
			{
				withTrail.add(trailTable);
				done = true;
			}
			withTrail.add(tab);
		}
		if(!done)
			withTrail.add(trailTable);

		return withTrail;
	}
}
