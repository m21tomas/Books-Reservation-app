package soft.project.demo.recaptcha;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service
public class ValidateCaptcha {
	
	private static final Logger LOG = LoggerFactory.getLogger(ValidateCaptcha.class);
	
	private final RestTemplate template;
    @Value("${google.recaptcha.verification.endpoint}")
    String recaptchaEndpoint;
    @Value("${google.recaptcha.secret}")
    String recaptchaSecret;
 
    public ValidateCaptcha(final RestTemplateBuilder templateBuilder) {
        this.template = templateBuilder.build();
    }
 
    // method validate the captcha response coming from the client
    // and return either true or false after the validation.
    // reference url - https://developers.google.com/recaptcha/docs/verify
    public boolean validateCaptcha(final String captchaResponse) {
	    // "captchaResponse" length validation is omitted for brevity.
	         
	    LOG.info("Going to validate the captcha response = [{}]", captchaResponse);
	    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    // "secret" is a required param and it represents the shared key between your site 
	    // and the recaptcha.
	    params.add("secret", recaptchaSecret);
	    // "response" is a required param and it represents the user token provided
	    // by the recaptcha client-side integration on your site.
	    params.add("response", captchaResponse);
	 
	    CaptchaResponse apiResponse = null;
	    try {
	        apiResponse = template.postForObject(recaptchaEndpoint, params, CaptchaResponse.class);
	    } catch (final RestClientException e) {
	        LOG.error("Some exception occurred while binding to the recaptcha endpoint.\n[{}]", e);
	    }
	 
	    if (Objects.nonNull(apiResponse) && apiResponse.isSuccess()) {
	       LOG.info("Captcha API response = [{}]", apiResponse.toString());
	       return true;
	    } else {
	       return false;
	    }
    }
}
