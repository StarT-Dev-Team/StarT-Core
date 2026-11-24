#version 150

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 BoxMat;

void main() {
    gl_Position = ProjMat * ModelViewMat * BoxMat * vec4(Position, 1.0);
}
