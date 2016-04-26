/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
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

package org.opennms.features.topology.plugins.topo.atlas.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.opennms.features.topology.api.GraphContainer;
import org.opennms.features.topology.api.info.InfoPanelItem;
import org.opennms.features.topology.api.support.VertexHopGraphProvider;
import org.opennms.features.topology.api.topo.Vertex;
import org.opennms.features.topology.plugins.topo.atlas.AtlasTopologyProvider;
import org.opennms.features.topology.plugins.topo.atlas.criteria.AtlasSubGraphCriteria;
import org.opennms.features.topology.plugins.topo.atlas.operations.NavigateOperation;
import org.opennms.features.topology.plugins.topo.atlas.vertices.DefaultAtlasVertex;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.BaseTheme;

public class NavigationInfoPanelItem implements InfoPanelItem {

    private final AtlasTopologyProvider topologyProvider;

    public NavigationInfoPanelItem(AtlasTopologyProvider topologyProvider) {
        this.topologyProvider = topologyProvider;
    }

    @Override
    public Component getComponent(GraphContainer container) {
        final AtlasSubGraphCriteria criteria = VertexHopGraphProvider.VertexHopCriteria.getSingleCriteriaForGraphContainer(container, AtlasSubGraphCriteria.class, false);
        final String namespace = container.getBaseTopology().getVertexNamespace();
        final List<String[]> breadcrumList = new ArrayList<>();

        String subgraphId = criteria.getSubGraphId();
        Vertex vertex = topologyProvider.getVertices().stream()
                .filter(v -> v instanceof DefaultAtlasVertex)
                .map(v -> (DefaultAtlasVertex) v)
                .filter(v -> Objects.equals(criteria.getSubGraphId(), v.getGlue()))
                .findFirst().orElse(null);
        while (vertex != null) {
            String caption = vertex.getLabel();
            breadcrumList.add(new String[]{caption, subgraphId});
            subgraphId = subgraphId.substring(0, subgraphId.lastIndexOf("."));
            vertex = container.getBaseTopology().getVertex(namespace, subgraphId);
        }

        final HorizontalLayout navigationLayout = new HorizontalLayout();
        navigationLayout.setSpacing(true);
        Button button = createButton(topologyProvider, container, "Regions", "regions");
        navigationLayout.addComponent(button);

        for (int i=0; i<breadcrumList.size(); i++) {
            navigationLayout.addComponent(new Label(">"));
            String[] strings = breadcrumList.get(i);
            navigationLayout.addComponent(createButton(topologyProvider, container, strings[0], strings[1]));
        }
        return navigationLayout;
    }

    private static Button createButton(AtlasTopologyProvider topologyProvider, GraphContainer container, String caption, String subgraphId) {
        Button button = new Button();
        button.addStyleName(BaseTheme.BUTTON_LINK);
        button.addClickListener((event) -> new NavigateOperation(topologyProvider).navigateTo(container, subgraphId));
        button.setCaption(caption);
        return button;
    }

    @Override
    public boolean contributesTo(GraphContainer container) {
        return VertexHopGraphProvider.VertexHopCriteria.getSingleCriteriaForGraphContainer(container, AtlasSubGraphCriteria.class, false) != null;
    }

    @Override
    public String getTitle(GraphContainer container) {
        return "Navigation";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
