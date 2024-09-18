#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform vec3 u_player;
uniform float u_light;
uniform float u_time;

const float outerRadius = .20, innerRadius = .10, intensity = 1.0;

void main() {
	vec4 color = texture(u_texture, v_texCoords) * v_color;
	vec2 coord = gl_FragCoord.xy / u_resolution;
	vec2 playerCoord = u_player.xy / u_resolution;

	float len = length(coord - playerCoord);
	float vignette = smoothstep(outerRadius + u_light, innerRadius + u_light, len);
	color.rgb = mix(color.rgb, color.rgb * vignette, intensity);
	gl_FragColor = color;
}
