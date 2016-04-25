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

package org.opennms.features.topology.plugins.topo.atlas.operations;

import java.util.List;

import org.opennms.features.topology.api.Operation;
import org.opennms.features.topology.api.OperationContext;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.features.topology.plugins.topo.atlas.AtlasTopologyProvider;
import org.opennms.features.topology.plugins.topo.atlas.criteria.AtlasSubGraphCriteria;
import org.opennms.features.topology.plugins.topo.atlas.vertices.DefaultAtlasVertex;

import com.google.common.base.Strings;

public class NavigateOperation implements Operation {

    private final AtlasTopologyProvider topologyProvider;

    public NavigateOperation(final AtlasTopologyProvider topologyProvider) {
        this.topologyProvider = topologyProvider;
    }
    
    @Override
    public boolean display(List<VertexRef> targets, OperationContext operationContext) {
        return true;
    }

    @Override
    public boolean enabled(List<VertexRef> targets, OperationContext operationContext) {
    	if (targets.size() == 1) {
            return !Strings.isNullOrEmpty(((DefaultAtlasVertex) topologyProvider.getVertex(targets.get(0))).getGlue());
        }

        return false;
    }

    @Override
    public String getId() {
        return "Navigate";
    }

    @Override
    public void execute(List<VertexRef> targets, OperationContext operationContext) {
        final DefaultAtlasVertex vertex = (DefaultAtlasVertex) topologyProvider.getVertex(targets.get(0));
        if (!Strings.isNullOrEmpty(vertex.getGlue())) {
            operationContext.getGraphContainer().clearCriteria();
            operationContext.getGraphContainer().addCriteria(new AtlasSubGraphCriteria(topologyProvider, vertex.getGlue()));
            operationContext.getGraphContainer().redoLayout();
        }
    }
}
