#pragma version(1)
#pragma rs java_package_name(com.justin.opencvcamera)
#pragma RS_FP_IMPRECISE

uchar3 rgb;

void setRGB(uchar r, uchar g, uchar b){
    rgb=(uchar3){r,g,b};
}

uchar __attribute__ ((kernel)) split(uchar4 in, uint32_t x, uint32_t y){
    uchar pixelOut=((in.r * rgb.r) + (in.g * rgb.g) + (in.b * rgb.b)) / (rgb.r+rgb.g+rgb.b);
    return pixelOut;
}
