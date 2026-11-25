#moj_import <start_core:st_noise.glsl>

struct Rift {
    vec2 shape;
    float width;
    float glow;
    float lens;
    float turbulence;
    vec2 zip;
    float flash;
    vec3 edgeColour;
    vec3 haloColour;
    vec3 voidColour;
    float time;
    float seed;
    int detail;
};

const float RF_KNOTS = 11.0;
const float RF_LENS_SHIFT = 0.45;

float rfKnot(float k, Rift r) {
    float base = stHash(vec3(k, r.seed * 97.0, 1.7)) - 0.5;
    float rate = 0.5 + 0.9 * stHash(vec3(k, r.seed * 31.0, 4.2));
    float drift = sin(r.time * rate + 6.2832 * stHash(vec3(k, 2.0, r.seed * 53.0)));
    return base * 0.5 + drift * 0.07 * r.turbulence;
}

float rfSeam(float y, Rift r) {
    float t = clamp(y / r.shape.x, -1.0, 1.0);
    float s = (t * 0.5 + 0.5) * RF_KNOTS;
    float k = floor(s);
    float offset = mix(rfKnot(k, r), rfKnot(k + 1.0, r), s - k);
    offset += (stNoise(vec3(y * 2.7, r.seed * 13.0, r.time * 0.4)) - 0.5) * 0.1 * (0.5 + r.turbulence);
    return offset * sqrt(max(1.0 - t * t, 0.0));
}

float rfHalfWidth(float y, Rift r) {
    float t = y / r.shape.x;
    float body = pow(max(1.0 - t * t, 0.0), 0.7) * (1.0 + 0.22 * t);
    float ragged = 1.0 + 0.45 * r.turbulence * (stNoise(vec3(y * 5.0, r.time * 1.3, r.seed * 7.0)) - 0.5);
    return r.width * body * ragged;
}

float rfDistance(vec2 p, Rift r, out float side) {
    float y = clamp(p.y, r.zip.x * r.shape.x, r.zip.y * r.shape.x);
    float dx = p.x - rfSeam(y, r);
    side = dx >= 0.0 ? 1.0 : -1.0;
    float along = p.y - y;
    if (abs(along) > 1e-3) return length(vec2(dx, along));
    return abs(dx) - rfHalfWidth(y, r);
}

vec3 rfStarLayer(vec3 p, float density, float twinkle) {
    vec3 cell = floor(p);
    vec3 h = stHash3(cell);
    if (h.x > density) return vec3(0.0);
    vec3 d = 0.25 + 0.5 * stHash3(cell + 41.0) - fract(p);
    float size = 0.003 + 0.011 * h.y * h.y;
    float flicker = 0.75 + 0.25 * sin(twinkle * (1.0 + 3.0 * h.z) + 40.0 * h.y);
    vec3 tint = mix(vec3(0.75, 0.85, 1.0), vec3(1.0, 0.75, 0.95), h.z);
    return tint * exp(-dot(d, d) / size) * (0.7 + 1.8 * h.y) * flicker;
}

vec3 rfInterior(vec3 rd, float d, Rift r) {
    vec3 dir = normalize(rd + vec3(0.0, 0.0, r.seed));
    float n = stFbm(dir * 2.6 + vec3(r.time * 0.02, r.seed * 5.0, 0.0));
    vec3 nebula = vec3(0.05, 0.55, 0.5) * smoothstep(0.42, 0.8, n) * 0.4 + r.haloColour * n * n * 0.25;
    vec3 colour = r.voidColour + nebula;
    colour += rfStarLayer(dir * 34.0, 0.5, r.time * 2.0);
    if (r.detail >= 2) colour += 0.6 * rfStarLayer(dir * 71.0 + 17.0, 0.5, r.time * 3.0);
    colour += r.haloColour * exp(d / 0.3) * 0.18 * r.glow;
    return colour;
}

vec3 rfEdge(float d, vec2 p, Rift r) {
    float a = abs(d);
    vec3 colour = r.edgeColour * exp(-a / 0.03) * 1.8 + mix(r.edgeColour, r.haloColour, 0.6) * exp(-a / 0.09) * 0.6;
    colour += r.haloColour * (d > 0.0 ? exp(-d / 0.35) : exp(d / 0.04)) * 0.5;
    colour *= r.glow;

    float dormant = 1.0 - smoothstep(0.0, 0.35, r.width);
    float column = 1.0 - smoothstep(0.6, 1.0, abs(p.y) / r.shape.x);
    colour += r.haloColour * exp(-max(d, 0.0) / 0.85) * 0.45 * dormant * column * (r.zip.y - r.zip.x) * 0.5;
    float crackReach = 0.45 * clamp(r.width / 0.4, 0.3, 1.0);
    if (r.detail >= 2 && r.width > 0.02 && d > 0.0 && d < crackReach) {
        float flicker = smoothstep(0.5, 0.62, stNoise(vec3(p * 1.3, r.time * 2.2 + r.seed * 9.0)));
        if (flicker > 0.0) {
            vec2 q = p + 0.25 * vec2(stNoise(vec3(p * 3.0, r.seed)), stNoise(vec3(p * 3.0, r.seed + 5.0)));
            vec2 v = stVoronoi(vec3(q * vec2(3.2, 1.6), r.seed * 11.0));
            float taper = 1.0 - d / crackReach;
            float crack = smoothstep(0.045 * taper, 0.0, v.y - v.x);
            colour += r.edgeColour * crack * taper * flicker * r.glow * 1.3;
        }
    }

    float zipped = 1.0 - (r.zip.y - r.zip.x) * 0.5;
    if (zipped > 0.001) {
        vec2 lo = vec2(rfSeam(r.zip.x * r.shape.x, r), r.zip.x * r.shape.x);
        vec2 hi = vec2(rfSeam(r.zip.y * r.shape.x, r), r.zip.y * r.shape.x);
        float spark = exp(-dot(p - lo, p - lo) / 0.04) + exp(-dot(p - hi, p - hi) / 0.04);
        colour += r.edgeColour * spark * 2.5 * zipped * r.glow;
    }
    colour += r.edgeColour * r.flash * (exp(-dot(p, p) / 0.08) * 3.0 + exp(-dot(p, p) / 1.2) * 0.6);
    return colour;
}

float rfLens(float d, Rift r) {
    float k = 1.0 - smoothstep(0.0, r.shape.y, max(d, 0.0));
    return r.lens * RF_LENS_SHIFT * k * k;
}
