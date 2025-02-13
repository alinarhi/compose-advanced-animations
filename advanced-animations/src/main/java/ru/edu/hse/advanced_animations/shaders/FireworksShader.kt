package ru.edu.hse.advanced_animations.shaders

import org.intellij.lang.annotations.Language

@Language("AGSL")
const val FIREWORKS_SHADER = """
uniform shader composable;
uniform float iTime;
uniform float2 iResolution;
uniform float numFireworks;
uniform float numParticles;

// Hash without sine by Dave Hoskins: https://www.shadertoy.com/view/4djSRW
vec3 hash31(float p) {
   vec3 mod3 = vec3(.1031,.11369,.13787);
   vec3 p3 = fract(vec3(p) * mod3);
   p3 += dot(p3, p3.yzx + 19.19);
   return fract(vec3((p3.x + p3.y)*p3.z, (p3.x+p3.z)*p3.y, (p3.y+p3.z)*p3.x));
}


// light fallof effect around pos
float light(vec2 uv, vec2 pos, float size) {
	uv -= pos;    
    size *= size;
    return size/dot(uv, uv);
}

float blend(float x, float y, float z, float t) {
    return smoothstep(x-z, x+z, t)*smoothstep(y+z, y-z, t);
}


vec3 firework(vec2 uv, vec2 p, float seed, float t) {	
    vec3 finalCol = vec3(0.);
    // random vector for color
    vec3 en = hash31(seed);
    vec3 col = en;
    
    for (float i=0.; i < 70.; i++) {
        // random direction
    	vec3 n = hash31(i) - .5;
        // particle position
		vec2 startP = p - vec2(0., t*t*.2);   // slight gravity effect
        vec2 endP = startP + normalize(n.xy)*n.z;
        // reversed parabolic changing from 0 to 1
        float pt = 1.-pow(t-1., 2.);   
        vec2 pos = mix(p, endP, pt);
        
        // get smaller smoothly
        float size = mix(.01, .005, smoothstep(0., .1, pt));
        size *= smoothstep(1., .1, pt);
        
        // sinusoidal sparkling
        float sparkle = (sin((pt+n.z)*100.)*.5+.5);
        sparkle = pow(sparkle, pow(en.x, 3.)*50.)*mix(0.01, .01, en.y*n.y);
      
        size += sparkle*blend(en.x, en.y, en.z, t);
        
        finalCol += col*light(uv, pos, size);
    }
    
    return finalCol;
}


vec4 main(in vec2 fragCoord) {
	vec2 uv = fragCoord.xy / iResolution.xy;
	uv -= .5;
    uv.x *= iResolution.x/iResolution.y;
    float t = iTime*.5;
    vec3 color = vec3(0.);
    
    for(float i=0.; i < 7.; i++) {
    	float et = t+i*11.234;
        float id = floor(et);
        // normalized
        et -= id;
        
        vec2 p = hash31(id).xy;
        p -= .5;
        //p.x *= 1.8;
        color += firework(uv, p, id, et);
    }
    
    return vec4(color, 1.);
}
"""