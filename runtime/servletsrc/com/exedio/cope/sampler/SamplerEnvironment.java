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

package com.exedio.cope.sampler;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import java.io.Serial;

final class SamplerEnvironment extends Item
{
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final DateField connectDate    = new DateField().toFinal().unique();
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final DateField initializeDate = new DateField().toFinal();
	@SuppressWarnings("unused")
	private static final DateField sampleDate     = new DateField().toFinal().defaultToNow();


	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final StringField hostname = new StringField().toFinal().optional().lengthMax(1000);


	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final StringField connectionUrl      = new StringField().toFinal().lengthMax(1000);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final StringField connectionUsername = new StringField().toFinal().lengthMax(1000);


	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final  StringField databaseProductName    = new StringField ().toFinal().lengthMax(1000);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final  StringField databaseProductVersion = new StringField ().toFinal().lengthMax(1000);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField databaseVersionMajor   = new IntegerField().toFinal();
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField databaseVersionMinor   = new IntegerField().toFinal();
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final  StringField driverName             = new StringField ().toFinal().lengthMax(1000);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final  StringField driverVersion          = new StringField ().toFinal().lengthMax(1000);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField driverVersionMajor     = new IntegerField().toFinal();
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField driverVersionMinor     = new IntegerField().toFinal();


	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final StringField buildTag = new StringField().toFinal().optional().lengthMax(1000);


	private SamplerEnvironment(final ActivationParameters ap){ super(ap); }
	@Serial
	private static final long serialVersionUID = 1l;
	static final Type<SamplerEnvironment> TYPE = TypesBound.newType(SamplerEnvironment.class, SamplerEnvironment::new);
}
