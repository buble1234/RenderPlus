package render.plus.mixin.accessors;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.render.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BufferBuilder.class)
public interface BufferBuilderAccessor {
    @Invoker("beginElement")
    long getBeginElement(VertexFormatElement element);
}