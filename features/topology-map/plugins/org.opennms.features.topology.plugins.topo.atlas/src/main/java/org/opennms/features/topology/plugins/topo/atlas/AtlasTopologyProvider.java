/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.topology.plugins.topo.atlas;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.opennms.features.topology.api.browsers.ContentType;
import org.opennms.features.topology.api.browsers.SelectionChangedListener;
import org.opennms.features.topology.api.support.VertexHopGraphProvider;
import org.opennms.features.topology.api.topo.AbstractEdge;
import org.opennms.features.topology.api.topo.AbstractTopologyProvider;
import org.opennms.features.topology.api.topo.Criteria;
import org.opennms.features.topology.api.topo.GraphProvider;
import org.opennms.features.topology.api.topo.SimpleEdgeProvider;
import org.opennms.features.topology.api.topo.SimpleVertexProvider;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.features.topology.graphml.GraphML;
import org.opennms.features.topology.graphml.GraphMLEdge;
import org.opennms.features.topology.graphml.GraphMLGraph;
import org.opennms.features.topology.graphml.GraphMLNode;
import org.opennms.features.topology.graphml.GraphMLProperties;
import org.opennms.features.topology.graphml.GraphMLReader;
import org.opennms.features.topology.graphml.InvalidGraphException;
import org.opennms.features.topology.plugins.topo.atlas.criteria.AtlasSubGraphCriteria;
import org.opennms.features.topology.plugins.topo.atlas.vertices.DefaultAtlasVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlasTopologyProvider extends AbstractTopologyProvider implements GraphProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AtlasTopologyProvider.class);

    public static final String TOPOLOGY_NAMESPACE = "atlas";

    private String defaultSubGraphId;


//    private String filename;

    // TODO MVR the namespace should be defined by the input file ...
    public AtlasTopologyProvider() {
        super(TOPOLOGY_NAMESPACE);
    }

    @Override
    public void refresh() {
        try {
            this.load(null);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void resetContainer() {
        super.resetContainer();
        defaultSubGraphId = null;
    }

    @Override
    public void load(final String filename) throws MalformedURLException, JAXBException {
        resetContainer();
        try (InputStream input = new FileInputStream("/Users/mvrueden/Desktop/test.graphml")) {
            GraphML graphML = GraphMLReader.read(input);
            defaultSubGraphId = graphML.getProperty("defaultSubGraph");
            final String namespace = graphML.getNamespace();
            if (!getVertexNamespace().equals(namespace)) {
                LoggerFactory.getLogger(this.getClass()).info("Creating new vertex provider with namespace {}", namespace);
                m_vertexProvider = new SimpleVertexProvider(namespace);
            }
            if (!getEdgeNamespace().equals(namespace)) {
                LoggerFactory.getLogger(this.getClass()).info("Creating new edge provider with namespace {}", namespace);
                m_edgeProvider = new SimpleEdgeProvider(namespace);
            }

            // Add all Nodes to container
            for (GraphMLGraph eachGraph : graphML.getGraphs()) {
                for (GraphMLNode vertex : eachGraph.getNodes()) {
                    DefaultAtlasVertex newVertex = new DefaultAtlasVertex(
                            vertex.getNamespace(),
                            vertex.getId(),
                            vertex.getLabel(),
                            eachGraph.getId(),
                            vertex.getProperty(GraphMLProperties.GRAPH_LINK));
                    newVertex.setIconKey(vertex.getIconKey());
                    newVertex.setIpAddress(vertex.getIpAddr());
                    newVertex.setLabel(vertex.getLabel());
                    newVertex.setLocked(vertex.isLocked());
                    if (vertex.getNodeID() != null) newVertex.setNodeID(vertex.getNodeID());
                    newVertex.setSelected(vertex.isSelected());
                    newVertex.setStyleName(vertex.getStyleName());
                    newVertex.setTooltipText(vertex.getTooltipText());
                    newVertex.setProperties(vertex.getProperties());
                    addVertices(newVertex);
                }
            }

            // Add all Edges to container
            for (GraphMLGraph eachGraph : graphML.getGraphs()) {
                for (GraphMLEdge edge : eachGraph.getEdges()) {
                    AbstractEdge newEdge = connectVertices(edge.getId(), edge.getSource(), edge.getTarget(), edge.getNamespace());
                    newEdge.setLabel(edge.getLabel());
                    newEdge.setTooltipText(edge.getTooltipText());
                }
            }
        } catch (InvalidGraphException | IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private AbstractEdge connectVertices(String id, GraphMLNode source, GraphMLNode target, String namespace) {
        DefaultAtlasVertex sourceVertex = (DefaultAtlasVertex) getVertex(source.getNamespace(), source.getId());
        DefaultAtlasVertex targetVertex = (DefaultAtlasVertex) getVertex(target.getNamespace(), target.getId());
        return connectVertices(id, sourceVertex, targetVertex, namespace);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Criteria> getDefaultCriteria() {
        return createDefaultCriteriaSet(this, defaultSubGraphId);
    }

    @Override
    public SelectionChangedListener.Selection getSelection(List<VertexRef> selectedVertices, ContentType contentType) {
        return SelectionChangedListener.Selection.NONE;
    }

    @Override
    public boolean contributesTo(ContentType type) {
        return false;
    }

    public String getDefaultSubGraphId() {
        return defaultSubGraphId;
    }

    public static Set<Criteria> createDefaultCriteriaSet(GraphProvider graphProvider, String subgraphId) {
        Set<Criteria> criteriaSet = new HashSet<>();

        if (subgraphId != null) {
            criteriaSet.add(new AtlasSubGraphCriteria(graphProvider.getVertexNamespace(), subgraphId));
        }
        List<DefaultAtlasVertex> vertices = graphProvider.getVertices().stream()
                .map(v -> (DefaultAtlasVertex) v)
                .filter(v -> Objects.equals(subgraphId, v.getSubGraphId()))
                .collect(Collectors.toList());
        if (!vertices.isEmpty()) {
            for (DefaultAtlasVertex eachVertex : vertices) {
                criteriaSet.add(new VertexHopGraphProvider.DefaultVertexHopCriteria(eachVertex));
            }
        }
        return criteriaSet;
    }
}
