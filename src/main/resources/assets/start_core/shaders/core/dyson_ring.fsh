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

uniform float RingIndex;
uniform float RingSweep;
uniform float RingBase;
uniform float RingSpacing;
uniform float RingWidth;
uniform float RingThickness;
uniform float RingGlow;
uniform float RingHeat;
uniform float RingAmbient;
uniform vec3 RingNormal0;
uniform vec3 RingNormal1;
uniform vec3 RingNormal2;
uniform vec3 RingNormal3;
uniform vec3 RingNormal4;
uniform vec3 RingNormal5;

in vec3 vPos;
in vec3 vNormal;
in vec2 vUv;
in float vInner;

out vec4 fragColor;

void main() {
    if (vUv.x > RingSweep) discard;
    DysonStar s = DysonStar(Radius, Color, Time, Activity, Giant, Heat, Brightness, Protostar, Detail, Seed);
    DysonRings rings = DysonRings(6, RingWidth, RingThickness, RingGlow, RingHeat, RingAmbient);
    vec3 normals[DS_MAX_RINGS] = vec3[](RingNormal0, RingNormal1, RingNormal2, RingNormal3, RingNormal4,
            RingNormal5, RingNormal5, RingNormal5);
    float radii[DS_MAX_RINGS];
    for (int i = 0; i < DS_MAX_RINGS; i++) radii[i] = RingBase + RingSpacing * float(i);

    int self = int(RingIndex + 0.5);
    vec3 viewDir = normalize(vPos + CenterOffset);
    vec3 color = dsRingShade(vPos, normalize(vNormal), viewDir, vUv, vInner > 0.5, self, normals, radii, rings, s);
    color += dsWeldGlow(vUv.x, RingSweep, radii[self], dsTheme(s));
    fragColor = vec4(stShoulder(color), 1.0);
}
