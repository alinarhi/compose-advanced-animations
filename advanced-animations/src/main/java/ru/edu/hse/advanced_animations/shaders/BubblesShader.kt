package ru.edu.hse.advanced_animations.shaders

import org.intellij.lang.annotations.Language


@Language("AGSL")
const val BUBBLES_SHADER = """
uniform shader composable;
uniform float iTime;
uniform float2 iResolution;
uniform float dir;

float rand(float x) {
    return fract(sin(x*716.743)*765.43);
}


float rand2(vec2 xy){
    return fract(sin(dot(xy, vec2(12.98, 78.033))) * 43758.5453);
}


vec3 palette1(in float t)
{
    vec3 a = vec3(0.672, 0.391, 0.771);
    vec3 b = vec3(0.390, 0.585, 0.685);
    vec3 c = vec3(0.532, 1.476, 0.844);
    vec3 d = vec3(2.397, 1.381, 5.652);
    return (a + b*cos(6.283185*(c*t+d)));
}

vec3 palette2(in float t)
{
    vec3 a = vec3(0.554, -0.057, 0.267);
    vec3 b = vec3(0.335, 0.024, 0.555);
    vec3 c = vec3(0.365, 0.315, 0.975);
    vec3 d = vec3(6.039, 3.522, 0.209);
    return a + b*cos(6.283185*(c*t+d));
}

struct ray {
	vec3 o, d;   
};
    
ray getRay(vec2 uv, vec3 camPos, vec3 lookAt) {
	ray cam;
    cam.o = camPos;
    
    vec3 f = normalize(lookAt-cam.o);
    vec3 r = cross(vec3(0.0, 1.0, 0.0), f);
    vec3 u = cross(f, r);
    
    vec3 c = cam.o + f;
    vec3 i = c + uv.x*r + uv.y*u;
    
    cam.d = normalize(i-cam.o);
    
    return cam;
}


vec2 bubbles(vec2 uv, float k, float t, float sgn) {
    t *= 5.0;
    sgn = sign(sgn);
    // partitioning for drops
    vec2 ratio = vec2(3.0, 1.0);
    
    vec2 uv1 = uv*k*ratio; 
    
    // number of each section
    vec2 id = floor(uv1);
    
    // move the whole grid to compensate the upwards movement of sine
    uv1.y += -sgn*t*0.43;
    // shift sections
    uv1.y += rand(id.x);
    uv.y += rand(id.x);
    
    // recalc id for shifted sections
    id = floor(uv1);
    uv1 = fract(uv1)-0.5;
    
    // offset time
    t += rand2(id)*6.28;
    
    // going down fast and slowly getting up
    float y = sgn*sin(t+sin(t+sin(t)*0.45))*0.42;
    y = sgn*sin(t+sin(t)*0.4)*0.42;
    vec2 pos = vec2(0.0, y);
    vec2 offset = (uv1-pos)/ratio;
   
    // drops
    float dSize = 0.1;
    float d = length(offset);
    float maskDrops = smoothstep(dSize, 0.05, d);
    
    //trails
    vec2 ratioTrails = vec2(1.0,2.0);
    vec2 offsetTrails = (fract(uv*k*ratio.x*ratioTrails)-0.5)/ratioTrails;
    float dt = length(offsetTrails);
    float maskTrails = smoothstep(dSize*4.0*(0.5-uv1.y), 0.0, dt)*smoothstep(-0.1, 0.1, uv1.y-pos.y);
    
    return vec2(maskDrops*offset*50.0+maskTrails*offsetTrails*10.0);
}


vec4 main(in vec2 fragCoord) {
    vec2 uv = fragCoord;
    uv.y = iResolution.y - fragCoord.y;
    uv /= iResolution.xy;
    uv -= 0.5;
    uv.x *= iResolution.x/iResolution.y;

    vec2 bubblesEffect = bubbles(uv, 3.0, iTime*0.5, dir);
    bubblesEffect += bubbles(uv+0.4, 1.5, iTime*0.3, dir);
    
    bubblesEffect *= 0.7;
    
    vec3 camPos = vec3(0.0, 0.0, -1.0);
    vec3 lookAt = vec3(0.0, 0.0, 1.0);
    
    ray r = getRay(uv+dir*bubblesEffect, camPos, lookAt);
    
    vec3 col = palette2(-dir*r.d.y+iTime*0.5);    
    return vec4(col, 1.0);
}
"""