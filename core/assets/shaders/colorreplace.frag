#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec3 oldcolor1;
uniform vec3 oldcolor2;
uniform vec3 newcolor1;
uniform vec3 newcolor2;

void main() {
  vec4 color = texture2D(u_texture, v_texCoords);
  float dst = 0.1;

  if (distance(color.rgb, oldcolor1) < dst) {
    color.rgb = newcolor1 + color.rgb - oldcolor1;
  } else if (distance(color.rgb, oldcolor2) < dst) {
    color.rgb = newcolor2 + color.rgb - oldcolor2;
  }
  gl_FragColor = v_color * color;
}
