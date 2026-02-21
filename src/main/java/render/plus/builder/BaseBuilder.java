package render.plus.builder;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import render.plus.shader.GlslShader;
import render.plus.mixin.accessors.DrawContextAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public abstract class BaseBuilder<T extends BaseBuilder<T>> {
    protected final DrawContext ctx;
    protected final float x, y, w, h;
    protected int color = 0xFFFFFFFF;

    protected BaseBuilder(DrawContext ctx, float x, float y, float w, float h) {
        this.ctx = ctx;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @SuppressWarnings("unchecked")
    public T color(int argb) {
        this.color = argb;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T color(int r, int g, int b, int a) {
        this.color = (a << 24) | (r << 16) | (g << 8) | b;
        return (T) this;
    }

    public abstract void draw();

    protected void emit(GlslShader shader, Vector4f data) {
        emit(shader, data, TextureSetup.empty());
    }

    protected void emit(GlslShader shader, Vector4f data, TextureSetup textureSetup) {
        ScreenRect bounds = new ScreenRect((int) x, (int) y, (int) w, (int) h).transformEachVertex(ctx.getMatrices());
        @Nullable
        ScreenRect scissor = ((DrawContextAccessor) ctx).getScissorStack().peekLast();

        float r = ColorHelper.getRed(color) / 255f;
        float g = ColorHelper.getGreen(color) / 255f;
        float b = ColorHelper.getBlue(color) / 255f;
        float a = ColorHelper.getAlpha(color) / 255f;

        float fx = x, fy = y, fw = w, fh = h;
        TextureSetup ts = textureSetup;

        ((DrawContextAccessor) ctx).getGuiRenderState()
                .addSimpleElement(new SimpleGuiElementRenderState() {
                    @Override
                    public void setupVertices(VertexConsumer vertices) {
                        if (vertices instanceof BufferBuilder bb) {
                            quad(bb, shader, fx, fy, fw, fh, r, g, b, a, data);
                        }
                    }

                    @Override
                    public RenderPipeline pipeline() {
                        return shader.pipeline();
                    }

                    @Override
                    public ScreenRect bounds() {
                        return bounds;
                    }

                    @Override
                    public TextureSetup textureSetup() {
                        return ts;
                    }

                    @Override
                    public @Nullable ScreenRect scissorArea() {
                        return scissor;
                    }
                });
    }

    protected void emitGlyph(GlslShader shader, float x, float y, float w, float h, float u0, float v0, float u1, float v1, int rgb, float alphaAsRange, Vector4f data, TextureSetup ts) {
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        float a = alphaAsRange;

        ScreenRect bounds = new ScreenRect((int) x, (int) y, (int) w, (int) h)
                .transformEachVertex(ctx.getMatrices());
        @Nullable
        ScreenRect scissor = ((DrawContextAccessor) ctx).getScissorStack().peekLast();

        float fx = x, fy = y, fw = w, fh = h;

        ((DrawContextAccessor) ctx).getGuiRenderState()
                .addSimpleElement(new SimpleGuiElementRenderState() {
                    @Override
                    public void setupVertices(VertexConsumer vertices) {
                        if (vertices instanceof BufferBuilder bb) {
                            bb.vertex(fx, fy + fh, 0);
                            bb.texture(u0, v1);
                            shader.writeAttrs(bb, u0, v1, fw, fh, data);
                            bb.color(r, g, b, a);
                            bb.vertex(fx + fw, fy + fh, 0);
                            bb.texture(u1, v1);
                            shader.writeAttrs(bb, u1, v1, fw, fh, data);
                            bb.color(r, g, b, a);
                            bb.vertex(fx + fw, fy, 0);
                            bb.texture(u1, v0);
                            shader.writeAttrs(bb, u1, v0, fw, fh, data);
                            bb.color(r, g, b, a);
                            bb.vertex(fx, fy, 0);
                            bb.texture(u0, v0);
                            shader.writeAttrs(bb, u0, v0, fw, fh, data);
                            bb.color(r, g, b, a);
                        }
                    }

                    @Override
                    public RenderPipeline pipeline() {
                        return shader.pipeline();
                    }

                    @Override
                    public ScreenRect bounds() {
                        return bounds;
                    }

                    @Override
                    public TextureSetup textureSetup() {
                        return ts;
                    }

                    @Override
                    public @Nullable ScreenRect scissorArea() {
                        return scissor;
                    }
                });
    }

    private static void quad(BufferBuilder b, GlslShader s, float x, float y, float w, float h, float r, float g, float bl, float a, Vector4f data) {
        vtx(b, s, x, y + h, 0, h, w, h, r, g, bl, a, data);
        vtx(b, s, x + w, y + h, w, h, w, h, r, g, bl, a, data);
        vtx(b, s, x + w, y, w, 0, w, h, r, g, bl, a, data);
        vtx(b, s, x, y, 0, 0, w, h, r, g, bl, a, data);
    }

    private static void vtx(BufferBuilder b, GlslShader s, float x, float y, float u, float v, float w, float h, float r, float g, float bl, float a, Vector4f data) {
        b.vertex(x, y, 0);
        b.texture(u, v);
        s.writeAttrs(b, u, v, w, h, data);
        b.color(r, g, bl, a);
    }
}