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

import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.InstrumentContext;
import com.exedio.cope.instrument.Wrap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BlockField<E extends Block> extends Pattern implements Copyable
{
	private static final long serialVersionUID = 1l;

	private final Class<E> valueClass;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final BlockType<E> valueType;
	private final LinkedHashMap<Feature, Feature> templateToComponent;
	private final HashMap<Feature, Feature> componentToTemplate;
	private final List<Feature> componentList;

	private BlockField(final BlockType<E> valueType)
	{
		this.valueClass = valueType.javaClass;

		this.valueType = valueType;

		if(!InstrumentContext.isRunning())
		{
			final LinkedHashMap<Feature, Feature> templateToComponent = new LinkedHashMap<>();
			final HashMap<Feature, Feature> componentToTemplate = new HashMap<>();

			final CopyMapper mapper = new CopyMapper();
			for(final Map.Entry<String, Feature> e : valueType.getTemplateMap().entrySet())
			{
				final Feature template = e.getValue();
				final Feature component = mapper.put(template, ((Copyable)template).copy(mapper));
				addSource(component, e.getKey(), new FeatureAnnotatedElementAdapter(template));
				templateToComponent.put(template, component);
				componentToTemplate.put(component, template);
			}
			this.templateToComponent = templateToComponent;
			this.componentToTemplate = componentToTemplate;
			this.componentList = Collections.unmodifiableList(new ArrayList<>(templateToComponent.values()));
		}
		else
		{
			this.templateToComponent = null;
			this.componentToTemplate = null;
			this.componentList = null;
		}
	}

	public static <E extends Block> BlockField<E> create(final BlockType<E> valueType)
	{
		return new BlockField<>(valueType);
	}

	@Override
	public BlockField<E> copy(final CopyMapper mapper)
	{
		return new BlockField<>(valueType);
	}

	public <X extends Feature> X of(final X template)
	{
		@SuppressWarnings("unchecked")
		final X result = (X)templateToComponent.get(template);
		if(result==null)
			throw new IllegalArgumentException(template + " is not a template of " + toString());
		return result;
	}

	public <X extends Feature> X getTemplate(final X component)
	{
		@SuppressWarnings("unchecked")
		final X result = (X)componentToTemplate.get(component);
		if(result==null)
			throw new IllegalArgumentException(component + " is not a component of " + toString());
		return result;
	}

	public List<Feature> getTemplates()
	{
		return valueType.templateList;
	}

	public List<Feature> getComponents()
	{
		return componentList;
	}

	@Wrap(order=10, name="{1}", doc="Returns the value of {0}.")
	@SuppressWarnings({"unchecked", "rawtypes"})
	public E get(final Item item)
	{
		return valueType.newValue(this, item);
	}

	public BlockType<E> getValueType()
	{
		return valueType;
	}

	public Class<E> getValueClass()
	{
		return valueClass;
	}
}
