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
in vec2 rr_size;
in vec4 rr_round;
in vec4 Color;

out vec2 vLocal;
out vec2 vSize;
out vec4 vRound;
out vec4 vColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position + ModelOffset, 1.0);
    vLocal = UV0;
    vSize = rr_size;
    vRound = rr_round;
    vColor = Color;
}
#endif

#ifdef FRAGMENT
in vec2 vLocal;
in vec2 vSize;
in vec4 vRound;
in vec4 vColor;

out vec4 fragColor;

float rrRadius(vec2 p, vec4 r) {
    if (p.x > 0.0) return (p.y > 0.0) ? r.y : r.x;
    else return (p.y > 0.0) ? r.w : r.z;
}

float rrSdf(vec2 local, vec2 sz, vec4 r) {
    vec2 hs = sz * 0.5;
    vec2 p = local - hs;
    float rd = clamp(rrRadius(p, r), 0.0, min(hs.x, hs.y));
    vec2 q = abs(p) - hs + vec2(rd);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - rd;
}

void main() {
    float dist = rrSdf(vLocal, vSize, vRound);
    float aa = max(fwidth(dist), 0.5);
    float alpha = smoothstep(0.0, -aa, dist);
    vec4 col = ColorModulator * vColor;
    col.a *= alpha;
    if (col.a == 0.0) discard;
    fragColor = col;
}
#endif