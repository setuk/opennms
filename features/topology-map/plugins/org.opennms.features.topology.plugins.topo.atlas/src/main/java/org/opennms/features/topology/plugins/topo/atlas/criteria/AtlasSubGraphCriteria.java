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

package org.opennms.features.topology.plugins.topo.atlas.criteria;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.opennms.features.topology.api.support.VertexHopGraphProvider;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.features.topology.plugins.topo.atlas.AtlasTopologyProvider;
import org.opennms.features.topology.plugins.topo.atlas.vertices.AbstractAtlasVertex;

public class AtlasSubGraphCriteria extends VertexHopGraphProvider.VertexHopCriteria {
    private final AtlasTopologyProvider topologyProvider;
    private final String subGraphId;

    public AtlasSubGraphCriteria(final AtlasTopologyProvider topologyProvider, final String subGraphId) {
        super("Sub-graph " + subGraphId);
        this.topologyProvider = topologyProvider;
        this.subGraphId = subGraphId;
    }

    public AtlasTopologyProvider getTopologyProvider() {
        return topologyProvider;
    }

    public String getSubGraphId() {
        return subGraphId;
    }

    @Override
    public Set<VertexRef> getVertices() {
        return topologyProvider.getVertices().stream().filter(vx -> subGraphId.equals(((AbstractAtlasVertex) vx).getSubGraphId())).collect(Collectors.toSet());
    }

    @Override
    public String getNamespace() {
        return topologyProvider.getVertexNamespace();
    }

    @Override
    public int hashCode() {
        return Objects.hash(subGraphId, topologyProvider);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof AtlasSubGraphCriteria) {
            return Objects.equals(topologyProvider, ((AtlasSubGraphCriteria) obj).getTopologyProvider()) &&
                    Objects.equals(subGraphId, ((AtlasSubGraphCriteria) obj).getSubGraphId());
        }
        return false;
    }
}
