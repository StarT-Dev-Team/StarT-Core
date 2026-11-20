package com.startechnology.start_core.api.dimensional_finder;

import com.startechnology.start_core.StarTConfig.DifficultyPreset;
import lombok.Getter;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DimensionalCoordinateTree {

    public enum EndKind {

        VORTEX("behaviour.start_core.coordinate_crystal.kind.vortex"),
        OBJECT_OF_INTEREST("behaviour.start_core.coordinate_crystal.kind.object_of_interest"),
        TURBULENCE("behaviour.start_core.coordinate_crystal.kind.turbulence"),
        GRAVITY_WELL("behaviour.start_core.coordinate_crystal.kind.gravity_well"),
        RIFT_ECHO("behaviour.start_core.coordinate_crystal.kind.rift_echo");

        @Getter
        private final String translationKey;

        EndKind(String translationKey) {
            this.translationKey = translationKey;
        }

        public static @Nullable EndKind byName(String name) {
            if (name == null || name.isEmpty()) return null;
            for (EndKind kind : values()) {
                if (kind.name().equalsIgnoreCase(name)) {
                    return kind;
                }
            }
            return null;
        }
    }

    @Getter
    public static class Node {

        private final int coord;
        private boolean onAddress;
        private final int depth;
        @Nullable
        private final Node parent;
        private final List<Node> children = new ArrayList<>();
        @Nullable
        private EndKind kind;

        public Node(int coord, boolean onAddress, int depth, @Nullable Node parent) {
            this.coord = coord;
            this.onAddress = onAddress;
            this.depth = depth;
            this.parent = parent;
        }

        public List<Node> getNeighbors() {
            List<Node> neighbors = new ArrayList<>(children.size() + 1);
            if (parent != null) {
                neighbors.add(parent);
            }
            neighbors.addAll(children);
            return neighbors;
        }
    }

    @Getter
    private final int[] address;
    @Getter
    private final Node root;
    @Getter
    private final List<Node> allNodes;
    @Getter
    private final List<Node> leaves;
    private final Map<Integer, Node> nodesByCoord;

    private DimensionalCoordinateTree(int[] address, Node root, List<Node> allNodes, List<Node> leaves,
                                      Map<Integer, Node> nodesByCoord) {
        this.address = Arrays.copyOf(address, address.length);
        this.root = root;
        this.allNodes = Collections.unmodifiableList(allNodes);
        this.leaves = Collections.unmodifiableList(leaves);
        this.nodesByCoord = Collections.unmodifiableMap(nodesByCoord);
    }

    public @Nullable Node getNode(int coord) {
        return this.nodesByCoord.get(coord);
    }

    public Node pickRandomLeaf(RandomSource random) {
        if (leaves.isEmpty()) {
            return this.root;
        }
        return this.leaves.get(random.nextInt(this.leaves.size()));
    }

    public List<Node> getAvailableNeighbors(Node cur, int prevCoord, int excludedCoord) {
        List<Node> opts = new ArrayList<>();
        for (Node neighbor : cur.getNeighbors()) {
            if (neighbor.coord != prevCoord && neighbor.coord != excludedCoord) {
                opts.add(neighbor);
            }
        }
        return opts;
    }

    public static DimensionalCoordinateTree generateTree(int[] address, DifficultyPreset cfg) {
        if (address == null || address.length == 0) {
            throw new IllegalArgumentException("Dimension address must contain at least one coordinate");
        }

        long seed = hashAddress(address);
        Random rng = new Random(seed);
        int n = address.length;
        int d = n - 1;

        Set<Integer> used = new HashSet<>();
        int maxC = 31;
        for (int c : address) {
            used.add(c);
            if (c > maxC) {
                maxC = c;
            }
        }
        int[] maxCHolder = new int[] { maxC };

        List<Node> allNodes = new ArrayList<>();
        Map<Integer, Node> byCoord = new HashMap<>();

        // 1. Goal point is the root; the address is just one branch of it
        Node root = new Node(address[n - 1], true, 0, null);
        allNodes.add(root);
        byCoord.put(root.coord, root);

        Node p = root;
        for (int i = n - 2; i >= 0; i--) {
            p = newChild(p, address[i], allNodes, byCoord);
            p.onAddress = true;
        }

        // 2. Alternate branches straight off goal point, each >= D long
        List<Node> pool = new ArrayList<>();
        int altBranches = cfg.altMin + rng.nextInt(cfg.altSpread);
        for (int i = 0; i < altBranches; i++) {
            int len = d + (rng.nextFloat() < 0.25f ? 0 : 1 + rng.nextInt(2));
            chain(root, len, pool, rng, used, maxCHolder, allNodes, byCoord);
        }
        for (Node node : allNodes) {
            if (node.onAddress && node != root && node.coord != address[0]) {
                pool.add(node);
            }
        }

        // 3. Side branches
        for (int pass = 0; pass < cfg.passes; pass++) {
            List<Node> created = new ArrayList<>();
            for (Node node : pool) {
                if (allNodes.size() > cfg.maxNodes) {
                    continue;
                }
                int count = 0;
                if (pass == 0 && node.onAddress && cfg.forceAddrBranch) {
                    count += 1;
                }
                double prob = (pass > 0) ? cfg.pSub : (node.onAddress ? cfg.pAddr : cfg.pOther);
                if (rng.nextFloat() < prob) {
                    count += 1;
                }
                for (int k = 0; k < count; k++) {
                    int len = Math.max(1, d - node.depth) + (rng.nextFloat() < 0.3f ? 1 : 0);
                    chain(node, len, created, rng, used, maxCHolder, allNodes, byCoord);
                }
            }
            pool = created;
        }

        // 4. Flavor for every dead end
        EndKind[] kinds = EndKind.values();
        List<Node> leaves = new ArrayList<>();
        for (Node node : allNodes) {
            if (node.children.isEmpty() && node != root) {
                node.kind = kinds[rng.nextInt(kinds.length)];
                leaves.add(node);
            }
        }
        // Fallback flavor for internal nodes in case a rewind stops on a degree-2 node
        for (Node node : allNodes) {
            if (node.kind == null && node != root) {
                node.kind = kinds[rng.nextInt(kinds.length)];
            }
        }

        return new DimensionalCoordinateTree(address, root, allNodes, leaves, byCoord);
    }

    private static long hashAddress(int[] address) {
        long hash = 0x9E3779B97F4A7C15L;
        for (int coord : address) {
            hash ^= coord + 0x9E3779B9L + (hash << 6) + (hash >> 2);
        }
        return hash;
    }

    private static int freshCoord(Random rng, Set<Integer> used, int[] maxCHolder) {
        int attempts = 0;
        while (true) {
            if (used.size() >= maxCHolder[0] || attempts > 8) {
                maxCHolder[0]++;
                attempts = 0;
            }
            int c = 1 + rng.nextInt(maxCHolder[0]);
            if (used.add(c)) {
                return c;
            }
            attempts++;
        }
    }

    private static Node newChild(Node from, int coord, List<Node> allNodes, Map<Integer, Node> byCoord) {
        Node child = new Node(coord, false, from.depth + 1, from);
        from.children.add(child);
        allNodes.add(child);
        byCoord.put(coord, child);
        return child;
    }

    private static void chain(Node from, int length, List<Node> out, Random rng, Set<Integer> used,
                              int[] maxCHolder, List<Node> allNodes, Map<Integer, Node> byCoord) {
        Node cur = from;
        for (int k = 0; k < length; k++) {
            cur = newChild(cur, freshCoord(rng, used, maxCHolder), allNodes, byCoord);
            out.add(cur);
        }
    }
}
