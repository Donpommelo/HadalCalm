#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
  vec4 color = texture(u_texture, v_texCoords);

  float average = (color.r + color.b + color.g) / 3.0;

  if (average <= 0.4) {
    color.rgb = vec3(0, 0, 0);
  }
  else {
    color.rgb = vec3(1, 1, 1);
  }
  gl_FragColor = v_color * color;
}
