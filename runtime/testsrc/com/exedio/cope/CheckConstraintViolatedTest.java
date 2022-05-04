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

import static com.exedio.cope.CheckConstraintViolatedTest.AnItem.TYPE;
import static com.exedio.cope.CheckConstraintViolatedTest.AnItem.alpha;
import static com.exedio.cope.CheckConstraintViolatedTest.AnItem.alphaLessBeta;
import static com.exedio.cope.CheckConstraintViolatedTest.AnItem.beta;
import static com.exedio.cope.SchemaInfo.supportsCheckConstraint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.SI;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class CheckConstraintViolatedTest extends TestWithEnvironment
{
	public CheckConstraintViolatedTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Test void testIt() throws SQLException
	{
		assertEquals(true, alphaLessBeta.isSupportedBySchemaIfSupportedByDialect());
		model.checkUnsupportedConstraints();

		insert(1, 10, 20);
		model.checkUnsupportedConstraints();

		insert(2, null, 20);
		model.checkUnsupportedConstraints();

		insert(3, 10, null);
		model.checkUnsupportedConstraints();

		insert(4, null, null);
		model.checkUnsupportedConstraints();

		final boolean supported = supportsCheckConstraint(model);
		try
		{
			insert(5, 20, 10);
			assertEquals(false, supported);
		}
		catch(final SQLException ignored)
		{
			assertEquals(true, supported);
		}

		if(supported)
		{
			model.checkUnsupportedConstraints();
		}
		else
		{
			try
			{
				model.checkUnsupportedConstraints();
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("constraint violated for AnItem_alphaLessBeta on 1 tuples.", e.getMessage());
				assertEquals(RuntimeException.class, e.getClass()); // TODO nicer class
			}
		}
	}

	private void insert(final int pk, final Integer a, final Integer b) throws SQLException
	{
		connection.execute(
				"INSERT INTO " + SI.tab(TYPE) +
				"("+SI.pk(TYPE)+","+SI.col(alpha)+","+SI.col(beta)+")" +
				"VALUES" +
				"("+pk+","+sql(a)+","+sql(b)+")");
	}

	private static String sql(final Integer i)
	{
		return i!=null ? Integer.toString(i) : "NULL";
	}

	static final class AnItem extends Item
	{
		static final IntegerField alpha = new IntegerField().toFinal().optional();

		static final IntegerField beta = new IntegerField().toFinal().optional();

		static final CheckConstraint alphaLessBeta = new CheckConstraint(alpha.less(beta));

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param alpha the initial value for field {@link #alpha}.
	 * @param beta the initial value for field {@link #beta}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem(
				@javax.annotation.Nullable final java.lang.Integer alpha,
				@javax.annotation.Nullable final java.lang.Integer beta)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AnItem.alpha,alpha),
			com.exedio.cope.SetValue.map(AnItem.beta,beta),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #alpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getAlpha()
	{
		return AnItem.alpha.get(this);
	}

	/**
	 * Returns the value of {@link #beta}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getBeta()
	{
		return AnItem.beta.get(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	// WARNING: do not use for any other test
	private static final Model MODEL = new Model(TYPE);
}
