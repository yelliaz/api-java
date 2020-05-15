package ca.magex.crm.amnesia.services;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.amnesia.Lookups;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.test.AbstractLookupServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaLookupServiceTests extends AbstractLookupServiceTests {

	@Autowired
	private AmnesiaDB db;

	@Autowired 
	private CrmLookupService lookupService;

	@Override
	public CrmLookupService getLookupService() {
		return lookupService;
	}

	@Override
	public void reset() {
		db.reset();
	}
	
	@Test
	public void testInvalidLookupClass() throws Exception {
		try {
			new Lookups<Object, Object>(List.of(), Object.class, Object.class);
			fail("Invalid lookup");
		} catch (Exception expected) { }
	}
	
}