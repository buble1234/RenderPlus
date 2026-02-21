package render.plus.builder.impl;

import net.minecraft.client.gui.DrawContext;
import org.joml.Vector4f;
import render.plus.builder.BaseBuilder;
import render.plus.shader.impl.RoundedRectShader;

public class RectBuilder extends BaseBuilder<RectBuilder> {

    private float tl, tr, br, bl;

    public RectBuilder(DrawContext ctx, float x, float y, float w, float h) {
        super(ctx, x, y, w, h);
    }

    public RectBuilder radius(float r) {
        tl = tr = br = bl = r;
        return this;
    }

    public RectBuilder radius(float tl, float tr, float br, float bl) {
        this.tl = tl;
        this.tr = tr;
        this.br = br;
        this.bl = bl;
        return this;
    }

    @Override
    public void draw() {
        emit(RoundedRectShader.get(), new Vector4f(tl, tr, br, bl));
    }
}