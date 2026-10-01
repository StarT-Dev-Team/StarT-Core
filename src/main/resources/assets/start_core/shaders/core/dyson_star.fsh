#version 150

#moj_import <start_core:dyson_common.glsl>

uniform vec3 CenterOffset;
uniform float Radius;
uniform vec3 Color;
uniform float Time;
uniform float Activity;
uniform float Giant;
uniform float Heat;
uniform float Brightness;
uniform float Protostar;
uniform float Detail;
uniform float Seed;

in vec3 vLocal;

out vec4 fragColor;

void main() {
    DysonStar s = DysonStar(Radius, Color, Time, Activity, Giant, Heat, Brightness, Protostar, Detail, Seed);
    vec3 camera = -CenterOffset;
    vec3 n = normalize(vLocal);
    vec3 color;
    if (dot(camera, camera) < Radius * Radius) {
        color = dsStarInterior(s);
    } else {
        color = dsStarSurface(n, normalize(n * Radius - camera), s);
    }
    fragColor = vec4(stShoulder(color), 1.0);
}
