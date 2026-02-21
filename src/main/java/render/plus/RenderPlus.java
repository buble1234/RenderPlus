package render.plus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import render.plus.builder.impl.RectBuilder;
import render.plus.builder.impl.TextBuilder;
import render.plus.builder.impl.TextureBuilder;
import render.plus.text.FontAtlas;

import static render.plus.shader.GlslShader.MOD_ID;

public class RenderPlus implements ModInitializer {

    private FontAtlas font;

    @Override
    public void onInitialize() {
    }

    public static RectBuilder rect(DrawContext ctx, float x, float y, float w, float h) {
        return new RectBuilder(ctx, x, y, w, h);
    }

    public static TextureBuilder texture(DrawContext ctx, Identifier texture, float x, float y, float w, float h) {
        return new TextureBuilder(ctx, texture, x, y, w, h);
    }

    public static TextBuilder text(DrawContext ctx, FontAtlas atlas, String text, float x, float y) {
        return new TextBuilder(ctx, atlas, text, x, y);
    }
}