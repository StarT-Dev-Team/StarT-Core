#version 150

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 CenterOffset;
uniform float Extent;

out vec3 vWorld;
out vec2 vQuad;

void main() {
    vec3 forward = normalize(CenterOffset);
    vec3 reference = abs(forward.y) < 0.99 ? vec3(0.0, 1.0, 0.0) : vec3(1.0, 0.0, 0.0);
    vec3 right = normalize(cross(forward, reference));
    vec3 up = cross(right, forward);
    vQuad = Position.xy;
    vWorld = CenterOffset + (right * Position.x + up * Position.y) * Extent;
    gl_Position = ProjMat * ModelViewMat * vec4(vWorld, 1.0);
}
