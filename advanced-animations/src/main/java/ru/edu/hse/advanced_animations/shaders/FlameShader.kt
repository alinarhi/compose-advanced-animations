package ru.edu.hse.advanced_animations.shaders

import org.intellij.lang.annotations.Language

@Language("AGSL")
const val FLAME_SHADER = """
uniform shader composable;
uniform float iTime;
uniform float2 iResolution;
const mat2 m = .4 * mat2(4, 3, -3, 4);

float rand2(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}


float smoothNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    
    return mix(mix(rand2(i + vec2(0.0, 0.0)), rand2(i + vec2(1.0, 0.0)), u.x),
               mix(rand2(i + vec2(0.0, 1.0)), rand2(i + vec2(1.0, 1.0)), u.x), u.y);
}


float fbm(vec2 uv) {
    vec2 uv0 = uv;
    uv = uv * vec2(5., 2.) - vec2(-2., -.25) - 3.1 * iTime * vec2(0., 1.);
	float f = 1.;
    float a = .5;
    float c = 2.5;
	
    for(int i = 0; i < 5; ++i) {
        uv.x -= .15 * clamp(1. - pow(uv0.y, 4.), 0., 1.) * smoothNoise(c * (uv + float(i) * .612 + iTime));
        c *= 2.;
        f += a * smoothNoise(uv + float(i) * .415);
        a /= 2.;
        uv *= m;
    }
    return f / 2.;
}

vec4 main(in vec2 fragCoord ) {
    vec2 uv = fragCoord;
    uv.y = iResolution.y - fragCoord.y;
    uv /= iResolution.xy;
    uv.x *= iResolution.x/iResolution.y;
    
    float fbmVal = fbm(uv) - uv.y*.5;
    vec4 col = vec4(pow(fbmVal,  1. + 4. * uv.y * uv.y), 0.,  0., .1); // red
    col += vec4(0., pow(fbmVal, 4. + 10. * uv.y * uv.y), 0., 1.); // yellow
    col += vec4(0.15, pow(fbmVal, 7. + 10. * uv.y * uv.y), 0.15, 1.); // final touch
    return col; 
}
"""