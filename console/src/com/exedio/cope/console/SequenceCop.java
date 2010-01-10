/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console;

import static com.exedio.cope.console.Format.format;

import java.util.List;

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.This;
import com.exedio.cope.info.SequenceInfo;

final class SequenceCop extends TestCop<SequenceInfo>
{
	SequenceCop(final Args args)
	{
		super(TAB_SEQUENCE, "sequences", args);
	}

	@Override
	protected SequenceCop newArgs(final Args args)
	{
		return new SequenceCop(args);
	}
	
	@Override
	List<SequenceInfo> getItems(final Model model)
	{
		return model.getSequenceInfo();
	}
	
	@Override
	String getCaption()
	{
		return "Sequences";
	}
	
	@Override
	String[] getHeadings()
	{
		return new String[]{"Type", "Name", "Start", "Min", "Max", "Count", "First", "Last"};
	}
	
	@Override
	void writeValue(final Out out, final SequenceInfo info, final int h)
	{
		final Feature feature = info.getFeature();
		final boolean unknown = !info.isKnown();
		switch(h)
		{
			case 0: out.write(feature.getType().getID()); break;
			case 1: out.write(feature.getName()); break;
			case 2: out.write(format(info.getStart())); break;
			case 3: out.write(format(info.getMinimum())); break;
			case 4: out.write(format(info.getMaximum())); break;
			case 5: out.write(format(info.getCount())); break;
			case 6: if(!unknown) out.write(format(info.getFirst())); break;
			case 7: if(!unknown) out.write(format(info.getLast())); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		}
	}
	
	@Override
	int test(final SequenceInfo info)
	{
		final Feature feature = info.getFeature();
		if(feature instanceof This)
			return ((This)feature).getType().checkPrimaryKey();
		else if(feature instanceof IntegerField)
			return ((IntegerField)feature).checkDefaultToNext();
		else
			return 111111111;
	}
}
