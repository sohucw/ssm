package com.sohucw.web;

import com.sohucw.common.Result;
import com.sohucw.entity.Seckill;
import com.sohucw.enums.SeckillStatEnum;
import com.sohucw.exception.RepeatKillException;
import com.sohucw.exception.SeckillCloseException;
import com.sohucw.service.SeckillService;
import com.sohucw.vo.Exposer;
import com.sohucw.vo.SeckillExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by baidu on 17/3/25.
 */

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model)
    {
        //list.jsp+mode=ModelAndView
        //获取列表页
        List<Seckill> list=seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model)
    {
        if (seckillId == null)
        {
            return "redirect:/seckill/list";
        }

        Seckill seckill=seckillService.getById(seckillId);
        if (seckill==null)
        {
            return "forward:/seckill/list";
        }

        model.addAttribute("seckill",seckill);

        return "detail";
    }

    //ajax ,json暴露秒杀接口的方法
    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public Result<Exposer> exposer(Long seckillId){
        Result<Exposer> result;
        try{
            Exposer exposer=seckillService.exportSeckillUrl(seckillId);
            result=new Result<Exposer>(true,exposer);
        }catch (Exception e){
            e.printStackTrace();
            result=new Result<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public Result<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone",required = false) Long phone)
    {
        if (phone==null)
        {
            return new Result<SeckillExecution>(false,"未注册");
        }
        Result<SeckillExecution> result;

        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            return new Result<SeckillExecution>(true, execution);
        }catch (RepeatKillException e1)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new Result<SeckillExecution>(false,execution);
        }catch (SeckillCloseException e2)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new Result<SeckillExecution>(false,execution);
        }
        catch (Exception e)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new Result<SeckillExecution>(false,execution);
        }

    }

    //获取系统时间
    @ResponseBody
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    public Result<Long> time()
    {
        Date now=new Date();
        return new Result<Long>(true,now.getTime());
    }

}
