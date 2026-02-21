#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
    float LineWidth;
};
layout(std140) uniform Projection {
    mat4 ProjMat;
};

#ifdef VERTEX
in vec3 Position;
in vec2 UV0;
in float MsdfRange;
in vec4 Color;

out vec2 vUV;
out vec4 vColor;
out float vRange;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position + ModelOffset, 1.0);
    vUV = UV0;
    vColor = Color;
    vRange = MsdfRange;
}
#endif

#ifdef FRAGMENT
uniform sampler2D Sampler0;

in vec2 vUV;
in vec4 vColor;
in float vRange;

out vec4 fragColor;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

void main() {
    vec3 msd = texture(Sampler0, vUV).rgb;
    float sd = median(msd.r, msd.g, msd.b) - 0.5;

    vec2 unitRange = vec2(vRange) / vec2(textureSize(Sampler0, 0));
    vec2 screenTexSize = vec2(1.0) / fwidth(vUV);
    float pxRange = max(0.5 * dot(unitRange, screenTexSize), 1.0);

    float alpha = smoothstep(-0.5, 0.5, sd * pxRange);

    vec4 col = vec4(vColor.rgb, 1.0) * ColorModulator;
    col.a = alpha;
    if (col.a < 0.01) discard;
    fragColor = col;
}
#endif