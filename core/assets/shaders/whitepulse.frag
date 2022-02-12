#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform float u_time;

uniform sampler2D u_texture;

void main() {
  vec4 color = texture2D(u_texture, v_texCoords);

  float whiteness = (sin(u_time * 10) + 3) / 4;
  color.r = mix(color.r, 0.9, whiteness);
  color.g = mix(color.g, 0.9, whiteness);
  color.b = mix(color.b, 0.9, whiteness);

  gl_FragColor = v_color * color;
}
