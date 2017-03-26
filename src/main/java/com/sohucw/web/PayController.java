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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


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
		String out_trade_no = "B54Ed0854C02698D8D81BB59B0ECFBF6"; // 订单号

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
		chl = UrlEncode(chl);
		String QRfromGoogle = "http://chart.apis.google.com/chart?chs=" + widhtHeight + "x" + widhtHeight
				+ "&cht=qr&chld=" + EC_level + "|" + margin + "&chl=" + chl;
        System.out.println(QRfromGoogle);
		return QRfromGoogle;
	}

	// 特殊字符处理
	public static String UrlEncode(String src) throws UnsupportedEncodingException, UnsupportedEncodingException {
		return URLEncoder.encode(src, "UTF-8").replace("+", "%20");
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