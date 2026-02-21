**RenderPlus - Render Library for Minecraft**

What can it do:
Rendering Rounded Rectangle, Rounded Texture Rectangle and MSDF Text.

How it works inside:
GlslShader - abstraction over RenderPipeline. Registers vertex format, blending, and shaders. Supports custom attributes and samplers.
VirtualShaderRegistry - stores GLSL source code in memory. One .glsl file is split into vertex and fragment code using #define VERTEX / FRAGMENT.
AttrRegistry - registers custom VertexFormatElement (float attributes) to pass additional data to the shader.

How use:
```groovy
dependencies {
    modImplementation 'com.github.buble1234:RenderPlus:1.21.10'
}
