#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform float u_time;

uniform sampler2D u_texture;

void main() {
	vec2 offset = vec2(sin(u_time * 3.0) * 0.005, cos(u_time * 3.0) * 0.005);
	vec4 color;
	color.r = texture(u_texture, v_texCoords + offset).r;
	color.g = texture(u_texture, v_texCoords).g;
	color.b = texture(u_texture, v_texCoords - offset).b;
	gl_FragColor = color;
}
