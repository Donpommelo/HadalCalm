#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
  vec4 color = texture2D(u_texture, v_texCoords);
  float dist = distance(v_texCoords, vec2(0.5));
  float vignette = smoothstep(0.8, 0.5, dist);
  color.rgb *= vignette;
  gl_FragColor = color;
}
