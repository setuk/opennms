package org.opennms.features.topology.plugins.topo.atlas.vertices;

public class DefaultAtlasVertex extends AbstractAtlasVertex {
    public DefaultAtlasVertex(final String id,
                              final String label,
                              final String glue) {
        super(id, label, glue);
    }

    @Override
    public int getLevel() {
        return 1;
    }
}
