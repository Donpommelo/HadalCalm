#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float completion;

void main() {
  vec4 color = texture(u_texture, v_texCoords);
  color.a = mix(color.a, 0.0, completion);
  gl_FragColor = v_color * color;
}
