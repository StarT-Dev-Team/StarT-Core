#moj_import <start_core:st_noise.glsl>

const int NS_MAX_STEPS = 100;
const float NS_MAX_DIST = 100.0;
const float NS_HIT_DIST = 50.0;
const float NS_FBM_MAX = 0.875;

struct NeutronStar {
    mat3 starMat;
    mat3 jetMat;
    mat3 loopMat[8];
    float loopGrow[8];
    float surfaceTime;
    float starRadius;
    vec4 surface;
    vec3 surfaceFlow;
    vec2 jetWidth;
    float jetLength;
    vec4 loopShape;
    float loopMinor;
    float beadPhase;
    float beadRadius;
    vec4 glowFalloff;
    vec4 glowWeight;
    int steps;
    float surfDist;
};

struct NSResult {
    float distance;
    float glow;
    bool hit;
    int mat;
    float weight;
};

float nsHash2D(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float nsNoise2D(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(nsHash2D(i + vec2(0.0, 0.0)), nsHash2D(i + vec2(1.0, 0.0)), u.x),
               mix(nsHash2D(i + vec2(0.0, 1.0)), nsHash2D(i + vec2(1.0, 1.0)), u.x), u.y);
}

float nsFbm2D(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 3; i++) {
        value += amplitude * nsNoise2D(p);
        p *= 2.5;
        amplitude *= 0.5;
    }
    return value;
}

float nsStolb(vec3 q, vec2 width) {
    float radius = width.x + abs(q.y) * width.y;
    return length(q.xz) - radius;
}

float nsTorus(vec3 p, vec2 t) {
    vec2 q = vec2(length(p.xz) - t.x, p.y);
    return length(q) - t.y;
}

float nsSurface(vec3 s, NeutronStar ns) {
    vec3 n = normalize(s);
    vec2 uv = vec2(atan(n.z, n.x), n.y);
    float t = ns.surfaceTime;
    vec2 warp = vec2(nsNoise2D(uv * ns.surface.z + t * ns.surfaceFlow.x),
                     nsNoise2D(uv * ns.surface.z + t * ns.surfaceFlow.y));
    return nsFbm2D(uv * ns.surface.y + warp * ns.surface.w + t * ns.surfaceFlow.z) * ns.surface.x;
}

void nsBeads(NeutronStar ns, out vec3 beads[8]) {
    for (int i = 0; i < 8; i++) {
        float t = ns.beadPhase + float(i);
        beads[i] = vec3((-ns.loopShape.x + cos(t) * ns.loopShape.y) / ns.loopShape.z, 0.0,
                        (sin(t) * ns.loopShape.y) / ns.loopShape.w);
    }
}

float nsGetDist(vec3 p, NeutronStar ns, vec3 beads[8], out int mat, out float jetY) {
    float r = length(p);

    vec3 qj = ns.jetMat * p;
    jetY = qj.y;
    float jet = max(nsStolb(qj, ns.jetWidth), abs(qj.y) - ns.jetLength);

    float loopReach = ns.loopShape.x + ns.loopShape.y + ns.loopMinor;
    float loopScale = min(1.0, min(ns.loopShape.z, ns.loopShape.w));
    float starUpper = r - ns.starRadius;

    float fields = 1e5;
    float beadDist = 1e5;
    float threshold = min(jet, starUpper);
    float loopsLower = min(loopScale * r - loopReach, r - loopReach / loopScale - ns.beadRadius);
    if (loopsLower < threshold) {
        for (int i = 0; i < 8; i++) {
            float grow = ns.loopGrow[i];
            if (grow <= 0.0) continue;
            mat3 m = ns.loopMat[i];
            float height = abs(m[0][1] * p.x + m[1][1] * p.y + m[2][1] * p.z);
            if (height - max(grow * ns.loopMinor, ns.beadRadius) >= min(threshold, fields)) continue;
            vec3 q = m * p / grow;
            vec3 qt = q;
            qt.z *= ns.loopShape.w;
            qt.x *= ns.loopShape.z;
            float magnit = nsTorus(qt + vec3(ns.loopShape.x, 0.0, 0.0), vec2(ns.loopShape.y, ns.loopMinor)) * grow;
            float bead = length(q - beads[i]) * grow - ns.beadRadius;
            fields = min(fields, magnit);
            beadDist = min(beadDist, bead);
        }
    }

    float planet = 1e5;
    if (r - ns.starRadius - ns.surface.x * NS_FBM_MAX < min(jet, min(fields, beadDist))) {
        vec3 s = ns.starMat * p;
        planet = length(s) - (ns.starRadius + nsSurface(s, ns));
    }

    float d = planet;
    mat = 1;
    if (jet < d) {
        d = jet;
        mat = 4;
    }
    if (fields < d) {
        d = fields;
        mat = 2;
    }
    if (beadDist < d) {
        d = beadDist;
        mat = 3;
    }
    return d;
}

NSResult nsMarch(vec3 ro, vec3 rd, float tMin, float tMax, NeutronStar ns) {
    vec3 beads[8];
    nsBeads(ns, beads);
    float limit = min(tMax, NS_MAX_DIST);
    float d = max(tMin, 0.0);
    float glow = 0.0;
    int mat = 1;
    float weight = ns.glowWeight.x;
    for (int i = 0; i < NS_MAX_STEPS; i++) {
        if (i >= ns.steps) break;
        float jetY;
        float ds = nsGetDist(ro + rd * d, ns, beads, mat, jetY);
        d += ds;
        float falloff;
        if (mat == 1) {
            weight = ns.glowWeight.x;
            falloff = ns.glowFalloff.x;
        } else if (mat == 2) {
            weight = ns.glowWeight.y;
            falloff = ns.glowFalloff.y;
        } else if (mat == 3) {
            weight = ns.glowWeight.z;
            falloff = ns.glowFalloff.z;
        } else {
            weight = ns.glowWeight.w * (1.0 - smoothstep(0.8 * ns.jetLength, ns.jetLength, abs(jetY)));
            falloff = ns.glowFalloff.w;
        }
        glow += weight / (abs(ds) + falloff);
        if (d > limit || abs(ds) < ns.surfDist) break;
    }
    return NSResult(d, glow, d <= limit && d < NS_HIT_DIST, mat, weight);
}

float nsHitAlpha(NSResult result) {
    return result.mat == 2 || result.mat == 3 ? 0.0 : clamp(result.weight, 0.0, 1.0);
}

vec2 nsCylinder(vec3 ro, vec3 rd, float halfLength, float radius) {
    vec2 slab = vec2(-1e9, 1e9);
    if (abs(rd.y) > 1e-8) {
        vec2 t = (vec2(-halfLength, halfLength) - ro.y) / rd.y;
        slab = vec2(min(t.x, t.y), max(t.x, t.y));
    } else if (abs(ro.y) > halfLength) {
        return vec2(1e9, -1e9);
    }
    float a = dot(rd.xz, rd.xz);
    float b = dot(ro.xz, rd.xz);
    float c = dot(ro.xz, ro.xz) - radius * radius;
    vec2 tube = vec2(-1e9, 1e9);
    if (a > 1e-10) {
        float h = b * b - a * c;
        if (h < 0.0) return vec2(1e9, -1e9);
        h = sqrt(h);
        tube = vec2(-b - h, -b + h) / a;
    } else if (c > 0.0) {
        return vec2(1e9, -1e9);
    }
    return vec2(max(slab.x, tube.x), min(slab.y, tube.y));
}

vec2 nsBounds(vec3 ro, vec3 rd, float haloRadius, float jetHalfLength, float jetRadius) {
    vec2 range = vec2(1e9, -1e9);
    float b = dot(ro, rd);
    float h = b * b - (dot(ro, ro) - haloRadius * haloRadius);
    if (h > 0.0) range = vec2(-b - sqrt(h), -b + sqrt(h));
    vec2 jet = nsCylinder(ro, rd, jetHalfLength, jetRadius);
    if (jet.x < jet.y) range = vec2(min(range.x, jet.x), max(range.y, jet.y));
    return range;
}

vec2 nsClosest(vec3 ro, vec3 rd) {
    float b = -dot(ro, rd);
    return vec2(b, length(ro + rd * b));
}

vec3 nsGlare(vec3 ro, vec3 rd, float flash, vec3 color) {
    vec2 c = nsClosest(ro, rd);
    if (flash <= 0.0 || c.x < 0.0) return vec3(0.0);
    return mix(color, vec3(1.0), 0.5) * flash * 0.03 / (c.y * c.y + 0.03);
}

vec3 nsShell(vec3 ro, vec3 rd, float radius, float intensity, vec3 color, float time) {
    if (intensity <= 0.0 || radius <= 0.0) return vec3(0.0);
    vec2 c = nsClosest(ro, rd);
    float x = c.y / radius;
    float limb = x < 1.0 ? inversesqrt(max(1.0 - x * x, 0.03)) : 0.0;
    float rim = exp(-(x - 1.0) * (x - 1.0) * 100.0);
    float grain = 0.7 + 0.6 * stNoise(normalize(ro + rd * c.x + vec3(1e-4)) * 6.0 + time * 0.5);
    return mix(color, vec3(1.0), 0.35) * intensity * (0.12 * limb + rim) * grain;
}
