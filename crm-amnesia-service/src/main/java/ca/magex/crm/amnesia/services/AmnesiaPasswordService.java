package ca.magex.crm.amnesia.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.PasswordDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaPasswordService implements CrmPasswordService {

	private Map<String, PasswordDetails> passwords;
	
	private long expiration = TimeUnit.DAYS.toMillis(365);
	
	private AmnesiaDB db;
	
	public AmnesiaPasswordService(AmnesiaDB db) {
		this.db = db;
		this.passwords = new HashMap<String, PasswordDetails>();
	}
	
	@Override
	public String getEncodedPassword(@NotNull String username) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getCipherText();
	}

	@Override
	public boolean isTempPassword(@NotNull String username) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.isTemporary();
	}

	@Override
	public boolean isExpiredPassword(@NotNull String username) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getExpiration() != null && passwordDetails.getExpiration().before(new Date());
	}

	@Override
	public boolean verifyPassword(String username, String rawPassword) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return !isExpired(passwordDetails.getExpiration()) && db.getPasswordEncoder().matches(rawPassword, passwordDetails.getCipherText());
	}
	
	private boolean isExpired(Date expirationDate) {
		return expirationDate != null && expirationDate.before(new Date());
	}

	@Override
	public String generateTemporaryPassword(@NotNull String username) {
		String tempPassword = db.generateId().toString();
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails != null) {
			passwords.put(
					username, 
					passwordDetails.withTemporaryPassword(
							db.getPasswordEncoder().encode(tempPassword), 
							new Date(System.currentTimeMillis() + expiration)));
		}
		else {
			passwords.put(
					username, 
					new PasswordDetails(
							db.getPasswordEncoder().encode(tempPassword), 
							true, 
							new Date(System.currentTimeMillis() + expiration)));
		}
		return tempPassword;
	}

	@Override
	public void updatePassword(@NotNull String username, @NotNull String encodedPassword) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		passwords.put(username, passwordDetails.withPassword(encodedPassword));
	}

}
