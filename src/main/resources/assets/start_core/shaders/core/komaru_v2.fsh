#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform vec2 ScreenSize;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 InvProjMat;
uniform mat3 InvViewRotMat;
uniform vec3 CameraPosition;

in vec3 vOrigin;
in vec3 vDirection;

out vec4 outColor;

vec3 ro, rd, cd;
void computeRay() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec2 ndc = uv * 2.0 - 1.0; // ndc.y = -ndc.y;
    vec4 clip = vec4(ndc, -1.0, 1.0);
    vec4 view = InvProjMat * clip;
    rd = normalize(mat3(InvViewRotMat) * view.xyz);
    ro = CameraPosition;

    vec4 cameraView = InvProjMat * vec4(0, 0, -1, 1);
    cd = normalize(mat3(InvViewRotMat) * cameraView.xyz);
}


void main() {
    computeRay();

    outColor = vec4(1.0, 0.0, 0.0, 1.0);
}
