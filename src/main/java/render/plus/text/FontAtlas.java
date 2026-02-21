package render.plus.text;

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.*;

public class FontAtlas {

    public record Glyph(
            float advance,
            float planeLeft, float planeBottom, float planeRight, float planeTop,
            float minU, float minV, float maxU, float maxV
    ) {
        public float width() {
            return planeRight - planeLeft;
        }

        public float height() {
            return planeTop - planeBottom;
        }

        public boolean hasGeometry() {
            return width() > 0 && height() > 0;
        }
    }

    private final Identifier png;
    private final Map<Integer, Glyph> glyphs = new HashMap<>();
    private final Map<Integer, Map<Integer, Float>> kerning = new HashMap<>();

    private float emSize, lineHeight, distanceRange;
    private TextureSetup textureSetup;

    public FontAtlas(Identifier json, Identifier png) {
        this.png = png;
        load(json);
    }

    private void load(Identifier json) {
        try {
            var mc = MinecraftClient.getInstance();
            var res = mc.getResourceManager().getResource(json).orElseThrow();
            var root = JsonParser.parseReader(new InputStreamReader(res.getInputStream())).getAsJsonObject();

            var atlas = root.getAsJsonObject("atlas");
            var metrics = root.getAsJsonObject("metrics");

            float atlasW = atlas.get("width").getAsFloat();
            float atlasH = atlas.get("height").getAsFloat();
            this.distanceRange = atlas.get("distanceRange").getAsFloat();
            this.emSize = metrics.get("emSize").getAsFloat();
            this.lineHeight = metrics.get("lineHeight").getAsFloat();

            for (var el : root.getAsJsonArray("glyphs")) {
                var g = el.getAsJsonObject();
                int unicode = g.get("unicode").getAsInt();
                float advance = g.has("advance") ? g.get("advance").getAsFloat() : 0f;

                float pl = 0, pb = 0, pr = 0, pt = 0;
                float mu = 0, mv = 0, xu = 0, xv = 0;

                if (g.has("planeBounds")) {
                    var pb2 = g.getAsJsonObject("planeBounds");
                    pl = pb2.get("left").getAsFloat();
                    pb = pb2.get("bottom").getAsFloat();
                    pr = pb2.get("right").getAsFloat();
                    pt = pb2.get("top").getAsFloat();
                }
                if (g.has("atlasBounds")) {
                    var ab = g.getAsJsonObject("atlasBounds");
                    mu = ab.get("left").getAsFloat() / atlasW;
                    xu = ab.get("right").getAsFloat() / atlasW;
                    mv = 1f - ab.get("top").getAsFloat() / atlasH;
                    xv = 1f - ab.get("bottom").getAsFloat() / atlasH;
                }

                glyphs.put(unicode, new Glyph(advance, pl, pb, pr, pt, mu, mv, xu, xv));
            }

            if (root.has("kerning")) {
                for (var el : root.getAsJsonArray("kerning")) {
                    var k = el.getAsJsonObject();
                    int l = k.get("unicode1").getAsInt();
                    int r = k.get("unicode2").getAsInt();
                    kerning.computeIfAbsent(l, x -> new HashMap<>())
                            .put(r, k.get("advance").getAsFloat());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load font: " + json, e);
        }
    }

    public TextureSetup textureSetup() {
        if (textureSetup == null) {
            var tex = MinecraftClient.getInstance().getTextureManager().getTexture(png);
            textureSetup = TextureSetup.withoutGlTexture(tex.getGlTextureView());
        }
        return textureSetup;
    }

    public Glyph getGlyph(int ch) {
        return glyphs.get(ch);
    }

    public float getEmSize() {
        return emSize;
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public float getDistanceRange() {
        return distanceRange;
    }

    public float getKerning(int l, int r, float scale) {
        return kerning.getOrDefault(l, Map.of()).getOrDefault(r, 0f) * scale;
    }

    public float measureWidth(String text, float size) {
        float w = 0;
        int prev = -1;
        float scale = size / emSize;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);
            Glyph g = getGlyph(c);
            if (g == null) continue;
            if (prev != -1) w += getKerning(prev, c, scale);
            w += g.advance() * scale;
            prev = c;
        }
        return w;
    }
}