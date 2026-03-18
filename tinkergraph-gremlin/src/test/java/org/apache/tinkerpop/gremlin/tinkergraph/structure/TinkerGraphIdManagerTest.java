/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.tinkergraph.structure;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
@RunWith(Enclosed.class)
public class TinkerGraphIdManagerTest {

    @RunWith(Parameterized.class)
    public static class NumberIdManagerTest {
        private static final Configuration longIdManagerConfig = new BaseConfiguration();
        private static final Configuration integerIdManagerConfig = new BaseConfiguration();

        @Parameterized.Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"coerceLong", 100l, 200l, 300l},
                    {"coerceInt", 100, 200, 300},
                    {"coerceDouble", 100d, 200d, 300d},
                    {"coerceFloat", 100f, 200f, 300f},
                    {"coerceString", "100", "200", "300"},
                    {"coerceMixed", 100d, 200f, "300"}});
        }

        @Parameterized.Parameter(value = 0)
        public String name;

        @Parameterized.Parameter(value = 1)
        public Object vertexIdValue;

        @Parameterized.Parameter(value = 2)
        public Object edgeIdValue;

        @Parameterized.Parameter(value = 3)
        public Object vertexPropertyIdValue;


        @BeforeClass
        public static void setup() {
            longIdManagerConfig.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_EDGE_ID_MANAGER, TinkerGraph.DefaultIdManager.LONG.name());
            longIdManagerConfig.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_ID_MANAGER, TinkerGraph.DefaultIdManager.LONG.name());
            longIdManagerConfig.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_PROPERTY_ID_MANAGER, TinkerGraph.DefaultIdManager.LONG.name());

            integerIdManagerConfig.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_EDGE_ID_MANAGER, TinkerGraph.DefaultIdManager.INTEGER.name());
            integerIdManagerConfig.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_ID_MANAGER, TinkerGraph.DefaultIdManager.INTEGER.name());
            integerIdManagerConfig.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_PROPERTY_ID_MANAGER, TinkerGraph.DefaultIdManager.INTEGER.name());
        }

        @Test
        public void shouldUseLongIdManagerToCoerceTypes() {
            final Graph graph = TinkerGraph.open(longIdManagerConfig);
            final Vertex v = graph.addVertex(T.id, vertexIdValue);
            final VertexProperty vp = v.property(VertexProperty.Cardinality.single, "test", "value", T.id, vertexPropertyIdValue);
            final Edge e = v.addEdge("self", v, T.id, edgeIdValue);

            assertEquals(100l, v.id());
            assertEquals(200l, e.id());
            assertEquals(300l, vp.id());
        }

        @Test
        public void shouldUseIntegerIdManagerToCoerceTypes() {
            final Graph graph = TinkerGraph.open(integerIdManagerConfig);
            final Vertex v = graph.addVertex(T.id, vertexIdValue);
            final VertexProperty vp = v.property(VertexProperty.Cardinality.single, "test", "value", T.id, vertexPropertyIdValue);
            final Edge e = v.addEdge("self", v, T.id, edgeIdValue);

            assertEquals(100, v.id());
            assertEquals(200, e.id());
            assertEquals(300, vp.id());
        }
    }


    @RunWith(Parameterized.class)
    public static class UuidIdManagerTest {
        private static final Configuration idManagerConfig = new BaseConfiguration();

        private static final UUID vertexId = UUID.fromString("0E939658-ADD2-4598-A722-2FC178E9B741");
        private static final UUID edgeId = UUID.fromString("748179AA-E319-8C36-41AE-F3576B73E05C");
        private static final UUID vertexPropertyId = UUID.fromString("EC27384C-39A0-923D-9410-271B585683B6");


        @Parameterized.Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"coerceUuid", vertexId, edgeId, vertexPropertyId},
                    {"coerceString", vertexId.toString(), edgeId.toString(), vertexPropertyId.toString()},
                    {"coerceMixed", vertexId, edgeId, vertexPropertyId.toString()}});
        }

        @Parameterized.Parameter(value = 0)
        public String name;

        @Parameterized.Parameter(value = 1)
        public Object vertexIdValue;

        @Parameterized.Parameter(value = 2)
        public Object edgeIdValue;

        @Parameterized.Parameter(value = 3)
        public Object vertexPropertyIdValue;


        @BeforeClass
        public static void setup() {
            idManagerConfig.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_EDGE_ID_MANAGER, TinkerGraph.DefaultIdManager.UUID.name());
            idManagerConfig.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_ID_MANAGER, TinkerGraph.DefaultIdManager.UUID.name());
            idManagerConfig.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_PROPERTY_ID_MANAGER, TinkerGraph.DefaultIdManager.UUID.name());
        }

        @Test
        public void shouldUseIdManagerToCoerceTypes() {
            final Graph graph = TinkerGraph.open(idManagerConfig);
            final Vertex v = graph.addVertex(T.id, vertexIdValue);
            final VertexProperty vp = v.property(VertexProperty.Cardinality.single, "test", "value", T.id, vertexPropertyIdValue);
            final Edge e = v.addEdge("self", v, T.id, edgeIdValue);

            assertEquals(vertexId, v.id());
            assertEquals(edgeId, e.id());
            assertEquals(vertexPropertyId, vp.id());
        }
    }

    /**
     * TINKERPOP-3214 - Tests that auto-generated vertex property IDs do not collide with explicitly-assigned vertex
     * property IDs. TinkerFactory.createModern() assigns vertex property IDs 0L-11L explicitly while the shared counter
     * stays at -1, so the first call to getNextId() would naively return 0L without the global vertex property map fix.
     * Covers LONG, INTEGER, and ANY managers (the three sequential counter-based implementations).
     */
    @RunWith(Parameterized.class)
    public static class VertexPropertyIdUniquenessTest {

        @Parameterized.Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {TinkerGraph.DefaultIdManager.LONG},
                    {TinkerGraph.DefaultIdManager.INTEGER},
                    {TinkerGraph.DefaultIdManager.ANY}});
        }

        @Parameterized.Parameter(value = 0)
        public TinkerGraph.DefaultIdManager idManager;

        private TinkerGraph openGraph() {
            final Configuration config = new BaseConfiguration();
            config.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_ID_MANAGER, idManager.name());
            config.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_EDGE_ID_MANAGER, idManager.name());
            config.addProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_PROPERTY_ID_MANAGER, idManager.name());
            return TinkerGraph.open(config);
        }

        /**
         * Regression test for TINKERPOP-3214: when VP IDs are assigned explicitly (leaving the shared counter
         * untouched at -1), the first auto-generated VP must not reuse any of those IDs.
         */
        @Test
        public void shouldNotGenerateDuplicateVertexPropertyIdAfterExplicitAssignment() {
            final TinkerGraph graph = openGraph();
            final Vertex v = graph.addVertex(T.id, 100);

            // Explicitly assign VP IDs 0-11, mirroring what TinkerFactory.createModern() does.
            for (int i = 0; i < 12; i++) {
                v.property(VertexProperty.Cardinality.list, "tag", "v" + i, T.id, i);
            }

            final Set<Object> existingVpIds = new HashSet<>();
            v.properties().forEachRemaining(vp -> existingVpIds.add(vp.id()));
            assertEquals(12, existingVpIds.size());

            // Auto-generate a new VP ID — must not collide with any of 0-11.
            final VertexProperty<?> newVp = v.property(VertexProperty.Cardinality.list, "tag", "auto");
            assertNotEquals("auto-generated VP id must not duplicate an explicitly-set VP id",
                    true, existingVpIds.contains(newVp.id()));
        }

        /**
         * All vertex property IDs across the graph must be globally unique regardless of whether they were
         * explicitly assigned or auto-generated.
         */
        @Test
        public void shouldMaintainGloballyUniqueVertexPropertyIds() {
            final TinkerGraph graph = openGraph();
            final Vertex v1 = graph.addVertex(T.id, 1);
            final Vertex v2 = graph.addVertex(T.id, 2);
            final Vertex v3 = graph.addVertex(T.id, 3);

            // Seed some explicit VP IDs, then add auto-generated ones on different vertices.
            v1.property(VertexProperty.Cardinality.single, "name", "alice", T.id, 10);
            v2.property(VertexProperty.Cardinality.single, "name", "bob", T.id, 11);
            v1.property(VertexProperty.Cardinality.single, "city", "seattle");
            v2.property(VertexProperty.Cardinality.single, "city", "portland");
            v3.property(VertexProperty.Cardinality.single, "city", "vancouver");

            final Set<Object> allVpIds = new HashSet<>();
            graph.vertices().forEachRemaining(v -> v.properties().forEachRemaining(vp ->
                    assertTrue("duplicate vertex property id: " + vp.id(), allVpIds.add(vp.id()))));
        }

        /**
         * Auto-generated vertex property IDs must be unique even when no explicit IDs are used.
         */
        @Test
        public void shouldGenerateUniqueAutoVertexPropertyIds() {
            final TinkerGraph graph = openGraph();
            final Vertex v = graph.addVertex();

            v.property(VertexProperty.Cardinality.list, "tag", "a");
            v.property(VertexProperty.Cardinality.list, "tag", "b");
            v.property(VertexProperty.Cardinality.list, "tag", "c");

            final Set<Object> ids = new HashSet<>();
            v.properties("tag").forEachRemaining(vp ->
                    assertTrue("duplicate auto-generated VP id: " + vp.id(), ids.add(vp.id())));
            assertEquals(3, ids.size());
        }

        /**
         * Auto-generated vertex property IDs must not collide with vertex or edge IDs, preserving the
         * existing cross-element-type uniqueness guarantee.
         */
        @Test
        public void shouldNotCollideWithVertexOrEdgeIds() {
            final TinkerGraph graph = openGraph();
            final Vertex v1 = graph.addVertex(T.id, 1);
            final Vertex v2 = graph.addVertex(T.id, 2);
            v1.addEdge("knows", v2, T.id, 3);

            final VertexProperty<?> vp = v1.property(VertexProperty.Cardinality.single, "name", "alice");

            assertNotEquals(idManager.convert(1), vp.id());
            assertNotEquals(idManager.convert(2), vp.id());
            assertNotEquals(idManager.convert(3), vp.id());
        }
    }
}
