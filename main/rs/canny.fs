#pragma version(1)
#pragma rs java_package_name(com.justin.opencvcamera)
#pragma RS_FP_IMPRECISE

rs_allocation inAllocation;
uint32_t width;
uint32_t height;
uchar UPPER=35;
uchar LOWER=10;

//uchar __attribute__ ((kernel)) suppress(uint32_t x, uint32_t y){
 //   uchar2 input = rsGetElementAt_uchar2(inAllocation, x, y);
   // uchar direction=(uchar)input.y;
//    uchar i=input.x;
  //  uchar output;
    //if(x>0 && y>0 && x<width-1 &&y<height-1){
  //  if (direction == 0) {
//		// horizontal, check left and right
//		uchar2 a = rsGetElementAt_uchar2(inAllocation, x - 1, y);
//		uchar2 b = rsGetElementAt_uchar2(inAllocation, x + 1, y);
//		output=a.x < i && b.x < i ? i : 0;
//	} else if (direction == 2) {
//		// vertical, check above and below
//		uchar2 a = rsGetElementAt_uchar2(inAllocation, x, y - 1);
//		uchar2 b = rsGetElementAt_uchar2(inAllocation, x, y + 1);
//		output=a.x < i && b.x < i ? i : 0;
//	} else if (direction == 1) {
//		// NW-SE
//		uchar2 a = rsGetElementAt_uchar2(inAllocation, x - 1, y - 1);
//		uchar2 b = rsGetElementAt_uchar2(inAllocation, x + 1, y + 1);
//		output=a.x < i && b.x < i ? i : 0;
//	} else{
//		// NE-SW
//		uchar2 a = rsGetElementAt_uchar2(inAllocation, x + 1, y - 1);
//		uchar2 b = rsGetElementAt_uchar2(inAllocation, x - 1, y + 1);
//		output=a.x < i && b.x < i ? i : 0;
//	}
//	if(output>UPPER){
//	    return (uchar){255};
//	}else if(output<LOWER){
//	    return (uchar){0};
//	}else{
//	    return (uchar){1};
//	}
  //  }else{
 //       return (uchar){0};
//    }
//}
float2 __attribute__ ((kernel)) suppress(uint32_t x, uint32_t y){
    if(x>0 && y>0 && x<width-1 &&y<height-1){
		float2 input = rsGetElementAt_float2(inAllocation, x, y);
		float d=input.y;
		uchar direction = (uchar)(round(d * 4.0f) + 4) % 4;
		uchar i=(uchar)input.x;
		uchar output;
    if (direction == 0) {
		// horizontal, check left and right
		float2 a = rsGetElementAt_float2(inAllocation, x - 1, y);
		float2 b = rsGetElementAt_float2(inAllocation, x + 1, y);
		output=(uchar)a.x < i && (uchar)b.x < i ? i : 0;
	} else if (direction == 2) {
		// vertical, check above and below
		float2 a = rsGetElementAt_float2(inAllocation, x, y - 1);
		float2 b = rsGetElementAt_float2(inAllocation, x, y + 1);
		output=(uchar)a.x < i && (uchar)b.x < i ? i : 0;
	} else if (direction == 1) {
		// NW-SE
		float2 a = rsGetElementAt_float2(inAllocation, x - 1, y - 1);
		float2 b = rsGetElementAt_float2(inAllocation, x + 1, y + 1);
		output=(uchar)a.x < i && (uchar)b.x < i ? i : 0;
	} else{
		// NE-SW
		float2 a = rsGetElementAt_float2(inAllocation, x + 1, y - 1);
		float2 b = rsGetElementAt_float2(inAllocation, x - 1, y + 1);
		output=(uchar)a.x < i && (uchar)b.x < i ? i : 0;
	}
	if(output>UPPER){
	    return (float2){255,d};
	}else if(output<LOWER){
	    return (float2){0,0};
	}else{
	    return (float2){1,d};
	}
    }else{
        return (float2){0,0};
    }
}