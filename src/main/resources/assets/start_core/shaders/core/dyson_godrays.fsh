#version 150

#moj_import <start_core:dyson_common.glsl>

uniform sampler2D SceneDepth;

uniform vec2 ScreenSize;
uniform vec2 StarUv;
uniform float StarDepth;
uniform float CoronaRadius;
uniform int Samples;
uniform vec3 RayTint;
uniform float RayIntensity;
uniform vec3 Overlay;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec3 color = Overlay;
    if (RayIntensity > 0.0) {
        color += stShoulder(dsGodRays(SceneDepth, uv, StarUv, StarDepth, CoronaRadius, ScreenSize.x / ScreenSize.y,
                Samples, RayTint, RayIntensity));
    }
    fragColor = vec4(color, 1.0);
}
