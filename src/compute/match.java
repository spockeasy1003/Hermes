package compute;

/**
 * Created by yyz on 17-3-7.
 */
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class match {
    int [][]event;
    int [][][]subs;
    int []subsidnumber;
    Random rand=new Random();
    int numberofsubs;
    int numberofattri;
    int numberofevent;
    Bucket [][]substore;
    Bucket2 [][]bucketstore;
    public static void main(String[] args) {
        Random n=new Random();



       /* List<Long> tt=new ArrayList<>();
        for(int i=0;i<10;i++){
            Long aaa;
            aaa=((long)n.nextInt(20))<<32;
            aaa=aaa+n.nextInt(50);
            tt.add(aaa);
        }
        for(int i=0;i<10;i++){
            System.out.format("%x", tt.get(i));
            System.out.print(" ");
        }
        System.out.println("after:");
        Collections.sort(tt,new Mycomparetor());
        for(int i=0;i<10;i++){
            System.out.format("%x", tt.get(i));
            System.out.print(" ");
        }*/
        Set<Integer> []result=new Set[10];
        for(int i=0;i<10;i++){
            result[i]=new HashSet<>();
        }
        int n1=100000;
        int n2=20;
        int n3=1000;
        System.out.println("订阅数目："+n1+"; "+"属性个数："+n2+"; "+"事件个数："+n3+"; ");
        System.out.println("实例化对象：");
        Date time=new Date();
        System.out.println(time);
		match test=new match();
		test.init(n1, n2, n3);
        Date time1=new Date();
        System.out.println("初始化：");
        System.out.println(time1);
		test.RandGenSubs(20);
		test.RandGenEvent(20);
        Date time2=new Date();
        System.out.println("存储bucket：");
        System.out.println(time2);
		test.storeubkcet();
        Date time3=new Date();
        System.out.println("排序bucket：");
        System.out.println(time3);
		test.bucketsortbyselct();

        Date time4=new Date();
        System.out.println(time4);
		/*for(int i:test.bucketstore[1][2].ubid[0]){
		    System.out.print(i+" ");
        }*/
		System.out.println("匹配算法开始：");
		Date time5=new Date();
		System.out.println(time5);
        result=test.matched();
        System.out.println("匹配算法结束：");
        Date time6=new Date();
        System.out.println(time6);
        /*for (int i=0;i<100;i++){
            for (int j=0;j<test.numberofattri;j++){
                System.out.print("["+test.subs[i][j][0]+"~"+test.subs[i][j][1]+"]   ");
            }
            System.out.println();
        }*/
       /* System.out.println("up subs; below event:");
        for(int i=0;i<n3;i++){
            for(int j=0;j<test.numberofattri;j++){
                System.out.print(test.event[i][j]+"; ");
            }
            System.out.println();
        }*/
        /*for(int i=0;i<n3;i++){
            System.out.println(result[i]);
        }*/


    }
    void init(int n1,int n2,int n3){
        subs=new int[n1][n2][2];
        event=new int[n3][n2];
        numberofsubs=n1;
        numberofattri=n2;
        numberofevent=n3;
        substore=new Bucket[n2][n2];
        subsidnumber=new int[n1];
        bucketstore=new Bucket2[n2][n2];
        // object init
        for(int i=0;i<numberofattri-1;i++){
            for(int j=i+1;j<numberofattri;j++){
                bucketstore[i][j]=new Bucket2();
            }
        }
    }
    void RandGenSubs(int randn){
        for(int i=0;i<numberofsubs;i++){
            for(int j=0;j<numberofattri;j++){
                float flag=rand.nextFloat();
                if(flag>0.1){
                    subs[i][j][0]=-1;
                    subs[i][j][1]=-1;
                    continue;
                }else{
                    int temp1=rand.nextInt(randn);
                    int temp2=rand.nextInt(randn);
                    subs[i][j][0]=temp1;
                    subs[i][j][1]=(temp1+temp2)>randn?randn:(temp1+temp2);
                }
            }
        }
    }
    void RandGenEvent(int randn){
        for(int j=0;j<numberofevent;j++){
            for(int i=0;i<numberofattri;i++){
                float flag=rand.nextFloat();
                if(flag>0.2){
                    event[j][i]=-1;
                    continue;
                }else{
                    event[j][i]=rand.nextInt(randn);
                }
            }
        }

    }
    void  storeubkcet(){
        for(int i=0;i<numberofsubs;i++){
            int []haveattri=new int[numberofattri];
            int k=0;
            for(int j=0;j<numberofattri;j++){
                haveattri[j]=-1;
            }
            for(int j=0;j<numberofattri;j++){
                if(subs[i][j][1]!=-1){
                    haveattri[k++]=j;
                }
            }
            subsidnumber[i]=k;
            for(int j=0;j<k-1;j++){
                bucketstore[haveattri[j]][haveattri[j+1]].ubid[0].add(i);
                bucketstore[haveattri[j]][haveattri[j+1]].ub[0].add(subs[i][haveattri[j]][1]);
                bucketstore[haveattri[j]][haveattri[j+1]].lbid[0].add(i);
                bucketstore[haveattri[j]][haveattri[j+1]].lb[0].add(subs[i][haveattri[j]][0]);

            }
            if(k>=2){
                bucketstore[haveattri[k-2]][haveattri[k-1]].ubid[1].add(i);
                bucketstore[haveattri[k-2]][haveattri[k-1]].ub[1].add(subs[i][haveattri[k-1]][1]);
                bucketstore[haveattri[k-2]][haveattri[k-1]].lbid[1].add(i);
                bucketstore[haveattri[k-2]][haveattri[k-1]].lb[1].add(subs[i][haveattri[k-1]][0]);
            }

        }
    }
    void StoreinBucket(){
        //store subscriptions in bucket;

        for(int i=0;i<numberofsubs;i++){
            int []haveattri=new int[numberofattri];
            int k=0;
            for(int j=0;j<numberofattri;j++){
                haveattri[j]=-1;
            }
            for(int j=0;j<numberofattri;j++){
                if(subs[i][j][1]!=-1){
                    haveattri[k++]=j;
                }
            }
            subsidnumber[i]=k;
            for(int j=0;j<k-1;j++){
                long addin;
                addin=((long)i)<<32;
                addin=addin+(long)subs[i][haveattri[j]][1];
                substore[haveattri[j]][haveattri[j+1]].ub[0].add(addin);//id + upbound
                addin=((long)i)<<32;
                addin=addin+(long)subs[i][haveattri[j]][0];
                substore[haveattri[j]][haveattri[j+1]].lb[0].add(addin);
            }
            //last one;
            if(k>=2){
                long addin;
                addin=((long)i)<<32;
                addin=addin+(long)subs[i][haveattri[k-1]][1];
                substore[haveattri[k-2]][haveattri[k-1]].ub[1].add(addin);//id + upbound
                addin=((long)i)<<32;
                addin=addin+(long)subs[i][haveattri[k-1]][0];
                substore[haveattri[k-2]][haveattri[k-1]].lb[1].add(addin);//id + low bound
            }


        }
    }
    void bucketsortbyselct(){
        for(int i=0;i<numberofattri-1;i++){
            for(int j=i+1;j<numberofattri;j++){
                int l1=bucketstore[i][j].ub[0].size();
                if(l1>1){
                    for(int t1=0;t1<l1-1;t1++){
                        int k=t1;
                        for(int t2=k+1;t2<l1;t2++){
                            if(bucketstore[i][j].ub[0].get(t2)<bucketstore[i][j].ub[0].get(k)){
                                k=t2;
                            }
                        }
                        if(t1!=k){
                            int temp=bucketstore[i][j].ub[0].get(t1);//swap ub
                            bucketstore[i][j].ub[0].set(t1,bucketstore[i][j].ub[0].get(k));
                            bucketstore[i][j].ub[0].set(k,temp);
                            temp=bucketstore[i][j].ubid[0].get(t1);//id
                            bucketstore[i][j].ubid[0].set(t1,bucketstore[i][j].ubid[0].get(k));
                            bucketstore[i][j].ubid[0].set(k,temp);
                        }
                    }
                }
                int l2=bucketstore[i][j].ub[1].size();
                if(l2>1){
                    for(int t1=0;t1<l2-1;t1++){
                        int k=t1;
                        for(int t2=k+1;t2<l2;t2++){
                            if(bucketstore[i][j].ub[1].get(t2)<bucketstore[i][j].ub[1].get(k)){
                                k=t2;
                            }
                        }
                        if(t1!=k){
                            int temp=bucketstore[i][j].ub[1].get(t1);//ub
                            bucketstore[i][j].ub[1].set(t1,bucketstore[i][j].ub[1].get(k));
                            bucketstore[i][j].ub[1].set(k,temp);
                            temp=bucketstore[i][j].ubid[1].get(t1);//id
                            bucketstore[i][j].ubid[1].set(t1,bucketstore[i][j].ubid[1].get(k));
                            bucketstore[i][j].ubid[1].set(k,temp);
                        }
                    }
                }
                int l3=bucketstore[i][j].lb[0].size();
                if(l3>1){
                    for(int t1=0;t1<l3-1;t1++){
                        int k=t1;
                        for(int t2=k+1;t2<l3;t2++){
                            if(bucketstore[i][j].lb[0].get(t2)<bucketstore[i][j].lb[0].get(k)){
                                k=t2;
                            }
                        }
                        if(t1!=k){
                            int temp=bucketstore[i][j].lb[0].get(t1);//ub
                            bucketstore[i][j].lb[0].set(t1,bucketstore[i][j].lb[0].get(k));
                            bucketstore[i][j].lb[0].set(k,temp);
                            temp=bucketstore[i][j].lbid[0].get(t1);//id
                            bucketstore[i][j].lbid[0].set(t1,bucketstore[i][j].lbid[0].get(k));
                            bucketstore[i][j].lbid[0].set(k,temp);
                        }
                    }
                }
                int l4=bucketstore[i][j].lb[1].size();
                if(l4>1){
                    for(int t1=0;t1<l4-1;t1++){
                        int k=t1;
                        for(int t2=k+1;t2<l4;t2++){
                            if(bucketstore[i][j].lb[1].get(t2)<bucketstore[i][j].lb[1].get(k)){
                                k=t2;
                            }
                        }
                        if(t1!=k){
                            int temp=bucketstore[i][j].lb[1].get(t1);//ub
                            bucketstore[i][j].lb[1].set(t1,bucketstore[i][j].lb[1].get(k));
                            bucketstore[i][j].lb[1].set(k,temp);
                            temp=bucketstore[i][j].lbid[1].get(t1);//id
                            bucketstore[i][j].lbid[1].set(t1,bucketstore[i][j].lbid[1].get(k));
                            bucketstore[i][j].lbid[1].set(k,temp);
                        }
                    }
                }
            }
        }

    }
    Set<Integer> bisectfind2(Bucket2 A,int l,int value){
        Set<Integer> result1=new HashSet<>();
        int indexsize=A.ub[l].size()-1;
        int low=0;
        int high=indexsize;
        int mid=0;

        while(low<high){
            mid=(low+high)/2;
            if(A.ub[l].get(mid)>=value){
                high=mid-1;
            }else if(A.ub[l].get(mid)<value){
                low=mid+1;
            }
        }
        int upflag=mid;
        if(upflag<=indexsize){
            for(int i=upflag;i<indexsize+1;i++){
                result1.add(A.ubid[l].get(i));
            }
        }
        int low2=0;
        int high2=A.ub[l].size()-1;
        while(low2<high2){
            mid=(low2+high2)/2;
            if(A.lb[l].get(mid)>value){
                high2=mid-1;
            }else if(A.lb[l].get(mid)<=value){
                low2=mid+1;
            }
        }
        int lowflag=low2-1;
        Set<Integer> result2=new HashSet<>();
        if(lowflag>=0){
            for(int i=0;i<=lowflag;i++){
                result2.add(A.lbid[l].get(i));
            }
        }
        //System.out.print("result1:");
        //System.out.println(result1);
        //System.out.print("result2:");
        //System.out.println(result2);

        result1.retainAll(result2);
        return result1;
    }
    void bukcetsort(){
        for(int i=0;i<numberofattri-1;i++){
            for(int j=i+1;j<numberofattri;j++){
                for(int l=0;l<2;l++){
                    if(substore[i][j].ub[l]!=null){
                        Collections.sort(substore[i][j].ub[l],new Mycomparetor());
                    }
                    if(substore[i][j].lb[l]!=null){
                        Collections.sort(substore[i][j].lb[l],new Mycomparetor());
                    }
                }
            }
        }
    }
    void bucketinsert(){

    }
    Set<Integer> bisectfind(Bucket A,int l,int value){
        Set<Integer> result1=new HashSet<>();
        int size=A.ub[l].size()-1;
        int low=0;
        int high=size;
        while(low<high){
            int mid=(low+high)/2;
            if((A.ub[l].get(mid)&0xffffffffL)>=value){
                high=mid-1;
            }else if((A.ub[l].get(mid)&0xffffffffL)<value){
                low=mid+1;
            }
        }
        int upflag=high+1;
        if(upflag<=size){
            for(int i=upflag;i<size+1;i++){
                result1.add((int)(A.ub[l].get(i)>>32));
            }
        }
        int low2=0;
        int high2=A.ub[l].size()-1;
        while(low2<high2){
            int mid=(low2+high2)/2;
            if((A.lb[l].get(mid)&0xffffffffL)>value){
                high2=mid-1;
            }else if((A.lb[l].get(mid)&0xffffffffL)<=value){
                low2=mid+1;
            }
        }
        int lowflag=low-1;
        Set<Integer> result2=new HashSet<>();
        if(lowflag>=0){
            for(int i=0;i<=lowflag;i++){
                result2.add((int)(A.lb[l].get(i)>>32));
            }
        }
        result1.retainAll(result2);
        return result1;
    }
    Set<Integer>[] matched(){
        Set<Integer>[]MatchedSub=new Set[numberofevent];
        for(int n=0;n<numberofevent;n++){
            MatchedSub[n]=new HashSet<>();
        }
        for(int n=0;n<numberofevent;n++){
            Set<Integer> MatchedSubID=new HashSet<>();
            Map<Integer,Integer> SubConCouters=new HashMap<>();
            int []haveattri=new int[numberofattri];
            int k=0;
            for(int j=0;j<numberofattri;j++){//init
                haveattri[j]=-1;
            }
            for(int j=0;j<numberofattri;j++){
                if(event[n][j]!=-1){//mark attribute;
                    haveattri[k++]=j;
                }
            }
            for(int i=0;i<k-1;i++){
                for(int j=i+1;j<k;j++){
                    for(int l=0;l<2;l++){
                        Set<Integer> ConMatchedSubIDs=new HashSet<>();

                        int value;
                        if(l==0){
                            value=event[n][haveattri[i]];
                        }else{
                            value=event[n][haveattri[j]];
                        }
                       /* if(i==0&&j==1){
                            Date test=new Date();
                            System.out.println("TEST TIME:"+test);
                        }*/
                        ConMatchedSubIDs.addAll(bisectfind2(bucketstore[haveattri[i]][haveattri[j]],l,value));
                        /*if(i==0&&j==1){
                            Date test2=new Date();
                            System.out.println("TEST TIME:"+test2);
                        }*/
                        for(int subid:ConMatchedSubIDs){
                            if(!SubConCouters.containsKey(subid)){
                                SubConCouters.put(subid, 1);
                            }else{
                                SubConCouters.put(subid, SubConCouters.get(subid)+1);
                            }
                        }
                    }
                }
            }

            for(int subid:SubConCouters.keySet()){
                //System.out.println("test:"+subid);
                //System.out.print(SubConCouters.get(subid)+"===?");
                //System.out.println(2*subsidnumber[subid]+" ~");
                if(SubConCouters.get(subid)==subsidnumber[subid]){
                    MatchedSubID.add(subid);
                    //System.out.println("match！！："+subid);
                }
            }
            MatchedSub[n].addAll(MatchedSubID);
            //System.out.println(MatchedSub[n]);
        }
        return MatchedSub;
    }

}
class Mycomparetor implements Comparator<Long>{

    @Override
    public int compare(Long a1,Long a2) {

        return (a1&(0xffffffffL)-a2&(0xffffffffL))>0?1:-1;
    }

}
class Bucket{
    List<Long> []ub=new List[2];
    List<Long> []lb=new List[2];
    Bucket(){
        ub[0]=new ArrayList<>();
        ub[1]=new ArrayList<>();
        lb[0]=new ArrayList<>();
        lb[1]=new ArrayList<>();
    }

}
class Bucket2{
    List<Integer> []ub=new List[2];
    List<Integer> []ubid=new List[2];
    List<Integer> []lb=new List[2];
    List<Integer> []lbid=new List[2];
    Bucket2(){
        ub[0]=new ArrayList<>();
        ub[1]=new ArrayList<>();
        lb[0]=new ArrayList<>();
        lb[1]=new ArrayList<>();
        ubid[0]=new ArrayList<>();
        ubid[1]=new ArrayList<>();
        lbid[0]=new ArrayList<>();
        lbid[1]=new ArrayList<>();
    }

}