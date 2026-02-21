package render.plus.core;

import com.mojang.blaze3d.shaders.ShaderType;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class VirtualShaderRegistry {
    private static final Map<Identifier, String> VERTEX = new HashMap<>();
    private static final Map<Identifier, String> FRAGMENT = new HashMap<>();

    public static String get(Identifier id, ShaderType type) {
        return switch (type) {
            case VERTEX -> VERTEX.get(id);
            case FRAGMENT -> FRAGMENT.get(id);
        };
    }

    private static String read(String path) {
        try (InputStream is = VirtualShaderRegistry.class.getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("Shader not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String insertDefine(String src, String define) {
        int nl = src.indexOf('\n');
        if (nl == -1) return src + "\n#define " + define + "\n";
        return src.substring(0, nl + 1) + "#define " + define + "\n" + src.substring(nl + 1);
    }

    public static void register(Identifier id) {
        if (VERTEX.containsKey(id)) return;
        String path = "/assets/" + id.getNamespace() + "/shaders/" + id.getPath() + ".glsl";
        String src = read(path);
        VERTEX.put(id, insertDefine(src, "VERTEX"));
        FRAGMENT.put(id, insertDefine(src, "FRAGMENT"));
    }
}