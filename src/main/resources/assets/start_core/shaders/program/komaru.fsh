#version 330

uniform mat4 GameInvProjMat;
uniform mat4 GameInvViewRotMat;
uniform vec3 CameraPosition;
uniform float CameraNearPlane;
uniform float CameraFarPlane;
uniform float Time;
uniform float GameTime;

uniform vec3 BeamOrigin;
uniform int AnimationTicks;
uniform int AnimationType;

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform samplerCube CubeMapSampler;

in vec2 uv;

out vec4 fragColor;

// --- import start_komaru.glsl

#define PI 3.1415926535897932384626433832795

// easing

float exponentialOut(float t) {
    return t == 1.0 ? t : 1.0 - pow(2.0, -10.0 * t);
}

// noise

mat3 noiseMap = mat3(0.00, 0.80, 0.60, -0.80, 0.36, -0.48, -0.60, -0.48, 0.64);

float hash(float n) {
    return fract(sin(n)*43758.5453);
}

float noise(in vec3 x) {
    vec3 p = floor(x);
    vec3 f = fract(x);

    f = f*f*(3.0-2.0*f);

    float n = p.x + p.y*57.0 + 113.0*p.z;

    float res = mix(mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),
                        mix( hash(n+ 57.0), hash(n+ 58.0),f.x),f.y),
                    mix(mix( hash(n+113.0), hash(n+114.0),f.x),
                        mix( hash(n+170.0), hash(n+171.0),f.x),f.y),f.z);
    return res;
}

float fbm(vec3 p) {
    float f;
    f  = 0.5000*noise(p); p = noiseMap*p*2.02;
    f += 0.2500*noise(p); p = noiseMap*p*2.03;
    f += 0.12500*noise(p); p = noiseMap*p*2.01;
    f += 0.06250*noise(p);
    return f;
}

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

vec2 rotate(vec2 v, float a) {
    float s = sin(a);
    float c = cos(a);
    mat2 m = mat2(c, s, -s, c);
    return m * v;
}

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

float sdBox(vec3 p, vec3 b) {
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) + min(max(q.x,max(q.y,q.z)),0.0);
}

vec2 opSubtraction(vec2 d2, vec2 d1) {
    d1.x = -d1.x;
    return d1.x > d2.x ? d1 : d2;
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
    float perc = clamp((value - min1) / (max1 - min1), 0.0, 1.0);
    return perc * (max2 - min2) + min2;
}

vec2 map(vec3 p) {
    float realTime = float(AnimationTicks) / 20. + Time;
    float rayAnimationProgress = mapValue(realTime, 0.0, 2.5, 0.0, 1.0);

    float sdf1H = 23. - 5.;
    vec3 sdf1Pos = BeamOrigin + vec3(0, 5, 0);
    vec2 sdf1 = vec2(sdRoundCone(p - sdf1Pos, .1, 2., sdf1H), 1);

    vec3 sdf2Pos = BeamOrigin + vec3(0, 23, 0);
    vec2 sdf2 = vec2(sdSphere(p - sdf2Pos, 2.5), 1);

    float sdf3H = 38. - 23.;
    vec3 sdf3Pos = BeamOrigin + vec3(0, 23, 0);
    vec2 sdf3 = vec2(sdRoundCone(p - sdf3Pos, 3. / 2., 2.3 / 2., sdf3H), 1);

    float sdf4H = 133. - 36. - 3.;
    vec3 sdf4Pos = BeamOrigin + vec3(0, 36. + 3., 0);
    vec2 sdf4 = vec2(sdVerticalCapsule(p - sdf4Pos, sdf4H, 2.3 / 2.), 1);

    float sphereY = mapValue(rayAnimationProgress, 0.0, 1.0, 36. + 3., 133.);
    vec3 sdf5Pos = BeamOrigin + vec3(0, sphereY, 0);
    float sphereRadius = mapValue(rayAnimationProgress, 0.0, 1.0, 2, 19.0);
    vec2 sdf5 = vec2(sdSphere(p - sdf5Pos, sphereRadius), 2);

    vec2 res = opSmoothUnion(0.5, sdf1,
        opSmoothUnion(0.5, sdf2,
            opSmoothUnion(0.1, sdf3,
                opSmoothUnion(mapValue(rayAnimationProgress, 0.0, 1.0, 0.2, 4.0), sdf4, sdf5))));
    res.x += sin(5 * p.y + -realTime * 2. * PI) * .025;

    vec3 sdfRingPos = BeamOrigin + vec3(0, 2, 0);
    vec3 sdfRingRelative = p - sdfRingPos;
    vec2 sdfRing = vec2(sdTorus(sdfRingRelative, vec2(21, 1)), 3);
    float angle1 = atan(sdfRingRelative.z, sdfRingRelative.x);
    sdfRing.x += sin(30 * angle1 + realTime * 2. * PI) * 0.1;

    vec3 sdfRingBoxPos = BeamOrigin + vec3(0, 2, 0);
    vec2 sdfRingBox1 = vec2(sdBox(p - sdfRingBoxPos, vec3(2, 2, 30)), 3.0);
    vec2 sdfRingBox2 = vec2(sdBox(p - sdfRingBoxPos, vec3(30, 2, 2)), 3.0);

    vec2 res2 = opSubtraction(opSubtraction(sdfRing, sdfRingBox1), sdfRingBox2);

    return opUnion(res, res2);
}

float calcHitDepth(float distance) {
    float z = distance * dot(cd, rd);
    float ndcDepth = -((CameraFarPlane + CameraNearPlane) / (CameraNearPlane - CameraFarPlane)) + ((2.0 * CameraFarPlane * CameraNearPlane) / (CameraNearPlane - CameraFarPlane)) / z;
    return (ndcDepth + 1.) / 2.;
}

float linearizeDepth01(float depth) {
    return CameraNearPlane * CameraFarPlane / (CameraFarPlane + depth * (CameraNearPlane - CameraFarPlane));
}

float linearizeDepth11(float depth) {
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

vec2 sphIntersect(in vec3 ro, in vec3 rd, in vec3 ce, float ra) {
    vec3 oc = ro - ce;
    float b = dot( oc, rd );
    vec3 qc = oc - b*rd;
    float h = ra*ra - dot( qc, qc );
    if( h<0.0 ) return vec2(-1.0); // no intersection
    h = sqrt( h );
    return vec2( -b-h, -b+h );
}

#define MAX_STEPS 70
#define SHADOW_STEPS 40
#define SHADOW_LENGTH 10.

float komaruSmokeDensity(in vec3 p) {
    float time = GameTime * 400.;
    vec3 torusPos = BeamOrigin + vec3(0, 133, 0);

    vec3 q = (p - torusPos) * 0.3;
    q.xz = rotate(q.xz, length(q.xz) * 0.5 - time * 0.4);
    q -= vec3(0, 1.2, 0) * time;

    float density = fbm(q);
    float a = 1.0 / 3.0;
    float phi = sdTorus((p - torusPos), vec2(27.0, 7.0));
    density *= 1 / (1 + exp(a * phi));

    return clamp(density, 0.0, 1.0);
}

vec4 komaruSmokeMarch(vec3 p, vec3 ray, float start, float end) {
    float density = 0.;

    float stepLength = abs(end - start) / float(MAX_STEPS);
    float shadowStepLength = SHADOW_LENGTH / float(SHADOW_STEPS);

    vec4 sum = vec4(0., 0., 0., 1.);

    float k = start;
    for (int i = 0; i < MAX_STEPS && k <= end; i++)
    {
        if (sum.a < 0.1) {
            break;
        }
        vec3 pos = p + ray * k;
        float d = komaruSmokeDensity(pos);

        if(d > 0.01)
        {
            vec3 torusPos = BeamOrigin + vec3(0, 133, 0);
            vec3 light = -normalize(pos - torusPos);
            vec3 lpos = pos + light * shadowStepLength;
            float shadow = 0.;

            for (int s = 0; s < SHADOW_STEPS; s++)
            {
                lpos += light * shadowStepLength;
                float lsample = komaruSmokeDensity(lpos);
                shadow += lsample;
            }

            density = clamp((d / float(MAX_STEPS)) * 20.0, 0.0, 1.0);
            float s = exp((-shadow / float(SHADOW_STEPS)) * 5.);
            sum.rgb += vec3(s * density) * vec3(2.5, .5, .5) * sum.a;
            sum.a *= 1. - density;

            sum.rgb += exp(-komaruSmokeDensity(pos + vec3(0,0.25,0.0)) * .2) * density * vec3(0.15, 0.45, 1.1) * sum.a;
        }

        k += stepLength;
    }

    return sum;
}

float diskIntersect(in vec3 ro, in vec3 rd, vec3 c, vec3 n, float r)  {
    vec3  o = ro - c;
    float t = -dot(n,o)/dot(rd,n);
    vec3  q = o + rd*t;
    return (dot(q,q)<r*r) ? t : -1.0;
}

vec3 blendNormal(vec3 dst, vec4 src) {
    return (dst * (1.0 - src.a)) + (src.rgb * src.a);
}

vec4 blendOver(vec4 a, vec4 b) {
    float newAlpha = mix(b.a, 1.0, a.a);
    vec3 newColor = mix(b.a * b.rgb, a.rgb, a.a);
    float divideFactor = (newAlpha > 0.001 ? (1.0 / newAlpha) : 1.0);
    return vec4(divideFactor * newColor, newAlpha);
}

void komaruMain(in float solidDepth, out vec4 color, out vec3 normal, out float depth) {
    color = vec4(0);
    normal = vec3(0);
    depth = -1;

    float glowTotal = 0;

    float realTime = float(AnimationTicks) / 20. + Time;

    float sphereRadiusPerc = mapValue(realTime, 2.5, 5.0, 0.0, 1.0);
    float sphereRadius = AnimationType == 1 ? mapValue(exponentialOut(sphereRadiusPerc), 0.0, 1.0, 0.1, 17.0) : mapValue(exponentialOut(sphereRadiusPerc), 0.0, 1.0, 17.0, 0.1);

    // BALL
    vec2 disk = sphIntersect(ro, rd, BeamOrigin + vec3(0, 133, 0), sphereRadius);
    float diskMin = min(disk.x, disk.y);
    if (disk.y > 0. && (disk.x < 0. || disk.x < solidDepth)) {
        color = vec4(texture(CubeMapSampler, rd).rgb, 1.0);
        if (disk.x < 0.) {
            solidDepth = disk.y;
        } else {
            solidDepth = disk.x;
        }
    }

    // RAY
    float t = CameraNearPlane;
    for (int i = 0; i < 120 && t < CameraFarPlane; i++) {
        vec3 p = ro + rd * t;
        vec2 d = map(p);

        t += d.x;
        if (t > solidDepth) {
            break;
        }
        if (abs(d.x) >= 0.005) {
            // float glowBit = 5e-3 / pow(d.x, 2.0);
            // glowTotal += glowBit;
            continue;
        }

        vec4 newColor = vec4(0.145, 0.051, 0.227, .2);
        if (d.y == 2) newColor = vec4(0.145, 0.051, 0.227, .05);
        if (d.y == 3) newColor = vec4(1.0, 1.0, 0.0, .2);

        vec3 hitPoint = ro + rd * (t - d.x);
        normal = mapNormal(hitPoint);
        depth = calcHitDepth(t);

        float k = fresnel(3., normal, -rd);
        newColor = mix(newColor, vec4(1.00, 0.40, 0.70, .1), k);

        if (color.a > 0.999) {
            color = vec4(blendNormal(color.rgb, newColor), 1.0);
        } else {
            color = newColor;
        }

        break;
    }

    // DISK
    //    vec2 sphI = sphIntersect(ro, rd, BeamOrigin + vec3(0, 133, 0), 40.);
    //    if (sphI.y > 0. && (sphI.x < 0. || sphI.x < solidDepth)) {
    //        vec4 cloudColor = komaruSmokeMarch(ro, rd, max(sphI.x, 0.), min(solidDepth, sphI.y));
    //        cloudColor.a = clamp(1 - pow(cloudColor.a, 3.0), 0.0, 1.0);
    //        if (cloudColor.a > 0.001) {
    //            if (color.a > 0.999) {
    //                color = vec4(blendNormal(color.rgb, cloudColor), 1.0);
    //            } else {
    //                color = blendOver(color, cloudColor);
    //            }
    //        }
    //        // depth = sphI.x;
    //    }
}

// --- end import start_komaru.glsl

vec3 mcBlend(vec3 dst, vec4 src) {
    return (dst * (1.0 - src.a)) + src.rgb;
}

float calculateRealSolidDepth() {
    float depth = texture(DiffuseDepthSampler, uv).r;
    float linearDepth = linearizeDepth01(depth);
    return linearDepth / dot(rd, cd);
}

void main() {
    computeRay();

    float depth = calculateRealSolidDepth();
    vec3 diffuse = texture(DiffuseSampler, uv).rgb;

    vec4 sdfColor = vec4(0);
    vec3 sdfNormal = vec3(0);
    float sdfDepth = 0;
    komaruMain(depth, sdfColor, sdfNormal, sdfDepth);

    if (sdfColor.a > 0.01) {
        fragColor = vec4(mcBlend(diffuse, sdfColor), 1.0);
    } else {
        fragColor = vec4(diffuse, 1.0);
    }
}
