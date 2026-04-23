#version 330

uniform mat4 GameInvProjMat;
uniform mat4 GameInvViewRotMat;
uniform vec3 CameraPosition;
uniform float CameraNearPlane;
uniform float CameraFarPlane;
uniform float Time;
uniform float GameTime;

uniform vec3 BeamOrigin;

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D TranslucentSampler;
uniform sampler2D TranslucentDepthSampler;
uniform sampler2D ItemEntitySampler;
uniform sampler2D ItemEntityDepthSampler;
uniform sampler2D ParticlesSampler;
uniform sampler2D ParticlesDepthSampler;
uniform sampler2D CloudsSampler;
uniform sampler2D CloudsDepthSampler;
uniform sampler2D WeatherSampler;
uniform sampler2D WeatherDepthSampler;

in vec2 uv;

out vec4 fragColor;

// --- import start_komaru.glsl

#define PI 3.1415926535897932384626433832795

vec3 ro, rd, cd;
void computeRay() {
    vec2 ndc = uv * 2.0 - 1.0; // ndc.y = -ndc.y;
    vec4 clip = vec4(ndc, -1.0, 1.0);
    vec4 view = GameInvProjMat * clip;
    rd = normalize(mat3(GameInvViewRotMat) * view.xyz);
    ro = CameraPosition;

    vec4 cameraView = GameInvProjMat * vec4(0, 0, -1, 1);
    cd = normalize(mat3(GameInvViewRotMat) * cameraView.xyz);
}

float dot2(in vec2 v) { return dot(v, v); }
float dot2(in vec3 v) { return dot(v, v); }

float sdSphere(vec3 p, float s) {
    return length(p) - s;
}

float sdVerticalCapsule(vec3 p, float h, float r) {
    p.y -= clamp(p.y, 0.0, h);
    return length(p) - r;
}

float sdCappedCylinder(vec3 p, float r, float h) {
    vec2 d = abs(vec2(length(p.xz), p.y)) - vec2(r, h);
    return min(max(d.x, d.y), 0.0) + length(max(d, 0.0));
}

float sdCone(vec3 p, vec2 q) {
    vec2 w = vec2(length(p.xz), p.y);
    vec2 a = w - q * clamp(dot(w, q) / dot(q, q), 0.0, 1.0);
    vec2 b = w - q * vec2(clamp(w.x / q.x, 0.0, 1.0), 1.0);
    float k = sign(q.y);
    float d = min(dot(a, a), dot(b, b));
    float s = max(k * (w.x * q.y - w.y * q.x), k * (w.y - q.y));
    return sqrt(d) * sign(s);
}

float sdRoundCone(vec3 p, float r1, float r2, float h) {
    float b = (r1 - r2) / h;
    float a = sqrt(1.0 - b * b);

    vec2 q = vec2(length(p.xz), p.y);
    float k = dot(q, vec2(-b, a));
    if (k < 0.0) return length(q) - r1;
    if (k > a * h) return length(q - vec2(0.0, h)) - r2;
    return dot(q, vec2(a, b)) - r1;
}


float sdCappedCone(vec3 p, float h, float r1, float r2) {
    vec2 q = vec2(length(p.xz), p.y);
    vec2 k1 = vec2(r2, h);
    vec2 k2 = vec2(r2 - r1, 2.0 * h);
    vec2 ca = vec2(q.x - min(q.x, (q.y < 0.0) ? r1 : r2), abs(q.y) - h);
    vec2 cb = q - k1 + k2 * clamp(dot(k1 - q, k2) / dot2(k2), 0.0, 1.0);
    float s = (cb.x < 0.0 && ca.y < 0.0) ? -1.0 : 1.0;
    return s * sqrt(min(dot2(ca), dot2(cb)));
}

float sdTorus(vec3 p, vec2 t) {
    vec2 q = vec2(length(p.xz) - t.x, p.y);
    return length(q) - t.y;
}

vec2 opUnion(vec2 d1, vec2 d2) {
    return d1.x < d2.x ? d1 : d2;
}

float opUnion(float d1, float d2) {
    return d1 < d2 ? d1 : d2;
}

float opSmoothUnion(float k, float d1, float d2) {
    k *= 4.0;
    float h = max(k - abs(d1 - d2), 0.0);
    return min(d1, d2) - h * h * 0.25 / k;
}

vec2 opSmoothUnion(float k, vec2 d1, vec2 d2) {
    k *= 4.0;
    float h = max(k - abs(d1.x - d2.x), 0.0);
    vec2 d = d1.x < d2.x ? d1 : d2;
    d.x -= h * h * 0.25 / k;
    return d;
}

float mapValue(float value, float min1, float max1, float min2, float max2) {
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

/*
ill give you relative to bottom center and then tell controller relative

bottom circle is 41 diameter (inclusive of the center of the "beam") with a diameter of 3 blocks
it is centered at a height of 4 blocks

it cuts out for 5 blocks on each main cardinal axis.

controller is on the center outmost bottom layer, the bottom layer is 63 blocks in diameter

[x] the center beam starts a a point at the center with a height of 5
[x] broadens out to a width of 1 at 13 blocks
[x] it continues to widen to 3 blocks at height of 17
[x] a width of 5 block at a height of 21
[x] at height of 28 it goes back to a width 4
[x] at height of 32 it goes to a width of 3
[x] at 36 height goes to a width of 2.5
[ ] 2.5 width is maintained till height of 133 blocks and the uses next 10 blocks (till height of 143) to fan to rift
[ ] rift should be a width of 57 blocks
*/

vec2 map(vec3 p) {
    float sdf1H = 23. - 5.;
    vec3 sdf1Pos = BeamOrigin + vec3(0, 5, 0);
    vec2 sdf1 = vec2(sdRoundCone(p - sdf1Pos, .1, 2., sdf1H), 1);

    vec3 sdf2Pos = BeamOrigin + vec3(0, 23, 0);
    vec2 sdf2 = vec2(sdSphere(p - sdf2Pos, 2.5), 2);

    float sdf3H = 38. - 23.;
    vec3 sdf3Pos = BeamOrigin + vec3(0, 23, 0);
    vec2 sdf3 = vec2(sdRoundCone(p - sdf3Pos, 3. / 2., 2.3 / 2., sdf3H), 3);

    float sdf4H = 133. - 36. - 3.;
    vec3 sdf4Pos = BeamOrigin + vec3(0, 36. + 3., 0);
    vec2 sdf4 = vec2(sdVerticalCapsule(p - sdf4Pos, sdf4H, 2.3 / 2.), 4);

    float sdf5H = 133. - 36. - 3.;
    vec3 sdf5Pos = BeamOrigin + vec3(0, 133. + 3., 0);
    vec2 sdf5 = vec2(sdVerticalCapsule(p - sdf5Pos, sdf4H, 2.3 / 2.), 4);

    vec2 res = opSmoothUnion(0.5, sdf1, opSmoothUnion(0.5, sdf2, opSmoothUnion(0.1, sdf3, sdf4)));
    res.x += sin(5 * p.y + -Time * 2. * PI) * .025;

    vec3 sdfRingPos = BeamOrigin + vec3(0, 2, 0);
    vec2 sdfRing = vec2(sdTorus(p - sdfRingPos, vec2(21, 1)), 5);

    return opUnion(res, sdfRing);
/*
    float sdf1H = 13. - 5.;
    vec3 sdf1Pos = BeamOrigin + vec3(0, 5, 0);
    vec2 sdf1 = vec2(sdCone(p - sdf1Pos, vec2(.5, sdf1H)), 1);

    float sdf2H = (17. - 13.) / 2.;
    vec3 sdf2Pos = BeamOrigin + vec3(0, 13. + sdf2H - 0.1 , 0);
    vec2 sdf2 = vec2(sdCappedCone(p - sdf2Pos, sdf2H - 0.2, 1. / 2., 3. / 2.), 2);

    float sdf3H = (21. - 17.) / 2.;
    vec3 sdf3Pos = BeamOrigin + vec3(0, 17. + sdf3H, 0);
    vec2 sdf3 = vec2(sdCappedCone(p - sdf3Pos, sdf3H, 3. / 2., 5. / 2.), 3);

    float sdf4H = (28. - 21.) / 2.;
    vec3 sdf4Pos = BeamOrigin + vec3(0, 21. + sdf4H, 0);
    vec2 sdf4 = vec2(sdCappedCone(p - sdf4Pos, sdf4H, 5. / 2., 4. / 2.), 4);

    float sdf5H = (32. - 28.) / 2.;
    vec3 sdf5Pos = BeamOrigin + vec3(0, 28. + sdf5H, 0);
    vec2 sdf5 = vec2(sdCappedCone(p - sdf5Pos, sdf5H, 4. / 2., 3. / 2.), 5);

    float sdf6H = (36. - 32.) / 2.;
    vec3 sdf6Pos = BeamOrigin + vec3(0, 32. + sdf6H, 0);
    vec2 sdf6 = vec2(sdCappedCone(p - sdf6Pos, sdf6H, 3. / 2., 2.5 / 2.), 6);

    float sdf7H = (133. - 36.) / 2.;
    vec3 sdf7Pos = BeamOrigin + vec3(0, 36. + sdf7H, 0);
    vec2 sdf7 = vec2(sdCappedCylinder(p - sdf7Pos, 2.5 / 2., sdf7H), 7);

    float sdf8H = (143. - 133.) / 2.;
    vec3 sdf8Pos = BeamOrigin + vec3(0, 133. + sdf8H, 0);
    vec2 sdf8 = vec2(sdCappedCone(p - sdf8Pos, sdf8H, 2.5 / 2., 57. / 2.), 8);

    vec2 res =
    opSmoothUnion(0.2, sdf1,
      opSmoothUnion(0.05, sdf2,
        opSmoothUnion(0.05, sdf3,
          opSmoothUnion(0.05, sdf4,
            opSmoothUnion(0.05, sdf5,
              opSmoothUnion(0.05, sdf6,
                opSmoothUnion(0.05, sdf7, sdf8)
              )
            )
          )
        )
      )
    );

    // res.x += sin(5 * p.y + -Time * 2. * PI) * .025;

    return res;*/
}

float calcHitDepth(float distance) {
    float z = distance * dot(cd, rd);
    float ndcDepth = -((CameraFarPlane + CameraNearPlane) / (CameraNearPlane - CameraFarPlane)) + ((2.0 * CameraFarPlane * CameraNearPlane) / (CameraNearPlane - CameraFarPlane)) / z;
    return ((1. * ndcDepth) + 0. + 1.) / 2.;
}

float linearizeDepth(float depth) {
    return (2.0 * CameraNearPlane) / (CameraFarPlane + CameraNearPlane - depth * (CameraFarPlane - CameraNearPlane));
}

vec4 blend(vec4 dst, vec4 src) {
    return vec4((dst.rgb * (1.0 - src.a)) + src.rgb * src.a, 1.0);
}

float fresnel(float amount, vec3 normal, vec3 view) {
    return pow(1.0 - clamp(dot(normalize(normal), view), 0., 1.), amount);
}

vec3 mapNormal(vec3 p) {
    const float eps = 0.0001;
    const vec2 h = vec2(eps, 0);
    return normalize(vec3(map(p + h.xyy).x - map(p - h.xyy).x, map(p + h.yxy).x - map(p - h.yxy).x, map(p + h.yyx).x - map(p - h.yyx).x));
}

void komaruMain(in float solidDepth, out vec4 color, out vec3 normal, out float depth) {
    computeRay();

    color = vec4(0);
    normal = vec3(0);
    depth = -1;

    float glowTotal = 0;

    float t = CameraNearPlane;
    for (int i = 0; i < 120 && t < CameraFarPlane; i++) {
        vec3 p = ro + rd * t;
        vec2 d = map(p);

        t += d.x;
        float hitDepth = calcHitDepth(t);

        if (hitDepth > solidDepth) {
            break;
        }
        if (abs(d.x) >= 0.005) {
            // float glowBit = 5e-3 / pow(d.x, 2.0);
            // glowTotal += glowBit;
            continue;
        }

        color = vec4(1.0, 1.0, 1.0, .2);
        if (d.y == 1) color = vec4(0.90, 0.10, 0.10, .2); // Vivid Red
        if (d.y == 2) color = vec4(0.20, 0.40, 1.00, .2); // Azure Blue
        if (d.y == 3) color = vec4(0.10, 0.80, 0.20, .2); // Emerald Green
        if (d.y == 4) color = vec4(1.00, 0.90, 0.10, .2); // Golden Yellow
        if (d.y == 5) color = vec4(1.00, 0.50, 0.00, .2); // Bright Orange
        if (d.y == 6) color = vec4(0.60, 0.20, 0.80, .2); // Deep Purple
        if (d.y == 7) color = vec4(0.10, 0.90, 0.90, .2); // Electric Cyan
        if (d.y == 8) color = vec4(1.00, 0.40, 0.70, .2); // Hot Pink
        if (d.y == 9) color = vec4(0.60, 0.40, 0.20, .2); // Earthy Brown
        if (d.y == 10) color = vec4(0.80, 0.85, 0.90, .2); // Silver/White

        // color = vec4(1.00, 0.40, 0.70, 0.3);

        vec3 hitPoint = ro + rd * (t - d.x);
        normal = mapNormal(hitPoint);
        depth = hitDepth;

        // depth = hitDepth;
        break;
    }

    if (color.a > 0) {
        float k = fresnel(3., normal, -rd);
        color = mix(color, vec4(1.00, 1.0, 1.0, .2), k);
    }
}

// --- end import start_komaru.glsl

vec4 color_layers[7];
float depth_layers[7];
int active_layers = 0;

void try_insert(vec4 color, float depth) {
    if (color.a == 0.0) {
        return;
    }

    color_layers[active_layers] = color;
    depth_layers[active_layers] = depth;

    int jj = active_layers++;
    int ii = jj - 1;
    while (jj > 0 && depth_layers[jj] > depth_layers[ii]) {
        float depthTemp = depth_layers[ii];
        depth_layers[ii] = depth_layers[jj];
        depth_layers[jj] = depthTemp;

        vec4 colorTemp = color_layers[ii];
        color_layers[ii] = color_layers[jj];
        color_layers[jj] = colorTemp;

        jj = ii--;
    }
}

vec3 mcBlend(vec3 dst, vec4 src) {
    return (dst * (1.0 - src.a)) + src.rgb;
}

vec4 computeTransparency(vec4 sdfColor, float sdfDepth) {
    color_layers[0] = vec4(texture(DiffuseSampler, uv).rgb, 1.0);
    depth_layers[0] = texture(DiffuseDepthSampler, uv).r;
    active_layers = 1;

    try_insert(sdfColor, sdfDepth);
    try_insert(texture(TranslucentSampler, uv), texture(TranslucentDepthSampler, uv).r);
    try_insert(texture(ItemEntitySampler, uv), texture(ItemEntityDepthSampler, uv).r);
    try_insert(texture(ParticlesSampler, uv), texture(ParticlesDepthSampler, uv).r);
    try_insert(texture(WeatherSampler, uv), texture(WeatherDepthSampler, uv).r);
    try_insert(texture(CloudsSampler, uv), texture(CloudsDepthSampler, uv).r);

    vec3 texelAccum = color_layers[0].rgb;
    for (int ii = 1; ii < active_layers; ++ii) {
        texelAccum = mcBlend(texelAccum, color_layers[ii]);
    }

    return vec4(texelAccum.rgb, 1.0);
}

void main() {
    float depth = texture(DiffuseDepthSampler, uv).r;
    vec4 original = texture(DiffuseSampler, uv);

    vec4 sdfColor = vec4(0);
    vec3 sdfNormal = vec3(0);
    float sdfDepth = 0;
    komaruMain(depth, sdfColor, sdfNormal, sdfDepth);

    fragColor = computeTransparency(sdfColor, sdfDepth);
}
