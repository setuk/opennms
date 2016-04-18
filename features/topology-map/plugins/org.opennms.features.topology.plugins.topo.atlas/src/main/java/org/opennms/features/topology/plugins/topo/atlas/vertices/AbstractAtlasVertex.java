package org.opennms.features.topology.plugins.topo.atlas.vertices;

import org.opennms.features.topology.api.topo.LevelAware;
import org.opennms.features.topology.api.topo.SimpleLeafVertex;
import org.opennms.features.topology.plugins.topo.atlas.AtlasTopologyProvider;

public abstract class AbstractAtlasVertex extends SimpleLeafVertex implements LevelAware {
    private final String glue;

    public AbstractAtlasVertex(final String id,
                               final String label,
                               final String glue) {
        super(AtlasTopologyProvider.TOPOLOGY_NAMESPACE, id, null, null);
        this.setLabel(label);

        this.glue = glue;
    }

    public String getGlue() {
        return this.glue;
    }
}
