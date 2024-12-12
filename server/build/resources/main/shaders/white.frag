#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
  vec4 color = texture(u_texture, v_texCoords) * v_color;
  color.rgb = vec3(1, 1, 1);
  gl_FragColor = color;
}
