#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
  vec4 color = texture2D(u_texture, v_texCoords);
  color.a = mix(color.a, 0.0, 0.7);
  gl_FragColor = v_color * color;
}
