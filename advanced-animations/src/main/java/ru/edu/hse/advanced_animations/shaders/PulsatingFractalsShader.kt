package ru.edu.hse.advanced_animations.shaders

import org.intellij.lang.annotations.Language

@Language("AGSL")
const val PULSATING_FRACTALS_SHADER = """
uniform shader composable;
uniform float iTime;
uniform float2 iResolution;
// cosine based palette, 4 vec3 params
vec3 palette(in float t)
{
    vec3 a = vec3(0.5, 0.5, 0.5);
    vec3 b = vec3(0.5, 0.5, 0.5);
    vec3 c = vec3(1.0, 1.0, 1.0);
    vec3 d = vec3(0.51, 0.048, 0.0);
    return a + b*cos( 6.283185*(c*t+d));
}

vec4 main(in vec2 fragCoord) {
    vec2 uv = fragCoord;
    uv.y = iResolution.y - fragCoord.y;
    uv /= iResolution.xy;
    uv = uv * 2.0 - 1.0; 
    uv.x *= iResolution.x / iResolution.y;
    // distortion
    uv.x += sin(uv.y*5.2)*0.05;
    uv.y -= sin(uv.x*7.)*0.08;
    uv *= 1.5;
    
    vec2 uv0 = uv;
    vec3 finalCol = vec3(0.);
    
    for (float i=0.0; i<2.0; i++) {    
        //uv = fract(uv * 1.2) - 0.5;
    
        // break the symmetry + pulsating
        uv = fract(uv * (1.5 + cos(iTime))) - 0.5;
        
        
        float d0 = length(uv0);
        float d = length(uv) * exp(-d0);
        
        vec3 col = palette(d0 + i*0.4 + iTime*0.3);
        
        
        float k = 20.0;
        d = sin(d*k - iTime*3.0)/k; 
        d = abs(d);
        
        d = 0.003/d;

        finalCol += col * d;
    }

    vec4 fragColor = vec4(finalCol,1.);
    return fragColor;
}
"""