package soft.project.demo.recaptcha;

import org.springframework.lang.NonNull;

public class RecaptchaDTO {
	
	@NonNull
    String captchaResponse;
	
	public RecaptchaDTO() {}

	public RecaptchaDTO(String captchaResponse) {
		super();
		this.captchaResponse = captchaResponse;
	}

	public String getCaptchaResponse() {
		return captchaResponse;
	}

	public void setCaptchaResponse(String captchaResponse) {
		this.captchaResponse = captchaResponse;
	}

	@Override
	public String toString() {
		return "RecaptchaDTO [captchaResponse=" + captchaResponse + "]";
	}
	
	
}
