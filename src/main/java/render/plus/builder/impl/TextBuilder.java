package render.plus.builder.impl;

import net.minecraft.client.gui.DrawContext;
import org.joml.Vector4f;
import render.plus.builder.BaseBuilder;
import render.plus.shader.impl.MsdfShader;
import render.plus.text.FontAtlas;
import render.plus.text.TextAlign;

public class TextBuilder extends BaseBuilder<TextBuilder> {
    private final FontAtlas atlas;
    private final String text;
    private float size = 16f;
    private TextAlign align = TextAlign.LEFT;

    public TextBuilder(DrawContext ctx, FontAtlas atlas, String text, float x, float y) {
        super(ctx, x, y, 0, 0);
        this.atlas = atlas;
        this.text = text;
    }

    public TextBuilder size(float size) {
        this.size = size;
        return this;
    }

    public TextBuilder align(TextAlign a) {
        this.align = a;
        return this;
    }

    @Override
    public void draw() {
        float scale = size / atlas.getEmSize();
        float totalW = atlas.measureWidth(text, size);

        float cursor = switch (align) {
            case LEFT -> x;
            case CENTER -> x - totalW / 2f;
            case RIGHT -> x - totalW;
        };

        int rgb = color | 0xFF000000;
        int prev = -1;

        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);
            FontAtlas.Glyph g = atlas.getGlyph(c);
            if (g == null) continue;

            if (prev != -1) cursor += atlas.getKerning(prev, c, scale);

            if (g.hasGeometry()) {
                float gx = cursor + g.planeLeft() * scale;
                float gy = y - g.planeTop() * scale;
                float gw = g.width() * scale;
                float gh = g.height() * scale;

                emitGlyph(
                        MsdfShader.get(),
                        gx, gy, gw, gh,
                        g.minU(), g.minV(), g.maxU(), g.maxV(),
                        rgb, 1.0f,
                        new Vector4f(atlas.getDistanceRange(), 0, 0, 0),
                        atlas.textureSetup()
                );
            }

            cursor += g.advance() * scale;
            prev = c;
        }
    }
}