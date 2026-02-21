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
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            font = new FontAtlas(
                    Identifier.of(MOD_ID, "fonts/sf_medium.json"),
                    Identifier.of(MOD_ID, "fonts/sf_medium.png")
            );
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            rect(drawContext, 10, 10, 200, 200).radius(15, 40, 70, 5).draw();
            texture(drawContext, Identifier.of(MOD_ID, "textures/img.png"), 10, 220, 200, 200).radius(15, 40, 70, 5).draw();
            text(drawContext, font, "RenderPlus!", 220, 60).size(15).draw();
        });
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