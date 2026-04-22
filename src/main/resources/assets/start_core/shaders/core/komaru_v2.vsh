#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform vec2 ScreenSize;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 InvProjMat;
uniform mat3 InvViewRotMat;
uniform vec3 CameraPosition;

in vec3 Position;

out vec3 vOrigin;
out vec3 vDirection;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vOrigin = vec3(0, 0, 0);
    vDirection = vec3(0, 0, 0);
}
