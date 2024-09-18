#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform float u_time;

uniform sampler2D u_texture;

void main() {

  vec2 coord = v_texCoords;
  float noise = fract(sin(dot(coord * u_time, vec2(12.9898, 78.233))) * 43758.5453);
  coord += (noise - 0.5) * 0.02;
  gl_FragColor = texture(u_texture, coord);
}
