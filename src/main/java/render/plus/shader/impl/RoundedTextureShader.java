package render.plus.shader.impl;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;
import render.plus.shader.GlslShader;

public class RoundedTextureShader extends GlslShader {
    private static RoundedTextureShader instance;

    public static RoundedTextureShader get() {
        if (instance == null) instance = new RoundedTextureShader();
        return instance;
    }

    private final Attr size;
    private final Attr round;

    private RoundedTextureShader() {
        size = attr("rr_size", 2);
        round = attr("rr_round", 4);
        buildPipeline(Identifier.of(MOD_ID, "core/rounded_texture"), "Sampler0");
    }

    @Override
    public void writeAttrs(BufferBuilder b, float u, float v, float w, float h, Vector4f data) {
        size.write(b, w, h);
        round.write(b, data.x, data.y, data.z, data.w);
    }
}