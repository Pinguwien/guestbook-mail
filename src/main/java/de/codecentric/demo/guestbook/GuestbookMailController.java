package de.codecentric.demo.guestbook;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS, RequestMethod.PUT})
public class GuestbookMailController {

	private final MailSender sender;
	
	@Value("${mail.from}")
	private String from;
	
	@Value("${mail.to}")
	private String to;

	@Autowired
	public GuestbookMailController(MailSender sender) {
		this.sender = sender;
	}

	@PostMapping(value = "/mail",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> sendMail(@RequestBody GuestbookEntry entry, Principal p) {
		Boolean isSent = false;
		//Map<String, Object> customAttributes = ((KeycloakAuthenticationToken) p).getAccount().getKeycloakSecurityContext().getToken().getOtherClaims();
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(from);
		msg.setTo(to);
		msg.setSubject("[Guestbook Entry] " + entry.getTitle());
		msg.setText(entry.getCommenter() + " schrieb:\r\n\r\n" + entry.getComment() + "\r\n");
		
		sender.send(msg);
		try {
			Thread.sleep(1500);
			isSent = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().body(isSent);
	}
	
}
