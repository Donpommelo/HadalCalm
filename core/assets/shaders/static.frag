#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform float u_time;

uniform sampler2D u_texture;

float random(float offset, vec2 tex_coord, float time) {
  // pick two numbers that are unlikely to repeat
  vec2 non_repeating = vec2(12.9898 * time, 78.233 * time);

  // multiply our texture coordinates by the non-repeating numbers, then add them together
  float sum = dot(tex_coord, non_repeating);

  // calculate the sine of our sum to get a range between -1 and 1
  float sine = sin(sum);

  // multiply the sine by a big, non-repeating number so that even a small change will result in a big color jump
  float huge_number = sine * 43758.5453 * offset;

  // get just the numbers after the decimal point
  float fraction = fract(huge_number);

  // send the result back to the caller
  return fraction;
}

void main() {

  // find the current pixel color
  vec4 color = texture2D(u_texture, v_texCoords);

  // if it's not transparent
  if (color.a > 0.0) {
    // make a color where the RGB values are the same random number and A is 1; multiply by the node alpha so we can fade in or out
    gl_FragColor = vec4(vec3(random(1.0, v_texCoords, u_time)), 1) * color.a * v_color.a;
  } else {
    // use the (transparent) color
    gl_FragColor = color;
  }
}
