package com.luck.pictureselector;

/**
 * Created by daoyou.huo on 2023/3/10.
 * Description: 占空比计算
 */
public class DutyCycleUtil {

  /*
   根据电压值动态计算占空比，来实现在不同档位下的各档位功率在不同电压下输出相同

   比如 3档下，输出功率为15W， 那么在4.2V、4.0V、3.8V。。。。3.2V下的输出功率都是15W，通过在每个电压值下输出不同占空比来实现

   已知值：
   1）每档的输出功率
      ① 一档 输出功率 4W
      ② 二档 输出功率 9W
      ③ 三档 输出功率 15W
      注：这三个值是预估的值，后续需要实测

   2）电阻值：0.6 Ω

  */


    /**
     *  传入档位及电压值动态计算出占空比
     *
     * @param gear     档位    值为 1,2,3
     * @param voltage  电压值  值为 4.2V~3.0V
     * @return 返回占空比
     */
    public int getDutyCycle(int gear,float voltage){

        //先判断电压值是否在有效范围内
        if (voltage<3.0 || voltage>4.29){
            return 0;
        }

        int result = 10;
        float resistance=0.6f;//电阻值，这个值是固定值
        float gearPower = 0.0f;//档位的输出功率值，每个档位有固定值，后续需要实测调整
        if (gear==1){
            //1档位
            gearPower = 4;//4W
        }else if (gear==2){
            //2档位
            gearPower = 9;//9W
        }else if (gear==3){
            //3档位
            gearPower = 15;//15W
        }

        // 1 先根据电压值和电阻值计算出电流
        float electricity = voltage/resistance;
        // 2 再根据 电流值和电压值算出功率
        float power = voltage * electricity;
        // 3 最后 根据档位输出功率来计算输出占空比
        float dutyCycle =  gearPower/power;

        // 输出占空比
        result = (int)(dutyCycle*100);

        // 注：如果计算出的占空比，导致温度过高或者过低，需要调整每个档位的 gearPower 值，
        // 这个值具体多少，后续需要实际测量

        return result;
    }



}
