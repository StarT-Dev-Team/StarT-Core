#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 CenterOffset;
uniform mat4 RingMat;
uniform float RingBreak;

out vec3 vPos;
out vec3 vNormal;
out vec2 vUv;
out float vInner;

vec3 turnAbout(vec3 v, vec3 axis, float angle) {
    return v * cos(angle) + cross(axis, v) * sin(angle) + axis * dot(axis, v) * (1.0 - cos(angle));
}

void main() {
    vec3 local = Position;
    vec3 normal = Normal;
    if (RingBreak > 0.0) {
        float segment = floor(Color.r * 8.0 + 0.5);
        float mid = (segment + 0.5) / 8.0 * 6.2831853;
        vec3 outward = vec3(cos(mid), sin(mid), 0.0);
        vec3 pivot = outward * length(Position.xy);
        vec3 axis = vec3(-sin(mid), cos(mid), 0.0);
        float angle = RingBreak * (1.2 + 0.35 * segment) * (mod(segment, 2.0) < 0.5 ? 1.0 : -1.0);
        local = pivot + turnAbout(local - pivot, axis, angle) + outward * RingBreak * 24.0;
        normal = turnAbout(normal, axis, angle);
    }
    vPos = mat3(RingMat) * local;
    vNormal = mat3(RingMat) * normal;
    vUv = UV0;
    vInner = Color.g;
    gl_Position = ProjMat * ModelViewMat * vec4(vPos + CenterOffset, 1.0);
}
