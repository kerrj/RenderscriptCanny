#pragma version(1)
#pragma rs java_package_name(com.justin.opencvcamera)
#pragma RS_FP_IMPRECISE

rs_allocation inAllocation;
uint32_t width;
uint32_t height;
float gradientThreshold;

 //uchar4 __attribute__ ((kernel)) hysteresis(uint32_t x, uint32_t y){
     // if(x>1 && y>1 && x<width-2 &&y<height-2){
  	//	uchar input=rsGetElementAt_uchar(inAllocation,x,y);
 ////////// 		if(input==255){
 // 			return (uchar4){0,0,0,255};
 // 		}else if(input==0){
//  			return (uchar4){255,255,255,255};
  //   	}else if(input==1){
         //        	uchar a = rsGetElementAt_uchar(inAllocation,x - 1, y - 1);
        //         	uchar b= rsGetElementAt_uchar(inAllocation,x, y - 1);
       //      		uchar c= rsGetElementAt_uchar(inAllocation,x + 1, y - 1);
      //       		uchar d= rsGetElementAt_uchar(inAllocation,x - 1, y);
     //        		uchar e= rsGetElementAt_uchar(inAllocation,x + 1, y);
    //         		uchar f= rsGetElementAt_uchar(inAllocation,x - 1, y + 1);
             //		uchar g= rsGetElementAt_uchar(inAllocation,x, y + 1);
             //		uchar h= rsGetElementAt_uchar(inAllocation,x + 1, y + 1);
             //		if(a+b+c+d+e+f+g+h>20){
            // 			return (uchar4){0,0,0,255};
            // 		}else{
            // 		    uchar a= rsGetElementAt_uchar(inAllocation,x - 2, y - 2);
           //             uchar b= rsGetElementAt_uchar(inAllocation,x - 1, y - 2);
          //          	uchar c= rsGetElementAt_uchar(inAllocation,x, y - 2);
         //           	uchar d= rsGetElementAt_uchar(inAllocation,x + 1, y - 2);
        //            	uchar e= rsGetElementAt_uchar(inAllocation,x + 2, y - 2);
       //             	uchar f= rsGetElementAt_uchar(inAllocation,x - 2, y - 1);
      //              	uchar g= rsGetElementAt_uchar(inAllocation,x + 2, y - 1);
     //             	    uchar h= rsGetElementAt_uchar(inAllocation,x - 2, y);
    //                	uchar i= rsGetElementAt_uchar(inAllocation,x + 2, y);
                 //   	uchar j= rsGetElementAt_uchar(inAllocation,x - 2, y + 1);
                //     	uchar k= rsGetElementAt_uchar(inAllocation,x + 2, y + 1);
               //      	uchar l= rsGetElementAt_uchar(inAllocation,x - 2, y + 2);
              //       	uchar m= rsGetElementAt_uchar(inAllocation,x - 1, y + 2);
             //        	uchar n= rsGetElementAt_uchar(inAllocation,x, y + 2);
           ////         	uchar o= rsGetElementAt_uchar(inAllocation,x + 1, y + 2);
         ////           	uchar p= rsGetElementAt_uchar(inAllocation,x + 2, y + 2);
        //            	if(a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p>50){
       //               		return (uchar4){0,0,0,255};
      //               	}else{
     //                		return (uchar4){255,255,255,255};
    //                 	}
   //          		}
  //		}else{
  //			return (uchar4){255,255,255,255};
  //		}
  //	}else{
  //		return (uchar4){255,255,255,255};
//  	}
//  }

   uchar4 __attribute__ ((kernel)) hysteresis(uint32_t x, uint32_t y){
        if(x>1 && y>1 && x<width-2 &&y<height-2){
    		float2 input=rsGetElementAt_float2(inAllocation,x,y);
    		if(input.x==255){
    		    if(input.y!=0){
                    if(fabs(rsGetElementAt_float2(inAllocation,x - 1, y - 1).y-input.y)<gradientThreshold){
                        return (uchar4){255,255,255,255};
                    }else if(fabs(rsGetElementAt_float2(inAllocation,x, y - 1).y-input.y)<gradientThreshold ){
                        return (uchar4){255,255,255,255};
                    }else if(fabs(rsGetElementAt_float2(inAllocation,x + 1, y - 1).y-input.y)<gradientThreshold ){
                        return (uchar4){255,255,255,255};
                    }else if(fabs(rsGetElementAt_float2(inAllocation,x - 1, y).y-input.y)<gradientThreshold ){
                        return (uchar4){255,255,255,255};
                    }else if(fabs(rsGetElementAt_float2(inAllocation,x + 1, y).y-input.y)<gradientThreshold ){
                        return (uchar4){255,255,255,255};
                    }else if(fabs(rsGetElementAt_float2(inAllocation,x - 1, y + 1).y-input.y)<gradientThreshold){
                        return (uchar4){255,255,255,255};
                    }else if(fabs(rsGetElementAt_float2(inAllocation,x, y + 1).y-input.y)<gradientThreshold){
                        return (uchar4){255,255,255,255};
                    }else if(fabs(rsGetElementAt_float2(inAllocation,x + 1, y + 1).y-input.y)<gradientThreshold){
                        return (uchar4){255,255,255,255};
                    }else{
                        return (uchar4){0,0,0,255};
                    }
                }else{
                    return (uchar4){0,0,0,255};
                }
    		}else if(input.x==0){
    			return (uchar4){255,255,255,255};
          	}else if(input.x==1){
                   	float2 a = rsGetElementAt_float2(inAllocation,x - 1, y - 1);
                   	float2 b= rsGetElementAt_float2(inAllocation,x, y - 1);
               		float2 c= rsGetElementAt_float2(inAllocation,x + 1, y - 1);
               		float2 d= rsGetElementAt_float2(inAllocation,x - 1, y);
               		float2 e= rsGetElementAt_float2(inAllocation,x + 1, y);
               		float2 f= rsGetElementAt_float2(inAllocation,x - 1, y + 1);
               		float2 g= rsGetElementAt_float2(inAllocation,x, y + 1);
               		float2 h= rsGetElementAt_float2(inAllocation,x + 1, y + 1);
               		if(a.x+b.x+c.x+d.x+e.x+f.x+g.x+h.x>20){
               			return (uchar4){0,0,0,255};
               		}else{
               		    float2 a= rsGetElementAt_float2(inAllocation,x - 2, y - 2);
                        float2 b= rsGetElementAt_float2(inAllocation,x - 1, y - 2);
                      	float2 c= rsGetElementAt_float2(inAllocation,x, y - 2);
                      	float2 d= rsGetElementAt_float2(inAllocation,x + 1, y - 2);
                      	float2 e= rsGetElementAt_float2(inAllocation,x + 2, y - 2);
                      	float2 f= rsGetElementAt_float2(inAllocation,x - 2, y - 1);
                      	float2 g= rsGetElementAt_float2(inAllocation,x + 2, y - 1);
						float2 h= rsGetElementAt_float2(inAllocation,x - 2, y);
                      	float2 i= rsGetElementAt_float2(inAllocation,x + 2, y);
                      	float2 j= rsGetElementAt_float2(inAllocation,x - 2, y + 1);
                       	float2 k= rsGetElementAt_float2(inAllocation,x + 2, y + 1);
                       	float2 l= rsGetElementAt_float2(inAllocation,x - 2, y + 2);
                       	float2 m= rsGetElementAt_float2(inAllocation,x - 1, y + 2);
                       	float2 n= rsGetElementAt_float2(inAllocation,x, y + 2);
                      	float2 o= rsGetElementAt_float2(inAllocation,x + 1, y + 2);
                      	float2 p= rsGetElementAt_float2(inAllocation,x + 2, y + 2);
                      	if(a.x+b.x+c.x+d.x+e.x+f.x+g.x+h.x+i.x+j.x+k.x+l.x+m.x+n.x+o.x+p.x>50){
                        		return (uchar4){0,0,0,255};
                       	}else{
                       		return (uchar4){255,255,255,255};
                       	}
               		}
    		}else{
    			return (uchar4){255,255,255,255};
    		}
    	}else{
    		return (uchar4){255,255,255,255};
    	}
    }
