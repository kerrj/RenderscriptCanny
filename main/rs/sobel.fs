#pragma version(1)
#pragma rs java_package_name(com.justin.opencvcamera)
#pragma RS_FP_IMPRECISE

rs_allocation inAllocation;
uint32_t width;
uint32_t height;

//uchar2 __attribute__ ((kernel)) sobel(uint32_t x,uint32_t y){
//    if(x>0 && y>0 && x<width-1 &&y<height-1){
//        //get all values around pixel
//        uchar topleft=     rsGetElementAt_uchar(inAllocation,x-1,y-1);
 //       uchar left=        rsGetElementAt_uchar(inAllocation,x-1,y);
  //      uchar bottomleft=  rsGetElementAt_uchar(inAllocation,x-1,y+1);
   //     uchar top=         rsGetElementAt_uchar(inAllocation,x,y-1);
//        uchar bottom=      rsGetElementAt_uchar(inAllocation,x,y+1);
 //       uchar topright=    rsGetElementAt_uchar(inAllocation,x+1,y-1);
  //      uchar right=       rsGetElementAt_uchar(inAllocation,x+1,y);
   //     uchar bottomright= rsGetElementAt_uchar(inAllocation,x+1,y+1);
    //    //get x kernel value
//        float xValue=(-topleft-2*left-bottomleft+topright+2*right+bottomright)/2;
 //       //get y kernel value
  //      float yValue=(-topleft-2*top-topright+bottomleft+2*bottom+bottomright)/2;
   //     uchar mag=(uchar)half_sqrt(xValue*xValue+yValue*yValue);
    //    uchar d = (uchar)(round(native_atan2pi(yValue, xValue) * 4.0f) + 4) % 4;
  //     return (uchar2){mag,d};
   // }else{
 //       return (uchar2){0,0};
 //   }
//}
float2 __attribute__ ((kernel)) sobel(uint32_t x,uint32_t y){
    if(x>0 && y>0 && x<width-1 &&y<height-1){
        //get all values around pixel
        uchar topleft=     rsGetElementAt_uchar(inAllocation,x-1,y-1);
        uchar left=        rsGetElementAt_uchar(inAllocation,x-1,y);
        uchar bottomleft=  rsGetElementAt_uchar(inAllocation,x-1,y+1);
        uchar top=         rsGetElementAt_uchar(inAllocation,x,y-1);
        uchar bottom=      rsGetElementAt_uchar(inAllocation,x,y+1);
        uchar topright=    rsGetElementAt_uchar(inAllocation,x+1,y-1);
        uchar right=       rsGetElementAt_uchar(inAllocation,x+1,y);
        uchar bottomright= rsGetElementAt_uchar(inAllocation,x+1,y+1);
        //get x kernel value
        float xValue=(-topleft-2*left-bottomleft+topright+2*right+bottomright)/2;
        //get y kernel value
        float yValue=(-topleft-2*top-topright+bottomleft+2*bottom+bottomright)/2;
       return (float2){half_sqrt(xValue*xValue+yValue*yValue),(float)atan2pi((float)yValue, (float)xValue)};
    }else{
        return (float2){0,0};
    }
}