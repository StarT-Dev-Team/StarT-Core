#version 150

#moj_import <start_core:hell_flame_common.glsl>
#moj_import <start_core:st_view.glsl>

uniform sampler2D SceneDepth;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 InvProjMat;
uniform vec2 ScreenSize;
uniform vec3 CenterOffset;
uniform mat3 Basis;
uniform vec2 Bounds;
uniform vec4 Shape;
uniform float Lift;
uniform vec4 Turbulence;
uniform float FlameTime;
uniform float Core;
uniform float Intensity;
uniform vec3 Theme;
uniform vec3 Fringe;
uniform float Smoke;
uniform vec4 Tint;
uniform float Seed;
uniform int Steps;

out vec4 fragColor;

float depthAt(vec3 cameraRelative) {
    vec4 clip = ProjMat * ModelViewMat * vec4(cameraRelative, 1.0);
    return clamp(clip.z / clip.w * 0.5 + 0.5, 0.0, 1.0);
}

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec3 rdWorld = stViewRay(uv, InvProjMat, ModelViewMat);
    float sceneDepth = texture(SceneDepth, uv).r;
    float sceneDistance = sceneDepth < 1.0 ? stSceneDistance(uv, sceneDepth, InvProjMat) : 1e9;

    vec3 ro = Basis * -CenterOffset;
    vec3 rd = Basis * rdWorld;
    vec2 range = hfCylinder(ro, rd, Bounds.x, -0.25, Bounds.y);
    float tMin = max(range.x, 0.0);
    float tMax = min(range.y, sceneDistance);
    if (tMax <= tMin) discard;

    HellFlame f;
    f.shape = Shape;
    f.lift = Lift;
    f.turbulence = Turbulence;
    f.flameTime = FlameTime;
    f.core = Core;
    f.intensity = Intensity;
    f.theme = Theme;
    f.fringe = Fringe;
    f.smoke = Smoke;
    f.tint = Tint;
    f.seed = Seed;
    f.bounds = Bounds;

    float jitter = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    HFResult result = hfMarch(ro, rd, tMin, tMax, Steps, jitter, f);
    float alpha = 1.0 - result.transmittance;
    vec3 color = stShoulder(result.color);
    if (alpha < 0.002 && max(color.r, max(color.g, color.b)) < 0.5 / 255.0) discard;
    gl_FragDepth = result.coverDistance >= 0.0 ? depthAt(rdWorld * result.coverDistance) : sceneDepth;
    fragColor = vec4(color, alpha);
}
