package com.zzw.bilibili.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {

    public static void main(String[] args) {
        int[]nums = new int[]{0,-1,1,2,-2,3,4,-2,-5};

        int[][] ints = threeSum(nums);

       for(int num[]:ints){
           for(int nn:num){
               System.out.println(nn);
           }
        }


    }


        public static int[][] threeSum(int[] nums) {
            List<int[]> res = new ArrayList();

            if(nums.length<3||nums==null) return null;

            Arrays.sort(nums);

            for(int i=0;i<nums.length;++i){

                if(nums[i]>0) break;

                if(i>0&&nums[i]==nums[i-1]) continue;

                int l =i+1;
                int r =nums.length-1;

                while(l<r){

                    int sum = nums[i]+nums[l]+nums[r];

                    if(sum==0){

                        res.add(new int[]{ nums[i],nums[l],nums[r]});

                        while(l<r&&nums[l+1]==nums[l]){
                            l++;
                        }

                        while(l<r&&nums[r-1]==nums[r]){
                            r--;
                        }

                        l++;
                        r--;


                    }else if(sum>0){
                        r--;
                    }else{
                        l++;
                    }








                }


            }




            return res.toArray(new int[res.size()][]);
        }


}
