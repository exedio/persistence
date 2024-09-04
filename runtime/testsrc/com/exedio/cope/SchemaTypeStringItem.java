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

import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=NONE, genericConstructor=NONE)
public final class SchemaTypeStringItem extends Item
{
	static final SchemaTypeStringField f1        = new SchemaTypeStringField(      1);
	static final SchemaTypeStringField f2        = new SchemaTypeStringField(      2);
	static final SchemaTypeStringField f85       = new SchemaTypeStringField(     85);
	static final SchemaTypeStringField f86       = new SchemaTypeStringField(     86);
	static final SchemaTypeStringField f21845    = new SchemaTypeStringField(  21845);
	static final SchemaTypeStringField f21846    = new SchemaTypeStringField(  21846);
	static final SchemaTypeStringField f5592405  = new SchemaTypeStringField(5592405);
	static final SchemaTypeStringField f5592406  = new SchemaTypeStringField(5592406);
	static final SchemaTypeStringField f10485760 = new SchemaTypeStringField(10485760);
	static final SchemaTypeStringField f10485761 = new SchemaTypeStringField(10485761);
	static final SchemaTypeStringField fMax      = new SchemaTypeStringField(Integer.MAX_VALUE);

	@MysqlExtendedVarchar static final SchemaTypeStringField f85Ext    = new SchemaTypeStringField(   85);
	@MysqlExtendedVarchar static final SchemaTypeStringField f86Ext    = new SchemaTypeStringField(   86);
	@MysqlExtendedVarchar static final SchemaTypeStringField f16382Ext = new SchemaTypeStringField(16382);
	@MysqlExtendedVarchar static final SchemaTypeStringField f16383Ext = new SchemaTypeStringField(16383);
	@MysqlExtendedVarchar static final SchemaTypeStringField f20845Ext = new SchemaTypeStringField(20845);
	@MysqlExtendedVarchar static final SchemaTypeStringField f20846Ext = new SchemaTypeStringField(21846);


	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for schemaTypeStringItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<SchemaTypeStringItem> TYPE = com.exedio.cope.TypesBound.newType(SchemaTypeStringItem.class,SchemaTypeStringItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private SchemaTypeStringItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
