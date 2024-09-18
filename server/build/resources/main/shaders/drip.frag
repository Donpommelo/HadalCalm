#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform float u_time;

void main() {
	vec2 coord = gl_FragCoord.xy / u_resolution;
	float aTime = u_time * 0.25;

	float color = 0.0;

	color += sin(coord.x * 50.0 + cos(aTime + coord.y * 10.0 + sin(coord.x * 50.0 + aTime * 1.0)));
	color += cos(coord.x * 20.0 + sin(aTime + coord.y * 10.0 + cos(coord.x * 50.0 + aTime * 2.0)));
	color += sin(coord.x * 30.0 + cos(aTime + coord.y * 10.0 + sin(coord.x * 50.0 + aTime * 3.0)));
	color += cos(coord.x * 10.0 + sin(aTime + coord.y * 10.0 + cos(coord.x * 50.0 + aTime * 4.0)));

	gl_FragColor = vec4(vec3(color, color, 0.4), 1.0);
}
