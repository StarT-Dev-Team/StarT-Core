#version 150

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 CenterOffset;
uniform float Radius;

out vec3 vLocal;

void main() {
    vLocal = Position;
    gl_Position = ProjMat * ModelViewMat * vec4(Position * Radius + CenterOffset, 1.0);
}
