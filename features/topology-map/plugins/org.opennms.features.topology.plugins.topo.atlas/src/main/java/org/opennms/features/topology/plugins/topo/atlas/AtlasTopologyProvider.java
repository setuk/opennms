/*******************************************************************************
 * This file is part of OpenNMS(R).
 * <p>
 * Copyright (C) 2012-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 * http://www.gnu.org/licenses/
 * <p>
 * For more information contact:
 * OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/
 * http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.topology.plugins.topo.atlas;

import org.opennms.features.topology.api.browsers.ContentType;
import org.opennms.features.topology.api.browsers.SelectionChangedListener;
import org.opennms.features.topology.api.support.VertexHopGraphProvider;
import org.opennms.features.topology.api.topo.AbstractTopologyProvider;
import org.opennms.features.topology.api.topo.Criteria;
import org.opennms.features.topology.api.topo.GraphProvider;
import org.opennms.features.topology.api.topo.Vertex;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.features.topology.plugins.topo.atlas.vertices.DefaultAtlasVertex;
import org.opennms.features.topology.plugins.topo.atlas.vertices.ParentAtlasVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;
import java.util.List;

public class AtlasTopologyProvider extends AbstractTopologyProvider implements GraphProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AtlasTopologyProvider.class);

    public static final String TOPOLOGY_NAMESPACE = "atlas";

    private String subgraph = "regions";

    public AtlasTopologyProvider() {
        super(TOPOLOGY_NAMESPACE);
    }

    private void loadRoot() {
        final Vertex vxParent = new ParentAtlasVertex("parent", "Regions", "regions");

        final Vertex vxNorth = new DefaultAtlasVertex("north", "North", "north");
        final Vertex vxWest = new DefaultAtlasVertex("west", "West", "west");
        final Vertex vxSouth = new DefaultAtlasVertex("south", "South", "south");
        final Vertex vxEast = new DefaultAtlasVertex("east", "East", "east");

        addVertices(vxParent, vxNorth, vxSouth, vxWest, vxEast);

        connectVertices(vxParent, vxNorth);
        connectVertices(vxParent, vxWest);
        connectVertices(vxParent, vxSouth);
        connectVertices(vxParent, vxEast);
    }

    private void loadSite(final String site) {
        final Vertex vxParent = new ParentAtlasVertex("parent", site, "regions");

        final Vertex vxSite1 = new DefaultAtlasVertex("site" + site + "1", site + "1", null);
        final Vertex vxSite2 = new DefaultAtlasVertex("site" + site + "2", site + "2", null);
        final Vertex vxSite3 = new DefaultAtlasVertex("site" + site + "3", site + "3", null);

        addVertices(vxParent, vxSite1, vxSite2, vxSite3);

        connectVertices(vxParent, vxSite1);
        connectVertices(vxParent, vxSite2);
        connectVertices(vxParent, vxSite3);
    }

    private void load() {
        resetContainer();

        switch (subgraph) {
            case "north":
                loadSite("North");
                break;

            case "west":
                loadSite("West");
                break;

            case "south":
                loadSite("South");
                break;

            case "east":
                loadSite("East");
                break;

            case "regions":
            default:
                loadRoot();
                break;
        }
    }

//    private void load(final URI source, final WrappedGraph graph) {
//        String namespace = graph.m_namespace == null
//                           ? TOPOLOGY_NAMESPACE
//                           : graph.m_namespace;
//        if (getVertexNamespace() != namespace) {
//            LoggerFactory.getLogger(this.getClass()).info("Creating new vertex provider with namespace {}", namespace);
//            m_vertexProvider = new SimpleVertexProvider(namespace);
//        }
//        if (getEdgeNamespace() != namespace) {
//            LoggerFactory.getLogger(this.getClass()).info("Creating new edge provider with namespace {}", namespace);
//            m_edgeProvider = new SimpleEdgeProvider(namespace);
//        }
//        resetContainer();
//        for (WrappedVertex vertex : graph.m_vertices) {
//            if (vertex.namespace == null) {
//                vertex.namespace = getVertexNamespace();
//                LoggerFactory.getLogger(this.getClass()).warn("Setting namespace on vertex to default: {}", vertex);
//            }
//
//            if (vertex.id == null) {
//                LoggerFactory.getLogger(this.getClass()).warn("Invalid vertex unmarshalled from {}: {}", source.toString(), vertex);
//            }
//            AbstractAtlasVertex newVertex;
//            if (vertex.group) {
//                newVertex = new SimpleGroup(vertex.namespace, vertex.id);
//                if (vertex.x != null) {
//                    newVertex.setX(vertex.x);
//                }
//                if (vertex.y != null) {
//                    newVertex.setY(vertex.y);
//                }
//            } else {
//                newVertex = new SimpleLeafVertex(vertex.namespace, vertex.id, vertex.x, vertex.y);
//            }
//            newVertex.setIconKey(vertex.iconKey);
//            newVertex.setIpAddress(vertex.ipAddr);
//            newVertex.setLabel(vertex.label);
//            newVertex.setLocked(vertex.locked);
//            if (vertex.nodeID != null) {
//                newVertex.setNodeID(vertex.nodeID);
//            }
//            if (!newVertex.equals(vertex.parent)) {
//                newVertex.setParent(vertex.parent);
//            }
//            newVertex.setSelected(vertex.selected);
//            newVertex.setStyleName(vertex.styleName);
//            newVertex.setTooltipText(vertex.tooltipText);
//            addVertices(newVertex);
//        }
//
//        for (WrappedEdge edge : graph.m_edges) {
//            if (edge.namespace == null) {
//                edge.namespace = getEdgeNamespace();
//                LoggerFactory.getLogger(this.getClass()).warn("Setting namespace on edge to default: {}", edge);
//            }
//
//            if (edge.id == null || edge.source == null || edge.target == null) {
//                LoggerFactory.getLogger(this.getClass()).warn("Invalid edge unmarshalled from {}: {}", source.toString(), edge);
//            } else if (edge.id.startsWith(SIMPLE_EDGE_ID_PREFIX)) {
//                try {
//                    /*
//                     * This code will be necessary if we allow edges to be created
//
//                    // Find the highest index group number and start the index for new groups above it
//                    int edgeNumber = Integer.parseInt(edge.getId().substring(SIMPLE_EDGE_ID_PREFIX.length()));
//
//                    if (m_edgeCounter <= edgeNumber) {
//                        m_edgeCounter = edgeNumber + 1;
//                    }
//                    */
//                } catch (NumberFormatException e) {
//                    // Ignore this edge ID since it doesn't conform to our pattern for auto-generated IDs
//                }
//            }
//            AbstractEdge newEdge = connectVertices(edge.id, edge.source, edge.target, edge.namespace);
//            newEdge.setLabel(edge.label);
//            newEdge.setTooltipText(edge.tooltipText);
//            //addEdges(newEdge);
//        }
//
//        for (WrappedVertex vertex : graph.m_vertices) {
//            if (vertex.parent != null && !vertex.equals(vertex.parent)) {
//                LoggerFactory.getLogger(this.getClass()).debug("Setting parent of " + vertex + " to " + vertex.parent);
//                setParent(vertex, vertex.parent);
//            }
//        }
//    }

    @Override
    public void refresh() {
        this.load();
    }

    @Override
    public void load(final String filename) throws MalformedURLException, JAXBException {
        this.load();
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Criteria getDefaultCriteria() {
        return new VertexHopGraphProvider.DefaultVertexHopCriteria(getVertex(getVertexNamespace(), "parent"));
    }

    @Override
    public SelectionChangedListener.Selection getSelection(List<VertexRef> selectedVertices, ContentType contentType) {
        return SelectionChangedListener.Selection.NONE;
    }

    @Override
    public boolean contributesTo(ContentType type) {
        return false;
    }

    public void setSubgraph(final String subgraph) {
        this.subgraph = subgraph;

        this.load();
    }
}
