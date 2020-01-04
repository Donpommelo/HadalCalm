#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform float u_time;

void main() {
	vec2 coord = gl_FragCoord.xy / u_resolution;

	float color = 0.0;

	color += sin(u_time + coord.y * 5.0 + cos(u_time + coord.x * 10.0 + sin(u_time + coord.y * 15.0 + cos(u_time + coord.x * 20.0 + sin(u_time * 2.0 + coord.y * 25.0)))));

	gl_FragColor = vec4(vec3(color - coord.x + coord.y, color + coord.x - coord.y, .5), 1.0);
}
