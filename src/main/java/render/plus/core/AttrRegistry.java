package render.plus.core;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.HashMap;
import java.util.Map;

public class AttrRegistry {
    private static final Map<String, VertexFormatElement> CACHE = new HashMap<>();

    public static VertexFormatElement get(String name, int count) {
        return CACHE.computeIfAbsent(name, k -> {
            for (int i = 0; i < VertexFormatElement.MAX_COUNT; i++) {
                if (VertexFormatElement.byId(i) == null) {
                    return VertexFormatElement.register(
                            i, 0,
                            VertexFormatElement.Type.FLOAT,
                            VertexFormatElement.Usage.UV,
                            count
                    );
                }
            }
            throw new RuntimeException("No free VertexFormatElement slots for: " + name);
        });
    }
}