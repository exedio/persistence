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

package com.exedio.dsmf;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Graph
{
	private static final Logger logger = LoggerFactory.getLogger(Graph.class);
	private static final Edge[] EMPTY_EDGE_ARRAY = new Edge[0];

	private final ArrayList<Node> nodesOrdered;
	private final LinkedHashSet<Edge> edgesBroken;

	Graph(final Schema schema)
	{
		final List<Edge> allEdges;
		final List<Node> allNodes;
		{
			final HashMap<String, Node> nodeByTableName = new HashMap<>();
			final ArrayList<Edge> allEdgesM = new ArrayList<>();
			final ArrayList<Node> allNodesM = new ArrayList<>();
			for(final Table table : schema.getTables())
			{
				final ArrayList<Edge> edges = new ArrayList<>();
				for(final Constraint constraint : table.getConstraints())
					if(constraint instanceof final ForeignKeyConstraint fk &&
						!table.name.equals(fk.getTargetTable())) // self references are ignored completely
					{
						edges.add(new Edge(fk));
					}
				allEdgesM.addAll(edges);

				final Node node = new Node(table, edges.toArray(EMPTY_EDGE_ARRAY));
				allNodesM.add(node);
				if(nodeByTableName.putIfAbsent(table.name, node)!=null)
					throw new RuntimeException(table.name);
			}

			for(final Edge edge : allEdgesM)
			{
				final Node node = nodeByTableName.get(edge.constraint.getTargetTable());
				if(node==null)
					throw new RuntimeException(edge.constraint.getTargetTable());
				edge.setNode(node);
			}
			allEdges = Collections.unmodifiableList(allEdgesM);
			allNodes = Collections.unmodifiableList(allNodesM);
		}

		nodesOrdered = new ArrayList<>();
		edgesBroken = new LinkedHashSet<>();

		{
			final LinkedList<Node> allNodesRest = new LinkedList<>(allNodes);
			final LinkedHashSet<Edge> allEdgesRest = new LinkedHashSet<>(allEdges);
			do
			{
				boolean wasPossible;
				do
				{
					wasPossible = false;
					for(final Iterator<Node> iterator = allNodesRest.iterator(); iterator.hasNext(); )
					{
						final Node node = iterator.next();
						if(node.isPossible(edgesBroken))
						{
							nodesOrdered.add(node);
							node.markAsDone();

							// Do not use allEdgesRest.removeAll(node.getEdges()) as it can be slow when node.getEdges().size()
							// is greater than or equal to allEdgesRest.size(). In this case, node.getEdges().contains()
							// is called for each element in "allEdgesRest", which will perform a linear search.
							// Found by idea inspection Call to 'set.removeAll(list)' may work slowly.
							node.getEdges().forEach(allEdgesRest::remove);

							wasPossible = true;
							iterator.remove();
						}
					}
				}
				while(wasPossible);

				if(allNodesRest.isEmpty())
				{
					break;
				}
				else
				{
					final Edge brokenEdge = allEdgesRest.iterator().next();
					allEdgesRest.remove(brokenEdge);
					edgesBroken.add(brokenEdge);
				}
			}
			while(true);
		}

		if(logger.isInfoEnabled())
			logger.info(MessageFormat.format(
					"broken {0} of {1} edges for cycle-free subgraph",
					edgesBroken.size(),
					allEdges.size()));
	}

	List<Table> getTablesOrdered()
	{
		final ArrayList<Table> result = new ArrayList<>(nodesOrdered.size());
		for(final Node node : nodesOrdered)
			result.add(node.table);
		return Collections.unmodifiableList(result);
	}

	Set<ForeignKeyConstraint> getConstraintsBroken()
	{
		final LinkedHashSet<ForeignKeyConstraint> result = new LinkedHashSet<>();
		for(final Edge edge : edgesBroken)
			result.add(edge.constraint);
		return Collections.unmodifiableSet(result);
	}

	static final class Node
	{
		final Table table;
		private final Edge[] edges;
		private boolean done = false;

		@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
		Node(final Table table, final Edge[] edges)
		{
			this.table = table;
			this.edges = edges;
		}

		List<Edge> getEdges()
		{
			return List.of(edges);
		}

		boolean isPossible(final HashSet<Edge> brokenEdges)
		{
			for(final Edge edge : edges)
			{
				if(brokenEdges.contains(edge))
					continue;

				final Node node = edge.getNode();
				assert node!=this; // self references are ignored before
				if(!node.isDone())
					return false;
			}
			return true;
		}

		boolean isDone()
		{
			return done;
		}

		void markAsDone()
		{
			assert !done;
			done = true;
		}

		@Override
		public String toString()
		{
			return table.name;
		}
	}

	static final class Edge
	{
		final ForeignKeyConstraint constraint;
		private Node node = null;

		Edge(final ForeignKeyConstraint constraint)
		{
			this.constraint = constraint;
		}

		Node getNode()
		{
			assert node!=null;
			return node;
		}

		void setNode(final Node node)
		{
			assert node!=null;
			assert this.node==null;
			this.node = node;
		}

		@Override
		public String toString()
		{
			return constraint.name;
		}
	}
}
