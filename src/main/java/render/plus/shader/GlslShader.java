package render.plus.shader;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import render.plus.core.AttrRegistry;
import render.plus.core.VirtualShaderRegistry;
import render.plus.mixin.accessors.BufferBuilderAccessor;

import java.util.ArrayList;
import java.util.List;

public abstract class GlslShader {
    public static String MOD_ID = "renderplus"; // Hi, change this to your MOD_ID value!

    public record Attr(VertexFormatElement element) {
        public void write(BufferBuilder builder, float... values) {
            long ptr = ((BufferBuilderAccessor) builder).getBeginElement(element);
            for (int i = 0; i < values.length; i++) {
                MemoryUtil.memPutFloat(ptr + i * 4L, values[i]);
            }
        }
    }

    private RenderPipeline pipeline;
    private final List<Attr> attrs = new ArrayList<>();

    protected Attr attr(String name, int components) {
        Attr a = new Attr(AttrRegistry.get(name, components));
        attrs.add(a);
        return a;
    }

    protected void buildPipeline(Identifier id) {
        VirtualShaderRegistry.register(id);

        VertexFormat.Builder fb = VertexFormat.builder()
                .add("Position", VertexFormatElement.POSITION)
                .add("UV0", VertexFormatElement.UV0);

        for (Attr a : attrs) {
            fb.add("attr_" + a.element().hashCode(), a.element());
        }

        VertexFormat format = fb.add("Color", VertexFormatElement.COLOR).build();

        pipeline = RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .withVertexFormat(format, VertexFormat.DrawMode.QUADS)
                        .withVertexShader(id)
                        .withFragmentShader(id)
                        .withLocation(id.withPrefixedPath("pipeline/"))
                        .build()
        );
    }

    protected void buildPipeline(Identifier id, String... samplers) {
        VirtualShaderRegistry.register(id);

        VertexFormat.Builder fb = VertexFormat.builder()
                .add("Position", VertexFormatElement.POSITION)
                .add("UV0", VertexFormatElement.UV0);

        for (Attr a : attrs) {
            fb.add("attr_" + a.element().hashCode(), a.element());
        }

        VertexFormat format = fb.add("Color", VertexFormatElement.COLOR).build();

        var builder = RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(format, VertexFormat.DrawMode.QUADS)
                .withVertexShader(id)
                .withFragmentShader(id)
                .withLocation(id.withPrefixedPath("pipeline/"));

        for (String sampler : samplers) {
            builder.withSampler(sampler);
        }

        pipeline = RenderPipelines.register(builder.build());
    }

    public RenderPipeline pipeline() {
        return pipeline;
    }

    public abstract void writeAttrs(BufferBuilder builder, float u, float v, float w, float h, Vector4f data);
}