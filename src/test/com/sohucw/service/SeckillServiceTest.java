package com.sohucw.service;

import com.sohucw.entity.Seckill;
import com.sohucw.exception.RepeatKillException;
import com.sohucw.exception.SeckillCloseException;
import com.sohucw.exception.SeckillException;
import com.sohucw.vo.Exposer;
import com.sohucw.vo.SeckillExecution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by baidu on 17/3/25.
 */


@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})

public class SeckillServiceTest {
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckills=seckillService.getSeckillList();
        System.out.println(seckills);

    }

    @Test
    public void getById() throws Exception {
        long seckillId=1000;
        Seckill seckill=seckillService.getById(seckillId);
        System.out.println(seckill);
    }


    @Test
    public void exportSeckillUrl() throws Exception {

        long seckillId=1000;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        System.out.println(exposer);

    }


    @Test
    public void executeSeckill() throws Exception {

        long seckillId=1000;
        long userPhone=13476191876L;
        String md5="bf204e2683e7452aa7db1a50b5713bae";

        try {
            SeckillExecution seckillExecution=seckillService.executeSeckill(seckillId,userPhone,md5);
            System.out.println(seckillExecution);
        } catch (RepeatKillException e) {
            e.printStackTrace();
        } catch (SeckillException e1) {
            e1.printStackTrace();
        }

    }

    @Test//完整逻辑代码测试，注意可重复执行
    public void testSeckillLogic() throws Exception {
        long seckillId=1000;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed())
        {

            System.out.println(exposer);

            long userPhone=187111;
            String md5=exposer.getMd5();

            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
                System.out.println(seckillExecution);
            }catch (RepeatKillException e)
            {
                e.printStackTrace();
            }catch (SeckillCloseException e1)
            {
                e1.printStackTrace();
            }
        }else {
            //秒杀未开启
            System.out.println(exposer);
        }
    }
}
