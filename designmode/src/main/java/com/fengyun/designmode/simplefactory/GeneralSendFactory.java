package fengyun.designmode.simplefactory;

import fengyun.designmode.mode.MailSender;
import fengyun.designmode.mode.Sender;
import fengyun.designmode.mode.SmsSender;

public class GeneralSendFactory {
	
	public Sender produce(String type){
		if("mail".equals(type)){
			return new MailSender();
		}else if("sms".equals(type)){
			return new SmsSender();
		}else{
			System.out.println("请输入正确类型！");
			return null;
		}
	}
	
}
