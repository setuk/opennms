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

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.BaseTheme;

public class NavigationInfoPanelItem implements InfoPanelItem {
    @Override
    public Component getComponent(GraphContainer container) {
        AtlasSubGraphCriteria criteria = VertexHopGraphProvider.VertexHopCriteria.getSingleCriteriaForGraphContainer(container, AtlasSubGraphCriteria.class);

        HorizontalLayout navigationLayout = new HorizontalLayout();

        if (!criteria.getSubGraphId().contains(".")) {
            Button button = new Button();
            button.addStyleName(BaseTheme.BUTTON_LINK);
            button.setCaption("Regions");
            navigationLayout.addComponent(button);
        } else {
            String subgraphId = criteria.getSubGraphId();
            do {
                String newSubgraphId = subgraphId.substring(0, subgraphId.lastIndexOf("."));
                String label = subgraphId.substring(subgraphId.lastIndexOf("."));
                

                navigationLayout.addComponent(new Label(label), navigationLayout.getComponentCount() - 1);

            } while ();


        }
        return navigationLayout;
    }

    @Override
    public boolean contributesTo(GraphContainer container) {
        return container.getBaseTopology() instanceof AtlasTopologyProvider;
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
