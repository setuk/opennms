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

import org.opennms.features.topology.api.GraphContainer;
import org.opennms.features.topology.api.info.InfoPanelItem;
import org.opennms.features.topology.api.support.VertexHopGraphProvider;
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

        // Path elements of Navigation
        final HorizontalLayout navigationLayout = new HorizontalLayout();
        navigationLayout.setSpacing(true);
        for (DefaultAtlasVertex eachAtlasVertex : criteria.getVertices()) {
            if (navigationLayout.getComponentCount() >= 1) {
                navigationLayout.addComponent(new Label(" > "));
            }
            navigationLayout.addComponent(createButton(topologyProvider, container, eachAtlasVertex, eachAtlasVertex.getGlue()));
        }

        return navigationLayout;
    }

    private static Button createButton(AtlasTopologyProvider topologyProvider, GraphContainer container, DefaultAtlasVertex vertex, String subgraphId) {
        Button button = new Button();
        button.addStyleName(BaseTheme.BUTTON_LINK);
        button.addClickListener((event) -> new NavigateOperation(topologyProvider).navigateTo(container, vertex, subgraphId));
        button.setCaption(vertex.getLabel());
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
