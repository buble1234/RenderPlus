package render.plus.mixin;

import com.mojang.blaze3d.shaders.ShaderType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import render.plus.core.VirtualShaderRegistry;

/**
 * Author: zx
 * Since: 21.02.2026, в 12:49:15
 */
@Mixin(targets = "net.minecraft.client.gl.ShaderLoader$Cache")
public class ShaderLoader$CacheMixin {

    @Inject(method = "getSource", at = @At("RETURN"), cancellable = true)
    private void onGetSource(Identifier id, ShaderType type, CallbackInfoReturnable<String> cir) {
        if (cir.getReturnValue() == null) {
            String src = VirtualShaderRegistry.get(id, type);
            if (src != null) cir.setReturnValue(src);
        }
    }
}