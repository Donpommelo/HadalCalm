#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
  vec4 color = texture(u_texture, v_texCoords);

  color.r = dot(color.rbg, vec3(.393, .769, .189));
  color.g = dot(color.rbg, vec3(.349, .686, .168));
  color.b = dot(color.rbg, vec3(.272, .534, .131));

  gl_FragColor = v_color * color;
}
