package com.sohucw.pay;

public class PayConfigUtilcoy {

	//以下相关参数需要根据自己实际情况进行配置
	public static String APP_ID = "wxbb0099167e5553dd";// appid

	public static String APP_SECRET = "0fb66116e9f46d2d71116b30f0addd85";// appsecret 
	public static String MCH_ID = "1404269502";// 你的商业号
	public static String API_KEY = "B54E40854C02698D8D86BB59B0ECFBF6";// API key


	//http://172.24.82.29:8080/
	public static String CREATE_IP = "172.24.82.29";// key
	public static String UFDODER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";//统一下单接口 
	public static String NOTIFY_URL = "http://80.20.1.195:8080/wpay/Re_notify";//回调地址
}
