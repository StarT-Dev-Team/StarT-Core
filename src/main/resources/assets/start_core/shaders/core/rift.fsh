#version 150

#moj_import <start_core:rift_common.glsl>
#moj_import <start_core:st_color.glsl>
#moj_import <start_core:st_view.glsl>

uniform sampler2D SceneColor;
uniform sampler2D SceneDepth;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 InvProjMat;
uniform vec2 ScreenSize;
uniform vec3 CenterOffset;
uniform mat3 Basis;
uniform vec2 Bounds;
uniform vec2 Shape;
uniform float Width;
uniform float Glow;
uniform float Lens;
uniform float Turbulence;
uniform vec2 Zip;
uniform float Flash;
uniform vec3 EdgeColour;
uniform vec3 HaloColour;
uniform vec3 VoidColour;
uniform float RiftTime;
uniform float Seed;
uniform int Detail;

out vec4 fragColor;

vec4 project(vec3 cameraRelative) {
    vec4 clip = ProjMat * ModelViewMat * vec4(cameraRelative, 1.0);
    return vec4(clip.xy / clip.w * 0.5 + 0.5, clip.z / clip.w * 0.5 + 0.5, clip.w);
}

float sceneDistance(vec2 uv) {
    float depth = texture(SceneDepth, uv).r;
    return depth < 1.0 ? stSceneDistance(uv, depth, InvProjMat) : 1e9;
}

vec3 refracted(vec2 uv, vec2 target, vec2 split, float planeDistance) {
    if (sceneDistance(target) < planeDistance) return texture(SceneColor, uv).rgb;
    if (Detail == 0) return texture(SceneColor, clamp(target, 0.001, 0.999)).rgb;
    return vec3(texture(SceneColor, clamp(target + split, 0.001, 0.999)).r,
                texture(SceneColor, clamp(target, 0.001, 0.999)).g,
                texture(SceneColor, clamp(target - split, 0.001, 0.999)).b);
}

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec3 rdWorld = stViewRay(uv, InvProjMat, ModelViewMat);
    vec3 ro = Basis * -CenterOffset;
    vec3 rd = Basis * rdWorld;
    if (abs(rd.z) < 1e-5) discard;
    float t = -ro.z / rd.z;
    if (t <= 0.0) discard;
    vec3 hit = ro + rd * t;
    if (abs(hit.x) > Bounds.x || abs(hit.y) > Bounds.y) discard;
    float sceneDepth = texture(SceneDepth, uv).r;
    if (sceneDistance(uv) < t) discard;

    Rift r;
    r.shape = Shape;
    r.width = Width;
    r.glow = Glow;
    r.lens = Lens;
    r.turbulence = Turbulence;
    r.zip = Zip;
    r.flash = Flash;
    r.edgeColour = EdgeColour;
    r.haloColour = HaloColour;
    r.voidColour = VoidColour;
    r.time = RiftTime;
    r.seed = Seed;
    r.detail = Detail;

    float side;
    float d = rfDistance(hit.xy, r, side);
    if (d > Shape.y) discard;
    float fade = 1.0 - smoothstep(Bounds.y - 1.4, Bounds.y, abs(hit.y));
    fade *= 1.0 - smoothstep(Bounds.x - 1.2, Bounds.x, abs(hit.x));
    fade *= fade;
    float edgeFade = (1.0 - smoothstep(Bounds.y - 0.8, Bounds.y, abs(hit.y))) * (1.0 - smoothstep(Bounds.x - 0.5, Bounds.x, abs(hit.x)));
    vec3 edge = rfEdge(d, hit.xy, r) * edgeFade;
    vec3 hitWorld = rdWorld * t;

    if (d < 0.0) {
        vec3 colour = rfInterior(rdWorld, d, r) + edge;
        gl_FragDepth = project(hitWorld).z;
        fragColor = vec4(stShoulder(colour), 1.0);
        return;
    }

    vec3 across = transpose(Basis) * vec3(side, 0.0, 0.0);
    float haze = 1.0 + 0.35 * (stNoise(vec3(hit.xy * 3.0, RiftTime * 1.7)) - 0.5);
    float shift = rfLens(d, r) * haze * fade;
    vec2 target = project(hitWorld + across * shift).xy;
    vec2 split = (target - uv) * 0.08;
    float alpha = (1.0 - smoothstep(0.0, Shape.y, d)) * fade * stScreenFade(target);
    vec3 scene = refracted(uv, target, split, t);
    if (alpha < 0.002 && max(edge.r, max(edge.g, edge.b)) < 0.5 / 255.0) discard;
    gl_FragDepth = sceneDepth;
    fragColor = vec4(scene * alpha + stShoulder(edge), alpha);
}
