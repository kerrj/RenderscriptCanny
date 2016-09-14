#pragma version(1)
#pragma rs java_package_name(com.justin.opencvcamera)
#pragma RS_FP_PRELAXED

//rs_allocation inAllocation;
//rs_allocation edgesAllocation;
//rs_allocation accumulationAllocation;
//ushort width;
//ushort height;
//ushort2 edges[1];
//ushort2 accumulation[1];

//uchar __attribute__((kernel)) accumulate(uint x){
    //accumulation[x][0]=(ushort2){1,0};
    //rsSetElementAt_ushort2(accumulationAllocation,(ushort2){1,0},x,0);
 //   ushort increment_size=(ushort)width/400;
  //  ushort start_index=(ushort)x*increment_size;
   // ushort y_array_index=1;
  //  for(ushort x_index=start_index;x_index<start_index+increment_size;x_index++){
    //    for(ushort y_index=0;y_index<height;y_index++){
   //         uchar4 in=rsGetElementAt_uchar4(inAllocation,x_index,y_index);
 //           if(in.r==0){
           //     edges[x][y_array_index]=(ushort2){x_index,y_index};
                //rsSetElementAt_ushort2(edgesAllocation,(ushort2){x_index,y_index},x,y_array_index);
   //             y_array_index++;
       //     }
     //   }
    //}
   // edges[x][0]=(ushort2){y_array_index-1,0};
    //rsSetElementAt_ushort2(edgesAllocation,(ushort2){y_array_index-1},x,0);
  //  return (uchar)0;
//}


//uchar __attribute__((kernel)) midpointCircle(uint x){
  //  ushort index=0;
   // if(x>0){
    //    for(ushort k=0;k<x;k++){
     //       for(ushort i=k;i<400;i++){
    ///            index+=edges[i][0].x;
      //      }
       // }
   // }
//    ushort row_pixels=edges[x][0].x;
    //ushort row_pixels=rsGetElementAt_ushort2(edgesAllocation,x,0).x;
    //for(ushort native_row_index=1;native_row_index<row_pixels+1;native_row_index++){
     //   for(ushort column=native_row_index;column<row_pixels+1;column++){
  //          ushort x1=edges[x][native_row_index].x;
            //ushort x1=rsGetElementAt_ushort2(edgesAllocation,x,native_row_index).x;
    //        ushort y1=edges[x][native_row_index].y;
            //ushort y1=rsGetElementAt_ushort2(edgesAllocation,x,native_row_index).y;
     //       ushort x2=edges[x][column].x;
            //ushort x2=rsGetElementAt_ushort2(edgesAllocation,x,column).x;
     //       ushort y2=edges[x][column].y;
            //ushort y2=rsGetElementAt_ushort2(edgesAllocation,x,column).y;
        //    if(half_sqrt((float)(x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))<300){
                //ushort x_acc=(ushort)(x1+x2)/2;
                //ushort y_acc=(ushort)(y1+y2)/2;
                //ushort thread=(ushort)x_acc/2;
                //ushort index=accumulation[thread][0].x;
                //ushort index=rsGetElementAt_ushort2(accumulationAllocation,thread,0).x;
                //accumulation[thread][index]=(ushort2){x_acc,y_acc};
                //rsSetElementAt_ushort2(accumulationAllocation,(ushort2){x_acc,y_acc},thread,index);
                //accumulation[thread][0]=(ushort2){index+1,0};
                //rsSetElementAt_ushort2(accumulationAllocation,(ushort2){index+1,0},thread,0);
 //           }
   //     }
 //       for(ushort row=x+1;row<400;row++){
   //         for(ushort column=1; column<edges[row][0].x+1; column++){
            //ushort stop=rsGetElementAt_ushort2(edgesAllocation,row,0).x;
            //for(ushort column=1; column<stop; column++){
     //           ushort x1=edges[x][native_row_index].x;
                //ushort x1=rsGetElementAt_ushort2(edgesAllocation,x,native_row_index).x;
      //          ushort y1=edges[x][native_row_index].y;
                //ushort y1=rsGetElementAt_ushort2(edgesAllocation,x,native_row_index).y;
      //          ushort x2=edges[x][column].x;
                //ushort x2=rsGetElementAt_ushort2(edgesAllocation,x,column).x;
      //          ushort y2=edges[x][column].y;
                //ushort y2=rsGetElementAt_ushort2(edgesAllocation,x,column).y;
             //   if(half_sqrt((float)(x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))<300){
                    //ushort x_acc=(ushort)(x1+x2)/2;
                    //ushort y_acc=(ushort)(y1+y2)/2;
                    //ushort thread=(ushort)x_acc/2;
                    //ushort index=accumulation[thread][0].x;
                    //ushort index=rsGetElementAt_ushort2(accumulationAllocation,thread,0).x;
                    //accumulation[thread][index]=(ushort2){x_acc,y_acc};
                    //rsSetElementAt_ushort2(accumulationAllocation,(ushort2){x_acc,y_acc},thread,index);
                    //accumulation[thread][0]=(ushort2){index+1,0};
                    //rsSetElementAt_ushort2(accumulationAllocation,(ushort2){index+1,0},thread,0);
      //          }
    //        }
   //     }
 //   }
   // return (uchar){0};
//}