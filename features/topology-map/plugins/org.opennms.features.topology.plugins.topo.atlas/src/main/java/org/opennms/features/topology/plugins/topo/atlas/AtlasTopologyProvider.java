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

import java.net.MalformedURLException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.opennms.features.topology.api.browsers.ContentType;
import org.opennms.features.topology.api.browsers.SelectionChangedListener;
import org.opennms.features.topology.api.topo.AbstractTopologyProvider;
import org.opennms.features.topology.api.topo.Criteria;
import org.opennms.features.topology.api.topo.GraphProvider;
import org.opennms.features.topology.api.topo.Vertex;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.features.topology.plugins.topo.atlas.criteria.AtlasSubGraphCriteria;
import org.opennms.features.topology.plugins.topo.atlas.vertices.DefaultAtlasVertex;
import org.opennms.features.topology.plugins.topo.atlas.vertices.ParentAtlasVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlasTopologyProvider extends AbstractTopologyProvider implements GraphProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AtlasTopologyProvider.class);

    public static final String TOPOLOGY_NAMESPACE = "atlas";

    public AtlasTopologyProvider() {
        super(TOPOLOGY_NAMESPACE);
    }

    private void loadRoot() {
        final Vertex vxParent = new ParentAtlasVertex("regions", "Regions", "regions-subgraph", null);

        final Vertex vxNorth = new DefaultAtlasVertex("north", "North", "regions-subgraph", "north-subgraph");
        final Vertex vxWest = new DefaultAtlasVertex("west", "West", "regions-subgraph", "west-subgraph");
        final Vertex vxSouth = new DefaultAtlasVertex("south", "South", "regions-subgraph", "south-subgraph");
        final Vertex vxEast = new DefaultAtlasVertex("east", "East", "regions-subgraph", "east-subgraph");

        addVertices(vxParent, vxNorth, vxSouth, vxWest, vxEast);

        connectVertices(vxParent, vxNorth);
        connectVertices(vxParent, vxWest);
        connectVertices(vxParent, vxSouth);
        connectVertices(vxParent, vxEast);

        loadSite("North", "north-subgraph", "regions-subgraph");
        loadSite("South", "south-subgraph", "regions-subgraph");
        loadSite("East", "east-subgraph", "regions-subgraph");
        loadSite("West", "west-subgraph", "regions-subgraph");
    }

    private void loadSite(final String site, final String subGraphId, final String glue) {

        final Vertex vxParent = new ParentAtlasVertex(subGraphId, site, subGraphId, glue);

        final Vertex vxSite1 = new DefaultAtlasVertex(site.toLowerCase() + "1", site + "1", null, null);
        final Vertex vxSite2 = new DefaultAtlasVertex(site.toLowerCase() + "2", site + "2", null, null);
        final Vertex vxSite3 = new DefaultAtlasVertex(site.toLowerCase() + "3", site + "3", null, null);

        addVertices(vxParent, vxSite1, vxSite2, vxSite3);

        connectVertices(vxParent, vxSite1);
        connectVertices(vxParent, vxSite2);
        connectVertices(vxParent, vxSite3);
    }

    private void load() {
        resetContainer();
        loadRoot();
    }

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
        return new AtlasSubGraphCriteria(this, "regions-subgraph");
    }

    @Override
    public SelectionChangedListener.Selection getSelection(List<VertexRef> selectedVertices, ContentType contentType) {
        return SelectionChangedListener.Selection.NONE;
    }

    @Override
    public boolean contributesTo(ContentType type) {
        return false;
    }
}
