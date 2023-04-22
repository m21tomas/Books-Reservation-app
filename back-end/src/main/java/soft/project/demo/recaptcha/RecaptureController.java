package soft.project.demo.recaptcha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path="/api")
public class RecaptureController {
	
	private static final Logger LOG = LoggerFactory.getLogger(RecaptureController.class);
	// injected to validate the captcha response coming in the request.
    @Autowired
    ValidateCaptcha service;
 
    // URL - http://localhost:8080/api/welcome
    @PostMapping("/verify")
    @ResponseStatus(code = HttpStatus.OK)
    public boolean welcome(@RequestBody final RecaptchaDTO dto) throws ForbiddenException {
        final boolean isValidCaptcha = service.validateCaptcha(dto.getCaptchaResponse());
        if (!isValidCaptcha) {
        	LOG.error("Forbidden exception as the captcha is invalid - INVALID_CAPTCHA");
            //throw new ForbiddenException("INVALID_CAPTCHA");
        }
 
        return isValidCaptcha;
    }
}
