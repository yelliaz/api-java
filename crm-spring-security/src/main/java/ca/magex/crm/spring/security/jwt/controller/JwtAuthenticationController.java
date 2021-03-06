package ca.magex.crm.spring.security.jwt.controller;

import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.spring.security.auth.AuthDetails;
import ca.magex.crm.spring.security.jwt.JwtToken;
import ca.magex.crm.spring.security.jwt.JwtTokenService;
import io.jsonwebtoken.JwtException;

@RestController
@CrossOrigin
@Profile(MagexCrmProfiles.AUTH_EMBEDDED_JWT)
public class JwtAuthenticationController {

	@Autowired private UserDetailsService userDetailsService;
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private JwtTokenService jwtTokenService;

	@PostMapping(value = "/authenticate")
	public ResponseEntity<JwtToken> createAuthenticationToken(@RequestBody String jwtRequest) throws Exception {
		try {
			JSONObject json = new JSONObject(jwtRequest);
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(
							json.getString("username"),
							json.getString("password")));
			return ResponseEntity.ok(new JwtToken(jwtTokenService.generateToken(authentication)));
		}
		catch(AuthenticationException e) {
			LoggerFactory.getLogger(getClass()).info("Authentication Failure: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		catch(JSONException json) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		catch(Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error processing authentication request", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping(value = "/validate")
	public ResponseEntity<?> validateToken(@RequestBody String jwtToken) {
		try {
			JSONObject json = new JSONObject(jwtToken);
			String username = jwtTokenService.validateToken(json.getString("token"));
			Date expiration = jwtTokenService.getExpiration(json.getString("token"));
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			return ResponseEntity.ok(new AuthDetails(username, expiration, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
		}		
		catch(JSONException json) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		catch(JwtException jwt) {
			return ResponseEntity.ok().body(new AuthDetails(jwt.getMessage()));
		}
	}
}