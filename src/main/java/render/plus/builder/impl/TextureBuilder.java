package render.plus.builder.impl;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;
import render.plus.builder.BaseBuilder;
import render.plus.shader.impl.RoundedRectShader;
import render.plus.shader.impl.RoundedTextureShader;

public class TextureBuilder extends BaseBuilder<TextureBuilder> {

    private float tl, tr, br, bl;
    private final Identifier texture;

    public TextureBuilder(DrawContext ctx, Identifier texture, float x, float y, float w, float h) {
        super(ctx, x, y, w, h);
        this.texture = texture;
    }

    public TextureBuilder radius(float r) {
        tl = tr = br = bl = r;
        return this;
    }

    public TextureBuilder radius(float tl, float tr, float br, float bl) {
        this.tl = tl;
        this.tr = tr;
        this.br = br;
        this.bl = bl;
        return this;
    }

    @Override
    public void draw() {
        var texManager = MinecraftClient.getInstance().getTextureManager();
        var tex = texManager.getTexture(texture);
        GpuTextureView view = tex.getGlTextureView();
        emit(RoundedTextureShader.get(), new Vector4f(tl, tr, br, bl), TextureSetup.withoutGlTexture(view));
    }
}