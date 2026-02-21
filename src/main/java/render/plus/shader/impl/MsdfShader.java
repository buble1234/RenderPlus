package render.plus.shader.impl;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;
import render.plus.shader.GlslShader;

public class MsdfShader extends GlslShader {
    private static MsdfShader instance;
    private final Attr attrRange;

    public static MsdfShader get() {
        if (instance == null) instance = new MsdfShader();
        return instance;
    }

    private MsdfShader() {
        attrRange = attr("MsdfRange", 1);
        buildPipeline(Identifier.of(MOD_ID, "core/msdf"), "Sampler0");
    }

    @Override
    public void writeAttrs(BufferBuilder b, float u, float v, float w, float h, Vector4f data) {
        attrRange.write(b, data.x);
    }
}