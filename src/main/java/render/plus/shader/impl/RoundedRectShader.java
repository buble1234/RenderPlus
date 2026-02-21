package render.plus.shader.impl;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;
import render.plus.shader.GlslShader;

public class RoundedRectShader extends GlslShader {
    private static RoundedRectShader instance;

    public static RoundedRectShader get() {
        if (instance == null) instance = new RoundedRectShader();
        return instance;
    }

    private final Attr size;
    private final Attr round;

    private RoundedRectShader() {
        size = attr("rr_size", 2);
        round = attr("rr_round", 4);
        buildPipeline(Identifier.of(MOD_ID, "core/rounded_rect"));
    }

    @Override
    public void writeAttrs(BufferBuilder b, float u, float v, float w, float h, Vector4f data) {
        size.write(b, w, h);
        round.write(b, data.x, data.y, data.z, data.w);
    }
}