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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Features;
import com.exedio.cope.FunctionField;
import com.exedio.cope.ItemField;
import com.exedio.cope.misc.Arrays;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class CopyFields implements Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	static final CopyFields EMPTY = new CopyFields(null);

	private final FunctionField<?>[] fields;
	private Map<FunctionField<?>,FunctionField<?>> copies;

	private CopyFields(final FunctionField<?>[] fields)
	{
		this.fields = fields;
	}

	CopyFields copy()
	{
		// the EMPTY instance is non-modifyable, so it doesn't need to be copied
		return this==EMPTY ? EMPTY : new CopyFields(fields);
	}

	CopyFields add(final FunctionField<?> add)
	{
		requireNonNull(add, "add");
		if(fields==null)
			return new CopyFields(new FunctionField<?>[]{add});

		for (final FunctionField<?> field : fields)
		{
			if (add.equals(field))
				throw new IllegalArgumentException("added '"+add+"' twice");
		}
		return new CopyFields(Arrays.append(fields, add));
	}

	static void onMountAll(final Features features, final ItemField<?> parent, final FunctionField<?>[] elements, final CopyFields[] copyFields)
	{
		assert elements.length==copyFields.length;
		final Map<FunctionField<?>,FunctionField<?>> allCopies = new LinkedHashMap<>();
		for (int i=0; i<elements.length; i++)
		{
			copyFields[i].contributeCopies(allCopies, parent, elements[i]);
		}
		for (int i=0; i<elements.length; i++)
		{
			copyFields[i].storeCopies(Collections.unmodifiableMap(allCopies));
		}
		for (final Map.Entry<FunctionField<?>, FunctionField<?>> entry : allCopies.entrySet())
		{
			features.put(entry.getKey().getName(), entry.getValue(), new FeatureAnnotatedElementAdapter(entry.getKey()));
		}
	}

	private void contributeCopies(final Map<FunctionField<?>,FunctionField<?>> allCopies, final ItemField<?> parent, final FunctionField<?> element)
	{
		assert copies==null;
		if (fields!=null)
		{
			for (final FunctionField<?> template: fields)
			{
				FunctionField<?> copy = allCopies.get(template);
				if (copy==null)
				{
					copy = template.copy().noCopyFrom().nonUnique().noDefault().copyFrom(parent);
				}
				allCopies.put(template, copy.copyFrom((ItemField<?>)element));
			}
		}
	}

	private void storeCopies(final Map<FunctionField<?>,FunctionField<?>> allCopies)
	{
		assert copies==null;
		if (fields!=null)
		{
			copies = new HashMap<>();
			for (final FunctionField<?> template: fields)
			{
				copies.put(template, requireNonNull(allCopies.get(template)));
			}
		}
	}

	void onMount(final Features features, final ItemField<?> parent, final FunctionField<?> element)
	{
		onMountAll(features, parent, new FunctionField<?>[]{element}, new CopyFields[]{this});
	}

	List<FunctionField<?>> getTemplates()
	{
		return fields==null ? List.of() : List.of(fields);
	}

	<T> FunctionField<T> getCopyField(final FunctionField<T> template)
	{
		if (copies==null)
		{
			throw new IllegalStateException("no CopyConstraints declared");
		}
		final FunctionField<?> copy = copies.get(template);
		if (copy==null)
		{
			throw new IllegalStateException("no copy for "+template);
		}
		@SuppressWarnings("unchecked")
		final FunctionField<T> result = (FunctionField<T>)copy;
		return result;
	}

	void failIfNotEmpty()
	{
		if (fields!=null)
			throw new IllegalStateException("cannot copy if copyWith is set");
	}
}
