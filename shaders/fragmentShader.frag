#version 330 core

uniform sampler2D terrainTexture;

in vec3 exColor;
in vec2 exTexCoord;

out vec4 gl_FragColor;

void main() {
	gl_FragColor = texture(terrainTexture, exTexCoord) * vec4(exColor, 1.0);
    //gl_FragColor = vec4(exColor.x,exColor.y,exColor.z,1.0f);
}