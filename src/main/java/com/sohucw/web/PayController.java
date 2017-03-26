package com.sohucw.web;

import com.sohucw.common.Result;
import com.sohucw.entity.Book;
import com.sohucw.enums.AppointStateEnum;
import com.sohucw.exception.NoNumberException;
import com.sohucw.exception.RepeatAppointException;
import com.sohucw.pay.HttpUtil;
import com.sohucw.pay.PayCommonUtil;
import com.sohucw.pay.PayConfigUtil;
import com.sohucw.pay.XMLUtil;
import com.sohucw.service.BookService;
import com.sohucw.vo.AppointExecution;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;


@Controller
@RequestMapping("/pay") // url:/模块/资源/{id}/细分 /seckill/list
public class PayController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	@RequestMapping(value = "/buy", method = RequestMethod.GET)
	public String weixin_pay() throws Exception {
		// 账号信息
		String appid = PayConfigUtil.APP_ID;  // appid
		//String appsecret = PayConfigUtil.APP_SECRET; // appsecret
		String mch_id = PayConfigUtil.MCH_ID; // 商业号
		String key = PayConfigUtil.API_KEY; // key

		String currTime = PayCommonUtil.getCurrTime();
		String strTime = currTime.substring(8, currTime.length());
		String strRandom = PayCommonUtil.buildRandom(4) + "";
		String nonce_str = strTime + strRandom;

		Integer order_price = 1; // 价格   注意：价格的单位是分
		String body = "陈建伟test商品";   // 商品名称
		String out_trade_no = "B54Esd0802698D8D81BB59B0ECFBF6"; // 订单号

		// 获取发起电脑 ip
		String spbill_create_ip = PayConfigUtil.CREATE_IP;
		// 回调接口
		String notify_url = PayConfigUtil.NOTIFY_URL;
		String trade_type = "NATIVE";   //JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付，统一下单接口trade_type的传参可参考这里
		//MICROPAY--刷卡支付，刷卡支付有单独的支付接口，不调用统一下单接口

		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mch_id);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("out_trade_no", out_trade_no);
		packageParams.put("total_fee", order_price.toString());
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("notify_url", notify_url);
		packageParams.put("trade_type", trade_type);

		String sign = PayCommonUtil.createSign("UTF-8", packageParams, key);
		packageParams.put("sign", sign);

		String requestXML = PayCommonUtil.getRequestXml(packageParams);
		System.out.println("requestXML:  "+requestXML);

		String resXml = HttpUtil.postData(PayConfigUtil.UFDODER_URL, requestXML);

		System.out.println("resXml: "+ resXml);
		Map map = XMLUtil.doXMLParse(resXml);
		//String return_code = (String) map.get("return_code");
		//String prepay_id = (String) map.get("prepay_id");
		String urlCode = (String) map.get("code_url");
		System.out.println("urlCode============: "+ urlCode);
		return QRfromGoogle(urlCode);
	}


	public static String QRfromGoogle(String chl) throws Exception {
		int widhtHeight = 300;
		String EC_level = "L";
		int margin = 0;
	//	chl = UrlEncode(chl);
		String QRfromGoogle = "http://chart.apis.google.com/chart?chs=" + widhtHeight + "x" + widhtHeight
				+ "&cht=qr&chld=" + EC_level + "|" + margin + "&chl=" + chl;
        System.out.println(QRfromGoogle);
		return QRfromGoogle;
	}

	// 特殊字符处理
	public static String UrlEncode(String src) throws UnsupportedEncodingException, UnsupportedEncodingException {
		return URLEncoder.encode(src, "UTF-8").replace("+", "%20");
	}


    // 支付后的回调方法
	public void weixin_notify(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//读取参数
		InputStream inputStream ;
		StringBuffer sb = new StringBuffer();
		inputStream = request.getInputStream();
		String s ;
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		while ((s = in.readLine()) != null){
			sb.append(s);
		}
		in.close();
		inputStream.close();

		//解析xml成map
		Map<String, String> m = new HashMap<String, String>();
		m = XMLUtil.doXMLParse(sb.toString());

		//过滤空 设置 TreeMap
		SortedMap<Object,Object> packageParams = new TreeMap<Object,Object>();
		Iterator it = m.keySet().iterator();
		while (it.hasNext()) {
			String parameter = (String) it.next();
			String parameterValue = m.get(parameter);

			String v = "";
			if(null != parameterValue) {
				v = parameterValue.trim();
			}
			packageParams.put(parameter, v);
		}

		// 账号信息
		String key = PayConfigUtil.API_KEY; // key

	//	logger.info(packageParams);
		//判断签名是否正确
		if(PayCommonUtil.isTenpaySign("UTF-8", packageParams,key)) {
			//------------------------------
			//处理业务开始
			//------------------------------
			String resXml = "";
			if("SUCCESS".equals((String)packageParams.get("result_code"))){
				// 这里是支付成功
				//////////执行自己的业务逻辑////////////////
				String mch_id = (String)packageParams.get("mch_id");
				String openid = (String)packageParams.get("openid");
				String is_subscribe = (String)packageParams.get("is_subscribe");
				String out_trade_no = (String)packageParams.get("out_trade_no");

				String total_fee = (String)packageParams.get("total_fee");

				logger.info("mch_id:"+mch_id);
				logger.info("openid:"+openid);
				logger.info("is_subscribe:"+is_subscribe);
				logger.info("out_trade_no:"+out_trade_no);
				logger.info("total_fee:"+total_fee);

				//////////执行自己的业务逻辑////////////////

				logger.info("支付成功");
				//通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
				resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

			} else {
				logger.info("支付失败,错误信息：" + packageParams.get("err_code"));
				resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
						+ "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
			}
			//------------------------------
			//处理业务完毕
			//------------------------------
			BufferedOutputStream out = new BufferedOutputStream(
					response.getOutputStream());
			out.write(resXml.getBytes());
			out.flush();
			out.close();
		} else{
			logger.info("通知签名验证失败");
		}

	}

	public static void  main (String[] args) {
		PayController pc = new PayController();
		try {
			pc.weixin_pay();
		} catch (Exception e) {
			e.printStackTrace();;
		}

	}
}